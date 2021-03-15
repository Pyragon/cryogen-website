package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.io.IOException;

public class MaterialProp23 extends MaterialProperty {

    public static int anInt9873;

    public static int anInt9872;

    public MaterialProp23() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            noPalette = stream.readUnsignedByte() == 1;
        }
    }

    @Override
    public int[][] getPixels(int i_1) {
        int[][] ints_3 = aClass308_7670.method5463(i_1);
        if (aClass308_7670.aBool3619) {
            int[] ints_4 = ints_3[0];
            int[] ints_5 = ints_3[1];
            int[] ints_6 = ints_3[2];
            for (int i_7 = 0; i_7 < Class316.anInt3670; i_7++) {
                method15383(i_7, i_1);
                int[][] ints_8 = method12333(0, anInt9872);
                ints_4[i_7] = ints_8[0][anInt9873];
                ints_5[i_7] = ints_8[1][anInt9873];
                ints_6[i_7] = ints_8[2][anInt9873];
            }
        }
        return ints_3;
    }

    public void method15383(int i_1, int i_2) {
        int i_4 = Class316.anIntArray3672[i_1];
        int i_5 = Class316.anIntArray3668[i_2];
        float f_6 = (float) Math.atan2(i_4 - 2048, i_5 - 2048);
        if (f_6 >= -3.141592653589793 && f_6 <= -2.356194490192345) {
            anInt9873 = i_1;
            anInt9872 = i_2;
        } else if (f_6 <= -1.5707963267948966 && f_6 >= -2.356194490192345) {
            anInt9873 = i_2;
            anInt9872 = i_1;
        } else if (f_6 <= -0.7853981633974483 && f_6 >= -1.5707963267948966) {
            anInt9873 = Class316.anInt3670 - i_2;
            anInt9872 = i_1;
        } else if (f_6 <= 0.0F && f_6 >= -0.7853981633974483) {
            anInt9873 = i_1;
            anInt9872 = Class316.anInt3671 - i_2;
        } else if (f_6 >= 0.0F && f_6 <= 0.7853981633974483D) {
            anInt9873 = Class316.anInt3670 - i_1;
            anInt9872 = Class316.anInt3671 - i_2;
        } else if (f_6 >= 0.7853981633974483D && f_6 <= 1.5707963267948966D) {
            anInt9873 = Class316.anInt3670 - i_2;
            anInt9872 = Class316.anInt3671 - i_1;
        } else if (f_6 >= 1.5707963267948966D && f_6 <= 2.356194490192345D) {
            anInt9873 = i_2;
            anInt9872 = Class316.anInt3671 - i_1;
        } else if (f_6 >= 2.356194490192345D && f_6 <= 3.141592653589793D) {
            anInt9873 = Class316.anInt3670 - i_1;
            anInt9872 = i_2;
        }
        anInt9873 &= Class316.anInt3669;
        anInt9872 &= Class316.anInt3673;
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            for (int i_4 = 0; i_4 < Class316.anInt3670; i_4++) {
                method15383(i_4, i_1);
                int[] ints_5 = method12317(0, anInt9872);
                ints_3[i_4] = ints_5[anInt9873];
            }
        }
        return ints_3;
    }
}
