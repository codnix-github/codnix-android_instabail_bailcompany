package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CourtDateModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	int id,warrantId;
	String courtDate;


	public CourtDateModel() {
		this.courtDate = "";

	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWarrantId() {
		return warrantId;
	}

	public void setWarrantId(int warrantId) {
		this.warrantId = warrantId;
	}

	public String getCourtDate() {
		return courtDate;
	}

	public void setCourtDate(String courtDate) {
		this.courtDate = courtDate;
	}
}
