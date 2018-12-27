package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class Bullion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String unPaidFor7;
	private String unPaidForMonth;
	private String unPaidForAll;
	private String overdueAmount;
	private String acountStatus;
	private String totalAsset;
	private String availableLimit;

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

}
