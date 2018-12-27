package com.il360.shenghecar.model.home;



import java.io.Serializable;
import java.util.List;

public class ArrayOfNews implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int  code;
	private String desc;
	private List<News> result;
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
	public List<News> getResult() {
		return result;
	}
	public void setResult(List<News> result) {
		this.result = result;
	}
}
