package com.example.shareholders.jacksonModel.survey;

import java.util.Arrays;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

public class HottestComment {
	@Column
	private String creatorUuid;
	@Column
	private String creatorName;
	@Column
	private String creatorLogoUrl;
	@Id
	private String topicUuid;
	@Column
	private String content;
	@Column
	private Media[] medias;
	@Column
	private int likeNum;
	@Column
	private int commentNum;
	@Column
	private int readNum;
	@Column
	private int transpondNum;
	@Column
	private HottestComment refTopic;
	@Column
	private String creationTime;
	@Column
	private boolean original;
	@Column
	private boolean followed;
	@Column
	private boolean liked;
	@Column
	private boolean createdByMe;
	@Column
	private String topicType;
	@Column
	private String surveyUuid;
	@Column
	private String surveyName;

	public String getCreatorUuid() {
		return creatorUuid;
	}

	public void setCreatorUuid(String creatorUuid) {
		this.creatorUuid = creatorUuid;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCreatorLogoUrl() {
		return creatorLogoUrl;
	}

	public void setCreatorLogoUrl(String creatorLogoUrl) {
		this.creatorLogoUrl = creatorLogoUrl;
	}

	public String getTopicUuid() {
		return topicUuid;
	}

	public void setTopicUuid(String topicUuid) {
		this.topicUuid = topicUuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Media[] getMedias() {
		return medias;
	}

	public void setMedias(Media[] medias) {
		this.medias = medias;
	}

	public int getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getReadNum() {
		return readNum;
	}

	public void setReadNum(int readNum) {
		this.readNum = readNum;
	}

	public int getTranspondNum() {
		return transpondNum;
	}

	public void setTranspondNum(int transpondNum) {
		this.transpondNum = transpondNum;
	}

	public HottestComment getRefTopic() {
		return refTopic;
	}

	public void setRefTopic(HottestComment refTopic) {
		this.refTopic = refTopic;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
	}

	public boolean isFollowed() {
		return followed;
	}

	public void setFollowed(boolean followed) {
		this.followed = followed;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public boolean isCreatedByMe() {
		return createdByMe;
	}

	public void setCreatedByMe(boolean createdByMe) {
		this.createdByMe = createdByMe;
	}

	public String getTopicType() {
		return topicType;
	}

	public void setTopicType(String topicType) {
		this.topicType = topicType;
	}

	public String getSurveyUuid() {
		return surveyUuid;
	}

	public void setSurveyUuid(String surveyUuid) {
		this.surveyUuid = surveyUuid;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	@Override
	public String toString() {
		return "HottestComment [creatorUuid=" + creatorUuid + ", creatorName="
				+ creatorName + ", creatorLogoUrl=" + creatorLogoUrl
				+ ", topicUuid=" + topicUuid + ", content=" + content
				+ ", medias=" + Arrays.toString(medias) + ", likeNum="
				+ likeNum + ", commentNum=" + commentNum + ", readNum="
				+ readNum + ", transpondNum=" + transpondNum + ", refTopic="
				+ refTopic + ", creationTime=" + creationTime + ", original="
				+ original + ", followed=" + followed + ", liked=" + liked
				+ ", createdByMe=" + createdByMe + ", topicType=" + topicType
				+ ", surveyUuid=" + surveyUuid + ", surveyName=" + surveyName
				+ "]";
	}

}
