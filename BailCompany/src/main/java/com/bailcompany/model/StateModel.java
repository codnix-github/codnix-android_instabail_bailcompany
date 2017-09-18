package com.bailcompany.model;

import java.io.Serializable;

public class StateModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;
	String Name;
	String ForAgent;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getForAgent() {
		return ForAgent;
	}
	public void setForAgent(String forAgent) {
		ForAgent = forAgent;
	}

}
