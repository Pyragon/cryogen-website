package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.io.IOException;

public class MaterialProp23 extends MaterialProperty {

    public MaterialProp23() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            noPalette = stream.readUnsignedByte() == 1;
        }
    }
}
