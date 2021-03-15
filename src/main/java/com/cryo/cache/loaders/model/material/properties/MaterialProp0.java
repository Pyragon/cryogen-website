package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;
import com.cryo.utils.JagexArrayUtils;

public class MaterialProp0 extends MaterialProperty {

    int anInt9860 = 4096;

    MaterialProp0() {
        super(0, true);
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        if(opcode == 0)
            anInt9860 = (stream.readUnsignedByte() << 12) / 255;
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            JagexArrayUtils.method8365(ints_3, 0, Class316.anInt3670, anInt9860);
        }
        return ints_3;
    }

    @Override
    public int[][] getPixels(int i_1) {
        return null;
    }
}
