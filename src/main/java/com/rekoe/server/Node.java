package com.rekoe.server;

import java.net.*;
import java.io.*;

/**
 * 用户链表的结点类
 */
public class Node {
	String username = null;
	Socket socket = null;
	ObjectOutputStream output = null;
	ObjectInputStream input = null;
		
	Node next = null;
}