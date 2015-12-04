package com.example.shareholders.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.util.CommentJumpUtil;
import com.lidroid.xutils.BitmapUtils;

public class CommentListViewAdapter extends BaseAdapter {
	
/*	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnLoading(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true).cacheOnDisc(true).build();*/
	
	private BitmapUtils bitmapUtils;

	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> lists;

	public CommentListViewAdapter(Context context,
			List<Map<String, Object>> lists) {
		this.context = context;
		
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
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
			converView = inflater.inflate(R.layout.item_message_comment_list,
					arg2, false);

			viewHolder.head = (CircleImageView) converView
					.findViewById(R.id.message_center_headView);
			viewHolder.name = (TextView) converView
					.findViewById(R.id.friend_name);
			viewHolder.word = (TextView) converView
					.findViewById(R.id.friend_say);
			viewHolder.time = (TextView) converView
					.findViewById(R.id.friend_time);
			viewHolder.motion = (TextView) converView
					.findViewById(R.id.friend_motion);

			converView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) converView.getTag();
		}

		// viewHolder.head.setImageResource((Integer) lists.get(position).get(
		// "icon"));
		bitmapUtils.display(viewHolder.head, 
				lists.get(position).get("extValue3").toString());
		/*ImageAware imageAware = new ImageViewAware(viewHolder.head, false);
		ImageLoader.getInstance().displayImage(lists.get(position).get("extValue3").toString(),
		imageAware, defaultOptions);*/
		/*ImageLoader.getInstance().displayImage(
				lists.get(position).get("extValue3").toString(),
				viewHolder.head);*/
		viewHolder.name.setText((CharSequence) lists.get(position).get(
				"extValue2"));
		viewHolder.word.setText((CharSequence) lists.get(position).get(
				"content"));
		viewHolder.time.setText(dataFormat((Long) lists.get(position).get(
				"time")));
		viewHolder.motion.setText((CharSequence) lists.get(position).get(
				"extValue1"));
		converView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//跳转到评论跳转帮助类，进行个评的跳转
				new CommentJumpUtil(context, lists.get(position).get("senderId").toString());
			}
		});
		return converView;
	}

	class ViewHolder {
		CircleImageView head;
		TextView name;
		TextView word;
		TextView time;
		TextView motion;
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
