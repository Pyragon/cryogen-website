package com.cryo.entities.forums;

import spark.Request;
import spark.Response;

public interface PrivateSection {

    String getName();

    String decode(String action, Request request, Response response);

}
