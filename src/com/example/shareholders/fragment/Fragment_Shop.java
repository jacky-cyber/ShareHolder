package com.example.shareholders.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.PrivateCredentialPermission;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.shop.CompanyDetailActivity;
import com.example.shareholders.activity.shop.GoodsDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.shop.NewProduct;
import com.example.shareholders.jacksonModel.shop.PopularCompany;
import com.example.shareholders.jacksonModel.shop.PopularProduct;
import com.example.shareholders.jacksonModel.shop.PromionProduct;
import com.example.shareholders.jacksonModel.shop.ShopBanner;
import com.example.shareholders.util.Mapper;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Shop extends Fragment implements OnItemClickListener {

	// private List<ImageView> headList;
	private List<View> dots; // 图片标题正文的那些点
	private int currentItem = 0; // 当前图片的索引号
	private ScheduledExecutorService scheduledExecutorService; // 切换当前显示的图片
	private ArrayList<PopularCompany> popularCompanies = new ArrayList<PopularCompany>();

	@ViewInject(R.id.ll_dots)
	private LinearLayout ll_dots;
	@ViewInject(R.id.vp_toutiao_head)
	ViewPager headPage;
	// 热门商品
	@ViewInject(R.id.gridview_popular)
	private GridView gridview_popular;
	// 热门公司
	@ViewInject(R.id.gridview_enterprise)
	private GridView gridview_enterprise;
	// 最新商品
	@ViewInject(R.id.gridview_newest)
	private GridView gridview_newest;
	// 促销商品
	@ViewInject(R.id.gridview_promote)
	private GridView gridview_promote;

	EnterpriseAdapter enterpriseAdapter;
	GoodsAdapter goodsAdapter;

	private DbUtils dbUtils;

	private ArrayList<HashMap<String, String>> companies;
	private ArrayList<HashMap<String, String>> hotGoods;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shop, null);
		dbUtils = DbUtils.create(getActivity());
		ViewUtils.inject(this, view);
		init();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	};

	private void init() {
		dots = new ArrayList<View>();
		companies = new ArrayList<HashMap<String, String>>();
		hotGoods = new ArrayList<HashMap<String, String>>();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.commit();
		addHead();
		setGridView();
	}

	private void setGridView() {

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (10 * (78 + 7.5) * density);// 10列的宽度
		int itemWidth = (int) (78 * density);// 列宽78dp
		int spacingWidth = (int) (7.5 * density);// 间距7。5dp
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
		gridview_popular.setLayoutParams(params); // 重点
		gridview_popular.setColumnWidth(itemWidth); // 重点
		gridview_popular.setHorizontalSpacing(spacingWidth); // 间距
		gridview_popular.setStretchMode(GridView.NO_STRETCH);
		gridview_popular.setNumColumns(10); // 重点
		// gridview_popular.setAdapter(goodsAdapter);
		getPopularGoods();

		// gridview_popular.setOnItemClickListener(this);

		int gridview_width_enterprise = (int) (10 * (102 + 0) * density);// 10列的宽度
		int item_width_enterprise = (int) (102 * density);// 列宽102dp
		LinearLayout.LayoutParams params_enterprise = new LinearLayout.LayoutParams(
				gridview_width_enterprise,
				LinearLayout.LayoutParams.MATCH_PARENT);
		gridview_enterprise.setLayoutParams(params_enterprise); // 重点
		gridview_enterprise.setColumnWidth(item_width_enterprise); // 重点
		gridview_enterprise.setHorizontalSpacing(0); // 间距
		gridview_enterprise.setStretchMode(GridView.NO_STRETCH);
		gridview_enterprise.setNumColumns(10); // 重点
		// gridview_enterprise.setAdapter(enterpriseAdapter);
		getPopularShops();
		// getHotCompanies();
		// gridview_enterprise.setOnItemClickListener(this);

		gridview_newest.setLayoutParams(params); // 重点
		gridview_newest.setColumnWidth(itemWidth); // 重点
		gridview_newest.setHorizontalSpacing(spacingWidth); // 间距
		gridview_newest.setStretchMode(GridView.NO_STRETCH);
		gridview_newest.setNumColumns(10); // 重点
		// gridview_newest.setAdapter(goodsAdapter);
		getNewProduct();
		// gridview_newest.setOnItemClickListener(this);

		gridview_promote.setLayoutParams(params); // 重点
		gridview_promote.setColumnWidth(itemWidth); // 重点
		gridview_promote.setHorizontalSpacing(spacingWidth); // 间距
		gridview_promote.setStretchMode(GridView.NO_STRETCH);
		gridview_promote.setNumColumns(10); // 重点
		// gridview_promote.setAdapter(goodsAdapter);
		getPromotionProducts();
		// gridview_promote.setOnItemClickListener(this);

	}

	/**
	 * 热门店铺（公司）
	 */

	private void getPopularShops() {
		String url = AppConfig.URL_SHOP + "shop/listPopularShop";

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
								dbUtils.deleteAll(PopularCompany.class);
								for (int i = 0; i < jsonArray.length(); i++) {
									PopularCompany popularCompany = objectMapper
											.readValue(jsonArray.get(i)
													.toString(),
													PopularCompany.class);
									Log.d("jatjat", popularCompany.toString());
									dbUtils.save(popularCompany);
									popularCompanies.add(popularCompany);
								}
								gridview_enterprise
										.setAdapter(new EnterpriseAdapter(
												popularCompanies));

								// item点击跳到商铺详情
								gridview_enterprise
										.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> arg0,
													View arg1, int position,
													long arg3) {
												Intent intent = new Intent(
														getActivity(),
														CompanyDetailActivity.class);

												Bundle bundle = new Bundle();
												// 传递shopId
												bundle.putString(
														"shopId",
														popularCompanies.get(
																position)
																.getShopId()
																+ "");
												intent.putExtras(bundle);

												startActivity(intent);
											}
										});

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
							popularCompanies.clear();
							popularCompanies = (ArrayList<PopularCompany>) dbUtils
									.findAll(PopularCompany.class);
							gridview_enterprise
									.setAdapter(new EnterpriseAdapter(
											popularCompanies));

							// item点击跳到商铺详情
							gridview_enterprise
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long arg3) {
											Intent intent = new Intent(
													getActivity(),
													CompanyDetailActivity.class);

											Bundle bundle = new Bundle();
											// 传递shopId
											bundle.putString(
													"shopId",
													popularCompanies.get(
															position)
															.getShopId()
															+ "");
											intent.putExtras(bundle);

											startActivity(intent);
										}
									});

							Log.d("666666666666", companies.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		stringRequest.setTag("getPopularShops");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 新品上架接口
	 */
	private void getNewProduct() {
		String url = AppConfig.URL_SHOP + "product/getNewProduct";

		JSONObject params = new JSONObject();
		try {
			params.put("pageIndex", 1);
			params.put("pageSize", 10);
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
							} else {

								JSONArray jsonArray = new JSONArray(response);

								final List<NewProduct> datas = new ArrayList<NewProduct>();

								Mapper mapper = new Mapper();
								dbUtils.deleteAll(NewProduct.class);
								for (int i = 0; i < jsonArray.length(); i++) {
									NewProduct product = mapper.readValue(
											jsonArray.get(i).toString(),
											NewProduct.class);
									dbUtils.save(product);

									datas.add(product);
								}
								gridview_newest.setAdapter(new NewGoodsAdapter(
										datas));

							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("liang_error1", e.toString());
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error", jsonObject.getString("description"));

						} catch (Exception e) {

							Log.d("error_exception", e.toString());
						}
						List<NewProduct> datas = new ArrayList<NewProduct>();
						datas.clear();
						try {
							datas = dbUtils.findAll(NewProduct.class);

						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gridview_newest.setAdapter(new NewGoodsAdapter(datas));

					}
				});

		stringRequest.setTag("getNewProducts");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 推荐商品接口
	 */
	private void getPromotionProducts() {
		String url = AppConfig.URL_SHOP + "promotion/getPromToHomePage";
		Log.d("promotion_url", url);
		JSONObject params = new JSONObject();

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							if (response.equals("")) {
								// ToastUtils.showToast(getActivity(),
								// "推荐商品没有数据");
							} else {

								JSONArray jsonArray = new JSONArray(response);

								final ArrayList<PromionProduct> datas = new ArrayList<PromionProduct>();

								Mapper mapper = new Mapper();
								dbUtils.deleteAll(PromionProduct.class);
								for (int i = 0; i < jsonArray.length(); i++) {
									PromionProduct product = mapper.readValue(
											jsonArray.get(i).toString(),
											PromionProduct.class);
									dbUtils.save(product);
									Log.d("jjjjjjjjjjjjjjjjj",
											product.toString());
									datas.add(product);
								}
								gridview_promote
										.setAdapter(new TuiJianGoodsAdapter(
												datas));

							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						List<PromionProduct> datas = new ArrayList<PromionProduct>();
						datas.clear();

						try {
							datas = dbUtils.findAll(PromionProduct.class);
							gridview_popular
									.setAdapter(new TuiJianGoodsAdapter(datas));
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

		stringRequest.setTag("getPromotionProducts");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 热门商品接口
	 */
	private void getPopularGoods() {

		String url = AppConfig.URL_SHOP + "product/getPopularProduct";

		JSONObject params = new JSONObject();
		try {
			params.put("pageIndex", 1);
			params.put("pageSize", 10);
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
								// ToastUtils.showToast(getActivity(),
								// "热门商品没有数据");
							} else {

								JSONArray jsonArray = new JSONArray(response);

								final ArrayList<PopularProduct> datas = new ArrayList<PopularProduct>();
								Iterator<String> iterator = null;
								Mapper mapper = new Mapper();
								dbUtils.deleteAll(PopularProduct.class);
								for (int i = 0; i < jsonArray.length(); i++) {
									PopularProduct product = mapper.readValue(
											jsonArray.get(i).toString(),
											PopularProduct.class);
									dbUtils.save(product);

									datas.add(product);
								}
								gridview_popular.setAdapter(new GoodsAdapter(
										datas));

							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						List<PopularProduct> datas = new ArrayList<PopularProduct>();
						datas.clear();

						try {
							datas = dbUtils.findAll(PopularProduct.class);
							gridview_popular
									.setAdapter(new GoodsAdapter(datas));
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		stringRequest.setTag("getPopularGoods");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int arg2,
			long arg3) {
		switch (parent.getId()) {
		case R.id.gridview_popular:
		case R.id.gridview_newest:
		case R.id.gridview_promote:
			// startActivity(new Intent(getActivity(),
			// GoodsDetailsActivity.class));
			break;
		case R.id.gridview_enterprise:
			// startActivity(new Intent(getActivity(),
			// CompanyDetailActivity.class));
		default:
			break;
		}

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
		public View getView(int position, View contentView, ViewGroup arg2) {
			ViewHolder viewHolder = null;

			if (contentView == null) {
				contentView = minflater.inflate(R.layout.item_enterprise, null);
				viewHolder = new ViewHolder();

				viewHolder.iv_logo = (ImageView) contentView
						.findViewById(R.id.iv_logo);
				viewHolder.tv_company = (TextView) contentView
						.findViewById(R.id.tv_company);

				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}

			ImageLoader.getInstance().displayImage(
					list.get(position).getPicUrl(), viewHolder.iv_logo);
			viewHolder.tv_company.setText(list.get(position).getShopName());

			return contentView;
		}

		class ViewHolder {
			TextView tv_company;
			ImageView iv_logo;
		}

	}

	/**
	 * 推荐商品adapter
	 */
	private class TuiJianGoodsAdapter extends BaseAdapter {
		private List<PromionProduct> list;
		private LayoutInflater minflater;

		public TuiJianGoodsAdapter(List<PromionProduct> datas) {
			minflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.list = datas;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
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
				contentView = minflater.inflate(R.layout.item_goods, null);

				viewHolder = new ViewHolder();
				viewHolder.iv_good = (ImageView) contentView
						.findViewById(R.id.iv_good);
				viewHolder.tv_detail = (TextView) contentView
						.findViewById(R.id.tv_detail);
				viewHolder.tv_price = (TextView) contentView
						.findViewById(R.id.tv_price);

				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}

			// JSONArray picarray = (JSONArray)
			// list.get(position).get("picarray");

			ImageLoader.getInstance().displayImage(
					list.get(position).getPicUrl(), viewHolder.iv_good);

			viewHolder.tv_detail.setText(list.get(position).getProdName());
			viewHolder.tv_price
					.setText(list.get(position).getProdPrice() + "元");

			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							GoodsDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("prodUuid", list.get(position)
							.getProdUuid());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			return contentView;
		}

		class ViewHolder {
			ImageView iv_good;
			TextView tv_detail;
			TextView tv_price;
		}

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
				contentView = minflater.inflate(R.layout.item_goods, null);

				viewHolder = new ViewHolder();
				viewHolder.iv_good = (ImageView) contentView
						.findViewById(R.id.iv_good);
				viewHolder.tv_detail = (TextView) contentView
						.findViewById(R.id.tv_detail);
				viewHolder.tv_price = (TextView) contentView
						.findViewById(R.id.tv_price);

				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}

			// JSONArray picarray = (JSONArray)
			// list.get(position).get("picarray");
			// String picUrl = null;
			// try {
			// picUrl = picarray.getJSONObject(0).getString("picUrl");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			ImageLoader.getInstance()
					.displayImage((String) list.get(position).getPicUrl(),
							viewHolder.iv_good);

			viewHolder.tv_detail.setText(list.get(position).getProdName());
			viewHolder.tv_price
					.setText(list.get(position).getProdPrice() + "元");

			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							GoodsDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("prodUuid", list.get(position)
							.getProdUuid());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			return contentView;
		}

		class ViewHolder {
			ImageView iv_good;
			TextView tv_detail;
			TextView tv_price;
		}

	}

	/**
	 * 商品adapter
	 */
	private class NewGoodsAdapter extends BaseAdapter {
		private List<NewProduct> list = new ArrayList<NewProduct>();
		private LayoutInflater minflater;

		public NewGoodsAdapter(List<NewProduct> list) {
			minflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
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
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {
			ViewHolder viewHolder = null;

			if (contentView == null) {
				contentView = minflater.inflate(R.layout.item_goods, null);

				viewHolder = new ViewHolder();
				viewHolder.iv_good = (ImageView) contentView
						.findViewById(R.id.iv_good);
				viewHolder.tv_detail = (TextView) contentView
						.findViewById(R.id.tv_detail);
				viewHolder.tv_price = (TextView) contentView
						.findViewById(R.id.tv_price);

				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}

			// JSONArray picarray = (JSONArray)
			// list.get(position).get("picarray");
			// String picUrl = null;
			// try {
			// picUrl = picarray.getJSONObject(0).getString("picUrl");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			ImageLoader.getInstance()
					.displayImage((String) list.get(position).getPicUrl(),
							viewHolder.iv_good);

			viewHolder.tv_detail.setText(list.get(position).getProdName());
			viewHolder.tv_price
					.setText(list.get(position).getProdPrice() + "元");

			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							GoodsDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("prodUuid", list.get(position)
							.getProdUuid());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			return contentView;
		}

		class ViewHolder {
			ImageView iv_good;
			TextView tv_detail;
			TextView tv_price;
		}

	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (headPage) {
				currentItem = (currentItem + 1) % dots.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片

			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			headPage.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	/**
	 * 添加头部 头部接口
	 */

	private void addHead() {
		String url = AppConfig.URL_SHOP + "news/getAdvertiseToHomePage";
		Log.d("shop_url", url);
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, new JSONObject(), new Response.Listener<String>() {

					@Override
					public void onResponse(final String response) {
						Log.d("asddasfdfas", response);
						try {
							Mapper objectMapper = new Mapper();
							ArrayList<String> datas = new ArrayList<String>();
							// 存储商铺id
							ArrayList<String> idList = new ArrayList<String>();
							// 清除数据库缓存
							dbUtils.deleteAll(ShopBanner.class);
							JSONArray jsonArray = new JSONArray(response);
							Log.d("asddasfdfas", response);

							for (int i = 0; i < jsonArray.length(); i++) {
								// 缓存数据库
								ShopBanner shopBanner = objectMapper.readValue(
										jsonArray.getJSONObject(i).toString(),
										ShopBanner.class);
								datas.add(shopBanner.getPicUrl());
								idList.add(shopBanner.getProdUuid() + "");
								dbUtils.save(shopBanner);
							}

							ArrayList<ImageView> images = new ArrayList<ImageView>();
							for (int i = 0; i < datas.size(); i++) {
								ImageView imageView = new ImageView(
										getActivity());
								imageView.setScaleType(ScaleType.FIT_XY);
								ImageLoader.getInstance().displayImage(
										datas.get(i), imageView);
								imageView.setScaleType(ScaleType.CENTER_CROP);
								images.add(imageView);

								View dot = LayoutInflater.from(getActivity())
										.inflate(R.layout.item_dots, null);
								dots.add(dot.findViewById(R.id.v_dot));
								ll_dots.addView(dot);
							}

							headPage.setAdapter(new ViewpagerDotsAdapter(
									images, idList, getActivity()));// 设置填充ViewPager页面的适配器
							headPage.setOnPageChangeListener(new MyPageChangeListener());

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("jatjatjat", e.toString());
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("liang_error", "head error");
						List<ShopBanner> banners = new ArrayList<ShopBanner>();
						List<ImageView> imageViews = new ArrayList<ImageView>();
						List<String> ids = new ArrayList<String>();
						try {
							// 失败读取数据库缓存
							banners = dbUtils.findAll(ShopBanner.class);
							Log.d("jatjat", banners.toString());
							for (int i = 0; i < banners.size(); i++) {
								ImageView imageView = new ImageView(
										getActivity());
								imageView.setScaleType(ScaleType.FIT_XY);
								// 加载图片
								ImageLoader.getInstance().displayImage(
										banners.get(i).getPicUrl(), imageView);
								imageView.setScaleType(ScaleType.CENTER_CROP);
								imageViews.add(imageView);
								ids.add(banners.get(i).getProdUuid() + "");
								View dot = LayoutInflater.from(getActivity())
										.inflate(R.layout.item_dots, null);
								dots.add(dot.findViewById(R.id.v_dot));
								ll_dots.addView(dot);
							}
							headPage.setAdapter(new ViewpagerDotsAdapter(
									imageViews, ids, getActivity()));// 设置填充ViewPager页面的适配器
							headPage.setOnPageChangeListener(new MyPageChangeListener());
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

		stringRequest.setTag("getHead");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	public class ViewpagerDotsAdapter extends PagerAdapter {

		private List<ImageView> imageViews = new ArrayList<ImageView>();
		private List<String> list = new ArrayList<String>();
		private Context context;

		public ViewpagerDotsAdapter(List<ImageView> imageViews,
				List<String> myList, Context context) {
			// TODO Auto-generated constructor stub
			this.imageViews = imageViews;
			this.list = myList;
			this.context = context;
		}

		@Override
		public int getCount() {
			return imageViews.size();
		}

		/*
		 * 功能：ViewPager中的图片的点击函数
		 */
		@Override
		public Object instantiateItem(View v, final int position) {
			View view = imageViews.get(position);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("prodUuid", list.get(position));
					intent.setClass(getActivity(), GoodsDetailsActivity.class);
					getActivity().startActivity(intent);
				}
			});
			ViewPager viewPager = (ViewPager) v;
			viewPager.addView(view);
			return imageViews.get(position);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;

			dots.get(oldPosition).setBackgroundResource(
					R.drawable.ico_huadongdian2);
			dots.get(position)
					.setBackgroundResource(R.drawable.ico_huadongdian);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("getPopularGoods");
		MyApplication.getRequestQueue().cancelAll("getPromotionProducts");
		MyApplication.getRequestQueue().cancelAll("getNewProducts");
		MyApplication.getRequestQueue().cancelAll("getPopularShops");
		super.onDestroy();
	}

}
