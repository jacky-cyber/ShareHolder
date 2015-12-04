package com.example.shareholders.jacksonModel.survey;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class LastestSurvey {
	@Id
	private String uuid;
	@Column
	private String surveyName;
	@Column
	private String logo;
	@Column
	private String beginDate;
	@Column
	private String endDate;
	@Column
	private int countFollow;
	@Column
	private String locationName;
	@Column
	private String state;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getCountFollow() {
		return countFollow;
	}

	public void setCountFollow(int countFollow) {
		this.countFollow = countFollow;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "LastestSurvey [uuid=" + uuid + ", surveyName=" + surveyName
				+ ", logo=" + logo + ", beginDate=" + beginDate + ", endDate="
				+ endDate + ", countFollow=" + countFollow + ", locationName="
				+ locationName + ", state=" + state + "]";
	}
}
