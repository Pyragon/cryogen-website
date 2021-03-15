package com.cryo.cache.loaders.model.material.properties;

import com.cryo.cache.io.InputStream;
import lombok.Data;

@Data
public abstract class MaterialProperty {

	protected Class320 aClass320_7667;
	protected Class308 aClass308_7670;
    public boolean noPalette;
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
		if (noPalette) {
			aClass320_7667.clear();
			aClass320_7667 = null;
		} else {
			aClass308_7670.clear();
			aClass308_7670 = null;
		}
    }

	public int[][] method12333(int i_1, int i_2) {
		if (params[i_1].noPalette) {
			int[] ints_4 = params[i_1].method12319(i_2);
			int[][] ints_5 = {ints_4, ints_4, ints_4};
			return ints_5;
		} else {
			return params[i_1].getPixels(i_2);
		}
	}

    public void method12315(int width, int height) {
		int i_4 = anInt7668 == 255 ? height : anInt7668;
		if (noPalette) {
			aClass320_7667 = new Class320(i_4, height, width);
		} else {
			aClass308_7670 = new Class308(i_4, height, width);
		}
	}

	public int[] method12317(int width, int height) {
		return !params[width].noPalette ? params[width].getPixels(height)[0] : params[width].method12319(height);
	}

	public abstract int[] method12319(int i_1);

	public abstract int[][] getPixels(int i_1);

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
