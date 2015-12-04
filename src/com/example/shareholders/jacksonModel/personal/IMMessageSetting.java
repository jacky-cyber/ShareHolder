package com.example.shareholders.jacksonModel.personal;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

/**
 * 缓存屏蔽个人信息
 * @author jat
 *
 */
public class IMMessageSetting {

	@Id
	private String iMName;
	@Column
	private String uuid;
	public String getiMName() {
		return iMName;
	}
	public void setiMName(String iMName) {
		this.iMName = iMName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString() {
		return "IMMessageSetting [iMName=" + iMName + ", uuid=" + uuid + "]";
	}
	
}
