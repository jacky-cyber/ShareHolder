package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.shareholders.R;
import com.example.shareholders.adapter.CenterFriendAdapter;
import com.example.shareholders.adapter.CenterFriendListAdapter;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.IMMessageSetting;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_MessageFriend extends Fragment {
	@ViewInject(R.id.message_center_friend_list)
	private ListView friendList;
	//无消息
	@ViewInject(R.id.rl_no_content)
	private RelativeLayout rl_no_content;

	//标注环信是否登录成功
	private int flag = 0;

	/** 会话列表 */
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	private CenterFriendAdapter adapter;

	/** 消息列表 */
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	/** 好友列表集合 */
	private List<String> usernames = new ArrayList<String>();

	/** 环信会话 */
	EMConversation conversation;

	// 数据库
	DbUtils dbUtils;



	// ChatEntity entity;

	PersonalInformation entity = new PersonalInformation();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				break;

			case 1:
				// 个人资料实例
				entity = (PersonalInformation) msg.obj;
				break;

			case 2:
				if (flag==1) {
					list.clear();
					conversationList = loadConversitionsWithRecentChat();
					catchData(conversationList);
					// 初始化环信好友列表
					// new CatchFriendsListTask().execute();
				}
				break;
				
			case 3:
				//消息置顶，查出与数据库保存的置顶用户，移除它，再把它添加到第一
				for (int i = 0; i < list.size(); i++) {
					Log.d("mylist", list.toString());
					if (list.get(i).get("uuid").equals(
							RsSharedUtil.getString(getActivity(), AppConfig.MESSAGE_TOP))) {
						HashMap<String, Object> map=new HashMap<String, Object>();
						map.put("uuid", list.get(i).get("uuid"));
						map.put("count", list.get(i).get("count"));
						map.put("content", list.get(i).get("content"));
						map.put("time", list.get(i).get("time"));
						map.put("logo", list.get(i).get("logo"));
						map.put("username", list.get(i).get("username"));
						map.put("type", 1);
						list.remove(list.get(i));
						list.add(0,map);
					}
				}
				if (list.size()==0||list==null) {
					rl_no_content.setVisibility(View.VISIBLE);
					friendList.setVisibility(View.GONE);
				}else {
					rl_no_content.setVisibility(View.GONE);
					friendList.setVisibility(View.VISIBLE);
					friendList.setAdapter(new CenterFriendAdapter(getActivity(),
							list));
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
		View v = inflater.inflate(R.layout.fragment_message_activity_friend,
				null);

		ViewUtils.inject(this, v);
		loginIm();

		try {
			dbUtils = DbUtils.create(getActivity());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("dgjhdhds_error", e.toString());
		}
		return v;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHandler.sendEmptyMessage(2);

	}

	/**
	 * 初始化历史记录列表
	 * 
	 * @param conversations
	 */
	private void catchData(List<EMConversation> conversations) {

		Log.d("datasize", conversations.size()+"");
		int i = 0;
		for (EMConversation conversation : conversations) {
			i++;
			Map<String, Object> map = new HashMap<String, Object>();
			//防止对话为空
			if (conversation==null) {
				break;
			}

			EMMessage message = conversation.getLastMessage();
			String uuid = "";
			Log.d("mylist", message.toString());

			// 最后一条回话时间
			map.put("time", message.getMsgTime());
			if (conversation.getUserName().equals("user_follow")) {
				try {
					Log.d("mylist", "mylist");
					map.put("uuid", message.getStringAttribute("senderId").toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("mylist", e.toString());
				}
				map.put("IMName", "user_follow");
				//区分好友关注还是聊天
				map.put("type", 0);
				map.put("logo", "drawable://");
				map.put("username", "好友关注");
			}else {
				//获取用户uuid
				try {
					map.put("type", 1);
					uuid = message.getStringAttribute("uuid");
					if (uuid.equals("")
							||uuid.equals(RsSharedUtil.getString(getActivity(),
									AppConfig.UUID))) {
						uuid = message.getStringAttribute("userUuid");
						map.put("uuid", uuid);
					} else {
						map.put("uuid", uuid);
					}
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					entity = dbUtils.findById(PersonalInformation.class, uuid);
					if (entity==null) {
						synchronized (entity) {
							getFriendDetails(uuid,i);
						}
						entity = dbUtils.findById(PersonalInformation.class, uuid);
					}
					//					Log.d("mylogo", entity+"");
					map.put("logo", entity.getUserLogo());
					map.put("username", entity.getUserName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//					Log.d("mylogo", e.toString());
					map.put("logo", "");
					map.put("username", "");
				}
			}
			// 未读数目
			map.put("count", conversation.getUnreadMsgCount());
			// 回话最后一句内容
			TextMessageBody textMessageBody = (TextMessageBody) message
					.getBody();
			map.put("content", textMessageBody.getMessage());

			list.add(map);
		}
		mHandler.sendEmptyMessage(3);
	}


	/**
	 * 获取所有会话
	 * 
	 * @return
	 */
	private List<EMConversation> loadConversitionsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getLastMessage()!=null) {
					if (!conversation.getUserName().equals("my_survey")
							&& !conversation.getUserName().equals("security_alert")
							&& !conversation.getUserName().equals("info_alert")
							&& !conversation.getUserName().equals("topic_alert")
							&& !conversation.getUserName().equals("app_system")
							&& !(conversation.getType() == EMConversationType.ChatRoom)) {
						try {
							if (dbUtils.findById(IMMessageSetting.class, conversation.getUserName())!=null) {
								conversation.resetUnreadMsgCount();
								EMChatManager.getInstance().clearConversation(conversation.getUserName());
//								EMChatManager.getInstance().deleteConversation(username);
							}else {
								sortList.add(new Pair<Long, EMConversation>(conversation
										.getLastMessage().getMsgTime(), conversation));
							}
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		try {
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			// TODO: handle exception
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1,
					final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	/**
	 * 获取好友个人资料
	 * 
	 * @param uuid
	 */
	private void getFriendDetails(final String uuid,final int i) {
		String url = AppConfig.VERSION_URL + "user/profile.json?access_token=";
		url = url
				+ RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN)
				+ "&userUuid=" + uuid;
		Log.d("jjjjjjjjj", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.d("jjjjjjjjj", response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					PersonalInformation myInformation = new PersonalInformation();
					myInformation.setUuid(uuid);
					myInformation.setUserLogo(jsonObject
							.getString("userLogo"));
					myInformation.setUserName(jsonObject
							.getString("userName"));
					myInformation.setCoin(jsonObject.getInt("coin"));
					myInformation.setIndustryCode(jsonObject
							.getString("industryCode"));
					myInformation.setIndustryName(jsonObject
							.getString("industryName"));
					myInformation.setIntroduction(jsonObject
							.getString("introduction"));
					myInformation.setLocationCode(jsonObject
							.getString("locationCode"));
					myInformation.setLocationName(jsonObject
							.getString("locationName"));
					Map<String, Object> map = list.get(i);
					map.put("logo", myInformation.getUserLogo());
					map.put("username", myInformation.getUserName());
					list.add(i, map);
					CenterFriendAdapter adapter = new CenterFriendAdapter(getActivity(),
							list);
					friendList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					try {
						dbUtils.save(myInformation);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("mylogo", e.toString()+"66666");
					}
					Message message = new Message();
					message.obj = myInformation;
					message.what = 1;
					Log.d("jjjjjjjjjjjjjjjjjjjjjj",
							myInformation.toString());
					mHandler.sendMessage(message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d("mylogo", error.toString());
			}
		});
		stringRequest.setTag("getPersonDetails");
		MyApplication.getRequestQueue().add(stringRequest);
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("getPersonDetails");
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
		// 环信登录
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
					mHandler.sendEmptyMessage(2);
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
