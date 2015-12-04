package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.shareholders.activity.fund.FundDetailsActivity;
import com.example.shareholders.activity.fund.FundSearchActivity;
import com.example.shareholders.activity.fund.OptionalFundActivity.ManageFlag;
import com.example.shareholders.common.DragListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_OptionalFundDisplay extends Fragment implements
		OnItemClickListener {

	@ViewInject(R.id.fl_optional_fund_add_tip)
	private FrameLayout fl_optional_fund_add_tip;
	@ViewInject(R.id.ll_optional_fund_list)
	private LinearLayout ll_optional_fund_list;
	@ViewInject(R.id.ll_optional_fund_item_title)
	private LinearLayout ll_optional_fund_item_title;
	// 基金列表
	@ViewInject(R.id.mv_foud_list)
	private DragListView lv_foud_list;
	// 基金名字
	@ViewInject(R.id.rl_fl_foud_name_title)
	private TextView rl_fl_foud_name_title;
	// 基金万份收益（货币）、最新净值（非货币）
	@ViewInject(R.id.rl_fl_foud_num_title)
	private TextView rl_fl_foud_num_title;
	// 基金7日年变化率（货币）、日涨幅（非货币）
	@ViewInject(R.id.rl_fl_foud_accrual_title)
	private TextView rl_fl_foud_accrual_title;

	private lvFoudListAdapter lv_foud_adapter;
	private ArrayList<HashMap<String, Object>> lv_foud_hashMaps;
	private HashMap<String, Object> hashMap;

	private int fund_select_num;
	final int OPEN_FUND_NUM = 0;
	final int CLOSED_FUND_NUM = 1;
	final int MONEY_FUND_NUM = 2;

	// 初始化
	RequestQueue volleyRequestQueue;

	public Fragment_OptionalFundDisplay() {
	};

	public Fragment_OptionalFundDisplay(int num) {
		fund_select_num = num;
	};

	// 回调接口
	private onUpdateListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (onUpdateListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onUpdateListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_optional_fund_display, null);
		ViewUtils.inject(this, v);
		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		initList();
		initView();
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		getConcernedFund();
		super.onResume();
	}

	@OnClick(R.id.tv_optional_fund_add_tip)
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.tv_optional_fund_add_tip:
			startActivity(new Intent(getActivity(), FundSearchActivity.class));
			break;
		default:
			break;
		}
	}

	private void initList() {
		FundCount.item_open_fund_num = 0;
		FundCount.item_closed_fund_num = 0;
		FundCount.item_money_fund_num = 0;
		hashMap = new HashMap<String, Object>();
		lv_foud_hashMaps = new ArrayList<HashMap<String, Object>>();
		lv_foud_list.setFocusable(false);
		lv_foud_list.setOnItemClickListener(this);
	}

	private void initView() {
		switch (fund_select_num) {
		case OPEN_FUND_NUM:
		case CLOSED_FUND_NUM:
			rl_fl_foud_num_title
					.setText(R.string.optional_fund_newest_net_worth);
			rl_fl_foud_accrual_title.setText(R.string.optional_fund_daily_rise);
			break;
		case MONEY_FUND_NUM:
			rl_fl_foud_num_title.setText(R.string.foud_accrual);
			rl_fl_foud_accrual_title.setText(R.string.foud_annual_rate);
			break;
		default:
			break;
		}
		viewInavailable();
		getConcernedFund();
	}

	private void viewAvailable() {
		fl_optional_fund_add_tip.setVisibility(View.GONE);
		ll_optional_fund_list.setVisibility(View.VISIBLE);
	}

	private void viewInavailable() {
		fl_optional_fund_add_tip.setVisibility(View.VISIBLE);
		ll_optional_fund_list.setVisibility(View.GONE);
	}

	private void clearList() {
		lv_foud_hashMaps.clear();
	}

	public class lvFoudListAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public lvFoudListAdapter(Context context,
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
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_activity_optional_fund_list, parent,
						false);
				holder.item_fund_name = (TextView) view
						.findViewById(R.id.item_fund_name);
				holder.item_fund_num = (TextView) view
						.findViewById(R.id.item_fund_num);
				holder.item_fund_type = (TextView) view
						.findViewById(R.id.item_fund_type);
				holder.item_fund_newest_net_worth = (TextView) view
						.findViewById(R.id.item_fund_newest_net_worth);
				holder.item_fund_daily_rate = (TextView) view
						.findViewById(R.id.item_fund_daily_rate);
				holder.item_fund_name_manage = (TextView) view
						.findViewById(R.id.item_fund_name_manage);
				holder.item_fund_num_manage = (TextView) view
						.findViewById(R.id.item_fund_num__manage);
				holder.item_fund_name_rl = (RelativeLayout) view
						.findViewById(R.id.item_fund_name_rl);
				holder.iv_of_item_right = (ImageView) view
						.findViewById(R.id.iv_of_item_right);
				holder.iv_of_item_delete = (ImageView) view
						.findViewById(R.id.iv_of_item_delete);
				holder.iv_of_item_drag = (ImageView) view
						.findViewById(R.id.iv_of_item_drag);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 非管理状态
			holder.item_fund_name.setText((String) list.get(position).get(
					"name"));
			holder.item_fund_num.setText((String) list.get(position).get(
					"symbol"));
			try {
				holder.item_fund_type.setText(list.get(position)
						.get("category").toString().substring(0, 3));
			} catch (Exception e) {
				e.printStackTrace();
			}
			switch (fund_select_num) {
			case OPEN_FUND_NUM:
			case CLOSED_FUND_NUM:
				holder.item_fund_newest_net_worth.setText((String) list.get(
						position).get("nav"));
				holder.item_fund_daily_rate.setText((String) list.get(position)
						.get("lastDayGrowthRate"));
				break;
			case MONEY_FUND_NUM:
				holder.item_fund_newest_net_worth.setText((String) list.get(
						position).get("achieveReturn"));
				holder.item_fund_daily_rate.setText((String) list.get(position)
						.get("annualizedYield"));
				break;
			default:
				break;
			}

			// 管理状态
			holder.item_fund_name_manage.setText((String) list.get(position)
					.get("name"));
			holder.item_fund_num_manage.setText((String) list.get(position)
					.get("symbol"));

			if (ManageFlag.state) {// 管理状态
				holder.item_fund_newest_net_worth.setVisibility(View.GONE);
				holder.item_fund_daily_rate.setVisibility(View.GONE);
				holder.item_fund_name_rl.setVisibility(View.GONE);
				holder.iv_of_item_right.setVisibility(View.GONE);
				ll_optional_fund_item_title.setVisibility(View.INVISIBLE);
				holder.item_fund_name_manage.setVisibility(View.VISIBLE);
				holder.item_fund_num_manage.setVisibility(View.VISIBLE);
				holder.iv_of_item_delete.setVisibility(View.VISIBLE);
				holder.iv_of_item_drag.setVisibility(View.VISIBLE);
			} else {// 非管理状态
				holder.item_fund_newest_net_worth.setVisibility(View.VISIBLE);
				holder.item_fund_daily_rate.setVisibility(View.VISIBLE);
				holder.item_fund_name_rl.setVisibility(View.VISIBLE);
				holder.iv_of_item_right.setVisibility(View.VISIBLE);
				ll_optional_fund_item_title.setVisibility(View.VISIBLE);
				holder.item_fund_name_manage.setVisibility(View.GONE);
				holder.item_fund_num_manage.setVisibility(View.GONE);
				holder.iv_of_item_delete.setVisibility(View.GONE);
				holder.iv_of_item_drag.setVisibility(View.GONE);
			}

			holder.iv_of_item_delete.setOnClickListener(new MyOnClickListener(
					position));

			return view;
		}

		class ViewHolder {

			TextView item_fund_name;
			TextView item_fund_num;
			TextView item_fund_type;
			TextView item_fund_newest_net_worth;
			TextView item_fund_daily_rate;
			TextView item_fund_name_manage;
			TextView item_fund_num_manage;
			RelativeLayout item_fund_name_rl;
			ImageView iv_of_item_right;
			ImageView iv_of_item_delete;
			ImageView iv_of_item_drag;
		}

		public void remove(HashMap<String, Object> dragItem) {
			// TODO Auto-generated method stub
			list.remove(dragItem);
		}

		public void insert(HashMap<String, Object> dragItem, int dragPosition) {
			// TODO Auto-generated method stub
			list.add(dragPosition, dragItem);
		}

		public class MyOnClickListener implements OnClickListener {

			private int position;

			public MyOnClickListener(int position) {
				super();
				this.position = position;

			}

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String symbolString = list.get(position).get("symbol")
						.toString();
				hashMap = list.get(position);
				list.remove(position);
				notifyDataSetChanged();
				noConcern(symbolString, position);
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!ManageFlag.state) {
			Intent intent = new Intent(getActivity(), FundDetailsActivity.class);
			try {
				intent.putExtra("fund_type", lv_foud_hashMaps.get(position)
						.get("category").toString());
			} catch (Exception e) {
				intent.putExtra("fund_type", "货币型基金");
			}
			intent.putExtra("symbol",
					lv_foud_hashMaps.get(position).get("symbol").toString());
			startActivity(intent);
		}

	}

	public void updateView() {
		if (ManageFlag.state) {

		} else {
			getConcernedFund();
		}
		try {
			lv_foud_adapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 获取关注的基金列表
	 * 
	 * @param num
	 */
	private void getConcernedFund() {
		String fundType = null;
		switch (fund_select_num) {
		case OPEN_FUND_NUM:
			fundType = "OPEN";
			break;
		case CLOSED_FUND_NUM:
			fundType = "CLOSE";
			break;
		case MONEY_FUND_NUM:
			fundType = "CURRENCY";
			break;

		default:
			break;
		}
		String url = AppConfig.URL_FUND + "list/myConcerned.json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token")
				// + "d7abc947-6df0-4073-bc74-ba4b24c478b2"
				+ "&fundType=" + fundType;
		Log.d(fundType, fundType + ":" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("获取基金列表", "FUND:" + response.toString());
							// 如果没有数据
							if (response.toString().equals("")
									|| response.toString().equals("[0]")) {
								try {
									viewInavailable();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
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
									viewAvailable();
									lv_foud_hashMaps = datas;
									lv_foud_adapter = new lvFoudListAdapter(
											getActivity(), lv_foud_hashMaps);
									lv_foud_list.setAdapter(lv_foud_adapter);
									switch (fund_select_num) {
									case OPEN_FUND_NUM:
										FundCount.item_open_fund_num = lv_foud_hashMaps
												.size();
										break;
									case CLOSED_FUND_NUM:
										FundCount.item_closed_fund_num = lv_foud_hashMaps
												.size();
										break;
									case MONEY_FUND_NUM:
										FundCount.item_money_fund_num = lv_foud_hashMaps
												.size();
										break;

									default:
										break;
									}
									mListener.onUpdate();
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
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							ToastUtils.showToast(getActivity(),
									jsonObject.getString("description"));

						} catch (Exception e) {

							// ToastUtils.showToast(
							// getApplicationContext(),getResources().getString(
							// R.string.unknown_error));
						}
					}

				});
		volleyRequestQueue.add(stringRequest);
	}

	// 与OptionalFundActivity交互的静态类
	public static class FundCount {
		public static int item_open_fund_num;
		public static int item_closed_fund_num;
		public static int item_money_fund_num;
	}

	/**
	 * 取消关注基金
	 * 
	 * @param follow
	 * @param symbol
	 * @param iv
	 */
	private void noConcern(final String symbol, final int position) {
		String url = AppConfig.URL_USER + "security.json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token");
		JSONObject params = new JSONObject();
		try {
			params.put("follow", "false");
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
						Toast.makeText(getActivity(), "股票" + symbol + "已取消",
								Toast.LENGTH_SHORT).show();
						switch (fund_select_num) {
						case OPEN_FUND_NUM:
							FundCount.item_open_fund_num--;
							if (FundCount.item_open_fund_num <= 0) {
								viewInavailable();
								mListener.onUpdate();
							}
							break;
						case CLOSED_FUND_NUM:
							FundCount.item_closed_fund_num--;
							if (FundCount.item_closed_fund_num <= 0) {
								viewInavailable();
								mListener.onUpdate();
							}
							break;
						case MONEY_FUND_NUM:
							FundCount.item_money_fund_num--;
							if (FundCount.item_money_fund_num <= 0) {
								viewInavailable();
								mListener.onUpdate();
							}
							break;

						default:
							break;
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

							// ToastUtils.showToast(
							// getApplicationContext(),getResources().getString(
							// R.string.unknown_error));
						}

						lv_foud_adapter.insert(hashMap, position);
						lv_foud_adapter.notifyDataSetChanged();
					}

				});
		volleyRequestQueue.add(stringRequest);

	}

	// 回调接口，通知OptionalFundActivity更新
	public interface onUpdateListener {
		public void onUpdate();
	}
}
