package com.il360.shenghecar.model.goods;

import java.io.Serializable;
import java.util.List;

public class ArrayOfMeal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer code;
	private String desc;
	private List<Meal> result;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<Meal> getResult() {
		return result;
	}

	public void setResult(List<Meal> result) {
		this.result = result;
	}

}
