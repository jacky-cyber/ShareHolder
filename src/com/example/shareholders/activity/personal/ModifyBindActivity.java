package com.example.shareholders.activity.personal;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.BindingState;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

@ContentView(R.layout.activity_modify_bind)
public class ModifyBindActivity extends Activity {
	public static Tencent mTencent;
	private AuthInfo mAuthInfo;
	// 电话
	@ViewInject(R.id.tv_tele)
	private TextView tv_tele;

	// 邮箱
	@ViewInject(R.id.tv_email)
	private TextView tv_email;

	// QQ
	@ViewInject(R.id.tv_qq)
	private TextView tv_qq;

	// 微信
	@ViewInject(R.id.tv_wechat)
	private TextView tv_wechat;

	// 微博
	@ViewInject(R.id.tv_weibo)
	private TextView tv_weibo;

	//电话箭头
	@ViewInject(R.id.iv_tele)
	private ImageView iv_tele;

	//邮箱箭头
	@ViewInject(R.id.iv_email)
	private ImageView iv_email;

	private BindingState bindingState;
	// 微博sso
	SsoHandler mSsoHandler;
	//	ProgressDialog progressDialog;
	//"正在加载"的旋转框
	private LoadingDialog loadingDialog;
	private Context context;

	//判断是哪一类绑定,进行回调的区分，解决冲突
	private int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		// 获取tencent实例
		mTencent = Tencent.createInstance(AppConfig.TENCENT_APP_ID, this);
		//		progressDialog = new ProgressDialog(this);
		context = ModifyBindActivity.this;
		loadingDialog = new LoadingDialog(context);

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
				new AlertDialog.Builder(ModifyBindActivity.this)
				.setTitle("返回为空").setMessage("登录失败")
				.setNegativeButton("知道了", null)
				.create()
				.show();
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				new AlertDialog.Builder(ModifyBindActivity.this)
				.setTitle("返回为空")
				.setMessage("登录失败")
				.setNegativeButton("知道了", null).
				create()
				.show();
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

