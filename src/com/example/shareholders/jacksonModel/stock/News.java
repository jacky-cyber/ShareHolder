package com.example.shareholders.jacksonModel.stock;

import com.lidroid.xutils.db.annotation.Id;

public class News {
	@Id
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
