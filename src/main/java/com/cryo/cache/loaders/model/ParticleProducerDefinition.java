package com.cryo.cache.loaders.model;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;

import java.util.HashMap;

public class ParticleProducerDefinition {

    private int id;

    public int[] anIntArray562;
    public short minimumAngleH;

    public short maximumAngleH;

    public short minimumAngleV;

    public short maximumAngleV;

    public int minimumSpeed;

    public int maximumSpeed;

    public int anInt542;

    public int anInt569;

    public int maximumSize;

    public int minimumSize;
    public int minimumLifetime;
    public int maximumLifetime;
    public int minimumParticleRate;
    public int maximumParticleRate;
    public int[] anIntArray559;
    public int[] anIntArray561;
    public int anInt591 = -2;
    public int anInt600 = -2;
    public int anInt557;
    public int textureId = -1;
    public int anInt573 = -1;
    public int fadeColor;
    public boolean activeFirst = true;
    public int anInt537 = -1;
    public int lifetime = -1;
    public int minimumSetting;
    public boolean periodic = true;
    public int endSpeed = -1;
    public boolean uniformColorVariance = true;
    public int[] anIntArray582;
    public boolean aBool572 = true;
    public int endSize = -1;
    public boolean aBool574;
    public boolean aBool534 = true;
    public boolean aBool576;
    public boolean aBool541 = true;
    public boolean aBool578;
    public int anInt565;
    public int anInt581;
    public int anInt551;
    public int anInt584;
    public int anInt585;
    public int anInt587;
    public int anInt588;
    public int anInt590;
    public int colorFadeStart;
    public int alphaFadeStart;
    public int fadeRedStep;
    public int fadeGreenStep;
    public int fadeBlueStep;
    public int startSpeedChange;
    public int fadeAlphaStep;
    public int speedStep;
    public int startSizeChange;
    public int sizeChangeStep;
    int minimumStartColor;
    int maximumStartColor;
    int colorFading = 100;
    int alphaFading = 100;
    int speedChange = 100;
    int sizeChange = 100;
    int anInt566;
    int anInt599;
    int anInt586;
    int anInt575;

    private static HashMap<Integer, ParticleProducerDefinition> defs = new HashMap<>();

    public static ParticleProducerDefinition getDefinitions(int id) {
        if(defs.containsKey(id)) return defs.get(id);
        byte[] data = Cache.STORE.getIndex(IndexType.PARTICLES).getFile(0, id);
        if(data == null) return null;
        ParticleProducerDefinition defs = new ParticleProducerDefinition(id);
        defs.decode(new InputStream(data));
        defs.init();
        ParticleProducerDefinition.defs.put(id, defs);
        return defs;
    }

    public ParticleProducerDefinition(int id) {
        this.id = id;
    }