				if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
						&& !TextUtils.isEmpty(openId)) {
					mTencent.setAccessToken(token, expires);
					mTencent.setOpenId(openId);
				}
				Log.d("token", token);
				bindThird("QQ", token);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//		progressDialog.setMessage("请稍等...");
		//		progressDialog.show();
		loadingDialog.setLoadingString("请稍等...");
		loadingDialog.showLoadingDialog();
		handler.sendEmptyMessageDelayed(0x1234,1000);
		super.onResume();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x1234)
				init();
		};
	};

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("init");
		MyApplication.getRequestQueue().cancelAll("bindThird");
		MyApplication.getRequestQueue().cancelAll("releaseBind");
		super.onDestroy();
	}

	@OnClick({ R.id.rl_return, R.id.rl_tete, R.id.rl_email, R.id.rl_qq,
		R.id.rl_wechat, R.id.rl_weibo })
	public void onclick(View view) {
		if (!BtnClickUtils.isFastDoubleClick()) {
			switch (view.getId()) {
			// 返回
			case R.id.rl_return:
				finish();
				break;
				// 电话
			case R.id.rl_tete:
				if (bindingState.getBindPhone().equals(""))
					startActivity(new Intent(ModifyBindActivity.this,
							BindPhoneActivity.class));
				break;
				// 邮箱
			case R.id.rl_email:
				if (bindingState.getBindEmail().equals(""))
					startActivity(new Intent(ModifyBindActivity.this,
							BindEmailActivity.class));
				break;
				// QQ
			case R.id.rl_qq:
				flag = 0;
				if (!bindingState.isBindQQ())
					mTencent.login(ModifyBindActivity.this, "all",
							loginListener);
				else
					releaseBind("QQ");
				break;
				// 微信
			case R.id.rl_wechat:
				// 没有绑定微信
				if (!bindingState.isBindWeixin()) {
					RsSharedUtil.putBoolean(getApplicationContext(),
							"wechatBinding", true);
					SendAuth.Req req = new SendAuth.Req();
					req.scope = "snsapi_userinfo";
					MyApplication.getIWXAPI().sendReq(req);
				} else
					releaseBind("WEIXIN");
				break;
				// 微博
			case R.id.rl_weibo:
				flag = 1;
				if (!bindingState.isBindWeibo()) {
					// 微博回调
					mAuthInfo = new AuthInfo(this, AppConfig.WEIBO_APP_KEY,
							AppConfig.REDIRECT_URL, AppConfig.SCOPE);
					mSsoHandler = new SsoHandler(this, mAuthInfo);
					mSsoHandler.authorize(new AuthListener(
							ModifyBindActivity.this));
				} else{
					releaseBind("WEIBO");
				}
				break;

			default:
				break;
			}
		}
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
					bindThird("WEIBO", mAccessToken.getToken());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			Toast.makeText(context, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	private void GetBindState(BindingState bindingState) {
		tv_tele.setText(bindingState.getBindPhone().equals("") ? "未绑定"
				: bindingState.getBindPhone());
		if (!bindingState.getBindPhone().equals("")) {
			iv_tele.setVisibility(View.GONE);
		}
		tv_email.setText(bindingState.getBindEmail().equals("") ? "未绑定"
				: bindingState.getBindEmail());
		if (!bindingState.getBindEmail().equals("")) {
			iv_email.setVisibility(View.GONE);
		}
		if (bindingState.isBindQQ())
			tv_qq.setText("已绑定");
		else
			tv_qq.setText("未绑定");
		if (bindingState.isBindWeixin())
			tv_wechat.setText("已绑定");
		else
			tv_wechat.setText("未绑定");
		if (bindingState.isBindWeibo())
			tv_weibo.setText("已绑定");
		else
			tv_weibo.setText("未绑定");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
		if (flag==1) {
			if (mSsoHandler != null) {
				mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}else if (flag==0) {
			// 腾讯回调
			mTencent.onActivityResultData(requestCode, resultCode, data,
					loginListener);
			if (requestCode == Constants.REQUEST_API) {
				if (resultCode == Constants.RESULT_LOGIN) {
					Tencent.handleResultData(data, loginListener);
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void releaseBind(String type) {
		String url = AppConfig.URL_THIRD
				+ "bind/release.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&type=" + type;
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.d("取消绑定", response);
				//				progressDialog.setMessage("请稍等...");
				//				progressDialog.show();
				loadingDialog.setLoadingString("请稍等...");
				loadingDialog.showLoadingDialog();
				init();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				try {
					Log.d("取消绑定", error.toString());
					//					progressDialog.setMessage("请稍等...");
					//					progressDialog.show();
					loadingDialog.setLoadingString("请稍等...");
					loadingDialog.showLoadingDialog();
					init();
				} 
				catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		stringRequest.setTag("releaseBind");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 绑定第三方应用
	 * 
	 * @param type
	 */
	private void bindThird(final String type, String access_token) {
		String url = AppConfig.URL_THIRD
				+ "bind.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&accessToken="
						+ access_token + "&type=" + type;

		Log.d("mAccessToken", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				//				progressDialog.setMessage("请稍等...");
				//				progressDialog.show();
				loadingDialog.setLoadingString("请稍等...");
				loadingDialog.showLoadingDialog();
				init();
				Log.d("绑定", "成功");
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d("绑定", error.toString());
				//				progressDialog.setMessage("请稍等...");
				//				progressDialog.show();
				loadingDialog.setLoadingString("请稍等...");
				loadingDialog.showLoadingDialog();
				init();
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(error.data());
					Log.d("绑定", jsonObject.getString("description"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		stringRequest.setTag("bindThird");
		MyApplication.getRequestQueue().add(stringRequest);
	}
}
