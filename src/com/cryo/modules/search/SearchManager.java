package com.cryo.modules.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import com.cryo.Website;
import com.cryo.modules.WebModule;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.CookieManager;
import com.cryo.utils.Utilities;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class SearchManager {
	
	private HashMap<String, Class<?>> filters;
	
	public void load() {
		filters = new HashMap<>();
		try {
			for(Class<?> c : Utilities.getClasses("com.cryo.modules.search.impl")) {
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
	
	public static void registerEndpoints(Website website) {
		post("/search/:module", (req, res) -> new Gson().toJson(handleEndpoint(req.params(":module"), req, res)));
		get("/search/:module", (req, res) -> new Gson().toJson(handleEndpoint(req.params(":module"), req, res)));
	}
	
	public static Properties handleEndpoint(String module, Request request, Response response) {
		String query = request.queryParams("query");
		String pageStr = request.queryParams("page");
		String archivedStr = request.queryParams("archived");
		String paramStr = request.queryParams("params");
		String searchName = request.queryParams("searchname");
		Properties prop = new Properties();
		if(Utilities.isNullOrEmpty(query, pageStr, archivedStr)) {
			prop.put("success", false);
			prop.put("error", "Missing some parameters required for searching.");
			return prop;
		}
		prop = search(module, query, Integer.parseInt(pageStr), Boolean.parseBoolean(archivedStr), request, response, paramStr == null ? new HashMap<String, String>() : new Gson().fromJson(paramStr, HashMap.class), searchName);
		return prop;
	}
	
	public static Properties search(String module, String text, int page, boolean archived, Request request, Response response, HashMap<String, String> params, String searchName) {
		Optional<SearchEndpoints> optional = SearchEndpoints.getEndpoint(searchName);
		Account account = CookieManager.getAccount(request);
		Properties prop = new Properties();
		if(account == null)
			return prop;
		while(true) {
			if(!optional.isPresent()) {
				prop.put("success", false);
				prop.put("error", "Invalid endpoint!");
				break;
			}
			SearchEndpoints endpoint = optional.get();
			if(endpoint.getRights() > 0) {
				if(account.getRights() < endpoint.getRights()) {
					prop.put("success", false);
					prop.put("error", "Insuficcient permissions");
					break;
				}
			}
			HashMap<String, Object> model = new HashMap<>();
			text = text.replaceAll(", ", ",");
			String[] queries = text.split(",");
			if(queries.length == 0) {
				prop.put("success", false);
				prop.put("error", "Invalid search parameters");
				break;
			}
			HashMap<String, Filter> filters = new HashMap<>();
			HashMap<String, String> sFilters = new HashMap<>();
			boolean incorrect = false;
			for(String query : queries) {
				if(!query.contains(":")) {
					prop.put("success", false);
					prop.put("error", "Invalid search parameters. Please read instructions on how to search.");
					return prop;
				}
				String[] values = query.split(":");
				if(values.length != 2)
					continue;
				String filterName = values[0];
				String value = values[1].toLowerCase();
				Filter filter = Website.instance().getSearchManager().getFilter(filterName);
				if(filter == null) {
					incorrect = true;
					continue;
				}
				if(filters.containsKey(filter.getName())) {
					prop.put("success", false);
					prop.put("error", "You cannot have two of the same filters.");
					return prop;
				}
				if(!filter.appliesTo(searchName, archived)) {
					incorrect = true;
					continue;
				}
				if(value.startsWith(" "))
					value = value.replaceFirst(" ", "");
				if(!filter.setValue(module, value)) {
					incorrect = true;
					continue;
				}
				filters.put(filter.getName(), filter);
				sFilters.put(filter.getName(), value);
			}
			if(filters.size() == 0 && incorrect == true) {
				prop.put("success", false);
				prop.put("error", "Search contains invalid keys.");
				return prop;
			}
			ArrayList<Filter> filterA = new ArrayList<>();
			filterA.addAll(filters.values());
			String username = endpoint.getRights() == 0 ? account.getUsername() : null;
			Object[] data = endpoint.getConnection().handleRequest("search", getQueryValue(module, filterA), page, archived, params, username, module);
			Object[] countData = endpoint.getConnection().handleRequest("search-results", getQueryValue(module, filterA), archived, params, username, module);
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
			model.put(endpoint.getKey(), results);
			model.put("staff", endpoint.getRights() > 0);
			String html = null;
			try {
				html = WebModule.render(endpoint.getJadeFile(), model, request, response);
			} catch(Exception e) {
				e.printStackTrace();
				prop.put("success", false);
				prop.put("error", "Error loading search list.");
				return prop;
			}
			prop.put("success", true);
			prop.put("html", html);
			prop.put("pageTotal", resultSize);
			prop.put("filters", sFilters);
			return prop;
		}
		return prop;
	}
	
	public static Properties getQueryValue(String mod, ArrayList<Filter> filters) {
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
