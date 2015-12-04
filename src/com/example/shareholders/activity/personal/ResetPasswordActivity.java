package com.example.shareholders.activity.personal;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.example.shareholders.activity.login.FindPasswordActivity;
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_reset_password)
public class ResetPasswordActivity extends Activity {

	// 原密码
	@ViewInject(R.id.et_old_password)
	private ClearEditText et_old_password;

	// 新密码
	@ViewInject(R.id.et_new_password)
	private ClearEditText et_new_password;

	// 再次输入新密码
	@ViewInject(R.id.et_new_password_again)
	private ClearEditText et_new_password_again;
	//忘记密码
	@ViewInject(R.id.tv_forget_password)
	private TextView tv_forget_password;

	// 确定
	@ViewInject(R.id.tv_confirm)
	private TextView tv_confirm;

	private ProgressDialog progressDialog;

	private boolean reset_success = false;

	private static final int CAN_CONFIRM = 0;
	private static final int CAN_NOT_CONFIRM = 1;

	private boolean old_valid = false;// 原密码是否输入
	private boolean new_valid = false;// 新密码是否输入
	private boolean new_again_valid = false;// 再次输入密码是否输入

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CAN_CONFIRM) {
				Log.d("liang", "" + old_valid + " " + new_valid + " "
						+ new_again_valid);
				if (old_valid && new_valid && new_again_valid) { // 三个输入框都有输入内容
					tv_confirm
							.setBackgroundResource(R.drawable.btn_login_enable);
				}
			} else if (msg.what == CAN_NOT_CONFIRM) {
				tv_confirm.setBackgroundResource(R.drawable.btn_login);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		tv_forget_password.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
				startActivity(intent);
			}
		});
		setTextWatcher();

	}

	private void setTextWatcher() {
		// 原密码
		et_old_password.addTextChangedListener(new TextWatcher() {

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
				Message msg = new Message();
				if (et_old_password.getText().toString().equals("")) {
					old_valid = false;
					msg.what = CAN_NOT_CONFIRM;
					handler.sendMessage(msg);
				} else {
					old_valid = true;
					msg.what = CAN_CONFIRM;
					handler.sendMessage(msg);
				}
			}
		});

		// 新密码
		et_new_password.addTextChangedListener(new TextWatcher() {

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
				Message msg = new Message();
				if (et_new_password.getText().toString().equals("")) {
					new_valid = false;
					msg.what = CAN_NOT_CONFIRM;
					handler.sendMessage(msg);
				} else {
					new_valid = true;
					msg.what = CAN_CONFIRM;
					handler.sendMessage(msg);
				}
			}
		});

		// 再次输入新密码
		et_new_password_again.addTextChangedListener(new TextWatcher() {

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
				Message msg = new Message();
				if (et_new_password_again.getText().toString().equals("")) {
					new_again_valid = false;
					msg.what = CAN_NOT_CONFIRM;
					handler.sendMessage(msg);
				} else {
					new_again_valid = true;
					msg.what = CAN_CONFIRM;
					handler.sendMessage(msg);
				}
			}
		});

	}

	@OnClick({ R.id.rl_return, R.id.tv_confirm })
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.rl_return:
			finish();
			break;
		// 确定
		case R.id.tv_confirm:
			// 同时都有输入时
			if (old_valid && new_valid && new_again_valid) {
				if (et_old_password.getText().toString().equals("dss1234567888")) {
					InternetDialog internetDialog = new InternetDialog(
							ResetPasswordActivity.this);
					internetDialog.showInternetDialog("旧密码错误!", false);
					// showAlertDialog(getResources().getString(
					// R.string.wrong_passaord));
					// Toast.makeText(this, "旧密码错误", 3000).show();
				} else if (!et_new_password.getText().toString()
						.equals(et_new_password_again.getText().toString())) {// 密码两次输入不同
					InternetDialog internetDialog = new InternetDialog(
							ResetPasswordActivity.this);
					internetDialog.showInternetDialog("两个新密码不相同!", false);
					// showAlertDialog(getResources().getString(
					// R.string.password_different));
					// Toast.makeText(this, "两个新密码不相同。。", 3000).show();
				} else if (et_new_password.getText().toString()
						.equals(et_old_password.getText().toString())) {// 新密码和原密码相同
					InternetDialog internetDialog = new InternetDialog(
							ResetPasswordActivity.this);
					internetDialog.showInternetDialog("新密码和旧密码相同!", false);
					// showAlertDialog(getResources().getString(
					// R.string.reset_new_old_same));
					// Toast.makeText(this, "新密码和旧密码相同。。", 3000).show();
				} else {// 修改成功
					reset_success = true;
					String oldWordString = et_old_password.getText().toString()
							.trim();
					String newWord = et_new_password.getText().toString()
							.trim();

					getNewPassword(oldWordString, newWord);// 修改密码
				}
			}
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

	private void getNewPassword(String oldPassword, String newPassword) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在修改密码...");
		progressDialog.show();

		String url = AppConfig.URL_ACCOUNT
				+ "password/modify.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&" + "old-password="
				+ oldPassword + "&new-password=" + newPassword;
		Log.d("USER", url);
		StringRequest newPasswordRequest = new StringRequest(Method.PUT, url,
				null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						progressDialog.dismiss();
						InternetDialog internetDialog = new InternetDialog(
								ResetPasswordActivity.this);
						internetDialog.showInternetDialog("修改密码成功!", true);
						// 两秒后返回上一页
						Timer timer = new Timer(true);
						timer.schedule(timerTask, 2000);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							// showAlertDialog(jsonObject.getString("description"));
							InternetDialog internetDialog = new InternetDialog(
									ResetPasswordActivity.this);
							internetDialog.showInternetDialog(
									jsonObject.getString("description"), false);
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				});

		newPasswordRequest.setTag("newPasswordRequest");
		MyApplication.getRequestQueue().add(newPasswordRequest);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("newPasswordRequest");
		super.onDestroy();
	}

	private void showAlertDialog(String message) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		dialog.setCancelable(false);
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_reset_password_layout);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);
		tv_message.setText(message);

		TextView tv_confirm = (TextView) window.findViewById(R.id.tv_confirm);
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (reset_success) {// 修改成功
					finish(); // 结束活动
				} else {
					dialog.dismiss();
				}
			}
		});
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
