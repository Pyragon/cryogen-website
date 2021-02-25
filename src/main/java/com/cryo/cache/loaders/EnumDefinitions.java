package com.cryo.cache.loaders;

import com.cryo.cache.*;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.cs2.CS2Type;
import com.cryo.entities.WorldTile;
import com.cryo.utils.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class EnumDefinitions {

    public transient CS2Type keyType;
    public char keyTypeChar;
    public transient CS2Type valueType;
    public char valueTypeChar;
    private String defaultStringValue;
    private int defaultIntValue;
    private HashMap<Long, Object> values;

    private static final ConcurrentHashMap<Integer, EnumDefinitions> ENUMS_CACHE = new ConcurrentHashMap<Integer, EnumDefinitions>();

    public static void main(String[] args) throws IOException {
        Cache.init();
        File file = new File("enums.txt");
        if (file.exists())
            file.delete();
        else
            file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.append("//Version = 727\n");
        writer.flush();
        for (int i = 0; i < 100000; i++) {
            EnumDefinitions map = getEnum(i);
            if (map == null || map.getValues() == null)
                continue;
            writer.append(i + ": " + map.toString());
            writer.newLine();
            writer.flush();
        }
        writer.close();
    }

    public static final EnumDefinitions getEnum(int enumId) {
        EnumDefinitions script = ENUMS_CACHE.get(enumId);
        if (script != null)
            return script;
        byte[] data = Cache.STORE.getIndex(IndexType.ENUMS).getFile(FileType.ENUMS.archiveId(enumId),
                FileType.ENUMS.fileId(enumId));
        script = new EnumDefinitions();
        if (data != null)
            script.readValueLoop(new InputStream(data));
        ENUMS_CACHE.put(enumId, script);
        return script;

    }

    public int getDefaultIntValue() {
        return defaultIntValue;
    }

    public String getDefaultStringValue() {
        return defaultStringValue;
    }

    public HashMap<Long, Object> getValues() {
        return values;
    }

    public Object getValue(long key) {
        if (values == null)
            return null;
        return values.get(key);
    }

    public long getKeyForValue(Object value) {
        for (Long key : values.keySet()) {
            if (values.get(key).equals(value))
                return key;
        }
        return -1;
    }

    public int getSize() {
        if (values == null)
            return 0;
        return values.size();
    }

    public int getIntValue(long key) {
        if (values == null)
            return defaultIntValue;
        Object value = values.get(key);
        if (value == null || !(value instanceof Integer))
            return defaultIntValue;
        return (Integer) value;
    }

    public int getKeyIndex(long key) {
        if (values == null)
            return -1;
        int i = 0;
        for (long k : values.keySet()) {
            if (k == key)
                return i;
            i++;
        }
        return -1;
    }

    public int getIntValueAtIndex(int i) {
        if (values == null)
            return -1;
        return (int) values.values().toArray()[i];
    }

    public String getStringValue(long key) {
        if (values == null)
            return defaultStringValue;
        Object value = values.get(key);
        if (value == null || !(value instanceof String))
            return defaultStringValue;
        return (String) value;
    }

    private void readValueLoop(InputStream stream) {
        for (;;) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                break;
            readValues(stream, opcode);
        }
    }

    private void readValues(InputStream stream, int opcode) {
        if (opcode == 1) {
            keyTypeChar = Utilities.cp1252ToChar((byte) stream.readByte());
            keyType = CS2Type.forJagexChar(keyTypeChar);
        } else if (opcode == 2) {
            valueTypeChar = Utilities.cp1252ToChar((byte) stream.readByte());
            valueType = CS2Type.forJagexChar(valueTypeChar);
        } else if (opcode == 3)
            defaultStringValue = stream.readString();
        else if (opcode == 4)
            defaultIntValue = stream.readInt();
        else if (opcode == 5 || opcode == 6 || opcode == 7 || opcode == 8) {
            int count = stream.readUnsignedShort();
            int loop = opcode == 7 || opcode == 8 ? stream.readUnsignedShort() : count;
            if (values == null)
                values = new HashMap<Long, Object>(Utilities.getHashMapSize(count));
            for (int i = 0; i < loop; i++) {
                int key = opcode == 7 || opcode == 8 ? stream.readUnsignedShort() : stream.readInt();
                Object value = opcode == 5 || opcode == 7 ? stream.readString() : stream.readInt();
                values.put((long) key, value);
            }
        }
    }

    public EnumDefinitions() {
        defaultStringValue = "null";
    }

    public Object keyToType(long l) {
        if (keyType == CS2Type.COMPONENT) {
            long interfaceId = l >> 16;
            long componentId = l - (interfaceId << 16);
            return "IComponent(" + interfaceId + ", " + componentId + ")";
        } else if (keyType == CS2Type.LOC) {
            return new WorldTile((int) l);
//        } else if (keyType == CS2Type.SKILL) {
//            int idx = (int) l;
//            if (idx >= SkillManager.SKILL_NAME.length)
//                return l;
//            return idx + "(" + SkillManager.SKILL_NAME[((int) l)] + ")";
        } else if (keyType == CS2Type.ITEMCODE) {
            return l + "(" + ItemDefinitions.getItemDefinitions((int) l).getName() + ")";
        } else if (keyType == CS2Type.NPC) {
            return l + "(" + NPCDefinitions.getNPCDefinitions((int) l).getName() + ")";
        } else if (keyType == CS2Type.STRUCT) {
            return l + ": " + StructDefinitions.getStruct((int) l);
        }
        return l;
    }

    public Object valToType(Object o) {
        if (valueType == CS2Type.COMPONENT) {
            if (o instanceof String)
                return o;
            long interfaceId = ((int) o) >> 16;
            long componentId = ((int) o) - (interfaceId << 16);
            return "IComponent(" + interfaceId + ", " + componentId + ")";
        } else if (valueType == CS2Type.LOC) {
            if (o instanceof String)
                return o;
            return new WorldTile(((int) o));
//        } else if (valueType == CS2Type.SKILL) {
//            if (o instanceof String)
//                return o;
//            int idx = (int) o;
//            if (idx >= SkillManager.SKILL_NAME.length)
//                return o;
//            return idx + "(" + SkillManager.SKILL_NAME[((int) o)] + ")";
        } else if (valueType == CS2Type.ITEMCODE) {
            if (o instanceof String)
                return o;
            return ((int) o) + " (" + ItemDefinitions.getItemDefinitions(((int) o)).getName() + ")";
        } else if (valueType == CS2Type.NPC) {
            if (o instanceof String)
                return o;
            return ((int) o) + " (" + NPCDefinitions.getNPCDefinitions(((int) o)).getName() + ")";
        } else if (valueType == CS2Type.STRUCT) {
            return o + ": " + StructDefinitions.getStruct((int) o);
        } else if (valueType == CS2Type.ENUM) {
            return o + ": " + getEnum((int) o);
        }
        return o;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (getValues() == null)
            return "null";
        s.append("<" + keyType + ", " + valueType + "> - " + getDefaultStringValue() + " - " + getDefaultIntValue()
                + " { ");
        s.append("\r\n");
        for (Long l : getValues().keySet()) {
            s.append(keyToType(l));
            s.append(" = ");
            s.append(valToType(getValues().get(l)));
            s.append("\r\n");
        }
        s.append("} \r\n");
        return s.toString();
    }
}
