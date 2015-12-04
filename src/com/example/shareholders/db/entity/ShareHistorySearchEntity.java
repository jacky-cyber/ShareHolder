package com.example.shareholders.db.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class ShareHistorySearchEntity {
	@Id
	private String shortname;
	@Column
	private String symbol;
	@Column
	private String securityType;

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getShortname() {
		return shortname;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}


	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getSecurityType() {
		return securityType;
	}
}
