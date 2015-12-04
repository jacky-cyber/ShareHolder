package com.example.shareholders.adapter;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;

@SuppressLint("NewApi")
public class GridViewAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private List<Map<String, Object>> lists;

	public GridViewAdapter(Context context, List<Map<String, Object>> lists) {
		this.context = context;
		this.lists = lists;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
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
	public View getView(final int position, View converView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (converView == null) {
			viewHolder = new ViewHolder();
			converView = inflater.inflate(R.layout.item_gridview_industry,
					arg2, false);
			viewHolder.tv = (TextView) converView
					.findViewById(R.id.tv_industry_name);
			converView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}

		if ((Boolean) lists.get(position).get("isClick")) {
			viewHolder.tv.setTextColor(context.getResources().getColor(
					R.color.white));
			viewHolder.tv
					.setBackgroundResource(R.drawable.btn_industry_selected_style);
		} else {
			viewHolder.tv.setTextColor(context.getResources().getColor(
					R.color.gridview_item_color));
			viewHolder.tv.setBackgroundResource(R.drawable.btn_industry_style);
		}

		viewHolder.tv.setText((CharSequence) lists.get(position).get(
				"industry_name"));

		viewHolder.tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!(Boolean) lists.get(position).get("isClick")) {
					for (int i = 0; i < lists.size(); i++) {
						lists.get(i).put("isClick", false);
					}
					lists.get(position).put("isClick", true);
					notifyDataSetChanged();
				}

			}
		});
		return converView;
	}

	class ViewHolder {
		TextView tv;
	}

}
