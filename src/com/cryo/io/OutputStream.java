package com.cryo.io;

import com.cryo.utils.Utilities;

public class OutputStream extends Stream {

	private static final int[] BIT_MASK = new int[32];
	private int opcodeStart = 0;

	static {
		for (int i = 0; i < 32; i++)
			BIT_MASK[i] = (1 << i) - 1;
	}

	public OutputStream(int capacity) {
		setBuffer(new byte[capacity]);
		init();
	}

	public OutputStream() {
		setBuffer(new byte[16]);
		init();
	}

	public void init() {

	}

	public void write5ByteInteger(long value) {
		writeByte((int) (value >> 32));
		writeInt((int) (value & 0xffffffff));
	}

	public OutputStream(byte[] buffer) {
		this.setBuffer(buffer);
		this.offset = buffer.length;
		length = buffer.length;
	}

	public OutputStream(int[] buffer) {
		setBuffer(new byte[buffer.length]);
		for (int value : buffer)
			writeByte(value);
	}

	public void checkCapacityPosition(int position) {
		if (position >= getBuffer().length) {
			byte[] newBuffer = new byte[position + 16];
			System.arraycopy(getBuffer(), 0, newBuffer, 0, getBuffer().length);
			setBuffer(newBuffer);
		}
	}

	public void skip(int length) {
		setOffset(offset() + length);
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public final void encodeXTEA(int keys[], int start, int end) {
		int o = offset;
		int j = (end - start) / 8;
		offset = start;
		for (int k = 0; k < j; k++) {
			int l = readInt();
			int i1 = readInt();
			int sum = 0;
			int delta = 0x9e3779b9;
			for (int l1 = 32; l1-- > 0;) {
				l += sum + keys[3 & sum] ^ i1 + (i1 >>> 5 ^ i1 << 4);
				sum += delta;
				i1 += l + (l >>> 5 ^ l << 4) ^ keys[(0x1eec & sum) >>> 11] + sum;
			}

			offset -= 8;
			writeInt(l);
			writeInt(i1);
		}
		offset = o;
	}

	private final int readInt() {
		offset += 4;
		return ((0xff & buffer[-3 + offset]) << 16) + ((((0xff & buffer[-4 + offset]) << 24) + ((buffer[-2 + offset] & 0xff) << 8)) + (buffer[-1 + offset] & 0xff));
	}

	public void writeBytes(byte[] b, int offset, int length) {
		checkCapacityPosition(this.offset() + length - offset);
		System.arraycopy(b, offset, getBuffer(), this.offset(), length);
		this.setOffset(this.offset() + (length - offset));
	}

	public void write_bytes(byte[] b) {
		int offset = 0;
		int length = b.length;
		checkCapacityPosition(this.offset() + length - offset);
		System.arraycopy(b, offset, getBuffer(), this.offset(), length);
		this.setOffset(this.offset() + (length - offset));
	}

	public void addBytes128(byte[] data, int offset, int len) {
		for (int k = offset; k < len; k++)
			writeByte((byte) (data[k] + 128));
	}

	public void addBytesS(byte[] data, int offset, int len) {
		for (int k = offset; k < len; k++)
			writeByte((byte) (-128 + data[k]));
	}

	public void addBytes_Reverse(byte[] data, int offset, int len) {
		for (int i = len - 1; i >= 0; i--) {
			writeByte((data[i]));
		}
	}

	public void addBytes_Reverse128(byte[] data, int offset, int len) {
		for (int i = len - 1; i >= 0; i--) {
			writeByte((byte) (data[i] + 128));
		}
	}

	public void writeByte(int i) {
		writeByte(i, offset++);
	}

	public void writeNegativeByte(int i) {
		writeByte(-i, offset++);
	}

	public void writeByte(int i, int position) {
		checkCapacityPosition(position);
		getBuffer()[position] = (byte) i;
	}

	public void write_byte128(int i) {
		writeByte(i + 128);
	}

	public void writeByteC(int i) {
		writeByte(-i);
	}

	public void write128Byte(int i) {
		writeByte(128 - i);
	}

	public void writeShortLE128(int i) {
		writeByte(i + 128);
		writeByte(i >> 8);
	}

	public void writeShort128(int i) {
		writeByte(i >> 8);
		writeByte(i + 128);
	}

	public void write_smart(int i) {
		if (i >= 128) {
			writeShort(i + 32768);
		} else {
			writeByte(i);
		}
	}

	public void writeBigSmart(int i) {
		if (i >= Short.MAX_VALUE)
			writeInt(i - Integer.MAX_VALUE - 1);
		else {
			writeShort(i >= 0 ? i : 32767);
		}
	}

	public void writeShort(int i) {
		writeByte(i >> 8);
		writeByte(i);
	}

	public void write_shortLE(int i) {
		writeByte(i);
		writeByte(i >> 8);
	}

	public void write_24bit_integer(int i) {
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void write24BitIntegerV2(int i) {
		writeByte(i >> 16);
		writeByte(i);
		writeByte(i >> 8);
	}

	public void writeInt(int i) {
		writeByte(i >> 24);
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void write_intV1(int i) {
		writeByte(i >> 8);
		writeByte(i);
		writeByte(i >> 24);
		writeByte(i >> 16);
	}

	public void write_intV2(int i) {
		writeByte(i >> 16);
		writeByte(i >> 24);
		writeByte(i);
		writeByte(i >> 8);
	}

	public void write_intLE(int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);
		writeByte(i >> 24);
	}

	public void write_long(long l) {
		writeByte((int) (l >> 56));
		writeByte((int) (l >> 48));
		writeByte((int) (l >> 40));
		writeByte((int) (l >> 32));
		writeByte((int) (l >> 24));
		writeByte((int) (l >> 16));
		writeByte((int) (l >> 8));
		writeByte((int) l);
	}

	public void writePSmarts(int i) {
		if (i < 128) {
			writeByte(i);
			return;
		}
		if (i < 32768) {
			writeShort(32768 + i);
			return;
		} else {
			System.out.println("Error psmarts out of range:");
			return;
		}
	}

	public void writeString(String s) {
		checkCapacityPosition(offset() + s.length() + 1);
		System.arraycopy(s.getBytes(), 0, getBuffer(), offset(), s.length());
		setOffset(offset() + s.length());
		writeByte(0);
	}

	public void writeGJString(String s) {
		writeByte(0);
		writeString(s);
	}

	public void putGJString3(String s) {
		writeByte(0);
		writeString(s);
		writeByte(0);
	}

	public void endPacketVarByte() {
		writeByte(offset() - (opcodeStart + 2) + 1, opcodeStart);
	}

	public void end_packet_var_short() {
		int size = offset() - (opcodeStart + 2);
		writeByte(size >> 8, opcodeStart++);
		writeByte(size, opcodeStart);
	}

	public void initBitAccess() {
		bitPosition = offset() * 8;
	}

	public void finishBitAccess() {
		setOffset((bitPosition + 7) / 8);
	}

	public int getBitPos(int i) {
		return 8 * i - bitPosition;
	}

	public void writeBits(int numBits, int value) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		for (; numBits > bitOffset; bitOffset = 8) {
			checkCapacityPosition(bytePos);
			getBuffer()[bytePos] &= ~BIT_MASK[bitOffset];
			getBuffer()[bytePos++] |= value >> numBits - bitOffset & BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}
		checkCapacityPosition(bytePos);
		if (numBits == bitOffset) {
			getBuffer()[bytePos] &= ~BIT_MASK[bitOffset];
			getBuffer()[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			getBuffer()[bytePos] &= ~(BIT_MASK[numBits] << bitOffset - numBits);
			getBuffer()[bytePos] |= (value & BIT_MASK[numBits]) << bitOffset - numBits;
		}
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

}