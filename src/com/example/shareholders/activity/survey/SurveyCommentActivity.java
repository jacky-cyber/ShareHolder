package com.example.shareholders.activity.survey;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.personal.PersonalCommentActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.NoScrollGridView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.receiver.UpdateCommentReceiver;
import com.example.shareholders.receiver.UpdateCommentReceiver.UpLoadDownListener;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_survey_comment)
public class SurveyCommentActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, UpLoadDownListener {
	

	private BitmapUtils bitmapUtils;
	
	private BitmapUtils bitmapUtils1;
	// 全部评论
	@ViewInject(R.id.tv_all)
	private TextView tv_all;
	// 热门评论
	@ViewInject(R.id.tv_popular)
	private TextView tv_popular;
	// 股友评论
	@ViewInject(R.id.tv_friend)
	private TextView tv_friend;
	// 发起者评论
	@ViewInject(R.id.tv_initiator)
	private TextView tv_initiator;

	// 弹出菜单
	@ViewInject(R.id.rl_menu)
	private RelativeLayout rl_menu;

	// 弹出菜单
	@ViewInject(R.id.iv_menu)
	private ImageView iv_menu;

	@ViewInject(R.id.title_text)
	private TextView title_text;

	// 提示没有评论
	// @ViewInject(R.id.tv_no_comment)
	// private TextView tv_no_comment;
	// 评论列表
	@ViewInject(R.id.mv_comment_list)
	private ListView mv_comment_list;
	// 暂无评论的提示
	@ViewInject(R.id.ll_wupinglun)
	private LinearLayout ll_wupinglun;
	// 初始化
	RequestQueue volleyRequestQueue;
	/** 全部评论 */
	private List<HashMap<String, Object>> allTopics;
	/** 热门评论 */
	private List<HashMap<String, Object>> hotTopics;
	/** 股友评论 */
	private List<HashMap<String, Object>> friendTopics;
	/** 发起人评论 */
	private List<HashMap<String, Object>> initiatorTopics;
	/** 全局topics */
	private List<HashMap<String, Object>> topics;

	private CommentAdapter commentAdapter;
	// 当前选中的tab
	private int currentTab;
	// 上下拉刷新
	@ViewInject(R.id.refresh4)
	private PullToRefreshView refresh;
	// pageSize,固定为5条话题
	private static int PAGE_SIZE = 5;
	// pageIndex,从0递增
	private int all_index = 0;
	private int hot_index = 0;
	private int friend_index = 0;
	private int initiator_index = 0;
	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;
	// 是否需要刷新
	UpdateCommentReceiver updateCommentReceiver;

	@ViewInject(R.id.rl)
	private RelativeLayout rl_parent;

	private ShareUtils popupWindow;

	@ViewInject(R.id.background)
	private RelativeLayout background;

//	private AlertDialog internertDialog = null;
	private LoadingDialog loadingDialog ;
	private boolean all_finished = false;// 获取所有评论的后台数据是否完成
	private boolean hottest_finished = false; // 获取热门的后台数据是否完成
	private boolean friend_finished = false; // 获取股友的后台数据是否完成
	private boolean creator_finished = false; // 获取发起人评论的后台数据是否完成

//	private Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 1:
//				if (internertDialog != null && internertDialog.isShowing()) {
//					// 当所有评论和热门评论以及股友评论，发起人评论的数据都加载完，加载对话框消失
//					if (hottest_finished && all_finished && friend_finished
//							&& creator_finished) {
//
//						internertDialog.dismiss();
//						hottest_finished = false;
//						all_finished = false;
//						friend_finished = false;
//						creator_finished = false;
//						Message message = new Message();
//						commentAdapter = new CommentAdapter(
//								SurveyCommentActivity.this, allTopics);
//						mv_comment_list.setAdapter(commentAdapter);
//						if (allTopics.size() == 0) {
//							ll_wupinglun.setVisibility(View.VISIBLE);
//						} else {
//							ll_wupinglun.setVisibility(View.GONE);
//						}
//					}
//				}
//				break;
//
//			case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力
//
//				if (internertDialog != null && internertDialog.isShowing()) {
//					internertDialog.dismiss();
//					// showInternetDialog();
//				}
//
//				break;
//
//			case 3: // 提示网络异常的对话框消失
//				if (internertDialog != null && internertDialog.isShowing()) {
//					internertDialog.dismiss();
//				}
//				break;
//
//			default:
//				break;
//			}
//		};
//	};
	// 第一次进来
	private boolean isFirst = true;

	// 记录点击的是哪一条评论
	private int commentPosition = 0;
	private boolean scanSuccess = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewUtils.inject(this);
		
		bitmapUtils=new BitmapUtils(SurveyCommentActivity.this);
	
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		
		
		bitmapUtils1= new BitmapUtils(SurveyCommentActivity.this);
		bitmapUtils1 .configDefaultLoadingImage(R.drawable.empty_photo);
		bitmapUtils1.configDefaultLoadFailedImage(R.drawable.empty_photo);

		registerPublishFailReceiver();
		registerPublishSuccessReceiver();
		registerTranspondSuccessReceiver();
		registerTranspondFailReceiver();
		loadingDialog = new LoadingDialog(SurveyCommentActivity.this);
		//正在加载
		loadingDialog.showLoadingDialog();
//		showLoadingDialog();
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		init();
		/*iLoader = ImageLoader.getInstance();
		iLoader.resume();*/
		bitmapUtils.resume();
		bitmapUtils1.resume();
		mv_comment_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				bitmapUtils.resume();
				bitmapUtils1.resume();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				bitmapUtils.pause();
				bitmapUtils1.pause();
			}
		});
		
		
		new AsyncGetTopics().execute();
		// ClearTab();
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
//	private void showLoadingDialog() {
//		internertDialog = new AlertDialog.Builder(this).create();
//		internertDialog.show();
//		internertDialog.setCancelable(false);
//
//		Window window = internertDialog.getWindow();
//		window.setContentView(R.layout.dialog_no_internet);
//
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.dimAmount = 0.0f;
//		window.setAttributes(lp);
//		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//		 new Thread(new Runnable() {
//		
//		 @Override
//		 public void run() {
//		 Message msg = new Message();
//		 msg.what = 2;
//		 mHandler.sendMessageDelayed(msg, 5000);
//		 }
//		 }).start();
//
//	}

	/**
	 * 提示網絡不給力
	 */
