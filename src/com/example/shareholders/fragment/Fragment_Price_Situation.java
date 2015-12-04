package com.example.shareholders.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.stock.ShareAndFriendsSearchActivity;
import com.example.shareholders.adapter.FriendListViewAdapter;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyHScrollView;
import com.example.shareholders.common.MyHScrollView.OnScrollChangedListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

//行情Fragment
public class Fragment_Price_Situation extends Fragment {

	@ViewInject(R.id.iv_nothing)
	ImageView iv_nothing;
	JSONArray postarray;// 从1.5.1中获得的关注股票代码
	ArrayList<HashMap<String, String>> listhead;// 股票名称和代码
	ArrayList<HashMap<String, String>> datas;// 1.5.2返回的数据
	ListView mListView1;
	MyAdapter myAdapter;
	RelativeLayout mHead;
	LinearLayout main;
	View mview;
	View item_view;
	RadioGroup radioGroup;
	String aget = new String();
	// 是否排序
	private int ifSort;
	// 是否实时更新
	private boolean ifposthandler = false;
	private Handler mHandler;
	private ArrayList<HashMap<String, String>> share_hashMaps;
	private RotateAnimation animation;
	// 加载旋转框
	private LoadingDialog loadingDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		postarray = new JSONArray();
		listhead = new ArrayList<HashMap<String, String>>();
		datas = new ArrayList<HashMap<String, String>>();
		// mHandler = new Handler();
		mview = inflater.inflate(R.layout.fragment_share_list, container, false);
		item_view = (View) mview.findViewById(R.id.fragment_share_list_head);
		radioGroup = (RadioGroup) item_view.findViewById(R.id.share_radiogroup);
		ViewUtils.inject(this, mview);
		loadingDialog = new LoadingDialog(getActivity());

		ifSort = -1;
		mHead = (RelativeLayout) mview.findViewById(R.id.fragment_share_list_head);
		mHead.setFocusable(true);
		mHead.setClickable(true);
		// mHead.setBackgroundColor(Color.parseColor("#b2d235"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		mListView1 = (ListView) mview.findViewById(R.id.fsl_share_list);

		// 跳到MyStockDetailsActivity并传入ArrayList

