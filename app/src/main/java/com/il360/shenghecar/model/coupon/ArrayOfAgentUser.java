package com.il360.shenghecar.model.coupon;

import java.io.Serializable;
import java.util.List;

public class ArrayOfAgentUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AgentUser agentUser;
	private List<AgentUser> list;

	public AgentUser getAgentUser() {
		return agentUser;
	}

	public void setAgentUser(AgentUser agentUser) {
		this.agentUser = agentUser;
	}

	public List<AgentUser> getList() {
		return list;
	}

	public void setList(List<AgentUser> list) {
		this.list = list;
	}

}
