package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp30 extends MaterialProperty {

    int anInt9761 = 2048;
    int anInt9759 = 1024;
    int anInt9760 = 3072;

    public MaterialProp30() {
        super(1, false);
    }

    @Override
    public int[][] getPixels(int i_1) {
        int[][] ints_3 = aClass308_7670.method5463(i_1);
        if (aClass308_7670.aBool3619) {
            int[][] ints_4 = method12333(0, i_1);
            int[] ints_5 = ints_4[0];
            int[] ints_6 = ints_4[1];
            int[] ints_7 = ints_4[2];
            int[] ints_8 = ints_3[0];
            int[] ints_9 = ints_3[1];
            int[] ints_10 = ints_3[2];

            for (int i_11 = 0; i_11 < Class316.anInt3670; i_11++) {
                ints_8[i_11] = (anInt9761 * ints_5[i_11] >> 12) + anInt9759;
                ints_9[i_11] = (anInt9761 * ints_6[i_11] >> 12) + anInt9759;
                ints_10[i_11] = (anInt9761 * ints_7[i_11] >> 12) + anInt9759;
            }
        }

        return ints_3;
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            int[] ints_4 = method12317(0, i_1);

            for (int i_5 = 0; i_5 < Class316.anInt3670; i_5++) {
                ints_3[i_5] = (anInt9761 * ints_4[i_5] >> 12) + anInt9759;
            }
        }

        return ints_3;
    }

    @Override
    public void init() {
        anInt9761 = anInt9760 - anInt9759;
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9759 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9760 = stream.readUnsignedShort();
                break;
            case 2:
                noPalette = stream.readUnsignedByte() == 1;
                break;
        }

    }

}
