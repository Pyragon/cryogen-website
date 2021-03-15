package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp1 extends MaterialProperty {

    int anInt9869;
    int anInt9870;
    int anInt9871;

    MaterialProp1() {
        super(0, false);
        readValues(0);
    }

    void readValues(int value) {
        anInt9869 = (value & 0xff) << 4;
        anInt9870 = (value & 0xff00) >> 4;
        anInt9871 = (value & 0xff0000) >> 12;
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        if(opcode == 0)
            readValues(stream.read24BitUnsignedInt());
    }

    @Override
    public int[] method12319(int i_1) {
        return null;
    }

    @Override
    public int[][] getPixels(int i_1) {
        int[][] pixels = aClass308_7670.method5463(i_1);
        if (aClass308_7670.aBool3619) {
            int[] ints_4 = pixels[0];
            int[] ints_5 = pixels[1];
            int[] ints_6 = pixels[2];

            for (int i_7 = 0; i_7 < Class316.anInt3670; i_7++) {
                ints_4[i_7] = anInt9871;
                ints_5[i_7] = anInt9870;
                ints_6[i_7] = anInt9869;
            }
        }

        return pixels;
    }

}
