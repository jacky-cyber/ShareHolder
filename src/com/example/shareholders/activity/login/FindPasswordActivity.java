package com.example.shareholders.activity.login;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
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
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.BindingState;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_find_password)
public class FindPasswordActivity extends Activity implements AfterLogin {
	@ViewInject(R.id.background)
	private RelativeLayout background;

	//返回图标
	@ViewInject(R.id.title_note)
	private ImageView title_note;
	
	@ViewInject(R.id.et_password)
	private ClearEditText et_password;
	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;
	// 手机号
	@ViewInject(R.id.et_account)
	private ClearEditText et_account;
	// 下一步
	@ViewInject(R.id.tv_next)
	private TextView tv_next;
	//顶部栏
	@ViewInject(R.id.rl_top)
	private RelativeLayout rl_top;
	//标题
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	// 获取验证码
	@ViewInject(R.id.tv_get_verification)
	private TextView tv_get_verification;
	LoginReceiver loginReceiver;
	/**
	 * 若账号或密码为空，enable为false
	 */
	private boolean account_enable = false;
	private boolean passward_enable = false;
	private boolean login_enable = false;
	
	
	
	//"正在加载"的旋转框
	private LoadingDialog loadingDialog;
	
	private BindingState bindingState;

	private static final int DISABLE_LOGIN = 0;// 不可登录
	private static final int ENABLE_LOGIN = 1; // 可以登录s
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 不可登录，登录按钮设置为灰色
			case DISABLE_LOGIN:
				login_enable = false;
				tv_next.setClickable(false);
				tv_next.setBackgroundResource(R.drawable.btn_login);

				break;
			case ENABLE_LOGIN:
				login_enable = account_enable && passward_enable;
				// 账号和密码同时不为空时，才可登录
				if (login_enable) {
					tv_next.setClickable(true);
					tv_next.setBackgroundResource(R.drawable.btn_login_enable);
				}

