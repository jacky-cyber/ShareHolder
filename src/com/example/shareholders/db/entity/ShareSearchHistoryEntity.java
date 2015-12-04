package com.example.shareholders.db.entity;

import org.json.JSONArray;

import com.lidroid.xutils.db.annotation.Id;

public class ShareSearchHistoryEntity {
	@Id
	private String shortname;
	private String symbol;
	private boolean addcut;
	private JSONArray sharejsonArray = new JSONArray();

	public void setSharejsonArray(JSONArray sharejsonArray) {
		this.sharejsonArray = sharejsonArray;
	}

	public JSONArray getSharejsonArray() {
		sharejsonArray.put(shortname);
		sharejsonArray.put(symbol);
		sharejsonArray.put(addcut);
		return sharejsonArray;
	}
}
