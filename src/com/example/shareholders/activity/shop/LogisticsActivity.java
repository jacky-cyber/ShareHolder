package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_logistics)
public class LogisticsActivity extends Activity {

	@ViewInject(R.id.lv_logistics)
	private ListView lv_logistics;

	private ArrayList<HashMap<String, String>> logistics;
	private LogisticsAdapter logisticsAdapter;

	private final int DELIVER = 0;
	private final int ARRIVAL = 1;
	private final int ARRIVING = 2;
	private int[] status = new int[4];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Init();
	}

	@OnClick({ R.id.title_note ,R.id.rl_return})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	private void Init() {
		status[0] = ARRIVAL;
		status[1] = ARRIVING;
		status[2] = ARRIVING;
		status[3] = DELIVER;
		logistics = new ArrayList<HashMap<String, String>>();
		logisticsAdapter = new LogisticsAdapter(getApplicationContext(),
				logistics);
		lv_logistics.setAdapter(logisticsAdapter);
	}

	private class LogisticsAdapter extends BaseAdapter {

		Context context;
		ArrayList<HashMap<String, String>> list;

		public LogisticsAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
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
			if (status[position] == DELIVER) {
				if (view == null) {
					view = LayoutInflater.from(context).inflate(
							R.layout.item_logistic_last, parent, false);
				}
			} else if (status[position] == ARRIVING) {
				if (view == null) {
					view = LayoutInflater.from(context).inflate(
							R.layout.item_logistic_normal, parent, false);
				}
			} else {
				if (view == null) {
					view = LayoutInflater.from(context).inflate(
							R.layout.item_logistic_first, parent, false);
				}
			}

			return view;
		}

	}

}
