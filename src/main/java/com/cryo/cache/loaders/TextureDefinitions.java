package com.cryo.cache.loaders;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.entities.annotations.EndpointSubscriber;

@EndpointSubscriber
public class TextureDefinitions {

    public int id;
    public boolean isGroundMesh;
    public boolean isHalfSize;
    public boolean skipTriangles;
    public int brightness;
    public int shadowFactor;
    public int effectId;
    public int effectParam1;
    public int colour;
    public int textureSpeedU;
    public int textureSpeedV;
    public boolean aBool2087;
    public boolean isBrickTile;
    public int useMipmaps;
    public boolean repeatS;
    public boolean repeatT;
    public boolean hdr;
    public int combineMode;
    public int effectParam2;
    public int blendType;
    
    private static TextureDefinitions[] textures;

    private TextureDefinitions(int id) {
        this.id = id;
    }

    public static void parseTextureDefs() {

        byte[] data = Cache.STORE.getIndex(IndexType.TEXTURE_DEFINITIONS).getFile(0, 0);
        InputStream stream = new InputStream(data);
        int textureDefSize = stream.readUnsignedShort();
		textures = new TextureDefinitions[textureDefSize];
		
		for(int i = 0; i < textures.length; i++)
			if(stream.readUnsignedByte() == 1)
				textures[i] = new TextureDefinitions(i);

        for (int i = 0; i < textures.length; i++) {
            if (textures[i] != null)
                textures[i].isGroundMesh = stream.readUnsignedByte() == 0;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].isHalfSize = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].skipTriangles = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].brightness = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].shadowFactor = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].effectId = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].effectParam1 = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].colour = stream.readUnsignedShort();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].textureSpeedU = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].textureSpeedV = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].aBool2087 = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].isBrickTile = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].useMipmaps = stream.readByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].repeatS = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].repeatT = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].hdr = stream.readUnsignedByte() == 1;
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].combineMode = stream.readUnsignedByte();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].effectParam2 = stream.readInt();
        }
        for (int i = 0; i < textureDefSize; i++) {
            if (textures[i] != null)
                textures[i].blendType = stream.readUnsignedByte();
        }
    }

    public static TextureDefinitions getDefinitions(int id) {
       	if(textures == null)
       		parseTextureDefs();
       	return textures[id];
    }
}
