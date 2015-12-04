package com.example.shareholders.activity.survey;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.VoidRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_inform_edit)
public class InformEditActivity extends Activity {

	@ViewInject(R.id.inform_edit)
	private EditText et_inform_edit;
	@ViewInject(R.id.tv_publish)
	private TextView tv_public;

	private AlertDialog mDialog;
	private static String content = null;
	@ViewInject(R.id.iv_edit_return)
	private ImageView iv_return;

	private RequestQueue volleyRequestQueue;
	private String uuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/**
		 * 获取前一个activity传过来uuid数据
		 */
		Bundle bundle = getIntent().getExtras();
		uuid = bundle.getString("uuid");

		ViewUtils.inject(this);
		init();
		et_inform_edit.setFocusableInTouchMode(true);

		et_inform_edit.requestFocus();

		Timer timer = new Timer();

		timer.schedule(new TimerTask()

		{

			public void run()

			{

				InputMethodManager inputManager =

				(InputMethodManager) et_inform_edit.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(et_inform_edit, 0);

			}

		},

		998);
		String text = RsSharedUtil.getString(getApplicationContext(),
				"edit_content");
		et_inform_edit.setText(text);
	}

	/**
	 * 方法名：init 功 能：初始化 返回值：无
	 */
	public void init() {
		volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
		content = et_inform_edit.getText().toString();

		mDialog = new AlertDialog.Builder(InformEditActivity.this).create();
		et_inform_edit.addTextChangedListener(textWatcher);
		tv_public.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!BtnClickUtils.isFastDoubleClick()) {
					if (content.length() == 0) {
						Toast.makeText(getApplicationContext(), "请先输入内容",
								Toast.LENGTH_SHORT).show();
					} else {

						dialogShow("123", content);
					}
				}
			}
		});
		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * 功能：记录数据
				 */
				if (!BtnClickUtils.isFastDoubleClick()) {
					if (!et_inform_edit.getText().toString().equals("")) {
						RsSharedUtil.putString(InformEditActivity.this,
								"edit_content", et_inform_edit.getText()
										.toString());
					}
					finish();
				}
			}
		});
	}

	/**
	 * 功能:监听edittext里字数的变化
	 */
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@SuppressLint("ResourceAsColor")
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			content = et_inform_edit.getText().toString();
			if (content.length() == 0) {

				tv_public.setTextColor(getResources().getColor(
						R.color.inform_edit_text));
			} else {

				tv_public.setTextColor(getResources().getColor(R.color.white));

			}

		}

	};

	/**
	 * 方法名：netConnect 功 能：与后台交互，post方式 参 数：title,content(提交的内容),uuid(用户的uuid)
	 * 返回值：无
	 */
	public void netConnect(String title, String content, String uuid) {

		String mark = RsSharedUtil.getString(getApplicationContext(),
				"access_token");
		String url = AppConfig.URL_MESSAGE + "official/new.json?"
				+ "access_token=" + mark;

		JSONObject params = new JSONObject();

		try {
			params.put("title", title);
			params.put("content", content);
			params.put("uuid", uuid);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		VoidRequest request = new VoidRequest(Request.Method.POST, url, params,
				new Response.Listener<Void>() {

					@Override
					public void onResponse(Void response) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "提交成功",
								Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

					}
				}

		);
		volleyRequestQueue.add(request);
	}

	/**
	 * 方法名：dialogShow 功 能：提交数据，创建成功 参 数：title(接口是多余的),content(通知的内容) 返回值：无
	 */
	public void dialogShow(final String title, final String content) {
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_inform_edit);
		((TextView) mDialog.getWindow().findViewById(R.id.tv_dialog_content))
				.setText(getResources().getString(R.string.inform_edit_dialog));
		mDialog.getWindow().findViewById(R.id.tv_confirm)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						netConnect(title, content, uuid);
						et_inform_edit.setText("");

						RsSharedUtil.putString(InformEditActivity.this,
								"edit_content", et_inform_edit.getText()
										.toString());

						Intent intent = new Intent();

						/**
						 * 调用setResult方法表示我将Intent对象返回给之前的那个Activity，
						 * 这样就可以在onActivityResult方法中得到Intent对象，
						 */
						setResult(1, intent);
						finish();
						mDialog.dismiss();
					}
				});

		mDialog.getWindow().findViewById(R.id.tv_cancel)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
				});
	}

}
