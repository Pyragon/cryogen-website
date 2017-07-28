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
 *         Created on: Mar 8, 2017 at 10:45:02 AM
 */
public class LiveModule extends WebModule {
	
	public static int CLIENT_REVISION = 1;
	
	public static int AUTO_REVISION = 1;
	
	public static String PATH = "/live";
	
	public LiveModule(Website website) {
		super(website);
	}
	
	@Override
	public String decodeRequest(Request request, Response response, RequestType type) {
		String action = request.queryParams("action");
		String path = Website.getProperties().getProperty("live-path");
		if (action == null)
			return Website.render404(request, response);
		if (action.equals("get-revision"))
			return Integer.toString(CLIENT_REVISION);
		else if(action.equals("get-auto-revision"))
			return Integer.toString(AUTO_REVISION);
		else if (action.equals("download-auto")) {
			String method = request.queryParams("method");
			if (method == null)
				return Website.render404(request, response);
			String name = "Cryogen Client";
			if (method.equals("jar"))
				name += ".jar";
			else
				name += ".zip";
			File file = new File(path + "" + name);
			if (!file.exists())
				return Website.error("No file could be found.");
			return Website.sendFile(file, response, MediaType.ZIP);
		} else if (action.equals("download")) {
			String version = request.queryParams("version");
			if (version == null)
				return Website.render404(request, response);
			String name = "cryogen_live_r" + version+".jar";
			File file = new File(path + "" + name);
			if (!file.exists())
				return Website.error("No file could be found.");
			return Website.sendFile(file, response, MediaType.ZIP);
		}
		return Website.render404(request, response);
	}
	
}
