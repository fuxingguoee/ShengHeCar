package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.List;

public class ArrayOfBaiTiao1 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BaiTiao1> billList;
	private int currentPage;
	private int pageSize;
	private int pageCount;
	private int totalCount;
	private Result result;

	public List<BaiTiao1> getBillList() {
		return billList;
	}

	public void setBillList(List<BaiTiao1> billList) {
		this.billList = billList;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
