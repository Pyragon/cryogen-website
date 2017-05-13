package com.cryo;

import static spark.Spark.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

import com.cryo.db.DBConnectionManager;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.io.OutputStream;
import com.cryo.modules.TestModule;
import com.cryo.modules.account.AccountOverviewModule;
import com.cryo.modules.account.register.RegisterModule;
import com.cryo.modules.account.shop.ShopManager;
import com.cryo.modules.account.support.AccountSupportModule;
import com.cryo.modules.highscores.HighscoresModule;
import com.cryo.modules.index.IndexModule;
import com.cryo.modules.live.LiveModule;
import com.cryo.modules.login.LoginModule;
import com.cryo.modules.login.LogoutModule;
import com.cryo.modules.staff.StaffModule;
import com.cryo.modules.staff.search.SearchManager;
import com.cryo.paypal.PaypalManager;
import com.cryo.tasks.TaskManager;
import com.cryo.tasks.impl.EmailVerifyTask;
import com.cryo.utils.Utilities;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.paypal.api.payments.Payment;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.Cleanup;
import lombok.Getter;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: Mar 7, 2017 at 1:45:54 AM
 */
public class Website {

	public static String PATH = "http://cryogen-rsps.com/";

	private static Website INSTANCE;

	public static volatile boolean LOADED;
	
	private static Scanner scanner;

	private static @Getter Properties properties;

	private @Getter DBConnectionManager connectionManager;
	
	private @Getter PaypalManager paypalManager;

	private Timer fastExecutor;
	
	private @Getter SearchManager searchManager;

	private static File FAVICON = null;

