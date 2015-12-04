package com.example.shareholders.activity.personal;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends Activity {

	// 投诉内容
	@ViewInject(R.id.et_complaint)
	private EditText et_complaint;

	// 提交
	@ViewInject(R.id.tv_commit)
	private TextView tv_commit;
	// ProgressDialog progressDialog;
	// "正在加载"的旋转框
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		loadingDialog = new LoadingDialog(FeedbackActivity.this);
		setTextWatcher();
		
	}

	private void setTextWatcher() {
		et_complaint.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (et_complaint.getText().toString().equals("")) {
					tv_commit.setBackgroundResource(R.drawable.btn_login);
				} else {
					tv_commit
							.setBackgroundResource(R.drawable.btn_login_enable);
				}
			}
		});
	}

	@OnClick({ R.id.title_note, R.id.tv_commit ,R.id.rl_return})
	public void onclick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.title_note:
			finish();
			break;
		// 提交
		case R.id.tv_commit:
//			progressDialog = new ProgressDialog(FeedbackActivity.this);
//			progressDialog.setMessage("正在提交，请稍等...");
			loadingDialog.setLoadingString("正在提交，请稍等...");

			String context = et_complaint.getText().toString();
			if (context.equals("")) {
				InternetDialog internetDialog = new InternetDialog(
						FeedbackActivity.this);
				internetDialog.showInternetDialog("请将数据填写完整!", false);
			} else {
//				progressDialog.show();
				loadingDialog.showLoadingDialog();
				postSuggest(context);
			}

			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
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

	private void postSuggest(String suggest) {
		JSONObject params = new JSONObject();
		try {
			params.put("content", suggest);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = AppConfig.VERSION_URL + "user/feedback.json?access_token=";
		url = url + RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN);
		Log.d("反馈", url);
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						// TODO Auto-generated method stub
						// Toast.makeText(getApplication(), "提交成功",
						// Toast.LENGTH_SHORT).show();
//						progressDialog.dismiss();
						loadingDialog.dismissDialog();
						InternetDialog internetDialog = new InternetDialog(
								FeedbackActivity.this);
						internetDialog.showInternetDialog("提交成功!", true);
						// 两秒后返回上一页
						Timer timer = new Timer(true);
						timer.schedule(timerTask, 2000);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
		stringRequest.setTag("FeedbackActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("FeedbackActivity");
	}

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
