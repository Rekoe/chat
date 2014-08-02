package com.rekoe.server;

import io.netty.channel.Channel;

/**
 * 用户链表的结点类
 */
public class Node {
	short msgType;
	String username;
	Channel channel;
	Node next;
}