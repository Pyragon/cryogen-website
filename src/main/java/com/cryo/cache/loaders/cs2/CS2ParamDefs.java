package com.cryo.cache.loaders.cs2;

import com.cryo.cache.Cache;
import com.cryo.cache.FileType;
import com.cryo.cache.IndexType;
import com.cryo.cache.io.InputStream;
import com.cryo.utils.Logger;
import com.cryo.utils.Utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

public final class CS2ParamDefs {

	public int id;
	public int defaultInt;
	public boolean autoDisable = true;
	public char charVal;
	public String defaultString;

	private static final ConcurrentHashMap<Integer, CS2ParamDefs> maps = new ConcurrentHashMap<Integer, CS2ParamDefs>();
	
//	public static void main(String[] args) throws IOException {
//		Cache.init();
//		File file = new File("params.txt");
//		if (file.exists())
//			file.delete();
//		else
//			file.createNewFile();
//		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//		writer.append("//Version = 727\n");
//		writer.flush();
//		for (int i = 0; i < Cache.STORE.getIndex(IndexType.CONFIG).getValidFilesCount(FileType.PARAMS.getId()); i++) {
//			CS2ParamDefs param = getParams(i);
//			if (param == null)
//				continue;
//			writer.append(i + " - '"+param.charVal+"'->" + param.type + ", "+param.autoDisable+" int: " + param.defaultInt + " str:\"" + param.defaultString + "\"");
//			writer.newLine();
//			writer.flush();
//		}
//		writer.close();
//	}

	public static final CS2ParamDefs getParams(int paramId) {
		CS2ParamDefs param = maps.get(paramId);
		if (param != null)
			return param;
		byte[] data = Cache.STORE.getIndex(IndexType.CONFIG).getFile(FileType.PARAMS.getId(), paramId);
		param = new CS2ParamDefs();
		param.id = paramId;
		if (data != null)
			param.readValueLoop(new InputStream(data));
		maps.put(paramId, param);
		return param;
	}


	private void readValueLoop(InputStream stream) {
		for (;;) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

    public boolean isString() {
        return charVal == 's';
    }

	private void readValues(InputStream stream, int opcode) {
		if (opcode == 1) {
			charVal = Utilities.cp1252ToChar((byte) stream.readByte());
			//type = CS2Type.forJagexDesc(charVal);
		} else if (opcode == 2) {
			defaultInt = stream.readInt();
		} else if (opcode == 4) {
			autoDisable = false;
		} else if (opcode == 5) {
			defaultString = stream.readString();
		}
	}
}
