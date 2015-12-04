package com.example.shareholders.jacksonModel.stock;

/**
 * 1.2.9 获取股票的报价数据
 * 
 * 开发者：袁展智 方法：Get
 * 
 * url
 * 
 * http://120.24.254.176:8080/shareholder-server/api/v1.0/quotation/
 * quotationDetaill/{股票代码}.json?access_token={访问令牌}&securityType={STOCK,INDEX}
 * 示例： http://120.24.254.176:8080/shareholder-server/api/v1.0/quotation/
 * quotationDetaill/600060.json?access_token=
 * 78ebefa6-e7f2-420d-b6bd-cf43e13977dd&securityType=STOCK
 * 
 * @author warren
 * 
 */
public class quotationDetail {

	private double price;

	private double avgPrice;
	private double changeRatio;
	private double change;
	private double openPrice;
	private double highPrice;
	private double lowPrice;
	private double preClosePrice;
	private double volume;
	private double amount;
	private double turnoverRate;
	private double pe;
	private double pb;
	private double navps;
	private double eps;
	private double marketValue;
	private double circulatedMarketValue;
	private double totalShare;
	private double circulatedShare;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(double avgPrice) {
		this.avgPrice = avgPrice;
	}

	public double getChangeRatio() {
		return changeRatio;
	}

	public void setChangeRatio(double changeRatio) {
		this.changeRatio = changeRatio;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public double getPreClosePrice() {
		return preClosePrice;
	}

	public void setPreClosePrice(double preClosePrice) {
		this.preClosePrice = preClosePrice;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public double getPb() {
		return pb;
	}

	public void setPb(double pb) {
		this.pb = pb;
	}

	public double getNavps() {
		return navps;
	}

	public void setNavps(double navps) {
		this.navps = navps;
	}

	public double getEps() {
		return eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public double getCirculatedMarketValue() {
		return circulatedMarketValue;
	}

	public void setCirculatedMarketValue(double circulatedMarketValue) {
		this.circulatedMarketValue = circulatedMarketValue;
	}

	public double getTotalShare() {
		return totalShare;
	}

	public void setTotalShare(double totalShare) {
		this.totalShare = totalShare;
	}

	public double getCirculatedShare() {
		return circulatedShare;
	}

	public void setCirculatedShare(double circulatedShare) {
		this.circulatedShare = circulatedShare;
	}

}
