package com.cryo.cache.loaders;

import com.cryo.cache.Cache;
import com.cryo.cache.FileType;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.model.ModelDefinitions;
import com.cryo.cache.loaders.model.RSMesh;

import java.util.Arrays;
import java.util.HashMap;

public class IdentiKitDefinition {

    private int id;
    private int[] modelIds;
    private short[] originalColours;
    private short[] replacementColours;
    private short[] originalTextures;
    private short[] replacementTextures;
    private int[] headModels = {-1, -1, -1, -1, -1};

    private static HashMap<Integer, IdentiKitDefinition> defs = new HashMap<>();

    public static IdentiKitDefinition getIdentikitDefinition(int id) {
        if (defs.containsKey(id)) return defs.get(id);
        byte[] data = Cache.STORE.getIndex(IndexType.CONFIG).getFile(FileType.IDENTIKIT.getId(), id);
        if (data == null) return null;
        IdentiKitDefinition defs = new IdentiKitDefinition(id);
        defs.decode(new InputStream(data));
        IdentiKitDefinition.defs.put(id, defs);
        return defs;
    }

    public IdentiKitDefinition(int id) {
        this.id = id;
    }

    public RSMesh renderHead(boolean upscale) {
        RSMesh[] meshes = new RSMesh[5];
        int index = 0;
        for (int i = 0; i < 5; i++) {
            if (headModels[i] != -1) {
                ModelDefinitions defs = ModelDefinitions.getModelDefinitions(headModels[i]);
                if (defs != null)
                    meshes[index++] = defs.getMesh();
            }
        }
        for (int i = 0; i < 5; i++) {
            if (meshes[i] != null && meshes[i].version < 13)
                meshes[i].upscale();
        }
        RSMesh mesh = new RSMesh(meshes, index);
        if (originalColours != null) {
            for (int i = 0; i < originalColours.length; i++) {
                mesh.recolour(originalColours[i], replacementColours[i]);
            }
        }
        if (originalTextures != null) {
            for (int i = 0; i < originalTextures.length; i++) {
                mesh.retexture(originalTextures[i], replacementTextures[i]);
            }
        }
        return mesh;
    }

    public RSMesh renderBody() {
        if (modelIds == null)
            return null;
        RSMesh[] meshes = new RSMesh[modelIds.length];
        int index = 0;
        while (index < modelIds.length) {
            meshes[index] = ModelDefinitions.getModelDefinitions(modelIds[index]).getMesh();
            ++index;
        }
        for (int i_5 = 0; i_5 < modelIds.length; i_5++) {
            if (meshes[i_5].version < 13) {
                meshes[i_5].upscale();
            }
        }
        RSMesh mesh = meshes.length == 1 ? meshes[0] : new RSMesh(meshes, meshes.length);
        if (mesh == null)
            return null;
        if (originalColours != null) {
            System.out.println("REPLACING IDENTIKIT COLOURS: "+Arrays.toString(originalColours)+", "+Arrays.toString(replacementColours));
            for (index = 0; index < originalColours.length; index++) {
                mesh.recolour(originalColours[index], replacementColours[index]);
            }
        }
        if (originalTextures != null) {
            System.out.println("REPLACING IDENTIKIT TEXTURES: "+Arrays.toString(originalTextures)+", "+Arrays.toString(replacementTextures));
            for (index = 0; index < originalTextures.length; index++) {
                mesh.retexture(originalTextures[index], replacementTextures[index]);
            }
        }
        return mesh;
    }

    public void decode(int opcode, InputStream stream) {
        if (opcode == 1)
            stream.readUnsignedByte();
        else if (opcode == 2) {
            int count = stream.readUnsignedByte();
            modelIds = new int[count];
            for (int i = 0; i < count; i++) {
                modelIds[i] = stream.readBigSmart();
            }
        } else if (opcode == 40) {
            int count = stream.readUnsignedByte();
            originalColours = new short[count];
            replacementColours = new short[count];
            for (int i = 0; i < count; i++) {
                originalColours[i] = (short) stream.readUnsignedShort();
                replacementColours[i] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 41) {
            int count = stream.readUnsignedByte();
            originalTextures = new short[count];
            replacementTextures = new short[count];
            for (int i = 0; i < count; i++) {
                originalTextures[i] = (short) stream.readUnsignedShort();
                replacementTextures[i] = (short) stream.readUnsignedShort();
            }
        } else if (opcode >= 60 && opcode < 70) {
            headModels[opcode - 60] = stream.readBigSmart();
        }
    }

    public void decode(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) break;
            decode(opcode, stream);
        }
    }
}
