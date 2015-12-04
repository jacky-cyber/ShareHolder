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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_FundNetWorth extends Fragment {

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
	// 基金净值
	@ViewInject(R.id.tv_fd_param_net_worth)
	private TextView tv_fd_param_net_worth;
	// 基金涨幅
	@ViewInject(R.id.tv_fd_param_rise)
	private TextView tv_fd_param_rise;
	// 基金累计净值
	@ViewInject(R.id.tv_fd_param_accumulated_net_worth)
	private TextView tv_fd_param_accumulated_net_worth;
	// 基金排名
	@ViewInject(R.id.tv_fd_param_ranking_num)
	private TextView tv_fd_param_ranking_num;
	// 基金排名总数
	@ViewInject(R.id.tv_fd_param_ranking_total)
	private TextView tv_fd_param_ranking_total;
	// 基金费率（已去掉）
	@ViewInject(R.id.tv_fd_param_rate)
	private TextView tv_fd_param_rate;
	// 每万份收益（货币）
	@ViewInject(R.id.tv_fd_param_accrual)
	private TextView tv_fd_param_accrual;
	// 7日年化收益（货币）
	@ViewInject(R.id.tv_fd_param_annual_rate)
	private TextView tv_fd_param_annual_rate;
	// 收益计算日期（货币）
	@ViewInject(R.id.tv_fd_param_accrual_date)
	private TextView tv_fd_param_accrual_date;
	// 基金排名（货币）
	@ViewInject(R.id.tv_fd_param_ranking_num_money)
	private TextView tv_fd_param_ranking_num_money;
	// 基金排名总数（货币）
	@ViewInject(R.id.tv_fd_param_ranking_total_money)
	private TextView tv_fd_param_ranking_total_money;
	// 基金管理人
	@ViewInject(R.id.tv_fd_param_managers)
	private TextView tv_fd_param_managers;
	// 非货币基金参数
	@ViewInject(R.id.ll_fund_details_param)
	private LinearLayout ll_fund_details_param;
	// 货币基金列表标题
	@ViewInject(R.id.ll_fund_details_money_param)
	private LinearLayout ll_fund_details_money_param;
	// 非货币基金列表标题
	@ViewInject(R.id.ll_list_title)
	private LinearLayout ll_list_title;
	// 货币基金参数
	@ViewInject(R.id.ll_list_title_money)
	private LinearLayout ll_list_title_money;
	// 基金查看明细
	@ViewInject(R.id.tv_fd_param_details)
	private TextView tv_fd_param_details;
	// 基金日净值历史明细
	@ViewInject(R.id.lv_fd_history_list)
	private MyListView lv_fd_history_list;

	@ViewInject(R.id.rb_1)
	private RadioButton rb_1;
	@ViewInject(R.id.rb_2)
	private RadioButton rb_2;

	@ViewInject(R.id.cc)
	private CombinedChart combinedChart;

	private ArrayList<HashMap<String, Object>> lv_fund_details_hashMaps;
	private lvFundDetailsListAdapter adapter;

	private int chart_item_num = 23;
	private int list_item_num = 10;

	// 初始化
	RequestQueue volleyRequestQueue;
	// 基金代码
	private String symbol;

	// 基金代码
	private String shortFinacing = "false";

	// 基金类型
	private String category = "股票型基金";

	private String type = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_fund_net_worth, null);
		ViewUtils.inject(this, v);
		volleyRequestQueue = Volley.newRequestQueue(getActivity());

		initView();

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onActivityCreated(savedInstanceState);
	}

	@OnClick({ R.id.tv_fd_param_details, R.id.rb_1, R.id.rb_2, R.id.rb_month,
			R.id.rb_festival, R.id.rb_halfyear, R.id.rb_year })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_fd_param_details:
			Intent intent = new Intent(getActivity(),
					FundDetailsWatchDetailsActivity.class);
			intent.putExtra("symbol", symbol);
			startActivity(intent);
			break;
		case R.id.rb_1:
			if (shortFinacing.equals("false") && !category.equals("货币型基金")) {// 如果是非货币基金
			type = "nav";
			} else {
				type = "achievereturn";
			}

			showCombinedChart(combinedChart, lv_fund_details_hashMaps,
					lv_fund_details_hashMaps.size(), type);
			break;
		case R.id.rb_2:
			if (shortFinacing.equals("false") && !category.equals("货币型基金")) {// 如果是非货币基金
				type = "accumulativenav";
			} else {
				type = "annualizedyield";
			}
			showCombinedChart(combinedChart, lv_fund_details_hashMaps,
					lv_fund_details_hashMaps.size(), type);
			break;
		case R.id.rb_month:
			chart_item_num = 23;
			getNavHistory(0, chart_item_num);
			break;
		case R.id.rb_festival:
			chart_item_num = 69;
			getNavHistory(0, chart_item_num);
			break;
		case R.id.rb_halfyear:
			chart_item_num = 138;
			getNavHistory(0, chart_item_num);
			break;
		case R.id.rb_year:
			chart_item_num = 276;
			getNavHistory(0, chart_item_num);
			break;
		}
	}

	private void initView() {

		lv_fund_details_hashMaps = new ArrayList<HashMap<String, Object>>();

		symbol = getActivity().getIntent().getStringExtra("symbol");
		getFundDetails();
		// getNavHistory(0, 5);
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
			return list.size() > list_item_num ? list_item_num : list.size();
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
			if (shortFinacing.equals("false") && !category.equals("货币型基金")) {// 如果是非货币基金
				if (view == null) {
					holder = new ViewHolder();
					view = LayoutInflater.from(context).inflate(
							R.layout.item_fund_details_history, null);
					holder.item_fund_details_date = (TextView) view
							.findViewById(R.id.item_fund_details_date);
					holder.item_fund_details_asset = (TextView) view
							.findViewById(R.id.item_fund_details_asset);
					holder.item_fund_details_accumulate = (TextView) view
							.findViewById(R.id.item_fund_details_accumulate);
					holder.item_fund_details_growth_rate = (TextView) view
							.findViewById(R.id.item_fund_details_growth_rate);
					holder.item_fund_details_accraul = (TextView) view
							.findViewById(R.id.item_fund_details_accraul);
					holder.item_fund_details_annual_rate = (TextView) view
							.findViewById(R.id.item_fund_details_annual_rate);

					view.setTag(holder);

				} else {
					holder = (ViewHolder) view.getTag();
				}

				try {
					long Time = Long.parseLong(list.get(position)
							.get("tradingdate").toString());
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
					String creationTime = dateFormat.format(new Date(Time));
					holder.item_fund_details_date.setText(creationTime);
				} catch (Exception e) {
					holder.item_fund_details_date.setText("--");
				}
				try {
					String nav = (String) list.get(position).get("nav");
					holder.item_fund_details_asset.setText(String.format(
							"%.4f", Double.parseDouble(nav)));
				} catch (Exception e) {
					holder.item_fund_details_asset.setText("--");
				}
				try {
					String accumulativenav = (String) list.get(position).get(
							"accumulativenav");
					holder.item_fund_details_accumulate.setText(String.format(
							"%.4f", Double.parseDouble(accumulativenav)));
				} catch (NumberFormatException e) {
					holder.item_fund_details_accumulate.setText("--");
				}
				try {
					Double rise = Double.parseDouble((String) list
							.get(position).get("rise"));
					if (rise > 0) {
						holder.item_fund_details_growth_rate.setText("+"
								+ String.format("%.2f", rise) + "%");
					} else {
						holder.item_fund_details_growth_rate.setText(String
								.format("%.2f", rise) + "%");
					}
				} catch (NumberFormatException e) {
					holder.item_fund_details_growth_rate.setText("--");
				}
			} else {// 如果是货币基金
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

				try {
					long Time = Long.parseLong(list.get(position)
							.get("tradingdate").toString());
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
					String creationTime = dateFormat.format(new Date(Time));
					holder.item_fund_details_date.setText(creationTime);
				} catch (NumberFormatException e) {
					holder.item_fund_details_date.setText("--");
				}
				try {
					String achievereturn = (String) list.get(position).get(
							"achievereturn");
					holder.item_fund_details_accraul.setText(String.format(
							"%.4f", Double.parseDouble(achievereturn)));
				} catch (NumberFormatException e) {
					holder.item_fund_details_accraul.setText("--");
				}
				try {
					Double annualizedyield = Double.parseDouble((String) list
							.get(position).get("annualizedyield"));
					if (annualizedyield > 0) {
						holder.item_fund_details_annual_rate.setText("+"
								+ String.format("%.2f", annualizedyield) + "%");
					} else {
						holder.item_fund_details_annual_rate.setText(String
								.format("%.2f", annualizedyield) + "%");
					}
				} catch (NumberFormatException e) {
					holder.item_fund_details_annual_rate.setText("--");
				}
			}

			return view;
		}

		class ViewHolder {

			TextView item_fund_details_date;
			TextView item_fund_details_asset;
			TextView item_fund_details_accumulate;
			TextView item_fund_details_growth_rate;
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
									category = detailsObject
											.getString("category");
									tv_fd_fund_type.setText(category);
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

									shortFinacing = detailsObject
											.getString("shortFinancing");
									if (shortFinacing.equals("false")
											&& !category.equals("货币型基金")) {// 如果是非货币基金
										tv_fd_param_net_worth
												.setText(detailsObject
														.getString("nav"));
										String rise = detailsObject
												.getString("rise");
										tv_fd_param_rise.setText(String.format(
												"%.2f",
												Double.parseDouble(rise) * 10)
												+ "%");
										tv_fd_param_accumulated_net_worth.setText(detailsObject
												.getString("accumulativenav"));
										tv_fd_param_ranking_num
												.setText(detailsObject
														.getString("rank"));
										tv_fd_param_ranking_total
												.setText(detailsObject
														.getString("count"));
										tv_fd_param_rate
												.setVisibility(View.INVISIBLE);
									} else {// 如果是货币基金
										ll_fund_details_param
												.setVisibility(View.GONE);
										ll_fund_details_money_param
												.setVisibility(View.VISIBLE);
										ll_list_title.setVisibility(View.GONE);
										ll_list_title_money
												.setVisibility(View.VISIBLE);

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
										tv_fd_param_ranking_num_money
												.setText(detailsObject
														.getString("rank"));
										tv_fd_param_ranking_total_money
												.setText(detailsObject
														.getString("count"));
									}

									tv_fd_param_managers.setText(detailsObject
											.getString("fundcompanyname"));

									if (shortFinacing.equals("false")
											&& !category.equals("货币型基金")) {// 如果是非货币基金
										type = "nav";
									} else {// 如果是货币基金
										type = "achievereturn";
										rb_1.setText("每万份收益（月）");
										rb_2.setText("7日年化收益率");
									}

									getNavHistory(0, chart_item_num);

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
											e.printStackTrace();
										}
										datas.add(data);
									}
									lv_fund_details_hashMaps = datas;
									adapter = new lvFundDetailsListAdapter(
											getActivity(),
											lv_fund_details_hashMaps);
									lv_fd_history_list.setAdapter(adapter);
									showCombinedChart(combinedChart,
											lv_fund_details_hashMaps,
											lv_fund_details_hashMaps.size(),
											type);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
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
		String url = AppConfig.URL_USER
				+ "security.json?access_token="
				+ RsSharedUtil.getString(getActivity(),
						"access_token")
						+ "&followType=" + follow;
		JSONArray array = new JSONArray();
		JSONObject params = new JSONObject();
		try {
			params.put("symbol", symbol);
			params.put("type", "FUND");
			array.put(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("params", params.toString());
		StringRequest stringRequest = new StringRequest(array,
				url, new Listener<String>() {

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
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							Log.d("error_Exception", e.toString());
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

	private void showCombinedChart(CombinedChart combinedChart,
			ArrayList<HashMap<String, Object>> arrayList, int count, String type) {

		float gridWidth = 0.5f;

		if (arrayList.size() < count) {
			count = arrayList.size();
		}

		combinedChart.clear();
		combinedChart.setNoDataText("暂无数据");
		combinedChart.setDescription("");
		combinedChart.setDrawGridBackground(false);
		combinedChart.getLegend().setEnabled(false);
		combinedChart.setDragEnabled(false);
		// combinedChart.setPinchZoom(false);
		combinedChart.setDoubleTapToZoomEnabled(false);

		// draw bars behind lines
		combinedChart.setDrawOrder(new DrawOrder[] {
				DrawOrder.LINE });

		String[] dates = new String[count];
		for (int i = 0; i < count; i++) {
			long Time = Long.parseLong(arrayList.get(arrayList.size() - 1 - i)
					.get("tradingdate").toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			dates[i] = dateFormat.format(new Date(Time));

		}

		CombinedData data = new CombinedData(dates);

		int[] colors = { Color.WHITE };
		// int[] colors = { Color.WHITE };
		data.setData(generateLineData(arrayList, count, type));

		XAxis xAxis = combinedChart.getXAxis();
		xAxis.setAxisLineColor(Color.LTGRAY);
		xAxis.setGridColor(Color.WHITE);
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setTextColor(Color.BLACK);
		xAxis.setAxisLineWidth(gridWidth);
		xAxis.setGridLineWidth(gridWidth);
		xAxis.setLabelsToSkip(count - 2);
		xAxis.setTextSize(9.0f);
		xAxis.setAvoidFirstLastClipping(true);
		// xAxis.setDrawLabels(false);

		YAxis yAxisLeft = combinedChart.getAxisLeft();
		yAxisLeft.setAxisLineColor(Color.BLACK);
		yAxisLeft.setGridColor(Color.LTGRAY);
		yAxisLeft.setLabelCount(3, false);
		// yAxisLeft.setDrawLabels(false);
		yAxisLeft.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
		yAxisLeft.setAxisMinValue(data.getYMin(AxisDependency.LEFT));
		yAxisLeft.setAxisMaxValue(data.getYMax(AxisDependency.LEFT));
		// yAxisLeft.setAxisMinValue(data.getYMin(AxisDependency.LEFT) -
		// 0.2f);
		// yAxisLeft.setAxisMaxValue(data.getYMax(AxisDependency.LEFT) +
		// 0.2f);
		yAxisLeft.setStartAtZero(false);
		yAxisLeft.setTextColor(Color.BLACK);
		yAxisLeft.setAxisLineWidth(gridWidth);
		yAxisLeft.setGridLineWidth(gridWidth);
		yAxisLeft.setTextSize(9.0f);

		YAxis yAxisRight = combinedChart.getAxisRight();
		yAxisRight.setEnabled(false);
		// yAxisRight.setAxisLineColor(Color.WHITE);
		// yAxisRight.setDrawGridLines(false);
		// yAxisRight.setLabelCount(3, false);
		// yAxisRight.setPosition(YAxisLabelPosition.INSIDE_CHART);
		// yAxisRight.setAxisMinValue(data.getYMin(AxisDependency.RIGHT));
		// yAxisRight.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT));
		// yAxisRight.setStartAtZero(false);
		// yAxisRight.setTextColor(Color.WHITE);
		// yAxisRight.setAxisLineWidth(gridWidth);
		// yAxisRight.setTextSize(9.0f);

		combinedChart.setData(data);
		combinedChart.invalidate();

	}

	// 分时图
	private LineData generateLineData(
			ArrayList<HashMap<String, Object>> arrayList, int dataCount,
			String type) {

		LineData d = new LineData();
		ArrayList<Entry> entries = new ArrayList<Entry>();

		for (int index = 0; index < dataCount; index++) {
			float val;
			try {
				val = Float
						.parseFloat(arrayList.get(arrayList.size() - 1 - index)
								.get(type).toString());
				entries.add(new Entry(val, index));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LineDataSet dataSet = new LineDataSet(entries, null);
		dataSet.setAxisDependency(AxisDependency.LEFT);
		dataSet.setColor(Color.GREEN);
		dataSet.setDrawCircles(false);
		dataSet.setLineWidth(0.5f);
		dataSet.setDrawCircles(false);
		dataSet.setDrawFilled(true);
		dataSet.setFillColor(Color.GREEN);
		// dataSet.setDrawCubic(true);
		// dataSet.setCubicIntensity(0.1f);
		dataSet.setDrawValues(false);
		d.addDataSet(dataSet);

		return d;
	}
}
