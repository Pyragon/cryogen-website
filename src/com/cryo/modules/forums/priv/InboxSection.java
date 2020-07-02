package com.cryo.modules.forums.priv;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.Notification;
import com.cryo.entities.forums.InboxMessage;
import com.cryo.entities.forums.PrivateSection;
import com.cryo.entities.forums.SentMessage;
import com.cryo.managers.CookieManager;
import com.cryo.managers.NotificationManager;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.DisplayNames;
import com.cryo.utils.Utilities;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.cryo.Website.error;

public class InboxSection implements PrivateSection {
    @Override
    public String getName() {
        return "inbox";
    }

    @Override
    public String decode(String action, Request request, Response response) {
        Properties prop = new Properties();
        if(!CookieManager.isLoggedIn(request))
            return WebModule.showLoginPage("/forums/private/inbox", request, response);
        Account account = CookieManager.getAccount(request);
        HashMap<String, Object> model = new HashMap<>();
        switch(action) {
            case "load":
                try {
                    String html = WebModule.render("./source/modules/forums/private/inbox/inbox.jade", model, request, response);
                    prop.put("success", true);
                    prop.put("html", html);
                } catch (Exception e) {
                    return error("Error loading inbox.");
                }
                break;
            case "load-list":
                int page = Integer.parseInt(request.queryParams("page"));
                if(page == 0) page = 1;
                int offset = (page - 1) * 10;
                List<InboxMessage> messages = AccountConnection.connection().selectList("inbox", "account_id=?", "LIMIT "+offset+",10", InboxMessage.class, account.getId());
                if(messages == null) return error("Error loading private messages.");
                int count = AccountConnection.connection().selectCount("inbox", "account_id=?", account.getId());
                count = (int) Utilities.roundUp(count, 10);
                model.put("messages", messages);
                model.put("type", "inbox");
                String html = WebModule.render("./source/modules/forums/private/inbox/inbox_list.jade", model, request, response);
                prop.put("success", true);
                prop.put("html", html);
                prop.put("pageTotal", count);
                break;
            case "new-message":
                //TODO - Add delay to avoid people spamming link
                String method = request.queryParams("method");
                if(method.equals("view")) {
                    prop.put("success", true);
                    prop.put("html", WebModule.render("./source/modules/forums/private/inbox/new_message.jade", model, request, response));
                    break;
                }
                //TODO - permissions? does person have private off, or has us blocked, etc
                String username = request.queryParams("username");
                String subject = request.queryParams("subject");
                String body = request.queryParams("body");
                if(Utilities.isNullOrEmpty(username, subject, body))
                    return error("All fields must be filled out.");
                if(subject.length() < 5 || subject.length() > 50) return error("Subject must be between 5 and 50 characters.");
                if(body.length() < 5 || body.length() > 500) return error("Body must be between 5 and 500 characters.");
                username = DisplayNames.getUsername(username);
                Account recipient = GlobalConnection.connection().selectClass("player_data", "username=?", Account.class, username);
                if(recipient == null) return error("Unable to find that player.");
                //Add to db, then send notificaton to recipient, delete from draft after we figure that out
                SentMessage sentMessage = new SentMessage(-1, account.getId(), recipient.getId(), subject, body, null, null);
                InboxMessage inboxMessage = new InboxMessage(-1, recipient.getId(), account.getId(), subject, body, false,null, null);;
                AccountConnection.connection().insert("sent", sentMessage.data());
                AccountConnection.connection().insert("inbox", inboxMessage.data());
                NotificationManager.addNotification(recipient, "New PM Received", "New message from $for-name="+account.getUsername()+"$end", "fa fa-envelope", "", "/forums/private/inbox");
                prop.put("success", true);
                break;
            case "read-message":
                String idString = request.queryParams("id");
                int id;
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return error("Error parsing id. please try again later.");
                }
                InboxMessage message = AccountConnection.connection().selectClass("inbox", "id=?", InboxMessage.class, id);
                if(message == null || message.getAccountId() != account.getId())
                    return error("Unable to find that message. Please try again.");
                AccountConnection.connection().set("inbox", "`read`=?", "id=?", 1, message.getId());
                model.put("message", message);
                prop.put("success", true);
                prop.put("subject", message.getSubject());
                prop.put("html", WebModule.render("./source/modules/forums/private/inbox/view_message.jade", model, request, response));
                break;
            case "delete-message":
                idString = request.queryParams("id");
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return error("Error parsing id. please try again later.");
                }
                message = AccountConnection.connection().selectClass("inbox", "id=?", InboxMessage.class, id);
                if(message == null || message.getAccountId() != account.getId())
                    return error("Unable to find that message. Please try again.");
                AccountConnection.connection().delete("inbox", "id=?", id);
                prop.put("success", true);
                break;
            case "mark-read":
                idString = request.queryParams("id");
                try {
                    id = Integer.parseInt(idString);
                } catch(Exception e) {
                    return error("Error parsing id. please try again later.");
                }
                message = AccountConnection.connection().selectClass("inbox", "id=?", InboxMessage.class, id);
                if(message == null || message.getAccountId() != account.getId())
                    return error("Unable to find that message. Please try again.");
                boolean read = !message.isRead();
                AccountConnection.connection().set("inbox", "`read`=?", "id=?", read, message.getId());
                prop.put("read", read);
                prop.put("success", true);
                break;
        }
        return Website.getGson().toJson(prop);
    }
}
