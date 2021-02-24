package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;

public class MaterialPropTexture extends MaterialProperty {

    int[] pixels;
    int height;
    int width;
    int textureId = -1;

    public MaterialPropTexture() {
        super(0, false);
    }

    @Override
    public void reset() {
        super.reset();
        pixels = null;
    }

    @Override
    public void decode(int i_1, InputStream stream) {
        if (i_1 == 0) {
            textureId = stream.readUnsignedShort();
        }

    }

    @Override
    public int getTextureId() {
        return textureId;
    }

}
