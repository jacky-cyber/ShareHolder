package com.example.shareholders.fragment;

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
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.FinanceActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment_Plate extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener {
	/*@ViewInject(R.id.ls_palte_list)
	private ListView ls_palte_list;*/
	@ViewInject(R.id.gv_palte_list)
	private GridView gv_palte_list;
	private RotateAnimation animation;
	private ArrayList<HashMap<String, Object>> lists=new ArrayList<HashMap<String,Object>>();
	private Madapter madapter;
	View mview;
	// 上下拉刷新
	@ViewInject(R.id.refresh_plate)
	private PullToRefreshView refresh;
	// pageSize,固定为14条
	private static int PAGE_SIZE = 14;
	// 判断是否第一次进来并且更新数据
	private boolean flag = true;
	// pageIndex,从0递增
	private int all_index = 0;
	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;
	// 类型
	private String plateTypeCode = "INDUSTRY";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_plate, container, false);
		ViewUtils.inject(this, mview);
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		initview();
		return mview;
	}

	private void initview() {
		// 设置旋转动画
		animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// animation.setFillAfter(true);
		animation.setDuration(500);// 设置动画持续时间
		// animation.setRepeatCount(2);//重复次数
		madapter = new Madapter(getActivity(), lists);
		gv_palte_list.setAdapter(madapter);
		gv_palte_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getActivity(), FinanceActivity.class);
				intent.putExtra("platetitle", lists.get(position).get("platetitle").toString());
				intent.putExtra("platecode", lists.get(position).get("platecode").toString());
				intent.putExtra("plateTypeCode", lists.get(position).get("plateTypeCode").toString());
				startActivity(intent);
			}
		});
		getsituation(all_index, PAGE_SIZE, HEAD);
	}

	private void getsituation(int pageIndex, int pagerSize, final int type) {
		String url = AppConfig.URL_QUOTATION + "plate/tree.json?";
		url += "&plateTypeCode=" + plateTypeCode + "&pageSize=" + pagerSize + "&pageIndex=" + pageIndex;
		Log.d("1.8.1url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					Log.d("1.8.1res", response);
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("plateTreeResponses");
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
					Log.d("1.8.1list", lists.toString());
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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	@OnClick({ R.id.rb_hangye, R.id.rb_diqu })
	private void onclick(View view) {
		switch (view.getId()) {
		case R.id.rb_hangye:
			plateTypeCode = "INDUSTRY";
			all_index = 0;
			getsituation(all_index, PAGE_SIZE, HEAD);
			break;
		case R.id.rb_diqu:
			plateTypeCode = "REGION";
			all_index = 0;
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
			if (list==null) {
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
				convertView = inflater.inflate(R.layout.item_palte_grid_content, null);
			}
			TextView tv_plate_name = (TextView) AbViewHolder.get(convertView, R.id.tv_plate_name);
			TextView tv_plate_num = (TextView) AbViewHolder.get(convertView, R.id.tv_plate_num);
			try {
				tv_plate_name.setText(list.get(position).get("platetitle").toString());
				tv_plate_num.setText(list.get(position).get("platecode").toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			return convertView;
		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.d("onFooterRefresh", "onFooterRefresh");
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
				Log.d("onHeaderRefresh", "onHeaderRefresh");
				getsituation(all_index, PAGE_SIZE, HEAD);
				refresh.onHeaderRefreshComplete();
			}
		}, 1000);
	}
}
