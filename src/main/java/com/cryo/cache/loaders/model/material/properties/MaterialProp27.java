package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp27 extends MaterialProperty {

    int anInt9593 = 10;
    int anInt9594 = 2048;
    int anInt9595;
    int[] anIntArray9596;
    int[] anIntArray9592;

    public MaterialProp27() {
        super(0, true);
    }

    @Override
    public void init() {
        method14842();
    }

    void method14842() {
        int i_2 = 0;
        anIntArray9596 = new int[anInt9593 + 1];
        anIntArray9592 = new int[anInt9593 + 1];
        int i_3 = 4096 / anInt9593;
        int i_4 = i_3 * anInt9594 >> 12;

        for (int i_5 = 0; i_5 < anInt9593; i_5++) {
            anIntArray9592[i_5] = i_2;
            anIntArray9596[i_5] = i_4 + i_2;
            i_2 += i_3;
        }

        anIntArray9592[anInt9593] = 4096;
        anIntArray9596[anInt9593] = anIntArray9596[0] + 4096;
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9593 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9594 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9595 = stream.readUnsignedByte();
        }

    }

}
