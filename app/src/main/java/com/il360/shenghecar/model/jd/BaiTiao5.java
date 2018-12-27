package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class BaiTiao5 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productName;
	private String orderUrl;
	private String orderAmount;
	private String loanAmountDiscount;
	private String sysCode;
	private String bizCode;
	private String consumerDate;
	private String orderId;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderUrl() {
		return orderUrl;
	}

	public void setOrderUrl(String orderUrl) {
		this.orderUrl = orderUrl;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getLoanAmountDiscount() {
		return loanAmountDiscount;
	}

	public void setLoanAmountDiscount(String loanAmountDiscount) {
		this.loanAmountDiscount = loanAmountDiscount;
	}

	public String getSysCode() {
		return sysCode;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	public String getConsumerDate() {
		return consumerDate;
	}

	public void setConsumerDate(String consumerDate) {
		this.consumerDate = consumerDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
