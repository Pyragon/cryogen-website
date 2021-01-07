package com.cryo.entities.list;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SortAndFilter {

    String value() default "";

    String dbName() default "";

    boolean onArchive() default false;
}
