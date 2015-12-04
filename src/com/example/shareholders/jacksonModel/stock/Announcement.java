package com.example.shareholders.jacksonModel.stock;

import com.lidroid.xutils.db.annotation.Id;

public class Announcement {
	@Id
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
