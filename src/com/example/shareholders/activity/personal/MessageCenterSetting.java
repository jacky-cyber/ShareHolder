package com.example.shareholders.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.message_setting)
public class MessageCenterSetting extends Activity {
	// 显示图标
	@ViewInject(R.id.iv_setting_img)
	private ImageView iv_setting_img;
	// 显示标题
	@ViewInject(R.id.iv_setting_name)
	private TextView iv_setting_name;
	// 屏蔽图标
	@ViewInject(R.id.iv_setting_notify)
	private ImageView iv_setting_notify;
	// 清楚所有信息
	@ViewInject(R.id.tv_delete_all)
	private TextView tv_delete_all;

	// 设置类型
	private String type;

	private int id;

	// 推送是否打开
	private boolean isOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		type = getIntent().getExtras().getString("type");
		if (type.equals("survey")) {
			id = 1;
			isOpen = RsSharedUtil.getBoolean(this, "message_survey_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
		} else if (type.equals("stock")) {
			id = 2;

			iv_setting_img.setImageResource(R.drawable.ico_gujiatixing);
			iv_setting_name.setText("股价提醒");
			isOpen = RsSharedUtil.getBoolean(this, "message_stock_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}

		} else if (type.equals("friend")) {
			id = 3;
			iv_setting_img.setImageResource(R.drawable.ico_pinglundongtai);
			iv_setting_name.setText("评论动态");
			isOpen = RsSharedUtil.getBoolean(this, "message_friend_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
		} else if (type.equals("system")) {
			id = 4;
			iv_setting_img.setImageResource(R.drawable.ico_xitongtongzhi);
			iv_setting_name.setText("系统消息");
			isOpen = RsSharedUtil.getBoolean(this, "message_system_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
		}
	}

	@OnClick({ R.id.title_note, R.id.tv_delete_all, R.id.iv_setting_notify,
			R.id.tv_delete_all ,R.id.rl_return})
	private void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.iv_setting_notify:
			setData();
			break;	
		case R.id.tv_delete_all:
			deleteData();
			break;
		case R.id.rl_return:
			finish();
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
		if (id == 1) {
			// 确定按钮的点击事件
			dialog.setMessage("删除所有消息记录吗?");
			dialog.setPositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 取消按钮的点击事件
					EMChatManager.getInstance().clearConversation("my_survey");
					Intent intent=new Intent();
					intent.putExtra("delete", 1);
					intent.setAction("delete_message_center");
					sendBroadcast(intent);
					Toast.makeText(MessageCenterSetting.this, "删除成功！", 2000)
							.show();
					dialog.dismiss();
				}
			});

		}
		if (id == 2) {
			// 确定按钮的点击事件
			dialog.setMessage("删除所有消息记录吗?");
			dialog.setPositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 取消按钮的点击事件
					EMChatManager.getInstance().clearConversation(
							"security_alert");
					Intent intent=new Intent();
					intent.putExtra("delete", 2);
					intent.setAction("delete_message_center");
					sendBroadcast(intent);
					Toast.makeText(MessageCenterSetting.this, "删除成功！", 2000)
							.show();
					dialog.dismiss();
				}
			});

		}
		if (id == 3) {
			// 确定按钮的点击事件
			dialog.setMessage("删除所有消息记录吗?");
			dialog.setPositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 取消按钮的点击事件
					EMChatManager.getInstance()
							.clearConversation("topic_alert");
					Intent intent=new Intent();
					intent.putExtra("delete", 3);
					intent.setAction("delete_message_center");
					sendBroadcast(intent);
					Toast.makeText(MessageCenterSetting.this, "删除成功！", 2000).show();
					dialog.dismiss();
				}
			});

		}
		if (id == 4) {
			// 确定按钮的点击事件
			dialog.setMessage("删除所有消息记录吗?");
			dialog.setPositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 取消按钮的点击事件
					EMChatManager.getInstance().clearConversation("app_system");
					Intent intent=new Intent();
					intent.putExtra("delete", 4);
					intent.setAction("delete_message_center");
					sendBroadcast(intent);
					Toast.makeText(MessageCenterSetting.this, "删除成功！", 2000)
							.show();
					
					dialog.dismiss();
				}
			});

		}

	}

	private void setData() {
		if (id == 1) {

			RsSharedUtil.putBoolean(this, "message_survey_set", !isOpen);
			isOpen = RsSharedUtil.getBoolean(this, "message_survey_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
			Log.d("ttttttttttttttttt", isOpen + "");
		} else if (id == 2) {
			RsSharedUtil.putBoolean(this, "message_stock_set", !isOpen);
			isOpen = RsSharedUtil.getBoolean(this, "message_stock_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
		} else if (id == 3) {
			RsSharedUtil.putBoolean(this, "message_friend_set", !isOpen);
			isOpen = RsSharedUtil.getBoolean(this, "message_friend_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
		} else if (id == 4) {
			RsSharedUtil.putBoolean(this, "message_system_set", !isOpen);
			isOpen = RsSharedUtil.getBoolean(this, "message_system_set", true);
			if (isOpen) {
				iv_setting_notify.setImageResource(R.drawable.open);
			} else {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}
		}
	}

}
