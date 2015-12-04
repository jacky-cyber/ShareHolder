package com.example.shareholders.jacksonModel.personal;

public class StockFriend {

	private String sortLetters;

	private String uuid;

	private String userName;

	private String userLogo;

	private String coin;

	private String type;

	private String industryName;

	private String locationName;

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}

	public String UserLogo() {
		return this.userLogo;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getIndustryName() {
		return this.industryName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getCoin() {
		return this.coin;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
