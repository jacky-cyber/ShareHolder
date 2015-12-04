package com.example.shareholders.activity.login;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.example.shareholders.service.GetWeiBoInfoService;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends Activity implements AfterLogin {
	boolean isFrist;
	// 背景
	@ViewInject(R.id.background)
	private RelativeLayout background;
	// 地区代码
	@ViewInject(R.id.tv_area_code)
	private TextView tv_area_code;
	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;
	// QQ绑定
	@ViewInject(R.id.iv_qq)
	private ImageView iv_qq;
	public static Tencent mTencent;
	// 选择地区代码
	@ViewInject(R.id.tv_choose_area_number)
	private TextView tv_choose_area_number;
	// 获取验证码
	@ViewInject(R.id.tv_get_verification_code)
	private TextView tv_get_verification_code;
	// 注册
	@ViewInject(R.id.tv_register)
	private TextView tv_register;
	// 电话号码
	@ViewInject(R.id.et_tele)
	private TextView et_tele;
	// 验证码
	@ViewInject(R.id.et_verify_code)
	private TextView et_verify_code;
	// 登录
	@ViewInject(R.id.tv_login)
	private TextView tv_login;
	// 用投行圈账号直接登录
	@ViewInject(R.id.tv_login_directly)
	private TextView tv_login_directly;
	// 对话框
	private AlertDialog mDialog = null;
	// 微博sso
	SsoHandler mSsoHandler;
	//标注第三方注册
	private int type = 0;

	// 登录接收器
	LoginReceiver loginReceiver;
	// 获取地区代码
	BitmapUtils bitmapUtils;
	private static final int GET_CODE = 2;
	private AuthInfo mAuthInfo;
	private static final int DISABLE_REGISTER = 0;// 不可注册
	private static final int ENABLE_REGISTER = 1; // 可以注册
	private boolean account_enable = false;
	private boolean verify_enable = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 不可登录，登录按钮设置为灰色
			case DISABLE_REGISTER:
				tv_register.setClickable(false);
				tv_register.setBackgroundResource(R.drawable.btn_login);
				break;
			case ENABLE_REGISTER:
				tv_register.setClickable(true);
				tv_register.setBackgroundResource(R.drawable.btn_login_enable);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		// 增加下滑线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_get_verification_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_choose_area_number.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_get_verification_code.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		// 获取tencent实例
		mTencent = Tencent.createInstance(AppConfig.TENCENT_APP_ID, this);
		mDialog = new AlertDialog.Builder(this).create();
		// 提示用户可以用投行圈账号登录
		showdialog(getResources().getString(R.string.tips));
		setTextWatcher();
		tv_get_verification_code.setTextColor(getResources().getColor(R.color.get_code_unable));
		tv_get_verification_code.setClickable(false);
		bitmapUtils = BitmapUtilFactory.getInstance();
		isFrist = false;
		try {
			Intent intent2 = getIntent();
			Bundle bundle2 = intent2.getExtras();
			isFrist = bundle2.getBoolean("isFrist");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(loginReceiver);
		MyApplication.getRequestQueue().cancelAll("getMsg");
		MyApplication.getRequestQueue().cancelAll("valifyMessage");
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

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			Log.d("腾讯登录的json", values.toString());
			/*
			 * 获取登录后的用户信息
			 */
			try {
				String token = values.getString(Constants.PARAM_ACCESS_TOKEN);
				RsSharedUtil.putString(getApplicationContext(), AppConfig.THIRD_ACCESS_TOKEN, token);
				String expires = values.getString(Constants.PARAM_EXPIRES_IN);
				String openId = values.getString(Constants.PARAM_OPEN_ID);
				if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
					mTencent.setAccessToken(token, expires);
					mTencent.setOpenId(openId);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UserInfo mInfo = new UserInfo(RegisterActivity.this, mTencent.getQQToken());
			mInfo.getUserInfo(new IUiListener() {

				@Override
				public void onError(UiError arg0) {
					// TODO Auto-generated method stub
					Log.d("错误信息", arg0.toString());
				}

				@Override
				public void onComplete(Object arg0) {
					// TODO Auto-generated method stub
					Log.d("用户信息", arg0.toString());
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(arg0.toString());
						// 将用户名和头像url写入share_preferences
						RsSharedUtil.putString(getApplicationContext(), AppConfig.FIGURE_URL,
								jsonObject.getString(AppConfig.FIGURE_URL));
						RsSharedUtil.putString(getApplicationContext(), AppConfig.NICKNAME,
								jsonObject.getString(AppConfig.NICKNAME));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 是第三方注册，并且type为QQ
					RsSharedUtil.putBoolean(getApplicationContext(), AppConfig.IS_FORM_THIRD, true);
					RsSharedUtil.putString(getApplicationContext(), AppConfig.THIRD_TYPE, "QQ");
					RsSharedUtil.putString(RegisterActivity.this, AppConfig.OPENID, "");
					startActivity(new Intent(RegisterActivity.this, ServerTermActivity.class));

				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub

				}
			});

		}
	};

	@OnClick({ R.id.tv_return, R.id.iv_qq, R.id.tv_choose_area_number, R.id.tv_login, R.id.tv_login_directly,
			R.id.iv_weibo, R.id.tv_get_verification_code, R.id.tv_register, R.id.iv_wechat })
	private void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_return:
			finish();
			break;
		// qq绑定登录
		case R.id.iv_qq:
			type =0;
			mTencent.login(RegisterActivity.this, "all", loginListener);
			break;
		case R.id.tv_choose_area_number:
			intent.setClass(this, AreaCodeActivity.class);
			startActivityForResult(intent, GET_CODE);
			break;
		// 登录
		case R.id.tv_login:
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			break;
		// 用投行圈账号直接登录
		case R.id.tv_login_directly:
			intent.setClass(this, TouHQLoginActivity.class);
			startActivity(intent);
			break;
		// 微博
		case R.id.iv_weibo:
			type = 1;
			mAuthInfo = new AuthInfo(this, AppConfig.WEIBO_APP_KEY, AppConfig.REDIRECT_URL, AppConfig.SCOPE);
			mSsoHandler = new SsoHandler(this, mAuthInfo);
			mSsoHandler.authorize(new AuthListener(RegisterActivity.this));
			break;
		// 获取验证码
		case R.id.tv_get_verification_code:
			getVerifyCode(getApplicationContext(), et_tele.getText().toString().trim());
			break;
		// 注册
		case R.id.tv_register:
			// 验证短信验证码填写是否正确
			valifyMessage(et_tele.getText().toString().trim(), et_verify_code.getText().toString().trim());
			break;
		case R.id.iv_wechat:
			RsSharedUtil.putBoolean(getApplicationContext(), "wechatRegister", true);
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			MyApplication.getIWXAPI().sendReq(req);
			break;
		default:
			break;
		}
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

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void getVerifyCode(final Context context, final String phoneNumber) {
		final View contentView = LayoutInflater.from(this).inflate(R.layout.popup_verify, null);

		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		// popwindow位置
		popupWindow.showAtLocation(background, Gravity.CENTER, 0, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		// 图片
		final WebView iv_refer_pic = (WebView) contentView.findViewById(R.id.iv_refer_pic);
		// 关闭图标
		ImageView iv_close = ((ImageView) contentView.findViewById(R.id.iv_close));
		// 输入框
		final EditText et_edit = (EditText) contentView.findViewById(R.id.et_edit);
		// 确认按钮
		final TextView tv_confirm = (TextView) contentView.findViewById(R.id.tv_confirm);
		tv_confirm.setClickable(false);
		// 验证码图片url

		final String url = AppConfig.URL_ACCOUNT + "validation/image.json?phone=" + phoneNumber;
		Log.d("验证码图片url", url);
		iv_refer_pic.loadUrl(url);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		iv_refer_pic.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		et_edit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		et_edit.addTextChangedListener(new TextWatcher() {
			
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
				// TODO Auto-generated method stub
				if (et_edit.getText().length()==4) {
					tv_confirm.setClickable(true);
					tv_confirm.setBackground(getResources().getDrawable(R.drawable.btn_login_enable));
				}else {
					tv_confirm.setClickable(false);
					tv_confirm.setBackground(getResources().getDrawable(R.drawable.btn_login_unable));
				}
			}
		});
		// 点击图片换一张验证码
		iv_refer_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("点击时间", "hahahah");
				iv_refer_pic.clearCache(true);
				iv_refer_pic.loadUrl(url);
			}
		});
		iv_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		tv_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getMsg(phoneNumber, et_edit.getText().toString().trim());
				popupWindow.dismiss();
			}
		});
	}

	/**
	 * 获取短信验证码
	 * 
	 * @param phoneNumber
	 * @param verificationCode
	 */
	public void getMsg(final String phoneNumber, String verificationCode) {
		String url = AppConfig.URL_ACCOUNT + "message/send/other.json?contact=" + phoneNumber + "&verificationCode="
				+ verificationCode + "&type=SIGNUP";
		Log.d("获取短信验证码", url);
		StringRequest stringRequest = new StringRequest(url, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				showdialog("短信已经发送到" + phoneNumber);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				try {
					JSONObject jsonObject = new JSONObject(error.data());
					showdialog(jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		stringRequest.setTag("getMsg");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	public void valifyMessage(final String phoneNumber, final String msg) {
		Log.d("phoneNumber", phoneNumber);
		Log.d("msg", msg);
		String url = AppConfig.URL_ACCOUNT + "validate/token.json?contact=" + phoneNumber + "&verificationCode=" + msg
				+ "&type=SIGNUP";
		StringRequest stringRequest = new StringRequest(url, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				RsSharedUtil.putString(getApplicationContext(), AppConfig.PHONE_NUMBER, phoneNumber);
				RsSharedUtil.putString(getApplicationContext(), AppConfig.VERIFY_CODE, msg);
				RsSharedUtil.putBoolean(getApplicationContext(), AppConfig.IS_FORM_THIRD, false);

				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, ServerTermActivity.class);
				intent.putExtra("isFirst", isFrist);
				startActivity(intent);

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

				try {
					JSONObject jsonObject = new JSONObject(error.data());
					showdialog(jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		stringRequest.setTag("valifyMessage");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("哈哈哈哈", "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
		if (requestCode == GET_CODE && resultCode == RESULT_OK) {
			tv_area_code.setText("+" + data.getExtras().getString("code"));
		} else {
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
					RsSharedUtil.putString(RegisterActivity.this, AppConfig.OPENID, "");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context.startActivity(new Intent(context, ServerTermActivity.class));
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
				new AlertDialog.Builder(RegisterActivity.this).setTitle("返回为空").setMessage("登录失败")
						.setNegativeButton("知道了", null).create().show();
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				new AlertDialog.Builder(RegisterActivity.this).setTitle("返回为空").setMessage("登录失败")
						.setNegativeButton("知道了", null).create().show();
				return;
			}
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

	private void setTextWatcher() {
		// 密码
		et_verify_code.addTextChangedListener(new TextWatcher() {

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
				if (et_verify_code.getText().toString().equals("")) {
					verify_enable = false;
					msg.what = DISABLE_REGISTER;
					handler.sendMessage(msg);
				} else {
					verify_enable = true;
					if (account_enable) {
						msg.what = ENABLE_REGISTER;
						handler.sendMessage(msg);
					}
				}
			}
		});

		// 账号
		et_tele.addTextChangedListener(new TextWatcher() {

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
				// 判断是否为正确的手机号
				if (!DateComparator.isMobile(et_tele.getText().toString().trim())) {
					account_enable = false;
					tv_get_verification_code.setTextColor(getResources().getColor(R.color.get_code_unable));
					tv_get_verification_code.setClickable(false);
					msg.what = DISABLE_REGISTER;
					handler.sendMessage(msg);

				} else {
					account_enable = true;
					tv_get_verification_code.setTextColor(getResources().getColor(R.color.get_code_able));
					tv_get_verification_code.setClickable(true);
					if (verify_enable) {
						msg.what = ENABLE_REGISTER;
						handler.sendMessage(msg);
					}
				}
			}
		});

	}

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
		LogUtils.d("退出");
		finish();
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
