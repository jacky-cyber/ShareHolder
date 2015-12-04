package com.example.shareholders.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundDetailsWatchDetailsActivity;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.MyToast;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_FundNetWorth_Money extends Fragment {

	// 基金名称
	@ViewInject(R.id.tv_fd_fund_name)
	private TextView tv_fd_fund_name;
	// 基金代码
	@ViewInject(R.id.tv_fd_fund_code)
	private TextView tv_fd_fund_code;
	// 基金类型
	@ViewInject(R.id.tv_fd_fund_type)
	private TextView tv_fd_fund_type;
	// 基金是否关注
	@ViewInject(R.id.tv_fd_concern)
	private TextView tv_fd_concern;
	// 基金认购方式
	@ViewInject(R.id.tv_fd_open_purchase)
	private TextView tv_fd_open_purchase;
	// 基金赎回状态
	@ViewInject(R.id.tv_fd_open_redemption)
	private TextView tv_fd_open_redemption;

	@ViewInject(R.id.tv_fd_param_accrual)
	private TextView tv_fd_param_accrual;
	@ViewInject(R.id.tv_fd_param_annual_rate)
	private TextView tv_fd_param_annual_rate;
	@ViewInject(R.id.tv_fd_param_accrual_date)
	private TextView tv_fd_param_accrual_date;
	@ViewInject(R.id.tv_fd_param_ranking_num)
	private TextView tv_fd_param_ranking_num;
	@ViewInject(R.id.tv_fd_param_ranking_total)
	private TextView tv_fd_param_ranking_total;

	@ViewInject(R.id.tv_fd_param_details)
	private TextView tv_fd_param_details;
	@ViewInject(R.id.lv_fd_history_list)
	private MyListView lv_fd_history_list;

	private ArrayList<HashMap<String, Object>> lv_fund_details_hashMaps;
	private lvFundDetailsListAdapter adapter;

	private int item_num = 5;

	// 初始化
	RequestQueue volleyRequestQueue;
	// 基金代码
	private String symbol;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.fragment_fund_details_net_worth_money, null);
		ViewUtils.inject(this, v);
		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		initView();
		initList();
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onActivityCreated(savedInstanceState);
	}

	@OnClick(R.id.tv_fd_param_details)
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fd_param_details:
			Intent intent = new Intent(getActivity(),
					FundDetailsWatchDetailsActivity.class);
			intent.putExtra("symbol", symbol);
			startActivity(intent);
			break;
		}
	}

	private void initView() {
		symbol = getActivity().getIntent().getStringExtra("symbol");
		getFundDetails();
		getNavHistory(0, 5);
		tv_fd_concern.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tv_fd_concern.getText()
						.equals(getResources().getString(
								R.string.fund_details_concern))) {
					tv_fd_concern
							.setText(R.string.fund_details_already_concern);
					concern("true", symbol);
				} else {
					tv_fd_concern.setText(R.string.fund_details_concern);
					concern("false", symbol);
				}
			}
		});

	}

	private void initList() {
		lv_fund_details_hashMaps = new ArrayList<HashMap<String, Object>>();
	}

	public class lvFundDetailsListAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public lvFundDetailsListAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
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
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_fund_money_history_list, null);
				holder.item_fund_details_date = (TextView) view
						.findViewById(R.id.item_fund_details_date);
				holder.item_fund_details_accraul = (TextView) view
						.findViewById(R.id.item_fund_details_accraul);
				holder.item_fund_details_annual_rate = (TextView) view
						.findViewById(R.id.item_fund_details_annual_rate);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			long Time = Long.parseLong(list.get(position).get("tradingdate")
					.toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			String creationTime = dateFormat.format(new Date(Time));
			holder.item_fund_details_date.setText(creationTime);
			String achievereturn = (String) list.get(position).get(
					"achievereturn");
			holder.item_fund_details_accraul.setText(String.format("%.4f",
					Double.parseDouble(achievereturn)));
			Double annualizedyield = Double.parseDouble((String) list.get(
					position).get("annualizedyield"));
			if (annualizedyield > 0) {
				holder.item_fund_details_annual_rate.setText("+"
						+ String.format("%.2f", annualizedyield * 10) + "%");
			} else {
				holder.item_fund_details_annual_rate.setText(String.format(
						"%.2f", annualizedyield * 10) + "%");
			}

			return view;
		}

		class ViewHolder {

			TextView item_fund_details_date;
			TextView item_fund_details_accraul;
			TextView item_fund_details_annual_rate;
		}
	}

	/**
	 * 获取基金详情信息
	 */
	private void getFundDetails() {
		String url = AppConfig.URL_FUND + "info/" + symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("基金详情url", "Details url:" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("获取基金详情", "Details:" + response.toString());
							// 如果没有数据
							if (response.toString().equals("")
									|| response.toString().equals("[0]")) {

							} else {
								try {
									JSONObject detailsObject = new JSONObject(
											response.toString());
									tv_fd_fund_name.setText(detailsObject
											.getString("shortname"));
									tv_fd_fund_code.setText(detailsObject
											.getString("symbol"));
									tv_fd_fund_type.setText(detailsObject
											.getString("category"));
									if (detailsObject.getString("followed")
											.equals("false")) {
										tv_fd_concern
												.setText(R.string.fund_details_concern);
									} else {
										tv_fd_concern
												.setText(R.string.fund_details_already_concern);
									}
									tv_fd_open_purchase.setText(detailsObject
											.getString("subscriptionmode"));
									if (detailsObject.getString("redeemstatus")
											.equals("1")) {
										tv_fd_open_redemption
												.setText(R.string.fund_details_open_redemption);
									} else {
										tv_fd_open_redemption
												.setText(R.string.fund_details_pause_redemption);
									}
									long time = Long.parseLong(detailsObject
											.getString("tradingdate"));
									SimpleDateFormat dateFormat = new SimpleDateFormat(
											"yyyy-MM-dd");
									tv_fd_param_accrual_date.setText(dateFormat
											.format(new Date(time)));
									String achievereturn = detailsObject
											.getString("achievereturn");
									tv_fd_param_accrual.setText(String.format(
											"%.4f",
											Double.parseDouble(achievereturn)));
									String annualizedyield = detailsObject
											.getString("annualizedyield");
									tv_fd_param_annual_rate.setText(String.format(
											"%.2f",
											Double.parseDouble(annualizedyield) * 10)
											+ "%");
									tv_fd_param_ranking_num
											.setText(detailsObject
													.getString("rank"));
									tv_fd_param_ranking_total
											.setText(detailsObject
													.getString("count"));
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

	/**
	 * 获取历史基金净值
	 */
	private void getNavHistory(int pageNo, int pageSize) {
		String url = AppConfig.URL_FUND + "navHistory/" + symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token")
				+ "&pageNo=" + pageNo + "&pageSize=" + pageSize;
		Log.d("历史净值url", "navHistory url:" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("获取历史净值", "navHistory:" + response.toString());
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
									lv_fund_details_hashMaps = datas;
									adapter = new lvFundDetailsListAdapter(
											getActivity(),
											lv_fund_details_hashMaps);
									lv_fd_history_list.setAdapter(adapter);
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

	/**
	 * 关注/取消关注基金
	 * 
	 * @param follow
	 * @param symbol
	 * @param iv
	 */
	private void concern(final String follow, String symbol) {
		String url = AppConfig.URL_USER + "security.json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token");
		JSONObject params = new JSONObject();
		try {
			params.put("follow", follow);
			params.put("symbol", symbol);
			params.put("type", "FUND");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("params", params.toString());
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (follow.equals("true")) {
							MyToast.makeText(getActivity(), "成功添加到自选基金",
									Toast.LENGTH_SHORT).show();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							ToastUtils.showToast(getActivity(),
									jsonObject.getString("description"));

						} catch (Exception e) {

						}
						if (follow.equals("false")) {
							tv_fd_concern
									.setText(R.string.fund_details_already_concern);
						} else {
							tv_fd_concern
									.setText(R.string.fund_details_concern);
						}
					}

				});
		volleyRequestQueue.add(stringRequest);
	}

}
