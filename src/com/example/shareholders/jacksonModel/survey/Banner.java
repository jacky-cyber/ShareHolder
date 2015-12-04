package com.example.shareholders.jacksonModel.survey;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * 4.1.1 获取经典热门调研 by LPH on 2015/8/10
 * url:http://120.24.254.176:8080/shareholder-
 * server/api/v1.0/survey/heatest.json?access_token={访问令牌} 方法：POST
 * 
 * @author warren
 * 
 */
public class Banner {
	@Id
	private String uuid;
	@Column
	private String picture;
	@Column
	private String surveyName;
	@Column
	private String symbol;
	@Column
	private String symbolName;
	@Column
	private String securityType;
	@Column
	private String unlistedHistory;
	@Column
	private String nowPrice;
	@Column
	private String preClosePrice;
	@Column
	private String achievereturn;
	@Column
	private String annualizedyield;
	@Column
	private String nav;
	@Column
	private String accumulativenav;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getUnlistedHistory() {
		return unlistedHistory;
	}

	public void setUnlistedHistory(String unlistedHistory) {
		this.unlistedHistory = unlistedHistory;
	}

	public String getSymbolName() {
		return symbolName;
	}

	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	public String getNowPrice() {
		return nowPrice;
	}

	public void setNowPrice(String nowPrice) {
		this.nowPrice = nowPrice;
	}

	public String getPreClosePrice() {
		return preClosePrice;
	}

	public void setPreClosePrice(String preClosePrice) {
		this.preClosePrice = preClosePrice;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getAchievereturn() {
		return achievereturn;
	}

	public void setAchievereturn(String achievereturn) {
		this.achievereturn = achievereturn;
	}

	public String getAnnualizedyield() {
		return annualizedyield;
	}

	public void setAnnualizedyield(String annualizedyield) {
		this.annualizedyield = annualizedyield;
	}

	public String getNav() {
		return nav;
	}

	public void setNav(String nav) {
		this.nav = nav;
	}

	public String getAccumulativenav() {
		return accumulativenav;
	}

	public void setAccumulativenav(String accumulativenav) {
		this.accumulativenav = accumulativenav;
	}

	@Override
	public String toString() {
		return "Banner [uuid=" + uuid + ", picture=" + picture
				+ ", surveyName=" + surveyName + ", symbol=" + symbol
				+ ", symbolName=" + symbolName + ", securityType="
				+ securityType
				+ ", unlistedHistory=" + ", nowPrice=" + nowPrice
				+ ", preClosePrice=" + preClosePrice
				+ ", achievereturn=" + achievereturn + ", annualizedyield="
				+ annualizedyield + ", nav=" + nav + ", accumulativenav="
				+ accumulativenav + ", unlistedHistory="
				+ unlistedHistory + "]";
	}

}
