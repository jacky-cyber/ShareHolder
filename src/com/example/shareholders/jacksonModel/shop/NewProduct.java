package com.example.shareholders.jacksonModel.shop;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * 商城首页商品信息
 * 
 * @author jat
 * 
 */
public class NewProduct {
	@Id
	private int prodId;
	@Column
	private String prodUuid;
	@Column
	private String prodName;
	@Column
	private double prodPrice;
	@Column
	private String PicUrl;

	public int getProdId() {
		return prodId;
	}

	public void setProdId(int prodId) {
		this.prodId = prodId;
	}

	public String getProdUuid() {
		return prodUuid;
	}

	public void setProdUuid(String prodUuid) {
		this.prodUuid = prodUuid;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public double getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(double prodPrice) {
		this.prodPrice = prodPrice;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	@Override
	public String toString() {
		return "Product [prodId=" + prodId + ", prodUuid=" + prodUuid
				+ ", prodName=" + prodName + ", prodPrice=" + prodPrice
				+ ", PicUrl=" + PicUrl + "]";
	}

}
