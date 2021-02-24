package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp5 extends MaterialProperty {

    int anInt9842 = 1;
    int anInt9843 = 1;

    public MaterialProp5() {
        super(1, false);
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        switch (opcode) {
            case 0:
                anInt9842 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9843 = stream.readUnsignedByte();
                break;
            case 2:
                noPalette = stream.readUnsignedByte() == 1;
        }
    }
}
