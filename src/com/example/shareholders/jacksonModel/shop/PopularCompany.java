package com.example.shareholders.jacksonModel.shop;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class PopularCompany {

	@Column
	private String shopName;
	@Column
	private String picUrl;
	@Id
	private int shopId;

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	@Override
	public String toString() {
		return "PopularShop [shopName=" + shopName + ", picUrl=" + picUrl
				+ ", shopId=" + shopId + "]";
	}

}
