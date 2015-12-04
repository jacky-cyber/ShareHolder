package com.example.shareholders.db.entity;

import com.lidroid.xutils.db.annotation.Id;

public class SearchHistoryEntity {
	@Id
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
