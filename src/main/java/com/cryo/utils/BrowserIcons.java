package com.cryo.utils;

import com.cryo.Website;

public enum BrowserIcons {

    CHROME,
    FIREFOX,
    EDGE,
    OPERA,
    MOON,
    SAFARI,
    DEFAULT;

    public static String getPath(String browser) {
        BrowserIcons icon = null;
        for(BrowserIcons i : BrowserIcons.values()) {
            if(browser.toLowerCase().contains(i.name().toLowerCase()))
                icon = i;
        }
        if(icon == null)
            icon = BrowserIcons.DEFAULT;
        return Website.getProperties().getProperty("path")+"images/browsers/"+icon.name().toLowerCase()+".png";
    }

}
