package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class Bill implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String curBillShouldPayAmount;
	private String curBillShouldPayMinAmount;
	private String nextBillShouldPayMinAmount;
	private String billLimitPay;
	private String unPaidFor7;
	private String unPaidForMonth;
	private String unPaidForAll;
	private String overdueAmount;
	private String acountStatus;
	private String totalAsset;
	private String availableLimit;
	private String canPlan;
	private String nextBillLimitPay;
	private String nextBillId;
	private String realoverdays;
	private String isLoan;
	private String billOverdueStatus;
	private String dayFeeAmount;

	public String getCurBillShouldPayAmount() {
		return curBillShouldPayAmount;
	}

	public void setCurBillShouldPayAmount(String curBillShouldPayAmount) {
		this.curBillShouldPayAmount = curBillShouldPayAmount;
	}

	public String getCurBillShouldPayMinAmount() {
		return curBillShouldPayMinAmount;
	}

	public void setCurBillShouldPayMinAmount(String curBillShouldPayMinAmount) {
		this.curBillShouldPayMinAmount = curBillShouldPayMinAmount;
	}

	public String getNextBillShouldPayMinAmount() {
		return nextBillShouldPayMinAmount;
	}

	public void setNextBillShouldPayMinAmount(String nextBillShouldPayMinAmount) {
		this.nextBillShouldPayMinAmount = nextBillShouldPayMinAmount;
	}

	public String getBillLimitPay() {
		return billLimitPay;
	}

	public void setBillLimitPay(String billLimitPay) {
		this.billLimitPay = billLimitPay;
	}

	public String getUnPaidFor7() {
		return unPaidFor7;
	}

	public void setUnPaidFor7(String unPaidFor7) {
		this.unPaidFor7 = unPaidFor7;
	}

	public String getUnPaidForMonth() {
		return unPaidForMonth;
	}

	public void setUnPaidForMonth(String unPaidForMonth) {
		this.unPaidForMonth = unPaidForMonth;
	}

	public String getUnPaidForAll() {
		return unPaidForAll;
	}

	public void setUnPaidForAll(String unPaidForAll) {
		this.unPaidForAll = unPaidForAll;
	}

	public String getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(String overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public String getAcountStatus() {
		return acountStatus;
	}

	public void setAcountStatus(String acountStatus) {
		this.acountStatus = acountStatus;
	}

	public String getTotalAsset() {
		return totalAsset;
	}

	public void setTotalAsset(String totalAsset) {
		this.totalAsset = totalAsset;
	}

	public String getAvailableLimit() {
		return availableLimit;
	}

	public void setAvailableLimit(String availableLimit) {
		this.availableLimit = availableLimit;
	}

	public String getCanPlan() {
		return canPlan;
	}

	public void setCanPlan(String canPlan) {
		this.canPlan = canPlan;
	}

	public String getNextBillLimitPay() {
		return nextBillLimitPay;
	}

	public void setNextBillLimitPay(String nextBillLimitPay) {
		this.nextBillLimitPay = nextBillLimitPay;
	}

	public String getNextBillId() {
		return nextBillId;
	}

	public void setNextBillId(String nextBillId) {
		this.nextBillId = nextBillId;
	}

	public String getRealoverdays() {
		return realoverdays;
	}

	public void setRealoverdays(String realoverdays) {
		this.realoverdays = realoverdays;
	}

	public String getIsLoan() {
		return isLoan;
	}

	public void setIsLoan(String isLoan) {
		this.isLoan = isLoan;
	}

	public String getBillOverdueStatus() {
		return billOverdueStatus;
	}

	public void setBillOverdueStatus(String billOverdueStatus) {
		this.billOverdueStatus = billOverdueStatus;
	}

	public String getDayFeeAmount() {
		return dayFeeAmount;
	}

	public void setDayFeeAmount(String dayFeeAmount) {
		this.dayFeeAmount = dayFeeAmount;
	}

}
