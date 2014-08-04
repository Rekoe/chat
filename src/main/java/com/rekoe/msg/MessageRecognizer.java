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
		}
		return null;
	}

}
