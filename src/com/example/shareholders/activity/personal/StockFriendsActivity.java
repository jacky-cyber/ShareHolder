/*package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.ShareAndFriendsSearchActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.SideBarFoudSearch;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.common.SideBarFoudSearch.OnTouchingLetterChangedListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_stock_friends)
public class StockFriendsActivity extends Activity implements
OnHeaderRefreshListener, OnFooterRefreshListener {

	@ViewInject(R.id.tv_follow)
	private TextView tv_follow;
	@ViewInject(R.id.tv_not_follow)
	private TextView tv_not_follow;
	@ViewInject(R.id.tv_follow_each_other)
	private TextView tv_follow_each_other;
	@ViewInject(R.id.lv_friends)
	private ListView lv_friends;
	// 按字母搜索的sidebar
	@ViewInject(R.id.sb_fs_sidebar)
	private SideBarFoudSearch sideBar;
	// 搜索显示的字母
	@ViewInject(R.id.tv_fs_dialog1)
	private TextView tv_fs_dialog1;

	private FriendsAdapter adapter;

	private RequestQueue volleyRequestQueue;
	
	// 上下拉刷新
	@ViewInject(R.id.refresh1)
	private PullToRefreshView refresh1;
	
	//全局数据
	private ArrayList<HashMap<String, String>> allDatas;
	
	// 当前选中的tab
	private int currentTab=0;

	// pageSize,固定为5个好友
	private static int PAGE_SIZE = 10;
	// pageIndex,从0递增
	private int mutual_index = 0;
	private int follow_index = 0;
	private int followed_index = 0;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;
	ImageLoader iLoader;
	
	private ArrayList<HashMap<String, String>> mutualFriends;
	private ArrayList<HashMap<String, String>> followFriends;
	private ArrayList<HashMap<String, String>> followedFriends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initData();
		initTag(mutualFriends);
	}

	private void initData() {
		
		refresh1.setOnHeaderRefreshListener(this);
		refresh1.setOnFooterRefreshListener(this);
		iLoader = ImageLoader.getInstance();
		iLoader.resume();

		lv_friends.setOnScrollListener(new OnScrollListener() {

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
		
		allDatas= new ArrayList<HashMap<String, String>>();
		mutualFriends = new ArrayList<HashMap<String, String>>();
		followFriends = new ArrayList<HashMap<String, String>>();
		followedFriends = new ArrayList<HashMap<String, String>>();
		volleyRequestQueue = Volley.newRequestQueue(this);		
		adapter = new FriendsAdapter(getApplicationContext(), mutualFriends);
		lv_friends.setAdapter(adapter);
		//OnTab(0);
		new AsyncgGetFriend().execute();
	}

	@OnClick({ R.id.title_note, R.id.title_search, R.id.tv_follow,
			R.id.tv_not_follow, R.id.tv_follow_each_other })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.title_search:
			Intent intent = new Intent(StockFriendsActivity.this,ShareAndFriendsSearchActivity.class);
			startActivity(intent);
			RsSharedUtil.putBoolean(getApplicationContext(), AppConfig.SET_PAGE, true);
			break;
		case R.id.tv_follow_each_other:
			currentTab=0;
			OnTab(0);
			break;
		case R.id.tv_follow:
			currentTab=1;
			OnTab(1);
			break;
		case R.id.tv_not_follow:
			currentTab=2;
			OnTab(2);
			break;
		
		default:
			break;
		}
	}

	private void OnTab(int num) {
		clearTab();
		switch (num) {
		case 0:
			tv_follow_each_other.setBackgroundResource(R.drawable.bg_tab_selected);
			setHashMaps(num);
			break;
		case 1:
			tv_follow.setBackgroundResource(R.drawable.bg_tab_selected);
			setHashMaps(num);
			break;
		case 2:
			tv_not_follow.setBackgroundResource(R.drawable.bg_tab_selected);
			setHashMaps(num);
			break;
		default:
			break;
		}

	}

	private void setHashMaps(int tag) {
		switch (tag) {
		case 0:
				initTag(mutualFriends);
				adapter = new FriendsAdapter(this, mutualFriends);
				lv_friends.setAdapter(adapter);
		
			break;
		case 1:
				initTag(followFriends);
				adapter = new FriendsAdapter(this, followFriends);
				lv_friends.setAdapter(adapter);

			break;
		case 2:
				initTag(followedFriends);
				adapter = new FriendsAdapter(this, followedFriends);
				lv_friends.setAdapter(adapter);
			break;

		default:
			break;
		}

	}

	private void clearTab() {
		tv_follow.setBackgroundResource(R.drawable.bg_tab_unselected);
		tv_not_follow.setBackgroundResource(R.drawable.bg_tab_unselected);
		tv_follow_each_other.setBackgroundResource(R.drawable.bg_tab_unselected);
	}

	private void initTag(final ArrayList<HashMap<String, String>> friendList) {
		sideBar.setTextView(tv_fs_dialog1);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				try {
					int position = new FriendsAdapter(getApplicationContext(),
							friendList).getPositionForSection(s.charAt(0));
					if (position != -1) {
						lv_friends.setSelection(position);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("StockFriendsActivity");
		super.onDestroy();
	}

	private class AsyncgGetFriend extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// 获取数据
			getMutualFriend(0,10,HEAD);
			getFollowFriend(0,10,HEAD);
			getFollowedFriend(0,10,HEAD);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}

	private void getMutualFriend(int pageIndex,int pageSize,final int type) {
		String url = AppConfig.VERSION_URL
				+ "user/follow/list.json?access_token=";
		url += RsSharedUtil.getString(getApplication(), AppConfig.ACCESS_TOKEN);
		url = url + "&type=MUTUAL&userUuid=myself&pageIndex="+pageIndex+"&pageSize="+pageSize;
		Log.d("MutualFriend_url", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("MutualFriend Success", response);
						try {
							JSONObject jsonobject = new JSONObject(response);
							JSONArray jsonArray = jsonobject
									.getJSONArray("users");
							final ArrayList<HashMap<String, String>> datas 
											= new ArrayList<HashMap<String, String>>();
							HashMap<String, String> data = null;
							Iterator<String> iterator = null;
							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
									
								}
								datas.add(data);
							}
								
								if (type == FOOT)
									mutualFriends.addAll(datas);
								else 
								{
									mutualFriends.clear();
									mutualFriends.addAll(datas);
								}
								//updateList(0);
								adapter.notifyDataSetChanged();

							Log.d("MutualFriends List",
									mutualFriends.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//Log.d("MutualFriend error", error.data().toString());
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	private void getFollowFriend(int pageIndex,int pageSize,final int type) {
		String url = AppConfig.VERSION_URL
				+ "user/follow/list.json?access_token=";
		url += RsSharedUtil.getString(getApplication(), AppConfig.ACCESS_TOKEN);
		url = url + "&type=FOLLOW" + "&userUuid=myself&pageIndex="+pageIndex+"&pageSize="+pageSize;

		Log.d("FollowFriend", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("FollowFriend Success", response);
						try {
							final ArrayList<HashMap<String, String>> datas 
								= new ArrayList<HashMap<String, String>>();
							JSONObject jsonobject = new JSONObject(response);
							JSONArray jsonArray = jsonobject
									.getJSONArray("users");
							HashMap<String, String> data = null;
							Iterator<String> iterator = null;
							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								datas.add(data);
							}
							
							if (type == FOOT)
								followFriends.addAll(datas);
							else 
							{
								followFriends.clear();
								followFriends.addAll(datas);
							}
							adapter.notifyDataSetChanged();
							//updateList(0);				
							Log.d("followFriends List",
									followedFriends.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//Log.d("FollowFriend error", error.data().toString());
					}
				});
		volleyRequestQueue.add(stringRequest);
	}

	private void getFollowedFriend(int pageIndex,int pageSize,final int type) {
		String url = AppConfig.VERSION_URL
				+ "user/follow/list.json?access_token=";
		url += RsSharedUtil.getString(getApplication(), AppConfig.ACCESS_TOKEN);
		url = url + "&type=FOLLOWED&userUuid=myself&pageIndex="+pageIndex+"&pageSize="+pageSize;
		Log.d("FollowedFriend_url", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("FollowedFriend success", response);

						try {
							final ArrayList<HashMap<String, String>> datas 
									= new ArrayList<HashMap<String, String>>();
							JSONObject jsonobject = new JSONObject(response);
							JSONArray jsonArray = jsonobject
									.getJSONArray("users");
							HashMap<String, String> data = null;
							Iterator<String> iterator = null;
							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								datas.add(data);
							}
							
							if (type == FOOT)
								followedFriends.addAll(datas);
							else 
							{
								followedFriends.clear();
								followedFriends.addAll(datas);
							}
							adapter.notifyDataSetChanged();
							//updateList(0);
							Log.d("followedFriends List",
									followedFriends.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//Log.d("FollowedFriend  error", error.data().toString());
					}
				});
		volleyRequestQueue.add(stringRequest);
	}
	
	
	
	public void updateList(int position) {
		adapter.notifyDataSetChanged();
		if (currentTab == position) {
			setHashMaps(position);
		}
	}

	public class FriendsAdapter extends BaseAdapter implements SectionIndexer {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private LayoutInflater mInflater;

		FriendsAdapter(Context context, ArrayList<HashMap<String, String>> datas) {
			this.list = datas;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return list.size();
			if (list==null) {
				return 0;
			}
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater
						.inflate(R.layout.item_friend_list, null);

				holder.ci_image = (CircleImageView) contentView
						.findViewById(R.id.ci_friend_figure);
				holder.tv_name = (TextView) contentView
						.findViewById(R.id.tv_userName);
				holder.tv_industry = (TextView) contentView
						.findViewById(R.id.tv_industry);
				holder.tv_position = (TextView) contentView
						.findViewById(R.id.tv_location);

				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}

			ImageLoader.getInstance().displayImage(
					list.get(position).get("userLogo"), holder.ci_image);
			holder.tv_name.setText((CharSequence) list.get(position).get(
					"userName"));
			holder.tv_industry.setText((CharSequence) list.get(position).get(
					"industryName"));
			holder.tv_position.setText((CharSequence) list.get(position).get(
					"locationName"));
			return contentView;
		}

		class ViewHolder {

			CircleImageView ci_image;
			TextView tv_name;
			TextView tv_industry;
			TextView tv_position;
		}

 *//**
 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
 */
