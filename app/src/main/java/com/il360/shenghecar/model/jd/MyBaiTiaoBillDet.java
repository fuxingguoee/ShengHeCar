package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class MyBaiTiaoBillDet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billTitle;//
	private String billTime;//
	private String amount;//
	private String orderId;//

	public String getBillTitle() {
		return billTitle;
	}

	public void setBillTitle(String billTitle) {
		this.billTitle = billTitle;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
