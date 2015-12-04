package com.example.shareholders.fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyHScrollView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.MyHScrollView.OnScrollChangedListener;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_Price_Situation.MyAdapter.ViewHolder;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_NewThrid_Situation extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener {
	ListView mListView1;
	RelativeLayout mHead;
	View mview;
	private ArrayList<HashMap<String, Object>> lists;
	private Madapter madapter;
	// 上下拉刷新
	@ViewInject(R.id.refresh_newthird)
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_newthrid, container, false);
		ViewUtils.inject(this, mview);
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		mListView1 = (ListView) mview.findViewById(R.id.newthird_list);

		initview();
		getsituation(all_index, PAGE_SIZE, HEAD);
		/*mListView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
				ArrayList<HashMap<String, Object>> stocks = new ArrayList<HashMap<String,Object>>();
				stocks.add(lists.get(position));
				intent.putExtra("stocks", stocks);
				intent.putExtra("position", -1);
				startActivity(intent);
			}
		});*/
		return mview;
	}

	private void initview() {
		lists = new ArrayList<HashMap<String, Object>>();
		madapter = new Madapter(getActivity(), lists);
		mListView1.setAdapter(madapter);

	}

	private void getsituation(int pageIndex, int pagerSize, final int type) {
		String url = AppConfig.VERSION_URL + "neeq/quotation/list.json?";
		url += "&pageSize=" + pagerSize + "&pageIndex=" + pageIndex;
		Log.d("new1.2url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					Log.d("new1.2res", response);
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("quotationListResponses");
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
					Log.d("new1.2list", lists.toString());
					madapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("eeerr", e.toString());
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

	public void gettoactivity(){
		getsituation(all_index, PAGE_SIZE, HEAD);
		//madapter.notifyDataSetChanged();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
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

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_newthird_list_content, null);
			}
			TextView iv_newthird_name = (TextView) AbViewHolder.get(convertView, R.id.iv_newthird_name);
			TextView iv_newthird_num = (TextView) AbViewHolder.get(convertView, R.id.iv_newthird_num);
			TextView tv_newthird_new = (TextView) AbViewHolder.get(convertView, R.id.tv_newthird_new);
			TextView tv_newthird_zhangdie = (TextView) AbViewHolder.get(convertView, R.id.iv_newthird_zhangdie);
			TextView tv_newthird_zhangfu = (TextView) AbViewHolder.get(convertView, R.id.tv_newthird_zhangfu);
			Log.d("ads111222", list.toString());

			DecimalFormat df = new DecimalFormat("#0.00");// 固定格式
			NumberFormat nf = java.text.NumberFormat.getPercentInstance();
			// nf.setMaximumIntegerDigits(1);// 小数点前保留几位
			nf.setMinimumFractionDigits(2);// 小数点后保留几位
			if (isnull(position, "symbolName")) {
				iv_newthird_name.setText("---");
			} else {
				iv_newthird_name.setText(list.get(position).get("symbolName").toString());
			}

			if (isnull(position, "symbol")) {
				iv_newthird_num.setText("---");
			} else {
				iv_newthird_num.setText(list.get(position).get("symbol").toString());
			}

			if (isnull(position, "closeprice")) {
				tv_newthird_new.setText("---");
			} else {
				tv_newthird_new.setText(list.get(position).get("closeprice").toString());
			}

			if (isnull(position, "closeprice") || isnull(position, "preClosePrice")) {
				tv_newthird_zhangdie.setText("---");
				tv_newthird_zhangfu.setText("---");
				tv_newthird_new.setTextColor(getResources().getColor(R.color.text_333333));
				tv_newthird_zhangdie.setTextColor(getResources().getColor(R.color.text_333333));
				tv_newthird_zhangfu.setTextColor(getResources().getColor(R.color.text_333333));
			} else {
				String closeprice = list.get(position).get("closeprice").toString();
				String preClosePrice = list.get(position).get("preClosePrice").toString();
				Double zhangdie = new Double(closeprice) - new Double(preClosePrice);
				if (zhangdie >= 0) {
					tv_newthird_zhangdie.setTextColor(getResources().getColor(R.color.share_f73131));
					tv_newthird_zhangfu.setTextColor(getResources().getColor(R.color.share_f73131));
					tv_newthird_new.setTextColor(getResources().getColor(R.color.share_f73131));
				} else {
					tv_newthird_zhangdie.setTextColor(getResources().getColor(R.color.share_53a00a));
					tv_newthird_zhangfu.setTextColor(getResources().getColor(R.color.share_53a00a));
					tv_newthird_new.setTextColor(getResources().getColor(R.color.share_53a00a));
				}
				String zhangfu = nf.format(zhangdie / new Double(preClosePrice));
				tv_newthird_zhangdie.setText(df.format(zhangdie));
				tv_newthird_zhangfu.setText(zhangfu);
			}

			return convertView;
		}

		private boolean isnull(int position, String string) {
			if (list.get(position).get(string).toString().equals("")
					|| list.get(position).get(string).toString() == null
					||list.get(position).get(string).toString().equals("null")) {
				return true;
			} else {
				return false;
			}
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
	
}
