package com.rekoe.msg.codec;

public abstract class MessageType {

	public final static short GS_REGISTE = 1000;
	public final static short GS_CHAT_OPEN = 1001;
	public final static short GS_FORBID_USER = 1002;
	public final static short GS_KILL_USER = 1003;
	public final static short GS_REGISTE_SYSTEM = 1004;
	/** 用户在地图数据 */
	public final static short GS_DATA_MAP = 200;
	public final static short CS_LOGIN = 100;
	public final static short SC_LOGIN_RESULT = 102;
	public final static short CS_CHAT = 101;
	/**
	 * 全部频道
	 */
	public final static short CHANNEL_ALL = 1;
	/**
	 * 世界
	 */
	public final static short CHANNEL_WORLD = 2;
	/**
	 * 军团
	 */
	public final static short CHANNEL_CORPS = 3;
	/**
	 * 活动
	 */
	public final static short CHANNEL_ACTIVITY = 4;
	/**
	 * GM
	 */
	public final static short CHANNEL_GM = 5;

	/**
	 * 新用户登陆
	 */
	public final static short SC_WELLCOME = 600;
	public final static short SC_CHAT = 601;
}
