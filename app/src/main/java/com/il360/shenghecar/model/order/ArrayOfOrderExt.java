package com.il360.shenghecar.model.order;

import java.io.Serializable;
import java.util.List;

public class ArrayOfOrderExt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer totalCount;
	private List<OrderExt> list;

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public List<OrderExt> getList() {
		return list;
	}

	public void setList(List<OrderExt> list) {
		this.list = list;
	}
}
