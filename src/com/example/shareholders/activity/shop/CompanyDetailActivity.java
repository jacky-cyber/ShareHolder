package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.stock.MyStockDetailsParamActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_company_detail)
public class CompanyDetailActivity extends Activity implements
		OnItemClickListener {
	private static final int String = 0;

	// 返回
	@ViewInject(R.id.title_note)
	private ImageView title_note;

	// // 最新推出的列表
	// @ViewInject(R.id.lv_newest_push)
	// private MyListView lv_newest_push;

	// // 顶部图片
	// @ViewInject(R.id.iv_top_photo)
	// private ImageView iv_top_photo;

	// 公司头像
	@ViewInject(R.id.iv_company_icon)
	private ImageView iv_company_icon;

	// 公司名称
	@ViewInject(R.id.tv_company_name)
	private TextView tv_company_name;

	// 关注人数
	@ViewInject(R.id.tv_follow_num)
	private TextView tv_follow_num;

	// 商品总数
	@ViewInject(R.id.tv_good_num)
	private TextView tv_good_num;

	// 新品总数
	@ViewInject(R.id.tv_new_goods_nums)
	private TextView tv_new_goods_nums;

	// 关注布局
	@ViewInject(R.id.rl_guanzhu)
	private LinearLayout rl_guanzhu;

	// 关注图标
	@ViewInject(R.id.iv_guanzhu_img)
	private ImageView iv_guanzhu_img;

	// 关注
	@ViewInject(R.id.tv_guanzhu)
	private TextView tv_guanzhu;

	// 头部图片viewpager
	@ViewInject(R.id.vp_gd)
	private ViewPager vp_gd;

	// 目前图片的定位
	@ViewInject(R.id.tv_gd_image_num)
	private TextView tv_gd_image_num;

	// 头部图像总数量
	@ViewInject(R.id.tv_gd_image_all)
	private TextView tv_gd_image_all;

	// 商店id
	private String shopId = null;

	// 是否关注
	private boolean isFollow = false;
	// 公司logo
	private String picUrl;
	// 公司uuid
	private String shopUuid = "0";
	BitmapUtils bitmapUtils = null;

	// 股票代码
	private String securitySymbol;
	// 股票简称
	private String securityName;
	// 股票类型
	private String securityType;

	private List<String> listPic = new ArrayList<String>();
	// 头部图像
	private ArrayList<ImageView> imageViews;

	// 正在加载的旋转框
	private LoadingDialog loadingDialog;
	// 状态提示框
	private InternetDialog internetDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();

		internetDialog = new InternetDialog(this);

		shopId = getIntent().getExtras().getString("shopId");
		bitmapUtils = BitmapUtilFactory.getInstance();
		
		// 股票代码
		securitySymbol = null;
		// 股票简称
		securityName = null;
		// 股票类型
		securityType = null;
		
		// Log.d("liang_shopId", shopId);

		// /**
		// * 模拟数据
		// */
		// lv_newest_push.setAdapter(new NewPushAdapter(this,
		// new ArrayList<HashMap<String, String>>()));
		// lv_newest_push.setOnItemClickListener(this);

		getTopPic();
		getFollow();
		// getZiXun();
		getCompanyDetail();

	}

	/**
	 * 最新咨询接口
	 */
	private void getZiXun() {
		String url = AppConfig.URL_SHOP + "news/findNewsBypage";
		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONArray jsonArray = new JSONArray(response);
							ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> data = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								Iterator<String> iterator = jsonArray
										.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.getString(key));

								}
								datas.add(data);
							}

							// lv_newest_push.setAdapter(new NewPushAdapter(
							// CompanyDetailActivity.this, datas));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("liang_error1", "最新咨询解析错误：" + e.toString());
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
	}

	// 获取商铺关注状态
	private void getFollow() {
		String url = AppConfig.URL_SHOP + "shop/getFollowed";
		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
			params.put("custUuid", RsSharedUtil.getString(
					CompanyDetailActivity.this, AppConfig.UUID));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(java.lang.String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.get("IsFollowed").equals("true")) {
								isFollow = true;
								tv_guanzhu.setText("已关注");
								iv_guanzhu_img
										.setImageResource(R.drawable.guanzhu_selected);
							} else {
								isFollow = false;
								tv_guanzhu.setText("关注");
								iv_guanzhu_img
										.setImageResource(R.drawable.guanzhu_normal);
							}
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
		stringRequest.setTag("getFollowed");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 获取顶部和公司头像
	 */
	private void getTopPic() {
		String url = AppConfig.URL_SHOP + "shop/getShopPic";
		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		imageViews = new ArrayList<ImageView>();
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(java.lang.String response) {
						// TODO Auto-generated method stub
						try {
							JSONArray jsonArray = new JSONArray(response);
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);
								if (jsonObject.get("picType")
										.equals("shopLogo")) {
									picUrl = jsonObject.getString("picUrl");
								} else {
									listPic.add(jsonObject.getString("picUrl"));
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 公司图片
						ImageLoader.getInstance().displayImage(picUrl,
								iv_company_icon);
						tv_gd_image_all.setText(listPic.size() + "");
						for (int i = 0; i < listPic.size(); i++) {
							ImageView imageView = new ImageView(
									CompanyDetailActivity.this);
							imageView.setScaleType(ScaleType.FIT_XY);
							ImageLoader.getInstance().displayImage(
									listPic.get(i), imageView);
							imageViews.add(imageView);
						}

						vp_gd.setAdapter(new CycleAdapter(imageViews));
						vp_gd.setOnPageChangeListener(new MyPagerChangeListener());

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
		stringRequest.setTag("getTopPic");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 店铺详情接口
	 */
	private void getCompanyDetail() {
		String url = AppConfig.URL_SHOP + "shop/getShopInfo";

		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// Log.d("liang_company_detail", response);

						try {
							JSONObject jsonObject = new JSONObject(response);

							Log.d("dj_response", response);
							
							
							// jsonObject.getString("picUrl")

							// 公司名称
							tv_company_name.setText(jsonObject
									.getString("shopName"));
							// 关注数
							tv_follow_num.setText(jsonObject
									.getInt("shopFollowers")+"");
							// 商品总数
							tv_good_num.setText(jsonObject
									.getInt("shopProductNum")+"");
							//商店uuid
							shopUuid = jsonObject.getString("shopUuid");
							// 新品总数
							tv_new_goods_nums.setText(jsonObject
									.getInt("shopNewProductNum")+"");
							securitySymbol = jsonObject
									.getString("symbol");
							securityName = jsonObject
									.getString("shortname");
							securityType = jsonObject
									.getString("securityType");

							Log.d("dj_symbol111",securitySymbol);
							Log.d("dj_shortname111",securityName);
							Log.d("dj_securityType111",securityType);
							
							loadingDialog.dismissDialog();

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("dj_error", "店铺详情解析错误:" + e.toString());
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("dj_error", error.toString());
					}
				});

		stringRequest.setTag("getCompanyDetail");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	// 关注店铺
	private void followCertainShop() {
		String url = AppConfig.URL_SHOP + "shop/followCertainShop";
		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
			Log.d("dj_shopId", shopId);
			params.put("custUuid", RsSharedUtil.getString(
					CompanyDetailActivity.this, AppConfig.UUID));
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(java.lang.String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.get("status").equals(
									"Follow Successfully")) {
								// popupTips(getApplicationContext(), "已关注");
								internetDialog.showInternetDialog("已关注", true);
								isFollow = true;
								tv_guanzhu.setText("已关注");
								iv_guanzhu_img
										.setImageResource(R.drawable.guanzhu_selected);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// popupTips(getApplicationContext(), "关注失败");
						internetDialog.showInternetDialog("关注失败", false);
						tv_guanzhu.setText("关注");
						iv_guanzhu_img
								.setImageResource(R.drawable.guanzhu_normal);
					}
				});

		stringRequest.setTag("getFollow");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 取消关注店铺
	private void cancelFollowCertainShop() {
		String url = AppConfig.URL_SHOP + "shop/cancelFollowCertainShop";
		JSONObject params = new JSONObject();
		try {
			params.put("custUuid", RsSharedUtil.getString(
					CompanyDetailActivity.this, AppConfig.UUID));
			params.put("shopId", shopId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(java.lang.String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.get("status").equals(
									"unfollow Successfully")) {
								isFollow = false;
								tv_guanzhu.setText("关注");
								iv_guanzhu_img
										.setImageResource(R.drawable.guanzhu_normal);
								// popupTips(getApplicationContext(), "已取消");
								internetDialog.showInternetDialog("已取消", true);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// popupTips(getApplicationContext(), "取消关注失败");
						internetDialog.showInternetDialog("取消失败", false);
						tv_guanzhu.setText("已关注");
						iv_guanzhu_img
								.setImageResource(R.drawable.guanzhu_selected);
					}
				});

		stringRequest.setTag("cancelFollow");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("getCompanyDetail");
		MyApplication.getRequestQueue().cancelAll("getNewZiXun");
		MyApplication.getRequestQueue().cancelAll("getFollowed");
		MyApplication.getRequestQueue().cancelAll("getFollow");
		MyApplication.getRequestQueue().cancelAll("cancelFollow");
		MyApplication.getRequestQueue().cancelAll("getTopPic");
		super.onDestroy();
	}

	/**
	 * 跳到个股详情
	 * 
	 * @param v
	 */
	private void toCompanyStockDetails() {
		Log.d("dj_toCompanyStockDetails()", "do");
//		Log.d("dj_symbol",securitySymbol);
//		Log.d("dj_shortname",securityName);
//		Log.d("dj_securityType",securityType);
		
		ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("symbol", securitySymbol);
		map.put("shortname", securityName);
		map.put("securityType", securityType);
		stocks.add(map);
		Intent intent = new Intent(getApplicationContext(),
				MyStockDetailsActivity.class);
		intent.putExtra("stocks", stocks);
		intent.putExtra("position", 0);
		startActivity(intent);
	}

	/**
	 * 跳到公司简介
	 * 
	 * @param v
	 */
	private void toCompanyIntrouduction(){
		Intent intent = new Intent(CompanyDetailActivity.this,
				MyStockDetailsParamActivity.class);
		intent.putExtra("name", securityName);
		intent.putExtra("symbol", securitySymbol);
		intent.putExtra("ifFromCompanyDetails", "true");
		startActivity(intent);
	}
	
	
	@OnClick({ R.id.title_note, R.id.title_research, R.id.ll_company_introduce,
			R.id.ll_company_gupianxiangqing, R.id.rl_all_goods,
			R.id.rl_new_goods, R.id.rl_guanzhu,R.id.rl_return })
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.title_note:
			finish();
			break;

		// 搜索
		case R.id.title_research:
			Intent intent = new Intent(CompanyDetailActivity.this,
					CompanyGoodsListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("shopUuid", shopUuid);
			bundle.putString("shopId", shopId);
			bundle.putString("flag", "all_goods");
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		// 公司简介
		case R.id.ll_company_introduce:
			toCompanyIntrouduction();
			break;
		// 股票详情
		case R.id.ll_company_gupianxiangqing:
			Log.d("dj_xiangqing", "click");
			toCompanyStockDetails();
			break;
		//新品
		case R.id.rl_new_goods:
			Intent intent1 = new Intent(CompanyDetailActivity.this,
					CompanyGoodsListActivity.class);
			Bundle bundle1 = new Bundle();
			bundle1.putString("shopUuid", shopUuid);
			bundle1.putString("shopId", shopId);
			bundle1.putString("flag", "new_goods");
			intent1.putExtras(bundle1);
			startActivity(intent1);
			break;
		//全部商品
		case R.id.rl_all_goods:
			Intent intent2 = new Intent(CompanyDetailActivity.this,
					CompanyGoodsListActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putString("shopUuid", shopUuid);
			bundle2.putString("shopId", shopId);
			bundle2.putString("flag", "all_goods");
			intent2.putExtras(bundle2);
			startActivity(intent2);
			break;

		case R.id.rl_guanzhu:
			//登录才能关注
			if (!RsSharedUtil.getString(CompanyDetailActivity.this, AppConfig.ACCESS_TOKEN).equals("")) {
				
				if (isFollow) {
					tv_guanzhu.setText("关注");
					iv_guanzhu_img.setImageResource(R.drawable.guanzhu_normal);
					cancelFollowCertainShop();
				} else {
					tv_guanzhu.setText("已关注");
					iv_guanzhu_img.setImageResource(R.drawable.guanzhu_selected);
					followCertainShop();
				}
			}else {//不登录则提示需要登录
				InternetDialog internetDialog = new InternetDialog(CompanyDetailActivity.this);
				internetDialog.showInternetDialog("请先登录", false);
			}
			break;
			
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		startActivity(new Intent(getApplicationContext(),
				GoodsDetailsActivity.class));
	}

	class NewPushAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		private int item_type;// item的类型：有图片或唔图片
		private final static int PHOTO_TYPE = 0; // item的类型：包含图片
		private final static int NO_PHOTO_TYPE = 1; // item的类型：没有图片

		public NewPushAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
			item_type = NO_PHOTO_TYPE;// 默认为无图片
		}

		@Override
		public int getItemViewType(int position) {
			/**
			 * 模拟数据
			 */
			// if (position == 0) {// 有图片
			return PHOTO_TYPE;
			// } else {// 无图片
			// return NO_PHOTO_TYPE;
			// }
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
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
		public View getView(int position, View converView, ViewGroup parent) {

			PhotoViewHolder photoViewHolder = null;
			NoPhotoViewHolder noPhotoViewHolder = null;
			/**
			 * 先判断item中是否包含图片
			 */
			item_type = getItemViewType(position);

			if (converView == null) {
				/**
				 * 根据item_type判断选择哪个布局
				 */
				if (item_type == PHOTO_TYPE) { // 有图片
					converView = inflater.inflate(
							R.layout.item_company_detail_photo, parent, false);
					photoViewHolder = new PhotoViewHolder();
					// 商品头像
					photoViewHolder.iv_goods = (ImageView) converView
							.findViewById(R.id.iv_goods);

					// 商品名称
					photoViewHolder.tv_name = (TextView) converView
							.findViewById(R.id.tv_name);

					// 商品价格
					photoViewHolder.tv_price = (TextView) converView
							.findViewById(R.id.tv_price);

					// 商品推出日期
					photoViewHolder.tv_date = (TextView) converView
							.findViewById(R.id.tv_date);

					converView.setTag(photoViewHolder);
				} else {// 无图片
					converView = inflater.inflate(
							R.layout.iten_company_detail_no_photo, parent,
							false);
					noPhotoViewHolder = new NoPhotoViewHolder();
					// 信息
					noPhotoViewHolder.tv_message = (TextView) converView
							.findViewById(R.id.tv_message);

					// 日期
					noPhotoViewHolder.tv_date = (TextView) converView
							.findViewById(R.id.tv_date);

					converView.setTag(noPhotoViewHolder);
				}

			} else {
				if (item_type == PHOTO_TYPE) {// 有图片
					photoViewHolder = (PhotoViewHolder) converView.getTag();
				} else { // 无图片
					noPhotoViewHolder = (NoPhotoViewHolder) converView.getTag();
				}
			}

			return converView;
		}

		/**
		 * item有图片时的ViewHolder
		 * 
		 * @author Administrator
		 * 
		 */
		class PhotoViewHolder {
			ImageView iv_goods;// 商品图片
			TextView tv_name; // 商品名称
			TextView tv_price;// 商品价格
			TextView tv_date; // 商品推出的日期
		}

		/**
		 * item无图片时的ViewHolder
		 * 
		 * @author Administrator
		 * 
		 */
		class NoPhotoViewHolder {
			TextView tv_message; // 信息
			TextView tv_date; // 发布日期
		}

	}

	public class CycleAdapter extends PagerAdapter {

		ArrayList<ImageView> imageViews;

		public CycleAdapter(ArrayList<ImageView> ImageViews) {
			this.imageViews = ImageViews;
		}

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public boolean isViewFromObject(View v, Object o) {
			return v == o;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(imageViews.get(position));
		}

		@Override
		public Object instantiateItem(View v, final int position) {
			View view = imageViews.get(position);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			ViewPager viewPager = (ViewPager) v;
			viewPager.addView(view);
			return imageViews.get(position);
		}

	}

	public class MyPagerChangeListener implements OnPageChangeListener {

		// 实现ViewPager.OnPageChangeListener接口
		@Override
		public void onPageSelected(int position) {
			Log.d("pageradapter", "onPageSelected:" + position);
			tv_gd_image_num.setText("" + (position + 1));
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// 什么都不干
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// 什么都不干
		}

	}

	/**
	 * 弹出提示
	 * 
	 * @param context
	 * @param tips
	 * @return
	 */
	public void popupTips(Context context, String tips) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.item_toast_popup, null);

		TextView tv_item = (TextView) contentView.findViewById(R.id.tv_item);
		tv_item.setText(tips);

		final AlertDialog dialog = new AlertDialog.Builder(
				CompanyDetailActivity.this).create();
		dialog.show();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (dm.widthPixels / 5) * 3;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setContentView(contentView);

	}

}
