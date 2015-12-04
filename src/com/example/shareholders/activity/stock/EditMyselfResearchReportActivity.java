package com.example.shareholders.activity.stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_editmyself_research_report)
public class EditMyselfResearchReportActivity extends Activity {
	// 返回键
	@ViewInject(R.id.title_note)
	private ImageView iv_return;

	// 分享(TextView)
	@ViewInject(R.id.tv_share_news)
	private TextView tv_share_news;

	// 收藏(TextView)
	@ViewInject(R.id.tv_collect_news)
	private TextView tv_collect_news;

	// 收藏(ImageView)
	@ViewInject(R.id.iv_collect_news)
	private ImageView iv_collect_news;

	// 标题
	@ViewInject(R.id.tv_researchReport_title)
	private TextView tv_researchReport_title;
	// 时间
	@ViewInject(R.id.tv_researchReport_date)
	private TextView tv_researchReport_date;
	// 内容
	@ViewInject(R.id.tv_researchReport_content)
	private TextView tv_researchReport_content;
	// 新闻来源
	@ViewInject(R.id.tv_researchReport_source)
	private TextView tv_researchReport_source;

	private AlertDialog myDialog = null;
	private boolean isCollected = true;

	// 正在加载的旋转框
	private LoadingDialog loadingDialog;

	private String newid;
	// 文件下载地址
	private String fileStoragePath;

	// 文件类型
	private String fileType;

	@ViewInject(R.id.v_bg)
	private View v_bg;
	@ViewInject(R.id.rl_parent)
	private RelativeLayout rl_parent;
	String newsid;
	private ShareUtils popupWindow;
	
	String title="";

	/*
	 * @ViewInject(R.id.rl_download) private RelativeLayout rl_download;
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		loadingDialog = new LoadingDialog(EditMyselfResearchReportActivity.this);
		init();
	}

	private void init() {
		loadingDialog.showLoadingDialog();
		v_bg.setAlpha(0.0f);
		newsid = getIntent().getExtras().getString("reportId");

		String url = AppConfig.URL_INFO + "report/" + newsid
				+ ".json?access_token=";
		url += RsSharedUtil.getString(EditMyselfResearchReportActivity.this,
				AppConfig.ACCESS_TOKEN);

		Log.d("dj_researchReport_detail", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_researchReport_details_response",
								response.toString());

						try {
							JSONObject jsonObject = new JSONObject(response);
							HashMap<String, Object> data = new HashMap<String, Object>();
							Iterator<String> iterator = jsonObject.keys();

							while (iterator.hasNext()) {
								String key = iterator.next();
								data.put(key, jsonObject.get(key).toString());
							}
							setView(data);
							loadingDialog.dismissDialog();
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}

					private void setView(HashMap<String, Object> data) {

						// fileStoragePath=data.get("fileStoragePath").toString();
						// fileType=data.get("fileType").toString();
						// rl_download.setVisibility(View.VISIBLE);
						if (data.get("followed").toString().equals("false")) {
							Log.d("ccctf", "sdas");
							isCollected=false;
						}else {
							Log.d("ccctf", "asd");
							isCollected=true;
						}
						initCollected();
						title=data.get("title")
								.toString();
						tv_researchReport_title.setText(data.get("title")
								.toString());

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						long date_long = Long.parseLong(data.get("declareDate")
								.toString());
						String declareDate = simpleDateFormat.format(new Date(
								date_long));
						tv_researchReport_date.setText(declareDate);

						tv_researchReport_content.setText(data.get("summary")
								.toString());
						tv_researchReport_source.setText(data.get(
								"reportInstitutionName").toString());
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("dj_researchReport_detail_errorResponse",
									jsonObject.getString("description"));
						} catch (Exception e) {
							// TODO: handle exception
							Log.d("dj_researchReport_detail_errorResponse",
									e.toString());
						}
					}
				});
		stringRequest.setTag("ResearchReport");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method 
		MyApplication.getRequestQueue().cancelAll("ResearchReport");
		MyApplication.getRequestQueue().cancelAll("stubChangenews");
		super.onDestroy();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (myDialog.isShowing()) {
					myDialog.dismiss();
				}
			}
		}
	};

	private void collectNews() {

		myDialog = new AlertDialog.Builder(EditMyselfResearchReportActivity.this).create();
		myDialog.show();
		handler.sendEmptyMessageDelayed(0x123, 1000);
		myDialog.setCancelable(false);
		myDialog.getWindow().setContentView(R.layout.item_toast_popup);
		TextView tv_item = (TextView) myDialog.getWindow().findViewById(R.id.tv_item);
		String url=AppConfig.URL_INFO+"follow.json?access_token=";
		url+=RsSharedUtil.getString(EditMyselfResearchReportActivity.this, AppConfig.ACCESS_TOKEN);
		url+="&id="+newsid;
		if (isCollected) {
			url+="&infoType=REP&type=CANCEL";
			tv_item.setText("已取消收藏");
			tv_collect_news.setText("收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_normal);
		}else {
			url+="&infoType=REP&type=FOLLOW";
			tv_item.setText("已收藏");
			tv_collect_news.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			tv_collect_news.setText("已收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_selected);
		}
		Log.d("4.1url", url);
		StringRequest stringRequest=new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				isCollected = !isCollected;
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		stringRequest.setTag("Changenews");
		MyApplication.getRequestQueue().add(stringRequest);
	}
	private void initCollected(){
		if (isCollected) {
			tv_collect_news.setText("已收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_selected);
		} else {
			tv_collect_news.setText("收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_normal);
		}
	}
	@OnClick({ R.id.title_note, R.id.tv_share_news, R.id.rl_collect,
	/* R.id.rl_download */})
	private void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;

		case R.id.tv_share_news:
			showShare(title);
			break;
		case R.id.rl_collect:
			collectNews();
			break;
		default:
			break;
		}
	}

	private void showShare(String content) {
		v_bg.setAlpha(0.5f);
		popupWindow = new ShareUtils(
				EditMyselfResearchReportActivity.this, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				v_bg.setAlpha(0.0f);

			}
		});
	}

}
