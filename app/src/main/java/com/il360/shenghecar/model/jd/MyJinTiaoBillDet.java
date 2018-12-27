package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class MyJinTiaoBillDet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String periods;// 期数
	private String billTime;//
	private String amount;//
	private String principalAmt;//
	private String interestAmt;//
	private String status;// 状态名称

	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public String getBillTime() {
		return billTime;
	}

	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPrincipalAmt() {
		return principalAmt;
	}

	public void setPrincipalAmt(String principalAmt) {
		this.principalAmt = principalAmt;
	}

	public String getInterestAmt() {
		return interestAmt;
	}

	public void setInterestAmt(String interestAmt) {
		this.interestAmt = interestAmt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
