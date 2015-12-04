package com.example.shareholders.jacksonModel.stock;

/**
 * 1.6.2 获得提醒信息
 * 
 * BY 王立超 2015/8/11 15:48:48
 * 
 * 方法：GET
 * 
 * URL
 * 
 * http://120.24.254.176:8080/shareholder-server/api/v1.0/info/getSecurityAlert/
 * {股票代码}.json?access_token={访问令牌}securityType={STOCK(股票)/INDEX(指数)}
 * http://120.24
 * .254.176:8080/shareholder-server/api/v1.0/info/getSecurityAlert/600885.
 * json?access_token=78ebefa6-e7f2-420d-b6bd-cf43e13977dd&securityType=STOCK
 * 
 * @author warren
 * 
 */
public class SecurityAlert {
	private String uuid;
	private String symbol;
	private double highPrice;
	private double lowPrice;
	private double changeRatio;
	private long amount;
	private double turnoverRate;
	private boolean newsAnnRep;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public double getChangeRatio() {
		return changeRatio;
	}

	public void setChangeRatio(double changeRatio) {
		this.changeRatio = changeRatio;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public double getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(double turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public boolean isNewsAnnRep() {
		return newsAnnRep;
	}

	public void setNewsAnnRep(boolean newsAnnRep) {
		this.newsAnnRep = newsAnnRep;
	}
}