//	private void showInternetDialog(String msg) {
//		internertDialog = new AlertDialog.Builder(this).create();
//		internertDialog.show();
//		internertDialog.setCancelable(false);
//
//		Window window = internertDialog.getWindow();
//		window.setContentView(R.layout.dialog_dianzan);
//
//		ProgressBar progress_bar = (ProgressBar) window
//				.findViewById(R.id.progress_bar);
//		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
//		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);
//
//		progress_bar.setVisibility(View.GONE);
//		iv_tips.setVisibility(View.VISIBLE);
//		tv_message.setText(msg);
//
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.dimAmount = 0.0f;
//		window.setAttributes(lp);
//		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				Message msg = new Message();
//				msg.what = 3;
//				mHandler.sendMessageDelayed(msg, 2000);
//			}
//		}).start();
//
//	}


	@Override
	public void onStart() {
		// 接受广播,收到就刷新界面
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("updateComment");
		updateCommentReceiver = new UpdateCommentReceiver();
		registerReceiver(updateCommentReceiver, intentFilter);
		updateCommentReceiver.setUpLoadDownListener(this);
		super.onStart();
	};

	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(updateCommentReceiver);
		unregisterReceiver(publishFailReceiver);
		unregisterReceiver(transpondSuccessReceiver);
		unregisterReceiver(transpondFailReceiver);
		unregisterReceiver(publishSuccessReceiver);
		super.onDestroy();
	}

	private void init() {
		currentTab = 0;

		allTopics = new ArrayList<HashMap<String, Object>>();
		hotTopics = new ArrayList<HashMap<String, Object>>();
		friendTopics = new ArrayList<HashMap<String, Object>>();
		initiatorTopics = new ArrayList<HashMap<String, Object>>();
		commentAdapter = new CommentAdapter(SurveyCommentActivity.this,
				allTopics);
		mv_comment_list.setAdapter(commentAdapter);
		volleyRequestQueue = Volley.newRequestQueue(SurveyCommentActivity.this);

	}

	private void ClearTab() {
		tv_all.setBackgroundResource(R.color.title_text_choose_color);
		tv_popular.setBackgroundResource(R.color.title_text_choose_color);
		tv_friend.setBackgroundResource(R.color.title_text_choose_color);
		tv_initiator.setBackgroundResource(R.color.title_text_choose_color);
	}

	private void OnTab(TextView tv) {
		tv.setBackgroundResource(R.color.title_text_chosen_color);
		switch (tv.getId()) {
		case R.id.tv_all:
			if (currentTab != 0) {
				currentTab = 0;
				setHashMaps(currentTab);
			}
			break;
		case R.id.tv_popular:
			if (currentTab != 1) {
				currentTab = 1;
				setHashMaps(currentTab);
			}
			break;
		case R.id.tv_friend:
			if (currentTab != 2) {
				currentTab = 2;
				setHashMaps(currentTab);
			}
			break;
		case R.id.tv_initiator:
			if (currentTab != 3) {
				currentTab = 3;
				setHashMaps(currentTab);
			}
			break;
		default:
			break;
		}

	}

	@OnClick({ R.id.tv_all, R.id.tv_popular, R.id.tv_friend, R.id.tv_initiator,
			R.id.iv_write_letter, R.id.rl_return, R.id.rl_menu })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		case R.id.tv_all:
			ClearTab();
			OnTab(tv_all);
			break;
		case R.id.tv_popular:
			ClearTab();
			OnTab(tv_popular);
			break;
		case R.id.tv_friend:
			ClearTab();
			OnTab(tv_friend);
			break;
		case R.id.tv_initiator:
			ClearTab();
			OnTab(tv_initiator);
			break;
		case R.id.iv_write_letter:
			Intent intent = new Intent(SurveyCommentActivity.this,
					PublishTopicActivity.class);
			String surveyUuid = getIntent().getExtras().getString("uuid");
			Bundle bundle = new Bundle();
			bundle.putString("surveyUuid", surveyUuid);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		// 弹出菜单
		case R.id.rl_menu:
			showPopUp(iv_menu);
			break;
		default:
			break;
		}
	}

	/**
	 * 1:本地先发一个话题，同时用service与后台对接 2:转发成功就刷新allTopics
	 * 3:跳到singleComment也刷新allTopics
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("requestCode", requestCode + "");
		Log.d("resultCode", resultCode + "");
		// 如果是发布话题
		switch (resultCode) {
		case 1:// 新话题
				// HashMap<String, Object> newTopic = new HashMap<String,
				// Object>();
				// String content = data.getExtras().getString("content");
				// newTopic.put("content", content);
				// Log.d("文字的内容", content.toString());
				// JSONArray picturesaArray = new JSONArray();
				// String[] pictures =
				// data.getExtras().getStringArray("pic_list");
				// for (int i = 0; i < pictures.length; i++) {
				// Log.d("图片的路径", pictures[i].toString());
				// JSONObject jsonObject = new JSONObject();
				// try {
				// jsonObject.put("url", "file:///" + pictures[i]);
				// jsonObject.put("type", "PICTURE");
				// picturesaArray.put(jsonObject);
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// }
				// // 所有图片
				// newTopic.put("medias", picturesaArray);
				// newTopic.put("creatorName", "钟沃文");
				// newTopic.put("creatorLogoUrl", "creatorLogoUrl");
				// newTopic.put("likeNum", 0);
				// newTopic.put("commentNum", 0);
				// newTopic.put("readNum", 0);
				// newTopic.put("transpondNum", 0);
				// newTopic.put("creationTime", System.currentTimeMillis());
				// newTopic.put("original", true);
				// newTopic.put("refTopic", "null");
				//
				// newTopic.put("followed", "false");
				// newTopic.put("liked", "false");
				// allTopics.add(0, newTopic);
				// commentAdapter = new
				// CommentAdapter(SurveyCommentActivity.this,
				// allTopics);
				// mv_comment_list.setAdapter(commentAdapter);
				// commentAdapter.notifyDataSetChanged();
				// showLoadingDialog();
			// isFirst = true;
			// currentTab = 0;
			// new AsyncGetTopics().execute();
			// ClearTab();
			// tv_all.setBackgroundResource(R.color.title_text_chosen_color);
//			showLoadingDialog();
			
			//正在加载
			loadingDialog.showLoadingDialog();

			break;
		case 2:// 转发
			Log.d("liang_activity_result", "transpond_back");
//			showLoadingDialog();
			loadingDialog.showLoadingDialog();
			break;
		case 3:// 跳到singleComment
				// getAllTopics(0, 10, HEAD);
				// commentAdapter = new
				// CommentAdapter(SurveyCommentActivity.this,
				// allTopics);
				// mv_comment_list.setAdapter(commentAdapter);
			HashMap<String, Object> mdata = null;
			int new_comment_num =Integer.parseInt(allTopics.get(commentPosition).get("commentNum").toString());
			int new_scan_num = 0;

			switch (currentTab) {
			case 0: // 当前页为所有评论
				mdata = allTopics.get(commentPosition);
				new_comment_num += data.getIntExtra("add_comment_count",
						Integer.parseInt(mdata.get("commentNum").toString()));
				mdata.put("commentNum", "" + (new_comment_num ));
				Log.w("comment","" + (new_comment_num - 1));

				if (scanSuccess) {// 浏览成功
					new_scan_num = Integer.parseInt(mdata.get("readNum")
							.toString()) + 1;
					mdata.put("readNum", new_scan_num + "");
				}

				allTopics.set(commentPosition, mdata);
				commentAdapter.notifyDataSetChanged();
				break;

			case 1: // 当前页为热门评论
				mdata = hotTopics.get(commentPosition);
				new_comment_num += data.getIntExtra("add_comment_count",
						Integer.parseInt(mdata.get("commentNum").toString()));
				mdata.put("commentNum", "" + (new_comment_num ));

				if (scanSuccess) {// 浏览成功
					new_scan_num = Integer.parseInt(mdata.get("readNum")
							.toString()) + 1;
					mdata.put("readNum", new_scan_num + "");
				}

				hotTopics.set(commentPosition, mdata);
				commentAdapter.notifyDataSetChanged();
				break;
			case 2: // 当前页为股友评论
				mdata = friendTopics.get(commentPosition);
				new_comment_num += data.getIntExtra("add_comment_count",
						Integer.parseInt(mdata.get("commentNum").toString()));
				mdata.put("commentNum", "" + (new_comment_num ));

				if (scanSuccess) {// 浏览成功
					new_scan_num = Integer.parseInt(mdata.get("readNum")
							.toString()) + 1;
					mdata.put("readNum", new_scan_num + "");
				}

				friendTopics.set(commentPosition, mdata);
				commentAdapter.notifyDataSetChanged();
				break;
			case 3: // 当前页为发起人评论
				mdata = initiatorTopics.get(commentPosition);
				new_comment_num += data.getIntExtra("add_comment_count",
						Integer.parseInt(mdata.get("commentNum").toString()));
				mdata.put("commentNum", "" + (new_comment_num ));

				if (scanSuccess) {// 浏览成功
					new_scan_num = Integer.parseInt(mdata.get("readNum")
							.toString()) + 1;
					mdata.put("readNum", new_scan_num + "");
				}

				initiatorTopics.set(commentPosition, mdata);
				commentAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

			commentAdapter.notifyDataSetChanged();
			break;
		}
	}

	/*
	 * 点赞功能
	 * 
	 * @param iv_praise
	 * 
	 * @param tv_praise_num
	 * 
	 * @param position
	 * 
	 * @param topics
	 */
	private void praise(final ImageView iv_praise,
			final TextView tv_praise_num, final int position,
			final List<HashMap<String, Object>> topics) {

		iv_praise.setClickable(false);
		int praiseNum = Integer.parseInt(tv_praise_num.getText().toString());
		praiseNum++;
		tv_praise_num.setText("" + praiseNum);
		iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
		String uuid = topics.get(position).get("topicUuid").toString();
		String url = AppConfig.URL_TOPIC + "like.json?topicUuid=" + uuid;
		url = url
				+ "&access_token="
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token");
		Log.d("点赞url", "url" + url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						topics.get(position).put("liked", true);
						iv_praise.setClickable(true);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						int praiseNum = Integer.parseInt(tv_praise_num
								.getText().toString());
						praiseNum--;
						tv_praise_num.setText("" + praiseNum);
						if (topics.get(position).get("liked").toString()
								.equals("true")) {
							iv_praise
									.setImageResource(R.drawable.btn_dianzan_selected_cl);
						} else {
							iv_praise
									.setImageResource(R.drawable.btn_dianzan_normal_cl);
						}

						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

//							showInternetDialog(jsonObject
//									.getString("description"));
							loadingDialog.setFlag(false);
							loadingDialog.setInternetString(jsonObject
									.getString("description"));
							loadingDialog.showInternetDialog();

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						iv_praise.setClickable(true);

					}
				});
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 话题adapter
	 * 
	 * @author Administrator
	 * 
	 */
	public class CommentAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;

		public CommentAdapter(Context context,
				List<HashMap<String, Object>> mtopics) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
			topics = mtopics;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return topics.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.comment_item, null);
			}
			// 点赞数
			final TextView tv_praise_num = AbViewHolder.get(convertView,
					R.id.tv_praise_num);
			tv_praise_num.setText(topics.get(position).get("likeNum")
					.toString());
			RelativeLayout rl_comment = AbViewHolder.get(convertView,
					R.id.rl_comment_white1);
			if (topics.get(position).get("surveyUuid") != null) {
				rl_comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (!BtnClickUtils.isFastDoubleClick()) {
							Log.d("lele_original",
									topics.get(position).get("original")
											.toString());

							// 评论的是第几条数据
							commentPosition = position;

							Bundle bundle = new Bundle();
							bundle.putString("surveyUuid", topics.get(position)
									.get("surveyUuid").toString());

							bundle.putString("creatorUuid", topics
									.get(position).get("creatorUuid")
									.toString());
							bundle.putString("creatorName", topics
									.get(position).get("creatorName")
									.toString());
							bundle.putString("creationTime",
									topics.get(position).get("creationTime")
											.toString());
							bundle.putString("creatorLogoUrl",
									topics.get(position).get("creatorLogoUrl")
											.toString());
							bundle.putString("content", topics.get(position)
									.get("content").toString());
							bundle.putString("likeNum", tv_praise_num.getText()
									.toString());
							bundle.putString("commentNum", topics.get(position)
									.get("commentNum").toString());
							bundle.putString("readNum", topics.get(position)
									.get("readNum").toString());
							bundle.putString("transpondNum",
									topics.get(position).get("transpondNum")
											.toString());
							bundle.putString("topicUuid", topics.get(position)
									.get("topicUuid").toString());
							bundle.putString("liked",
									topics.get(position).get("liked")
											.toString());
							bundle.putString("refTopic", topics.get(position)
									.get("refTopic").toString());
							bundle.putString("medias", topics.get(position)
									.get("medias").toString());
							bundle.putString("followed", topics.get(position)
									.get("followed").toString());
							Intent intent = new Intent(
									SurveyCommentActivity.this,
									SingleCommentActivity.class);
							intent.putExtras(bundle);
							startActivityForResult(intent, 3);
							new AsyncSendReadNum().execute(topics.get(position)
									.get("topicUuid").toString());
						}
					}
				});
			}
			// 更多
			ImageView iv_more = AbViewHolder.get(convertView, R.id.iv_more);
			if (topics.get(position).get("surveyUuid") != null) {
				iv_more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						// 当用户选择menu中的转发功能是需要使用surveyUuid
						String surveyUuid = topics.get(position)
								.get("surveyUuid").toString();
						// 判断转发的话题是否是原创话题
						String original = (String) topics.get(position).get(
								"original");

						initMenu(SurveyCommentActivity.this,
								R.layout.item_comment_more_popup, position);

					}
				});
			}
			// 头像
			CircleImageView ci_face = AbViewHolder.get(convertView,
					R.id.ci_face);
		
			bitmapUtils.display(ci_face,
					topics.get(position).get("creatorLogoUrl").toString());

			ci_face.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (topics
							.get(position)
							.get("creatorUuid")
							.toString()
							.equals(RsSharedUtil.getString(
									SurveyCommentActivity.this, AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(SurveyCommentActivity.this,
								MyProfileActivity.class);
						startActivity(intent);
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid",
								topics.get(position).get("creatorUuid")
										.toString());
						bundle.putString("userName",
								topics.get(position).get("creatorName")
										.toString());
						Intent intent = new Intent();
						intent.setClass(SurveyCommentActivity.this,
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});

			// 发起人姓名
			TextView tv_name = AbViewHolder.get(convertView, R.id.tv_name);
			tv_name.setText(topics.get(position).get("creatorName").toString());

			tv_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (topics
							.get(position)
							.get("creatorUuid")
							.toString()
							.equals(RsSharedUtil.getString(
									SurveyCommentActivity.this, AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(SurveyCommentActivity.this,
								MyProfileActivity.class);
						startActivity(intent);
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid",
								topics.get(position).get("creatorUuid")
										.toString());
						bundle.putString("userName",
								topics.get(position).get("creatorName")
										.toString());
						Intent intent = new Intent();
						intent.setClass(SurveyCommentActivity.this,
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});

			// 话题发出的时间
			TextView tv_time = AbViewHolder.get(convertView, R.id.tv_time);
			long Time = Long.parseLong(topics.get(position).get("creationTime")
					.toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			String creationTime = dateFormat.format(new Date(Time));
			tv_time.setText(creationTime);
			// 内容
			TextView tv_detial = AbViewHolder.get(convertView, R.id.tv_detial);
			if (!topics.get(position).get("content").toString().equals("")) {
				tv_detial.setVisibility(View.VISIBLE);
				tv_detial.setText(topics.get(position).get("content")
						.toString());
			}

			// 评论数
			TextView tv_comment_num = AbViewHolder.get(convertView,
					R.id.tv_comment_num);
			tv_comment_num.setText(topics.get(position).get("commentNum")
					.toString());
			// 浏览量
			TextView tv_scan_num = AbViewHolder.get(convertView,
					R.id.tv_scan_num);
			tv_scan_num.setText(topics.get(position).get("readNum").toString());
			// 图片列表
			NoScrollGridView gridView = AbViewHolder.get(convertView,
					R.id.gridView);

			// 是否有图
			JSONArray mediasArray;
			try {
				mediasArray = new JSONArray(topics.get(position).get("medias")
						.toString());
				final String urls[] = new String[mediasArray.length()];
				for (int i = 0; i < mediasArray.length(); i++) {
					JSONObject jsonObject = mediasArray.getJSONObject(i);
					urls[i] = jsonObject.getString("url");
				}
				if (urls != null && urls.length > 0) {
					gridView.setVisibility(View.VISIBLE);
					gridView.setAdapter(new MyGridAdapter(urls, mContext));
					gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							imageBrower(position, urls);
						}
					});
				} else {
					gridView.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 是否有转发
			LinearLayout ll_transpon = AbViewHolder.get(convertView,
					R.id.ll_transpon);
			TextView tv_creator_name = AbViewHolder.get(convertView,
					R.id.tv_creator_name);
			TextView tv_creator_content = AbViewHolder.get(convertView,
					R.id.tv_creator_content);
			// 图片
			ImageView iv_creator_face = AbViewHolder.get(convertView,
					R.id.iv_creator_face);
			// 是否有图
			if (topics.get(position).get("refTopic").toString().equals("null")) {
				ll_transpon.setVisibility(View.GONE);
			} else {
				final JSONObject refObject;
				try {
					refObject = new JSONObject(topics.get(position)
							.get("refTopic").toString());
					tv_creator_name.setText(refObject.getString("creatorName")
							+ ":");
					tv_creator_content.setText(refObject.getString("content"));
					JSONArray mediasArray2;
					try {
						mediasArray2 = new JSONArray(refObject.getString(
								"medias").toString());
						final String urls[] = new String[mediasArray2.length()];
						// 取第一张图片
						for (int i = 0; i < mediasArray2.length(); i++) {
							JSONObject jsonObject = mediasArray2
									.getJSONObject(i);
							urls[i] = jsonObject.getString("url");
						}
						for (int i = 0; i < mediasArray2.length(); i++) {
							JSONObject jsonObject = mediasArray2
									.getJSONObject(i);
							urls[i] = jsonObject.getString("url");
						}
						if (urls != null && urls.length > 0) {
							iv_creator_face.setVisibility(View.VISIBLE);
							
							bitmapUtils1.display(iv_creator_face,urls[0]);
							/*ImageLoader.getInstance().displayImage(urls[0],
									iv_creator_face);*/
						} else {
							iv_creator_face.setVisibility(View.GONE);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ll_transpon.setVisibility(View.VISIBLE);
					ll_transpon.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							try {
								Bundle bundle = new Bundle();
								bundle.putString("surveyUuid",
										topics.get(position).get("surveyUuid")
												.toString());
								bundle.putString("creatorUuid", refObject
										.getString("creatorUuid").toString());
								bundle.putString("creatorName",
										refObject.getString("creatorName"));
								bundle.putString("creationTime",
										refObject.getString("creationTime"));
								bundle.putString("creatorLogoUrl",
										refObject.getString("creatorLogoUrl"));
								bundle.putString("content",
										refObject.getString("content"));
								bundle.putString("likeNum",
										refObject.getString("likeNum"));
								bundle.putString("commentNum",
										refObject.getString("commentNum"));
								bundle.putString("readNum",
										refObject.getString("readNum"));
								bundle.putString("transpondNum",
										refObject.getString("transpondNum"));
								bundle.putString("topicUuid",
										refObject.getString("topicUuid"));
								bundle.putString("liked",
										refObject.getString("liked"));
								bundle.putString("refTopic",
										refObject.getString("refTopic"));
								bundle.putString("medias",
										refObject.getString("medias"));
								bundle.putString("followed",
										refObject.getString("followed"));
								Intent intent = new Intent(
										SurveyCommentActivity.this,
										SingleCommentActivity.class);
								intent.putExtras(bundle);
								startActivityForResult(intent, 3);
								new AsyncSendReadNum().execute(refObject
										.getString("topicUuid"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			//分享内容
			final String contents=topics.get(position).get("creatorName").toString()+":\n"+
			topics.get(position).get("content").toString();
			// 分享
			RelativeLayout rl_share = AbViewHolder.get(convertView,
					R.id.rl_share);

			rl_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					showShare(contents);
				}
			});

			// 点赞
			boolean liked = Boolean.parseBoolean(topics.get(position)
					.get("liked").toString());
			final ImageView iv_praise = AbViewHolder.get(convertView,
					R.id.iv_praise);
			if (liked) {
				iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
			} else {
				iv_praise.setImageResource(R.drawable.btn_dianzan_normal_cl);
			}
			iv_praise.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (!BtnClickUtils.isFastDoubleClick()) {
						praise(iv_praise, tv_praise_num, position, topics);
					}

				}
			});

			return convertView;
		}

		private void imageBrower(int position, String[] urls) {
			Intent intent = new Intent(mContext, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
			mContext.startActivity(intent);
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(final Context context, int view, final int position) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getWidth();
		manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		// popwindow位置
		popupWindow.showAtLocation(background, Gravity.BOTTOM, 0, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		TextView tv_transmit = (TextView) contentView
				.findViewById(R.id.tv_transmit);
		TextView tv_copy = (TextView) contentView.findViewById(R.id.tv_copy);
		TextView tv_collect = (TextView) contentView
				.findViewById(R.id.tv_collect);
		TextView tv_report = (TextView) contentView
				.findViewById(R.id.tv_report);
		TextView tv_cancel = (TextView) contentView
				.findViewById(R.id.tv_cancel);
		//判断是不是自己的评论，隐藏收藏与举报功能
		if (topics.get(position).get("creatorUuid").equals(RsSharedUtil.getString(getApplicationContext(), AppConfig.UUID))) {
			tv_report.setVisibility(View.GONE);
			tv_collect.setVisibility(View.GONE);
		}
		// 转发
		tv_transmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString("surveyUuid",
						topics.get(position).get("surveyUuid").toString());
				bundle.putString("refUuid",
						topics.get(position).get("topicUuid").toString());
				bundle.putString("creatorName",
						topics.get(position).get("creatorName").toString());
				bundle.putString("medias", "null");
				bundle.putString("content", topics.get(position).get("content")
						.toString());
				bundle.putString("position", "" + position);

				Intent intent = new Intent(SurveyCommentActivity.this,
						TranspondActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);

				popupWindow.dismiss();
			}
		});
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast(context, R.layout.item_toast_popup, background, 1);
				popupWindow.dismiss();
			}
		});
		if (topics.get(position).get("followed").toString().equals("true"))
			tv_collect.setText("取消收藏");
		if (topics.get(position).get("followed").toString().equals("false"))
			tv_collect.setText("收藏");
		tv_collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Collect(topics.get(position).get("topicUuid").toString(),
						position);
				popupWindow.dismiss();
			}
		});
		tv_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast(context, R.layout.item_toast_popup, background, 3);
				popupWindow.dismiss();
			}
		});
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void Toast(Context context, int view, View rl, int position) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);
		TextView tv_item = (TextView) contentView.findViewById(R.id.tv_item);
		switch (position) {
		case 1:
			tv_item.setText("已复制");
			break;
		case 2:
			tv_item.setText("收藏成功");
			break;
		case 3:
			tv_item.setText("已举报");
			break;
		case 4:
			tv_item.setText("取消收藏成功");
			break;
		}
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		context.getSystemService(Context.WINDOW_SERVICE);
		// popwindow位置
		popupWindow.showAtLocation(rl, Gravity.CENTER, 0, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
	}

	/**
	 * 获取所有的评论
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void getAllTopics(int pageIndex, int pageSize, final int type) {

		Bundle bundle = getIntent().getExtras();

		String url = AppConfig.URL_TOPIC
				+ "list/survey/newest.json?access_token="
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token") + "&surveyUuid="
				+ bundle.getString("uuid") + "&pageIndex=" + pageIndex
				+ "&pageSize=" + pageSize;
		Log.d("所有评论url", url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取全部评论", response.toString());
						// 如果有评论，隐藏无评论的提示
						// ll_wupinglun.setVisibility(View.GONE);
						try {
							JSONArray all = new JSONArray(response.toString());
							final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
							for (int i = 0; i < all.length(); i++) {
								HashMap<String, Object> data = new HashMap<String, Object>();
								Iterator<String> jsIterator;
								try {
									jsIterator = all.getJSONObject(i).keys();
									while (jsIterator.hasNext()) {
										String key = jsIterator.next();
										data.put(key,
												all.getJSONObject(i).get(key)
														.toString());
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								datas.add(data);

							}
							if (type == FOOT)
								allTopics.addAll(datas);
							else {
								allTopics.clear();
								// allTopics = datas;
								allTopics.addAll(datas);
							}
							updateList(0);
							Log.d("allTopics.length()", allTopics.size() + "");

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 加载所有评论的数据后通知Handler
						all_finished = true;
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
						loadingDialog.dismissDialog();

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						all_finished = true;
						loadingDialog.dismissDialog();
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
						// Log.d("liang_all_finished", "" + all_finished);

					}
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 获取热门评论
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void getHottestTopics(int pageIndex, int pageSize, final int type) {
		Bundle bundle = getIntent().getExtras();
		String url = AppConfig.URL_TOPIC
				+ "list/survey/hottest.json?access_token="
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token") + "&surveyUuid="
				+ bundle.getString("uuid") + "&pageIndex=" + pageIndex
				+ "&pageSize=" + pageSize;
		Log.d("热门评论url", url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取热门评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getActivity(), "没有任何话题",
							// Toast.LENGTH_SHORT).show();
						} else {
							// ll_wupinglun.setVisibility(View.GONE);
							try {
								JSONArray all = new JSONArray(response
										.toString());
								final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
								for (int i = 0; i < all.length(); i++) {
									HashMap<String, Object> data = new HashMap<String, Object>();
									Iterator<String> jsIterator;
									try {
										jsIterator = all.getJSONObject(i)
												.keys();
										while (jsIterator.hasNext()) {
											String key = jsIterator.next();
											data.put(key, all.getJSONObject(i)
													.get(key).toString());
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									datas.add(data);

								}
								// 替换或者增加数据
								if (type == FOOT)
									hotTopics.addAll(datas);
								else {
									hotTopics.clear();
									// hotTopics = datas;
									hotTopics.addAll(datas);
								}
								updateList(1);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						// 加载热门评论的数据后通知Handler
						hottest_finished = true;
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
						loadingDialog.dismissDialog();

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						hottest_finished = true;
						loadingDialog.dismissDialog();
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
						
						// Log.d("liang_hottest_finished", "" +
						// hottest_finished);
					}
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 获取股友评论
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void getFriendTopics(int pageIndex, int pageSize, final int type) {

		Bundle bundle = getIntent().getExtras();
		String url = AppConfig.URL_TOPIC
				+ "list/survey/myConcernedUser.json?access_token="
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token") + "&surveyUuid="
				+ bundle.getString("uuid") + "&pageIndex=" + pageIndex
				+ "&pageSize=" + pageSize;
		// String
		// url=AppConfig.URL+"api/v1.0/topic/list/survey/myConcernedUser.json?access_token=d7abc947-6df0-4073-bc74-ba4b24c478b2&surveyUuid=123456&pageIndex=0&pageSize=5";
		Log.d("股友评论url", url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取股友评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getActivity(), "没有任何话题",
							// Toast.LENGTH_SHORT).show();
						} else {
							// ll_wupinglun.setVisibility(View.GONE);
							try {
								JSONArray all = new JSONArray(response
										.toString());
								final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
								for (int i = 0; i < all.length(); i++) {
									HashMap<String, Object> data = new HashMap<String, Object>();
									Iterator<String> jsIterator;
									try {
										jsIterator = all.getJSONObject(i)
												.keys();
										while (jsIterator.hasNext()) {
											String key = jsIterator.next();
											data.put(key, all.getJSONObject(i)
													.get(key).toString());
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									datas.add(data);

								}
								// 替换或者增加数据
								if (type == FOOT)
									friendTopics.addAll(datas);
								else {
									friendTopics.clear();
									// friendTopics = datas;
									friendTopics.addAll(datas);
								}
								updateList(2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						// 加载完股友的数据后通知Handler
						friend_finished = true;
						loadingDialog.dismissDialog();
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						friend_finished = true;
						loadingDialog.dismissDialog();
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
						// Log.d("liang_friend_finished", "" + friend_finished);
					}
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 获取发起人评论
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void getCreatorTopics(int pageIndex, int pageSize, final int type) {
		Bundle bundle = getIntent().getExtras();
		String uuid = bundle.getString("uuid");

		String url = AppConfig.URL_TOPIC
				+ "list/survey/creator.json?access_token=";
		url = url
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token");
		url = url + "&surveyUuid=" + uuid + "&pageIndex=" + pageIndex
				+ "&pageSize=" + pageSize;
		// String
		// url=AppConfig.URL+"api/v1.0/topic/list/survey/creator.json?access_token=d7abc947-6df0-4073-bc74-ba4b24c478b2&surveyUuid=123456&pageIndex=0&pageSize=5";
		Log.d("发起人评论url", url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取发起人评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getActivity(), "没有任何话题",
							// Toast.LENGTH_SHORT).show();
						} else {
							// ll_wupinglun.setVisibility(View.GONE);
							try {
								JSONArray all = new JSONArray(response
										.toString());
								final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
								for (int i = 0; i < all.length(); i++) {
									HashMap<String, Object> data = new HashMap<String, Object>();
									Iterator<String> jsIterator;
									try {
										jsIterator = all.getJSONObject(i)
												.keys();
										while (jsIterator.hasNext()) {
											String key = jsIterator.next();
											data.put(key, all.getJSONObject(i)
													.get(key).toString());
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									datas.add(data);

								}
								// 替换或者增加数据
								if (type == FOOT)
									initiatorTopics.addAll(datas);
								else {
									initiatorTopics.clear();
									// initiatorTopics = datas;
									initiatorTopics.addAll(datas);
								}
								updateList(3);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						// 加载发起人评论的数据后通知Handler
						creator_finished = true;
						loadingDialog.dismissDialog();
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						creator_finished = true;
						loadingDialog.dismissDialog();
//						Message msg = new Message();
//						msg.what = 1;
//						mHandler.sendMessage(msg);
						// Log.d("liang_creator_finished", "" +
						// creator_finished);
					}
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 更新列表
	 * 
	 * @param position
	 *            0:全部 1:热评 2:股友 3:发起人
	 */
	public void updateList(int position) {
		commentAdapter.notifyDataSetChanged();
		if (currentTab == position && isFirst) {
			isFirst = false;
			setHashMaps(position);
		}
	}

	/**
	 * 设置hashmaps
	 * 
	 * @param position
	 *            0 : 全部 1 : 热评 2 : 股友 3 : 发起人
	 */
	public void setHashMaps(int position) {
		Log.d("position", "position:" + position);
		switch (position) {

		case 0:
			if (allTopics.size() > 0) {// topics>0则show
				ll_wupinglun.setVisibility(View.GONE);
				refresh.setVisibility(View.VISIBLE);
				commentAdapter = new CommentAdapter(SurveyCommentActivity.this,
						allTopics);

				mv_comment_list.setAdapter(commentAdapter);

				Log.d("allTopics.length()", allTopics.size() + "");
			} else {
				ll_wupinglun.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.GONE);
			}

			break;

		case 1:

			if (hotTopics.size() > 0) {// topics>0则show
				ll_wupinglun.setVisibility(View.GONE);
				refresh.setVisibility(View.VISIBLE);
				commentAdapter = new CommentAdapter(

				SurveyCommentActivity.this, hotTopics);
				mv_comment_list.setAdapter(commentAdapter);

				Log.d("hotTopics.length()", hotTopics.size() + "");
			} else {
				ll_wupinglun.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.GONE);
			}
			break;

		case 2:

			if (friendTopics.size() > 0) {// topics>0则show
				ll_wupinglun.setVisibility(View.GONE);
				refresh.setVisibility(View.VISIBLE);
				commentAdapter = new CommentAdapter(

				SurveyCommentActivity.this, friendTopics);
				mv_comment_list.setAdapter(commentAdapter);

				Log.d("friendTopics.length()", friendTopics.size() + "");
			} else {
				ll_wupinglun.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.GONE);
			}

			break;

		case 3:

			if (initiatorTopics.size() > 0) {// topics>0则show
				ll_wupinglun.setVisibility(View.GONE);
				refresh.setVisibility(View.VISIBLE);
				commentAdapter = new CommentAdapter(

				SurveyCommentActivity.this, initiatorTopics);
				mv_comment_list.setAdapter(commentAdapter);

				Log.d("initiatorTopics.length()", initiatorTopics.size() + "");
			} else {
				ll_wupinglun.setVisibility(View.VISIBLE);
				refresh.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 异步加载数据
	 * 
	 * @author Administrator
	 * 
	 */
	private class AsyncGetTopics extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {

		};

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// 获取数据
			getAllTopics(all_index, PAGE_SIZE, HEAD);
			getFriendTopics(friend_index, PAGE_SIZE, HEAD);
			getHottestTopics(hot_index, PAGE_SIZE, HEAD);
			getCreatorTopics(initiator_index, PAGE_SIZE, HEAD);

//			mv_comment_list.setOnScrollListener(new OnScrollListener() {
//				@Override
//				public void onScrollStateChanged(AbsListView arg0, int arg1) {
//					// TODO Auto-generated method stub
//					//iLoader.resume();
//				}
//
//				@Override
//				public void onScroll(AbsListView arg0, int arg1, int arg2,
//						int arg3) {
//					// TODO Auto-generated method stub
//					//iLoader.pause();
//				}
//			});

			return null;
		}

		@Override
		protected void onProgressUpdate(Void[] values) {

		};

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}

	/**
	 * 关注，取消关注
	 * 
	 * @param uuid
	 */
	private void Collect(String uuid, final int position) {
		String url = AppConfig.URL_USER + "topic.json?access_token=";
		url = url
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token") + "&uuid=" + uuid;
		Log.d("collect", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("RRRRRRRRRRRRRRRRRRRRR", response.toString());
						// TODO Auto-generated method stub
						HashMap<String, Object> hashMap = topics.get(position);
						if (response.equals("true")) {
							Toast(SurveyCommentActivity.this,
									R.layout.item_toast_popup, background, 2);
							hashMap.put("followed", "true");
							topics.set(position, hashMap);
						}
						if (response.equals("false")) {
							Toast(SurveyCommentActivity.this,
									R.layout.item_toast_popup, background, 4);
							hashMap.put("followed", "false");
							topics.set(position, hashMap);
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("RRRRRRRRRRRRRRRRRRRRR", error.toString());
					}
				});
		volleyRequestQueue.add(stringRequest);
	}

	// 分享
	private void showShare(String content) {
		
		
		background.setAlpha(0.7f);
		popupWindow = new ShareUtils(SurveyCommentActivity.this,
				rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				background.setAlpha(0.0f);

			}
		});
	}

	/**
	 * 发起阅读动作
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void sendReadnum(String topicUUID) {
		String url = AppConfig.URL_TOPIC
				+ "addReadNum.json?access_token="
				+ RsSharedUtil.getString(SurveyCommentActivity.this,
						"access_token") + "&topicUuid=" + topicUUID;
		Log.d("阅读量url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						scanSuccess = true;
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						scanSuccess = false;
					}
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 异步发送数据
	 * 
	 * @author Administrator
	 * 
	 */
	private class AsyncSendReadNum extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {

		};

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// 获取数据
			sendReadnum(arg0[0]);
			return null;
		}

		@Override
		protected void onProgressUpdate(Void[] values) {

		};

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		switch (currentTab) {
		case 0:
			all_index = 0;
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					isFirst = true;
					getAllTopics(all_index, PAGE_SIZE, HEAD);
					refresh.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		case 1:
			friend_index = 0;
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getFriendTopics(friend_index, PAGE_SIZE, HEAD);
					refresh.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		case 2:
			hot_index = 0;
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getHottestTopics(hot_index, PAGE_SIZE, HEAD);
					refresh.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		case 3:
			initiator_index = 0;
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getCreatorTopics(initiator_index, PAGE_SIZE, HEAD);
					refresh.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		switch (currentTab) {
		case 0:
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getAllTopics(++all_index, PAGE_SIZE, FOOT);
					refresh.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		case 1:
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getFriendTopics(++friend_index, PAGE_SIZE, FOOT);
					refresh.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		case 2:
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getHottestTopics(++hot_index, PAGE_SIZE, FOOT);
					refresh.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		case 3:
			refresh.postDelayed(new Runnable() {

				@Override
				public void run() {
					getCreatorTopics(++initiator_index, PAGE_SIZE, FOOT);
					refresh.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		}
	}

	public class MyGridAdapter extends BaseAdapter {
		private String[] files;

		private LayoutInflater mLayoutInflater;

		public MyGridAdapter(String[] files, Context context) {
			this.files = files;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return files == null ? 0 : files.length;
		}

		@Override
		public String getItem(int position) {
			return files[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyGridViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new MyGridViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.gridview_item,
						parent, false);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.album_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (MyGridViewHolder) convertView.getTag();
			}
			String url = getItem(position);
			
			bitmapUtils1.display(viewHolder.imageView, url);
			//iLoader.displayImage(url, viewHolder.imageView);
			return convertView;
		}

		private class MyGridViewHolder {
			ImageView imageView;
		}
	}

	/**
	 * 成功发送评论后刷新界面
	 */
	@Override
	public void ToUpdate() {
		// TODO Auto-generated method stub
		// all_index = 0;
		// Log.d("发送评论成功", "哈哈哈哈");
		// refresh.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// getAllTopics(all_index, PAGE_SIZE, HEAD);
		// refresh.onHeaderRefreshComplete();
		// }
		// }, 2000);

		isFirst = true;
		all_index = 0;
		hot_index = 0;
		friend_index = 0;
		initiator_index = 0;
		currentTab = 0;
		new AsyncGetTopics().execute();
		ClearTab();
		tv_all.setBackgroundResource(R.color.title_text_chosen_color);
		
	}

	/**
	 * 发表评论失败
	 */
	protected BroadcastReceiver publishFailReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			if (internertDialog != null && internertDialog.isShowing()) {
//				internertDialog.dismiss();
////				showInternetDialog("评论发表失败，请重试");
//				InternetDialog internetDialog = new InternetDialog(getApplicationContext());
//				internetDialog.showInternetDialog("评论发表失败，请重试", false);
//			}
			loadingDialog.dismissDialog();
			InternetDialog internetDialog = new InternetDialog(SurveyCommentActivity.this);
			internetDialog.showInternetDialog("评论发表失败，请重试", false);
		}
	};
	/**
	 * 发表评论成功
	 */
	protected BroadcastReceiver publishSuccessReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			if (internertDialog != null && internertDialog.isShowing()) {
//				internertDialog.dismiss();
////				showInternetDialog("发表成功");
//				InternetDialog internetDialog = new InternetDialog(getApplicationContext());
//				internetDialog.showInternetDialog("发表成功", true);
//			}
			loadingDialog.dismissDialog();
			InternetDialog internetDialog = new InternetDialog(SurveyCommentActivity.this);
			internetDialog.showInternetDialog("发表成功", true);
		}
	};

	/**
	 * 注册发表评论失败的广播接收器
	 */
	private void registerPublishFailReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("publish_topic_fail");
		this.registerReceiver(this.publishFailReceiver, filter);
	}
	/**
	 * 注册发表评论成功的广播接收器
	 */
	private void registerPublishSuccessReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("updateComment");
		this.registerReceiver(this.publishSuccessReceiver, filter);
	}

	/**
	 * 转发成功
	 */
	protected BroadcastReceiver transpondSuccessReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			isFirst = true;
			all_index = 0;
			hot_index = 0;
			friend_index = 0;
			initiator_index = 0;
			currentTab = 0;
			new AsyncGetTopics().execute();
			ClearTab();
			tv_all.setBackgroundResource(R.color.title_text_chosen_color);
		}
	};

	/**
	 * 注册转发成功的广播接收器
	 */
	private void registerTranspondSuccessReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("transpond_topic_success");
		this.registerReceiver(this.transpondSuccessReceiver, filter);
	}

	/**
	 * 转发失败
	 */
	protected BroadcastReceiver transpondFailReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("liang_transpond_fail", "fail");
