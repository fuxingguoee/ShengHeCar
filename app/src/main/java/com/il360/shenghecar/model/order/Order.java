package com.il360.shenghecar.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

public class Order implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 商品订单号
	 **/
	private String orderNo;
	/**
	 * 用户id
	 **/
	private Integer userId;
	/**
	 * 商品id
	 **/
	private Integer goodsId;

	/**
	 * 商品名称
	 **/
	private String goodsName;
	/**
	 * 购买数量
	 **/
	private Integer orderNum;
	/**
	 * 商品价格
	 **/
	private BigDecimal goodsPrice;
	/**
	 * 状态0待支付1申请退款2审核通过，待打款3已完成退款5取消订单4退款审核不通过
	 **/
	private Integer status;
	/**
	 * 收货地址id
	 **/
	private String addressId;
	/**
	 * 创建时间
	 **/
	private String createTime;

	/**
	 * 拒绝理由
	 */
	private String refuseReson;

	/**
	 * 签名
	 */
	private String orderSign;


	private String goodsDesc;// 商品描述
	private String smallPic;// 图片地址
	private String name;//收货人
    private String phone;//收货电话
    private String address;//收货地址
	private String province;//省
	private String city;//市
	private String area;//区
    private String statusDesc;//状态描述
    private String txySignPic;//腾讯云签名地址


	public String getOrderSign() {
		return orderSign;
	}

	public void setOrderSign(String orderSign) {
		this.orderSign = orderSign;
	}

	public String getRefuseReson() {
		return refuseReson;
	}

	public void setRefuseReson(String refuseReson) {
		this.refuseReson = refuseReson;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderNo() {
		return this.orderNo;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getGoodsId() {
		return this.goodsId;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsName() {
		return this.goodsName;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getOrderNum() {
		return this.orderNum;
	}

	public void setGoodsPrice(BigDecimal goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public BigDecimal getGoodsPrice() {
		return this.goodsPrice;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getAddressId() {
		return this.addressId;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public String getCollectTime() {
		return null;
	}

	public String getSmallPic() {
		return smallPic;
	}

	public void setSmallPic(String smallPic) {
		this.smallPic = smallPic;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTxySignPic() {
		return txySignPic;
	}

	public void setTxySignPic(String txySignPic) {
		this.txySignPic = txySignPic;
	}
}
