package com.rekoe.msg;


public interface IMessageRecognizer {
	public AbstractMessage createMessage(short type);
}
