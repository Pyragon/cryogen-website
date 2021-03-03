package com.cryo.cache.loaders.animations;

import com.cryo.Website;
import com.cryo.cache.Cache;
import com.cryo.cache.FileType;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.modules.account.AccountUtils;
import com.cryo.utils.Utilities;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static com.cryo.utils.Utilities.error;

@EndpointSubscriber
public class AnimationDefinitions {

	public int id;
	public int replayMode;
	public int[] interfaceFrames;
	public int[] frameDurations;
	public int[][] soundSettings;
	public int loopDelay = -1;
	public boolean[] aBoolArray5915;
	public int priority = -1;
	public int leftHandItem = 65535;
	public int rightHandItem = 65535;
	public int[] anIntArray5919;
	public int animatingPrecedence;
	public int walkingPrecedence;
	public int[] frameHashes;
	public int[] frameSetIds;
	public int[] anIntArray5923;
	public boolean aBool5923;
	public boolean tweened;
	public int[] soundDurations;
	public int[] anIntArray5927;
	public boolean aBool5928;
	public int maxLoops = 1;
	public int[][] soundFlags;
	private AnimationFrameSet[] frameSets;

	public HashMap<Integer, Object> clientScriptMap = new HashMap<Integer, Object>();

	private static Gson gson;

	static {
		buildGson();
	}

	private static final ConcurrentHashMap<Integer, AnimationDefinitions> animDefs = new ConcurrentHashMap<Integer, AnimationDefinitions>();
	private static final HashMap<Integer, Integer> itemAnims = new HashMap<Integer, Integer>();

	@Endpoint(method = "POST", endpoint = "/animations/:id")
	public static String loadAnimation(Request request, Response response) {
		Account account = AccountUtils.getAccount(request);
		if(account == null) return error("Session has expired. Please refresh the page and try again.");
		if(!NumberUtils.isDigits(request.params(":id")))
			return error("Invalid animation id. Please make sure it is a number and try again.");
		int id = Integer.parseInt(request.params(":id"));
		AnimationDefinitions defs = AnimationDefinitions.getDefs(id);
		if(defs == null)
			return error("Invalid animation. Please try again.");
		//TODO - is animation playable on player model
		//TODO - allow only super donators to play some animations
		Properties prop = new Properties();
		prop.put("success", true);
		prop.put("animation", gson.toJson(defs));
		return Website.getGson().toJson(prop);
	}

	public static void init() {
		for (int i = 0; i < Utilities.getAnimationDefinitionsSize(); i++) {
			AnimationDefinitions defs = getDefs(i);
			if (defs == null)
				continue;
			if (defs.leftHandItem != -1 && defs.leftHandItem != 65535) {
				itemAnims.put(defs.leftHandItem, i);
			}
			if (defs.rightHandItem != -1 && defs.rightHandItem != 65535) {
				itemAnims.put(defs.rightHandItem, i);
			}
		}
	}

	public AnimationFrameSet[] getFrameSets() {
		if (frameSets == null) {
			frameSets = new AnimationFrameSet[frameSetIds.length];
			for (int i = 0; i < frameSetIds.length; i++)
				frameSets[i] = AnimationFrameSet.getFrameSet(frameSetIds[i]);
		}
		return frameSets;
	}

	public static int getAnimationWithItem(int itemId) {
		if (itemAnims.get(itemId) != null)
			return itemAnims.get(itemId);
		return -1;
	}

