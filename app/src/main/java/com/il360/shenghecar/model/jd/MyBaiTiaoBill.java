package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class MyBaiTiaoBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billAmt;//
	private String billDay;//
	private String billRemainAmt;//
	private String billLimitTime;//
	private String status;//
	private String jdBillId;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJdBillId() {
		return jdBillId;
	}

	public void setJdBillId(String jdBillId) {
		this.jdBillId = jdBillId;
	}

	public String getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}

}
