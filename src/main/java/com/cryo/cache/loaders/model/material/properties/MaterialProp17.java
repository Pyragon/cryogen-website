package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp17 extends MaterialProperty {

    int anInt9922;
    int anInt9925;
    int anInt9926;
    int anInt9918;
    int anInt9919;
    int anInt9923;
    int anInt9924;
    int anInt9921;
    int anInt9927;

    public MaterialProp17() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9918 = stream.readShort();
                break;
            case 1:
                anInt9919 = (stream.readByte() << 12) / 100;
                break;
            case 2:
                anInt9923 = (stream.readByte() << 12) / 100;
        }

    }

}
