package com.bailcompany.model;

import java.io.Serializable;

public class AgentBidingModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7839695487929115041L;
	String RequestId;
	String AgentId;
	String AgentName;
	String AgentImage;
	String Amount;
	String Description;
	String AcceptedStatus;

	public String getAgentImage() {
		return AgentImage;
	}

	public void setAgentImage(String agentImage) {
		AgentImage = agentImage;
	}

	public String getAgentName() {
		return AgentName;
	}

	public void setAgentName(String agentName) {
		AgentName = agentName;
	}

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getAgentId() {
		return AgentId;
	}

	public void setAgentId(String agentId) {
		AgentId = agentId;
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
