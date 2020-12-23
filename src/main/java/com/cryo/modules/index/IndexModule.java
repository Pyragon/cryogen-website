package com.cryo.modules.index;

import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.forums.BBCode;
import com.cryo.entities.forums.Post;
import com.cryo.entities.forums.Thread;
import spark.Request;
import spark.Response;

import static com.cryo.Website.getConnection;
import static com.cryo.utils.Utilities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@EndpointSubscriber
public class IndexModule {

    @Endpoint(method = "GET", endpoint = "/")
    public static String load(String endpoint, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();

        List<Post> posts = getConnection("cryogen_forum")
                .selectList("threads", "archived=0 AND forum_id IN (1,2)", "ORDER BY added DESC LIMIT 4", Thread.class)
                .stream()
                .map(t -> t.getFirstPost())
                .collect(Collectors.toList());
        model.put("newsPosts", posts);
        model.put("redirect", "/");
        String html;
        try {
            html = renderPage("index/index", model, request, response);
        } catch(Exception e) {
            return render500(request, response);
        }
        return html;
    }

}
