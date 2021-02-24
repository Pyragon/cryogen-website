package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp1 extends MaterialProperty {

    int anInt9869;
    int anInt9870;
    int anInt9871;

    MaterialProp1() {
        super(0, false);
        readValues(0);
    }

    void readValues(int value) {
        anInt9869 = (value & 0xff) << 4;
        anInt9870 = (value & 0xff00) >> 4;
        anInt9871 = (value & 0xff0000) >> 12;
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        if(opcode == 0)
            readValues(stream.read24BitUnsignedInt());
    }

}
