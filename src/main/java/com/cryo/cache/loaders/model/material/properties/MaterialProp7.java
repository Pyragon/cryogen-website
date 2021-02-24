package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

import java.awt.*;
import java.awt.event.ActionEvent;

public class MaterialProp7 extends MaterialProperty {

    int anInt9895 = 6;

    public MaterialProp7() {
        super(2, false);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        switch (i_1) {
            case 0:
                anInt9895 = stream.readUnsignedByte();
                break;
            case 1:
                noPalette = stream.readUnsignedByte() == 1;
        }

    }

}
