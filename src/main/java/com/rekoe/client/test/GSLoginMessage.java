package com.rekoe.client.test;

import com.rekoe.msg.codec.AbstractMessage;
import com.rekoe.msg.codec.MessageType;

public class GSLoginMessage extends AbstractMessage {

	private String username;

	public GSLoginMessage() {
		super();
	}

	public GSLoginMessage(String username) {
		super();
		this.username = username;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_LOGIN;
	}

	@Override
	public void writeImpl() {
		writeString(username);
	}

	public String getUsername() {
		return username;
	}

	@Override
	public void readImpl() {
		username = readString();
	}
}
