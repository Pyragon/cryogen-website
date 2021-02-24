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
