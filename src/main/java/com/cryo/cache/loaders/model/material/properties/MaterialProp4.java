package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.util.Random;

public class MaterialProp4 extends MaterialProperty {

    int anInt9982 = 4;
    int anInt9994 = 8;
    int anInt9986;
    int anInt9987 = 1024;
    int anInt9984 = 1024;
    int anInt9989 = 409;
    int anInt9990 = 204;
    int anInt9991 = 81;
    int anInt9993;
    int anInt9988;
    int anInt9995;
    int[] anIntArray9996;
    int[][] anIntArrayArray9979;
    int[][] anIntArrayArray9992;

    public MaterialProp4() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9982 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9994 = stream.readUnsignedByte();
                break;
            case 2:
                anInt9989 = stream.readUnsignedShort();
                break;
            case 3:
                anInt9990 = stream.readUnsignedShort();
                break;
            case 4:
                anInt9987 = stream.readUnsignedShort();
                break;
            case 5:
                anInt9986 = stream.readUnsignedShort();
                break;
            case 6:
                anInt9991 = stream.readUnsignedShort();
                break;
            case 7:
                anInt9984 = stream.readUnsignedShort();
        }

    }

}
