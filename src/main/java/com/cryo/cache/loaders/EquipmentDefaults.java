package com.cryo.cache.loaders;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.annotations.WebStartSubscriber;
import com.cryo.utils.Logger;

@WebStartSubscriber
public class EquipmentDefaults {

    public static final byte HAT = 0, CAPE = 1, AMULET = 2, WEAPON = 3, CHEST = 4, SHIELD = 5, LEGS = 7, HANDS = 9, FEET = 10, RING = 12, ARROWS = 13, AURA = 14;

    public static final int FEMALE_HAIR_STRUCT_LOOKUP = 2341;
    public static final int MALE_HAIR_STRUCT_LOOKUP = 2338;
    public static final int MALE_HAIR_SLOT_LOOKUP = 2339;
    public static final int FEMALE_HAIR_SLOT_LOOKUP = 2342;

    public static final int HAIR_WITH_HAT_PARAM = 790;
    public static final int HAIR_WITH_FACE_MASK_PARAM = 791;

    public static EquipmentDefaults EQUIPMENT_DEFAULTS;

    public static short[][] DEFAULT_SKIN_COLOURS;
    public static short[][][] SKIN_COLOURS;

    public int[] hidden;
    public int offhandSlot = -1;
    public int weaponSlot = -1;
    public int[] hiddenAnimationOffhandSlots;
    public int[] hiddenAnimationWeaponSlots;

    @WebStart
    public static void loadEquipmentDefaults() {
        byte[] data = Cache.STORE.getIndex(IndexType.DEFAULTS).getAnyFile(6);
        if(data == null) {
            Logger.log("EquipmentDefaults", "Error loading equipment defaults!", true);
            return;
        }
        EquipmentDefaults defaults = new EquipmentDefaults();
        defaults.decode(new InputStream(data));
        EQUIPMENT_DEFAULTS = defaults;
    }

    public static boolean hideArms(int id) {
        ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
        if(defs == null) return false;
        String name = defs.getName();
        if (name.contains("chainbody")) return true;
        if (name.contains("d'hide body") || name.contains("dragonhide body") || name.equals("stripy pirate shirt") || (name.contains("chainbody") && (name.contains("iron") || name.contains("bronze") || name.contains("steel") || name.contains("black") || name.contains("mithril") || name.contains("adamant") || name.contains("rune") || name.contains("white"))) || name.equals("leather body") || name.equals("hardleather body") || name.contains("studded body"))
            return false;
        return defs.isEquipType(6);
    }

    public static boolean hideHair(int id) {
        ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
        if(defs == null) return false;
        String name = defs.getName();
        if (name.contains("full helm") || name.equals("Virtus mask") || name.equals("Vanguard helm")) return true;
        return defs.isEquipType(8);
    }

    public static boolean hideBeard(int id) {
        ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
        if(defs == null) return false;
        return defs.isEquipType(11);
    }

    public static int getHatHairStyle(int baseStyle, boolean isFaceMask, boolean isFemale) {
        EnumDefinitions lookup = EnumDefinitions.getEnum(!isFemale ? MALE_HAIR_SLOT_LOOKUP : FEMALE_HAIR_SLOT_LOOKUP);
        int slot = lookup.getIntValue(baseStyle);
        EnumDefinitions structLookup = EnumDefinitions.getEnum(!isFemale ? MALE_HAIR_STRUCT_LOOKUP : FEMALE_HAIR_STRUCT_LOOKUP);
        int structID = structLookup.getIntValue(slot);
        return StructDefinitions.getStruct(structID).getIntValue(isFaceMask ? HAIR_WITH_FACE_MASK_PARAM : HAIR_WITH_HAT_PARAM, -1);
    }

    public void decode(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            if (opcode == 1) {
                int count = stream.readUnsignedByte();
                hidden = new int[count];
                for (int i_5 = 0; i_5 < hidden.length; i_5++) {
                    hidden[i_5] = stream.readUnsignedByte();
                    if (hidden[i_5] != 0) {
                        //int i_10000 = this.equipmentSlots[i_5];
                    }
                }
            } else if (opcode == 3) {
                offhandSlot = stream.readUnsignedByte();
            } else if (opcode == 4) {
                weaponSlot = stream.readUnsignedByte();
            } else if (opcode == 5) {
                hiddenAnimationOffhandSlots = new int[stream.readUnsignedByte()];
                for (int i_4 = 0; i_4 < hiddenAnimationOffhandSlots.length; i_4++) {
                    hiddenAnimationOffhandSlots[i_4] = stream.readUnsignedByte();
                }
            } else if (opcode == 6) {
                hiddenAnimationWeaponSlots = new int[stream.readUnsignedByte()];
                for (int i_4 = 0; i_4 < hiddenAnimationWeaponSlots.length; i_4++) {
                    hiddenAnimationWeaponSlots[i_4] = stream.readUnsignedByte();
                }
            }
        }
    }
}
