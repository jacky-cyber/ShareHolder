package com.example.shareholders.activity.shop;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_goods_details)
public class GoodsDetailsActivity extends Activity {

	@ViewInject(R.id.title_fs_more)
	private ImageView title_fs_more;
	@ViewInject(R.id.tv_gd_title)
	private TextView tv_gd_title;
	@ViewInject(R.id.ll_gd_bottom)
	private RelativeLayout ll_gd_bottom;
	@ViewInject(R.id.ll_gd)
	private RelativeLayout ll_gd;
	@ViewInject(R.id.vp_gd)
	private ViewPager vp_gd;
	@ViewInject(R.id.tv_gd_image_num)
	private TextView tv_gd_image_num;
	@ViewInject(R.id.tv_gd_image_all)
	private TextView tv_gd_image_all;
	// 收藏商品
	@ViewInject(R.id.ll_collect_goods)
	private LinearLayout ll_collect_goods;

	// 商品名称
	@ViewInject(R.id.tv_gd_goods_name)
	private TextView tv_gd_goods_name;

	// 商品原价格
	@ViewInject(R.id.tv_gd_goods_old_price)
	private TextView tv_gd_goods_old_price;

	// 公司名称
	@ViewInject(R.id.tv_gd_goods_company)
	private TextView tv_gd_goods_company;

	// 详情底部公司名称
	@ViewInject(R.id.tv_gd_company_name)
	private TextView tv_gd_company_name;

	// 收藏
	@ViewInject(R.id.tv_gd_goods_favorite)
	private TextView tv_gd_goods_favorite;

	// 好评
	@ViewInject(R.id.tv_gd_goods_comment)
	private TextView tv_gd_goods_comment;

	// 好评率
	@ViewInject(R.id.tv_gd_applause_rate)
	private TextView tv_gd_applause_rate;

	// 评论人数
	@ViewInject(R.id.tv_gd_comment_num)
	private TextView tv_gd_comment_num;

	// 店铺
	// 商品总数
	@ViewInject(R.id.tv_gd_goods_num)
	private TextView tv_gd_goods_num;
	// 关注数量
	@ViewInject(R.id.tv_gd_concern_num)
	private TextView tv_gd_concern_num;
	// 新品数量
	@ViewInject(R.id.tv_gd_new_goods_num)
	private TextView tv_gd_new_goods_num;
	// 公司详情
	@ViewInject(R.id.tv_gd_company_introduce)
	private TextView tv_gd_company_introduce;
	// 公司logo
	@ViewInject(R.id.iv_gd_company_logo)
	private ImageView iv_gd_company_logo;

	// 收藏商品
	@ViewInject(R.id.tv_gd_bottom_collect)
	private TextView tv_gd_bottom_collect;

	// popupwindow背后阴影
	@ViewInject(R.id.background)
	private RelativeLayout background;
	// 分享的弹框
	private ShareUtils popupWindow;
	// 整个父容器的布局
	@ViewInject(R.id.ll_gd)
	private RelativeLayout rl_parent;
	
	private ArrayList<ImageView> imageViews;

	private String prodUuid = null;

	private Boolean isCollected = false;
	private String prodId;
	
	private String share_content="";

	// 商铺id
	private int shopId;

	private String picUrl;

	// 第一张商品相片
	private String iv_productUrl = null;
	// 商品名称
	private String tv_productName = null;
	// 商品价格
	private String tv_price = null;

	// baidu locaton
	private LocationClient locationClient;
	private BDLocationListener bdLocationListener;

	// 正在加载的旋转框
	private LoadingDialog loadingDialog;
	// 状态提示框
	private InternetDialog internetDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		prodUuid = bundle.getString("prodUuid");
		Log.d("dj_prodUuid", prodUuid);

		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();

		internetDialog = new InternetDialog(this);

		initView();

