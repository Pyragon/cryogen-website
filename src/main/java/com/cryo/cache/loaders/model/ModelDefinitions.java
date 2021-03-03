package com.cryo.cache.loaders.model;

import com.cryo.Website;
import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.*;
import com.cryo.cache.loaders.animations.AnimationDefinitions;
import com.cryo.entities.accounts.Account;
import com.cryo.utils.Logger;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

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

    public static void dumpModel(int id) {
        byte[] data = Cache.STORE.getIndex(IndexType.MODELS).getFile(id, 0);
        if(data == null) return;
        File file = new File("models/"+id+".dat");
        if(file.exists()) file.delete();
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ModelDefinitions(int id) {
        this.id = id;
    }

    public static RSMesh renderPlayerHead(Account account) {
        try {
            int[] look = account.getLook();
            RSMesh[] meshes = new RSMesh[15];
            int size = 0;
            LinkedTreeMap<String, Object>[] equipped = account.getEquippedItems();
            if (equipped == null)
                equipped = new LinkedTreeMap[15];
            int id = (int) equipped[EquipmentDefaults.HAT].get("id");
            if(id != -1) {
                ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
                RSMesh mesh;
                if(defs != null && (mesh = defs.getHeadMesh(account.getGender() == 1, null)) != null)
                    meshes[size++] = mesh;
            }
            for (int l : look) {
                if (l == -1) continue;
                IdentiKitDefinition defs = IdentiKitDefinition.getIdentikitDefinition(l);
                RSMesh mesh;
                if (defs == null || (mesh = defs.renderHead(size > 0)) == null) continue;
                meshes[size++] = mesh;
            }
            return new RSMesh(meshes, size);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RSMesh renderPlayerBody(Account account) {
        try {
            int[] look = account.getLook();
            LinkedTreeMap<String, Object>[] equipped = account.getEquippedItems();
            if (equipped == null)
                equipped = new LinkedTreeMap[20];
            RSMesh[] meshes = new RSMesh[20];
            int size = 0;
            for(int i = 0; i < 4; i++) {
                if(equipped[i] == null) continue;
                int id = (int) ((double) equipped[i].get("id"));
                if(id == -1) continue;
                MeshModifier modifier = null;
                if(equipped[i].containsKey("colours")) {
                    short[] colours = new short[4];
                    ArrayList<Double> iColours = (ArrayList<Double>) equipped[i].get("colours");
                    int index = 0;
                    for(Double col : iColours)
                        colours[index++] = col.shortValue();
                    modifier = new MeshModifier(colours, null);
                }
                ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
                RSMesh mesh;
                if (defs == null || (mesh = defs.getBodyMesh(account.getGender() == 1, modifier)) == null) continue;
                meshes[size++] = mesh;
            }
            //chest
            RSMesh mesh;
            int id = (int) ((double) equipped[EquipmentDefaults.CHEST].get("id"));
            if(id == -1)
                mesh = renderLook(2);
            else
                mesh = renderItem(account, id, null);
            if(mesh != null)
                meshes[size++] = mesh;

            id = (int) ((double) equipped[EquipmentDefaults.SHIELD].get("id"));
            if(id != -1)
                meshes[size++] = renderItem(account, id, null);

            id = (int) ((double) equipped[EquipmentDefaults.CHEST].get("id"));
            mesh = null;
            if(look[3] == -1 && (id == -1 || !EquipmentDefaults.hideArms(id)))
                mesh = renderLook(3);
            else if(id != -1 && !EquipmentDefaults.hideArms(id))
                mesh = renderIdentikit(account.getGender() == 0 ? 26 : 61);
            if(mesh != null)
                meshes[size++] = mesh;

            id = (int) ((double) equipped[EquipmentDefaults.LEGS].get("id"));
            if(id == -1)
                mesh = renderLook(5);
            else
                mesh = renderItem(account, id, null);
            if(mesh != null)
                meshes[size++] = mesh;

            id = (int) ((double) equipped[EquipmentDefaults.HAT].get("id"));
            mesh = null;
            if(look[0] != -1 && (id == -1 || !EquipmentDefaults.hideHair(id))) {
                if(id == -1)
                    mesh = renderLook(0);
                else {
                    ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
                    if(defs != null) {
                        int style = EquipmentDefaults.getHatHairStyle(getDefaultLook()[0], defs.faceMask(), account.getGender() == 1);
                        if (style != -1)
                            mesh = renderIdentikit(style);
                    }
                }
            }
            if(mesh != null)
                meshes[size++] = mesh;

            id = (int) ((double) equipped[EquipmentDefaults.HANDS].get("id"));
            if(id == -1)
                mesh = renderLook(4);
            else
                mesh = renderItem(account, id, null);
            meshes[size++] = mesh;

            id = (int) ((double) equipped[EquipmentDefaults.FEET].get("id"));
            if(id == -1)
                mesh = renderLook(6);
            else
                mesh = renderItem(account, id, null);
            meshes[size++] = mesh;

            boolean male = account.getGender() == 0;
            int slot = male ? EquipmentDefaults.HAT : EquipmentDefaults.CHEST;
            id = (int) ((double) equipped[slot].get("id"));
            if(male && look[1] != -1 && (id == -1 || (male && !EquipmentDefaults.hideBeard(id))))
                meshes[size++] = renderLook(1);

            slot = EquipmentDefaults.AURA;
            id = (int) ((double) equipped[slot].get("id"));
            if(id != -1 && equipped[slot].containsKey("models")) {
                ArrayList<Double> models = (ArrayList<Double>) equipped[slot].get("models");
                for(Double modelId : models) {
                    if(modelId == -1) continue;
                    ModelDefinitions defs = ModelDefinitions.getModelDefinitions(modelId.intValue());
                    if(defs == null) continue;
                    meshes[size++] = defs.getMesh();
                }
            }

            mesh = new RSMesh(meshes, size);
            EntityDefaults defaults = EntityDefaults.ENTITY_DEFAULTS;
            if(account.getColours() != null && defaults != null) {
                for(int i = 0; i < 10; i++) {
                    for(int j = 0; j < defaults.getOriginalColours()[i].length; j++) {
                        if(account.getColours()[i] < defaults.getReplacementColours()[i][j].length) {
                            mesh.recolour(defaults.getOriginalColours()[i][j], defaults.getReplacementColours()[i][j][account.getColours()[i]]);
                        }
                    }
                }
            }
            BASDefinitions defs = BASDefinitions.getDefs(getRenderEmote(account));
            AnimationDefinitions animation = AnimationDefinitions.getDefs(defs.standAnimation);
            if(animation != null)
                mesh.animation = animation;
            mesh.animationBones = mesh.getBones(true);
            return mesh;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getRenderEmote(Account account) {
        int id = (int) ((double) account.getEquippedItems()[EquipmentDefaults.WEAPON].get("id"));
        if(id == -1)
            return 1426;
        return ItemDefinitions.getItemDefinitions(id).getRenderAnimId();
    }

    public static RSMesh renderLook(int look) {
        int id = getDefaultLook()[look];
        if(id == -1) return null;
        return renderIdentikit(id);
    }

    public static RSMesh renderIdentikit(int id) {
        IdentiKitDefinition defs = IdentiKitDefinition.getIdentikitDefinition(id);
        if(defs == null) return null;
        return defs.renderBody();
    }

    public static RSMesh renderItem(Account account, int id, MeshModifier modifier) {
        ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
        if(defs == null) return null;
        return defs.getBodyMesh(account.getGender() == 1, modifier);
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

    public static int getAuraModelId(int weaponId) {
        if(weaponId == -1) return 8719;
        String name = ItemDefinitions.getItemDefinitions(weaponId).getName();
        if (name.contains("dagger")) return 8724;
        if (name.contains("whip")) return 8725;
        if (name.contains("2h sword") || name.contains("godsword")) return 8773;
        if (name.contains("sword") || name.contains("scimitar") || name.contains("korasi")) return 8722;
        return 8719;
    }

    public static int getAuraModelId2(int aura) {
        switch (aura) {
            case 22905: // Corruption.
                return 16449;
            case 22899: // Salvation.
                return 16465;
            case 23848: // Harmony.
                return 68605;
            case 22907: // Greater corruption.
                return 16464;
            case 22901: // Greater salvation.
                return 16524;
            case 23850: // Greater harmony.
                return 68610;
            case 22909: // Master corruption.
                return 16429;
            case 22903: // Master salvation.
                return 16450;
            case 23852: // Master harmony.
                return 68607;
            case 23874: // Supreme corruption.
                return 68615;
            case 23876: // Supreme salvation.
                return 68611;
            case 23854: // Supreme harmony.
                return 68613;
            default:
                return -1;
        }
    }
}
