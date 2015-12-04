package com.example.shareholders.activity.survey;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
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
import android.widget.AdapterView.OnItemClickListener;
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
import com.example.shareholders.common.NoScrollGridView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.receiver.UpdateCommentReceiver;
import com.example.shareholders.receiver.UpdateCommentReceiver.UpLoadDownListener;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.NetWorkCheck;
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

@ContentView(R.layout.activity_review_details)
public class ReviewDetailsActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, UpLoadDownListener {

	
	private BitmapUtils bitmapUtils = null;
	
	
	private BitmapUtils bitmapUtils1 = null;
	@ViewInject(R.id.ll_layout)
	private RelativeLayout rLayout;
	@ViewInject(R.id.background)
	private RelativeLayout background;
	@ViewInject(R.id.rl_rd_title)
	private RelativeLayout rl_return;
	@ViewInject(R.id.rl_rd_comments)
	private RelativeLayout rl_comments;

	// 评论列表
	@ViewInject(R.id.mv_rd_comment_list)
	private ListView mv_comment_list;

	// 评论标题
	// 全部评论
	@ViewInject(R.id.tv_rd_all)
	private TextView tv_all;
	// 热门评论
	@ViewInject(R.id.tv_rd_popular)
	private TextView tv_popular;

	// 提示没有评论
	@ViewInject(R.id.ll_wupinglun)
	private LinearLayout ll_wupinglun;
	// 上下拉刷新
	@ViewInject(R.id.refresh1)
	private PullToRefreshView refresh1;
	// 初始化
	RequestQueue volleyRequestQueue;
	// 全部评论
	private List<HashMap<String, Object>> allTopics;
	// 热门评论
	private List<HashMap<String, Object>> hotTopics;
	// 股友评论
	private List<HashMap<String, Object>> friendTopics;
	// 发起人评论
	private List<HashMap<String, Object>> initiatorTopics;

	private CommentAdapter commentAdapter;
	// 全局topics
	private List<HashMap<String, Object>> topics;
	// 当前选中的tab
	private int currentTab = 0;

	// pageSize,固定为5条话题
	private static int PAGE_SIZE = 10;
	// pageIndex,从0递增
	private int all_index = 0;
	private int hot_index = 0;
	private int friend_index = 0;
	private int initiator_index = 0;
	private String followed;
	private String topicUuid;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	private ShareUtils popupWindow;

	@ViewInject(R.id.ll_layout)
	private RelativeLayout rl_parent;

	@ViewInject(R.id.rl_shallow)
	private View rl_shoallow;

	private void OnTab(TextView tv) {
		tv.setBackgroundResource(R.drawable.bg_all_commment_selected);
		switch (tv.getId()) {
		case R.id.tv_rd_all:
			if (currentTab != 0) {
				currentTab = 0;
				setHashMaps(currentTab);
			}
			break;
		case R.id.tv_rd_popular:
			if (currentTab != 1) {
				currentTab = 1;
				setHashMaps(currentTab);
			}
			break;
		default:
			break;
		}
	}

	// 记录自己当前浏览的是哪一条数据
	private int scanPosition = 0;
	// 记录发起阅读动作是否成功
	private boolean scan_success = false;

	private AlertDialog internertDialog = null;
	private boolean all_finished = false; // 获取所有评论的后台数据是否完成
	private boolean hot_finished = false; // 获取热门的后台数据是否完成

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					if (all_finished && hot_finished) { // 当获取所有评论和热门评论都完成，才令加载的对话框消失
						if (currentTab == 0) {// 当前是按时间排序且没有数据
							if (allTopics.size() == 0) {
								ll_wupinglun.setVisibility(View.VISIBLE);
							}
						} else if (currentTab == 1) {// 当前是按热度排序且没有数据
							if (hotTopics.size() == 0) {
								ll_wupinglun.setVisibility(View.VISIBLE);
							}
						} else {
							ll_wupinglun.setVisibility(View.GONE);
						}

						internertDialog.dismiss();
						all_finished = false;
						hot_finished = false;
					}
				}
				break;

			case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力

				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
					// showInternetDialog();
				}

				break;

			case 3: // 提示网络异常的对话框消失
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;
			default:
				break;
			}
		};
	};

	UpdateCommentReceiver updateCommentReceiver;
	ImageLoader iLoader;

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
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		
		bitmapUtils = new BitmapUtils(ReviewDetailsActivity.this);
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_default_headview);

		
		bitmapUtils1= new BitmapUtils(ReviewDetailsActivity.this);
		bitmapUtils1 .configDefaultLoadingImage(R.drawable.empty_photo);
		bitmapUtils1.configDefaultLoadFailedImage(R.drawable.empty_photo);
		init();

		showLoadingDialog();
		new AsyncGetTopics().execute();
        
		refresh1.setOnHeaderRefreshListener(this);
		refresh1.setOnFooterRefreshListener(this);