	public Website() {
		loadProperties();
		FAVICON = new File(properties.getProperty("favico"));
		connectionManager = new DBConnectionManager();
		searchManager = new SearchManager();
		paypalManager = new PaypalManager(this);
		searchManager.load();
		fastExecutor = new Timer();
		ShopManager.load(this);
		port(Integer.parseInt(properties.getProperty("port")));
		PaypalManager.createAPIContext();
		staticFiles.externalLocation("source/");
		//staticFiles.expireTime(600); // ten minutes
		get(IndexModule.PATH, (req, res) -> {
			return new IndexModule(this).decodeRequest(req, res, RequestType.GET);
		});
		get(AccountOverviewModule.PATH, (req, res) -> {
			return new AccountOverviewModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post(AccountOverviewModule.PATH, (req, res) -> {
			return new AccountOverviewModule(this).decodeRequest(req, res, RequestType.POST);
		});
		get(AccountSupportModule.PATH, (req, res) -> {
			return new AccountSupportModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post(AccountSupportModule.PATH, (req, res) -> {
			return new AccountSupportModule(this).decodeRequest(req, res, RequestType.POST);
		});
		post("/vote", (req, res) -> {
			return new AccountOverviewModule(this).decodeVotePost(req, res);
		});
		get(LoginModule.PATH, (req, res) -> {
			return new LoginModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post(LoginModule.PATH, (req, res) -> {
			return new LoginModule(this).decodeRequest(req, res, RequestType.POST);
		});
		post(LogoutModule.PATH, (req, res) -> {
			return new LogoutModule(this).decodeRequest(req, res, RequestType.POST);
		});
		get(HighscoresModule.PATH, (req, res) -> {
			return new HighscoresModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post(HighscoresModule.PATH, (req, res) -> {
			return new HighscoresModule(this).decodeRequest(req, res, RequestType.POST);
		});
		get(LogoutModule.PATH, (req, res) -> {
			return new LogoutModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post(StaffModule.PATH, (req, res) -> {
			return new StaffModule(this).decodeRequest(req, res, RequestType.POST);
		});
		get(StaffModule.PATH, (req, res) -> {
			return new StaffModule(this).decodeRequest(req, res, RequestType.GET);
		});
		get("/kill_web", (req, res) -> {
			System.exit(0);
			return "";
		});
		get("/paypal_error", (req, res) -> {
			return error("Error getting payment URL");
		});
		get("/process_payment", (req, res) -> {
			return new PaypalManager(this).decodeRequest(req, res, RequestType.GET);
		});
		get("/test", (req, res) -> {
			return new TestModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post("/test", (req, res) -> {
			return new TestModule(this).decodeRequest(req, res, RequestType.POST);
		});
		get("/redirect", (req, res) -> {
			HashMap<String, Object> model = new HashMap<>();
			model.put("redirect", "/logout");
			return Jade4J.render("./source/modules/redirect.jade", model);
		});
		get(LiveModule.PATH, (req, res) -> {
			return new LiveModule(this).decodeRequest(req, res, RequestType.GET);
		});
		get(RegisterModule.PATH, (req, res) -> {
			return new RegisterModule(this).decodeRequest(req, res, RequestType.GET);
		});
		post(RegisterModule.PATH, (req, res) -> {
			return new RegisterModule(this).decodeRequest(req, res, RequestType.POST);
		});
		get("/players", (req, res) -> {
			return Integer.toString(Utilities.getOnlinePlayers());
		});
		get("/online", (req, res) -> {
			SocketAddress addr = new InetSocketAddress("localhost", 43594);
			@Cleanup Socket socket = new Socket();
			try {
				socket.connect(addr, 5_000);
			} catch(IOException e) {
				return "offline";
			}
			return "online";
		});
		get("/create", (req, res) -> {
			return Jade4J.render("./source/modules/staff/punishments/create_punish.jade", new HashMap<String, Object>());
		});
		get("/favicon.ico", (req, res) -> {
			try {
				java.io.InputStream in = null;
				java.io.OutputStream out = null;
				try {
					in = new BufferedInputStream(new FileInputStream(FAVICON));
					out = new BufferedOutputStream(res.raw().getOutputStream());
					res.raw().setContentType(MediaType.ICO.toString());
					res.status(200);
					ByteStreams.copy(in, out);
					out.flush();
					return "";
				} finally {
					in.close();
					out.close();
				}
			} catch(Exception e) {
				res.status(400);
				return e.getMessage();
			}
		});
		get("*", Website::render404);
		after("*", (req, res) -> {
			
		});
		LOADED = true;
		fastExecutor.schedule(new TaskManager(), 0, 1000);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				System.out.println("hi");
				LOADED = false;
				if(scanner != null)
					scanner.close();
				System.out.println("Stopping spark.");
				Spark.stop();
			}
			
		});
		System.out.println("Listening on port: "+properties.getProperty("port"));
	}
	
	public static void sendToServer(OutputStream stream) throws IOException {
		InetSocketAddress address = new InetSocketAddress("localhost", 5555);
		@Cleanup Socket socket = new Socket();
		socket.connect(address, 1500);
		byte[] bytes = new byte[stream.offset()];
		stream.setOffset(0);
		stream.getBytes(bytes, 0, bytes.length);
		socket.getOutputStream().write(bytes);
	}

	public static String render404(Request request, Response response) {
		response.status(404);
		HashMap<String, Object> model = new HashMap<>();
		model.put("random", getRandomImageLink());
		try {
			return Jade4J.render("./source/modules/404.jade", model);
		} catch (JadeCompilerException | IOException e) {
			e.printStackTrace();
		}
		return error("Error rendering 404 page! Don't worry, we have put the hamsters back on their wheels! Shouldn't be long...");
	}

	public static String getRandomImageLink() {
		File[] files = new File("./source/images/404/").listFiles();
		File random = files[new Random().nextInt(files.length)];
		return String.format("%simages/404/%s", PATH, random.getName());
	}

	public static String error(String error) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("code", 100);
		data.put("status", error);
		return new Gson().toJson(data);
	}

	public static void loadProperties() {
		File file = new File("props.json");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String json = reader.readLine();
			Gson gson = new Gson();
			properties = gson.fromJson(json, Properties.class);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Website instance() {
		return INSTANCE;
	}

	public static void main(String[] args) {
		INSTANCE = new Website();

	}

	public static enum RequestType {
		POST, GET
	}

}
