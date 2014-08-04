package com.rekoe.msg;


public class LoginOutMessage extends AbstractMessage {

	private String username;
	
	public LoginOutMessage() {
		super();
	}

	public LoginOutMessage(String username) {
		super();
		this.username = username;
	}

	@Override
	public short getMessageType() {
		return MessageType.CS_LOGIN_OUT;
	}

	@Override
	public void readImpl() {
		this.username = readString();
	}

	@Override
	public void writeImpl() {
		writeString(username);
	}

	public String getUsername() {
		return username;
	}

}
