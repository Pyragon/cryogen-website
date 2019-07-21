package com.cryo.entities;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MySQLRead {

    String value() default "null";
    boolean isRealLong() default false;

}