				break;
			case 0x1234:
				init();
				break;
			default:
				break;
			}
		};
	};
	
	private Context context=FindPasswordActivity.this;

	private AlertDialog mDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setLoadingString("请稍等...");
		loadingDialog.showLoadingDialog();


		//如果没有网络获取绑定状态，则退出
		if (!NetWorkCheck.isNetworkConnected(context)) {
			
			InternetDialog internetDialog = new InternetDialog(FindPasswordActivity.this);
			internetDialog.showInternetDialog("网络异常", false);
			//定时任务
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					FindPasswordActivity.this.finish();
				}
			};
	
			Timer timer = new Timer();
			timer.schedule(task, 2000);

		}
		else {
			handler.sendEmptyMessage(0x1234);
		}

		//判断是修改密码还是忘记密码
		Intent intent  = getIntent();
		int flag = intent.getIntExtra("flag", 0);
		if (flag==1) {
			//设置修改密码样式
			tv_title.setText("修改密码");
			rl_top.setBackgroundColor(Color.parseColor("#2146a9"));
			tv_title.setTextColor(Color.parseColor("#ffffff"));
			title_note.setVisibility(View.VISIBLE);
			tv_return.setVisibility(View.GONE);
		}

		// 下滑线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_get_verification.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		// 设置账号和密码的TextWatcher
		setTextWatcher();
		mDialog = new AlertDialog.Builder(this).create();
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
				if (!DateComparator.isMobile(et_account.getText().toString()
						.trim())) {
					tv_get_verification.setTextColor(getResources().getColor(
							R.color.get_code_unable));
					tv_get_verification.setClickable(false);
					account_enable = false;
					msg.what = DISABLE_LOGIN;
					handler.sendMessage(msg);
				} else {
					tv_get_verification.setTextColor(getResources().getColor(
							R.color.get_code_able));
					tv_get_verification.setClickable(true);
					account_enable = true;
					if (!login_enable) {
						msg.what = ENABLE_LOGIN;
						handler.sendMessage(msg);
					}
				}
			}
		});

	}

	@OnClick({ R.id.tv_return, R.id.tv_choose_area_number, R.id.tv_next,
			R.id.iv_shareholder, R.id.tv_get_verification ,R.id.title_note})
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
		// 下一步
		case R.id.tv_next:
			valifyMessage(et_account.getText().toString().trim(), et_password
					.getText().toString().trim());
			break;
		// 获取验证码
		case R.id.tv_get_verification:
			getVerifyCode(this, et_account.getText().toString().trim());
			break;
		case R.id.title_note:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(loginReceiver);
		MyApplication.getRequestQueue().cancelAll("getMsg");
		MyApplication.getRequestQueue().cancelAll("valifyMessage");
		MyApplication.getRequestQueue().cancelAll("init");
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

	public void valifyMessage(final String phoneNumber, final String msg) {
		Log.d("phoneNumber", phoneNumber);
		Log.d("msg", msg);
		String url = AppConfig.URL_ACCOUNT + "validate/token.json?contact="
				+ phoneNumber + "&verificationCode=" + msg
				+ "&type=RESET_PASSWORD";
		Log.d("valifyMessage", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.PHONE_NUMBER, phoneNumber);
						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.VERIFY_CODE, msg);
						startActivity(new Intent(FindPasswordActivity.this,
								NewPasswordActivity.class));
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

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void getVerifyCode(final Context context, final String phoneNumber) {
		final View contentView = LayoutInflater.from(this).inflate(
				R.layout.popup_verify, null);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
		final WebView iv_refer_pic = (WebView) contentView
				.findViewById(R.id.iv_refer_pic);
		// 关闭图标
		ImageView iv_close = ((ImageView) contentView
				.findViewById(R.id.iv_close));
		// 输入框
		final EditText et_edit = (EditText) contentView
				.findViewById(R.id.et_edit);
		// 确认按钮
		final TextView tv_confirm = (TextView) contentView
				.findViewById(R.id.tv_confirm);
		tv_confirm.setClickable(false);
		// 验证码图片url
		final String url = AppConfig.URL_ACCOUNT
				+ "validation/image.json?phone=" + phoneNumber;
		Log.d("1.2url", url);
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
				Log.d("tc_queding", "asd");
				getMsg(phoneNumber, et_edit.getText().toString().trim());
				popupWindow.dismiss();
			}
		});
	}

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

	/**
	 * 获取短信验证码
	 * 
	 * @param phoneNumber
	 * @param verificationCode
	 */
	public void getMsg(final String phoneNumber, String verificationCode) {
		String url = AppConfig.URL_ACCOUNT + "message/send/other.json?contact="
				+ phoneNumber + "&verificationCode=" + verificationCode
				+ "&type=RESET_PASSWORD";
		Log.d("getmsgurl", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

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
	
	
	
	/**
	 * 读取绑定状态
	 */
	private void init() {
		String url = AppConfig.URL_ACCOUNT+ "bind/state.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				//				progressDialog.dismiss();
				loadingDialog.dismissDialog();
				ObjectMapper objectMapper = new ObjectMapper();
				bindingState = new BindingState();
				try {
					bindingState = objectMapper.readValue(response,BindingState.class);
					GetBindState(bindingState);
				} catch (JsonParseException e) {
					Log.d("JsonParseException", e.toString());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					Log.d("JsonMappingException", e.toString());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					Log.d("IOException", e.toString());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				//				progressDialog.dismiss();
				loadingDialog.dismissDialog();
				try {
					Log.d("error", error.data().toString());
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
		stringRequest.setTag("init");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	
	private void GetBindState(BindingState bindingState) {

		if (bindingState.getBindPhone().equals("")) {
			

			InternetDialog internetDialog = new InternetDialog(FindPasswordActivity.this);
			internetDialog.showInternetDialog("请先绑定手机号！！", false);
			//定时任务
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					FindPasswordActivity.this.finish();
				}
			};
	
			Timer timer = new Timer();
			timer.schedule(task, 2000);
		}
		else {
			
		}
		
	}
	
	
	

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
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
