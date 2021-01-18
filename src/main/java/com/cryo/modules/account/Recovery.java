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
import com.cryo.utils.Utilities;
import com.mysql.cj.util.StringUtils;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import spark.Request;
import spark.Response;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Recovery {

    public static Class<com.cryo.entities.accounts.support.Recovery> CLASS = com.cryo.entities.accounts.support.Recovery.class;

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

    @SPAEndpoint("/recover/check")
    public static String checkRecovery(Request request, Response response) {
        String key = request.queryParamOrDefault("key", null);
        HashMap<String, Object> model = new HashMap<>();
        if(key == null) {
            if (request.requestMethod().equals("GET"))
                return renderPage("account/support/recovery/check_recovery", model, request, response);
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        }
        boolean email = false;
        boolean discord = false;
        boolean view = false;
        com.cryo.entities.accounts.support.Recovery recovery;
        if((recovery = getConnection("cryogen_recovery").selectClass("recoveries", "email_key=?", CLASS, key)) != null)
            email = true;
        else if((recovery = getConnection("cryogen_recovery").selectClass("recoveries", "discord_key=?", CLASS, key)) != null)
            discord = true;
        else if((recovery = getConnection("cryogen_recovery").selectClass("recoveries", "view_key=?", CLASS, key)) != null)
            view = true;
        else
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        if(recovery.getStatus() != 1 && !view)
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        Account account = AccountUtils.getAccount(recovery.getUsername());
        if(account == null)
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        model.put("email", email);
        model.put("discord", discord);
        model.put("view", view);
        model.put("user", account);
        model.put("key", key);
        model.put("recovery", recovery);
        return renderPage("account/support/recovery/check-recovery", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/recover/reset")
    public static String recoveryReset(Request request, Response response) {
        String key = request.queryParamOrDefault("key", null);
        HashMap<String, Object> model = new HashMap<>();
        if(key == null) {
            if (request.requestMethod().equals("GET"))
                return renderPage("account/support/recovery/check_recovery", model, request, response);
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        }
        boolean email = true;
        com.cryo.entities.accounts.support.Recovery recovery = getConnection("cryogen_recovery").selectClass("recoveries", "email_key=?", CLASS, key);
        if(recovery == null) {
            recovery = getConnection("cryogen_recovery").selectClass("recoveries", "discord_key=?", CLASS, key);
            if(recovery != null)
                email = false;
            else
                return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        }
        if(recovery.getStatus() != 1)
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        Account account = recovery.getAccount();
        if(account == null)
            return redirect("/", "Invalid key.", null, "Invalid key. Redirecting you to home. Please recheck your link and try again.", request, response);
        String password = request.queryParams("password");
        if(StringUtils.isNullOrEmpty(password) || password.length() < 6 || password.length() > 20)
            return error("Password must be between 6 and 20 characters.");
        String salt = account.getSalt();
        String hash = BCrypt.hashPassword(password, salt);
        if(hash.equals(account.getPassword()))
            return error("That is your current password. Please choose a different password.");
        account.setPassword(hash);
        getConnection("cryogen_accounts").delete("sessions", "username=?", account.getUsername());
        getConnection("cryogen_global").set("player_data", "password=?", "username=?", hash, account.getUsername());
        getConnection("cryogen_recovery").set("recoveries", "status=3, reason='Recovered by "+(email ? "Email" : "Discord")+"', decided=DEFAULT", "id=?", recovery.getId());
        return Utilities.redirect("/", "Password Reset Successful", null, "Your password has been successfully reset. Any current sessions will be logged out when possible.", request, response);
    }

    @Endpoint(method = "POST", endpoint = "/recover/submit")
    public static String submitRecovery(Request request, Response response) {
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
            if (result != null)
                return result;
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
            String viewKey = Utilities.generateRandomString(10);
            ArrayList<Integer> previousPasswordStatuses = new ArrayList<>();
            int correctRecoveryQuestions = 0;
            Account account = AccountUtils.getAccount(username);
            if (account == null) {
                com.cryo.entities.accounts.support.Recovery recovery = new com.cryo.entities.accounts.support.Recovery(-1, username, viewKey, null, null, 0, email, 0, discord,
                        null, cico, isp, Website.getGson().toJson(previousPasswordStatuses), correctRecoveryQuestions, additional, 1, null, null, null, null, null, null);
                getConnection("cryogen_recovery").insert("recoveries", recovery.data());
                HashMap<String, Object> model = new HashMap<>();
                model.put("user", null);
                model.put("name", Utilities.formatNameForDisplay(username));
                model.put("key", viewKey);
                model.put("link", Website.getProperties().getProperty("path")+"recover/check?key="+viewKey);
                return renderPage("account/support/recovery/submitted", model, request, response);
            }
            //-1 = nothing entered, account does have value
            //-2 = nothing entered, account does not have value
            //1 = value entered, account does have value, incorrect
            //2 = value entered, account does not have value
            //3 = value entered, account does have value, correct, email/discord message sent
            int emailStatus;
            int discordStatus;
            Timestamp creationDate = null;
            if (!StringUtils.isNullOrEmpty(creation)) {
                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                try {
                    Date date = format.parse(creation);
                    if (date == null)
                        return error("Error parsing calendar date. Please check the date and try again.");
                    creationDate = new Timestamp(date.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                    return error("Error parsing calendar date. Please check the date and try again.");
                }
            }
            int index = -1;
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
                    sendRecoveryEmail(account.getEmail(), viewKey, emailKey, account, request, response);
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
            com.cryo.entities.accounts.support.Recovery recovery = new com.cryo.entities.accounts.support.Recovery(-1, username, viewKey, emailKey, discordKey, emailStatus, email, discordStatus, discord,
                    creationDate, cico, isp, Website.getGson().toJson(previousPasswordStatuses), correctRecoveryQuestions, additional, 1, null, null, null, null, null, null);
            getConnection("cryogen_recovery").insert("recoveries", recovery.data());
            HashMap<String, Object> model = new HashMap<>();
            model.put("key", viewKey);
            model.put("link", Website.getProperties().getProperty("path")+"recover/check?key="+viewKey);
            model.put("user", account);
            return renderPage("account/support/recovery/submitted", model, request, response);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error submitting");
        }
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
            "Please keep in mind, this key will expire in 24 hours. After that, you will need to either resubmit your recovery, or rely on an admin accepting the recovery based on the information provided.",
            "To reset your password: "+Website.getProperties().get("path")+"recover/check?key={{key}}",
            "To view the recovery: "+Website.getProperties().get("path")+"recover/check?key={{viewKey}}"
    };

    public static void sendDiscordRecoveryMessage(User user, Account account, String discordKey) {
        PrivateChannel channel = user.openPrivateChannel().complete();
        for(String message : DISCORD_RECOVERY_MESSAGE) {
            message = message.replace("{{name}}", account.getDisplayName()).replace("{{key}}", discordKey);
            channel.sendMessage(message).queue();
        }
    }

    public static void sendRecoveryEmail(String email, String viewKey, String key, Account account, Request request, Response response) {
        try {
            String link = Website.getProperties().getProperty("path") + "recover/check?key=" + key;
            String viewLink = Website.getProperties().getProperty("path") + "recover/check?key=" + viewKey;
            HashMap<String, Object> model = new HashMap<>();
            model.put("user", account);
            model.put("key", key);
            model.put("viewKey", viewKey);
            model.put("link", link);
            model.put("viewLink", viewLink);
            String message = renderPage("account/support/recovery/recovery-email", model, null,"GET", request, response);
            message += "<br><br><br>" +
                    "Hello, " + account.getDisplayName() + "!<br>" +
                    "A password recovery request has been made for your account.<br>" +
                    "If you did not make this request, please click LINK for information on how to secure your account.<br>" +
                    "If you did make this request, simply follow the link below to continue with resetting your password.<br>" +
                    "Recovery password link: "+link+"<br>" +
                    "View Recovery link: "+viewLink;
            Utilities.sendEmail(email, "Cryogen Password Recovery", message);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
