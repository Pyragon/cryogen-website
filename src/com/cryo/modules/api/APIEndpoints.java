package com.cryo.modules.api;

import com.cryo.Website;
import com.cryo.server.APIConnection;
import com.mashape.unirest.http.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

@AllArgsConstructor
@RequiredArgsConstructor
public enum APIEndpoints {

    FORUMS_POST(1, "/forums/post", "Returns a parsed version of a forum post.", 0, "GET",
            new APIParameter[] {
                    new APIParameter("post_id", "Integer", "Post ID to look up.", false, 1) },
            new APIReturn[] {
                    new APIReturn("success", "Boolean", "Whether action was successful or not."),
                    new APIReturn("error", "String", "Error message if success returns false."),
                    new APIReturn("post", "PostDAO", "Returns a PostDAO object containing information about the post.")
    }),
    FORUMS_POSTS(2, "/forums/posts", "Returns a list of posts filtered by 'filter' parameter", 0, "GET",
            new APIParameter[] {
                    new APIParameter("filter", "String", "Filter for which posts to view, currently only supports 'latest'", true, "latest"),
                    new APIParameter("limit", "Integer", "Number of posts to return", true, 5) },
            new APIReturn[] { new APIReturn("success", "Boolean", "Whether action was successful or not."),
                    new APIReturn("error", "String", "Error message if success returns false."),
                    new APIReturn("threads", "List<PostDAO>", "Returns a list of PostDAO objects containing information about the post.")
    }),
    ONLINE_PLAYERS(1, "/game/players/online", "Returns amount of players online." +
            "", -1, "GET", null,
            new APIReturn[] {
                    new APIReturn("success", "Boolean", "Whether action was successful or not."),
                    new APIReturn("error", "String", "Error message if success returns false"),
                    new APIReturn("online", "OnlineInfoDAO", "DAO containing information on players online.")
    }),
    MINIGAME_INFO(2, "/game/minigame", "Returns information about minigames in Cryogen.", 0, "GET",
            new APIParameter[] {
                    new APIParameter("filter", "String", "List of minigames to filter return value from", true, null) },
            new APIReturn[] {
                    new APIReturn("success", "Boolean", "Whether action was successful or not."),
                    new APIReturn("error", "String", "Error message if success returns false"),
                    new APIReturn("active", "String", "Name of the currently active minigame."),
                    new APIReturn("remaining", "Long", "Time remaining until next active minigame in milliseconds."),
                    new APIReturn("minigames", "HashMap<String, MinigameInfoDAO>", "Map containing information on minigames in Cryogen.")
    }),
    GET_CLIENT_INFO(1, "/live/get/:version", "Get client data about a specific version.", -1, "GET",
            new APIParameter[] {
                    new APIParameter("version", "String", "Version of the client to return data for.", true, "latest") },
            new APIReturn[] {
                    new APIReturn("success", "Boolean", "Whether action was successful or not."),
                    new APIReturn("error", "String", "Error message if success returns false"),
                    new APIReturn("version", "String", "Version of the client data returned."),
                    new APIReturn("last_update", "String", "Last update to client when this version was released."),
                    new APIReturn("is_latest", "Boolean", "Whether version returned is latest released.")
    }),
    DOWNLOAD_CLIENT(2, "/live/download/:version", "Download a certain version of the Cryogen client.", -1, "GET",
            new APIParameter[] {
                    new APIParameter("version", "String", "Version of the client to download.", false, "latest")
            }, null),
    LOGIN(1, "/login", "Handles login for the API.", -1, new String[] { "GET", "POST" }, true,
            new APIParameter[] {
                    new APIParameter("username", "String", "Username to login with."),
                    new APIParameter("password", "String", "Password to login with."),
                    new APIParameter("expiry", "Long", "Duration for the token to be active. Default: 3 hours", true, 10800000),
                    new APIParameter("revoke", "Boolean", "Option to revoke all other tokens for your account on successful login.", true, false) },
            new APIReturn[] {
                    new APIReturn("success", "Boolean", "Whether login was successful or not."),
                    new APIReturn("error", "String", "Error message if success returns false"),
                    new APIReturn("expiry", "Long", "Time in millis of when the token will expire."),
                    new APIReturn("token", "String", "Token to be used in authenticating with the Cryogen API.")
    }),
    STATUS_INFO(1, "/status/:action", "Returns status information about the server, website, or client.", -1, "GET",
            new APIParameter[] {
                    new APIParameter("action", "String", "Which action to query the status of.", true, "uptime") },
            new APIReturn[] {
                    new APIReturn("uptime", "Long", "Time passed since server start in milliseconds."),
                    new APIReturn("formatted", "String", "Time passed since server start formatted into a String")
    }),
    UPDATE_INFO(1, "/updates", "Returns information about updates made to the Cryogen website, server, and client.", -1, "GET",
            new APIParameter[] {
                    new APIParameter("filter", "String", "Filters separated by a comma. Available filters: web, server, and client", true, "web,server,client"),
                    new APIParameter("limit", "Integer", "Number of updates to return. Max 50.", true, 10) },
            new APIReturn[] {
                    new APIReturn("commits", "List<CommitInfoDAO>", "List of DAOs containing information on a Github commit.")
    }),
    USER_DATA(1, "/users/me", "Returns information about your in-game account. To be expanded upon.", 0, "GET", null,
            new APIReturn[] {
                    new APIReturn("username", "String", "Your in-game username."),
                    new APIReturn("display_name", "String", "Your in-game display name."),
                    new APIReturn("creation_time", "CreationTimeDAO", "DAO containing information on your creation time.")
    });
    private final @Getter int id;
    private final @Getter String endpoint;
    private final @Getter String description;
    private final @Getter int permissions;
    private final Object requestMethod;

    private @Getter boolean noExample;

    private final @Getter APIParameter[] parameters;

    private final @Getter APIReturn[] returns;

    public String getExample() {
        return examples.containsKey(endpoint) ? examples.get(endpoint) : null;
    }

    private static @Getter HashMap<String, String> examples;

    public Object getRequestMethod() {
        if(requestMethod instanceof String) return requestMethod;
        return Arrays.toString((String[]) requestMethod).replace("[", "").replace("]", "");
    }

    static {
        examples = new HashMap<>();
        APIConnection con = new APIConnection(Website.getProperties().getProperty("test-user"), Website.getProperties().getProperty("test-pass"));
        Arrays.stream(APIEndpoints.values()).forEach(v -> {
            try {
                if(v.isNoExample()) {
                    examples.put(v.endpoint, Website.error("Unable to load example for this endpoint."));
                    return;
                }
                Properties prop = null;
                if (v.parameters != null) {
                    prop = new Properties();
                    for (APIParameter parameter : v.parameters) {
                        if(parameter.getDefaultValue() == null) continue;
                        prop.put(parameter.getName(), parameter.getDefaultValue());
                    }
                }
                JsonNode node = con.getResponse(v.endpoint, prop, v.getRequestMethod() instanceof String ? (String) v.getRequestMethod() : (String) ((Object[]) v.getRequestMethod())[0], v.getPermissions());
                String ret;
                if (node == null) {
                    ret = Website.error("Error loading from API.");
                    examples.put(v.endpoint, ret);
                    return;
                }
                ret = node.getObject().toString(4);
                examples.put(v.endpoint, ret);
            } catch(Exception e) {
                System.err.println("Error loading endpoint: "+v.endpoint);
                e.printStackTrace();
            }
        });
    }

}
