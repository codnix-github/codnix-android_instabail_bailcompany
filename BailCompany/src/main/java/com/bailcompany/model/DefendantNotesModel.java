package com.bailcompany.model;

import java.io.Serializable;

public class DefendantNotesModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839695487929115041L;
	private String Id;
	private String DefId;
	private String Note;
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

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
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
