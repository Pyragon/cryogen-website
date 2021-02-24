package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp25 extends MaterialProperty {

    int anInt9907 = 4096;
    int anInt9908 = 4096;
    int anInt9909 = 4096;
    int anInt9903 = 409;
    int[] anIntArray9911 = new int[3];

    public MaterialProp25() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9903 = stream.readUnsignedShort();
                break;
            case 1:
                anInt9907 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9908 = stream.readUnsignedShort();
                break;
            case 3:
                anInt9909 = stream.readUnsignedShort();
                break;
            case 4:
                int i_4 = stream.read24BitUnsignedInt();
                anIntArray9911[0] = (i_4 & 0xff0000) << 4;
                anIntArray9911[1] = (i_4 & 0xff00) >> 4;
                anIntArray9911[2] = (i_4 & 0xff) >> 12;
        }

    }

}
