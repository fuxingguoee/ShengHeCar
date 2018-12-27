package com.il360.shenghecar.model.coupon;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserCoupon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer userId;
	private Integer volumeId;// 券id
	private Integer number;// 拥有总数
	private Integer usedNumber;// 已使用数
	private Integer status;// 1可用0不可用
	private String createTime;// 创建时间
	private String updateTime;// 更新时间
	private String name;// 券名称
	private Integer type;// 1折扣券2代金券
	private BigDecimal achieveAmount;// 0表示无条件 其他表示满多少才能使用
	private BigDecimal reduceAmount;// 折扣或者减数额
	private Long startTime;// 开始时间
	private Long endTime;// 结束时间
	private Integer useScope;// 使用范围1全额付款2分期付款

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public  Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(Integer volumeId) {
		this.volumeId = volumeId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getUsedNumber() {
		return usedNumber;
	}

	public void setUsedNumber(Integer usedNumber) {
		this.usedNumber = usedNumber;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getAchieveAmount() {
		return achieveAmount;
	}

	public void setAchieveAmount(BigDecimal achieveAmount) {
		this.achieveAmount = achieveAmount;
	}

	public BigDecimal getReduceAmount() {
		return reduceAmount;
	}

	public void setReduceAmount(BigDecimal reduceAmount) {
		this.reduceAmount = reduceAmount;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getUseScope() {
		return useScope;
	}

	public void setUseScope(Integer useScope) {
		this.useScope = useScope;
	}

}
