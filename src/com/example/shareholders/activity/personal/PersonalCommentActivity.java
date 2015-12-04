package com.example.shareholders.activity.personal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.stock.StockCommentActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.activity.survey.SingleCommentActivity;
import com.example.shareholders.activity.survey.TranspondActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_personal_comment)
public class PersonalCommentActivity extends Activity implements
OnHeaderRefreshListener, OnFooterRefreshListener {
	
	

	// 头部栏标题
	@ViewInject(R.id.title_text)
	private TextView title_text;

	@ViewInject(R.id.my_comment_list)
	private ListView my_comment_list;

	// 暂无评论的提示
	@ViewInject(R.id.ll_wupinglun)
	private LinearLayout ll_wupinglun;

	private BitmapUtils bitmapUtils = null;

	// 上下拉刷新
	@ViewInject(R.id.refresh1)
	private PullToRefreshView refresh1;

	// 全局数据
	private ArrayList<HashMap<String, Object>> allDatas = new ArrayList<HashMap<String,Object>>();

	private MyCommentAdapter adapter;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	// 数据增加
	private int pageadd = 0;

	private int pageSize = 5;
	private int pageIndex = 0;

	// popupWindow阴影
	@ViewInject(R.id.v_bg)
	private View v_bg;
	private ShareUtils popupWindow;
	@ViewInject(R.id.rl_paren)
	private RelativeLayout rl_parent;
	ImageLoader iLoader;

	String userUuid = "";
	String userName = "";

	private int scanPosition = 0; // 标记浏览的是哪一条数据
	private boolean scan_success = false; // 是否浏览成功

	/**
	 * 信息提示
	 */

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;

				// case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力
				//
				// if (internertDialog != null && internertDialog.isShowing()) {
				// internertDialog.dismiss();
				// showInternetDialog();
				// }
				//
				// break;
				//
				// case 3: // 提示网络异常的对话框消失
				// if (internertDialog != null && internertDialog.isShowing()) {
				// internertDialog.dismiss();
				// }
				// break;
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
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		Log.d("myuuid", RsSharedUtil.getString(getApplicationContext(), AppConfig.UUID));
		
		initView();
		getBundle();

		showLoadingDialog();
		init(0, 5, FOOT);
	}

	private void initView() {
		v_bg.setAlpha(0.0f);
		iLoader = ImageLoader.getInstance();
		iLoader.resume();

		my_comment_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				iLoader.resume();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				iLoader.pause();
			}
		});

		refresh1.setOnHeaderRefreshListener(this);
		refresh1.setOnFooterRefreshListener(this);
		allDatas = new ArrayList<HashMap<String, Object>>();

		adapter = new MyCommentAdapter(PersonalCommentActivity.this, allDatas);
		my_comment_list.setAdapter(adapter);

		//item监听，跳转个评
		my_comment_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (allDatas.get(position).get("surveyUuid") != null) { 
					Bundle bundle = new Bundle();
					bundle.putString("surveyUuid",
							allDatas.get(position).get("surveyUuid").toString());
					bundle.putString("creatorName",
							allDatas.get(position).get("creatorName")
							.toString());
					bundle.putString("followed", allDatas.get(position).get("followed")
							.toString());
					bundle.putString("creationTime", allDatas.get(position)
							.get("creationTime").toString());
					bundle.putString("creatorLogoUrl", allDatas.get(position)
							.get("creatorLogoUrl").toString());
					bundle.putString("content",
							allDatas.get(position).get("content").toString());
					bundle.putString("likeNum",
							allDatas.get(position).get("likeNum").toString());
					bundle.putString("commentNum",
							allDatas.get(position).get("commentNum").toString());
					bundle.putString("readNum",
							allDatas.get(position).get("readNum").toString());
					bundle.putString("transpondNum", allDatas.get(position)
							.get("transpondNum").toString());
					bundle.putString("topicUuid",
							allDatas.get(position).get("topicUuid").toString());
					bundle.putString("liked",
							allDatas.get(position).get("liked").toString());
					bundle.putString("refTopic",
							allDatas.get(position).get("refTopic").toString());
					bundle.putString("medias",
							allDatas.get(position).get("medias").toString());
					bundle.putString("createByMe",
							"true");
					bundle.putSerializable("creatorUuid", 
							allDatas.get(position).get("creatorUuid").toString());
					//调研个评
					Intent intent = new Intent(getApplicationContext(),
							SingleCommentActivity.class);
					intent.putExtras(bundle);
					Log.w("milk1",intent.getExtras().getString("createByMe"));
					startActivity(intent);
					new AsyncSendReadNum().execute(allDatas.get(position)
							.get("topicUuid").toString());
				}else {
					Bundle bundle = new Bundle();
					bundle.putString("creatorLogoUrl",
							allDatas.get(position).get("creatorLogoUrl").toString());
					bundle.putString("topicUuid", allDatas.get(position).get("topicUuid")
							.toString());
					bundle.putString("followed", allDatas.get(position).get("followed")
							.toString());
					bundle.putString("content", allDatas.get(position).get("content")
							.toString());
					bundle.putString("creatorName", allDatas.get(position).get("creatorName")
							.toString());
					bundle.putString("creatorUuid", allDatas.get(position).get("creatorUuid")
							.toString());
					bundle.putString("creationTime", allDatas.get(position).get("creationTime")
							.toString());
					bundle.putString("securitySymbol",
							allDatas.get(position).get("securitySymbol").toString());
					bundle.putString("securityName", allDatas.get(position).get("securityName")
							.toString());
					bundle.putString("likeNum", allDatas.get(position).get("likeNum")
							.toString());
					bundle.putString("readNum", allDatas.get(position).get("readNum")
							.toString());
					bundle.putString("transpondNum", allDatas.get(position).get("transpondNum")
							.toString());
					bundle.putString("liked", allDatas.get(position).get("liked").toString());
					bundle.putString("refTopic", allDatas.get(position).get("refTopic")
							.toString());
					bundle.putString("commentNum", allDatas.get(position).get("commentNum")
							.toString());
					try {
						bundle.putString("medias", allDatas.get(position).get("medias")
								.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//沪深个评评论
					Intent intent = new Intent(getApplicationContext(), StockCommentActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

	}

	@OnClick({ R.id.rl_return })
	private void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * 获取某个用户的评论
	 */
	private void init(int pageIndex, int pageSize, final int type) {

		String url = AppConfig.VERSION_URL
				+ "topic/list/topicListOfUserCreated.json?access_token=";
		url += RsSharedUtil.getString(PersonalCommentActivity.this,
				AppConfig.ACCESS_TOKEN);

		if (userUuid.equals("")) {// 用户的评论
			url = url + "&pageSize=" + pageSize + "&pageIndex=" + pageIndex;
		} else {// 别的用户的评论
			url += "&userUuid=" + userUuid + "&pageSize=" + pageSize
					+ "&pageIndex=" + pageIndex;
			Log.d("dj_url", url);
		}

		Log.d("liang_url_my_comment", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// Log.d("liang_my_comment_response",
				// response.toString());
				try {
					JSONArray jsonArray = new JSONArray(response);
					final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> data = null;
					Iterator<String> iterator = null;

					for (int i = 0; i < jsonArray.length(); i++) {
						data = new HashMap<String, Object>();
						iterator = jsonArray.getJSONObject(i).keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							data.put(key, jsonArray.getJSONObject(i)
									.get(key).toString());
						}
						datas.add(data);
					}

					// 替换或者增加数据
					if (type == FOOT)
						allDatas.addAll(datas);
					else {
						allDatas.clear();
						allDatas.addAll(datas);
					}
					adapter.notifyDataSetChanged();

					if (allDatas.size() == 0) {
						ll_wupinglun.setVisibility(View.VISIBLE);
					} else {
						ll_wupinglun.setVisibility(View.GONE);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("liang_my_comment_error1", e.toString());
					e.printStackTrace();
				}

				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("liang_my_comment_error2", error.toString());

				if (allDatas.size() == 0) {
					ll_wupinglun.setVisibility(View.VISIBLE);
				} else {
					ll_wupinglun.setVisibility(View.GONE);
				}

				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);

			}
		});
		stringRequest.setTag("My_Comment");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void getBundle() {
		try {
			Bundle bundle = getIntent().getExtras();
			userUuid = bundle.getString("userUuid");
			userName = bundle.getString("userName");
			if (!userName.equals("")) {
				title_text.setText(userName + "的评论");
			}
		} catch (Exception e) {
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub


		if (requestCode == 3 && resultCode == 3) { //从SingleComment返回

			HashMap<String, Object> mData = allDatas.get(scanPosition);
			if (scan_success) {// 浏览成功
				int new_scan_num = Integer.parseInt(mData.get("readNum")
						.toString()) + 1;

				mData.put("readNum", "" + new_scan_num);
				scan_success = false;
			}

			try {
				int comment_count=Integer.parseInt(mData.get("commentNum").toString());
				int add_comment_count=data.getIntExtra("add_comment_count", comment_count);
				comment_count+=add_comment_count;
				mData.put("commentNum", ""+comment_count);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("liang_back_Exception", e.toString());
			}

			allDatas.set(scanPosition, mData);
			adapter.notifyDataSetChanged();

		}

		if (requestCode == 3 && resultCode == 4) { //从StockComment返回

			HashMap<String, Object> mData = allDatas.get(scanPosition);
			try {
				scan_success=data.getBooleanExtra("scan_success", false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (scan_success) {// 浏览成功
				int new_scan_num = Integer.parseInt(mData.get("readNum")
						.toString()) + 1;
				mData.put("readNum", "" + new_scan_num);
				scan_success = false;
			}

			try {
				int comment_count=Integer.parseInt(mData.get("commentNum").toString());
				int add_comment_count=data.getIntExtra("add_comment_count", comment_count);
				comment_count+=add_comment_count;
				mData.put("commentNum", ""+comment_count);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("liang_back_Exception", e.toString());
			}

			allDatas.set(scanPosition, mData);
			adapter.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {

		MyApplication.getRequestQueue().cancelAll("My_Comment");
		MyApplication.getRequestQueue().cancelAll("sendReadnum");
		MyApplication.getRequestQueue().cancelAll("dianzan");

		super.onDestroy();
	}

	class MyCommentAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, Object>> list;

		public MyCommentAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View converView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				converView = inflater.inflate(R.layout.item_personal_comment,
						parent, false);

				viewHolder = new ViewHolder();
				viewHolder.tv_survey_name = (TextView) converView
						.findViewById(R.id.tv_survey_name);
				viewHolder.ci_face = (CircleImageView) converView
						.findViewById(R.id.ci_face);
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_name);
				viewHolder.tv_time = (TextView) converView
						.findViewById(R.id.tv_time);
				viewHolder.tv_detial = (TextView) converView
						.findViewById(R.id.tv_detial);
				viewHolder.iv_more = (ImageView) converView
						.findViewById(R.id.iv_more);
				viewHolder.ll_transpon = (LinearLayout) converView
						.findViewById(R.id.ll_transpon);
				viewHolder.iv_creator_face = (ImageView) converView
						.findViewById(R.id.iv_creator_face);
				viewHolder.tv_creator_name = (TextView) converView
						.findViewById(R.id.tv_creator_name);
				viewHolder.tv_creator_content = (TextView) converView
						.findViewById(R.id.tv_creator_content);
				viewHolder.rl_share = (RelativeLayout) converView
						.findViewById(R.id.rl_share);
				viewHolder.tv_praise_num = (TextView) converView
						.findViewById(R.id.tv_praise_num);
				viewHolder.tv_comment_num = (TextView) converView
						.findViewById(R.id.tv_comment_num);
				viewHolder.tv_scan_num = (TextView) converView
						.findViewById(R.id.tv_scan_num);
				viewHolder.rl_comment = (RelativeLayout) converView
						.findViewById(R.id.rl_comment);
				viewHolder.rl_praise = (RelativeLayout) converView
						.findViewById(R.id.rl_praise);
				viewHolder.iv_praise = (ImageView) converView
						.findViewById(R.id.iv_praise);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			try {
				// Log.d("liang_list_size", ""+list.size());
				// Log.d("liang_survey_Name", list.get(position)
				// .get("surveyName").toString());
				//
				// 调研活动名称
				if (list.get(position).get("topicType").toString()
						.equals("SURVEY")) { // 话题类型为调研话题
					viewHolder.tv_survey_name.setText(list.get(position)
							.get("surveyName").toString());
				} else if (list.get(position).get("topicType").toString()
						.equals("SECURITY")) { // 话题类型为证券话题
					viewHolder.tv_survey_name.setText(list.get(position)
							.get("securityName").toString()
							+ " "
							+ list.get(position).get("securitySymbol")
							.toString());
				} else if (list.get(position).get("topicType").toString()
						.equals("PORTFOLIO")) { // 话题类型为证券话题
					viewHolder.tv_survey_name.setText(list.get(position)
							.get("portfolioName").toString());
				}

				// viewHolder.tv_survey_name.setText(list.get(position)
				// .get("surveyName").toString());

				viewHolder.tv_survey_name
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						//跳转到调研详情
						if (list.get(position).get("topicType").equals("SURVEY")) {
							String uuid = list.get(position)
									.get("surveyUuid").toString();
							Intent intent = new Intent(
									PersonalCommentActivity.this,
									DetailSurveyActivity.class);
							Bundle bundle = new Bundle();

							bundle.putString("uuid", uuid);
							intent.putExtras(bundle);
							PersonalCommentActivity.this
							.startActivity(intent);
						}else{
							//跳转到个股详情
							ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("symbol", list.get(position).get("securitySymbol").toString());
							map.put("shortname", list.get(position).get("securityName").toString());
							map.put("securityType", list.get(position).get("securityType").toString());
							stocks.add(map);
							Intent intent = new Intent(getApplicationContext(), MyStockDetailsActivity.class);
							intent.putExtra("stocks", stocks);
							intent.putExtra("position", 0);
							startActivity(intent);
						}
					}
				});
				
				final String contents=list.get(position).get("creatorName").toString()+":\n"
											+list.get(position).get("content").toString();
				viewHolder.rl_share.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showSharePopupWindow(contents);
					}
				});

				// 头像
				bitmapUtils.display(viewHolder.ci_face,
						list.get(position).get("creatorLogoUrl").toString());
				viewHolder.ci_face.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (allDatas
								.get(position)
								.get("creatorUuid")
								.toString()
								.equals(RsSharedUtil.getString(
										PersonalCommentActivity.this,
										AppConfig.UUID))) {
							Intent intent = new Intent();
							intent.setClass(PersonalCommentActivity.this,
									MyProfileActivity.class);
							startActivity(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid",
									list.get(position).get("creatorUuid")
									.toString());
							bundle.putString("userName", list.get(position)
									.get("creatorName").toString());
							Intent intent = new Intent();
							intent.setClass(PersonalCommentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}

					}
				});
				// 评论者的昵称
				viewHolder.tv_name.setText(list.get(position)
						.get("creatorName").toString());
				viewHolder.tv_name.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (allDatas
								.get(position)
								.get("creatorUuid")
								.toString()
								.equals(RsSharedUtil.getString(
										PersonalCommentActivity.this,
										AppConfig.UUID))) {
							Intent intent = new Intent();
							intent.setClass(PersonalCommentActivity.this,
									MyProfileActivity.class);
							startActivity(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid",
									list.get(position).get("creatorUuid")
									.toString());
							bundle.putString("userName", list.get(position)
									.get("creatorName").toString());
							Intent intent = new Intent();
							intent.setClass(PersonalCommentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				});

				// 转发或复制
				viewHolder.iv_more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						initMenu(getApplicationContext(),
								R.layout.item_comment_my_popup, position);
					}
				});

				// 时间
				String time_str = list.get(position).get("creationTime")
						.toString();
				Long time_long = Long.parseLong(time_str);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yy-MM-dd HH:mm");
				String time = simpleDateFormat.format(new Date(time_long));
				viewHolder.tv_time.setText(time);

				// 评论内容
				viewHolder.tv_detial.setText(list.get(position).get("content")
						.toString());

				// 是否有转发内容
				if (list.get(position).get("refTopic").toString()
						.equals("null")) {
					viewHolder.ll_transpon.setVisibility(View.GONE);
				} else {
					final JSONObject refTopic;
					try {
						refTopic = new JSONObject(list.get(position)
								.get("refTopic").toString());
						// 转发的名字
						viewHolder.tv_creator_name.setText(refTopic.get(
								"creatorName").toString()
								+ ":");
						// 转发的内容
						viewHolder.tv_creator_content.setText(refTopic.get(
								"content").toString());

						JSONArray mediasArray2;
						try {

							mediasArray2 = new JSONArray(refTopic.getString(
									"medias").toString());
							final String urls[] = new String[mediasArray2
							                                 .length()];
							// 取第一张图片
							for (int i = 0; i < mediasArray2.length(); i++) {
								JSONObject jsonObject = mediasArray2
										.getJSONObject(i);
								urls[i] = jsonObject.getString("url");
							}
							if (urls != null && urls.length > 0) {
								viewHolder.iv_creator_face
								.setVisibility(View.VISIBLE);
								ImageLoader.getInstance().displayImage(urls[0],
										viewHolder.iv_creator_face);
							} else {
								viewHolder.iv_creator_face
								.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						viewHolder.ll_transpon.setVisibility(View.VISIBLE);
						// 转发内容的头像
						bitmapUtils.display(viewHolder.iv_creator_face,
								refTopic.get("creatorLogoUrl").toString());

						//二级评论跳转
						viewHolder.ll_transpon
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								try {
									if (list.get(position).get("surveyUuid")!=null) {
										Bundle bundle = new Bundle();
										bundle.putString(
												"surveyUuid",
												list.get(position)
												.get("surveyUuid")
												.toString());
										bundle.putString(
												"creatorUuid",
												refTopic.getString(
														"creatorUuid")
														.toString());
										bundle.putString(
												"followed",
												refTopic.getString("followed"));
										bundle.putString(
												"creatorName",
												refTopic.getString("creatorName"));
										bundle.putString(
												"creationTime",
												refTopic.getString("creationTime"));
										bundle.putString(
												"creatorLogoUrl",
												refTopic.getString("creatorLogoUrl"));
										bundle.putString(
												"content",
												refTopic.getString("content"));
										bundle.putString(
												"likeNum",
												refTopic.getString("likeNum"));
										bundle.putString(
												"commentNum",
												refTopic.getString("commentNum"));
										bundle.putString(
												"readNum",
												refTopic.getString("readNum"));
										bundle.putString(
												"transpondNum",
												refTopic.getString("transpondNum"));
										bundle.putString(
												"topicUuid",
												refTopic.getString("topicUuid"));
										bundle.putString("liked",
												refTopic.getString("liked"));
										bundle.putString(
												"refTopic",
												refTopic.getString("refTopic"));
										bundle.putString("medias", refTopic
												.getString("medias"));
										bundle.putString("createByMe", "false");
										Log.w("milk1","false");
										Intent intent = new Intent(
												getApplicationContext(),
												SingleCommentActivity.class);
										intent.putExtras(bundle);
										startActivity(intent);
										new AsyncSendReadNum().execute(refTopic
												.getString("topicUuid"));
									}else {
										Bundle bundle = new Bundle();
										bundle.putString("creatorLogoUrl",
												refTopic.get("creatorLogoUrl").toString());
										bundle.putString("topicUuid", refTopic.get("topicUuid")
												.toString());
										bundle.putString("followed", refTopic.get("followed")
												.toString());
										bundle.putString("content", refTopic.get("content")
												.toString());
										bundle.putString("creatorName", refTopic.get("creatorName")
												.toString());
										bundle.putString("creatorUuid", refTopic.get("creatorUuid")
												.toString());
										bundle.putString("creationTime", refTopic.get("creationTime")
												.toString());
										bundle.putString("securitySymbol",
												list.get(position).get("securitySymbol").toString());
										bundle.putString("securityName", list.get(position).get("securityName")
												.toString());
										bundle.putString("likeNum", refTopic.get("likeNum")
												.toString());
										bundle.putString("readNum", refTopic.get("readNum")
												.toString());
										bundle.putString("transpondNum", refTopic.get("transpondNum")
												.toString());
										bundle.putString("liked", refTopic.get("liked").toString());
										bundle.putString("refTopic", refTopic.get("refTopic")
												.toString());
										bundle.putString("commentNum", refTopic.get("commentNum")
												.toString());
										try {
											bundle.putString("medias", refTopic.get("medias")
													.toString());
										} catch (Exception e) {
											// TODO Auto-generated catch block
											Log.d("liang", e.toString());
											e.printStackTrace();
										}

										//沪深个评评论
										Intent intent = new Intent(getApplicationContext(), StockCommentActivity.class);
										intent.putExtras(bundle);
										startActivity(intent);
									}
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						Log.d("liang_Exception_getView()", e1.toString());
						e1.printStackTrace();
					}
				}

				// 点赞数目
				viewHolder.tv_praise_num.setText(list.get(position)
						.get("likeNum").toString());

				// 评论数目
				viewHolder.tv_comment_num.setText(list.get(position)
						.get("commentNum").toString());

				// 阅读数目
				viewHolder.tv_scan_num.setText(list.get(position)
						.get("readNum").toString());

				// 评论
				viewHolder.rl_comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						if (list.get(position).get("topicType").toString()
								.equals("SURVEY")) { // 话题类型为调研话题
							gotoSurvey(position);
						} else if (list.get(position).get("topicType")
								.toString().equals("SECURITY")) { // 话题类型为证券话题
							sendBundle(position);
						}

					}
				});

				// 点赞
				viewHolder.rl_praise.setOnClickListener(new DianZanListener(
						list.get(position).get("topicUuid").toString(),
						viewHolder.iv_praise, viewHolder.tv_praise_num));
			} catch (Exception e) {
				Log.d("liang_Exception_GetView", position + " " + e.toString());
				e.printStackTrace();
			}

			return converView;
		}

		class ViewHolder {
			TextView tv_survey_name;
			CircleImageView ci_face;
			TextView tv_name;
			TextView tv_time;
			TextView tv_detial;
			LinearLayout ll_transpon;
			ImageView iv_more;
			ImageView iv_creator_face;
			TextView tv_creator_name;
			TextView tv_creator_content;
			RelativeLayout rl_share;
			TextView tv_praise_num;
			TextView tv_comment_num;
			TextView tv_scan_num;
			RelativeLayout rl_comment;
			RelativeLayout rl_praise;
			ImageView iv_praise;
		}

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
		popupWindow.showAtLocation(rl_parent, Gravity.BOTTOM, 0, 0);
		v_bg.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				v_bg.setAlpha(0.0f);
			}
		});
		TextView tv_transmit = (TextView) contentView
				.findViewById(R.id.tv_transmit);
		TextView tv_copy = (TextView) contentView.findViewById(R.id.tv_copy);

		TextView tv_cancel = (TextView) contentView
				.findViewById(R.id.tv_cancel);
		tv_transmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//先判断是调研评论还是股票评论
				Bundle bundle = new Bundle();
				if (allDatas.get(position).get("topicType").equals("SURVEY")) {
					bundle.putString("surveyUuid",
							allDatas.get(position).get("surveyUuid").toString());
				}else {
					// medias标志，传过去的时候便不用处理medias;
					bundle.putString("mediasFlag", "flag");
					bundle.putString("securitySymbol", allDatas.get(position).get("securitySymbol").toString());
				}
				bundle.putString("refUuid",
						allDatas.get(position).get("topicUuid").toString());
				bundle.putString("creatorName",
						allDatas.get(position).get("creatorName").toString());
				bundle.putString("medias", allDatas.get(position).get("medias")
						.toString());
				bundle.putString("content",
						allDatas.get(position).get("content").toString());

				Intent intent = new Intent(PersonalCommentActivity.this,
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
				String content = allDatas.get(position).get("content").toString();

				ClipboardManager copy = (ClipboardManager) PersonalCommentActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
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

	// share 一键分享
	private void showSharePopupWindow(String content) {
		v_bg.setAlpha(0.7f);
		popupWindow = new ShareUtils(PersonalCommentActivity.this,
				rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				v_bg.setAlpha(0.0f);

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
		String url = AppConfig.URL_TOPIC + "addReadNum.json?access_token="
				+ RsSharedUtil.getString(this, "access_token") + "&topicUuid="
				+ topicUUID;
		Log.d("liang_scan_url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {

				scan_success = true;
				// Log.d("liang_scan_response", ""+scan_success);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("liang_desctiption_scan",
							jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("liang_Exception_scan", e.toString());
					e.printStackTrace();
				}
			}
		}

				);

		stringRequest.setTag("sendReadnum");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	class DianZanListener implements OnClickListener {

		String topicUuid;
		ImageView iv_praise;
		TextView tv_praise_num;

		public DianZanListener(String topicUuid, ImageView iv_praise,
				TextView tv_praise_num) {
			this.topicUuid = topicUuid;
			this.iv_praise = iv_praise;
			this.tv_praise_num = tv_praise_num;
		}

		@Override
		public void onClick(View arg0) {
			String access_token = RsSharedUtil.getString(
					PersonalCommentActivity.this, AppConfig.ACCESS_TOKEN);
			String url = AppConfig.VERSION_URL + "topic/like.json?topicUuid="
					+ topicUuid + "&access_token=" + access_token;

			StringRequest stringRequest = new StringRequest(Request.Method.GET,
					url, null, new Response.Listener<String>() {

				@Override
				public void onResponse(String response) {
					iv_praise
					.setImageResource(R.drawable.btn_dianzan_selected_cl);
					int praise_num = Integer.parseInt(tv_praise_num
							.getText().toString());
					tv_praise_num.setText("" + praise_num);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					try {
						JSONObject jsonObject = new JSONObject(error
								.data());
						String failed = jsonObject
								.getString("description");
						// Toast.makeText(PersonalCommentActivity.this,
						// failed, 1).show();
						showInternetDialog(failed);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("liang_error2", e.toString());
					}
				}
			});

			stringRequest.setTag("dianzan");
			MyApplication.getRequestQueue().add(stringRequest);

		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh1.postDelayed(new Runnable() {

			@Override
			public void run() {
				init(++pageadd, pageSize, FOOT);
				refresh1.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		pageadd = 0;
		refresh1.postDelayed(new Runnable() {

			@Override
			public void run() {
				init(pageadd, pageSize, HEAD);
				refresh1.onHeaderRefreshComplete();
			}
		}, 2000);

	}

	/**
	 * 跳转到调研活动的个评
	 * 
	 * @param position
	 */
	private void gotoSurvey(final int position) {
		scanPosition = position;

		Bundle bundle = new Bundle();
		bundle.putString("surveyUuid", allDatas.get(position).get("surveyUuid")
				.toString());
		bundle.putString("creatorName",
				allDatas.get(position).get("creatorName").toString());
		bundle.putString("creationTime",
				allDatas.get(position).get("creationTime").toString());
		bundle.putString("creatorLogoUrl",
				allDatas.get(position).get("creatorLogoUrl").toString());
		bundle.putString("content", allDatas.get(position).get("content")
				.toString());
		bundle.putString("likeNum", allDatas.get(position).get("likeNum")
				.toString());
		bundle.putString("commentNum", allDatas.get(position).get("commentNum")
				.toString());
		bundle.putString("readNum", allDatas.get(position).get("readNum")
				.toString());
		bundle.putString("transpondNum",
				allDatas.get(position).get("transpondNum").toString());
		bundle.putString("topicUuid", allDatas.get(position).get("topicUuid")
				.toString());
		bundle.putString("liked", allDatas.get(position).get("liked")
				.toString());
		bundle.putString("refTopic", allDatas.get(position).get("refTopic")
				.toString());
		bundle.putString("medias", allDatas.get(position).get("medias")
				.toString());
		Intent intent = new Intent(this, SingleCommentActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 3);

		new AsyncSendReadNum().execute(allDatas.get(position).get("topicUuid")
				.toString());
	}

	// 把数据传到StockCommentActivity
	private void sendBundle(int position) {

		scanPosition = position;

		Bundle bundle = new Bundle();
		bundle.putString("creatorUuid",
				allDatas.get(position).get("creatorUuid").toString());
		bundle.putString("creatorName",
				allDatas.get(position).get("creatorName").toString());
		bundle.putString("creationTime",
				allDatas.get(position).get("creationTime").toString());
		bundle.putString("creatorLogoUrl",
				allDatas.get(position).get("creatorLogoUrl").toString());
		bundle.putString("securitySymbol",
				allDatas.get(position).get("securitySymbol").toString());
		bundle.putString("securityName",
				allDatas.get(position).get("securityName").toString());
		bundle.putString("content", allDatas.get(position).get("content")
				.toString());
		bundle.putString("likeNum", allDatas.get(position).get("likeNum")
				.toString());
		bundle.putString("commentNum", allDatas.get(position).get("commentNum")
				.toString());
		bundle.putString("readNum", allDatas.get(position).get("readNum")
				.toString());
		bundle.putString("transpondNum",
				allDatas.get(position).get("transpondNum").toString());
		bundle.putString("topicUuid", allDatas.get(position).get("topicUuid")
				.toString());
		bundle.putString("liked", allDatas.get(position).get("liked")
				.toString());
		bundle.putString("refTopic", allDatas.get(position).get("refTopic")
				.toString());
		bundle.putString("followed", allDatas.get(position).get("followed")
				.toString());
		try {
			bundle.putString("medias", allDatas.get(position).get("mediaUrls")
					.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("liang", e.toString());
			e.printStackTrace();
		}

		Intent intent = new Intent(this, StockCommentActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 3);

		//		new AsyncSendReadNum().execute(allDatas.get(position).get("topicUuid")
		//				.toString());

	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private AlertDialog internertDialog = null;

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

	}

	/**
	 * 点赞后显示的信息
	 * 
	 * @param msg
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
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 1500);
			}
		}).start();

	}

}
