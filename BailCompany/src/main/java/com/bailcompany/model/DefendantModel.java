package com.bailcompany.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class DefendantModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839695487929115041L;
	private String Id;
	private String CompanyId;
	private String FirstName;
	private String LastName;
	private String Address;
	private String Town;
	private String State;
	private String Zipcode;
	private String DOB;
	private String SSN;
	private String POB;
	private String HomeTele;
	private String CellTele;
	private String Height;
	private String Weight;
	private String HairColor;
	private String EyeColor;
	private String Tattoos;
	private String MaritalStatus;
	private String Photo;
	private String FacebookURL;
	private String TwitterURL;
	private String GoogleURL;
	private String Status;
	private String ModifyOn;
	private String StateName;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

	public String getFirstName() {
		return FirstName;
	}

	public void setFirstName(String firstName) {
		FirstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getTown() {
		return Town;
	}

	public void setTown(String town) {
		Town = town;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getZipcode() {
		return Zipcode;
	}

	public void setZipcode(String zipcode) {
		Zipcode = zipcode;
	}

	public String getDOB() {
		return DOB;
	}

	public void setDOB(String DOB) {
		this.DOB = DOB;
	}

	public String getSSN() {
		return SSN;
	}

	public void setSSN(String SSN) {
		this.SSN = SSN;
	}

	public String getPOB() {
		return POB;
	}

	public void setPOB(String POB) {
		this.POB = POB;
	}

	public String getHomeTele() {
		return HomeTele;
	}

	public void setHomeTele(String homeTele) {
		HomeTele = homeTele;
	}

	public String getCellTele() {
		return CellTele;
	}

	public void setCellTele(String cellTele) {
		CellTele = cellTele;
	}

	public String getHeight() {
		return Height;
	}

	public void setHeight(String height) {
		Height = height;
	}

	public String getWeight() {
		return Weight;
	}

	public void setWeight(String weight) {
		Weight = weight;
	}

	public String getHairColor() {
		return HairColor;
	}

	public void setHairColor(String hairColor) {
		HairColor = hairColor;
	}

	public String getEyeColor() {
		return EyeColor;
	}

	public void setEyeColor(String eyeColor) {
		EyeColor = eyeColor;
	}

	public String getTattoos() {
		return Tattoos;
	}

	public void setTattoos(String tattoos) {
		Tattoos = tattoos;
	}

	public String getMaritalStatus() {
		return MaritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		MaritalStatus = maritalStatus;
	}

	public String getPhoto() {
		return Photo;
	}

	public void setPhoto(String photo) {
		Photo = photo;
	}

	public String getFacebookURL() {
		return FacebookURL;
	}

	public void setFacebookURL(String facebookURL) {
		FacebookURL = facebookURL;
	}

	public String getTwitterURL() {
		return TwitterURL;
	}

	public void setTwitterURL(String twitterURL) {
		TwitterURL = twitterURL;
	}

	public String getGoogleURL() {
		return GoogleURL;
	}

	public void setGoogleURL(String googleURL) {
		GoogleURL = googleURL;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getModifyOn() {
		return ModifyOn;
	}

	public void setModifyOn(String modifyOn) {
		ModifyOn = modifyOn;
	}

	public String getStateName() {
		return StateName;
	}

	public void setStateName(String stateName) {
		StateName = stateName;
	}
}
