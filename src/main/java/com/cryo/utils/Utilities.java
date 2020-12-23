package com.cryo.utils;

import com.cryo.Website;
import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.entities.WebStart;
import com.cryo.entities.WebStartSubscriber;
import com.google.common.reflect.ClassPath;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

public class Utilities {

    public static String renderPage(String module, HashMap<String, Object> model, Request request, Response response) {
        if(model == null)
            model = new HashMap<>();
        model.put("format", new FormatUtils());
        module = "./public/modules/"+module+".jade";
        try {
            return Jade4J.render(module, model);
        } catch(Exception e) {
            e.printStackTrace();
            return render404(request, response);
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

    public static String render500(Request request, Response response) {
        return "";
    }

    public static String render404(Request request, Response response) {
        response.status(404);
        HashMap<String, Object> model = new HashMap<>();
        model.put("random", getRandomImageLink());
        try {
            return Jade4J.render("./public/modules/utils/404.jade", model);
        } catch (JadeCompilerException | IOException e) {
            e.printStackTrace();
        }
        return error("Error rendering 404 page! Don't worry, we have put the hamsters back on their wheels! Shouldn't be long...");
    }

    public static String getRandomImageLink() {
        File[] files = new File("./public/images/404/").listFiles();
        File random = files[new Random().nextInt(files.length)];
        return String.format("%simages/404/%s", Website.getProperties().getProperty("path"), random.getName());
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
        try {
            ArrayList<Class<?>> classes = Utilities.getClassesWithAnnotation("com.cryo", EndpointSubscriber.class);
            for(Class<?> clazz : classes) {
                if(!clazz.isAnnotationPresent(EndpointSubscriber.class)) continue;
                for(Method method : clazz.getMethods()) {
                    int count = method.getParameterCount();
                    if((count != 2 && count != 3) || !method.isAnnotationPresent(Endpoint.class)) continue;
                    if(!Modifier.isStatic(method.getModifiers())) {
                        Logger.log("EndpointInitializer", "Expected method to be static in order for endpoint to work! "+method.getName());
                        throw new RuntimeException();
                    }
                    if(method.getReturnType() != String.class) {
                        Logger.log("EndpointInitializer", "Expected endpoint to return a String! "+method.getName());
                        throw new RuntimeException();
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
        Logger.log("EndpointInitializer", "Loaded "+get+" GET and "+post+" POST endpoints in "+(end-start)+"ms.");
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
                        Logger.log("WebStartInitializer", "Expected startup method to be static! "+method.getName());
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

}
