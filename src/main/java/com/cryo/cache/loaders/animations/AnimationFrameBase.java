package com.cryo.cache.loaders.animations;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationFrameBase {

    private static final ConcurrentHashMap<Integer, AnimationFrameBase> FRAME_BASES = new ConcurrentHashMap<Integer, AnimationFrameBase>();

    public int id;
    public int[][] labels;
    public int[] anIntArray7561;
    public int[] transformationTypes;
    public boolean[] aBoolArray7563;
    public int count;

    public AnimationFrameBase(int id) {
        this.id = id;
    }

    public static AnimationFrameBase getFrame(int frameBaseId) {
        if (FRAME_BASES.get(frameBaseId) != null)
            return FRAME_BASES.get(frameBaseId);
        byte[] frameBaseData = Cache.STORE.getIndex(IndexType.ANIMATION_FRAME_BASES).getFile(frameBaseId, 0);
        if (frameBaseData == null) {
            return null;
        }
        AnimationFrameBase defs = new AnimationFrameBase(frameBaseId);
        defs.decode(new InputStream(frameBaseData));
        FRAME_BASES.put(frameBaseId, defs);
        return defs;
    }

    public void decode(InputStream buffer) {
        count = buffer.readUnsignedByte();
        transformationTypes = new int[count];
        labels = new int[count][];
        aBoolArray7563 = new boolean[count];
        anIntArray7561 = new int[count];
        for (int i_0_ = 0; i_0_ < count; i_0_++) {
            transformationTypes[i_0_] = buffer.readUnsignedByte();
            if (transformationTypes[i_0_] == 6)
                transformationTypes[i_0_] = 2;
        }
        for (int i_1_ = 0; i_1_ < count; i_1_++)
            aBoolArray7563[i_1_] = buffer.readUnsignedByte() == 1;
        for (int i_2_ = 0; i_2_ < count; i_2_++)
            anIntArray7561[i_2_] = buffer.readUnsignedShort();
        for (int i_3_ = 0; i_3_ < count; i_3_++)
            labels[i_3_] = new int[buffer.readUnsignedByte()];
        for (int i_4_ = 0; i_4_ < count; i_4_++) {
            for (int i_5_ = 0; (i_5_ < labels[i_4_].length); i_5_++)
                labels[i_4_][i_5_] = buffer.readUnsignedByte();
        }
    }
}
