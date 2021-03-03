package com.cryo.cache.loaders.animations;

import com.cryo.Website;
import com.cryo.cache.Cache;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.cache.store.Index;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.utils.list.LinkedList;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Properties;

import static com.cryo.utils.Utilities.error;

@EndpointSubscriber
public class AnimationFrameSet {

	private static Gson gson;

	static {
		buildGson();
	}

	private int id;
	private byte[][] frameData;
	private AnimationFrame[] frames;

	public static HashMap<Integer, AnimationFrameSet> sets = new HashMap<>();

	public AnimationFrameSet(int id) {
		this.id = id;
	}

	static int count = 0;

	public boolean decodeFrameData() {
		if (frames != null) return true;
		try {
			if (Cache.STORE == null)
				return false;
			Index setIndex = Cache.STORE.getIndex(IndexType.ANIMATION_FRAME_SETS);
			Index frameIndex = Cache.STORE.getIndex(IndexType.ANIMATION_FRAME_BASES);
			if (frameData == null) {
				int[] fileIds = setIndex.getTable().getArchives()[id].getValidFileIds();
				frameData = new byte[fileIds.length][];
				for (int i = 0; i < frameData.length; i++)
					frameData[i] = setIndex.getFile(id, fileIds[i]);
			}

			LinkedList frameBaseList = new LinkedList();
			int fileCount = setIndex.getTable().getArchives()[id].getFiles().length;
			frames = new AnimationFrame[fileCount];
			int[] fileIds = setIndex.getTable().getArchives()[id].getValidFileIds();

			for (int i = 0; i < fileIds.length; i++) {
				byte[] frameData = this.frameData[i];
				InputStream frameBuffer = new InputStream(frameData);
				frameBuffer.setOffset(1);
				int frameId = frameBuffer.readUnsignedShort();
				AnimationFrameBase frameBase = null;

				for (AnimationFrameBase iter = (AnimationFrameBase) frameBaseList.getBack(); iter != null; iter = (AnimationFrameBase) frameBaseList.getPrevious()) {
					if (frameId == iter.id) {
						frameBase = iter;
						break;
					}
				}

				if (frameBase == null) {
					frameBase = new AnimationFrameBase(frameId);
					byte[] data = frameIndex.getAnyFile(frameId);
					try {
						frameBase.decode(new InputStream(data));
					} catch(Exception e) {
						e.printStackTrace();
					}
					frameBaseList.insertBack(frameBase);
				}

				//stupid hack for gson
				GsonFrameBase file = new GsonFrameBase(frameBase.id, frameBase.labels, frameBase.anIntArray7561, frameBase.transformationTypes, frameBase.aBoolArray7563, frameBase.count);

				int fileId = fileIds[i];
				frames[fileId] = AnimationFrame.getFrame(this.id, fileId, frameData, file);
			}
			frameData = null;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static AnimationFrameSet getFrameSet(int id) {
		try {
			if (sets.containsKey(id)) return sets.get(id);
			AnimationFrameSet set = new AnimationFrameSet(id);
			if (set.decodeFrameData()) {
				sets.put(id, set);
				return set;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Endpoint(method = "POST", endpoint = "/animations/sets/:id")
	public static String getFrameSetData(Request request, Response response) {
		if (!NumberUtils.isDigits(request.params(":id")))
			return error("Error parsing id.");
		int id = Integer.parseInt(request.params(":id"));
		AnimationFrameSet set = getFrameSet(id);
		if (set == null)
			return error("Unable to find frame set. Please try again.");
		Properties prop = new Properties();
		prop.put("success", true);
		String json;
		try {
			json = gson.toJson(set);
			prop.put("set", json);
			return Website.getGson().toJson(prop);
		} catch (Exception e) {
			e.printStackTrace();
			return error("Error loading frame data.");
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
