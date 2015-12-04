package com.example.shareholders.fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.newthird.NewThirdSearchActivity;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_NewThrid_Myself extends Fragment implements OnHeaderRefreshListener {
	// 上下拉刷新
	@ViewInject(R.id.refresh_newthird)
	private PullToRefreshView refresh;
	@ViewInject(R.id.iv_nothing)
	private ImageView iv_nothing;
	ListView mListView1;
	RelativeLayout mHead;
	View mview;
	private ArrayList<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();
	private Madapter madapter;
	private String actoken;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_newthrid, container, false);
		ViewUtils.inject(this, mview);
		refresh.setOnHeaderRefreshListener(this);
		mListView1 = (ListView) mview.findViewById(R.id.newthird_list);
		initview();
		return mview;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		getsituation();
		super.onResume();
	}

	private void initview() {
		actoken=RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		if (RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN).equals("")) {
			iv_nothing.setVisibility(View.VISIBLE);
			// PullToRefreshView refresh存在时iv_nothing设置不了点击事件
			refresh.setVisibility(View.GONE);
		}
		madapter = new Madapter(getActivity(), lists);
		mListView1.setAdapter(madapter);
		// getsituation();
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
	}

	@OnClick({ R.id.iv_nothing })
	private void onclick(View view) {
		Log.d("asd1117a", "asd");
		switch (view.getId()) {
		case R.id.iv_nothing:
			Log.d("asd1117b", "asd");
			startActivity(new Intent(getActivity(), NewThirdSearchActivity.class));
			break;

		default:
			break;
		}
	}

	private void getsituation() {
		lists.clear();
		String url = AppConfig.VERSION_URL + "neeq/self/quotation.json?access_token=";
		/*try {
			Log.d("getaaaaa0", RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.d("getaaaaa11", e1.toString());
		}*/
		url += actoken;

		Log.d("new1.3url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				if (response==null||response.equals("")) {
					iv_nothing.setVisibility(View.VISIBLE);
					// PullToRefreshView refresh存在时iv_nothing设置不了点击事件
					refresh.setVisibility(View.GONE);
				}else {
					iv_nothing.setVisibility(View.GONE);
					refresh.setVisibility(View.VISIBLE);
				}
				try {
					Log.d("new1.3res", response);
					JSONArray jsonArray = new JSONArray(response);
					HashMap<String, Object> hashMap = null;
					Iterator<String> iterator = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						hashMap = new HashMap<String, Object>();
						iterator = jsonArray.getJSONObject(i).keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							hashMap.put(key, jsonArray.getJSONObject(i).get(key));
						}
						lists.add(hashMap);
					}
					Log.d("new1.3list", lists.toString());
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
		getsituation();
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
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				getsituation();
				refresh.onHeaderRefreshComplete();
			}
		}, 1000);
	}

}
