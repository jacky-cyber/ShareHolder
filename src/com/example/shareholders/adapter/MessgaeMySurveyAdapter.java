package com.example.shareholders.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;

public class MessgaeMySurveyAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> lists;

	public MessgaeMySurveyAdapter(Context context,
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
	public View getView(final int position, View converView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (converView == null) {
			viewHolder = new ViewHolder();
			converView = inflater.inflate(R.layout.item_message_survey_list,
					arg2, false);

			viewHolder.content = (TextView) converView
					.findViewById(R.id.item_message_big);
			viewHolder.time = (TextView) converView
					.findViewById(R.id.item_message_small);
			converView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}
		viewHolder.content.setText((CharSequence) lists.get(position).get(
				"content"));
		long time = (Long) lists.get(position).get("time");
		viewHolder.time.setText(dataFormat(time));

		return converView;
	}

	class ViewHolder {
		TextView content;
		TextView time;
	}

	// 格式化时间
	private String dataFormat(long time) {
		SimpleDateFormat formatter;
		long nowTime = System.currentTimeMillis();
		long date = nowTime - time;
		if (date < 86400000) {
			formatter = new SimpleDateFormat("HH:mm");
		} else if (date > 86400000 && date < 86400000 * 2) {
			return "昨天";
		} else {
			formatter = new SimpleDateFormat("MM-dd");
		}
		String dateString = formatter.format(time);
		return dateString;
	}

}
