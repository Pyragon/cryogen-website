package com.cryo.modules.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@AllArgsConstructor
public enum APISections {

    OVERVIEW(new String[] {
            "Documentation for the current BETA Cryogen API.",
            "Any bugs in the API can be submitted through the website just like in-game bugs. The API is very limited at the moment, but that is mainly due to not knowing what people would like to do with it. Should you make a suggestion with a feature for the API, i will most likely add it, I want people to have full API coverage for whatever they need.",
            "Each endpoint shows an example. This example is created on each server restart by querying each endpoint using a test account created for the sole purpose of API examples.",
            "Each endpoint can be tested from the page. For those that require login, you will need to be logged in to the website to use. A token will then be generated for you for use with the endpoint. Please note that revoke=true is used when generating the token and therefor all other tokens will be deleted."
    }, null),
    FORUMS(new String[] {
            "Although the API mainly serves the server, I have added endpoints for the forums for ease of access.",
            "This section of the API is very limited at the moment, and will need a serious overhaul soon incorporating linked forums accounts."
    }, new APIEndpoints[]{
            APIEndpoints.FORUMS_POST,
            APIEndpoints.FORUMS_POST
    }),
    HIGHSCORES(new String[] { }, null),
    IN_GAME(null, new APIEndpoints[]{
            APIEndpoints.ONLINE_PLAYERS,
            APIEndpoints.MINIGAME_INFO
    }),
    CLIENT(null, new APIEndpoints[] {
            APIEndpoints.GET_CLIENT_INFO,
            APIEndpoints.DOWNLOAD_CLIENT
    }),
    LOGIN(null, new APIEndpoints[]{
            APIEndpoints.LOGIN
    }),
    STATUS_INFO(null, "Status Information", new APIEndpoints[] {
            APIEndpoints.STATUS_INFO
    }),
    UPDATE_INFO(null, "Update Information", new APIEndpoints[]{
            APIEndpoints.UPDATE_INFO
    }),
    USER_DATA(null, new APIEndpoints[]{
            APIEndpoints.USER_DATA
    });

    private @Getter final String[] messages;
    private @Getter String name;
    private @Getter final APIEndpoints[] endpoints;

    private static @Getter HashMap<String, APISections> sections;

    static {
        sections = new HashMap<>();
        for(APISections section : APISections.values())
            sections.put(section.name().toLowerCase().replaceAll("_", "-"), section);
    }

    public APIEndpoints getEndpoint(int id) {
        Optional<APIEndpoints> optional = Arrays.stream(endpoints).filter(e -> e.getId() == id).findAny();
        if(!optional.isPresent()) return null;
        return optional.get();
    }

}
