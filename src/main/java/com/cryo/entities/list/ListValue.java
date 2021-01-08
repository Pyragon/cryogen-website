package com.cryo.entities.list;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ListValue {

    String value();

    int order() default -1;

    boolean onArchive() default false;
    boolean notOnArchive() default false;
    boolean formatAsTime() default false;
    boolean formatAsTimestamp() default false;
    boolean formatAsNumber() default false;
    boolean formatAsUser() default false;

    boolean returnsValue() default false;

    String requiresModule() default "";

    String className() default "";

    boolean isButton() default false;
}
