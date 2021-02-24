package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.util.Random;

public class MaterialProp28 extends MaterialProperty {

    public static int anInt9792;
    int anInt9782;
    int anInt9791 = 1024;
    int anInt9784 = 2048;
    int anInt9772 = 409;
    int anInt9786 = 819;
    int anInt9787 = 1024;
    int anInt9788;
    int anInt9780 = 1024;
    int anInt9790 = 1024;
    int anInt9789;

    public MaterialProp28() {
        super(0, true);
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        switch (opcode) {
            case 0:
                anInt9782 = stream.readUnsignedByte();
                break;
            case 1:
                anInt9791 = stream.readUnsignedShort();
                break;
            case 2:
                anInt9784 = stream.readUnsignedShort();
                break;
            case 3:
                anInt9772 = stream.readUnsignedShort();
                break;
            case 4:
                anInt9786 = stream.readUnsignedShort();
                break;
            case 5:
                anInt9787 = stream.readUnsignedShort();
                break;
            case 6:
                anInt9788 = stream.readUnsignedByte();
                break;
            case 7:
                anInt9780 = stream.readUnsignedShort();
                break;
            case 8:
                anInt9790 = stream.readUnsignedShort();
                break;
        }
    }
}
