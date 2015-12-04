package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.shareholders.R;
import com.example.shareholders.adapter.CommentListViewAdapter;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * @category 这个类是MessageCenterActivity里面的消息中心 评论动态Activity
 * */
@ContentView(R.layout.activity_person_comment)
public class PersonCommentActivity extends Activity {

	@ViewInject(R.id.message_comment_list)
	private ListView friendList;
	@ViewInject(R.id.rl_no_content)
	private RelativeLayout rl_no_content;
	

	private CommentListViewAdapter adapter;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	// 评论信息列表
	private List<EMMessage> messages = new ArrayList<EMMessage>();

	// 环信conversation
	EMConversation conversation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewUtils.inject(this);
		
		// initView();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		conversation = EMChatManager.getInstance().getConversation(
				"topic_alert");
		messages = conversation.getAllMessages();
		list.clear();
		if (adapter != null) {

			if (messages.size() == 0) {
				adapter.notifyDataSetChanged();
			}
		}
		for (int i = 0; i < messages.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 获取消息内容
			TextMessageBody body = (TextMessageBody) messages.get(i).getBody();
			map.put("content", body.getMessage());
			try {
				map.put("time", messages.get(i).getMsgTime());
				map.put("type", messages.get(i).getStringAttribute("type"));
				map.put("senderId",
						messages.get(i).getStringAttribute("senderId"));
				map.put("extValue1",
						messages.get(i).getStringAttribute("extValue1"));
				map.put("extValue2",
						messages.get(i).getStringAttribute("extValue2"));
				map.put("extValue3",
						messages.get(i).getStringAttribute("extValue3"));
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
			list.add(map);
		}
		if (messages.size()==0) {
			rl_no_content.setVisibility(View.VISIBLE);
		}else {
			sortData();
			adapter = new CommentListViewAdapter(this, list);
			friendList.setAdapter(adapter);
		}
		// 所有评论未读消息数清零
		conversation.resetUnreadMsgCount();
	}

	// 根据评论时间进行排序
	private void sortData() {
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int ret = 0;
				try {
					ret = ((Long) o2.get("time")).compareTo((Long) o1
							.get("time"));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return ret;
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 获取数据
		initData();
	}

	@OnClick({ R.id.message_comment_return, R.id.tv_comment_set })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_comment_return:
			finish();
			break;
		case R.id.tv_comment_set:
			Intent intent = new Intent(this, MessageCenterSetting.class);
			intent.putExtra("type", "friend");
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
