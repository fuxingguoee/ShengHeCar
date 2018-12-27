package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArrayOfMyJinTiaoBill implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MyJinTiaoBillDet> detailList = new ArrayList<MyJinTiaoBillDet>();
	private MyJinTiaoBill jintiaoBill;

	public List<MyJinTiaoBillDet> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<MyJinTiaoBillDet> detailList) {
		this.detailList = detailList;
	}

	public MyJinTiaoBill getJintiaoBill() {
		return jintiaoBill;
	}

	public void setJintiaoBill(MyJinTiaoBill jintiaoBill) {
		this.jintiaoBill = jintiaoBill;
	}

}
