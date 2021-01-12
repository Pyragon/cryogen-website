package com.cryo.modules.index;

import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.entities.annotations.SPAEndpoint;
import com.cryo.entities.accounts.HSData;
import com.cryo.entities.forums.Post;
import com.cryo.entities.forums.Thread;
import spark.Request;
import spark.Response;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@EndpointSubscriber
public class Index {

    @SPAEndpoint("/")
    public static String renderIndexPage(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        List<Post> posts = getConnection("cryogen_forum")
                .selectList("threads", "archived=0 AND forum_id IN (1,2)", "ORDER BY added DESC LIMIT 4", Thread.class)
                .stream()
                .map(t -> t.getFirstPost())
                .collect(Collectors.toList());

        List<HSData> hsdata = getConnection("cryogen_global").selectList("highscores", null, "ORDER BY total_level DESC, total_xp DESC, total_xp_stamp DESC LIMIT 10", HSData.class, null);
        model.put("hsdata", hsdata);

        model.put("newsPosts", posts);
        return renderPage("index/index", model, request, response);
    }

}
