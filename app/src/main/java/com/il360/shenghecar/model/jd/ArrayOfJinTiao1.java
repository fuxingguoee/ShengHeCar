package com.il360.shenghecar.model.jd;

import java.io.Serializable;
import java.util.List;

public class ArrayOfJinTiao1 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<JinTiao1> detailsInfo;
	private int pageSize;
	private int pageNum;
	private int totalCount;
	private int pageCount;

	public List<JinTiao1> getDetailsInfo() {
		return detailsInfo;
	}

	public void setDetailsInfo(List<JinTiao1> detailsInfo) {
		this.detailsInfo = detailsInfo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
}
