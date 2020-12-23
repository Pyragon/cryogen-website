package com.cryo.modules.accounts;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.utils.DisplayNames;

import static com.cryo.Website.getConnection;

public class AccountUtils {

    public static Account getAccount(int id) {
        return getConnection("cryogen_global").selectClass("player_data", "id=?", Account.class, id);
    }

    public static Account getAccount(String username) {
        return getConnection("cryogen_global").selectClass("player_data", "username=?", Account.class, username);
    }

    public static String crownHTML(Account account) {
        String colour = "";
        String img = "";
        String display = "";
        if(account.getRights() == 2) {
            colour = "#FF0000";
            img = "admin_ing.gif";
        } else if(account.getRights() == 1) {
            colour = "#0174DF";
            img = "mod_ing.gif";
        } else if(account.getDonator() == 3) {
            colour = "#98C7F3";
            img = "hroller_ing.png";
        } else if(account.getDonator() == 2) {
            colour = "#01A9DB";
            img = "sdonator_ing.png";
        } else if(account.getDonator() == 1) {
            colour = "#004300";
            img = "donator_ing.png";
        }
        if(colour != "")
            display += "<span style=\"color: "+colour+";\"><strong><img src=\""+Website.getProperties().getProperty("path")+"/images/crowns/"+img+"\"/> ";
        display += DisplayNames.getDisplayName(account.getUsername());
        if(colour != "")
            display += "</span></strong>";
        return display;
    }
}
