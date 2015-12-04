package com.example.shareholders.activity.login;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_new_password)
public class NewPasswordActivity extends Activity {

	// 新密码
	@ViewInject(R.id.et_new_password)
	private ClearEditText et_new_password;

	// 再次输入新密码
	@ViewInject(R.id.et_new_password_again)
	private ClearEditText et_new_password_again;

	// 确定
	@ViewInject(R.id.tv_confirm)
	private TextView tv_confirm;

	private AlertDialog mDialog = null;

	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;

//	ProgressDialog progressDialog;
	private LoadingDialog loadingDialog;
	/**
	 * 若账号或密码为空，enable为false
	 */
	private boolean password_enable = false;
	private boolean passward_again_enable = false;
	private boolean confirm_enable = false;

	private static final int DISABLE_LOGIN = 0;// 不可登录
	private static final int ENABLE_LOGIN = 1; // 可以登录s
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 不可登录，登录按钮设置为灰色
			case DISABLE_LOGIN:
				confirm_enable = false;
				tv_confirm.setClickable(false);
				tv_confirm.setBackgroundResource(R.drawable.btn_login);

				break;
			case ENABLE_LOGIN:
				confirm_enable = password_enable && passward_again_enable;
				// 账号和密码同时不为空时，才可登录
				if (confirm_enable) {
					tv_confirm.setClickable(true);
					tv_confirm
							.setBackgroundResource(R.drawable.btn_login_enable);
				}

				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		mDialog = new AlertDialog.Builder(this).create();
		// 下划线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		setTextWatcher();
		// 密码输入是否符合格式
		et_new_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (et_new_password.hasFocus() == false) {
					// 密码输入是否符合格式
					if (!DateComparator.passwordFormatRegister(et_new_password
							.getText().toString().trim())) {
						showdialog("密码为6位数字");
					}
				}
			}
		});
		et_new_password_again
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						if (et_new_password_again.hasFocus() == false) {
							// 密码输入是否符合格式
							if (!(et_new_password_again.getText().toString()
									.trim()).equals(et_new_password_again
									.getText().toString().trim())) {
								showdialog("两次密码输入必须一样！");
							}
						}
					}
				});
	}

	private void setTextWatcher() {
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
					password_enable = false;
					msg.what = DISABLE_LOGIN;
					handler.sendMessage(msg);
				} else {
					password_enable = true;
					if (!confirm_enable) {
						msg.what = ENABLE_LOGIN;
						handler.sendMessage(msg);
					}
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
					passward_again_enable = false;
					msg.what = DISABLE_LOGIN;
					handler.sendMessage(msg);
				} else {
					passward_again_enable = true;
					if (!confirm_enable) {
						msg.what = ENABLE_LOGIN;
						handler.sendMessage(msg);
					}
				}
			}
		});
//		progressDialog = new ProgressDialog(this);
		loadingDialog = new LoadingDialog(NewPasswordActivity.this);
	}

	@OnClick({ R.id.tv_return, R.id.tv_confirm })
	private void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		// 退出
		case R.id.tv_return:
			finish();
			break;
		// 确定
		case R.id.tv_confirm:
			if (!(et_new_password_again.getText().toString().trim())
					.equals(et_new_password_again.getText().toString().trim())) {
				showdialog("两次密码输入必须一样！");
			} else if (et_new_password_again.getText().toString().trim()
					.equals("")) {
				showdialog("密码不能为空");
			} else {
//				progressDialog.setMessage("请稍等...");
//				progressDialog.show();
				loadingDialog.setLoadingString("请稍等...");
				loadingDialog.showLoadingDialog();
				forgetPassWord(et_new_password.getText().toString().trim());
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 登录失败时弹出的对话框
	 */
	private void showLoginFailedDialog(String msg) {
		mDialog = new AlertDialog.Builder(this).create();
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_new_password_layout);

		TextView tv_wrong_msg = (TextView) mDialog.getWindow().findViewById(
				R.id.tv_wrong_msg);

		tv_wrong_msg.setText(msg);

		TextView tv_confirm = (TextView) mDialog.getWindow().findViewById(
				R.id.tv_confirm);
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();

			}
		});

	}

	TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private void forgetPassWord(String password) {
		String url = AppConfig.URL_ACCOUNT
				+ "password/reset.json?verificationCode=";
		url = url
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.VERIFY_CODE);
		url = url
				+ "&contact="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.PHONE_NUMBER);
		url = url + "&password=" + password;
		Log.d("忘记密码url", url);
		StringRequest stringRequest = new StringRequest(url, new JSONObject(),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
//						showdialog("修改密码成功");
						Intent intent = new Intent("LoginReceiver");
						sendBroadcast(intent);
//						progressDialog.dismiss();
						loadingDialog.dismissDialog();
						//提示成功
						LoadingDialog loadingDialog2 = new LoadingDialog(NewPasswordActivity.this);
						loadingDialog2.setFlag(true);
						loadingDialog2.setInternetString("修改密码成功");
						loadingDialog2.showInternetDialog();
						
						startActivity(new Intent(NewPasswordActivity.this,
								LoginActivity.class));
						Timer timer = new Timer();
						timer.schedule(task, 2000);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
//						progressDialog.dismiss();
						loadingDialog.dismissDialog();
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							showdialog(jsonObject.getString("description"));

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
		stringRequest.setTag("forgetPassWord");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("forgetPassWord");
		super.onDestroy();
	};

	/**
	 * show出各种提示对话框
	 */
	private void showdialog(String tips) {
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_survey_list2);
		((TextView) mDialog.getWindow().findViewById(R.id.tv_dialog_content))
				.setText(tips);
		mDialog.getWindow().findViewById(R.id.tv_confirm)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
				});
	}
}
