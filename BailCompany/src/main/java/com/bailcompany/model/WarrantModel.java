package com.bailcompany.model;

import java.io.Serializable;

public class WarrantModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String Township, Amount,case_no,PowerNo;

	public WarrantModel() {
		this.Township = "";
		this.Amount = "";
		this.case_no = "";
		this.PowerNo = "";
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getTownship() {
		return Township;
	}

	public void setTownship(String township) {
		Township = township;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getCase_no() {
		return case_no;
	}

	public void setCase_no(String case_no) {
		this.case_no = case_no;
	}

	public String getPowerNo() {
		return PowerNo;
	}

	public void setPowerNo(String powerNo) {
		PowerNo = powerNo;
	}
}
