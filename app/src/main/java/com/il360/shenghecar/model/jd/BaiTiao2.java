package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class BaiTiao2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billId;
	private String billAmt;
	private String billDate;
	private String sdpAmt;
	private String minPayAmt;
	private String planedAmt;
	private String payedAmt;
	private String refundAmt;
	private String isCurBill;
	private String canRepay;
	private String nextOverAmt;
	private String nextDayAmt;

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

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
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

	public String getPlanedAmt() {
		return planedAmt;
	}

	public void setPlanedAmt(String planedAmt) {
		this.planedAmt = planedAmt;
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

	public String getIsCurBill() {
		return isCurBill;
	}

	public void setIsCurBill(String isCurBill) {
		this.isCurBill = isCurBill;
	}

	public String getCanRepay() {
		return canRepay;
	}

	public void setCanRepay(String canRepay) {
		this.canRepay = canRepay;
	}

	public String getNextOverAmt() {
		return nextOverAmt;
	}

	public void setNextOverAmt(String nextOverAmt) {
		this.nextOverAmt = nextOverAmt;
	}

	public String getNextDayAmt() {
		return nextDayAmt;
	}

	public void setNextDayAmt(String nextDayAmt) {
		this.nextDayAmt = nextDayAmt;
	}

}
