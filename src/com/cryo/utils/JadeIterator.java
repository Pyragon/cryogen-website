package com.cryo.utils;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: March 09, 2017 at 8:37:25 AM
 */
public class JadeIterator {
	
	public int[] iterate(int size) {
		int[] array = new int[size];
		for(int i = 0; i < array.length; i++)
			array[i] = i;
		return array;
	}
	
}
