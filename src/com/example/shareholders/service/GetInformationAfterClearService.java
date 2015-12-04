package com.example.shareholders.service;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
public class GetInformationAfterClearService extends IntentService {
	public GetInformationAfterClearService() {
		super("GetInformationAfterLogin");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("哈哈哈哈哈", "知足主张");
		//initFromNet();
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

	private void initFromNet()
	{
		String url=AppConfig.URL_USER+"profile.json?access_token="+RsSharedUtil.getString(
				getApplicationContext(), AppConfig.ACCESS_TOKEN)+"&userUuid=myself";
		
		Log.d("person_url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							DbUtils dbUtils = DbUtils.create(getApplicationContext());
							Log.d("person_responsee", response);
								JSONObject jsonobject = new JSONObject(response);
								String  names = jsonobject.getString("userName");
								String  userLogo = jsonobject.getString("userLogo");
								String  introduction = jsonobject.getString("introduction");
								String  industryName = jsonobject.getString("industryName");
								String  locationName = jsonobject.getString("locationName");
								String coin=jsonobject.getString("coin");
								String industyCode=jsonobject.getString("industryCode");
								String locationCode=jsonobject.getString("locationCode");

								PersonalInformation personalInformation1;
								
								personalInformation1=new PersonalInformation();
								personalInformation1.setCoin(Integer.parseInt(coin));
								personalInformation1.setIndustryCode(industyCode);
								personalInformation1.setIndustryName(industryName);							
								personalInformation1.setLocationCode(locationCode);
								personalInformation1.setLocationName(locationName);
								personalInformation1.setIntroduction(introduction);			
								personalInformation1.setUserLogo(userLogo);
								personalInformation1.setUserName(names);
								personalInformation1.setUuid(RsSharedUtil.getString(getApplicationContext(),
										AppConfig.UUID));
								dbUtils.save(personalInformation1);

								
						} catch (Exception e) {
							Log.d("error_description",
									e.toString()+"222");
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("error_description",
								error.toString()+"333");
						try {
							
								JSONObject jsonObject = new JSONObject(error
										.data());
								Log.d("error_description",
										jsonObject.getString("description"));
							
						}
						catch (Exception e) {

							Log.d("error_description",
									e.toString()+"444");
						}
					}
				});

		MyApplication.getRequestQueue().add(stringRequest);
	}

}
