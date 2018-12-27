package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class MyBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String billType;//1白条还款流水 2 白条退款记录 3 白条消费明细 4 小金库收益 5 小金库转入 6 小金库转出 7 小金库冻结解冻
	private String billAmount;//金额
	private String billTime;//账单时间
	private String billTitle;//账单标题
	private String otherName;//其他信息
	private String billSn;//账单编号
	private String statusName;//状态名称

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(String billAmount) {
		this.billAmount = billAmount;
	}

	public String getBillTime() {
		return billTime;
	}

	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}

	public String getBillTitle() {
		return billTitle;
	}

	public void setBillTitle(String billTitle) {
		this.billTitle = billTitle;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getBillSn() {
		return billSn;
	}

	public void setBillSn(String billSn) {
		this.billSn = billSn;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
