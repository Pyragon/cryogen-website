package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp14 extends MaterialProperty {

    int anInt9885 = 585;

    public MaterialProp14() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9885 = stream.readUnsignedShort();
            default:
        }
    }
}
