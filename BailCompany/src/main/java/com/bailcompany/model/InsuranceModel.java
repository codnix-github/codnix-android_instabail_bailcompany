package com.bailcompany.model;

import java.io.Serializable;

public class InsuranceModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int id;
	String name, createdDate, CompanyName, InsAddress, APT, Town, InsState,
			Zip, BusinessPhone, ContatcPerson, LicenseNo, Email;
	boolean ForAgent;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getInsAddress() {
		return InsAddress;
	}

	public void setInsAddress(String insAddress) {
		InsAddress = insAddress;
	}

	public String getAPT() {
		return APT;
	}

	public void setAPT(String aPT) {
		APT = aPT;
	}

	public String getTown() {
		return Town;
	}

	public void setTown(String town) {
		Town = town;
	}

	public String getInsState() {
		return InsState;
	}

	public void setInsState(String insState) {
		InsState = insState;
	}

	public String getZip() {
		return Zip;
	}

	public void setZip(String zip) {
		Zip = zip;
	}

	public String getBusinessPhone() {
		return BusinessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		BusinessPhone = businessPhone;
	}

	public String getContatcPerson() {
		return ContatcPerson;
	}

	public void setContatcPerson(String contatcPerson) {
		ContatcPerson = contatcPerson;
	}

	public String getLicenseNo() {
		return LicenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		LicenseNo = licenseNo;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public boolean isForAgent() {
		return ForAgent;
	}

	public void setForAgent(boolean forAgent) {
		ForAgent = forAgent;
	}

}
