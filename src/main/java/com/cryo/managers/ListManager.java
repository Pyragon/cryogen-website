package com.cryo.managers;

import com.cryo.entities.accounts.Account;
import com.cryo.entities.accounts.filters.Filter;
import com.cryo.entities.list.*;
import com.cryo.entities.shop.Package;
import com.cryo.utils.Utilities;
import com.google.common.base.CaseFormat;
import com.mysql.cj.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.AccessibleObject;
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
                buildSortable(module, model, clazz, sortValues, archive);
                buildFilterable(module, model, clazz, filterValues, archive);
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
                        if(args.length > 1)
                            args[1] = module;
                        if(args.length > 2)
                            args[2] = archive;
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
        buildSortable(module, model, clazz, sortValues, archive);
        buildFilterable(module, model, clazz, filterValues, archive);
    }

    public static Filter getFilter(String name, AccessibleObject accessible) {
        if(!accessible.isAnnotationPresent(Filterable.class) && !accessible.isAnnotationPresent(SortAndFilter.class)) return null;
        Class<?> clazz = Object.class;
        if(accessible.isAnnotationPresent(Filterable.class))
            clazz = accessible.getAnnotation(Filterable.class).values();
        else if(accessible.isAnnotationPresent(SortAndFilter.class))
            clazz = accessible.getAnnotation(SortAndFilter.class).values();
        if(clazz == Object.class) return null;
        if(!clazz.isEnum()) return null;
        Object[] constants = clazz.getEnumConstants();
        for(Object constant : constants) {
            if(!Filter.class.isAssignableFrom(constant.getClass())) continue;
            Filter filter = (Filter) constant;
            if(filter.getName().equals(name)) return filter;
        }
        return null;
    }

    public static Object[] getCondition(ArrayList<ArrayList<Object>> filterValues, Class<?> clazz, boolean archived) {
        String condition = " AND ";
        ArrayList<Object> values = new ArrayList<>();
        for(int i = 0; i < filterValues.size(); i++) {
            ArrayList<Object> filter = filterValues.get(i);
            String name = (String) filter.get(0);
            Object value = filter.get(1);
            if(value instanceof String && StringUtils.isNullOrEmpty((String) value)) continue;
            Field field = getFilterField(clazz, name, archived);
            String dbName;
            if(field == null) {
                Method method = getFilterMethod(clazz, name, archived);
                if(method == null) continue;
                dbName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, method.getName());
                boolean not = false;
                if(method.isAnnotationPresent(Filterable.class)) {
                    Filterable filterable = method.getAnnotation(Filterable.class);
                    if(filterable.onArchive() && !archived) continue;
                    if(filterable.values() != Object.class) {
                        if(value.equals("NONE")) continue;
                        Filter f = getFilter((String) value, method);
                        if(f == null) continue;
                        not = f.not();
                        value = f.value();
                    }
                    if(!filterable.dbName().equals(""))
                        dbName = filterable.dbName();
                } else if(method.isAnnotationPresent(SortAndFilter.class)) {
                    SortAndFilter filterable = method.getAnnotation(SortAndFilter.class);
                    if(filterable.onArchive() && !archived) continue;
                    if(filterable.values() != Object.class) {
                        if(value.equals("NONE")) continue;
                        Filter f = getFilter((String) value, method);
                        if(f == null) continue;
                        not = f.not();
                        value = f.value();
                    }
                    if(!filterable.dbName().equals(""))
                        dbName = filterable.dbName();
                }
                System.out.println(not);
                if(value instanceof String)
                    condition += dbName + " LIKE ? AND ";
                else
                    condition += dbName + " "+(not ? "!" : "")+"= ? AND ";
                values.add(value);
                continue;
            }
            dbName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            boolean not = false;
            if(field.isAnnotationPresent(Filterable.class)) {
                Filterable filterable = field.getAnnotation(Filterable.class);
                if(filterable.onArchive() && !archived) continue;
                if(filterable.values() != Object.class) {
                    if(value.equals("NONE")) continue;
                    Filter f = getFilter((String) value, field);
                    if(f == null) continue;
                    not = f.not();
                    value = f.value();
                }
                if(!filterable.dbName().equals(""))
                    dbName = filterable.dbName();
            } else if(field.isAnnotationPresent(SortAndFilter.class)) {
                SortAndFilter filterable = field.getAnnotation(SortAndFilter.class);
                if(filterable.onArchive() && !archived) continue;
                if(filterable.values() != Object.class) {
                    if(value.equals("NONE")) continue;
                    Filter f = getFilter((String) value, field);
                    if(f == null) continue;
                    not = f.not();
                    value = f.value();
                }
                if(!filterable.dbName().equals(""))
                    dbName = filterable.dbName();
            }
            System.out.println(not);
            if(value instanceof String)
                condition += dbName + " LIKE ? AND ";
            else
                condition += dbName + " "+(not ? "!" : "")+"= ? AND ";
            values.add(value);
            continue;
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
        totalPages = (int) Utilities.roundUp(totalPages, 10);
        if(totalPages < 1)
            totalPages = 1;
        if(page > totalPages)
            page = totalPages;
        if(page < 1)
            page = 1;
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

    public static Method getSortMethod(Class<?> clazz, String name, boolean archive) {
        for(Method method : clazz.getDeclaredMethods()) {
            if(!method.isAnnotationPresent(Sortable.class) && !method.isAnnotationPresent(SortAndFilter.class)) continue;
            if(method.isAnnotationPresent(Sortable.class)) {
                if(method.getAnnotation(Sortable.class).onArchive() && !archive) continue;
                if(method.getAnnotation(Sortable.class).value().equals(name))
                    return method;
            } else {
                if(method.getAnnotation(SortAndFilter.class).onArchive() && !archive) continue;
                if(method.getAnnotation(SortAndFilter.class).value().equals(name))
                    return method;
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

    public static SortedOrFilteredValue getValue(SortedOrFilteredValue value, AccessibleObject accessible) {
        Class<?> annotationValue;
        if(accessible.isAnnotationPresent(Filterable.class))
            annotationValue = accessible.getAnnotation(Filterable.class).values();
        else
            annotationValue = accessible.getAnnotation(SortAndFilter.class).values();
        if(annotationValue == Object.class || !annotationValue.isEnum()) return value;
        ArrayList<Properties> values = new ArrayList<>();
        Object[] constants = annotationValue.getEnumConstants();
        for(Object constant : constants) {
            if(!Filter.class.isAssignableFrom(constant.getClass())) continue;
            Filter filter = (Filter) constant;
            Properties prop = new Properties();
            prop.put("key", filter.title());
            prop.put("selected", value.getValue().equals(filter.getName()));
            prop.put("value", filter.getName());
            prop.put("not", filter.not());
            values.add(prop);
        }
        value.setValues(values);
        return value;
    }

    public static <T> void buildFilterable(String module, HashMap<String, Object> model, Class<?> clazz, ArrayList<ArrayList<Object>> filterValues, boolean archive) {
        try {
            ArrayList<SortedOrFilteredValue> filterable = new ArrayList<>();
            for (ArrayList<Object> sorted : filterValues) {
                String key = (String) sorted.get(0);
                String value = (String) sorted.get(1);
                if (value == null || value.equals("") || value.equals("NONE")) continue;
                int order = (int) Math.floor((double) sorted.get(2));
                Field field = getFilterField(clazz, key, archive);
                if (field == null) {
                    Method method = getFilterMethod(clazz, key, archive);
                    if (method == null) continue;
                    if (!method.isAnnotationPresent(Filterable.class) && !method.isAnnotationPresent(SortAndFilter.class))
                        continue;
                    String name;
                    if (method.isAnnotationPresent(Filterable.class)) {
                        Filterable filter = method.getAnnotation(Filterable.class);
                        if (filter.onArchive() && !archive) continue;
                        if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                        name = method.getAnnotation(Filterable.class).value();
                    } else {
                        SortAndFilter filter = method.getAnnotation(SortAndFilter.class);
                        if (filter.onArchive() && !archive) continue;
                        if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                        name = method.getAnnotation(SortAndFilter.class).value();
                    }
                    filterable.add(getValue(new SortedOrFilteredValue(name, value, order), method));
                    model.put("activeFilter", true);
                    continue;
                }
                if (!field.isAnnotationPresent(Filterable.class) && !field.isAnnotationPresent(SortAndFilter.class))
                    continue;
                String name;
                if (field.isAnnotationPresent(Filterable.class)) {
                    Filterable filter = field.getAnnotation(Filterable.class);
                    if (filter.onArchive() && !archive) continue;
                    if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(Filterable.class).value();
                } else {
                    SortAndFilter filter = field.getAnnotation(SortAndFilter.class);
                    if (filter.onArchive() && !archive) continue;
                    if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(SortAndFilter.class).value();
                }
                filterable.add(getValue(new SortedOrFilteredValue(name, value, order), field));
                model.put("activeFilter", true);
            }
            int i = filterable.size();
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Filterable.class) && !field.isAnnotationPresent(SortAndFilter.class))
                    continue;
                String name;
                if (field.isAnnotationPresent(Filterable.class)) {
                    Filterable filter = field.getAnnotation(Filterable.class);
                    if (filter.onArchive() && !archive) continue;
                    if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(Filterable.class).value();
                } else {
                    SortAndFilter filter = field.getAnnotation(SortAndFilter.class);
                    if (filter.onArchive() && !archive) continue;
                    if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(SortAndFilter.class).value();
                }
                SortedOrFilteredValue value = getValue(new SortedOrFilteredValue(name, "", i++), field);
                if (filterable.contains(value)) continue;
                filterable.add(value);
            }
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(Filterable.class) && !method.isAnnotationPresent(SortAndFilter.class))
                    continue;
                String name;
                if (method.isAnnotationPresent(Filterable.class)) {
                    Filterable filter = method.getAnnotation(Filterable.class);
                    if (filter.onArchive() && !archive) continue;
                    if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = method.getAnnotation(Filterable.class).value();
                } else {
                    SortAndFilter filter = method.getAnnotation(SortAndFilter.class);
                    if (filter.onArchive() && !archive) continue;
                    if (!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = method.getAnnotation(SortAndFilter.class).value();
                }
                SortedOrFilteredValue value = getValue(new SortedOrFilteredValue(name, "", i++), method);
                if (filterable.contains(value)) continue;
                filterable.add(value);
            }
            model.put("filterValues", filterable);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void buildSortable(String module, HashMap<String, Object> model, Class<?> clazz, ArrayList<ArrayList<Object>> sortValues, boolean archive) {
        try {
            ArrayList<SortedOrFilteredValue> sortable = new ArrayList<>();
            for (ArrayList<Object> sorted : sortValues) {
                String key = (String) sorted.get(0);
                String value = (String) sorted.get(1);
                if (value == null || value.equalsIgnoreCase("none")) continue;
                int order = (int) Math.floor((double) sorted.get(2));
                Field field = getSortField(clazz, key, archive);
                if (field == null) {
                    Method method = getSortMethod(clazz, key, archive);
                    if (method == null) continue;
                    if (!method.isAnnotationPresent(Sortable.class) && !method.isAnnotationPresent(SortAndFilter.class))
                        continue;
                    String name;
                    if (method.isAnnotationPresent(Sortable.class)) {
                        Sortable filter = method.getAnnotation(Sortable.class);
                        if(filter.onArchive() && !archive) continue;
                        if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                        name = method.getAnnotation(Sortable.class).value();
                    } else {
                        SortAndFilter filter = method.getAnnotation(SortAndFilter.class);
                        if(filter.onArchive() && !archive) continue;
                        if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                        name = method.getAnnotation(SortAndFilter.class).value();
                    }
                    sortable.add(new SortedOrFilteredValue(name, value, order));
                    continue;
                }
                if (!field.isAnnotationPresent(Sortable.class) && !field.isAnnotationPresent(SortAndFilter.class))
                    continue;
                String name;
                if (field.isAnnotationPresent(Sortable.class)) {
                    Sortable filter = field.getAnnotation(Sortable.class);
                    if(filter.onArchive() && !archive) continue;
                    if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(Sortable.class).value();
                } else {
                    SortAndFilter filter = field.getAnnotation(SortAndFilter.class);
                    if(filter.onArchive() && !archive) continue;
                    if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(SortAndFilter.class).value();
                }
                sortable.add(new SortedOrFilteredValue(name, value, order));
            }
            int i = sortable.size();
            ArrayList<SortedOrFilteredValue> addons = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Sortable.class) && !field.isAnnotationPresent(SortAndFilter.class))
                    continue;
                String name;
                int order = field.getAnnotation(ListValue.class).order();
                if (field.isAnnotationPresent(Sortable.class)) {
                    Sortable filter = field.getAnnotation(Sortable.class);
                    if(filter.onArchive() && !archive) continue;
                    if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(Sortable.class).value();
                } else {
                    SortAndFilter filter = field.getAnnotation(SortAndFilter.class);
                    if(filter.onArchive() && !archive) continue;
                    if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = field.getAnnotation(SortAndFilter.class).value();
                }
                SortedOrFilteredValue value = new SortedOrFilteredValue(name, "none", i++);
                if (sortable.contains(value) || addons.contains(value)) continue;
                value.setSecondIndex(order);
                addons.add(value);
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Sortable.class) && !method.isAnnotationPresent(SortAndFilter.class))
                    continue;
                String name;
                int order = method.getAnnotation(ListValue.class).order();
                if (method.isAnnotationPresent(Sortable.class)) {
                    Sortable filter = method.getAnnotation(Sortable.class);
                    if(filter.onArchive() && !archive) continue;
                    if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = method.getAnnotation(Sortable.class).value();
                } else {
                    SortAndFilter filter = method.getAnnotation(SortAndFilter.class);
                    if(filter.onArchive() && !archive) continue;
                    if(!filter.requiresModule().equals("") && !filter.requiresModule().equals(module)) continue;
                    name = method.getAnnotation(SortAndFilter.class).value();
                }
                SortedOrFilteredValue value = new SortedOrFilteredValue(name, "none", i++);
                if (sortable.contains(value) || addons.contains(value)) continue;
                value.setSecondIndex(order);
                addons.add(value);
            }
            addons.sort(Comparator.comparingInt(SortedOrFilteredValue::getSecondIndex));
            sortable.addAll(addons);
            model.put("sortValues", sortable);
        } catch(Exception e) {
            e.printStackTrace();
        }
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
