package com.example.shareholders.jacksonModel.shop;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class ShopBanner {

	@Id
	private int prodId;
	@Column
	private String prodUuid;
	@Column
	private String picUrl;

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

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public String toString() {
		return "ShopBanner [prodId=" + prodId + ", prodUuid=" + prodUuid
				+ ", picUrl=" + picUrl + "]";
	}

}
