package com.cryo.utils;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.*;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.google.common.reflect.ClassPath;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import org.joda.time.DateTime;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.post;

public class Utilities {

    public static String renderPage(String module, HashMap<String, Object> model, String endpoint, Request request, Response response) {
        return renderPage(module, model, endpoint, request.requestMethod(), request, response);
    }

    public static String renderPage(String module, HashMap<String, Object> model, Request request, Response response) {
        return renderPage(module, model, null, request.requestMethod(), request, response);
    }

    public static String renderPage(String module, HashMap<String, Object> model, String endpoint, String method, Request request, Response response) {
        try {
            Account account = AccountUtils.getAccount(request);
            if(endpoint != null && account == null)
                return Login.renderLoginPage(endpoint, request, response);
            if(request.requestMethod().equals("GET"))
                return Jade4J.render("./source/modules/default/default.jade", model);
            if(model == null)
                model = new HashMap<>();
            model.put("format", new FormatUtils());
            Properties prop = new Properties();
            prop.put("success", true);
            model.put("loggedIn", account != null);
            if(account != null)
                model.put("user", account);
            if(Boolean.parseBoolean(request.queryParamOrDefault("first", "false"))) {
                prop.put("body", Jade4J.render("./source/modules/default/body.jade", model));
                prop.put("footer", Jade4J.render("./source/modules/default/footer.jade", model));
            }
            module = "./source/modules/"+module+".jade";
            String html = Jade4J.render(module, model);
            if(method.equals("GET"))
                return html;
            prop.put("html", html);
            return Website.getGson().toJson(prop);
        } catch(Exception e) {
            e.printStackTrace();
            return render500(request, response);
        }
    }

    public static String renderList(HashMap<String, Object> model, Request request, Response response) {
        try {
            String sorted = renderPage("utils/list/sort", model, null, "GET", request, response);
            String filtered = renderPage("utils/list/filter", model, null, "GET", request, response);
            String list = renderPage("utils/list/list", model, null, "GET", request, response);
            Properties prop = new Properties();
            prop.put("success", true);
            prop.put("html", list);
            prop.put("sort", sorted);
            prop.put("filter", filtered);
            if(model.containsKey("activeFilter"))
                prop.put("activeFilter", true);
            if(model.containsKey("page"))
                prop.put("page", model.get("page"));
            if(model.containsKey("total"))
                prop.put("total", model.get("total"));
            return Website.getGson().toJson(prop);
        } catch(Exception e) {
            e.printStackTrace();
            return error("Error loading list. Please report this bug via Github.");
        }
    }

    public static String formatNameForProtocol(String name) {
        if (name == null)
            return "";
        name = name.replaceAll(" ", "_");
        name = name.toLowerCase();
        return name;
    }

    public static String formatNameForDisplay(String name) {
        if (name == null)
            return "";
        name = name.replaceAll("_", " ");
        name = name.toLowerCase();
        StringBuilder newName = new StringBuilder();
        boolean wasSpace = true;
        for (int i = 0; i < name.length(); i++) {
            if (wasSpace) {
                newName.append(("" + name.charAt(i)).toUpperCase());
                wasSpace = false;
            } else {
                newName.append(name.charAt(i));
            }
            if (name.charAt(i) == ' ') {
                wasSpace = true;
            }
        }
        return newName.toString();
    }

