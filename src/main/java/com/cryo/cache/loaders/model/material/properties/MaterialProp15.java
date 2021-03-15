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
    public static int anInt10003;
    public static int anInt4777;
    public static int anInt118;
    public static int anInt4266;

    public MaterialProp15() {
        super(0, true);
    }

    @Override
    public int[] method12319(int i_1) {
        int[] ints_3 = aClass320_7667.method5721(i_1);
        if (aClass320_7667.aBool3722) {
            int i_4 = Class316.anIntArray3668[i_1] * anInt9962 + 2048;
            int i_5 = i_4 >> 12;
            int i_6 = i_5 + 1;
            for (int i_7 = 0; i_7 < Class316.anInt3670; i_7++) {
                anInt10003 = Integer.MAX_VALUE;
                anInt4777 = Integer.MAX_VALUE;
                anInt118 = Integer.MAX_VALUE;
                anInt4266 = Integer.MAX_VALUE;
                int i_8 = Class316.anIntArray3672[i_7] * anInt9961 + 2048;
                int i_9 = i_8 >> 12;
                int i_10 = i_9 + 1;
                for (int i_11 = i_5 - 1; i_11 <= i_6; i_11++) {
                    int i_12 = aByteArray9965[(i_11 >= anInt9962 ? i_11 - anInt9962 : i_11) & 0xff] & 0xff;
                    for (int i_13 = i_9 - 1; i_13 <= i_10; i_13++) {
                        int i_14 = 2 * (aByteArray9965[i_12 + (i_13 >= anInt9961 ? i_13 - anInt9961 : i_13) & 0xff] & 0xff);
                        int i_15 = i_8 - (aShortArray9966[i_14++] + (i_13 << 12));
                        int i_16 = i_4 - (aShortArray9966[i_14] + (i_11 << 12));
                        int i_17;
                        switch (anInt9949) {
                            case 1:
                                i_17 = i_16 * i_16 + i_15 * i_15 >> 12;
                                break;
                            case 2:
                                i_17 = (i_15 < 0 ? -i_15 : i_15) + (i_16 < 0 ? -i_16 : i_16);
                                break;
                            case 3:
                                i_15 = i_15 < 0 ? -i_15 : i_15;
                                i_16 = i_16 < 0 ? -i_16 : i_16;
                                i_17 = Math.max(i_15, i_16);
                                break;
                            case 4:
                                i_15 = (int) (Math.sqrt((i_15 < 0 ? -i_15 : i_15) / 4096.0F) * 4096.0D);
                                i_16 = (int) (Math.sqrt((i_16 < 0 ? -i_16 : i_16) / 4096.0F) * 4096.0D);
                                i_17 = i_15 + i_16;
                                i_17 = i_17 * i_17 >> 12;
                                break;
                            case 5:
                                i_15 *= i_15;
                                i_16 *= i_16;
                                i_17 = (int) (Math.sqrt(Math.sqrt((i_16 + i_15) / 1.6777216E7F)) * 4096.0D);
                                break;
                            default:
                                i_17 = (int) (Math.sqrt((i_15 * i_15 + i_16 * i_16) / 1.6777216E7F) * 4096.0D);
                        }
                        if (i_17 < anInt4266) {
                            anInt10003 = anInt4777;
                            anInt4777 = anInt118;
                            anInt118 = anInt4266;
                            anInt4266 = i_17;
                        } else if (i_17 < anInt118) {
                            anInt10003 = anInt4777;
                            anInt4777 = anInt118;
                            anInt118 = i_17;
                        } else if (i_17 < anInt4777) {
                            anInt10003 = anInt4777;
                            anInt4777 = i_17;
                        } else if (i_17 < anInt10003) {
                            anInt10003 = i_17;
                        }
                    }
                }
                switch (anInt9963) {
                    case 0:
                        ints_3[i_7] = anInt4266;
                        break;
                    case 1:
                        ints_3[i_7] = anInt118;
                        break;
                    case 2:
                        ints_3[i_7] = anInt118 - anInt4266;
                        break;
                    case 3:
                        ints_3[i_7] = anInt4777;
                        break;
                    case 4:
                        ints_3[i_7] = anInt10003;
                }
            }
        }
        return ints_3;
    }

    @Override
    public int[][] getPixels(int i_1) {
        return null;
    }

    @Override
    public void init() {
        aByteArray9965 = Class316.method5588(anInt9959);
        method15412();
    }

    public void method15412() {
        Random random_2 = new Random(anInt9959);
        aShortArray9966 = new short[512];
        if (anInt9960 > 0) {
            for (int i_3 = 0; i_3 < 512; i_3++) {
                aShortArray9966[i_3] = (short) MaterialProp4.method7931(random_2, anInt9960);
            }
        }
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
