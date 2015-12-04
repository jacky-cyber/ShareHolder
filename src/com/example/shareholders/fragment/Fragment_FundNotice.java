package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundNoticeDetailsActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_FundNotice extends Fragment {
	@ViewInject(R.id.lv_fund_announce)
	private ListView lv_notice;
	private FundNoticeAdapter adapter;

	private int pageIndex = 0;
	private int pageSize = 10;

	private RequestQueue volleyRequestQueue = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_fund_notice, null);
		ViewUtils.inject(this, v);
		volleyRequestQueue = Volley.newRequestQueue(getActivity());

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {

		getNoticeList();

		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 获取基金资讯的列表
	 */
	private void getNoticeList() {
		String url = AppConfig.URL_INFO + "ann/list.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");

		JSONObject params = new JSONObject();
		try {
			String symbol = getActivity().getIntent().getExtras()
					.getString("symbol");

			params.put("symbol", symbol);
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("") || response.equals("[0]")) {
							// ToastUtils.showToast(getActivity(), "没有数据");

						} else {
							try {
								JSONArray jsonArray = new JSONArray(response
										.toString());
								Iterator<String> jIterator = null;
								final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = null;

								for (int i = 0; i < jsonArray.length(); i++) {
									jIterator = jsonArray.getJSONObject(i)
											.keys();
									data = new HashMap<String, String>();
									while (jIterator.hasNext()) {
										String key = jIterator.next();
										data.put(key, jsonArray
												.getJSONObject(i)
												.getString(key));
									}

									datas.add(data);

								}

								adapter = new FundNoticeAdapter(getActivity(),
										datas);
								lv_notice.setAdapter(adapter);

								lv_notice
										.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> parent,
													View view, int position,
													long id) {
												// TODO Auto-generated method
												// stub

												Intent intent = new Intent(
														getActivity(),
														FundNoticeDetailsActivity.class);

												Bundle bundle = new Bundle();
												// 传递公告id
												bundle.putString(
														"announcementid",
														datas.get(position)
																.get("announcementid"));

												intent.putExtras(bundle);

												startActivity(intent);
											}
										});

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	public class FundNoticeAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;

		public FundNoticeAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			// TODO Auto-generated constructor stub
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_fund_announce, null);

				holder.tv_notice_text = (TextView) view
						.findViewById(R.id.tv_fund_an_text);
				holder.tv_notice_date = (TextView) view
						.findViewById(R.id.tv_fund_an_date);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 公告标题
			holder.tv_notice_text.setText(list.get(position).get("title"));

			// 公告时间
			String date = list.get(position).get("declaredate");
			long date_long = Long.parseLong(date);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			date = simpleDateFormat.format(new Date(date_long));

			holder.tv_notice_date.setText(date);
			return view;
		}

		class ViewHolder {

			TextView tv_notice_text;
			TextView tv_notice_date;
		}
	}

}
