package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class JKAccount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AccountResult accountResult;
	private String incomeAward;

	public AccountResult getAccountResult() {
		return accountResult;
	}

	public void setAccountResult(AccountResult accountResult) {
		this.accountResult = accountResult;
	}

	public String getIncomeAward() {
		return incomeAward;
	}

	public void setIncomeAward(String incomeAward) {
		this.incomeAward = incomeAward;
	}

}
