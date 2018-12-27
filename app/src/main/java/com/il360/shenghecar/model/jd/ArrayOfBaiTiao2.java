package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class ArrayOfBaiTiao2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaiTiao2 billNOTOut;
	private String sdpAmt;
	private String sdpLoanAmt;
	private String sdpPlanFee;
	private String sdpDayAmt;
	private String sdpOverAmt;
	private String isCanRepayAll;

	public BaiTiao2 getBillNOTOut() {
		return billNOTOut;
	}

	public void setBillNOTOut(BaiTiao2 billNOTOut) {
		this.billNOTOut = billNOTOut;
	}

	public String getSdpAmt() {
		return sdpAmt;
	}

	public void setSdpAmt(String sdpAmt) {
		this.sdpAmt = sdpAmt;
	}

	public String getSdpLoanAmt() {
		return sdpLoanAmt;
	}

	public void setSdpLoanAmt(String sdpLoanAmt) {
		this.sdpLoanAmt = sdpLoanAmt;
	}

	public String getSdpPlanFee() {
		return sdpPlanFee;
	}

	public void setSdpPlanFee(String sdpPlanFee) {
		this.sdpPlanFee = sdpPlanFee;
	}

	public String getSdpDayAmt() {
		return sdpDayAmt;
	}

	public void setSdpDayAmt(String sdpDayAmt) {
		this.sdpDayAmt = sdpDayAmt;
	}

	public String getSdpOverAmt() {
		return sdpOverAmt;
	}

	public void setSdpOverAmt(String sdpOverAmt) {
		this.sdpOverAmt = sdpOverAmt;
	}

	public String getIsCanRepayAll() {
		return isCanRepayAll;
	}

	public void setIsCanRepayAll(String isCanRepayAll) {
		this.isCanRepayAll = isCanRepayAll;
	}

}
