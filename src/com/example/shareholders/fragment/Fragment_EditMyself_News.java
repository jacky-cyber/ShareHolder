package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.EditMyselfNewsActivity;
import com.example.shareholders.adapter.EditMyselfNewsAdapter;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_My_MutualFriend.FriendsAdapter;
import com.example.shareholders.jacksonModel.personal.LocalMutualStockFriend;
import com.example.shareholders.jacksonModel.stock.StockNews;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.Mapper;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_EditMyself_News extends Fragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	@ViewInject(R.id.lv_edit_myself_news)
	private ListView editNewsList;

	private EditMyselfNewsAdapter mAdapter;

	// 上下拉刷新
	@ViewInject(R.id.refresh_news)
	private PullToRefreshView refresh;

	//无自选股的界面提示
	@ViewInject(R.id.tv_wuzixuangu)
	private TextView tv_wuzixuangu;
	
	// pageSize,固定为5条话题
	private static int PAGE_SIZE = 10;
	
	//判断是否第一次进来并且更新数据
	private boolean flag = true;

	// pageIndex,从0递增
	private int all_index = 0;

	/** 全部 新闻 */
	private ArrayList<HashMap<String, Object>> allNews;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;
	
	//正在加载的旋转框
	private LoadingDialog loadingDialog;
	//本地数据库
	private DbUtils dbUtils;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_edit_myself_news, null);
		ViewUtils.inject(this, v);
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		dbUtils=DbUtils.create(getActivity());
		loadingDialog = new LoadingDialog(getActivity());
		initView();
		getAllNews(0, 10, HEAD);
		return v;
	}

	private void initView() {
		if (!flag) {
			loadingDialog.showLoadingDialog();
		}
		Log.d("dj_news", "showLoadingDialog()");
		allNews = new ArrayList<HashMap<String, Object>>();
		mAdapter = new EditMyselfNewsAdapter(getActivity(), allNews);
		editNewsList.setAdapter(mAdapter);
		
		// 设置item监听
		editNewsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						EditMyselfNewsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("newsid", allNews.get(position).get("newsid")
						.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onDestroy() {

		MyApplication.getRequestQueue().cancelAll("News1");
		super.onDestroy();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		all_index = 0;
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				getAllNews(all_index, PAGE_SIZE, HEAD);
				refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	/** 储存在数据库的新闻 */
	private List<StockNews> dbNewsArrayList=new ArrayList<StockNews>();
	private void getAllNews(int pageIndex, int pagerSize, final int type) {
		String url = AppConfig.URL_INFO + "new/myConcerned.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url += "&pageIndex=" + pageIndex + "&pageSize=" + pagerSize;

		Log.d("dj_newsaaaa", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_news_response", response.toString());

						flag = false;
						try {
							JSONArray jsonArray = new JSONArray(response);
							dbNewsArrayList.clear();
							Mapper mapper = new Mapper();
							dbUtils.deleteAll(StockNews.class);
							Log.d("db1_news_response", "111111");

							for (int i = 0; i < jsonArray.length(); i++) {
								StockNews news = mapper
										.readValue(jsonArray.get(i).toString(),StockNews.class);
								dbUtils.saveOrUpdate(news);
								dbNewsArrayList.add(news);
								Log.d("4444444_news_response", "success"+dbNewsArrayList.get(i).getTitle());
							}
							Log.d("db2_news_response", "22222");
							
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
								allNews.addAll(datas);
							else {
								allNews.clear();
								allNews.addAll(datas);
							}
							mAdapter.notifyDataSetChanged();
							Log.d("dj_allNews.size()", allNews.size()+"");
							if (allNews.size()==0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}

						} catch (Exception e) {
							Log.d("dj_allNews.size()", allNews.size()+"");
							if (allNews.size()==0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub
						
						try {
							Log.d("db3_news_response", "dddddddddddd333");
							dbNewsArrayList.clear();
							dbNewsArrayList= dbUtils.findAll(StockNews.class);
							Log.d("db3_news_response", dbNewsArrayList.get(0).getTitle());
							if (dbNewsArrayList != null) {
								Log.d("db4_news_response", dbNewsArrayList.toString());
								initFromDb(dbNewsArrayList);
							}
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							Log.d("dj_allNews.size()", allNews.size()+"");
							if (allNews.size()==0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}

						} catch (Exception e1) {
							Log.d("dj_allNews.size()", allNews.size()+"");
							if (allNews.size()==0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						}
						flag = false;
					}
				});

		stringRequest.setTag("News1");
		MyApplication.getRequestQueue().add(stringRequest);
		loadingDialog.dismissDialog();
	}
	private void initFromDb(List<StockNews> list)
	{
		ArrayList<HashMap<String, Object>> news=new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object>  hashMap;
		for(int i=0;i<list.size();i++)
		{
			hashMap=new HashMap<String, Object>();
			hashMap.put("newsid", list.get(i).getNewsid());
			hashMap.put("declaredate", list.get(i).getDeclaredate());
			hashMap.put("title", list.get(i).getTitle());
			hashMap.put("newssummary", list.get(i).getNewssummary());
			hashMap.put("listedCompanyShortName",
					list.get(i).getListedCompanyShortName());
			hashMap.put("symbol", list.get(i).getSymbol());
			news.add(hashMap);
		}
		EditMyselfNewsAdapter adapter = new EditMyselfNewsAdapter(getActivity(), news);
		editNewsList.setAdapter(adapter);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				getAllNews(++all_index, PAGE_SIZE, FOOT);
				refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}

}
