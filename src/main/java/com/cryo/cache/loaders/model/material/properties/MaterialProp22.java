package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp22 extends MaterialProperty {

    public MaterialProp22() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            noPalette = stream.readUnsignedByte() == 1;
        }

    }

}
