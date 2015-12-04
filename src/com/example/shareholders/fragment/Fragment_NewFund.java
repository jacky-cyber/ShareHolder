package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundDetailsActivity;
import com.example.shareholders.activity.fund.FundSearchActivity;
import com.example.shareholders.activity.fund.OptionalFundActivity.ManageFlag;
import com.example.shareholders.common.DragListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_NewFund extends Fragment implements OnItemClickListener {

	@ViewInject(R.id.fl_optional_fund_add_tip)
	private FrameLayout fl_optional_fund_add_tip;
	@ViewInject(R.id.ll_optional_fund_list)
	private LinearLayout ll_optional_fund_list;
	@ViewInject(R.id.ll_optional_fund_item_title)
	private LinearLayout ll_optional_fund_item_title;
	// 基金列表
	@ViewInject(R.id.mv_foud_list)
	private DragListView lv_foud_list;

	private ArrayList<String> lv_foud_name;
	private ArrayList<String> lv_foud_num;
	private ArrayList<String> lv_foud_rate;
	private lvFoudListAdapter lv_foud_adapter;
	private ArrayList<HashMap<String, Object>> lv_foud_hashMaps;

	private int fund_select_num;
	final int open_foud_num = 0;
	final int closed_foud_num = 1;
	final int money_foud_num = 2;
	int item_open_fund_num = 10;
	int item_closed_fund_num = 0;
	int item_money_fund_num = 5;

	public Fragment_NewFund(int num) {
		fund_select_num = num;
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_optional_fund_display, null);
		ViewUtils.inject(this, v);
		initList();
		initView(fund_select_num);
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
		lv_foud_name = new ArrayList<String>();
		lv_foud_num = new ArrayList<String>();
		lv_foud_rate = new ArrayList<String>();
		lv_foud_hashMaps = new ArrayList<HashMap<String, Object>>();
		lv_foud_list.setFocusable(false);
		lv_foud_list.setOnItemClickListener(this);
	}

	private void initView(int num) {
		switch (num) {
		case open_foud_num:
			if (item_open_fund_num > 0) {
				viewAvailable();
				showOpenFund();
			} else {
				viewInavailable();
			}
			break;
		case closed_foud_num:
			if (item_closed_fund_num > 0) {
				viewAvailable();
				showClosedFund();
			} else {
				viewInavailable();
			}
			break;
		case money_foud_num:
			if (item_money_fund_num > 0) {
				viewAvailable();
				showMoneyFund();
			} else {
				viewInavailable();
			}
			break;
		default:
			break;
		}
	}

	private void viewAvailable() {
		fl_optional_fund_add_tip.setVisibility(View.GONE);
		ll_optional_fund_list.setVisibility(View.VISIBLE);
	}

	private void viewInavailable() {
		fl_optional_fund_add_tip.setVisibility(View.VISIBLE);
		ll_optional_fund_list.setVisibility(View.GONE);
	}

	public void showOpenFund() {
		for (int i = 0; i < item_open_fund_num; i++) {
			lv_foud_name.add("广发纳斯达克" + i);
			lv_foud_num.add("000055");
			lv_foud_rate.add("92.11%");
		}
		for (int i = 0; i < item_open_fund_num; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("item_fund_name", lv_foud_name.get(i));
			item.put("item_fund_num", lv_foud_num.get(i));
			item.put("item_fund_daily_rate", lv_foud_rate.get(i));

			lv_foud_hashMaps.add(item);
		}
		lv_foud_adapter = new lvFoudListAdapter(getActivity(), lv_foud_hashMaps);
		lv_foud_list.setAdapter(lv_foud_adapter);

	}

	public void showClosedFund() {
		for (int i = 0; i < item_closed_fund_num; i++) {
			lv_foud_name.add("广发纳斯达克" + i);
			lv_foud_num.add("000055");
			lv_foud_rate.add("92.11%");
		}
		for (int i = 0; i < item_closed_fund_num; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("item_fund_name", lv_foud_name.get(i));
			item.put("item_fund_num", lv_foud_num.get(i));
			item.put("item_fund_daily_rate", lv_foud_rate.get(i));

			lv_foud_hashMaps.add(item);
		}
		lv_foud_adapter = new lvFoudListAdapter(getActivity(), lv_foud_hashMaps);
		lv_foud_list.setAdapter(lv_foud_adapter);

	}

	public void showMoneyFund() {
		for (int i = 0; i < item_money_fund_num; i++) {
			lv_foud_name.add("广发纳斯达克" + i);
			lv_foud_num.add("000055");
			lv_foud_rate.add("92.11%");
		}
		for (int i = 0; i < item_money_fund_num; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("item_fund_name", lv_foud_name.get(i));
			item.put("item_fund_num", lv_foud_num.get(i));
			item.put("item_fund_daily_rate", lv_foud_rate.get(i));

			lv_foud_hashMaps.add(item);
		}
		lv_foud_adapter = new lvFoudListAdapter(getActivity(), lv_foud_hashMaps);
		lv_foud_list.setAdapter(lv_foud_adapter);

	}

	public class lvFoudListAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;

		public lvFoudListAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_activity_optional_fund_list, parent,
						false);
				holder.item_fund_name = (TextView) view
						.findViewById(R.id.item_fund_name_manage);
				holder.item_fund_num = (TextView) view
						.findViewById(R.id.item_fund_num__manage);
				holder.item_fund_daily_rate = (TextView) view
						.findViewById(R.id.item_fund_daily_rate);
				holder.item_fund_name.setVisibility(View.VISIBLE);
				holder.item_fund_num.setVisibility(View.VISIBLE);
				holder.item_fund_daily_rate.setVisibility(View.VISIBLE);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.item_fund_name.setText((String) list.get(position).get(
					"item_fund_name"));
			holder.item_fund_num.setText((String) list.get(position).get(
					"item_fund_num"));
			holder.item_fund_daily_rate.setText((String) list.get(position)
					.get("item_fund_daily_rate"));

			return view;
		}

		class ViewHolder {

			TextView item_fund_name;
			TextView item_fund_num;
			TextView item_fund_daily_rate;
		}

		public void remove(HashMap<String, Object> dragItem) {
			list.remove(dragItem);
		}

		public void insert(HashMap<String, Object> dragItem, int dragPosition) {
			list.add(dragPosition, dragItem);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!ManageFlag.state) {
			Intent intent = new Intent(getActivity(), FundDetailsActivity.class);
			intent.putExtra("fund_type", "" + fund_select_num);
			startActivity(intent);
		}

	}

	public void updateView() {
		try {
			lv_foud_adapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
