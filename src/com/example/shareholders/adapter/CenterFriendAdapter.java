package com.example.shareholders.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.shop.ChatActivity;
import com.example.shareholders.common.CircleImageView;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class CenterFriendAdapter extends BaseAdapter {

	
	private BitmapUtils bitmapUtils;
	
	private Context context;
	private LayoutInflater inflater;
	private List<Map<String, Object>> lists;

	public CenterFriendAdapter(Context context, List<Map<String, Object>> lists) {
		this.context = context;
		this.lists = lists;
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils
				.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		inflater = LayoutInflater.from(this.context);
		Log.d("jjjjjj", lists.toString() + "6666666666");
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

		} 
		
		else {
			
			viewHolder = (ViewHolder) converView.getTag();
		}
		Log.d("mylogo", lists.toString());
		try {
			if (lists.get(position).get("logo")!=null) {
				bitmapUtils.display(viewHolder.head,
						lists.get(position).get("logo").toString());
			}
			if (lists.get(position).get("username")!=null) {
				viewHolder.name.setText((CharSequence) lists.get(position).get(
						"username"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			/*ImageLoader.getInstance().displayImage(
					(String) lists.get(position).get("logo"), viewHolder.head);*/
			

/*			viewHolder.head.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(context,OtherPeolpeInformationActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("uuid", lists.get(position).get("uuid").toString());
					bundle.putString("userName",lists.get(position).get("username").toString());
					bundle.putString("useLogo", lists.get(position).get("logo").toString());
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});*/

		
		viewHolder.word.setText((CharSequence) lists.get(position).get(
				"content"));
		long time = (Long) lists.get(position).get("time");
		if (time == 0l) {
			viewHolder.time.setText("");
		} else {
			viewHolder.time.setText(dataFormat(time));
		}
		viewHolder.time.setText(dataFormat(time));
		if (lists.get(position).get("count").equals(0)) {
			viewHolder.motion.setVisibility(View.GONE);
		} else {
			viewHolder.motion.setText(lists.get(position).get("count") + "");
		}
		converView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳到聊天界面
				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra("uuid", lists.get(position).get("uuid") + "");
				intent.putExtra("type", lists.get(position).get("type")+"");
				if (lists.get(position).get("type").equals(0)) {
					intent.putExtra("IMName", lists.get(position).get("IMName").toString());
				}
				context.startActivity(intent);

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
