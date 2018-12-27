package com.il360.shenghecar.model.order;

import java.io.Serializable;

public class Stages implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer stagesNumber;
	private String stagesRate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStagesNumber() {
		return stagesNumber;
	}

	public void setStagesNumber(Integer stagesNumber) {
		this.stagesNumber = stagesNumber;
	}

	public String getStagesRate() {
		return stagesRate;
	}

	public void setStagesRate(String stagesRate) {
		this.stagesRate = stagesRate;
	}
}
