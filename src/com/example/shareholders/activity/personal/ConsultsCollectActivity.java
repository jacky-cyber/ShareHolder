package com.example.shareholders.activity.personal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.EditMyselfAnnouncementActivity;
import com.example.shareholders.activity.stock.EditMyselfNewsActivity;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_consults_collect)
public class ConsultsCollectActivity extends ActionBarActivity implements OnHeaderRefreshListener,OnFooterRefreshListener{
	@ViewInject(R.id.lv_consults_collect)
	private ListView lv_consults_collect;
	
	@ViewInject(R.id.ll_wuzixun)
	private RelativeLayout ll_wuzixun;
	@ViewInject(R.id.pull_to_refresh)
	private PullToRefreshView pull_to_refresh;
	//全局数据
	final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
	//页码和页数大小
	private int pageNo = 0;
	private int pageSize = 5;
	//标志上下拉刷新
	private int HEAD = 0;
	private int FOOT = 1;
	
	private LoadingDialog loadingDialog;
	//适配器
	private ConsultsCollectAdapter adapter;

	// 初始化
	// RequestQueue volleyRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();
		pull_to_refresh.setOnHeaderRefreshListener(this);
		pull_to_refresh.setOnFooterRefreshListener(this);
		// volleyRequestQueue = Volley.newRequestQueue(this);
		getzixunsc(pageNo,pageSize,HEAD);
	}

	private void getzixunsc(int pageNo,int pageSize,final int type) {

		/*
		 * * String url = AppConfig.URL_INFO +
		 * "follow/list.json?access_token="+AppConfig.ACCESS_TOKEN+
		 * "&pageIndex=0&pageSize=5&type=ALL";
		 */

		if (type==HEAD) {
			datas.clear();
		}
		
		String url = AppConfig.URL_INFO + "follow/list.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token")
				+ "&pageIndex="+pageNo+"&pageSize="+pageSize
				+"&type=ALL";
		Log.d("qweq", url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("lele_response", response.toString());
						if (response.equals("") || response.equals("[0]")) {
							Log.d("lele_no_content", "No Content");
							loadingDialog.dismissDialog();
						} else {
							Log.d("lele_zixun", response.toString());
							try {
								JSONObject object = new JSONObject(response
										.toString());
								JSONArray all = object
										.getJSONArray("responses");
								Log.e("all", all.toString());
								HashMap<String, String> data = null;
								Iterator<String> iterator = null;
								for (int i = 0; i < all.length(); i++) {
									data = new HashMap<String, String>();
									iterator = all.getJSONObject(i).keys();
									while (iterator.hasNext()) {
										String key = iterator.next();
										data.put(key,
												all.getJSONObject(i).get(key)
														.toString());
									}
									datas.add(data);
								}
								//无数据显示无数据页面
								if (datas.size()==0) {
									ll_wuzixun.setVisibility(View.VISIBLE);
								}else {
									//下拉刷新或者出事刷新需要初始化适配器
									if (type==HEAD) {
										adapter = new ConsultsCollectAdapter(
												ConsultsCollectActivity.this,
												datas);
										lv_consults_collect.setAdapter(adapter);
									}else {
										//上拉刷新通知更新
										adapter.notifyDataSetChanged();
									}
									
								}
								loadingDialog.dismissDialog();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
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
							Log.d("lele_error", jsonObject.get("description")
									.toString());

						} catch (Exception e) {
							Log.d("lele_error", "未知错误");
						}

					}
				});
		// volleyRequestQueue.add(stringRequest);
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	@OnClick({ R.id.rl_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	private class ConsultsCollectAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		public ConsultsCollectAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_consults_collect_list, null);

				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				holder.tv_symbol = (TextView) view.findViewById(R.id.tv_symbol);
				holder.tv_content = (TextView) view
						.findViewById(R.id.tv_content);
				holder.tv_date = (TextView) view.findViewById(R.id.tv_date);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_name.setText(list.get(position).get("shortName"));
			holder.tv_symbol.setText(list.get(position).get("symbol"));
			holder.tv_content.setText(list.get(position).get("title"));

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			long date_long = Long.parseLong(list.get(position).get("infoDate"));

			String date = simpleDateFormat.format(new Date(date_long));
			holder.tv_date.setText(date);
			
			//点击名字跳转个股详情
			holder.tv_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("symbol", list.get(position).get("symbol").toString());
					map.put("shortname", list.get(position).get("shortName").toString());
					map.put("securityType", list.get(position).get("type").toString());
					stocks.add(map);
					Intent intent = new Intent(getApplicationContext(), MyStockDetailsActivity.class);
					intent.putExtra("stocks", stocks);
					intent.putExtra("position", 0);
					startActivity(intent);
				}
			});
			//跳转到公告详情页面
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context,
							EditMyselfAnnouncementActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("announcementid", list.get(position).get("id").toString());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});

			return view;
		}

		class ViewHolder {
			TextView tv_name;
			TextView tv_symbol;
			TextView tv_content;
			TextView tv_date;

		}
	}

	//底部上拉刷新
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		
		pull_to_refresh.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pageNo=pageNo+1;
				getzixunsc(pageNo,pageSize,FOOT);
				pull_to_refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}

	//头部下拉刷新
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pageNo = 0;
				getzixunsc(pageNo, pageSize, HEAD);
				pull_to_refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}
}
