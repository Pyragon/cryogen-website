package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp31 extends MaterialProperty {

    int anInt9741;
    int anInt9740;
    int anInt9742 = 1365;
    int anInt9743 = 20;

    public MaterialProp31() {
        super(0, true);
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        switch (opcode) {
            case 0:
                anInt9742 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9743 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9741 = stream.readUnsignedShort();
                break;
            case 3:
                anInt9740 = stream.readUnsignedShort();
                break;
        }

    }

}
