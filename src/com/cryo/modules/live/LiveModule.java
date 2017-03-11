package com.cryo.modules.live;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.cryo.Website;
import com.cryo.Website.RequestType;
import com.cryo.modules.WebModule;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.net.MediaType;

import spark.Request;
import spark.Response;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: Mar 8, 2017 at 10:45:02 AM
 */
public class LiveModule extends WebModule {
	
	public static String PATH = "/live/*";
	
	private static File ZIP_PATH = new File("D:/Cryogen Client.zip");
	private static File JAR_PATH = new File("D:/Play Cryogen.jar");

	public LiveModule(Website website) {
		super(website);
	}

	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		String path = request.pathInfo().replace("/live/", "");
		if(!path.startsWith("download"))
			return Website.render404(request, response);
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				File file = ZIP_PATH;
				String fileType = request.queryParams("file_type");
				String fileName = "Cryogen Client.zip";
				if(fileType != null && fileType.equals("jar")) {
					file = JAR_PATH;
					fileName = "Play Cryogen.jar";
				}
				in = new BufferedInputStream(new FileInputStream(file));
				out = new BufferedOutputStream(response.raw().getOutputStream());
				response.raw().setContentType(MediaType.ZIP.toString());
				response.raw().setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.status(200);
				ByteStreams.copy(in, out);
				out.flush();
				return "";
			} finally {
				Closeables.close(in, true);
			}
		} catch(Exception e) {
			response.status(400);
			return e.getMessage();
		}
	}
	
}
