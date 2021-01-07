package com.cryo.modules.account.sections;

import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.VoteSite;
import com.cryo.entities.accounts.Account;
import com.cryo.managers.ListManager;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

@EndpointSubscriber
public class Vote {

    @Endpoint(method = "POST", endpoint = "/account/vote/load")
    public static String renderVotePage(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return Login.renderLoginPage("/account/vote", request, response);
        HashMap<String, Object> model = new HashMap<>();
        model.put("title", "Votes");
        model.put("module", "/account/vote");
        model.put("moduleId", "account-vote");
        model.put("info", new ArrayList<String>() {{
            add("The following page lists all the available sites to vote on, as well as information on your total votes.");
            add("You may redeem vote points via the in-game voting store.");
        }});
        return renderPage("utils/list/list-page", model, request, response);
    }

    @Endpoint(method = "POST", endpoint = "/account/vote/table")
    public static String renderTable(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null) return error("Session has expired. Please refresh the page and try again.");
        HashMap<String, Object> model = new HashMap<>();
        List<VoteSite> sites = getConnection("cryogen_vote").selectList("vote_sites", VoteSite.class);
        ListManager.buildTable(model, sites, VoteSite.class, account, new ArrayList<>(), new ArrayList<>(),false);
        return renderList(model, request, response);
    }

}
