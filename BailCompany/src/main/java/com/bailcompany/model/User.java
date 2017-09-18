package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1331546984460233368L;
	private String CVV;
	private String CardExpiry;
	private String CardNumber;
	private String CardType;
	private String packageId;
	private String addonOneEnabled;
	private String addonTwoEnabled;
	private String totalPaymentForLastSubscription;
	private String subscriptionStartTime;
	private String subscriptionEndTime;
	private String CompanyName;
	private String CompanyId;
	private String NameOnCard;
	private String ZipCode;
	private String address;
	private String city;
	private String country;
	private String email;
	private String licenseno;
	private String name;
	private String fname;
	private String lname;
	private String password;
	private String phone;
	private String photo;
	private String state;
	private String username;
	private String rolename;
	private String ownername;
	private String usercode;
	private String licenseExpi;
	private String lisencestate;
	private String CompanyApprovalStatus;
	private String CanSeeAgentState;
	private String CanSeeAgentInsurance;
	private String APT;
	private String BusinessPhone;
	private String DeviceId;
	private String tempAccessCode;
	private String packageExpired;
	private ArrayList<String> insurance, insurancesName, statesName, states;

	public String getPackageExpired() {
		return packageExpired;
	}

	public void setPackageExpired(String packageExpired) {
		this.packageExpired = packageExpired;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getAddonOneEnabled() {
		return addonOneEnabled;
	}

	public void setAddonOneEnabled(String addonOneEnabled) {
		this.addonOneEnabled = addonOneEnabled;
	}

	public String getAddonTwoEnabled() {
		return addonTwoEnabled;
	}

	public void setAddonTwoEnabled(String addonTwoEnabled) {
		this.addonTwoEnabled = addonTwoEnabled;
	}

	public String getTotalPaymentForLastSubscription() {
		return totalPaymentForLastSubscription;
	}

	public void setTotalPaymentForLastSubscription(
			String totalPaymentForLastSubscription) {
		this.totalPaymentForLastSubscription = totalPaymentForLastSubscription;
	}

	public String getSubscriptionStartTime() {
		return subscriptionStartTime;
	}

	public void setSubscriptionStartTime(String subscriptionStartTime) {
		this.subscriptionStartTime = subscriptionStartTime;
	}

	public String getSubscriptionEndTime() {
		return subscriptionEndTime;
	}

	public void setSubscriptionEndTime(String subscriptionEndTime) {
		this.subscriptionEndTime = subscriptionEndTime;
	}

	public String getTempAccessCode() {
		return tempAccessCode;
	}

	public void setTempAccessCode(String tempAccessCode) {
		this.tempAccessCode = tempAccessCode;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getLicenseExpi() {
		return licenseExpi;
	}

	public void setLicenseExpi(String licenseExpi) {
		this.licenseExpi = licenseExpi;
	}

	public String getLisencestate() {
		return lisencestate;
	}

	public void setLisencestate(String lisencestate) {
		this.lisencestate = lisencestate;
	}

	public String getCompanyApprovalStatus() {
		return CompanyApprovalStatus;
	}

	public void setCompanyApprovalStatus(String companyApprovalStatus) {
		CompanyApprovalStatus = companyApprovalStatus;
	}

	public String getCanSeeAgentState() {
		return CanSeeAgentState;
	}

	public void setCanSeeAgentState(String canSeeAgentState) {
		CanSeeAgentState = canSeeAgentState;
	}

	public String getCanSeeAgentInsurance() {
		return CanSeeAgentInsurance;
	}

	public void setCanSeeAgentInsurance(String canSeeAgentInsurance) {
		CanSeeAgentInsurance = canSeeAgentInsurance;
	}

	public String getAPT() {
		return APT;
	}

	public void setAPT(String aPT) {
		APT = aPT;
	}

	public String getBusinessPhone() {
		return BusinessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		BusinessPhone = businessPhone;
	}

	public String getDeviceId() {
		return DeviceId;
	}

	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}

	public ArrayList<String> getInsurancesName() {
		return insurancesName;
	}

	public void setInsurancesName(ArrayList<String> insurancesName) {
		this.insurancesName = insurancesName;
	}

	public ArrayList<String> getStatesName() {
		Log.e("get statesName", "statesName=" + statesName);
		return statesName;
	}

	public void setStatesName(ArrayList<String> statesName) {
		Log.e("set statesName", "statesName=" + statesName);
		this.statesName = statesName;
	}

	public ArrayList<String> getStates() {
		return states;
	}

	public void setStates(ArrayList<String> states) {
		this.states = states;
	}

	public ArrayList<String> getInsurance() {
		return insurance;
	}

	public void setInsurance(ArrayList<String> insurance) {
		this.insurance = insurance;
	}

	public String getCVV() {
		return CVV;
	}

	public void setCVV(String cVV) {
		CVV = cVV;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCardExpiry() {
		return CardExpiry;
	}

	public String getCardNumber() {
		return CardNumber;
	}

	public String getCardType() {
		return CardType;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public String getNameOnCard() {
		return NameOnCard;
	}

	public String getZipCode() {
		return ZipCode;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getEmail() {
		return email;
	}

	public String getLicenseno() {
		return licenseno;
	}

	public String getName() {
		return name;
	}

	public String getFName() {
		return fname;
	}
	public String getLName() {
		return lname;
	}
	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public String getPhoto() {
		return photo;
	}

	public String getState() {

		return state;
	}

	public void setCardExpiry(String cardExpiry) {
		CardExpiry = cardExpiry;
	}

	public void setCardNumber(String cardNumber) {
		CardNumber = cardNumber;
	}

	public void setCardType(String cardType) {
		CardType = cardType;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public void setNameOnCard(String nameOnCard) {
		NameOnCard = nameOnCard;
	}

	public void setZipCode(String zipCode) {
		ZipCode = zipCode;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLicenseno(String licenseno) {
		this.licenseno = licenseno;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setFName(String fname) {
		this.fname = fname;
	}
	public void setLName(String lname) {
		this.lname = lname;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public void setState(ArrayList<String> state) {
		Log.e("@@@@@@state", "state=" + state);
		this.states = state;
	}

	public String getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(String companyId) {
		CompanyId = companyId;
	}

}
