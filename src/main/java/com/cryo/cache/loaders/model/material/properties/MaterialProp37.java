package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp37 extends MaterialProperty {

    int anInt9830 = 2048;
    int anInt9829;
    int anInt9833;
    int anInt9831 = 2048;
    int anInt9832 = 12288;
    int anInt9828 = 4096;
    int anInt9834 = 8192;

    public MaterialProp37() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9830 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9829 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9833 = stream.readUnsignedShort();
                break;
            case 3:
                anInt9831 = stream.readUnsignedShort();
                break;
            case 4:
                anInt9832 = stream.readUnsignedShort();
                break;
            case 5:
                anInt9828 = stream.readUnsignedShort();
                break;
            case 6:
                anInt9834 = stream.readUnsignedShort();
                break;
        }

    }

}
