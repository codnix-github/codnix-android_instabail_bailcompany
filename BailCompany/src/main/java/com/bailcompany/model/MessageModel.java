package com.bailcompany.model;

import java.io.Serializable;

public class MessageModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String Requestid;
	String MessageId;
	String Type;
	String Message;
	String Time;
	String FromUserId;
	String FromRole;
	String FromName;
	String FromPhoto;
	String ToUserId;
	String ToRole;
	String ToName;
	String ToPhoto;
	String MessageRead;
	String FromSocketID;
	String ToSocketID;

	public String getFromSocketID() {
		return FromSocketID;
	}

	public void setFromSocketID(String fromSocketID) {
		FromSocketID = fromSocketID;
	}

	public String getToSocketID() {
		return ToSocketID;
	}

	public void setToSocketID(String toSocketID) {
		ToSocketID = toSocketID;
	}

	public String getMessageRead() {
		return MessageRead;
	}

	public void setMessageRead(String messageRead) {
		MessageRead = messageRead;
	}

	public String getMessageId() {
		return MessageId;
	}

	public void setMessageId(String messageId) {
		MessageId = messageId;
	}

	public String getRequestid() {
		return Requestid;
	}

	public void setRequestid(String requestid) {
		Requestid = requestid;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getFromUserId() {
		return FromUserId;
	}

	public void setFromUserId(String fromUserId) {
		FromUserId = fromUserId;
	}

	public String getFromRole() {
		return FromRole;
	}

	public void setFromRole(String fromRole) {
		FromRole = fromRole;
	}

	public String getFromName() {
		return FromName;
	}

	public void setFromName(String fromName) {
		FromName = fromName;
	}

	public String getFromPhoto() {
		return FromPhoto;
	}

	public void setFromPhoto(String fromPhoto) {
		FromPhoto = fromPhoto;
	}

	public String getToUserId() {
		return ToUserId;
	}

	public void setToUserId(String toUserId) {
		ToUserId = toUserId;
	}

	public String getToRole() {
		return ToRole;
	}

	public void setToRole(String toRole) {
		ToRole = toRole;
	}

	public String getToName() {
		return ToName;
	}

	public void setToName(String toName) {
		ToName = toName;
	}

	public String getToPhoto() {
		return ToPhoto;
	}

	public void setToPhoto(String toPhoto) {
		ToPhoto = toPhoto;
	}

}
