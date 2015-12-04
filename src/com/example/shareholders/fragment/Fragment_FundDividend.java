package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_FundDividend extends Fragment {

	// 分红配送详情列表
	@ViewInject(R.id.lv_dividend_distribution)
	private MyListView lv_dividend_distribution;

	// 分红拆分详情列表
	@ViewInject(R.id.lv_dividend_split)
	private MyListView lv_dividend_split;

	// 分红配送详情列表适配器
	private DistributionAdapter distributionAdapter;
	// 分红拆分详情列表适配器
	private SplitAdapter splitAdapter;

	private RequestQueue volleyRequestQueue = null;

	// 分红配送详情和分红拆分详情都需要基金代码
	private String symbol = null;
	private String access_token = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_fund_dividend, null);
		ViewUtils.inject(this, v);

		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		symbol = getActivity().getIntent().getExtras().getString("symbol");
		access_token = RsSharedUtil.getString(getActivity(), "access_token");

		getDistribution();
		getSplit();

		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 获取分红拆分详情的后台数据
	 */
	private int pageSize_split = 10;
	private int pageIndex_split = 0;

	private void getSplit() {
		String url = AppConfig.URL_FUND + "resolution/" + symbol;
		url += ".json?access_token=" + access_token + "&pageNo="
				+ pageIndex_split;
		url += "&pageSize=" + pageSize_split;

		Log.d("liang_url_split", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("")) {
							Log.d("liang_error", "No content");
						} else {
							try {
								JSONArray jsonArray = new JSONArray(response);
								ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
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
								lv_dividend_split.setAdapter(new SplitAdapter(
										getActivity(), datas));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.d("error.Exception", e.toString());
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
	 * 获取分红配送详情的后台数据
	 */
	private int pageSize_distribution = 10;
	private int pageIndex_distribution = 0;

	private void getDistribution() {
		String url = AppConfig.URL_FUND + "dividend/" + symbol;
		url += ".json?access_token=" + access_token + "&pageNo="
				+ pageIndex_distribution;
		url += "&pageSize=" + pageSize_distribution;

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("")) {
							Log.d("liang_error", "No content");
						} else {
							try {
								JSONArray jsonArray = new JSONArray(response);
								ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
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
								lv_dividend_distribution
										.setAdapter(new DistributionAdapter(
												getActivity(), datas));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.d("error_Exception", e.toString());
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
	 * 分红配送详情的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class DistributionAdapter extends BaseAdapter {

		LayoutInflater inflater;
		Context context;
		ArrayList<HashMap<String, String>> lists;

		public DistributionAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
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
				converView = inflater.inflate(
						R.layout.item_dividend_distribution, parent, false);
				// 年份
				viewHolder.tv_year = (TextView) converView
						.findViewById(R.id.tv_year);
				// 权限登记日
				viewHolder.tv_equality_register_date = (TextView) converView
						.findViewById(R.id.tv_equality_register_date);
				// 除息日
				viewHolder.tv_ex_dividend_date = (TextView) converView
						.findViewById(R.id.tv_ex_dividend_date);
				// 每份分红
				viewHolder.tv_per_dividend = (TextView) converView
						.findViewById(R.id.tv_per_dividend);
				// 分红发放日
				viewHolder.tv_dividend_grant_date = (TextView) converView
						.findViewById(R.id.tv_dividend_grant_date);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");

			long announcementdate_long = Long.parseLong(lists.get(position)
					.get("announcementdate"));
			String announcementdate = simpleDateFormat.format(new Date(
					announcementdate_long));
			viewHolder.tv_year.setText(announcementdate);

			long recorddate_long = Long.parseLong(lists.get(position).get(
					"recorddate"));
			String recorddate = simpleDateFormat.format(new Date(
					recorddate_long));
			viewHolder.tv_equality_register_date.setText(recorddate);

			long primaryexdividenddate_long = Long.parseLong(lists
					.get(position).get("primaryexdividenddate"));
			String primaryexdividenddate = simpleDateFormat.format(new Date(
					primaryexdividenddate_long));
			viewHolder.tv_ex_dividend_date.setText(primaryexdividenddate);

			viewHolder.tv_per_dividend.setText(lists.get(position).get(
					"dividendpershare"));

			long primarypaydateDividend_long = Long.parseLong(lists.get(
					position).get("primarypaydateDividend"));
			String primarypaydateDividend = simpleDateFormat.format(new Date(
					primarypaydateDividend_long));
			viewHolder.tv_per_dividend.setText(primarypaydateDividend);

			return converView;
		}

		class ViewHolder {
			TextView tv_year;
			TextView tv_equality_register_date;
			TextView tv_ex_dividend_date;
			TextView tv_per_dividend;
			TextView tv_dividend_grant_date;
		}

	}

	/**
	 * 分红拆分详情适配器
	 * 
	 * @author Administrator
	 * 
	 */
	class SplitAdapter extends BaseAdapter {

		LayoutInflater inflater;
		Context context;
		ArrayList<HashMap<String, String>> lists;

		public SplitAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			this.context = context;
			inflater = LayoutInflater.from(context);
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
						R.layout.item_dividend_split, parent, false);
				// 权益登记日
				viewHolder.tv_equality_register_date = (TextView) converView
						.findViewById(R.id.tv_equality_register_date);
				// 拆分类型
				viewHolder.tv_split_category = (TextView) converView
						.findViewById(R.id.tv_split_category);
				// 拆分折算比例
				viewHolder.tv_split_convert_rate = (TextView) converView
						.findViewById(R.id.tv_split_convert_rate);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");

			long splitdate_long = Long.parseLong(lists.get(position).get(
					"splitdate"));
			String splitdate = simpleDateFormat
					.format(new Date(splitdate_long));
			viewHolder.tv_equality_register_date.setText(splitdate);

			viewHolder.tv_split_category.setText(lists.get(position).get(
					"splitratio")
					+ "%");

			viewHolder.tv_split_convert_rate.setText(lists.get(position).get(
					"conversionratio")
					+ "%");

			return converView;
		}

		class ViewHolder {
			TextView tv_equality_register_date;
			TextView tv_split_category;
			TextView tv_split_convert_rate;
		}

	}

}
