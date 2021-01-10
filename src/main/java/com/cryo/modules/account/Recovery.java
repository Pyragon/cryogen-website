package com.cryo.modules.account;

import com.cryo.Website;
import com.cryo.entities.accounts.PreviousPassList;
import com.cryo.entities.accounts.support.RecoveryQuestion;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.modules.index.Index;
import com.cryo.utils.BCrypt;
import com.cryo.utils.DateUtils;
import com.cryo.utils.Utilities;
import com.mysql.cj.util.StringUtils;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.error;
import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Recovery {

    @SPAEndpoint("/recover")
    public static String renderRecoveryPage(Request request, Response response) {
        if(AccountUtils.getAccount(request) != null)
            return Index.renderIndexPage(request, response);
        String username = request.queryParamOrDefault("username", "");
        HashMap<String, Object> model = new HashMap<>();
        model.put("username", username);
        model.put("sitekey", Website.getProperties().getProperty("captcha_site_key"));
        model.put("questions", RecoveryQuestion.getQuestions().values());
        return renderPage("account/support/recovery/index", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/recover/submit")
    public static String submitRecovery(Request request, Response response) {
        //username, email, discord, creation, cico, isp, JSON.stringify(questions), JSON.stringify(previous), additional
        try {
            if (AccountUtils.getAccount(request) != null)
                return Index.renderIndexPage(request, response);
            String username = request.queryParams("username");
            String email = request.queryParams("email");
            String discord = request.queryParams("discord");
            String creation = request.queryParams("creation");
            String cico = request.queryParams("cico");
            String isp = request.queryParams("isp");
            String questionsS = request.queryParams("questions");
            String previousS = request.queryParams("previous");
            String additional = request.queryParams("additional");
            String captchaResponse = request.queryParams("response");

            ArrayList<ArrayList<Object>> questions = Website.getGson().fromJson(questionsS, ArrayList.class);
            ArrayList<String> previous = Website.getGson().fromJson(previousS, ArrayList.class);
            String result = Utilities.checkCaptchaResult(captchaResponse);
            ArrayList<Integer> used = new ArrayList<>();
            ArrayList<String> usedS = new ArrayList<>();
//            if (result != null)
//                return result;
            if (StringUtils.isNullOrEmpty(username))
                return error("Username must be specified.");
            if (allNull(questions, previous, email, discord, creation, cico, isp, additional))
                return error("At least one value other than username must be filled in.");
            if (previous.size() > 3)
                return error("Too many previous passwords entered. Please report this bug via Github if it persists.");
            for (String pass : previous) {
                if (usedS.contains(pass))
                    return error("Please do not enter duplicate previous passwords.");
                if (!pass.equals(""))
                    usedS.add(pass);
            }
            if (questions.size() > 3)
                return error("Too many previous questions entered. Please report this bug via Github if it persists.");
            Account account = AccountUtils.getAccount(username);
            if (account == null) {
                //enter empty recovery into database, have task to automatically go through randomly every 2-3 hours and decline any where the username is invalid
                //don't even show these ones to staff.
                return error("Account null.");
            }
            //-1 = nothing entered, account does have value
            //-2 = nothing entered, account does not have value
            //1 = value entered, account does have value, incorrect
            //2 = value entered, account does not have value
            //3 = value entered, account does have value, correct, email/discord message sent
            int emailStatus = 0;
            int discordStatus = 0;
            int dateDiff = -1;
            if (!StringUtils.isNullOrEmpty(creation)) {
                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                try {
                    Date date = format.parse(creation);
                    if (date == null)
                        return error("Error parsing calendar date. Please check the date and try again.");
                    dateDiff = (int) DateUtils.getDateDiff(date, account.getAdded(), TimeUnit.DAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                    return error("Error parsing calendar date. Please check the date and try again.");
                }
            }
            int index = -1;
            used = new ArrayList<>();
            int correctRecoveryQuestions = 0;
            for (ArrayList<Object> values : questions) {
                index++;
                if (index == 3)
                    return error("Too many recovery questions. Please report this bug via Github.");
                int id = (int) Math.floor((double) values.get(0));
                String value = (String) values.get(1);
                if (id == -1 || StringUtils.isNullOrEmpty(value))
                    continue;
                if (!RecoveryQuestion.getQuestions().containsKey(id))
                    return error("Invalid recovery question selected. Please report this bug via Github.");
                if (used.contains(id))
                    return error("You cannot have duplicate recovery questions!");
                used.add(id);
                ArrayList<Object> question = account.getQuestionById(id);
                if (question != null && ((String) question.get(1)).equalsIgnoreCase(value))
                    correctRecoveryQuestions++;
            }
            String emailKey = null;
            String discordKey = null;
            if (!StringUtils.isNullOrEmpty(email)) {
                if (email.equalsIgnoreCase(account.getEmail())) {
                    emailKey = Utilities.generateRandomString(10);
                    emailStatus = 3;
                    sendRecoveryEmail(account.getEmail(), emailKey, account, request, response);
                } else if (account.getEmail() == null || account.getEmail().equals(""))
                    emailStatus = 2;
                else
                    emailStatus = 1;
            } else
                emailStatus = (account.getEmail() != null && !account.getEmail().equals("")) ? -1 : -2;
            User user = account.getDiscordUser();
            if (!StringUtils.isNullOrEmpty(discord)) {
                if (user == null)
                    discordStatus = 2;
                else if (!user.getAsTag().equalsIgnoreCase(discord)) {
                    discordStatus = 1;
                } else {
                    discordKey = Utilities.generateRandomString(10);
                    discordStatus = 3;
                    sendDiscordRecoveryMessage(user, account, discordKey);
                }
            } else
                discordStatus = user != null ? -1 : -2;
            ArrayList<Integer> previousPasswordStatuses = new ArrayList<>();
            PreviousPassList previousPasswords = account.getPreviousPasswords();
            String salt = account.getSalt();
            for (String pass : previous) {
                if (pass.equals("")) {
                    previousPasswordStatuses.add(-1);
                    continue;
                }
                String hash = BCrypt.hashPassword(pass, salt);
                previousPasswordStatuses.add(previousPasswords.getHashes().contains(hash) ? 1 : 0);
            }
            String viewKey = Utilities.generateRandomString(10);
            com.cryo.entities.accounts.support.Recovery recovery = new com.cryo.entities.accounts.support.Recovery(-1, username, viewKey, emailKey, discordKey, emailStatus, email, discordStatus, discord,
                    !StringUtils.isNullOrEmpty(creation), dateDiff, cico, isp, Website.getGson().toJson(previousPasswordStatuses), correctRecoveryQuestions, additional, 1, null, null, null, null, null, null);
            getConnection("cryogen_recovery").insert("recoveries", recovery.data());
            return error("Entered recovery.");
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error submitting");
        }
    }

    @Endpoint(method = "GET", endpoint = "/recover/email/:key")
    public static String renderRecoveryEmail(Request request, Response response) {
        String key = request.params(":key");
        HashMap<String, Object> model = new HashMap<>();
        model.put("key", key);
        model.put("user", AccountUtils.getAccount("cody"));
        model.put("link", Website.getProperties().getProperty("path")+"recover/check?key="+key);
        return renderPage("account/support/recovery/recovery_email", model, request, response);
    }

    public static boolean allNull(ArrayList<ArrayList<Object>> questions, ArrayList<String> previous, String... strings) {
        for(String str : strings) {
            if (!StringUtils.isNullOrEmpty(str))
                return false;
        }
        for(String str : previous) {
            if (!StringUtils.isNullOrEmpty(str))
                return false;
        }
        for(ArrayList<Object> question : questions) {
            int id = (int) Math.floor((double) question.get(0));
            if(id != -1) return false;
        }
        return true;
    }

    public static String[] DISCORD_RECOVERY_MESSAGE = {
            "Hello, {{name}}. An account recovery has been made for your in-game account.",
            "The recovery included your discord tag, so you are receiving this message.",
            "If you did not make this recovery attempt, please go to http://cryogen-rsps.com/support/recoveries and remove the recovery, admins will still need to approve it's removal.",
            "If you are consistently getting recoveries through discord, please read the forums for information on how to disable recovering via Discord.",
            "If you DID make this recovery attempt, please click on the following link to be redirected to the website where you can reset your password:",
            "http://cryogen-rsps.com/recover/check?key={{key}}"
    };

    public static void sendDiscordRecoveryMessage(User user, Account account, String discordKey) {
        PrivateChannel channel = user.openPrivateChannel().complete();
        for(String message : DISCORD_RECOVERY_MESSAGE) {
            message = message.replace("{{name}}", account.getDisplayName()).replace("{{key}}", discordKey);
            channel.sendMessage(message).queue();
        }
    }

    public static void sendRecoveryEmail(String email, String key, Account account, Request request, Response response) {
        try {
            String link = Website.getProperties().getProperty("path") + "recover/check?key=" + key;
            HashMap<String, Object> model = new HashMap<>();
            model.put("user", account);
            model.put("key", key);
            model.put("link", link);
            String message = renderPage("account/support/recovery/recovery_email", model, null,"GET", request, response);
            message += "<br><br><br>" +
                    "Hello, " + account.getDisplayName() + "!<br>" +
                    "A password recovery request has been made for your account.<br>" +
                    "If you did not make this request, please click LINK for information on how to secure your account.<br>" +
                    "If you did make this request, simply follow the link below to continue with resetting your password.<br>" +
                    link;
            Utilities.sendEmail(email, "Cryogen Password Recovery", message);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
