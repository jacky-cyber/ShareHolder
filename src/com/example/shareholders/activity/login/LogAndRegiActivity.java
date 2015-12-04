package com.example.shareholders.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.shareholders.R;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_log_and_regi)
public class LogAndRegiActivity extends Activity implements AfterLogin {
	LoginReceiver loginReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

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

	@OnClick({ R.id.ll_look, R.id.tv_login, R.id.tv_register })
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		// 随便看看
		case R.id.ll_look:
			intent.setAction("LoginReceiver");
			Bundle bundle = new Bundle();
			bundle.putInt("index", 1);
			intent.putExtras(bundle);
			sendBroadcast(intent);
			break;

		// 登录
		case R.id.tv_login:
			intent.setClass(this, LoginActivity.class);
			intent.putExtra("isFirst", true);
			startActivity(intent);
			break;
		// 注册
		case R.id.tv_register:
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
		default:
			break;
		}

	}

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
		finish();
	}
	
	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(loginReceiver);
		super.onDestroy();
	}
}
