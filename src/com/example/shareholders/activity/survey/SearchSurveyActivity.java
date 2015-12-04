package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.shareholders.R;
import com.example.shareholders.adapter.SortAdapter;
import com.example.shareholders.common.CharacterParser;
import com.example.shareholders.common.MyGridView;
import com.example.shareholders.common.PinyinComparator;
import com.example.shareholders.common.SideBar;
import com.example.shareholders.common.SideBar.OnTouchingLetterChangedListener;
import com.example.shareholders.common.SortModel;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_search_survey)
public class SearchSurveyActivity extends Activity {
	@ViewInject(R.id.rl_confirm)
	private RelativeLayout rl_confirm;
	// 选择的城市
	@ViewInject(R.id.tv_location_content)
	private TextView tv_location_content;
	// 当前城市
	@ViewInject(R.id.tv_location)
	private TextView tv_location;
	@ViewInject(R.id.dialog)
	private TextView dialog;
	// 全部城市列表
	@ViewInject(R.id.lv_city)
	private ListView lv_city;
	// 右边的bar
	@ViewInject(R.id.sidrbar)
	private SideBar sideBar;
	// 分类城市适配器
	private SortAdapter adapter;
	// 热门城市表
	@ViewInject(R.id.mv_cities)
	private MyGridView mv_cities;
	// 城市名称
	private String locationName;
	// 城市代码
	private String locationCode;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	// 默认城市
	@ViewInject(R.id.tv_default_city)
	private TextView tv_default_city;
	private String defaultCode;
	// 默认城市
	private String defaultCity;
	private RequestQueue volleyRequestQueue;

	/**
	 * 进行定位
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			tv_location.setText(location.getCity());
			// Log.d("广州", location.getCityCode());
		}
	}

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

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<String> allCitiesList = new ArrayList<String>();
	private List<String> allCodesList = new ArrayList<String>();
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(this);
		// 初始化热门城市
		initHeatGridViewInfo();
		initGridViewInfo();
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		// 定位
		initLocation();
		mLocationClient.start();
		Bundle bundle = getIntent().getExtras();
		try {
			// 获取默认城市名称和城市代码
			defaultCity = bundle.getString("locationName");
			defaultCode = bundle.getString("locationCode");
			Log.d("locationName", bundle.getString("locationName"));
			Log.d("locationCode", bundle.getString("locationCode"));
			if (!defaultCity.trim().equals("")) {
				tv_location_content.setText(defaultCity);
				tv_default_city.setText(defaultCity);
				locationCode = defaultCode;
			}
		} catch (Exception e) {
			defaultCity = "";
			defaultCode = "";
		}
	}

	private void initHeatGridViewInfo() {
		String url = AppConfig.URL_SURVEY + "city/heat.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						// Toast.makeText(getActivity(), response.toString(),
						// 1).show();
						final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
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
						PopularCitiesAdapter popularCitiesAdapter = new PopularCitiesAdapter(
								SearchSurveyActivity.this, datas);
						mv_cities.setAdapter(popularCitiesAdapter);
						mv_cities
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
										if (!BtnClickUtils.isFastDoubleClick())
											tv_location_content.setText(datas
													.get(position).get(
															"locationName"));
										locationName = datas.get(position).get(
												"locationName");
										locationCode = datas.get(position).get(
												"locationCode");
									}
								});
					}
				}, null);

		volleyRequestQueue.add(jsonArrayRequest);

	}

	/**
	 * 设置gridView显示的信息
	 */
	private void initGridViewInfo() {

		String url = AppConfig.URL_SURVEY + "city/all.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						// ToastUtils.showToast(getActivity(),
						// response.toString());
						// Toast.makeText(getActivity(),response.toString(),1).show();
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

						// Log.d("lele_datas", datas.toString());
						// 把数据分别放到行业名称和行业代码的集合中
						for (int i = 0; i < datas.size(); i++) {
							allCitiesList.add(""
									+ datas.get(i).get("locationName"));
							allCodesList.add(""
									+ datas.get(i).get("locationCode"));
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
	 * 通过城市名称获取城市代码
	 * 
	 * @param locationName
	 */
	private void getLocationCode(String locationName) {
		String url = AppConfig.URL_SURVEY + "city/query.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		JSONObject params = new JSONObject();
		try {
			params.put("locationName", locationName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url,
				params, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							Log.d("error.statuCode()", response.toString() + "");
							locationCode = response.getString("locationCode")
									.toString();
							tv_location_content.setText(tv_location.getText()
									.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("error.statuCode()", error.toString() + "");
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
		volleyRequestQueue.add(jsonObjectRequest);
	}

	@OnClick({ R.id.rl_return, R.id.tv_relocate, R.id.tv_location,
			R.id.tv_default_city, R.id.rl_confirm })
	private void onClick(View v) {
		switch (v.getId()) {
		// 确认
		case R.id.rl_confirm:
			if (!tv_location_content.equals("")) {
				Intent intent = new Intent();
				intent.putExtra("locationName", tv_location_content.getText()
						.toString());
				intent.putExtra("locationCode", tv_location_content.getText()
						.toString());
				SearchSurveyActivity.this.setResult(1, intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "请选择城市！",
						Toast.LENGTH_SHORT);
			}
			break;
		// 点击默认城市
		case R.id.tv_default_city:
			tv_location_content.setText(tv_default_city.getText().toString());
			locationName = defaultCity;
			locationCode = defaultCode;
			break;
		// 点击当前城市
		case R.id.tv_location:
			// 获取城市代码并设置当前城市
			Log.d("哈哈哈哈h", tv_location.getText().toString());
			getLocationCode(tv_location.getText().toString());
			break;
		// 重新定位
		case R.id.tv_relocate:
			mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
			mLocationClient.registerLocationListener(myListener); // 注册监听函数
			// 定位
			initLocation();
			mLocationClient.start();
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	class PopularCitiesAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> popular_cities;

		public PopularCitiesAdapter(Context context,
				ArrayList<HashMap<String, String>> popular_cities) {
			this.context = context;
			this.popular_cities = popular_cities;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return popular_cities.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.item_popular_city, null);
			}
			TextView tv_city = AbViewHolder.get(convertView, R.id.tv_city);
			tv_city.setText(popular_cities.get(position).get("locationName"));
			return convertView;
		}

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
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				if (!BtnClickUtils.isFastDoubleClick())
					tv_location_content.setText(((SortModel) adapter
							.getItem(position)).getName());
				locationName = ((SortModel) adapter.getItem(position))
						.getName();
				locationCode = allCodesList.get(position);
			}
		});

		SourceDateList = filledData(allCitiesList);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		lv_city.setAdapter(adapter);
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

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
}
