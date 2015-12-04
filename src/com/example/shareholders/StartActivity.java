package com.example.shareholders;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_start)
public class StartActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (RsSharedUtil.getString(getApplicationContext(), AppConfig.ACCESS_TOKEN).equals("")) {
					Intent intent=new Intent();
					intent.setClass(StartActivity.this, LoginActivity.class);
					intent.putExtra("fromstartactivity", true);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), MainActivity.class);
					startActivity(intent);
				}

				finish();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000);
	}

	/*
	 * @OnClick(R.id.iv_activity_start) private void onclick(View view){ switch
	 * (view.getId()) { case R.id.iv_activity_start: Intent intent = new
	 * Intent(); intent.setClass(getApplicationContext(), MainActivity.class);
	 * startActivity(intent); break;
	 * 
	 * default: break; } }
	 */
}
