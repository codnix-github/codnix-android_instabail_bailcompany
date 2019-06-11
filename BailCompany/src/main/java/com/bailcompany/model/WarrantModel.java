package com.bailcompany.model;

import java.io.Serializable;
import java.util.ArrayList;

public class WarrantModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int id;
    String Township, Amount, case_no, PowerNo, Notes, AmountReceived;
    String CourtDate, Status,LatestCourtDate;
    ArrayList<CourtDateModel> courtDates;

    public WarrantModel() {
        this.Township = "";
        this.Amount = "";
        this.AmountReceived = "";
        this.case_no = "";
        this.PowerNo = "";
        this.Notes = "";
        this.CourtDate = "";
        this.LatestCourtDate="";
        this.courtDates = null;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAmountReceived() {
        return AmountReceived;
    }

    public void setAmountReceived(String amountReceived) {
        AmountReceived = amountReceived;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTownship() {
        return Township;
    }

    public void setTownship(String township) {
        Township = township;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCase_no() {
        return case_no;
    }

    public void setCase_no(String case_no) {
        this.case_no = case_no;
    }

    public String getPowerNo() {
        return PowerNo;
    }

    public void setPowerNo(String powerNo) {
        PowerNo = powerNo;
    }

    public String getCourtDate() {
        return CourtDate;
    }

    public void setCourtDate(String courtDate) {
        CourtDate = courtDate;
    }

    public String getLatestCourtDate() {
        return LatestCourtDate;
    }

    public void setLatestCourtDate(String latestCourtDate) {
        LatestCourtDate = latestCourtDate;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public ArrayList<CourtDateModel> getCourtDates() {
        return courtDates;
    }

    public void setCourtDates(ArrayList<CourtDateModel> courtDates) {
        this.courtDates = courtDates;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
