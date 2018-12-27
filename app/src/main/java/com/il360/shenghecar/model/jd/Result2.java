package com.il360.shenghecar.model.jd;

import java.io.Serializable;

public class Result2 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String isSuccess;
	private String code;
	private String info;

	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
