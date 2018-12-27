package com.il360.shenghecar.model.goods;

import java.io.Serializable;

public class Goods implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Integer goodsId;// 商品id
	private String goodsName;// 商品名
	private String goodsPrice;// 商品价格
	private String goodsDesc;// 商品描述
	private String discountPrice;//优惠价格
	private Integer status;// 状态 1有效
	private String smallPic;//小图片
	private String detailsPic;
	private String bigPic;
	private Integer ext1;// 1二手车0不是
	private Integer isShow;//是否显示0不显示1显
	private Integer goodCode;// 1新品2二手
	private String creater;
	private String updater;
	private String goodsPic;
	private String createTime;
	private String updateTime;

	public String getGoodsPic() {
		return goodsPic;
	}

	public void setGoodsPic(String goodsPic) {
		this.goodsPic = goodsPic;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}


	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public Integer getExt1() {
		return ext1;
	}

	public void setExt1(Integer ext1) {
		this.ext1 = ext1;
	}

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}


	public Integer getGoodCode() {
		return goodCode;
	}

	public void setGoodCode(Integer goodCode) {
		this.goodCode = goodCode;
	}

	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}

	public String getDetailsPic() {
		return detailsPic;
	}

	public void setDetailsPic(String detailsPic) {
		this.detailsPic = detailsPic;
	}

	public String getBigPic() {
		return bigPic;
	}

	public void setBigPic(String bigPic) {
		this.bigPic = bigPic;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSmallPic() {
		return smallPic;
	}

	public void setSmallPic(String smallPic) {
		this.smallPic = smallPic;
	}
//    public void setIsShow(int isShow) {
//        this.isShow = isShow;
//    }
//
//	public void setGoodCode(int goodCode) {
//		this.goodCode = goodCode;
//	}
}
