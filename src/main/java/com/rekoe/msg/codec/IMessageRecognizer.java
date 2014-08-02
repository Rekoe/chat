package com.rekoe.msg.codec;

public interface IMessageRecognizer {
	public AbstractMessage createMessage(short type);
}
