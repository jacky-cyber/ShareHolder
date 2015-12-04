package com.example.shareholders.jacksonModel.shop;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

import android.R.integer;

public class TuijianProduct {

	@Id
	private int prodId;
	@Column
	private String prodUuid;
	@Column
	private String prodName;
	@Column
	private double promDiscount;
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

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public double getPromDiscount() {
		return promDiscount;
	}

	public void setPromDiscount(double promDiscount) {
		this.promDiscount = promDiscount;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public String toString() {
		return "TuijianProduct [prodId=" + prodId + ", prodUuid=" + prodUuid
				+ ", prodName=" + prodName + ", promDiscount=" + promDiscount
				+ ", picUrl=" + picUrl + ", shopUuid=" + "]";
	}

}
