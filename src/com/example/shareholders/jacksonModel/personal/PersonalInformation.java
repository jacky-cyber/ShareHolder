package com.example.shareholders.jacksonModel.personal;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * 3.1 获取个人资料并写入本地数据库
 * url：http://120.24.254.176:8080/shareholder-server/api/v1.0/user/profile.json?
 * access_token={访问令牌}&userUuid={用户uuid} 方法：GET 描述：获取指定用户的个人资料
 * 
 * @author warren
 * 
 */
public class PersonalInformation {
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
	private String LocationName;
	@Column
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserLogo() {
		return userLogo;
	}

	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getLocationName() {
		return LocationName;
	}

	public void setLocationName(String locationName) {
		LocationName = locationName;
	}

	@Override
	public String toString() {
		return "PersonalInformation [uuid=" + uuid + ", userName=" + userName
				+ ", userLogo=" + userLogo + ", coin=" + coin
				+ ", introduction=" + introduction + ", industryCode="
				+ industryCode + ", industryName=" + industryName
				+ ", locationCode=" + locationCode + ", LocationName="
				+ LocationName + "]";
	}

}
