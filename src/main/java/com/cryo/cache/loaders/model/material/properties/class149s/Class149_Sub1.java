package com.cryo.cache.loaders.model.material.properties.class149s;

import com.cryo.cache.loaders.model.material.properties.MaterialProp29;
import com.cryo.utils.JagexArrayUtils;

public class Class149_Sub1 extends Class149 {
	
	public static int[] anIntArray36;

	public int anInt9274;
	public int anInt9273;
	public int anInt9276;
	public int anInt9275;

	public Class149_Sub1(int i_1, int i_2, int i_3, int i_4, int i_5, int i_6, int i_7) {
		super(i_5, i_6, i_7);
		anInt9274 = i_1;
		anInt9273 = i_2;
		anInt9276 = i_3;
		anInt9275 = i_4;
	}

	@Override
	public void method2561(int i_1, int i_2) {
	}

	public static void method813(int i_0, int i_1, int i_2, int i_3) {
		int i_5 = 0;
		int i_6 = i_2;
		int i_7 = -i_2;
		int i_8 = -1;
		JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_1], i_0 - i_2, i_0 + i_2, i_3);
		while (i_6 > i_5) {
			i_8 += 2;
			i_7 += i_8;
			++i_5;
			if (i_7 >= 0) {
				--i_6;
				i_7 -= i_6 << 1;
				int[] ints_9 = MaterialProp29.anIntArrayArray7072[i_6 + i_1];
				int[] ints_10 = MaterialProp29.anIntArrayArray7072[i_1 - i_6];
				int i_11 = i_0 + i_5;
				int i_12 = i_0 - i_5;
				JagexArrayUtils.method3922(ints_9, i_12, i_11, i_3);
				JagexArrayUtils.method3922(ints_10, i_12, i_11, i_3);
			}
			int i_13 = i_0 + i_6;
			int i_14 = i_0 - i_6;
			int[] ints_15 = MaterialProp29.anIntArrayArray7072[i_5 + i_1];
			int[] ints_16 = MaterialProp29.anIntArrayArray7072[i_1 - i_5];
			JagexArrayUtils.method3922(ints_15, i_14, i_13, i_3);
			JagexArrayUtils.method3922(ints_16, i_14, i_13, i_3);
		}
	}

	public static void method4866(int i_0, int i_1, int i_2, int i_3) {
		int i_5 = 0;
		int i_6 = i_2;
		int i_7 = -i_2;
		int i_8 = -1;
		int i_9 = Class149_Sub3.method4890(i_0 + i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		int i_10 = Class149_Sub3.method4890(i_0 - i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
		JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_1], i_10, i_9, i_3);
		while (i_6 > i_5) {
			i_8 += 2;
			i_7 += i_8;
			int i_11;
			int i_12;
			int i_13;
			int i_14;
			if (i_7 > 0) {
				--i_6;
				i_7 -= i_6 << 1;
				i_11 = i_1 - i_6;
				i_12 = i_6 + i_1;
				if (i_12 >= MaterialProp29.anInt7070 && i_11 <= MaterialProp29.anInt7068) {
					i_13 = Class149_Sub3.method4890(i_0 + i_5, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
					i_14 = Class149_Sub3.method4890(i_0 - i_5, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
					if (i_12 <= MaterialProp29.anInt7068) {
						JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_12], i_14, i_13, i_3);
					}
					if (i_11 >= MaterialProp29.anInt7070) {
						JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_11], i_14, i_13, i_3);
					}
				}
			}
			++i_5;
			i_11 = i_1 - i_5;
			i_12 = i_5 + i_1;
			if (i_12 >= MaterialProp29.anInt7070 && i_11 <= MaterialProp29.anInt7068) {
				i_13 = Class149_Sub3.method4890(i_0 + i_6, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
				i_14 = Class149_Sub3.method4890(i_0 - i_6, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
				if (i_12 <= MaterialProp29.anInt7068) {
					JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_12], i_14, i_13, i_3);
				}
				if (i_11 >= MaterialProp29.anInt7070) {
					JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_11], i_14, i_13, i_3);
				}
			}
		}
	}

	public static void method6292(int i_0, int i_1, int i_2, int i_3) {
		if (i_0 - i_2 >= MaterialProp29.anInt7071 && i_0 + i_2 <= MaterialProp29.anInt7069 && i_1 - i_2 >= MaterialProp29.anInt7070 && i_2 + i_1 <= MaterialProp29.anInt7068) {
			method813(i_0, i_1, i_2, i_3);
		} else {
			method4866(i_0, i_1, i_2, i_3);
		}
	}

	public static void method3751(int i_0, int i_1, int i_2, int i_3, int i_4) {
		int i_6 = 0;
		int i_7 = i_3;
		int i_8 = i_2 * i_2;
		int i_9 = i_3 * i_3;
		int i_10 = i_9 << 1;
		int i_11 = i_8 << 1;
		int i_12 = i_3 << 1;
		int i_13 = i_10 + (1 - i_12) * i_8;
		int i_14 = i_9 - i_11 * (i_12 - 1);
		int i_15 = i_8 << 2;
		int i_16 = i_9 << 2;
		int i_17 = ((i_6 << 1) + 3) * i_10;
		int i_18 = ((i_3 << 1) - 3) * i_11;
		int i_19 = i_16 * (i_6 + 1);
		int i_20 = i_15 * (i_3 - 1);
		JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_1], i_0 - i_2, i_0 + i_2, i_4);
		while (i_7 > 0) {
			if (i_13 < 0) {
				while (i_13 < 0) {
					i_13 += i_17;
					i_14 += i_19;
					i_17 += i_16;
					i_19 += i_16;
					++i_6;
				}
			}
			if (i_14 < 0) {
				i_13 += i_17;
				i_14 += i_19;
				i_17 += i_16;
				i_19 += i_16;
				++i_6;
			}
			i_13 += -i_20;
			i_14 += -i_18;
			i_18 -= i_15;
			i_20 -= i_15;
			--i_7;
			int i_21 = i_1 - i_7;
			int i_22 = i_7 + i_1;
			int i_23 = i_0 + i_6;
			int i_24 = i_0 - i_6;
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_21], i_24, i_23, i_4);
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_22], i_24, i_23, i_4);
		}
	}

	public static void method15405(int i_0, int i_1, int i_2, int i_3, int i_4) {
		int i_6 = 0;
		int i_7 = i_3;
		int i_8 = i_2 * i_2;
		int i_9 = i_3 * i_3;
		int i_10 = i_9 << 1;
		int i_11 = i_8 << 1;
		int i_12 = i_3 << 1;
		int i_13 = i_10 + (1 - i_12) * i_8;
		int i_14 = i_9 - i_11 * (i_12 - 1);
		int i_15 = i_8 << 2;
		int i_16 = i_9 << 2;
		int i_17 = ((i_6 << 1) + 3) * i_10;
		int i_18 = ((i_3 << 1) - 3) * i_11;
		int i_19 = i_16 * (i_6 + 1);
		int i_20 = i_15 * (i_3 - 1);
		int i_21;
		int i_22;
		if (i_1 >= MaterialProp29.anInt7070 && i_1 <= MaterialProp29.anInt7068) {
			i_21 = Class149_Sub3.method4890(i_0 + i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_22 = Class149_Sub3.method4890(i_0 - i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_1], i_22, i_21, i_4);
		}
		while (i_7 > 0) {
			if (i_13 < 0) {
				while (i_13 < 0) {
					i_13 += i_17;
					i_14 += i_19;
					i_17 += i_16;
					i_19 += i_16;
					++i_6;
				}
			}
			if (i_14 < 0) {
				i_13 += i_17;
				i_14 += i_19;
				i_17 += i_16;
				i_19 += i_16;
				++i_6;
			}
			i_13 += -i_20;
			i_14 += -i_18;
			i_18 -= i_15;
			i_20 -= i_15;
			--i_7;
			i_21 = i_1 - i_7;
			i_22 = i_7 + i_1;
			if (i_22 >= MaterialProp29.anInt7070 && i_21 <= MaterialProp29.anInt7068) {
				int i_23 = Class149_Sub3.method4890(i_0 + i_6, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
				int i_24 = Class149_Sub3.method4890(i_0 - i_6, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
				if (i_21 >= MaterialProp29.anInt7070) {
					JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_21], i_24, i_23, i_4);
				}
				if (i_22 <= MaterialProp29.anInt7068) {
					JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_22], i_24, i_23, i_4);
				}
			}
		}
	}

	public static void method14584(int i_0, int i_1, int i_2, int i_3, int i_4) {
		if (i_3 == i_2) {
			method6292(i_0, i_1, i_2, i_4);
		} else if (i_0 - i_2 >= MaterialProp29.anInt7071 && i_0 + i_2 <= MaterialProp29.anInt7069 && i_1 - i_3 >= MaterialProp29.anInt7070 && i_3 + i_1 <= MaterialProp29.anInt7068) {
			method3751(i_0, i_1, i_2, i_3, i_4);
		} else {
			method15405(i_0, i_1, i_2, i_3, i_4);
		}

	}

	public static void method7170(int i_0) {
		if (anIntArray36 == null || anIntArray36.length < i_0) {
			anIntArray36 = new int[i_0];
		}

	}

	public static void method2637(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5) {
		method7170(i_2);
		int i_7 = 0;
		int i_8 = i_2 - i_5;
		if (i_8 < 0) {
			i_8 = 0;
		}
		int i_9 = i_2;
		int i_10 = -i_2;
		int i_11 = i_8;
		int i_12 = -i_8;
		int i_13 = -1;
		int i_14 = -1;
		int[] ints_15 = MaterialProp29.anIntArrayArray7072[i_1];
		int i_16 = i_0 - i_8;
		int i_17 = i_0 + i_8;
		JagexArrayUtils.method3922(ints_15, i_0 - i_2, i_16, i_4);
		JagexArrayUtils.method3922(ints_15, i_16, i_17, i_3);
		JagexArrayUtils.method3922(ints_15, i_17, i_0 + i_2, i_4);
		while (i_9 > i_7) {
			i_13 += 2;
			i_14 += 2;
			i_10 += i_13;
			i_12 += i_14;
			if (i_12 >= 0 && i_11 >= 1) {
				anIntArray36[i_11] = i_7;
				--i_11;
				i_12 -= i_11 << 1;
			}
			++i_7;
			int[] ints_18;
			int[] ints_19;
			int i_20;
			int i_21;
			int i_22;
			int i_23;
			int i_24;
			if (i_10 >= 0) {
				--i_9;
				i_10 -= i_9 << 1;
				if (i_9 >= i_8) {
					ints_18 = MaterialProp29.anIntArrayArray7072[i_9 + i_1];
					ints_19 = MaterialProp29.anIntArrayArray7072[i_1 - i_9];
					i_20 = i_0 + i_7;
					i_21 = i_0 - i_7;
					JagexArrayUtils.method3922(ints_18, i_21, i_20, i_4);
					JagexArrayUtils.method3922(ints_19, i_21, i_20, i_4);
				} else {
					ints_18 = MaterialProp29.anIntArrayArray7072[i_9 + i_1];
					ints_19 = MaterialProp29.anIntArrayArray7072[i_1 - i_9];
					i_20 = anIntArray36[i_9];
					i_21 = i_0 + i_7;
					i_22 = i_0 - i_7;
					i_23 = i_0 + i_20;
					i_24 = i_0 - i_20;
					JagexArrayUtils.method3922(ints_18, i_22, i_24, i_4);
					JagexArrayUtils.method3922(ints_18, i_24, i_23, i_3);
					JagexArrayUtils.method3922(ints_18, i_23, i_21, i_4);
					JagexArrayUtils.method3922(ints_19, i_22, i_24, i_4);
					JagexArrayUtils.method3922(ints_19, i_24, i_23, i_3);
					JagexArrayUtils.method3922(ints_19, i_23, i_21, i_4);
				}
			}
			ints_18 = MaterialProp29.anIntArrayArray7072[i_7 + i_1];
			ints_19 = MaterialProp29.anIntArrayArray7072[i_1 - i_7];
			i_20 = i_0 + i_9;
			i_21 = i_0 - i_9;
			if (i_7 < i_8) {
				i_22 = i_11 < i_7 ? anIntArray36[i_7] : i_11;
				i_23 = i_0 + i_22;
				i_24 = i_0 - i_22;
				JagexArrayUtils.method3922(ints_18, i_21, i_24, i_4);
				JagexArrayUtils.method3922(ints_18, i_24, i_23, i_3);
				JagexArrayUtils.method3922(ints_18, i_23, i_20, i_4);
				JagexArrayUtils.method3922(ints_19, i_21, i_24, i_4);
				JagexArrayUtils.method3922(ints_19, i_24, i_23, i_3);
				JagexArrayUtils.method3922(ints_19, i_23, i_20, i_4);
			} else {
				JagexArrayUtils.method3922(ints_18, i_21, i_20, i_4);
				JagexArrayUtils.method3922(ints_19, i_21, i_20, i_4);
			}
		}
	}

	public static void method1174(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5) {
		method7170(i_2);
		int i_7 = 0;
		int i_8 = i_2 - i_5;
		if (i_8 < 0) {
			i_8 = 0;
		}
		int i_9 = i_2;
		int i_10 = -i_2;
		int i_11 = i_8;
		int i_12 = -i_8;
		int i_13 = -1;
		int i_14 = -1;
		int i_16;
		int i_17;
		int i_18;
		int i_19;
		if (i_1 >= MaterialProp29.anInt7070 && i_1 <= MaterialProp29.anInt7068) {
			int[] ints_15 = MaterialProp29.anIntArrayArray7072[i_1];
			i_16 = Class149_Sub3.method4890(i_0 - i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_17 = Class149_Sub3.method4890(i_0 + i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_18 = Class149_Sub3.method4890(i_0 - i_8, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_19 = Class149_Sub3.method4890(i_0 + i_8, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			JagexArrayUtils.method3922(ints_15, i_16, i_18, i_4);
			JagexArrayUtils.method3922(ints_15, i_18, i_19, i_3);
			JagexArrayUtils.method3922(ints_15, i_19, i_17, i_4);
		}
		while (i_9 > i_7) {
			i_13 += 2;
			i_14 += 2;
			i_10 += i_13;
			i_12 += i_14;
			if (i_12 >= 0 && i_11 >= 1) {
				--i_11;
				i_12 -= i_11 << 1;
				anIntArray36[i_11] = i_7;
			}
			++i_7;
			int i_20;
			int i_21;
			int[] ints_22;
			int i_23;
			if (i_10 >= 0) {
				--i_9;
				i_10 -= i_9 << 1;
				i_23 = i_1 - i_9;
				i_16 = i_9 + i_1;
				if (i_16 >= MaterialProp29.anInt7070 && i_23 <= MaterialProp29.anInt7068) {
					if (i_9 >= i_8) {
						i_17 = Class149_Sub3.method4890(i_0 + i_7, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						i_18 = Class149_Sub3.method4890(i_0 - i_7, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						if (i_16 <= MaterialProp29.anInt7068) {
							JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_16], i_18, i_17, i_4);
						}
						if (i_23 >= MaterialProp29.anInt7070) {
							JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_23], i_18, i_17, i_4);
						}
					} else {
						i_17 = anIntArray36[i_9];
						i_18 = Class149_Sub3.method4890(i_0 + i_7, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						i_19 = Class149_Sub3.method4890(i_0 - i_7, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						i_20 = Class149_Sub3.method4890(i_0 + i_17, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						i_21 = Class149_Sub3.method4890(i_0 - i_17, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						if (i_16 <= MaterialProp29.anInt7068) {
							ints_22 = MaterialProp29.anIntArrayArray7072[i_16];
							JagexArrayUtils.method3922(ints_22, i_19, i_21, i_4);
							JagexArrayUtils.method3922(ints_22, i_21, i_20, i_3);
							JagexArrayUtils.method3922(ints_22, i_20, i_18, i_4);
						}
						if (i_23 >= MaterialProp29.anInt7070) {
							ints_22 = MaterialProp29.anIntArrayArray7072[i_23];
							JagexArrayUtils.method3922(ints_22, i_19, i_21, i_4);
							JagexArrayUtils.method3922(ints_22, i_21, i_20, i_3);
							JagexArrayUtils.method3922(ints_22, i_20, i_18, i_4);
						}
					}
				}
			}
			i_23 = i_1 - i_7;
			i_16 = i_7 + i_1;
			if (i_16 >= MaterialProp29.anInt7070 && i_23 <= MaterialProp29.anInt7068) {
				i_17 = i_0 + i_9;
				i_18 = i_0 - i_9;
				if (i_17 >= MaterialProp29.anInt7071 && i_18 <= MaterialProp29.anInt7069) {
					i_17 = Class149_Sub3.method4890(i_17, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
					i_18 = Class149_Sub3.method4890(i_18, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
					if (i_7 < i_8) {
						i_19 = i_11 < i_7 ? anIntArray36[i_7] : i_11;
						i_20 = Class149_Sub3.method4890(i_0 + i_19, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						i_21 = Class149_Sub3.method4890(i_0 - i_19, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
						if (i_16 <= MaterialProp29.anInt7068) {
							ints_22 = MaterialProp29.anIntArrayArray7072[i_16];
							JagexArrayUtils.method3922(ints_22, i_18, i_21, i_4);
							JagexArrayUtils.method3922(ints_22, i_21, i_20, i_3);
							JagexArrayUtils.method3922(ints_22, i_20, i_17, i_4);
						}
						if (i_23 >= MaterialProp29.anInt7070) {
							ints_22 = MaterialProp29.anIntArrayArray7072[i_23];
							JagexArrayUtils.method3922(ints_22, i_18, i_21, i_4);
							JagexArrayUtils.method3922(ints_22, i_21, i_20, i_3);
							JagexArrayUtils.method3922(ints_22, i_20, i_17, i_4);
						}
					} else {
						if (i_16 <= MaterialProp29.anInt7068) {
							JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_16], i_18, i_17, i_4);
						}
						if (i_23 >= MaterialProp29.anInt7070) {
							JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_23], i_18, i_17, i_4);
						}
					}
				}
			}
		}
	}

	public static void method12838(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5) {
		if (i_0 - i_2 >= MaterialProp29.anInt7071 && i_0 + i_2 <= MaterialProp29.anInt7069 && i_1 - i_2 >= MaterialProp29.anInt7070 && i_2 + i_1 <= MaterialProp29.anInt7068) {
			method2637(i_0, i_1, i_2, i_3, i_4, i_5);
		} else {
			method1174(i_0, i_1, i_2, i_3, i_4, i_5);
		}
	}

	public static void method15241(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		int i_8 = 0;
		int i_9 = i_3;
		int i_10 = 0;
		int i_11 = i_2 - i_6;
		int i_12 = i_3 - i_6;
		int i_13 = i_2 * i_2;
		int i_14 = i_3 * i_3;
		int i_15 = i_11 * i_11;
		int i_16 = i_12 * i_12;
		int i_17 = i_14 << 1;
		int i_18 = i_13 << 1;
		int i_19 = i_16 << 1;
		int i_20 = i_15 << 1;
		int i_21 = i_3 << 1;
		int i_22 = i_12 << 1;
		int i_23 = i_17 + (1 - i_21) * i_13;
		int i_24 = i_14 - i_18 * (i_21 - 1);
		int i_25 = i_19 + (1 - i_22) * i_15;
		int i_26 = i_16 - i_20 * (i_22 - 1);
		int i_27 = i_13 << 2;
		int i_28 = i_14 << 2;
		int i_29 = i_15 << 2;
		int i_30 = i_16 << 2;
		int i_31 = i_17 * 3;
		int i_32 = i_18 * (i_21 - 3);
		int i_33 = i_19 * 3;
		int i_34 = i_20 * (i_22 - 3);
		int i_35 = i_28;
		int i_36 = (i_3 - 1) * i_27;
		int i_37 = i_30;
		int i_38 = i_29 * (i_12 - 1);
		int[] ints_39 = MaterialProp29.anIntArrayArray7072[i_1];
		JagexArrayUtils.method3922(ints_39, i_0 - i_2, i_0 - i_11, i_5);
		JagexArrayUtils.method3922(ints_39, i_0 - i_11, i_0 + i_11, i_4);
		JagexArrayUtils.method3922(ints_39, i_0 + i_11, i_0 + i_2, i_5);

		while (i_9 > 0) {
			boolean bool_40 = i_9 <= i_12;
			if (bool_40) {
				if (i_25 < 0) {
					while (i_25 < 0) {
						i_25 += i_33;
						i_26 += i_37;
						i_33 += i_30;
						i_37 += i_30;
						++i_10;
					}
				}

				if (i_26 < 0) {
					i_25 += i_33;
					i_26 += i_37;
					i_33 += i_30;
					i_37 += i_30;
					++i_10;
				}

				i_25 += -i_38;
				i_26 += -i_34;
				i_34 -= i_29;
				i_38 -= i_29;
			}

			if (i_23 < 0) {
				while (i_23 < 0) {
					i_23 += i_31;
					i_24 += i_35;
					i_31 += i_28;
					i_35 += i_28;
					++i_8;
				}
			}

			if (i_24 < 0) {
				i_23 += i_31;
				i_24 += i_35;
				i_31 += i_28;
				i_35 += i_28;
				++i_8;
			}

			i_23 += -i_36;
			i_24 += -i_32;
			i_32 -= i_27;
			i_36 -= i_27;
			--i_9;
			int i_41 = i_1 - i_9;
			int i_42 = i_9 + i_1;
			int i_43 = i_0 + i_8;
			int i_44 = i_0 - i_8;
			if (bool_40) {
				int i_45 = i_0 + i_10;
				int i_46 = i_0 - i_10;
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_41], i_44, i_46, i_5);
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_41], i_46, i_45, i_4);
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_41], i_45, i_43, i_5);
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_42], i_44, i_46, i_5);
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_42], i_46, i_45, i_4);
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_42], i_45, i_43, i_5);
			} else {
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_41], i_44, i_43, i_5);
				JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_42], i_44, i_43, i_5);
			}
		}

	}

	public static void method6824(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		int i_8 = 0;
		int i_9 = i_3;
		int i_10 = 0;
		int i_11 = i_2 - i_6;
		int i_12 = i_3 - i_6;
		int i_13 = i_2 * i_2;
		int i_14 = i_3 * i_3;
		int i_15 = i_11 * i_11;
		int i_16 = i_12 * i_12;
		int i_17 = i_14 << 1;
		int i_18 = i_13 << 1;
		int i_19 = i_16 << 1;
		int i_20 = i_15 << 1;
		int i_21 = i_3 << 1;
		int i_22 = i_12 << 1;
		int i_23 = i_17 + (1 - i_21) * i_13;
		int i_24 = i_14 - i_18 * (i_21 - 1);
		int i_25 = i_19 + (1 - i_22) * i_15;
		int i_26 = i_16 - i_20 * (i_22 - 1);
		int i_27 = i_13 << 2;
		int i_28 = i_14 << 2;
		int i_29 = i_15 << 2;
		int i_30 = i_16 << 2;
		int i_31 = i_17 * 3;
		int i_32 = i_18 * (i_21 - 3);
		int i_33 = i_19 * 3;
		int i_34 = i_20 * (i_22 - 3);
		int i_35 = i_28;
		int i_36 = (i_3 - 1) * i_27;
		int i_37 = i_30;
		int i_38 = i_29 * (i_12 - 1);
		int i_40;
		int i_41;
		int i_42;
		int i_43;
		if (i_1 >= MaterialProp29.anInt7070 && i_1 <= MaterialProp29.anInt7068) {
			int[] ints_39 = MaterialProp29.anIntArrayArray7072[i_1];
			i_40 = Class149_Sub3.method4890(i_0 - i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_41 = Class149_Sub3.method4890(i_0 + i_2, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_42 = Class149_Sub3.method4890(i_0 - i_11, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			i_43 = Class149_Sub3.method4890(i_0 + i_11, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
			JagexArrayUtils.method3922(ints_39, i_40, i_42, i_5);
			JagexArrayUtils.method3922(ints_39, i_42, i_43, i_4);
			JagexArrayUtils.method3922(ints_39, i_43, i_41, i_5);
		}

		while (i_9 > 0) {
			boolean bool_47 = i_9 <= i_12;
			if (bool_47) {
				if (i_25 < 0) {
					while (i_25 < 0) {
						i_25 += i_33;
						i_26 += i_37;
						i_33 += i_30;
						i_37 += i_30;
						++i_10;
					}
				}

				if (i_26 < 0) {
					i_25 += i_33;
					i_26 += i_37;
					i_33 += i_30;
					i_37 += i_30;
					++i_10;
				}

				i_25 += -i_38;
				i_26 += -i_34;
				i_34 -= i_29;
				i_38 -= i_29;
			}

			if (i_23 < 0) {
				while (i_23 < 0) {
					i_23 += i_31;
					i_24 += i_35;
					i_31 += i_28;
					i_35 += i_28;
					++i_8;
				}
			}

			if (i_24 < 0) {
				i_23 += i_31;
				i_24 += i_35;
				i_31 += i_28;
				i_35 += i_28;
				++i_8;
			}

			i_23 += -i_36;
			i_24 += -i_32;
			i_32 -= i_27;
			i_36 -= i_27;
			--i_9;
			i_40 = i_1 - i_9;
			i_41 = i_9 + i_1;
			if (i_41 >= MaterialProp29.anInt7070 && i_40 <= MaterialProp29.anInt7068) {
				i_42 = Class149_Sub3.method4890(i_0 + i_8, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
				i_43 = Class149_Sub3.method4890(i_0 - i_8, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
				if (bool_47) {
					int i_44 = Class149_Sub3.method4890(i_0 + i_10, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
					int i_45 = Class149_Sub3.method4890(i_0 - i_10, MaterialProp29.anInt7071, MaterialProp29.anInt7069);
					int[] ints_46;
					if (i_40 >= MaterialProp29.anInt7070) {
						ints_46 = MaterialProp29.anIntArrayArray7072[i_40];
						JagexArrayUtils.method3922(ints_46, i_43, i_45, i_5);
						JagexArrayUtils.method3922(ints_46, i_45, i_44, i_4);
						JagexArrayUtils.method3922(ints_46, i_44, i_42, i_5);
					}

					if (i_41 <= MaterialProp29.anInt7068) {
						ints_46 = MaterialProp29.anIntArrayArray7072[i_41];
						JagexArrayUtils.method3922(ints_46, i_43, i_45, i_5);
						JagexArrayUtils.method3922(ints_46, i_45, i_44, i_4);
						JagexArrayUtils.method3922(ints_46, i_44, i_42, i_5);
					}
				} else {
					if (i_40 >= MaterialProp29.anInt7070) {
						JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_40], i_43, i_42, i_5);
					}

					if (i_41 <= MaterialProp29.anInt7068) {
						JagexArrayUtils.method3922(MaterialProp29.anIntArrayArray7072[i_41], i_43, i_42, i_5);
					}
				}
			}
		}

	}

	public static void method5316(int i_0, int i_1, int i_2, int i_3, int i_4, int i_5, int i_6) {
		if (i_3 == i_2) {
			method12838(i_0, i_1, i_2, i_4, i_5, i_6);
		} else if (i_0 - i_2 >= MaterialProp29.anInt7071 && i_0 + i_2 <= MaterialProp29.anInt7069 && i_1 - i_3 >= MaterialProp29.anInt7070 && i_3 + i_1 <= MaterialProp29.anInt7068) {
			method15241(i_0, i_1, i_2, i_3, i_4, i_5, i_6);
		} else {
			method6824(i_0, i_1, i_2, i_3, i_4, i_5, i_6);
		}
	}

	@Override
	public void method2556(int i_1, int i_2) {
		int i_4 = anInt9274 * i_1 >> 12;
		int i_5 = anInt9276 * i_1 >> 12;
		int i_6 = i_2 * anInt9273 >> 12;
		int i_7 = i_2 * anInt9275 >> 12;
		method14584(i_4, i_6, i_5, i_7, anInt1743);
	}

	@Override
	public void method2557(int i_1, int i_2) {
		int i_4 = anInt9274 * i_1 >> 12;
		int i_5 = anInt9276 * i_1 >> 12;
		int i_6 = i_2 * anInt9273 >> 12;
		int i_7 = i_2 * anInt9275 >> 12;
		method5316(i_4, i_6, i_5, i_7, anInt1743, anInt1741, anInt1742);
	}
}
