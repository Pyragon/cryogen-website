package com.cryo;

import com.cryo.cache.Cache;
import com.cryo.managers.TaskManager;
import com.cryo.utils.CorsFilter;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ipinfo.api.IPInfo;
import io.ipinfo.api.IPInfoBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;

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
    private static JDA JDA;

    @Getter
    private static UserAgentAnalyzer userAgentAnalyzer;

    @Getter
    private ConnectionManager connectionManager;

    @Getter
    private static IPInfo IPLookup;

    private Timer fastExecutor;

    public void load() {
        long start = System.currentTimeMillis();

        buildGson();
        loadProperties();
        port(Integer.parseInt(properties.getProperty("port", "8085")));
        if(properties.contains("cert_path")) {
            String path = properties.getProperty("cert_path");
            String pass = properties.getProperty("cert_key");
            secure(path, pass, null, null);
        }
        exception(Exception.class, Utilities.handleExceptions());
        staticFiles.externalLocation("public/");
        staticFiles.expireTime(0); // ten minutes
        staticFiles.header("Access-Control-Allow-Origin", "*");
        CorsFilter.apply();

        connectionManager = new ConnectionManager();
        fastExecutor = new Timer();
        IPLookup = new IPInfoBuilder()
                    .setToken(properties.getProperty("ip_info_token"))
                    .build();
        buildJDA();
        buildUserAgentAnalyzer();

        Utilities.initializeEndpoints();

        redirect.get("/discord", "https://discord.gg/SxHFJdhq5N");
        get("*", Utilities::render404);
        post("*", Utilities::render404);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log(Website.class, "Shutdown hook caught. Website shutting down!");
            stop();
        }));

        Utilities.sendStartupHooks();
        fastExecutor.schedule(new TaskManager(), 1000, 1000);
        Logger.log(Website.class, "Website started in "+(System.currentTimeMillis()-start)+"ms and is now listening on port: "+properties.getProperty("port"));
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

    public static void buildJDA() {
        try {
            JDA = JDABuilder
                    .createDefault(properties.getProperty("discord_token"))
                    .build();
            JDA.awaitReady();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void buildUserAgentAnalyzer() {
        userAgentAnalyzer = UserAgentAnalyzer
                .newBuilder()
                .withCache(1234)
                .withField("DeviceClass")
                .withAllFields()
                .build();
    }

    public static void loadProperties() {
        properties = loadProperties("data/props.json");
    }

    public static Properties loadProperties(String fileName) {
        File file = new File(fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null)
                builder.append(line);
            String json = builder.toString();
            Properties properties = getGson().fromJson(json, Properties.class);
            reader.close();
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties!", e);
        }
    }

    public static void main(String[] args) {
        INSTANCE = new Website();
        INSTANCE.load();
    }

}
