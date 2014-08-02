package com.rekoe.msg.codec;

import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class AbstractMessage {

	public abstract short getMessageType();

	private ByteBuf byteBuf;
	public Channel channel;

	public abstract void readImpl();

	public abstract void writeImpl();

	public void read() {
		readImpl();
	}

	public void write() throws Exception {
		byteBuf.markWriterIndex();
		byteBuf.writeInt(6);
		byteBuf.writeShort(getMessageType());
		writeImpl();
		int messageLength = byteBuf.writerIndex();
		if (messageLength > 6) {
			throw new Exception("消息太大:" + messageLength + ",type:" + this.getMessageType());
		}
		byteBuf.resetWriterIndex();
		byteBuf.writeInt(messageLength);
		byteBuf.setIndex(0, messageLength);
	}

	public ByteBuf getByteBuf() {
		return byteBuf;
	}

	public void setByteBuf(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
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
		value = StringUtils.defaultIfBlank(value, "Empty Message");
		byte[] _bytes = value.getBytes();
		byteBuf.writeShort(_bytes.length);
		byteBuf.writeBytes(_bytes);
	}

	public void writeShort(short value) {
		byteBuf.writeShort(value);
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

	public boolean readBoolean() {
		return byteBuf.readBoolean();
	}

	public double readDouble() {
		return byteBuf.readDouble();
	}

	public String readString() {
		String value = null;
		int _len = readShort();// readInt();
		if (_len > 0) {
			byte[] _bytes = new byte[_len];
			byteBuf.readBytes(_bytes);
			value = new String(_bytes);
		}
		return value;
	}

	public ByteBuf getBuffer() {
		return this.byteBuf;
	}

	public void setBuffer(ByteBuf byteBuf) {
		this.byteBuf = byteBuf;
	}

	public void writeBoolean(boolean data) {
		this.byteBuf.writeBoolean(data);
	}
}
