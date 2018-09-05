package com.cryo.modules.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.json.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class APIParameter {

    private final String name, type, description;
    private boolean optional;
    private Object defaultValue;

    public APIParameter setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public APIParameter setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public static void main(String[] args) {
        try {
            JsonObject object = Json.createReader(new FileInputStream("./data/endpoints.json")).readObject();
            StringBuilder builder = new StringBuilder();
            for(String key : object.keySet()) {
                System.out.println(key);
                JsonObject section = object.getJsonObject(key);
                if(!section.containsKey("endpoints")) continue;
                JsonArray endpoints = section.getJsonArray("endpoints");
                for(JsonValue value : endpoints) {
                    JsonObject endpoint = (JsonObject) value;
                    builder.append("(");
                    builder.append(endpoint.getInt("id")+", \""+endpoint.getString("endpoint")+"\", \""+endpoint.getString("description")+"\", "+endpoint.getInt("permissions"));
                    if(endpoint.get("request_method") instanceof JsonString)
                        builder.append(", \""+endpoint.getString("request_method")+"\"");
                    else {
                        builder.append(", new String[] { ");
                        JsonArray array = endpoint.getJsonArray("request_method");
                        for(int i = 0; i < array.size(); i++) {
                            builder.append("\""+array.getString(i)+"\"");
                            if(i != array.size()-1)
                                builder.append(", ");
                        }
                        builder.append(" }");
                    }
                    if(endpoint.containsKey("parameters")) {
                        builder.append(", new APIParameter[] { ");
                        JsonArray parameters = endpoint.getJsonArray("parameters");
                        for(int i = 0; i < parameters.size(); i++) {
                            JsonObject parameter = parameters.getJsonObject(i);
                            builder.append("new APIParameter(\""+parameter.getString("name")+"\", \""+parameter.getString("type")+"\", \""+parameter.getString("description")+"\"");
                            if(parameter.containsKey("optional") || parameter.containsKey("default")) {
                                builder.append(", "+(parameter.containsKey("optional") ? parameter.getBoolean("optional") : "false"));
                                builder.append(", "+(parameter.containsKey("default") ? parameter.get("default") : "null"));
                            }
                            builder.append(")");
                            if(i != parameters.size()-1)
                                builder.append(",");
                        }
                        builder.append(" }");
                    } else
                        builder.append(", null");
                    if(endpoint.containsKey("return")) {
                        builder.append(", new APIReturn[] { ");
                        JsonArray returns = endpoint.getJsonArray("return");
                        for(int i = 0; i < returns.size(); i++) {
                            JsonObject returnO = returns.getJsonObject(i);
                            builder.append("new APIReturn(\""+returnO.getString("name")+"\", \""+returnO.getString("type")+"\", \""+returnO.getString("description")+"\")");
                            if(i != returns.size()-1)
                                builder.append(",");
                        }
                        builder.append(" }");
                    } else
                        builder.append(", null");
                    builder.append(")");
                    System.out.println(builder.toString());
                    builder = new StringBuilder();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
