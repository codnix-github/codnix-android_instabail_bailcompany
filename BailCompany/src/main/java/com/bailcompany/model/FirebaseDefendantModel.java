package com.bailcompany.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FirebaseDefendantModel {

    /**
     *
     */
    //private static final long serialVersionUID = 7839695487929115041L;
    private String DefId;
    private String DateTime;
    private String DefName;
    private String Lat;
    private String Lng;
    private String Provider;
    private String TimeZone;
    private String Token;
    private String Battery;
    private String MobileData;
    private String NetworkInfoUpdateTime;
    private String Wifi;
    private String FastConnection;
    private String BatteryUpdateTime;


    public String getDefId() {
        return DefId;
    }

    public void setDefId(String defId) {
        DefId = defId;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getDefName() {
        return DefName;
    }

    public void setDefName(String defName) {
        DefName = defName;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }


    public String getProvider() {
        return Provider;
    }

    public void setProvider(String provider) {
        Provider = provider;
    }

    public String getTimeZone() {
        return TimeZone;
    }

    public void setTimeZone(String timeZone) {
        TimeZone = timeZone;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }

    public String getMobileData() {
        return MobileData;
    }

    public void setMobileData(String mobileData) {
        MobileData = mobileData;
    }

    public String getNetworkInfoUpdateTime() {
        return NetworkInfoUpdateTime;
    }

    public void setNetworkInfoUpdateTime(String networkInfoUpdateTime) {
        NetworkInfoUpdateTime = networkInfoUpdateTime;
    }

    public String getWifi() {
        return Wifi;
    }

    public void setWifi(String wifi) {
        Wifi = wifi;
    }

    public String getFastConnection() {
        return FastConnection;
    }

    public void setFastConnection(String fastConnection) {
        FastConnection = fastConnection;
    }

    public String getBatteryUpdateTime() {
        return BatteryUpdateTime;
    }

    public void setBatteryUpdateTime(String batteryUpdateTime) {
        BatteryUpdateTime = batteryUpdateTime;
    }
}
