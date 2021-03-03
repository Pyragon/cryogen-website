package com.cryo.utils.list;

public class LinkedList {


	static int anInt5606;
	public Node head = new Node();
	Node current;

	public LinkedList() {
		head.next = head;
		head.previous = head;
	}

	public void clear() {
		while (true) {
			Node node_1 = head.next;
			if (node_1 == head) {
				current = null;
				return;
			}
			node_1.unlink();
		}
	}

	public Node getPrevious() {
		Node node_1 = current;
		if (node_1 == head) {
			current = null;
			return null;
		} else {
			current = node_1.next;
			return node_1;
		}
	}

	public Node popTail() {
		Node node_1 = head.next;
		if (node_1 == head) {
			return null;
		} else {
			node_1.unlink();
			return node_1;
		}
	}

	public Node getBack() {
		Node node_1 = head.next;
		if (node_1 == head) {
			current = null;
			return null;
		} else {
			current = node_1.next;
			return node_1;
		}
	}

	public boolean method7861() {
		return head.next == head;
	}

	public Node getNext() {
		Node node_1 = head.previous;
		if (node_1 == head) {
			current = null;
			return null;
		} else {
			current = node_1.previous;
			return node_1;
		}
	}

	public void insertFront(Node node_1) {
		if (node_1.previous != null) {
			node_1.unlink();
		}
		node_1.previous = head;
		node_1.next = head.next;
		node_1.previous.next = node_1;
		node_1.next.previous = node_1;
	}

	public void insertBack(Node node_1) {
		if (node_1.previous != null) {
			node_1.unlink();
		}
		node_1.previous = head.previous;
		node_1.next = head;
		node_1.previous.next = node_1;
		node_1.next.previous = node_1;
	}
}
