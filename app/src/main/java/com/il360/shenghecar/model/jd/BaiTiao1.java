package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class BaiTiao1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billId;
	private String billAmt;
	private String payedAmt;
	private String refundAmt;
	private String planedAmt;
	private String restPlanAmt;
	private String status;
	private String billDate;
	private String billLimitDate;
	private String sdpAmt;
	private String minPayAmt;
	private String isCurBill;
	private String isInPayDay;
	private String realOverDays;
	private Result result;

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(String billAmt) {
		this.billAmt = billAmt;
	}

	public String getPayedAmt() {
		return payedAmt;
	}

	public void setPayedAmt(String payedAmt) {
		this.payedAmt = payedAmt;
	}

	public String getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(String refundAmt) {
		this.refundAmt = refundAmt;
	}

	public String getPlanedAmt() {
		return planedAmt;
	}

	public void setPlanedAmt(String planedAmt) {
		this.planedAmt = planedAmt;
	}

	public String getRestPlanAmt() {
		return restPlanAmt;
	}

	public void setRestPlanAmt(String restPlanAmt) {
		this.restPlanAmt = restPlanAmt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getBillLimitDate() {
		return billLimitDate;
	}

	public void setBillLimitDate(String billLimitDate) {
		this.billLimitDate = billLimitDate;
	}

	public String getSdpAmt() {
		return sdpAmt;
	}

	public void setSdpAmt(String sdpAmt) {
		this.sdpAmt = sdpAmt;
	}

	public String getMinPayAmt() {
		return minPayAmt;
	}

	public void setMinPayAmt(String minPayAmt) {
		this.minPayAmt = minPayAmt;
	}

	public String getIsCurBill() {
		return isCurBill;
	}

	public void setIsCurBill(String isCurBill) {
		this.isCurBill = isCurBill;
	}

	public String getIsInPayDay() {
		return isInPayDay;
	}

	public void setIsInPayDay(String isInPayDay) {
		this.isInPayDay = isInPayDay;
	}

	public String getRealOverDays() {
		return realOverDays;
	}

	public void setRealOverDays(String realOverDays) {
		this.realOverDays = realOverDays;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
}
