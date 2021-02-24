package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp21 extends MaterialProperty {

    public MaterialProp21() {
        super(3, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            noPalette = stream.readUnsignedByte() == 1;
        }

    }

}
