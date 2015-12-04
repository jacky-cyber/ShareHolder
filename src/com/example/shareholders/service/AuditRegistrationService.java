package com.example.shareholders.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;

/**
 * 提交调研报名审核通过
 * 
 * @author jat
 * 
 */
public class AuditRegistrationService extends IntentService {

	// surveyUuid
	private String surveyUuid;
	// 报名人id集合
	private ArrayList<String> userUuidsList = null;

	RequestQueue volleyRequestQueue = null;

	public AuditRegistrationService() {
		super(null);
		// super(name);
		// TODO Auto-generated constructor stub
	}

	// 提交报名审核
	private void postAuditRegistration() {
		String url = AppConfig.URL_SURVEY + "enroll/check.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		JSONObject params = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			params.put("status", "SUCCESS");
			for (int i = 0; i < userUuidsList.size(); i++) {
				array.put(userUuidsList.get(i));
			}
			if (userUuidsList.size() == 0) {
				return;
			}
			params.put("userUuids", array);
			params.put("surveyUuid", surveyUuid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						// Toast.makeText(getApplicationContext(), "提交成功",
						// 0).show();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Toast.makeText(getApplicationContext(), "提交失败",
						// 0).show();
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		volleyRequestQueue = Volley.newRequestQueue(this);

		Bundle bundle = intent.getExtras();
		// 获取提交报名审核通过用户uuid
		userUuidsList = bundle.getStringArrayList("userUuid");
		// 调研uuid
		surveyUuid = bundle.getString("surveyUuid");
		postAuditRegistration();
	}

}
