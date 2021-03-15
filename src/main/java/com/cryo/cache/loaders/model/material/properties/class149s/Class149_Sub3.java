package com.cryo.cache.loaders.model.material.properties.class149s;

import com.cryo.cache.loaders.model.material.properties.MaterialProp29;
import com.cryo.utils.JagexArrayUtils;

public class Class149_Sub3 extends Class149 {

	public int anInt9376;
	public int anInt9375;
	public int anInt9377;
	public int anInt9379;

	public Class149_Sub3(int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		super(-1, i_5, i_6);
		anInt9376 = i_1;
		anInt9375 = i_2;
		anInt9377 = i_3;
		anInt9379 = i_4;
	}

	@Override
	public void method2561(int i_1, int i_2) {
		int i_4 = anInt9376 * i_1 >> 12;
		int i_5 = anInt9377 * i_1 >> 12;
		int i_6 = i_2 * anInt9375 >> 12;
		int i_7 = i_2 * anInt9379 >> 12;
		method6159(i_4, i_6, i_5, i_7, anInt1741);
	}

	@Override
	public void method2556(int i_1, int i_2) {
	}

	@Override
	public void method2557(int i_1, int i_2) {
	}

	public static int method4890(int i_0, int i_1, int i_2) {
		return i_0 < i_1 ? i_1 : (Math.min(i_0, i_2));
	}

	public static void method12746(int i_0, int i_1, int i_2, int i_3) {
		if (i_0 >= MaterialProp29.anInt7071 && i_0 <= MaterialProp29.anInt7069) {
			i_1 = method4890(i_1, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
			i_2 = method4890(i_2, MaterialProp29.anInt7070, MaterialProp29.anInt7068);
			method13411(i_0, i_1, i_2, i_3);
		}

	}

	public static void method13411(int i_0, int i_1, int i_2, int i_3) {
		int i_5;
		if (i_1 > i_2) {
			for (i_5 = i_2; i_5 < i_1; i_5++) {
				MaterialProp29.anIntArrayArray7072[i_5][i_0] = i_3;
			}
		} else {
			for (i_5 = i_1; i_5 < i_2; i_5++) {
				MaterialProp29.anIntArrayArray7072[i_5][i_0] = i_3;
			}
		}
	}

	public static void method11250(int i_0, int i_1, int i_2, int i_3) {
		if (i_2 >= MaterialProp29.anInt7070 && i_2 <= MaterialProp29.anInt7068) {
			i_0 = method4890(i_0, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_1 = method4890(i_1, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			method568(i_0, i_1, i_2, i_3);
		}
	}

	public static void method568(int i_0, int i_1, int i_2, int i_3) {
		if (i_0 > i_1) {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_2], i_1, i_0, i_3);
		} else {
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_2], i_0, i_1, i_3);
		}
	}

	public static void method6159(int i_0, int i_1, int i_2, int i_3, int i_4) {
		int i_6 = i_2 - i_0;
		int i_7 = i_3 - i_1;
		if (i_6 == 0) {
			if (i_7 != 0) {
				method12746(i_0, i_1, i_3, i_4);
			}
		} else if (i_7 == 0) {
			method11250(i_0, i_2, i_1, i_4);
		} else {
			int i_8 = (i_7 << 12) / i_6;
			int i_9 = i_1 - (i_0 * i_8 >> 12);
			int i_10;
			int i_11;
			if (i_0 < MaterialProp29.anInt7071) {
				i_10 = (i_8 * MaterialProp29.anInt7071 >> 12) + i_9;
				i_11 = MaterialProp29.anInt7071;
			} else if (i_0 > MaterialProp29.anInt7069) {
				i_10 = (i_8 * MaterialProp29.anInt7069 >> 12) + i_9;
				i_11 = MaterialProp29.anInt7069;
			} else {
				i_10 = i_1;
				i_11 = i_0;
			}
			int i_12;
			int i_13;
			if (i_2 < MaterialProp29.anInt7071) {
				i_12 = (i_8 * MaterialProp29.anInt7071 >> 12) + i_9;
				i_13 = MaterialProp29.anInt7071;
			} else if (i_2 > MaterialProp29.anInt7069) {
				i_12 = (i_8 * MaterialProp29.anInt7069 >> 12) + i_9;
				i_13 = MaterialProp29.anInt7069;
			} else {
				i_12 = i_3;
				i_13 = i_2;
			}
			if (i_10 < MaterialProp29.anInt7070) {
				i_10 = MaterialProp29.anInt7070;
				i_11 = (MaterialProp29.anInt7070 - i_9 << 12) / i_8;
			} else if (i_10 > MaterialProp29.anInt7068) {
				i_10 = MaterialProp29.anInt7068;
				i_11 = (MaterialProp29.anInt7068 - i_9 << 12) / i_8;
			}
			if (i_12 < MaterialProp29.anInt7070) {
				i_12 = MaterialProp29.anInt7070;
				i_13 = (MaterialProp29.anInt7070 - i_9 << 12) / i_8;
			} else if (i_12 > MaterialProp29.anInt7068) {
				i_12 = MaterialProp29.anInt7068;
				i_13 = (MaterialProp29.anInt7068 - i_9 << 12) / i_8;
			}
			method11220(i_11, i_10, i_13, i_12, i_4);
		}
	}

	public static void method11220(int i_0, int i_1, int i_2, int i_3, int i_4) {
		int i_01 = i_0;
		int i_21 = i_2;
		int i_15 = i_1;
		int i_31 = i_3;
		int i_6 = i_31 - i_15;
		int i_7 = i_21 - i_01;
		if (i_7 == 0) {
			if (i_6 != 0) {
				method13411(i_01, i_15, i_31, i_4);
			}
		} else if (i_6 == 0) {
			method568(i_01, i_21, i_15, i_4);
		} else {
			if (i_6 < 0) {
				i_6 = -i_6;
			}
			if (i_7 < 0) {
				i_7 = -i_7;
			}
			boolean bool_8 = i_6 > i_7;
			int i_9;
			int i_10;
			if (bool_8) {
				i_9 = i_01;
				i_10 = i_21;
				i_01 = i_15;
				i_15 = i_9;
				i_21 = i_31;
				i_31 = i_10;
			}
			if (i_01 > i_21) {
				i_9 = i_01;
				i_10 = i_15;
				i_01 = i_21;
				i_21 = i_9;
				i_15 = i_31;
				i_31 = i_10;
			}
			i_9 = i_15;
			i_10 = i_21 - i_01;
			int i_11 = i_31 - i_15;
			int i_12 = -(i_10 >> 1);
			int i_13 = i_15 < i_31 ? 1 : -1;
			if (i_11 < 0) {
				i_11 = -i_11;
			}
			int i_14;
			if (bool_8) {
				for (i_14 = i_01; i_14 <= i_21; i_14++) {
					MaterialProp29.anIntArrayArray7072[i_14][i_9] = i_4;
					i_12 += i_11;
					if (i_12 > 0) {
						i_9 += i_13;
						i_12 -= i_10;
					}
				}
			} else {
				for (i_14 = i_01; i_14 <= i_21; i_14++) {
					MaterialProp29.anIntArrayArray7072[i_9][i_14] = i_4;
					i_12 += i_11;
					if (i_12 > 0) {
						i_9 += i_13;
						i_12 -= i_10;
					}
				}
			}
		}
	}
}
