package com.example.shareholders.jacksonModel.personal;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class LocalFollowedStockFriend {

	private String sortLetters;

	@Id
	private String uuid;
	@Column
	private String userName;
	@Column
	private String userLogo;
	@Column
	private int coin;
	@Column
	private String introduction;
	@Column
	private String industryCode;
	@Column
	private String industryName;
	@Column
	private String locationCode;
	@Column
	private String locationName;
	@Column
	private String type;

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

	public String getUserLogo() {
		return this.userLogo;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getCoin() {
		return this.coin;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIntroduction() {
		return this.introduction;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getIndustryCode() {
		return this.industryCode;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getIndustryName() {
		return this.industryName;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationCode() {
		return this.locationCode;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
