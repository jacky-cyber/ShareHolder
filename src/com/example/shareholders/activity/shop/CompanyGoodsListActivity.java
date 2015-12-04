package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.shop.SearchGoodsActivity.ResultAdapter;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 店铺商品列表
 * 
 * @author jat
 * 
 */
@ContentView(R.layout.activity_company_goods_list)
public class CompanyGoodsListActivity extends Activity {

	// 搜索输入框
	@ViewInject(R.id.et_search_text)
	private EditText et_search_text;

	// 找不到相关商品的提示
	@ViewInject(R.id.ll_tips)
	private LinearLayout ll_tips;

	// 显示搜索结果的列表
	@ViewInject(R.id.lv_goods)
	private ListView lv_goods;

	// 灰色分割部分
	@ViewInject(R.id.iv_gray)
	private ImageView iv_gray;

	private int pageSize = 10;
	private int pageIndex = 0;
	private String shopUuid;
	private String shopId;
	private String flag;

	ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

	ArrayList<HashMap<String, String>> search_data = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Intent intent = getIntent();
		shopUuid = intent.getExtras().getString("shopUuid");
		shopId = intent.getExtras().getString("shopId");
		flag = intent.getExtras().getString("flag");
		Log.d("dj_shopUuid", shopUuid);
		Log.d("dj_shopId", shopId);
		Log.d("dj_flag", flag);
		// 新品
		if (flag.equals("new_goods")) {
			getShopNewProduct();
		} else { // 全部商品
			findByShop();
		}

