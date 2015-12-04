package com.example.shareholders.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;

/**
 * 左侧滑出menu的适配器 list: 显示的menu数据 context：上下文对象
 * 
 * @author Lx-1019
 * 
 */
public class LeftMenuListAdapter extends BaseAdapter {

	private List<String> list;
	private Context context;

	public LeftMenuListAdapter(List<String> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold view;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.left_menu_item, null);
			TextView tv_menu_name = (TextView) convertView
					.findViewById(R.id.tv_menu_name);
			view = new ViewHold(tv_menu_name);
			convertView.setTag(view);
		} else {
			view = (ViewHold) convertView.getTag();
		}
		view.tv_menu_name.setText(list.get(position));
		return convertView;
	}

	class ViewHold {
		TextView tv_menu_name;

		public ViewHold(TextView tv_menu_name) {
			super();
			this.tv_menu_name = tv_menu_name;
		}
	}
}
