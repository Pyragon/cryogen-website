package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp33 extends MaterialProperty {

    boolean aBool9974 = true;
    int anInt9975 = 4096;

    public MaterialProp33() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9975 = stream.readUnsignedShort();
                break;
            case 1:
                aBool9974 = stream.readUnsignedByte() == 1;
                break;
        }
    }
}
