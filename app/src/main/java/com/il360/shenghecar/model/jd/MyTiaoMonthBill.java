package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class MyTiaoMonthBill implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billAmt;//账单总额
	private String billType;//1 白条账单 2 金条账单
	private String billDay;//出账日期
	private String billRemainAmt;//剩余待还
	private String billLimitTime;//最后还款日期或结清时间
	private String periods;//分期数
	private String status;//1 已结清 2 待还款

	public String getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillDay() {
		return billDay;
	}

	public void setBillDay(String billDay) {
		this.billDay = billDay;
	}

	public String getBillRemainAmt() {
		return billRemainAmt;
	}

	public void setBillRemainAmt(String billRemainAmt) {
		this.billRemainAmt = billRemainAmt;
	}

	public String getBillLimitTime() {
		return billLimitTime;
	}

	public void setBillLimitTime(String billLimitTime) {
		this.billLimitTime = billLimitTime;
	}

	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
