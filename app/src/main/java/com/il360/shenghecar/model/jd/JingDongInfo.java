package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JingDongInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BasicInfo basicInfo;
	private List<ArrayOfMyBaiTiaoBill> baiTiaoList = new ArrayList<ArrayOfMyBaiTiaoBill>();
	private List<ArrayOfMyJinTiaoBill> jinTiaoList = new ArrayList<ArrayOfMyJinTiaoBill>();
	private List<MyBill> billList = new ArrayList<MyBill>();

	public BasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInfo basicInfo) {
		this.basicInfo = basicInfo;
	}

	public List<MyBill> getBillList() {
		return billList;
	}

	public void setBillList(List<MyBill> billList) {
		this.billList = billList;
	}

	public List<ArrayOfMyBaiTiaoBill> getBaiTiaoList() {
		return baiTiaoList;
	}

	public void setBaiTiaoList(List<ArrayOfMyBaiTiaoBill> baiTiaoList) {
		this.baiTiaoList = baiTiaoList;
	}

	public List<ArrayOfMyJinTiaoBill> getJinTiaoList() {
		return jinTiaoList;
	}

	public void setJinTiaoList(List<ArrayOfMyJinTiaoBill> jinTiaoList) {
		this.jinTiaoList = jinTiaoList;
	}

}
