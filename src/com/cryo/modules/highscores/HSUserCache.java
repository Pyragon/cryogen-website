package com.cryo.modules.highscores;

import com.cryo.cache.impl.HighscoresCache;
import com.cryo.modules.highscores.HSUtils.HSData;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 29, 2017 at 1:50:02 AM
 */

@Data
public class HSUserCache {
	
	public HSUserCache(HSData data, long expiry) {
		this.data = data;
		this.expiry = expiry;
	}
	
	private final HSData data;
	private long expiry;
}
