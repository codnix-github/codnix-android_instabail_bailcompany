package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PackageModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String packageID;
	String packageName;
	String packageDiscription;
	String packageType;
	String packacgeDuration;
	String packagePrice;
	String freeBondRequest;
	String perRequestPrice;
	String supportsAddons;
	String packageStatus;
	String createddAt;
	String updatedAt;
	ArrayList<AddOns> addOns = new ArrayList<AddOns>();

	public String getFreeBondRequest() {
		return freeBondRequest;
	}

	public void setFreeBondRequest(String freeBondRequest) {
		this.freeBondRequest = freeBondRequest;
	}

	public String getPackageID() {
		return packageID;
	}

	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageDiscription() {
		return packageDiscription;
	}

	public void setPackageDiscription(String packageDiscription) {
		this.packageDiscription = packageDiscription;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getPackacgeDuration() {
		return packacgeDuration;
	}

	public void setPackacgeDuration(String packacgeDuration) {
		this.packacgeDuration = packacgeDuration;
	}

	public String getPackagePrice() {
		return packagePrice;
	}

	public void setPackagePrice(String packagePrice) {
		this.packagePrice = packagePrice;
	}

	public String getPerRequestPrice() {
		return perRequestPrice;
	}

	public void setPerRequestPrice(String perRequestPrice) {
		this.perRequestPrice = perRequestPrice;
	}

	public String getSupportsAddons() {
		return supportsAddons;
	}

	public void setSupportsAddons(String supportsAddons) {
		this.supportsAddons = supportsAddons;
	}

	public String getPackageStatus() {
		return packageStatus;
	}

	public void setPackageStatus(String packageStatus) {
		this.packageStatus = packageStatus;
	}

	public String getCreateddAt() {
		return createddAt;
	}

	public void setCreateddAt(String createddAt) {
		this.createddAt = createddAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ArrayList<AddOns> getAddOns() {
		return addOns;
	}

	public void setAddOns(ArrayList<AddOns> addOns) {
		this.addOns = addOns;
	}

}
