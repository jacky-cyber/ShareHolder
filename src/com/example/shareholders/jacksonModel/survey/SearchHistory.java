package com.example.shareholders.jacksonModel.survey;

import com.lidroid.xutils.db.annotation.Id;

public class SearchHistory {

	@Id
	private String history;
	
	
	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	
	
}
