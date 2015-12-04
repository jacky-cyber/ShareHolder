package com.example.shareholders.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;

public class PostPersonalInformationService extends IntentService {

	public PostPersonalInformationService() {
		super("PostPersonalInformationService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		modifyPersonalInformation(intent.getExtras().getString("logo"));

	}

	private void modifyPersonalInformation(String url) {
		String requestURL = AppConfig.VERSION_URL
				+ "user/profile/change.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userLogo", url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST,
				requestURL, jsonObject, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("修改个人头像", "成功");
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("修改个人头像", error.toString());
					}

				});
		MyApplication.getRequestQueue().add(stringRequest);
	}
}
