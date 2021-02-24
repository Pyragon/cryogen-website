package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialPropSprite extends MaterialProperty {

    public static int SPRITE_ARCHIVE_ID = -1;
    int[] pixels;
    int width;
    int height;
    int spriteId = -1;

    public MaterialPropSprite() {
        super(0, false);
    }

    @Override
    public void reset() {
        super.reset();
        pixels = null;
    }

    @Override
    public void decode(int opcode, InputStream buffer) {
        if (opcode == 0) {
            spriteId = buffer.readUnsignedShort();
        }

    }

    @Override
    public int getSpriteId() {
        return spriteId;
    }

}
