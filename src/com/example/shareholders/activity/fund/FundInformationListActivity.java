package com.example.shareholders.activity.fund;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundHomeActivity.FundHomeZiXuanAdapter.ViewHolder;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fund_information_list)
public class FundInformationListActivity extends Activity {

	@ViewInject(R.id.lv_fi_list)
	ListView lv_fi_list;

	ArrayList<String> titleArrayList;
	ArrayList<String> timeArrayList;
	ArrayList<String> detailsArrayList;
	ArrayList<HashMap<String, Object>> informationHashMaps;
	InformationListAdapter adapter;

	private RequestQueue volleyRequestQueue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(this);

		getZiXun();
	}

	@OnClick(R.id.iv_fund_return)
	public void onClick(View v) {
		finish();
	}

	/**
	 * 获取基金资讯列表的后台数据
	 */
	private int pageSize_Zixun = 15;
	private int pageIndex_Zixun = 0;

	private void getZiXun() {
		Log.d("lele_start", "start");
		String url = AppConfig.URL_INFO + "news/list.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		JSONObject params = new JSONObject();
		try {
			params.put("pageIndex", pageIndex_Zixun);
			params.put("pageSize", pageSize_Zixun);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("lele_url", url);

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("lele_response", response.toString());
						if (response.equals("") || response.equals("[0]")) {
							Log.d("lele_no_content", "No Content");
						} else {
							Log.d("lele_zixun", response.toString());
							try {
								JSONArray jsonArray = new JSONArray(response);
								final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = null;
								Iterator<String> iterator = null;

								for (int i = 0; i < jsonArray.length(); i++) {
									data = new HashMap<String, String>();
									iterator = jsonArray.getJSONObject(i)
											.keys();
									while (iterator.hasNext()) {
										String key = iterator.next();
										data.put(key, jsonArray
												.getJSONObject(i).get(key)
												.toString());
									}
									datas.add(data);
								}

								lv_fi_list
										.setAdapter(new InformationListAdapter(
												FundInformationListActivity.this,
												datas));

								lv_fi_list
										.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> arg0,
													View arg1, int position,
													long arg3) {
												Intent intent = new Intent(
														getApplication(),
														FundInformationDetailsActivity.class);

												Bundle bundle = new Bundle();
												bundle.putString("newsid",
														datas.get(position)
																.get("newsid"));
												intent.putExtras(bundle);

												startActivity(intent);

											}
										});

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("lele_error", error.toString());
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error", jsonObject.get("description")
									.toString());

						} catch (Exception e) {
							Log.d("lele_error", "未知错误");
						}

					}
				});

		volleyRequestQueue.add(stringRequest);

		Log.d("lele_end", "end");

	}

	public class InformationListAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> list;
		private Context context;
		ViewHolder holder;
		private LayoutInflater mInflater;

		public InformationListAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_fund_zixuan, null);

				holder.tv_title = (TextView) view
						.findViewById(R.id.tv_zi_title);
				holder.tv_content = (TextView) view
						.findViewById(R.id.tv_zi_content);

				holder.tv_date = (TextView) view.findViewById(R.id.tv_zi_date);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_title.setText(list.get(position).get("title"));

//			String newssummary = list.get(position).get("newssummary");
//			if (newssummary.equals("null")) {
//				newssummary = "";
//			}
//			holder.tv_content.setText(newssummary);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			long date_long = Long.parseLong(list.get(position).get(
					"declaredate"));

			String date = simpleDateFormat.format(new Date(date_long));

			holder.tv_date.setText(date);

			return view;
		}

		class ViewHolder {

			TextView tv_title;

			TextView tv_content;
			TextView tv_date;

		}
	}

}
