package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_sponsor_inform)
public class InformSponsorActivity extends Activity {
	@ViewInject(R.id.lv_announce)
	private ListView lv_announce;
	@ViewInject(R.id.iv_brush)
	private ImageView iv_brush;

	private ArrayList<String> al_announce_text;
	private ArrayList<String> al_announce_date;
	private ArrayList<HashMap<String, Object>> hashmap;
	private InformSponsorAdapter am_adapter;
	private int itemSum = 4;
	private String announce_text1 = "活动取消，好可惜，上市公司临时改变了注意，希望下次还有机会";
	private String announce_text2 = "明天的调研活动推迟到后天";
	private String announce_date = "2015.12.12";

	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
		/*
		 * 功能：ListView数据初始化
		 */
		al_announce_text.add(announce_text1);
		al_announce_date.add(announce_date);

		for (int i = 0; i < itemSum; i++) {
			al_announce_text.add(announce_text2);
			al_announce_date.add(announce_date);
		}
		putHashMap(itemSum + 1);
	}

	public void init() {
		al_announce_text = new ArrayList<String>();
		al_announce_date = new ArrayList<String>();
		hashmap = new ArrayList<HashMap<String, Object>>();

		mDialog = new AlertDialog.Builder(InformSponsorActivity.this).create();
	}

	public void putHashMap(int sum) {
		for (int i = 0; i < sum; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("tv_announcement_text", al_announce_text.get(i));
			item.put("tv_announcement_date", al_announce_date.get(i));
			hashmap.add(item);
			am_adapter = new InformSponsorAdapter(getApplicationContext(),
					hashmap);
			lv_announce.setAdapter(am_adapter);

		}

	}

	@OnClick(R.id.iv_brush)
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_brush:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), InformEditActivity.class);
			startActivityForResult(intent, 1);

			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 1 && resultCode == 1) {
			dialogShow();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void dialogShow() {
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_inform_layout);
		((TextView) mDialog.getWindow().findViewById(
				R.id.tv_inform_dialog_content)).setText(getResources()
				.getString(R.string.inform_dialog));
		mDialog.getWindow().findViewById(R.id.btn_inform_confirm)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						mDialog.dismiss();
					}
				});

	}

	/*
	 * ListView的适配器
	 */
	@SuppressLint("ResourceAsColor")
	public class InformSponsorAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public InformSponsorAdapter(Context context,
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
						R.layout.item_inform, null);

				holder.tv_announce_text = (TextView) view
						.findViewById(R.id.tv_announce_text_item);
				holder.tv_announce_date = (TextView) view
						.findViewById(R.id.tv_announce_date_item);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (position == 0) {
				holder.tv_announce_text.setTextColor(R.color.inform_first_text);
			}
			holder.tv_announce_text.setText((CharSequence) list.get(position)
					.get("tv_announcement_text"));
			holder.tv_announce_date.setText((CharSequence) list.get(position)
					.get("tv_announcement_date"));

			return view;
		}

		class ViewHolder {

			TextView tv_announce_text;
			TextView tv_announce_date;

		}
	}

}
