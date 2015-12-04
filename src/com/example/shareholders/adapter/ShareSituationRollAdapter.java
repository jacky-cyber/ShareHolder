package com.example.shareholders.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;

public class ShareSituationRollAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> list;
	private LayoutInflater minflater;
	private Context context;

	public ShareSituationRollAdapter(Context context,
			ArrayList<HashMap<String, Object>> list) {
		this.context = context;
		this.list = list;
		minflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
	public View getView(int position, View contentView, ViewGroup arg2) {
		ViewHolder viewHolder = null;

		if (contentView == null) {
			contentView = minflater
					.inflate(R.layout.item_share_roll_list, null);

			viewHolder = new ViewHolder();
			viewHolder.srl_content = (TextView) contentView
					.findViewById(R.id.srl_context);
			contentView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) contentView.getTag();
		}

		viewHolder.srl_content.setText((CharSequence) list.get(position).get(
				"content"));

		return contentView;
	}

	class ViewHolder {
		TextView srl_content;

	}

}