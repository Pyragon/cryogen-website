package com.cryo.utils.list;

import java.util.Iterator;

public class HashTableIterator implements Iterator {

	Node aNode_5441;

	IterableNodeMap aClass465_5439;

	public int anInt5442;

	Node aNode_5440;

	public HashTableIterator(IterableNodeMap iterablenodemap_1) {
		aClass465_5439 = iterablenodemap_1;
		method7512();
	}

	public Node first() {
		method7512();
		return (Node) next();
	}

	@Override
	public boolean hasNext() {
		if (aClass465_5439.nodes[anInt5442 - 1] != aNode_5440) {
			return true;
		} else {
			while (anInt5442 < aClass465_5439.size) {
				if (aClass465_5439.nodes[++anInt5442 - 1].next != aClass465_5439.nodes[anInt5442 - 1]) {
					aNode_5440 = aClass465_5439.nodes[anInt5442 - 1].next;
					return true;
				}
				aNode_5440 = aClass465_5439.nodes[anInt5442 - 1];
			}
			return false;
		}
	}

	@Override
	public void remove() {
		if (aNode_5441 == null) {
			throw new IllegalStateException();
		} else {
			aNode_5441.unlink();
			aNode_5441 = null;
		}
	}

	@Override
	public Object next() {
		Node node_1;
		if (aClass465_5439.nodes[anInt5442 - 1] != aNode_5440) {
			node_1 = aNode_5440;
			aNode_5440 = node_1.next;
			aNode_5441 = node_1;
			return node_1;
		} else {
			while (anInt5442 < aClass465_5439.size) {
				node_1 = aClass465_5439.nodes[++anInt5442 - 1].next;
				if (node_1 != aClass465_5439.nodes[anInt5442 - 1]) {
					aNode_5440 = node_1.next;
					aNode_5441 = node_1;
					return node_1;
				}
			}
			return null;
		}
	}

	public void method7512() {
		aNode_5440 = aClass465_5439.nodes[0].next;
		anInt5442 = 1;
		aNode_5441 = null;
	}
}
