package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.personal.PersonalMessageCenterSetting;
import com.example.shareholders.adapter.MsgAdapter;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.ChatMsgEntity;
import com.example.shareholders.db.entity.ChatMsgEntity.MsgType;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_chat)
public class ChatActivity extends Activity {

	@ViewInject(R.id.lv_chat)
	private ListView lv_chat;
	@ViewInject(R.id.et_send_msg)
	private EditText et_send_msg;
	// 设置
	@ViewInject(R.id.title_config)
	private ImageView title_config;
	// 标题栏
	@ViewInject(R.id.tv_titile)
	private TextView tv_titile;
	//底部发送栏
	@ViewInject(R.id.rl_bottom)
	private RelativeLayout rl_bottom;

	// 环信用户名
	private String friendName = "";

	//分享的内容
	private String share_content="";

	// 用户头像
	private String friendLogo = "";

	private boolean isLogin=false;

	private List<String> uuidList = new ArrayList<String>();

	/** 我的uuid */
	private String myUuid;

	/** 对方uuid */
	private String uuid;

	private LoadingDialog loadingDialog;

	// 聊天信息实体
	// private ChatEntity chatEntity;

	private PersonalInformation information;

	private MsgAdapter msgAdapter;
	/** 消息实体 */
	private ArrayList<ChatMsgEntity> chatMsgEntities = new ArrayList<ChatMsgEntity>();
	/** 消息集合 */
	List<EMMessage> messages = new ArrayList<EMMessage>();
	/** 环信会话 */
	EMConversation conversation;

	// 用户名
	private String username;

	// 我的头像
	private String myLogo;

	// 我的用户名呢称
	private String myName;

	// 数据库
	DbUtils dbUtils;
	//有没有已分享
	private boolean share = false;

	// @ViewInject(R.id.iv_more)
	// private ImageView iv_more;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				msgAdapter.notifyDataSetChanged();
				lv_chat.setSelection(msgAdapter.getCount() - 1);
				break;

			case 1:
				friendName = (String) msg.obj;

				//如果会话为空，则重新登录环信
				if (conversation == null) 
				{
					loginIm();
					conversation = EMChatManager.getInstance().getConversation(
							friendName);
				}
				conversation = EMChatManager.getInstance().getConversation(friendName);

				messages = conversation.getAllMessages();
				// 获取所有消息
				getAllMessage(messages);
				// 防止漏接消息
				EMChat.getInstance().setAppInited();
				// 清零所有未读消息
				conversation.resetUnreadMsgCount();
				//登录成功
				isLogin = true;
				int i = 0;
				//判断是否分享，分享则跳转分享
				if (share_content!=null&&!share) {
					share = true;
					Log.d("iiiii", i+"");
					i++;
					mHandler.sendEmptyMessage(7);
				}
				break;

			case 2:
				information = (PersonalInformation) msg.obj;
				Log.d("chatEntity", information.toString());
				username = information.getUserName();
				friendLogo = information.getUserLogo();
				tv_titile.setText(username);
				break;

			case 3:
				myLogo = ((PersonalInformation) msg.obj).getUserLogo();
				break;

