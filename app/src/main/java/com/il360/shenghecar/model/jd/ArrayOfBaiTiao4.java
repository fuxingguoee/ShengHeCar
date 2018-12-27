package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.List;

public class ArrayOfBaiTiao4 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<BaiTiao4> btRefundVoList;
	private int pageSize;
	private int currentPage;
	private int totalPage;
	private int totalNum;
	private Result result;

	public List<BaiTiao4> getBtRefundVoList() {
		return btRefundVoList;
	}

	public void setBtRefundVoList(List<BaiTiao4> btRefundVoList) {
		this.btRefundVoList = btRefundVoList;
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

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
