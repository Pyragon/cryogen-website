package com.cryo.managers;

import com.cryo.entities.accounts.Account;
import com.cryo.entities.list.*;
import com.cryo.entities.shop.Package;
import com.cryo.utils.Utilities;
import com.google.common.base.CaseFormat;
import com.mysql.cj.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ListManager {

    public static <T> void buildTable(HashMap<String, Object> model, String module, List<T> list, Class<?> clazz, Account account, ArrayList<ArrayList<Object>> sortValues, ArrayList<ArrayList<Object>> filterValues, boolean archive) {
        try {
            List<ListRowValue> columns = new ArrayList<>();
            List<ListRow> rows = new ArrayList<>();
            boolean ordered = false;
            if (list.size() == 0) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(ListValue.class)) continue;
                    ListValue annotation = field.getAnnotation(ListValue.class);
                    if (annotation.onArchive() && !archive) continue;
                    if (annotation.notOnArchive() && archive) continue;
                    if(!annotation.requiresModule().equals("") && !annotation.requiresModule().equals(module)) continue;
                    ListRowValue value = new ListRowValue(annotation.value());
                    if (columns.contains(value)) continue;
                    if (annotation.order() != -1) {
                        value.setOrder(annotation.order());
                        ordered = true;
                    }
                    columns.add(value);
                }
                for (Method method : clazz.getMethods()) {
                    if (!method.isAnnotationPresent(ListValue.class)) continue;
                    ListValue annotation = method.getAnnotation(ListValue.class);
                    if (annotation.onArchive() && !archive) continue;
                    if (annotation.notOnArchive() && archive) continue;
                    if(!annotation.requiresModule().equals("") && !annotation.requiresModule().equals(module)) continue;
                    ListRowValue value = new ListRowValue(annotation.value());
                    if (columns.contains(value)) continue;
                    if (annotation.order() != -1) {
                        value.setOrder(annotation.order());
                        ordered = true;
                    }
                    columns.add(value);
                }
                if (ordered)
                    columns = columns.stream().sorted(Comparator.comparingInt(ListRowValue::getOrder)).collect(Collectors.toList());
                model.put("columns", columns);
                model.put("rows", rows);
                buildSortable(model, clazz, sortValues, archive);
                buildFilterable(model, clazz, filterValues, archive);
                return;
            }
            for (T t : list) {
                Field f = t.getClass().getDeclaredField("id");
                f.setAccessible(true);
                ListRow row = new ListRow((int) f.get(t));
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(ListValue.class)) continue;
                    ListValue annotation = field.getAnnotation(ListValue.class);
                    if (annotation.onArchive() && !archive) continue;
                    if (annotation.notOnArchive() && archive) continue;
                    if(!annotation.requiresModule().equals("") && !annotation.requiresModule().equals(module)) continue;
                    ListRowValue value = new ListRowValue(annotation.value());
                    if (!columns.contains(value)) {
                        if (annotation.order() != -1) {
                            value.setOrder(annotation.order());
                            ordered = true;
                        }
                        columns.add(value);
                    }
                    try {
                        field.setAccessible(true);
                        value = annotation.returnsValue() ? (ListRowValue) field.get(t) : getValue(field.get(t), annotation);
                        row.getValues().add(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        row.getValues().add(new ListRowValue("Error"));
                    }
                }
                for (Method method : clazz.getMethods()) {
                    if (!method.isAnnotationPresent(ListValue.class)) continue;
                    ListValue annotation = method.getAnnotation(ListValue.class);
                    if (annotation.onArchive() && !archive) continue;
                    if (annotation.notOnArchive() && archive) continue;
                    if(!annotation.requiresModule().equals("") && !annotation.requiresModule().equals(module)) continue;
                    ListRowValue value = new ListRowValue(annotation.value());
                    if (!columns.contains(value)) {
                        if (annotation.order() != -1) {
                            value.setOrder(annotation.order());
                            ordered = true;
                        }
                        columns.add(value);
                    }
                    try {
                        Object[] args = new Object[method.getParameterCount()];
                        if(args.length > 0)
                            args[0] = account;
                        value = annotation.returnsValue() ? (ListRowValue) method.invoke(t, args) : getValue(method.invoke(t, args), annotation);
                        row.getValues().add(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        row.getValues().add(new ListRowValue("Error"));
                    }
                }
                if (ordered)
                    row.setValues(row.getValues().stream().sorted(Comparator.comparingInt(ListRowValue::getOrder)).collect(Collectors.toList()));
                rows.add(row);
            }
            if (ordered)
                columns = columns.stream().sorted(Comparator.comparingInt(ListRowValue::getOrder)).collect(Collectors.toList());
            model.put("columns", columns);
            model.put("rows", rows);
        } catch(Exception e) {
            e.printStackTrace();
            model.put("columns", new ArrayList<>());
            model.put("rows", new ArrayList<>());
        }
        buildSortable(model, clazz, sortValues, archive);
        buildFilterable(model, clazz, filterValues, archive);
    }

    public static Object[] getCondition(ArrayList<ArrayList<Object>> filterValues, Class<?> clazz, boolean archived) {
        String condition = " AND ";
        ArrayList<Object> values = new ArrayList<>();
        for(int i = 0; i < filterValues.size(); i++) {
            ArrayList<Object> filter = filterValues.get(i);
            String name = (String) filter.get(0);
            String value = (String) filter.get(1);
            if(StringUtils.isNullOrEmpty(value)) continue;
            Field field = getFilterField(clazz, name, archived);
            if(field == null) continue;
            String dbName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            if(field.isAnnotationPresent(Filterable.class)) {
                if(!field.getAnnotation(Filterable.class).dbName().equals(""))
                    dbName = field.getAnnotation(Filterable.class).dbName();
            } else if(field.isAnnotationPresent(SortAndFilter.class)) {
                if (!field.getAnnotation(SortAndFilter.class).dbName().equals(""))
                    dbName = field.getAnnotation(SortAndFilter.class).dbName();
            }
            condition += dbName+" LIKE ? AND ";
            values.add(value);
        }
        if(condition.equals(" AND "))
            condition = null;
        else
            condition = condition.substring(0, condition.length()-5);
        return new Object[] { condition, values };
    }

    public static String getOrder(HashMap<String, Object> model, ArrayList<ArrayList<Object>> sortValues, Class<?> clazz, int page, int totalPages, boolean archived) {
        String order = "ORDER BY ";
        for(int i = 0; i < sortValues.size(); i++) {
            ArrayList<Object> sort = sortValues.get(i);
            String name = (String) sort.get(0);
            String value = (String) sort.get(1);
            if(value.equals("none") || !(value.equals("asc") || value.equals("desc")))
                continue;
            Field field = getSortField(clazz, name, archived);
            if(field == null) continue;
            String dbName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            if(field.isAnnotationPresent(Sortable.class)) {
                if(!field.getAnnotation(Sortable.class).dbName().equals(""))
                    dbName = field.getAnnotation(Sortable.class).dbName();
            } else if(field.isAnnotationPresent(SortAndFilter.class))
                if(!field.getAnnotation(SortAndFilter.class).dbName().equals(""))
                    dbName = field.getAnnotation(SortAndFilter.class).dbName();
            order += dbName+" "+value+", ";
        }
        if(order.equals("ORDER BY "))
            order = "";
        else
            order = order.substring(0, order.length()-2);
        if(page < 1)
            page = 1;
        totalPages = (int) Utilities.roundUp(totalPages, 10);
        if(page > totalPages)
            page = totalPages;
        model.put("page", page);
        model.put("total", totalPages);
        order += " LIMIT "+((page - 1) * 10)+",10";
        return order;
    }

    public static Method getFilterMethod(Class<?> clazz, String name, boolean archive) {
        for(Method method : clazz.getMethods()) {
            if(!method.isAnnotationPresent(Filterable.class) && !method.isAnnotationPresent(SortAndFilter.class)) continue;
            if(method.isAnnotationPresent(Filterable.class)) {
                if(method.getAnnotation(Filterable.class).onArchive() && !archive) continue;
                if(method.getAnnotation(Filterable.class).value().equals(name))
                    return method;
            } else {
                if(method.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                if(method.getAnnotation(SortAndFilter.class).value().equals(name))
                    return method;
            }
        }
        return null;
    }

    public static Field getFilterField(Class<?> clazz, String name, boolean archive) {
        for(Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(Filterable.class) && !field.isAnnotationPresent(SortAndFilter.class)) continue;
            if(field.isAnnotationPresent(Filterable.class)) {
                if(field.getAnnotation(Filterable.class).onArchive() && !archive) continue;
                if(field.getAnnotation(Filterable.class).value().equals(name))
                    return field;
            } else {
                if(field.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                if(field.getAnnotation(SortAndFilter.class).value().equals(name))
                    return field;
            }
        }
        return null;
    }

    public static Field getSortField(Class<?> clazz, String name, boolean archive) {
        for(Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(Sortable.class) && !field.isAnnotationPresent(SortAndFilter.class)) continue;
            if(field.isAnnotationPresent(Sortable.class)) {
                if(field.getAnnotation(Sortable.class).onArchive() && !archive) continue;
                if(field.getAnnotation(Sortable.class).value().equals(name))
                    return field;
            } else {
                if(field.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                if(field.getAnnotation(SortAndFilter.class).value().equals(name))
                    return field;
            }
        }
        return null;
    }

    public static <T> void buildFilterable(HashMap<String, Object> model, Class<?> clazz, ArrayList<ArrayList<Object>> filterValues, boolean archive) {
        ArrayList<SortedOrFilteredValue> filterable = new ArrayList<>();
        for(ArrayList<Object> sorted : filterValues) {
            String key = (String) sorted.get(0);
            String value = (String) sorted.get(1);
            if(value.equals("")) continue;
            int order = (int) Math.floor((double) sorted.get(2));
            Field field = getFilterField(clazz, key, archive);
            if(field == null) {
                Method method = getFilterMethod(clazz, key, archive);
                if(method == null) continue;
                if(!method.isAnnotationPresent(Filterable.class) && !method.isAnnotationPresent(SortAndFilter.class)) continue;
                String name;
                if(method.isAnnotationPresent(Filterable.class)) {
                    if(method.getAnnotation(Filterable.class).onArchive() && !archive) continue;
                    name = method.getAnnotation(Filterable.class).value();
                } else {
                    if(method.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                    name = method.getAnnotation(SortAndFilter.class).value();
                }
                filterable.add(new SortedOrFilteredValue(name, value, order));
                model.put("activeFilter", true);
                continue;
            }
            if(!field.isAnnotationPresent(Filterable.class) && !field.isAnnotationPresent(SortAndFilter.class)) continue;
            String name;
            if(field.isAnnotationPresent(Filterable.class)) {
                if(field.getAnnotation(Filterable.class).onArchive() && !archive) continue;
                name = field.getAnnotation(Filterable.class).value();
            } else {
                if(field.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                name = field.getAnnotation(SortAndFilter.class).value();
            }
            filterable.add(new SortedOrFilteredValue(name, value, order));
            model.put("activeFilter", true);
        }
        int i = filterable.size();
        for(Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(Filterable.class) && !field.isAnnotationPresent(SortAndFilter.class)) continue;
            String name;
            if(field.isAnnotationPresent(Filterable.class)) {
                if(field.getAnnotation(Filterable.class).onArchive() && !archive) continue;
                name = field.getAnnotation(Filterable.class).value();
            } else {
                if(field.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                name = field.getAnnotation(SortAndFilter.class).value();
            }
            SortedOrFilteredValue value = new SortedOrFilteredValue(name, "", i++);
            if(filterable.contains(value)) continue;
            filterable.add(value);
        }
        for(Method method : clazz.getMethods()) {
            if(!method.isAnnotationPresent(Filterable.class) && !method.isAnnotationPresent(SortAndFilter.class)) continue;
            String name;
            if(method.isAnnotationPresent(Filterable.class)) {
                if(method.getAnnotation(Filterable.class).onArchive() && !archive) continue;
                name = method.getAnnotation(Filterable.class).value();
            } else {
                if(method.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                name = method.getAnnotation(SortAndFilter.class).value();
            }
            SortedOrFilteredValue value = new SortedOrFilteredValue(name, "", i++);
            if(filterable.contains(value)) continue;
            filterable.add(value);
        }
        model.put("filterValues", filterable);
    }

    public static <T> void buildSortable(HashMap<String, Object> model, Class<?> clazz, ArrayList<ArrayList<Object>> sortValues, boolean archive) {
        ArrayList<SortedOrFilteredValue> sortable = new ArrayList<>();
        for(ArrayList<Object> sorted : sortValues) {
            String key = (String) sorted.get(0);
            String value = (String) sorted.get(1);
            if(value.equals("none")) continue;
            int order = (int) Math.floor((double) sorted.get(2));
            Field field = getSortField(clazz, key, archive);
            if(field == null) continue;
            if(!field.isAnnotationPresent(Sortable.class) && !field.isAnnotationPresent(SortAndFilter.class)) continue;
            String name;
            if(field.isAnnotationPresent(Sortable.class)) {
                if(field.getAnnotation(Sortable.class).onArchive() && !archive) continue;
                name = field.getAnnotation(Sortable.class).value();
            } else {
                if(field.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                name = field.getAnnotation(SortAndFilter.class).value();
            }
            sortable.add(new SortedOrFilteredValue(name, value, order));
        }
        int i = sortable.size();
        for(Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(Sortable.class) && !field.isAnnotationPresent(SortAndFilter.class)) continue;
            String name;
            if(field.isAnnotationPresent(Sortable.class)) {
                if(field.getAnnotation(Sortable.class).onArchive() && !archive) continue;
                name = field.getAnnotation(Sortable.class).value();
            } else {
                if(field.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                name = field.getAnnotation(SortAndFilter.class).value();
            }
            SortedOrFilteredValue value = new SortedOrFilteredValue(name, "none", i++);
            if(sortable.contains(value)) continue;
            sortable.add(value);
        }
        model.put("sortValues", sortable);
    }

    public static ListRowValue getValue(Object obj, ListValue annotation) {
        ListRowValue value = new ListRowValue(obj);
        if(annotation.formatAsNumber())
            value.setShouldFormatAsNumber(true);
        if(annotation.formatAsTime())
            value.setShouldFormatAsTime(true);
        if(annotation.formatAsTimestamp())
            value.setShouldFormatAsTimestamp(true);
        if(annotation.formatAsUser())
            value.setShouldFormatAsUser(true);
        if(annotation.order() != -1)
            value.setOrder(annotation.order());
        if(!annotation.className().equals(""))
            value.setClassName(annotation.className());
        if(annotation.isButton())
            value.setButton(true);
        return value;
    }
}
