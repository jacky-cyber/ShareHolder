package com.example.shareholders.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.example.shareholders.R;
import com.example.shareholders.activity.shop.ChatActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.IMMessageSetting;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 用户聊天信息设置
 * @author jat
 *
 */
@ContentView(R.layout.personal_message_setting)
public class PersonalMessageCenterSetting extends Activity {
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
	//消息置顶
	@ViewInject(R.id.iv_setting_message_on_top)
	private ImageView iv_setting_message_on_top;
	@ViewInject(R.id.ll_head)
	private LinearLayout ll_head;
	//头像
	private String logoUrl = "";
	//用户名
	private String userName = "";
	//环信名
	private String IMName = "";
	//数据库
	DbUtils dbUtils;
	private String uuid;
	private IMMessageSetting message;
	//清楚消息标志
	private boolean flag = false;
	//消息置顶
	private boolean message_top = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Intent intent = getIntent();
		userName = intent.getExtras().getString("userName");
		logoUrl = intent.getExtras().getString("logoUrl");
		IMName = intent.getExtras().getString("IMName");
		uuid = intent.getExtras().getString("uuid");
		//初始化数据库
		try {
			dbUtils = DbUtils.create(getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initView();
	}

	/**
	 * 初始化界面
	
	 */
	
	
	
	private void initView() {
		//是否置顶
		if (RsSharedUtil.getString(PersonalMessageCenterSetting.this,
				AppConfig.MESSAGE_TOP).equals(uuid)) {
			message_top=true;
			iv_setting_message_on_top.setImageResource(R.drawable.open);
		}
		else {
			message_top=false;
			iv_setting_message_on_top.setImageResource(R.drawable.guan);
		}
		
		ImageLoader.getInstance().displayImage(logoUrl, iv_setting_img);
		iv_setting_name.setText(userName);
//		message_top = RsSharedUtil.getBoolean(getApplicationContext(), AppConfig.MESSAGE_TOP, false);
//		if (message_top) {
//			iv_setting_message_on_top.setImageResource(R.drawable.open);
//		}else {
//			iv_setting_message_on_top.setImageResource(R.drawable.guan);
//		}
		//取出数据库记录，看是否为屏蔽状态
		try {
			message = dbUtils.findById(IMMessageSetting.class, IMName);
			if (message==null) {
				iv_setting_notify.setImageResource(R.drawable.guan);
			}else{
				iv_setting_notify.setImageResource(R.drawable.open);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK) 
		{
			Intent intent = new Intent(PersonalMessageCenterSetting.this,ChatActivity.class);
			intent.putExtra("flag", flag);
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@OnClick({ R.id.title_note, R.id.tv_delete_all, R.id.iv_setting_notify,
		R.id.tv_delete_all,R.id.iv_setting_message_on_top,R.id.ll_head ,R.id.rl_return})
	private void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			Intent intent = new Intent(PersonalMessageCenterSetting.this,ChatActivity.class);
			intent.putExtra("flag", flag);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.iv_setting_notify:
			//改变屏蔽信息
			setData();
			break;
		case R.id.tv_delete_all:
			//删除消息记录
			deleteData();
			break;
		case R.id.iv_setting_message_on_top:
			dealTop();
			break;
		case R.id.ll_head:
			Intent intent2 = new Intent(getApplicationContext(), OtherPeolpeInformationActivity.class);
			intent2.putExtra("uuid", uuid);
			startActivity(intent2);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}
	
	//消息置顶
	private void dealTop(){
		if (message_top) {
			iv_setting_message_on_top.setImageResource(R.drawable.guan);
			message_top=false;
			RsSharedUtil.putString(PersonalMessageCenterSetting.this,
					AppConfig.MESSAGE_TOP,"");
			
		}
		
		else {
			message_top=true;
			RsSharedUtil.putString(PersonalMessageCenterSetting.this,
					AppConfig.MESSAGE_TOP, uuid);
			iv_setting_message_on_top.setImageResource(R.drawable.open);

		}
		
	}

	//删除数据
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
		dialog.setMessage("删除所有消息记录吗?");
		dialog.setPositiveButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消按钮的点击事件
				EMChatManager.getInstance().clearConversation(IMName);
				flag = true;
				Toast.makeText(PersonalMessageCenterSetting.this, "删除成功！", 2000)
				.show();
				dialog.dismiss();
			}
		});
	}

	/**
	 * 改变屏蔽状态
	 */
	private void setData() {
		try {
			//获取数据库，查询记录
			IMMessageSetting message = dbUtils.findById(IMMessageSetting.class, IMName);
			//空为未屏蔽，有值则为已屏蔽
			if (message==null) {
				//为屏蔽则保持记录到数据库，设为屏蔽
				message = new IMMessageSetting();
				message.setiMName(IMName);
				message.setUuid(uuid);
				dbUtils.save(message);
				Log.d("jatjat", dbUtils.findAll(IMMessageSetting.class).toString()+"1111111111");
				iv_setting_notify.setImageResource(R.drawable.open);
			}else {
				//已屏蔽则删除数据库记录，设为未屏蔽
				dbUtils.deleteById(IMMessageSetting.class, IMName);
				iv_setting_notify.setImageResource(R.drawable.guan);
				Log.d("jatjat", dbUtils.findAll(IMMessageSetting.class).toString());
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("jatjat", "error "+e.toString());
		}
	}

}
