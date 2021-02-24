package com.cryo.cache.loaders.model;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.utils.Trig;

import java.util.HashMap;

public class ParticleArchive1Def {

    private int id;
    public int anInt4038;
    public int anInt4035;
    public int anInt4027;
    public int anInt4036;
    public int anInt4037;
    public int anInt4041;
    public int anInt4039;
    public int anInt4040;
    public int anInt4024;
    public int anInt4033;
    public long aLong4044;
    int anInt4034;
    boolean aBool4042;

    private static HashMap<Integer, ParticleArchive1Def> defs = new HashMap<>();

    public static ParticleArchive1Def getDefs(int id) {
        if(defs.containsKey(id)) return defs.get(id);
        byte[] data = Cache.STORE.getIndex(IndexType.PARTICLES).getFile(1, id);
        if(data == null) return null;
        ParticleArchive1Def defs = new ParticleArchive1Def(id);
        defs.decode(new InputStream(data));
        defs.init();
        ParticleArchive1Def.defs.put(id, defs);
        return defs;
    }

    public ParticleArchive1Def(int id) {
        this.id = id;
    }

    void decode(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            readValue(stream, opcode);
        }
    }

    void readValue(InputStream stream, int opcode) {
        if (opcode == 1) {
            if (false) {
                throw new IllegalStateException();
            }
            anInt4034 = stream.readUnsignedShort();
        } else if (opcode == 2) {
            stream.readUnsignedByte();
        } else if (opcode == 3) {
            if (false) {
                throw new IllegalStateException();
            }
            anInt4027 = stream.readInt();
            anInt4036 = stream.readInt();
            anInt4037 = stream.readInt();
        } else if (opcode == 4) {
            if (false)
                anInt4038 = stream.readUnsignedByte();
            anInt4035 = stream.readInt();
        } else if (opcode == 6) {
            if (false) {
                throw new IllegalStateException();
            }
            anInt4041 = stream.readUnsignedByte();
        } else if (opcode == 8) {
            if (true) {
                anInt4040 = 1;
            }
        } else if (opcode == 9) {
            anInt4039 = 1;
        } else if (opcode == 10) {
            if (false)
                aBool4042 = true;
        }
    }

    void init() {
        anInt4024 = Trig.COSINE[anInt4034 << 3];
        long long_2 = anInt4027;
        long long_4 = anInt4036;
        long long_6 = anInt4037;
        anInt4033 = (int) Math.sqrt((long_2 * long_2 + long_4 * long_4 + long_6 * long_6));
        if (anInt4035 == 0) {
            anInt4035 = 1;
        }
        if (anInt4038 == 0) {
            aLong4044 = 2147483647L;
        } else if (anInt4038 == 1) {
            aLong4044 = anInt4033 * 8 / anInt4035;
            aLong4044 *= aLong4044;
        } else if (anInt4038 == 2) {
            aLong4044 = anInt4033 * 8 / anInt4035;
        }
        if (aBool4042) {
            anInt4033 *= -1;
        }
    }
}
