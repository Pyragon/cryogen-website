package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.util.Random;

public class MaterialProp15 extends MaterialProperty {

    public static String aString9967;
    byte[] aByteArray9965 = new byte[512];
    short[] aShortArray9966 = new short[512];
    int anInt9959;
    int anInt9960 = 2048;
    int anInt9961 = 5;
    int anInt9962 = 5;
    int anInt9963 = 2;
    int anInt9949 = 1;

    public MaterialProp15() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9961 = anInt9962 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9959 = stream.readUnsignedByte();
                break;
            case 2:
                anInt9960 = stream.readUnsignedShort();
                break;
            case 3:
                anInt9963 = stream.readUnsignedByte();
                break;
            case 4:
                anInt9949 = stream.readUnsignedByte();
                break;
            case 5:
                anInt9961 = stream.readUnsignedByte();
                break;
            case 6:
                anInt9962 = stream.readUnsignedByte();
        }
    }
}
