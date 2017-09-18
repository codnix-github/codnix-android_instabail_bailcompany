package com.bailcompany.model;

import android.util.Log;

public class GetAnAgentModel {
	
	private double warrentAmount;
	private String charginTown;
	private String indemnitorName;
	private String indemnitorFName;
	private String indemnitorLName;
	private String IndemnitorPhone;
	
	public double getWarrentAmount() {
		Log.e("warrentAmount",""+warrentAmount);
		return warrentAmount;
		
	}
	public void setWarrentAmount(double warrentAmount) {
		Log.e("warrentAmount set",""+warrentAmount);
		this.warrentAmount = warrentAmount;
	}
	public String getCharginTown() {
		Log.e("charginTown",""+charginTown);
		return charginTown;
	
	}
	public void setCharginTown(String charginTown) {
		Log.e("charginTown set",""+charginTown);
		this.charginTown = charginTown;
	}
	public String getIndemnitorName() {
		Log.e("indemnitorName",""+indemnitorName);
		return indemnitorName;
		
	}

	public void setIndemnitorName(String indemnitorName) {
		Log.e("indemnitorName set",""+indemnitorName);
		this.indemnitorName = indemnitorName;
	}

	public String getIndemnitorFName() {
		Log.e("indemnitorName",""+indemnitorFName);
		return indemnitorFName;

	}

	public void setIndemnitorFName(String indemnitorfName) {
		Log.e("indemnitorName set",""+indemnitorFName);
		this.indemnitorFName = indemnitorfName;
	}

	public String getIndemnitorLName() {
		Log.e("indemnitorName",""+indemnitorLName);
		return indemnitorLName;

	}

	public void setIndemnitorLName(String indemnitorlName) {
		Log.e("indemnitorName set",""+indemnitorLName);
		this.indemnitorLName = indemnitorlName;
	}
	public String getIndemnitorPhone() {
		return IndemnitorPhone;
	}
	public void setIndemnitorPhone(String indemnitorPhone) {
		IndemnitorPhone = indemnitorPhone;
	}
	
	

}
