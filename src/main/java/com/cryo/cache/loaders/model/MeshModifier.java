package com.cryo.cache.loaders.model;

public class MeshModifier {

    public int[] femaleBody;
    public int[] maleBody;

    public int[] femaleHead;
    public int[] maleHead;

    public short[] modifiedColours;
    public short[] modifiedTextures;

    public MeshModifier(short[] modifiedColours, short[] modifiedTextures) {
        this.modifiedColours = modifiedColours;
        this.modifiedTextures = modifiedTextures;
    }
}
