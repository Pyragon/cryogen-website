package com.cryo;

import static spark.Spark.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.cryo.cache.CachingManager;
import com.cryo.comments.CommentsManager;
import com.cryo.comments.CommentsModule;
import com.cryo.db.DBConnectionManager;
import com.cryo.db.DBConnectionManager.Connection;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.db.impl.ShopConnection;
import com.cryo.modules.TestModule;
import com.cryo.modules.account.AccountModule;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.RegisterModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.recovery.RecoveryModule;
import com.cryo.modules.highscores.HighscoresModule;
import com.cryo.modules.index.IndexModule;
import com.cryo.modules.live.LiveModule;
import com.cryo.modules.login.LoginModule;
import com.cryo.modules.login.LogoutModule;
import com.cryo.modules.search.SearchManager;
import com.cryo.modules.staff.StaffModule;
import com.cryo.paypal.PaypalManager;
import com.cryo.server.ServerConnection;
import com.cryo.server.item.ServerItem;
import com.cryo.server.item.ShopItem;
import com.cryo.tasks.TaskManager;
import com.cryo.tasks.impl.EmailVerifyTask;
import com.cryo.utils.CookieManager;
import com.cryo.utils.CorsFilter;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;
import com.cryo.utils.UtilityModule;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.api.payments.Payment;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: Mar 7, 2017 at 1:45:54 AM
 */
public class Website {

	public static String PATH = "http://cryogen.live/";

	private static Website INSTANCE;

	public static @Getter @Setter int SHUTDOWN_TIME;

	public static volatile boolean LOADED;

	private static @Getter Properties properties;

	private @Getter final DBConnectionManager connectionManager;

	private @Getter final PaypalManager paypalManager;

	private @Getter final CachingManager cachingManager;

	private @Getter final CommentsManager commentsManager;

	private @Getter final SearchManager searchManager;

	private final Timer fastExecutor;

	private static File FAVICON = null;

	private static @Getter Gson Gson;

