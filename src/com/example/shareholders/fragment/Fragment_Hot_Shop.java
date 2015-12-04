package com.example.shareholders.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.shop.CompanyDetailActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.shop.PopularCompany;
import com.example.shareholders.util.Mapper;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 热门公司
 * 
 * @author jat
 * 
 */
public class Fragment_Hot_Shop extends Fragment implements OnFooterRefreshListener,OnHeaderRefreshListener{

	DbUtils dbUtils;

	@ViewInject(R.id.lv_shop_list)
	private ListView lv_shop_list;
	//上下拉刷新
	@ViewInject(R.id.pull_to_refresh)
	private PullToRefreshView pull_to_refresh;
	//页数与页大小
	private int pageNo = 1;
	private int pageSize = 15;
	
	//头部与底部刷新
	private int HEAD = 0;
	private int FOOT = 1;
	
	//适配器
	private EnterpriseAdapter adapter;
	//列表数据
	final ArrayList<PopularCompany> popularCompanies = new ArrayList<PopularCompany>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_shop_list, null);
		ViewUtils.inject(this, view);
		dbUtils = DbUtils.create(getActivity());
		//a初始化数据
		getPopularShops(pageNo,pageSize,HEAD);
		//上下拉设置刷新
		pull_to_refresh.setOnFooterRefreshListener(this);
		pull_to_refresh.setOnHeaderRefreshListener(this);
		lv_shop_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
		return view;
	}

	/**
	 * 热门店铺（公司）
	 */

	private void getPopularShops(int pageNo, int pageSize, final int type) {
		//如果是首次加载或者是下拉刷新，则清除已有数据，重新加载一次数据
		if (type==HEAD) {
			popularCompanies.clear();
		}
		
		String url = AppConfig.URL_SHOP + "shop/ListPopularShop";
		Log.d("dj_hopShop_url", url);
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, new JSONObject(), new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("")) {
							// Toast.makeText(getActivity(), "热门公司无数据",
							// 1).show();
						} else {
							try {
								popularCompanies.clear();
								Mapper objectMapper = new Mapper();
								JSONArray jsonArray = new JSONArray(response);
								//数据加载成功，则删除缓存数据，重新进行缓存最新数据
								dbUtils.deleteAll(PopularCompany.class);
								for (int i = 0; i < jsonArray.length(); i++) {
									PopularCompany popularCompany = objectMapper
											.readValue(jsonArray.get(i)
													.toString(),
													PopularCompany.class);
									//缓存最新数据
									dbUtils.save(popularCompany);
									popularCompanies.add(popularCompany);
								}
								//如果是第一次加载或者是下拉刷新，则建立适配器
								if (type==HEAD) {
									adapter = new EnterpriseAdapter(popularCompanies);
									lv_shop_list.setAdapter(adapter);
								}
								//数据加载完毕，通知适配器更新数据
								adapter.notifyDataSetChanged();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Log.d("liang_error_response", e.toString());
							}

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("liang_error", "error");
						try {
							//数据访问失败，则取出缓存，进行显示
							ArrayList<PopularCompany> popularCompanies = new ArrayList<PopularCompany>();
							popularCompanies.clear();
							popularCompanies = (ArrayList<PopularCompany>) dbUtils
									.findAll(PopularCompany.class);
							lv_shop_list.setAdapter(new EnterpriseAdapter(
									popularCompanies));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		stringRequest.setTag("getPopularShops");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("getPopularShops");
	}

	/**
	 * 热门公司adapter
	 */
	private class EnterpriseAdapter extends BaseAdapter {
		private ArrayList<PopularCompany> list;
		private LayoutInflater minflater;

		public EnterpriseAdapter(ArrayList<PopularCompany> list) {
			minflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
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
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {
			ViewHolder viewHolder = null;

			if (contentView == null) {
				contentView = minflater.inflate(R.layout.item_shop_shop, null);
				viewHolder = new ViewHolder();

				viewHolder.iv_item_shop_logo = (ImageView) contentView
						.findViewById(R.id.iv_item_shop_logo);
				viewHolder.tv_item_shop_name = (TextView) contentView
						.findViewById(R.id.tv_item_shop_name);

				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}

			ImageLoader.getInstance().displayImage(
					list.get(position).getPicUrl(),
					viewHolder.iv_item_shop_logo);
			viewHolder.iv_item_shop_logo.setScaleType(ScaleType.FIT_XY);
			viewHolder.tv_item_shop_name.setText(list.get(position)
					.getShopName());
			//监听进入公司详情
			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							CompanyDetailActivity.class);

					Bundle bundle = new Bundle();
					// 传递shopId
					bundle.putString("shopId", list.get(position).getShopId()
							+ "");
					intent.putExtras(bundle);

					startActivity(intent);
				}
			});
			return contentView;
		}

		class ViewHolder {
			TextView tv_item_shop_name;
			ImageView iv_item_shop_logo;
		}

	}

	//下拉刷新，对已有数据进行清除，把页数设为1，重新加载一次数据
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//页数为1
				pageNo = 1;
				getPopularShops(pageNo, pageSize, HEAD);
				//下拉刷新完成
				pull_to_refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	//上拉刷新，页数加1，加载数据，通知更新
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//页数加1
//				pageNo = pageNo+1;
//				getPopularShops(pageNo, pageSize, FOOT);
				//上拉刷新完成
				pull_to_refresh
				.onFooterRefreshComplete();
			}
		}, 0);
	}
}
