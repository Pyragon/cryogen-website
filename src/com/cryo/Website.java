package com.cryo;

import com.cryo.cache.CachingManager;
import com.cryo.entities.shop.ShoppingCart;
import com.cryo.managers.CommentsManager;
import com.cryo.managers.NotificationManager;
import com.cryo.modules.CommentsModule;
import com.cryo.db.DBConnectionManager;
import com.cryo.db.impl.ShopConnection;
import com.cryo.managers.CookieManager;
import com.cryo.modules.TestModule;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.AccountModule;
import com.cryo.modules.account.RegisterModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.modules.account.recovery.RecoveryModule;
import com.cryo.modules.api.APIModule;
import com.cryo.modules.api.APISections;
import com.cryo.modules.forums.BBCodeManager;
import com.cryo.modules.highscores.HighscoresModule;
import com.cryo.modules.index.IndexModule;
import com.cryo.modules.live.LiveModule;
import com.cryo.modules.login.LoginModule;
import com.cryo.modules.login.LogoutModule;
import com.cryo.modules.search.SearchManager;
import com.cryo.modules.staff.StaffModule;
import com.cryo.managers.PaypalManager;
import com.cryo.tasks.TaskManager;
import com.cryo.utils.*;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static spark.Spark.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 *         Created on: Mar 7, 2017 at 1:45:54 AM
 */
public class Website {

	private static Website INSTANCE;

	public static @Getter @Setter int SHUTDOWN_TIME = -1;

	public static volatile boolean LOADED;

	private static @Getter Properties properties;

	private @Getter
	DBConnectionManager connectionManager;

	private @Getter
	PaypalManager paypalManager;

	private @Getter
	CachingManager cachingManager;

	private @Getter
	CommentsManager commentsManager;

	private @Getter
	SearchManager searchManager;

	private @Getter
	ConnectionManager nConnectionManager;

    @Getter
    private BBCodeManager BBCodeManager;

	private Timer fastExecutor;

	private static File FAVICON = null;

	private static Gson Gson;

	public static Gson getGson() {
		return Gson;
	}

	public void load() {
		try {
			Gson = buildGson();
			loadProperties();
			FAVICON = new File(properties.getProperty("favico"));
			connectionManager = new DBConnectionManager();
			nConnectionManager = new ConnectionManager();
			commentsManager = new CommentsManager();
			paypalManager = new PaypalManager(this);
			cachingManager = new CachingManager();
			cachingManager.loadCachedItems();
			fastExecutor = new Timer();
			searchManager = new SearchManager();
            BBCodeManager = new BBCodeManager();
            BBCodeManager.load();
			ShoppingCart.loadShopItems();
			searchManager.load();
			port(Integer.parseInt(properties.getProperty("port")));
			PaypalManager.createAPIContext();
			staticFiles.externalLocation("source/");
			staticFiles.expireTime(0); // ten minutes
			staticFiles.header("Access-Control-Allow-Origin", "*");
			CorsFilter.apply();
			DisplayNames.init();
			APISections.loadSections();
			AccountModule.registerEndpoints(this);
			SearchManager.registerEndpoints(this);
			StaffModule.registerEndpoints(this);
			CommentsModule.registerEndpoints(this);
			NotificationManager.registerEndpoints();
			Utilities.registerEndpoints(UtilityModule.ENDPOINTS, UtilityModule.decodeRequest);
			APIModule.registerEndpoints(this);
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
			get("/test", (req, res) -> new TestModule(this).decodeRequest(req, res, RequestType.GET));
			post("/test", (req, res) -> new TestModule(this).decodeRequest(req, res, RequestType.POST));
			get("/redirect", (req, res) -> {
				HashMap<String, Object> model = new HashMap<>();
				model.put("redirect", "/logout");
				return Jade4J.render("./source/modules/redirect.jade", model);
			});
			get(LiveModule.PATH, (req, res) -> new LiveModule(this).decodeRequest(req, res, RequestType.GET));
			get("/recover", (req, res) -> new RecoveryModule(this, "recover").decodeRequest(req, res, RequestType.GET));
			post("/recover", (req, res) -> new RecoveryModule(this, "recover").decodeRequest(req, res, RequestType.POST));
			get("/view_status", (req, res) -> new RecoveryModule(this, "view_status").decodeRequest(req, res, RequestType.GET));
			post("/view_status", (req, res) -> new RecoveryModule(this, "view_status").decodeRequest(req, res, RequestType.POST));
			get(RegisterModule.PATH, (req, res) -> new RegisterModule(this).decodeRequest(req, res, RequestType.GET));
			post(RegisterModule.PATH, (req, res) -> new RegisterModule(this).decodeRequest(req, res, RequestType.POST));
			for (Class<?> c : Utilities.getClasses("com.cryo.modules")) {
				try {
					if (!WebModule.class.isAssignableFrom(c)) continue;
					if (c.getName().equals("com.cryo.modules.WebModule")) continue;
					Object o = c.newInstance();
					if (!(o instanceof WebModule)) continue;
					WebModule module = (WebModule) o;
					int i = 0;
					while (i < module.getEndpoints().length) {
						String method = module.getEndpoints()[i++];
						String path = module.getEndpoints()[i++];
						if (method.equals("GET")) get(path, (req, res) -> module.decodeRequest(path, req, res));
						else post(path, (req, res) -> module.decodeRequest(path, req, res));
					}
				} catch (Exception e) {

				}
			}
			get("/favicon.ico", (req, response) -> {
	                    @Cleanup InputStream in = null;
	                    @Cleanup OutputStream out = null;
	                    try {
	                        in = new BufferedInputStream(new FileInputStream(FAVICON));
	                        out = new BufferedOutputStream(response.raw().getOutputStream());
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
			fastExecutor.schedule(new TaskManager(), 1000, 1000);
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

	public static DBConnection getConnection(String schema) {
		return INSTANCE.getNConnectionManager().getConnection(schema);
	}

	public static Gson buildGson() {
		return new GsonBuilder()
				.serializeNulls()
				.setVersion(1.0)
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();
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
		return String.format("%simages/404/%s", Website.getProperties().getProperty("path"), random.getName());
	}

	public static String error(String error) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("code", 100);
		data.put("status", error);
		return getGson().toJson(data);
	}

	public static void loadProperties() {
		File file = new File("data/props.json");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder builder = new StringBuilder();
			while((line = reader.readLine()) != null)
				builder.append(line);
			String json = builder.toString();
			properties = getGson().fromJson(json, Properties.class);
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
		INSTANCE.load();

	}

	public static enum RequestType {
		POST, GET
	}

}
