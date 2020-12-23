package com.cryo;

import com.cryo.entities.Endpoint;
import com.cryo.entities.EndpointSubscriber;
import com.cryo.utils.CorsFilter;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.management.RuntimeErrorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Properties;

import static spark.Spark.*;

@Slf4j
public class Website {

    @Getter
    private static Website INSTANCE;

    @Getter
    private static Properties properties;

    @Getter
    private static Gson gson;

    @Getter
    private ConnectionManager connectionManager;

    public void load() {
        buildGson();
        loadProperties();
        port(Integer.parseInt(properties.getProperty("port", "8085")));
        exception(Exception.class, Utilities.handleExceptions());
        staticFiles.externalLocation("public/");
        staticFiles.expireTime(0); // ten minutes
        staticFiles.header("Access-Control-Allow-Origin", "*");
        CorsFilter.apply();

        connectionManager = new ConnectionManager();

        Utilities.initializeEndpoints();

        get("*", Utilities::render404);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log(Website.class, "Shutdown hook caught. Website shutting down!");
            stop();
        }));

        Utilities.sendStartupHooks();
        Logger.log(Website.class, "Website is now listening on port: "+properties.getProperty("port"));
    }

    public static DBConnection getConnection(String schema) {
        return INSTANCE.connectionManager.getConnection(schema);
    }

    public static void buildGson() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setVersion(1.0)
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
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
            throw new RuntimeException("Error loading properties!", e);
        }
    }

    public static void main(String[] args) {
        INSTANCE = new Website();
        INSTANCE.load();
    }

}
