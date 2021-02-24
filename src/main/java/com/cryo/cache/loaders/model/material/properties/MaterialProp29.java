package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialProp29 extends MaterialProperty {

    public MaterialProp29() {
        super(0, true);
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            int size = stream.readUnsignedByte();
            for (int i = 0; i < size; i++) {
                int opcode = stream.readUnsignedByte();
                switch (opcode) {
                    case 0:
                        method4165(stream);
                        break;
                    case 1:
                        method8842(stream);
                        break;
                    case 2:
                        method7033(stream);
                        break;
                    case 3:
                        method7644(stream);
                        break;
                }
            }
        } else if (i_1 == 1) {
            noPalette = stream.readUnsignedByte() == 1;
        }
    }

    void method4165(InputStream stream) {
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.read24BitUnsignedInt();
        stream.readUnsignedByte();
    }

    void method8842(InputStream stream) {
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.read24BitUnsignedInt();
        stream.readUnsignedByte();
    }

    void method7033(InputStream stream) {
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.read24BitUnsignedInt();
        stream.read24BitUnsignedInt();
        stream.readUnsignedByte();
    }

    void method7644(InputStream stream) {
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.readShort();
        stream.read24BitUnsignedInt();
        stream.read24BitUnsignedInt();
        stream.readUnsignedByte();
    }
}
