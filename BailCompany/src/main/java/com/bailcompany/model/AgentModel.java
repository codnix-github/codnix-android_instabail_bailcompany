package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class AgentModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839695487929115041L;
	private String username;
	private String password, rolename, createddate, NameOnCard, CardType,
			companyname, APT, ZIP, DeviceId;
	private String agentName;
	private String UserId;
	private String IsOnline;
	private String agentId;
	private String photoUrl;
	private String address;
	private String city;
	private String state;
	private String country;
	private String phone;
	private String email;
	private String licenseNo;
	private String licenseExpire;
	private String LicenseState;
	private String longitude;
	private String latitude;
	private String agentApprovalStatus;
	private String cardNumber;
	private String cardExpiry;
	private String reqStatus;
	private String LocationLongitude;
	private String LocationLatitude;
	private String instabailAgent;

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

	boolean IsAccept;

	private String UserCode;

	private ArrayList<String> insuranceList, states, insurancesName,
			statesName;

	public boolean isIsAccept() {
		return IsAccept;
	}

	public void setIsAccept(boolean isAccept) {
		IsAccept = isAccept;
	}

	public ArrayList<String> getInsurancesName() {
		return insurancesName;
	}

	public void setInsurancesName(ArrayList<String> insurancesName) {
		this.insurancesName = insurancesName;
	}

	public ArrayList<String> getStatesName() {
		return statesName;
	}

	public void setStatesName(ArrayList<String> statesName) {
		this.statesName = statesName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getCreateddate() {
		return createddate;
	}

	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}

	public String getNameOnCard() {
		return NameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		NameOnCard = nameOnCard;
	}

	public String getCardType() {
		return CardType;
	}

	public void setCardType(String cardType) {
		CardType = cardType;
	}

	public ArrayList<String> getStates() {
		return states;
	}

	public void setStates(ArrayList<String> states) {
		this.states = states;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getAPT() {
		return APT;
	}

	public void setAPT(String aPT) {
		APT = aPT;
	}

	public String getZIP() {
		return ZIP;
	}

	public void setZIP(String zIP) {
		ZIP = zIP;
	}

	public String getDeviceId() {
		return DeviceId;
	}

	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}

	public String getUserCode() {
		return UserCode;
	}

	public void setUserCode(String userCode) {
		UserCode = userCode;
	}

	public String getUsername() {
		return username;
	}

	public String getUserId() {
		return UserId;
	}

	public String getIsOnline() {
		return IsOnline;
	}

	public String getAgentId() {
		return agentId;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public String getLicenseExpire() {
		return licenseExpire;
	}

	public String getLicenseState() {
		Log.e("LicenseState", "=" + LicenseState);
		return LicenseState;
	}

	public String getLongitude() {

		return longitude;
	}

	public String getLatitude() {

		return latitude;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {

		this.agentName = agentName;
		Log.e("agentName", "=" + agentName);
	}

	public String getAgentApprovalStatus() {
		return agentApprovalStatus;
	}

	public void setAgentApprovalStatus(String agentApprovalStatus) {
		this.agentApprovalStatus = agentApprovalStatus;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardExpiry() {
		return cardExpiry;
	}

	public void setCardExpiry(String cardExpiry) {
		this.cardExpiry = cardExpiry;
	}

	public ArrayList<String> getInsuranceList() {
		return insuranceList;
	}

	public void setInsuranceList(ArrayList<String> insuranceList) {
		this.insuranceList = insuranceList;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public void setIsOnline(String isOnline) {
		IsOnline = isOnline;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public void setLicenseExpire(String licenseExpire) {
		this.licenseExpire = licenseExpire;
	}

	public void setLicenseState(String licenseState) {
		LicenseState = licenseState;
		Log.e("LicenseState", "=" + LicenseState);
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getReqStatus() {
		return reqStatus;
	}

	public void setReqStatus(String reqStatus) {
		this.reqStatus = reqStatus;
	}

	public String getInstabailAgent() {
		return instabailAgent;
	}

	public void setInstabailAgents(String instabailAgent) {
		this.instabailAgent = instabailAgent;
	}
}
