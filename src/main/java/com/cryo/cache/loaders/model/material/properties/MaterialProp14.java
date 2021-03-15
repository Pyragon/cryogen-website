package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp14 extends MaterialProperty {

    int anInt9885 = 585;

    public MaterialProp14() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9885 = stream.readUnsignedShort();
            default:
        }
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            int i_4 = Class316.anIntArray3668[i_1];
            for (int i_5 = 0; i_5 < Class316.anInt3670; i_5++) {
                int i_6 = Class316.anIntArray3672[i_5];
                int i_7;
                if (i_6 > anInt9885 && i_6 < 4096 - anInt9885 && i_4 > 2048 - anInt9885 && i_4 < anInt9885 + 2048) {
                    i_7 = 2048 - i_6;
                    i_7 = i_7 < 0 ? -i_7 : i_7;
                    i_7 <<= 12;
                    i_7 /= 2048 - anInt9885;
                    ints_3[i_5] = 4096 - i_7;
                } else if (i_6 > 2048 - anInt9885 && i_6 < anInt9885 + 2048) {
                    i_7 = i_4 - 2048;
                    i_7 = i_7 < 0 ? -i_7 : i_7;
                    i_7 -= anInt9885;
                    i_7 <<= 12;
                    ints_3[i_5] = i_7 / (2048 - anInt9885);
                } else if (i_4 >= anInt9885 && i_4 <= 4096 - anInt9885) {
                    if (i_6 >= anInt9885 && i_6 <= 4096 - anInt9885) {
                        ints_3[i_5] = 0;
                    } else {
                        i_7 = 2048 - i_4;
                        i_7 = i_7 < 0 ? -i_7 : i_7;
                        i_7 <<= 12;
                        i_7 /= 2048 - anInt9885;
                        ints_3[i_5] = 4096 - i_7;
                    }
                } else {
                    i_7 = i_6 - 2048;
                    i_7 = i_7 < 0 ? -i_7 : i_7;
                    i_7 -= anInt9885;
                    i_7 <<= 12;
                    ints_3[i_5] = i_7 / (2048 - anInt9885);
                }
            }
        }
        return ints_3;
    }

    @Override
    public int[][] getPixels(int i_1) {
        return null;
    }
}
