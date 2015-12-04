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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
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

@ContentView(R.layout.activity_fund_home)
public class FundHomeActivity extends Activity implements OnItemClickListener {
	@ViewInject(R.id.lv_fund_tuijian)
	private ListView lv_fund_tuijian;
	/*
	 * 推荐基金
	 */

	private ArrayList<HashMap<String, Object>> map_tui;
	private FundHomeTuiJianAdapter adapter_tui;
	/*
	 * 自选基金
	 */
	@ViewInject(R.id.lv_fund_zixuan)
	private ListView lv_fund_zixuan;
	private ArrayList<String> al_title;
	private ArrayList<String> al_content;
	private ArrayList<String> al_date;
	private ArrayList<String> al_time;
	private ArrayList<HashMap<String, Object>> map_zi;
	private FundHomeZiXuanAdapter adapter_zi;

	// 初始化
	RequestQueue volleyRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(this);

		init();

		getZiXun();
		getRecommendedFund(0, 6);

		// init();

	}

	/**
	 * 获取基金资讯列表的后台数据
	 */
	private int pageSize_Zixun = 3;
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

								lv_fund_zixuan
										.setAdapter(new FundHomeZiXuanAdapter(
												FundHomeActivity.this, datas));

								lv_fund_zixuan
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

	public void init() {

		lv_fund_tuijian.setOnItemClickListener(this);

		/*
		 * 自选基金初始化
		 */
		al_content = new ArrayList<String>();
		al_title = new ArrayList<String>();
		al_date = new ArrayList<String>();
		al_time = new ArrayList<String>();

		map_zi = new ArrayList<HashMap<String, Object>>();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		Intent intent = new Intent(this, FundDetailsActivity.class);
		intent.putExtra("fund_type", ""
				+ map_tui.get(position).get("category").toString());
		intent.putExtra("symbol", ""
				+ map_tui.get(position).get("symbol").toString());

		startActivity(intent);
	}

	@OnClick({ R.id.iv_fund_return, R.id.ll_managed_funds, R.id.ll_fund_search,
			R.id.ll_fund_inquiry, R.id.ll_recommended_funds,
			R.id.ll_new_development_funds, R.id.ll_fund_information,
			R.id.tv_more, R.id.tv_foucus_more })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_fund_return:
			finish();
			break;
		case R.id.ll_managed_funds:
			startActivity(new Intent(this, OptionalFundActivity.class));
			break;
		case R.id.ll_fund_search:
			startActivity(new Intent(this, FundSearchActivity.class));
			break;
		case R.id.ll_fund_inquiry:
			startActivity(new Intent(this, QueryFundActivity.class));
			break;
		case R.id.ll_recommended_funds:
			startActivity(new Intent(this, FundListActivity.class));
			break;
		case R.id.ll_new_development_funds:
			startActivity(new Intent(this, NewFundActivity.class));
			break;
		case R.id.ll_fund_information:
			startActivity(new Intent(this, FundInformationListActivity.class));
			break;
		case R.id.tv_more:
			startActivity(new Intent(this, FundListActivity.class));
			break;
		case R.id.tv_foucus_more:
			startActivity(new Intent(this, FundInformationListActivity.class));
			break;
		default:
			break;
		}
	}

	/*
	 * 推荐基金ListView适配器
	 */
	public class FundHomeTuiJianAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public FundHomeTuiJianAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size() - 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int num, View view, ViewGroup parent) {
			int position = num + 1;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_fund_tuijian, null);

				holder.tv_text = (TextView) view.findViewById(R.id.tv_text);
				holder.tv_num = (TextView) view.findViewById(R.id.tv_num);
				holder.tv_style = (TextView) view.findViewById(R.id.tv_style);
				holder.tv_per = (TextView) view.findViewById(R.id.tv_percence);
				holder.tv_per_num = (TextView) view
						.findViewById(R.id.tv_percence_num);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_text.setText((CharSequence) list.get(position)
					.get("name"));
			holder.tv_num.setText((CharSequence) list.get(position).get(
					"symbol"));
			holder.tv_style.setText((CharSequence) list.get(position).get(
					"category"));
			holder.tv_per.setText("上月收益率");
			String growthRate = list.get(position).get("growthRate").toString();
			try {
				holder.tv_per_num.setText(String.format("%.2f",
						Double.parseDouble(growthRate) * 10)
						+ "%");
			} catch (NumberFormatException e) {
				holder.tv_per_num.setText("0.00%");
				e.printStackTrace();
			}

			return view;
		}

		class ViewHolder {

			TextView tv_text;

			TextView tv_num;
			TextView tv_style;
			TextView tv_per;
			TextView tv_per_num;

		}
	}

	/*
	 * 自选基金ListView适配器
	 */
	public class FundHomeZiXuanAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		public FundHomeZiXuanAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
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

	/**
	 * 获取年收益率排名前五的开放式基金
	 * 
	 * @param num
	 */
	private void getRecommendedFund(int pageIndex, int pageSize) {

		long time = System.currentTimeMillis();
		String endYear = new SimpleDateFormat("yyyy").format(new Date(time));
		String MonthDay = new SimpleDateFormat("/MM/dd").format(new Date(time));
		int startYear = Integer.parseInt(endYear) - 1;
		String url = AppConfig.URL_FUND
				+ "list/bestOnTradingDate.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						"access_token")
				// + "d7abc947-6df0-4073-bc74-ba4b24c478b2"
				+ "&startDate=" + startYear + MonthDay + "&endDate=" + endYear
				+ MonthDay + "&page=" + pageIndex + "&size=" + pageSize;
		Log.d("url", "url:" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("获取推荐基金列表", "FUND:" + response.toString());
							// 如果没有数据
							if (response.toString().equals("")
									|| response.toString().equals("[0]")) {

							} else {
								try {
									JSONArray all = new JSONArray(response
											.toString());
									final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
									for (int i = 0; i < all.length(); i++) {
										HashMap<String, Object> data = new HashMap<String, Object>();
										Iterator<String> jsIterator;
										try {
											jsIterator = all.getJSONObject(i)
													.keys();
											while (jsIterator.hasNext()) {
												String key = jsIterator.next();
												data.put(key,
														all.getJSONObject(i)
																.get(key)
																.toString());
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										datas.add(data);
									}
									map_tui = datas;
									adapter_tui = new FundHomeTuiJianAdapter(
											getApplicationContext(), map_tui);
									lv_fund_tuijian.setAdapter(adapter_tui);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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

}