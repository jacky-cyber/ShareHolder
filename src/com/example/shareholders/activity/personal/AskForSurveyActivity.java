package com.example.shareholders.activity.personal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.ActivityCreateCompanyBrief;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.ActionSheetDialog;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.constant.WBConstants.Msg;

@ContentView(R.layout.activity_ask_for_survey)
public class AskForSurveyActivity extends ActionBarActivity {
	@ViewInject(R.id.rl_enterprise_name)
	private RelativeLayout rl_enterprise_name;
	// 创建公司
	private int ENTERPRISE = 0;
	// 公司名称
	@ViewInject(R.id.tv_enterprise_name)
	private TextView tv_enterprise_name;
	// 开始时间
	@ViewInject(R.id.tv_time_before)
	private TextView tv_time_before;
	// 结束时间
	@ViewInject(R.id.tv_time_after)
	private TextView tv_time_after;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	View timepickerview;
	WheelMain wheelMain;

	// 其他说明
	@ViewInject(R.id.et_other_statement)
	private EditText et_other_statement;

	private AlertDialog tipsDialog = null;

	private String symbol = ""; // 企业代码
	private String type = "";// 企业类型
	private String noted = "";// 其他说明
	private String beginDate = "";// 调研开始时间（必填）
	private String endDate = "";// 调研结束时间（必填）

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (tipsDialog != null && tipsDialog.isShowing()) {
					tipsDialog.dismiss();
				}
				break;
			case 1:
				if (tipsDialog != null && tipsDialog.isShowing()) {
					tipsDialog.dismiss();
					finish();
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
		tv_time_before.setText(dateFormat.format(new Date()).toString());
		tv_time_after.setText(dateFormat.format(new Date()).toString());
	}

	@OnClick({ R.id.tv_enterprise_name, R.id.rl_return, R.id.tv_time_before,
			R.id.tv_time_after, R.id.tv_next })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		// 调研公司名称
		case R.id.tv_enterprise_name:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(),
					ActivityCreateCompanyBrief.class);

			Bundle bundle = new Bundle();
			bundle.putInt("type", 123);
			intent.putExtras(bundle);

			startActivityForResult(intent, ENTERPRISE);
			break;
		case R.id.tv_time_before:
			setWheelMain(tv_time_before);
			new ActionSheetDialog(this, wheelMain, tv_time_before, tv_time_after,
					"choose_time").builder().setTitle("请选择日期")
					.setCancelable(true).setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,1).show();

			break;
		case R.id.tv_time_after:
			setWheelMain(tv_time_after);
			new ActionSheetDialog(this, wheelMain, tv_time_before, tv_time_after, "choose_time")
					.builder().setTitle("请选择日期").setCancelable(true)
					.setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,2).show();
			break;
		// 提交
		case R.id.tv_next:
			if (tv_enterprise_name.getText().toString().equals("")) {
				NullTips();
			} else {
				submitDemand();
			}

			break;

		default:
			break;
		}
	}
	
	//定时任务
	TimerTask task =new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			finish();
		}
	};

	private void submitDemand() {
		// 显示正在提交的对话框
		final LoadingDialog loadingDialog = new LoadingDialog(AskForSurveyActivity.this);
		String msg = "提交中，请稍后...";
		loadingDialog.setLoadingString(msg);
		loadingDialog.showLoadingDialog();
//		showDialog(msg, false);

		String url = AppConfig.URL_SURVEY + "requirement.json?access_token="
				+ RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN);

		Log.d("dj_url", url);
		
		JSONObject params = new JSONObject();

		noted = et_other_statement.getText().toString();
		beginDate = tv_time_before.getText().toString();
		endDate = tv_time_after.getText().toString();
		Log.d("dj_beginDate", beginDate);
		Log.d("dj_endDate", endDate);
		beginDate = tv_time_before.getText().toString().replace(".", "-");
		endDate = tv_time_after.getText().toString().replace(".", "-");
		Log.d("dj_beginDate", beginDate);
		Log.d("dj_endDate", endDate);
		
		try {
			params.put("symbol", symbol);
			params.put("type", type);
			params.put("noted", noted);
			params.put("beginDate", beginDate);
			params.put("endDate", endDate);
			Log.d("dj_symbol", symbol);
			Log.d("dj_type", type);
			Log.d("dj_noted", noted);
			Log.d("dj_beginDate", beginDate);
			Log.d("dj_endDate", endDate);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Log.d("liang_url_demand", url);
		// Log.d("liang_params_demand", params.toString());

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
//						if (tipsDialog != null && tipsDialog.isShowing()) {
//							tipsDialog.dismiss();
//						}
						loadingDialog.dismissDialog();
						
						InternetDialog internetDialog = new InternetDialog(AskForSurveyActivity.this);
						String msg = "您的需求已成功提交给后台，若相关的企业有调研活动，系统将第一时间通知您!";
						internetDialog.showInternetDialog(msg, true);
						Log.d("dj_response", "success");
						//两秒后返回上级页面
						Timer timer = new Timer();
						timer.schedule(task, 2000);
						
//						showDialog(msg, true);

//						new Thread(new Runnable() {
//
//							@Override
//							public void run() {
//								Message msg = new Message();
//								msg.what = 1;
//								mHandler.sendMessageDelayed(msg, 2000);
//							}
//						}) {
//						}.start();

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							loadingDialog.dismissDialog();
							JSONObject jsonObject = new JSONObject(error.data());
							String description = jsonObject
									.getString("description");
							Log.d("liang_json_error", description);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							loadingDialog.dismissDialog();
							e.printStackTrace();
							Log.d("liang_exception", e.toString());
						}
					}
				});

		stringRequest.setTag("submitDemand");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void NullTips() {
		String msg = "请选择企业";
		showDialog(msg, true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessageDelayed(msg, 2000);
			}
		}) {
		}.start();
	}

	private void showDialog(String msg, boolean warning) {
		tipsDialog = new AlertDialog.Builder(this).create();
		tipsDialog.show();

		Window window = tipsDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);

		tv_message.setText(msg);

		if (warning) { // 警告信息
			progress_bar.setVisibility(View.GONE);
			iv_tips.setVisibility(View.VISIBLE);
		} else {
			progress_bar.setVisibility(View.VISIBLE);
			iv_tips.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("submitDemand");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// 创建调研公司
		if (requestCode == ENTERPRISE) {
			// 根据所选调研公司返回的调研名称，调研城市，股票代码，行业代码，以及地点代码
			try {
				symbol = data.getExtras().getString("symbol");
				type = data.getExtras().getString("companyType");

				tv_enterprise_name.setText(data.getExtras().getString(
						"shortName"));
			}

			catch (Exception e) {
				Log.d("Exception", e.toString());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setWheelMain(TextView tv) {
		LayoutInflater inflater = LayoutInflater.from(this);
		timepickerview = inflater.inflate(R.layout.timepicker2, null);
		ScreenInfo screenInfo = new ScreenInfo(this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = tv.getText().toString().replaceAll(".", "-");
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
	}
}
