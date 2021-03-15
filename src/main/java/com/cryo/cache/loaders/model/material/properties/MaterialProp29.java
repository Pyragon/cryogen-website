package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.model.material.properties.class149s.*;

public class MaterialProp29 extends MaterialProperty {

    public Class149[] aClass149Array9862;

    public static int[][] anIntArrayArray7072;

    public static int anInt7071;

    public static int anInt7069 = 100;

    public static int anInt7070;

    public static int anInt7068 = 100;

    public MaterialProp29() {
        super(0, true);
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            method15372(aClass320_7667.method5722());
        }
        return ints_3;
    }

    @Override
    public int[][] getPixels(int i_1) {
        int[][] ints_3 = aClass308_7670.method5463(i_1);
        if (aClass308_7670.aBool3619) {
            int i_4 = Class316.anInt3670;
            int i_5 = Class316.anInt3671;
            int[][] ints_6 = new int[i_5][i_4];
            int[][][] ints_7 = aClass308_7670.method5464();
            method15372(ints_6);
            for (int i_8 = 0; i_8 < Class316.anInt3671; i_8++) {
                int[] ints_9 = ints_6[i_8];
                int[][] ints_10 = ints_7[i_8];
                int[] ints_11 = ints_10[0];
                int[] ints_12 = ints_10[1];
                int[] ints_13 = ints_10[2];
                for (int i_14 = 0; i_14 < Class316.anInt3670; i_14++) {
                    int i_15 = ints_9[i_14];
                    ints_13[i_14] = (i_15 & 0xff) << 4;
                    ints_12[i_14] = (i_15 & 0xff00) >> 4;
                    ints_11[i_14] = (i_15 & 0xff0000) >> 12;
                }
            }
        }
        return ints_3;
    }

    public static void method16086(int i_2, int i_3) {
        anInt7071 = 0;
        anInt7069 = i_2;
        anInt7070 = 0;
        anInt7068 = i_3;
    }

    public static void method3936(int[][] ints_0) {
        anIntArrayArray7072 = ints_0;
    }

    public void method15372(int[][] ints_1) {
        int i_3 = Class316.anInt3670;
        int i_4 = Class316.anInt3671;
        method3936(ints_1);
        method16086(Class316.anInt3669, Class316.anInt3673);
        if (aClass149Array9862 != null) {
            for (int i_5 = 0; i_5 < aClass149Array9862.length; i_5++) {
                Class149 class149_6 = aClass149Array9862[i_5];
                int i_7 = class149_6.anInt1743;
                int i_8 = class149_6.anInt1741;
                if (i_7 >= 0) {
                    if (i_8 >= 0) {
                        class149_6.method2557(i_3, i_4);
                    } else {
                        class149_6.method2556(i_3, i_4);
                    }
                } else if (i_8 >= 0) {
                    class149_6.method2561(i_3, i_4);
                }
            }
        }
    }

    @Override
    public void decode(int opcode, InputStream stream) {
        if (opcode == 0) {
            int size = stream.readUnsignedByte();
            aClass149Array9862 = new Class149[size];
            for (int i = 0; i < size; i++) {
                int type = stream.readUnsignedByte();
                switch (type) {
                    case 0:
                        aClass149Array9862[i] = method4165(stream);
                        break;
                    case 1:
                        aClass149Array9862[i] = method8842(stream);
                        break;
                    case 2:
                        aClass149Array9862[i] = method7033(stream);
                        break;
                    case 3:
                        aClass149Array9862[i] = method7644(stream);
                        break;
                }
            }
        } else if (opcode == 1) {
            noPalette = stream.readUnsignedByte() == 1;
        }
    }

    public static Class149_Sub1 method7644(InputStream stream) {
        return new Class149_Sub1(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.read24BitUnsignedInt(), stream.read24BitUnsignedInt(), stream.readUnsignedByte());
    }

    public static Class149_Sub4 method7033(InputStream stream) {
        return new Class149_Sub4(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.read24BitUnsignedInt(), stream.read24BitUnsignedInt(), stream.readUnsignedByte());
    }

    public static Class149_Sub3 method4165(InputStream stream) {
        return new Class149_Sub3(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.read24BitUnsignedInt(), stream.readUnsignedByte());
    }

    public static Class149_Sub2 method8842(InputStream stream) {
        return new Class149_Sub2(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort(), stream.read24BitUnsignedInt(), stream.readUnsignedByte());
    }
}
