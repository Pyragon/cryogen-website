package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.net.Socket;

public class MaterialProp8 extends MaterialProperty {

    int[][] anIntArrayArray9800;
    int[] anIntArray9798;
    int[] anIntArray9796;
    int anInt9799;
    short[] aShortArray9801 = new short[257];

    public MaterialProp8() {
        super(1, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            anInt9799 = stream.readUnsignedByte();
            anIntArrayArray9800 = new int[stream.readUnsignedByte()][2];

            for (int i_4 = 0; i_4 < anIntArrayArray9800.length; i_4++) {
                anIntArrayArray9800[i_4][0] = stream.readUnsignedShort();
                anIntArrayArray9800[i_4][1] = stream.readUnsignedShort();
            }
        }

    }

}
