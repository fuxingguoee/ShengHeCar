package com.il360.shenghecar.model.user;

import java.io.Serializable;
import java.util.List;

public class ArrayOfCommisApply implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int totalCount;
	private List<UserWithdrawals> list;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<UserWithdrawals> getList() {
		return list;
	}

	public void setList(List<UserWithdrawals> list) {
		this.list = list;
	}
}
