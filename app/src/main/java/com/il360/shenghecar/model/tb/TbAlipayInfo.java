package com.il360.shenghecar.model.tb;

import java.io.Serializable;

public class TbAlipayInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String account;
	private String realName;
	private String idCard;
	private String authStatus;
	private int accBal;
	private int ebaoBal;
	private int totalEarningsAmt;
	private int hbUsableAmt;
	private int hbAmount;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}
	public int getAccBal() {
		return accBal;
	}
	public void setAccBal(int accBal) {
		this.accBal = accBal;
	}
	public int getEbaoBal() {
		return ebaoBal;
	}
	public void setEbaoBal(int ebaoBal) {
		this.ebaoBal = ebaoBal;
	}
	public int getTotalEarningsAmt() {
		return totalEarningsAmt;
	}
	public void setTotalEarningsAmt(int totalEarningsAmt) {
		this.totalEarningsAmt = totalEarningsAmt;
	}
	public int getHbUsableAmt() {
		return hbUsableAmt;
	}
	public void setHbUsableAmt(int hbUsableAmt) {
		this.hbUsableAmt = hbUsableAmt;
	}
	public int getHbAmount() {
		return hbAmount;
	}
	public void setHbAmount(int hbAmount) {
		this.hbAmount = hbAmount;
	}
	
	

}
