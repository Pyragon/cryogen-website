package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp9 extends MaterialProperty {

    boolean aBool9878 = true;
    boolean aBool9875 = true;

    public MaterialProp9() {
        super(1, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                aBool9878 = stream.readUnsignedByte() == 1;
                break;
            case 1:
                aBool9875 = stream.readUnsignedByte() == 1;
                break;
            case 2:
                noPalette = stream.readUnsignedByte() == 1;
        }
    }
}
