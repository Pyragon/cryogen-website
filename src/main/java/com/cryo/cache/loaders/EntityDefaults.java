package com.cryo.cache.loaders;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.annotations.WebStartSubscriber;
import lombok.Data;

@Data
@WebStartSubscriber
public class EntityDefaults {

    public static EntityDefaults ENTITY_DEFAULTS;

    private int maximumHits = 4;
    private int[] hitOffsetsX;
    private int[] hitOffsetsY;
    private int anInt7030 = 4;
    private int anInt7032 = 4;
    private int anInt7028 = 7;
    private int profilingModel = -1;
    private boolean aBool7044 = true;
    private boolean npcMessagesEnabled = true;
    private int npcMessageDuration = 2;
    private boolean enablePlayerMessages = true;
    private int playerMessageDuration = 3;
    private int anInt7045 = -1;
    private int anInt7046 = -1;
    private int loginWindow;
    private int lobbyWindow;
    private short[][] originalColours;
    private short[][][] replacementColours;

    @WebStart
    public static void loadEntityDefaults() {
        byte[] data = Cache.STORE.getIndex(IndexType.DEFAULTS).getAnyFile(3);
        if(data == null) return;
        EntityDefaults defaults = new EntityDefaults();
        defaults.decode(new InputStream(data));
        ENTITY_DEFAULTS = defaults;
    }

    private void decode(InputStream buffer) {
        boolean loadedOffsets = false;
        while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                if (!loadedOffsets) {
                    if (hitOffsetsX == null) {
                        maximumHits = 4;
                        hitOffsetsX = new int[4];
                        hitOffsetsY = new int[4];
                    }
                    for (opcode = 0; opcode < hitOffsetsX.length; opcode++) {
                        hitOffsetsX[opcode] = 0;
                        hitOffsetsY[opcode] = opcode * 20;
                    }
                }
                return;
            }
            if (opcode == 1) {
                if (hitOffsetsX == null) {
                    maximumHits = 4;
                    hitOffsetsX = new int[4];
                    hitOffsetsY = new int[4];
                }
                for (int i_5 = 0; i_5 < hitOffsetsX.length; i_5++) {
                    hitOffsetsX[i_5] = buffer.readShort();
                    hitOffsetsY[i_5] = buffer.readShort();
                }
                loadedOffsets = true;
            } else if (opcode == 2) {
                profilingModel = buffer.readBigSmart();
            } else if (opcode == 3) {
                maximumHits = buffer.readUnsignedByte();
                hitOffsetsX = new int[maximumHits];
                hitOffsetsY = new int[maximumHits];
            } else if (opcode == 4) {
                aBool7044 = false;
            } else if (opcode == 5) {
                loginWindow = buffer.read24BitUnsignedInt();
            } else if (opcode == 6) { //wtf?
                lobbyWindow = buffer.read24BitUnsignedInt();
            } else if (opcode == 7) {
                originalColours = new short[10][4];
                replacementColours = new short[10][4][];
                for (int i_5 = 0; i_5 < 10; i_5++) {
                    for (int i_6 = 0; i_6 < 4; i_6++) {
                        int i_7 = buffer.readUnsignedShort();
                        if (i_7 == 65535)
                            i_7 = -1;
                        originalColours[i_5][i_6] = (short) i_7;
                        int i_8 = buffer.readUnsignedShort();
                        replacementColours[i_5][i_6] = new short[i_8];
                        for (int i_9 = 0; i_9 < i_8; i_9++) {
                            int i_10 = buffer.readUnsignedShort();
                            if (i_10 == 65535)
                                i_10 = -1;
                            replacementColours[i_5][i_6][i_9] = (short) i_10;
                        }
                    }
                }
            } else if (opcode == 8) {
                npcMessagesEnabled = false;
            } else if (opcode == 9) {
                npcMessageDuration = buffer.readUnsignedByte();
            } else if (opcode == 10) {
                enablePlayerMessages = false;
            } else if (opcode == 11) {
                playerMessageDuration = buffer.readUnsignedByte();
            } else if (opcode == 12) {
                anInt7045 = buffer.readUnsignedShort();
                anInt7046 = buffer.readUnsignedShort();
            } else if (opcode == 13) {
                anInt7032 = buffer.readUnsignedByte();
            } else if (opcode == 14) {
                anInt7030 = buffer.readUnsignedByte();
            } else if (opcode == 15) {
                anInt7028 = buffer.readUnsignedByte();
            }
        }
    }
}
