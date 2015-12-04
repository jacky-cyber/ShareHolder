package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.adapter.MessgaeMySurveyAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_person_message_mysurvey)
public class MessageMySurveyActivity extends Activity {
	@ViewInject(R.id.person_message_mysurvey)
	private ListView MessageList;

	@ViewInject(R.id.iv_set)
	private ImageView iv_set;
	@ViewInject(R.id.rl_no_content)
	private RelativeLayout rl_no_content;

	// 环信conversation
	EMConversation conversation;

	// 消息集合
	List<EMMessage> messages = new ArrayList<EMMessage>();

	private MessgaeMySurveyAdapter adapter;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		// initView();
	}

	@OnClick({ R.id.message_center_return, R.id.iv_set })
	private void OnClick(View view) {
		switch (view.getId()) {
		case R.id.message_center_return:
			finish();
			break;
		case R.id.iv_set:
			Intent intent = new Intent(this, MessageCenterSetting.class);
			intent.putExtra("type", "survey");
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUnReadMessage();// 读取调研情况
	}

	/**
	 * 读取调研
	 */
	private void getUnReadMessage() {
		// 获取前20条消息
		conversation = EMChatManager.getInstance().getConversation("my_survey");
		messages = conversation.getAllMessages();
		list.clear();
		if (adapter != null) {

			if (messages.size() == 0) {
				adapter.notifyDataSetChanged();
			}
		}
		for (int i = 0; i < messages.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("senderId",
						messages.get(i).getStringAttribute("senderId"));
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TextMessageBody textMessageBody = (TextMessageBody) messages.get(i)
					.getBody();
			map.put("content", textMessageBody.getMessage());
			map.put("time", messages.get(i).getMsgTime());
			map.put("id", messages.get(i).getMsgId());
			list.add(map);
		}
		if (list.size()==0) {
			rl_no_content.setVisibility(View.VISIBLE);
		}else {
			adapter = new MessgaeMySurveyAdapter(this, list);
			MessageList.setAdapter(adapter);
			MessageList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getApplicationContext(), DetailSurveyActivity.class);
					intent.putExtra("uuid",list.get(position).get("senderId").toString());
					startActivity(intent);
				}
			});
		}
		
		conversation.resetUnreadMsgCount();

	}


}
