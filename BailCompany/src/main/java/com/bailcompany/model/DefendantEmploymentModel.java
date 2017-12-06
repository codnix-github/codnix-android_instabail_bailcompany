package com.bailcompany.model;

import java.io.Serializable;

public class DefendantEmploymentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839695487929115041L;
	private String Id;
	private String DefId;
	private String Employer;
	private String Occupation;
	private String Address;
	private String City;
	private String State;
	private String Zip;
	private String Telephone;
	private String Supervisor;
	private String Duration;
	private String Status;
	private String ModifyOn;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getDefId() {
		return DefId;
	}

	public void setDefId(String defId) {
		DefId = defId;
	}

	public String getEmployer() {
		return Employer;
	}

	public void setEmployer(String employer) {
		Employer = employer;
	}

	public String getOccupation() {
		return Occupation;
	}

	public void setOccupation(String occupation) {
		Occupation = occupation;
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

	public String getSupervisor() {
		return Supervisor;
	}

	public void setSupervisor(String supervisor) {
		Supervisor = supervisor;
	}

	public String getDuration() {
		return Duration;
	}

	public void setDuration(String duration) {
		Duration = duration;
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
}
