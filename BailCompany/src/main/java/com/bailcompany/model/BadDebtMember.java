package com.bailcompany.model;

import java.io.Serializable;

public class BadDebtMember implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String BadDebtMemberId;
	String CompanyId;
	String CompanyName;
	String CompanyPicture;
	String Name;

	String Picture;
	String Address;
	String City;
	String State;
	String Zip;

	String Telephone;
	String DOB;
	String DefendentOrCosigner;
	String AmountOwed;
	String CreatedDateTime;
	String Read;
	public String getRead() {
		return Read;
	}
	public void setRead(String read) {
		Read = read;
	}
	public String getBadDebtMemberId() {
		return BadDebtMemberId;
	}
	public void setBadDebtMemberId(String badDebtMemberId) {
		BadDebtMemberId = badDebtMemberId;
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
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
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
	public String getDefendentOrCosigner() {
		return DefendentOrCosigner;
	}
	public void setDefendentOrCosigner(String defendentOrCosigner) {
		DefendentOrCosigner = defendentOrCosigner;
	}
	public String getAmountOwed() {
		return AmountOwed;
	}
	public void setAmountOwed(String amountOwed) {
		AmountOwed = amountOwed;
	}
	public String getCreatedDateTime() {
		return CreatedDateTime;
	}
	public void setCreatedDateTime(String createdDateTime) {
		CreatedDateTime = createdDateTime;
	}
	
	

}
