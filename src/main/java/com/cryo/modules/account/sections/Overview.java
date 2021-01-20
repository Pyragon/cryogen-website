package com.cryo.modules.account.sections;

import com.cryo.Website;
import com.cryo.entities.accounts.support.RecoveryQuestion;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.discord.Discord;
import com.cryo.entities.accounts.discord.Verify;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.TFA;
import com.cryo.utils.Utilities;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.error;
import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class Overview {

    @Endpoint(method = "POST", endpoint = "/account/overview/load")
    public static String renderOverviewPage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        HashMap<String, Object> model = new HashMap<>();
        assert account != null;
        Discord discord = getConnection("cryogen_discord").selectClass("linked", "username=?", Discord.class, account.getUsername());
        if(discord != null)
            model.put("discord", discord);
        model.put("questions", RecoveryQuestion.getQuestions().values());
        return renderPage("account/sections/overview", model, "/account/overview", request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/overview/question")
    public static String getQuestion(Request request, Response response) {
        com.cryo.entities.accounts.Account account = AccountUtils.getAccount(request);
        if(account == null)
            return Login.renderLoginPage("/account/overview", request, response);
        String idString = request.queryParams("id");
        if(!NumberUtils.isDigits(idString))
            return error("Could not parse id. "+idString);
        int id = Integer.parseInt(idString);
        if(id == -1)
            return error("Could not parse id.");
        Properties prop = new Properties();
        if(account.getRecoveryQuestions() == null || account.getRecoveryQuestions().size() < id-1 || account.getRecoveryQuestions().get(id) == null)
            prop.put("question", "");
        else
            prop.put("question", account.getRecoveryQuestions().get(id));
        prop.put("success", true);
        return Website.getGson().toJson(prop);
    }

    @Endpoint(method = "POST", endpoint = "/account/overview/save")
    public static String saveSettings(Request request, Response response) {
        com.cryo.entities.accounts.Account account = AccountUtils.getAccount(request);
        if(account == null)
            return Login.renderLoginPage("/account/overview", request, response);
        String displayName = request.queryParams("display");
        String userTitle = request.queryParams("userTitle");
        String email = request.queryParams("email");
        String discord = request.queryParams("discord");
        String questions = request.queryParams("questions");
        String tfa = request.queryParams("tfa");
        boolean changeDiscord = false;
        boolean changeDisplay = false;
        boolean changeUserTitle = false;
        boolean changeEmail = false;
        boolean changeQuestions = false;
        boolean changeTFA = false;
        Verify verify = null;
        if(displayName != null && !displayName.equalsIgnoreCase(account.getDisplayName())) {
            displayName = Utilities.formatNameForDisplay(displayName);
            if(!DisplayNames.nameExists(displayName, account.getUsername()))
                return error("Display name is already taken! Please try another.");
            changeDisplay = true;
        }
        if((userTitle == null && (account.getCustomUserTitle() != null && !account.getCustomUserTitle().equals("")))
                || (userTitle != null && account.getCustomUserTitle() == null)
                || (userTitle != null && account.getCustomUserTitle() != null && !userTitle.equals(account.getCustomUserTitle()))) {
            if(account.getDonator() == 0)
                return error("You are not able to change your forum user title. Please report this issue via Github.");
            if(userTitle != null && !userTitle.equals("") && (userTitle.length() < 3 || userTitle.length() > 30))
                return error("User title must be between 3 and 30 characters.");
            changeUserTitle = true;
        }
        String realEmail = account.getEmail();
        if((email == null && (realEmail != null && !realEmail.equals("")))
                || (email != null && (realEmail == null || realEmail.equals("")))
                || (email != null && realEmail != null && !email.equals(realEmail))) {
            if(email != null && !email.equals("") && !email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
                return error("Email is invalid. Please check it and retry.");
            changeEmail = true;
        }
        if(discord != null && !discord.equals("")) {
            verify = getConnection("cryogen_discord").selectClass("verify", "random=?", Verify.class, discord);
            if(verify == null)
                return error("Invalid discord random provided. Please double check and try again.");
            changeDiscord = true;
        }
        boolean tfaEnabled = false;
        if(tfa != null) {
            try {
                tfaEnabled = Boolean.parseBoolean(tfa);
            } catch(Exception e) {
                System.out.println(tfa);
                return error("Error parsing TFA. Please refresh the page and try again.");
            }
            if(tfaEnabled == (account.getTFAKey() == null))
                changeTFA = true;
        }
        ArrayList<ArrayList<Object>> recoveryQuestions = null;
        try {
            if (questions != null) {
                recoveryQuestions = Website.getGson().fromJson(questions, ArrayList.class);
                if (recoveryQuestions != null) {
                    int index = -1;
                    ArrayList<Integer> used = new ArrayList<>();
                    for(ArrayList<Object> values : recoveryQuestions) {
                        index++;
                        if (index == 3)
                            return error("Too many recovery questions. Please report this bug via Github.");
                        int id = (int) Math.floor((double) values.get(0));
                        String value = (String) values.get(1);
                        if (id == -1 || StringUtils.isNullOrEmpty(value))
                            continue;
                        if(value.length() < 5)
                            return error("Recovery answers must be at least 5 characters.");
                        if (!RecoveryQuestion.getQuestions().containsKey(id))
                            return error("Invalid recovery question selected. Please report this bug via Github.");
                        if(used.contains(id))
                            return error("You cannot have duplicate recovery questions!");
                        String cur = (String) account.getRecoveryQuestions().get(index).get(1);
                        if(cur.equals(value)) continue;
                        used.add(id);
                        for(int i = 0; i < 3; i++) {
                            if(i == index) continue;
                            if(account.getQuestion(i) != null && (int) Math.floor((double) account.getQuestion(i).get(0)) == id && (int) Math.floor((double) recoveryQuestions.get(index).get(0)) == (int) Math.floor((double) account.getQuestion(index).get(0)))
                                return error("You cannot have duplicate recovery questions!");
                            changeQuestions = true;
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error parsing recovery questions. Please report this bug via Github if it persists.");
        }
        if(!changeDiscord && !changeDisplay && !changeEmail && !changeUserTitle && !changeQuestions)
            return error("No settings have been changed.");
        Properties prop = new Properties();
        prop.put("success", true);
        if(changeDiscord) {
            getConnection("cryogen_discord").delete("verify", "id=?", verify.getId());
            Discord disc = new Discord(-1, account.getUsername(), verify.getDiscordId(), null);
            getConnection("cryogen_discord").insert("linked", disc.data());
            prop.put("discord", disc.getIdString());
        }
        if(changeDisplay) {
            if(!DisplayNames.changeName(account.getUsername(), displayName))
                return error("We encountered an error trying to change your display name. Please post an issue via Github if this problem persists.");
            prop.put("display", displayName);
        }
        if(changeUserTitle)
            getConnection("cryogen_global").set("player_data", "custom_user_title=?", "username=?", userTitle, account.getUsername());
        if(changeEmail) {
            //TODO - todo once new domain is purchased
        }
        if(changeTFA) {
            if(!tfaEnabled) {
                account.setTfaKey(null);
                getConnection("cryogen_global").set("player_data", "tfa_key=NULL", "username=?", account.getUsername());
            } else {
                String key = TFA.generateSecretKey();
                String sessionId = request.cookie("cryo_sess");
                if(sessionId == null)
                    sessionId = request.session().attribute("cryo_sess");
                account.setTfaKey(key);
                getConnection("cryogen_accounts").delete("sessions", "username = ? AND session_id != ?", account.getUsername(), sessionId);
                getConnection("cryogen_global").set("player_data", "tfa_key=?", "username=?", key, account.getUsername());
            }
            prop.put("tfa", tfaEnabled);
        }
        if(changeQuestions) {
            try {
                int index = -1;
                for(ArrayList<Object> values : recoveryQuestions) {
                    index++;
                    int id = (int) Math.floor((double) values.get(0));
                    String value = (String) values.get(1);
                    if (id == -1 || StringUtils.isNullOrEmpty(value)) {
                        account.getRecoveryQuestions().put(index, null);
                        continue;
                    }
                    if (!RecoveryQuestion.getQuestions().containsKey(id))
                        return error("Invalid recovery question selected. Please report this bug via Github.");
                    if(account.getRecoveryQuestions() == null) {
                        account.setQuestions("");
                        account.setRecoveries(new HashMap<>());
                    }
                    account.getRecoveryQuestions().put(index, new ArrayList<Object>() {{
                        add(id);
                        add(value);
                    }});
                }
                HashMap<String, Object[]> wtf = null;
                if(account.getRecoveryQuestions() != null) {
                    wtf = new HashMap<>();
                    for(Integer key : account.getRecoveryQuestions().keySet())
                        wtf.put(Integer.toString(key), account.getRecoveryQuestions().get(key) == null ? null : new Object[] { account.getRecoveryQuestions().get(key).get(0), account.getRecoveryQuestions().get(key).get(1) });
                }
                getConnection("cryogen_global").set("player_data", "recovery_questions=?", "username=?", Website.getGson().toJson(wtf), account.getUsername());
            } catch(Exception e) {
                e.printStackTrace();
                return error("Error setting recovery questions. Please report this problem via Github if it persists.");
            }
        }
        return Website.getGson().toJson(prop);
    }

}
