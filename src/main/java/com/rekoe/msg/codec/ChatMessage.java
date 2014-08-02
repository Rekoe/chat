package com.rekoe.msg.codec;

public class ChatMessage extends AbstractMessage {

	private String msg;
	private short type;

	public ChatMessage() {
		super();
	}

	public ChatMessage(short type, String msg) {
		super();
		this.msg = msg;
		this.type = type;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_CHAT;
	}

	@Override
	public void readImpl() {
		this.type = readShort();
		this.msg = readString();
	}

	@Override
	public void writeImpl() {
		writeShort(type);
		writeString(msg);
	}

	public String getMsg() {
		return msg;
	}

	public short getType() {
		return type;
	}

}
