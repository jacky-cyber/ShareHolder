package com.example.shareholders.activity.personal;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.survey.ActivityCreateCompanyBrief;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_apply_for_statement)
public class ApplyForStatementActivity extends Activity {

	private int COMPANY = 2;
	@ViewInject(R.id.et_set_name)
	private TextView tv_company;
	@ViewInject(R.id.et_your_position)
	private EditText et_work;
	@ViewInject(R.id.et_contact_way)
	private EditText et_number;

	private HashMap<String, String> map = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

	}

	@OnClick({ R.id.rl_return, R.id.rl_set_name, R.id.tv_next })
	private void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;

		case R.id.rl_set_name:
			intent.setClass(getApplication(), ActivityCreateCompanyBrief.class);
			intent.putExtra("type", 111);
			startActivityForResult(intent, COMPANY);
			break;

		case R.id.tv_next:
			checkCommit();
			break;
		default:
			break;
		}
	}

	private void checkCommit() {

		if (tv_company.getText().toString().equals("")
				|| et_work.getText().toString().equals("")
				|| et_number.getText().toString().equals("")) {
			InternetDialog internetDialog = new InternetDialog(
					ApplyForStatementActivity.this);
			internetDialog.showInternetDialog("请将数据填写完整！", false);
			// Toast.makeText(this, "请将数据填写完整！", Toast.LENGTH_SHORT).show();
		} else {
			String company = tv_company.getText().toString().trim();
			String position = et_work.getText().toString().trim();
			String phone = et_number.getText().toString().trim();
			postData(company, position, phone);

		}

	}

	/**
	 * TimerTask 定时任务
	 */

	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			finish();
		}
	};

	private void postData(String company, String position, String phone) {

		JSONObject params = new JSONObject();
		try {
			params.put("companySymbol", map.get("symbol"));
			params.put("companyType", map.get("companyType"));
			params.put("position", position);
			params.put("contact", phone);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = AppConfig.VERSION_URL
				+ "company/manager.json?access_token=";
		url = url + RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN);

		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {

				Log.d("ApplyForStatementActivity", response);
				// TODO Auto-generated method stub
				InternetDialog internetDialog = new InternetDialog(
						ApplyForStatementActivity.this);
				internetDialog.showInternetDialog("您的申请已提交到后台，请等待审核！", true);
				// 两秒后返回上一页
				Timer timer = new Timer(true);
				timer.schedule(timerTask, 2000);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				InternetDialog internetDialog = new InternetDialog(
						ApplyForStatementActivity.this);
				internetDialog.showInternetDialog("提交失败！", false);
			}
		});
		stringRequest.setTag("ApplyForStatementActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("ApplyForStatementActivity");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == COMPANY) {
			try {
				map.put("symbol", data.getExtras().getString("symbol"));
				map.put("companyType", data.getExtras()
						.getString("companyType"));
				map.put("companyName", data.getExtras().getString("shortName"));
				tv_company.setText(data.getExtras().getString("shortName"));
			}

			catch (Exception e) {
				Log.d("Exception", e.toString());
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	// 下面三个用于隐藏软键盘
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getRawX() > left && event.getRawX() < right
					&& event.getRawY() > top && event.getRawY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
