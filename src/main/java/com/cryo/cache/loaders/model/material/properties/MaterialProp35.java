package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp35 extends MaterialProperty {

    int anInt9886 = 4096;

    public MaterialProp35() {
        super(1, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            anInt9886 = stream.readUnsignedShort();
        }

    }

}
