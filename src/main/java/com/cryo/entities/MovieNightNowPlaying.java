package com.cryo.entities;

import lombok.Data;

@Data
public class MovieNightNowPlaying {

    private final String title;
    private final String type;
    private String show;
    private String seasonEpisode;
}
