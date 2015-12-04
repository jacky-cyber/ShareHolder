package com.example.shareholders.receiver;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MessageCenterActivity;
import com.example.shareholders.jacksonModel.personal.IMMessageSetting;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;

/**
 * 接收环信新信息
 * 
 * @author jat
 * 
 */
public class NewMessageBroadcastReceiver extends BroadcastReceiver {

	private int survey_message_id = 0;
	private int stock_message_id = 0;
	private int system_message_id = 0;
	private int friend_message_id = 0;
	private int friend_follow_id = 0;
	private List<IMMessageSetting> list = new ArrayList<IMMessageSetting>();
	DbUtils dbUtils;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 注销广播
		abortBroadcast();
		// 初始化数据库
		// 取出所有被屏蔽用户的信息进行消息清零
		try {
			dbUtils = DbUtils.create(context);
			list = dbUtils.findAll(IMMessageSetting.class);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
		String msgId = intent.getStringExtra("msgid");
		// 发送方
		String username = intent.getStringExtra("from");
		// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
		EMMessage message = EMChatManager.getInstance().getMessage(msgId);
		// Log.d("newmessagereciver", message.toString());
		// Log.d("newmessagereciver", message.getStringAttribute("type", null));
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(username);
		// Log.d("pingbi", list.toString());
		// 处理屏蔽消息
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getiMName().equals(username)) {
					conversation.resetUnreadMsgCount();
					EMChatManager.getInstance().clearConversation(username);
//					EMChatManager.getInstance().deleteConversation(username);
				}
			}
		}

		// 调研消息内容
		String tv_survey_content = "";
		// 调研ID
		String uuid_survey = "";
		// 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (conversation.getUserName().equals("my_survey")) {
			if (RsSharedUtil.getBoolean(context, "message_survey_set", true)) {

				try {
					uuid_survey = message.getStringAttribute("senderId");
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TextMessageBody body = (TextMessageBody) message.getBody();
				tv_survey_content = body.getMessage() + "";

				Intent intentSurvey = new Intent(context,
						MessageCenterActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("uuid", uuid_survey);
				intent.putExtras(bundle);
				PendingIntent pendingIntent2 = PendingIntent.getActivity(
						context, 0, intentSurvey, 0);
				// API11之后才支持
				Notification notify2 = new Notification.Builder(context)
						.setSmallIcon(R.drawable.ico_wodediaoyan) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
																	// // icon)
						.setTicker("Hello，股东会有新调研消息...")// 设置在status //
														// bar上显示的提示文字
						.setContentTitle("股东会调研信息")// 设置在下拉status //
													// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
						.setContentText(tv_survey_content)// TextView中显示的详细内容
						.setContentIntent(pendingIntent2) // 关联PendingIntent
						.getNotification(); // 需要注意build()是在API level //
											// 16及之后增加的，在API11中可以使用getNotificatin()来代替
				notify2.flags |= Notification.FLAG_AUTO_CANCEL;
				manager.notify(survey_message_id++, notify2);
			}
		}

		// 自选股通知
		String stock_id = "";
		String stock_content = "";
		if (conversation.getUserName().equals("info_alert")) {
			if (RsSharedUtil.getBoolean(context, "message_stock_set", true)) {
				Intent intentStock = new Intent(context,
						MessageCenterActivity.class);

				TextMessageBody body1 = (TextMessageBody) message.getBody();
				stock_content = body1.getMessage() + "";
				try {
					stock_id = message.getStringAttribute("senderId");
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Bundle bundle = new Bundle();
				bundle.putString("stock_id", stock_id);
				intent.putExtras(bundle);
				PendingIntent pendingIntent2 = PendingIntent.getActivity(
						context, 0, intentStock, 0);
				// API11之后才支持
				Notification notify2 = new Notification.Builder(context)
						.setSmallIcon(R.drawable.ico_wodediaoyan) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
																	// // icon)
						.setTicker("Hello，股东会自选股有新消息...")// 设置在status //
															// bar上显示的提示文字
						.setContentTitle("股东会自选股信息")// 设置在下拉status //
													// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
						.setContentText(stock_content)// TextView中显示的详细内容
						.setContentIntent(pendingIntent2) // 关联PendingIntent
						.getNotification(); // 需要注意build()是在API level //
											// 16及之后增加的，在API11中可以使用getNotificatin()来代替
				notify2.flags |= Notification.FLAG_AUTO_CANCEL;
				manager.notify(stock_message_id++, notify2);
			}
		}

		// 系统通知
		String system_content = "";
		if (conversation.getUserName().equals("security_alert")) {
			if (RsSharedUtil.getBoolean(context, "message_system_set", true)) {
				Intent intentStock = new Intent(context,
						MessageCenterActivity.class);
				TextMessageBody body2 = (TextMessageBody) message.getBody();
				system_content = body2.getMessage() + "";
				/*
				 * Bundle bundle = new Bundle(); bundle.putString("stock_id",
				 * stock_id); intent.putExtras(bundle);
				 */
				PendingIntent pendingIntent2 = PendingIntent.getActivity(
						context, 0, intentStock, 0);
				// API11之后才支持
				Notification notify2 = new Notification.Builder(context)
						.setSmallIcon(R.drawable.ico_wodediaoyan) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
																	// // icon)
						.setTicker("股东会系统新消息...")// 设置在status // bar上显示的提示文字
						.setContentTitle("股东会系统信息")// 设置在下拉status //
													// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
						.setContentText(system_content)// TextView中显示的详细内容
						.setContentIntent(pendingIntent2) // 关联PendingIntent
						.getNotification(); // 需要注意build()是在API level //
											// 16及之后增加的，在API11中可以使用getNotificatin()来代替
				notify2.flags |= Notification.FLAG_AUTO_CANCEL;
				manager.notify(system_message_id++, notify2);
			}
		}

		// 好友消息通知
		// 评论UUID
		String friend_id = "";
		// 评论类型，
		String friend_type = "";
		// 原评论内容
		String friend_extValue1 = "";
		// 评论人昵称
		String friend_extValue2 = "";
		// 评论人logo
		String friend_extValue3 = "";

		if (conversation.getUserName().equals("topic_alert")) {
			if (RsSharedUtil.getBoolean(context, "message_friend_set", true)) {

				Intent intentStock = new Intent(context,
						MessageCenterActivity.class);

				TextMessageBody body3 = (TextMessageBody) message.getBody();
				system_content = body3.getMessage() + "";
				try {
					friend_id = message.getStringAttribute("senderId");
					friend_type = message.getStringAttribute("type");
					friend_extValue1 = message.getStringAttribute("extValue1");
					friend_extValue2 = message.getStringAttribute("extValue2");
					friend_extValue3 = message.getStringAttribute("extValue3");

				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * Bundle bundle = new Bundle();
				 * bundle.putString("friend_id",friend_id);
				 * bundle.putString("friend_type",friend_type);
				 * bundle.putString("friend_extValue1",friend_extValue1);
				 * bundle.putString("friend_extValue2",friend_extValue2);
				 * bundle.putString("friend_extValue3",friend_extValue3);
				 * intent.putExtras(bundle);
				 */

				PendingIntent pendingIntent2 = PendingIntent.getActivity(
						context, 0, intentStock, 0);
				String ticker = "";
				if (friend_type.equals("LIKE")) {
					ticker = "" + friend_extValue2 + "点赞了你的评论...";
				} else if (friend_type.equals("FORWORDING")) {

					ticker = "" + friend_extValue2 + "转发了你的评论...";
				} else {
					ticker = "" + friend_extValue2 + "评论了你的评论...";
				}

				Log.d("Notification notify2 ", "gggggggggggggggggggggg");
				// API11之后才支持
				Bitmap btm = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.ico_wodediaoyan);
				Notification notify2 = new Notification.Builder(context)
						.setSmallIcon(R.drawable.ico_wodediaoyan) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
						.setLargeIcon(btm) // // icon)
						.setTicker(ticker)// 设置在status // bar上显示的提示文字
						.setContentTitle(ticker)// 设置在下拉status //
												// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
						.setContentText(friend_extValue1)// TextView中显示的详细内容
						.setContentIntent(pendingIntent2) // 关联PendingIntent
						.getNotification(); // 需要注意build()是在API level //
											// 16及之后增加的，在API11中可以使用getNotificatin()来代替
				notify2.flags |= Notification.FLAG_AUTO_CANCEL;
				manager.notify(friend_message_id++, notify2);

			}

		}

		/*
		 * // 自选股通知 String stock_id = ""; String stock_content = ""; if
		 * (conversation.getUserName().equals("info_alert")) { if
		 * (RsSharedUtil.getBoolean(context, "message_stock_set", true)) {
		 * Intent intentStock = new Intent(context,
		 * MessageCenterActivity.class);
		 * 
		 * TextMessageBody body1 = (TextMessageBody) message.getBody();
		 * stock_content = body1.getMessage() + ""; try { stock_id =
		 * message.getStringAttribute("senderId"); } catch (EaseMobException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * Bundle bundle = new Bundle(); bundle.putString("stock_id", stock_id);
		 * intent.putExtras(bundle); PendingIntent pendingIntent2 =
		 * PendingIntent.getActivity( context, 0, intentStock, 0); // API11之后才支持
		 * Notification notify2 = new Notification.Builder(context)
		 * .setSmallIcon(R.drawable.ico_wodediaoyan) //
		 * 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示
		 * ，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap) // // icon)
		 * .setTicker("Hello，股东汇自选股有新消息...")// 设置在status // // bar上显示的提示文字
		 * .setContentTitle("股东汇自选股信息")// 设置在下拉status // //
		 * bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
		 * .setContentText(stock_content)// TextView中显示的详细内容
		 * .setContentIntent(pendingIntent2) // 关联PendingIntent
		 * .getNotification(); // 需要注意build()是在API level // //
		 * 16及之后增加的，在API11中可以使用getNotificatin()来代替 notify2.flags |=
		 * Notification.FLAG_AUTO_CANCEL; manager.notify(survey_message_id++,
		 * notify2); } }
		 */
		
		
		

		// 好友关注通知
		if (conversation.getUserName().equals("user_follow")) {
			
			Log.d("friend_follow", "success");
			
			Intent intentFriend = new Intent(context,
					MessageCenterActivity.class);
			intentFriend.putExtra("user_follow", "user_follow");

			PendingIntent pendingIntent2 = PendingIntent.getActivity(context,
					0, intentFriend, 0);
			// API11之后才支持
			Notification notify2 = new Notification.Builder(context)
					.setSmallIcon(R.drawable.invest_circle) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
																// // icon)
					.setTicker("股东会系统新消息")// 设置在status // bar上显示的提示文字
					.setContentTitle("股东会消息中心通知")// 设置在下拉status // //
													// bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
					.setContentText("有好友关注了你")// TextView中显示的详细内容
					.setContentIntent(pendingIntent2) // 关联PendingIntent
					.getNotification(); // 需要注意build()是在API level //
										// 16及之后增加的，在API11中可以使用getNotificatin()来代替
			notify2.flags |= Notification.FLAG_AUTO_CANCEL;
			manager.notify(friend_follow_id++, notify2);
		}

		
		
		// 如果是群聊消息，获取到group id
		if (!username.equals(username)) {
			// 消息不是发给当前会话，return
			return;
		}
	}

}
