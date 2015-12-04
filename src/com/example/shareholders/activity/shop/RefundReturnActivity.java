package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_refund_return)
public class RefundReturnActivity extends Activity {

	@ViewInject(R.id.lv_refund_return)
	private ListView lv_refund_return;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		lv_refund_return.setAdapter(new RefundReturnAdapter(this,
				new ArrayList<HashMap<String, String>>()));
		lv_refund_return.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positio, long arg3) {
				startActivity(new Intent(RefundReturnActivity.this,
						RefundDetailActivity.class));
			}
		});
	}

	@OnClick({ R.id.iv_return ,R.id.rl_return})
	public void onClick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.iv_return:
			finish();
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	class RefundReturnAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		public RefundReturnAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
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
			if (converView == null) {
				converView = inflater.inflate(R.layout.item_order_list, parent,
						false);
			}
			TextView tv_status = (TextView) AbViewHolder.get(converView,
					R.id.tv_status);
			tv_status.setText("卖家处理中");
			TextView tv_pay = (TextView) AbViewHolder.get(converView,
					R.id.tv_pay);
			tv_pay.setVisibility(View.GONE);

			return converView;
		}

	}

}
