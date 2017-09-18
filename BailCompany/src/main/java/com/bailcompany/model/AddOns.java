package com.bailcompany.model;

public class AddOns {
	String addonId;
	String addonTitle;
	String addonDescription;
	String addonPrice;

	public String getAddonId() {
		return addonId;
	}

	public void setAddonId(String addonId) {
		this.addonId = addonId;
	}

	public String getAddonTitle() {
		return addonTitle;
	}

	public void setAddonTitle(String addonTitle) {
		this.addonTitle = addonTitle;
	}

	public String getAddonDescription() {
		return addonDescription;
	}

	public void setAddonDescription(String addonDescription) {
		this.addonDescription = addonDescription;
	}

	public String getAddonPrice() {
		return addonPrice;
	}

	public void setAddonPrice(String addonPrice) {
		this.addonPrice = addonPrice;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	String updatedAt;
	String createdAt;
}
