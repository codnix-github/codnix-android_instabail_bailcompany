package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BondDocuments implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int id;
    ArrayList<String> defendantPhoto;
    ArrayList<String> cosignerPhoto;
    ArrayList<String> otherDocuments;

    public BondDocuments() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getDefendantPhoto() {
        return defendantPhoto;
    }

    public void setDefendantPhoto(ArrayList<String> defendantPhoto) {
        this.defendantPhoto = defendantPhoto;
    }

    public ArrayList<String> getCosignerPhoto() {
        return cosignerPhoto;
    }

    public void setCosignerPhoto(ArrayList<String> cosignerPhoto) {
        this.cosignerPhoto = cosignerPhoto;
    }

    public ArrayList<String> getOtherDocuments() {
        return otherDocuments;
    }

    public void setOtherDocuments(ArrayList<String> otherDocuments) {
        this.otherDocuments = otherDocuments;
    }
}
