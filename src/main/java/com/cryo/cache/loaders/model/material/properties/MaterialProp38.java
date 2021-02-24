package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.util.Random;

public class MaterialProp38 extends MaterialProperty {

    int anInt10009;
    int anInt10013 = 2000;
    int anInt10010 = 16;
    int anInt10004;
    int anInt10011 = 4096;

    public MaterialProp38() {
        super(0, true);
    }

    public static int method15436(int i_0, int i_1, int i_2) {
        i_2 &= 0x3;
        return i_2 == 0 ? i_0 : (i_2 == 1 ? i_1 : (i_2 == 2 ? 4095 - i_0 : 4095 - i_1));
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt10009 = stream.readUnsignedByte();
                break;
            case 1:
                anInt10013 = stream.readUnsignedShort();
                break;
            case 2:
                anInt10010 = stream.readUnsignedByte();
                break;
            case 3:
                anInt10004 = stream.readUnsignedShort();
                break;
            case 4:
                anInt10011 = stream.readUnsignedShort();
                break;
        }

    }

}
