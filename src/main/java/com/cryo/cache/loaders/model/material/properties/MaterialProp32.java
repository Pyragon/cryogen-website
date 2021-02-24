package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp32 extends MaterialProperty {

    int anInt9810 = 4096;
    int anInt9813 = 3216;
    int anInt9807 = 3216;
    int[] anIntArray9808 = new int[3];

    public MaterialProp32() {
        super(1, true);
    }

    @Override
    public void init() {
        method15266();
    }

    void method15266() {
        double d_2 = Math.cos(anInt9807 / 4096.0F);
        anIntArray9808[0] = (int) (Math.sin(anInt9813 / 4096.0F) * d_2 * 4096.0D);
        anIntArray9808[1] = (int) (Math.cos(anInt9813 / 4096.0F) * d_2 * 4096.0D);
        anIntArray9808[2] = (int) (Math.sin(anInt9807 / 4096.0F) * 4096.0D);
        int i_4 = anIntArray9808[0] * anIntArray9808[0] >> 12;
        int i_5 = anIntArray9808[1] * anIntArray9808[1] >> 12;
        int i_6 = anIntArray9808[2] * anIntArray9808[2] >> 12;
        int i_7 = (int) (Math.sqrt(i_4 + i_5 + i_6 >> 12) * 4096.0D);
        if (i_7 != 0) {
            anIntArray9808[0] = (anIntArray9808[0] << 12) / i_7;
            anIntArray9808[1] = (anIntArray9808[1] << 12) / i_7;
            anIntArray9808[2] = (anIntArray9808[2] << 12) / i_7;
        }

    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9810 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9813 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9807 = stream.readUnsignedShort();
                break;
        }

    }

}
