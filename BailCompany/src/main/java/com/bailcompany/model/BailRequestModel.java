package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BailRequestModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String DefendantName,DefendantFName,DefendantLName, DefDOB, DefSSN, DefBookingNumber, LocationLongitude,
			LocationLatitude, PaymentPlan, InstructionForAgent, CreatedDate,
			SentRequestTime, Location, CompanyImage, CompanyName, AgentId,
			AgentImage, AgentName, isComplete, isCancel, isAbort,
			CompanyOfferToPay, NumberIndemnitors, Read, bondInsuranceId,
			bondInsuranceName, IsAccept, ammountForCommission,comments;

	int Id, CompanyId, AgentRequestId;
	boolean NeedCourtFee, NeedBailSource, IsCallAgency, NeedPaperWork,
			NeedIndemnitorPaperwork, NeedDefendantPaperwork,
			PaymentAlreadyReceived;
	String AmountToCollect;
	ArrayList<WarrantModel> WarrantList;
	ArrayList<CourtDateModel> CourtDates;
	BondDocuments BondDocuments;
	ArrayList<IndemnitorModel> IndemnitorsList;
	ArrayList<InsuranceModel> InsuranceList;
	String SenderCompanyId, SenderCompanyName, SenderCompanyImage,
			BondRequestCompany;
	String selfAssigned;
	String amountReceived, powerNo;

	public BailRequestModel() {
		this.isComplete = "0";
		this.isCancel = "0";
		this.isAbort = "0";
		this.IsAccept = "0";
	}

	public String getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(String amountReceived) {
		this.amountReceived = amountReceived;
	}

	public String getPowerNo() {
		return powerNo;
	}

	public void setPowerNo(String powerNo) {
		this.powerNo = powerNo;
	}

	public String getSelfAssigned() {
		return selfAssigned;
	}

	public void setSelfAssigned(String selfAssigned) {
		this.selfAssigned = selfAssigned;
	}

	public String getAmmountForCommission() {
		return ammountForCommission;
	}

	public void setAmmountForCommission(String ammountForCommission) {
		this.ammountForCommission = ammountForCommission;
	}

	public String getBondInsuranceId() {
		return bondInsuranceId;
	}

	public void setBondInsuranceId(String bondInsuranceId) {
		this.bondInsuranceId = bondInsuranceId;
	}

	public String getBondInsuranceName() {
		return bondInsuranceName;
	}

	public void setBondInsuranceName(String bondInsuranceName) {
		this.bondInsuranceName = bondInsuranceName;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

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

	public String getCompanyOfferToPay() {
		return CompanyOfferToPay;
	}

	public void setCompanyOfferToPay(String CompanyOfferToPay) {
		this.CompanyOfferToPay = CompanyOfferToPay;
	}

	public String getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(String isComplete) {
		this.isComplete = isComplete;
	}

	public String getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(String isCancel) {
		this.isCancel = isCancel;
	}

	public String getIsAbort() {
		return isAbort;
	}

	public void setIsAbort(String isAbort) {
		this.isAbort = isAbort;
	}

	public String getBondRequestCompany() {
		return BondRequestCompany;
	}

	public void setBondRequestCompany(String bondRequestCompany) {
		BondRequestCompany = bondRequestCompany;
	}

	public String getSenderCompanyId() {
		return SenderCompanyId;
	}

	public void setSenderCompanyId(String senderCompanyId) {
		SenderCompanyId = senderCompanyId;
	}

	public String getSenderCompanyName() {
		return SenderCompanyName;
	}

	public void setSenderCompanyName(String senderCompanyName) {
		SenderCompanyName = senderCompanyName;
	}

	public String getSenderCompanyImage() {
		return SenderCompanyImage;
	}

	public void setSenderCompanyImage(String senderCompanyImage) {
		SenderCompanyImage = senderCompanyImage;
	}

	public ArrayList<InsuranceModel> getInsuranceList() {
		return InsuranceList;
	}

	public void setInsuranceList(ArrayList<InsuranceModel> insuranceList) {
		InsuranceList = insuranceList;
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

	public ArrayList<WarrantModel> getWarrantList() {
		return WarrantList;
	}

	public void setWarrantList(ArrayList<WarrantModel> warrantList) {
		WarrantList = warrantList;
	}

	public ArrayList<CourtDateModel> getCourtDates() {
		return CourtDates;
	}

	public void setCourtDates(ArrayList<CourtDateModel> courtDates) {
		CourtDates = courtDates;
	}

	public ArrayList<IndemnitorModel> getIndemnitorsList() {
		return IndemnitorsList;
	}

	public void setIndemnitorsList(ArrayList<IndemnitorModel> indemnitorsList) {
		IndemnitorsList = indemnitorsList;
	}

	public int getAgentRequestId() {
		return AgentRequestId;
	}

	public void setAgentRequestId(int agentRequestId) {
		AgentRequestId = agentRequestId;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getDefendantName() {
		return DefendantName;
	}

	public void setDefendantName(String defendantName) {
		DefendantName = defendantName;
	}
	public String getDefendantFName() {
		return DefendantFName;
	}
	public void setDefendantFName(String defendantFName) {
		DefendantFName = defendantFName;
	}
	public String getDefendantLName() {
		return DefendantLName;
	}

	public void setDefendantLName(String defendantLName) {
		DefendantLName = defendantLName;
	}
	public String getDefDOB() {
		return DefDOB;
	}

	public void setDefDOB(String defDOB) {
		DefDOB = defDOB;
	}

	public String getDefSSN() {
		return DefSSN;
	}

	public void setDefSSN(String defSSN) {
		DefSSN = defSSN;
	}

	public String getDefBookingNumber() {
		return DefBookingNumber;
	}

	public void setDefBookingNumber(String defBookingNumber) {
		DefBookingNumber = defBookingNumber;
	}

	public String getLocationLongitude() {
		return LocationLongitude;
	}

	public void setLocationLongitude(String locationLongitude) {
		LocationLongitude = locationLongitude;
	}

	public String getLocationLatitude() {
		return LocationLatitude;
	}

	public void setLocationLatitude(String locationLatitude) {
		LocationLatitude = locationLatitude;
	}

	public String getPaymentPlan() {
		return PaymentPlan;
	}

	public void setPaymentPlan(String paymentPlan) {
		PaymentPlan = paymentPlan;
	}

	public String getInstructionForAgent() {
		return InstructionForAgent;
	}

	public void setInstructionForAgent(String instructionForAgent) {
		InstructionForAgent = instructionForAgent;
	}

	public String getCreatedDate() {
		return CreatedDate;
	}

	public void setCreatedDate(String createdDate) {
		CreatedDate = createdDate;
	}

	//
	public String getSentRequestTime() {

		return SentRequestTime;
	}

	public void setSentRequestTime(String requestTime) {
		SentRequestTime = requestTime;

	}

	//

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getNumberIndemnitors() {
		return NumberIndemnitors;
	}

	public void setNumberIndemnitors(String numberIndemnitors) {
		NumberIndemnitors = numberIndemnitors;
	}

	public int getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(int companyId) {
		CompanyId = companyId;
	}

	public String getAgentId() {
		return AgentId;
	}

	public void setAgentId(String agentId) {
		AgentId = agentId;
	}

	public boolean isNeedCourtFee() {
		return NeedCourtFee;
	}

	public void setNeedCourtFee(boolean needCourtFee) {
		NeedCourtFee = needCourtFee;
	}

	public boolean isNeedBailSource() {
		return NeedBailSource;
	}

	public void setNeedBailSource(boolean needBailSource) {
		NeedBailSource = needBailSource;
	}

	public boolean isIsCallAgency() {
		return IsCallAgency;
	}

	public void setIsCallAgency(boolean isCallAgency) {
		IsCallAgency = isCallAgency;
	}

	public boolean isNeedPaperWork() {
		return NeedPaperWork;
	}

	public void setNeedPaperWork(boolean needPaperWork) {
		NeedPaperWork = needPaperWork;
	}

	public boolean isNeedIndemnitorPaperwork() {
		return NeedIndemnitorPaperwork;
	}

	public void setNeedIndemnitorPaperwork(boolean needIndemnitorPaperwork) {
		NeedIndemnitorPaperwork = needIndemnitorPaperwork;
	}

	public boolean isNeedDefendantPaperwork() {
		return NeedDefendantPaperwork;
	}

	public void setNeedDefendantPaperwork(boolean needDefendantPaperwork) {
		NeedDefendantPaperwork = needDefendantPaperwork;
	}

	public boolean isPaymentAlreadyReceived() {
		return PaymentAlreadyReceived;
	}

	public void setPaymentAlreadyReceived(boolean paymentAlreadyReceived) {
		PaymentAlreadyReceived = paymentAlreadyReceived;
	}

	public String isIsAccept() {
		return IsAccept;
	}

	public void setIsAccept(String isAccept) {
		IsAccept = isAccept;
	}

	public String getAmountToCollect() {
		return AmountToCollect;
	}

	public void setAmountToCollect(String amountToCollect) {
		AmountToCollect = amountToCollect;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public com.bailcompany.model.BondDocuments getBondDocuments() {
		return BondDocuments;
	}

	public void setBondDocuments(com.bailcompany.model.BondDocuments bondDocuments) {
		BondDocuments = bondDocuments;
	}
}
