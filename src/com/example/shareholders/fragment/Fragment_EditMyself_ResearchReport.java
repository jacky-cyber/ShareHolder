package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.EditMyselfResearchReportActivity;
import com.example.shareholders.adapter.EditMyselfResearchReportAdapter;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_EditMyself_ResearchReport extends Fragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	@ViewInject(R.id.lv_edit_myself_news)
	private ListView editRearchReportList;

	private EditMyselfResearchReportAdapter mAdapter;

	// 上下拉刷新
	@ViewInject(R.id.refresh_news)
	private PullToRefreshView refresh;

	// 无自选股的界面提示
	@ViewInject(R.id.tv_wuzixuangu)
	private TextView tv_wuzixuangu;
	
	// pageSize,固定为5条话题
	private static int PAGE_SIZE = 10;

	// pageIndex,从0递增
	private int all_index = 0;

	/** 全部 新闻 */
	private ArrayList<HashMap<String, Object>> allNews;

	// 正在加载的旋转框
	private LoadingDialog loadingDialog;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_edit_myself_news, null);
		ViewUtils.inject(this, v);
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		loadingDialog = new LoadingDialog(getActivity());
		initView();
		init(0, 10, HEAD);
		return v;
	}

	private void initView() {
		loadingDialog.showLoadingDialog();
		Log.d("dj_researchReport", "showLoadingDialog()");
		allNews = new ArrayList<HashMap<String, Object>>();
		mAdapter = new EditMyselfResearchReportAdapter(getActivity(), allNews);
		editRearchReportList.setAdapter(mAdapter);
	}

	private void init(int pageIndex, int pageSize, final int type) {
		String url = AppConfig.URL_INFO
				+ "report/myConcerned.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url += "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;

		Log.d("dj_researchReport", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_researchReport_response", response.toString());

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

							if (type == FOOT) {
								allNews.addAll(datas);
								Log.d("allNewsallNews", "" + allNews.size());
							} else {
								allNews.clear();
								allNews.addAll(datas);
							}
							mAdapter.notifyDataSetChanged();

							// 设置item监听
							editRearchReportList
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long arg3) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													getActivity(),
													EditMyselfResearchReportActivity.class);
											Bundle bundle = new Bundle();
											bundle.putString(
													"reportId",
													allNews.get(position)
															.get("reportId")
															.toString());
											intent.putExtras(bundle);

											startActivity(intent);
										}
									});
							
							Log.d("dj_researchReport.size()", allNews.size()+"");
							if (allNews.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_researchReport",
									e.toString());
							Log.d("dj_researchReport.size()", allNews.size()+"");
							if (allNews.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_researchReport",
									jsonObject.toString());
							
							Log.d("dj_researchReport.size()", allNews.size()+"");
							if (allNews.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("dj_researchReport.size()", allNews.size()+"");
							if (allNews.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						}

					}
				});

		stringRequest.setTag("ResearchReport");
		MyApplication.getRequestQueue().add(stringRequest);
		loadingDialog.dismissDialog();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("ResearchReport");

		super.onDestroy();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		all_index = 0;
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				init(all_index, PAGE_SIZE, HEAD);
				refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				init(++all_index, PAGE_SIZE, FOOT);
				refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}
}