    void readValues(InputStream buffer, int opcode) {
        if (opcode == 1) {
            minimumAngleH = (short) buffer.readUnsignedShort();
            maximumAngleH = (short) buffer.readUnsignedShort();
            minimumAngleV = (short) buffer.readUnsignedShort();
            maximumAngleV = (short) buffer.readUnsignedShort();
            byte scale = 3;
            minimumAngleH <<= scale;
            maximumAngleH <<= scale;
            minimumAngleV <<= scale;
            maximumAngleV <<= scale;
        } else if (opcode == 2) {
            buffer.readUnsignedByte();
        } else if (opcode == 3) {
            minimumSpeed = buffer.readInt();
            maximumSpeed = buffer.readInt();
        } else if (opcode == 4) {
            anInt542 = buffer.readUnsignedByte();
            anInt569 = buffer.readByte();
        } else if (opcode == 5) {
            minimumSize = maximumSize = buffer.readUnsignedShort() << 12 << 2;
        } else if (opcode == 6) {
            minimumStartColor = buffer.readInt();
            maximumStartColor = buffer.readInt();
        } else if (opcode == 7) {
            minimumLifetime = buffer.readUnsignedShort();
            maximumLifetime = buffer.readUnsignedShort();
        } else if (opcode == 8) {
            minimumParticleRate = buffer.readUnsignedShort();
            maximumParticleRate = buffer.readUnsignedShort();
        } else {
            int i_5;
            int count;
            if (opcode == 9) {
                count = buffer.readUnsignedByte();
                anIntArray559 = new int[count];
                for (i_5 = 0; i_5 < count; i_5++) {
                    anIntArray559[i_5] = buffer.readUnsignedShort();
                }
            } else if (opcode == 10) {
                count = buffer.readUnsignedByte();
                anIntArray561 = new int[count];
                for (i_5 = 0; i_5 < count; i_5++) {
                    anIntArray561[i_5] = buffer.readUnsignedShort();
                }
            } else if (opcode == 12) {
                anInt591 = buffer.readByte();
            } else if (opcode == 13) {
                anInt600 = buffer.readByte();
            } else if (opcode == 14) {
                anInt557 = buffer.readUnsignedShort();
            } else if (opcode == 15) {
                textureId = buffer.readUnsignedShort();
            } else if (opcode == 16) {
                activeFirst = buffer.readUnsignedByte() == 1;
                anInt537 = buffer.readUnsignedShort();
                lifetime = buffer.readUnsignedShort();
                periodic = buffer.readUnsignedByte() == 1;
            } else if (opcode == 17) {
                anInt573 = buffer.readUnsignedShort();
            } else if (opcode == 18) {
                fadeColor = buffer.readInt();
            } else if (opcode == 19) {
                minimumSetting = buffer.readUnsignedByte();
            } else if (opcode == 20) {
                colorFading = buffer.readUnsignedByte();
            } else if (opcode == 21) {
                alphaFading = buffer.readUnsignedByte();
            } else if (opcode == 22) {
                endSpeed = buffer.readInt();
            } else if (opcode == 23) {
                speedChange = buffer.readUnsignedByte();
            } else if (opcode == 24) {
                uniformColorVariance = false;
            } else if (opcode == 25) {
                count = buffer.readUnsignedByte();
                anIntArray582 = new int[count];
                for (i_5 = 0; i_5 < count; i_5++) {
                    anIntArray582[i_5] = buffer.readUnsignedShort();
                }
            } else if (opcode == 26) {
                aBool572 = false;
            } else if (opcode == 27) {
                endSize = buffer.readUnsignedShort() << 12 << 2;
            } else if (opcode == 28) {
                sizeChange = buffer.readUnsignedByte();
            } else if (opcode == 29) {
                buffer.readShort();
            } else if (opcode == 30) {
                aBool574 = true;
            } else if (opcode == 31) {
                minimumSize = buffer.readUnsignedShort() << 12 << 2;
                maximumSize = buffer.readUnsignedShort() << 12 << 2;
            } else if (opcode == 32) {
                aBool534 = false;
            } else if (opcode == 33) {
                aBool576 = true;
            } else if (opcode == 34) {
                aBool541 = false;
            }
        }
    }

    void init() {
        if (anInt591 > -2 || anInt600 > -2) {
            aBool578 = true;
        }
        anInt565 = minimumStartColor >> 16 & 0xff;
        anInt566 = maximumStartColor >> 16 & 0xff;
        anInt581 = anInt566 - anInt565;
        anInt551 = minimumStartColor >> 8 & 0xff;
        anInt599 = maximumStartColor >> 8 & 0xff;
        anInt584 = anInt599 - anInt551;
        anInt585 = minimumStartColor & 0xff;
        anInt586 = maximumStartColor & 0xff;
        anInt587 = anInt586 - anInt585;
        anInt588 = minimumStartColor >> 24 & 0xff;
        anInt575 = maximumStartColor >> 24 & 0xff;
        anInt590 = anInt575 - anInt588;
        if (fadeColor != 0) {
            colorFadeStart = colorFading * maximumLifetime / 100;
            alphaFadeStart = alphaFading * maximumLifetime / 100;
            if (colorFadeStart == 0) {
                colorFadeStart = 1;
            }
            fadeRedStep = ((fadeColor >> 16 & 0xff) - (anInt581 / 2 + anInt565) << 8) / colorFadeStart;
            fadeGreenStep = ((fadeColor >> 8 & 0xff) - (anInt584 / 2 + anInt551) << 8) / colorFadeStart;
            fadeBlueStep = ((fadeColor & 0xff) - (anInt587 / 2 + anInt585) << 8) / colorFadeStart;
            if (alphaFadeStart == 0) {
                alphaFadeStart = 1;
            }
            fadeAlphaStep = ((fadeColor >> 24 & 0xff) - (anInt590 / 2 + anInt588) << 8) / alphaFadeStart;
            fadeRedStep += fadeRedStep > 0 ? -4 : 4;
            fadeGreenStep += fadeGreenStep > 0 ? -4 : 4;
            fadeBlueStep += fadeBlueStep > 0 ? -4 : 4;
            fadeAlphaStep += fadeAlphaStep > 0 ? -4 : 4;
        }
        if (endSpeed != -1) {
            startSpeedChange = maximumLifetime * speedChange / 100;
            if (startSpeedChange == 0) {
                startSpeedChange = 1;
            }
            speedStep = (endSpeed - ((maximumSpeed - minimumSpeed) / 2 + minimumSpeed)) / startSpeedChange;
        }
        if (endSize != -1) {
            startSizeChange = sizeChange * maximumLifetime / 100;
            if (startSizeChange == 0) {
                startSizeChange = 1;
            }
            sizeChangeStep = (endSize - ((maximumSize - minimumSize) / 2 + minimumSize)) / startSizeChange;
        }
    }

    void decode(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            readValues(stream, opcode);
        }
    }
}
