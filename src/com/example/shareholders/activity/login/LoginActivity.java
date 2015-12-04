package com.example.shareholders.activity.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
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
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.example.shareholders.service.GetInformationAfterLoginService;
import com.example.shareholders.service.GetWeiBoInfoService;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.DateComparator;
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
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.component.view.LoadingBar;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity implements AfterLogin {
	boolean fromstartactivity;
	boolean isFrist;
	//游客身份进入
	@ViewInject(R.id.tv_visitor)
	private TextView tv_visitor;
	// 密码
	@ViewInject(R.id.et_password)
	private ClearEditText et_password;
	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;
	// 注册
	@ViewInject(R.id.tv_register)
	private TextView tv_register;

	// 忘记密码
	@ViewInject(R.id.tv_forget_password)
	private TextView tv_forget_password;
	// 账号
	@ViewInject(R.id.et_account)
	private ClearEditText et_account;
	// 登录按钮
	@ViewInject(R.id.tv_login)
	private TextView tv_login;
	// private ProgressDialog progressDialog;
	// 请稍等进度框
	private LoadingDialog loadingDialog;
	//第三方登录回调标志
	private int type = 0;
	/**
	 * 若账号或密码为空，enable为false
	 */
	private boolean account_enable = false;
	private boolean passward_enable = false;
	private boolean login_enable = false;
	public static Tencent mTencent;
	private AuthInfo mAuthInfo;

	DbUtils dbUtils;
	// 微博sso
	SsoHandler mSsoHandler;
	// 登录接收器
	LoginReceiver loginReceiver;
	private static final int DISABLE_LOGIN = 0;// 不可登录
	private static final int ENABLE_LOGIN = 1; // 可以登录
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 不可登录，登录按钮设置为灰色
			case DISABLE_LOGIN:
				login_enable = false;
				tv_login.setClickable(false);
				tv_login.setBackgroundResource(R.drawable.btn_login);

				break;
			case ENABLE_LOGIN:
				login_enable = account_enable && passward_enable;
				// 账号和密码同时不为空时，才可登录
				if (login_enable) {
					tv_login.setClickable(true);
					tv_login.setBackgroundResource(R.drawable.btn_login_enable);
				}

				break;
			default:
				break;
			}
		}
	};

	private AlertDialog mDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		dbUtils = DbUtils.create(this);
		// 下滑线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_forget_password.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_visitor.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		// 默认手机号
		if (RsSharedUtil.getString(getApplicationContext(), AppConfig.PHONE_NUMBER) != "") {
			et_account.setText(RsSharedUtil.getString(getApplicationContext(), AppConfig.PHONE_NUMBER));
			account_enable = true;
		}
		// 设置账号和密码的TextWatcher
		setTextWatcher();
		mDialog = new AlertDialog.Builder(this).create();
		mTencent = Tencent.createInstance(AppConfig.TENCENT_APP_ID, this);
		// progressDialog = new ProgressDialog(this);
		loadingDialog = new LoadingDialog(LoginActivity.this, "请稍等...", false, false, null);
		fromstartactivity = false;
		try {
			Intent intent3 = getIntent();	
			fromstartactivity = intent3.getBooleanExtra("fromstartactivity", false);
			if (fromstartactivity) {
				tv_return.setVisibility(View.GONE);
				tv_visitor.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setTextWatcher() {
		// 密码
		et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (DateComparator.passwordFormat(arg0 + "")) {
					Message msg = new Message();
					msg.what = ENABLE_LOGIN;
					handler.sendMessage(msg);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
					if (!et_account.getText().toString().equals("")) {
						account_enable = true;
					}
					passward_enable = true;
					if (!login_enable && DateComparator.passwordFormat(et_password.getText().toString())) {
						msg.what = ENABLE_LOGIN;
						handler.sendMessage(msg);
					}
				}
			}
		});

		// 账号
		et_account.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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

	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(loginReceiver);
		MyApplication.getRequestQueue().cancelAll("getMsg");
		MyApplication.getRequestQueue().cancelAll("thirdLogin");
		MyApplication.getRequestQueue().cancelAll("valifyMessage");
		MyApplication.getRequestQueue().cancelAll("IMDetails");
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
					RsSharedUtil.putString(LoginActivity.this, AppConfig.ACCESS_TOKEN,
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
					Intent getInformationIntent = new Intent(LoginActivity.this, GetInformationAfterLoginService.class);
					startService(getInformationIntent);					
					//从startactivity来时mainactivity还没启动
					if (fromstartactivity) {
						Intent intent=new Intent();
						intent.setClass(LoginActivity.this,MainActivity.class);
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
							// 跳到调研页,即index = 1;
							Intent intent = new Intent("LoginReceiver");
							Bundle bundle = new Bundle();
							bundle.putInt("index", 1);
							intent.putExtras(bundle);
							sendBroadcast(intent);
						} else {

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
						+ RsSharedUtil.getString(LoginActivity.this, "access_token");

				http2.send(HttpRequest.HttpMethod.GET, url2, null, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg2) {
						try {
							JSONObject jsonObject2 = new JSONObject(arg2.result);
							RsSharedUtil.putString(LoginActivity.this, "survey_right", jsonObject2.getString("type"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});// 第二个请求：请求用户权限

			}
		});// 第一个请求：登录
	}

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
		url += RsSharedUtil.getString(LoginActivity.this, AppConfig.ACCESS_TOKEN);
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

	@OnClick({ R.id.tv_return, R.id.tv_login, R.id.iv_shareholder, R.id.tv_forget_password, R.id.tv_choose_area_number,
			R.id.tv_register, R.id.iv_qq, R.id.iv_weibo, R.id.iv_wechat,R.id.tv_visitor })
	private void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

		// 退出
		case R.id.tv_return:
			finish();
			break;
		// 选择区号
		case R.id.tv_choose_area_number:
			intent.setClass(this, AreaCodeActivity.class);
			startActivity(intent);
			break;
		//游客进入
		case R.id.tv_visitor:
			intent.setClass(LoginActivity.this,MainActivity.class);
			intent.putExtra("isfromstart", true);
			startActivity(intent);
			break;
		// 登录
		case R.id.tv_login:
			if (!BtnClickUtils.isFastDoubleClick()) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(tv_login.getWindowToken(), 0);
				Login(et_account.getText().toString(), et_password.getText().toString());
			}
			break;
		// 投行圈登录
		case R.id.iv_shareholder:
			if (!BtnClickUtils.isFastDoubleClick()) {
				intent.setClass(this, TouHQLoginActivity.class);
				intent.putExtra("fromstartactivity", getIntent().getBooleanExtra("fromstartactivity", false));
				intent.putExtra("isFrist", getIntent().getBooleanExtra("isFrist",true));
				startActivity(intent);
			}
			break;

		// 忘记密码
		case R.id.tv_forget_password:
			if (!BtnClickUtils.isFastDoubleClick()) {
				intent.setClass(this, FindPasswordActivity.class);
				intent.putExtra("flag", 0);
				startActivity(intent);
			}
			break;
		// 去注册
		case R.id.tv_register:
			if (!BtnClickUtils.isFastDoubleClick()) {
				intent.setClass(this, RegisterActivity.class);
				intent.putExtra("isFirst", isFrist);
				startActivity(intent);
			}
			break;
		// QQ授权登录
		case R.id.iv_qq:
			type = 0;
			if (!BtnClickUtils.isFastDoubleClick()) {
				mTencent.login(LoginActivity.this, "all", loginListener);
			}
			break;
		case R.id.iv_weibo:
			type = 1;
			if (!BtnClickUtils.isFastDoubleClick()) {
				mAuthInfo = new AuthInfo(this, AppConfig.WEIBO_APP_KEY, AppConfig.REDIRECT_URL, AppConfig.SCOPE);
				mSsoHandler = new SsoHandler(this, mAuthInfo);
				mSsoHandler.authorize(new AuthListener(LoginActivity.this));
			}
			break;
		case R.id.iv_wechat:
			if (!BtnClickUtils.isFastDoubleClick()) {
				RsSharedUtil.putBoolean(getApplicationContext(), "wechatRegister", false);
				SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				MyApplication.getIWXAPI().sendReq(req);
			}
		default:
			break;
		}
	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d("腾讯登录的json", values.toString());
			/*
			 * 获取登录后的用户信息
			 */
			try {
				String token = values.getString(Constants.PARAM_ACCESS_TOKEN);
				String expires = values.getString(Constants.PARAM_EXPIRES_IN);
				String openId = values.getString(Constants.PARAM_OPEN_ID);

				if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
					mTencent.setAccessToken(token, expires);
					mTencent.setOpenId(openId);
				}
				String QQ_URL = AppConfig.URL_THIRD + "qq/login.json?accessToken=" + token;
				Log.d("QQ_URL", QQ_URL);
				thirdLogin(QQ_URL);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("哈哈哈哈", "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
		if (type==0) {
			// 腾讯回调
			mTencent.onActivityResultData(requestCode, resultCode, data, loginListener);
			if (requestCode == Constants.REQUEST_API) {
				if (resultCode == Constants.RESULT_LOGIN) {
					Tencent.handleResultData(data, loginListener);

				}
			}
		}
		if (type==1) {
			// 微博回调
			if (mSsoHandler != null) {
				mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class AuthListener implements WeiboAuthListener {
		private Oauth2AccessToken mAccessToken;
		private Context context;

		public AuthListener(Context context) {
			this.context = context;
		}

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {
				// 保存 Token 到 SharedPreferences
				try {
					/*
					 * 获取微博登录后的信息
					 */
					Intent intent = new Intent(context, GetWeiBoInfoService.class);
					Bundle bundle = new Bundle();
					bundle.putString("access_token", mAccessToken.getToken());
					bundle.putString("uid", mAccessToken.getUid());
					intent.putExtras(bundle);
					context.startService(intent);
					// 是第三方注册，并且type为WEIBO
					RsSharedUtil.putBoolean(context, AppConfig.IS_FORM_THIRD, true);
					RsSharedUtil.putString(context, AppConfig.THIRD_TYPE, "WEIBO");
					Log.d("微博啊微博啊", mAccessToken.getToken());
					RsSharedUtil.putString(context, AppConfig.THIRD_ACCESS_TOKEN, mAccessToken.getToken());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String WEIBO_URL = AppConfig.URL_THIRD + "weibo/login.json?accessToken=" + mAccessToken.getToken();
				Log.d("WEIBO_URL", WEIBO_URL);
				thirdLogin(WEIBO_URL);
			} else {
				// 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
				String code = values.getString("code");

			}
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(context, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * QQ登录回调
	 * 
	 * @author warren
	 * 
	 */
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			if (null == response) {
				new AlertDialog.Builder(LoginActivity.this).setTitle("返回为空").setMessage("登录失败")
						.setNegativeButton("知道了", null).create().show();
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				new AlertDialog.Builder(LoginActivity.this).setTitle("返回为空").setMessage("登录失败")
						.setNegativeButton("知道了", null).create().show();
				return;
			}
			Log.d("腾讯登录的json", response.toString());
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("onCancel", "onCancel");
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			Log.d("error", arg0.toString());
		}
	}

	/**
	 * 登录失败时弹出的对话框
	 */
	private void showLoginFailedDialog(String msg) {
		mDialog = new AlertDialog.Builder(this).create();
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_login_faileed_layout);

		TextView tv_wrong_msg = (TextView) mDialog.getWindow().findViewById(R.id.tv_wrong_msg);

		tv_wrong_msg.setText(msg);

		TextView tv_confirm = (TextView) mDialog.getWindow().findViewById(R.id.tv_confirm);
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});

	}

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
		finish();
	}

	/**
	 * 第三方登录
	 * 
	 * @param url
	 */
	private void thirdLogin(String url) {
		// progressDialog.show();
		// progressDialog.setMessage("请稍等...");
		loadingDialog.showLoadingDialog();
		StringRequest stringRequest = new StringRequest(url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub

				Log.d("response", response);
				if (response.trim().equals("false"))
					showdialog("请先绑定注册！");
				else {
					try {

						JSONObject jsonObject = new JSONObject(response);
						RsSharedUtil.putString(LoginActivity.this, AppConfig.ACCESS_TOKEN,
								jsonObject.getString("access_token"));
						Intent getInformationIntent = new Intent(LoginActivity.this,
								GetInformationAfterLoginService.class);
						startService(getInformationIntent);
						// 跳到调研页,即index = 1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// TODO Auto-generated method stub
					//从startactivity来时mainactivity还没启动
					if (fromstartactivity) {
						Intent intent=new Intent();
						intent.setClass(LoginActivity.this,MainActivity.class);
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
							// 跳到调研页,即index = 1;
							Intent intent = new Intent("LoginReceiver");
							Bundle bundle = new Bundle();
							bundle.putInt("index", 1);
							intent.putExtras(bundle);
							sendBroadcast(intent);
						} else {

							Intent intent = new Intent("LoginReceiver");
							Bundle bundle = new Bundle();
							bundle.putInt("index", -1);
							intent.putExtras(bundle);
							sendBroadcast(intent);
						}
					}
					// progressDialog.dismiss();
					loadingDialog.dismissDialog();
					finish();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				// progressDialog.dismiss();
				loadingDialog.dismissDialog();
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					showdialog(jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		stringRequest.setTag("thirdLogin");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * show出各种提示对话框
	 */
	private void showdialog(String tips) {
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_survey_list2);
		((TextView) mDialog.getWindow().findViewById(R.id.tv_dialog_content)).setText(tips);
		mDialog.getWindow().findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
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
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (event.getRawX() > left && event.getRawX() < right && event.getRawY() > top
					&& event.getRawY() < bottom) {
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
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
