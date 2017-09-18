package com.bailcompany.model;

import java.io.Serializable;

public class Payment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String nameOnCard;
	String cardNumber;
	String cvv;
	String exDate;
	String cardType;
	String nameOnAccount;
	String accType;
	String accNumber;
	String routingNumber;
	String creadtedDate;
	String incNumber;

	public String getNameOnCard() {
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getExDate() {
		return exDate;
	}

	public void setExDate(String exDate) {
		this.exDate = exDate;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getNameOnAccount() {
		return nameOnAccount;
	}

	public void setNameOnAccount(String nameOnAccount) {
		this.nameOnAccount = nameOnAccount;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getRoutingNumber() {
		return routingNumber;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public String getCreadtedDate() {
		return creadtedDate;
	}

	public void setCreadtedDate(String creadtedDate) {
		this.creadtedDate = creadtedDate;
	}

	public String getIncNumber() {
		return incNumber;
	}

	public void setIncNumber(String incNumber) {
		this.incNumber = incNumber;
	}
}
