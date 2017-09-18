package com.postingagent.model;

import java.io.Serializable;

import android.util.Log;

public class InsuranceModel2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String InsuranceId, InsuranceName;

	public String getInsuranceId() {
		return InsuranceId;
	}

	public void setInsuranceId(String insuranceId) {
		InsuranceId = insuranceId;
	}

	public String getInsuranceName() {
		Log.e("insurance model","insurancename="+InsuranceName);
		return InsuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		InsuranceName = insuranceName;
		Log.e("insurance model","insurancename="+InsuranceName);
	}

}
