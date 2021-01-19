package com.cryo.entities.accounts.filters;

public interface Filter {

    String getName();
    String title();
    String key();
    Object value();
    boolean not();
}
