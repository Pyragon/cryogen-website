package com.cryo.cache.loaders.model.material;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.model.material.properties.MaterialProperty;
import lombok.Getter;

import java.util.HashMap;

public class MaterialDefinitions {

    private int id;
    @Getter
    MaterialProperty[] properties;
    int[] spritePropertyIds;
    int[] texturePropertyIds;
    MaterialProperty opaqueProperty;
    MaterialProperty translucentProperty;
    MaterialProperty combinedProperty;

    private static HashMap<Integer, MaterialDefinitions> defs = new HashMap<>();

    public static MaterialDefinitions getMaterialDefinitions(int id) {
        if(defs.containsKey(id)) return defs.get(id);
        byte[] data = Cache.STORE.getIndex(IndexType.TEXTURES).getAnyFile(id);
        if(data == null) return null;
        MaterialDefinitions defs = new MaterialDefinitions(id);
        defs.decode(data);
        MaterialDefinitions.defs.put(id, defs);
        return defs;
    }

    public MaterialDefinitions(int id) {
        this.id = id;
    }

    public void decode(byte[] data) {
        InputStream stream = new InputStream(data);

        int size = stream.readUnsignedByte();
        int spriteProperties = 0;
        int textureProperties = 0;
        int[][] propertyParameters = new int[size][];
        properties = new MaterialProperty[size];

        for(int i = 0; i < size; i++) {
            MaterialProperty property = MaterialProperty.decode(stream);
            if(property.getSpriteId() >= 0)
                spriteProperties++;
            if(property.getTextureId() >= 0)
                textureProperties++;

            int paramCount = property.getParams().length;
            propertyParameters[i] = new int[paramCount];

            for(int j = 0; j < paramCount; j++)
                propertyParameters[i][j] = stream.readUnsignedByte();

            properties[i] = property;
        }

        spritePropertyIds = new int[spriteProperties];
        spriteProperties = 0;
        texturePropertyIds = new int[textureProperties];
        textureProperties = 0;

        for (int i = 0; i < size; i++) {
            MaterialProperty property = properties[i];
            int paramLen = property.getParams().length;

            for (int j = 0; j < paramLen; j++) {
                property.getParams()[j] = properties[propertyParameters[i][j]];
            }

            int spriteId = property.getSpriteId();
            int textureId = property.getTextureId();
            if (spriteId > 0) {
                spritePropertyIds[spriteProperties++] = spriteId;
            }

            if (textureId > 0) {
                texturePropertyIds[textureProperties++] = textureId;
            }

            propertyParameters[i] = null;
        }

        opaqueProperty = properties[stream.readUnsignedByte()];
        translucentProperty = properties[stream.readUnsignedByte()];
        combinedProperty = properties[stream.readUnsignedByte()];
    }

    public int[] renderToIntPixels(double d_3, int width, int height, boolean bool_7) {
        for(int i = 0; i < properties.length; i++)
            properties[i].method12315(width, height);
        int[] pixels = new int[width * height];
        return pixels;
    }
}