	public static final AnimationDefinitions getDefs(int emoteId) {
		try {
			AnimationDefinitions defs = animDefs.get(emoteId);
			if (defs != null)
				return defs;
			byte[] data = Cache.STORE.getIndex(IndexType.ANIMATIONS).getFile(FileType.ANIMATIONS.archiveId(emoteId),
					FileType.ANIMATIONS.fileId(emoteId));
			defs = new AnimationDefinitions();
			defs.id = emoteId;
			if (data != null)
				defs.readValueLoop(new InputStream(data));
			defs.setAnimationPrecedence();
			animDefs.put(emoteId, defs);
			return defs;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	private void readValueLoop(InputStream stream) {
		for (; ; ) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	public int getEmoteTime() {
		if (frameDurations == null)
			return 0;
		int ms = 0;
		for (int i : frameDurations)
			ms += i * 20;
		return ms;
	}

	public int getEmoteGameTicks() {
		return getEmoteTime() / 600;
	}

	private void readValues(InputStream stream, int opcode) {
		if (1 == opcode) {
			int frameCount = stream.readUnsignedShort();
			frameDurations = new int[frameCount];
			for (int i = 0; i < frameCount; i++)
				frameDurations[i] = stream.readUnsignedShort();
			frameHashes = new int[frameCount];
			frameSetIds = new int[frameCount];
			for (int i = 0; i < frameCount; i++)
				frameHashes[i] = stream.readUnsignedShort();
			for (int i = 0; i < frameCount; i++)
				frameHashes[i] += (stream.readUnsignedShort() << 16);
			for (int i = 0; i < frameCount; i++)
				frameSetIds[i] = frameHashes[i] >>> 16;
		} else if (opcode == 2)
			loopDelay = stream.readUnsignedShort();
		else if (3 == opcode) {
			aBoolArray5915 = new boolean[256];
			int size = stream.readUnsignedByte();
			for (int i = 0; i < size; i++)
				aBoolArray5915[stream.readUnsignedByte()] = true;
		} else if (5 == opcode)
			priority = stream.readUnsignedByte();
		else if (6 == opcode)
			leftHandItem = stream.readUnsignedShort();
		else if (opcode == 7)
			rightHandItem = stream.readUnsignedShort();
		else if (8 == opcode)
			maxLoops = stream.readUnsignedByte();
		else if (9 == opcode)
			animatingPrecedence = stream.readUnsignedByte();
		else if (10 == opcode)
			walkingPrecedence = stream.readUnsignedByte();
		else if (opcode == 11)
			replayMode = stream.readUnsignedByte();
		else if (opcode == 12) {
			int i_9_ = stream.readUnsignedByte();
			interfaceFrames = new int[i_9_];
			for (int i_10_ = 0; i_10_ < i_9_; i_10_++)
				interfaceFrames[i_10_] = stream.readUnsignedShort();
			for (int i_11_ = 0; i_11_ < i_9_; i_11_++)
				interfaceFrames[i_11_] = (stream.readUnsignedShort() << 16) + interfaceFrames[i_11_];
		} else if (13 == opcode) {
			int i_12_ = stream.readUnsignedShort();
			soundSettings = new int[i_12_][];
			soundFlags = new int[i_12_][];
			for (int i_13_ = 0; i_13_ < i_12_; i_13_++) {
				int i_14_ = stream.readUnsignedByte();
				if (i_14_ > 0) {
					soundSettings[i_13_] = new int[i_14_];
					soundSettings[i_13_][0] = stream.read24BitInt();
					soundFlags[i_13_] = new int[3];
					soundFlags[i_13_][0] = soundSettings[i_13_][0] >> 8;
					soundFlags[i_13_][1] = soundSettings[i_13_][0] >> 5 & 0x7;
					soundFlags[i_13_][2] = soundSettings[i_13_][0] & 0x1f;
					for (int i_15_ = 1; i_15_ < i_14_; i_15_++)
						soundSettings[i_13_][i_15_] = stream.readUnsignedShort();
				}
			}
		} else if (opcode == 14)
			aBool5923 = true;
		else if (opcode == 15)
			tweened = true;
		else if (opcode != 16) {
			if (18 == opcode)
				aBool5928 = true;
			else if (19 == opcode) {
				if (soundDurations == null) {
					soundDurations = new int[soundSettings.length];
					for (int i_16_ = 0; i_16_ < soundSettings.length; i_16_++)
						soundDurations[i_16_] = 255;
				}
				soundDurations[stream.readUnsignedByte()] = stream.readUnsignedByte();
			} else if (opcode == 20) {
				if (null == anIntArray5927 || null == anIntArray5919) {
					anIntArray5927 = new int[soundSettings.length];
					anIntArray5919 = new int[soundSettings.length];
					for (int i_17_ = 0; i_17_ < soundSettings.length; i_17_++) {
						anIntArray5927[i_17_] = 256;
						anIntArray5919[i_17_] = 256;
					}
				}
				int i_18_ = stream.readUnsignedByte();
				anIntArray5927[i_18_] = stream.readUnsignedShort();
				anIntArray5919[i_18_] = stream.readUnsignedShort();
			} else if (249 == opcode) {
				int i_19_ = stream.readUnsignedByte();
				for (int i_21_ = 0; i_21_ < i_19_; i_21_++) {
					boolean bool = stream.readUnsignedByte() == 1;
					int i_22_ = stream.read24BitInt();
					if (bool)
						clientScriptMap.put(i_22_, stream.readString());
					else
						clientScriptMap.put(i_22_, stream.readInt());
				}
			}
		}
	}

	void setAnimationPrecedence() {
		if (animatingPrecedence == -1) {
			if (aBoolArray5915 != null)
				animatingPrecedence = 2;
			else
				animatingPrecedence = 0;
		}
		if (walkingPrecedence == -1) {
			if (null != aBoolArray5915)
				walkingPrecedence = 2;
			else
				walkingPrecedence = 0;
		}
	}

	public static void buildGson() {
		gson = new GsonBuilder()
				.serializeNulls()
				.setVersion(1.0)
				.disableHtmlEscaping()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();
	}
}