		getProductDetail();

	}

	/**
	 * 获取商品详情
	 */
	private void getProductDetail() {
		String url = AppConfig.URL_SHOP + "product/findProductDetail";
		Log.d("dj_product_url", url);
		JSONObject params = new JSONObject();
		try {
			params.put("prodUuid", prodUuid);
			params.put("custUuid", RsSharedUtil.getString(
					GoodsDetailsActivity.this, AppConfig.UUID));
			Log.d("dj_prodUuid", prodUuid);
			Log.d("dj_uuid", RsSharedUtil.getString(GoodsDetailsActivity.this,
					AppConfig.UUID));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("liang_response_detail", response);
						try {
							JSONObject jsonObject = new JSONObject(response);

							// 商品名称
							tv_productName = jsonObject.getString("prodName");
							tv_gd_goods_name.setText(tv_productName);
							// 商品价格
							tv_price = "￥" + jsonObject.getString("prodPrice");
							tv_gd_goods_old_price.setText(tv_price);

							//分享内容
							share_content=tv_productName+":\n"+tv_price;
							
							prodId = jsonObject.getString("prodId");

							// 收藏数
							tv_gd_goods_favorite.setText("收藏"
									+ jsonObject.getString("prodStore"));

							Log.d("jatjatjat", jsonObject.getString("prodUuid"));

							// 判断是否已经收藏
							if (jsonObject.getInt("IsFollowed") == 200) {
								isCollected = true;
							} else {
								isCollected = false;
							}

							// tv_gd_company_name.setText(jsonObject
							// .getString("prodName"));

							// 评论人数
							tv_gd_comment_num.setText(jsonObject
									.getString("prodCommentNum") + "人评论");

							// 好评率--3星及以上等级的评论占整个评论的比率
							float goodTotalscore = Float.parseFloat(jsonObject
									.getString("prodTotalscore")) * 100;
							float goodRaw = (float) (Math
									.round(goodTotalscore * 10)) / 10;
							tv_gd_applause_rate.setText("好评率" + goodRaw + "%");

							if (isCollected) {
								tv_gd_bottom_collect.setText("已收藏");
								tv_gd_bottom_collect
										.setCompoundDrawablesWithIntrinsicBounds(
												R.drawable.btn_shoucang_selected,
												0, 0, 0);
							} else {
								tv_gd_bottom_collect.setText("收藏");
								tv_gd_bottom_collect
										.setCompoundDrawablesWithIntrinsicBounds(
												R.drawable.btn_shoucang, 0, 0,
												0);
							}
							imageViews = new ArrayList<ImageView>();
							JSONArray jsonArray1 = jsonObject
									.getJSONArray("picarray");
							for (int i = 0; i < jsonArray1.length(); i++) {
								ImageView imageView = new ImageView(
										GoodsDetailsActivity.this);
								imageView.setScaleType(ScaleType.FIT_XY);
								// Log.d("jatjat",
								// jsonArray1.getJSONObject(i).get("picUrl")+"");
								ImageLoader.getInstance().displayImage(
										jsonArray1.getJSONObject(i).get(
												"picUrl")
												+ "", imageView);
								imageViews.add(imageView);
							}
							// 把第一张照片传过去评论那边
							iv_productUrl = jsonArray1.getJSONObject(0).get(
									"picUrl")
									+ "";

							tv_gd_image_all.setText(imageViews.size() + "");
							vp_gd.setAdapter(new CycleAdapter(imageViews));
							vp_gd.setOnPageChangeListener(new MyPagerChangeListener());
							// 商铺id
							shopId = jsonObject.getInt("shopId");
							// 获取商店详情
							getShopDetails();
							// 获取顶部图片
							getTopPic();
							// 加载框消失
							loadingDialog.dismissDialog();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("liang_error2", error.toString());
					}
				});

		stringRequest.setTag("getProductDetail");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	// 根据shopId获取店铺详情
	private void getShopDetails() {
		// TODO Auto-generated method stub
		String url = AppConfig.URL_SHOP + "shop/getShopInfo";
		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(response);
							tv_gd_company_name.setText(jsonObject
									.getString("shopName"));
							tv_gd_goods_company.setText(jsonObject
									.getString("shopName"));
							tv_gd_goods_num.setText("推出产品"
									+ jsonObject.getInt("shopProductNum"));
							tv_gd_concern_num.setText("关注人数"
									+ jsonObject.getInt("shopFollowers"));
							tv_gd_new_goods_num.setText("新品"
									+ jsonObject.getInt("shopNewProductNum"));
							tv_gd_company_introduce.setText(jsonObject
									.getString("shopInfo"));
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

		stringRequest.setTag("getShopDetails");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 收藏商品
	 */
	private void collectGoods() {
		String url = AppConfig.URL_SHOP + "collect/followProduct";

		JSONObject params = new JSONObject();
		JSONObject product = new JSONObject();
		try {
			params.put("custUuid", RsSharedUtil.getString(
					GoodsDetailsActivity.this, AppConfig.UUID));
			// product.put("prodId", prodId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("prodId", prodId);
			params.put("product", jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("dj_collect_response", "success");
						// popupTips(GoodsDetailsActivity.this, "已收藏");
						internetDialog.showInternetDialog("已收藏", true);
						isCollected = true;
						tv_gd_bottom_collect.setText("已收藏");
						tv_gd_bottom_collect
								.setCompoundDrawablesWithIntrinsicBounds(
										R.drawable.btn_shoucang_selected, 0, 0,
										0);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("dj_collect_response", "faile");
						tv_gd_bottom_collect
								.setCompoundDrawablesWithIntrinsicBounds(
										R.drawable.btn_shoucang, 0, 0, 0);
						tv_gd_bottom_collect.setText("收藏");
						// popupTips(GoodsDetailsActivity.this, "收藏失败");
						internetDialog.showInternetDialog("收藏失败", false);
					}
				});

		stringRequest.setTag("collectGoods");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	/**
	 * 取消收藏
	 * 
	 * @param symbol
	 */
	private void cancelCollectGood() {
		String url = AppConfig.URL_SHOP + "collect/unfollowProduct";

		Log.d("dj_url", "url:" + url);

		JSONObject params = new JSONObject();
		try {
			params.put("prodId", prodId);
			params.put("custUuid", RsSharedUtil.getString(
					GoodsDetailsActivity.this, AppConfig.UUID));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("dj_cancel", response);
							Log.d("dj_cancelCollect_response", "success");
							// popupTips(GoodsDetailsActivity.this, "已取消");
							internetDialog.showInternetDialog("已取消", true);
							isCollected = false;
							tv_gd_bottom_collect.setText("收藏");
							tv_gd_bottom_collect
									.setCompoundDrawablesWithIntrinsicBounds(
											R.drawable.btn_shoucang, 0, 0, 0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("dj_cancel1", response);
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							Log.d("dj_cancelCollect_response", "fail");
							tv_gd_bottom_collect
									.setCompoundDrawablesWithIntrinsicBounds(
											R.drawable.btn_shoucang_selected,
											0, 0, 0);
							tv_gd_bottom_collect.setText("已收藏");
							// popupTips(GoodsDetailsActivity.this, "取消失败");
							internetDialog.showInternetDialog("取消失败", false);
						} catch (Exception e) {

							// ToastUtils.showToast(
							// getApplicationContext(),getResources().getString(
							// R.string.unknown_error));
						}
					}

				});
		stringRequest.setTag("cancelCollectGood");
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
								}
							}
							ImageLoader.getInstance().displayImage(picUrl,
									iv_gd_company_logo);
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
		stringRequest.setTag("getTopPic");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("getProductDetail");
		MyApplication.getRequestQueue().cancelAll("collectGoods");
		MyApplication.getRequestQueue().cancelAll("cancelCollectGood");
		MyApplication.getRequestQueue().cancelAll("getShopDetails");
		super.onDestroy();
	}

	private void initView() {

		prodUuid = getIntent().getExtras().getString("prodUuid");
	}

	// 初始化百度定位
	private void initLocation() {
		locationClient = new LocationClient(getApplicationContext());
		bdLocationListener = new MyLocationListener();
		locationClient.registerLocationListener(bdLocationListener);
		LocationClientOption option = new LocationClientOption();

		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要

		locationClient.setLocOption(option);
		locationClient.start();
	}

	@OnClick({ R.id.title_fs_note, R.id.title_fs_more,
			R.id.ll_gd_image_and_text_details, R.id.ll_gd_goods_comment,
			R.id.tv_gd_bottom_buy_now, R.id.tv_gd_company_name,
			R.id.ll_gd_company, R.id.ll_collect_goods ,R.id.rl_return})
	private void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.title_fs_note:
			finish();
			break;
		// 更多
		case R.id.title_fs_more:
			initMenu(this, title_fs_more);
			break;
		// 图文详情
		case R.id.ll_gd_image_and_text_details:
			startActivity(new Intent(this, ImageAndTextDetailsActivity.class)
					.putExtra("prodUuid", prodUuid));
			break;
		// 商品评价
		case R.id.ll_gd_goods_comment:
			//登录才能进去看评论列表
			if (!RsSharedUtil.getString(GoodsDetailsActivity.this, AppConfig.ACCESS_TOKEN).equals("")) {
				Bundle bundle = new Bundle();
				bundle.putString("prodUuid", prodUuid);
				// 把商品名称，价格和第一张图片传过去
				bundle.putString("iv_productUrl", iv_productUrl);
				bundle.putString("tv_productName", tv_productName);
				bundle.putString("tv_price", tv_price);
				Log.d("dj_pu_bundle", iv_productUrl + tv_productName + tv_price);
				Intent intent = new Intent(this, GoodsCommentActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}else {//不登录则提示需要登录
				InternetDialog internetDialog = new InternetDialog(GoodsDetailsActivity.this);
				internetDialog.showInternetDialog("请先登录", false);
			}
			
			break;
		// 选择分类
		// case R.id.ll_gd_choose_goods:
		// popupChooseGoods(GoodsDetailsActivity.this);
		// break;
		// case R.id.rl_gd_choose_address:
		// popupChooseDistirct(GoodsDetailsActivity.this);
		// break;
		case R.id.tv_gd_bottom_buy_now:
			startActivity(new Intent(this, FillOrderActivity.class));
			break;
		// case R.id.tv_gd_bottom_service:
		// startActivity(new Intent(this, ChatActivity.class));
		// break;
		// case R.id.tv_gd_company_name:
		// Intent intent2 = new Intent(GoodsDetailsActivity.this,
		// CompanyDetailActivity.class);
		// intent2.putExtra("shopId", shopId);
		// // intent2.putExtra("picUrl", value)
		// startActivity(intent2);
		// break;
		case R.id.ll_collect_goods:
			//登录才能收藏
			if (!RsSharedUtil.getString(GoodsDetailsActivity.this, AppConfig.ACCESS_TOKEN).equals("")) {
				
				if (isCollected) { // 开始为“已收藏”，点击之后为“收藏”
					Log.d("dj_isCollected", isCollected + "");
					tv_gd_bottom_collect.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.btn_shoucang, 0, 0, 0);
					tv_gd_bottom_collect.setText("收藏");
					cancelCollectGood();
				} else { // 开始为“收藏”，点击之后为“已收藏”
					Log.d("dj_isCollected", isCollected + "");
					tv_gd_bottom_collect.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.btn_shoucang_selected, 0, 0, 0);
					tv_gd_bottom_collect.setText("已收藏");
					collectGoods();
				}
				
			}else {//不登录则提示需要登录
				InternetDialog internetDialog = new InternetDialog(GoodsDetailsActivity.this);
				internetDialog.showInternetDialog("请先登录", false);
			}
			break;

		case R.id.ll_gd_company:
			Intent intent2 = new Intent(GoodsDetailsActivity.this,
					CompanyDetailActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putString("shopId", shopId + "");
			intent2.putExtras(bundle2);
			startActivity(intent2);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(Context context, View viewGroup) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.item_goods_details_popup, null);
		RelativeLayout rl_gd_collet = (RelativeLayout) contentView
				.findViewById(R.id.rl_gd_collet);
		RelativeLayout rl_gd_home = (RelativeLayout) contentView
				.findViewById(R.id.rl_gd_home);
		RelativeLayout rl_gd_share = (RelativeLayout) contentView
				.findViewById(R.id.rl_gd_share);
		// 搜索
		rl_gd_collet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(GoodsDetailsActivity.this,
						SearchGoodsActivity.class));
			}
		});
		// 首页
		rl_gd_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		// 分享
		rl_gd_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// new SharePopupWindow(GoodsDetailsActivity.this,);
				showShare(share_content);
			}
		});

		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		// 获取xoff
		int xpos = manager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;
		// popwindow位置
		popupWindow.showAsDropDown(viewGroup, xpos, 0);
		// background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				// background.setAlpha(0.0f);
			}
		});
	}

	/**
	 * 弹出选择分类和数量
	 * 
	 * @param context
	 * @return
	 */
	public void popupChooseGoods(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.item_gd_popup_shopping_cart, null);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_gd_popup_ok);
		ImageView iv_gd_popup_cancel = (ImageView) contentView
				.findViewById(R.id.iv_gd_popup_cancel);

		final AlertDialog dialog = new AlertDialog.Builder(
				GoodsDetailsActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});

		iv_gd_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 弹出选择送货地址
	 * 
	 * @param context
	 * @return
	 */
	public void popupChooseDistirct(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_select_address, null);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_ok);

		final AlertDialog dialog = new AlertDialog.Builder(
				GoodsDetailsActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	// 分享的方法
	private void showShare(String content) {
		background.setAlpha(0.7f);
		popupWindow = new ShareUtils(GoodsDetailsActivity.this, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				background.setAlpha(0.0f);

			}
		});
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
				GoodsDetailsActivity.this).create();
		dialog.show();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = (dm.widthPixels / 5) * 3;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setContentView(contentView);

	}

	public class CycleAdapter extends PagerAdapter {

		ArrayList<ImageView> imageViews;

		public CycleAdapter(ArrayList<ImageView> imageViews) {
			this.imageViews = imageViews;
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

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());

			// 获取所在地区
			String district = location.getDistrict();
			sb.append("\ndistrict : ");
			sb.append(district);

			// 获取所在城市
			String city = location.getCity();
			sb.append("\ncity : ");
			sb.append(city);

			// 获取所在省份
			String province = location.getProvince();
			sb.append("\nprovince : ");
			sb.append(province);

			try {
				if (district.equals("null") || city.equals("null")
						|| province.equals("null")) {
					Toast.makeText(getApplicationContext(), "定位失败",
							Toast.LENGTH_SHORT).show();
				} else {
					// tv_gd_region.setText(district);
					// tv_gd_region.setText(city);
					// tv_gd_region.setText(province);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "定位失败",
						Toast.LENGTH_SHORT).show();
			}

			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			Log.i("BaiduLocationApiDem", sb.toString());
		}
	}

}
