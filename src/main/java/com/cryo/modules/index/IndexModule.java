package com.cryo.modules.index;

import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.SPAEndpoint;
import com.cryo.entities.accounts.HSData;
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

    @SPAEndpoint("/")
    public static String load(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();

        List<Post> posts = getConnection("cryogen_forum")
                .selectList("threads", "archived=0 AND forum_id IN (1,2)", "ORDER BY added DESC LIMIT 4", Thread.class)
                .stream()
                .map(t -> t.getFirstPost())
                .collect(Collectors.toList());

        //highscores users, this seriously needs to be fixed before release
        //most servers just order by level, then xp
        //this way, once multiple people have the same total level and xp, it'll order by id
        //runescape's highscores order by first person to reach that xp, regardless of id
        List<HSData> hsdata = getConnection("cryogen_global").selectList("highscores", null, "ORDER BY total_level DESC, total_xp DESC, total_xp_stamp DESC LIMIT 10", HSData.class, null);
        model.put("hsdata", hsdata);

        model.put("newsPosts", posts);
        String html;
        try {
            html = renderPage("index/index", model, request, response);
        } catch(Exception e) {
            return render500(request, response);
        }
        if(request.requestMethod().equals("GET"))
            return html;
        return success(html);
    }

}
