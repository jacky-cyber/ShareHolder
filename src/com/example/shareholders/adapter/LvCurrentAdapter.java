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

/*
 * 
 */
public class LvCurrentAdapter extends BaseAdapter {
	private ViewHolder holder;
	private ArrayList<HashMap<String, Object>> list;
	private Context context;
	private LayoutInflater mInflater;

	public LvCurrentAdapter(Context context,
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
					R.layout.item_fragment_price_currentsurvey, null);

			holder.current_icon = (ImageView) view
					.findViewById(R.id.current_icon);
			holder.current_name = (TextView) view
					.findViewById(R.id.current_name);
			holder.current_watch_num = (TextView) view
					.findViewById(R.id.current_watch_num);
			holder.current_time = (TextView) view
					.findViewById(R.id.current_time);
			holder.current_place = (TextView) view
					.findViewById(R.id.current_place);
			holder.tv_state = (TextView) view.findViewById(R.id.tv_state);

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.current_icon.setImageResource((Integer) list.get(position).get(
				"current_icon"));
		holder.current_name.setText((CharSequence) list.get(position).get(
				"current_name"));
		holder.current_watch_num.setText((CharSequence) list.get(position).get(
				"current_watch_num"));
		holder.current_time.setText((CharSequence) list.get(position).get(
				"current_time"));
		holder.current_place.setText((CharSequence) list.get(position).get(
				"current_place"));

		return view;
	}

	class ViewHolder {
		ImageView current_icon;
		TextView current_name;
		TextView current_watch_num;
		TextView current_time;
		TextView current_place;
		TextView tv_state;

	}
}
