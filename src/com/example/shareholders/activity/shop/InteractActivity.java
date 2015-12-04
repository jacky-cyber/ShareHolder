package com.example.shareholders.activity.shop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Direct;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.DensityUtil;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.TestPicActivity;
import com.example.shareholders.adapter.MsgAdapter;
import com.example.shareholders.common.AudioRecorderButton;
import com.example.shareholders.common.AudioRecorderButton.AudioFinishRecorderListener;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.Bimp;
import com.example.shareholders.db.entity.ChatMsgEntity;
import com.example.shareholders.db.entity.ChatMsgEntity.MsgType;
import com.example.shareholders.db.entity.FileUtils;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.recorder.MediaManager;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_interact)
public class InteractActivity extends Activity {

	@ViewInject(R.id.rl_parent)
	private RelativeLayout rl_parent;
	@ViewInject(R.id.lv_chat)
	private ListView lv_chat;
	@ViewInject(R.id.et_send_msg)
	private EditText et_send_msg;
	@ViewInject(R.id.tv_send)
	private TextView tv_send;
	@ViewInject(R.id.iv_more)
	private ImageView iv_more;
	@ViewInject(R.id.iv_record)
	private AudioRecorderButton iv_record;

	private MsgAdapter msgAdapter;
	private ArrayList<ChatMsgEntity> chatMsgEntities = new ArrayList<ChatMsgEntity>();

	// 我的uuid
	private String myUuid;
	
	private boolean isLogin = false;
	
	// 是否初次加载标志
	private boolean flag = true;

	// 录音路径
	private String recordFilePath;
	// 我的名字
	private String myName = "";
	// 我的头像
	private String myLogo = "";
	private MediaPlayer player = null;
	public static boolean isPlaying = false;

	private static final int TAKE_PICTURE = 0x000000;

	EMConversation conversation;
	
	private PersonalInformation information;

	// 聊天室id
	private String groupId;

	// 图片路径
	private String path = "";

	// 数据库
	private DbUtils dbUtils;

	// 发送失败
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				final int position = msg.what;
				final EMMessage message = (EMMessage) msg.obj;
				msgAdapter.sendFail(position);
				break;

			case 1:
				// 重新登录环信
				loginIm(1);
				break;

			case 2:
				// 异步更新listview数据
				msgAdapter.notifyDataSetChanged();
				lv_chat.setSelection(msgAdapter.getCount() - 1);
				break;
			case 3:

				break;

			case 4:
				break;
			case 5:
				// 加入聊天室
				onChatroomViewCreation();
				break;
				
