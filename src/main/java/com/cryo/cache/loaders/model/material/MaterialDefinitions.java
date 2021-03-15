package com.cryo.cache.loaders.model.material;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.model.material.properties.Class316;
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

    private static double aDouble9479 = -1.0D;
    public static int[] anIntArray9474 = new int[256];

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

    public int[] renderTexturePixels(int textureId, float f_2, int i_3, int i_4, boolean bool_5, boolean isBrickTile) {
        return getMaterialDefinitions(textureId).renderIntPixels(f_2, i_3, i_4, bool_5, isBrickTile);
    }

    public int[] getTexturePropertyIds() {
        return texturePropertyIds;
    }

    public int[] renderIntPixels(double d_3, int width, int height, boolean bool_7, boolean bool_8) {

        for(int i = 0; i < properties.length; i++)
            properties[i].method12315(width, height);

        method3895(d_3);
        Class316.method5593(width, height);
        int[] pixels = new int[width * height];
        int i_11;
        int i_12;
        byte b_13;
        if (bool_7) {
            i_11 = width - 1;
            i_12 = -1;
            b_13 = -1;
        } else {
            i_11 = 0;
            i_12 = width;
            b_13 = 1;
        }

        int i_14 = 0;

        int i_15;
        for (i_15 = 0; i_15 < height; i_15++) {
            int[] ints_16;
            int[] ints_17;
            int[] ints_18;
            if (opaqueProperty.noPalette) {
                int[] ints_19 = opaqueProperty.method12319(i_15);
                ints_16 = ints_19;
                ints_17 = ints_19;
                ints_18 = ints_19;
            } else {
                int[][] ints_26 = opaqueProperty.getPixels(i_15);
                ints_16 = ints_26[0];
                ints_17 = ints_26[1];
                ints_18 = ints_26[2];
            }

            if (bool_8) {
                i_14 = i_15;
            }

            for (int i_25 = i_11; i_25 != i_12; i_25 += b_13) {
                int i_20 = ints_16[i_25] >> 4;
                if (i_20 > 255) {
                    i_20 = 255;
                }

                if (i_20 < 0) {
                    i_20 = 0;
                }

                int i_21 = ints_17[i_25] >> 4;
                if (i_21 > 255) {
                    i_21 = 255;
                }

                if (i_21 < 0) {
                    i_21 = 0;
                }

                int i_22 = ints_18[i_25] >> 4;
                if (i_22 > 255) {
                    i_22 = 255;
                }

                if (i_22 < 0) {
                    i_22 = 0;
                }

                i_20 = anIntArray9474[i_20];
                i_21 = anIntArray9474[i_21];
                i_22 = anIntArray9474[i_22];
                int i_23 = i_22 + (i_21 << 8) + (i_20 << 16);
                if (i_23 != 0) {
                    i_23 |= -16777216;
                }

                pixels[i_14++] = i_23;
                if (bool_8) {
                    i_14 += width - 1;
                }
            }
        }

        for (i_15 = 0; i_15 < properties.length; i_15++) {
            properties[i_15].reset();
        }
        return pixels;
    }

    public int[] renderIntPixels(double d_3, int width, int height, boolean bool_7) {

        for (int i_9 = 0; i_9 < properties.length; i_9++) {
            properties[i_9].method12315(width, height);
        }

        method3895(d_3);
        Class316.method5593(width, height);
        int[] pixels = new int[width * height];
        int i_10 = 0;

        int i_11;
        for (i_11 = 0; i_11 < height; i_11++) {
            int[] ints_12;
            int[] ints_13;
            int[] ints_14;
            int[] ints_15;
            if (opaqueProperty.noPalette) {
                ints_15 = opaqueProperty.method12319(i_11);
                ints_12 = ints_15;
                ints_13 = ints_15;
                ints_14 = ints_15;
            } else {
                int[][] ints_22 = opaqueProperty.getPixels(i_11);
                ints_12 = ints_22[0];
                ints_13 = ints_22[1];
                ints_14 = ints_22[2];
            }

            if (translucentProperty.noPalette) {
                ints_15 = translucentProperty.method12319(i_11);
            } else {
                ints_15 = translucentProperty.getPixels(i_11)[0];
            }

            if (bool_7) {
                i_10 = i_11;
            }

            for (int i_16 = width - 1; i_16 >= 0; --i_16) {
                int i_17 = ints_12[i_16] >> 4;
                if (i_17 > 255) {
                    i_17 = 255;
                }

                if (i_17 < 0) {
                    i_17 = 0;
                }

                int i_18 = ints_13[i_16] >> 4;
                if (i_18 > 255) {
                    i_18 = 255;
                }

                if (i_18 < 0) {
                    i_18 = 0;
                }

                int i_19 = ints_14[i_16] >> 4;
                if (i_19 > 255) {
                    i_19 = 255;
                }

                if (i_19 < 0) {
                    i_19 = 0;
                }

                i_17 = anIntArray9474[i_17];
                i_18 = anIntArray9474[i_18];
                i_19 = anIntArray9474[i_19];
                int i_20;
                if (i_17 == 0 && i_18 == 0 && i_19 == 0) {
                    i_20 = 0;
                } else {
                    i_20 = ints_15[i_16] >> 4;
                    if (i_20 > 255) {
                        i_20 = 255;
                    }

                    if (i_20 < 0) {
                        i_20 = 0;
                    }
                }

                pixels[i_10++] = i_19 + (i_18 << 8) + (i_20 << 24) + (i_17 << 16);
                if (bool_7) {
                    i_10 += width - 1;
                }
            }
        }

        for (i_11 = 0; i_11 < properties.length; i_11++) {
            properties[i_11].reset();
        }

        return pixels;
    }

    public float[] renderFloatPixels(int i_3, int i_4, boolean blend) {

        for (int i_7 = 0; i_7 < properties.length; i_7++) {
            properties[i_7].method12315(i_3, i_4);
        }

        Class316.method5593(i_3, i_4);
        float[] floats_18 = new float[i_3 * i_4 * 4];
        int i_8 = 0;

        int i_9;
        for (i_9 = 0; i_9 < i_4; i_9++) {
            int[] ints_10;
            int[] ints_11;
            int[] ints_12;
            int[] ints_13;
            if (opaqueProperty.noPalette) {
                ints_13 = opaqueProperty.method12319(i_9);
                ints_10 = ints_13;
                ints_11 = ints_13;
                ints_12 = ints_13;
            } else {
                int[][] ints_19 = opaqueProperty.getPixels(i_9);
                ints_10 = ints_19[0];
                ints_11 = ints_19[1];
                ints_12 = ints_19[2];
            }

            if (translucentProperty.noPalette) {
                ints_13 = translucentProperty.method12319(i_9);
            } else {
                ints_13 = translucentProperty.getPixels(i_9)[0];
            }

            int[] ints_14;
            if (combinedProperty.noPalette) {
                ints_14 = combinedProperty.method12319(i_9);
            } else {
                ints_14 = combinedProperty.getPixels(i_9)[0];
            }

            if (blend) {
                i_8 = i_9 << 2;
            }

            for (int i_15 = i_3 - 1; i_15 >= 0; --i_15) {
                float f_16 = ints_13[i_15] / 4096.0F;
                float f_17 = (1.0F + ints_14[i_15] * 31.0F / 4096.0F) / 4096.0F;
                if (f_16 < 0.0F) {
                    f_16 = 0.0F;
                } else if (f_16 > 1.0F) {
                    f_16 = 1.0F;
                }

                floats_18[i_8++] = ints_10[i_15] * f_17;
                floats_18[i_8++] = f_17 * ints_11[i_15];
                floats_18[i_8++] = ints_12[i_15] * f_17;
                floats_18[i_8++] = f_16;
                if (blend) {
                    i_8 += (i_3 << 2) - 4;
                }
            }
        }

        for (i_9 = 0; i_9 < properties.length; i_9++) {
            properties[i_9].reset();
        }

        return floats_18;
    }

    public static void method3895(double d_0) {
        if (d_0 != aDouble9479) {
            for (int i_2 = 0; i_2 < 256; i_2++) {
                int i_3 = (int) (Math.pow(i_2 / 255.0D, d_0) * 255.0D);
                anIntArray9474[i_2] = Math.min(255, i_3);
            }
            aDouble9479 = d_0;
        }
    }
}
