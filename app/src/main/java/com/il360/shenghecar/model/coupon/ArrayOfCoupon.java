package com.il360.shenghecar.model.coupon;

import java.io.Serializable;
import java.util.List;

public class ArrayOfCoupon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int totalCount;
	private List<UserCoupon> list;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<UserCoupon> getList() {
		return list;
	}

	public void setList(List<UserCoupon> list) {
		this.list = list;
	}

}
