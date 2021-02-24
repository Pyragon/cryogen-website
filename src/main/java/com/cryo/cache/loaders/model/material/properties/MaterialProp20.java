package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp20 extends MaterialProperty {

    int anInt9673 = 4;
    int anInt9674 = 4;

    public MaterialProp20() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9673 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9674 = stream.readUnsignedByte();
        }

    }

}
