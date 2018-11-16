package com.ubtechinc.goldenpig.comm.entity;

public class PairPig {

    private int pairUserId;
    private String serialNumber;
    private String pairSerialNumber;
    private int userId;

    public int getPairUserId() {
        return pairUserId;
    }

    public void setPairUserId(int pairUserId) {
        this.pairUserId = pairUserId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPairSerialNumber() {
        return pairSerialNumber;
    }

    public void setPairSerialNumber(String pairSerialNumber) {
        this.pairSerialNumber = pairSerialNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PairPig{" +
                "pairUserId=" + pairUserId +
                ", serialNumber='" + serialNumber + '\'' +
                ", pairSerialNumber='" + pairSerialNumber + '\'' +
                ", userId=" + userId +
                '}';
    }
}
