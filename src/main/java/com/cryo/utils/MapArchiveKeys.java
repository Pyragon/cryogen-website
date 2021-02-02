package com.cryo.utils;

import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.annotations.WebStartSubscriber;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;

@WebStartSubscriber
public final class MapArchiveKeys {

	private final static HashMap<Integer, int[]> keys = new HashMap<>();

	public static final int[] getMapKeys(int regionId) {
		int[] arr = keys.get(regionId);
		if (arr == null || (arr[0] == 0 && arr[1] == 0 && arr[2] == 0 && arr[3] == 0))
			return null;
		return arr;
	}

	@WebStart
	public static final void loadPackedKeys() {
		try {
			String PACKED_PATH = "./data/map.keys";
			File file = new File(PACKED_PATH);
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int regionId = buffer.getShort() & 0xffff;
				int[] xteas = new int[4];
				for (int index = 0; index < 4; index++)
					xteas[index] = buffer.getInt();
				keys.put(regionId, xteas);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

}
