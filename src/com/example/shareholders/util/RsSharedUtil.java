package com.example.shareholders.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.config.AppConfig;

//TODO: Auto-generated Javadoc

/**
 * 
 * @author warren
 * @version 1.0
 * @date-2015-1-25
 */
public class RsSharedUtil {

	private static final String SHARED_PATH = AppConfig.SHARED_PATH;

	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		return context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
	}

	public static void putInt(Context context, String key, int value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, 0);
	}

	public static void putLong(Context context, String key, Long value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putLong(key, value);
		edit.commit();
	}

	public static Long getLong(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getLong(key, 0);
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static String getString(Context context, String key) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key, "");
	}

	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key, defValue);
	}
	
	// 判断登录状态
	public static boolean getLoginState(Context context){
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		return sharedPreferences.getString(AppConfig.ACCESS_TOKEN, "").equals("")?false:true;
	}

	// 判断登录状态并可提示未登录
	public static boolean getLoginStateAndShow(Context context) {
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
		boolean isLogined = sharedPreferences.getString(AppConfig.ACCESS_TOKEN,
				"").equals("") ? false : true;
		if (!isLogined) {
			InternetDialog internetDialog = new InternetDialog(context);
			internetDialog.showInternetDialog("请先登录", false);
		}
		return isLogined;
	}

}
