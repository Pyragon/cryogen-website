package com.cryo.modules.staff.entities;

import spark.Request;
import spark.Response;

public interface StaffSection {
	
	public String getName();
	
	public String decode(String action, Request request, Response response);

}
