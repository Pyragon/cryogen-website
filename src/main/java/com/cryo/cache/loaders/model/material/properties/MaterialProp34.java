package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp34 extends MaterialProperty {

    byte[] aByteArray9732 = new byte[512];
    int anInt9736 = 4;
    int anInt9737 = 1638;
    int anInt9735 = 4;
    int anInt9725 = 4;
    int anInt9733;
    short[] aShortArray9730;
    boolean aBool9726 = true;
    short[] aShortArray9731;

    public MaterialProp34() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                aBool9726 = stream.readUnsignedByte() == 1;
                break;
            case 1:
                anInt9736 = stream.readUnsignedByte();
                break;
            case 2:
                anInt9737 = stream.readShort();
                if (anInt9737 < 0) {
                    aShortArray9730 = new short[anInt9736];
                    for (int i_4 = 0; i_4 < anInt9736; i_4++) {
                        aShortArray9730[i_4] = (short) stream.readShort();
                    }
                }
                break;
            case 3:
                anInt9725 = anInt9735 = stream.readUnsignedByte();
                break;
            case 4:
                anInt9733 = stream.readUnsignedByte();
                break;
            case 5:
                anInt9725 = stream.readUnsignedByte();
                break;
            case 6:
                anInt9735 = stream.readUnsignedByte();
        }
    }

    void method15199() {
        int i_2;
        if (anInt9737 > 0) {
            aShortArray9730 = new short[anInt9736];
            aShortArray9731 = new short[anInt9736];
            for (i_2 = 0; i_2 < anInt9736; i_2++) {
                aShortArray9730[i_2] = (short) ((int) (Math.pow(anInt9737 / 4096.0F, i_2) * 4096.0D));
                aShortArray9731[i_2] = (short) ((int) Math.pow(2.0D, i_2));
            }
        } else if (aShortArray9730 != null && aShortArray9730.length == anInt9736) {
            aShortArray9731 = new short[anInt9736];
            for (i_2 = 0; i_2 < anInt9736; i_2++) {
                aShortArray9731[i_2] = (short) ((int) Math.pow(2.0D, i_2));
            }
        }
    }
}
