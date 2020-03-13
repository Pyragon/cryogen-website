package com.cryo.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public abstract class MySQLDao {

    public Object[] data() {
        ArrayList<Object> list = new ArrayList<>();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if(!Modifier.isFinal(field.getModifiers()) && !field.isAnnotationPresent(MySQLRead.class))
                    continue;
                field.setAccessible(true);
                Object value = field.get(this);
                if (field.isAnnotationPresent(MySQLDefault.class)) {
                    if(value instanceof Integer) {
                        if((int) value == -1) {
                            list.add("DEFAULT");
                            continue;
                        }
                    } else {
                        list.add("DEFAULT");
                        continue;
                    }
                }
                if(value == null) {
                    list.add("NULL");
                    continue;
                }
                list.add(value);
            }
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return list.toArray();
    }

}
