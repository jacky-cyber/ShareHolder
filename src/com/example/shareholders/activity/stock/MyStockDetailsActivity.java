package com.example.shareholders.activity.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.PublishDiscussActivity;
import com.example.shareholders.adapter.AnnouncementAdapter;
import com.example.shareholders.adapter.EditMyselfCommentsAdapter;
import com.example.shareholders.adapter.MsgAdapter;
import com.example.shareholders.adapter.NewsAdapter;
import com.example.shareholders.adapter.ResearchReportAdapter;
import com.example.shareholders.adapter.SurveyAdapter;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.KeyboardListenRelativeLayout;
import com.example.shareholders.common.KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.common.TopFloatScrollView;
import com.example.shareholders.common.TopFloatScrollView.OnScrollListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.ChatMsgEntity;
import com.example.shareholders.fragment.Fragment_Chart;
import com.example.shareholders.fragment.Fragment_Chart.OnFinishChartListener;
import com.example.shareholders.fragment.Fragment_Chat;
import com.example.shareholders.fragment.Fragment_Chat.OnFinishListener;
import com.example.shareholders.util.DensityUtil;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_my_stock_details)
public class MyStockDetailsActivity extends FragmentActivity implements
		OnScrollListener, OnFinishListener, OnFinishChartListener {

	@ViewInject(R.id.sc_topfloat)
	private TopFloatScrollView sc_topfloat;
	@ViewInject(R.id.vp)
	private MyViewPager vp;
	@ViewInject(R.id.lv)
	private MyListView lv;

	// vp指示器
	@ViewInject(R.id.iv_indicator1)
	private CircleImageView iv_indicator1;
	@ViewInject(R.id.iv_indicator2)
	private CircleImageView iv_indicator2;
	@ViewInject(R.id.iv_indicator3)
	private CircleImageView iv_indicator3;
	@ViewInject(R.id.iv_indicator4)
	private CircleImageView iv_indicator4;

	// 标题
	@ViewInject(R.id.tv_stock_name)
	private TextView tv_stock_name;
	@ViewInject(R.id.tv_stock_code)
	private TextView tv_stock_code;

	@ViewInject(R.id.rl_title)
	private RelativeLayout rl_title;
	@ViewInject(R.id.ll_main_param)
	private LinearLayout ll_main_param;
	@ViewInject(R.id.layout_stock_details_param)
	private View layout_stock_details_param;
	@ViewInject(R.id.view_bottom)
	private View view_bottom;

	@ViewInject(R.id.tv_now_price)
	private TextView tv_now_price;

	@ViewInject(R.id.tv_change)
	private TextView tv_change;
	@ViewInject(R.id.tv_change_ratio)
	private TextView tv_change_ratio;
	@ViewInject(R.id.iv_ratio)
	private ImageView iv_ratio;

	@ViewInject(R.id.tv_open_price)
	private TextView tv_open_price;
	@ViewInject(R.id.tv_close_price)
	private TextView tv_close_price;

	@ViewInject(R.id.tv_high_price)
	private TextView tv_high_price;
	@ViewInject(R.id.tv_low_price)
	private TextView tv_low_price;

	@ViewInject(R.id.tv_description1)
	private TextView tv_description1;
	@ViewInject(R.id.tv_description2)
	private TextView tv_description2;
	@ViewInject(R.id.tv_description3)
	private TextView tv_description3;
	@ViewInject(R.id.tv_description4)
	private TextView tv_description4;
	@ViewInject(R.id.tv_description5)
	private TextView tv_description5;
	@ViewInject(R.id.tv_param1)
	private TextView tv_param1;
	@ViewInject(R.id.tv_param2)
	private TextView tv_param2;
	@ViewInject(R.id.tv_param3)
	private TextView tv_param3;
	@ViewInject(R.id.tv_param4)
	private TextView tv_param4;
	@ViewInject(R.id.tv_param5)
	private TextView tv_param5;
	@ViewInject(R.id.iv_concerned)
	private ImageView iv_concerned;

	// 五档
	@ViewInject(R.id.tv_five)
	private TextView tv_five;
	@ViewInject(R.id.layout_five)
	private TableLayout layout_five;
	@ViewInject(R.id.rl_parent)
	private KeyboardListenRelativeLayout rl_parent;
	@ViewInject(R.id.layout_five_buyer1)
	private View layout_five_buyer1;

	@ViewInject(R.id.iv_stock_left)
	private ImageView iv_stock_left;
	@ViewInject(R.id.iv_stock_right)
	private ImageView iv_stock_right;
	@ViewInject(R.id.iv_stock_refresh)
	private ImageView iv_stock_refresh;

	@ViewInject(R.id.iv_more)
	private ImageView iv_more;

	@ViewInject(R.id.iv_more_details)
	private ImageView iv_more_details;

	// tab
	@ViewInject(R.id.layout_tab)
	private LinearLayout layout_tab;
	@ViewInject(R.id.ll_tab)
	private LinearLayout ll_tab;
	@ViewInject(R.id.ll_topfloat)
	private LinearLayout ll_topfloat;

	@ViewInject(R.id.ll_share_collect)
	private LinearLayout ll_share_collect;

	@ViewInject(R.id.ll_chat_param)
	private LinearLayout ll_chat_param;

	// 底部
	@ViewInject(R.id.tv_comment)
	private TextView tv_comment;
	@ViewInject(R.id.tv_share)
	private TextView tv_share;
	@ViewInject(R.id.tv_alert)
	private TextView tv_alert;

	// 聊一聊
	@ViewInject(R.id.ll_chatroom)
	private LinearLayout ll_chatroom;
	@ViewInject(R.id.rl_chatlist)
	private RelativeLayout rl_chatlist;
	@ViewInject(R.id.iv_chat_show)
	private ImageView iv_chat_show;

	// 新闻
	@ViewInject(R.id.rb_price1)
	private RadioButton rb_price1;
	// 公告
	@ViewInject(R.id.rb_price2)
	private RadioButton rb_price2;
	// 调研
	@ViewInject(R.id.rb_price3)
	private RadioButton rb_price3;
	// 研报
	@ViewInject(R.id.rb_price4)
	private RadioButton rb_price4;
	// 评论
	@ViewInject(R.id.rb_price5)
	private RadioButton rb_price5;

	// popWindow的背景
	@ViewInject(R.id.v_bg)
	private View v_bg;

	@ViewInject(R.id.vp_chat)
	private MyViewPager vp_chat;
	private MsgAdapter msgAdapter;
	private ArrayList<ChatMsgEntity> chatMsgEntities = new ArrayList<ChatMsgEntity>();

	boolean isChatShowed = false;
	
	private String share_content="";

	// 图表的
	private ArrayList<Fragment> fragments;
	private boolean isHideFive;
	private int floatLayoutTop;

	final int selectColor = R.color.stock_details_indicator_color;

	String stockName="";
	String symbol="";
	boolean isConcerned;
	ArrayList<HashMap<String, String>> stocks;
	int position;

	ArrayList<HashMap<String, Object>> fivedatas;
	int[] fiveDataIds = { R.id.layout_five_buyer1, R.id.layout_five_buyer2,
			R.id.layout_five_buyer3, R.id.layout_five_buyer4,
			R.id.layout_five_buyer5, R.id.layout_five_seller1,
			R.id.layout_five_seller2, R.id.layout_five_seller3,
			R.id.layout_five_seller4, R.id.layout_five_seller5 };
	String[] fiveDataNames = { "买1", "买2", "买3", "买4", "买5", "卖1", "卖2", "卖3",
			"卖4", "卖5" };

	ArrayList<HashMap<String, String>> surveys;
	ArrayList<HashMap<String, Object>> news;
	ArrayList<HashMap<String, Object>> announcements;
	ArrayList<HashMap<String, Object>> reports;
	ArrayList<HashMap<String, Object>> comments;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	Context context;

	private String groupId = null;

	double ratio;

	double amount;

	double turnoverRate;

	private boolean isFinish1 = false;
	private boolean isFinish2 = false;
	private boolean isFinish3 = false;
	private boolean isFinish4 = false;

	private boolean isStock = true;

	// private SharePopupWindow popupWindow;
	private ShareUtils popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		context = MyStockDetailsActivity.this;
		init();

		// showLineChart(50);
		// showBarChart(50);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sc_topfloat.scrollTo(0, 0);
	}

	@Override
	public void onDestroy() {
		// TODO
		MyApplication.getRequestQueue().cancelAll("MyStockDetailsActivity");
		super.onDestroy();
	}

	/*
	 * 在Activity的生命周期中，onCreate()--onStart()--onResume()都不是窗体Visible的时间点，
	 * 真正的窗体完成初始化可见获取焦点可交互是在onWindowFocusChanged()方法被执行时
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			// floatLayoutTop = ll_chatroom.getBottom(); // 获取浮动Layout的顶部位置
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

			// LayoutParams layoutParams = ll_listview.getLayoutParams();
			// layoutParams.height = displayMetrics.heightPixels;
			// layoutParams.width = LayoutParams.MATCH_PARENT;
			// ll_listview.setLayoutParams(layoutParams);
			// ll_listview.setVisibility(View.VISIBLE);

			LayoutParams layoutParams2 = rl_chatlist.getLayoutParams();
			layoutParams2.height = displayMetrics.heightPixels
					- rl_title.getHeight() - ll_chat_param.getHeight()
					- ll_chatroom.getHeight() - DensityUtil.dip2px(context, 20);
			layoutParams2.width = LayoutParams.MATCH_PARENT;
			rl_chatlist.setLayoutParams(layoutParams2);
		}
	}

	@OnClick({ R.id.title_note, R.id.iv_more, R.id.ll_chatroom, R.id.rb_price1,
			R.id.rb_price2, R.id.rb_price3, R.id.rb_price4, R.id.rb_price5,
			R.id.ll_param, R.id.iv_stock_left,
			R.id.iv_stock_right, R.id.iv_stock_refresh, R.id.title_search,
			R.id.tv_comment, R.id.tv_share, R.id.tv_alert })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.title_search:
			isChatShowed = false;
			rl_chatlist.setVisibility(View.GONE);
			layout_stock_details_param.setVisibility(View.VISIBLE);
			ll_tab.setVisibility(View.VISIBLE);
			ll_share_collect.setVisibility(View.VISIBLE);
			view_bottom.setVisibility(View.VISIBLE);
			lv.setVisibility(View.VISIBLE);
			iv_more.setVisibility(View.VISIBLE);
			iv_chat_show.setImageResource(R.drawable.liaoyiliao_up);
			sc_topfloat.scrollTo(0, 0);
			startActivity(new Intent(context,
					ShareAndFriendsSearchActivity.class));
			break;
		case R.id.iv_more:
			isHideFive = !isHideFive;
			if (isHideFive) {
				tv_five.setVisibility(View.GONE);
				layout_five.setVisibility(View.GONE);
			} else {
				tv_five.setVisibility(View.VISIBLE);
				layout_five.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.ll_chatroom:
			if (RsSharedUtil.getLoginStateAndShow(context)) {
				isChatShowed = !isChatShowed;
				if (isChatShowed) {
					tv_five.setVisibility(View.GONE);
					layout_five.setVisibility(View.GONE);
					layout_stock_details_param.setVisibility(View.GONE);
					ll_tab.setVisibility(View.GONE);
					ll_share_collect.setVisibility(View.GONE);
					view_bottom.setVisibility(View.GONE);
					lv.setVisibility(View.GONE);
					iv_more.setVisibility(View.INVISIBLE);
					sc_topfloat.scrollTo(0, ll_chat_param.getTop());
					rl_chatlist.setVisibility(View.VISIBLE);
					iv_chat_show.setImageResource(R.drawable.liaoyiliao_down);
					getGroupId();
				} else {
					rl_chatlist.setVisibility(View.GONE);
					view_bottom.setVisibility(View.VISIBLE);
					lv.setVisibility(View.VISIBLE);
					ll_tab.setVisibility(View.VISIBLE);
					ll_share_collect.setVisibility(View.VISIBLE);
					layout_stock_details_param.setVisibility(View.VISIBLE);
					iv_more.setVisibility(View.VISIBLE);
					iv_chat_show.setImageResource(R.drawable.liaoyiliao_up);
					sc_topfloat.scrollTo(0, 0);
				}
			}
			break;
		case R.id.ll_param: {
			Intent intent = new Intent(MyStockDetailsActivity.this,
					MyStockDetailsParamActivity.class);
			intent.putExtra("name", stockName);
			intent.putExtra("symbol", symbol);
			intent.putExtra("ifFromCompanyDetails", "false");
			startActivity(intent);
			break;
		}
		case R.id.rb_price1:
			if (news.size() > 0) {
				showNews();
			} else {
				getAllNews(0, 10, FOOT);
			}
			break;
		case R.id.rb_price2:
			if (announcements.size() > 0) {
				showAnnouncements();
			} else {
				showAnnouncements();
				getAllAnnouncements(0, 10, FOOT);
			}
			break;
		case R.id.rb_price3:
			if (surveys.size() > 0) {
				showSurvey();
			} else {
				showSurvey();
				getAllSurveys(0, 10, FOOT);
			}
			break;
		case R.id.rb_price4:
			if (reports.size() > 0) {
				showResearchReports();
			} else {
				showResearchReports();
				getAllReports(0, 10, FOOT);
			}
			break;
		case R.id.rb_price5:
			if (comments.size() > 0) {
				showComments();
			} else {
				showComments();
				getAllComments(0, 10, FOOT);
			}
			break;
		case R.id.iv_stock_left:
			if (position > 0) {
				position--;
				dataRefresh();
			}
			break;
		case R.id.iv_stock_right:
			if (position < stocks.size() - 1) {
				position++;
				dataRefresh();
			}
			break;
		case R.id.iv_stock_refresh:
			dataRefresh();
			break;
		case R.id.tv_comment: {
			if (RsSharedUtil.getLoginStateAndShow(context)) {
				Intent intent = new Intent(context,
						PublishDiscussActivity.class);
				intent.putExtra("securitySymbol", symbol);
				intent.putExtra("securityTopicType", "STOCK");
				startActivity(intent);
			}
			break;
		}
		case R.id.tv_share:
			showShare(share_content);
			break;
		case R.id.tv_alert:
			if (RsSharedUtil.getLoginStateAndShow(context)) {
				addAlert();
			}
			break;
		default:
			break;
		}
	}

	private void init() {

		sc_topfloat.scrollTo(0, 0);

		stocks = (ArrayList<HashMap<String, String>>) getIntent().getExtras()
				.get("stocks");
		position = getIntent().getExtras().getInt("position");
		fivedatas = new ArrayList<HashMap<String, Object>>();

		// 初始化时改变此值即可区分股票与指数
		try {
			if (getIntent().getExtras().getString("securityType")
					.equals("index")) {
				isStock = false;
				;
			} else {
				isStock = true;
			}
		} catch (Exception e) {
			isStock = true;
		}
		if (!isStock) {// 若为非股票详情，即指数详情
			iv_more_details.setVisibility(View.INVISIBLE);
			iv_more.setVisibility(View.INVISIBLE);
			rb_price3.setVisibility(View.GONE);
		}

		isHideFive = true;

		sc_topfloat.setOnScrollListener(this);
		surveys = new ArrayList<HashMap<String, String>>();
		news = new ArrayList<HashMap<String, Object>>();
		announcements = new ArrayList<HashMap<String, Object>>();
		reports = new ArrayList<HashMap<String, Object>>();
		comments = new ArrayList<HashMap<String, Object>>();

		dataRefresh();

		rl_parent
				.setOnKeyboardStateChangedListener(new IOnKeyboardStateChangedListener() {

					@Override
					public void onKeyboardStateChanged(int state) {
						switch (state) {
						case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE:
							sc_topfloat.scrollTo(0, ll_chat_param.getTop());
							break;
						case KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW:
							// sideBar.setVisibility(View.INVISIBLE);
						default:
							break;
						}

					}
				});

	}

	private void loginChatRoom() {
		if (!groupId.isEmpty()) {
			ArrayList<Fragment> chatfragments = new ArrayList<Fragment>();
			chatfragments.add(new Fragment_Chat(groupId));
			vp_chat.setAdapter(new ViewPagerAdapter(
					getSupportFragmentManager(), chatfragments));
			vp_chat.setOffscreenPageLimit(1);
		}
	}

	private void dataRefresh() {
		RotateAnimation animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(500);
		animation.setRepeatCount(-1);
		iv_stock_refresh.startAnimation(animation);
		
		if (position >= 0) {
			stockName = stocks.get(position).get("shortname");
			symbol = stocks.get(position).get("symbol");
			tv_stock_name.setText(stockName);
			tv_stock_code.setText(symbol);
		} else if (position == -1) {
			stockName = stocks.get(0).get("symbolName");
			symbol = stocks.get(0).get("symbol");
			tv_stock_name.setText(stockName);
			tv_stock_code.setText(symbol);
		} else if (position == -2) {
			stockName = stocks.get(0).get("name");
			symbol = stocks.get(0).get("symbol");
			tv_stock_name.setText(stockName);
			tv_stock_code.setText(symbol);
		} else if (position == -3) {
			stockName = stocks.get(0).get("shortName");
			symbol = stocks.get(0).get("symbol");
			tv_stock_name.setText(stockName);
			tv_stock_code.setText(symbol);
		}
		fragments = new ArrayList<Fragment>();
		fragments.add(new Fragment_Chart(0, symbol));
		fragments.add(new Fragment_Chart(1, symbol));
		fragments.add(new Fragment_Chart(2, symbol));
		fragments.add(new Fragment_Chart(3, symbol));

		vp.setNoScroll(false);
		vp.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				fragments));
		vp.setOffscreenPageLimit(4);
		vp.setOnPageChangeListener(new MyPagerChangeListener());

		// 初始化只需要getnews
		getAllNews(0, 10, FOOT);
		getFiveData();
		quotationDetaill();
		getConcerned();
	}

	private void onPage(int num) {
		clearPage();
		switch (num) {
		case 0:
			iv_indicator1.setImageResource(selectColor);
			if (isStock) {
				iv_more.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			iv_indicator2.setImageResource(selectColor);
			iv_more.setVisibility(View.INVISIBLE);
			break;
		case 2:
			iv_indicator3.setImageResource(selectColor);
			iv_more.setVisibility(View.INVISIBLE);
			break;
		case 3:
			iv_indicator4.setImageResource(selectColor);
			iv_more.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
	}

	private void clearPage() {
		iv_indicator1.setImageResource(R.color.white);
		iv_indicator2.setImageResource(R.color.white);
		iv_indicator3.setImageResource(R.color.white);
		iv_indicator4.setImageResource(R.color.white);
	}

	private void showSurvey() {
		SurveyAdapter adapter = new SurveyAdapter(MyStockDetailsActivity.this,
				surveys);
		lv.setAdapter(adapter);
		// 设置item监听
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyStockDetailsActivity.this,
						EditMyselfResearchReportActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("reportId",
						reports.get(position).get("reportId").toString());
				intent.putExtras(bundle);

				startActivity(intent);
			}
		});
	}

	private void showNews() {
		NewsAdapter adapter = new NewsAdapter(MyStockDetailsActivity.this, news);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,
						EditMyselfNewsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("newsid", news.get(position).get("newsid")
						.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void showAnnouncements() {
		AnnouncementAdapter adapter = new AnnouncementAdapter(
				MyStockDetailsActivity.this, announcements);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,
						EditMyselfAnnouncementActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("announcementid", announcements.get(position)
						.get("announcementid").toString());
				intent.putExtras(bundle);

				startActivity(intent);
			}
		});
	}

	private void showResearchReports() {
		ResearchReportAdapter adapter = new ResearchReportAdapter(
				MyStockDetailsActivity.this, reports);
		lv.setAdapter(adapter);
		// 设置item监听
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyStockDetailsActivity.this,
						EditMyselfResearchReportActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("reportId",
						reports.get(position).get("reportId").toString());
				intent.putExtras(bundle);

				startActivity(intent);
			}
		});
	}

	private void showComments() {
		EditMyselfCommentsAdapter adapter = new EditMyselfCommentsAdapter(
				MyStockDetailsActivity.this, comments, rl_parent, false);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Bundle bundle = new Bundle();
				bundle.putString("creatorUuid",
						comments.get(position).get("creatorUuid").toString());
				bundle.putString("creatorName",
						comments.get(position).get("creatorName").toString());
				bundle.putString("creationTime",
						comments.get(position).get("creationTime").toString());
				bundle.putString("creatorLogoUrl",
						comments.get(position).get("creatorLogoUrl").toString());
				bundle.putString("content",
						comments.get(position).get("content").toString());
				bundle.putString("likeNum",
						comments.get(position).get("likeNum").toString());
				bundle.putString("commentNum",
						comments.get(position).get("commentNum").toString());
				bundle.putString("readNum",
						comments.get(position).get("readNum").toString());
				bundle.putString("transpondNum",
						comments.get(position).get("transpondNum").toString());
				bundle.putString("topicUuid",
						comments.get(position).get("topicUuid").toString());
				bundle.putString("liked", comments.get(position).get("liked")
						.toString());
				bundle.putString("refTopic",
						comments.get(position).get("refTopic").toString());
				bundle.putString("followed",
						comments.get(position).get("followed").toString());
				try {
					bundle.putString("medias",
							comments.get(position).get("mediaUrls").toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("liang", e.toString());
					e.printStackTrace();
				}

				Intent intent = new Intent(context, StockCommentActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	public class MyPagerChangeListener implements OnPageChangeListener {

		// 实现ViewPager.OnPageChangeListener接口
		@Override
		public void onPageSelected(int position) {
			Log.d("pageradapter", "onPageSelected:" + position);
			onPage(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// 什么都不干
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// 什么都不干
		}

	}

	/**
	 * //监听滚动Y值变化，通过addView和removeView来实现悬停效果
	 * 
	 * @param scrollY
	 */
	@Override
	public void onScroll(int scrollY) {
		// floatLayoutTop = ll_chatroom.getBottom(); // 获取浮动Layout的顶部位置
		// floatLayoutTop = ll_chatroom.getBottom();
		// if (scrollY >= floatLayoutTop) {
		// if (layout_tab.getParent() != ll_topfloat) {
		// ll_tab.removeView(layout_tab);
		// ll_topfloat.addView(layout_tab);
		// }
		// } else {
		// if (layout_tab.getParent() != ll_tab) {
		// ll_topfloat.removeView(layout_tab);
		// ll_tab.addView(layout_tab);
		// }
		// }
	}

	void getFiveData() {
		String url = AppConfig.URL_QUOTATION + "fiveData.json?"
				+ "securityType=STOCK" + "&symbol=" + symbol;

		Log.d("getFiveData", "url:" + url);

		JSONObject params = new JSONObject();

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("response", "getFiveData" + response);
						try {
							JSONArray array = new JSONArray(response);
							for (int i = 0; i < array.length(); i++) {
								HashMap<String, Object> data = new HashMap<String, Object>();
								Iterator<String> jsIterator;
								try {
									jsIterator = array.getJSONObject(i).keys();
									while (jsIterator.hasNext()) {
										String key = jsIterator.next();
										data.put(key, array.getJSONObject(i)
												.get(key).toString());
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								fivedatas.add(data);
							}

							showFiveData(fivedatas);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("error", error.toString());
					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void showFiveData(ArrayList<HashMap<String, Object>> fivedatas) {
		// View layout_five_buyer1 = getLayoutInflater().inflate(
		// R.id.layout_five_buyer1, null);
		// View layout_five_buyer1 = findViewById(R.id.layout_five_buyer1);
		int[] volumes = new int[10];
		int maxBuy;
		int maxSell;
		for (int i = 0; i < fivedatas.size(); i++) {
			volumes[i] = Integer.parseInt(fivedatas.get(i).get("volume")
					.toString());
		}

		// 找出最大值
		maxBuy = volumes[0];
		for (int i = 1; i < 5; i++) {
			if (maxBuy < volumes[i]) {
				maxBuy = volumes[i];
			}
		}

		maxSell = volumes[5];
		for (int i = 6; i < 10; i++) {
			if (maxSell < volumes[i]) {
				maxSell = volumes[i];
			}
		}

		for (int i = 0; i < fivedatas.size(); i++) {
			TextView tv_param_name = (TextView) findViewById(fiveDataIds[i])
					.findViewById(R.id.tv_param_name);
			tv_param_name.setText(fiveDataNames[i]);
			TextView tv_param = (TextView) findViewById(fiveDataIds[i])
					.findViewById(R.id.tv_param);
			tv_param.setText(fivedatas.get(i).get("price").toString());
			TextView tv_volume = (TextView) findViewById(fiveDataIds[i])
					.findViewById(R.id.tv_volume);
			tv_volume.setText("" + volumes[i]);
			ProgressBar progressBar = (ProgressBar) findViewById(fiveDataIds[i])
					.findViewById(R.id.progress_bar);
			if (i < 5) {
				progressBar.setProgress(volumes[i] * 100 / maxBuy);
			} else {
				progressBar.setProgress(volumes[i] * 100 / maxSell);
			}
		}

	}

	void quotationDetaill() {

		String securityType = null;
		if (isStock) {
			securityType = "STOCK";
		} else {
			securityType = "INDEX";
		}

		String url = AppConfig.URL_QUOTATION + "quotationDetaill/" + symbol
				+ ".json?" + "securityType=" + securityType;

		Log.d("quotationDetaill", "url:" + url);

		JSONObject params = new JSONObject();

		// JsonArray 的请求
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("response", "quotationDetaill:" + response);

						showNewestPrice(response);

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("error", error.toString());
					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void showNewestPrice(String response) {
		try {
			
			if (isStock) {
				JSONObject object = new JSONObject(response);
				tv_now_price.setText(String.format("%.2f",
						Double.parseDouble(object.getString("price"))));
				tv_change.setText(String.format("%.2f",
						Double.parseDouble(object.getString("change"))));
				ratio = Double.parseDouble(object.getString("changeRatio"));
				tv_change_ratio.setText(String.format("%.2f", ratio * 100)
						+ "%");
				String nowString = String.format("%.2f",
						Double.parseDouble(object.getString("price")));
				String change = String.format("%.2f",
						Double.parseDouble(object.getString("change")));
				String radioString = String.format("%.2f", ratio * 100) + "%";
				// 分享的内容
				share_content = stockName + " " + symbol + "\n现价:" + nowString
						+ " 涨跌:" + change + " 涨幅:" + radioString;
				Log.d("hbx_share_content", share_content);
				if (ratio < 0) {
					iv_ratio.setImageResource(R.drawable.ico_stock_down);
				}
				tv_open_price.setText(String.format("%.2f",
						Double.parseDouble(object.getString("openPrice"))));
				tv_close_price.setText(String.format("%.2f",
						Double.parseDouble(object.getString("preClosePrice"))));
				tv_high_price.setText(String.format("%.2f",
						Double.parseDouble(object.getString("highPrice"))));
				tv_low_price.setText(String.format("%.2f",
						Double.parseDouble(object.getString("lowPrice"))));
				amount = Double.parseDouble(object.getString("amount"));
				tv_param1.setText(String.format("%.2f", amount / 100000000)
						+ "亿");
				tv_param2
						.setText(String.format(
								"%.2f",
								Double.parseDouble(object.getString("volume")) / 100000000)
								+ "亿");
				turnoverRate = Double.parseDouble(object
						.getString("turnoverRate"));
				tv_param3.setText(String.format("%.2f", turnoverRate) + "%");
				tv_param4.setText(String.format("%.2f",
						Double.parseDouble(object.getString("pe"))));
				tv_param5
						.setText(String.format("%.1f",
								Double.parseDouble(object
										.getString("marketValue")) / 100000000)
								+ "亿");
			} else {
				JSONObject object = new JSONObject(response);
				tv_now_price.setText(String.format("%.0f",
						Double.parseDouble(object.getString("price"))));
				tv_change.setText(String.format("%.2f",
						Double.parseDouble(object.getString("change"))));
				ratio = Double.parseDouble(object.getString("changeRatio"));
				tv_change_ratio.setText(String.format("%.2f", ratio * 100)
						+ "%");
				String nowString = String.format("%.2f",
						Double.parseDouble(object.getString("price")));
				String change = String.format("%.2f",
						Double.parseDouble(object.getString("change")));
				String radioString = String.format("%.2f", ratio * 100) + "%";
				// 分享的内容
				share_content = stockName + " " + symbol + "\n现价:" + nowString
						+ " 涨跌:" + change + " 涨幅:" + radioString;
				Log.d("hbx_share_content", share_content);
				if (ratio < 0) {
					iv_ratio.setImageResource(R.drawable.ico_stock_down);
				}
				tv_open_price.setText(String.format("%.0f",
						Double.parseDouble(object.getString("openPrice"))));
				tv_close_price.setText(String.format("%.0f",
						Double.parseDouble(object.getString("preClosePrice"))));
				tv_high_price.setText(String.format("%.0f",
						Double.parseDouble(object.getString("highPrice"))));
				tv_low_price.setText(String.format("%.0f",
						Double.parseDouble(object.getString("lowPrice"))));
				amount = Double.parseDouble(object.getString("amount"));
				tv_description3.setText("上涨");
				tv_description4.setText("下跌");
				tv_description5.setText("停盘");
				tv_param1.setText(String.format("%.2f", amount / 100000000)
						+ "亿");
				tv_param2
						.setText(String.format(
								"%.2f",
								Double.parseDouble(object.getString("volume")) / 100000000)
								+ "亿");
				turnoverRate = Double.parseDouble(object
						.getString("turnoverRate"));
				tv_param3.setText("--");
				tv_param4.setText("--");
				tv_param5.setText("--");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// Toast.makeText(getApplicationContext(), "数据解析错误：" +
			// e.getMessage(),
			// Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 获取最近调研的后台数据
	 */
	private void getAllSurveys(int pageIndex, int pageSize, final int type) {
		// Log.d("liang_datas_size_start", no_filter_datas.size() + "");

		String url = AppConfig.URL_SURVEY + "search.json?";
		Log.d("和浩峰", url);
		JSONObject params = new JSONObject();
		try {

			// JSONArray jsonArray = new JSONArray();
			// jsonArray.put("440300");
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("sortType", RsSharedUtil.getString(
					MyStockDetailsActivity.this, "filter_sortType"));

			// 如果调研状态为"null"，即不需要对调研状态进行筛选
			if (!RsSharedUtil.getString(MyStockDetailsActivity.this,
					"filter_surveyState").equals("null")) {
				params.put("surveyState", RsSharedUtil.getString(
						MyStockDetailsActivity.this, "filter_surveyState"));
			}
			Log.d("和浩峰", params.toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Log.d("liang_survey_list_url", url);
		// Log.d("liang_survey_list_params", params.toString());

		// 制定post请求
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.POST, url, params,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						// iv_stock_refresh.clearAnimation();

						if (response.toString() != ""
								&& response.toString() != "[0]") {
							// currentPage++;
						}

						JSONObject jsonObject = null;

						JSONArray jsonArray = null;
						try {
							jsonArray = new JSONArray(response
									.getString("surveys"));

							jsonObject = new JSONObject(response
									.getString("pageable"));
							// totalPages = Integer.parseInt(jsonObject.get(
							// "totalPages").toString());

						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// 存放后台数据的hashmap的List
						final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < jsonArray.length(); i++) {
							HashMap<String, String> data = new HashMap<String, String>();

							Iterator<String> jsIterator;
							try {
								jsIterator = jsonArray.getJSONObject(i).keys();

								while (jsIterator.hasNext()) {
									String key = jsIterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							datas.add(data);

						}

						surveys = datas;
						showSurvey();
						// if (no_filter_datas.size() == 0) {
						// ll_wuhuodong.setVisibility(View.VISIBLE);
						// mPullToRefreshView.setVisibility(View.GONE);
						// } else {
						// ll_wuhuodong.setVisibility(View.GONE);
						// mPullToRefreshView.setVisibility(View.VISIBLE);
						// }

						// Log.d("liang_datas_size_end", no_filter_datas.size()
						// + "");

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						// iv_stock_refresh.clearAnimation();

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
				});

		MyApplication.getRequestQueue().add(jsonObjectRequest);

	}

	private void getAllNews(int pageIndex, int pagerSize, final int type) {
		String url = AppConfig.URL_INFO
 + "new/list/security.json?" + "symbol="
				+ symbol
				+ "&pageIndex=" + pageIndex + "&pageSize=" + pagerSize
				+ "&type=STOCK";

		Log.d("dj_news", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_news_response", response.toString());

						// iv_stock_refresh.clearAnimation();
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
							news = datas;
							showNews();
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_news", e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_news", jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void getAllAnnouncements(int pageIndex, int pageSize, final int type) {
		String url = AppConfig.URL_INFO + "ann/list.json?";

		JSONObject object = new JSONObject();
		try {
			object.put("symbol", symbol);
			object.put("type", "STOCK");
			object.put("pageIndex", pageIndex);
			object.put("pageSize", pageSize);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Log.d("dj_announcement", url);
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				object, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_announcement_response", response.toString());

						// iv_stock_refresh.clearAnimation();

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
							for (int i = 0; i < datas.size(); i++) {
								HashMap<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("announcementid",
										datas.get(i).get("announcementid"));
								hashMap.put("tv_announcement_text", datas
										.get(i).get("title"));
								hashMap.put("tv_announcement_date", datas
										.get(i).get("declaredate"));
								announcements.add(hashMap);
							}
							showAnnouncements();
							// if (type == FOOT)
							// {
							// announcements.addAll(datas);
							// Log.d("allNewsallNews", ""+announcements.size());
							// }
							// else
							// {
							// announcements.clear();
							// announcements.addAll(datas);
							// }
							// mAdapter.notifyDataSetChanged();

						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_announcement", e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_announcement",
									jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	private void getAllReports(int pageIndex, int pageSize, final int type) {
		String url = AppConfig.URL_INFO
 + "report/list/security.json?";
		url += "pageIndex=" + pageIndex + "&pageSize=" + pageSize + "&symbol="
				+ symbol + "&type=STOCK";

		Log.d("dj_researchReport", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_researchReport_response", response.toString());

						// iv_stock_refresh.clearAnimation();

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
							reports = datas;
							showResearchReports();
							// if (type == FOOT)
							// {
							// allNews.addAll(datas);
							// Log.d("allNewsallNews", ""+allNews.size());
							// }
							// else
							// {
							// allNews.clear();
							// allNews.addAll(datas);
							// }
							// mAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_researchReport",
									e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_researchReport",
									jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	// 获取评论

	private void getAllComments(int pageIndex, int pageSize, final int type) {

		String url = AppConfig.URL_TOPIC + "list/security.json?";
		url += "sortType=NEWEST&symbol="
				+ symbol
				+ "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;

		Log.d("dj_comments", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_comments_response", response.toString());
						// iv_stock_refresh.clearAnimation();

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
							comments = datas;
							showComments();
							// // 替换或者增加数据
							// if (type == FOOT)
							// list.addAll(datas);
							// else {
							// list.clear();
							// list.addAll(datas);
							// }
							// // 监听变化
							// mAdapter.notifyDataSetChanged();

						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_comments", e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_comments",
									jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	private void getGroupId() {
		String url = AppConfig.URL_QUOTATION + "group.json?access_token="
				+ RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN)
				+ "&symbol=" + symbol + "&type=STOCK";

		Log.d("group", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("group", response.toString());
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject object = new JSONObject(response);
							groupId = object.getString("groupId");
							loginChatRoom();
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("group", e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("group", jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void getConcerned() {
		String url = AppConfig.URL_QUOTATION + "followType.json?access_token="
				+ RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN);

		JSONArray array = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			object.put("symbol", symbol);
			object.put("securityType", "STOCK");
			array.put(object);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("getConcerned", url);
		StringRequest stringRequest = new StringRequest(array, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("getConcerned", response.toString());
						// iv_stock_refresh.clearAnimation();

						try {
							JSONArray array = new JSONArray(response);
							isConcerned = Boolean.parseBoolean(array
									.getJSONObject(0).getString("followType"));
							showConcerned(isConcerned);
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("getConcerned", e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						// iv_stock_refresh.clearAnimation();

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("getConcerned", jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void showConcerned(final boolean concerned) {
		this.isConcerned = concerned;
		if (isConcerned) {
			iv_concerned.setImageResource(R.drawable.stock_delete);
		} else {
			iv_concerned.setImageResource(R.drawable.stock_add);
		}
		iv_concerned.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				concernStock(!isConcerned);
			}
		});
	}

	private void concernStock(final boolean isConcerned) {
		iv_concerned.setClickable(false);
		String concerned = null;
		if (isConcerned) {
			concerned = "true";
			iv_concerned.setImageResource(R.drawable.stock_delete);
		} else {
			concerned = "false";
			iv_concerned.setImageResource(R.drawable.stock_add);
		}
		String url = AppConfig.URL_USER + "security.json?access_token="
				+ RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN)
				+ "&followType=" + concerned;

		JSONArray array = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			object.put("symbol", symbol);
			object.put("type", "STOCK");
			array.put(object);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("Concern", url);
		StringRequest stringRequest = new StringRequest(array, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("Concern", response.toString());
						iv_concerned.setClickable(true);
						if (isConcerned) {
							InternetDialog internetDialog = new InternetDialog(
									MyStockDetailsActivity.this);
							internetDialog.showInternetDialog("关注成功", true);
							// Toast.makeText(context, "关注成功", 1).show();
							showConcerned(true);
						} else {
							InternetDialog internetDialog = new InternetDialog(
									MyStockDetailsActivity.this);
							internetDialog.showInternetDialog("取消关注成功", true);
							// Toast.makeText(context, "取消关注成功", 1).show();
							showConcerned(false);
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						if (isConcerned) {
							InternetDialog internetDialog = new InternetDialog(
									MyStockDetailsActivity.this);
							internetDialog.showInternetDialog("关注失败", false);
							// Toast.makeText(context, "关注失败", 1).show();
							showConcerned(false);
						} else {
							InternetDialog internetDialog = new InternetDialog(
									MyStockDetailsActivity.this);
							internetDialog.showInternetDialog("取消关注失败", false);
							// Toast.makeText(context, "取消关注失败", 1).show();
							showConcerned(true);
						}
						iv_concerned.setClickable(true);
						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("Concern", jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("MyStockDetailsActivity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void addAlert() {
		if (symbol != null) {

			Bundle bundle = new Bundle();
			bundle.putString("symbol", symbol);
			bundle.putString("securityType", "STOCK");
			bundle.putString("shortname", stocks.get(position).get("shortname"));
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(getApplicationContext(), RemindEditActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		// finish();
	}

	private class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... arg0) {
			getAllAnnouncements(0, 10, FOOT);
			getAllSurveys(0, 10, FOOT);
			getAllReports(0, 10, FOOT);
			getAllComments(0, 10, FOOT);
			return null;
		}

	}

	private void showShare(String content) {

		v_bg.setAlpha(0.5f);
		popupWindow = new ShareUtils(context, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

				v_bg.setAlpha(0.0f);
			}
		});
	}

	@Override
	public void onFinishChart(int chartType) {
		switch (chartType) {
		case 0:
			isFinish1 = true;
			break;
		case 1:
			isFinish2 = true;
			break;
		case 2:
			isFinish3 = true;
			break;
		case 3:
			isFinish4 = true;
			break;
		default:
			break;
		}
		if (isFinish1 && isFinish2 && isFinish3 && isFinish4) {
			iv_stock_refresh.clearAnimation();
		}

	}
}