/*		iLoader = ImageLoader.getInstance();
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

		// 初始化

		/*
		 * //获取两个Tag的评论 new AsyncGetTopics().execute();
		 */

		ClearTab();
		int position = 0;
		try {
			position = getIntent().getExtras().getInt("position");
		} catch (Exception e) {

		}
		if (position == 1) {
			OnTab(tv_popular);
		} else
			OnTab(tv_all);
		
		//检测网络连接状态
				if (!NetWorkCheck.isNetworkConnected(this)) {
					InternetDialog internetDialog = new InternetDialog(
							ReviewDetailsActivity.this);
					internetDialog
					.showInternetDialog("网络异常",false);
				}
	}

	@Override
	protected void onResume() {
		super.onResume();

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 3 && resultCode == 3) { // 从SingleComment返回

			HashMap<String, Object> mData = null;

			if (currentTab == 0) {
				mData = allTopics.get(scanPosition);
			} else {
				mData = hotTopics.get(scanPosition);
			}

			
			if (scan_success) {// 浏览成功
				int new_scan_num = Integer.parseInt(mData.get("readNum")
						.toString()) + 1;
				mData.put("readNum", "" + new_scan_num);
				scan_success = false;
			}

			try {
				int comment_count = Integer.parseInt(mData.get("commentNum")
						.toString());
				int add_comment_count = data.getIntExtra("add_comment_count",
						comment_count);
				comment_count += add_comment_count;
				mData.put("commentNum", "" + comment_count);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("liang_back_Exception", e.toString());
			}

			if (currentTab==0) {
				allTopics.set(scanPosition, mData);
			} else {
				hotTopics.set(scanPosition, mData);
			}
			
			commentAdapter.notifyDataSetChanged();
			
			
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void init() {
		currentTab = 0;
		allTopics = new ArrayList<HashMap<String, Object>>();
		hotTopics = new ArrayList<HashMap<String, Object>>();
		friendTopics = new ArrayList<HashMap<String, Object>>();
		initiatorTopics = new ArrayList<HashMap<String, Object>>();
		commentAdapter = new CommentAdapter(this, allTopics);
		mv_comment_list.setAdapter(commentAdapter);
		volleyRequestQueue = Volley.newRequestQueue(this);
		mv_comment_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("surveyUuid",
						topics.get(position).get("surveyUuid").toString());
				bundle.putString("creatorName",
						topics.get(position).get("creatorName").toString());
				bundle.putString("creationTime",
						topics.get(position).get("creationTime").toString());
				bundle.putString("creatorLogoUrl",
						topics.get(position).get("creatorLogoUrl").toString());
				bundle.putString("content", topics.get(position).get("content")
						.toString());
				bundle.putString("likeNum", topics.get(position).get("likeNum")
						.toString());
				bundle.putString("commentNum",
						topics.get(position).get("commentNum").toString());
				bundle.putString("readNum", topics.get(position).get("readNum")
						.toString());
				bundle.putString("transpondNum",
						topics.get(position).get("transpondNum").toString());
				bundle.putString("topicUuid",
						topics.get(position).get("topicUuid").toString());
				bundle.putString("liked", topics.get(position).get("liked")
						.toString());
				bundle.putString("refTopic",
						topics.get(position).get("refTopic").toString());
				bundle.putString("medias", topics.get(position).get("medias")
						.toString());
				bundle.putString("followed", topics.get(position).get("followed")
						.toString());
			
				Intent intent = new Intent(getApplicationContext(),
						SingleCommentActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				new AsyncSendReadNum().execute(topics.get(position)
						.get("topicUuid").toString());
			}
		});
	}

	@OnClick({ R.id.title_rd_note, R.id.tv_detail, R.id.tv_comment,
			R.id.iv_share, R.id.rl_sign, R.id.rl_collect, R.id.tv_rd_all,
			R.id.tv_rd_popular, R.id.iv_nav, R.id.iv_my_comment })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_rd_note:
			finish();
			break;
		case R.id.tv_rd_all:
			ClearTab();
			OnTab(tv_all);
			break;

		case R.id.tv_rd_popular:
			ClearTab();
			OnTab(tv_popular);
			break;
		case R.id.iv_nav:
			break;
		case R.id.iv_my_comment:
			startActivity(new Intent(ReviewDetailsActivity.this,
					PersonalCommentActivity.class));
			break;

		default:
			break;
		}
	}

	private void ClearTab() {
		tv_all.setBackgroundResource(R.drawable.bg_all_comment_normal);
		tv_popular.setBackgroundResource(R.drawable.bg_all_comment_normal);
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

		// iv_praise.setClickable(false);
		int praiseNum = Integer.parseInt(tv_praise_num.getText().toString());
		praiseNum++;
		tv_praise_num.setText("" + praiseNum);
		iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
		String uuid = topics.get(position).get("topicUuid").toString();
		String url = AppConfig.URL_TOPIC + "like.json?topicUuid=" + uuid;
		url = url + "&access_token="
				+ RsSharedUtil.getString(this, "access_token");
		Log.d("liang_dianzan_click", "click");

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("liang_dianzan_success", "success");
						topics.get(position).put("liked", true);
						// iv_praise.setClickable(true);
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
						// iv_praise.setClickable(true);
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

							showInternetDialog(jsonObject
									.getString("description"));
							Log.d("liang_description",
									jsonObject.getString("description"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("liang_dianzan_Exception", e.toString());
						}
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
			// TODO Auto-generated method stub7
			return topics.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.comment_special_item,
						null);
			}
			RelativeLayout rl_comment = AbViewHolder.get(convertView,
					R.id.detail);
			if (topics.get(position).get("surveyUuid") != null) {
				rl_comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						scanPosition = position;

						Bundle bundle = new Bundle();
						bundle.putString("surveyUuid", topics.get(position)
								.get("surveyUuid").toString());
						bundle.putString("creatorUuid", topics.get(position)
								.get("creatorUuid").toString());
						bundle.putString("creatorName", topics.get(position)
								.get("creatorName").toString());
						bundle.putString("creationTime", topics.get(position)
								.get("creationTime").toString());
						bundle.putString("creatorLogoUrl", topics.get(position)
								.get("creatorLogoUrl").toString());
						bundle.putString("content",
								topics.get(position).get("content").toString());
						bundle.putString("likeNum",
								topics.get(position).get("likeNum").toString());
						bundle.putString("commentNum", topics.get(position)
								.get("commentNum").toString());
						bundle.putString("readNum",
								topics.get(position).get("readNum").toString());
						bundle.putString("transpondNum", topics.get(position)
								.get("transpondNum").toString());
						bundle.putString("topicUuid",
								topics.get(position).get("topicUuid")
										.toString());
						bundle.putString("liked",
								topics.get(position).get("liked").toString());
						bundle.putString("refTopic",
								topics.get(position).get("refTopic").toString());
						bundle.putString("medias",
								topics.get(position).get("medias").toString());
						Intent intent = new Intent(getApplicationContext(),
								SingleCommentActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 3);
						new AsyncSendReadNum().execute(topics.get(position)
								.get("topicUuid").toString());
					}
				});
			}
			// 更多
			ImageView iv_more = AbViewHolder.get(convertView, R.id.iv_more);
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
					if (topics
							.get(position)
							.get("creatorUuid")
							.toString()
							.equals(RsSharedUtil.getString(
									ReviewDetailsActivity.this, AppConfig.UUID))) {
						initMenu(getApplicationContext(),
								R.layout.item_comment_my_popup, position);
					}else{
						initMenuWithAll(getApplicationContext(),
								R.layout.item_comment_more_popup, position);
					}
					

				}
			});
			// 调研名
			TextView tv_survey_name = AbViewHolder.get(convertView,
					R.id.tv_survey_name);
			tv_survey_name.setText(topics.get(position).get("surveyName")
					.toString());
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
									ReviewDetailsActivity.this, AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(ReviewDetailsActivity.this,
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
						bundle.putString("useLogo",
								topics.get(position).get("creatorLogoUrl")
										.toString());

						Intent intent = new Intent();
						intent.setClass(ReviewDetailsActivity.this,
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
									ReviewDetailsActivity.this, AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(ReviewDetailsActivity.this,
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
						intent.setClass(ReviewDetailsActivity.this,
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
			tv_detial.setText(topics.get(position).get("content").toString());

			// 点赞数
			final TextView tv_praise_num = AbViewHolder.get(convertView,
					R.id.tv_praise_num);
			tv_praise_num.setText(topics.get(position).get("likeNum")
					.toString());
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
					// 转发的布局内容
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
										getApplicationContext(),
										SingleCommentActivity.class);
								intent.putExtras(bundle);
								startActivity(intent);
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

			// 分享
			RelativeLayout rl_share = AbViewHolder.get(convertView,
					R.id.rl_share);
			
			//分享内容
			final String contents=topics.get(position).get("creatorName").toString()+":\n"+
			topics.get(position).get("content").toString();

			rl_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					rl_shoallow.setVisibility(View.VISIBLE);

					
					popupWindow = new ShareUtils(
							ReviewDetailsActivity.this, rl_parent,contents);

					popupWindow.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss() {
							rl_shoallow.setVisibility(View.GONE);

						}
					});
				}
			});

			tv_survey_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String uuid = topics.get(position).get("surveyUuid")
							.toString();
					Intent intent = new Intent(ReviewDetailsActivity.this,
							DetailSurveyActivity.class);
					Bundle bundle = new Bundle();

					bundle.putString("uuid", uuid);
					intent.putExtras(bundle);
					ReviewDetailsActivity.this.startActivity(intent);
				}
			});
			
			Log.d("hahahah", topics.toString());

			// 点赞
			String liked = topics.get(position).get("liked").toString();
			final ImageView iv_praise = AbViewHolder.get(convertView,
					R.id.iv_praise);
			if (liked.equals("true")) {
				iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
			} else {
				iv_praise.setImageResource(R.drawable.btn_dianzan_normal_cl);
			}
			RelativeLayout rl_price = AbViewHolder.get(convertView,
					R.id.rl_praise);

			rl_price.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					praise(iv_praise, tv_praise_num, position, topics);
				}
			});

			return convertView;

			// return null;
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
	public void initMenuWithAll(final Context context, int view, final int position) {
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
		popupWindow.showAtLocation(rLayout, Gravity.BOTTOM, 0, 0);
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
		//判断如果是自己的，则隐藏收藏和举报的选项
		if (topics.get(position).get("creatorUuid").equals(RsSharedUtil.getString(getApplicationContext(), AppConfig.UUID))) {
			tv_collect.setVisibility(View.GONE);
			tv_report.setVisibility(View.GONE);
		}
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
				bundle.putString("medias", topics.get(position).get("medias")
						.toString());
				bundle.putString("content", topics.get(position).get("content")
						.toString());

				Intent intent = new Intent(ReviewDetailsActivity.this,
						TranspondActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
				popupWindow.dismiss();
			}
		});
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Toast(context, R.layout.item_toast_popup, rLayout, 1);
				String content = topics.get(position).get("content").toString();

				ClipboardManager copy = (ClipboardManager) ReviewDetailsActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
				Toast(context, R.layout.item_toast_popup, background, 1);
				popupWindow.dismiss();
			}
		});
		
		followed = topics.get(position).get("followed").toString();
		if (followed.equals("true")) {
			tv_collect.setText("取消收藏");
		}
		if (followed.equals("false"))
			tv_collect.setText("收藏");
		// 点击收藏
		tv_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String url = AppConfig.URL_USER + "topic.json?access_token=";
				url = url
						+ RsSharedUtil.getString(ReviewDetailsActivity.this, "access_token")
						+ "&uuid="
						+ topics.get(position).get("topicUuid").toString();
				StringRequest stringRequest = new StringRequest(url, null,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {
								Log.d("dj_collect", response.toString());
								// TODO Auto-generated method stub
								if (response.equals("true")) {
									HashMap<String, Object> data = topics
											.get(position);
									data.put("followed", "true");
									topics.set(position, data);
									Toast(ReviewDetailsActivity.this,
											R.layout.item_toast_popup,
											rl_parent, 2);
									followed = "true";
									
								}
								if (response.equals("false")) {
									HashMap<String, Object> data = topics
											.get(position);
									data.put("followed", "false");
									topics.set(position, data);
									Toast(ReviewDetailsActivity.this,
											R.layout.item_toast_popup,
											rl_parent, 4);
									followed = "false";
									
								}
							}

						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
							}
						});
				volleyRequestQueue.add(stringRequest);
				// Toast(context, R.layout.item_toast_popup, rl_parent, 2);
				popupWindow.dismiss();
			}
		});

		tv_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast(context, R.layout.item_toast_popup, rLayout, 3);
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
		popupWindow.showAtLocation(rLayout, Gravity.BOTTOM, 0, 0);
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
				bundle.putString("medias", topics.get(position).get("medias")
						.toString());
				bundle.putString("content", topics.get(position).get("content")
						.toString());
				
				Intent intent = new Intent(ReviewDetailsActivity.this,
						TranspondActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
				popupWindow.dismiss();
			}
		});
		tv_copy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Toast(context, R.layout.item_toast_popup, rLayout, 1);
				
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
		case 0:
			tv_item.setText("已转发");
			break;
		case 1:
			tv_item.setText("已复制");
			break;
		case 2:
			tv_item.setText("已收藏");
			break;
		case 3:
			tv_item.setText("已举报");
			break;
		case 4:
			tv_item.setText("取消收藏");
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
	 * 
	 * @param pageSize
	 */
	private void getAllTopics(int pageIndex, int pageSize, final int type) {

		String url = AppConfig.URL_TOPIC
				+ "list/survey/newest.json?access_token=";
		url = url + RsSharedUtil.getString(this, "access_token");
		url = url + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取全部评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getApplicationContext(), "没有任何评论",
							// Toast.LENGTH_SHORT).show();
						} else {
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
								if (type == FOOT)
									allTopics.addAll(datas);
								else {
									allTopics.clear();
									// allTopics = datas;
									allTopics.addAll(datas);
								}
								updateList(0);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						all_finished = true;
                     
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

						all_finished = true;

						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

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
		String url = AppConfig.URL_TOPIC
				+ "list/survey/hottest.json?access_token=";
		url = url + RsSharedUtil.getString(this, "access_token");
		url = url + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
		Log.d("url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取热门评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getApplicationContext(), "没有任何评论",
							// Toast.LENGTH_SHORT).show();
						} else {

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
									hotTopics = datas;
								}
								updateList(1);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						hot_finished = true;

						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

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

						hot_finished = true;

						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

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
		String url = AppConfig.URL_TOPIC
				+ " list/survey/myConcernedUser.json?access_token=";
		url = url + RsSharedUtil.getString(this, "access_token");
		url = url + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
		// String
		// url=AppConfig.URL+" list/survey/myConcernedUser.json?access_token=d7abc947-6df0-4073-bc74-ba4b24c478b2&surveyUuid=123456&pageIndex=0&pageSize=5";
		Log.d("url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取股友评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getApplicationContext(), "没有任何评论",
							// Toast.LENGTH_SHORT).show();
						} else {
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
									friendTopics = datas;
								}
								updateList(2);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
		String url = AppConfig.URL_TOPIC + " mine.json?access_token=";
		url = url + RsSharedUtil.getString(this, "access_token");
		url = url + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
		// String
		// url=AppConfig.URL+" list/survey/creator.json?access_token=d7abc947-6df0-4073-bc74-ba4b24c478b2&surveyUuid=123456&pageIndex=0&pageSize=5";
		Log.d("我的url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("获取我的评论", response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getApplicationContext(), "没有任何评论",
							// Toast.LENGTH_SHORT).show();
						} else {
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
									initiatorTopics = datas;
								}
								updateList(3);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
					}
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 更新列表
	 * 
	 * @param position
	 *            0 : 全部 1 : 热评 2 : 股友 3 : 发起人
	 */
	public void updateList(int position) {
		commentAdapter.notifyDataSetChanged();
		if (currentTab == position) {
			setHashMaps(position);
		}
	}

	/**
	 * 设置hashmaps
	 * 
	 * @param position
	 *            时间 0 : 热度 1
	 */
	public void setHashMaps(int position) {
		switch (position) {
		case 0:
			if (allTopics.size() > 0) {// topics>0则show
				ll_wupinglun.setVisibility(View.GONE);
				refresh1.setVisibility(View.VISIBLE);
				commentAdapter = new CommentAdapter(this, allTopics);
				mv_comment_list.setAdapter(commentAdapter);

				// commentAdapter.notifyDataSetChanged();

				Log.d("allTopics.length()", allTopics.size() + "");
			} else {
				ll_wupinglun.setVisibility(View.VISIBLE);
				refresh1.setVisibility(View.GONE);
			}
			break;

		case 1:
			if (hotTopics.size() > 0) {// topics>0则show
				ll_wupinglun.setVisibility(View.GONE);
				refresh1.setVisibility(View.VISIBLE);
				commentAdapter = new CommentAdapter(this, hotTopics);
				mv_comment_list.setAdapter(commentAdapter);
				Log.d("hotTopics.length()", hotTopics.size() + "");
			} else {
				ll_wupinglun.setVisibility(View.VISIBLE);
				refresh1.setVisibility(View.GONE);
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
			getHottestTopics(hot_index, PAGE_SIZE, HEAD);
            
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
	 * 发起阅读动作
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void sendReadnum(String topicUUID) {
		String url = AppConfig.URL_TOPIC + "addReadNum.json?access_token="
				+ RsSharedUtil.getString(this, "access_token") + "&topicUuid="
				+ topicUUID;
		Log.d("阅读量url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						scan_success = true;
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						scan_success = false;
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
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getAllTopics(all_index, PAGE_SIZE, HEAD);
					refresh1.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		case 1:
			friend_index = 0;
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getFriendTopics(friend_index, PAGE_SIZE, HEAD);
					refresh1.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		case 2:
			hot_index = 0;
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getHottestTopics(hot_index, PAGE_SIZE, HEAD);
					refresh1.onHeaderRefreshComplete();
				}
			}, 2000);
			break;
		case 3:
			initiator_index = 0;
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getCreatorTopics(initiator_index, PAGE_SIZE, HEAD);
					refresh1.onHeaderRefreshComplete();
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
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getAllTopics(++all_index, PAGE_SIZE, FOOT);
					refresh1.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		case 1:
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getFriendTopics(++friend_index, PAGE_SIZE, FOOT);
					refresh1.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		case 2:
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getHottestTopics(++hot_index, PAGE_SIZE, FOOT);
					refresh1.onFooterRefreshComplete();
				}
			}, 2000);
			break;
		case 3:
			refresh1.postDelayed(new Runnable() {

				@Override
				public void run() {
					getCreatorTopics(++initiator_index, PAGE_SIZE, FOOT);
					refresh1.onFooterRefreshComplete();
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
			return convertView;
		}

		private class MyGridViewHolder {
			ImageView imageView;
		}
	}

	@Override
	public void ToUpdate() {
		// TODO Auto-generated method stub
		all_index = 0;
		refresh1.postDelayed(new Runnable() {

			@Override
			public void run() {
				getAllTopics(all_index, PAGE_SIZE, HEAD);
				refresh1.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private void showLoadingDialog() {
		internertDialog = new AlertDialog.Builder(this).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessageDelayed(msg, 5000);
			}
		}).start();

	}

	/**
	 * 提示網絡不給力
	 */
	private void showInternetDialog(String msg) {
		internertDialog = new AlertDialog.Builder(this).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_dianzan);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);

		progress_bar.setVisibility(View.GONE);
		iv_tips.setVisibility(View.VISIBLE);
		tv_message.setText(msg);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessageDelayed(msg, 2000);
			}
		}).start();

	}

	
}
