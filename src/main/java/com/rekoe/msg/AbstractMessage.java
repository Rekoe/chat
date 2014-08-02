package com.rekoe.msg;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;

public abstract class AbstractMessage {
	public static final int ERR_MESSAGE_LENGTH = 1024 * 300; // 300K,这个消息体太大了，err消息
	private ByteBuf byteBuf;

	public abstract void writeImpl();

	public abstract void readImpl();

	public abstract short getMessageType();

	public void write() throws Exception {
		byteBuf.clear();
		byteBuf.markWriterIndex();
		// 写消息
		byteBuf.writeInt(4 + 2);// ###写消息长度字段,先占位置
		byteBuf.writeShort(getMessageType()); // ###写消息id
		writeImpl(); // ###写消息实体内容

		// 回写长度
		int messageLength = byteBuf.writerIndex();

		if (messageLength > ERR_MESSAGE_LENGTH) {
			throw new Exception("消息太大:" + messageLength + ",type:" + this.getMessageType());
		}
		byteBuf.resetWriterIndex();// buffer的写指针回到写长度的位置，即上面记录的markWriterIndex
		byteBuf.writeInt(messageLength);// 回写写入真实长度
		byteBuf.setIndex(0, messageLength);// 恢复读指针和写指针的位置
	}

	public void writeInt(int value) {
		byteBuf.writeInt(value);
	}

	public void writeLong(long value) {
		byteBuf.writeLong(value);
	}

	public void writeByte(byte value) {
		byteBuf.writeByte(value);
	}

	public void writeBytes(byte[] _bytes) {
		byteBuf.writeBytes(_bytes);
	}

	public void writeString(String value) {
		byte[] _bytes = StringUtils.defaultString(value, "").getBytes(Charset.forName("UTF-8"));
		byteBuf.writeShort(_bytes.length);
		byteBuf.writeBytes(_bytes);
	}

	public void writeShort(short value) {
		byteBuf.writeShort(value);
	}

	public void writeBoolean(boolean data) {
		this.byteBuf.writeBoolean(data);
	}

	public void writeFloat(float value) {
		byteBuf.writeFloat(value);
	}

	public void writeDouble(double value) {
		byteBuf.writeDouble(value);
	}

	public int readInt() {
		return byteBuf.readInt();
	}

	public long readLong() {
		return byteBuf.readLong();
	}

	public short readShort() {
		return byteBuf.readShort();
	}

	public byte readByte() {
		return byteBuf.readByte();
	}

	public float readFloat() {
		return byteBuf.readFloat();
	}

	public double readDouble() {
		return byteBuf.readDouble();
	}

	public String readString() {
		int _len = readShort();// readInt();
		if (_len > 0) {
			byte[] _bytes = new byte[_len];
			byteBuf.readBytes(_bytes);
			return new String(_bytes, Charset.forName("UTF-8"));
		}
		return null;
	}

	public ByteBuf getBuffer() {
		return this.byteBuf;
	}

	public void setByteBuf(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public void read() {
		readImpl();
	}
}
