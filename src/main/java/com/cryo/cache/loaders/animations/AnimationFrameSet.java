package com.cryo.cache.loaders.animations;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationFrameSet {

    private static final ConcurrentHashMap<Integer, AnimationFrameSet> FRAME_COLLECTIONS = new ConcurrentHashMap<Integer, AnimationFrameSet>();

    public int id;
    private AnimationFrame[] frames;

    public static void main(String[] args) throws IOException {
        Cache.init();

    }

    public AnimationFrame[] getFrames() {
        return frames;
    }

    public static AnimationFrameSet getFrameSet(int id) {
        if (FRAME_COLLECTIONS.get(id) != null)
            return FRAME_COLLECTIONS.get(id);
        if (id > Cache.STORE.getIndex(IndexType.ANIMATION_FRAME_SETS).getTable().getArchives().length)
            return null;
        int[] files = Cache.STORE.getIndex(IndexType.ANIMATION_FRAME_SETS).getTable().getArchives()[id]
                .getValidFileIds();
        if (files == null)
            return null;

        AnimationFrameSet defs = new AnimationFrameSet();
        defs.id = id;
        byte[][] frameData = new byte[files.length][];
        for (int i = 0; i < files.length; i++)
            frameData[i] = Cache.STORE.getIndex(IndexType.ANIMATION_FRAME_SETS).getFile(id, files[i]);

        defs.frames = new AnimationFrame[frameData.length];
        for (int i = 0; i < frameData.length; i++) {
            InputStream stream = new InputStream(frameData[i]);
            stream.setOffset(1);
            int frameBaseId = stream.readUnsignedShort();
            defs.frames[i] = AnimationFrame.getFrame(frameData[i], AnimationFrameBase.getFrame(frameBaseId));
        }

        FRAME_COLLECTIONS.put(id, defs);
        return defs;
    }
}
