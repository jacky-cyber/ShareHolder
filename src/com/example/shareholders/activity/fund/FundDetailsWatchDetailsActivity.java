package com.example.shareholders.activity.fund;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fund_details_watch_details)
public class FundDetailsWatchDetailsActivity extends Activity {

	@ViewInject(R.id.lv_fd_information_list)
	private MyListView lv_fd_information_list;
	@ViewInject(R.id.tv_fd_information_manager)
	private TextView tv_fd_information_manager;
	@ViewInject(R.id.tv_fd_information_date)
	private TextView tv_fd_information_date;
	@ViewInject(R.id.tv_fd_information_intro)
	private TextView tv_fd_information_intro;

	private ArrayList<String> list_fd_information_name;
	private ArrayList<String> list_fd_information_param;
	private ArrayList<HashMap<String, Object>> list_fd_hashMaps;
	private MyAdapter lv_fd_information_adapter;

	// 初始化
	RequestQueue volleyRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(this);
		setInformation();
	}

	@OnClick(R.id.title_fd_note)
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_fd_note:
			finish();
			break;
		default:
			break;
		}
	}

	private void setInformation() {

		list_fd_information_name = new ArrayList<String>();
		list_fd_information_param = new ArrayList<String>();
		list_fd_hashMaps = new ArrayList<HashMap<String, Object>>();

		list_fd_information_name.add(this
				.getString(R.string.fund_details_fund_name));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_fund_code));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_fund_type));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_risk_level));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_register_date));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_asset_size));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_share_size));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_fund_keeper));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_fund_custodian));
		list_fd_information_name.add(this
				.getString(R.string.fund_details_fund_manager));

		Intent intent = getIntent();
		String symbol = intent.getStringExtra("symbol");
		getFundDetails(symbol);
	}

	private class MyAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public MyAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
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
						R.layout.item_activity_fund_details_information_list,
						null);
				holder.item_fd_name = (TextView) view
						.findViewById(R.id.item_fd_information_name);
				holder.item_fd_param = (TextView) view
						.findViewById(R.id.item_fd_information_param);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.item_fd_name
					.setText((String) list.get(position).get("name"));
			holder.item_fd_param.setText((String) list.get(position).get(
					"param"));

			return view;
		}

		class ViewHolder {
			TextView item_fd_name;
			TextView item_fd_param;
		}

	}

	/**
	 * 获取关注的基金列表
	 * 
	 * @param symbol
	 */
	private void getFundDetails(String symbol) {
		String url = AppConfig.URL_FUND
				+ "detailInfo.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						"access_token") + "&symbol=" + symbol;

		Log.d("url", "url:" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("查看明细", "FundDetails:" + response.toString());
							// 如果没有数据
							if (response.toString().equals("")
									|| response.toString().equals("[0]")) {

							} else {
								try {
									JSONObject detailsObject = new JSONObject(
											response.toString());
									list_fd_information_param.add(detailsObject
											.getString("fullName"));
									list_fd_information_param.add(detailsObject
											.getString("symbol"));
									list_fd_information_param.add(detailsObject
											.getString("category"));
									list_fd_information_param.add(detailsObject
											.getString("riskDescription"));
									long Time = Long.parseLong(detailsObject
											.getString("inceptionDate"));
									SimpleDateFormat inceptionDateFormat = new SimpleDateFormat(
											"yyyy-MM-dd");
									list_fd_information_param
											.add(inceptionDateFormat
													.format(Time));
									double inceptionTNA = Double.parseDouble(new BigDecimal(
											detailsObject
													.getString("inceptionTNA"))
											.toPlainString());
									list_fd_information_param.add(String
											.format("%.2f",
													inceptionTNA / 100000000)
											+ "亿");
									double inceptionShares = Double.parseDouble(new BigDecimal(
											detailsObject
													.getString("inceptionShares"))
											.toPlainString());
									list_fd_information_param.add(String
											.format("%.2f",
													inceptionShares / 100000000)
											+ "亿");
									list_fd_information_param.add(detailsObject
											.getString("fundCompanyName"));
									list_fd_information_param.add(detailsObject
											.getString("custodian"));
									list_fd_information_param.add(detailsObject
											.getString("fundManagerName"));

									for (int i = 0; i < 10; i++) {
										HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
										itemHashMap
												.put("name",
														list_fd_information_name
																.get(i));
										itemHashMap.put("param",
												list_fd_information_param
														.get(i));

										list_fd_hashMaps.add(itemHashMap);
									}
									lv_fd_information_adapter = new MyAdapter(
											getApplicationContext(),
											list_fd_hashMaps);
									lv_fd_information_list
											.setAdapter(lv_fd_information_adapter);

									tv_fd_information_manager.setText(detailsObject
											.getString("fundManagerName"));
									if (!detailsObject.getString(
											"serviceStartDate").equals("null")) {
										long date = Long.parseLong(detailsObject
												.getString("serviceStartDate"));
										SimpleDateFormat dateFormat = new SimpleDateFormat(
												"yyyy-MM-dd");
										tv_fd_information_date.setText("(上任时间："
												+ dateFormat.format(new Date(
														date)) + ")");
									}

									tv_fd_information_intro.setText(detailsObject
											.getString("fundManagerResume"));

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
