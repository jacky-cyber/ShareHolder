/**
 * 
 */
package com.example.shareholders.db.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * @author warren 录音类
 */
public class Recorder {
	// 名称
	@Id
	String name;
	// 录音时长
	@Column
	int time;
	// 文件路径
	@Id
	String filePath;
	// 日期
	@Column
	String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
