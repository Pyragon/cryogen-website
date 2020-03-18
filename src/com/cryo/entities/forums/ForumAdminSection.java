package com.cryo.entities.forums;

import spark.Request;
import spark.Response;

public interface ForumAdminSection {

    public String getName();

    public String decode(String action, Request request, Response response);
}