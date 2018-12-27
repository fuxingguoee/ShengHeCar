package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class InvList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String investorId;
	private String investorName;
	private String amountRate;

	public String getInvestorId() {
		return investorId;
	}

	public void setInvestorId(String investorId) {
		this.investorId = investorId;
	}

	public String getInvestorName() {
		return investorName;
	}

	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}

	public String getAmountRate() {
		return amountRate;
	}

	public void setAmountRate(String amountRate) {
		this.amountRate = amountRate;
	}

}
