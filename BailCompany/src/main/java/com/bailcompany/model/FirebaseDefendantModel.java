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
}
