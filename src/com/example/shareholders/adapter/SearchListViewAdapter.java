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
import com.example.shareholders.common.RoundRectImageView;

public class SearchListViewAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> lists;

	public SearchListViewAdapter(Context context,
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
			converView = inflater.inflate(R.layout.item_search_result, arg2,
					false);

			viewHolder.head = (RoundRectImageView) converView
					.findViewById(R.id.iv_head);
			viewHolder.tv_title = (TextView) converView
					.findViewById(R.id.tv_title);
			viewHolder.tv_time = (TextView) converView
					.findViewById(R.id.tv_time);
			viewHolder.tv_location = (TextView) converView
					.findViewById(R.id.tv_location);
			viewHolder.tv_follow_number = (TextView) converView
					.findViewById(R.id.tv_follow_member_number);
			viewHolder.tv_state = (TextView) converView
					.findViewById(R.id.tv_state);

			converView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}

		viewHolder.head.setImageResource((Integer) lists.get(position).get(
				"icon"));
		viewHolder.tv_title.setText((CharSequence) lists.get(position).get(
				"title"));
		viewHolder.tv_time.setText((CharSequence) lists.get(position).get(
				"time"));
		viewHolder.tv_location.setText((CharSequence) lists.get(position).get(
				"location"));
		viewHolder.tv_follow_number.setText(""
				+ (Integer) lists.get(position).get("follow_members"));
		viewHolder.tv_state.setText((CharSequence) lists.get(position).get(
				"state"));

		return converView;
	}

	class ViewHolder {
		RoundRectImageView head;
		TextView tv_title;
		TextView tv_time;
		TextView tv_location;
		TextView tv_follow_number;
		TextView tv_state;
	}

}
