package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class MyJinTiaoBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String amount;//
	private String billTime;//
	private String payOffTime;//
	private String periods;// 期数
	private String status;// 状态名称
	private String billSn;// 账单编号

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBillTime() {
		return billTime;
	}

	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}

	public String getPayOffTime() {
		return payOffTime;
	}

	public void setPayOffTime(String payOffTime) {
		this.payOffTime = payOffTime;
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

	public String getBillSn() {
		return billSn;
	}

	public void setBillSn(String billSn) {
		this.billSn = billSn;
	}

}
