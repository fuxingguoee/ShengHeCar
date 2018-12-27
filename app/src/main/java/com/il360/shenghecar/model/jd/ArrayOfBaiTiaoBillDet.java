package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArrayOfBaiTiaoBillDet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BaiTiaoBill> billList = new ArrayList<BaiTiaoBill>();
	private LastBill lastbill;
	private Result result;

	public List<BaiTiaoBill> getBillList() {
		return billList;
	}

	public void setBillList(List<BaiTiaoBill> billList) {
		this.billList = billList;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public LastBill getLastbill() {
		return lastbill;
	}

	public void setLastbill(LastBill lastbill) {
		this.lastbill = lastbill;
	}

}
