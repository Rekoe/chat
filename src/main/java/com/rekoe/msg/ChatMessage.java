package com.rekoe.msg;


public class ChatMessage extends AbstractMessage {

	private String msg;
	private short type;
	private String username;

	public ChatMessage() {
		super();
	}

	public ChatMessage(short type, String msg, String username) {
		super();
		this.msg = msg;
		this.type = type;
		this.username = username;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_CHAT;
	}

	@Override
	public void readImpl() {
		this.type = readShort();
		this.msg = readString();
		this.username = readString();
	}

	@Override
	public void writeImpl() {
		writeShort(type);
		writeString(msg);
		writeString(username);
	}

	public String getMsg() {
		return msg;
	}

	public short getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

}
