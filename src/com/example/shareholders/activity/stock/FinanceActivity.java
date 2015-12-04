package com.example.shareholders.activity.stock;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@ContentView(R.layout.activity_finance)
public class FinanceActivity extends Activity implements OnHeaderRefreshListener, OnFooterRefreshListener {
	private RotateAnimation animation;
	@ViewInject(R.id.lv_finance_list)
	private ListView lv_finance_list;
	@ViewInject(R.id.title_refresh)
	private ImageView title_refresh;
	private ArrayList<HashMap<String, Object>> lists = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> listscontent = new ArrayList<HashMap<String, Object>>();
	private Madapter madapter;
	// 上下拉刷新
	@ViewInject(R.id.refresh_finance)
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
	// 板块名称、板块类型、板块代码
	private String platetitle;
	private String plateTypeCode;
	private String platecode;
	// 总名称、代码
	@ViewInject(R.id.tv_finance_titile)
	private TextView tv_finance_titile;
	/*
	 * @ViewInject(R.id.tv_finance_num) private TextView tv_finance_num;
	 */
	JSONArray allpostarray=new JSONArray();// 1.5.2要传的参数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		initview();
	}

	private void initview() {
		// 设置旋转动画
		animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// animation.setFillAfter(true);
		animation.setDuration(500);// 设置动画持续时间
		// animation.setRepeatCount(2);//重复次数
		platetitle = getIntent().getStringExtra("platetitle");
		plateTypeCode = getIntent().getStringExtra("plateTypeCode");
		platecode = getIntent().getStringExtra("platecode");
		tv_finance_titile.setText(platetitle);
		// tv_finance_num.setText(platecode);
		/*madapter = new Madapter(this, lists, listscontent);
		lv_finance_list.setAdapter(madapter);*/
		madapter = new Madapter(FinanceActivity.this, lists, listscontent);
		lv_finance_list.setAdapter(madapter);
		getsituation(all_index, PAGE_SIZE, HEAD);
		lv_finance_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FinanceActivity.this, MyStockDetailsActivity.class);
				ArrayList<HashMap<String, Object>> stocks = new ArrayList<HashMap<String,Object>>();
				stocks.add(lists.get(position));
				Log.d("ccjstocks", stocks.toString());
				intent.putExtra("stocks", stocks);
				intent.putExtra("position", -1);
				startActivity(intent);
			}
		});
		
	}

	private void getsituation(int pageIndex, int pagerSize, final int type) {
		String url = AppConfig.URL_QUOTATION + "plate/stock.json?";
		url += "&plateTypeCode=" + plateTypeCode + "&platecode=" + platecode + "&pageSize=" + pagerSize + "&pageIndex="
				+ pageIndex;
		Log.d("1.8.2url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					Log.d("1.8.2res", response);
					// 1.5.2用的参数
					JSONArray postarray = new JSONArray();
					JSONObject post;

					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("plateStockResponses");
					ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> hashMap = null;
					Iterator<String> iterator = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						post = new JSONObject();
						hashMap = new HashMap<String, Object>();
						iterator = jsonArray.getJSONObject(i).keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							hashMap.put(key, jsonArray.getJSONObject(i).get(key));
						}
						post.put("symbol", jsonArray.getJSONObject(i).get("symbol").toString());
						post.put("securityType", "STOCK");
						postarray.put(post);
						datas.add(hashMap);
					}
					Log.d("getcontentpostarray", postarray.toString());
					if (type == HEAD) {
						lists.clear();
						lists.addAll(datas);
						allpostarray = new JSONArray(postarray.toString());
						
					} else {
						lists.addAll(datas);
						//JSONArray jsonArrayls=new JSONArray(allpostarray.toString());
						allpostarray=new JSONArray(allpostarray.toString()+postarray.toString());
					}
					Log.d("1.8.2list", lists.toString());
					Log.d("getcontentarray", allpostarray.toString());
					getcontent();
					//madapter.notifyDataSetChanged();
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

	private void getcontent() {
		String url = AppConfig.VERSION_URL + "quotation/newestPrice.json?";
		Log.d("getcontenturl", url);
		Log.d("getcontentarray2", allpostarray.toString());
		StringRequest stringRequest2 = new StringRequest(allpostarray, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					Log.d("1.5.2response", response);
					JSONArray jsonArray = new JSONArray(response);
					HashMap<String, Object> data = null;
					Iterator<String> iterator = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						data = new HashMap<String, Object>();
						iterator = jsonArray.getJSONObject(i).keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							data.put(key, jsonArray.getJSONObject(i).get(key).toString());
						}
						listscontent.add(data);
					}
					Log.d("listscontent", listscontent.toString());
					
					madapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest2.setTag("stringRequest2");
		MyApplication.getRequestQueue().add(stringRequest2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		MyApplication.getRequestQueue().cancelAll("stringRequest2");
		super.onDestroy();
	}

	@OnClick({ R.id.title_refresh, R.id.rl_return })
	private void onclick(View view) {
		switch (view.getId()) {
		case R.id.title_refresh:
			title_refresh.startAnimation(animation);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private class Madapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> list;
		private ArrayList<HashMap<String, Object>> listcontent;
		private Context context;
		private LayoutInflater inflater;

		public Madapter(Context context, ArrayList<HashMap<String, Object>> list,
				ArrayList<HashMap<String, Object>> listcontent) {
			this.context = context;
			this.list = list;
			this.listcontent = listcontent;
			Log.d("listcontent3", this.listcontent.toString());
			Log.d("listcontentsize", this.listcontent.size()+"");
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
				convertView = inflater.inflate(R.layout.item_finance_content, null);
			}
			TextView tv_finance_name = (TextView) AbViewHolder.get(convertView, R.id.tv_finance_name);
			TextView tv_finance_num = (TextView) AbViewHolder.get(convertView, R.id.tv_finance_num);
			TextView tv_finance_new = (TextView) AbViewHolder.get(convertView, R.id.tv_finance_new);
			TextView iv_finance_zhangdie = (TextView) AbViewHolder.get(convertView, R.id.iv_finance_zhangdie);
			TextView tv_finance_zhangfu = (TextView) AbViewHolder.get(convertView, R.id.tv_finance_zhangfu);
			//ImageView iv_finance_zhangfu = (ImageView) AbViewHolder.get(convertView, R.id.iv_finance_zhangfu);
			tv_finance_name.setText(list.get(position).get("symbolName").toString());
			tv_finance_num.setText(list.get(position).get("symbol").toString());
			DecimalFormat df = new DecimalFormat("#0.00");// 固定格式
			NumberFormat nf = java.text.NumberFormat.getPercentInstance();
			// nf.setMaximumIntegerDigits(1);// 小数点前保留几位
			nf.setMinimumFractionDigits(2);// 小数点后保留几位
			double a;
			String aget = new String();
			if (isnull2(position, "nowPrice")) {
				tv_finance_new.setText("---");
			} else {
				aget = listcontent.get(position).get("nowPrice").toString();
				a = Double.parseDouble(aget);
				tv_finance_new.setText(df.format(a));
			}
			// 涨跌、涨幅
			if (isnull2(position, "nowPrice") || isnull2(position, "latestClosePrice")) {
				iv_finance_zhangdie.setText("---");
				tv_finance_zhangfu.setText("---");
				tv_finance_zhangfu.setBackgroundColor(getResources().getColor(R.color.white));
			} else {
				a = Double.parseDouble(listcontent.get(position).get("nowPrice").toString())
						- Double.parseDouble(listcontent.get(position).get("latestClosePrice").toString());
				iv_finance_zhangdie.setText(df.format(a));
				a = a / Double.parseDouble(listcontent.get(position).get("latestClosePrice").toString());
				a = a * 100;
				if (a < 0) {
					tv_finance_zhangfu.setBackgroundColor(getResources().getColor(R.color.share_53a00a));
				} else {
					tv_finance_zhangfu.setBackgroundColor(getResources().getColor(R.color.share_f73131));
				}
				tv_finance_zhangfu.setTextColor(getResources().getColor(R.color.white));
				tv_finance_zhangfu.setText(df.format(a) + "%");
			}
			return convertView;
		}

		private boolean isnull(int position, String string) {
			if (list.get(position).get(string).toString().equals("")
					|| list.get(position).get(string).toString() == null
					|| list.get(position).get(string).toString().equals("null")) {
				return true;
			} else {
				return false;
			}
		}
		private boolean isnull2(int position, String string) {
			if (listcontent.get(position).get(string).toString().equals("")
					|| listcontent.get(position).get(string).toString() == null
					|| listcontent.get(position).get(string).toString().equals("null")) {
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
