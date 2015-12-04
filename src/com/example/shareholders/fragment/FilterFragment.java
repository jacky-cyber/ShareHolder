package com.example.shareholders.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.SurveyListActivity;
import com.example.shareholders.adapter.SortAdapter;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.CharacterParser;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyGridView;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.common.PinyinComparator;
import com.example.shareholders.common.SideBar;
import com.example.shareholders.common.SideBar.OnTouchingLetterChangedListener;
import com.example.shareholders.common.SortModel;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.example.shareholders.view.ActionSheetDialog;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

//假如用到位置提醒功能，需要import该类

public class FilterFragment extends Fragment {

	String keyCodeString = "";

	@ViewInject(R.id.gridview_industry)
	private MyGridView gridview_industry;

	@ViewInject(R.id.tv_start_date)
	private TextView tv_start_date;

	@ViewInject(R.id.tv_end_date)
	private TextView tv_end_date;

	@ViewInject(R.id.sv_industry)
	private ScrollView sv_industry;

	// 显示或收起城市列表的imageview
	@ViewInject(R.id.iv_show_cities)
	private ImageView iv_show_cities;

	// 城市列表的容器Framelayout
	@ViewInject(R.id.fl_all_cities)
	private FrameLayout fl_all_cities;

	// 所有城市
	@ViewInject(R.id.tv_all_cities)
	private TextView tv_all_cities;

	// 定位后显示的位置
	@ViewInject(R.id.tv_fixed_location)
	private TextView tv_fixed_location;

	// 定位时旋转的图标
	@ViewInject(R.id.iv_search_location)
	private ImageView iv_search_location;

	// // 选择搜索的城市
	// @ViewInject(R.id.tv_change_city)
	// private TextView tv_change_city;

	// 根据行业选择的gridview数据
	private List<String> industry_name_lists;
	private List<Boolean> isClick_lists;
	// 重新定位
	@ViewInject(R.id.tv_relocate)
	private TextView tv_relocate;
	private List<Map<String, String>> industry_hashMap_lists;

	@ViewInject(R.id.rl_cities)
	private RelativeLayout rl_cities;

	/**
	 * 所有城市
	 */
	boolean selectAllCities = false;

	@ViewInject(R.id.dialog)
	private TextView dialog;
	// 全部城市列表
	@ViewInject(R.id.lv_city)
	private ListView lv_city;
	// 右边的bar
	@ViewInject(R.id.sidrbar)
	private SideBar sideBar;
	// 分类城市适配器
	private CityAdapter adapter;
	// 是否显示所有城市列表
	private boolean showCities = false;

	// 显示所选择的城市名称
	@ViewInject(R.id.gv_selected_cities)
	private GridView gv_selected_cities;

	// 热门城市列表的容器
	@ViewInject(R.id.ll_heat_cities)
	private LinearLayout ll_heat_cities;
	// 热门城市列表
	@ViewInject(R.id.gv_heat_cities)
	private GridView gv_heat_cities;

	private Animation rotateAinm = null;
	private RequestQueue volleyRequestQueue;

	// 不能选择超过5个的提示框
	private AlertDialog mDialog = null;

