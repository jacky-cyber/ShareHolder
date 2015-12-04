package com.example.shareholders.activity.fund;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fund_notice_details)
public class FundNoticeDetailsActivity extends Activity {

	// 摘要内容
	@ViewInject(R.id.tv_fund_details)
	private TextView tv_details;

	// 摘要标题
	@ViewInject(R.id.tv_fund_an_text)
	private TextView tv_fund_an_text;

	// 公告时间
	@ViewInject(R.id.tv_fund_an_date)
	private TextView tv_fund_an_date;

	private RequestQueue volleyRequestQueue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		volleyRequestQueue = Volley
				.newRequestQueue(FundNoticeDetailsActivity.this);

		getDetail();

		super.onCreate(savedInstanceState);

	}

	/**
	 * 获取公告性详情的后台数据
	 */
	private void getDetail() {
		String url = AppConfig.URL_INFO + "ann/";
		String announcementid = getIntent().getExtras().getString(
				"announcementid");
		url += announcementid + ".json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		Log.d("lele_url2", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("") || response.equals("[0]")) {
							Log.d("lele_no_content", "No Content");
						} else {
							try {
								JSONObject jsonObject = new JSONObject(response
										.toString());
								Iterator<String> jIterator = jsonObject.keys();
								HashMap<String, String> data = new HashMap<String, String>();
								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, jsonObject.get(key)
											.toString());
								}

								// 公告时间是肯定有的
								String declaredate = jsonObject.get(
										"declaredate").toString();
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm");
								long declaredate_long = Long
										.parseLong(declaredate);
								declaredate = simpleDateFormat.format(new Date(
										declaredate_long));
								tv_fund_an_date.setText(declaredate);

								// 下载和浏览是矛盾的
								if (data.get("title").equals("")) { // 只可以下载

								} else { // 不可以下载，只能浏览
									// 摘要标题
									String summarytitle = jsonObject.get(
											"summarytitle").toString();
									// 摘要内容
									String summarycontent = jsonObject.get(
											"summarycontent").toString();

									tv_fund_an_text.setText(summarytitle);
									tv_details.setText(summarycontent);
								}

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
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error",
									jsonObject.getString("description"));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("lele_error", "未知错误");
						}

					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	@OnClick(R.id.iv_fund_details_return)
	public void onClick(View v) {
		finish();
	}
}
