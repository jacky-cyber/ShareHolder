package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.stock.ShareAndFriendsSearchActivity;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.ShareHistorySearchEntity;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Share_Search2 extends Fragment {

	private ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
	@ViewInject(R.id.share_search_s_list2)
	private ListView MessageList;
	HttpUtils http;
	DbUtils dbUtils;
	private boolean isfirstclick = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.fragment_share_search2, container, false);
		http = new HttpUtils();
		ViewUtils.inject(this, v);
		dbUtils = DbUtils.create(getActivity());

		// 每次点击item添加搜索历史，并调用inithistory()更新搜索历史
		ViewPager searchviewPager = (ViewPager) getActivity().findViewById(R.id.id_sfsearch_viewpager);
		ViewPagerAdapter searchAdapter = (ViewPagerAdapter) searchviewPager.getAdapter();
		final Fragment_Share_Search fragment_Share_Search = (Fragment_Share_Search) searchAdapter
				.instantiateItem(searchviewPager, 0);
		final ShareAndFriendsSearchActivity parentActivity = (ShareAndFriendsSearchActivity) getActivity();
		MessageList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub

				Log.d("onItemClick1", "onItemClick");
				ShareHistorySearchEntity entity = new ShareHistorySearchEntity();
				HashMap<String, String> messageitem = (HashMap<String, String>) MessageList.getAdapter()
						.getItem(position);
				entity.setShortname(messageitem.get("shortName"));
				entity.setSymbol(messageitem.get("symbol"));

				entity.setSecurityType(messageitem.get("securityType"));

				try {
					dbUtils.saveOrUpdate(entity);

				} catch (Exception e) {
					// TODO: handle exception
					Log.d("saveOrUpdate88", e.toString());
				}

				Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
				ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String, String>>();
				stocks.add(datas.get(position));
				Log.d("ccjstocks", stocks.toString());
				intent.putExtra("stocks", stocks);
				intent.putExtra("position", -3);
				startActivity(intent);

			}
		});

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("ss_edit");
		getActivity().registerReceiver(receiver, intentFilter);
		super.onActivityCreated(savedInstanceState);
	}

	// 用于注册广播的类
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			// 得到广播中得到的数据，并显示出来
			// 从广播得到edittext的内容
			try {
				String search = intent.getStringExtra("datas");
				String url = null;
				// 未登录
				if (RsSharedUtil.getString(getActivity(), "access_token").equals("")) {
					url = AppConfig.VERSION_URL + "quotation/search.json?";
					url += "&pageIndex=0&pageSize=10&keyword=" + search;
				} else {
					url = AppConfig.VERSION_URL + "quotation/search.json?access_token=";
					url += RsSharedUtil.getString(getActivity(), "access_token");
					url += "&pageIndex=0&pageSize=10&keyword=" + search;
				}

				Log.d("1.1.2url", url);

				http.send(HttpRequest.HttpMethod.GET, url, null, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.d("adSuccess", "Success");
						try {
							Log.d("result", arg0.result.toString());
							JSONArray jsonArray = new JSONArray(arg0.result.toString());
							Log.d("respone", jsonArray.toString());
							datas = new ArrayList<HashMap<String, String>>();
							for (int m = 0; m < jsonArray.length(); m++) {
								// 每个子项数据
								HashMap<String, String> data = new HashMap<String, String>();
								Iterator<String> jsIterator;
								try {
									jsIterator = jsonArray.getJSONObject(m).keys();
									while (jsIterator.hasNext()) {
										String key = jsIterator.next();
										data.put(key, jsonArray.getJSONObject(m).get(key).toString());
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								datas.add(data);
							}
							Log.d("datasssssss", datas.toString());
							ShareSearchAdapter2 mAdapter = new ShareSearchAdapter2(getActivity(), datas);
							MessageList.setAdapter(mAdapter);

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});
			} catch (Exception e) {
				Log.d("1.1.2false", e.toString());
				// TODO: handle exception
			}

		}
	};

	// 订阅自选股(取消订阅自选股)
	public void initfollow(final String follow, String symbol, String type) {
		final InternetDialog internetDialog = new InternetDialog(getActivity());
		JSONArray pArray = new JSONArray();
		JSONObject params = new JSONObject();
		try {
			params.put("symbol", symbol);
			params.put("type", type);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pArray.put(params);
		Log.d("pArray", pArray.toString());
		String url = AppConfig.URL_USER + "security.json?access_token=";
		url = url + RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url += "&followType=" + follow;
		Log.d("1.5.3url", url);

		StringRequest stringRequest = new StringRequest(pArray, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {

				Log.d("initfollow", response);
				// TODO Auto-generated method stub
				// Toast.makeText(getActivity().getApplication(),
				// "提交成功" + response, Toast.LENGTH_SHORT).show();
				Log.d("dj_follow", follow);
				if (follow.equals("true"))
					internetDialog.showInternetDialog("添加成功", true);
				else
					internetDialog.showInternetDialog("取消成功", true);
				// 提交成功后发送广播通知行情界面更新
				Intent intent = new Intent(); // 要发送的内容
				intent.setAction("situation_update"); // 设置广播的action
				getActivity().sendBroadcast(intent); // 发送广播
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("false", error.toString());
			}
		});
		stringRequest.setTag("ApplyForStatementActivity");
		MyApplication.getRequestQueue().add(stringRequest);
		http.sHttpCache.clear();
	}

	@Override
	public void onDestroy() {
		// 注销广播
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	public class ShareSearchAdapter2 extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> lists;

		public ShareSearchAdapter2(Context context, ArrayList<HashMap<String, String>> lists) {
			this.context = context;
			this.lists = lists;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return lists.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			// TODO Auto-generated method stub

			return lists.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {

			if (converView == null) {

				converView = inflater.inflate(R.layout.item_fragment_share_search_list, arg2, false);
			}

			TextView name = (TextView) AbViewHolder.get(converView, R.id.tv_sharefri_name);
			final TextView stockCode = (TextView) AbViewHolder.get(converView, R.id.tv_sharefri_content);
			final ImageView addcut = (ImageView) AbViewHolder.get(converView, R.id.iv_sharefri_addcut);
			final LinearLayout ll_sharefri_addcut = (LinearLayout) AbViewHolder.get(converView,
					R.id.ll_sharefri_addcut);
			final String securityType = lists.get(position).get("securityType");
			name.setText(lists.get(position).get("shortName"));
			stockCode.setText(lists.get(position).get("symbol"));
			Log.d("stockCode", stockCode.getText().toString());
			final String saddcut = lists.get(position).get("followed");
			if (saddcut.equals("false")) {
				addcut.setBackgroundDrawable(getResources().getDrawable(R.drawable.add));
			} else if (saddcut.equals("true")) {
				addcut.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_share_delete));
			}
			ll_sharefri_addcut.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (saddcut.equals("true")) {
						initfollow("false", stockCode.getText().toString(), securityType);
						addcut.setBackgroundDrawable(getResources().getDrawable(R.drawable.add));
						// 发广播通知与数据行情相关的页面更新
						Intent intent = new Intent(); // 要发送的内容
						intent.setAction("with_situation_update"); // 设置广播的action
						getActivity().sendBroadcast(intent); // 发送广播
					}
					if (saddcut.equals("false")) {
						if (RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN).equals("")) {
							/*
							 * startActivity(new Intent(getActivity(),
							 * LoginActivity.class));
							 */
							Intent intent = new Intent();
							intent.setClass(getActivity(), LoginActivity.class);
							intent.putExtra("isFirst", false);
							startActivity(intent);
						} else {
							// 发广播通知与数据行情相关的页面更新
							Intent intent = new Intent(); // 要发送的内容
							intent.setAction("with_situation_update"); // 设置广播的action
							getActivity().sendBroadcast(intent); // 发送广播
							initfollow("true", stockCode.getText().toString(), securityType);
							addcut.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_share_delete));
						}
					}
				}
			});
			return converView;
		}

	}
}