			case 6:
				if (Bimp.drr.size() > 0) {
					for (int i = 0; i < Bimp.drr.size(); i++) {
						String Str = Bimp.drr.get(i).substring(
								Bimp.drr.get(i).lastIndexOf("/") + 1,
								Bimp.drr.get(i).lastIndexOf("."));
						path = FileUtils.SDPATH + Str + ".JPEG";
						sendImage(msgAdapter.getCount());
					}
					Bimp.drr.clear();
				}
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
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		ViewUtils.inject(this);
		// 初始化数据库
		dbUtils = DbUtils.create(getApplicationContext());
		myUuid = RsSharedUtil.getString(InteractActivity.this, AppConfig.UUID);
		groupId = getIntent().getExtras().getString("groupId");
		try {
			information = dbUtils.findById(PersonalInformation.class, myUuid);
			if (information!=null) {
				myName = information.getUserName();
				myLogo = information.getUserLogo();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 加入聊天室
		onChatroomViewCreation();
		// 防止漏接信息
		EMChat.getInstance().setAppInited();
		EMChat.getInstance().init(getApplicationContext());
		Log.d("createchat", "createchat");
		//初始化
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MediaManager.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MediaManager.resume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {// 退出聊天室
			MediaManager.release();
			EMChatManager.getInstance().leaveChatRoom(groupId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onRestart() {
		// 从相册和拍照里面返回的时候调用这个发送图片
//		loginIm(0);
		mHandler.sendEmptyMessage(6);
		super.onRestart();
	}

	private void init() {
		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new PopupWindows(InteractActivity.this, rl_parent);

			}
		});

		et_send_msg.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (et_send_msg.getText().toString().length() > 0) {
					iv_more.setVisibility(View.GONE);
					tv_send.setVisibility(View.VISIBLE);
				} else {
					iv_more.setVisibility(View.VISIBLE);
					tv_send.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

		iv_record
				.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {

					@Override
					public void onFinish(float seconds, String filePath) {
						recordFilePath = filePath;
						popupSaveRecord(InteractActivity.this);
					}
				});

		EMChatManager.getInstance().registerEventListener(
				new EMEventListener() {

					@Override
					public void onEvent(EMNotifierEvent event) {
						// TODO Auto-generated method stub
						EMMessage message = (EMMessage) event.getData();
						dealMessage(message);
					}
				});

	}

	@OnClick({ R.id.title_note, R.id.tv_send })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.tv_send:
			sendMsg(msgAdapter.getCount());
			break;
		default:
			break;
		}
	}

	// 发送文字信息
	private void sendMsg(final int position) {
		if (et_send_msg.getText().toString().length() > 0 && isLogin) {

			// 创建一条文本消息
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			message.setChatType(ChatType.ChatRoom);
			// 设置消息body
			TextMessageBody txtBody = new TextMessageBody(et_send_msg.getText()
					.toString());
			message.addBody(txtBody);
			// 设置扩展字段
			message.setAttribute("uuid", myUuid);
			message.setAttribute("userLogo", myLogo);
			message.setAttribute("username", myName);
			// 设置接收人
			message.setReceipt(groupId);
			// 把消息加入到此会话对象中
			conversation.addMessage(message);

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setIsMine(true);
			entity.setName(myName);
			entity.setTime(System.currentTimeMillis());
			entity.setContent(et_send_msg.getText().toString());
			entity.setMessage(message);
			chatMsgEntities.add(entity);
			msgAdapter.notifyDataSetChanged();
			lv_chat.setSelection(msgAdapter.getCount() - 1);

			// 发送消息
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onError(int arg0, String arg1) {
					Message msg = new Message();
					msg.what = 0;
					msg.arg1 = position;
					msg.obj = message;
					mHandler.sendMessage(msg);
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

			et_send_msg.setText("");
		}
	}

	// 发送录音
	private void sendRecord(final int position) {
		//先判断有无登录环信成功，否则不可发送
		if (isLogin) {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			message.setChatType(ChatType.ChatRoom);
			VoiceMessageBody body = new VoiceMessageBody(new File(recordFilePath),
					(int) new File(recordFilePath).length());
			message.addBody(body);
			// 设置扩展字段
			message.setAttribute("uuid", myUuid);
			message.setAttribute("userLogo", myLogo);
			message.setAttribute("username", myName);
			message.setReceipt(groupId);
			conversation.addMessage(message);

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setIsMine(true);
			entity.setName(myName);
			entity.setTime(System.currentTimeMillis());
			entity.setType(MsgType.RECORD);
			entity.setResUrl(recordFilePath);
			entity.setMessage(message);
			chatMsgEntities.add(entity);
			msgAdapter.notifyDataSetChanged();
			lv_chat.setSelection(msgAdapter.getCount() - 1);

			// 发送消息
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onError(int arg0, String arg1) {
					Message msg = new Message();
					msg.what = position;
					msg.obj = message;
					mHandler.sendMessage(msg);
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
		}
	}

	// 发送图片
	private void sendImage(final int position) {
		//先判断有无登录环信成功，否则不可发送
		if (isLogin) {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.IMAGE);
			// 如果是群聊，设置chattype,默认是单聊
			message.setChatType(ChatType.ChatRoom);
			Log.d("path", path);
			ImageMessageBody body = new ImageMessageBody(new File(path));
			Log.d("path", body.toString());
			// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
			body.setSendOriginalImage(true);
			message.addBody(body);
			// 设置扩展字段
			message.setAttribute("uuid", myUuid);
			message.setAttribute("userLogo", myLogo);
			message.setAttribute("username", myName);
			message.setReceipt(groupId);
			conversation.addMessage(message);

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setIsMine(true);
			entity.setName(myName);
			entity.setTime(System.currentTimeMillis());
			entity.setType(MsgType.IMAGE);
			entity.setLocalUrl(path);
			entity.setMessage(message);
			chatMsgEntities.add(entity);
			msgAdapter.notifyDataSetChanged();
			lv_chat.setSelection(msgAdapter.getCount() - 1);

			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onError(int arg0, String arg1) {
					Message msg = new Message();
					msg.what = position;
					msg.obj = message;
					mHandler.sendMessage(msg);
					Log.d("chatactivity", arg1+"666666"+"="+arg0);
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

		}
	}

	private void resend(final int position, final EMMessage message) {
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
				Message msg = new Message();
				msg.what = position;
				msg.obj = message;
				mHandler.sendMessage(msg);
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
	}

	/**
	 * 弹出保存录音对话框
	 * 
	 * @param context
	 * @return
	 */
	public void popupSaveRecord(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.item_popup_save_record, null);
		TextView tv_ok = (TextView) contentView.findViewById(R.id.tv_ok);
		final EditText et_record = (EditText) contentView
				.findViewById(R.id.et_record);
		String recordFileName = recordFilePath.substring(recordFilePath
				.lastIndexOf("/") + 1);
		et_record.setText(recordFileName);

		final AlertDialog dialog = new AlertDialog.Builder(
				InteractActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);
		dialog.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = DensityUtil.dip2px(context, 286);
		dialog.getWindow().setAttributes(params);

		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String oldFilePath = recordFilePath;
				recordFilePath = recordFilePath.substring(0,
						(recordFilePath.lastIndexOf("/") + 1))
						+ et_record.getText().toString().trim();
				File file = new File(oldFilePath);
				file.renameTo(new File(recordFilePath));
				popupSendRecord(InteractActivity.this);
				dialog.dismiss();

			}
		});
	}

	/**
	 * 弹出发送录音对话框
	 * 
	 * @param context
	 * @return
	 */
	public void popupSendRecord(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_general_layout, rl_parent, false);
		ImageView iv_message_icon = (ImageView) contentView
				.findViewById(R.id.iv_message_icon);
		TextView tv_message = (TextView) contentView
				.findViewById(R.id.tv_message);
		TextView tv_confirm = (TextView) contentView
				.findViewById(R.id.tv_confirm);
		TextView tv_cancel = (TextView) contentView
				.findViewById(R.id.tv_cancel);
		iv_message_icon.setVisibility(View.GONE);
		tv_message.setText("是否发送录音？");

		final AlertDialog dialog = new AlertDialog.Builder(
				InteractActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);
		dialog.setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = DensityUtil.dip2px(context, 286);
		params.height = DensityUtil.dip2px(context, 190);
		dialog.getWindow().setAttributes(params);

		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendRecord(msgAdapter.getCount());
				dialog.dismiss();
			}
		});

		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				File file = new File(recordFilePath);
				if (file.exists()) {
					file.delete();
				}
				dialog.dismiss();
			}
		});

	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(InteractActivity.this,
							TestPicActivity.class);

					// bundle.putString("surveyUuid", surveyUuid);
					// intent.putExtras(bundle);
					// finish();
					startActivity(intent);

					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir,
				new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
						+ ".jpg");
		path = file.getPath();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)),
				TAKE_PICTURE);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			sendImage(msgAdapter.getCount());
			break;
		}
	}

	// 加入聊天室
	public void onChatroomViewCreation() {

		final ProgressDialog pd = ProgressDialog
				.show(this, "", "Joining......");
		try {
			EMChatManager.getInstance().joinChatRoom(groupId,
					new EMValueCallBack<EMChatRoom>() {

						@Override
						public void onSuccess(EMChatRoom value) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									pd.dismiss();
									isLogin  = true;
									EMChatRoom room = EMChatManager
											.getInstance().getChatRoom(groupId);
									Log.d("ChatRoom", "join room success : "
											+ room.getName());
//									Toast.makeText(getApplicationContext(),
//											"加入聊天室成功", 1).show();
									// 获取聊天历史记录
									onConversationInit();

									// onListViewCreation();
								}
							});
						}

						@Override
						public void onError(final int error, String errorMsg) {
							Log.d("ChatRoom", "join room failure : " + error);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// Toast.makeText(getApplicationContext(),
									// "加入聊天室失败", 1).show();
									pd.dismiss();
								}
							});
							// finish();
							mHandler.sendEmptyMessage(1);
						}
					});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "失败", 1).show();
			pd.dismiss();
		}
	}

	// 处理每一天消息
	private void dealMessage(EMMessage message) {
		//判断有没有登录环信成功
		if (isLogin) {
			PersonalInformation information;
			String uuid = "";
			try {
				if (message.direct == Direct.SEND) {
					information = dbUtils.findById(PersonalInformation.class,
							myUuid);
					if (information == null) {
						Log.d("uuuuuuuuuuu", uuid + "2222222222");
						profile(myUuid, message);
					}
					addMessage(message, information);
				} else {
					uuid = message.getStringAttribute("uuid");
					Log.d("uuuuuuuuuuu", uuid + "111111111");
					information = dbUtils.findById(PersonalInformation.class, uuid);
					if (information == null) {
						profile(uuid, message);
					} else {
						addMessage(message, information);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("main", e.toString());
			}
		}
	}

	// 添加数据
	private void addMessage(EMMessage message, PersonalInformation information) {
		ChatMsgEntity entity = new ChatMsgEntity();
		try {
			if (message.getStringAttribute("uuid").equals("")
					|| message.getStringAttribute("uuid").equals(
							RsSharedUtil.getString(getApplicationContext(),
									AppConfig.UUID))) {
				entity.setIsMine(true);
			} else {
				entity.setIsMine(false);
			}
			//获取时间头像用户名
			entity.setTime(message.getMsgTime());
			entity.setName(message.getStringAttribute("username"));
			entity.setUserLogo(message.getStringAttribute("userLogo"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (message.getType() == EMMessage.Type.TXT) {
			Log.d("recieve new msg:", "text");
			entity.setType(MsgType.TEXT);
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			entity.setContent(txtBody.getMessage());
		} else if (message.getType() == EMMessage.Type.VOICE) {
			Log.d("recieve new msg:", "voice");
			entity.setType(MsgType.RECORD);
			VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
			entity.setResUrl(voiceBody.getLocalUrl());
		} else if (message.getType() == EMMessage.Type.IMAGE) {
			Log.d("recieve new msg:", "image");
			entity.setType(MsgType.IMAGE);
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			entity.setResUrl(imageBody.getThumbnailUrl());
		}
		entity.setMessage(message);
		chatMsgEntities.add(entity);
		if (!flag) {
			mHandler.sendEmptyMessage(2);
		}
	}

	/**
	 * 通过uuid获取个人资料
	 * 
	 * @param uuid
	 *            用户uuid
	 */
	private void profile(final String userUuid, final EMMessage message) {
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
							dbUtils.saveOrUpdate(information);
							addMessage(message, information);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("chatEntity", e.toString());
							Log.d("main", e.toString());
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

	protected void onConversationInit() {

		final int pagesize = 20;

		conversation = EMChatManager.getInstance().getConversationByType(
				groupId, EMConversationType.ChatRoom);

		// 把此会话的未读数置为0
		conversation.markAllMessagesAsRead();

		// 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
		// 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
		final List<EMMessage> msgs = conversation.getAllMessages();
		// int msgCount = msgs != null ? msgs.size() : 0;
		// if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize)
		// {
		// String msgId = null;
		// if (msgs != null && msgs.size() > 0) {
		// msgId = msgs.get(0).getMsgId();
		// }
		// conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
		// }

		if (msgs != null) {
			conversation.loadMoreGroupMsgFromDB(groupId, pagesize);
			for (int i = 0; i < msgs.size(); i++) {
				dealMessage(msgs.get(i));
			}
			flag = false;
			msgAdapter = new MsgAdapter(InteractActivity.this, chatMsgEntities);
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
		}

		// 监听聊天室变化回调
		EMChatManager.getInstance().addChatRoomChangeListener(
				new EMChatRoomChangeListener() {

					@Override
					public void onChatRoomDestroyed(String roomId,
							String roomName) {
						if (roomId.equals(groupId)) {
							finish();
						}
					}

					@Override
					public void onMemberJoined(String roomId, String participant) {
					}

					@Override
					public void onMemberExited(String roomId, String roomName,
							String participant) {

					}

					@Override
					public void onMemberKicked(String roomId, String roomName,
							String participant) {
						if (roomId.equals(groupId)) {
							String curUser = EMChatManager.getInstance()
									.getCurrentUser();
							if (curUser.equals(participant)) {
								EMChatManager.getInstance().leaveChatRoom(
										groupId);
								finish();
							}
						}
					}

				});
	}

	// 计算压缩比
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 压缩图片
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
			int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeFile(path, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 登录环信
	 */
	private void loginIm(final int type) {
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
							isLogin = true;
							// 设置环信自动登录
							EMChat.getInstance().setAutoLogin(true);
							if (type==0) {
								mHandler.sendEmptyMessage(6);
							}else {
								mHandler.sendEmptyMessage(5);
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

}
