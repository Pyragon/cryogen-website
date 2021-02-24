package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;
import lombok.Data;

@Data
public abstract class MaterialProperty {

    protected boolean noPalette;
    private int anInt7668;
    private MaterialProperty[] params;

    MaterialProperty(int numParams, boolean bool_2) {
        noPalette = bool_2;
        params = new MaterialProperty[numParams];
    }

	public void init() {
    }

	public void decode(int i_1, InputStream stream) {
    }

    public int getSpriteId() {
        return -1;
    }

    public int getTextureId() {
        return -1;
    }

	public void reset() {
    }

	public static MaterialProperty decode(InputStream stream) {
	    stream.readUnsignedByte();
	    int opIndex = stream.readUnsignedByte();
	    MaterialProperty operation = getById(opIndex);
	    operation.anInt7668 = stream.readUnsignedByte();
	    int size = stream.readUnsignedByte();
	    for (int i = 0; i < size; i++) {
	        int opcode = stream.readUnsignedByte();
	        operation.decode(opcode, stream);
	    }
	    operation.init();
	    return operation;
	}

	private static MaterialProperty getById(int opcode) {
	    switch (opcode) {
	        case 0:
	            return new MaterialProp0();
	        case 1:
	            return new MaterialProp1();
	        case 2:
	            return new MaterialProp2();
	        case 3:
	            return new MaterialProp3();
	        case 4:
	            return new MaterialProp4();
	        case 5:
	            return new MaterialProp5();
	        case 6:
	            return new MaterialProp6();
	        case 7:
	            return new MaterialProp7();
	        case 8:
	            return new MaterialProp8();
	        case 9:
	            return new MaterialProp9();
	        case 10:
	            return new MaterialProp10();
	        case 11:
	            return new MaterialProp11();
	        case 12:
	            return new MaterialProp12();
	        case 13:
	            return new MaterialProp13();
	        case 14:
	            return new MaterialProp14();
	        case 15:
	            return new MaterialProp15();
	        case 16:
	            return new MaterialProp16();
	        case 17:
	            return new MaterialProp17();
	        case 18:
	            return new MaterialPropSpriteSub18();
	        case 19:
	            return new MaterialProp19();
	        case 20:
	            return new MaterialProp20();
	        case 21:
	            return new MaterialProp21();
	        case 22:
	            return new MaterialProp22();
	        case 23:
	            return new MaterialProp23();
	        case 24:
	            return new MaterialProp24();
	        case 25:
	            return new MaterialProp25();
	        case 26:
	            return new MaterialProp26();
	        case 27:
	            return new MaterialProp27();
	        case 28:
	            return new MaterialProp28();
	        case 29:
	            return new MaterialProp29();
	        case 30:
	            return new MaterialProp30();
	        case 31:
	            return new MaterialProp31();
	        case 32:
	            return new MaterialProp32();
	        case 33:
	            return new MaterialProp33();
	        case 34:
	            return new MaterialProp34();
	        case 35:
	            return new MaterialProp35();
	        case 36:
	            return new MaterialPropTexture();
	        case 37:
	            return new MaterialProp37();
	        case 38:
	            return new MaterialProp38();
	        case 39:
	            return new MaterialPropSprite();
	        default:
	            return null;
	    }
	}
}
