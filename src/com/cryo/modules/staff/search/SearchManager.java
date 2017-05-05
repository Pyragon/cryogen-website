package com.cryo.modules.staff.search;

import java.io.IOException;
import java.util.ArrayList;

import com.cryo.utils.Utilities;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:38:12 AM
 */
public class SearchManager {
	
	private ArrayList<Filter> filters;
	
	public void load() {
		filters = new ArrayList<>();
		try {
			for(Class<?> c : Utilities.getClasses("com.cryo.modules.staff.search.impl")) {
				Object instance = c.newInstance();
				if(!(instance instanceof Filter))
					continue;
				filters.add((Filter) instance);
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public Filter getFilter(String name) {
		for(Filter filter : filters) {
			if(filter.getName().equalsIgnoreCase(name))
				return filter;
		}
		return null;
	}
	
}