		// 设置列表item监听
		lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CompanyGoodsListActivity.this,
						GoodsDetailsActivity.class);
				// 把prodUuid传过去GoodsDetailActivity
				Bundle bundle = new Bundle();
				bundle.putString("prodUuid", datas.get(position)
						.get("prodUuid").toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	// 获取店铺所有商品
	private void findByShop() {
		String url = AppConfig.URL_SHOP + "product/findByshop";
		Log.d("dj_allGoods_url", url);
		JSONObject params = new JSONObject();
		try {
			params.put("shopUuid", shopUuid);
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_all_goods", response);
						try {
							JSONArray jsonArray = new JSONArray(response);
							Iterator<String> iterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								HashMap<String, String> data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();

									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								datas.add(data);
							}
							Log.d("dj_allgoods_datas", datas.toString());
							lv_goods.setAdapter(new GoodsAdapter(
									CompanyGoodsListActivity.this, datas));
						} catch (Exception e) {
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
		stringRequest.setTag("findByShop");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 获取店铺新品
	private void getShopNewProduct() {
		String url = AppConfig.URL_SHOP + "product/getShopNewProduct";
		Log.d("dj_newgoods_url", url);
		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
			params.put("pageIndex", pageIndex);
			params.put("pageSize", pageSize);
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_new_goods", response);
						try {
							JSONArray jsonArray = new JSONArray(response);
							Iterator<String> iterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								HashMap<String, String> data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();

									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								datas.add(data);
							}
							Log.d("dj_newgoods_datas", datas.toString());
							lv_goods.setAdapter(new NewGoodsAdapter(
									CompanyGoodsListActivity.this, datas));
						} catch (Exception e) {
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
		stringRequest.setTag("getShopNewProduct");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("findByShop");
		MyApplication.getRequestQueue().cancelAll("getShopNewProduct");
	}

	@OnClick({ R.id.rl_return, R.id.tv_ac_search, R.id.tv_shopping_again,R.id.rl_return })
	public void onClick(View v) {
		switch (v.getId()) {
		// 再去逛逛
		case R.id.tv_shopping_again:
			finish();
			break;
		// 返回
		case R.id.rl_return:
			finish();
			break;
		// 搜索
		case R.id.tv_ac_search:
			search_data.clear();
			String note = et_search_text.getText().toString().trim();
			for (int i = 0; i < datas.size(); i++) {
				if (datas.get(i).get("prodName").contains(note)) {
					search_data.add(datas.get(i));
				}
			}
			Log.d("dj_search_data", search_data.toString());
			if (et_search_text.getText().toString().equals("")
					|| search_data.toString().equals("[]")) {// 搜索为空或者没数据，则提示没该商品
				iv_gray.setVisibility(View.GONE);
				ll_tips.setVisibility(View.VISIBLE);
				lv_goods.setVisibility(View.GONE);
			} else {
				iv_gray.setVisibility(View.VISIBLE);
				ll_tips.setVisibility(View.GONE);
				lv_goods.setVisibility(View.VISIBLE);

				if (flag.equals("new_goods")) {
					lv_goods.setAdapter(new NewGoodsAdapter(
							CompanyGoodsListActivity.this, search_data));
				} else {
					lv_goods.setAdapter(new GoodsAdapter(
							CompanyGoodsListActivity.this, search_data));
				}
			}

			// 搜索完隐藏键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_search_text.getWindowToken(), 0);
			break;
		default:
			break;
		}
	}

	// 获得全部商品的适配器
	class GoodsAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		public GoodsAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return lists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = inflater
						.inflate(R.layout.item_search_goods_result_layout,
								parent, false);
				// 商品照片
				viewHolder.iv_goods = (ImageView) converView
						.findViewById(R.id.iv_goods);

				// 商品名称
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_name);

				// 商品价格
				viewHolder.tv_price = (TextView) converView
						.findViewById(R.id.tv_price);

				// 商品月销售量
				viewHolder.tv_sell_num = (TextView) converView
						.findViewById(R.id.tv_sell_num);

				// 商品好评数目
				viewHolder.tv_good_reputation_num = (TextView) converView
						.findViewById(R.id.tv_good_reputation_num);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}
			viewHolder.tv_name.setText(lists.get(position).get("prodName"));
			viewHolder.tv_price.setText(lists.get(position).get("prodPrice"));
			viewHolder.tv_good_reputation_num.setText(lists.get(position).get(
					"prodCommentNum"));
			viewHolder.tv_sell_num.setText(lists.get(position).get("prodSell"));
			try {
				JSONArray jsonArray = new JSONArray(lists.get(position).get(
						"picarray"));
				ImageLoader.getInstance().displayImage(
						jsonArray.getJSONObject(0).getString("picUrl"),
						viewHolder.iv_goods);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return converView;
		}

		class ViewHolder {
			ImageView iv_goods; // 商品照片
			TextView tv_name; // 商品名称
			TextView tv_price;// 商品价格
			TextView tv_sell_num;// 商品月销售量
			TextView tv_good_reputation_num;// 商品好评数目
		}

	}

	// 获得最新商品的适配器
	class NewGoodsAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		public NewGoodsAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return lists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = inflater
						.inflate(R.layout.item_search_goods_result_layout,
								parent, false);
				// 商品照片
				viewHolder.iv_goods = (ImageView) converView
						.findViewById(R.id.iv_goods);

				// 商品名称
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_name);

				// 商品价格
				viewHolder.tv_price = (TextView) converView
						.findViewById(R.id.tv_price);

				// 销售情况的布局
				viewHolder.ll_onsale = (LinearLayout) converView
						.findViewById(R.id.ll_onsale);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}
			// 商品名称
			viewHolder.tv_name.setText(lists.get(position).get("prodName"));
			// 商品价格
			viewHolder.tv_price.setText(lists.get(position).get("prodPrice"));
			// 商品图片
			ImageLoader.getInstance().displayImage(
					lists.get(position).get("picUrl"), viewHolder.iv_goods);
			// 把销售情况的布局隐藏
			viewHolder.ll_onsale.setVisibility(View.GONE);
			return converView;
		}

		class ViewHolder {
			ImageView iv_goods; // 商品照片
			TextView tv_name; // 商品名称
			TextView tv_price;// 商品价格
			LinearLayout ll_onsale;// 销售情况的布局
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getRawX() > left && event.getRawX() < right
					&& event.getRawY() > top && event.getRawY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
}
