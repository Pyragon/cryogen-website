package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp11 extends MaterialProperty {

    int anInt9971 = 4096;
    int anInt9970 = 4096;
    int anInt9968 = 4096;

    public MaterialProp11() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9971 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9970 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9968 = stream.readUnsignedShort();
        }

    }

}
