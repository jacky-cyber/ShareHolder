package com.example.shareholders.activity.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.example.shareholders.MainActivity;
import com.example.shareholders.R;
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.LocalFollowStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalFollowedStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalMutualStockFriend;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.example.shareholders.service.GetInformationAfterLoginService;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_tou_hqlogin)
public class TouHQLoginActivity extends Activity implements AfterLogin{

	// 密码
	@ViewInject(R.id.et_password)
	private ClearEditText et_password;
	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;
	// 忘记密码
	@ViewInject(R.id.tv_forget_password)
	private TextView tv_forget_password;
	// 账号
	@ViewInject(R.id.et_account)
	private ClearEditText et_account;
	// 登录按钮
	@ViewInject(R.id.tv_login)
	private TextView tv_login;
	//加载框
	private LoadingDialog loadingDialog;
	//数据库
	private DbUtils dbUtils;
	boolean fromstartactivity;
	boolean isFrist;
	
	private LoginReceiver loginReceiver;

	/**
	 * 若账号或密码为空，enable为false
	 */
	private boolean account_enable = false;
	private boolean passward_enable = false;
	private boolean login_enable = false;

	private static final int DISABLE_LOGIN = 0;// 不可登录
	private static final int ENABLE_LOGIN = 1; // 可以登录s
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 不可登录，登录按钮设置为灰色
			case DISABLE_LOGIN:
				login_enable = false;
				tv_login.setEnabled(false);
				tv_login.setBackgroundResource(R.drawable.btn_login);

				break;
			case ENABLE_LOGIN:
				login_enable = account_enable && passward_enable;
				// 账号和密码同时不为空时，才可登录
				if (login_enable) {
					tv_login.setEnabled(true);
					tv_login.setBackgroundResource(R.drawable.btn_login_enable);
				}

