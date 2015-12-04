package com.example.shareholders.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;

/**
 * 获取微博的个人信息
 * 
 * @author warren
 * 
 */
public class GetWeiBoInfoService extends IntentService {

	public GetWeiBoInfoService() {
		super("GetWeiBoInfoService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		String uid = arg0.getExtras().getString("uid");
		String access_token = arg0.getExtras().getString("access_token");
		GetWeiBoInfo(uid, access_token);
	}

	/**
	 * 获取头像和用户名，并把它放到sharePreferences上
	 * 
	 * @param uid
	 * @param access_token
	 */
	private void GetWeiBoInfo(String uid, String access_token) {
		String url = "https://api.weibo.com/2/users/show.json?uid=" + uid
				+ "&access_token=" + access_token;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("uid", uid);
			jsonObject.put("access_token", access_token);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(response.toString());
							// 将用户名和头像url写入share_preferences
							RsSharedUtil.putString(getApplicationContext(),
									AppConfig.NICKNAME,
									jsonObject.getString("screen_name"));
							RsSharedUtil.putString(getApplicationContext(),
									AppConfig.FIGURE_URL,
									jsonObject.getString("profile_image_url"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("error果", error.toString());
					}
				});
		MyApplication.getRequestQueue().add(stringRequest);
	}
}
