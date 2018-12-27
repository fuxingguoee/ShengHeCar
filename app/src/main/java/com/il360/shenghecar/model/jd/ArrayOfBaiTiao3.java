package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.List;

public class ArrayOfBaiTiao3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BaiTiao3> billPayList;
	private int pageSize;
	private int currentPage;
	private int totalPage;
	private int totalNum;
	private Result2 result;

	public List<BaiTiao3> getBillPayList() {
		return billPayList;
	}

	public void setBillPayList(List<BaiTiao3> billPayList) {
		this.billPayList = billPayList;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public Result2 getResult() {
		return result;
	}

	public void setResult(Result2 result) {
		this.result = result;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
