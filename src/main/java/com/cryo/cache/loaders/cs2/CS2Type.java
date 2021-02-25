/*
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.cryo.cache.loaders.cs2;

import com.cryo.utils.cs2.TextUtils;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.cryo.utils.cs2.GenericsUtils.*;

@Data
public class CS2Type {

	private String name;
	private Function<Object, String> format; 
	private int jagexId, jagexChar;
	private boolean jagex, structure, array;
	private CS2Type[] fTypes;
	private String[] fNames;
	private int iss, sss, lss;
	
	public static CS2Type VOID = new CS2Type("void", null, -1, -1, false, false, 0, 0, 0);
	public static CS2Type FUNCTION = new CS2Type("function", null, -1, -1, false, false, 1, 0, 0);
	public static CS2Type UNKNOWN = new CS2Type("??", null, -1, -1, false, false, 0, 0, 0);
	
	
	public static CS2Type INT = createIntJagexType("int", (c) -> isi(c) ? Integer.toString(asi(c)) : null, 0, 105);
	public static CS2Type BOOLEAN = createIntJagexType("boolean", (c) -> isi(c) && (asi(c) == 0 || asi(c) == 1) ? (asi(c) == 1 ? "true" : "false") : null, 1, 49);
	public static CS2Type QUEST = createIntJagexType("quest", 3, 58);
	public static CS2Type QUESTHELP = createIntJagexType("questhelp", 4, 59);
	public static CS2Type CURSOR = createIntJagexType("cursor", 5, 64);
	public static CS2Type SEQ = createIntJagexType("seq", 6, 65);
	public static CS2Type COLOUR = createIntJagexType("colour", (c) -> isi(c) ? ("rgba(" + shrn(asi(c), 16, 0xFF) + ", " + shrn(asi(c), 8, 0xFF) + ", " + shrn(asi(c), 0, 0xFF) + ", " + shrn(asi(c), 24, 0xFF)  + ")") : null, 7, 67);
	public static CS2Type LOC_SHAPE = createIntJagexType("loc_shape", 8, 72);
	public static CS2Type COMPONENT = createIntJagexType("component", 9, 73);
	public static CS2Type IDKIT = createIntJagexType("idkit", 0xA, 75);
	public static CS2Type MIDI = createIntJagexType("midi", 0xB, 77);
	public static CS2Type NPC_MODE = createIntJagexType("npc_mode", 0xC, 78);
	public static CS2Type SYNTH = createIntJagexType("synth", 0xE, 80);
	public static CS2Type AREA = createIntJagexType("area", 0x10, 82);
	public static CS2Type STAT = createIntJagexType("stat", 0x11, 83);
	public static CS2Type NPC_STAT = createIntJagexType("npc_stat", 0x12, 84);
	public static CS2Type WRITEINV = createIntJagexType("writeinv", 0x13, 86);
	public static CS2Type MESH = createIntJagexType("mesh", 0x14, 94);
	public static CS2Type MAPAREA = createIntJagexType("maparea", 0x15, 96);
	public static CS2Type COORDGRID = createIntJagexType("coordgrid", 0x16, 99);
	public static CS2Type GRAPHIC = createIntJagexType("graphic", 0x17, 100);
	public static CS2Type CHATPHRASE = createIntJagexType("chatphrase", 0x18, 101);
	public static CS2Type FONTMETRICS = createIntJagexType("fontmetrics", 0x19, 102);
	public static CS2Type ENUM = createIntJagexType("enum", 0x1A, 103);
	public static CS2Type JINGLE = createIntJagexType("jingle", 0x1C, 106);
	public static CS2Type CHATCAT = createIntJagexType("chatcat", 0x1D, 107);
	public static CS2Type LOC = createIntJagexType("loc", 0x1E, 108);
	public static CS2Type MODEL = createIntJagexType("model", 0x1F, 109);
	public static CS2Type NPC = createIntJagexType("npc", 0x20, 110);
	public static CS2Type OBJ = createIntJagexType("obj", 0x21, 111);
	public static CS2Type PLAYER_UID = createIntJagexType("player_uid", 0x22, 112);
	public static CS2Type STRING = createStringJagexType("string", (c) -> iss(c) ? TextUtils.quote(ass(c)) : null, 0x24, 115);
	public static CS2Type SPOTANIM = createIntJagexType("spotanim", 0x25, 116);
	public static CS2Type NPC_UID = createIntJagexType("npc_uid", 0x26, 117);
	public static CS2Type INV = createIntJagexType("inv", 0x27, 118);
	public static CS2Type TEXTURE = createIntJagexType("texture", 0x28, 120);
	public static CS2Type CATEGORY = createIntJagexType("category", 0x29, 121);
	public static CS2Type CHAR = createIntJagexType("char", (c) -> isi(c) ? TextUtils.quote((char)asi(c).intValue()) : null, 0x2A, 122);
	public static CS2Type LASER = createIntJagexType("laser", 0x2B, 124);
	public static CS2Type BAS = createIntJagexType("bas", 0x2C, -128);
	public static CS2Type COLLISION_GEOMETRY = createIntJagexType("collision_geometry", 0x2E, -121);
	public static CS2Type PHYSICS_MODEL = createIntJagexType("physics_model", 0x2F, -119);
	public static CS2Type PHYSICS_CONTROL_MODIFIER = createIntJagexType("physics_control_modifier", 0x30, -118);
	public static CS2Type CLANHASH = createLongJagexType("clanhash", 0x31, -116);
	public static CS2Type COORDFINE = createCoordJagexType("coordfine", 0x32, -114);
	public static CS2Type CUTSCENE = createIntJagexType("cutscene", 0x33, -102);
	public static CS2Type ITEMCODE = createIntJagexType("itemcode", 0x35, -95);
	public static CS2Type MAPSCENEICON = createIntJagexType("mapsceneicon", 0x37, -93);
	public static CS2Type CLANFORUMQFC = createLongJagexType("clanforumqfc", 0x38, -89);
	public static CS2Type VORBIS = createIntJagexType("vorbis", 0x39, -85);
	public static CS2Type VERIFY_OBJECT = createIntJagexType("verify_object", 0x3A, -82);
	public static CS2Type MAPELEMENT = createIntJagexType("mapelement", 0x3B, -75);
	public static CS2Type CATEGORYTYPE = createIntJagexType("categorytype", 0x3C, -74);
	public static CS2Type SOCIAL_NETWORK = createIntJagexType("social_network", 0x3D, -58);
	public static CS2Type HITMARK = createIntJagexType("hitmark", 0x3E, -41);
	public static CS2Type PACKAGE = createIntJagexType("package", 0x3F, -34);
	public static CS2Type PARTICLE_EFFECTOR = createIntJagexType("particle_effector", 0x40, -31);
	public static CS2Type PARTICLE_EMITTER = createIntJagexType("particle_emitter", 0x42, -23);
	public static CS2Type PLOGTYPE = createIntJagexType("plogtype", 0x43, -19);
	public static CS2Type UNSIGNED_INT = createIntJagexType("unsigned_int", 0x44, -18);
	public static CS2Type SKYBOX = createIntJagexType("skybox", 0x45, -13);
	public static CS2Type SKYDECOR = createIntJagexType("skydecor", 0x46, -6);
	public static CS2Type HASH64 = createLongJagexType("hash64", 71, -5);
	public static CS2Type INPUTTYPE = createIntJagexType("inputtype", 72, -50);
	public static CS2Type STRUCT = createIntJagexType("struct", 73, 74);
	public static CS2Type DBROW = createIntJagexType("dbrow", 74, -48);
	public static CS2Type GWC_PLATFORM = createIntJagexType("gwc_platform", 89, -14);
	public static CS2Type BUG_TEMPLATE = createIntJagexType("bug_template", 94, -22);
	public static CS2Type BILLING_AUTH_FLAG = createIntJagexType("billing_auth_flag", 95, -16);
	public static CS2Type ACCOUNT_FEATURE_FLAG = createIntJagexType("account_feature_flag", 96, -27);
	public static CS2Type INTERFACE = createIntJagexType("interface", (c) -> isi(c) ? ("iface(" + shrn(asi(c), 16, 0xFFFF) + ", " + shrn(asi(c), 0, 0xFFFF)) : null, 97, 97);
	public static CS2Type TOPLEVELINTERFACE = createIntJagexType("toplevelinterface", (c) -> isi(c) ? ("iface_t(" + shrn(asi(c), 16, 0xFFFF) + ", " + shrn(asi(c), 0, 0xFFFF)) : null, 98, 70);
	public static CS2Type OVERLAYINTERFACE = createIntJagexType("overlayinterface", (c) -> isi(c) ? ("iface_o(" + shrn(asi(c), 16, 0xFFFF) + ", " + shrn(asi(c), 0, 0xFFFF)) : null, 0x63, 76);
	public static CS2Type CLIENTINTERFACE = createIntJagexType("clientinterface", (c) -> isi(c) ? ("iface_c(" + shrn(asi(c), 16, 0xFFFF) + ", " + shrn(asi(c), 0, 0xFFFF)) : null, 0x64, -87);
	public static CS2Type MOVESPEED = createIntJagexType("movespeed", 0x65, -35);
	public static CS2Type MATERIAL = createIntJagexType("material", 0x66, -84);
	public static CS2Type SEQGROUP = createIntJagexType("seqgroup", 0x67, -8);
	public static CS2Type TEMP_HISCORE = createIntJagexType("temp_hiscore", 0x68, -28);
	public static CS2Type TEMP_HISCORE_LENGTH_TYPE = createIntJagexType("temp_hiscore_length_type", 0x69, -29);
	public static CS2Type TEMP_HISCORE_DISPLAY_TYPE = createIntJagexType("temp_hiscore_display_type", 0x6A, -30);
	public static CS2Type TEMP_HISCORE_CONTRIBUTE_RESULT = createIntJagexType("temp_hiscore_contribute_result", 0x6B, -32);
	public static CS2Type AUDIOGROUP = createIntJagexType("audiogroup", 0x6C, -64);
	public static CS2Type AUDIOMIXBUSS = createIntJagexType("audiomixbuss", 0x6D, -46);
	public static CS2Type LONG = createLongJagexType("long", (c) -> isl(c) ? (Long.toString(asl(c)) + "L") : null, 0x6E, -49);
	public static CS2Type CRM_CHANNEL = createIntJagexType("crm_channel", 0x6F, -52);
	public static CS2Type HTTP_IMAGE = createIntJagexType("http_image", 0x70, -55);
	public static CS2Type POP_UP_DISPLAY_BEHAVIOUR = createIntJagexType("pop_up_display_behaviour", 0x71, -54);
	public static CS2Type POLL = createIntJagexType("poll", 0x72, -9);
	public static CS2Type POINTLIGHT = createIntJagexType("pointlight", 0x75, -107);
	public static CS2Type PLAYER_GROUP = createLongJagexType("player_group", 0x76, -62);
	public static CS2Type PLAYER_GROUP_STATUS = createIntJagexType("player_group_status", 0x77, -61);
	public static CS2Type PLAYER_GROUP_INVITE_RESULT = createIntJagexType("player_group_invite_result", 0x78, -59);
	public static CS2Type PLAYER_GROUP_MODIFY_RESULT = createIntJagexType("player_group_modify_result", 0x79, -53);
	public static CS2Type PLAYER_GROUP_JOIN_OR_CREATE_RESULT = createIntJagexType("player_group_join_or_create_result", 0x7A, -51);
	public static CS2Type PLAYER_GROUP_AFFINITY_MODIFY_RESULT = createIntJagexType("player_group_affinity_modify_result", 0x7B, -43);
	public static CS2Type PLAYER_GROUP_DELTA_TYPE = createIntJagexType("player_group_delta_type", 0x7C, -78);
	public static CS2Type CLIENT_TYPE = createIntJagexType("client_type", 0x7D, -86);
	public static CS2Type TELEMETRY_INTERVAL = createIntJagexType("telemetry_interval", 0x7E, 63);
	
	private static CS2Type[] DEFAULT = new CS2Type[] { VOID, FUNCTION, UNKNOWN, INT, BOOLEAN, QUEST, QUESTHELP, CURSOR, SEQ, COLOUR, LOC_SHAPE, COMPONENT, IDKIT, MIDI, NPC_MODE, SYNTH, AREA, STAT, NPC_STAT, WRITEINV, MESH, MAPAREA, COORDGRID, GRAPHIC, CHATPHRASE, FONTMETRICS, ENUM, JINGLE, CHATCAT, LOC, MODEL, NPC, OBJ, PLAYER_UID, STRING, SPOTANIM, NPC_UID, INV, TEXTURE, CATEGORY, CHAR, LASER, BAS, COLLISION_GEOMETRY, PHYSICS_MODEL, PHYSICS_CONTROL_MODIFIER, CLANHASH, COORDFINE, CUTSCENE, ITEMCODE, MAPSCENEICON, CLANFORUMQFC, VORBIS, VERIFY_OBJECT, MAPELEMENT, CATEGORYTYPE, SOCIAL_NETWORK, HITMARK, PACKAGE, PARTICLE_EFFECTOR, PARTICLE_EMITTER, PLOGTYPE, UNSIGNED_INT, SKYBOX, SKYDECOR, HASH64, INPUTTYPE, STRUCT, DBROW, GWC_PLATFORM, BUG_TEMPLATE, BILLING_AUTH_FLAG, ACCOUNT_FEATURE_FLAG, INTERFACE, TOPLEVELINTERFACE, OVERLAYINTERFACE, CLIENTINTERFACE, MOVESPEED, MATERIAL, SEQGROUP, TEMP_HISCORE, TEMP_HISCORE_LENGTH_TYPE, TEMP_HISCORE_DISPLAY_TYPE, TEMP_HISCORE_CONTRIBUTE_RESULT, AUDIOGROUP, AUDIOMIXBUSS, LONG, CRM_CHANNEL, HTTP_IMAGE, POP_UP_DISPLAY_BEHAVIOUR, POLL, POINTLIGHT, PLAYER_GROUP, PLAYER_GROUP_STATUS, PLAYER_GROUP_INVITE_RESULT, PLAYER_GROUP_MODIFY_RESULT, PLAYER_GROUP_JOIN_OR_CREATE_RESULT, PLAYER_GROUP_AFFINITY_MODIFY_RESULT, PLAYER_GROUP_DELTA_TYPE, CLIENT_TYPE, TELEMETRY_INTERVAL };
	private static Map<String, CS2Type> CACHE = new HashMap<String, CS2Type>();
	
	static {
		for (int i = 0; i < DEFAULT.length; i++)
			CACHE.put(DEFAULT[i].toString(), DEFAULT[i]);
	}
	
	
	private static CS2Type createIntJagexType(String name, int jagexId, int jagexChar) {
		return new CS2Type(name, null, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar, false, true, 1, 0, 0);
	}
	
	private static CS2Type createIntJagexType(String name, Function<Object, String> format, int jagexId, int jagexChar) {
		return new CS2Type(name, format, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar, false, true, 1, 0, 0);
	}
	
    @SuppressWarnings("unused")
	private static CS2Type createStringJagexType(String name, int jagexId, int jagexChar) {
		return new CS2Type(name, null, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar, false, true, 0, 1, 0);
	}
	
	private static CS2Type createStringJagexType(String name, Function<Object, String> format, int jagexId, int jagexChar) {
		return new CS2Type(name, format, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar, false, true, 0, 1, 0);
	}
	
	private static CS2Type createLongJagexType(String name, int jagexId, int jagexChar) {
		return new CS2Type(name, null, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar, false, true, 0, 0, 1);
	}
	
	private static CS2Type createLongJagexType(String name, Function<Object, String> format, int jagexId, int jagexChar) {
		return new CS2Type(name, format, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar, false, true, 0, 0, 1);
	}
	
	private static CS2Type createCoordJagexType(String name, int jagexId, int jagexChar) {
		return createIntJagexType(name, jagexId, jagexChar < 0 ? (jagexChar & 0xFF) : jagexChar);
	}

	
	private CS2Type(String name, Function<Object, String> format, int jagexId, int jagexChar, boolean array, boolean jagex, int iss, int sss, int lss) {
		this.name = name;
		this.format = format;
		this.jagexId = jagexId;
		this.jagexChar = jagexChar;
		this.structure = false;
		this.array = array;
		this.jagex = jagex;
		this.iss = iss;
		this.sss = sss;
		this.lss = lss;
	}
	
	private CS2Type(String name, Function<Object, String> format, int jagexId, int jagexChar, boolean array, boolean jagex, CS2Type[] fTypes, String[] fNames) {
		this.name = name;
		this.format = format;
		this.jagexId = jagexId;
		this.jagexChar = jagexChar;
		this.structure = true;
		this.array = array;
		this.jagex = jagex;
		this.fTypes = fTypes;
		this.fNames = fNames;
		
		for (int i = 0; i < fTypes.length; i++) {
			this.iss += fTypes[i].intSS();
			this.sss += fTypes[i].stringSS();
			this.lss += fTypes[i].longSS();
		}
	}
	
	

	public String name() {
		return name;
	}
	
	public boolean usable() {
		return this != UNKNOWN;
	}

	public boolean structure() {
		return structure;
	}
	
	public boolean array() {
		return array;
	}
	
	public boolean jagex() {
		return jagex;
	}


	public int intSS() {
		return iss;
	}

	public int stringSS() {
		return sss;
	}

	public int longSS() {
		return lss;
	}
	
	public int totalSS() {
		return iss + sss + lss;
	}
	
	public int fields() {
		return fTypes != null ? fTypes.length : 0;
	}
	
	public CS2Type fieldType(int n) {
		return fTypes[n];
	}
	
	public String fieldName(int n) {
		return fNames[n] != null ? fNames[n] : ("unnamed_" + n);
	}
	
	
	public String desc() {
		return toString();
	}
	
	
	
	
	
	

	
	private int _chash = -1;	
	@Override
	public int hashCode() {
		if (_chash != -1)
			return _chash;
		return _chash = toString().hashCode();
	}
	
	private CS2Type _carray;
	public CS2Type getArrayType() {
		if (_carray != null)
			return _carray;
		
		if (array)
			throw new RuntimeException("Multidimensional arrays not supported.");
		return _carray = new CS2Type(this.name, null, jagexId, jagexChar, true, jagex, this.iss, this.sss, this.lss);
	}
	
	private CS2Type _element;
	public CS2Type getElementType() {
		if (_element != null)
			return _element;
		
		if (!array)
			throw new RuntimeException("getElementType() can't be called on nonarray type.");
		String desc = this.toString();
		return _element = forDesc(desc.substring(0,desc.length() - 2)); // remove []
	}
	
	public boolean equals(CS2Type other) {
		/*return name.equals(other.name) && 
			intStackSize == other.intStackSize && 
			stringStackSize == other.stringStackSize && 
			longStackSize == other.longStackSize && 
			structure == other.structure &&
			array == other.array &&
			jagex == other.jagex &&
			jagexId == other.jagexId &&
			jagexChar == other.jagexChar;*/
		return this == other;
	}

    public String toString2() {
        boolean structure = this.structure;
        this.structure = false;
        String s = toString();
        this.structure = structure;
        return s;
    }
	
	private String _string1;
	@Override
	public String toString() {
		if (_string1 != null)
			return _string1;
		
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		if (structure) {
			builder.append('(');
			for (int i = 0; i < fields(); i++) {
				if (i > 0)
					builder.append(';');
				builder.append(fTypes[i].toString());
				if (fNames[i] == null)
					continue;
				
				builder.append('$');
				builder.append(fNames[i].toString());
			}
			builder.append(')');
		}
		
		if (array)
			builder.append("[]");
		
		return _string1 = builder.toString();
	}
	
	public static CS2Type forDesc(String desc) {
		CS2Type cached = CACHE.get(desc);
		if (cached != null)
			return cached;
		
		String odesc = desc;
		if (!desc.contains("("))
			throw new RuntimeException("invalid type " + desc);	

		boolean isArray = false;
		if (desc.endsWith("[]")) {
			desc.substring(0, desc.length() - 2);
			isArray = true;	
		}
		
		String[] spl = desc.split("\\(");
		String name = spl[0];
			
		CS2Type type;
		if (!desc.contains(";")) {
			// old deprecated way
			String stackDesc = spl[1].substring(0, spl[1].length() - 1);
			String[] stackSpl = stackDesc.split("\\,");
			
			int iss = Integer.parseInt(stackSpl[0]);
			int sss = Integer.parseInt(stackSpl[1]);
			int lss = Integer.parseInt(stackSpl[2]);
			
			type = makeBasicStruct(name, isArray, iss, sss, lss);
		}
		else {
			String[] descriptors = new String[100];
			int dcount = 0;
			
			int start = desc.indexOf('(') + 1;
			int in = 1;
			for (int i = start; i < desc.length(); i++) {
				if (desc.charAt(i) == '(')
					in++;
				else if (desc.charAt(i) == ')') {
					in--;
					if (in < 0 || (in == 0 && (i + 1) < desc.length()))
						throw new RuntimeException("invalid type" + odesc);
					
					if (in == 0) {
						String descriptor = desc.substring(start, i);
						if (descriptor.length() > 0) {
							if (dcount++ >= descriptors.length)
								descriptors = Arrays.copyOf(descriptors, descriptors.length * 2);
							descriptors[dcount - 1] = desc.substring(start, i);
							start = i + 1;
						}
					}
				}
				
				if (in == 1 && desc.charAt(i) == ';') {
					String descriptor = desc.substring(start, i);
					if (descriptor.length() > 0) {
						if (dcount++ >= descriptors.length)
							descriptors = Arrays.copyOf(descriptors, descriptors.length * 2);
						descriptors[dcount - 1] = desc.substring(start, i);
						start = i + 1;
					}
				}
			}
			
			if (in != 0)
				throw new RuntimeException("invalid type" + odesc);
			
			CS2Type[] types = new CS2Type[dcount];
			String[] names = new String[dcount];
			
			
			for (int i = 0; i < dcount; i++) {
				String fdesc = descriptors[i];
				int last = -1;
				for (int idx = fdesc.indexOf('$'); idx != -1; idx = fdesc.indexOf('$', last+1))
					last = idx;
				
				String _desc;
				String _name;
				if (last != -1) {
					_desc = fdesc.substring(0, last);
					_name = fdesc.substring(last + 1);
				}
				else {
					_desc = fdesc;
					_name = null;
				}
				
				
				types[i] = CS2Type.forDesc(_desc);
				names[i] = _name;
			}
			
			type = new CS2Type(name, null, -1, -1, isArray, false, types, names);
		}
		
		if (type.totalSS() == 0)
			throw new RuntimeException("void redefinition");
		
		cached = CACHE.get(type.toString());
		if (cached != null) {
			CACHE.put(odesc, cached);
			return cached;
		}
		
		CACHE.put(odesc, type);
		return type;
	}
	
	public static CS2Type forJagexId(int id) {
		for (int i = 0; i < DEFAULT.length; i++)
			if (DEFAULT[i].jagex() && DEFAULT[i].jagexId == id)
				return DEFAULT[i];
		
		return CS2Type.INT;
	}
	
	public static CS2Type forJagexChar(int _char) {
		for (int i = 0; i < DEFAULT.length; i++)
			if (DEFAULT[i].jagex() && DEFAULT[i].jagexChar == _char)
				return DEFAULT[i];
		
		return CS2Type.INT;
	}
	
	
	public static CS2Type makeBasicStruct(String name, boolean array, int iss, int sss, int lss) {
		CS2Type[] types = new CS2Type[iss+sss+lss];

		for (int i = 0; i < iss; i++)
			types[i] = CS2Type.INT;
		for (int i = 0; i < sss; i++)
			types[i + iss] = CS2Type.STRING;
		for (int i = 0; i < lss; i++)
			types[i + iss + sss] = CS2Type.LONG;
		
		return makeAdvancedStruct(name, array, types);
	}
	
	public static CS2Type makeAdvancedStruct(String name, boolean array, CS2Type[] types) {
		return makeAdvancedStruct(name, array, types, new String[types.length]);
	}
	
	public static CS2Type makeAdvancedStruct(String name, boolean array, CS2Type[] types, String[] names) {
		CS2Type type = new CS2Type(name, null, -1, -1, array, false, types, names);
		if (type.totalSS() == 0)
			throw new RuntimeException("void redefinition");
		
		CS2Type cached = CACHE.get(type.toString());
		if (cached != null)
			return cached;
		
		CACHE.put(type.toString(), type);
		return type;
	}
	
	
	public static CS2Type merge(CS2Type t1, CS2Type t2) {
		if (t1 == t2)
			return t1;
		else if (t1 == CS2Type.UNKNOWN)
			return t2;
		else if (t2 == CS2Type.UNKNOWN)
			return t1;
		
		if (t1.iss != t2.iss || t1.sss != t2.sss || t1.lss != t2.lss || t1.array != t2.array)
			throw new RuntimeException("can't merge: diff stack sizes/types, "+(t1.iss-t2.iss)+", "+(t1.sss-t2.sss)+", "+(t1.lss-t2.lss)+", "+(t1.array==t2.array));
		else if (t1.array)
			return merge(t1.getElementType(), t2.getElementType()).getArrayType();
		else if (!t1.structure || !t2.structure)
			return t1.lss == 1 ? CS2Type.LONG : (t1.sss == 1 ? CS2Type.STRING : CS2Type.INT);
		
		if (t1.fTypes.length != t2.fTypes.length)
			throw new RuntimeException("TODO can't merge " + t1 + " with " + t2);
		
		String name = t1.name;
		if (!t1.name.equals(t2.name))
			name += "|" + t2.name;
		
		CS2Type[] types = new CS2Type[t1.fTypes.length];
		String[] names = new String[t1.fNames.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = merge(t1.fTypes[i], t2.fTypes[i]);
	
			if (Objects.equals(t1.fNames[i], t2.fNames[i])) {
				names[i] = t1.fNames[i];
				continue; 
			}
			
			names[i] = (t1.fNames[i] != null ? t1.fNames[i] : "unnamed") + "|" + (t2.fNames[i] != null ? t2.fNames[i] : "unnamed");
		}
		
		return makeAdvancedStruct(name, t1.array, types, names);
	}



}
