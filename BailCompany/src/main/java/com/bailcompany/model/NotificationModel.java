package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839695487929115042L;
	private String NotificationId;
	private String SenderId;
	private String ReceiverId;
	private String RequestId;
	private String IsRead;
	private String Message;
	private String CreatedAt;
	private String Type;
	private String ToastShown;
	private String Is_Seen;
	private String top_bar_click;
	private String Name;
	private String Photo;

	public String getNotificationId() {
		return NotificationId;
	}

	public void setNotificationId(String notificationId) {
		NotificationId = notificationId;
	}

	public String getSenderId() {
		return SenderId;
	}

	public void setSenderId(String senderId) {
		SenderId = senderId;
	}

	public String getReceiverId() {
		return ReceiverId;
	}

	public void setReceiverId(String receiverId) {
		ReceiverId = receiverId;
	}

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getIsRead() {
		return IsRead;
	}

	public void setIsRead(String isRead) {
		IsRead = isRead;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getCreatedAt() {
		return CreatedAt;
	}

	public void setCreatedAt(String createdAt) {
		CreatedAt = createdAt;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getToastShown() {
		return ToastShown;
	}

	public void setToastShown(String toastShown) {
		ToastShown = toastShown;
	}

	public String getIs_Seen() {
		return Is_Seen;
	}

	public void setIs_Seen(String is_Seen) {
		Is_Seen = is_Seen;
	}

	public String getTop_bar_click() {
		return top_bar_click;
	}

	public void setTop_bar_click(String top_bar_click) {
		this.top_bar_click = top_bar_click;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPhoto() {
		return Photo;
	}

	public void setPhoto(String photo) {
		Photo = photo;
	}
}
