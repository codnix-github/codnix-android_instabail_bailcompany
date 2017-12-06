package com.bailcompany.model;

import java.io.Serializable;

public class DefendantVehicleModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7839695487929115041L;
	private String Id;
	private String DefId;
	private String Year;
	private String Make;
	private String Model;
	private String Color;
	private String State;
	private String Registration;
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

	public String getYear() {
		return Year;
	}

	public void setYear(String year) {
		Year = year;
	}

	public String getMake() {
		return Make;
	}

	public void setMake(String make) {
		Make = make;
	}

	public String getModel() {
		return Model;
	}

	public void setModel(String model) {
		Model = model;
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		Color = color;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getRegistration() {
		return Registration;
	}

	public void setRegistration(String registration) {
		Registration = registration;
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
