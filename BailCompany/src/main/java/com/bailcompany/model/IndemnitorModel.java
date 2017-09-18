package com.bailcompany.model;

import java.io.Serializable;

public class IndemnitorModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String Name,FName,LName, PhoneNumber;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getFName() {
		return FName;
	}

	public void setFName(String fname) {
		FName = fname;
	}
	public String getLName() {
		return LName;
	}

	public void setLName(String lname) {
		LName = lname;
	}
	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		PhoneNumber = phoneNumber;
	}
	
	
}
