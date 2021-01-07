package com.cryo.entities.list;

import lombok.Data;

@Data
public class CustomAction {

    private final String title;
    private final String className;
    private final String icon;
    private boolean link;

    public CustomAction(String title, String className, String icon) {
        this(title, className, icon,true);
    }

    public CustomAction(String title, String className, String icon, boolean link) {
        this.title = title;
        this.className = className;
        this.icon = icon;
        this.link = link;
    }
}
