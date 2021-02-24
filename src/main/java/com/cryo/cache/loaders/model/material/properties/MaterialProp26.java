package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp26 extends MaterialProperty {

    int anInt9805;
    int anInt9803 = 4096;

    public MaterialProp26() {
        super(1, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9805 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9803 = stream.readUnsignedShort();
        }

    }

}
