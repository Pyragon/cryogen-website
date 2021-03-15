package com.cryo.cache.loaders.model.material.properties;

import com.cryo.utils.JagexArrayUtils;

public class MaterialProp3 extends MaterialProperty {

    public MaterialProp3() {
        super(0, true);
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            JagexArrayUtils.method8365(ints_3, 0, Class316.anInt3670, Class316.anIntArray3668[i_1]);
        }
        return ints_3;
    }

    @Override
    public int[][] getPixels(int i_1) {
        return new int[0][];
    }
}
