package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp33 extends MaterialProperty {

    boolean aBool9974 = true;
    int anInt9975 = 4096;

    public MaterialProp33() {
        super(1, false);
    }

    @Override
    public int[][] getPixels(int i_1) {
        int[][] ints_3 = aClass308_7670.method5463(i_1);
        if (aClass308_7670.aBool3619) {
            int[] ints_4 = method12317(0, i_1 - 1 & Class316.anInt3673);
            int[] ints_5 = method12317(0, i_1);
            int[] ints_6 = method12317(0, i_1 + 1 & Class316.anInt3673);
            int[] ints_7 = ints_3[0];
            int[] ints_8 = ints_3[1];
            int[] ints_9 = ints_3[2];
            for (int i_10 = 0; i_10 < Class316.anInt3670; i_10++) {
                int i_11 = (ints_6[i_10] - ints_4[i_10]) * anInt9975;
                int i_12 = (ints_5[i_10 + 1 & Class316.anInt3669] - ints_5[i_10 - 1 & Class316.anInt3669]) * anInt9975;
                int i_13 = i_12 >> 12;
                int i_14 = i_11 >> 12;
                int i_15 = i_13 * i_13 >> 12;
                int i_16 = i_14 * i_14 >> 12;
                int i_17 = (int) (Math.sqrt((i_15 + i_16 + 4096) / 4096.0F) * 4096.0D);
                int i_18;
                int i_19;
                int i_20;
                if (i_17 != 0) {
                    i_18 = i_12 / i_17;
                    i_19 = i_11 / i_17;
                    i_20 = 16777216 / i_17;
                } else {
                    i_18 = 0;
                    i_19 = 0;
                    i_20 = 0;
                }
                if (aBool9974) {
                    i_18 = (i_18 >> 1) + 2048;
                    i_19 = (i_19 >> 1) + 2048;
                    i_20 = (i_20 >> 1) + 2048;
                }
                ints_7[i_10] = i_18;
                ints_8[i_10] = i_19;
                ints_9[i_10] = i_20;
            }
        }
        return ints_3;
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9975 = stream.readUnsignedShort();
                break;
            case 1:
                aBool9974 = stream.readUnsignedByte() == 1;
                break;
        }
    }

    @Override
    public int[] method12319(int i_1) {
        return null;
    }
}
