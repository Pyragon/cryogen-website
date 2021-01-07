package com.cryo.modules.account.sections;

import com.cryo.Website;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.discord.Discord;
import com.cryo.entities.accounts.discord.Verify;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

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
        return renderPage("account/sections/overview", model, "/account/overview", request, response);
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
        boolean changeDiscord = false;
        boolean changeDisplay = false;
        boolean changeUserTitle = false;
        boolean changeEmail = false;
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
        if(!changeDiscord && !changeDisplay && !changeEmail && !changeUserTitle)
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
        return Website.getGson().toJson(prop);
    }

}
