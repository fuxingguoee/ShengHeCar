package com.il360.shenghecar.model;

import java.io.Serializable;

public class Loan implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private Integer loanId;
    private String name;
    private String phone;
    private Integer type;
    private String carModel;
    private String time;
    private String price;
    private String kilometers;
    private String location;
    private String amount;
    private String createTime;
    private Integer status;//1已处理0新建
    private String ext1;
    private String ext2;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public String getCarModel() {
        return carModel;
    }
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLoanId() {
        return loanId;
    }
    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getKilometers() {
        return kilometers;
    }
    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExt1() {
        return ext1;
    }
    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }
    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

}