package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp0 extends MaterialProperty {

    int anInt9860 = 4096;

    MaterialProp0() {
        super(0, true);
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        if(opcode == 0)
            anInt9860 = (stream.readUnsignedByte() << 12) / 255;
    }
}
