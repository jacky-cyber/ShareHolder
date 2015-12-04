package com.example.shareholders.service;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * 登录后获取个人资料存入本地数据库
 * 
 * @author warren
 * 
 */
public class GetInformationAfterLoginService extends IntentService {
	public GetInformationAfterLoginService() {
		super("GetInformationAfterLogin");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("哈哈哈哈哈", "知足主张");
		getPersonalInformation();
	}

	private void getPersonalInformation() {
		String url = AppConfig.URL_USER
				+ "profile.json?&userUuid=myself"
				+ "&access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN);
		Log.d("getPersonalInformation", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("getPersonalInformation", response);
						PersonalInformation personalInformation = new PersonalInformation();
						ObjectMapper objectMapper = new ObjectMapper();
						try {
							personalInformation = objectMapper.readValue(
									response, PersonalInformation.class);
							DbUtils dbUtils = DbUtils
									.create(getApplicationContext());
							// 把uuid写入shareprefrences用于下次查询用户资料
							RsSharedUtil.putString(getApplicationContext(),
									AppConfig.UUID,
									personalInformation.getUuid());
							Log.d("personalInformation",
									personalInformation.toString());
							// 更新或者新增
							dbUtils.saveOrUpdate(personalInformation);
							Intent intent = new Intent(
									"updateInformationReceiver");
							sendBroadcast(intent);
							
							Intent intent1 = new Intent(
									"updateShopHeadView");
							sendBroadcast(intent1);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("error", error.toString());
					}
				});
		MyApplication.getRequestQueue().add(stringRequest);
	}
}
