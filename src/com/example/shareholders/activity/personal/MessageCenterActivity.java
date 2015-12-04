package com.example.shareholders.activity.personal;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.example.shareholders.R;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_MessageFriend;
import com.example.shareholders.fragment.Fragment_MessageRemind;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * @category 这个类是Fragment_Me里面的消息中心Activity
 * */
@ContentView(R.layout.activity_message_center)
public class MessageCenterActivity extends FragmentActivity {
	// 返回
	@ViewInject(R.id.message_center_return)
	private RelativeLayout backBtn;

	private FragmentManager fragmentManager;
	private Fragment_MessageRemind fragment_remind;
	private Fragment_MessageFriend fragment_friend;

	@ViewInject(R.id.activity_message_remind)
	private RadioButton remindBtn;
	@ViewInject(R.id.activity_message_friend)
	private RadioButton friendBtn;
	public int flag = 0;
	//加载框
	LoadingDialog loadingDialog;



	//是否是从好友关注的Notification跳转而来
	private String user_follow="";

	private boolean first = false;

	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadingDialog.dismissDialog();
				Toast.makeText(getApplicationContext(), "登录环信失败", 0).show();
				fragmentManager = getSupportFragmentManager();
				initFragment();
				if (user_follow.equals("")) {
					setTabSelection(0);
				}
				else {
					friendBtn.setChecked(true);
					setTabSelection(1);
				}

				break;
			case 1:
				//环信登录成功
				flag=1;
				//初始化
				fragmentManager = getSupportFragmentManager();
				initFragment();
				if (user_follow.equals("")) {
					setTabSelection(0);
				}
				else {
					friendBtn.setChecked(true);
					setTabSelection(1);
				}
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		loadingDialog = new LoadingDialog(this);
		if (savedInstanceState==null) {
			first = true;
		}else {
			first = false;
		}

		try {
			user_follow=getIntent().getExtras().getString("user_follow");
		} catch (Exception e) {
			user_follow="";
		}
		//防止环信不稳定掉线，登录环信
		loginIm();

	}

	private void initFragment() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Log.d(this.toString(), "initFragments()");
		fragment_remind = new Fragment_MessageRemind();
		transaction.add(R.id.message_center_content, fragment_remind);
		fragment_friend = new Fragment_MessageFriend();
		transaction.add(R.id.message_center_content, fragment_friend);
		transaction.commitAllowingStateLoss();
	}

	private void setTabSelection(int i) {
		hideFragments();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		switch (i) {
		case 0:
			transaction.show(fragment_remind);
			break;
		case 1:
			transaction.show(fragment_friend);
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (fragment_remind != null) {
			transaction.hide(fragment_remind);
		}
		if (fragment_friend != null) {
			transaction.hide(fragment_friend);
		}

		transaction.commitAllowingStateLoss();

	}

	@OnClick({ R.id.activity_message_remind, R.id.activity_message_friend,
		R.id.message_center_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.activity_message_remind:
			setTabSelection(0);
			break;
		case R.id.activity_message_friend:
			setTabSelection(1);
			break;
		case R.id.message_center_return:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 登录环信
	 */
	private void loginIm() {
		loadingDialog.showLoadingDialog();
		String imUserName = null;
		String imPassword = null;
		try {
			imUserName = RsSharedUtil.getString(getApplicationContext(),
					AppConfig.IMUSER_NAME);
			imPassword = RsSharedUtil.getString(getApplicationContext(),
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
					// 设置环信自动登录
					EMChat.getInstance().setAutoLogin(true);
					loadingDialog.dismissDialog();
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
