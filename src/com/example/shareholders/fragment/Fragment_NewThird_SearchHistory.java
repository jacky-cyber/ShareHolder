package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.ShareHistorySearchEntity;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment_NewThird_SearchHistory extends Fragment{
	View mview;
	@ViewInject(R.id.share_search_s_list1)
	private ListView HistoryList;
	private List<ShareHistorySearchEntity> historyEntitieslist;
	private ArrayList<HashMap<String, String>> followlist;
	private ShareSearchAdapter adapter;
	Fragment_NewThird_Search fragment_NewThird_Search;
	DbUtils dbUtils;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		//布局和沪深的股票搜索一样，用同一个
		mview = inflater.inflate(R.layout.fragment_share_search, container, false);
		ViewUtils.inject(this,mview);
		dbUtils = DbUtils.create(getActivity());
		initview();
		initfollowed();
		return mview;
	}
	private void initview(){
		ViewPager myViewPager=(ViewPager) getActivity().findViewById(R.id.id_newthrid_viewpager);
		ViewPagerAdapter mainadapter=(ViewPagerAdapter) myViewPager.getAdapter();
		fragment_NewThird_Search=(Fragment_NewThird_Search) mainadapter.instantiateItem(myViewPager, 1);
		
		historyEntitieslist = new ArrayList<ShareHistorySearchEntity>();
		followlist = new ArrayList<HashMap<String, String>>();
		try {
			historyEntitieslist = dbUtils.findAll(Selector
					.from(ShareHistorySearchEntity.class));
			Log.d("historyEntitieslist", historyEntitieslist.toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.d("historyEntitieslise", e1.toString());
		}
		View view_clean_history;
		view_clean_history = LayoutInflater.from(getActivity()).inflate(
				R.layout.item_ll_search_clean_history, null);
		// 加在listview下面
		HistoryList.addFooterView(view_clean_history);
		view_clean_history.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					dbUtils.deleteAll(ShareHistorySearchEntity.class);
					historyEntitieslist.clear();
					adapter.notifyDataSetChanged();
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	public class ShareSearchAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		private List<ShareHistorySearchEntity> lists;
		ArrayList<HashMap<String, String>> followlist;

		public ShareSearchAdapter(Context context,
				List<ShareHistorySearchEntity> lists,
				ArrayList<HashMap<String, String>> followlist) {
			this.context = context;
			this.lists = lists;
			this.followlist = followlist;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (lists == null) {
				Log.d("asd", "asd");
				return 0;
			}
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = inflater.inflate(
						R.layout.item_fragment_share_search_list, arg2, false);
			}
			TextView shortname = (TextView) AbViewHolder.get(converView,
					R.id.tv_sharefri_name);
			final TextView symbol = (TextView) AbViewHolder.get(converView,
					R.id.tv_sharefri_content);
			final ImageView addcut = (ImageView) AbViewHolder.get(converView,
					R.id.iv_sharefri_addcut);
			final LinearLayout ll_sharefri_addcut = (LinearLayout) AbViewHolder.get(converView,
					R.id.ll_sharefri_addcut);
			final String securityType = lists.get(lists.size() - position - 1)
					.getSecurityType();
			shortname.setText(lists.get(lists.size() - position - 1)
					.getShortname());
			symbol.setText(lists.get(lists.size() - position - 1).getSymbol());
			final String saddcut;
			if (followlist.size() == 0) {

			} else {
				saddcut = followlist.get(position).get("followType");
				if (saddcut.equals("false")) {
					addcut.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.add));
				} else if (saddcut.equals("true")) {
					addcut.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.btn_share_delete));
				}
				ll_sharefri_addcut.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (saddcut.equals("true")) {

							fragment_NewThird_Search.initfollow("false", symbol
									.getText().toString(), securityType);
							addcut.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.add));

							initfollowed();
						} else {
							if (RsSharedUtil.getString(getActivity(),
									AppConfig.ACCESS_TOKEN).equals("")) {
								startActivity(new Intent(getActivity(),
										LoginActivity.class));
							} else {

								fragment_NewThird_Search.initfollow("true",
										symbol.getText().toString(),
										securityType);
								addcut.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.btn_share_delete));

								initfollowed();
							}
						}

					}
				});
			}

			return converView;
		}

	}

	private void initfollowed() {

		String url = AppConfig.VERSION_URL
				+ "quotation/followType.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("1.1.3url", url.toString());
		JSONArray jsonRequest = new JSONArray();
		JSONObject jsonObject = null;
		String symbol = null;
		String securityType = null;
		//判断list是否为空 list为空时用.size()会报空指针
		if (historyEntitieslist!=null) {
			for (int i = 0; i < historyEntitieslist.size(); i++) {
				jsonObject = new JSONObject();
				symbol = historyEntitieslist
						.get(historyEntitieslist.size() - i - 1).getSymbol();
				securityType = historyEntitieslist.get(
						historyEntitieslist.size() - i - 1).getSecurityType();
				try {
					jsonObject.put("symbol", symbol);
					jsonObject.put("securityType", securityType);
					jsonRequest.put(jsonObject);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		Log.d("jsonRequest", jsonRequest.toString());
		StringRequest stringRequest = new StringRequest(jsonRequest, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {
							followlist = new ArrayList<HashMap<String, String>>();
							JSONArray all = new JSONArray(response);
							Log.d("1.1.3all", all.toString());
							for (int i = 0; i < all.length(); i++) {
								HashMap<String, String> followmap = new HashMap<String, String>();
								followmap.put(
										"followType",
										all.getJSONObject(i).getString(
												"followType"));
								followlist.add(followmap);
							}
							Log.d("historyEntitieslist2",
									historyEntitieslist.toString());
							Log.d("followlist2", followlist.toString());
							adapter = new ShareSearchAdapter(getActivity(),
									historyEntitieslist, followlist);
							HistoryList.setAdapter(adapter);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);

	}
}