	/**
	 * 用于关闭提示框
	 */
	private boolean closeAutom = true; // 是否自动关闭
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (closeAutom && msg.what == 0) {
				mDialog.dismiss();
			}
		};
	};

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<String> allCitiesList = new ArrayList<String>(); // 城市名称的集合
	private ArrayList<String> citiesCodeList = new ArrayList<String>(); // 城市代码的集合
	private ArrayList<String> allIndustryList = new ArrayList<String>(); // 行业名称的集合
	private ArrayList<String> industryCodeList = new ArrayList<String>(); // 行业代码的集合

	private ArrayList<String> selectCitiesList = new ArrayList<String>(); // 选择的城市集合
	private ArrayList<String> selectCitiesCodeList = new ArrayList<String>(); // 选择的城市代码集合

	public static ArrayList<HashMap<String, String>> selectIndustryList = new ArrayList<HashMap<String, String>>(); // 最终选择的行业的代码集合
	private ArrayList<Boolean> selectIndustryCodeList = new ArrayList<Boolean>(); // 选择的行业的代码的集合

	private selectCitiesAdapter selectAdapter;

	private ArrayList<String> heatCitiesList = new ArrayList<String>(); // 热门城市的集合
	private ArrayList<String> heatCitiesCodeList = new ArrayList<String>(); // 热门城市代码的集合

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	/***********************************************************/
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	WheelMain wheelMain;
	View timepickerview;

	/**********************************************************/
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		// int span = 1000;
		// option.setScanSpan(span);//
		// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	// 模拟所有城市
	private void mockUpAllCitites() {
		String url = AppConfig.URL_SURVEY + "city/all.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {

						final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

						for (int i = 0; i < response.length(); i++) {
							HashMap<String, String> data = new HashMap<String, String>();
							Iterator<String> jIterator;
							try {
								jIterator = response.getJSONObject(i).keys();

								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, response.getJSONObject(i)
											.get(key).toString());
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							datas.add(data);

						}

						for (int i = 0; i < datas.size(); i++) {
							allCitiesList.add(datas.get(i).get("locationName")
									.toString());
							citiesCodeList.add(datas.get(i).get("locationCode")
									.toString());
						}

						initViews();
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
				});

		volleyRequestQueue.add(jsonArrayRequest);
	}

	/**
	 * 字母栏
	 */
	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					lv_city.setSelection(position);
				}

			}
		});

		lv_city.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 已选择城市达到五个，不作处理
				if (selectCitiesList.size() >= 5) {
					mDialog = new AlertDialog.Builder(getActivity()).create();
					mDialog.show();

					// 提示框出现时，计时，三秒后发送handler消息
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessageDelayed(msg, 3000);

					mDialog.setCancelable(false);
					mDialog.getWindow().setContentView(
							R.layout.dialog_create_activity_layout);

					ImageView iv = (ImageView) mDialog.getWindow()
							.findViewById(R.id.iv_gou);
					Button btn_complete = (Button) mDialog.getWindow()
							.findViewById(R.id.btn_inform_confirm);
					TextView tv_message = (TextView) mDialog.getWindow()
							.findViewById(R.id.tv_inform_dialog_content);

					iv.setImageResource(R.drawable.ico_gantanhao);
					tv_message.setText(getResources().getString(
							R.string.cities_limit));

					btn_complete.setText(getResources().getString(
							R.string.confirm));

					btn_complete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							closeAutom = false; // 不自动关闭提示框
							mDialog.dismiss();
						}
					});

					return;
				}

				/**
				 * 去除所有城市
				 */
				if (selectAllCities) {
					selectAllCities = false;
					selectCitiesList.clear();
					selectAdapter.notifyDataSetChanged();
				}

				/**
				 * 遍历已选择城市的列表
				 */
				int i = 0;
				for (i = 0; i < selectCitiesList.size(); i++) {
					if (((SortModel) adapter.getItem(position)).getName()
							.equals(selectCitiesList.get(i))) {
						break;
					}
				}

				// 如果已存在,不作处理
				if (i < selectCitiesList.size()) {
					return;
				}

				selectCitiesList.add(((SortModel) adapter.getItem(position))
						.getName());
				// selectCitiesCodeList.add(heatCitiesCodeList.get(position));

				/**
				 * 遍历城市数组，找出对应的城市代码
				 */

				getCityCode(((SortModel) adapter.getItem(position)).getName());

				selectAdapter.notifyDataSetChanged();

			}

			private void getCityCode(String city) {
				int i = 0;
				for (i = 0; i < allCitiesList.size(); i++) {
					if (city.equals(allCitiesList.get(i))) {
						break;
					}
				}

				// ToastUtils.showToast(getActivity(), "" +
				// citiesCodeList.get(i));
				selectCitiesCodeList.add(citiesCodeList.get(i));

			}
		});

		SourceDateList = filledData(allCitiesList);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new CityAdapter(getActivity(), SourceDateList);
		lv_city.setAdapter(adapter);

		// 根据输入框输入值的改变来过滤搜索
		// filter_edit.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		// filterData(s.toString());
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });
	}

	/**
	 * 填充数据
	 * 
	 * @param Data
	 * @return
	 */
	private List<SortModel> filledData(List<String> Data) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < Data.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(Data.get(i));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(Data.get(i));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_filter_layout,
				container, false);

		ViewUtils.inject(this, view);

		volleyRequestQueue = Volley.newRequestQueue(getActivity());

		initView();

		initGridViewInfo(); // 设置GridView的数据
		initHeatGridViewInfo(); // 获取热门城市的后台数据

		mLocationClient = new LocationClient(getActivity()
				.getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

		// 定位
		initLocation();
		mLocationClient.start();

		/**
		 * 加载所有城市
		 */

		mockUpAllCitites();

		return view;

	}

	/**
	 * 重新进入这个fragment时，所有选择状态都清除
	 */
	public void initAllInfo() {
		/**
		 * 选择城市的gridview的数据全部清除
		 */
		selectCitiesList.clear();
		selectCitiesCodeList.clear();
		selectAdapter.notifyDataSetChanged();

		/**
		 * 当选择城市的部分正在显示时，隐藏
		 */
		if (showCities) {
			showCities = !showCities;
			iv_show_cities.setImageResource(R.drawable.ico_wanxiala);
			rl_cities.setVisibility(View.GONE);
			sv_industry.setVisibility(View.VISIBLE);
		}

		/**
		 * 选择的行业都清除
		 */
		for (int i = 0; i < industryCodeList.size(); i++) {
			selectIndustryCodeList.set(i, false);
		}
		selectIndustryCodeList.set(0, true);
		selectIndustryList.clear();
		((MyGridViewAdapter) (gridview_industry.getAdapter()))
		.notifyDataSetChanged();

		/**
		 * 开始时间和结束时间设置为原来的时间
		 */
		tv_start_date.setText("2010.01.01");
		tv_end_date.setText("2015.12.12");

		tv_start_date.setTextColor(getResources().getColor(R.color.time_color));
		tv_end_date.setTextColor(getResources().getColor(R.color.time_color));

		// ToastUtils.showToast(getActivity(), selectCitiesList.size() + "");
	}

	private void initHeatGridViewInfo() {
		String url = AppConfig.URL_SURVEY + "city/heat.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> data = null;
						Iterator<String> jIterator;

						for (int i = 0; i < response.length(); i++) {
							try {
								jIterator = response.getJSONObject(i).keys();
								data = new HashMap<String, String>();

								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, response.getJSONObject(i)
											.get(key).toString());
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							datas.add(data);
						}
						for (int i = 0; i < datas.size(); i++) {
							heatCitiesList
							.add(datas.get(i).get("locationName"));
							heatCitiesCodeList.add(datas.get(i).get(
									"locationCode"));
						}
						//						gv_heat_cities.setAdapter(new HeatCitiesAdapter(
						//								getActivity()));
					}
				}, null);

		volleyRequestQueue.add(jsonArrayRequest);

	}

	/**
	 * 进行定位
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			tv_fixed_location.setText(location.getCity());
			if (rotateAinm != null) {
				iv_search_location.clearAnimation();
			}
		}
	}

	private void initView() {
		tv_all_cities.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		http: // 120.24.254.176:8080/shareholder-server/api/v1.0/survey/lastest.json?access_token=732a057d-bb40-4fac-878e-5676218a6808&pageSize=5&pageIndex=0
			tv_fixed_location.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		selectAdapter = new selectCitiesAdapter(getActivity());

		gv_selected_cities.setAdapter(selectAdapter);
	}

	@OnClick({ R.id.tv_start_date, R.id.tv_end_date, R.id.tv_confirm,
		R.id.rl_return, R.id.tv_all_cities, R.id.tv_fixed_location,
		R.id.tv_relocate, R.id.rl_show_city })
	public void onClick(View v) {
		switch (v.getId()) {
		// 重新定位
		case R.id.tv_relocate:
			// 定位时图标旋转的动画
			rotateAinm = AnimationUtils.loadAnimation(getActivity(),
					R.anim.rotate_location);
			// 动画结束后图标变回原样
			rotateAinm.setFillAfter(false);
			iv_search_location.startAnimation(rotateAinm);
			// rotateAinm.startNow();

			mLocationClient = new LocationClient(getActivity()
					.getApplicationContext()); // 声明LocationClient类
			mLocationClient.registerLocationListener(myListener); // 注册监听函数

			// 定位
			initLocation();
			mLocationClient.start();
			break;
			// 选择开始日期
		case R.id.tv_start_date:
			setWheelMain(tv_start_date);

			new ActionSheetDialog(getActivity(), wheelMain, tv_start_date,tv_end_date,
					"choose_time").builder().setTitle("请选择日期")
					.setCancelable(true).setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,1).show();

			break;

			// 选择结束日期
		case R.id.tv_end_date:
			setWheelMain(tv_end_date);

			new ActionSheetDialog(getActivity(), wheelMain,tv_start_date, tv_end_date,
					"choose_time").builder().setTitle("请选择日期")
					.setCancelable(true).setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,2).show();

			break;

			// 确定
		case R.id.tv_confirm:

			setFilterCondition();

			// SurveyListActivity.vp_list.setCurrentItem(0);
			((SurveyListActivity) getActivity()).clearAllTab();

			MyViewPager myViewPager = (MyViewPager) getActivity().findViewById(
					R.id.vp_list);
			ViewPagerAdapter adapter = (ViewPagerAdapter) myViewPager
					.getAdapter();

			SortFragment sortFragment = (SortFragment) adapter.instantiateItem(
					myViewPager, 0);
			RsSharedUtil.putBoolean(getActivity(), "filter", true);
			sortFragment.clearDatas();
			sortFragment.setFilterInfo(sortFragment.getPageSize(), 0);
			sortFragment.Reflash();
			sortFragment.setFilterShowDialog(true);
			sortFragment.setCurrentPage(0);
			myViewPager.setCurrentItem(0);
			/**
			 * popupWindow的选择条件全部设为默认
			 */
			// if (((SurveyListActivity) getActivity()).getClick()) {
			// ((SurveyListActivity) getActivity()).setClicks();
			// }

			break;

			// 所有城市
		case R.id.tv_all_cities:
			/**
			 * 选择城市的gridview的数据全部清除
			 */
			selectCitiesList.clear();
			selectCitiesList.add(getActivity().getResources().getString(
					R.string.all_city));
			selectAllCities = true;

			selectCitiesCodeList.clear();
			selectAdapter.notifyDataSetChanged();
			break;
			// 定位城市
		case R.id.tv_fixed_location:
			// tv_change_city.setText(tv_fixed_location.getText());
			break;

			// 显示或者收起城市列表
		case R.id.rl_show_city:
			if (showCities) {
				showCities = !showCities;
				iv_show_cities.setImageResource(R.drawable.ico_wanxiala);
				rl_cities.setVisibility(View.GONE);
				sv_industry.setVisibility(View.VISIBLE);
			} else {
				showCities = !showCities;
				iv_show_cities.setImageResource(R.drawable.ico_shanglajiantou2);
				rl_cities.setVisibility(View.VISIBLE);
				sv_industry.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}

	private void setFilterCondition() {
		RsSharedUtil.putString(getActivity(), "filter_startDate",
				transformTimeFormat(tv_start_date.getText().toString()));
		RsSharedUtil.putString(getActivity(), "filter_endDate",
				transformTimeFormat(tv_end_date.getText().toString()));

		// 进行条件筛选后，默认筛选结果按时间正序，调研状态为全部
		// RsSharedUtil.putString(getActivity(), "filter_surveyState", "null");
		// RsSharedUtil.putString(getActivity(), "filter_sortType", "timeDesc");
	}

	/**
	 * 设置时间滚轮的信息
	 */
	private void setWheelMain(TextView tv) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		timepickerview = inflater.inflate(R.layout.timepicker2, null);
		ScreenInfo screenInfo = new ScreenInfo(getActivity());
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = transformTimeFormat(tv.getText().toString());
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
	}

	/**
	 * 为gridview设置adapter
	 */
	private void initGridView() {
		gridview_industry.setAdapter(new MyGridViewAdapter(getActivity(),
				allIndustryList));
	}

	/**
	 * 讲yyyy.mm.dd转换为yyyy-mm-dd
	 * 
	 * @param time
	 * @return
	 */
	private String transformTimeFormat(String time) {
		int begin = 0;
		int end = 0;

		String returnTime = "";
		for (int i = 0; i < time.length(); i++) {
			if (time.charAt(i) == '.') {
				end = i;
				returnTime += time.substring(begin, end) + "-";
				begin = i + 1;
			}

			if (i == time.length() - 1) {
				end = time.length();
				returnTime += time.substring(begin, end);
			}
		}
		return returnTime.trim();
	}

	/**
	 * 设置gridView显示的信息
	 */
	private void initGridViewInfo() {

		String url = AppConfig.URL_SURVEY + "industry/all.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
						Iterator<String> jIterator;
						HashMap<String, String> data = null;
						for (int i = 0; i < response.length(); i++) {
							try {
								jIterator = response.getJSONObject(i).keys();
								data = new HashMap<String, String>();

								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, response.getJSONObject(i)
											.get(key).toString());
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							datas.add(data);
						}

						// 加上全部行业
						allIndustryList.add("全部行业");
						industryCodeList.add("0");
						selectIndustryCodeList.add(true);

						// Log.d("lele_datas", datas.toString());
						// 把数据分别放到行业名称和行业代码的集合中
						for (int i = 0; i < datas.size(); i++) {
							industryCodeList.add(datas.get(i).get(
									"industryCode"));
							allIndustryList.add(""
									+ datas.get(i).get("industryName"));

							selectIndustryCodeList.add(false);
						}

						initGridView();

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
				});

		volleyRequestQueue.add(jsonArrayRequest);
	}

	/**
	 * 获取选择的城市列表
	 * 
	 * @return
	 */
	public ArrayList<String> getSelectCitiesCode() {
		return selectCitiesCodeList;
	}

	/**
	 * 获取选择的城市列表
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getSelectIndustryList() {
		return selectIndustryList;
	}

	class MyGridViewAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private Context context;
		private ArrayList<String> lists;
		private boolean FilterAllIndustry = true; // 是否对全部行业进行筛选

		public MyGridViewAdapter(Context context, ArrayList<String> lists) {
			this.context = context;
			this.lists = lists;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allIndustryList.size();
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
		public View getView(final int position, View converView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = inflater.inflate(R.layout.item_gridview_industry,
						arg2, false);
				viewHolder.tv = (TextView) converView
						.findViewById(R.id.tv_industry_name);
				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.tv.setText((CharSequence) allIndustryList.get(position));

			if (selectIndustryCodeList.get(position)) {// 被选中
				viewHolder.tv
				.setBackgroundResource(R.drawable.btn_industry_selected_style);
				viewHolder.tv.setTextColor(context.getResources().getColor(
						R.color.white));
			} else { // 未被选中
				viewHolder.tv
				.setBackgroundResource(R.drawable.btn_industry_style);
				viewHolder.tv.setTextColor(context.getResources().getColor(
						R.color.gridview_item_color));
			}

			viewHolder.tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View tv) {

					// ToastUtils.showToast(getActivity(),
					// industryCodeList.get(position));

					boolean shouldNorify = false;

					// 如果选中全部行业，其他行业的状态清除
					if (position == 0) {
						for (int i = 0; i < selectIndustryCodeList.size(); i++) {
							selectIndustryCodeList.set(i, false);
						}
						selectIndustryCodeList.set(position, true);
						selectIndustryList.clear();
						notifyDataSetChanged();
						return;
					}

					// 如果所选行业多余5个，且点击的标签是未选的，弹出提示框
					if (selectIndustryList.size() >= 5
							&& !selectIndustryCodeList.get(position)) {

						mDialog = new AlertDialog.Builder(getActivity())
						.create();
						mDialog.show();

						// 提示框出现时，计时，三秒后发送handler消息
						Message msg = new Message();
						msg.what = 0;
						mHandler.sendMessageDelayed(msg, 3000);

						mDialog.setCancelable(false);
						mDialog.getWindow().setContentView(
								R.layout.dialog_create_activity_layout);

						ImageView iv = (ImageView) mDialog.getWindow()
								.findViewById(R.id.iv_gou);
						Button btn_complete = (Button) mDialog.getWindow()
								.findViewById(R.id.btn_inform_confirm);
						TextView tv_message = (TextView) mDialog.getWindow()
								.findViewById(R.id.tv_inform_dialog_content);

						iv.setImageResource(R.drawable.ico_gantanhao);
						tv_message.setText(getResources().getString(
								R.string.industry_limit));

						btn_complete.setText(getResources().getString(
								R.string.confirm));

						btn_complete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								closeAutom = false; // 不自动关闭提示框
								mDialog.dismiss();
							}
						});

						return;

					}

					// 如果选中其他行业，全部行业的选中状态去除
					if (selectIndustryCodeList.get(0)) {
						selectIndustryCodeList.set(0, false);
						shouldNorify = true;
					}

					selectIndustryCodeList.set(position,
							!(selectIndustryCodeList.get(position)));

					// ToastUtils.showToast(getActivity(),
					// "true "+selectIndustryCodeList.get(position));
					if (selectIndustryCodeList.get(position)) { // 未选中变为选中

						// 把选中的城市加到集合当中
						HashMap<String, String> industry = new HashMap<String, String>();
						industry.put("position", "" + position);
						industry.put("industryCode",
								industryCodeList.get(position));
						selectIndustryList.add(industry);

						((TextView) (tv))
						.setBackgroundResource(R.drawable.btn_industry_selected_style);
						((TextView) (tv)).setTextColor(context.getResources()
								.getColor(R.color.white));
					} else {
						for (int i = 0; i < selectIndustryList.size(); i++) {
							if (selectIndustryList.get(i).get("position")
									.equals("" + position)) {
								selectIndustryList.remove(i);
								break;
							}
						}

						((TextView) (tv))
						.setBackgroundResource(R.drawable.btn_industry_style);
						((TextView) (tv)).setTextColor(context.getResources()
								.getColor(R.color.gridview_item_color));
					}

					if (shouldNorify) {
						notifyDataSetChanged();
					}

				}
			});

			return converView;
		}

		class ViewHolder {
			TextView tv;
		}

	}

	/**
	 * 显示选择的城市
	 * 
	 * @author Administrator
	 * 
	 */
	class selectCitiesAdapter extends BaseAdapter {

		private Context context;
		LayoutInflater inflater;

		public selectCitiesAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return selectCitiesList.size();
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
		public View getView(final int position, View converView, ViewGroup arg2) {

			ViewHolder viewHolder = null;

			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = inflater.inflate(R.layout.item_select_city, arg2,
						false);
				viewHolder.tv = (TextView) converView
						.findViewById(R.id.tv_city);
				viewHolder.iv = (ImageView) converView
						.findViewById(R.id.iv_delete);
				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.tv.setText(selectCitiesList.get(position));
			viewHolder.tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if(selectAllCities){
						selectAllCities=false;
						selectCitiesList.clear();
						notifyDataSetChanged();
					}
					else{
						selectCitiesList.remove(position);
						selectCitiesCodeList.remove(position);
						notifyDataSetChanged();
					}
				}
			});

			return converView;
		}

		class ViewHolder {
			TextView tv;
			ImageView iv;
		}

	}


	class HeatCitiesAdapter extends BaseAdapter {

		private Context context;

		public HeatCitiesAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {

			return heatCitiesList.size();
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
		public View getView(final int position, View converView, ViewGroup arg2) {

			final TextView tv = new TextView(context);
			tv.setText(heatCitiesList.get(position));
			tv.setTextSize(13);
			tv.setTextColor(context.getResources().getColor(
					R.color.current_survey_text));
			tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// ToastUtils.showToast(getActivity(),
					// heatCitiesCodeList.get(position));
					// 已选择城市达到五个，不作处理
					if (selectCitiesList.size() >= 5) {
						mDialog = new AlertDialog.Builder(getActivity())
						.create();
						mDialog.show();
						// 提示框出现时，计时，三秒后发送handler消息
						Message msg = new Message();
						msg.what = 0;
						mHandler.sendMessageDelayed(msg, 3000);
						mDialog.setCancelable(false);
						mDialog.getWindow().setContentView(
								R.layout.dialog_create_activity_layout);

						ImageView iv = (ImageView) mDialog.getWindow()
								.findViewById(R.id.iv_gou);
						Button btn_complete = (Button) mDialog.getWindow()
								.findViewById(R.id.btn_inform_confirm);
						TextView tv_message = (TextView) mDialog.getWindow()
								.findViewById(R.id.tv_inform_dialog_content);

						iv.setImageResource(R.drawable.ico_gantanhao);
						tv_message.setText(getResources().getString(
								R.string.cities_limit));

						btn_complete.setText(getResources().getString(
								R.string.confirm));

						btn_complete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								closeAutom = false; // 不自动关闭提示框
								mDialog.dismiss();
							}
						});

						return;
					}

					/**
					 * 去除所有城市
					 */
					if (selectAllCities) {
						selectAllCities = false;
						selectCitiesList.clear();
						selectAdapter.notifyDataSetChanged();
						
						
					}

					/**
					 * 遍历已选择城市的列表
					 */
					int i = 0;
					for (i = 0; i <selectCitiesList.size(); i++) {
						if (tv.getText().toString()
								.equals(selectCitiesList.get(i))) {
							break;
						}
					}

					// 如果已存在,不作处理
					if (i < selectCitiesList.size()) {
						return;
					}

					selectCitiesList.add(tv.getText().toString());
					selectCitiesCodeList.add(heatCitiesCodeList.get(position));
					selectAdapter.notifyDataSetChanged();

				}
			});

			return tv;
		}

	}

	class CityAdapter extends BaseAdapter implements SectionIndexer {

		private List<SortModel> list = null;
		private ArrayList<Boolean> isClickList;
		private Context mContext;
		private HeatCitiesAdapter heatAdapter;
		private SortAdapter sortAdapter;
		private HeatCityHeadAdapter head;
		public CityAdapter(Context mContext, List<SortModel> list) {
			this.mContext = mContext;
			this.head=new HeatCityHeadAdapter(mContext);
			isClickList = new ArrayList<Boolean>();
			this.list = list;
			for (int i = 0; i < list.size(); i++) {
				isClickList.add(false);
			}

		}

		/**
		 * 当ListView数据发生变化时,调用此方法来更新ListView
		 * 
		 * @param list
		 */
		public void updateListView(List<SortModel> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			// boolean isClick=false;
			if (position==0) {
				return (head.getView(position, view, arg2));
			}else {

				viewHolder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(R.layout.area_item,
						null);
				viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
				viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
				view.setTag(viewHolder);

				final SortModel mContent = list.get(position-1);
				// 根据position获取分类的首字母的Char ascii值
				int section = getSectionForPosition(position-1);

				// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
				if (position == getPositionForSection(section)) {
					viewHolder.tvLetter.setVisibility(View.VISIBLE);
					viewHolder.tvLetter.setText(mContent.getSortLetters());
				} else {
					viewHolder.tvLetter.setVisibility(View.GONE);
				}

				viewHolder.tvTitle.setText(this.list.get(position-1).getName());

				return view;
			}
		}

		class ViewHolder {
			TextView tvLetter;
			TextView tvTitle;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {
			return list.get(position).getSortLetters().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i+1;
				}
			}

			return -1;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		private String getAlpha(String str) {
			String sortStr = str.trim().substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortStr.matches("[A-Z]")) {
				return sortStr;
			} else {
				return "#";
			}
		}

		@Override
		public Object[] getSections() {
			return null;
		} 




	}
	class HeatCityHeadAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;

		public HeatCityHeadAdapter(Context context) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return heatCitiesList.size();
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

		class ViewHolder {
			GridView gv;
		}


		@Override
		public View getView(final int position, View converView, ViewGroup arg2) {
			ViewHolder viewHolder = null;

			viewHolder = new ViewHolder();
			converView = inflater.inflate(R.layout.heat_city_item,
					arg2, false);

			viewHolder.gv = (GridView) converView
					.findViewById(R.id.gv_heat_cities);

			viewHolder.gv.setAdapter(new HeatCitiesAdapter(
					getActivity()));
			return converView;
		}

	}


}
