package com.cryo.cache.loaders.model;

public class SurfaceSkin {

    public int id;

    public int anInt2119;

    SurfaceSkin(int id, int i_2) {
        this.id = id;
        anInt2119 = i_2;
    }

    SurfaceSkin method2911(int i_1) {
        return new SurfaceSkin(id, i_1);
    }

    public ParticleArchive1Def getParticleArchive1Def() {
        return ParticleArchive1Def.getDefs(id);
    }
}
