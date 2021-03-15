package com.cryo.cache.loaders.model.material.properties;

import java.util.Date;
import java.util.Random;

public class Class316 {

	public static int[] anIntArray3677;
	public static int[] anIntArray3675;
	public static int[] anIntArray3672;
	public static int anInt3669;
	public static int anInt3670;
	public static int anInt3678;
	public static int[] anIntArray3668;
	public static int anInt3673;
	public static int anInt3671;
	public static Class223 aClass223_3679 = new Class223(16);

	public Class316() throws Throwable {
		throw new Error();
	}

	public static void method5586() {
		if (anIntArray3677 == null || anIntArray3675 == null) {
			anIntArray3677 = new int[256];
			anIntArray3675 = new int[256];
			for (int i_1 = 0; i_1 < 256; i_1++) {
				double d_2 = 6.283185307179586D * (i_1 / 255.0D);
				anIntArray3677[i_1] = (int) (Math.sin(d_2) * 4096.0D);
				anIntArray3675[i_1] = (int) (Math.cos(d_2) * 4096.0D);
			}
		}
	}

	public static byte[] method5588(int i_0) {
		CacheableNode_Sub2 class282_sub50_sub2_2 = (CacheableNode_Sub2) aClass223_3679.get(i_0);
		if (class282_sub50_sub2_2 == null) {
			byte[] bytes_3 = new byte[512];
			Random random_4 = new Random(i_0);
			int i_5;
			for (i_5 = 0; i_5 < 255; i_5++) {
				bytes_3[i_5] = (byte) i_5;
			}
			for (i_5 = 0; i_5 < 255; i_5++) {
				int i_6 = 255 - i_5;
				int i_7 = MaterialProp4.method7931(random_4, i_6);
				byte b_8 = bytes_3[i_7];
				bytes_3[i_7] = bytes_3[i_6];
				bytes_3[i_6] = bytes_3[511 - i_5] = b_8;
			}
			class282_sub50_sub2_2 = new CacheableNode_Sub2(bytes_3);
			aClass223_3679.put(class282_sub50_sub2_2, i_0);
		}
		return class282_sub50_sub2_2.aByteArray9472;
	}

	public static void method5593(int i_0, int i_1) {
		int i_3;
		if (i_0 != anInt3670) {
			anIntArray3672 = new int[i_0];
			for (i_3 = 0; i_3 < i_0; i_3++) {
				anIntArray3672[i_3] = (i_3 << 12) / i_0;
			}
			anInt3669 = i_0 - 1;
			anInt3670 = i_0;
			anInt3678 = i_0 * 32;
		}
		if (anInt3671 != i_1) {
			if (anInt3670 != i_1) {
				anIntArray3668 = new int[i_1];
				for (i_3 = 0; i_3 < i_1; i_3++) {
					anIntArray3668[i_3] = (i_3 << 12) / i_1;
				}
			} else {
				anIntArray3668 = anIntArray3672;
			}
			anInt3673 = i_1 - 1;
			anInt3671 = i_1;
		}
	}
}
