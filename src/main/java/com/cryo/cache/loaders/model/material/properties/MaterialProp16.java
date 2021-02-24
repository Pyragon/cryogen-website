package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp16 extends MaterialProperty {

    int anInt9762 = 1;
    int anInt9767 = 1;
    int anInt9768 = 204;

    public MaterialProp16() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9762 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9767 = stream.readUnsignedByte();
                break;
            case 2:
                anInt9768 = stream.readUnsignedShort();
        }

    }

}