			case 4:
				try {
					Thread.sleep(3000);
					finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			case 5:
				break;
			case 6:
				// 重新登录环信
				loginIm();
				break;

			case 7:
				//分享
				sendMsg(0);
				break;
			case 8:
				getFriendName(getIntent().getExtras().get("uuid").toString());
				break;
			case 9:
				et_send_msg.setText("");
				msgAdapter.notifyDataSetChanged();
				lv_chat.setSelection(msgAdapter.getCount() - 1);
				break;
			default:
				break;
			}

		}


	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		ViewUtils.inject(this);
		try {
			share_content=getIntent().getExtras().getString("share_content");
			Log.d("hbx_aaa",share_content);
		} catch (Exception e) {
			Log.d("share_content", e.toString());
		}
		loginIm();

		uuid = getIntent().getExtras().getString("uuid");
		myUuid = RsSharedUtil
				.getString(getApplicationContext(), AppConfig.UUID);
		dbUtils = DbUtils.create(getApplicationContext());
		myName = RsSharedUtil.getString(getApplicationContext(),
				AppConfig.NICKNAME);
		try {
			information = dbUtils.findById(PersonalInformation.class, uuid);
			if (information == null) {
				profile(uuid, 0);
			} 
			else 
			{
				friendLogo = information.getUserLogo();
				username = information.getUserName();
				tv_titile.setText(username);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			// myLogo = dbUtils.findById(PersonalInformation.class,
			// myUuid).getUserLogo();
			PersonalInformation information = dbUtils.findById(
					PersonalInformation.class, myUuid);
			if (information == null) {
				profile(myUuid, 1);
			} else {
				myLogo = information.getUserLogo();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (getIntent().getExtras().get("type").equals("0")) {
			tv_titile.setText("好友关注");
			title_config.setVisibility(View.GONE);
			conversation = EMChatManager.getInstance().getConversation(getIntent().getExtras().get("IMName").toString());
			messages = conversation.getAllMessages();
			// 获取所有消息
			getAllMessage(messages);
			// 防止漏接消息
			EMChat.getInstance().setAppInited();
			// 清零所有未读消息
			conversation.resetUnreadMsgCount();
			rl_bottom.setVisibility(View.GONE);
		}else {
			getFriendName(uuid);
		}
		setListener();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	/**
	 * 通过uuid获取个人资料
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param type
	 *            判断是获取对方资料还是获取本人资料
	 */
	private void profile(final String userUuid, final int type) {
		String url = AppConfig.VERSION_URL + "user/profile.json?access_token=";
		url = url
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + userUuid;
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				PersonalInformation information = new PersonalInformation();
				try {
					JSONObject jsonObject = new JSONObject(response);
					information.setUserLogo(jsonObject
							.getString("userLogo"));
					Log.d("jjjjjjjjjjjjjjjjj", "logo=      "
							+ jsonObject.getString("userLogo"));
					information.setUserName(jsonObject
							.getString("userName"));
					information.setCoin(jsonObject.getInt("coin"));
					information.setIndustryCode(jsonObject
							.getString("industryCode"));
					information.setIndustryName(jsonObject
							.getString("industryName"));
					information.setIntroduction(jsonObject
							.getString("introduction"));
					information.setLocationCode(jsonObject
							.getString("locationCode"));
					information.setLocationName(jsonObject
							.getString("locationName"));
					information.setUuid(userUuid);
					Log.d("chatEntity", information.toString());
					Message message = new Message();
					dbUtils.saveOrUpdate(information);
					message.obj = information;
					if (type == 0) {
						message.what = 2;
					} else {
						message.what = 3;
					}
					mHandler.sendMessage(message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("chatEntity", e.toString());
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest.setTag("profile");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 设置消息监听
	private void setListener() {
		EMChatManager.getInstance().registerEventListener(
				new EMEventListener() {

					@Override
					public void onEvent(EMNotifierEvent event) {
						// TODO Auto-generated method stub
						EMMessage message = (EMMessage) event.getData();
						Log.d("bbbbbbbbbb", message.toString()
								+ "aaaaaabbbbb");
						String userName = null;
						userName = message.getFrom();

						// 接收消息
						ChatMsgEntity entity = new ChatMsgEntity();
						try {
							if (userName.equals(friendName)) {
								entity.setIsMine(false);
								entity.setName(message.getStringAttribute("username"));
								entity.setUserLogo(message.getStringAttribute("userLogo"));
							} else {
								entity.setIsMine(true);
								entity.setName(message.getStringAttribute("username"));
								entity.setUserLogo(message.getStringAttribute("userLogo"));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						entity.setType(MsgType.TEXT);
						entity.setTime(message.getMsgTime());
						entity.setContent(((TextMessageBody) message.getBody())
								.getMessage());
						android.util.Log.d("bbbbbbbbbb", userName.equals(friendName)+"");
						if (userName.equals(friendName)) {
							chatMsgEntities.add(entity);
							// 未读消息清零
							conversation.resetUnreadMsgCount();
							// 异步更新列表
							mHandler.sendEmptyMessage(0);
						}

					}
				});
	}

	// 通过userUuid获取环信用户名
	private void getFriendName(String friendUuid) {
		String url = AppConfig.URL_ACCOUNT + "im/user/other.json?access_token=";
		url = url+ RsSharedUtil.getString(getApplicationContext(),
				AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + friendUuid;
		Log.d("jjjjjjjjj", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				if (response.equals("")) {
					Toast.makeText(getApplicationContext(),
							"此用户还没开通环信", 1).show();
					mHandler.sendEmptyMessage(4);
				}
				try {
					JSONObject jsonObject = new JSONObject(response);
					String name = jsonObject.getString("imName");
					Message message = new Message();
					message.obj = name;
					message.what = 1;
					mHandler.sendMessage(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest.setTag("getIMName");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("getIMName");
		MyApplication.getRequestQueue().cancelAll("profile");
	}

	// 获取消息,处理所有消息
	private void getAllMessage(List<EMMessage> messages) {
		if (getIntent().getExtras().get("type").equals("0")) {
			ChatMsgEntity entity = new ChatMsgEntity();
			for (int i = 0; i < messages.size(); i++) {
				entity.setIsMine(false);
				try {
					String uuid = messages.get(i).getStringAttribute("senderId");
					uuidList.add(uuid);
					//获取详细信息
					PersonalInformation information = dbUtils.findById(PersonalInformation.class, uuid);
					if (information==null) {
						profile(uuid, 0);
						information = dbUtils.findById(PersonalInformation.class, uuid);
					}
					//获取个人消息
					entity.setName(information.getUserName());
					entity.setUserLogo(information.getUserLogo());
					entity.setType(MsgType.TEXT);
					entity.setContent(((TextMessageBody) messages.get(i).getBody())
							.getMessage());
					entity.setTime(messages.get(i).getMsgTime());
					chatMsgEntities.add(entity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			for (int i = 0; i < messages.size(); i++) {
				ChatMsgEntity entity = new ChatMsgEntity();
				if (messages.get(i).getFrom().equals(friendName)) {
					entity.setIsMine(false);
					entity.setName(username);
					entity.setUserLogo(friendLogo);
				} else {
					entity.setIsMine(true);
					entity.setName(myName);
					entity.setUserLogo(myLogo);
				}
				entity.setType(MsgType.TEXT);
				entity.setContent(((TextMessageBody) messages.get(i).getBody())
						.getMessage());
				entity.setTime(messages.get(i).getMsgTime());
				chatMsgEntities.add(entity);
			}
			if (messages.size() == 0) {

			}
		}

		msgAdapter = new MsgAdapter(ChatActivity.this, chatMsgEntities);
		lv_chat.setAdapter(msgAdapter);
		lv_chat.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				imm.hideSoftInputFromWindow(getWindow().getDecorView()
						.getWindowToken(), 0);
			}
		});
		loadingDialog.dismissDialog();
		//如果是好友关注
		if (getIntent().getExtras().get("type").equals("0")) {
			lv_chat.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getApplicationContext(), OtherPeolpeInformationActivity.class);
					intent.putExtra("uuid", uuidList.get(position));
					startActivity(intent);
				}
			});
		}
	}

	@OnClick({ R.id.title_note, R.id.title_config, R.id.tv_send,R.id.rl_return})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.title_config:
			// 启动设置
			Intent intent = new Intent(getApplicationContext(), PersonalMessageCenterSetting.class);
			if (information==null) {
				try {
					information = dbUtils.findById(PersonalInformation.class, uuid);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (information!=null) {
				intent.putExtra("uuid", uuid);
				intent.putExtra("IMName", friendName);
				intent.putExtra("userName", information.getUserName());
				intent.putExtra("logoUrl", information.getUserLogo());
				startActivityForResult(intent, 1);
			}
			break;
		case R.id.tv_send:
			if (isLogin) {
				if (et_send_msg.getText().toString()==null||et_send_msg.getText().toString().equals("")) {

				}else {
					sendMsg(1);
				}
			}
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	// 发送文字消息
	private void sendMsg(final int type) {
		if (isLogin&&!isFinishing()) {
			// 创建一条文本消息
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			// message.setChatType(ChatType.GroupChat);
			// 设置消息body
			TextMessageBody txtBody;

			if (type==0) {
				txtBody = new TextMessageBody(share_content);
			}
			else {
				txtBody = new TextMessageBody(et_send_msg.getText()
						.toString());
			}

			message.addBody(txtBody);
			message.setAttribute("uuid", myUuid);
			message.setAttribute("userUuid", uuid);
			message.setAttribute("userLogo", myLogo);
			message.setAttribute("username", myName);
			// 设置接收人
			message.setReceipt(friendName);
			android.util.Log.d("share_content", message.toString());
			if (conversation!=null) {
				// 把消息加入到此会话对象中
				conversation.addMessage(message);
			}
			//				android.util.Log
			// 发送消息
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onError(int arg0, String arg1) {
//					mHandler.sendEmptyMessage(6);
				}

				@Override
				public void onProgress(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub

				}
			});
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setIsMine(true);
			entity.setName(myName);
			entity.setTime(System.currentTimeMillis());

			//判断是否分享
			if (type==0) {
				entity.setContent(share_content);
			}else {
				entity.setContent(et_send_msg.getText().toString());
			}

			et_send_msg.setText("");
			entity.setType(MsgType.TEXT);
			entity.setUserLogo(myLogo);
			chatMsgEntities.add(entity);
			share_content=null;
			android.util.Log.d("sendmessage", entity.toString());
			//通知UI更新
			mHandler.sendEmptyMessage(9);
		}
	}

	/**
	 * 登录环信
	 */
	private void loginIm() {
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
					//如果是分享而且好友环信明为空，则去获取完环信名再分享
					if (share_content!=null){
						mHandler.sendEmptyMessage(8);
					}
				}

				@Override
				public void onProgress(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.d("main", "登陆聊天服务器失败！");
					isLogin = false;
				}
			});
		}else {
			isLogin = false;
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getBooleanExtra("flag", false)) {
			chatMsgEntities.clear();
			msgAdapter.notifyDataSetChanged();
		}
	}

}
