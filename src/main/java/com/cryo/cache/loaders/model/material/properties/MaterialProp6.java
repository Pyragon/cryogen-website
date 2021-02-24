package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp6 extends MaterialProperty {

    int anInt9864;
    int anInt9868 = 4096;

    public MaterialProp6() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream rsbytebuffer_2) {
        switch (i_1) {
            case 0:
                anInt9864 = rsbytebuffer_2.readUnsignedShort();
                break;
            case 1:
                anInt9868 = rsbytebuffer_2.readUnsignedShort();
                break;
            case 2:
                noPalette = rsbytebuffer_2.readUnsignedByte() == 1;
        }

    }

}
