package com.cryo.modules.staff.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.cryo.Website;
import com.cryo.db.DatabaseConnection;
import com.cryo.modules.search.Filter;
import com.cryo.utils.Utilities;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:38:12 AM
 */
public class SearchManager {
	
	private HashMap<String, Class<?>> filters;
	
	public void load() {
		filters = new HashMap<>();
		try {
			for(Class<?> c : Utilities.getClasses("com.cryo.modules.staff.search.impl")) {
				Object instance = c.newInstance();
				if(!(instance instanceof Filter))
					continue;
				filters.put(((Filter) instance).name, c);
			}
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public Filter getFilter(String name) {
		if(!filters.containsKey(name)) return null;
		Class<?> o = filters.get(name);
		try {
			return (Filter) o.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Properties search(String mod, String text, int page, DatabaseConnection connection, Object... params) {
		return search(mod, text, page, false, connection);
	}
	
	public Properties search(String mod, String text, int page, boolean archive, DatabaseConnection connection, Object... params) {
		text = text.replaceAll(", ", ",");
		String[] queries = text.split(",");
		HashMap<String, Filter> filters = new HashMap<>();
		HashMap<String, String> sFilters = new HashMap<>();
		boolean incorrect = false;
		Properties prop = new Properties();
		for(String query : queries) {
			String[] values = query.split(":");
			if(values.length != 2)
				continue;
			String filterName = values[0];
			String value = values[1].toLowerCase();
			Filter filter = Website.instance().getSearchManager().getFilter(filterName);
			if(filter == null)
				continue;
			if(filters.containsKey(filter.getName())) {
				prop.put("success", false);
				prop.put("error", "You cannot have two of the same filters.");
				return prop;
			}
			if(!filter.appliesTo(mod)) {
				incorrect = true;
				continue;
			}
			if(!filter.setValue(mod, value)) {
				incorrect = true;
				continue;
			}
			filters.put(filter.getName(), filter);
			sFilters.put(filter.getName(), value);
		}
		ArrayList<Filter> filterA = new ArrayList<>();
		filterA.addAll(filters.values());
		Object[] data = connection.handleRequest("search-"+mod, getQueryValue(mod, filterA), page, archive, params);
		Object[] countData = connection.handleRequest("search-results-"+mod, getQueryValue(mod, filterA), archive, params);
		if(data == null || countData == null) {
			prop.put("success", false);
			String results = "No search results found.";
			if(incorrect)
				results += " Your search query contained invalid filters.";
			prop.put("error", results);
			return prop;
		}
		List<?> results = (List<?>) data[0];
		if(results.size() == 0) {
			prop.put("success", false);
			prop.put("error", "No search results found.");
			return prop;
		}
		for(Filter filter : filterA)
			results = filter.filterList(results);
		int resultSize = (int) countData[0];
		prop.put("success", true);
		prop.put("results", results);
		prop.put("pageTotal", resultSize);
		prop.put("filters", sFilters);
		return prop;
	}
	
	public Properties getQueryValue(String mod, ArrayList<Filter> filters) {
		if(filters.size() == 0) return null;
		List<Filter> applicable = filters.stream().filter(f -> {
			return f.getFilter(mod) != null;
		}).collect(Collectors.toList());
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < applicable.size(); i++) {
			Filter filter = applicable.get(i);
			builder.append(filter.getFilter(mod));
			if(i != applicable.size()-1)
				builder.append(" AND ");
		}
		ArrayList<Object> valueList = new ArrayList<>();
		for(Filter filter : applicable) {
			if(filter.getFilter(mod) != null && filter.getValue() != null)
				valueList.add(filter.getValue());
		}
		Object[] values = valueList.toArray(new Object[valueList.size()]);
		Properties prop = new Properties();
		prop.put("query", builder.toString());
		prop.put("values", values);
		return prop;
	}
	
}
