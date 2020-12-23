package com.cryo.utils;

import spark.Filter;
import spark.Spark;

import java.util.HashMap;

public final class CorsFilter {
    
    private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();
    
    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public final static void apply() {
        Filter filter = (request, response) -> corsHeaders.forEach((key, value) -> response.header(key, value));
        Spark.after(filter);
    }
    
    /**
     * Usage
     */
    public static void main(String[] args) {
        CorsFilter.apply(); // Call this before mapping thy routes
    }
}
