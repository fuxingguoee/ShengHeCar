package com.il360.shenghecar.model.user;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserReward implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 订单号 **/
	private String orderNo;
	/** 奖励金 **/
	private BigDecimal amount;
	/** 状态0新建1有效-1无效  **/
	private Integer status;
	/** 创建时间 **/
	private Long createTime;
	/** 名字 **/
	private String userNameStr;
	/** 电话 **/
	private String loginNameStr;
	/** 获奖情况 **/
	private String desc;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

	public String getUserNameStr() {
		return userNameStr;
	}

	public void setUserNameStr(String userNameStr) {
		this.userNameStr = userNameStr;
	}

	public String getLoginNameStr() {
		return loginNameStr;
	}

	public void setLoginNameStr(String loginNameStr) {
		this.loginNameStr = loginNameStr;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