	public Website() {
		try {
			if(System.getProperty("os.name").equals("Windows 10"))
				PATH = "http://localhost:8085/";
			loadProperties();
			Gson = buildGson();
			FAVICON = new File(properties.getProperty("favico"));
			connectionManager = new DBConnectionManager();
			commentsManager = new CommentsManager();
			paypalManager = new PaypalManager(this);
			cachingManager = new CachingManager();
			cachingManager.loadCachedItems();
			fastExecutor = new Timer();
			searchManager = new SearchManager();
			ShopConnection.load(this);
			searchManager.load();
			port(Integer.parseInt(properties.getProperty("port")));
			PaypalManager.createAPIContext();
			staticFiles.externalLocation("source/");
			staticFiles.expireTime(0); // ten minutes
			staticFiles.header("Access-Control-Allow-Origin", "*");
			CorsFilter.apply();
			System.out.println("here");
			AccountModule.registerEndpoints(this);
			SearchManager.registerEndpoints(this);
			StaffModule.registerEndpoints(this);
			CommentsModule.registerEndpoints(this);
			UtilityModule.registerEndpoints(this);
			get(IndexModule.PATH, (req, res) -> new IndexModule(this).decodeRequest(req, res, RequestType.GET));
			get(LoginModule.PATH, (req, res) -> new LoginModule(this).decodeRequest(req, res, RequestType.GET));
			post(LoginModule.PATH, (req, res) -> new LoginModule(this).decodeRequest(req, res, RequestType.POST));
			post(LogoutModule.PATH, (req, res) -> new LogoutModule(this).decodeRequest(req, res, RequestType.POST));
			get(HighscoresModule.PATH, (req, res) -> new HighscoresModule(this).decodeRequest(req, res, RequestType.GET));
			post(HighscoresModule.PATH, (req, res) -> new HighscoresModule(this).decodeRequest(req, res, RequestType.POST));
			get(LogoutModule.PATH, (req, res) -> new LogoutModule(this).decodeRequest(req, res, RequestType.GET));
			get("/kill_web", (req, res) -> {
				Account account = CookieManager.getAccount(req);
				if(account == null || account.getRights() < 2)
					return error("Insufficient permissions.");
				if(SHUTDOWN_TIME > 0)
					return "Website already being shutdown. Please wait.";
				String time = req.queryParams("delay");
				int delay = 30;
				if(time != null)
					delay = Integer.parseInt(time);
				SHUTDOWN_TIME = delay;
				fastExecutor.schedule(new TimerTask() {

					@Override
					public void run() {
						Website.SHUTDOWN_TIME--;
						if(Website.SHUTDOWN_TIME == 0)
							System.exit(0);
					}

				}, 0, 1000);
				return "Shutting down website in "+delay+" seconds.";
			});
			get("/paypal_error", (req, res) -> error("Error getting payment URL"));
			get("/process_payment", (req, res) -> new PaypalManager(this).decodeRequest(req, res, RequestType.GET));
			post("/grab_data", Website::grab_data);
			get("/grab_data", Website::grab_data);
			get("/test", (req, res) -> new TestModule(this).decodeRequest(req, res, RequestType.GET));
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
			get("/recover", (req, res) -> {
				return new RecoveryModule(this, "recover").decodeRequest(req, res, RequestType.GET);
			});
			post("/recover", (req, res) -> {
				return new RecoveryModule(this, "recover").decodeRequest(req, res, RequestType.POST);
			});
			get("/view_status", (req, res) -> {
				return new RecoveryModule(this, "view_status").decodeRequest(req, res, RequestType.GET);
			});
			post("/view_status", (req, res) -> {
				return new RecoveryModule(this, "view_status").decodeRequest(req, res, RequestType.POST);
			});
			get(RegisterModule.PATH, (req, res) -> {
				return new RegisterModule(this).decodeRequest(req, res, RequestType.GET);
			});
			System.out.println("here");
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
			get("/favicon.ico", (req, response) -> {
	                try {
	                    InputStream in = null;
	                    OutputStream out = null;
	                    try {
	                        in = new BufferedInputStream(new FileInputStream(FAVICON));
	                        out = new BufferedOutputStream(response.raw().getOutputStream());
	                        response.raw().setContentType(MediaType.ICO.toString());
	                        response.status(200);
	                        ByteStreams.copy(in, out);
	                        out.flush();
	                        return "";
	                    } finally {
	                        in.close();
	                    }
	                } catch (FileNotFoundException ex) {
	                    response.status(404);
	                    return ex.getMessage();
	                } catch (IOException ex) {
	                    response.status(500);
	                    return ex.getMessage();
	                }
	        });
			get("/rsfont", (req, res) -> {
				try {
					res.header("Access-Control-Allow-Origin", "*");
	                InputStream in = null;
	                OutputStream out = null;
	                try {
	                    in = new BufferedInputStream(new FileInputStream(new File("runescape_chat.woff2")));
	                    out = new BufferedOutputStream(res.raw().getOutputStream());
	                    res.raw().setContentType(MediaType.ANY_TYPE.type());
	                    res.status(200);
	                    ByteStreams.copy(in, out);
	                    out.flush();
	                    return "";
	                } finally {
	                    in.close();
	                }
	            } catch (FileNotFoundException ex) {
	            	res.status(404);
	                return ex.getMessage();
	            } catch (IOException ex) {
	            	res.status(500);
	                return ex.getMessage();
	            }
			});
			get("*", Website::render404);
			fastExecutor.schedule(new TaskManager(), 0, 1000);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				LOADED = false;
				System.out.println("Stopping spark.");
				stop();
			}));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("Listening on port: "+properties.getProperty("port"));
	}

	public static Gson buildGson() {
		return new GsonBuilder()
				.serializeNulls()
				.setVersion(1.0)
				.disableHtmlEscaping()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();
	}

	private static String grab_data(Request req, Response res) {
		Properties prop = new Properties();
		String action = req.queryParams("action");
		HashMap<String, Object> model = new HashMap<>();
		switch(action) {
			case "get-drops-feed":
				Object data = INSTANCE.getCachingManager().get("drops-feed-cache").getCachedData();
				if(data == null || !(data instanceof Properties))
					return error("Unable to retrieve from drops-feed-cache.");
				return new Gson().toJson((Properties) data);
			case "get-online-users":
				if(INSTANCE.getCachingManager().get("online-users-cache") == null)
					return error("Unable to retrieve from online-users-cache.");
				data  = INSTANCE.getCachingManager().get("online-users-cache").getCachedData();
				if(data == null || !(data instanceof String))
					return error("Unable to retrieve from online-users-cache.");
				return (String) data;
			case "get-item-div":
				int itemId = Integer.parseInt(req.queryParams("item"));
				data = INSTANCE.getCachingManager().get("server-item-cache").getCachedData(itemId);
				if(data == null || !(data instanceof ServerItem)) {
					prop.put("success", false);
					prop.put("error", "Unable to get server item.");
					break;
				}
				ServerItem server_item = (ServerItem) data;
				model.put("item", server_item);
				try {
					prop.put("success", true);
					prop.put("html", Jade4J.render("./source/forums/item_market.jade", model));
				} catch (JadeCompilerException | IOException e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", "Error retrieving item-div, please contact Cody if this problem persists.");
				}
				break;
			case "get-shop-div":
				int id = Integer.parseInt(req.queryParams("id"));
				Object[] arr = ForumConnection.connection().handleRequest("get-username", id);
				if(arr == null) {
					prop.put("success", false);
					prop.put("error", "Your forum account is not linked with your In-game account!");
					break;
				}
				String username = (String) arr[0];
				String url = ServerConnection.SERVER_URL+"/grab_data?action=get-shop-items&username="+username;
				String strres = "";//ServerConnection.getResponse(url);
				Properties response = new Gson().fromJson(strres, Properties.class);
				if(!response.getProperty("success").equals("true")) {
					prop.put("success", false);
					prop.put("error", "Error getting data from server.");
					break;
				}
				List<String> list = Gson.fromJson(response.getProperty("list"), List.class);
				ArrayList<ShopItem> items = new ArrayList<>();
				for(String li : list) {
					if(li.equals(""))
						continue;
					String[] values = li.split(":");
					id = Integer.parseInt(values[0]);
					int amount = Integer.parseInt(values[1]);
					int price = Integer.parseInt(values[2]);
					String name = values[3];
					String examine = values[4];
					ShopItem sItem = new ShopItem(id, amount, price);
					sItem.setName(name);
					sItem.setExamine(examine);
					items.add(sItem);
				}
				model = new HashMap<>();
				Account account = AccountUtils.getAccount(username);
				if(account == null) {
					prop.put("success", false);
					prop.put("error", "Error loading account DAO");
					break;
				}
				String crown = AccountUtils.crownHTML(account);
				model.put("username", username);
				model.put("crowned", crown);
				model.put("items", items);
				try {
					String html = Jade4J.render("./source/forums/personal_shop.jade", model);
					prop.put("success", true);
					prop.put("html", html);
					break;
				} catch (JadeCompilerException | IOException e) {
					e.printStackTrace();
					prop.put("success", false);
					prop.put("error", "Error compiling jade template");
				}
				break;
		}
		return new Gson().toJson(prop);
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

	public static Properties getNotLoggedError() {
		Properties prop = new Properties();
		prop.put("success", false);
		prop.put("error", "Session expired! Please reload the page to login again.");
		return prop;
	}

	public static HttpServletResponse sendFile(File file, Response res, MediaType type) {
		res.header("Content-Disposition", "attachment; filename="+file.getName());
		res.type("application/force-download");
		try {
			byte[] bytes = Files.toByteArray(file);
			HttpServletResponse raw = res.raw();
			raw.getOutputStream().write(bytes);
			raw.getOutputStream().flush();
			raw.getOutputStream().close();
			return res.raw();
		} catch(Exception e) { e.printStackTrace(); }
		return null;
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