/*
 * @Override public int getPositionForSection(int section) { // TODO
 * Auto-generated method stub // for (int i = 0; i < getCount(); i++) { //
 * String sortStr = (String) list.get(i).get("pyName"); // char firstChar =
 * sortStr.toUpperCase().charAt(0); // if (firstChar == section) { // return i;
 * // } // }
 * 
 * return -1;
 * 
 * }
 * 
 * @Override public int getSectionForPosition(int position) { // TODO
 * Auto-generated method stub return ((String)
 * (list.get(position).get("pyName"))).charAt(0); }
 * 
 * @Override public Object[] getSections() { // TODO Auto-generated method stub
 * return null; }
 * 
 * public void updateListView(ArrayList<HashMap<String, String>> list) {
 * this.list = list; notifyDataSetChanged(); }
 * 
 * public void clear() { list.clear(); notifyDataSetChanged(); } }
 * 
 * @Override public void onFooterRefresh(PullToRefreshView view) {
 * 
 * switch (currentTab) { case 0: refresh1.postDelayed(new Runnable() {
 * 
 * @Override public void run() { getMutualFriend(++mutual_index, PAGE_SIZE,
 * FOOT); refresh1.onFooterRefreshComplete(); } }, 2000); break; case 1:
 * refresh1.postDelayed(new Runnable() {
 * 
 * @Override public void run() { getFollowFriend(++follow_index, PAGE_SIZE,
 * FOOT); refresh1.onFooterRefreshComplete(); } }, 2000); break; case 2:
 * refresh1.postDelayed(new Runnable() {
 * 
 * @Override public void run() { getFollowFriend(++followed_index, PAGE_SIZE,
 * FOOT); refresh1.onFooterRefreshComplete(); } }, 2000); break; } }
 * 
 * @Override public void onHeaderRefresh(PullToRefreshView view) { switch
 * (currentTab) { case 0: mutual_index=0; refresh1.postDelayed(new Runnable() {
 * 
 * @Override public void run() { getMutualFriend(mutual_index, PAGE_SIZE, HEAD);
 * refresh1.onFooterRefreshComplete(); } }, 2000); break; case 1:
 * follow_index=0; refresh1.postDelayed(new Runnable() {
 * 
 * @Override public void run() { getFollowFriend(follow_index, PAGE_SIZE, HEAD);
 * refresh1.onFooterRefreshComplete(); } }, 2000); break; case 2:
 * followed_index=0; refresh1.postDelayed(new Runnable() {
 * 
 * @Override public void run() { getFollowFriend(followed_index, PAGE_SIZE,
 * HEAD); refresh1.onFooterRefreshComplete(); } }, 2000); break; }
 * 
 * } }
 */