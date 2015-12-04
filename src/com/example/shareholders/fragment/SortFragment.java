package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.adapter.SurveyAdapter;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class SortFragment extends Fragment implements OnHeaderRefreshListener,
OnFooterRefreshListener {


	// 无活动时的提示
	@ViewInject(R.id.ll_wuhuodong)
	private LinearLayout ll_wuhuodong;

	@ViewInject(R.id.lv_survey_list)
	private MyListView lv_survey_list;

	// 上下拉刷新
	@ViewInject(R.id.sort_pulltorefresh)
	private PullToRefreshView mPullToRefreshView;

	private int pageIndex = 0;
	private int pageSize = 15;
	public int currentPage = 0;// 当前页数
	private int totalPages = 0;

	private   ArrayList<HashMap<String, String>>  filter_datas = new ArrayList<HashMap<String, String>>();

	private AlertDialog mDialog = null;
	private boolean lock=false;

	/**
	 * 接口
	 */

	RequestQueue volleyRequestQueue;
	private BitmapUtils bitmapUtils = null;

	// 是否从筛选界面返回
	private boolean isFilter = false;

	// Activity中的ViewPager
	private MyViewPager myViewPager;
	private FilterFragment filterFragment;

	private boolean filter_showDialog = false;
	private SurveyAdapter listadapter;


	private LoadingDialog loadingDialog ;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_sort, container, false);
		ViewUtils.inject(this, view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		loadingDialog = new LoadingDialog(getActivity());
		

		return view;
	}

	/**
	 * 判断是否从筛选界面返回 如果是，加载筛选条件下的信息 否则，直接获取最近调研的信息
	 */
	private void decideFilter() {
		isFilter = RsSharedUtil.getBoolean(getActivity(), "filter", false);
		// isFilter=false;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		bitmapUtils = BitmapUtilFactory.getInstance();
		ll_wuhuodong.setVisibility(View.GONE);
		mPullToRefreshView
		.setVisibility(View.VISIBLE);

		// 一开始不筛选
		RsSharedUtil.putBoolean(getActivity(), "filter", false);
		// 默认筛选条件为时间顺序
		RsSharedUtil.putString(getActivity(), "filter_sortType", "timeDesc");
		// 默认调研状态为全部
		RsSharedUtil.putString(getActivity(), "filter_surveyState", "null");

		loadingDialog.showLoadingDialog();
		filter_datas.clear();
		ll_wuhuodong.setVisibility(View.GONE);
		setLatestInfo(pageIndex, pageSize);
//		new Thread(new Runnable(
//				) {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//
//				        try {
//							Thread.sleep(300);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}   
//			}
//		}).run();
	    Reflash();
		super.onActivityCreated(savedInstanceState);
	}



	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 令listView滑动到顶端
	 */
	public void setListViewTop() {
		lv_survey_list.setSelection(0);
	}

	/**
	 * 清空所有数据
	 */
	public void clearDatas() {
		filter_datas.clear();
		currentPage = 0;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * 获取ViewPager中的filterFragment
	 */
	private void getFilterFragment() {
		myViewPager = (MyViewPager) getActivity().findViewById(R.id.vp_list);
		ViewPagerAdapter adapter = (ViewPagerAdapter) myViewPager.getAdapter();
		filterFragment = (FilterFragment) adapter.instantiateItem(myViewPager,
				1);
	}

	/**
	 * 获取筛选条件,返回请求的参数params
	 */
	private JSONObject getFilterCondition() {
		filter_datas.clear();
		getFilterFragment();

		ArrayList<String> selectCitiesCodeList = filterFragment
				.getSelectCitiesCode();

		ArrayList<HashMap<String, String>> selectIndustryCode = filterFragment
				.getSelectIndustryList();
		// Toast.makeText(getActivity(), selectIndustryCode.toString(),
		// 1).show();

		JSONObject params = new JSONObject();

		try {
			// 开始时间参数
			params.put("startDate",
					RsSharedUtil.getString(getActivity(), "filter_startDate"));
			// 结束时间参数
			params.put("endDate",
					RsSharedUtil.getString(getActivity(), "filter_endDate"));

			/**
			 * 城市代码
			 */
			if (selectCitiesCodeList.size() > 0) {
				JSONArray citiesCode = new JSONArray();
				for (int i = 0; i < selectCitiesCodeList.size(); i++) {
					citiesCode.put(selectCitiesCodeList.get(i));
				}
				params.put("locationCode", citiesCode);
			}

			/**
			 * 行业代码
			 */
			if (selectIndustryCode.size() > 0) {
				JSONArray IndustryCode = new JSONArray();
				for (int i = 0; i < selectIndustryCode.size(); i++) {
					IndustryCode.put(selectIndustryCode.get(i).get(
							"industryCode"));
				}
				params.put("industryCode", IndustryCode);
			}

			/**
			 * 排序方式
			 */

			params.put("sortType",
					RsSharedUtil.getString(getActivity(), "filter_sortType"));

			/**
			 * 调研状态 如果调研状态为"null"，即不需要对调研状态进行筛选
			 */

			if (!RsSharedUtil.getString(getActivity(), "filter_surveyState")
					.equals("null")) {
				params.put("surveyState", RsSharedUtil.getString(getActivity(),
						"filter_surveyState"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return params;

	}

	public void setFilterInfo(int pageSize, int pageIndex) {

		if (filter_showDialog) {
			loadingDialog.showLoadingDialog();
			filter_showDialog = false;
		}
		// Toast.makeText(getActivity(), "setFilterInfo_start", 1).show();
		String url = AppConfig.URL_SURVEY + "search.json?";
		url =url+"&access_token="+ RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("421url", url);
		JSONObject params = getFilterCondition();
		try {
			params.put("pageSize", pageSize);
			params.put("pageIndex", pageIndex);

			params.put("sortType",
					RsSharedUtil.getString(getActivity(), "filter_sortType"));

			// 如果调研状态为"null"，即不需要对调研状态进行筛选
			if (!RsSharedUtil.getString(getActivity(), "filter_surveyState")
					.equals("null")) {
				params.put("surveyState", RsSharedUtil.getString(getActivity(),
						"filter_surveyState"));
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Toast.makeText(getActivity(), "" + params.toString(), 1).show();
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				lock=true;
				if (response.equals("")) {
					ll_wuhuodong.setVisibility(View.VISIBLE);
					mPullToRefreshView.setVisibility(View.GONE);
					 listadapter = new SurveyAdapter(
							getActivity(),
							new ArrayList<HashMap<String, String>>());
					lv_survey_list.setAdapter(listadapter);
				} else {
					currentPage++;

					try {
						JSONObject jsonObject = new JSONObject(response);

						JSONObject page_jsonObject = new JSONObject(
								jsonObject.getString("pageable"));
						totalPages = Integer.parseInt(page_jsonObject
								.get("totalPages").toString());

						JSONArray jsonArray = new JSONArray(jsonObject
								.getString("surveys"));

						for (int i = 0; i < jsonArray.length(); i++) {
							HashMap<String, String> data = new HashMap<String, String>();
							Iterator<String> jIterator;
							jIterator = jsonArray.getJSONObject(i)
									.keys();

							while (jIterator.hasNext()) {
								String key = jIterator.next();
								data.put(key, jsonArray
										.getJSONObject(i)
										.getString(key));
							}
							if(RsSharedUtil.getString(getActivity(), "filter_surveyState").equals("null")||RsSharedUtil.getString(getActivity(), "filter_surveyState")==null){
								filter_datas.add(data);
							}
							else{
								if(	RsSharedUtil.getString(getActivity(), "filter_surveyState").equals(data.get("state"))){
									filter_datas.add(data);
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (filter_datas.size() == 0) {
					ll_wuhuodong.setVisibility(View.VISIBLE);
					mPullToRefreshView.setVisibility(View.GONE);
				} else {
					ll_wuhuodong.setVisibility(View.GONE);
					mPullToRefreshView
					.setVisibility(View.VISIBLE);
				}
				loadingDialog.dismissDialog();
				lock=false;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(error.data());
					String description = jsonObject
							.getString("description");
					Log.d("liang_error", description);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 获取最近调研的后台数据
	 */
	public void setLatestInfo(int pageIndex, int pageSize) {
		// Log.d("liang_datas_size_start", no_filter_datas.size() + "");
		filter_datas.clear();
		ll_wuhuodong.setVisibility(View.GONE);
		String url = AppConfig.URL_SURVEY + "search.json?";
		url =url+"&access_token="+ RsSharedUtil.getString(getActivity(), "access_token");
		JSONObject params = new JSONObject();
		try {

			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
			params.put("sortType",
					RsSharedUtil.getString(getActivity(), "filter_sortType"));

			// 如果调研状态为"null"，即不需要对调研状态进行筛选
			if (!RsSharedUtil.getString(getActivity(), "filter_surveyState")
					.equals("null")) {
				params.put("surveyState", RsSharedUtil.getString(getActivity(),
						"filter_surveyState"));
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		// 制定post请求
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.POST, url, params,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						filter_datas.clear();
						lock=true;
						if (response.toString() != ""
								&& response.toString() != "[0]") {
							currentPage++;
						}
						
						JSONObject jsonObject = null;

						JSONArray jsonArray = null;
						try {
							jsonArray = new JSONArray(response
									.getString("surveys"));

							jsonObject = new JSONObject(response
									.getString("pageable"));
							totalPages = Integer.parseInt(jsonObject.get(
									"totalPages").toString());

						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

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
							if(RsSharedUtil.getString(getActivity(), "filter_surveyState").equals("null")||RsSharedUtil.getString(getActivity(), "filter_surveyState")==null){
								filter_datas.add(data);
							}else{
								if(	RsSharedUtil.getString(getActivity(), "filter_surveyState").equals(data.get("state"))){
									filter_datas.add(data);
								}
							}

						
							
						}
						if (filter_datas.size() == 0) {
							ll_wuhuodong.setVisibility(View.VISIBLE);
							mPullToRefreshView.setVisibility(View.GONE);
						} else {
							ll_wuhuodong.setVisibility(View.INVISIBLE);
							ll_wuhuodong.setVisibility(View.GONE);
							mPullToRefreshView
							.setVisibility(View.VISIBLE);
						}
						loadingDialog.dismissDialog();
						lock=false;
						
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

		volleyRequestQueue.add(jsonObjectRequest);

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

	@Override
	public void onFooterRefresh(PullToRefreshView view) {

		// mPullToRefreshView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// mPullToRefreshView.onFooterRefreshComplete();
		// }
		// }, 1000);

		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!RsSharedUtil.getBoolean(getActivity(), "filter", false)) {
					if (currentPage < totalPages) {
						setLatestInfo(currentPage, pageSize);
						Reflash();
					}
				} else {
					if (currentPage < totalPages) {
						setFilterInfo(pageSize, currentPage);
						Reflash();
					}
				}

				mPullToRefreshView.onFooterRefreshComplete();

			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (RsSharedUtil.getBoolean(getActivity(), "filter", false)) {
					clearDatas();
					setFilterInfo(pageSize, 0);
					Reflash();
					currentPage = 0;
				} else {
					clearDatas();
					setLatestInfo(0, pageSize);
					Reflash();
					currentPage = 0;
				}
				mPullToRefreshView.onHeaderRefreshComplete();

			}
		}, 1000);

	}

	public void setFilterShowDialog(boolean filter_showDialog) {
		this.filter_showDialog = filter_showDialog;
	}
public void Reflash(){
	ll_wuhuodong.setVisibility(View.GONE);
	while(lock);
	listadapter = new SurveyAdapter(
			getActivity(), filter_datas);
	lv_survey_list.setAdapter(listadapter);
	listadapter.notifyDataSetChanged();
	lv_survey_list.invalidateViews();      //强行刷新
	lv_survey_list
	.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(
				AdapterView<?> arg0, View arg1,
				int positon, long arg3) {
			Intent intent = new Intent(
					getActivity(),
					DetailSurveyActivity.class);

			Bundle bundle = new Bundle();
			bundle.putString("uuid",
					filter_datas.get(positon)
					.get("uuid"));

			intent.putExtras(bundle);

			startActivity(intent);

		}
	});
	
	if (lv_survey_list.getCount() == 0) {
//		ll_wuhuodong.setVisibility(View.VISIBLE);
		mPullToRefreshView.setVisibility(View.GONE);
	} else {
		ll_wuhuodong.setVisibility(View.GONE);
		mPullToRefreshView
		.setVisibility(View.VISIBLE);
	}

	filter_datas.clear();
}
}