				break;
			default:
				break;
			}
		};
	};

	private AlertDialog mDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		
		loadingDialog = new LoadingDialog(TouHQLoginActivity.this);
		try {
			dbUtils = DbUtils.create(getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 下滑线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_forget_password.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		fromstartactivity = getIntent().getBooleanExtra("fromstartactivity",false);
		isFrist = getIntent().getBooleanExtra("isFrist", true);

		// 设置账号和密码的TextWatcher
		setTextWatcher();

	}

	private void setTextWatcher() {
		// 密码
		et_password.addTextChangedListener(new TextWatcher() {

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
				if (et_password.getText().toString().equals("")) {
					passward_enable = false;
					msg.what = DISABLE_LOGIN;
					handler.sendMessage(msg);
				} else {
					passward_enable = true;
					if (!login_enable) {
						msg.what = ENABLE_LOGIN;
						handler.sendMessage(msg);
					}
				}
			}
		});

		// 账号
		et_account.addTextChangedListener(new TextWatcher() {

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
				if (et_account.getText().toString().equals("")) {
					account_enable = false;
					msg.what = DISABLE_LOGIN;
					handler.sendMessage(msg);
				} else {
					account_enable = true;
					if (!login_enable) {
						msg.what = ENABLE_LOGIN;
						handler.sendMessage(msg);
					}
				}
			}
		});

	}

	@OnClick({ R.id.tv_return, R.id.tv_login })
	private void onClick(View v) {
		switch (v.getId()) {
		// 退出
		case R.id.tv_return:
			finish();
			break;

		case R.id.tv_login:
//			if (!et_password.getText().toString().equals("123456")) {
//				showLoginFailedDialog(getResources().getString(
//						R.string.wrong_passward));
//			}
//			new LoginActivity().Login(et_account.getText().toString(), et_password.getText().toString());
			Login(et_account.getText().toString(), et_password.getText().toString());
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
		mDialog.getWindow()
				.setContentView(R.layout.dialog_login_faileed_layout);

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
	
	/**
	 * 登录
	 */
	public void Login(final String userName, String password) {
		// progressDialog.show();
		// progressDialog.setMessage("请稍等...");
		loadingDialog.showLoadingDialog();
		HttpUtils http = new HttpUtils();
		String url = "http://120.24.254.176:8080/shareholder-server/oauth/token?client_id=app-client&grant_type=password&scope=read&username="
				+ userName + "&password=" + password;

		Log.d("登录url", url);
		http.send(HttpRequest.HttpMethod.GET, url, null, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				showLoginFailedDialog(getResources().getString(R.string.wrong_passward));
				// progressDialog.dismiss();
				loadingDialog.dismissDialog();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.d("ResponseInfo", arg0.toString());
				try {
					RsSharedUtil.putString(getApplicationContext(), AppConfig.PHONE_NUMBER, userName);

					JSONObject jsonObject = new JSONObject(arg0.result);
					Log.d("getaccesstoken", jsonObject.getString("access_token"));
					RsSharedUtil.putString(TouHQLoginActivity.this, AppConfig.ACCESS_TOKEN,
							jsonObject.getString("access_token"));

					Log.d("logactivity", jsonObject.getString("access_token"));

					try {
						dbUtils.dropTable(LocalFollowedStockFriend.class);
						dbUtils.dropTable(LocalFollowStockFriend.class);
						dbUtils.dropTable(LocalMutualStockFriend.class);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getIMDetails();

					Log.d("access_token", jsonObject.getString("access_token"));
					Intent getInformationIntent = new Intent(TouHQLoginActivity.this, GetInformationAfterLoginService.class);
					startService(getInformationIntent);					
					//从startactivity来时mainactivity还没启动
					if (fromstartactivity) {
						Intent intent=new Intent();
						intent.setClass(TouHQLoginActivity.this,MainActivity.class);
						intent.putExtra("isfromstart", true);
						startActivity(intent);
					} else {
						isFrist = false;
						try {
							Intent intent2 = getIntent();
							isFrist = intent2.getBooleanExtra("isFirst", true);
							Log.d("gettttt0", isFrist + "");
						} catch (Exception e) {
							Log.d("gettttt2", "asd");
							// TODO: handle exception
						}
						if (isFrist) {
							Log.d("isfrist", isFrist+"");
							// 跳到调研页,即index = 1;
							Intent intent = new Intent("LoginReceiver");
							Bundle bundle = new Bundle();
							bundle.putInt("index", 1);
							intent.putExtras(bundle);
							sendBroadcast(intent);
						} else {

							Log.d("isfrist", "22222");
							Intent intent = new Intent("LoginReceiver");
							Bundle bundle = new Bundle();
							bundle.putInt("index", -1);
							intent.putExtras(bundle);
							sendBroadcast(intent);
						}
					}
					

					// progressDialog.dismiss();
					loadingDialog.dismissDialog();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				HttpUtils http2 = new HttpUtils();
				String url2 = AppConfig.SURVEY_RIGHT + "type.json?access_token="
						+ RsSharedUtil.getString(TouHQLoginActivity.this, "access_token");

				http2.send(HttpRequest.HttpMethod.GET, url2, null, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg2) {
						try {
							JSONObject jsonObject2 = new JSONObject(arg2.result);
							RsSharedUtil.putString(TouHQLoginActivity.this, "survey_right", jsonObject2.getString("type"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});// 第二个请求：请求用户权限

			}
		});// 第一个请求：登录
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

	/**
	 * 登录环信
	 */
	private void loginIM(String response) {
		String imUserName = null;
		String imPassword = null;
		try {
			// 截取用户名密码
			JSONObject jsonObject = new JSONObject(response);
			imUserName = jsonObject.getString("imUserName");
			imPassword = jsonObject.getString("imPassword");
			RsSharedUtil.putString(getApplicationContext(), AppConfig.IMUSER_NAME, imUserName);
			RsSharedUtil.putString(getApplicationContext(), AppConfig.IMUSER_PASSWORD, imPassword);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 环信登录
		EMChatManager.getInstance().login(imUserName, imPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.d("main", "登陆聊天服务器成功！");
				// 设置环信自动登录
				EMChat.getInstance().setAutoLogin(true);
			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.d("main", "登陆聊天服务器失败！" + arg1);
				Log.d("main", "登陆聊天服务器失败！");
			}
		});

	}

	/**
	 * 获取环信信息
	 */
	private String getIMDetails() {
		String url = AppConfig.URL_ACCOUNT + "im/user.json?access_token=";
		url += RsSharedUtil.getString(TouHQLoginActivity.this, AppConfig.ACCESS_TOKEN);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.d("main", response);
				loginIM(response);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d("main", error.toString());
			}
		});
		stringRequest.setTag("IMDetails");
		MyApplication.getRequestQueue().add(stringRequest);
		return "error";
	}

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
		Log.d("isfrist", "3333");
		finish();
	}

}
