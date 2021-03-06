package com.il360.shenghecar.model.hua;

import java.io.Serializable;
import java.util.List;

public class ArrayOfUserCredit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int code;
	private String desc;
	private List<UserCredit> result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<UserCredit> getResult() {
		return result;
	}

	public void setResult(List<UserCredit> result) {
		this.result = result;
	}

}
