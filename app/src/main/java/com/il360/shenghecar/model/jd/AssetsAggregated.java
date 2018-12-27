package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class AssetsAggregated implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String unPaidFor7;
	private String unPaidForMonth;
	private String unPaidForAll;
	private String overdueAmount;

	private Bill bill;
	private Bullion bullion;
	private Result result;

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

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public Bullion getBullion() {
		return bullion;
	}

	public void setBullion(Bullion bullion) {
		this.bullion = bullion;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
