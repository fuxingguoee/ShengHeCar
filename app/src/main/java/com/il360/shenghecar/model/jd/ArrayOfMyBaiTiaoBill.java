package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArrayOfMyBaiTiaoBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MyBaiTiaoBillDet> detailList= new ArrayList<MyBaiTiaoBillDet>();
	private MyBaiTiaoBill monthBill;

	public List<MyBaiTiaoBillDet> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<MyBaiTiaoBillDet> detailList) {
		this.detailList = detailList;
	}

	public MyBaiTiaoBill getMonthBill() {
		return monthBill;
	}

	public void setMonthBill(MyBaiTiaoBill monthBill) {
		this.monthBill = monthBill;
	}

}
