package com.cryo.modules.misc;

import com.cryo.Website;
import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.WebStart;
import com.cryo.entities.WebStartSubscriber;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import lombok.Cleanup;
import spark.Request;
import spark.Response;

import java.io.*;

@WebStartSubscriber
@EndpointSubscriber
public class FavIcon {

    private static File FAVICON;

    @WebStart
    public static void loadFavIcon() {
        FAVICON = new File(Website.getProperties().getProperty("favicon"));
    }

    @Endpoint(method = "GET", endpoint = "favicon.ico")
    public static String sendFavicon(Request request, Response response) {
        try {
            @Cleanup InputStream in = new BufferedInputStream(new FileInputStream(FAVICON));
            @Cleanup OutputStream out = new BufferedOutputStream(response.raw().getOutputStream());
            response.raw().setContentType(MediaType.ICO.toString());
            response.status(200);
            ByteStreams.copy(in, out);
            out.flush();
            return "";
        } catch (FileNotFoundException ex) {
            response.status(404);
            return ex.getMessage();
        } catch (IOException ex) {
            response.status(500);
            return ex.getMessage();
        }
    }
}
