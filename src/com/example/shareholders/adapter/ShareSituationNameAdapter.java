package com.example.shareholders.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;

public class ShareSituationNameAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> lists;

	public ShareSituationNameAdapter(Context context,
			List<Map<String, Object>> lists) {
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
	public View getView(int position, View converView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (converView == null) {
			viewHolder = new ViewHolder();
			converView = inflater.inflate(R.layout.item_share_situation_list,
					arg2, false);
			viewHolder.name = (TextView) converView
					.findViewById(R.id.item_share_situation_name);
			viewHolder.price = (TextView) converView
					.findViewById(R.id.item_share_situation_price);
			converView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}
		viewHolder.name.setText((CharSequence) lists.get(position).get("name"));
		viewHolder.price.setText((CharSequence) lists.get(position)
				.get("price"));
		return converView;
	}

	class ViewHolder {
		TextView name;
		TextView price;
	}

}
