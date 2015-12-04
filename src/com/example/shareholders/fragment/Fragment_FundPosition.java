package com.example.shareholders.fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_FundPosition extends Fragment {

	// 资产配置明细的列表
	@ViewInject(R.id.lv_configuration)
	private MyListView lv_configuration;

	// 股票投资明细的列表
	@ViewInject(R.id.lv_stock)
	private MyListView lv_stock;

	// 债券投资明细的列表
	@ViewInject(R.id.lv_bond)
	private MyListView lv_bond;

	// 股票投资明细的时间
	@ViewInject(R.id.tv_time_stock)
	private TextView tv_time_stock;

	// 债券投资明细的时间
	@ViewInject(R.id.tv_time_bond)
	private TextView tv_time_bond;

	// 资产配置明细的列表的适配器
	private ConfigurationAdapter configurationAdapter;
	// 股票投资明细的列表的适配器
	private StockAdapter stockAdapter;
	// 债券投资明细的列表的适配器
	private BondAdapter bondAdapter;

	private RequestQueue volleyRequestQueue = null;

	// 三个接口都需要基金代码
	private String symbol = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_fund_position, null);
		ViewUtils.inject(this, v);

		volleyRequestQueue = Volley.newRequestQueue(getActivity());

		return v;
	}

	/**
	 * 获取股票投资明细的后台数据
	 */
	int pageSize_stock = 10;
	int pageIndex_stock = 0;

	private void getStock() {
		String url = AppConfig.URL_FUND + "portfolioStock/";

		// 计算当前是第几季度
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		// 季度
		int quarter = month / 3 + 1;

		// 设置股票投资明细的时间
		tv_time_stock.setText(year + "年第" + quarter + "季度");

		url += symbol + "/" + quarter + ".json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		url += "&pageNo=" + pageIndex_stock + "&pageSize=" + pageSize_stock;
		Log.d("lele_url_stock", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("") || response.equals("[0]")) {
							// ToastUtils.showToast(getActivity(), "没有数据");

						} else {
							try {
								JSONArray jsonArray = new JSONArray(response
										.toString());
								Iterator<String> jIterator = null;
								final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = null;

								for (int i = 0; i < jsonArray.length(); i++) {
									jIterator = jsonArray.getJSONObject(i)
											.keys();
									data = new HashMap<String, String>();
									while (jIterator.hasNext()) {
										String key = jIterator.next();
										data.put(key, jsonArray
												.getJSONObject(i)
												.getString(key));
									}

									datas.add(data);

								}

								StockAdapter adapter = new StockAdapter(
										getActivity(), datas);
								lv_stock.setAdapter(adapter);

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
	 * 获取债券投资明细的后台数据
	 */
	int pageSize_bond = 10;
	int pageIndex_bond = 0;

	private void getBond() {
		String url = AppConfig.URL_FUND + "portfolioBond/";

		// 计算当前是第几季度
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		// 季度
		int quarter = month / 3 + 1;

		// 设置股票投资明细的时间
		tv_time_bond.setText(year + "年第" + quarter + "季度");

		url += symbol + "/" + quarter + ".json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		url += "&pageNo=" + pageIndex_bond + "&pageSize=" + pageSize_bond;

		Log.d("liang_url_bond", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("") || response.equals("[0]")) {
							// ToastUtils.showToast(getActivity(), "没有数据");

						} else {
							try {
								JSONArray jsonArray = new JSONArray(response
										.toString());
								Iterator<String> jIterator = null;
								final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = null;

								for (int i = 0; i < jsonArray.length(); i++) {
									jIterator = jsonArray.getJSONObject(i)
											.keys();
									data = new HashMap<String, String>();
									while (jIterator.hasNext()) {
										String key = jIterator.next();
										data.put(key, jsonArray
												.getJSONObject(i)
												.getString(key));
									}

									datas.add(data);

								}

								BondAdapter adapter = new BondAdapter(
										getActivity(), datas);
								lv_bond.setAdapter(adapter);

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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		symbol = getActivity().getIntent().getExtras().getString("symbol");

		getConfiguration();
		getStock();
		getBond();

		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 获取资产配置明细的后台数据
	 */
	private void getConfiguration() {
		String url = AppConfig.URL_FUND + "allocation.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		url += "&symbol=" + symbol;

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						if (response.equals("") || response.equals("[0]")) {
							// ToastUtils.showToast(getActivity(),
							// "No Content");
						} else {
							try {
								JSONObject jsonObject = new JSONObject(response);

								Iterator<String> jIterator = null;
								final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = new HashMap<String, String>();

								jIterator = jsonObject.keys();
								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, jsonObject.get(key)
											.toString());
								}

								datas.add(data);

								Log.d("liang_datas", datas.toString());

								ConfigurationAdapter adapter = new ConfigurationAdapter(
										getActivity(), datas);
								lv_configuration.setAdapter(adapter);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						Log.d("liang", "end");

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error",
									jsonObject.getString("descriptin"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error", error.toString());
						}
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	class ConfigurationAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		public ConfigurationAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = inflater.inflate(R.layout.item_configuration,
						parent, false);
				// 报告期
				viewHolder.tv_date = (TextView) converView
						.findViewById(R.id.tv_date);
				// 股票占净比
				viewHolder.tv_stock_rate = (TextView) converView
						.findViewById(R.id.tv_stock_rate);
				// 债券占净比
				viewHolder.tv_bond_rate = (TextView) converView
						.findViewById(R.id.tv_bond_rate);
				// 现金占净比
				viewHolder.tv_cash_rate = (TextView) converView
						.findViewById(R.id.tv_cash_rate);
				// 净资产
				viewHolder.tv_total_cash = (TextView) converView
						.findViewById(R.id.tv_total_cash);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
			long date_long = Long.parseLong(lists.get(position).get(
					"reportDate"));
			String date = simpleDateFormat.format(new Date(date_long));

			viewHolder.tv_date.setText(date);
			viewHolder.tv_stock_rate.setText(lists.get(position).get(
					"stockRatio")
					+ "%");

			String bondRatio = lists.get(position).get("bondRatio");
			if (bondRatio.equals("null")) {
				bondRatio = "--";
			} else {
				bondRatio += "%";
			}
			viewHolder.tv_bond_rate.setText(bondRatio);
			viewHolder.tv_cash_rate.setText(lists.get(position)
					.get("cashRatio") + "%");

			// 除以一亿，保留两位小数
			double totalAsset_double = Double.parseDouble(lists.get(position)
					.get("totalAsset"));
			totalAsset_double /= 100000000d;
			DecimalFormat decimalFormat = new DecimalFormat("#.00");
			String totalAsset = decimalFormat.format(totalAsset_double);

			viewHolder.tv_total_cash.setText(totalAsset);

			return converView;
		}

		class ViewHolder {
			TextView tv_date;
			TextView tv_stock_rate;
			TextView tv_bond_rate;
			TextView tv_cash_rate;
			TextView tv_total_cash;
		}

	}

	/**
	 * 股票投资明细适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class StockAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;
		Context context;

		public StockAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			// inflater = LayoutInflater.from(context);
			this.context = context;
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = LayoutInflater.from(context).inflate(
						R.layout.item_stock_detail, parent, false);
				// 名称
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_name);
				// 占净值比例
				viewHolder.tv_of_all_rate = (TextView) converView
						.findViewById(R.id.tv_of_all_rate);
				// 持股数
				viewHolder.tv_stock_number = (TextView) converView
						.findViewById(R.id.tv_stock_number);
				// 持仓数值
				viewHolder.tv_position_value = (TextView) converView
						.findViewById(R.id.tv_position_value);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.tv_name.setText(lists.get(position).get("name")
					.toString());
			viewHolder.tv_of_all_rate.setText(lists.get(position)
					.get("proportion").toString()
					+ "%");

			// 返回的持仓数值是元为单位的，除以10000并保留两位小数
			double shares = Double.parseDouble(lists.get(position)
					.get("shares"));
			shares /= 10000d;
			DecimalFormat decimalFormat = new DecimalFormat("#.00");
			String result = decimalFormat.format(shares);

			viewHolder.tv_stock_number.setText(result);

			// 返回持有数值是元为单位的，除以10000并保留两位小数
			double marketvalue = Double.parseDouble(lists.get(position).get(
					"marketvalue"));
			marketvalue /= 10000d;
			String marketvalue_result = decimalFormat.format(marketvalue);

			viewHolder.tv_position_value.setText(marketvalue_result);

			return converView;
		}

		class ViewHolder {
			TextView tv_name;
			TextView tv_of_all_rate;
			TextView tv_stock_number;
			TextView tv_position_value;
		}

	}

	/**
	 * 债券投资明细适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class BondAdapter extends BaseAdapter {

		LayoutInflater inflater;
		Context context;
		ArrayList<HashMap<String, String>> lists;

		public BondAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			// inflater = LayoutInflater.from(context);
			this.context = context;
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = LayoutInflater.from(context).inflate(
						R.layout.item_bond_detail, parent, false);
				// 名称
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_name);
				// 占净值比例
				viewHolder.tv_of_all_rate = (TextView) converView
						.findViewById(R.id.tv_of_all_rate);
				// 持仓数值
				viewHolder.tv_position_value = (TextView) converView
						.findViewById(R.id.tv_position_value);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.tv_name.setText(lists.get(position).get("name"));
			viewHolder.tv_of_all_rate.setText(lists.get(position).get(
					"proportion")
					+ "%");

			// 返回的持仓数值是元为单位的，除以10000并保留两位小数
			double shares = Double.parseDouble(lists.get(position)
					.get("shares"));
			shares /= 10000d;
			DecimalFormat decimalFormat = new DecimalFormat("#.00");
			String result = decimalFormat.format(shares);

			viewHolder.tv_position_value.setText(result);

			return converView;
		}

		class ViewHolder {
			TextView tv_name;
			TextView tv_of_all_rate;
			TextView tv_position_value;
		}

	}

}
