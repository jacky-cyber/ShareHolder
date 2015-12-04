package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.example.shareholders.activity.shop.GoodsDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.shop.PopularProduct;
import com.example.shareholders.util.Mapper;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 热门商品
 * @author jat
 *
 */
public class Fragment_Hot_Product extends Fragment implements OnFooterRefreshListener,OnHeaderRefreshListener{

	DbUtils dbUtils;

	@ViewInject(R.id.lv_shop_list)
	private ListView lv_shop_list;
	//上下拉刷新
	@ViewInject(R.id.pull_to_refresh)
	private PullToRefreshView pull_to_refresh;
	//页大小与页数
	private int indexPage = 1;
	private int pageSize = 15;

	//头部刷新底部刷新
	private int HEAD = 0;
	private int FOOT = 1;
	
	private GoodsAdapter adapter = null;

	ArrayList<PopularProduct> datas = new ArrayList<PopularProduct>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dbUtils = DbUtils.create(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_shop_list, null);
		ViewUtils.inject(this,view);
		getPopularGoods(indexPage,pageSize,HEAD);
		lv_shop_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
		//设置上下拉刷新
		pull_to_refresh.setOnFooterRefreshListener(this);
		pull_to_refresh.setOnHeaderRefreshListener(this);
		return view;
	}

	/**
	 * 热门商品接口
	 */
	private void getPopularGoods(int pageNo, int pageSize, final int type) {
		//如果是首次加载或者下拉刷新则清楚数据
		if (type == HEAD) {
			datas.clear();
		}
		String url = AppConfig.URL_SHOP + "product/getPopularProduct";
		Log.d("dj_hopProduct_url", url);
		JSONObject params = new JSONObject();
		try {
			params.put("pageIndex", pageNo);
			params.put("pageSize", pageSize);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					if (response.equals("")) {
						//						ToastUtils.showToast(getActivity(), "热门商品没有数据");
					} else {

						JSONArray jsonArray = new JSONArray(response);
						//数据迭代
						Iterator<String> iterator = null;
						Mapper mapper = new Mapper();
						dbUtils.deleteAll(PopularProduct.class);
						for (int i = 0; i < jsonArray.length(); i++) {
							PopularProduct product = mapper.readValue(jsonArray.get(i).toString(), PopularProduct.class);
							dbUtils.save(product);
							datas.add(product);
						}
						//首次加载或者下拉刷新则setadapter
						if (type==HEAD) {
							adapter = new GoodsAdapter(datas);
							lv_shop_list.setAdapter(adapter);
						}
						//通知更新
						adapter.notifyDataSetChanged();

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				//加载失败调用数据库缓存数据
				List<PopularProduct> datas = new ArrayList<PopularProduct>();
				datas.clear();

				try {
					datas = dbUtils.findAll(PopularProduct.class);
					lv_shop_list.setAdapter(new GoodsAdapter(
							datas));
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		stringRequest.setTag("getPopularGoods");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 商品adapter
	 */
	private class GoodsAdapter extends BaseAdapter {
		private List<PopularProduct> list = new ArrayList<PopularProduct>();
		private LayoutInflater minflater;

		public GoodsAdapter(List<PopularProduct> list) {
			minflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
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
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {
			ViewHolder viewHolder = null;

			if (contentView == null) {
				contentView = minflater.inflate(R.layout.item_shop_product, null);

				viewHolder = new ViewHolder();
				viewHolder.iv_item_shop_product_logo = (ImageView) contentView
						.findViewById(R.id.iv_item_shop_product_logo);
				viewHolder.iv_item_shop_product_logo.setScaleType(ScaleType.FIT_XY);
				viewHolder.tv_product_item_name = (TextView) contentView
						.findViewById(R.id.tv_product_item_name);
				viewHolder.tv_product_item_price = (TextView) contentView
						.findViewById(R.id.tv_product_item_price);

				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}

			//			JSONArray picarray = (JSONArray) list.get(position).get("picarray");
			//			String picUrl = null;
			//			try {
			//				picUrl = picarray.getJSONObject(0).getString("picUrl");
			//			} catch (Exception e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}

			ImageLoader.getInstance().displayImage((String) list.get(position).getPicUrl(), viewHolder.iv_item_shop_product_logo);

			viewHolder.tv_product_item_name.setText(list.get(position).getProdName());
			viewHolder.tv_product_item_price.setText(list.get(position).getProdPrice()
					+ "元");

			//设置跳转到商品详情
			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("dj_proUuid", list.get(position).getProdUuid());
					Intent intent = new Intent(
							getActivity(),
							GoodsDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString(
							"prodUuid",
							list.get(position).getProdUuid());
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});
			return contentView;
		}

		class ViewHolder {
			ImageView iv_item_shop_product_logo;
			TextView tv_product_item_name;
			TextView tv_product_item_price;
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("getPopularGoods");
	}

	//下拉刷新，重新加载一个数据
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				indexPage = 1;
				getPopularGoods(indexPage, pageSize, HEAD);
				//上拉完成
				pull_to_refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	//上拉刷新页数加1，访问后台获取数据通知更新
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//页数加1
//				indexPage = indexPage+1;
//				getPopularGoods(indexPage, pageSize, FOOT);
				//下拉完成
				pull_to_refresh.onFooterRefreshComplete();
			}
		}, 0);
	}

}
