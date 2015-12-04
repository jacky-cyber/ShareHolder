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
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.ShareHistorySearchEntity;
import com.example.shareholders.fragment.Fragment_Share_Search2.ShareSearchAdapter2;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_NewThird_Search extends Fragment {
	private String actoken;
	View mview;
	HttpUtils http;
	DbUtils dbUtils;
	@ViewInject(R.id.share_search_s_list2)
	private ListView newthird_search_list;
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

	Madapter mAdapter;
	ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		mview = inflater.inflate(R.layout.fragment_share_search2, container, false);
		http = new HttpUtils();
		ViewUtils.inject(this, mview);
		dbUtils = DbUtils.create(getActivity());
		initview();
		return mview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("nts_edit");
		getActivity().registerReceiver(receiver, intentFilter);
		super.onActivityCreated(savedInstanceState);
	}

	private void initview(){
		actoken=RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		mAdapter = new Madapter(getActivity(), datas);
		newthird_search_list.setAdapter(mAdapter);
		newthird_search_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				
				ShareHistorySearchEntity entity = new ShareHistorySearchEntity();
				HashMap<String, String> messageitem = (HashMap<String, String>) newthird_search_list
						.getAdapter().getItem(position);
				entity.setShortname(messageitem.get("shortName"));
				entity.setSymbol(messageitem.get("symbol"));

				entity.setSecurityType(messageitem.get("securityType"));

				try {
					dbUtils.saveOrUpdate(entity);

				} catch (Exception e) {
					// TODO: handle exception
					Log.d("saveOrUpdate88", e.toString());
				}
				/*Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
				ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
				stocks.add(datas.get(position));
				intent.putExtra("stocks", stocks);
				intent.putExtra("position", -3);
				startActivity(intent);*/
			}
		});
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
					url = AppConfig.VERSION_URL + "neeq/query.json?keyword="+search;
					url += "&pageSize="+PAGE_SIZE+"&pageIndex="+all_index;
				} else {
					url = AppConfig.VERSION_URL + "neeq/query.json?keyword="+search;
					url += "&pageSize="+PAGE_SIZE+"&pageIndex="+all_index+"&access_token=";
					url+=RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
				}
				Log.d("new2.1url", url);
				http.send(HttpRequest.HttpMethod.GET, url, null, new RequestCallBack<String>() {
					
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.d("ffaaaaaaal", "fffaal");
						Toast.makeText(getActivity(), "网络不给力", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						Log.d("adSuccess", "Success");
						try {
							Log.d("result", response.result.toString());
							JSONObject jsonObject=new JSONObject(response.result.toString());
							JSONArray jsonArray = jsonObject.getJSONArray("companyResponses");
							Log.d("respone", jsonArray.toString());
							datas.clear();
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
							mAdapter.notifyDataSetChanged();

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});
			} catch (Exception e) {
				Log.d("new2.1false", e.toString());
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
		url +=actoken;
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
				/*// 提交成功后发送广播通知行情界面更新
				Intent intent = new Intent(); // 要发送的内容
				intent.setAction("situation_update"); // 设置广播的action
				getActivity().sendBroadcast(intent); // 发送广播*/
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

	public class Madapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> lists;

		public Madapter(Context context, ArrayList<HashMap<String, String>> lists) {
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
						/*// 发广播通知与数据行情相关的页面更新
						Intent intent = new Intent(); // 要发送的内容
						intent.setAction("with_situation_update"); // 设置广播的action
						getActivity().sendBroadcast(intent); // 发送广播*/
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
							/*// 发广播通知与数据行情相关的页面更新
							Intent intent = new Intent(); // 要发送的内容
							intent.setAction("with_situation_update"); // 设置广播的action
							getActivity().sendBroadcast(intent); // 发送广播*/
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
