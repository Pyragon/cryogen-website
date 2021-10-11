package com.cryo.utils;

import com.cryo.Website;
import com.cryo.cache.Cache;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.*;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.Login;
import com.google.common.reflect.ClassPath;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mysql.cj.util.StringUtils;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.post;

public class Utilities {

    private static final Random RANDOM = new Random();

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
            prop.put("rights", account == null ? 0 : account.getRights());
            model.put("loggedIn", account != null);
            if(account != null)
                model.put("user", account);
            if(Boolean.parseBoolean(request.queryParamOrDefault("first", "false"))) {
                prop.put("body", Jade4J.render("./source/modules/default/body.jade", model));
                prop.put("footer", Jade4J.render("./source/modules/default/footer.jade", model));
            }
            if(model.containsKey("404"))
                prop.put("404", true);
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
            if(model.containsKey("itemId")) {
                prop.put("itemId", model.get("itemId"));
                prop.put("itemName", model.get("itemName"));
                prop.put("uid", model.get("uid"));
            }
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
        model.put("404", true);
        return renderPage("utils/404", model, request, response);
    }

    public static String getRandomImageLink() {
        File[] files = new File("./public/images/404/").listFiles();
        assert files != null : "Unable to find 404 images folder.";
        File random = files[new Random().nextInt(files.length)];
        return String.format("%simages/404/%s", Website.getProperties().getProperty("path"), random.getName());
    }

    public static String success() {
        return success(null);
    }

    public static String success(String html) {
        Properties properties = new Properties();
        properties.put("success", true);
        if(html != null)
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
                        Logger.log("EndpointInitializer", "Expected method to be static in order for endpoint to work! "+method.getName(), true);
                        throw new RuntimeException();
                    }
                    if(method.getReturnType() != String.class) {
                        Logger.log("EndpointInitializer", "Expected endpoint to return a String! "+method.getName(), true);
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
            record MethodData(int priority, Method method) {}
            ArrayList<Class<?>> classes = Utilities.getClassesWithAnnotation("com.cryo", WebStartSubscriber.class);
            ArrayList<MethodData> methods = new ArrayList<>();
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
                    WebStart webStart = method.getAnnotation(WebStart.class);
                    methods.add(new MethodData(webStart.priority(), method));
                    startup++;
                }
            }
            methods.stream().sorted(Comparator.comparingInt(m -> m.priority)).forEach(m -> {
                try {
                    m.method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch(Exception e) {
            Logger.handle(e);
            Logger.handle(e.getCause());
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

    public static final int packGJString2(int position, byte[] buffer, String String) {
        int length = String.length();
        int offset = position;
        for (int index = 0; length > index; index++) {
            int character = String.charAt(index);
            if (character > 127) {
                if (character > 2047) {
                    buffer[offset++] = (byte) ((character | 919275) >> 12);
                    buffer[offset++] = (byte) (128 | ((character >> 6) & 63));
                    buffer[offset++] = (byte) (128 | (character & 63));
                } else {
                    buffer[offset++] = (byte) ((character | 12309) >> 6);
                    buffer[offset++] = (byte) (128 | (character & 63));
                }
            } else buffer[offset++] = (byte) character;
        }
        return offset - position;
    }

    public static String readString(byte[] buffer, int i_1, int i_2) {
        char[] arr_4 = new char[i_2];
        int offset = 0;

        for (int i_6 = 0; i_6 < i_2; i_6++) {
            int i_7 = buffer[i_6 + i_1] & 0xff;
            if (i_7 != 0) {
                if (i_7 >= 128 && i_7 < 160) {
                    char var_8 = CP_1252_CHARACTERS[i_7 - 128];
                    if (var_8 == 0) {
                        var_8 = 63;
                    }

                    i_7 = var_8;
                }

                arr_4[offset++] = (char) i_7;
            }
        }

        return new String(arr_4, 0, offset);
    }

    public static char[] CP_1252_CHARACTERS = { '\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6',
            '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c',
            '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153',
            '\0', '\u017e', '\u0178' };

    public static char cp1252ToChar(byte i) {
        int i_35_ = i & 0xff;
        if (0 == i_35_) {
            throw new IllegalArgumentException("Non cp1252 character 0x" + Integer.toString(i_35_, 16) + " provided");
        }
        if (i_35_ >= 128 && i_35_ < 160) {
            int i_36_ = CP_1252_CHARACTERS[i_35_ - 128];
            if (0 == i_36_) {
                i_36_ = 63;
            }
            i_35_ = i_36_;
        }
        return (char) i_35_;
    }

    public static final int getNPCDefinitionsSize() {
        int lastArchiveId = Cache.STORE.getIndices()[18].getLastArchiveId();
        return lastArchiveId * 128 + Cache.STORE.getIndices()[18].getValidFilesCount(lastArchiveId);
    }

    public static final int getItemDefinitionsSize() {
        int lastArchiveId = Cache.STORE.getIndices()[19].getLastArchiveId();
        return (lastArchiveId * 256 + Cache.STORE.getIndices()[19].getValidFilesCount(lastArchiveId))+1;
    }

    public static long roundUp(long num, long divisor) {
        return (num + divisor - 1) / divisor;
    }

    public static boolean isNullOrEmpty(String... strings) {
        for(String str : strings) {
            if(StringUtils.isNullOrEmpty(str))
                return true;
        }
        return false;
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

    public static final int random(int maxValue) {
        if (maxValue <= 0) return 0;
        return RANDOM.nextInt(maxValue);
    }

    public static final int random(int min, int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : random(n));
    }

    private static final int[] HSL_2_RGB = new int[65536];

    static {
        double d = 0.7D;
        int i = 0;

        for(int i1 = 0; i1 != 512; ++i1) {
            float f = ((float)(i1 >> 3) / 64.0F + 0.0078125F) * 360.0F;
            float f1 = 0.0625F + (float)(7 & i1) / 8.0F;

            for(int i2 = 0; i2 != 128; ++i2) {
                float f2 = (float)i2 / 128.0F;
                float f3 = 0.0F;
                float f4 = 0.0F;
                float f5 = 0.0F;
                float f6 = f / 60.0F;
                int i3 = (int)f6;
                int i4 = i3 % 6;
                float f7 = f6 - (float)i3;
                float f8 = f2 * (-f1 + 1.0F);
                float f9 = f2 * (-(f7 * f1) + 1.0F);
                float f10 = (1.0F - f1 * (-f7 + 1.0F)) * f2;
                if (i4 == 0)
                {
                    f3 = f2;
                    f5 = f8;
                    f4 = f10;
                }
                else if (i4 == 1)
                {
                    f5 = f8;
                    f3 = f9;
                    f4 = f2;
                }
                else if (i4 == 2)
                {
                    f3 = f8;
                    f4 = f2;
                    f5 = f10;
                }
                else if (i4 == 3)
                {
                    f4 = f9;
                    f3 = f8;
                    f5 = f2;
                }
                else if (i4 == 4)
                {
                    f5 = f2;
                    f3 = f10;
                    f4 = f8;
                }
                else
                {
                    f4 = f8;
                    f5 = f9;
                    f3 = f2;
                }

                HSL_2_RGB[i++] = (int)((float)Math.pow(f3, d) * 256.0F) << 16
                        | (int)((float)Math.pow(f4, d) * 256.0F) << 8
                        | (int)((float)Math.pow(f5, d) * 256.0F);
            }
        }
    }

    public static int hslToRgb(int hsl) {
        return HSL_2_RGB[hsl]& 0xffffff;
    }

    public static int getHashMapSize(int size) {
        size--;
        size |= size >>> -1810941663;
        size |= size >>> 2010624802;
        size |= size >>> 10996420;
        size |= size >>> 491045480;
        size |= size >>> 1388313616;
        return 1 + size;
    }

    public static final int getAnimationDefinitionsSize() {
        int lastArchiveId = Cache.STORE.getIndices()[20].getLastArchiveId();
        return lastArchiveId * 128 + Cache.STORE.getIndices()[20].getValidFilesCount(lastArchiveId);
    }

}
