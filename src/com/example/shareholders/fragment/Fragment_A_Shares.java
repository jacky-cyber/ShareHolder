package com.example.shareholders.fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.MainActivity;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyHScrollView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.MyHScrollView.OnScrollChangedListener;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_Price_Situation.ListViewAndHeadViewTouchLinstener;
import com.example.shareholders.fragment.Fragment_Price_Situation.MyAdapter.OnScrollChangedListenerImp;
import com.example.shareholders.fragment.Fragment_Price_Situation.MyAdapter.ViewHolder;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_A_Shares extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener {

	//是否已经实时刷新
	private boolean haspost=false;
	private Handler mHandler;
	private RotateAnimation animation;
	MainActivity mainActivity;
	// 排序点击按钮
	View item_view;
	RadioGroup radioGroup;

	View mview;
	RelativeLayout mHead;
	@ViewInject(R.id.fsl_share_list)
	private ListView mListView1;

	// 上下拉刷新
	@ViewInject(R.id.refresh_abshare)
	private PullToRefreshView refresh;
	// pageSize,固定为10条
	private static int PAGE_SIZE = 10;
	// 判断是否第一次进来并且更新数据
	private boolean flag = true;
	// pageIndex,从0递增
	private int all_index = 0;
	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	// 股票类型
	private String stockType = "A_SHARE";
	// 排序字段
	private String sortField = "PRICE";
	// 是否倒序
	private String isDesc = "true";
	private ArrayList<HashMap<String, Object>> lists;
	private Madapter madapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		mview = inflater.inflate(R.layout.fragment_abshare_list, container, false);
		ViewUtils.inject(this, mview);
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		initview();
		mainActivity = (MainActivity) getActivity();
		mainActivity.title_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 已登录，跳转到situation
				getsituation(all_index, PAGE_SIZE, HEAD);
				mainActivity.title_refresh.startAnimation(animation);
			}
		});
		
		return mview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("getstockType");
		getActivity().registerReceiver(receiver, intentFilter);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (mainActivity.getmainviewpagerid()==5&&mainActivity.getsituationviewpagerid()==0&&haspost==false) {
			mHandler.post(mRunnable);
			haspost=true;
			Log.d("asdasduy80000000", "asdasduy80000000");
		}
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
		if (haspost) {
			mHandler.removeCallbacks(mRunnable);
			haspost=false;
			Log.d("asdasduy90000000", "asdasduy90000000");
		}
		super.onPause();
	}
	public void sethandler(int viewid){
		Log.d("viewid", viewid+"");
		if (viewid==0&&haspost==false) {
			mHandler.post(mRunnable);
			haspost=true;
		}else if (viewid!=0&&haspost==true) {
			mHandler.removeCallbacks(mRunnable);
			haspost=false;
		}
	}
	// 用于注册广播的类
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			stockType = intent.getStringExtra("stockType");
			getsituation(all_index, PAGE_SIZE, HEAD);
		}

	};

	private void initview() {
		mHandler=new Handler();
		// 设置旋转动画
		animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(500);
		item_view = (View) mview.findViewById(R.id.fragment_share_list_head);
		radioGroup = (RadioGroup) item_view.findViewById(R.id.share_radiogroup);
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		// 禁止listview下拉
		mListView1.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mHead = (RelativeLayout) mview.findViewById(R.id.fragment_share_list_head);
		mHead.setFocusable(true);
		mHead.setClickable(true);
		// mHead.setBackgroundColor(Color.parseColor("#b2d235"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		lists = new ArrayList<HashMap<String, Object>>();
		madapter = new Madapter(getActivity(), lists);
		mListView1.setAdapter(madapter);
		getsituation(all_index, PAGE_SIZE, HEAD);
		mListView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
				ArrayList<HashMap<String, Object>> stocks = new ArrayList<HashMap<String,Object>>();
				stocks.add(lists.get(position));
				intent.putExtra("stocks", stocks);
				intent.putExtra("position", -2);
				startActivity(intent);
			}
		});
	}

	private void getsituation(int pageIndex, int pagerSize, final int type) {
		String url = AppConfig.VERSION_URL + "quotation/quotationList.json?";
		url += "stockType=" + stockType + "&sortField=" + sortField + "&isDesc=" + isDesc;
		url += "&pageIndex=" + all_index + "&pageSize=" + pagerSize;
		Log.d("1.1.1url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					Log.d("1.1.1res", response);
					JSONArray jsonArray = new JSONArray(response);
					ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> hashMap = null;
					Iterator<String> iterator = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						hashMap = new HashMap<String, Object>();
						iterator = jsonArray.getJSONObject(i).keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							hashMap.put(key, jsonArray.getJSONObject(i).get(key));
						}
						datas.add(hashMap);
					}
					if (type == HEAD) {
						lists.clear();
						lists.addAll(datas);
					} else {
						lists.addAll(datas);
					}
					Log.d("1.1.1list", lists.toString());
					madapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("1.1.1eeerr", e.toString());
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	@OnClick({ R.id.share_new, R.id.share_updown, R.id.share_amountofincrease, R.id.share_tradingvolume,
			R.id.share_volumeoftransaction, R.id.share_max, R.id.share_min, R.id.share_swing, R.id.share_turnoverrate,
			R.id.share_PEratio, R.id.share_totalvalue })
	private void onclick(View view) {
		switch (view.getId()) {
		case R.id.share_new:
			sortField = "PRICE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_updown:
			sortField = "CHANGE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_amountofincrease:
			sortField = "CHANGE_RATIO";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_tradingvolume:
			sortField = "VOLUME";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_volumeoftransaction:
			sortField = "AMOUNT";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_max:
			sortField = "HIGH_PRICE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_min:
			sortField = "LOW_PRICE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_swing:
			sortField = "AMPLITUDE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;

		case R.id.share_turnoverrate:
			sortField = "PE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_PEratio:
			sortField = "MARKET_VALUE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.share_totalvalue:
			sortField = "CIRCULATED_MARKET_VALUE";
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;

		default:
			break;
		}
	}

	private class Madapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater inflater;

		public Madapter(Context context, ArrayList<HashMap<String, Object>> list) {
			this.context = context;
			this.list = list;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list == null) {
				return 0;
			}
			return list.size();
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

		@SuppressWarnings("unused")
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {

				synchronized (Fragment_A_Shares.this) {
					convertView = inflater.inflate(R.layout.item_fragment_share_list_content, null);

					MyHScrollView scrollView1 = (MyHScrollView) convertView
							.findViewById(R.id.share_horizontalScrollView1);
					MyHScrollView headSrcrollView = (MyHScrollView) mHead
							.findViewById(R.id.share_horizontalScrollView1);
					headSrcrollView.AddOnScrollChangedListener(new OnScrollChangedListenerImp(scrollView1));
				}
			}
			ImageView share_iv_ico = (ImageView) AbViewHolder.get(convertView, R.id.share_iv_ico);
			TextView item_share_name = (TextView) AbViewHolder.get(convertView, R.id.item_share_name);
			TextView item_share_num = (TextView) AbViewHolder.get(convertView, R.id.item_share_num);
			TextView item_share_2 = (TextView) AbViewHolder.get(convertView, R.id.item_share_2);
			TextView item_share_3 = (TextView) AbViewHolder.get(convertView, R.id.item_share_3);
			TextView item_share_4 = (TextView) AbViewHolder.get(convertView, R.id.item_share_4);
			TextView item_share_5 = (TextView) AbViewHolder.get(convertView, R.id.item_share_5);
			TextView item_share_6 = (TextView) AbViewHolder.get(convertView, R.id.item_share_6);
			// TextView item_share_7 = (TextView) AbViewHolder.get(convertView,
			// R.id.item_share_7);
			TextView item_share_8 = (TextView) AbViewHolder.get(convertView, R.id.item_share_8);
			TextView item_share_9 = (TextView) AbViewHolder.get(convertView, R.id.item_share_9);
			TextView item_share_10 = (TextView) AbViewHolder.get(convertView, R.id.item_share_10);
			TextView item_share_11 = (TextView) AbViewHolder.get(convertView, R.id.item_share_11);
			TextView item_share_12 = (TextView) AbViewHolder.get(convertView, R.id.item_share_12);
			TextView item_share_13 = (TextView) AbViewHolder.get(convertView, R.id.item_share_13);
			item_share_name.setText(list.get(position).get("name").toString());
			item_share_num.setText(list.get(position).get("symbol").toString());

			DecimalFormat df = new DecimalFormat("#0.00");// 固定格式
			NumberFormat nf = java.text.NumberFormat.getPercentInstance();
			// nf.setMaximumIntegerDigits(1);// 小数点前保留几位
			nf.setMinimumFractionDigits(2);// 小数点后保留几位
			double a;
			String aget = new String();
			if (isnull(position, "price")) {
				item_share_2.setText("---");
			} else {
				aget = list.get(position).get("price").toString();
				a = Double.parseDouble(aget);
				item_share_2.setText(df.format(a));
			}
			// 涨跌、涨幅
			if (isnull(position, "price") || isnull(position, "precloseprice")) {
				item_share_3.setText("---");
				item_share_4.setText("---");
				item_share_4.setBackgroundColor(getResources().getColor(R.color.white));
			} else {
				a = Double.parseDouble(list.get(position).get("price").toString())
						- Double.parseDouble(list.get(position).get("precloseprice").toString());
				item_share_3.setText(df.format(a));
				a = a / Double.parseDouble(list.get(position).get("precloseprice").toString());
				a = a * 100;
				if (a < 0) {
					item_share_4.setBackgroundColor(getResources().getColor(R.color.share_53a00a));
				} else {
					item_share_4.setBackgroundColor(getResources().getColor(R.color.share_f73131));
				}
				item_share_4.setTextColor(getResources().getColor(R.color.white));
				item_share_4.setText(df.format(a) + "%");
			}
			if (isnull(position, "volume")) {
				item_share_5.setText("---");
			} else {
				aget = list.get(position).get("volume").toString();
				a = Double.parseDouble(aget);
				if (a >= 1000000) {
					a = a / 1000000;
					item_share_5.setText(df.format(a) + "万");
				} else {
					a = a / 100;
					item_share_5.setText(df.format(a));
				}
				item_share_5.setText(df.format(a));
			}
			if (isnull(position, "amount")) {
				item_share_6.setText("---");
			} else {
				aget = list.get(position).get("amount").toString();
				a = Double.parseDouble(aget);
				a = a / 100000000;
				item_share_6.setText(df.format(a) + "亿");
			}
			if (isnull(position, "highPrice")) {
				item_share_8.setText("---");
			} else {
				aget = list.get(position).get("highPrice").toString();
				a = Double.parseDouble(aget);
				if (a >= 10000) {
					a = a / 10000;
					item_share_8.setText(df.format(a) + "万");
				} else {
					item_share_8.setText(df.format(a));
				}

			}
			if (isnull(position, "lowPrice")) {
				item_share_9.setText("---");
			} else {
				aget = list.get(position).get("lowPrice").toString();
				a = Double.parseDouble(aget);
				if (a >= 10000) {
					a = a / 10000;
					item_share_9.setText(df.format(a) + "万");
				} else {
					item_share_9.setText(df.format(a));
				}
			}
			// 振幅
			if (isnull(position, "highPrice") || isnull(position, "lowPrice") || isnull(position, "precloseprice")) {
				item_share_10.setText("---");
				item_share_10.setTextColor(getResources().getColor(R.color.text_333333));
				item_share_10.setVisibility(View.GONE);
			} else {
				a = (Double.parseDouble(list.get(position).get("highPrice").toString())
						- Double.parseDouble(list.get(position).get("lowPrice").toString()))
						/ Double.parseDouble(list.get(position).get("precloseprice").toString());
				a = a * 100;
				/*if (a < 0) {
					item_share_10.setTextColor(getResources().getColor(R.color.share_53a00a));
					share_iv_ico.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_share_die));
				} else {
					item_share_10.setTextColor(getResources().getColor(R.color.share_f73131));
					share_iv_ico.setBackgroundDrawable(getResources().getDrawable(R.drawable.ico_share_zhang));
				}*/

				if (Math.abs(a) >= 1000) {
					a = a / 10000;
					item_share_10.setText(df.format(a) + "万" + "%");
				} else {
					item_share_10.setText(df.format(a) + "%");
				}
			}
			if (isnull(position, "turnoverrate")) {
				item_share_11.setText("---");
			} else {
				aget = list.get(position).get("turnoverrate").toString();
				a = Double.parseDouble(aget);
				a = a * 100;
				item_share_11.setText(df.format(a) + "%");
			}
			if (isnull(position, "pe")) {
				item_share_12.setText("---");
			} else {
				aget = list.get(position).get("pe").toString();
				a = Double.parseDouble(aget);
				item_share_12.setText(df.format(a));
			}
			if (isnull(position, "circulatedMarketValue")) {
				item_share_13.setText("---");
			} else {
				aget = list.get(position).get("circulatedMarketValue").toString();
				a = Double.parseDouble(aget);
				if (a >= 100000000) {
					a = a / 100000000;
					item_share_13.setText(new DecimalFormat("#0").format(a) + "亿");
				} else if (a >= 10000) {
					a = a / 10000;
					item_share_13.setText(df.format(a) + "万");
				} else {
					item_share_13.setText(df.format(a));
				}
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

		private boolean isnull(int position, String string) {
			if (list.get(position).get(string).toString().equals("")
					|| list.get(position).get(string).toString() == null
					|| list.get(position).get(string).toString().equals("null")) {
				return true;
			} else {
				return false;
			}
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

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh.postDelayed(new Runnable() {
			@Override
			public void run() {
				getsituation(++all_index, PAGE_SIZE, FOOT);
				refresh.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		all_index = 0;
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				getsituation(all_index, PAGE_SIZE, HEAD);
				refresh.onHeaderRefreshComplete();
			}
		}, 1000);
	}
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			getsituation(all_index, PAGE_SIZE, HEAD);
			Log.d("cjlists", lists.toString());
			//madapter.notifyDataSetChanged();
			mHandler.postDelayed(this, 3000);
		}
	};
}
