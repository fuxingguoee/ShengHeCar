package com.il360.shenghecar.model;

import java.io.Serializable;

public class Car implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private Integer userId;
    private Integer exchangeId;
    private String name;
    private String phone;
    private String type;//1新车2二手车
    private String carModel;
    private String time;
    private String kilometers;
    private String location;
    private String create_time;
    private Integer status;//1已处理0新建
    private Integer ext1;//1已处理0新建
    private String ext2;//1已处理0新建
    private String frontPic;
    private String backPic;
    private String leftPic;
    private String rightPic;
    private String platePic;
    private String framePic;
    private String kilometersPic;
    private String licenseFrontPic;
    private String licenseBackPic;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(Integer exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKilometers() {
        return kilometers;
    }

    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExt1() {
        return ext1;
    }

    public void setExt1(Integer ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getFrontPic() {
        return frontPic;
    }

    public void setFrontPic(String frontPic) {
        this.frontPic = frontPic;
    }

    public String getBackPic() {
        return backPic;
    }

    public void setBackPic(String backPic) {
        this.backPic = backPic;
    }

    public String getLeftPic() {
        return leftPic;
    }

    public void setLeftPic(String leftPic) {
        this.leftPic = leftPic;
    }

    public String getRightPic() {
        return rightPic;
    }

    public void setRightPic(String rightPic) {
        this.rightPic = rightPic;
    }

    public String getPlatePic() {
        return platePic;
    }

    public void setPlatePic(String platePic) {
        this.platePic = platePic;
    }

    public String getFramePic() {
        return framePic;
    }

    public void setFramePic(String framePic) {
        this.framePic = framePic;
    }

    public String getKilometersPic() {
        return kilometersPic;
    }

    public void setKilometersPic(String kilometersPic) {
        this.kilometersPic = kilometersPic;
    }

    public String getLicenseFrontPic() {
        return licenseFrontPic;
    }

    public void setLicenseFrontPic(String licenseFrontPic) {
        this.licenseFrontPic = licenseFrontPic;
    }

    public String getLicenseBackPic() {
        return licenseBackPic;
    }

    public void setLicenseBackPic(String licenseBackPic) {
        this.licenseBackPic = licenseBackPic;
    }



}