package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.adapter.MessgaeMySurveyAdapter;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_person_message_system)
public class MessageSystemActivity extends Activity {
	@ViewInject(R.id.person_message_mysurvey)
	private ListView MessageList;

	@ViewInject(R.id.rl_no_content)
	private RelativeLayout rl_no_content;
	
	
	@ViewInject(R.id.tv_clear)
	private TextView tv_clear;

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
		tv_clear.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		// initView();
	}

	@OnClick({ R.id.message_center_return, R.id.tv_clear })
	private void OnClick(View view) {
		switch (view.getId()) {
		case R.id.message_center_return:
			finish();
			break;
		case R.id.tv_clear:
			deleteData();
			break;

		default:
			break;
		}
	}

	private void deleteData() {
		final GeneralDialog dialog = new GeneralDialog(this);

		dialog.setCancel(true);
		// 去掉图片提示
		dialog.noMessageIcon();

		dialog.setNegativeButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// 确定按钮的点击事件
		dialog.setMessage("删除所有系统消息吗?");
		dialog.setPositiveButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消按钮的点击事件
				EMChatManager.getInstance().clearConversation("app_system");
				Intent intent = new Intent();
				intent.putExtra("delete", 4);
				intent.setAction("delete_message_center");
				sendBroadcast(intent);
				Toast.makeText(MessageSystemActivity.this, "删除成功！", 2000).show();
				dialog.dismiss();
				getUnReadMessage();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getUnReadMessage();// 读取系统情况
	}

	/**
	 * 读取调研
	 */
	private void getUnReadMessage() {
		// 获取前20条消息
		conversation = EMChatManager.getInstance().getConversation("app_system");
		messages = conversation.getAllMessages();
		list.clear();
		if (adapter != null) {

			if (messages.size() == 0) {
				adapter.notifyDataSetChanged();
			}
		}
		for (int i = 0; i < messages.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();

			TextMessageBody textMessageBody = (TextMessageBody) messages.get(i).getBody();
			map.put("content", textMessageBody.getMessage());
			map.put("time", messages.get(i).getMsgTime());
			map.put("id", messages.get(i).getMsgId());
			list.add(map);
		}
		if (list.size() == 0) {
			rl_no_content.setVisibility(View.VISIBLE);
		} else {
			adapter = new MessgaeMySurveyAdapter(this, list);
			MessageList.setAdapter(adapter);
		}

		conversation.resetUnreadMsgCount();

	}

}
