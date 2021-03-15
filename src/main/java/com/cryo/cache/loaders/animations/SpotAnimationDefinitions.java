package com.cryo.cache.loaders.animations;

import com.cryo.Website;
import com.cryo.cache.Cache;
import com.cryo.cache.FileType;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.loaders.model.ModelDefinitions;
import com.cryo.cache.loaders.model.RSMesh;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.modules.account.AccountUtils;
import com.cryo.modules.account.sections.Overview;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Properties;

import static com.cryo.utils.Utilities.error;

@Data
@EndpointSubscriber
public class SpotAnimationDefinitions {

	private int animationId = -1;
	private byte aByte6982;
	private boolean aBool6968;
	private int defaultModelId;
	private int anInt6976 = 128;
	private int anInt6971 = 128;
	private int anInt6978;
	private int anInt6979;
	private int anInt6981;
	private int anInt6980 = -1;
	private short[] originalColours;

	private short[] modifiedColours;

	private short[] originalTextures;

	private short[] modifiedTextures;

	private int id;

	private static HashMap<Integer, SpotAnimationDefinitions> defs = new HashMap<>();

	public static SpotAnimationDefinitions getSpotAnimationDefinitions(int id) {
		if(defs.containsKey(id)) return defs.get(id);
		byte[] data = Cache.STORE.getIndex(IndexType.SPOT_ANIMS).getFile(FileType.SPOT_ANIMS.archiveId(id), FileType.SPOT_ANIMS.fileId(id));
		if(data == null)
			return null;
		SpotAnimationDefinitions defs = new SpotAnimationDefinitions(id);
		defs.decode(new InputStream(data));
		SpotAnimationDefinitions.defs.put(id, defs);
		return defs;
	}

	@Endpoint(method = "POST", endpoint = "/animations/spot/:id")
	public static String loadSpotAnimation(Request request, Response response) {
		Account account = AccountUtils.getAccount(request);
		if(account == null)
			return error("Session has expired. Please refresh the page and try again.");
		if(!NumberUtils.isDigits(request.params(":id")))
			return error("Invalid id. Please try again.");
		int id = Integer.parseInt(request.params(":id"));
		SpotAnimationDefinitions defs = SpotAnimationDefinitions.getSpotAnimationDefinitions(id);
		if(defs == null)
			return error("Unable to find spot animation. Please try again.");
		Properties prop = new Properties();
		prop.put("success", true);
		ModelDefinitions modelDefinitions = ModelDefinitions.getModelDefinitions(defs.defaultModelId);
		if(modelDefinitions == null)
			return error("Unable to load spot animations model. Please try again.");
		RSMesh mesh = modelDefinitions.getMesh();
		mesh.setRealColours();
		mesh.upscale();
		mesh.animationBones = mesh.getBones(true);
		prop.put("model", Overview.getGson().toJson(mesh));
		AnimationDefinitions animationDefinitions = AnimationDefinitions.getAnimationDefinitions(defs.animationId);
		if(animationDefinitions == null)
			return error("Unable to load animation defs. Please try again.");
		prop.put("animation", Overview.getGson().toJson(animationDefinitions));
		return Website.getGson().toJson(prop);
	}

	public SpotAnimationDefinitions(int id) {
		this.id = id;
	}

	public void decode(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				return;
			decode(stream, opcode);
		}
	}

	public void decode(InputStream stream, int opcode) {
		if (opcode == 1)
			defaultModelId = stream.readBigSmart();
		else if (opcode == 2)
			animationId = stream.readBigSmart();
		else if (opcode == 4)
			anInt6976 = stream.readUnsignedShort();
		else if (opcode == 5)
			anInt6971 = stream.readUnsignedShort();
		else if (opcode == 6)
			anInt6978 = stream.readUnsignedShort();
		else if (opcode == 7)
			anInt6979 = stream.readUnsignedByte();
		else if (opcode == 8)
			anInt6981 = stream.readUnsignedByte();
		else if (opcode == 9) {
			aByte6982 = 3;
			anInt6980 = 8224;
		} else if (opcode == 10)
			aBool6968 = true;
		else if (opcode == 11)
			aByte6982 = 1;
		else if (opcode == 12)
			aByte6982 = 4;
		else if (opcode == 13)
			aByte6982 = 5;
		else if (opcode == 14) {
			aByte6982 = 2;
			anInt6980 = stream.readUnsignedByte() * 256;
		} else if (opcode == 15) {
			aByte6982 = 3;
			anInt6980 = stream.readUnsignedShort();
		} else if (opcode == 16) {
			aByte6982 = 3;
			anInt6980 = stream.readInt();
		} else if(opcode == 40) {
			int size = stream.readUnsignedByte();
			originalColours = new short[size];
			modifiedColours = new short[size];
			for (int i = 0; i < size; i++) {
				originalColours[i] = (short) stream.readUnsignedShort();
				modifiedColours[i] = (short) stream.readUnsignedShort();
			}
		} else if(opcode == 41) {
			int size = stream.readUnsignedByte();
			originalTextures = new short[size];
			modifiedTextures = new short[size];
			for (int i = 0; i < size; i++) {
				originalTextures[i] = (short) stream.readUnsignedShort();
				modifiedTextures[i] = (short) stream.readUnsignedShort();
			}
		}
	}
}
