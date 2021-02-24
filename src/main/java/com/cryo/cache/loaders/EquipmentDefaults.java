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

    public static EquipmentDefaults DEFAULTS;

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
        DEFAULTS = defaults;
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
