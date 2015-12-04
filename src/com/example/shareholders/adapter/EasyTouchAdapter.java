package com.example.shareholders.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shareholders.R;

public class EasyTouchAdapter extends BaseAdapter {
	private ViewHolder holder;
	private ArrayList<HashMap<String, Object>> list;
	private Context context;
	private LayoutInflater mInflater;

	public EasyTouchAdapter(Context context,
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
					R.layout.item_easy_touch_, null);

			holder.iv_etl_right = (ImageView) view
					.findViewById(R.id.iv_etl_right);
			holder.tv_etl_name = (TextView) view.findViewById(R.id.tv_etl_name);
			holder.tv_etl_text = (TextView) view.findViewById(R.id.tv_etl_text);
			holder.tv_etl_time = (TextView) view.findViewById(R.id.tv_etl_time);
			holder.tv_etl_title = (TextView) view
					.findViewById(R.id.tv_etl_title);

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.iv_etl_right.setImageResource((Integer) list.get(position).get(
				"iv_etl_right"));
		holder.tv_etl_name.setText((CharSequence) list.get(position).get(
				"tv_etl_name"));
		holder.tv_etl_text.setText((CharSequence) list.get(position).get(
				"tv_etl_text"));
		holder.tv_etl_time.setText((CharSequence) list.get(position).get(
				"tv_etl_time"));
		holder.tv_etl_title.setText((CharSequence) list.get(position).get(
				"tv_etl_title"));

		return view;
	}

	class ViewHolder {

		TextView tv_etl_time;
		ImageView iv_etl_right;
		TextView tv_etl_name;
		TextView tv_etl_text;
		TextView tv_etl_title;

	}
}
