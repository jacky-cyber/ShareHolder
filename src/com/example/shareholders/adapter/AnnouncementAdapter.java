package com.example.shareholders.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;

public class AnnouncementAdapter extends BaseAdapter {
	private ViewHolder holder;
	private ArrayList<HashMap<String, Object>> list;
	private Context context;
	private LayoutInflater mInflater;

	public AnnouncementAdapter(Context context,
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
					R.layout.item_announcement, null);

			holder.tv_announce_text = (TextView) view
					.findViewById(R.id.tv_announce_text_item);
			holder.tv_announce_date = (TextView) view
					.findViewById(R.id.tv_announce_date_item);

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.tv_announce_text.setText((CharSequence) list.get(position).get(
				"tv_announcement_text"));

		// 发布日期
		String time_str = list.get(position).get("tv_announcement_date")
				.toString();
		Long time_long = Long.parseLong(time_str);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date(time_long));
		holder.tv_announce_date.setText(date);

		return view;
	}

	class ViewHolder {

		TextView tv_announce_text;
		TextView tv_announce_date;

	}
}
