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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.stock.StockCommentActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.activity.survey.ReviewDetailsActivity;
import com.example.shareholders.activity.survey.SingleCommentActivity;
import com.example.shareholders.activity.survey.TranspondActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_my_friend_moment)
public class MyFriendMomentActivity extends Activity implements
OnHeaderRefreshListener, OnFooterRefreshListener {

/*	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnLoading(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.build();*/
	
	private BitmapUtils bitmapUtils=null;
	@ViewInject(R.id.rl)
	private RelativeLayout rl_parent;

	@ViewInject(R.id.lv_my_friend_moment)
	private ListView lv_my_friend_momentListView;

	@ViewInject(R.id.pull_to_refresh)
	private PullToRefreshView pull_to_refresh;

	// 无股友动态时候的提示
	@ViewInject(R.id.ll_wu_moment)
	private LinearLayout ll_wu_moment;

	@ViewInject(R.id.background)
	private RelativeLayout background;

	// 一开始为15条数据
	private final static int FIRST_PAGESIZE = 15;
	// 上拉加载，每次5条
	private final static int ADD_PAGESIZE = 5;

	private int pageSize = 0;
	private int currentPage = 0;

	private static final int HEAD = 0;
	private static final int FOOT = 1;

	private int scanPosition = 0;// 标记浏览的是哪一条数据
	private boolean scan_success = false;// 标记是否浏览成功

	private ArrayList<HashMap<String, Object>> all_list = new ArrayList<HashMap<String, Object>>();
	private CommentAdapter adapter = null;

	private RequestQueue volleyRequestQueue;

	private ShareUtils popupWindow;
	
	private LoadingDialog loadingDialog;

	/**
	 * 信息提示
	 */

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
//				if (internertDialog != null && internertDialog.isShowing()) {
//					internertDialog.dismiss();
//				}
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
		
		initView();
	}

	private void initView() {
		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();
		volleyRequestQueue = Volley
				.newRequestQueue(MyFriendMomentActivity.this);

		pull_to_refresh.setOnHeaderRefreshListener(this);
		pull_to_refresh.setOnFooterRefreshListener(this);

		adapter = new CommentAdapter(this, all_list);
		lv_my_friend_momentListView.setAdapter(adapter);
	/*	lv_my_friend_momentListView.setOnScrollListener(new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			// TODO Auto-generated method stub
			bitmapUtils.resume();
		}
		
		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			bitmapUtils.pause();
		}
		});*/


		pageSize = FIRST_PAGESIZE;
		initData(HEAD, true);
	}
	

	private void initData(final int type, final boolean first) {
		String url = AppConfig.VERSION_URL
				+ "topic/list/myConcernedUser.json?access_token=";
		url += RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN);
		url = url + "&pageSize=" + pageSize + "&pageIndex=" + currentPage;
		// Log.d("liang_current_page", currentPage + "");
		//		 Log.d("liang_friend_url", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("liang_response_friends", response.toString());
				if (response.equals("")) {
					Log.d("liang_response_no_content", "no_content");
					if (currentPage > 0) {
						currentPage--;
					}
					if (currentPage==0) {
						ll_wu_moment.setVisibility(View.VISIBLE);
						loadingDialog.dismissDialog();
					}
				} 
				else {

					try {
						JSONArray jsonArray = new JSONArray(response);
						final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
						HashMap<String, Object> data = null;
						Iterator<String> iterator = null;

						for (int i = 0; i < jsonArray.length(); i++) {
							data = new HashMap<String, Object>();
							iterator = jsonArray.getJSONObject(i)
									.keys();
							while (iterator.hasNext()) {
								String key = iterator.next();
								data.put(key, jsonArray
										.getJSONObject(i).get(key)
										.toString());
							}
							datas.add(data);
						}

						if (type == HEAD) {
							all_list.clear();
							all_list.addAll(datas);
						} else {
							all_list.addAll(datas);
						}

						if (first) {// 如果是第一次获取数据，获取15
							// Log.d("liang_datas_size", datas.size() +
							// "");
							currentPage = (datas.size() - 1)
									/ ADD_PAGESIZE;
							pageSize = ADD_PAGESIZE;
							// Log.d("liang_firstpage", currentPage +
							// "");
						}

						// adapter = new CommentAdapter(
						// getApplicationContext(), all_list);

						adapter.notifyDataSetChanged();
						// lv_my_friend_momentListView.setAdapter(adapter);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("liang_exception", e.toString());
					}
				}

				if (all_list.size() == 0) {
					ll_wu_moment.setVisibility(View.VISIBLE);
				} else {
					ll_wu_moment.setVisibility(View.GONE);
				}
				
				loadingDialog.dismissDialog();

