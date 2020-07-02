package com.cryo.managers;

import com.cryo.Website;
import com.cryo.db.impl.AccountConnection;
import com.cryo.entities.Notification;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import lombok.Data;
import spark.Spark;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.cryo.Website.error;
import static spark.Spark.post;

@Data
public class NotificationManager {

    List<Notification> notifications;
    private Account account;

    public NotificationManager(Account account) {
        this.account = account;
        notifications = AccountConnection.connection().selectList("notifications", "user_id=?", Notification.class, account.getId());
    }

    public static void registerEndpoints() {
        post("/notifications/get/:count", (req, res) -> {
            Account account = CookieManager.getAccount(req);
            if(account == null) return error("Not logged in. Please refresh the page and try again.");
            NotificationManager manager = new NotificationManager(account);
            String countString = req.params(":count");
            int count;
            try {
                count = Integer.parseInt(countString);
            } catch(Exception e) {
                return error("Unable to parse count. Please try again.");
            }
            HashMap<String, Object> model = new HashMap<>();
            Properties prop = new Properties();
            List<Notification> saved = manager.notifications.stream().limit(count).collect(Collectors.toList());
            model.put("notificationsL", saved);
            prop.put("html", WebModule.render("./source/modules/utils/notification_block.jade", model, req, res));
            prop.put("success", true);
            return Website.getGson().toJson(prop);
        });
        post("/notifications/remove/:id", (request, response) -> {
            Account account = CookieManager.getAccount(request);
            if(account == null) return error("Not logged in. Please refresh the page and try again.");
            String idString = request.params(":id");
            String countString = request.params(":id");
            int id;
            int count;
            try {
                id = Integer.parseInt(idString);
                count = Integer.parseInt(countString);
            } catch(Exception e) {
                return error("Unable to parse id. Please try again.");
            }
            Notification notification = AccountConnection.connection().selectClass("notifications", "id=?", Notification.class, id);
            if(notification == null || notification.getUserId() != account.getId())
                return error("Unable to delete notification. Please try again later.");
            AccountConnection.connection().delete("notifications", "id=?", id);
            NotificationManager manager = new NotificationManager(account);
            HashMap<String, Object> model = new HashMap<>();
            Properties prop = new Properties();
            List<Notification> saved = manager.notifications.stream().limit(count).collect(Collectors.toList());
            model.put("notificationsL", saved);
            prop.put("html", WebModule.render("./source/modules/utils/notification_block.jade", model, request, response));
            prop.put("success", true);
            return Website.getGson().toJson(prop);
        });
    }

    public boolean hasUnreadNotifications() {
        return getUnreadNotificationsCount() > 0;
    }

    public long getUnreadNotificationsCount() {
        return notifications.stream().filter(n -> !n.isRead()).count();
    }

    public static void addNotification(Account account, String title, String content, String faIcon, String icon, String link) {
        Notification notification = new Notification(-1, account.getId(), faIcon, icon, title, content, link, false, null, null);
        AccountConnection.connection().insert("notifications", notification.data());
    }

}
