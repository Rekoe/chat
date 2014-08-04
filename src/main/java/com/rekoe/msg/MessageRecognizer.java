package com.rekoe.msg;

public class MessageRecognizer implements IMessageRecognizer {

	@Override
	public AbstractMessage createMessage(short type) {
		switch (type) {
			case MessageType.CS_LOGIN: {
				return new LoginMessage();
			}
			case MessageType.CS_CHAT: {
				return new ChatMessage();
			}
			case MessageType.CS_LOGIN_OUT: {
				return new LoginOutMessage();
			}
			case MessageType.CS_USER_NAME_LIST:{
				return new LoginUsersMessage();
			}
		}
		return null;
	}

}
