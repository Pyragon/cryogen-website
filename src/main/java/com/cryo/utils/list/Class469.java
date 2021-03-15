package com.cryo.utils.list;

import java.util.Iterator;

public class Class469 implements Iterator {

	public static boolean LOYALTY_ENABLED;
	CacheableNode aCacheableNode_5582;
	Queue aClass477_5584;
	CacheableNode aCacheableNode_5583;

	public Class469(Queue class477_1) {
		aClass477_5584 = class477_1;
		aCacheableNode_5583 = aClass477_5584.aCacheableNode_5629.aCacheableNode_8119;
		aCacheableNode_5582 = null;
	}

	public CacheableNode method7790() {
		method7800();
		return (CacheableNode) next();
	}

	@Override
	public Object next() {
		CacheableNode cacheablenode_1 = aCacheableNode_5583;
		if (cacheablenode_1 == aClass477_5584.aCacheableNode_5629) {
			cacheablenode_1 = null;
			aCacheableNode_5583 = null;
		} else {
			aCacheableNode_5583 = cacheablenode_1.aCacheableNode_8119;
		}

		aCacheableNode_5582 = cacheablenode_1;
		return cacheablenode_1;
	}

	@Override
	public boolean hasNext() {
		return aClass477_5584.aCacheableNode_5629 != aCacheableNode_5583;
	}

	@Override
	public void remove() {
		if (aCacheableNode_5582 == null) {
			throw new IllegalStateException();
		} else {
			aCacheableNode_5582.method13452();
			aCacheableNode_5582 = null;
		}
	}

	public void method7800() {
		aCacheableNode_5583 = aClass477_5584.aCacheableNode_5629.aCacheableNode_8119;
		aCacheableNode_5582 = null;
	}
}
