package com.bailcompany.model;

import java.io.Serializable;

public class BlackListMember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String BlacklistMemberId;
	String CompanyId;
	String CompanyName;
	String CompanyPicture;
	String NameOfDefendent;
	String Picture;
	String Address;
	String City;
	String State;
	String Zip;
	String AmountForfeited;
	String Telephone;
	String DOB;
	String DOF;
	String Notes;
	String Read;

	public String getAmountForfeited() {
		return AmountForfeited;
	}

	public void setAmountForfeited(String amountForfeited) {
		AmountForfeited = amountForfeited;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	String CreatedDateTime;

	public String getBlacklistMemberId() {
		return BlacklistMemberId;
	}

	public void setBlacklistMemberId(String blacklistMemberId) {
		BlacklistMemberId = blacklistMemberId;
	}

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getCompanyPicture() {
		return CompanyPicture;
	}

	public void setCompanyPicture(String companyPicture) {
		CompanyPicture = companyPicture;
	}

	public String getNameOfDefendent() {
		return NameOfDefendent;
	}

	public void setNameOfDefendent(String nameOfDefendent) {
		NameOfDefendent = nameOfDefendent;
	}

	public String getPicture() {
		return Picture;
	}

	public void setPicture(String picture) {
		Picture = picture;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getZip() {
		return Zip;
	}

	public void setZip(String zip) {
		Zip = zip;
	}

	public String getTelephone() {
		return Telephone;
	}

	public void setTelephone(String telephone) {
		Telephone = telephone;
	}

	public String getDOB() {
		return DOB;
	}

	public void setDOB(String dOB) {
		DOB = dOB;
	}

	public String getDOF() {
		return DOF;
	}

	public void setDOF(String dOF) {
		DOF = dOF;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public String getCreatedDateTime() {
		return CreatedDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		CreatedDateTime = createdDateTime;
	}
}
