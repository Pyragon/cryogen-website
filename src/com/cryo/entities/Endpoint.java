package com.cryo.entities;

import spark.Request;
import spark.Response;

@FunctionalInterface
public interface Endpoint {

    String interact(String endpoint, Request request, Response response);

}
