package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp12 extends MaterialProperty {

    int anInt9682;
    int anInt9684;
    int anInt9680 = 1;

    public MaterialProp12() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9682 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9684 = stream.readUnsignedByte();
            case 2:
            default:
                break;
            case 3:
                anInt9680 = stream.readUnsignedByte();
        }

    }

}
