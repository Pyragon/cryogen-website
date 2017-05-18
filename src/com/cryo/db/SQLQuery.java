package com.cryo.db;

import java.sql.ResultSet;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 16, 2017 at 2:43:08 AM
 */
@FunctionalInterface
public interface SQLQuery {
	
	public Object[] handleResult(ResultSet set);
	
}