    public static ArrayList<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotation) throws ClassNotFoundException, IOException {
        ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive(packageName)) {
            if (!Class.forName(info.getName()).isAnnotationPresent(annotation))
                continue;
            classes.add(Class.forName(info.getName()));
        }
        return classes;
    }

    public static ExceptionHandler handleExceptions() {
        return (e, req, res) -> {
            res.status(500);
            System.out.println(e.getClass().getName());
            res.body("<h1>Exception occurred</h1><div>" + e.getMessage() + "</div>");
        };
    }

    public static String redirect(String redirect, Request request, Response response) {
        return redirect(redirect, 5, null, null, null, request, response);
    }

    public static String redirect(String redirect, String title, String titleColour, String extraInfo, Request request, Response response) {
        return redirect(redirect, 5, title, titleColour, extraInfo, request, response);
    }

    public static String redirect(String redirect, int time, String title, String titleColour, String extraInfo, Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("redirect", redirect);
        model.put("time", time);
        if(title != null || extraInfo != null) {
            model.put("title", title);
            model.put("titleColour", titleColour);
            model.put("extraInfo", extraInfo);
        }
        return renderPage("utils/redirect", model, request, response);
    }

    public static String render500(Request request, Response response) {
        return "";
    }

    public static String render404(Request request, Response response) {
        response.status(200);
        HashMap<String, Object> model = new HashMap<>();
        model.put("random", getRandomImageLink());
        return renderPage("utils/404", model, request, response);
    }

    public static String getRandomImageLink() {
        File[] files = new File("./public/images/404/").listFiles();
        assert files != null : "Unable to find 404 images folder.";
        File random = files[new Random().nextInt(files.length)];
        return String.format("%simages/404/%s", Website.getProperties().getProperty("path"), random.getName());
    }

    public static String success(String html) {
        Properties properties = new Properties();
        properties.put("success", true);
        properties.put("html", html);
        return Website.getGson().toJson(properties);
    }

    public static String error(String error) {
        Properties properties = new Properties();
        properties.put("success", false);
        properties.put("error", error);
        return Website.getGson().toJson(properties);
    }

    public static void initializeEndpoints() {
        long start = System.currentTimeMillis();
        int get = 0;
        int post = 0;
        int classCount = 0;
        boolean classChecked = false;
        try {
            ArrayList<Class<?>> classes = Utilities.getClassesWithAnnotation("com.cryo", EndpointSubscriber.class);
            for(Class<?> clazz : classes) {
                if(!clazz.isAnnotationPresent(EndpointSubscriber.class)) continue;
                classChecked = false;
                for(Method method : clazz.getMethods()) {
                    int count = method.getParameterCount();
                    if((count != 2 && count != 3) || (!method.isAnnotationPresent(Endpoint.class) && !method.isAnnotationPresent(SPAEndpoint.class) && !method.isAnnotationPresent(SPAEndpoints.class))) continue;
                    if(!Modifier.isStatic(method.getModifiers())) {
                        Logger.log("EndpointInitializer", "Expected method to be static in order for endpoint to work! "+method.getName());
                        throw new RuntimeException();
                    }
                    if(method.getReturnType() != String.class) {
                        Logger.log("EndpointInitializer", "Expected endpoint to return a String! "+method.getName());
                        throw new RuntimeException();
                    }
                    if(!classChecked) {
                        classCount++;
                        classChecked = true;
                    }
                    if(method.isAnnotationPresent(SPAEndpoints.class)) {
                        SPAEndpoints endpoint = method.getAnnotation(SPAEndpoints.class);
                        assert !endpoint.value().equals("") : "Invalid endpoint: "+method.getName()+" in "+method.getDeclaringClass().getSimpleName();
                        for(String endpointString : endpoint.value().split(", ")) {
                            get++;
                            post++;
                            get(endpointString, (req, res) -> {
                                Object[] parameters = new Object[count];
                                parameters[0] = count == 3 ? endpointString : req;
                                parameters[1] = count == 3 ? req : res;
                                if(count == 3)
                                    parameters[2] = res;
                                return method.invoke(null, parameters);
                            });
                            post(endpointString, (req, res) -> {
                                Object[] parameters = new Object[count];
                                parameters[0] = count == 3 ? endpointString : req;
                                parameters[1] = count == 3 ? req : res;
                                if(count == 3)
                                    parameters[2] = res;
                                return method.invoke(null, parameters);
                            });
                        }
                        continue;
                    }
                    if (method.isAnnotationPresent(SPAEndpoint.class)) {
                        SPAEndpoint endpoint = method.getAnnotation(SPAEndpoint.class);
                        assert !endpoint.value().equals("") : "Invalid endpoint: "+method.getName()+" in "+method.getDeclaringClass().getSimpleName();
                        get++;
                        post++;
                        get(endpoint.value(), (req, res) -> {
                            Object[] parameters = new Object[count];
                            parameters[0] = count == 3 ? endpoint.value() : req;
                            parameters[1] = count == 3 ? req : res;
                            if(count == 3)
                                parameters[2] = res;
                            return method.invoke(null, parameters);
                        });
                        post(endpoint.value(), (req, res) -> {
                            Object[] parameters = new Object[count];
                            parameters[0] = count == 3 ? endpoint.value() : req;
                            parameters[1] = count == 3 ? req : res;
                            if(count == 3)
                                parameters[2] = res;
                            return method.invoke(null, parameters);
                        });
                        continue;
                    }
                    Endpoint endpoint = method.getAnnotation(Endpoint.class);
                    if(!endpoint.values()[0].equals("")) {
                        int index = 0;
                        while(index < endpoint.values().length) {
                            String endpointMethod = endpoint.values()[index++];
                            String endpointString = endpoint.values()[index++];
                            if(endpointMethod.equals("GET")) {
                                get(endpointString, (req, res) -> {
                                    Object[] parameters = new Object[count];
                                    parameters[0] = count == 3 ? endpointString : req;
                                    parameters[1] = count == 3 ? req : res;
                                    if(count == 3)
                                        parameters[2] = res;
                                    return method.invoke(null, parameters);
                                });
                                get++;
                            } else {
                                post(endpointString, (req, res) -> {
                                    Object[] parameters = new Object[count];
                                    parameters[0] = count == 3 ? endpointString : req;
                                    parameters[1] = count == 3 ? req : res;
                                    if(count == 3)
                                        parameters[2] = res;
                                    return method.invoke(null, parameters);
                                });
                                post++;
                            }
                        }
                    } else {
                        if(endpoint.method().equals("GET")) {
                            get(endpoint.endpoint(), (req, res) -> {
                                Object[] parameters = new Object[count];
                                parameters[0] = count == 3 ? endpoint.endpoint() : req;
                                parameters[1] = count == 3 ? req : res;
                                if(count == 3)
                                    parameters[2] = res;
                                return method.invoke(null, parameters);
                            });
                            get++;
                        } else {
                            post(endpoint.endpoint(), (req, res) -> {
                                Object[] parameters = new Object[count];
                                parameters[0] = count == 3 ? endpoint.endpoint() : req;
                                parameters[1] = count == 3 ? req : res;
                                if(count == 3)
                                    parameters[2] = res;
                                return method.invoke(null, parameters);
                            });
                            post++;
                        }
                    }
                }
            }
        } catch(Exception e) {
            Logger.handle(e);
        }
        long end = System.currentTimeMillis();
        Logger.log("EndpointInitializer", "Loaded "+get+" GET and "+post+" POST endpoints from "+classCount+" classes in "+(end-start)+"ms.");
    }

    public static void sendStartupHooks() {
        long start = System.currentTimeMillis();
        int startup = 0;
        try {
            ArrayList<Class<?>> classes = Utilities.getClassesWithAnnotation("com.cryo", WebStartSubscriber.class);
            for(Class<?> clazz : classes) {
                if(!clazz.isAnnotationPresent(WebStartSubscriber.class)) continue;
                for(Method method : clazz.getMethods()) {
                    if(method.getParameterCount() != 0 || !method.isAnnotationPresent(WebStart.class)) continue;
                    if(!Modifier.isStatic(method.getModifiers())) {
                        Logger.log("WebStartInitializer", "Expected startup method to be static! "+method.getName()+" in "+clazz.getSimpleName());
                        throw new RuntimeException();
                    }
                    if(method.getReturnType() != void.class) {
                        Logger.log("WebStartInitializer", "Expected startup method to be void! "+method.getName()+":"+method.getReturnType());
                        throw new RuntimeException();
                    }
                    method.invoke(null);
                    startup++;
                }
            }
        } catch(Exception e) {
            Logger.handle(e);
        }
        long end = System.currentTimeMillis();
        Logger.log("WebStartInitializer", "Executed "+startup+" startup methods in "+(end-start)+"ms.");
    }

    @SuppressWarnings({ "rawtypes" })
    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    @SuppressWarnings("rawtypes")
    private static List<Class> findClasses(File directory, String packageName) {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (Throwable e) {

                }
            }
        }
        return classes;
    }

    public static int getLevelForXp(int skill, double exp) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= (skill == 24 ? 120 : 99); lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) Math.floor(points / 4);
            if ((output - 1) >= exp) {
                return lvl;
            }
        }
        return skill == 24 ? 120 : 99;
    }

    public static long roundUp(long num, long divisor) {
        return (num + divisor - 1) / divisor;
    }

    public static String checkCaptchaResult(String response) {
        HttpRequestWithBody body = Unirest.post("https://www.google.com/recaptcha/api/siteverify?secret="+Website.getProperties().getProperty("captcha_secret_key")+"&response="+response);
        try {
            HashMap<String, Object> obj = Website.getGson().fromJson(body.asString().getBody(), HashMap.class);
            if (obj == null) return error("Error loading recaptcha response. Please refresh the page and try again.");
            if (!obj.containsKey("success") || !obj.containsKey("challenge_ts"))
                return error("Error loading recaptcha response. Please refresh the page and try again.");
            boolean success = (boolean) obj.get("success");
            if(!success) return error("You failed the recaptcha. Please refresh the page and try again.");
            DateTime dt = new DateTime(obj.get("challenge_ts"));
            if(FormatUtils.getDateDiff(dt.toDate(), new Date(), TimeUnit.MINUTES) > 10) return error("Token has expired. Please refresh the page and try again.");
        } catch (UnirestException e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
        return null;
    }

    public static String generateRandomString(int length) {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static void sendEmail(String email, String subject, String message) {
        Session session = null;
        final String email_user = (String) Website.getProperties().get("email");
        final String password = (String) Website.getProperties().get("email_pass");
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", true);

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email_user, password);
                }
            };

            session = Session.getDefaultInstance(props, auth);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        try {

            Message mime = new MimeMessage(session);
            mime.setFrom(new InternetAddress(email_user));
            mime.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            mime.setSubject(subject);
            mime.setContent(message, "text/html");


            try {
                Transport.send(mime);
            } catch(Exception e) {
                e.printStackTrace();
            }

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
