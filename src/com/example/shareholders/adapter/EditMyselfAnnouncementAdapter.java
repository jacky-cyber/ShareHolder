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

public class EditMyselfAnnouncementAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, Object>> list;
	private Context context;
	private LayoutInflater mInflater;

	public EditMyselfAnnouncementAdapter(Context context,
			ArrayList<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	public EditMyselfAnnouncementAdapter(Context context) {
		this.context = context;
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
		ViewHolder viewHolder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_edit_myself_news, null);

			viewHolder = new ViewHolder();
			viewHolder.shortName = (TextView) view
					.findViewById(R.id.tv_company_name);
			viewHolder.symbol = (TextView) view.findViewById(R.id.tv_symbol);
			viewHolder.title = (TextView) view.findViewById(R.id.tv_title);
			viewHolder.declareDaTe = (TextView) view
					.findViewById(R.id.tv_declareDate);

			view.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		try {
			// 股票简称
			viewHolder.shortName.setText((String) list.get(position).get(
					"shortname"));
			// 股票代码
			viewHolder.symbol.setText(list.get(position).get("symbol")
					.toString());

			// 公告标题
			viewHolder.title
					.setText(list.get(position).get("title").toString());

			// 发布日期
			String time_str = list.get(position).get("declaredate").toString();
			Long time_long = Long.parseLong(time_str);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			String date = simpleDateFormat.format(new Date(time_long));
			viewHolder.declareDaTe.setText(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}

	class ViewHolder {
		TextView shortName;
		TextView symbol;
		TextView title;
		TextView declareDaTe;
	}
}
