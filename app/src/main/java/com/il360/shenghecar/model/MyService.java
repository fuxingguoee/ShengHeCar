package com.il360.shenghecar.model;

/**
 * Created by Administrator on 2018/11/10 0010.
 */

import java.io.Serializable;

/**
 * Created by Administrator on 2018/10/8 0008.
 */

public class MyService implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String fcustid;
    private String bankNo;
    private String fIdCard;
    private String bankName;
    private String userName;
    private String loanPrice;
    private String carInfo;
    private String monthPrice;
    private String loanTime;
    private String qiShu;
    private String returnNo;
    private String payFee;



    public String getPayFee() {
        return payFee;
    }

    public void setPayFee(String payFee) {
        this.payFee = payFee;
    }

    public String getFcustid() {
        return fcustid;
    }

    public void setFcustid(String fcustid) {
        this.fcustid = fcustid;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }
    public String getfIdCard() {
        return fIdCard;
    }
    public void setfIdCard(String fIdCard) {
        this.fIdCard = fIdCard;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }

    public String getLoanPrice() {
        return loanPrice;
    }

    public void setLoanPrice(String loanPrice) {
        this.loanPrice = loanPrice;
    }

    public String getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(String monthPrice) {
        this.monthPrice = monthPrice;
    }

    public String getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(String loanTime) {
        this.loanTime = loanTime;
    }

    public String getQiShu() {
        return qiShu;
    }

    public void setQiShu(String qiShu) {
        this.qiShu = qiShu;
    }
    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(String returnno) {
        this.returnNo = returnNo;
    }
}

