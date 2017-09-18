package com.bailcompany.model;

import java.io.Serializable;

public class ChatUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String MessageFrom;
	String MessageTo;
	String RoleIdB;
	String MessageWay;
	String UserRole;
	String UserName;
	String UserPhoto;
	String SocketId;

	public String getSocketId() {
		return SocketId;
	}

	public void setSocketId(String socketId) {
		SocketId = socketId;
	}

	MessageModel LastMessage;

	public String getMessageFrom() {
		return MessageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		MessageFrom = messageFrom;
	}

	public String getMessageTo() {
		return MessageTo;
	}

	public void setMessageTo(String messageTo) {
		MessageTo = messageTo;
	}

	public String getRoleIdB() {
		return RoleIdB;
	}

	public void setRoleIdB(String roleIdB) {
		RoleIdB = roleIdB;
	}

	public String getMessageWay() {
		return MessageWay;
	}

	public void setMessageWay(String messageWay) {
		MessageWay = messageWay;
	}

	public String getUserRole() {
		return UserRole;
	}

	public void setUserRole(String userRole) {
		UserRole = userRole;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserPhoto() {
		return UserPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		UserPhoto = userPhoto;
	}

	public MessageModel getLastMessage() {
		return LastMessage;
	}

	public void setLastMessage(MessageModel lastMessage) {
		LastMessage = lastMessage;
	}

}
