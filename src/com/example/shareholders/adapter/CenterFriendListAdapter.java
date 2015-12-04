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
import com.example.shareholders.common.CircleImageView;

public class CenterFriendListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> lists;

	public CenterFriendListAdapter(Context context,
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
			converView = inflater.inflate(R.layout.item_message_friend_list,
					arg2, false);

			viewHolder.head = (CircleImageView) converView
					.findViewById(R.id.message_center_friend_ic);
			viewHolder.name = (TextView) converView
					.findViewById(R.id.item_friend_name);
			viewHolder.word = (TextView) converView
					.findViewById(R.id.item_friend_say);
			viewHolder.time = (TextView) converView
					.findViewById(R.id.item_friend_time);
			viewHolder.motion = (TextView) converView
					.findViewById(R.id.item_friend_count);

			converView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}

		viewHolder.head.setImageResource((Integer) lists.get(position).get(
				"icon"));
		viewHolder.name.setText((CharSequence) lists.get(position).get("name"));
		viewHolder.word.setText((CharSequence) lists.get(position).get("word"));
		viewHolder.time.setText((CharSequence) lists.get(position).get("time"));
		viewHolder.motion.setText((CharSequence) lists.get(position).get(
				"motion"));
		return converView;
	}

	class ViewHolder {
		CircleImageView head;
		TextView name;
		TextView word;
		TextView time;
		TextView motion;
	}

}
