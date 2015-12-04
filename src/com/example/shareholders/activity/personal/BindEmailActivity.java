package com.example.shareholders.activity.personal;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.DialogManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_bind_email)
public class BindEmailActivity extends Activity {
	// 邮箱号
	@ViewInject(R.id.et_tele)
	private ClearEditText et_tele;
	DialogManager dialogManager;
	// 邮箱格式是否正确
	boolean isProperEmail = false;
	boolean isProperCode = false;
	// 发送验证码
	@ViewInject(R.id.tv_get_verification_code)
	private TextView tv_get_verification_code;
	// 确定
	@ViewInject(R.id.tv_register)
	private TextView tv_register;
	// 验证码
	@ViewInject(R.id.et_verify_code)
	private ClearEditText et_verify_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		dialogManager = new DialogManager(BindEmailActivity.this);
		tv_get_verification_code.setClickable(false);
		et_tele.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (DateComparator.checkEmail(et_tele.getText().toString())) {
					isProperEmail = true;
					tv_register.setClickable(true);
					tv_get_verification_code.setTextColor(getResources()
							.getColor(R.color.get_code_able));
					tv_get_verification_code.setClickable(true);
					if (isProperCode) {
						tv_register.setClickable(true);
						tv_register
								.setBackgroundResource(R.drawable.btn_login_enable);
					} else {
						tv_register.setClickable(false);
						tv_register.setBackgroundResource(R.drawable.btn_login);
					}
				} else {
					tv_get_verification_code.setClickable(false);
					isProperEmail = false;
					tv_get_verification_code.setTextColor(getResources()
							.getColor(R.color.get_code_unable));
					tv_get_verification_code.setClickable(false);

				}
			}
		});
		et_verify_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (!et_verify_code.getText().toString().trim().equals("")) {
					isProperCode = true;
					if (isProperEmail) {
						tv_register.setClickable(true);
						tv_register
								.setBackgroundResource(R.drawable.btn_login_enable);
					} else {
						tv_register.setClickable(false);
						tv_register.setBackgroundResource(R.drawable.btn_login);
					}
				} else {
					isProperCode = false;
					tv_register.setClickable(false);
					tv_register.setBackgroundResource(R.drawable.btn_login);
				}
			}
		});
	}

	@OnClick({ R.id.rl_return, R.id.tv_get_verification_code, R.id.tv_register })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		// 获取验证码
		case R.id.tv_get_verification_code:
			getMsg(et_tele.getText().toString());
			break;
		// 确认绑定
		case R.id.tv_register:
			bindEmail(et_tele.getText().toString(), et_verify_code.getText()
					.toString());
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("getMsg");
		MyApplication.getRequestQueue().cancelAll("bindEmail");
		super.onDestroy();
	}

	private void bindEmail(final String email, String verificationCode) {
		String url = AppConfig.URL_ACCOUNT
				+ "bind.json?contact="
				+ email
				+ "&verificationCode="
				+ verificationCode
				+ "&access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						dialogManager.ShowBlueDialog();
						dialogManager.setBlueMessage("绑定成功");
						dialogManager.BluenoCancel();
						dialogManager.BluenoMessageIcon();
						dialogManager
								.setBluePositiveButton(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										dialogManager.dismiss();
										finish();
									}
								});
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							dialogManager.ShowBlueDialog();
							dialogManager.setBlueMessage(jsonObject
									.getString("description"));
							dialogManager.BluenoCancel();
							dialogManager.BluenoMessageIcon();
							dialogManager
									.setBluePositiveButton(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialogManager.dismiss();
										}
									});
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
		stringRequest.setTag("bindEmail");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 获取短信验证码
	 * 
	 * @param phoneNumber
	 * @param verificationCode
	 */
	public void getMsg(final String mail) {
		String url = AppConfig.URL_ACCOUNT
				+ "message/send/user.json?contact="
				+ mail
				+ "&type=BIND&access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						dialogManager.ShowBlueDialog();
						dialogManager.setBlueMessage("验证码已经发送到" + mail);
						dialogManager.BluenoCancel();
						dialogManager.BluenoMessageIcon();
						dialogManager
								.setBluePositiveButton(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										dialogManager.dismiss();
									}
								});
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("草草草", error.toString());
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("草草草", error.data().toString());
							dialogManager.ShowBlueDialog();

							dialogManager.setBlueMessage(jsonObject
									.getString("description"));
							dialogManager.BluenoCancel();
							dialogManager.BluenoMessageIcon();
							dialogManager
									.setBluePositiveButton(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											dialogManager.dismiss();
										}
									});
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

				});
		stringRequest.setTag("getMsg");
		MyApplication.getRequestQueue().add(stringRequest);

	}
}
