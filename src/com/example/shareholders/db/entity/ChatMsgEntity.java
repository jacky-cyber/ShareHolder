package com.example.shareholders.db.entity;

import com.easemob.chat.EMMessage;

public class ChatMsgEntity {

	private String name;

	private long time;

	private String content;

	private Boolean isMine;

	private Boolean isCreator;// 群聊里用到

	private int type;

	private String userLogo;// 头像

	private String resUrl;

	private String localUrl = null;

	private Boolean isSendSuccess = true;

	private EMMessage message;

	public static class MsgType {
		public final static int TEXT = 0;
		public final static int RECORD = 1;
		public final static int IMAGE = 2;;
	}

	public ChatMsgEntity() {
		// TODO Auto-generated constructor stub
	}

	public ChatMsgEntity(String name, long time, String text, Boolean isMine,
			String userLogo) {
		super();
		this.name = name;
		this.time = time;
		this.content = text;
		this.isMine = isMine;
		this.userLogo = userLogo;
	}

	public ChatMsgEntity(String name, long time, String text, Boolean isMine,
			Boolean isCreator, String userLogo) {
		super();
		this.name = name;
		this.time = time;
		this.content = text;
		this.isMine = isMine;
		this.isCreator = isCreator;
		this.userLogo = userLogo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getIsMine() {
		return isMine;
	}

	public void setIsMine(Boolean isMine) {
		this.isMine = isMine;
	}

	public Boolean getIsCreator() {
		return isCreator;
	}

	public void setIsCreator(Boolean isCreator) {
		this.isCreator = isCreator;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getResUrl() {
		return resUrl;
	}

	public void setResUrl(String resUrl) {
		this.resUrl = resUrl;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public Boolean getIsSendSuccess() {
		return isSendSuccess;
	}

	public void setIsSendSuccess(Boolean isSendSuccess) {
		this.isSendSuccess = isSendSuccess;
	}

	public String getUserLogo() {
		return userLogo;
	}

	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}

	public EMMessage getMessage() {
		return message;
	}

	public void setMessage(EMMessage message) {
		this.message = message;
	}
}
