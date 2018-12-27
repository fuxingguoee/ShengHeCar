package com.il360.shenghecar.model.coupon;

import java.io.Serializable;
import java.math.BigDecimal;

public class AgentUser implements Serializable {
	/**
	 * 
	 * 排行榜
	 */
	private static final long serialVersionUID = 1L;
	private Integer number;//推荐人数
	private Integer rank;//名次
	private String loginNameStr;//手机号码
	private BigDecimal invitationAmount;//总奖励金

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getLoginNameStr() {
		return loginNameStr;
	}

	public void setLoginNameStr(String loginNameStr) {
		this.loginNameStr = loginNameStr;
	}

	public BigDecimal getInvitationAmount() {
		return invitationAmount;
	}

	public void setInvitationAmount(BigDecimal invitationAmount) {
		this.invitationAmount = invitationAmount;
	}

}