//				Message msg = new Message();
//				msg.what = 1;
//				mHandler.sendMessage(msg);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("liang_description_friends",
							jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("liang_Exception", e.toString());
					e.printStackTrace();
				}

				if (all_list.size() == 0) {
					ll_wu_moment.setVisibility(View.VISIBLE);
				} else {
					ll_wu_moment.setVisibility(View.GONE);
				}
//
//				Message msg = new Message();
//				msg.what = 1;
//				mHandler.sendMessage(msg);

			}
		});
		stringRequest.setTag("MyFriendMomentActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 3 && resultCode == 3) { //从SingleComment返回

			HashMap<String, Object> mData = all_list.get(scanPosition);
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

			all_list.set(scanPosition, mData);
			adapter.notifyDataSetChanged();
		}

		if (requestCode == 3 && resultCode == 4) { //从StockComment返回

			HashMap<String, Object> mData = all_list.get(scanPosition);
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

			all_list.set(scanPosition, mData);
			adapter.notifyDataSetChanged();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("MyFriendMomentActivity");
		super.onDestroy();
	}

	@OnClick({ R.id.rl_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}


	class CommentAdapter extends BaseAdapter {

		// private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		CommentAdapter(Context context, ArrayList<HashMap<String, Object>> datas) {
			this.context = context;
			this.list = datas;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View converView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (converView == null) {

				converView = mInflater.inflate(
						R.layout.item_comment_collection, parent, false);

			}

			TextView tv_survey_name = AbViewHolder.get(converView,
					R.id.tv_survey_name);
			TextView tv_name = AbViewHolder.get(converView, R.id.tv_name);
			TextView tv_time = AbViewHolder.get(converView, R.id.tv_time);
			TextView tv_detail = AbViewHolder.get(converView, R.id.tv_detail);
			// TextView tv_share_num = AbViewHolder.get(converView,
			// R.id.tv_share_num);
			final TextView tv_praise_num = AbViewHolder.get(converView,
					R.id.tv_praise_num);
			TextView tv_comment_num = AbViewHolder.get(converView,
					R.id.tv_comment_num);
			TextView tv_scan_num = AbViewHolder.get(converView,
					R.id.tv_scan_num);
			CircleImageView ci_face = AbViewHolder
					.get(converView, R.id.ci_face);


			
			/**
			 * 更多
			 */
			ImageView iv_more = AbViewHolder.get(converView, R.id.iv_more);
			iv_more.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					// Log.d("liang_more", "click");

					// 当用户选择menu中的转发功能是需要使用surveyUuid
					//						String surveyUuid = list.get(position)
					//								.get("surveyUuid").toString();
					// 判断转发的话题是否是原创话题
					String original = list.get(position).get("original")
							.toString();

					initMenu(MyFriendMomentActivity.this,
							R.layout.item_comment_more_popup, position);

				}
			});

			ImageView iv_comment = AbViewHolder
					.get(converView, R.id.iv_comment);
			final ImageView iv_praise = AbViewHolder.get(converView,
					R.id.iv_praise);
			ImageView iv_share = AbViewHolder.get(converView, R.id.iv_share);

			try {

				if (list.get(position).get("topicType").toString()
						.equals("SURVEY")) { // 话题类型为调研话题
					tv_survey_name.setText(list.get(position).get("surveyName")
							.toString());
				} else if (list.get(position).get("topicType").toString()
						.equals("SECURITY")) { // 话题类型为证券话题
					tv_survey_name.setText(list.get(position)
							.get("securityName").toString()
							+ " "
							+ list.get(position).get("securitySymbol")
							.toString());
				} else if (list.get(position).get("topicType").toString()
						.equals("PORTFOLIO")) { // 话题类型为证券话题
					tv_survey_name.setText(list.get(position)
							.get("portfolioName").toString());
				}
				//活动或者个股的跳转
				tv_survey_name.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (list.get(position).get("topicType").toString()
								.equals("SURVEY")) { // 话题类型为调研话题
							startActivity(new Intent(getApplicationContext(), DetailSurveyActivity.class).putExtra("uuid", list.get(position)
									.get("surveyUuid").toString()));
						}else {
							//类型为个股
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

				//				tv_time.setText(""
				//						+ list.get(position).get("creationTime").toString());

				long time_long = Long.parseLong(list.get(position)
						.get("creationTime").toString());
				SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");
				String date = format.format(new Date(time_long));

				tv_time.setText( date);

				tv_detail.setText(list.get(position).get("content").toString());
				// tv_share_num.setText(list.get(position).get("transpondNum")
				// .toString());
				tv_praise_num.setText(list.get(position).get("likeNum")
						.toString());
				tv_comment_num.setText(list.get(position).get("commentNum")
						.toString());
				tv_scan_num.setText(list.get(position).get("readNum")
						.toString());

				tv_name.setText(list.get(position).get("creatorName")
						.toString());

				tv_name.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (list.get(position)
								.get("creatorUuid")
								.toString()
								.equals(RsSharedUtil.getString(
										MyFriendMomentActivity.this,
										AppConfig.UUID))) {
							Intent intent = new Intent();
							intent.setClass(MyFriendMomentActivity.this,
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
							intent.setClass(MyFriendMomentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);

							startActivity(intent);
						}
					}
				});

				/**
				 * 点击头像跳转到个人主页
				 */
				
				bitmapUtils.display(ci_face,list.get(position)
						.get("creatorLogoUrl").toString());
				/*ImageAware imageAware = new ImageViewAware(ci_face, false);
				ImageLoader.getInstance().displayImage(list.get(position)
						.get("creatorLogoUrl").toString(), imageAware, defaultOptions);*/
				/*ImageLoader.getInstance().displayImage(list.get(position)
						.get("creatorLogoUrl").toString(),
						ci_face, defaultOptions);*/
				/*ImageLoader.getInstance().displayImage(
						list.get(position).get("creatorLogoUrl").toString(),
						ci_face);*/
				ci_face.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (list.get(position)
								.get("creatorUuid")
								.toString()
								.equals(RsSharedUtil.getString(
										MyFriendMomentActivity.this,
										AppConfig.UUID))) {
							Intent intent = new Intent();
							intent.setClass(MyFriendMomentActivity.this,
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
							intent.setClass(MyFriendMomentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("liang_Exception_getView",
						"" + position + " " + e.toString());
			}
			Boolean liked = Boolean.parseBoolean(list.get(position)
					.get("liked").toString());
			if (liked) {
				iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
			} else {
				iv_praise.setImageResource(R.drawable.btn_dianzan_normal_cl);
			}
			// 分享按钮
			// iv_share.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			//
			// }
			// });

			// 分享
			RelativeLayout rl_share = AbViewHolder.get(converView,
					R.id.rl_share);

			//分享内容
			final String contentString=list.get(position).get("creatorName")+":\n"
			+list.get(position).get("content").toString()
					
					.toString();
			rl_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// Log.d("liang_share", "share");
					showShare(contentString);
				}
			});

			// 点赞按钮
			iv_praise.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try {
						iv_praise.setClickable(false);
						int praiseNum = Integer.parseInt(tv_praise_num
								.getText().toString());
						praiseNum++;
						tv_praise_num.setText("" + praiseNum);
						iv_praise
						.setImageResource(R.drawable.btn_dianzan_selected_cl);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String url = AppConfig.VERSION_URL
							+ "topic/like.json?topicUuid="
							+ list.get(position).get("topicUuid").toString()
							.trim();
					url += "&access_token=";
					url += RsSharedUtil.getString(context,
							AppConfig.ACCESS_TOKEN);
					// Log.d("liang_dianzan_url", url);
					StringRequest stringRequest = new StringRequest(url, null,
							new Listener<String>() {

						@Override
						public void onResponse(String response) {
							// TODO Auto-generated method stub
							// Log.d("liang_dianzan_response",
							// "success");
							CommentAdapter.this.list.get(position).put(
									"liked", true);
							iv_praise.setClickable(true);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {

							// Log.d("liang_dianzan_error", "failed");
							// TODO Auto-generated method stub
							try {
								Boolean liked = Boolean
										.parseBoolean(list
												.get(position)
												.get("liked")
												.toString());
								int praiseNum = Integer
										.parseInt(tv_praise_num
												.getText().toString());
								praiseNum--;
								tv_praise_num.setText("" + praiseNum);
								if (!liked) {
									iv_praise
									.setImageResource(R.drawable.btn_dianzan_normal_cl);
								}

								JSONObject jsonObject = new JSONObject(
										error.data());
								String description = jsonObject
										.getString("description");

								// Log.d("liang_dianzan_description",
								// description);

								iv_praise.setClickable(true);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								// Log.d("liang_exception_dianzan",
								// e.toString());

								e.printStackTrace();
							}
						}
					});

					volleyRequestQueue.add(stringRequest);

				}

			});

			// 评论
			// iv_comment.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			//
			// }
			// });

			RelativeLayout rl_comment = AbViewHolder.get(converView,
					R.id.rl_comment);

			rl_comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (list.get(position).get("topicType").toString()
							.equals("SURVEY")) { // 话题类型为调研话题
						gotoSurvey(position, tv_praise_num);
					} else if (list.get(position).get("topicType").toString()
							.equals("SECURITY")) { // 话题类型为证券话题
						sendBundle(position);
					}
				}
			});

			return converView;
		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				currentPage++;
				pageSize = ADD_PAGESIZE;
				initData(FOOT, false);

				pull_to_refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				currentPage = 0;
				pageSize = FIRST_PAGESIZE;
				initData(HEAD, true);
				pull_to_refresh.onHeaderRefreshComplete();
			}
		}, 2000);
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
		// 转发
		tv_transmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//先判断是调研评论还是股票评论
				Bundle bundle = new Bundle();
				if (all_list.get(position).get("topicType").equals("SURVEY")) {
					bundle.putString("surveyUuid",
							all_list.get(position).get("surveyUuid").toString());
				}else {
					// medias标志，传过去的时候便不用处理medias;
					bundle.putString("mediasFlag", "flag");
					bundle.putString("securitySymbol", all_list.get(position).get("securitySymbol").toString());
				}
				bundle.putString("refUuid",
						all_list.get(position).get("topicUuid").toString());
				bundle.putString("creatorName",
						all_list.get(position).get("creatorName").toString());
				bundle.putString("medias", "null");
				bundle.putString("content",
						all_list.get(position).get("content").toString());
				bundle.putString("position", "" + position);

				Intent intent = new Intent(MyFriendMomentActivity.this,
						TranspondActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);

				popupWindow.dismiss();
			}
		});
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String content = all_list.get(position).get("content").toString();

				ClipboardManager copy = (ClipboardManager) MyFriendMomentActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
				Toast(context, R.layout.item_toast_popup, background, 1);
				popupWindow.dismiss();
			}
		});
		if (all_list.get(position).get("followed").toString().equals("true"))
			tv_collect.setText("取消收藏");
		if (all_list.get(position).get("followed").toString().equals("false"))
			tv_collect.setText("收藏");

		boolean createdByMe = Boolean.parseBoolean(all_list.get(position)
				.get("createdByMe").toString());
		if (createdByMe) {// 如果是用户自己创建的话题
			tv_collect.setVisibility(View.GONE);
		}

		tv_collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Collect(all_list.get(position).get("topicUuid").toString(),
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
	 * 关注，取消关注
	 * 
	 * @param uuid
	 */
	private void Collect(String uuid, final int position) {
		String url = AppConfig.URL_USER + "topic.json?access_token=";
		url = url
				+ RsSharedUtil.getString(MyFriendMomentActivity.this,
						"access_token") + "&uuid=" + uuid;
		Log.d("collect", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("RRRRRRRRRRRRRRRRRRRRR", response.toString());
				// TODO Auto-generated method stub
				HashMap<String, Object> hashMap = all_list
						.get(position);
				if (response.equals("true")) {
					Toast(MyFriendMomentActivity.this,
							R.layout.item_toast_popup, background, 2);
					hashMap.put("followed", "true");
					all_list.set(position, hashMap);
				}
				if (response.equals("false")) {
					Toast(MyFriendMomentActivity.this,
							R.layout.item_toast_popup, background, 4);
					hashMap.put("followed", "false");
					all_list.set(position, hashMap);
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
	 * 发起阅读动作
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void sendReadnum(String topicUUID) {
		String url = AppConfig.URL_TOPIC
				+ "addReadNum.json?access_token="
				+ RsSharedUtil.getString(MyFriendMomentActivity.this,
						"access_token") + "&topicUuid=" + topicUUID;
		// Log.d("liang_scan", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				scan_success = true;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("liang_description_scan",
							jsonObject.getString("description"));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("liang_Exception_scan", e.toString());
				}
			}
		}

				);
		volleyRequestQueue.add(stringRequest);
	}


	// 分享
	private void showShare(String content) {
		background.setAlpha(0.7f);
		popupWindow = new ShareUtils(this, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				background.setAlpha(0.0f);

			}
		});
	}

	/**
	 * 跳转到调研活动的个评
	 * 
	 * @param position
	 * @param tv_praise_num
	 */
	private void gotoSurvey(final int position, final TextView tv_praise_num) {
		scanPosition = position;

		Bundle bundle = new Bundle();
		bundle.putString("surveyUuid", all_list.get(position).get("surveyUuid")
				.toString());

		bundle.putString("creatorUuid",
				all_list.get(position).get("creatorUuid").toString());
		bundle.putString("creatorName",
				all_list.get(position).get("creatorName").toString());
		bundle.putString("creationTime",
				all_list.get(position).get("creationTime").toString());
		bundle.putString("creatorLogoUrl",
				all_list.get(position).get("creatorLogoUrl").toString());
		bundle.putString("content", all_list.get(position).get("content")
				.toString());
		bundle.putString("likeNum", tv_praise_num.getText().toString());
		bundle.putString("commentNum", all_list.get(position).get("commentNum")
				.toString());
		bundle.putString("readNum", all_list.get(position).get("readNum")
				.toString());
		bundle.putString("transpondNum",
				all_list.get(position).get("transpondNum").toString());
		bundle.putString("topicUuid", all_list.get(position).get("topicUuid")
				.toString());
		bundle.putString("liked", all_list.get(position).get("liked")
				.toString());
		bundle.putString("refTopic", all_list.get(position).get("refTopic")
				.toString());
		bundle.putString("medias", all_list.get(position).get("medias")
				.toString());
		bundle.putString("followed", all_list.get(position).get("followed")
				.toString());
		Intent intent = new Intent(MyFriendMomentActivity.this,
				SingleCommentActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 3);
		new AsyncSendReadNum().execute(all_list.get(position).get("topicUuid")
				.toString());
	}

	// 把数据传到StockCommentActivity
	private void sendBundle(int position) {

		scanPosition = position;

		Bundle bundle = new Bundle();
		bundle.putString("creatorUuid",
				all_list.get(position).get("creatorUuid").toString());
		bundle.putString("creatorName",
				all_list.get(position).get("creatorName").toString());
		bundle.putString("creationTime",
				all_list.get(position).get("creationTime").toString());
		bundle.putString("creatorLogoUrl",
				all_list.get(position).get("creatorLogoUrl").toString());
		bundle.putString("securitySymbol",
				all_list.get(position).get("securitySymbol").toString());
		bundle.putString("securityName",
				all_list.get(position).get("securityName").toString());
		bundle.putString("content", all_list.get(position).get("content")
				.toString());
		bundle.putString("likeNum", all_list.get(position).get("likeNum")
				.toString());
		bundle.putString("commentNum", all_list.get(position).get("commentNum")
				.toString());
		bundle.putString("readNum", all_list.get(position).get("readNum")
				.toString());
		bundle.putString("transpondNum",
				all_list.get(position).get("transpondNum").toString());
		bundle.putString("topicUuid", all_list.get(position).get("topicUuid")
				.toString());
		bundle.putString("liked", all_list.get(position).get("liked")
				.toString());
		bundle.putString("refTopic", all_list.get(position).get("refTopic")
				.toString());
		bundle.putString("followed", all_list.get(position).get("followed")
				.toString());
		try {
			bundle.putString("medias", all_list.get(position).get("mediaUrls")
					.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("liang", e.toString());
			e.printStackTrace();
		}

		Intent intent = new Intent(this, StockCommentActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 3);

		//		new AsyncSendReadNum().execute(all_list.get(position).get("topicUuid")
		//				.toString());

	}

}
