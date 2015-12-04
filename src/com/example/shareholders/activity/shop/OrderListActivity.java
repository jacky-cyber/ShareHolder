package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_order_list)
public class OrderListActivity extends Activity {

	@ViewInject(R.id.lv_order_list)
	private ListView lv_order_list;
	@ViewInject(R.id.iv_all)
	private ImageView iv_all;
	@ViewInject(R.id.iv_obligations)
	private ImageView iv_obligations;
	@ViewInject(R.id.iv_to_deliver)
	private ImageView iv_to_deliver;
	@ViewInject(R.id.iv_to_receive)
	private ImageView iv_to_receive;
	@ViewInject(R.id.iv_to_comment)
	private ImageView iv_to_comment;

	private ArrayList<HashMap<String, String>> al_orders;
	private OrderAdapter orderAdapter;

	private int current_tab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Init();
	}

	@OnClick({ R.id.title_note, R.id.tv_return, R.id.tv_all,
			R.id.tv_obligations, R.id.tv_to_deliver, R.id.tv_to_receive,
			R.id.tv_to_comment,R.id.rl_return })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.tv_return:
			// startActivity(new
			// Intent(OrderListActivity.this,RefundReturnActivity.class));
			startActivity(new Intent(OrderListActivity.this,
					InteractActivity.class));
			break;
		case R.id.tv_all:
			current_tab = 0;
			onTab(current_tab);
			break;
		case R.id.tv_obligations:
			current_tab = 1;
			onTab(current_tab);
			break;
		case R.id.tv_to_deliver:
			current_tab = 2;
			onTab(current_tab);
			break;
		case R.id.tv_to_receive:
			current_tab = 3;
			onTab(current_tab);
			break;
		case R.id.tv_to_comment:
			current_tab = 4;
			onTab(current_tab);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void Init() {
		current_tab = 0;
		al_orders = new ArrayList<HashMap<String, String>>();
		orderAdapter = new OrderAdapter(this, al_orders);
		lv_order_list.setAdapter(orderAdapter);

	}

	private void clearTab() {
		iv_all.setVisibility(View.GONE);
		iv_obligations.setVisibility(View.GONE);
		iv_to_deliver.setVisibility(View.GONE);
		iv_to_receive.setVisibility(View.GONE);
		iv_to_comment.setVisibility(View.GONE);
	}

	private void onTab(int num) {
		clearTab();
		switch (num) {
		case 0:
			iv_all.setVisibility(View.VISIBLE);
			break;
		case 1:
			iv_obligations.setVisibility(View.VISIBLE);
			break;
		case 2:
			iv_to_deliver.setVisibility(View.VISIBLE);
			break;
		case 3:
			iv_to_receive.setVisibility(View.VISIBLE);
			break;
		case 4:
			iv_to_comment.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		;

	}

	private class OrderAdapter extends BaseAdapter {

		Context context;
		ArrayList<HashMap<String, String>> list;

		public OrderAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_order_list, parent, false);
			}

			// 公司
			TextView tv_company = (TextView) AbViewHolder.get(view,
					R.id.tv_company);
			tv_company.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(getApplicationContext(),
							CompanyDetailActivity.class));

				}
			});

			// 商品
			RelativeLayout rl_goods = (RelativeLayout) AbViewHolder.get(view,
					R.id.rl_goods);
			rl_goods.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(getApplicationContext(),
							OrderDetailsActivity.class));
				}
			});

			// 支付
			TextView tv_pay = (TextView) AbViewHolder.get(view, R.id.tv_pay);
			tv_pay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(getApplicationContext(),
							CashierDeskActivity.class));

				}
			});

			return view;
		}

	}
}
