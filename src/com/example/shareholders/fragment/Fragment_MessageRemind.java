package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.InitSmackStaticCode;
import com.easemob.chat.TextMessageBody;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MessageMySurveyActivity;
import com.example.shareholders.activity.personal.MessageShareRemindActivity;
import com.example.shareholders.activity.personal.MessageShopMessageActivity;
import com.example.shareholders.activity.personal.MessageSystemActivity;
import com.example.shareholders.activity.personal.PersonCommentActivity;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_MessageRemind extends Fragment {
	// 未读系统消息
	@ViewInject(R.id.tv_message_center_system_count)
	private TextView tv_message_center_system_count;
	// 未读评论消息
	@ViewInject(R.id.tv_message_center_comment_count)
	private TextView tv_message_center_comment_count;
	// 调研未读消息
	@ViewInject(R.id.tv_message_center_survey_count)
	private TextView tv_message_center_survey_count;
	// 股价提醒
	@ViewInject(R.id.tv_message_center_gujiatixing_count)
	private TextView tv_message_center_gujiatixing_count;
	// 系统消息内容
	@ViewInject(R.id.tv_system_content)
	private TextView tv_system_content;
	// 调用消息内容
	@ViewInject(R.id.tv_remind_content)
	private TextView tv_survey_content;
	// 股价提醒内容
	@ViewInject(R.id.tv_stock_remind_content)
	private TextView tv_stock_remind_content;
	// 评论内容
	@ViewInject(R.id.tv_comment_content)
	private TextView tv_comment_content;
	// 我的调用时间
	@ViewInject(R.id.message_center_time_1)
	private TextView message_center_time_1;
	// 股价提醒时间
	@ViewInject(R.id.message_center_time_2)
	private TextView message_center_time_2;
	// 评论动态时间
	@ViewInject(R.id.message_center_time_4)
	private TextView message_center_time_3;
	// 系统消息时间
	@ViewInject(R.id.message_center_time_5)
	private TextView message_center_time_4;
	// 环信conversation
	EMConversation conversation;
	// 未读数目
	int survey_count, system_count, comment_count, gujiatixing_count;
	
	//环信登录是否成功
	private int flag = 0;
	
	private BroadcastReceiver broadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getExtras().getInt("delete")==1)
			{
				tv_survey_content.setText("暂无消息");
				message_center_time_1.setText("");
			}
			if(intent.getExtras().getInt("delete")==2)
			{
				tv_stock_remind_content.setText("暂无消息");
				message_center_time_2.setText("");
			}
			if(intent.getExtras().getInt("delete")==3)
			{
				tv_comment_content.setText("暂无消息");
				message_center_time_3.setText("");
			}
			if(intent.getExtras().getInt("delete")==4)
			{
				tv_system_content.setText("暂无消息");
				message_center_time_4.setText("");
			}
			
		}};
		
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				
				break;
			case 1:
				// 更新未读数据
				if (flag==1) {
					init();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_message_activity_remind,
				null);
		ViewUtils.inject(this, v);
		loginIm();
		return v;
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		IntentFilter intentFilter=new IntentFilter("delete_message_center");
		getActivity().registerReceiver(broadcastReceiver,intentFilter);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(1);
		super.onResume();
	}

	/**
	 * 未读消息初始化 初始化时间和内容
	 */
	private void init() {
		EMMessage message;
		conversation = EMChatManager.getInstance().getConversation("my_survey");
		survey_count = conversation.getUnreadMsgCount();
		if (survey_count == 0) {
			tv_message_center_survey_count.setVisibility(View.GONE);
		} else {
			tv_message_center_survey_count.setVisibility(View.VISIBLE);
			tv_message_center_survey_count.setText(survey_count + "");

		}
		if (conversation.getAllMsgCount() > 0) {
			message = conversation.getLastMessage();
			TextMessageBody body = (TextMessageBody) message.getBody();
			tv_survey_content.setText(body.getMessage() + "");
			message_center_time_1.setText(dataFormat(message.getMsgTime()));
		}
		conversation = EMChatManager.getInstance().getConversation(
				"topic_alert");
		comment_count = conversation.getUnreadMsgCount();
		if (comment_count == 0) {
			tv_message_center_comment_count.setVisibility(View.GONE);
		} else {
			tv_message_center_comment_count.setVisibility(View.VISIBLE);
			tv_message_center_comment_count.setText(comment_count + "");
		}
		if (conversation.getAllMsgCount() > 0) {
			message = conversation.getLastMessage();
			TextMessageBody body = (TextMessageBody) message.getBody();
			tv_comment_content.setText(body.getMessage());
			message_center_time_3.setText(dataFormat(message.getMsgTime()));
		}
		conversation = EMChatManager.getInstance()
				.getConversation("app_system");
		system_count = conversation.getUnreadMsgCount();
		if (system_count == 0) {
			tv_message_center_system_count.setVisibility(View.GONE);
		} else {
			tv_message_center_system_count.setVisibility(View.VISIBLE);
			tv_message_center_system_count.setText(system_count + "");
		}
		if (conversation.getAllMsgCount() > 0) {
			message = conversation.getLastMessage();
			TextMessageBody body = (TextMessageBody) message.getBody();
			tv_system_content.setText(body.getMessage());
			message_center_time_4.setText(dataFormat(message.getMsgTime()));
		}
		conversation = EMChatManager.getInstance().getConversation(
				"security_alert");
		gujiatixing_count = conversation.getUnreadMsgCount();
		if (gujiatixing_count == 0) {
			tv_message_center_gujiatixing_count.setVisibility(View.GONE);
		} else {
			tv_message_center_gujiatixing_count.setVisibility(View.VISIBLE);
			tv_message_center_gujiatixing_count.setText(gujiatixing_count + "");
		}
		if (conversation.getAllMsgCount() > 0) {
			message = conversation.getLastMessage();
			TextMessageBody body = (TextMessageBody) message.getBody();
			tv_stock_remind_content.setText(body.getMessage());
			message_center_time_2.setText(dataFormat(message.getMsgTime()));
		}
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

	@OnClick({ R.id.message_center_comment_line,
			R.id.message_center_survey_line,
			R.id.message_center_shareprice_line,
			R.id.message_center_system_line})
	private void onClick(View v) {
		Intent intent = new Intent();
		if (flag==1) {
			switch (v.getId()) {
			case R.id.message_center_survey_line://调研
				intent.setClass(getActivity(), MessageMySurveyActivity.class);
				startActivity(intent);
				break;
			case R.id.message_center_comment_line://评论
				intent.setClass(getActivity(), PersonCommentActivity.class);
				startActivity(intent);
				break;

			case R.id.message_center_shareprice_line://股票
				intent.setClass(getActivity(), MessageShareRemindActivity.class);
				startActivity(intent);
				
				break;
			case R.id.message_center_system_line:
				intent.setClass(getActivity(), MessageSystemActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 登录环信
	 */
	private void loginIm() {
		String imUserName = null;
		String imPassword = null;
		try {
			imUserName = RsSharedUtil.getString(getActivity(),
					AppConfig.IMUSER_NAME);
			imPassword = RsSharedUtil.getString(getActivity(),
					AppConfig.IMUSER_PASSWORD);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (imUserName!=null&&imPassword!=null&&!imUserName.equals("")&&!imPassword.equals("")) {
			// 环信登录
			EMChatManager.getInstance().login(imUserName, imPassword,
					new EMCallBack() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Log.d("main", "登陆聊天服务器成功！");
							flag=1;
							// 设置环信自动登录
							EMChat.getInstance().setAutoLogin(true);
							mHandler.sendEmptyMessage(1);
						}

						@Override
						public void onProgress(int arg0, String arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.d("main", "登陆聊天服务器失败！");
							mHandler.sendEmptyMessage(0);
						}
					});
		}else {
			mHandler.sendEmptyMessage(0);
		}
	}

}
