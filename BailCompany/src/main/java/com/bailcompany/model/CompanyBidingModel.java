package com.bailcompany.model;

import java.io.Serializable;

public class CompanyBidingModel implements Serializable {
	private static final long serialVersionUID = 7839695487929115041L;
	String RequestId;
	String CompanyId;
	String CompanyName;
	String CompanyImage;
	String Amount;
	String Description;
	String AcceptedStatus;
	String bidingId;

	public String getBidingId() {
		return bidingId;
	}

	public void setBidingId(String bidingId) {
		this.bidingId = bidingId;
	}

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
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

	public String getCompanyImage() {
		return CompanyImage;
	}

	public void setCompanyImage(String companyImage) {
		CompanyImage = companyImage;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getAcceptedStatus() {
		return AcceptedStatus;
	}

	public void setAcceptedStatus(String acceptedStatus) {
		AcceptedStatus = acceptedStatus;
	}

}
