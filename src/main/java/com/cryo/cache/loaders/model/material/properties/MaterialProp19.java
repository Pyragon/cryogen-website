package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp19 extends MaterialProperty {

    int anInt9836 = 32768;

    public MaterialProp19() {
        super(3, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9836 = stream.readUnsignedShort() << 4;
                break;
            case 1:
                noPalette = stream.readUnsignedByte() == 1;
        }
    }
}