		mListView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
				ArrayList<HashMap<String, String>> stocks = listhead;
				intent.putExtra("stocks", stocks); 
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});

		/*
		 * mListView1.setOnItemLongClickListener(new OnItemLongClickListener() {
		 * 
		 * @Override public boolean onItemLongClick(AdapterView<?> arg0, View
		 * arg1, int position, long arg3) { // TODO Auto-generated method stub
		 * Intent intent = new Intent(getActivity(),
		 * MyStockDetailsActivity.class); ArrayList<HashMap<String, String>>
		 * stocks = listhead; intent.putExtra("stocks", stocks);
		 * intent.putExtra("position", position); startActivity(intent); return
		 * true; } });
		 */
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		// 禁止listview下拉
		mListView1.setOverScrollMode(View.OVER_SCROLL_NEVER);

		myAdapter = new MyAdapter(getActivity(), datas, R.layout.item_fragment_share_list_content);
		// initpost();
		mHandler = new Handler();
		// 切换到这个fragment时也加了一次导致有一些情况下加了两次,先去除实时刷新
		// mHandler.post(mRunnable);
		return mview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// 注册增加、取消关注广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("situation_update");
		getActivity().registerReceiver(receiver, intentFilter);

		/*
		 * // 注册停止更新广播 IntentFilter intentFilter2 = new IntentFilter();
		 * intentFilter.addAction("stop_situation_update");
		 * getActivity().registerReceiver(receiver2, intentFilter2);
		 */

		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.d("onResume", "onResume");
		ifSort = -1;
		initpost();

		TimerTask ta = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (ifposthandler) {
					mHandler.post(mRunnable);
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(ta, 500);

		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.d("onPause", "onPause");
		radioGroup.clearCheck();
		mHandler.removeCallbacks(mRunnable);
		super.onPause();
	}

	public void postrunnable() {
		ifposthandler = true;
		mHandler.post(mRunnable);
	}

	public void remvoerunnable() {
		Log.d("ertyuiop", "ertyu");
		ifposthandler = false;
		mHandler.removeCallbacks(mRunnable);
	}

	/*
	 * boolean isvisibleToUser = true;// 进fragment为true
	 * 
	 * @Override public void setUserVisibleHint(boolean isVisibleToUser) { //
	 * TODOAuto-generated method stub super.setUserVisibleHint(isVisibleToUser);
	 * Log.d("cjsetUserVisibleHint", "cjsetUserVisibleHint"); try { if
	 * (isVisibleToUser) { Log.d("isVisibleToUser", "isVisibleToUser"); if
	 * (!isvisibleToUser) isvisibleToUser = true; } else {
	 * Log.d("!isVisibleToUser", "!isVisibleToUser"); if (isvisibleToUser) {
	 * isvisibleToUser = false; } } if (!isvisibleToUser) {
	 * Log.d("isvisibleToUser", "isvisibleToUser");
	 * mHandler.removeCallbacks(mRunnable); } } catch (Exception e) { // TODO:
	 * handle exception Log.d("popipo", e.toString()); } }
	 */

	public void initpost() {
		// 1.5.1
		String url = AppConfig.VERSION_URL + "quotation/myStocks.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");

		// 未登录，跳转到notlogin
		if (RsSharedUtil.getString(getActivity(), "access_token").equals("")) {
			/*
			 * // 发广播跳转到notlogin页面 Intent intent = new Intent(); // 要发送的内容
			 * intent.setAction("notlogin"); // 设置广播的action
			 * getActivity().sendBroadcast(intent); // 发送广播
			 */
			iv_nothing.setVisibility(View.VISIBLE);
			mListView1.setVisibility(View.GONE);
		} else {
			iv_nothing.setVisibility(View.GONE);
			mListView1.setVisibility(View.VISIBLE);
		}
		// 显示正在加载
		// loadingDialog.showLoadingDialog();
		Log.d("initpost1.5.1url", url);
		StringRequest stringRequest1 = new StringRequest(url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				loadingDialog.dismissDialog();
				if (response.equals("") || response.equals("[0]")) {
					Log.d("initpostlele_no_content", "No Content");
					iv_nothing.setVisibility(View.VISIBLE);
					mListView1.setVisibility(View.GONE);
				} else {
					iv_nothing.setVisibility(View.GONE);
					mListView1.setVisibility(View.VISIBLE);
					Log.d("initpostlele_zixun", response.toString());
					try {
						listhead.clear();
						JSONArray all = new JSONArray(response);
						postarray = new JSONArray();
						HashMap<String, String> headdata = null;
						Log.d("all", all.toString());
						JSONObject post;
						Iterator<String> iterator = null;
						for (int i = 0; i < all.length(); i++) {
							headdata = new HashMap<String, String>();
							post = new JSONObject();

							iterator = all.getJSONObject(i).keys();
							while (iterator.hasNext()) {
								String key = iterator.next();
								Log.d("key", key);
								headdata.put(key, all.getJSONObject(i).get(key).toString());
							}
							post.put("symbol", all.getJSONObject(i).get("symbol").toString());
							post.put("securityType", all.getJSONObject(i).get("securityType").toString());
							listhead.add(headdata);
							Log.d("listhead", listhead.toString());
							// post.put("shortname",
							// all.getJSONObject(i).get("shortname").toString());
							datas.add(headdata);
							postarray.put(post);
						}
						initsituation();
						myAdapter = new MyAdapter(getActivity(), datas, R.layout.item_fragment_share_list_content);
						mListView1.setAdapter(myAdapter);
						myAdapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Log.d("initpostfalse", "fffffffff");
						e.printStackTrace();
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("initpostlele_error", error.toString());
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("initpostlele_error", jsonObject.get("description").toString());

				} catch (Exception e) {
					Log.d("initpostlele_error", "未知错误");
				}

			}
		});
		stringRequest1.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest1);
		myAdapter.notifyDataSetChanged();
	}

	// 接收增加、取消关注广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			// 1.5.1
			Log.d("123receiver", "receiver");
			String url = AppConfig.VERSION_URL + "quotation/myStocks.json?access_token=";
			url += RsSharedUtil.getString(getActivity(), "access_token");
			Log.d("1.5.1url", url);
			StringRequest stringRequest3 = new StringRequest(url, null, new Listener<String>() {

				@Override
				public void onResponse(String response) {

					if (response.equals("") || response.equals("[0]")) {
						iv_nothing.setVisibility(View.VISIBLE);
						mListView1.setVisibility(View.GONE);
						Log.d("llele_no_content", "No Content");
					} else {
						iv_nothing.setVisibility(View.GONE);
						mListView1.setVisibility(View.VISIBLE);
						Log.d("onReceive1.5.1respnoe", response.toString());
						try {
							listhead.clear();
							JSONArray all = new JSONArray(response);
							postarray = new JSONArray();
							HashMap<String, String> headdata = null;
							Log.d("all", all.toString());
							JSONObject post;
							Iterator<String> iterator = null;

							for (int i = 0; i < all.length(); i++) {
								Log.d("erererq", "yyyyy");
								headdata = new HashMap<String, String>();
								post = new JSONObject();
								iterator = all.getJSONObject(i).keys();

								while (iterator.hasNext()) {
									String key = iterator.next();
									Log.d("key", key);
									headdata.put(key, all.getJSONObject(i).get(key).toString());
								}
								post.put("symbol", all.getJSONObject(i).get("symbol").toString());
								post.put("securityType", all.getJSONObject(i).get("securityType").toString());
								listhead.add(headdata);
								post.put("shortname", all.getJSONObject(i).get("shortname").toString());
								datas.add(headdata);
								postarray.put(post);
							}
							Log.d("repostarray", postarray.toString());
							initsituation();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Log.d("onReceivefalse", e.toString());
							e.printStackTrace();
						}
					}
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Log.d("lele_error", error.toString());
					try {
						JSONObject jsonObject = new JSONObject(error.data());
						Log.d("lele_error", jsonObject.get("description").toString());

					} catch (Exception e) {
						Log.d("lele_error", "未知错误");
					}
				}
			});
			stringRequest3.setTag("stringRequest");
			MyApplication.getRequestQueue().add(stringRequest3);
			myAdapter.notifyDataSetChanged();
		}

	};

	public void initsituation() {
		// 1.5.2
		Log.d("listhead", listhead.toString());
		Log.d("postarray", postarray.toString());

		String url = AppConfig.VERSION_URL + "quotation/newestPrice.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("1.5.2url", url);
		Log.d("1.5.2postarray", postarray.toString());
		StringRequest stringRequest2 = new StringRequest(postarray, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("1.5.2_response", response.toString());
				if (response.equals("") || response.equals("[0]")) {
					Log.d("lele_no_content", "No Content");
				} else {
					Log.d("lele_zixun", response.toString());
					try {
						JSONArray all = new JSONArray(response);

						Log.d("all", all.toString());
						datas.clear();
						HashMap<String, String> data = null;
						Iterator<String> iterator = null;
						for (int i = 0; i < all.length(); i++) {

							data = new HashMap<String, String>();

							iterator = all.getJSONObject(i).keys();

							while (iterator.hasNext()) {
								String key = iterator.next();
								Log.d("key", key);
								if (key == "shortname") {
									// 不获取shortname字段,shortname从1.5.1获得的list里拿
								} else {
									data.put(key, all.getJSONObject(i).get(key).toString());
								}
							}
							data.put("shortname", listhead.get(i).get("shortname"));
							datas.add(data);
						}
						Log.d("cccccccccccccccccc", "ccccccccc");
						Log.d("1.5.2datas", datas.toString());

						// 根据点击的排序
						switch (ifSort) {
						case 1:
							Collections.sort(datas, new SortBynowPrice());
							break;
						case 2:
							Collections.sort(datas, new SortByzhangdie());
							break;
						case 3:
							Collections.sort(datas, new SortByzhangfu());
							break;
						case 4:
							Collections.sort(datas, new SortByvolume());
							break;
						case 5:
							Collections.sort(datas, new SortByamount());
							break;
						case 6:
							Collections.sort(datas, new SortByhighPrice());
							break;
						case 7:
							Collections.sort(datas, new SortBylowPrice());
							break;
						case 8:
							Collections.sort(datas, new SortByzhenfu());
							break;
						case 9:
							Collections.sort(datas, new SortByturnOverRate());
							break;
						case 10:
							Collections.sort(datas, new SortByepg());
							break;
						case 11:
							Collections.sort(datas, new SortBytotalMarketValue());
							break;
						default:
							break;
						}
						myAdapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Log.d("false1.5.2", e.toString());
						e.printStackTrace();
					}
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("lele_error", error.toString());
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("lele_error", jsonObject.get("description").toString());
				} catch (Exception e) {
					Log.d("lele_error", "未知错误");
				}

			}
		});
		stringRequest2.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	// 已经可以成功设置点击
	@OnClick({ R.id.share_new, R.id.share_updown, R.id.share_amountofincrease, R.id.share_tradingvolume,
			R.id.share_volumeoftransaction, R.id.share_max, R.id.share_min, R.id.share_swing, R.id.share_turnoverrate,
			R.id.share_PEratio, R.id.share_totalvalue, R.id.iv_nothing })
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_nothing:
			startActivity(new Intent(getActivity(), ShareAndFriendsSearchActivity.class));
			break;
		case R.id.share_new:

			ifSort = 1;
			Collections.sort(datas, new SortBynowPrice());
			Log.d("sortbynowaaa", datas.toString());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_updown:

			ifSort = 2;
			Collections.sort(datas, new SortByzhangdie());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_amountofincrease:

			ifSort = 3;
			Collections.sort(datas, new SortByzhangfu());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_tradingvolume:

			ifSort = 4;
			Collections.sort(datas, new SortByvolume());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_volumeoftransaction:

			ifSort = 5;
			Collections.sort(datas, new SortByamount());
			myAdapter.notifyDataSetChanged();
			break;

		case R.id.share_max:

			ifSort = 6;
			Collections.sort(datas, new SortByhighPrice());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_min:

			ifSort = 7;
			Collections.sort(datas, new SortBylowPrice());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_swing:

			ifSort = 8;
			Collections.sort(datas, new SortByzhenfu());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_turnoverrate:

			ifSort = 9;
			Collections.sort(datas, new SortByturnOverRate());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_PEratio:

			ifSort = 10;
			Collections.sort(datas, new SortByepg());
			myAdapter.notifyDataSetChanged();
			break;
		case R.id.share_totalvalue:

			ifSort = 11;
			Collections.sort(datas, new SortBytotalMarketValue());
			myAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}

	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// 当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
					.findViewById(R.id.share_horizontalScrollView1);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}

	public class MyAdapter extends BaseAdapter {
		// listhead短时间内固定，list不断刷新
		// private ArrayList<HashMap<String, String>> listhead;
		private ArrayList<HashMap<String, String>> list;
		public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();
		int id_row_layout;
		LayoutInflater mInflater;

		public MyAdapter(Context context, ArrayList<HashMap<String, String>> list, int id_row_layout) {
			super();

			this.list = list;
			this.id_row_layout = id_row_layout;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listhead.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			ViewHolder holder = null;
			if (convertView == null) {
				synchronized (Fragment_Price_Situation.this) {
					convertView = mInflater.inflate(id_row_layout, null);
					holder = new ViewHolder();

					MyHScrollView scrollView1 = (MyHScrollView) convertView
							.findViewById(R.id.share_horizontalScrollView1);

					holder.scrollView = scrollView1;
					// cj TextView tv = AbViewHolder.get(convertView,
					// R.id.item_share_name);

					holder.iv_ico = (ImageView) convertView.findViewById(R.id.share_iv_ico);
					holder.txt_name = (TextView) convertView.findViewById(R.id.item_share_name);
					holder.txt_num = (TextView) convertView.findViewById(R.id.item_share_num);
					holder.txt2 = (TextView) convertView.findViewById(R.id.item_share_2);
					holder.txt3 = (TextView) convertView.findViewById(R.id.item_share_3);
					holder.txt4 = (TextView) convertView.findViewById(R.id.item_share_4);
					holder.txt5 = (TextView) convertView.findViewById(R.id.item_share_5);
					holder.txt6 = (TextView) convertView.findViewById(R.id.item_share_6);
					/*
					 * holder.txt7 = (TextView) convertView
					 * .findViewById(R.id.item_share_7);
					 */
					holder.txt8 = (TextView) convertView.findViewById(R.id.item_share_8);
					holder.txt9 = (TextView) convertView.findViewById(R.id.item_share_9);
					holder.txt10 = (TextView) convertView.findViewById(R.id.item_share_10);
					holder.txt11 = (TextView) convertView.findViewById(R.id.item_share_11);
					holder.txt12 = (TextView) convertView.findViewById(R.id.item_share_12);
					holder.txt13 = (TextView) convertView.findViewById(R.id.item_share_13);

					MyHScrollView headSrcrollView = (MyHScrollView) mHead
							.findViewById(R.id.share_horizontalScrollView1);
					headSrcrollView.AddOnScrollChangedListener(new OnScrollChangedListenerImp(scrollView1));

					convertView.setTag(holder);
					mHolderList.add(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			DecimalFormat df = new DecimalFormat("#0.00");// 固定格式

			double a;

			holder.txt_name.setText(list.get(position).get("shortname"));
			holder.txt_num.setText(list.get(position).get("symbol"));
			try {
				aget = list.get(position).get("nowPrice");
				if (aget.equals("null") || aget == null) {
					holder.txt2.setText("---");
				} else {
					a = Double.parseDouble(aget);
					holder.txt2.setText(df.format(a));
				}

				// 涨跌
				if ((list.get(position).get("nowPrice").equals("null"))
						|| list.get(position).get("latestClosePrice").equals("null")) {
					holder.txt3.setText("---");
				} else {
					a = Double.parseDouble(list.get(position).get("nowPrice"))
							- Double.parseDouble(list.get(position).get("latestClosePrice"));
					holder.txt3.setText(df.format(a));
				}

				// 涨幅
				if ((list.get(position).get("nowPrice").equals("null"))
						|| list.get(position).get("latestClosePrice").equals("null")) {
					holder.txt4.setText("---");
					// holder.txt4.setTextColor(getResources().getColor(R.color.text_333333));
					holder.txt4.setBackgroundColor(getResources().getColor(R.color.white));
				} else {
					a = (Double.parseDouble(list.get(position).get("nowPrice"))
							- Double.parseDouble(list.get(position).get("latestClosePrice")))
							/ Double.parseDouble(list.get(position).get("latestClosePrice"));
					a = a * 100;
					if (a < 0) {
						holder.txt4.setBackgroundColor(getResources().getColor(R.color.share_53a00a));
					} else {
						holder.txt4.setBackgroundColor(getResources().getColor(R.color.share_f73131));
					}
					holder.txt4.setTextColor(getResources().getColor(R.color.white));
					holder.txt4.setText(df.format(a) + "%");
				}

				aget = list.get(position).get("volume");
				if (aget.equals("null")) {
					holder.txt5.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 1000000) {
						a = a / 1000000;
						holder.txt5.setText(df.format(a) + "万");
					} else {
						a = a / 100;
						holder.txt5.setText(df.format(a));
					}
				}

				aget = list.get(position).get("amount");
				if (aget.equals("null")) {
					holder.txt6.setText("---");
				} else {
					a = Double.parseDouble(aget);
					a = a / 100000000;
					holder.txt6.setText(df.format(a) + "亿");

				}

				// holder.txt7.setText(list.get(position).get("symbol"));
				aget = list.get(position).get("highPrice");
				if (aget.equals("null")) {
					holder.txt8.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 10000) {
						a = a / 10000;
						holder.txt8.setText(df.format(a) + "万");
					} else {
						holder.txt8.setText(df.format(a));
					}
				}

				aget = list.get(position).get("lowPrice");
				if (aget.equals("null")) {
					holder.txt9.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 10000) {
						a = a / 10000;
						holder.txt9.setText(df.format(a) + "万");
					} else {
						holder.txt9.setText(df.format(a));
					}
				}

				// 振幅
				if ((list.get(position).get("highPrice").equals("null"))
						|| list.get(position).get("lowPrice").equals("null")
						||list.get(position).get("latestClosePrice").equals("null")) {
					holder.txt10.setText("---");
					holder.txt10.setTextColor(getResources().getColor(R.color.text_333333));
					holder.iv_ico.setVisibility(View.GONE);
				} else {
					a = (Double.parseDouble(list.get(position).get("highPrice"))
							- Double.parseDouble(list.get(position).get("lowPrice")))
							/ Double.parseDouble(list.get(position).get("latestClosePrice"));
					a = a * 100;
					/*if (a < 0) {
						holder.txt10.setTextColor(getResources().getColor(R.color.share_53a00a));
						holder.iv_ico.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_share_die));
					} else {
						holder.txt10.setTextColor(getResources().getColor(R.color.share_f73131));
						holder.iv_ico.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_share_zhang));
					}*/

					if (Math.abs(a) >= 1000) {
						a = a / 10000;
						holder.txt10.setText(df.format(a) + "万" + "%");
					} else {
						holder.txt10.setText(df.format(a) + "%");
					}
				}

				aget = list.get(position).get("turnOverRate");
				if (aget.equals("null")) {
					holder.txt11.setText("---");
				} else {
					a = Double.parseDouble(aget);
					a = a * 100;
					holder.txt11.setText(df.format(a) + "%");
				}

				aget = list.get(position).get("epg");
				if (aget.equals("null")) {
					holder.txt12.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 100000000) {
						a = a / 100000000;
						holder.txt12.setText(df.format(a) + "亿");
					} else if (a >= 10000) {
						a = a / 10000;
						holder.txt12.setText(df.format(a) + "万");
					} else {
						holder.txt12.setText(df.format(a));
					}
				}

				aget = list.get(position).get("totalMarketValue");
				if (aget.equals("null")) {
					holder.txt13.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 100000000) {
						a = a / 100000000;
						holder.txt13.setText(new DecimalFormat("#0").format(a) + "亿");
					} else if (a >= 10000) {
						a = a / 10000;
						holder.txt13.setText(df.format(a) + "万");
					} else {
						holder.txt13.setText(df.format(a));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("fffff", "asdsadsad");
				e.printStackTrace();
			}
			return convertView;
		}

		class OnScrollChangedListenerImp implements OnScrollChangedListener {
			MyHScrollView mScrollViewArg;

			public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
				mScrollViewArg = scrollViewar;
			}

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				mScrollViewArg.smoothScrollTo(l, t);
			}
		};

		class ViewHolder {
			ImageView iv_ico;
			TextView txt_name;
			TextView txt_num;
			TextView txt2;
			TextView txt3;
			TextView txt4;
			TextView txt5;
			TextView txt6, txt7, txt8, txt9, txt10, txt11, txt12, txt13;
			HorizontalScrollView scrollView;
		}
	}// end class my

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			initsituation();
			Log.d("cjdatas", datas.toString());
			myAdapter.notifyDataSetChanged();
			mHandler.postDelayed(this, 3000);
		}
	};

	// 一对排序用的类。。。
	/*
	 * class SortByshortname implements Comparator {
	 * 
	 * @Override public int compare(Object o1, Object o2) { // TODO
	 * Auto-generated method stub HashMap<String, String> h1 = (HashMap<String,
	 * String>) o1; HashMap<String, String> h2 = (HashMap<String, String>) o2;
	 * if (h1.get("shortname").compareTo(h2.get("shortname")) < 0) { return -1;
	 * } else if (h1.get("shortname").compareTo(h2.get("shortname")) > 0) {
	 * return 1; } return 0; } }
	 */

	class SortBynowPrice implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("nowPrice");
				String s2 = h2.get("nowPrice");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}

	}

	class SortByzhangdie implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				double d1, d2;
				if (h1.get("nowPrice").equals("null") || h1.get("nowPrice") == null
						|| h1.get("latestClosePrice").equals("null") || h1.get("latestClosePrice") == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(h1.get("nowPrice")) - Double.parseDouble(h1.get("latestClosePrice"));
				}
				if (h2.get("nowPrice").equals("null") || h2.get("nowPrice") == null
						|| h2.get("latestClosePrice").equals("null") || h2.get("latestClosePrice") == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(h2.get("nowPrice")) - Double.parseDouble(h2.get("latestClosePrice"));
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}

	}

	class SortByzhangfu implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				double d1, d2;
				if (h1.get("nowPrice").equals("null") || h1.get("nowPrice") == null
						|| h1.get("latestClosePrice").equals("null") || h1.get("latestClosePrice") == null) {
					d1 = -200000;
				} else {
					d1 = (Double.parseDouble(h1.get("nowPrice")) - Double.parseDouble(h1.get("latestClosePrice")))
							/ Double.parseDouble(h1.get("latestClosePrice"));
				}
				if (h2.get("nowPrice").equals("null") || h2.get("nowPrice") == null
						|| h2.get("latestClosePrice").equals("null") || h2.get("latestClosePrice") == null) {
					d2 = -200000;
				} else {
					d2 = (Double.parseDouble(h2.get("nowPrice")) - Double.parseDouble(h2.get("latestClosePrice")))
							/ Double.parseDouble(h2.get("latestClosePrice"));
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}

	}

	class SortByzhenfu implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				double d1, d2;
				if (h1.get("highPrice").equals("null") || h1.get("highPrice") == null
						|| h1.get("lowPrice").equals("null") || h1.get("lowPrice") == null
						|| h1.get("latestClosePrice").equals("null") || h1.get("latestClosePrice") == null) {
					d1 = -200000;
				} else {
					d1 = (Double.parseDouble(h1.get("highPrice")) - Double.parseDouble(h1.get("lowPrice")))
							/ Double.parseDouble(h1.get("latestClosePrice"));
				}
				if (h2.get("highPrice").equals("null") || h2.get("highPrice") == null
						|| h2.get("lowPrice").equals("null") || h2.get("lowPrice") == null
						|| h2.get("latestClosePrice").equals("null") || h2.get("latestClosePrice") == null) {
					d2 = -200000;
				} else {
					d2 = (Double.parseDouble(h2.get("highPrice")) - Double.parseDouble(h2.get("lowPrice")))
							/ Double.parseDouble(h2.get("latestClosePrice"));
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}

	}

	class SortByvolume implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("volume");
				String s2 = h2.get("volume");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				
				if (d1<d2) {
					return 1;
				} else if (d1>d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

	class SortByamount implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("amount");
				String s2 = h2.get("amount");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

	class SortByhighPrice implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("highPrice");
				String s2 = h2.get("highPrice");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

	class SortBylowPrice implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("lowPrice");
				String s2 = h2.get("lowPrice");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

	class SortByturnOverRate implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("turnOverRate");
				String s2 = h2.get("turnOverRate");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

	class SortByepg implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("epg");
				String s2 = h2.get("epg");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

	class SortBytotalMarketValue implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;
			try {
				String s1 = h1.get("totalMarketValue");
				String s2 = h2.get("totalMarketValue");
				double d1,d2;
				if (s1.equals("null") || s1 == null) {
					d1 = -200000;
				} else {
					d1 = Double.parseDouble(s1);
				}
				if (s2.equals("null") || s2 == null) {
					d2 = -200000;
				} else {
					d2 = Double.parseDouble(s2);
				}
				if (d1 < d2) {
					return 1;
				} else if (d1 > d2) {
					return -1;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return 0;
		}
	}

}
