package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundHomeActivity.FundHomeTuiJianAdapter;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_about)
public class PersonalAboutActivity extends Activity {
	@ViewInject(R.id.background)
	private RelativeLayout background;
	@ViewInject(R.id.tv_tv_ba)
	private TextView tx_about;
	private ArrayList<HashMap<String, Object>> map_tui;
	private FundHomeTuiJianAdapter adapter_tui;// cjls

	@ViewInject(R.id.tv_tv_edition)
	TextView tx_edition;
	String appVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		GetappVersion();
		GetAbout();
	}

	private void GetappVersion() {
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			appVersion = info.versionName; // 版本名
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GetAbout() {
		String url = AppConfig.URL_USER + "about.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		StringRequest stringRequest = new StringRequest(url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response);
					String about=jsonObject.getString("about");
					tx_about.setText(about);
					//tx_edition.setText(appVersion);
					tx_edition.setText(about.substring(about.length()-3, about.length()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest.setTag("serverclause");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("title");
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	private void initwindow() {
		final View contentView = LayoutInflater.from(this).inflate(R.layout.popup_tiaokuan, null);
		final TextView tv_tiaokuang_content=(TextView)contentView.findViewById(R.id.tv_tiaokuang_content);
		ImageView iv_tiaokuan_return=(ImageView)contentView.findViewById(R.id.iv_tiaokuan_return);
		WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		final PopupWindow popupWindow = new PopupWindow(contentView, (int) (width / 6 * 5), (int) (height / 8 * 5));
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		// popwindow位置
		popupWindow.showAtLocation(background, Gravity.CENTER, 0, 0);
		//设置背景透明度
		background.setAlpha(0.7f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		String url=AppConfig.URL_ACCOUNT+"server-clause.json";
		StringRequest stringRequest=new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					Log.d("5.4respnese", response.toString());
					JSONObject jsonObject=new JSONObject(response);
					tv_tiaokuang_content.setText(jsonObject.getString("serverclause"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);
		//跳出弹窗
		iv_tiaokuan_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}
	

	
	@OnClick({ R.id.iv_iv_return, R.id.ll_pingfen, R.id.ll_tiaokuang, R.id.ll_daohang ,R.id.rl_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_iv_return:
			finish();
			break;
		case R.id.ll_pingfen:
			break;
		case R.id.ll_tiaokuang:
			background.setAlpha(0.7f);
			initwindow();
			break;
		case R.id.ll_daohang:
			startActivity(new Intent(PersonalAboutActivity.this, AboutIndexActivity.class));
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}
}
