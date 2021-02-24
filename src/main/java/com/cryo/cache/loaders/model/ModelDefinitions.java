package com.cryo.cache.loaders.model;

import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.EquipmentDefaults;
import com.cryo.cache.loaders.IdentiKitDefinition;
import com.cryo.cache.loaders.ItemDefinitions;
import com.cryo.entities.accounts.Account;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;

@Data
public class ModelDefinitions {

    private int id;
    private RSMesh mesh;

    private static HashMap<Integer, ModelDefinitions> defs = new HashMap<>();

    public static ModelDefinitions getModelDefinitions(int id) {
        if(defs.containsKey(id)) return defs.get(id);
        byte[] data = Cache.STORE.getIndex(IndexType.MODELS).getFile(id, 0);
        if(data == null) return null;
        ModelDefinitions defs = new ModelDefinitions(id);
        try {
            RSMesh mesh = new RSMesh();
            mesh.decode(data);
            defs.mesh = mesh;
        } catch(Exception e) {
            e.printStackTrace();
        }
        ModelDefinitions.defs.put(id, defs);
        return defs;
    }

    public ModelDefinitions(int id) {
        this.id = id;
    }

    public static RSMesh renderPlayerHead() {
        int[] defaultLook = getDefaultLook();
        RSMesh[] meshes = new RSMesh[defaultLook.length];
        int size = 0;
        for (int look : defaultLook) {
            if(look == -1) continue;
            IdentiKitDefinition defs = IdentiKitDefinition.getIdentikitDefinition(look);
            RSMesh mesh;
            if(defs == null || (mesh = defs.renderHead()) == null) continue;
             meshes[size++] = mesh;
        }
        return new RSMesh(meshes, size);
    }

    public static RSMesh renderPlayerBody(Account account) {
        try {
            int[] defaultLook = getDefaultLook();
            RSMesh[] meshes = new RSMesh[15];
            int size = 0;
            int[] equipped = account.getEquippedItems();
            if (equipped == null) {
                equipped = new int[15];
                Arrays.fill(equipped, -1);
            }
            for(int i = 0; i < 4; i++) {
                if(equipped[i] == -1) continue;
                ItemDefinitions defs = ItemDefinitions.getItemDefinitions(equipped[i]);
                RSMesh mesh;
                if (defs == null || (mesh = defs.getBodyMesh(account.getGender() == 1, null)) == null) continue;
                meshes[size++] = mesh;
            }
            for (int i = 0; i < defaultLook.length; i++) {
                int look = defaultLook[i];
                if (look == -1) continue;
                int equipSlot = getEquipmentSlot(i);
                if (equipSlot != -1 && equipped[equipSlot] != -1) {
                    ItemDefinitions defs = ItemDefinitions.getItemDefinitions(equipped[equipSlot]);
                    RSMesh mesh;
                    if (defs == null || (mesh = defs.getBodyMesh(account.getGender() == 1, null)) == null) continue;
                    meshes[size++] = mesh;
                    continue;
                }
                IdentiKitDefinition defs = IdentiKitDefinition.getIdentikitDefinition(look);
                RSMesh mesh;
                if (defs == null || (mesh = defs.renderBody()) == null) continue;
                meshes[size++] = mesh;
            }
            return new RSMesh(meshes, size);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getEquipmentSlot(int lookIndex) {
        switch(lookIndex) {
            case 2: return EquipmentDefaults.CHEST;
            case 4: return EquipmentDefaults.HANDS;
            case 5: return EquipmentDefaults.LEGS;
            case 6: return EquipmentDefaults.FEET;
            default: return -1;
        }
    }

    public static int[] getDefaultLook() {
        int[] look = new int[7];
        look[0] = 310; //face (minus jaw)
        look[1] = 16; //jaw
        look[2] = 452; //body
        look[3] = -1; // (arms probably)
        look[4] = 371; //hands
        look[5] = 627; //legs
        look[6] = 433; //feet
        return look;
    }
}
