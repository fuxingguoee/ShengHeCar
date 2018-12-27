package com.il360.shenghecar.model.goods;

import java.io.Serializable;
import java.math.BigDecimal;

public class Meal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer mealId;
	private String mealName;
	private String mealDesc;
	private BigDecimal mealPrice;
	private Integer status;
	private Long createTime;
	private String creater;
	private Long updateTime;
	private String updater;
	private Integer isDefault;// 1默认0非默认
	private String picUrl;

	public Integer getMealId() {
		return mealId;
	}

	public void setMealId(Integer mealId) {
		this.mealId = mealId;
	}

	public String getMealName() {
		return mealName;
	}

	public void setMealName(String mealName) {
		this.mealName = mealName;
	}

	public String getMealDesc() {
		return mealDesc;
	}

	public void setMealDesc(String mealDesc) {
		this.mealDesc = mealDesc;
	}

	public BigDecimal getMealPrice() {
		return mealPrice;
	}

	public void setMealPrice(BigDecimal mealPrice) {
		this.mealPrice = mealPrice;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

}