//			if (internertDialog != null && internertDialog.isShowing()) {
//				internertDialog.dismiss();
//				showInternetDialog("评论转发失败，请重试");
//			}
			loadingDialog.dismissDialog();
			InternetDialog internetDialog = new InternetDialog(SurveyCommentActivity.this);
			internetDialog.showInternetDialog("评论转发失败，请重试", false);
		}
	};

	/**
	 * 注册转发失败的广播接收器
	 */
	private void registerTranspondFailReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("transpond_topic_fail");
		this.registerReceiver(this.transpondFailReceiver, filter);
	}

	/**
	 * 弹出菜单
	 * 
	 * @param v
	 */
	private void showPopUp(View v) {
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.popup_survey_comment, null);

		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		// 设置点击外部推出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		// 偏移量
		int xoffset = popupWindow.getWidth() / 2;

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		// 显示popupWindow
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
				location[0] - v.getWidth() * 4 / 5, location[1] + v.getHeight()
						+ 5);

		// 所有评论
		TextView tv_all_comment = (TextView) contentView
				.findViewById(R.id.tv_all_comment);
		// 我的评论
		TextView tv_my_comment = (TextView) contentView
				.findViewById(R.id.tv_my_comment);

		tv_all_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(SurveyCommentActivity.this,
						ReviewDetailsActivity.class));
				popupWindow.dismiss();
			}
		});

		tv_my_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(SurveyCommentActivity.this,
						PersonalCommentActivity.class));
				popupWindow.dismiss();
			}
		});

	}
}
