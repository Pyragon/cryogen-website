package com.cryo.cache.loaders.model.material.properties.class149s;

import com.cryo.cache.loaders.model.material.properties.MaterialProp29;
import com.cryo.utils.JagexArrayUtils;

public class Class149_Sub4 extends Class149 {

	public int anInt9391;

	public int anInt9393;

	public int anInt9392;

	public int anInt9394;

	public Class149_Sub4(int i_1, int i_2, int i_3, int i_4, int i_5, int i_6, int i_7) {
		super(i_5, i_6, i_7);
		anInt9391 = i_1;
		anInt9393 = i_2;
		anInt9392 = i_3;
		anInt9394 = i_4;
	}

	public static void method14572(int i_0, int i_1, int i_2, int i_3, int i_4) {
		JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_2++], i_0, i_1, i_4);
		JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_3--], i_0, i_1, i_4);

		for (int i_6 = i_2; i_6 <= i_3; i_6++) {
			int[] ints_7 = MaterialProp29.anIntArrayArray7072[i_6];
			ints_7[i_0] = ints_7[i_1] = i_4;
		}

	}

	public static void method4561(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5) {
		int i_7 = i_5 + i_2;
		int i_8 = i_3 - i_5;
		int i_9;
		for (i_9 = i_2; i_9 < i_7; i_9++) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_9], i_0, i_1, i_4);
		}
		for (i_9 = i_3; i_9 > i_8; --i_9) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_9], i_0, i_1, i_4);
		}
		i_9 = i_5 + i_0;
		int i_10 = i_1 - i_5;
		for (int i_11 = i_7; i_11 <= i_8; i_11++) {
			int[] ints_12 = MaterialProp29.anIntArrayArray7072[i_11];
			JagexArrayUtils.method3922(ints_12, i_0, i_9, i_4);
			JagexArrayUtils.method3922(ints_12, i_10, i_1, i_4);
		}
	}

	public static void method1388(int i_0, int i_1, int i_2, int i_3, int i_4) {
		int i_01 = i_0;
		int i_11 = i_1;
		int i_21 = i_2;
		int i_31 = i_3;
		if (i_21 <= MaterialProp29.anInt7068 && i_31 >= MaterialProp29.anInt7070) {
			boolean bool_6;
			if (i_01 < MaterialProp29.anInt7071) {
				i_01 = MaterialProp29.anInt7071;
				bool_6 = false;
			} else if (i_01 > MaterialProp29.anInt7069) {
				i_01 = MaterialProp29.anInt7069;
				bool_6 = false;
			} else {
				bool_6 = true;
			}
			boolean bool_7;
			if (i_11 < MaterialProp29.anInt7071) {
				i_11 = MaterialProp29.anInt7071;
				bool_7 = false;
			} else if (i_11 > MaterialProp29.anInt7069) {
				i_11 = MaterialProp29.anInt7069;
				bool_7 = false;
			} else {
				bool_7 = true;
			}
			if (i_21 >= MaterialProp29.anInt7070) {
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_21++], i_01, i_11, i_4);
			} else {
				i_21 = MaterialProp29.anInt7070;
			}
			if (i_31 <= MaterialProp29.anInt7068) {
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_31--], i_01, i_11, i_4);
			} else {
				i_31 = MaterialProp29.anInt7068;
			}
			int i_8;
			if (bool_6 && bool_7) {
				for (i_8 = i_21; i_8 <= i_31; i_8++) {
					int[] ints_9 = MaterialProp29.anIntArrayArray7072[i_8];
					ints_9[i_01] = ints_9[i_11] = i_4;
				}
			} else if (bool_6) {
				for (i_8 = i_21; i_8 <= i_31; i_8++) {
					MaterialProp29.anIntArrayArray7072[i_8][i_01] = i_4;
				}
			} else if (bool_7) {
				for (i_8 = i_21; i_8 <= i_31; i_8++) {
					MaterialProp29.anIntArrayArray7072[i_8][i_11] = i_4;
				}
			}
		}
	}

	public static void method744(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5) {
		int i_7 = Class149_Sub3.method4890(i_2, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_8 = Class149_Sub3.method4890(i_3, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_9 = Class149_Sub3.method4890(i_0, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_10 = Class149_Sub3.method4890(i_1, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_11 = Class149_Sub3.method4890(i_5 + i_2, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_12 = Class149_Sub3.method4890(i_3 - i_5, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_13;
		for (i_13 = i_7; i_13 < i_11; i_13++) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_13], i_9, i_10, i_4);
		}
		for (i_13 = i_8; i_13 > i_12; --i_13) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_13], i_9, i_10, i_4);
		}
		i_13 = Class149_Sub3.method4890(i_5 + i_0, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_14 = Class149_Sub3.method4890(i_1 - i_5, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		for (int i_15 = i_11; i_15 <= i_12; i_15++) {
			int[] ints_16 = MaterialProp29.anIntArrayArray7072[i_15];
			JagexArrayUtils.method3922(ints_16, i_9, i_13, i_4);
			JagexArrayUtils.method3922(ints_16, i_14, i_10, i_4);
		}
	}

	public static void method1805(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5) {
		if (i_0 >= MaterialProp29.anInt7071 && i_1 <= MaterialProp29.anInt7069 && i_2 >= MaterialProp29.anInt7070 && i_3 <= MaterialProp29.anInt7068) {
			if (i_5 == 1) {
				method14572(i_0, i_1, i_2, i_3, i_4);
			} else {
				method4561(i_0, i_1, i_2, i_3, i_4, i_5);
			}
		} else if (i_5 == 1) {
			method1388(i_0, i_1, i_2, i_3, i_4);
		} else {
			method744(i_0, i_1, i_2, i_3, i_4, i_5);
		}
	}

	public static void method1564(int i_0, int i_1, int i_2, int i_3, int i_4) {
		for (int i_6 = i_2; i_6 <= i_3; i_6++) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_6], i_0, i_1, i_4);
		}
	}

	public static void method7728(int i_0, int i_1, int i_2, int i_3, int i_4) {
		int i_6 = Class149_Sub3.method4890(i_2, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_7 = Class149_Sub3.method4890(i_3, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_8 = Class149_Sub3.method4890(i_0, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_9 = Class149_Sub3.method4890(i_1, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		for (int i_10 = i_6; i_10 <= i_7; i_10++) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_10], i_8, i_9, i_4);
		}
	}

	public static void method6772(int i_0, int i_1, int i_2, int i_3, int i_4) {
		if (i_0 >= MaterialProp29.anInt7071 && i_1 <= MaterialProp29.anInt7069 && i_2 >= MaterialProp29.anInt7070 && i_3 <= MaterialProp29.anInt7068) {
			method1564(i_0, i_1, i_2, i_3, i_4);
		} else {
			method7728(i_0, i_1, i_2, i_3, i_4);
		}

	}

	public static void method3230(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		int i_8 = i_2 + i_6;
		int i_9 = i_3 - i_6;
		int i_10;
		for (i_10 = i_2; i_10 < i_8; i_10++) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_10], i_0, i_1, i_5);
		}
		for (i_10 = i_3; i_10 > i_9; --i_10) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_10], i_0, i_1, i_5);
		}
		i_10 = i_0 + i_6;
		int i_11 = i_1 - i_6;
		for (int i_12 = i_8; i_12 <= i_9; i_12++) {
			int[] ints_13 = MaterialProp29.anIntArrayArray7072[i_12];
			JagexArrayUtils.method3922(ints_13, i_0, i_10, i_5);
			JagexArrayUtils.method3922(ints_13, i_10, i_11, i_4);
			JagexArrayUtils.method3922(ints_13, i_11, i_1, i_5);
		}
	}

	public static void method4034(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		int i_8 = Class149_Sub3.method4890(i_2, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_9 = Class149_Sub3.method4890(i_3, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_10 = Class149_Sub3.method4890(i_0, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_11 = Class149_Sub3.method4890(i_1, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_12 = Class149_Sub3.method4890(i_2 + i_6, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_13 = Class149_Sub3.method4890(i_3 - i_6, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
		int i_14;
		for (i_14 = i_8; i_14 < i_12; i_14++) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_14], i_10, i_11, i_5);
		}
		for (i_14 = i_9; i_14 > i_13; --i_14) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_14], i_10, i_11, i_5);
		}
		i_14 = Class149_Sub3.method4890(i_0 + i_6, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_15 = Class149_Sub3.method4890(i_1 - i_6, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		for (int i_16 = i_12; i_16 <= i_13; i_16++) {
			int[] ints_17 = MaterialProp29.anIntArrayArray7072[i_16];
			JagexArrayUtils.method3922(ints_17, i_10, i_14, i_5);
			JagexArrayUtils.method3922(ints_17, i_14, i_15, i_4);
			JagexArrayUtils.method3922(ints_17, i_15, i_11, i_5);
		}
	}

	public static void method6731(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		if (i_0 >= MaterialProp29.anInt7071 && i_1 <= MaterialProp29.anInt7069 && i_2 >= MaterialProp29.anInt7070 && i_3 <= MaterialProp29.anInt7068) {
			method3230(i_0, i_1, i_2, i_3, i_4, i_5, i_6);
		} else {
			method4034(i_0, i_1, i_2, i_3, i_4, i_5, i_6);
		}
	}

	@Override
	public void method2561(int i_1, int i_2) {
		int i_4 = anInt9391 * i_1 >> 12;
		int i_5 = anInt9392 * i_1 >> 12;
		int i_6 = i_2 * anInt9393 >> 12;
		int i_7 = i_2 * anInt9394 >> 12;
		method1805(i_4, i_5, i_6, i_7, anInt1741, anInt1742);
	}

	@Override
	public void method2556(int i_1, int i_2) {
		int i_4 = anInt9391 * i_1 >> 12;
		int i_5 = anInt9392 * i_1 >> 12;
		int i_6 = i_2 * anInt9393 >> 12;
		int i_7 = i_2 * anInt9394 >> 12;
		method6772(i_4, i_5, i_6, i_7, anInt1743);
	}

	@Override
	public void method2557(int i_1, int i_2) {
		int i_4 = anInt9391 * i_1 >> 12;
		int i_5 = anInt9392 * i_1 >> 12;
		int i_6 = i_2 * anInt9393 >> 12;
		int i_7 = i_2 * anInt9394 >> 12;
		method6731(i_4, i_5, i_6, i_7, anInt1743, anInt1741, anInt1742);
	}
}
