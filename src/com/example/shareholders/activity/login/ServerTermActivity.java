package com.example.shareholders.activity.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_server_term)
public class ServerTermActivity extends Activity implements AfterLogin {
	boolean isFrist;
	// 同意
	@ViewInject(R.id.tv_agree)
	private TextView tv_agree;

	// 取消
	@ViewInject(R.id.tv_disargee)
	private TextView tv_disargee;

	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;
	// 内容
	@ViewInject(R.id.tv_detail)
	private TextView tv_detail;
	RequestQueue requestQueue;
	// 登录接收器
	LoginReceiver loginReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		requestQueue = Volley.newRequestQueue(this);
		// 下划线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		getServiceLaw();
		isFrist = false;
		try {
			Intent intent2 = getIntent();
			Bundle bundle2 = intent2.getExtras();
			isFrist = bundle2.getBoolean("isFrist");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(loginReceiver);
		MyApplication.getRequestQueue().cancelAll("getMsg");
		MyApplication.getRequestQueue().cancelAll("valifyMessage");
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// 接受广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("LoginReceiver");
		loginReceiver = new LoginReceiver();
		registerReceiver(loginReceiver, intentFilter);
		loginReceiver.setAfterLogin(this);
		super.onStart();
	};

	@OnClick({ R.id.tv_return, R.id.tv_disargee, R.id.tv_agree })
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.tv_return:
			finish();
			break;
		case R.id.tv_disargee:
			finish();
			break;
		case R.id.tv_agree:
			Intent intent=new Intent();
			intent.setClass(ServerTermActivity.this,
					AddInformationActivity.class);
			intent.putExtra("isFirst", isFrist);
			startActivity(intent);
		default:
			break;
		}
	}

	private void getServiceLaw() {
		String url = AppConfig.URL_ACCOUNT + "server-clause.json";
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							tv_detail.setText(jsonObject
									.getString("serverclause"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("error", error.toString());
					}
				}

		);
		requestQueue.add(stringRequest);
	}

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
		finish();
	}
	
}
