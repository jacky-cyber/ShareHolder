package com.example.shareholders.jacksonModel.stock;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class StockNews {
	@Id
	long newsid;
	@Column
	long declaredate;
	@Column
	String title;
	@Column
	String newssummary;
	@Column
	String listedCompanyShortName;
	@Column
	String symbol;
	
	public long getNewsid() {
		return newsid;
	}

	public void setNewsid(long newsid) {
		this.newsid = newsid;
	}
	
	public long getDeclaredate() {
		return this.declaredate;
	}

	public void setDeclaredate(long declaredate) {
		this.declaredate = declaredate;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getNewssummary() {
		return newssummary;
	}

	public void setNewssummary(String newssummary) {
		this.newssummary = newssummary;
	}
	

	
	public String getListedCompanyShortName() {
		return listedCompanyShortName;
	}

	public void setListedCompanyShortName(String listedCompanyShortName) {
		this.listedCompanyShortName= listedCompanyShortName;
	}
	
	

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}



}
