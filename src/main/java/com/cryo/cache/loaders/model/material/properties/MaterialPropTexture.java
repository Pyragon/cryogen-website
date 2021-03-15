package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.TextureDefinitions;
import com.cryo.cache.loaders.model.material.MaterialDefinitions;

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

	@Override
	public int[][] getPixels(int i_1) {
		int[][] ints_3 = aClass308_7670.method5463(i_1);
		if (aClass308_7670.aBool3619) {
			int i_4 = (height != Class316.anInt3671 ? height * i_1 / Class316.anInt3671 : i_1) * width;
			int[] ints_5 = ints_3[0];
			int[] ints_6 = ints_3[1];
			int[] ints_7 = ints_3[2];
			int i_8;
			int i_9;
			if (width == Class316.anInt3670) {
				for (i_8 = 0; i_8 < Class316.anInt3670; i_8++) {
					i_9 = pixels[i_4++];
					ints_7[i_8] = (i_9 & 0xff) << 4;
					ints_6[i_8] = (i_9 & 0xff00) >> 4;
					ints_5[i_8] = (i_9 & 0xff0000) >> 12;
				}
			} else {
				for (i_8 = 0; i_8 < Class316.anInt3670; i_8++) {
					i_9 = i_8 * width / Class316.anInt3670;
					int i_10 = pixels[i_4 + i_9];
					ints_7[i_8] = (i_10 & 0xff) << 4;
					ints_6[i_8] = (i_10 & 0xff00) >> 4;
					ints_5[i_8] = (i_10 & 0xff0000) >> 12;
				}
			}
		}

		return ints_3;
	}

	@Override
	public void method12315(int width, int height) {
		super.method12315(width, height);
		if (textureId == -1)
			return;
		TextureDefinitions defs = TextureDefinitions.getDefinitions(textureId);
		if (defs == null) return;
		int resolution = defs.isHalfSize ? 64 : 128;
		MaterialDefinitions materialDefinitions = MaterialDefinitions.getMaterialDefinitions(textureId);
		pixels = materialDefinitions.renderTexturePixels(textureId, 1.0F, resolution, resolution, false, defs.isBrickTile);
		this.height = resolution;
		this.width = resolution;

	}

    @Override
    public int[] method12319(int i_1) {
        return null;
    }

}
