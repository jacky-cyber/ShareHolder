package com.example.shareholders.fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundDetailsActivity;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.activity.survey.ReviewDetailsActivity;
import com.example.shareholders.activity.survey.SurveyListActivity;
import com.example.shareholders.adapter.LvFocusAdapter;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.survey.Banner;
import com.example.shareholders.jacksonModel.survey.HottestComment;
import com.example.shareholders.jacksonModel.survey.LastestSurvey;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.util.Mapper;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Survey extends Fragment  implements OnHeaderRefreshListener,OnFooterRefreshListener {
	
	private BitmapUtils bitmapUtils=null;
	// 下拉刷新
	@ViewInject(R.id.survey_pulltorefresh)
	private PullToRefreshView mPullToRefreshView;
	//
	@ViewInject(R.id.tv_foucus_more)
	private TextView tv_foucus_more;
	//
	@ViewInject(R.id.rl_detail)
	private RelativeLayout rl_detail;
	//
	@ViewInject(R.id.rl_survey)
	private RelativeLayout rl_survey;
	//
	@ViewInject(R.id.vp)
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> list_img; // 滑动的图片集合
	private List<View> dots; // 图片标题正文的那些点
	private int currentItem = 0; // 当前图片的索引号
	private ScheduledExecutorService scheduledExecutorService; // 切换当前显示的图片
	// 轮播图
	List<Banner> banners;
	// 判断是否第一次进来并且更新数据
	private boolean flag = true;
	private AsyncTask<Void, Void, Void> bannerTask = null;

	private AlertDialog mDialog = null;
	private AlertDialog internertDialog = null;
	DbUtils dbUtils;

	@ViewInject(R.id.current_survey_listview)
	public MyListView lv_current;
	@ViewInject(R.id.focus_listview)
	public MyListView lv_focus;

	// 近期调研adpater
	private  Lv_Current_Adapter lv_current_adapter;
	// 调研聚焦
	private  LvFocusAdapter lv_focus_adapter;

	@ViewInject(R.id.ll_dots)
	private LinearLayout ll_dots;

	private ProgressDialog progress;
	// 用户角色标志，0为普通用户，1为发起人
	private static int usernum;
	// 外部包围块
	@ViewInject(R.id.ll_bank)
	private LinearLayout bank;
	// 企业名称
	@ViewInject(R.id.tv_bank_name)
	private TextView tv_bank_name;
	// 股票代码
	@ViewInject(R.id.tv_bank_num1)
	private TextView tv_bank_num1;
	// 实时价格
	@ViewInject(R.id.tv_bank_num2)
	private TextView tv_bank_num2;
	// 涨幅
	@ViewInject(R.id.tv_bank_num_percent)
	private TextView tv_bank_num_percent;
	// K线图
	@ViewInject(R.id.my_charts_view)
	CombinedChart macandlestickchart;
	@ViewInject(R.id.tv_history)
	TextView tv_history;

	// banner上的公司名称
	@ViewInject(R.id.tv_banner_company_names)
	private TextView tv_banner_company_names;
	@ViewInject(R.id.rl_banner_company_names)
	private RelativeLayout rl_banner_company_names;
	@ViewInject(R.id.progress_bar)
	private ProgressBar progress_bar;
	private boolean hottest_finished = false; // 获取调研聚焦的后台数据是否完成
	private boolean banner_finished = false; // 获取调研banner的后台数据是否完成
	private boolean latest_finished = false; // 获取调研banner的后台数据是否完成
	LoginReceiver loginReceiver;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					// 当调研聚焦和近期调研以及banner的数据都加载完，加载对话框消失
					if (hottest_finished && banner_finished && latest_finished) {
						internertDialog.dismiss();
						hottest_finished = false;
						banner_finished = false;
						latest_finished = false;
						flag = false;
					}
				}
				flag = false;
				break;

			case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力

				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
					showInternetDialog();
					Log.d("wangluobugeili", "wangluobugeili");
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

	ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String, String>>();
	ArrayList<String> responses;
	ArrayList<HashMap<String, Object>> datas;
	ArrayList<HashMap<String, Object>> datas1;
	ArrayList<HashMap<String, Object>> datas2;
	ArrayList<HashMap<String, Object>> datas3;
	ArrayList<HashMap<String, Object>> datas4;

	ArrayList<String> securityTypeList;

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private void showLoadingDialog() {
		internertDialog = new AlertDialog.Builder(getActivity()).create();
		internertDialog.show();
		// 按返回键可以取消
		internertDialog.setCancelable(true);

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
	private void showInternetDialog() {
		internertDialog = new AlertDialog.Builder(getActivity()).create();
		internertDialog.show();
		internertDialog.setCancelable(true);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);
		progress_bar.setVisibility(View.GONE);
		iv_tips.setVisibility(View.VISIBLE);
		tv_message.setText("网络不給力");

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

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_survey, null);
		ViewUtils.inject(this, v);

		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.huodongphoto);

		// 判断是否第一次进来或者数据是否加载完
		if (!flag) {
			Log.d("firstloading", "666");
			if (NetWorkCheck.isNetworkConnected(getActivity())) {
				showLoadingDialog();
			}
		}
		
		dots = new ArrayList<View>();
		dbUtils = DbUtils.create(getActivity());
		responses = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			responses.add("null");
		}
		datas = new ArrayList<HashMap<String, Object>>();
		datas1 = new ArrayList<HashMap<String, Object>>();
		datas2 = new ArrayList<HashMap<String, Object>>();
		datas3 = new ArrayList<HashMap<String, Object>>();
		datas4 = new ArrayList<HashMap<String, Object>>();
		securityTypeList = new ArrayList<String>();
		getBanner();
		latestSurvey(0, 3);
		getHottestTopics(0, 3);
		
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setEnablePullLoadMoreDataStatus(false);
		mPullToRefreshView.setEnablePullTorefresh(false);
		mPullToRefreshView.setFooterEnable(false);
		
		
		
		
		return v;
	}
	
	/**
	 * 每60秒重新获取一次数据
	 */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				new GetCurrentPriceTask().execute();
				try {
					Thread.sleep(1000 * 60);
				} catch (InterruptedException e) {

				}
			}
		}
	};

	class GetCurrentPriceTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			for (int i = 0; i < banners.size(); i++) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				stocks.add(i, hashMap);
				getCurrentPrice(banners, i);
				long startMili=System.currentTimeMillis();
					new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {

						}
					}
				}).run();
				long endMili=System.currentTimeMillis();
				Log.w("thelastfour",""+(endMili-startMili));
			}
			return null;
		}

	}

	/**
	 * 获取实时数据
	 * 
	 * @param symbol
	 */
	private void getCurrentPrice(final List<Banner> banners, final int position) {
		try {
			String url = AppConfig.URL_QUOTATION
					+ "real-time.json?symbol="
					+ banners.get(position).getSymbol()
					+ "&securityType=STOCK&access_token="
					+ RsSharedUtil.getString(getActivity(),
							AppConfig.ACCESS_TOKEN);
			Log.w("thelastfour", url);
			StringRequest stringRequest = new StringRequest(url, null,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							// TODO Auto-generated method stub
							HashMap<String, String> hashMap = new HashMap<String, String>();
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(response);
								// 公司简称
								if(jsonObject.getString("shortname")==null||jsonObject.getString("shortname").equals("")){
								
									hashMap.put("shortname", "休市");
								}
								else{
									hashMap.put("shortname",
											jsonObject.getString("shortname"));
								}
								// Log.w("thelastfour",hashMap.get("shortname").toString());
								Log.w("thelastfour",
										jsonObject.getString("shortname"));
								// 今开价
								hashMap.put("openPrice",
										jsonObject.getString("openPrice"));
								// 股票代号
								hashMap.put("symbol", banners.get(position)
										.getSymbol());
								
								JSONArray jsonArray = new JSONArray(jsonObject
										.getString("quotations"));
								
								Log.w("thelastfour", "2," + jsonArray);
								JSONObject jsonObject2 = jsonArray
										.getJSONObject(0);
								
								// 当前价
								hashMap.put("price",
										jsonObject2.getString("price"));
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								hashMap.put("openPrice", "rest");
								hashMap.put("symbol", banners.get(position)
										.getSymbol());
								hashMap.put("price", "rest");

								Log.w("thelastthree", e);

							}
							stocks.add(position, hashMap);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							HashMap<String, String> hashMap = new HashMap<String, String>();
							hashMap.put("openPrice", "1111");
							hashMap.put("symbol", banners.get(position)
									.getSymbol());
							hashMap.put("price", "1111");
							hashMap.put("shortname", "1111");
							stocks.add(position, hashMap);
						}
					});
			stringRequest.setTag("getCurrentPrice");
			MyApplication.getRequestQueue().add(stringRequest);
		} catch (Exception e) {

		}
	}

	/**
	 * 调研聚焦
	 */
	List<HottestComment> hottestComments = new ArrayList<HottestComment>();

	public  void getHottestTopics(int pageIndex, int pageSize) {
		String url = AppConfig.URL_TOPIC
				+ "list/survey/hottest.json?"
				
				+ "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub

						try {
							hottestComments.clear();
							ObjectMapper objectMapper = new ObjectMapper();
							objectMapper.getSerializerProvider()
									.setNullValueSerializer(
											new JsonSerializer<Object>() {

												@Override
												public void serialize(
														Object arg0,
														JsonGenerator arg1,
														SerializerProvider arg2)
														throws IOException,
														JsonProcessingException {
													// TODO Auto-generated
													// method stub
													arg1.writeString("");
												}
											});
							dbUtils.deleteAll(HottestComment.class);
							JSONArray all = new JSONArray(response.toString());
							for (int i = 0; i < all.length(); i++) {
								HottestComment hottestComment = objectMapper
										.readValue(all.get(i).toString(),
												HottestComment.class);
								dbUtils.saveOrUpdate(hottestComment);
								hottestComments.add(hottestComment);
							}
							if (hottestComments != null) {
								DoForHottestComment(hottestComments);
							}

							// 当后台数据加载完后通知mHandler
							hottest_finished = true;
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);

						} catch (Exception e) {
							// TODO Auto-generated catch block

						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							hottestComments.clear();
							hottestComments = dbUtils
									.findAll(HottestComment.class);
							if (hottestComments != null) {
								DoForHottestComment(hottestComments);
							}
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {

						}
					}
				}

		);
		stringRequest.setTag("hottestComments");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void DoForHottestComment(List<HottestComment> hottestComments) {
		lv_focus.setFocusable(false);
		lv_focus_adapter = new LvFocusAdapter(getActivity(), hottestComments);
		lv_focus.setAdapter(lv_focus_adapter);

	}

	/**
	 * 获取banner图片
	 */

	public void getBanner() {
		banners = new ArrayList<Banner>();
		list_img = new ArrayList<ImageView>();

		final LayoutInflater mInflater = LayoutInflater.from(getActivity());
		ll_dots.removeAllViews();
		dots.clear();
		list_img.clear();
		String url = AppConfig.URL_SURVEY + "heatest.json?";
		//url = url + RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("liang_url_banner", url);
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, new JSONObject(),
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						// Log.d("response", response.toString());
						banners.clear();
						try {
							dbUtils.deleteAll(Banner.class);
							dbUtils.dropTable(Banner.class);
						} catch (DbException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						ObjectMapper objectMapper = new ObjectMapper();
						for (int m = 0; m < response.length(); m++) {
							// 将banner写入数据库
							try {

								Banner banner = objectMapper.readValue(response
										.getJSONObject(m).toString(),
										Banner.class);
								// dbUtils.saveOrUpdate(banner);
								banners.add(banner);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								// Toast.makeText(getActivity(), "更新数据库错误" + e1,
								// 1)
								// .show();
							}
						}
						DoForBanner(mInflater);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						banners.clear();
						try {
							banners = dbUtils.findAll(Banner.class);
							DoForBanner(mInflater);
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {
						}
					}

				}

		);
		jsonArrayRequest.setTag("getBanner");
		MyApplication.getRequestQueue().add(jsonArrayRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("hottestComments");
		MyApplication.getRequestQueue().cancelAll("getBanner");
		MyApplication.getRequestQueue().cancelAll("lastestSurveys");
		super.onDestroy();
	}

	private void DoForBanner(LayoutInflater mInflater) {
		if (banners.size() > 0) {
			Log.d("banner", banners.toString());
			rl_banner_company_names.setVisibility(View.VISIBLE);
		}
		securityTypeList.clear();
		for (int i = 0; i < banners.size(); i++) {

			ImageView imageView = new ImageView(getActivity());
			imageView.setScaleType(ScaleType.FIT_XY);
			ImageLoader.getInstance().displayImage(banners.get(i).getPicture(),
					imageView);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			if (list_img.size() < 5) {
				list_img.add(imageView);
			}
			View dot = mInflater.inflate(R.layout.item_pager_dots, null);
			if (dots.size() < 5) {
				dots.add(dot.findViewById(R.id.v_dot));
			}
			ll_dots.addView(dot);

			// new getKLineDataTask().execute();
			// if( bannerTask!=null){
			// bannerTask.cancel(true);
			// }
			// bannerTask=new getKLineDataTask();
			// bannerTask.execute();

			String securityType = banners.get(i).getSecurityType();
			securityTypeList.add(securityType);
			if (securityType.equals("LISTED")) {
				getKLineData(banners.get(i).getSymbol(), i);

			} else if (securityType.equals("UNLISTED")) {
				responses.set(i, banners.get(i).getUnlistedHistory());

			} else if (securityType.equals("STAS")) {
				getNeeqLineData(banners.get(i).getSymbol(), i);

			} else if (securityType.equals("FUND_OTHER")
					|| securityType.equals("FUND_CURRENCY")) {
				getNavHistory(banners.get(i).getSymbol(), i);
			}
		}
		viewPager.setAdapter(new ViewpagerDotsAdapter(list_img, getActivity()));// 设置填充ViewPager页面的适配器
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

		// 当banner后台数据加载完毕，通知mhandler
		banner_finished = true;

		Message msg = new Message();
		msg.what = 1;
		mHandler.sendMessage(msg);
		new Thread(runnable).start();
	}

	/**
	 * 获取最新调研
	 * 
	 * @param pageIndex
	 * @param pageSize
	 */
	List<LastestSurvey> lastestSurveys = new ArrayList<LastestSurvey>();

	public void latestSurvey(int pageIndex, int pageSize) {
		String url = AppConfig.URL_SURVEY + "lastest.json?";
		url = url 
				+ "&pageSize=" + pageSize + "&pageIndex=" + pageIndex+"&access_token="+RsSharedUtil.getString(getActivity(), "access_token");
		Log.w("近期调研", url);
		

		// 3.json post请求处理
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						// TODO Auto-generated method stub
						Log.w("近期调研", response.toString());
						Mapper objectMapper = new Mapper();
						try {
							lastestSurveys.clear();
							dbUtils.deleteAll(LastestSurvey.class);
							for (int m = 0; m < response.length(); m++) {
								// 每个子项数据
								LastestSurvey lastestSurvey;
								lastestSurvey = objectMapper
										.readValue(response.get(m).toString(),
												LastestSurvey.class);
								dbUtils.saveOrUpdate(lastestSurvey);
								lastestSurveys.add(lastestSurvey);
								if (lastestSurveys != null) {
									DoForLastestSurvey(lastestSurveys);
								}
							}

							// 当后台数据加载完后通知mHandler
							latest_finished = true;
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);

						} catch (Exception e) {
							// TODO Auto-generated catch block
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							lastestSurveys.clear();
							lastestSurveys = dbUtils
									.findAll(LastestSurvey.class);
							if (lastestSurveys != null) {
								DoForLastestSurvey(lastestSurveys);
							}
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {
						}
					}

				}

		);
		jsonArrayRequest.setTag("lastestSurveys");
		// 4.请求对象放入请求队列
		MyApplication.getRequestQueue().add(jsonArrayRequest);

	}

	private void DoForLastestSurvey(final List<LastestSurvey> lastestSurveys) {

		lv_current_adapter = new Lv_Current_Adapter(getActivity(),
				lastestSurveys);
		lv_current.setAdapter(lv_current_adapter);
		// 近期调研的item监听
		lv_current.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String uuid = lastestSurveys.get(position).getUuid();
				
					if(!RsSharedUtil.getLoginState(getActivity())){
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent(getActivity(),
								DetailSurveyActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("uuid", uuid);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			
		});
	}

	@OnClick({ R.id.ll_more, R.id.rl_detail, R.id.ll_foucus_more })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_more:
			if(!RsSharedUtil.getLoginState(getActivity())){
				Intent intent = new Intent(getActivity(),
						LoginActivity.class);
				startActivity(intent);
			}else{
			Intent intent = new Intent(getActivity(), SurveyListActivity.class);
			startActivity(intent);
			}
			break;
		case R.id.rl_detail:
			if(!RsSharedUtil.getLoginState(getActivity())){
				Intent intent = new Intent(getActivity(),
						LoginActivity.class);
				startActivity(intent);
			}else{
			startActivity(new Intent(getActivity(), DetailSurveyActivity.class));
			}
			break;
		case R.id.ll_foucus_more:
			if(!RsSharedUtil.getLoginState(getActivity())){
				Intent intent = new Intent(getActivity(),
						LoginActivity.class);
				startActivity(intent);
			}else{
			startActivity(new Intent(getActivity(), ReviewDetailsActivity.class));
			}
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		}
	};
	
	/***
	 * 下拉刷新
	 */
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {

				lv_current.setAdapter(null);
				lv_focus.setAdapter(null);
				// getBanner();
				latestSurvey(0, 3);
				getHottestTopics(0, 3);
				mPullToRefreshView.onHeaderRefreshComplete();

			}
		}, 1000);

	}
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				
			}
		}, 1000);
		
	}
	@Override
	public void onStart() {

		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每五秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 6,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % list_img.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片

			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(final int position) {
			currentItem = position;

			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			tv_banner_company_names.setText(banners.get(position)
					.getSurveyName());

			oldPosition = position;
			String securityType = securityTypeList.get(position);
			if (securityType.equals("UNLISTED")) {
				try {
					progress_bar.setVisibility(View.GONE);
					rl_survey.setVisibility(View.GONE);
					tv_history.setVisibility(View.VISIBLE);
					tv_history.setText(responses.get(position));

				} catch (Exception e) {
					rl_survey.setVisibility(View.GONE);
					tv_history.setVisibility(View.GONE);
					progress_bar.setVisibility(View.VISIBLE);
				}
			} else if (securityType.equals("FUND_OTHER")) {

				ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
				try {
					JSONArray all = new JSONArray(responses.get(position));
					for (int i = 0; i < all.length(); i++) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						Iterator<String> jsIterator;
						try {
							jsIterator = all.getJSONObject(i).keys();
							while (jsIterator.hasNext()) {
								String key = jsIterator.next();
								data.put(key, all.getJSONObject(i).get(key)
										.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						arrayList.add(data);
					}
					showFundChart(macandlestickchart, arrayList, "nav");
				} catch (Exception e1) {
					e1.printStackTrace();
					macandlestickchart.clear();
				}
				try {
					progress_bar.setVisibility(View.GONE);
					tv_bank_name.setText(banners.get(position).getSymbolName());
					tv_bank_num1.setText(banners.get(position).getSymbol());
					float nav = Float
							.parseFloat(banners.get(position).getNav());
					float accumulativenav = Float.parseFloat(banners.get(
							position).getAccumulativenav());

					if (nav == 0.0f) {
						tv_bank_num2.setText("--");
						tv_bank_num_percent.setText("--");
					} else {
						tv_bank_num2.setText("" + nav);
						tv_bank_num_percent.setText("" + accumulativenav);
						if (accumulativenav >= 0) {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style);
						} else {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style_green);
						}
					}
					
					bank.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									FundDetailsActivity.class);
							intent.putExtra("symbol", banners.get(position)
									.getSymbol());
							startActivity(intent);
						}
					});

					tv_history.setVisibility(View.GONE);
					rl_survey.setVisibility(View.VISIBLE);

				} catch (Exception e) {
					rl_survey.setVisibility(View.GONE);
					tv_history.setVisibility(View.GONE);
					progress_bar.setVisibility(View.VISIBLE);
				}

			} else if (securityType.equals("FUND_CURRENCY")) {
				ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
				try {
					JSONArray all = new JSONArray(responses.get(position));
					for (int i = 0; i < all.length(); i++) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						Iterator<String> jsIterator;
						try {
							jsIterator = all.getJSONObject(i).keys();
							while (jsIterator.hasNext()) {
								String key = jsIterator.next();
								data.put(key, all.getJSONObject(i).get(key)
										.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						arrayList.add(data);
					}

					showFundChart(macandlestickchart, arrayList,
								"achievereturn");
				} catch (Exception e1) {
					e1.printStackTrace();
					macandlestickchart.clear();
				}
				try {
					progress_bar.setVisibility(View.GONE);
					tv_bank_name.setText(banners.get(position).getSymbolName());
					tv_bank_num1.setText(banners.get(position).getSymbol());
					float achievereturn = Float.parseFloat(banners
							.get(position).getAchievereturn());
					float annualizedyield = Float.parseFloat(banners.get(
							position).getAnnualizedyield());

					if (achievereturn == 0.0f) {
						tv_bank_num2.setText("--");
						tv_bank_num_percent.setText("--");
					} else {
						tv_bank_num2.setText("" + achievereturn);
						tv_bank_num_percent.setText("" + annualizedyield);
						if (annualizedyield >= 0) {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style);
						} else {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style_green);
						}
					}

					bank.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									FundDetailsActivity.class);
							intent.putExtra("symbol", banners.get(position)
									.getSymbol());
							startActivity(intent);
						}
					});

					tv_history.setVisibility(View.GONE);
					rl_survey.setVisibility(View.VISIBLE);

				} catch (Exception e) {
					Log.w("thelastfour", e);
					rl_survey.setVisibility(View.GONE);
					tv_history.setVisibility(View.GONE);
					progress_bar.setVisibility(View.VISIBLE);
				}

			} else if (securityType.equals("LISTED")) {

				ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
				try {
					JSONArray array = new JSONArray(responses.get(position));
					for (int i = 0; i < array.length(); i++) {
						String[] strings = array.get(i).toString().split(" ");
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("date", strings[0]);
						hashMap.put("symbol", strings[1]);
						hashMap.put("open", strings[2]);
						hashMap.put("close", strings[3]);
						hashMap.put("max", strings[4]);
						hashMap.put("min", strings[5]);
						hashMap.put("sum", strings[6]);
						hashMap.put("money", strings[7]);
						hashMap.put("avg", strings[8]);
						arrayList.add(hashMap);
					}

					showCombinedChart(macandlestickchart, arrayList, 60);
				} catch (Exception e1) {
					e1.printStackTrace();
					macandlestickchart.clear();
				}
				try {
					progress_bar.setVisibility(View.GONE);
					tv_bank_name.setText(banners.get(position).getSymbolName());
					tv_bank_num1.setText(banners.get(position).getSymbol());
					float nowPrice = Float.parseFloat(banners.get(position)
							.getNowPrice());
					float preClosePrice = Float.parseFloat(banners
							.get(position).getPreClosePrice());
					float rise = (nowPrice - preClosePrice) / preClosePrice;

					if (nowPrice == 0.0f) {
						tv_bank_num2.setText("--");
						tv_bank_num_percent.setText("--");
					} else {
						tv_bank_num2.setText(banners.get(position)
								.getNowPrice());
						tv_bank_num_percent.setText(String.format("%.2f",
								rise * 100) + "%");
						if (nowPrice - preClosePrice >= 0) {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style);
						} else {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style_green);
						}
					}

					bank.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									MyStockDetailsActivity.class);
							ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> hashMap = new HashMap<String, String>();
							hashMap.put("shortname", banners.get(position)
									.getSymbolName());
							hashMap.put("symbol", banners.get(position)
									.getSymbol());
							temp.add(hashMap);
							intent.putExtra("stocks", temp);
							intent.putExtra("position", 0);
							startActivity(intent);
						}
					});

					tv_history.setVisibility(View.GONE);
					rl_survey.setVisibility(View.VISIBLE);

				} catch (Exception e) {
					Log.w("thelastfour", e);
					rl_survey.setVisibility(View.GONE);
					tv_history.setVisibility(View.GONE);
					progress_bar.setVisibility(View.VISIBLE);
				}

			} else if (securityType.equals("STAS")) {

				ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
				try {
					JSONObject object = new JSONObject(responses.get(position));
					JSONArray array = object.getJSONArray("quotationResponses");
					for (int i = 0; i < array.length(); i++) {
						JSONObject result = array.getJSONObject(i);
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("date", result.getString("date"));
						// hashMap.put("symbol",
						// result.getString("date"));
						hashMap.put("open", result.getString("openPrice"));
						hashMap.put("close", result.getString("closeprice"));
						hashMap.put("max", result.getString("highPrice"));
						hashMap.put("min", result.getString("lowPrice"));
						arrayList.add(hashMap);
					}

					showCombinedChart(macandlestickchart, arrayList, 60);
				} catch (Exception e1) {
					e1.printStackTrace();
					macandlestickchart.clear();
				}
				try {
					progress_bar.setVisibility(View.GONE);
					tv_bank_name.setText(banners.get(position).getSymbolName());
					tv_bank_num1.setText(banners.get(position).getSymbol());
					float nowPrice = Float.parseFloat(banners.get(position)
							.getNowPrice());
					float preClosePrice = Float.parseFloat(banners
							.get(position).getPreClosePrice());
					float rise = (nowPrice - preClosePrice) / preClosePrice;

					if (nowPrice == 0.0f) {
						tv_bank_num2.setText("--");
						tv_bank_num_percent.setText("--");
					} else {
						tv_bank_num2.setText(banners.get(position)
								.getNowPrice());
						tv_bank_num_percent.setText(String.format("%.2f",
								rise * 100) + "%");
						if (nowPrice - preClosePrice >= 0) {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style);
						} else {
							tv_bank_num_percent
									.setBackgroundResource(R.drawable.bg_text_confirm_percen_style_green);
						}
					}

					tv_history.setVisibility(View.GONE);
					rl_survey.setVisibility(View.VISIBLE);

				} catch (Exception e) {
					Log.w("thelastfour", e);
					rl_survey.setVisibility(View.GONE);
					tv_history.setVisibility(View.GONE);
					progress_bar.setVisibility(View.VISIBLE);
				}

			}
		}

		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			scheduledExecutorService.shutdown();
			scheduledExecutorService = Executors
					.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 10,
					10, TimeUnit.SECONDS);
		}

	}

	public class Lv_Current_Adapter extends BaseAdapter {
		private ViewHolder holder;
		private List<LastestSurvey> lastestSurveys;
		private Context context;
		private LayoutInflater mInflater;


		public Lv_Current_Adapter(Context context,
				List<LastestSurvey> lastestSurveys2) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.lastestSurveys = lastestSurveys2;
			mInflater = LayoutInflater.from(context);
			mDialog = new AlertDialog.Builder(getActivity()).create();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lastestSurveys.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lastestSurveys.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_fragment_price_currentsurvey, parent,
						false);

				holder.current_icon = (ImageView) view
						.findViewById(R.id.current_icon);
				holder.current_name = (TextView) view
						.findViewById(R.id.current_name);
				holder.current_watch_num = (TextView) view
						.findViewById(R.id.current_watch_num);
				holder.current_time = (TextView) view
						.findViewById(R.id.current_time);
				holder.current_place = (TextView) view
						.findViewById(R.id.current_place);

				// 报名状态
				holder.iv_state = (ImageView) view.findViewById(R.id.iv_state);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			// 头像

			bitmapUtils.display(holder.current_icon,
					lastestSurveys.get(position).getLogo());
			
			
			Log.d("TOUXIANG",lastestSurveys.get(position).getLogo()+"ddd");
			/*		.displayImage(lastestSurveys.get(position).getLogo(),
							holder.current_icon,defaultOptions);*/
			holder.current_place.setText(lastestSurveys.get(position)
					.getLocationName());
			// 调研名称
			holder.current_name.setText(lastestSurveys.get(position)
					.getSurveyName());
			// 关注人数
			holder.current_watch_num.setText(lastestSurveys.get(position)
					.getCountFollow() + "");
			final String uuid = (lastestSurveys.get(position).getUuid());
			long begin = Long.parseLong(lastestSurveys.get(position)
					.getBeginDate());
			long end = Long
					.parseLong(lastestSurveys.get(position).getEndDate());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			// 开始日期
			String beginDate = dateFormat.format(new Date(begin));
			// 结束日期
			String endDate = dateFormat.format(new Date(end));
			holder.current_time.setText(beginDate + "----" + endDate);

			// 获取活动调研状态
			final String state = lastestSurveys.get(position).getState();

			// 判断活动状态
			
			if (state.equals("ENROLLING")) {// 未报名
				holder.iv_state.setImageResource(R.drawable.btn_baoming);
			} else if (state.equals("SUCCESS")) {// 已报名
				holder.iv_state.setImageResource(R.drawable.btn_yibaoming);
			} else if (state.equals("ENROLL")) {// 待审核
				holder.iv_state.setImageResource(R.drawable.btn_daishenhe);
			} else if (state.equals("FAILED")) {// 已满人
				holder.iv_state.setImageResource(R.drawable.btn_daishenhe);
			} else if (state.equals("SURVEYING")) {// 进行中
				holder.iv_state.setImageResource(R.drawable.ico_jinxingzhong);
			} else if (state.equals("SURVEYEND")) {// 已结束
				holder.iv_state.setImageResource(R.drawable.ico_yijieshu);
			}
			
			

			holder.iv_state.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (state.equals("ENROLLING")) {// 未报名
						Log.d("position", position + "");
						// 报名
						enroll(lastestSurveys.get(position).getUuid(),
								(ImageView) arg0);
                        Toast.makeText(getActivity(), state, 400);
					} else if (state.equals("SUCCESS")) {// 已报名

						// showDialog(getActivity().getResources().getString(
						// R.string.enroll_already));
						InternetDialog internetDialog = new InternetDialog(
								getActivity());
						internetDialog.showInternetDialog(
								getActivity().getResources().getString(
										R.string.enroll_already), true);
						 Toast.makeText(getActivity(), state, 400);
					} else if (state.equals("FAILED")) {// 已满人
						InternetDialog internetDialog = new InternetDialog(
								getActivity());
						internetDialog.showInternetDialog(
								getActivity().getResources().getString(
										R.string.enroll_full), false);
						 Toast.makeText(getActivity(), state, 400);
					} else if (state.equals("ENROLL")) {// 待审核
						InternetDialog internetDialog = new InternetDialog(
								getActivity());
						internetDialog.showInternetDialog(
								getActivity().getResources().getString(
										R.string.enroll_under_audlt), true);
						 Toast.makeText(getActivity(), state, 400);
					}

				}
			});
			return view;
		}

		class ViewHolder {
			ImageView current_icon;
			TextView current_name;
			TextView current_watch_num;
			TextView current_time;
			TextView current_place;
			ImageView iv_state;

		}

	}

	// 报名方法
	private void enroll(String uuid, final ImageView iv_state) {
		String url = AppConfig.URL_SURVEY + "enroll.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		url += "&surveyUuid=" + uuid;

		Log.d("dj_enroll_url", url);
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						InternetDialog internetDialog = new InternetDialog(
								getActivity());
						internetDialog.showInternetDialog(
								getActivity().getResources().getString(
										R.string.enroll_under_audlt), true);
						iv_state.setImageResource(R.drawable.btn_daishenhe);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							InternetDialog internetDialog = new InternetDialog(
									getActivity());
							internetDialog.showInternetDialog(
									jsonObject.getString("description"), false);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 点击活动状态后弹出的对话框
	 */
	private void showDialog(String dialog_message) {
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_survey_list2);

		// 修改提示信息
		TextView message = (TextView) mDialog.getWindow().findViewById(
				R.id.tv_dialog_content);
		message.setText(dialog_message);

		// 点击确定
		mDialog.getWindow().findViewById(R.id.tv_confirm)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mDialog.dismiss();
					}
				});
	}

	

	/*
	 * 功能：ViewPager的适配器
	 */
	public class ViewpagerDotsAdapter extends PagerAdapter {

		private List<ImageView> imageViews = new ArrayList<ImageView>();
		private Context context;

		public ViewpagerDotsAdapter(List<ImageView> imageViews, Context context) {
			// TODO Auto-generated constructor stub
			this.imageViews = imageViews;
			this.context = context;
		}

		@Override
		public int getCount() {
			return imageViews.size();
		}

		/*
		 * 功能：ViewPager中的图片的点击函数
		 */
		@Override
		public Object instantiateItem(View v, final int position) {
			View view = imageViews.get(position);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!RsSharedUtil.getLoginState(getActivity())){
						Intent intent = new Intent(getActivity(),
								LoginActivity.class);
						startActivity(intent);
					}else{
					Bitmap bitmap=((BitmapDrawable)list_img.get(position).getDrawable()).getBitmap(); 
						// 图片发生改变时使用此步骤，防止缓存里的图片没有刷新
						// 解码位图，使其在intent传送时更加安全
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
						// 压缩过大的图片,以190为标准图片,大于190的需要压缩
					if(bitmap.getHeight()<=190)	{
						bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
					}
			else  {
							// 原始高度2400的图片需要压缩20%
					bitmap.compress(Bitmap.CompressFormat.JPEG,80, baos);
			       }
					
					byte [] bitmapByte =baos.toByteArray();
					Intent intent = new Intent(getActivity(),DetailSurveyActivity.class);
					intent.putExtra("bitmap", bitmapByte);
					Bundle bundle = new Bundle();
					bundle.putString("uuid", banners.get(position).getUuid());
					intent.putExtras(bundle);
					
					Log.w("thelastone", Integer.toString(bitmapByte.length));
					getActivity().startActivity(intent);

				}}
			});
			ViewPager viewPager = (ViewPager) v;
			viewPager.addView(view);
			return imageViews.get(position);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

	}

	private void getKLineData(String symbol,
 final int position) {
		String url = AppConfig.URL_QUOTATION + "candlestick.json?access_token="
				+ RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN)
				+ "&securityType=STOCK&symbol=" + symbol
				+ "&type=MONTH&adjust=NONE";
		Log.d("getKLineData1111", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						responses.set(position, response);

						// try {
						// datas.clear();
						// JSONArray array = new JSONArray(response);
						// for (int i = 0; i < array.length(); i++) {
						// String[] strings = array.get(i).toString()
						// .split(" ");
						// HashMap<String, Object> hashMap = new HashMap<String,
						// Object>();
						// hashMap.put("date", strings[0]);
						// hashMap.put("symbol", strings[1]);
						// hashMap.put("open", strings[2]);
						// hashMap.put("close", strings[3]);
						// hashMap.put("max", strings[4]);
						// hashMap.put("min", strings[5]);
						// hashMap.put("sum", strings[6]);
						// hashMap.put("money", strings[7]);
						// hashMap.put("avg", strings[8]);
						// datas.add(hashMap);
						// }
						// // showCombinedChart(macandlestickchart, datas, 60);
						// viewPager.setCurrentItem(currentItem);
						// } catch (Exception e) {
						//
						// }

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		stringRequest.setTag("getKLineData");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void getNeeqLineData(String symbol,
 final int position) {
		String url = AppConfig.VERSION_URL + "neeq/quotation.json?"
				+ "symbol=" + symbol + "&pageSize=60&pageIndex=0";
		Log.d("getNeeqLineData", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						responses.set(position, response);

						// try {
						// datas.clear();
						// JSONObject object = new JSONObject(response);
						// JSONArray array = object
						// .getJSONArray("quotationResponses");
						// for (int i = 0; i < array.length(); i++) {
						// JSONObject result = array.getJSONObject(i);
						// HashMap<String, Object> hashMap = new HashMap<String,
						// Object>();
						// hashMap.put("date", result.getString("date"));
						// // hashMap.put("symbol",
						// // result.getString("date"));
						// hashMap.put("open",
						// result.getString("openPrice"));
						// hashMap.put("close",
						// result.getString("closeprice"));
						// hashMap.put("max",
						// result.getString("highPrice"));
						// hashMap.put("min", result.getString("lowPrice"));
						// // hashMap.put("sum", result.getString("date"));
						// // hashMap.put("money",
						// // result.getString("date"));
						// // hashMap.put("avg", result.getString("date"));
						// datas.add(hashMap);
						// }
						// // showCombinedChart(macandlestickchart, datas, 60);
						// viewPager.setCurrentItem(currentItem);
						// } catch (Exception e) {
						//
						// }

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		stringRequest.setTag("getKLineData");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void showCombinedChart(CombinedChart combinedChart,
			ArrayList<HashMap<String, Object>> arrayList, int count) {

		if (arrayList.size() < count) {
			combinedChart.clear();
			return;
		}

		combinedChart.clear();
		combinedChart.setNoDataText("暂无数据");
		combinedChart.setDescription("");
		combinedChart.setDrawGridBackground(false);
		combinedChart.getLegend().setEnabled(false);
		combinedChart.setBackgroundColor(getResources().getColor(
				R.color.total_gray));
		// combinedChart.setDragEnabled(false);
		combinedChart.setPinchZoom(false);
		// combinedChart.setScaleYEnabled(false);
		combinedChart.setDoubleTapToZoomEnabled(false);
		// combinedChart.setScaleMinima(1, 1);
		// draw bars behind lines
		combinedChart.setDrawOrder(new DrawOrder[] { DrawOrder.CANDLE,
				DrawOrder.LINE });

		String[] dates = new String[count];
		for (int i = 0; i < count; i++) {
			dates[i] = arrayList.get(arrayList.size() - count + i).get("date")
					.toString();
		}

		CombinedData data = new CombinedData(dates);
		CandleData candleData = generateCandleData(arrayList, count);
		data.setData(candleData);

		YAxis rightAxis = combinedChart.getAxisRight();
		rightAxis.setDrawGridLines(true);
		rightAxis.setGridColor(Color.WHITE);
		rightAxis.setGridLineWidth(0.5f);
		rightAxis.setTextColor(Color.WHITE);
		rightAxis.setAxisLineWidth(0.5f);
		rightAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
		rightAxis.setAxisMinValue(data.getYMin(AxisDependency.RIGHT) - 0.2f);
		rightAxis.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT) + 0.2f);
		rightAxis.setStartAtZero(false);
		rightAxis.setShowOnlyMinMax(true);
		rightAxis.setEnabled(false);

		YAxis leftAxis = combinedChart.getAxisLeft();
		leftAxis.setDrawGridLines(false);
		leftAxis.setDrawLabels(false);
		leftAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
		leftAxis.setEnabled(false);

		XAxis xAxis = combinedChart.getXAxis();
		xAxis.setAxisLineColor(Color.WHITE);
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setTextColor(Color.WHITE);
		xAxis.setAxisLineWidth(0.5f);
		xAxis.setDrawGridLines(false);
		xAxis.setDrawLabels(false);
		xAxis.setEnabled(false);

		int[] colors = { Color.WHITE, Color.RED, Color.GREEN, Color.BLUE };
		// data.setData(generateLineData(count, colors));
		combinedChart.setData(data);
		combinedChart.moveViewToX(280);
		combinedChart.invalidate();
	}

	protected CandleData generateCandleData(
			ArrayList<HashMap<String, Object>> arrayList, int count) {

		CandleData d = new CandleData();

		ArrayList<CandleEntry> entries = new ArrayList<CandleEntry>();
		int[] colors = new int[count];

		for (int index = 0; index < count; index++) {
			float shadowH = Float.parseFloat(arrayList
					.get(arrayList.size() - count + index).get("max")
					.toString());
			float shadowL = Float.parseFloat(arrayList
					.get(arrayList.size() - count + index).get("min")
					.toString());
			float open = Float.parseFloat(arrayList
					.get(arrayList.size() - count + index).get("open")
					.toString());
			float close = Float.parseFloat(arrayList
					.get(arrayList.size() - count + index).get("close")
					.toString());
			entries.add(new CandleEntry(index, shadowH, shadowL, open, close));
			if (close >= open) {
				colors[index] = Color.RED;
			} else {
				colors[index] = Color.GREEN;
			}
		}

		CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
		set.setAxisDependency(AxisDependency.RIGHT);
		set.setColors(colors);
		// set.setDecreasingColor(Color.GREEN);
		// set.setIncreasingColor(Color.RED);
		set.setShadowColorSameAsCandle(true);
		set.setDecreasingPaintStyle(Style.FILL);
		set.setIncreasingPaintStyle(Style.FILL);
		set.setDrawValues(false);
		d.addDataSet(set);

		return d;
	}

	private void showFundChart(CombinedChart combinedChart,
			ArrayList<HashMap<String, Object>> arrayList, String type) {

		int count = arrayList.size();
		float gridWidth = 0.5f;

		if (arrayList.size() < count) {
			count = arrayList.size();
		}

		combinedChart.clear();
		combinedChart.setNoDataText("暂无数据");
		combinedChart.setDescription("");
		combinedChart.setDrawGridBackground(false);
		combinedChart.getLegend().setEnabled(false);
		combinedChart.setDragEnabled(false);
		// combinedChart.setPinchZoom(false);
		combinedChart.setDoubleTapToZoomEnabled(false);

		// draw bars behind lines
		combinedChart.setDrawOrder(new DrawOrder[] { DrawOrder.LINE });

		String[] dates = new String[count];
		for (int i = 0; i < count; i++) {
			long Time = Long.parseLong(arrayList.get(arrayList.size() - 1 - i)
					.get("tradingdate").toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			dates[i] = dateFormat.format(new Date(Time));

		}

		CombinedData data = new CombinedData(dates);

		int[] colors = { Color.WHITE };
		// int[] colors = { Color.WHITE };
		data.setData(generateLineData(arrayList, count, type));

		XAxis xAxis = combinedChart.getXAxis();
		xAxis.setAxisLineColor(Color.LTGRAY);
		xAxis.setGridColor(Color.WHITE);
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setTextColor(Color.BLACK);
		xAxis.setAxisLineWidth(gridWidth);
		xAxis.setGridLineWidth(gridWidth);
		xAxis.setLabelsToSkip(count - 2);
		xAxis.setTextSize(9.0f);
		xAxis.setAvoidFirstLastClipping(true);
		// xAxis.setDrawLabels(false);

		YAxis yAxisLeft = combinedChart.getAxisLeft();
		yAxisLeft.setAxisLineColor(Color.BLACK);
		yAxisLeft.setGridColor(Color.LTGRAY);
		yAxisLeft.setLabelCount(3, false);
		// yAxisLeft.setDrawLabels(false);
		yAxisLeft.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
		yAxisLeft.setAxisMinValue(data.getYMin(AxisDependency.LEFT));
		yAxisLeft.setAxisMaxValue(data.getYMax(AxisDependency.LEFT));
		// yAxisLeft.setAxisMinValue(data.getYMin(AxisDependency.LEFT) -
		// 0.2f);
		// yAxisLeft.setAxisMaxValue(data.getYMax(AxisDependency.LEFT) +
		// 0.2f);
		yAxisLeft.setStartAtZero(false);
		yAxisLeft.setTextColor(Color.BLACK);
		yAxisLeft.setAxisLineWidth(gridWidth);
		yAxisLeft.setGridLineWidth(gridWidth);
		yAxisLeft.setTextSize(9.0f);

		YAxis yAxisRight = combinedChart.getAxisRight();
		yAxisRight.setEnabled(false);
		// yAxisRight.setAxisLineColor(Color.WHITE);
		// yAxisRight.setDrawGridLines(false);
		// yAxisRight.setLabelCount(3, false);
		// yAxisRight.setPosition(YAxisLabelPosition.INSIDE_CHART);
		// yAxisRight.setAxisMinValue(data.getYMin(AxisDependency.RIGHT));
		// yAxisRight.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT));
		// yAxisRight.setStartAtZero(false);
		// yAxisRight.setTextColor(Color.WHITE);
		// yAxisRight.setAxisLineWidth(gridWidth);
		// yAxisRight.setTextSize(9.0f);

		combinedChart.setData(data);
		combinedChart.invalidate();

	}

	// 分时图
	private LineData generateLineData(
			ArrayList<HashMap<String, Object>> arrayList, int dataCount,
			String type) {

		LineData d = new LineData();
		ArrayList<Entry> entries = new ArrayList<Entry>();

		for (int index = 0; index < dataCount; index++) {
			float val = Float.parseFloat(arrayList
					.get(arrayList.size() - 1 - index).get(type).toString());
			entries.add(new Entry(val, index));
		}
		LineDataSet dataSet = new LineDataSet(entries, null);
		dataSet.setDrawFilled(true);
		dataSet.setFillColor(Color.GREEN);
		dataSet.setAxisDependency(AxisDependency.LEFT);
		dataSet.setColor(Color.GREEN);
		dataSet.setDrawCircles(false);
		dataSet.setLineWidth(0.5f);
		dataSet.setDrawCircles(false);
		// dataSet.setDrawCubic(true);
		// dataSet.setCubicIntensity(0.1f);
		dataSet.setDrawValues(false);
		d.addDataSet(dataSet);

		return d;
	}

	/**
	 * 获取历史基金净值
	 */
	private void getNavHistory(String symbol,
 final int position) {
		String url = AppConfig.URL_FUND + "navHistory/" + symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token")
				+ "&pageNo=" + 0 + "&pageSize=" + 60;
		Log.d("历史净值url", "navHistory url:" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						responses.set(position, response);

					}
					// try {
					// Log.d("获取历史净值", "navHistory:" + response.toString());
					// // 如果没有数据
					// if (response.toString().equals("")
					// || response.toString().equals("[0]")) {
					//
					// } else {
					// try {
					// JSONArray all = new JSONArray(response
					// .toString());
					// ArrayList<HashMap<String, Object>> arrayList = new
					// ArrayList<HashMap<String,Object>>();
					// for (int i = 0; i < all.length(); i++) {
					// HashMap<String, Object> data = new HashMap<String,
					// Object>();
					// Iterator<String> jsIterator;
					// try {
					// jsIterator = all.getJSONObject(i)
					// .keys();
					// while (jsIterator.hasNext()) {
					// String key = jsIterator.next();
					// data.put(key,
					// all.getJSONObject(i)
					// .get(key)
					// .toString());
					// }
					// } catch (JSONException e) {
					// e.printStackTrace();
					// }
					// datas.add(data);
					// }
					// viewPager.setCurrentItem(currentItem);
					// } catch (JSONException e) {
					// e.printStackTrace();
					// }
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// }

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							Log.d("error_Exception", e.toString());
						}
					}

				});
		MyApplication.getRequestQueue().add(stringRequest);
	}

}
