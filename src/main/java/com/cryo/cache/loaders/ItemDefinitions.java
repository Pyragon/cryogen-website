package com.cryo.cache.loaders;

import com.cryo.cache.*;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.io.OutputStream;
import com.cryo.cache.loaders.model.MeshModifier;
import com.cryo.cache.loaders.model.ModelDefinitions;
import com.cryo.cache.loaders.model.RSMesh;
import com.cryo.utils.Utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class ItemDefinitions {

    private static final ConcurrentHashMap<Integer, ItemDefinitions> itemsDefinitions = new ConcurrentHashMap<Integer, ItemDefinitions>();
    private static final ConcurrentHashMap<String, ItemDefinitions> itemDefinitionsByS = new ConcurrentHashMap<>();

    public int id;
    public boolean loaded;

    public int modelId;
    public String name;

    // model size information
    public int modelZoom;
    public int modelRotationX;
    public int modelRotationY;
    public int modelOffsetX;
    public int modelOffsetY;

    public int realOffsetX;
    public int realOffsetY;

    // extra information
    public int stackable;
    public int value;
    public boolean membersOnly;

    // wearing model information
    public int maleEquip1;
    public int femaleEquip1;
    public int maleEquip2;
    public int femaleEquip2;

    // options
    public String[] groundOptions;
    public String[] inventoryOptions;

    // model information
    public short[] originalModelColours;
    public short[] modifiedModelColours;
    public short[] originalTextureIds;
    public short[] modifiedTextureIds;
    public byte[] spriteRecolourIndices;
    public byte[] unknownArray3;
    public int[] quests;
    // extra information, not used for newer items
    public boolean grandExchange;

    public int maleEquip3;
    public int femaleEquip3;
    public int maleHead1;
    public int femaleHead1;
    public int maleHead2;
    public int femaleHead2;
    public int modelRotationZ;
    public int unknownInt6;
    public int certId;
    public int certTemplateId;
    public int[] stackIds;
    public int[] stackAmounts;
    public int resizeX;
    public int resizeY;
    public int resizeZ;
    public int ambient;
    public int contrast;
    public int teamId;
    public int lendId;
    public int lendTemplateId;
    public int maleWearXOffset;
    public int maleWearYOffset;
    public int maleWearZOffset;
    public int femaleWearXOffset;
    public int femaleWearYOffset;
    public int femaleWearZOffset;
    public int unknownInt18;
    public int unknownInt19;
    public int unknownInt20;
    public int unknownInt21;
    public int customCursorOp1;
    public int customCursorId1;
    public int customCursorOp2;
    public int customCursorId2;
    public int wearPos;
    public int wearPos2;

    // extra added
    public boolean noted;
    public boolean lended;

    public HashMap<Integer, Object> clientScriptData;
    public HashMap<Integer, Integer> itemRequiriments;
    public int[] unknownArray5;
    public int[] unknownArray4;
    public byte[] unknownArray6;

    public transient byte[] data;

    private int multiStackSize;

    private int pickSizeShift;
    private short i_97_;
    private int i_96_;

    public static final ItemDefinitions getItemDefinitions(int itemId) {
        ItemDefinitions def = itemsDefinitions.get(itemId);
        if (def == null) itemsDefinitions.put(itemId, def = new ItemDefinitions(itemId));
        return def;
    }

    public static final ItemDefinitions getItemDefinitions(String name) {
        if (itemDefinitionsByS.containsKey(name)) return itemDefinitionsByS.get(name);
        for (int i = 0; i < Utilities.getItemDefinitionsSize(); i++) {
            ItemDefinitions defs = getItemDefinitions(i);
            if (defs == null)
                continue;
            itemDefinitionsByS.put(name, defs);
            if (defs.getName().equalsIgnoreCase(name))
                return defs;
        }
        return null;
    }

    private void setStackable(boolean stackable) {
        this.stackable = stackable ? 1 : 0;
    }

    private void setName(String name) {
        this.name = name;
    }

    public static final void clearItemsDefinitions() {
        itemsDefinitions.clear();
    }

    public ItemDefinitions() {

    }

    public ItemDefinitions(int id) {
        this.id = id;
        setDefaultsVariableValues();
        setDefaultOptions();
        loadItemDefinitions();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public final void loadItemDefinitions() {
        data = Cache.STORE.getIndex(IndexType.ITEMS).getFile(FileType.ITEMS.archiveId(id), FileType.ITEMS.fileId(id));
        if (data == null) return;
        readOpcodeValues(new InputStream(data));
        if (certTemplateId != -1) toNote();
        if (lendTemplateId != -1) toLend();
        if (bindTemplateId != -1) toBind();
        loaded = true;
    }

    public byte[] getData() {
        return data;
    }

    public void toNote() {
        // ItemDefinitions noteItem; //certTemplateId
        ItemDefinitions realItem = getItemDefinitions(certId);
        membersOnly = realItem.membersOnly;
        value = realItem.value;
        name = realItem.name;
        stackable = 1;
        noted = true;
        clientScriptData = realItem.clientScriptData;
    }

    public void toBind() {
        // ItemDefinitions lendItem; //lendTemplateId
        ItemDefinitions realItem = getItemDefinitions(bindId);
        originalModelColours = realItem.originalModelColours;
        maleEquip3 = realItem.maleEquip3;
        femaleEquip3 = realItem.femaleEquip3;
        teamId = realItem.teamId;
        value = 0;
        membersOnly = realItem.membersOnly;
        name = realItem.name;
        inventoryOptions = new String[5];
        groundOptions = realItem.groundOptions;
        if (realItem.inventoryOptions != null) for (int optionIndex = 0; optionIndex < 4; optionIndex++)
            inventoryOptions[optionIndex] = realItem.inventoryOptions[optionIndex];
        inventoryOptions[4] = "Destroy";
        maleEquip1 = realItem.maleEquip1;
        maleEquip2 = realItem.maleEquip2;
        femaleEquip1 = realItem.femaleEquip1;
        femaleEquip2 = realItem.femaleEquip2;
        clientScriptData = realItem.clientScriptData;
        wearPos = realItem.wearPos;
        wearPos2 = realItem.wearPos2;
        this.wearPos3 = realItem.wearPos3;
    }

    public void toLend() {
        // ItemDefinitions lendItem; //lendTemplateId
        ItemDefinitions realItem = getItemDefinitions(lendId);
        originalModelColours = realItem.originalModelColours;
        maleEquip3 = realItem.maleEquip3;
        femaleEquip3 = realItem.femaleEquip3;
        teamId = realItem.teamId;
        value = 0;
        membersOnly = realItem.membersOnly;
        name = realItem.name;
        inventoryOptions = new String[5];
        groundOptions = realItem.groundOptions;
        if (realItem.inventoryOptions != null) for (int optionIndex = 0; optionIndex < 4; optionIndex++)
            inventoryOptions[optionIndex] = realItem.inventoryOptions[optionIndex];
        inventoryOptions[4] = "Discard";
        maleEquip1 = realItem.maleEquip1;
        maleEquip2 = realItem.maleEquip2;
        femaleEquip1 = realItem.femaleEquip1;
        femaleEquip2 = realItem.femaleEquip2;
        clientScriptData = realItem.clientScriptData;
        wearPos = realItem.wearPos;
        wearPos2 = realItem.wearPos2;
        lended = true;
    }

    public int getArchiveId() {
        return getId() >>> 8;
    }

    public int getFileId() {
        return 0xff & getId();
    }

    public boolean isDestroyItem() {
        if (inventoryOptions == null) return false;
        for (String option : inventoryOptions) {
            if (option == null) continue;
            if (option.equalsIgnoreCase("destroy")) return true;
        }
        return false;
    }

    public boolean containsOption(int i, String option) {
        if (inventoryOptions == null || inventoryOptions[i] == null || inventoryOptions.length <= i) return false;
        return inventoryOptions[i].equals(option);
    }

    public boolean containsOption(String option) {
        if (inventoryOptions == null) return false;
        for (String o : inventoryOptions) {
            if (o == null || !o.equals(option)) continue;
            return true;
        }
        return false;
    }

    public String getInventoryOption(int optionId) {
        switch (id) {
            case 6099:
            case 6100:
            case 6101:
            case 6102:
                if (optionId == 2)
                    return "Temple";
                break;
            case 19760:
            case 13561:
            case 13562:
                if (optionId == 0)
                    return inventoryOptions[1];
                else if (optionId == 1)
                    return inventoryOptions[0];
                break;
        }
        if (inventoryOptions == null)
            return "null";
        if (optionId >= inventoryOptions.length)
            return "null";
        if (inventoryOptions[optionId] == null)
            return "null";
        return inventoryOptions[optionId];
    }

    public boolean isWearItem() {
        return wearPos != -1;
    }

    public boolean containsInventoryOption(int i, String option) {
        if (inventoryOptions == null || inventoryOptions[i] == null || inventoryOptions.length <= i) return false;
        return inventoryOptions[i].equals(option);
    }

    public int getStageOnDeath() {
        if (clientScriptData == null) return 0;
        Object protectedOnDeath = clientScriptData.get(1397);
        if (protectedOnDeath != null && protectedOnDeath instanceof Integer) return (Integer) protectedOnDeath;
        return 0;
    }

    public boolean hasSpecialBar() {
        if (clientScriptData == null) return false;
        Object specialBar = clientScriptData.get(686);
        if (specialBar != null && specialBar instanceof Integer) return (Integer) specialBar == 1;
        return false;
    }

    public int getAttackSpeed() {
        if (clientScriptData == null) return 4;
        Object attackSpeed = clientScriptData.get(14);
        if (attackSpeed != null && attackSpeed instanceof Integer) return (int) attackSpeed;
        return 4;
    }

    public int getStabAttack() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(0);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getSlashAttack() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(1);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getCrushAttack() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(2);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getMagicAttack() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(3);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getRangeAttack() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(4);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getStabDef() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(5);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getSlashDef() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(6);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getCrushDef() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(7);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getMagicDef() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(8);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getRangeDef() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(9);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getSummoningDef() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(417);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getAbsorveMeleeBonus() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(967);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getAbsorveMageBonus() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(969);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getAbsorveRangeBonus() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(968);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getStrengthBonus() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(641);
        if (value != null && value instanceof Integer) return (int) value / 10;
        return 0;
    }

    public int getRangedStrBonus() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(643);
        if (value != null && value instanceof Integer) return (int) value / 10;
        return 0;
    }

    public int getMagicDamage() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(685);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getPrayerBonus() {
        if (id > 25439 || clientScriptData == null) return 0;
        Object value = clientScriptData.get(11);
        if (value != null && value instanceof Integer) return (int) value;
        return 0;
    }

    public int getRenderAnimId() {
        if (id == 23680)//&& id <= 23682)
            return 2574;
        if (clientScriptData == null) return 1426;
        Object animId = clientScriptData.get(644);
        if (animId != null && animId instanceof Integer) return (Integer) animId;
        return 1426;
    }

    public int getModelZoom() {
        return modelZoom;
    }

    public int getModelOffset1() {
        return modelOffsetX;
    }

    public int getModelOffset2() {
        return modelOffsetY;
    }

    public void setDefaultOptions() {
        groundOptions = new String[]{null, null, "take", null, null};
        inventoryOptions = new String[]{null, null, null, null, "drop"};
    }

    public void setDefaultsVariableValues() {
        name = "null";
        maleEquip1 = -1;
        maleEquip2 = -1;
        femaleEquip1 = -1;
        femaleEquip2 = -1;
        modelZoom = 2000;
        lendId = -1;
        lendTemplateId = -1;
        certId = -1;
        certTemplateId = -1;
        resizeZ = 128;
        value = 1;
        maleEquip3 = -1;
        femaleEquip3 = -1;
        bindTemplateId = -1;
        bindId = -1;
        teamId = -1;
        wearPos = -1;
        wearPos2 = -1;
        wearPos3 = -1;
    }

    public final void readValues(InputStream stream, int opcode) {
        if (opcode == 1) modelId = stream.readBigSmart();
        else if (opcode == 2) name = stream.readString();
        else if (opcode == 4) modelZoom = stream.readUnsignedShort();
        else if (opcode == 5) modelRotationX = stream.readUnsignedShort();
        else if (opcode == 6) modelRotationY = stream.readUnsignedShort();
        else if (opcode == 7) {
            realOffsetX = modelOffsetX = stream.readUnsignedShort();
            if (modelOffsetX > 32767) modelOffsetX -= 65536;
            modelOffsetX <<= 0;
        } else if (opcode == 8) {
            realOffsetY = modelOffsetY = stream.readUnsignedShort();
            if (modelOffsetY > 32767) modelOffsetY -= 65536;
            modelOffsetY <<= 0;
        } else if (opcode == 11) stackable = 1;
        else if (opcode == 12) value = stream.readInt();
        else if (opcode == 13) {
            wearPos = stream.readUnsignedByte();
        } else if (opcode == 14) {
            wearPos2 = stream.readUnsignedByte();
        } else if (opcode == 16) membersOnly = true;
        else if (opcode == 18) { // added
            multiStackSize = stream.readUnsignedShort();
        } else if (opcode == 23) maleEquip1 = stream.readBigSmart();
        else if (opcode == 24) maleEquip2 = stream.readBigSmart();
        else if (opcode == 25) femaleEquip1 = stream.readBigSmart();
        else if (opcode == 26) femaleEquip2 = stream.readBigSmart();
        else if (opcode == 27) wearPos3 = stream.readUnsignedByte();
        else if (opcode >= 30 && opcode < 35) groundOptions[opcode - 30] = stream.readString();
        else if (opcode >= 35 && opcode < 40) inventoryOptions[opcode - 35] = stream.readString();
        else if (opcode == 40) {
            int length = stream.readUnsignedByte();
            originalModelColours = new short[length];
            modifiedModelColours = new short[length];
            for (int index = 0; index < length; index++) {
                originalModelColours[index] = (short) stream.readUnsignedShort();
                modifiedModelColours[index] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 41) {
            int length = stream.readUnsignedByte();
            originalTextureIds = new short[length];
            modifiedTextureIds = new short[length];
            for (int index = 0; index < length; index++) {
                originalTextureIds[index] = (short) stream.readUnsignedShort();
                modifiedTextureIds[index] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 42) {
            int length = stream.readUnsignedByte();
            spriteRecolourIndices = new byte[length];
            for (int index = 0; index < length; index++)
                spriteRecolourIndices[index] = (byte) stream.readByte();
        } else if (opcode == 44) {
            i_96_ = stream.readUnsignedShort();
            int arraySize = 0;
            for (int modifier = 0; modifier > 0; modifier++) {
                arraySize++;
                unknownArray3 = new byte[arraySize];
                byte offset = 0;
                for (int index = 0; index < arraySize; index++) {
                    if ((i_96_ & 1 << index) > 0) {
                        unknownArray3[index] = offset;
                    } else {
                        unknownArray3[index] = -1;
                    }
                }
            }
        } else if (45 == opcode) {
            i_97_ = (short) stream.readUnsignedShort();
            int i_98_ = 0;
            for (int i_99_ = i_97_; i_99_ > 0; i_99_ >>= 1)
                i_98_++;
            unknownArray6 = new byte[i_98_];
            byte i_100_ = 0;
            for (int i_101_ = 0; i_101_ < i_98_; i_101_++) {
                if ((i_97_ & 1 << i_101_) > 0) {
                    unknownArray6[i_101_] = i_100_;
                    i_100_++;
                } else unknownArray6[i_101_] = (byte) -1;
            }
        } else if (opcode == 65) grandExchange = true;
        else if (opcode == 78) maleEquip3 = stream.readBigSmart();
        else if (opcode == 79) femaleEquip3 = stream.readBigSmart();
        else if (opcode == 90) maleHead1 = stream.readBigSmart();
        else if (opcode == 91) femaleHead1 = stream.readBigSmart();
        else if (opcode == 92) maleHead2 = stream.readBigSmart();
        else if (opcode == 93) femaleHead2 = stream.readBigSmart();
        else if (opcode == 94) {// new
            int anInt7887 = stream.readUnsignedShort();
        } else if (opcode == 95) modelRotationZ = stream.readUnsignedShort();
        else if (opcode == 96) unknownInt6 = stream.readUnsignedByte();
        else if (opcode == 97) certId = stream.readUnsignedShort();
        else if (opcode == 98) certTemplateId = stream.readUnsignedShort();
        else if (opcode >= 100 && opcode < 110) {
            if (stackIds == null) {
                stackIds = new int[10];
                stackAmounts = new int[10];
            }
            stackIds[opcode - 100] = stream.readUnsignedShort();
            stackAmounts[opcode - 100] = stream.readUnsignedShort();
        } else if (opcode == 110) resizeX = stream.readUnsignedShort();
        else if (opcode == 111) resizeY = stream.readUnsignedShort();
        else if (opcode == 112) resizeZ = stream.readUnsignedShort();
        else if (opcode == 113) ambient = stream.readByte();
        else if (opcode == 114) contrast = stream.readByte() * 5;
        else if (opcode == 115) teamId = stream.readUnsignedByte();
        else if (opcode == 121) lendId = stream.readUnsignedShort();
        else if (opcode == 122) lendTemplateId = stream.readUnsignedShort();
        else if (opcode == 125) {
            maleWearXOffset = stream.readByte() << 2;
            maleWearYOffset = stream.readByte() << 2;
            maleWearZOffset = stream.readByte() << 2;
        } else if (opcode == 126) {
            femaleWearXOffset = stream.readByte() << 2;
            femaleWearYOffset = stream.readByte() << 2;
            femaleWearZOffset = stream.readByte() << 2;
        } else if (opcode == 127) { //CHECK CLIENT FOR THESE VALUES
            unknownInt18 = stream.readUnsignedByte();
            unknownInt19 = stream.readUnsignedShort();
        } else if (opcode == 128) {
            unknownInt20 = stream.readUnsignedByte();
            unknownInt21 = stream.readUnsignedShort();
        } else if (opcode == 129) {
            customCursorOp1 = stream.readUnsignedByte();
            customCursorId1 = stream.readUnsignedShort();
        } else if (opcode == 130) {
            customCursorOp2 = stream.readUnsignedByte();
            customCursorId2 = stream.readUnsignedShort();
        } else if (opcode == 132) {
            int length = stream.readUnsignedByte();
            quests = new int[length];
            for (int index = 0; index < length; index++)
                quests[index] = stream.readUnsignedShort();
        } else if (opcode == 134) {
            pickSizeShift = stream.readUnsignedByte();
        } else if (opcode == 139) {
            bindId = stream.readUnsignedShort();
        } else if (opcode == 140) {
            bindTemplateId = stream.readUnsignedShort();
        } else if (opcode >= 142 && opcode < 147) {
            if (unknownArray4 == null) {
                unknownArray4 = new int[6];
                Arrays.fill(unknownArray4, -1);
            }
            unknownArray4[opcode - 142] = stream.readUnsignedShort();
        } else if (opcode >= 150 && opcode < 155) {
            if (null == unknownArray5) {
                unknownArray5 = new int[5];
                Arrays.fill(unknownArray5, -1);
            }
            unknownArray5[opcode - 150] = stream.readUnsignedShort();
        } else if (opcode == 156) { // new

        } else if (157 == opcode) {// new
            boolean aBool7955 = true;
        } else if (161 == opcode) {// new
            int anInt7904 = stream.readUnsignedShort();
        } else if (162 == opcode) {// new
            int anInt7923 = stream.readUnsignedShort();
        } else if (163 == opcode) {// new
            int anInt7939 = stream.readUnsignedShort();
        } else if (164 == opcode) {// new coinshare shard
            String aString7902 = stream.readString();
        } else if (opcode == 165) {// new
            stackable = 2;
        } else if (opcode == 242) {
            int oldInvModel = stream.readBigSmart();
        } else if (opcode == 243) {
            int oldMaleEquipModelId3 = stream.readBigSmart();
        } else if (opcode == 244) {
            int oldFemaleEquipModelId3 = stream.readBigSmart();
        } else if (opcode == 245) {
            int oldMaleEquipModelId2 = stream.readBigSmart();
        } else if (opcode == 246) {
            int oldFemaleEquipModelId2 = stream.readBigSmart();
        } else if (opcode == 247) {
            int oldMaleEquipModelId1 = stream.readBigSmart();
        } else if (opcode == 248) {
            int oldFemaleEquipModelId1 = stream.readBigSmart();
        } else if (opcode == 251) {
            int length = stream.readUnsignedByte();
            int[] oldoriginalModelColors = new int[length];
            int[] oldmodifiedModelColors = new int[length];
            for (int index = 0; index < length; index++) {
                oldoriginalModelColors[index] = stream.readUnsignedShort();
                oldmodifiedModelColors[index] = stream.readUnsignedShort();
            }
        } else if (opcode == 252) {
            int length = stream.readUnsignedByte();
            short[] oldoriginalTextureColors = new short[length];
            short[] oldmodifiedTextureColors = new short[length];
            for (int index = 0; index < length; index++) {
                oldoriginalTextureColors[index] = (short) stream.readUnsignedShort();
                oldmodifiedTextureColors[index] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 249) {
            int length = stream.readUnsignedByte();
            if (clientScriptData == null) clientScriptData = new HashMap<Integer, Object>(length);
            for (int index = 0; index < length; index++) {
                boolean stringInstance = stream.readUnsignedByte() == 1;
                int key = stream.read24BitInt();
                Object value = stringInstance ? stream.readString() : stream.readInt();
                clientScriptData.put(key, value);
            }
        } else {
            throw new RuntimeException("MISSING OPCODE " + opcode + " FOR ITEM " + getId());
        }
    }

    public int bindTemplateId;
    public int bindId;

    public final void readOpcodeValues(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) break;
            readValues(stream, opcode);
        }
    }

    public boolean faceMask() {
        if (id == 4168)
            return true;
        if (clientScriptData == null) return false;
        if (!clientScriptData.containsKey(625)) return false;
        return (int) clientScriptData.get(625) == 1;
    }

    public String getName() {
        return name;
    }

    public int getFemaleWornModelId1() {
        return femaleEquip1;
    }

    public int getFemaleWornModelId2() {
        return femaleEquip2;
    }

    public int getFemaleWornModelId3() {
        return femaleEquip3;
    }

    public int getMaleWornModelId1() {
        return maleEquip1;
    }

    public int getMaleWornModelId2() {
        return maleEquip2;
    }

    public int getMaleWornModelId3() {
        return maleEquip3;
    }

    public boolean isOverSized() {
        return modelZoom > 5000;
    }

    public boolean isLended() {
        return lended;
    }

    public boolean isMembersOnly() {
        return membersOnly;
    }

    public boolean isStackable() {
        return stackable == 1 || id == 0;
    }

    public boolean isNoted() {
        return noted;
    }

    public int getLendId() {
        return lendId;
    }

    public int getCertId() {
        return certId;
    }

    public int getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public int getEquipSlot() {
        return wearPos;
    }

    public int getEquipType() {
        return wearPos2;
    }

    public boolean isEquipType(int equipType) {
        return this.wearPos2 == equipType;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static int getEquipType(String name) {
        if (name.contains("sword") || name.contains("dagger") || name.contains("scimitar") || name.contains("whip") || name.contains("spear") || name.contains("mace") || name.contains("battleaxe") || name.contains("staff") || name.contains("Staff") || name.contains("battleaxe") || name.contains("hatchet") || name.contains("pickaxe") || name.contains("axe") || name.contains("wand") || name.contains("katana") || name.contains("Katana") || name.contains("scythe") || name.contains("maul")) {
            return 11;
        }
        if (name.contains("plate") || name.contains("body") || name.contains("apron") || name.contains("chest") || name.contains("top")) {
            return 3;
        }
        if (name.contains("gloves")) {
            return 5;
        }
        if (name.contains("boots")) {
            return 6;
        }
        if (name.contains("skirt") || name.contains("legs") || name.contains("bottom")) {
            return 4;
        }
        if (name.contains("helm") || name.contains("hat") || name.contains("hood")) {
            return 0;
        }
        if (name.contains("shield") || name.contains("ket-xil") || name.equalsIgnoreCase("book") || name.contains("defender") || name.contains("teddy") || name.contains("vyre'lector")) {
            return 9;
        }
        if (name.contains("cape") || name.contains("Cape")) {
            return 1;
        }
        if (name.contains("neck")) {
            return 2;
        }
        if (name.contains("arrow") || name.contains("bolt")) {
            return 7;
        }
        if (name.contains("bow")) {
            return 12;
        }
        return -1;
    }

    public int getSheatheModelId() {
        if (clientScriptData == null) return -1;
        Object modelId = clientScriptData.get(2820);
        if (modelId != null && modelId instanceof Integer) return (Integer) modelId;
        return -1;
    }

    public int getCSOpcode(int opcode) {
        if (clientScriptData != null) {
            Object value = clientScriptData.get(opcode);
            if (value != null && value instanceof Integer) return (int) value;
        }
        return 0;
    }

    public boolean isMeleeTypeGear() {
        return getCSOpcode(2821) == 1;
    }

    public boolean isRangeTypeGear() {
        return getCSOpcode(2822) == 1;
    }

    public boolean isMagicTypeGear() {
        return getCSOpcode(2823) == 1;
    }

    public boolean isAllTypeGear() {
        return getCSOpcode(2824) == 1;
    }

    public boolean isMeleeTypeWeapon() {
        return getCSOpcode(2825) == 1;
    }

    public boolean isRangeTypeWeapon() {
        return getCSOpcode(2826) == 1;
    }

    public boolean isMagicTypeWeapon() {
        return getCSOpcode(2827) == 1;
    }

    public int getSpecialAttackAmount() {
        if (clientScriptData == null) return 0;
        final Object data = clientScriptData.get(4332);
        if (data != null && data instanceof Integer) return (Integer) data;
        return 0;
    }

    /**
     * checks if the 3th option is bind and returns true
     *
     * @return
     */
    public boolean isBindItem() {
        if (inventoryOptions == null) return false;
        for (String option : inventoryOptions) {
            if (option == null) continue;
            if (option.equalsIgnoreCase("bind")) return true;
        }
        return false;
    }

    public double getDungShopValueMultiplier() {
        if (clientScriptData == null) return 1;
        Object value = clientScriptData.get(1046);
        if (value != null && value instanceof Integer) return ((Integer) value).doubleValue() / 100;
        return 1;
    }

    public int wearPos3;

    public int getEquipType2() {
        return wearPos3;
    }


    public boolean isShield() {
        return getCSOpcode(2832) == 1;
    }

    private final byte[] encode() {
        OutputStream stream = new OutputStream();

        stream.writeByte(1);
        stream.writeBigSmart(modelId);

        if (!name.equals("null")) {
            stream.writeByte(2);
            stream.writeString(name);
        }

        if (modelZoom != 2000) {
            stream.writeByte(4);
            stream.writeShort(modelZoom);
        }

        if (modelRotationX != 0) {
            stream.writeByte(5);
            stream.writeShort(modelRotationX);
        }

        if (modelRotationY != 0) {
            stream.writeByte(6);
            stream.writeShort(modelRotationY);
        }

        if (realOffsetX != 0) {
            stream.writeByte(7);
            stream.writeShort(realOffsetX);
        }

        if (realOffsetY != 0) {
            stream.writeByte(8);
            stream.writeShort(realOffsetY);
        }

        if (stackable == 1)
            stream.writeByte(11);

        if (value != 1) {
            stream.writeByte(12);
            stream.writeInt(value);
        }

        if (wearPos != -1) {
            stream.writeByte(13);
            stream.writeByte(wearPos);
        }

        if (wearPos2 != -1) {
            stream.writeByte(14);
            stream.writeByte(wearPos2);
        }

        if (membersOnly)
            stream.writeByte(16);

        if (multiStackSize != 0) {
            stream.writeByte(18);
            stream.writeShort(multiStackSize);
        }

        if (maleEquip1 != -1) {
            stream.writeByte(23);
            stream.writeBigSmart(maleEquip1);
        }

        if (maleEquip2 != -1) {
            stream.writeByte(24);
            stream.writeBigSmart(maleEquip2);
        }

        if (femaleEquip1 != -1) {
            stream.writeByte(25);
            stream.writeBigSmart(femaleEquip1);
        }

        if (femaleEquip2 != -1) {
            stream.writeByte(26);
            stream.writeBigSmart(femaleEquip2);
        }

        if (wearPos3 != -1) {
            stream.writeByte(27);
            stream.writeByte(wearPos3);
        }

        String[] DEFAULTGROUND = new String[]{null, null, "take", null, null};
        for (int i = 0; i < groundOptions.length; i++) {
            if (groundOptions[i] != null && !groundOptions[i].equals(DEFAULTGROUND[i])) {
                stream.writeByte(30 + i);
                stream.writeString(groundOptions[i]);
            }
        }

        String[] DEFAULTINV = new String[]{null, null, null, null, "drop"};
        for (int i = 0; i < inventoryOptions.length; i++) {
            if (inventoryOptions[i] != null && !inventoryOptions[i].equals(DEFAULTINV[i])) {
                stream.writeByte(35 + i);
                stream.writeString(inventoryOptions[i]);
            }
        }

        if (originalModelColours != null && modifiedModelColours != null) {
            stream.writeByte(40);
            stream.writeByte(originalModelColours.length);
            for (int i = 0; i < originalModelColours.length; i++) {
                stream.writeShort(originalModelColours[i]);
                stream.writeShort(modifiedModelColours[i]);
            }
        }

        if (originalTextureIds != null && modifiedTextureIds != null) {
            stream.writeByte(41);
            stream.writeByte(originalTextureIds.length);
            for (int i = 0; i < originalTextureIds.length; i++) {
                stream.writeShort(originalTextureIds[i]);
                stream.writeShort(modifiedTextureIds[i]);
            }
        }

        if (spriteRecolourIndices != null) {
            stream.writeByte(42);
            stream.writeByte(spriteRecolourIndices.length);
            for (int i = 0; i < spriteRecolourIndices.length; i++)
                stream.writeByte(spriteRecolourIndices[i]);
        }

        if (unknownArray3 != null) {
            stream.writeByte(44);
            stream.writeShort(i_96_);
        }

        if (unknownArray6 != null) {
            stream.writeByte(45);
            stream.writeShort(i_97_);
        }

        if (grandExchange) {
            stream.writeByte(65);
        }

        if (maleEquip3 != -1) {
            stream.writeByte(78);
            stream.writeBigSmart(maleEquip3);
        }

        if (femaleEquip3 != -1) {
            stream.writeByte(79);
            stream.writeBigSmart(femaleEquip3);
        }

        if (maleHead1 != 0) {
            stream.writeByte(90);
            stream.writeBigSmart(maleHead1);
        }

        if (femaleHead1 != 0) {
            stream.writeByte(91);
            stream.writeBigSmart(femaleHead1);
        }

        if (maleHead2 != 0) {
            stream.writeByte(92);
            stream.writeBigSmart(maleHead2);
        }

        if (femaleHead2 != 0) {
            stream.writeByte(93);
            stream.writeBigSmart(femaleHead2);
        }

        if (modelRotationZ != 0) {
            stream.writeByte(95);
            stream.writeShort(modelRotationZ);
        }

        if (unknownInt6 != 0) {
            stream.writeByte(96);
            stream.writeByte(unknownInt6);
        }

        if (certId != -1) {
            stream.writeByte(97);
            stream.writeShort(certId);
        }

        if (certTemplateId != -1) {
            stream.writeByte(98);
            stream.writeShort(certTemplateId);
        }

        if (stackIds != null && stackAmounts != null) {
            for (int i = 0; i < stackIds.length; i++) {
                if (stackIds[i] == 0 && stackAmounts[i] == 0)
                    continue;
                stream.writeByte(100 + i);
                stream.writeShort(stackIds[i]);
                stream.writeShort(stackAmounts[i]);
            }
        }

        if (resizeX != 0) {
            stream.writeByte(110);
            stream.writeShort(resizeX);
        }

        if (resizeY != 0) {
            stream.writeByte(111);
            stream.writeShort(resizeY);
        }

        if (resizeZ != 128) {
            stream.writeByte(112);
            stream.writeShort(resizeZ);
        }

        if (ambient != 0) {
            stream.writeByte(113);
            stream.writeByte(ambient);
        }

        if (contrast != 0) {
            stream.writeByte(114);
            stream.writeByte(contrast / 5);
        }

        if (teamId != -1) {
            stream.writeByte(115);
            stream.writeByte(teamId);
        }

        if (lendId != -1) {
            stream.writeByte(121);
            stream.writeShort(lendId);
        }

        if (lendTemplateId != -1) {
            stream.writeByte(122);
            stream.writeShort(lendTemplateId);
        }

        if (maleWearXOffset != 0 || maleWearYOffset != 0 || maleWearZOffset != 0) {
            stream.writeByte(125);
            stream.writeByte(maleWearXOffset >> 2);
            stream.writeByte(maleWearYOffset >> 2);
            stream.writeByte(maleWearZOffset >> 2);
        }

        if (femaleWearXOffset != 0 || femaleWearYOffset != 0 || femaleWearZOffset != 0) {
            stream.writeByte(126);
            stream.writeByte(femaleWearXOffset >> 2);
            stream.writeByte(femaleWearYOffset >> 2);
            stream.writeByte(femaleWearZOffset >> 2);
        }

        if (unknownInt18 != -1 || unknownInt19 != -1) {
            stream.writeByte(127);
            stream.writeByte(unknownInt18);
            stream.writeShort(unknownInt19);
        }

        if (unknownInt20 != 0 || unknownInt21 != 0) {
            stream.writeByte(128);
            stream.writeByte(unknownInt20);
            stream.writeShort(unknownInt21);
        }

        if (customCursorOp1 != 0 || customCursorId1 != 0) {
            stream.writeByte(129);
            stream.writeByte(customCursorOp1);
            stream.writeShort(customCursorId1);
        }

        if (customCursorOp2 != 0 || customCursorId2 != 0) {
            stream.writeByte(130);
            stream.writeByte(customCursorOp2);
            stream.writeShort(customCursorId2);
        }

        if (quests != null) {
            stream.writeByte(132);
            stream.writeByte(quests.length);
            for (int index = 0; index < quests.length; index++)
                stream.writeShort(quests[index]);
        }

        if (pickSizeShift != 0) {
            stream.writeByte(134);
            stream.writeByte(pickSizeShift);
        }

        if (bindId != -1) {
            stream.writeByte(139);
            stream.writeShort(bindId);
        }

        if (bindTemplateId != -1) {
            stream.writeByte(140);
            stream.writeShort(bindTemplateId);
        }

        if (clientScriptData != null) {
            stream.writeByte(249);
            stream.writeByte(clientScriptData.size());
            for (int key : clientScriptData.keySet()) {
                Object value = clientScriptData.get(key);
                stream.writeByte(value instanceof String ? 1 : 0);
                stream.write24BitInteger(key);
                if (value instanceof String) {
                    stream.writeString((String) value);
                } else {
                    stream.writeInt((Integer) value);
                }
            }
        }
        stream.writeByte(0);

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);
        return data;
    }

    public RSMesh getBodyMesh(boolean isFemale, MeshModifier modifier) {
        int equip1;
        int equip2;
        int equip3;
        if (isFemale) {
            if (modifier != null && modifier.femaleBody != null) {
                equip1 = modifier.femaleBody[0];
                equip2 = modifier.femaleBody[1];
                equip3 = modifier.femaleBody[2];
            } else {
                equip1 = femaleEquip1;
                equip2 = femaleEquip2;
                equip3 = femaleEquip3;
            }
        } else if (modifier != null && modifier.maleBody != null) {
            equip1 = modifier.maleBody[0];
            equip2 = modifier.maleBody[1];
            equip3 = modifier.maleBody[2];
        } else {
            equip1 = maleEquip1;
            equip2 = maleEquip2;
            equip3 = maleEquip3;
        }
        if (equip1 == -1)
            return null;
        ModelDefinitions defs = ModelDefinitions.getModelDefinitions(equip1);
        if(defs == null)
            return null;
        RSMesh mesh = defs.getMesh();
        if (mesh == null)
            return null;
        if (mesh.version < 13)
            mesh.upscale();
        if (equip2 != -1) {
            RSMesh equip2Mesh = ModelDefinitions.getModelDefinitions(equip2).getMesh();
            if (equip2Mesh.version < 13)
                equip2Mesh.upscale();
            if (equip3 != -1) {
                RSMesh equip3Mesh = ModelDefinitions.getModelDefinitions(equip3).getMesh();
                if (equip3Mesh.version < 13)
                    equip3Mesh.upscale();
                RSMesh[] meshes = {mesh, equip2Mesh, equip3Mesh};
                mesh = new RSMesh(meshes, 3);
            } else {
                RSMesh[] meshes = {mesh, equip2Mesh};
                mesh = new RSMesh(meshes, 2);
            }
        }
//        if (!isFemale && (maleWearXOffset != 0 || maleWearYOffset != 0 || maleWearZOffset != 0))
//            mesh.translate(maleWearXOffset, maleWearYOffset, maleWearZOffset);
//        if (isFemale && (femaleWearXOffset != 0 || femaleWearYOffset != 0 || femaleWearZOffset != 0))
//            mesh.translate(femaleWearXOffset, femaleWearYOffset, femaleWearZOffset);
        int i;
        short[] modified;
        if (originalModelColours != null) {
            if (modifier != null && modifier.modifiedColours != null)
                modified = modifier.modifiedColours;
            else
                modified = modifiedModelColours;
            for (i = 0; i < originalModelColours.length; i++)
                mesh.recolour(originalModelColours[i], modified[i]);
        }
        if (originalTextureIds != null) {
            if (modifier != null && modifier.modifiedTextures != null)
                modified = modifier.modifiedTextures;
            else
                modified = modifiedTextureIds;
            for (i = 0; i < originalTextureIds.length; i++)
                mesh.retexture(originalTextureIds[i], modified[i]);
        }
        return mesh;
    }

    public RSMesh getHeadMesh(boolean isFemale, MeshModifier modifier) {
        int headModel1;
        int headModel2;
        if (isFemale) {
            if (modifier != null && modifier.femaleHead != null) {
                headModel1 = modifier.femaleHead[0];
                headModel2 = modifier.femaleHead[1];
            } else {
                headModel1 = femaleHead1;
                headModel2 = femaleHead2;
            }
        } else if (modifier != null && modifier.maleHead != null) {
            headModel1 = modifier.maleHead[0];
            headModel2 = modifier.maleHead[1];
        } else {
            headModel1 = maleHead1;
            headModel2 = maleHead2;
        }
        if (headModel1 == -1)
            return null;
        RSMesh mesh = ModelDefinitions.getModelDefinitions(headModel1).getMesh();
        if (mesh.version < 13)
            mesh.upscale();
        if (headModel2 != -1) {
            RSMesh mesh2 = ModelDefinitions.getModelDefinitions(headModel2).getMesh();
            if (mesh2.version < 13)
                mesh2.upscale();
            RSMesh[] meshes = {mesh, mesh2};
            mesh = new RSMesh(meshes, 2);
        }
        int i;
        short[] modified;
        if (originalModelColours != null) {
            if (modifier != null && modifier.modifiedColours != null)
                modified = modifier.modifiedColours;
            else
                modified = modifiedModelColours;
            for (i = 0; i < originalModelColours.length; i++)
                mesh.recolour(originalModelColours[i], modified[i]);
        }
        if (originalTextureIds != null) {
            if (modifier != null && modifier.modifiedTextures != null)
                modified = modifier.modifiedTextures;
            else
                modified = modifiedTextureIds;
            for (i = 0; i < originalTextureIds.length; i++)
                mesh.retexture(originalTextureIds[i], modified[i]);
        }
        return mesh;
    }
}