package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.personal.OtherPeopleStockActivity;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_My_Stock extends Fragment {
	@ViewInject(R.id.mv_stock)
	MyListView mv_stock;

	// 提示无评论
	@ViewInject(R.id.tv_no_content)
	private TextView tv_no_content;

	// 查看更多
	@ViewInject(R.id.tv_watch_more1)
	private TextView tv_watch_more1;

	// 标题
	@ViewInject(R.id.tv_message)
	private TextView tv_message;

	private String userName = "";
	private String userUuid = "";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_stock, null);
		ViewUtils.inject(this, v);
		init();
		return v;
	}

	private void init() {
		String url = AppConfig.VERSION_URL
				+ "user/listed/list.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		if (getActivity() instanceof OtherPeolpeInformationActivity) {
			url = url + "&userUuid="
					+ getActivity().getIntent().getExtras().getString("uuid")
					+ "&pageSize=3&pageIndex=0";
			tv_message.setText(getActivity().getIntent().getExtras()
					.getString("userName")
					+ "的自选股：");
			userUuid = getActivity().getIntent().getExtras().getString("uuid");
			userName = getActivity().getIntent().getExtras()
					.getString("userName");
			Log.d("userUuid", userUuid);
		} else {
			url = url + "&userUuid=myself&pageSize=3&pageIndex=0";
			// 获取个人信息
			DbUtils dbUtils = DbUtils.create(getActivity());
			try {
				PersonalInformation personalInformation = dbUtils.findById(
						PersonalInformation.class,
						RsSharedUtil.getString(getActivity(), AppConfig.UUID));
				tv_message.setText("我的自选股：");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.d("Fragment_My_Stock_url", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("Fragment_My_Stock_Respone", response.toString()
								+ "成功");

						try {
							JSONObject jsonObject = new JSONObject(response);
							String bodyString = jsonObject
									.getString("listFollowQuoteds");
							JSONArray jsonArray = new JSONArray(bodyString);
							final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
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
								mv_stock.setAdapter(new StockListAdapter(
										getActivity(), datas));

								if (datas.size() == 0) {
									tv_watch_more1.setVisibility(View.GONE);
									tv_no_content.setVisibility(View.VISIBLE);
								} else {

									tv_watch_more1.setVisibility(View.VISIBLE);
									tv_no_content.setVisibility(View.GONE);
								}

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		stringRequest.setTag("Fragment_My_Stock");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@OnClick({ R.id.tv_watch_more1 })
	public void onClick(View v) {
		switch (v.getId()) {
		// 查看更多
		case R.id.tv_watch_more1:
			if (userUuid.equals("")) {// 用户自身的自选股列表，跳转到主页的沪深
//				startActivity(new Intent(getActivity(), MainActivity.class));
				Intent intent = new Intent(getActivity(),
						OtherPeopleStockActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userUuid", RsSharedUtil.getString(getActivity(), AppConfig.UUID));
				bundle.putString("userName", "我");
				intent.putExtras(bundle);

				startActivity(intent);
			} else {

				Intent intent = new Intent(getActivity(),
						OtherPeopleStockActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userUuid", userUuid);
				bundle.putString("userName", userName);
				intent.putExtras(bundle);

				startActivity(intent);
			}
//			brocastSetPage();
			break;

		default:
			break;
		}
	}

	/**
	 * 通知首页的viewpager切换到沪深页
	 */
	public void brocastSetPage() {
		Intent intent = new Intent();
		intent.setAction("currentItem2");
		getActivity().sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_My_Stock");
		super.onDestroy();
	}

	class StockListAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		StockListAdapter(Context context,
				ArrayList<HashMap<String, String>> datas) {
			this.context = context;
			this.list = datas;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater.inflate(R.layout.item_stock_list, null);

				holder.tv_enterprise = (TextView) contentView
						.findViewById(R.id.tv_enterprise);
				holder.tv_symbol = (TextView) contentView
						.findViewById(R.id.tv_symbol);
				holder.tv_range = (TextView) contentView
						.findViewById(R.id.tv_range);
				holder.tv_current_price = (TextView) contentView
						.findViewById(R.id.tv_current_price);

				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}

			holder.tv_enterprise.setText((CharSequence) list.get(position).get(
					"shortname"));
			holder.tv_symbol.setText((CharSequence) list.get(position).get(
					"symbol"));
			holder.tv_range.setText((CharSequence) list.get(position).get(
					"growthRate"));
			holder.tv_current_price.setText((CharSequence) list.get(position)
					.get("newPrice"));
			contentView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//跳转到个股详情
					ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("symbol", list.get(position).get("symbol").toString());
					map.put("shortname", list.get(position).get("shortname").toString());
					map.put("securityType", list.get(position).get("securityType").toString());
					stocks.add(map);
					Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
					intent.putExtra("stocks", stocks);
					intent.putExtra("position", 0);
					startActivity(intent);
				}
			});
			return contentView;
		}

		class ViewHolder {

			TextView tv_enterprise;
			TextView tv_symbol;
			TextView tv_range;
			TextView tv_current_price;
		}

	}
}
