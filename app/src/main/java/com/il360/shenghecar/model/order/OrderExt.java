package com.il360.shenghecar.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by lepc on 2018/7/12.
 */

public class OrderExt implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String orderNo;//商品订单号
    private BigDecimal orderAmount;//白条金额
    private BigDecimal fee;//退款手续费
    private BigDecimal rate;//退款利率（%）
    private String expireDay;//还款到期时间
    private Integer userId;//用户id
    private Integer status;//0未还1已还
    private BigDecimal overdueFee;//逾期费用
    private BigDecimal overdueRate;//逾期利率（%）
    private String closeDay;//还款时间
    private String createTime;//创建时间

    public String getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(String expireDay) {
        this.expireDay = expireDay;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getOverdueFee() {
        return overdueFee;
    }

    public void setOverdueFee(BigDecimal overdueFee) {
        this.overdueFee = overdueFee;
    }

    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    public String getCloseDay() {
        return closeDay;
    }

    public void setCloseDay(String closeDay) {
        this.closeDay = closeDay;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
