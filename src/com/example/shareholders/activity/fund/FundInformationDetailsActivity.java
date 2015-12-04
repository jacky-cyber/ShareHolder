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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fund_information_details)
public class FundInformationDetailsActivity extends Activity {

	// 内容
	@ViewInject(R.id.tv_fi_details)
	private TextView tv_fi_details;

	// 标题
	@ViewInject(R.id.tv_fi_title)
	private TextView tv_fi_title;

	// 时间
	@ViewInject(R.id.tv_fi_time)
	private TextView tv_fi_time;
	
	// 來源
		@ViewInject(R.id.tv_fi_source)
		private TextView tv_fi_source;

	private RequestQueue volleyRequestQueue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		volleyRequestQueue = Volley.newRequestQueue(this);

		getZiXunDetail();

	}

	private void getZiXunDetail() {
		String newsid = getIntent().getExtras().getString("newsid");

		String url = AppConfig.URL_INFO+"news/";
		url += newsid + ".json?access_token="
				+ RsSharedUtil.getString(this, "access_token");

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						try {
							JSONObject jsonObject = new JSONObject(response);
							HashMap<String, String> data = new HashMap<String, String>();
							Iterator<String> iterator = jsonObject.keys();

							while (iterator.hasNext()) {
								String key = iterator.next();
								data.put(key, jsonObject.get(key).toString());
							}

							setView(data);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					private void setView(HashMap<String, String> data) {
						tv_fi_title.setText(data.get("title"));

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						long date_long = Long
								.parseLong(data.get("declaredate"));
						String date = simpleDateFormat.format(new Date(
								date_long));
						tv_fi_time.setText(date);
						
						tv_fi_source.setText(data.get("newssource"));

						tv_fi_details.setText(data.get("newscontent"));
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
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
				});

		volleyRequestQueue.add(stringRequest);

	}

	@OnClick(R.id.iv_fi_details_return)
	public void onClick(View v) {
		finish();
	}

}
