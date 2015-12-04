package com.example.shareholders.wxapi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.MainActivity;
import com.example.shareholders.activity.login.ServerTermActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private void handleIntent(Intent paramIntent) {
		MyApplication.getIWXAPI().handleIntent(paramIntent, this);
		Log.d("resp.errCode", "resp.errCode");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("resp.errCode", "resp.errCode");
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
		Log.d("resp.errCode", "resp.errCode");
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		Log.d("resp.errCode", "resp.errCode");
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		Log.d("resp.errCode", resp.errCode + "");
		switch (resp.errCode) {

		case BaseResp.ErrCode.ERR_OK:
			try {
				String code = ((SendAuth.Resp) resp).code;
				// 根据返回的code获取openId，access_token，头像和昵称
				getTemporaryTokenAndId(code);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}
		finish();
	}

	/**
	 * 用code获取微信的openId和access_token
	 * 
	 * @param code
	 */
	private void getTemporaryTokenAndId(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
				+ "appid=" + AppConfig.WEIXIN_APP_ID + "&secret="
				+ AppConfig.AppSecret + "&code=" + code
				+ "&grant_type=authorization_code";
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							// 是第三方注册，并且type为WEIXIN
							RsSharedUtil.putBoolean(WXEntryActivity.this,
									AppConfig.IS_FORM_THIRD, true);
							RsSharedUtil.putString(WXEntryActivity.this,
									AppConfig.THIRD_TYPE, "WEIXIN");
							RsSharedUtil.putString(WXEntryActivity.this,
									AppConfig.THIRD_ACCESS_TOKEN,
									jsonObject.getString("access_token"));
							RsSharedUtil.putString(WXEntryActivity.this,
									AppConfig.OPENID,
									jsonObject.getString("openid"));
							Log.d("openid", jsonObject.getString("openid"));
							// 如果是绑定
							if (RsSharedUtil.getBoolean(
									getApplicationContext(), "wechatBinding",
									false)) {
								bindThird(jsonObject.getString("access_token"),
										jsonObject.getString("openid"));
							} else {
								// 如果是注册
								if (RsSharedUtil.getBoolean(
										getApplicationContext(),
										"wechatRegister", true)) {
									getWeiXinInfo(jsonObject
											.getString("access_token"),
											jsonObject.getString("openid"));
									finish();
								}
								// 如果是登录
								else {
									wechatLogin(jsonObject
											.getString("access_token"),
											jsonObject.getString("openid"));
								}
							}
						} catch (JSONException e) {
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
		stringRequest.setTag("getTemporaryTokenAndId");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 绑定微信
	 * 
	 * @param access_token
	 * @param openid
	 */
	public void bindThird(String access_token, String openid) {
		String url = AppConfig.URL_THIRD
				+ "bind.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&accessToken="
				+ access_token + "&openid=" + openid + "&type=WEIXIN";
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("绑定", "成功");
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("绑定", error.toString());
					}
				});
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 获取用户个人信息
	 * 
	 * @param access_token
	 * @param openid
	 */
	private void getWeiXinInfo(String access_token, String openid) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="
				+ access_token + "&openid=" + openid;
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
									jsonObject.getString("nickname"));
							RsSharedUtil.putString(getApplicationContext(),
									AppConfig.FIGURE_URL,
									jsonObject.getString("headimgurl"));
							startActivity(new Intent(WXEntryActivity.this,
									ServerTermActivity.class));
						} catch (JSONException e) {
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
		stringRequest.setTag("getWeiXinInfo");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 微信登录
	 * 
	 * @param url
	 */
	private void wechatLogin(String access_token, String openid) {
		String url = AppConfig.URL_THIRD + "weixin/login.json?accessToken="
				+ access_token + "&openid=" + openid;
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub

						Log.d("response", response);
						if (response.trim().equals("false"))
							Toast.makeText(getApplicationContext(), "请先绑定", 0).show();
						else {
							try {
								JSONObject jsonObject = new JSONObject(response);
								RsSharedUtil.putString(WXEntryActivity.this,
										AppConfig.ACCESS_TOKEN,
										jsonObject.getString("access_token"));
								// 跳到调研页,即index = 1;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// TODO Auto-generated method stub
							Intent intent = new Intent("LoginReceiver");
							Bundle bundle = new Bundle();
							bundle.putInt("index", 1);
							intent.putExtras(bundle);
							sendBroadcast(intent);
							
							
							
							startActivity(new Intent(WXEntryActivity.this,
									MainActivity.class));
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error", jsonObject.getString("description"));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
		stringRequest.setTag("wechatLogin");
		MyApplication.getRequestQueue().add(stringRequest);
	}
}
