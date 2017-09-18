package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FugitiveRequestModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String DefendantName, DefDOB, DefSSN, DefBookingNumber, LocationLongitude,
			LocationLatitude, InstructionForAgent, CreatedDate,
			DefendantAddress, SentRequestTime, Location, Id, NumberIndemnitors,
			CompanyId, AgentId, AgentName, AgentImage, isComplete, isCancel,
			isAbort, DefendantImage, AmountorPercentage,
			AmountCompanyWillToPay, DayLeftBeforeBailSeized, zipCode,
			RecoveryStateID, RecoveryStateName, homeNumber, cellNumber,
			townShip, forefeiture, Read;

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	String IsAccept;

	public String isIsAccept() {
		return IsAccept;
	}

	public void setIsAccept(String isAccept) {
		IsAccept = isAccept;
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

	String isFemale;

	public String getHomeNumber() {
		return homeNumber;
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}

	public String getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(String cellNumber) {
		this.cellNumber = cellNumber;
	}

	public String getTownShip() {
		return townShip;
	}

	public void setTownShip(String townShip) {
		this.townShip = townShip;
	}

	public String getForefeiture() {
		return forefeiture;
	}

	public void setForefeiture(String forefeiture) {
		this.forefeiture = forefeiture;
	}

	public String getIsFemale() {
		return isFemale;
	}

	public void setIsFemale(String isFemale) {
		this.isFemale = isFemale;
	}

	public double getAmountToCollect() {
		return AmountToCollect;
	}

	public void setAmountToCollect(double amountToCollect) {
		AmountToCollect = amountToCollect;
	}

	public String getDefendantAddress() {
		return DefendantAddress;
	}

	public void setDefendantAddress(String defendantAddress) {
		DefendantAddress = defendantAddress;
	}

	public String getAmountCompanyWillToPay() {
		return AmountCompanyWillToPay;
	}

	public void setAmountCompanyWillToPay(String amountCompanyWillToPay) {
		AmountCompanyWillToPay = amountCompanyWillToPay;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	boolean NeedCourtFee, NeedBailSource, IsCallAgency, NeedPaperWork,
			NeedIndemnitorPaperwork, NeedDefendantPaperwork;
	double AmountToCollect;
	ArrayList<WarrantModel> WarrantList;
	ArrayList<IndemnitorModel> IndemnitorsList;

	public String getDefendantName() {
		return DefendantName;
	}

	public void setDefendantName(String defendantName) {
		DefendantName = defendantName;
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

	public String getSentRequestTime() {
		return SentRequestTime;
	}

	public void setSentRequestTime(String sentRequestTime) {
		SentRequestTime = sentRequestTime;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getNumberIndemnitors() {
		return NumberIndemnitors;
	}

	public void setNumberIndemnitors(String numberIndemnitors) {
		NumberIndemnitors = numberIndemnitors;
	}

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

	public String getAgentId() {
		return AgentId;
	}

	public void setAgentId(String agentId) {
		AgentId = agentId;
	}

	public String getAgentName() {
		return AgentName;
	}

	public void setAgentName(String agentName) {
		AgentName = agentName;
	}

	public String getAgentImage() {
		return AgentImage;
	}

	public void setAgentImage(String agentImage) {
		AgentImage = agentImage;
	}

	public String getDefendantImage() {
		return DefendantImage;
	}

	public void setDefendantImage(String defendantImage) {
		DefendantImage = defendantImage;
	}

	public String getAmountorPercentage() {
		return AmountorPercentage;
	}

	public void setAmountorPercentage(String amountorPercentage) {
		AmountorPercentage = amountorPercentage;
	}

	public String getDayLeftBeforeBailSeized() {
		return DayLeftBeforeBailSeized;
	}

	public void setDayLeftBeforeBailSeized(String dayLeftBeforeBailSeized) {
		DayLeftBeforeBailSeized = dayLeftBeforeBailSeized;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getRecoveryStateID() {
		return RecoveryStateID;
	}

	public void setRecoveryStateID(String recoveryStateID) {
		RecoveryStateID = recoveryStateID;
	}

	public String getRecoveryStateName() {
		return RecoveryStateName;
	}

	public void setRecoveryStateName(String recoveryStateName) {
		RecoveryStateName = recoveryStateName;
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

	public ArrayList<WarrantModel> getWarrantList() {
		return WarrantList;
	}

	public void setWarrantList(ArrayList<WarrantModel> warrantList) {
		WarrantList = warrantList;
	}

	public ArrayList<IndemnitorModel> getIndemnitorsList() {
		return IndemnitorsList;
	}

	public void setIndemnitorsList(ArrayList<IndemnitorModel> indemnitorsList) {
		IndemnitorsList = indemnitorsList;
	}
}
