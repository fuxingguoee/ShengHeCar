package com.il360.shenghecar.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/11/28 0028.
 */

public class Company implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private Integer branchId;//分公司id
    private String branchName;//分公司名
    private String branchPhone;//联系方式
    private String operator;//归属业务员
    private String operatorPhone;//业务员联系号码
    private String createTime;
    private Integer status;//1已处理0新建
    private String ext1;
    private String ext2;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchPhone() {
        return branchPhone;
    }

    public void setBranchPhone(String branchPhone) {
        this.branchPhone = branchPhone;
    }

    public String getOperatorPhone() {
        return operatorPhone;
    }

    public void setOperatorPhone(String operatorPhone) {
        this.operatorPhone = operatorPhone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

}
