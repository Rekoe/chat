package com.rekoe.client.test;

import com.rekoe.msg.codec.MessageType;

/**
 * 聊天用户注册消息
 * 
 * @author kouxian
 */
public class GSTestRegisteMessage extends AbstractTestMessage {
	private long uid;
	private String leageID;
	private int serverid;
	private String name;
	private String token;
	private int vipLevel;
	private int stageProperty;
	private boolean isLock;
	private int lockMinute;

	public GSTestRegisteMessage(long uid, String leageID, int serverid, String name, String token, int vipLevel, int stageProperty, boolean isLock, int lockMinute) {
		super();
		this.uid = uid;
		this.leageID = leageID;
		this.serverid = serverid;
		this.name = name;
		this.token = token;
		this.vipLevel = vipLevel;
		this.stageProperty = stageProperty;
		this.isLock = isLock;
		this.lockMinute = lockMinute;
	}

	@Override
	public void writeImpl() {
		writeInt(this.serverid);
		writeLong(this.uid);
		writeString(this.leageID);
		writeString(this.name);
		writeString(this.token);
		writeInt(this.vipLevel);
		writeInt(this.stageProperty);
		writeBoolean(this.isLock);
		writeInt(this.lockMinute);
	}

	@Override
	public short getMessageType() {
		return MessageType.GS_REGISTE;
	}

	public long getUid() {
		return uid;
	}

	public String getToken() {
		return token;
	}

	public String getLeageID() {
		return leageID;
	}

	public int getServerid() {
		return serverid;
	}

	public String getName() {
		return name;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public int getStageProperty() {
		return stageProperty;
	}

	public boolean isLock() {
		return isLock;
	}

	public int getLockMinute() {
		return lockMinute;
	}
}
