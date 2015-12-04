package com.example.shareholders.activity.survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_transpond)
public class TranspondActivity extends Activity {

	// 转发输入的内容
	@ViewInject(R.id.et_transpond_content)
	private EditText et_transpond_content;

	// 转发的图片
	@ViewInject(R.id.iv_creator_face)
	private ImageView iv_creator_face;

	// 转发人
	@ViewInject(R.id.tv_creator_name)
	private TextView tv_creator_name;

	// 转发的内容
	@ViewInject(R.id.tv_creator_content)
	private TextView tv_creator_content;

	@ViewInject(R.id.tv_send)
	private TextView tv_send;

	private RequestQueue volleyRequestQueue;

	private String securitySymbol;

	Intent intent;

	private boolean canPublish = false;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				getFocus();
				break;
			case 2:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
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
		intent = new Intent();
		initView();
		volleyRequestQueue = Volley.newRequestQueue(this);

		tv_send.setTextColor(getResources().getColor(R.color.total_gray));

		getKeyBoard();
		setTextWhater();
	}

	private void setTextWhater() {
		et_transpond_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (et_transpond_content.getText().toString().equals("")) {
					tv_send.setTextColor(getResources().getColor(
							R.color.total_gray));
					canPublish = false;
				} else {
					tv_send.setTextColor(getResources().getColor(R.color.white));
					canPublish = true;
				}
			}
		});
	}

	/**
	 * 弹出软键盘，不知道什么原因，必须要延迟200ms后才能弹出
	 */
	private void getKeyBoard() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 200);

			}
		}) {
		}.start();

	}

	private void initView() {
		Bundle bundle = getIntent().getExtras();
		tv_creator_name.setText(bundle.getString("creatorName"));
		tv_creator_content.setText(bundle.getString("content"));
		JSONArray mediasArray2;
		if (bundle.getString("mediasFlag") != null) {
			iv_creator_face.setVisibility(View.GONE);
			securitySymbol = bundle.getString("securitySymbol");
		} else {
			Log.d("liang", bundle.getString("medias").toString());
			try {
				mediasArray2 = new JSONArray(bundle.getString("medias")
						.toString());
				final String urls[] = new String[mediasArray2.length()];
				// 取第一张图片
				for (int i = 0; i < mediasArray2.length(); i++) {
					JSONObject jsonObject = mediasArray2.getJSONObject(i);
					urls[i] = jsonObject.getString("url");
				}
				if (urls != null && urls.length > 0) {
					iv_creator_face.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(urls[0],
							iv_creator_face);
				} else {
					iv_creator_face.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@OnClick({ R.id.rl_publish, R.id.rl_return })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_publish:
			if (!canPublish) {
				showInternetDialog("内容不能为空");
				break;
			}
			setResult(2);
			finish();
			String content = et_transpond_content.getText().toString();
			transpondTopic(content);

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			break;
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 转发话题
	 * 
	 * @param content
	 */
	private void transpondTopic(String content) {
		Bundle bundle = getIntent().getExtras();
		if (content.equals("")) {
			Toast.makeText(TranspondActivity.this, "转发内容不能为空", 1).show();
		}else if (bundle.getString("mediasFlag") != null) { // 从StockComments那边传数据过来

			String securitySymbol = bundle.getString("securitySymbol");
			String refUuid = bundle.getString("refUuid");
			Log.d("dj_securitySymbol", securitySymbol);
			/**
			 * 从后台获取数据
			 */
			Log.d("dj___", "test");
			linkServer(content, securitySymbol, refUuid, "STOCK");

			intent.putExtra("content", content);
			setResult(2, intent);
		} else {
			String surveyUuid = bundle.getString("surveyUuid");
			String refUuid = bundle.getString("refUuid");
			Log.d("dj_surveyUuid", surveyUuid);
			/**
			 * 从后台获取数据
			 */
			linkServer(content, surveyUuid, refUuid);

			// intent.putExtra("content", content);
			// setResult(2, intent);
		}
	}

	// 调研转发时连接服务器
	private void linkServer(String content, String surveyUuid, String refUuid) {
		String url = AppConfig.URL_TOPIC + "add.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		JSONObject params = new JSONObject();

		try {
			params.put("content", content);
			params.put("surveyUuid", surveyUuid);
			params.put("refUuid", refUuid);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("lele_response", response.toString());

						if (response.equals("") || response.equals("[0]")) {

							// Toast.makeText(TranspondActivity.this,
							// "没有任何数据返回", Toast.LENGTH_SHORT);

							// Toast.makeText(TranspondActivity.this,
							// "没有任何数据返回",
							// Toast.LENGTH_SHORT).show();

						} else {
							try {
								JSONObject jsonObject = new JSONObject(
										response.toString());
								// ToastUtils.showToast(TranspondActivity.this,
								// "转发成功");

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						Intent intent = new Intent();
						intent.setAction("transpond_topic_success");
						sendBroadcast(intent);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {

							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							ToastUtils.showToast(TranspondActivity.this,

							jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						Log.d("liang_transpond_brocast", "fail");
						Intent intent = new Intent();
						intent.setAction("transpond_topic_fail");
						sendBroadcast(intent);

					}
				});

		volleyRequestQueue.add(stringRequest);
		setResult(2);

	}

	// 股票话题转发时连接服务器
	private void linkServer(String content, String securitySymbol,
			String refUuid, String securityTopicType) {
		String url = AppConfig.URL_TOPIC + "add.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		Log.d("dj_transport", url);
		JSONObject params = new JSONObject();

		try {
			params.put("content", content);
			params.put("securitySymbol", securitySymbol);
			params.put("refUuid", refUuid);
			params.put("securityTopicType", securityTopicType);
			Log.d("dj_params", params.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("dj_transpond_response", response.toString());

						if (response.equals("") || response.equals("[0]")) {
							Toast.makeText(TranspondActivity.this, "没有任何数据返回",
									Toast.LENGTH_SHORT).show();
						} else {
							try {
								JSONObject jsonObject = new JSONObject(
										response.toString());
								finish();
								ToastUtils.showToast(TranspondActivity.this,
										"转发成功");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							ToastUtils.showToast(TranspondActivity.this,
									jsonObject.getString("description"));

						} catch (Exception e) {

							ToastUtils.showToast(
									TranspondActivity.this,
									getResources().getString(
											R.string.unknown_error));
						}
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	private AlertDialog internertDialog = null;

	private void showInternetDialog(String msg) {
		internertDialog = new AlertDialog.Builder(this).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_dianzan);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);

		progress_bar.setVisibility(View.GONE);
		iv_tips.setVisibility(View.VISIBLE);
		tv_message.setText(msg);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessageDelayed(msg, 1500);
			}
		}).start();

	}

	/**
	 * 输入框获取焦点
	 */
	private void getFocus() {
		et_transpond_content.setFocusable(true);

		et_transpond_content.setFocusableInTouchMode(true);

		et_transpond_content.requestFocus();

		InputMethodManager inputManager =

		(InputMethodManager) et_transpond_content.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.showSoftInput(et_transpond_content, 0);
	}

}
