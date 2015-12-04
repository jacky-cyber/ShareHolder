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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_shopping_cart)
public class ShoppingCartActivity extends Activity {

	// 空购物车
	@ViewInject(R.id.ll_empty_shopping_cart)
	private LinearLayout ll_empty_shopping_cart;

	// 列表
	@ViewInject(R.id.lv_sc)
	private ListView lv_sc;
	// 总价
	@ViewInject(R.id.tv_sc_price)
	private TextView tv_sc_price;
	// 未编辑状态
	@ViewInject(R.id.rl_sc_total)
	private RelativeLayout rl_sc_total;
	// 编辑状态
	@ViewInject(R.id.rl_sc_collect_and_delete)
	private RelativeLayout rl_sc_collect_and_delete;

	private ArrayList<HashMap<String, String>> list;
	private ItemAdapter itemAdapter;

	// 商品总价
	private int totalPrice = 0;
	// 是否编辑
	private Boolean isEdit;

	private ArrayList<Boolean> selectedList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Init();
	}

	@OnClick({ R.id.title_fs_note, R.id.tv_sc_edit,R.id.rl_return })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		case R.id.tv_sc_edit:
			isEdit = !isEdit;
			if (isEdit) {
				rl_sc_total.setVisibility(View.GONE);
				rl_sc_collect_and_delete.setVisibility(View.VISIBLE);
			} else {
				rl_sc_total.setVisibility(View.VISIBLE);
				rl_sc_collect_and_delete.setVisibility(View.GONE);
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
	protected void onStop() {
		MyApplication.getRequestQueue().cancelAll("getShoppingCart");
		super.onStop();
	}

	private void Init() {
		selectedList = new ArrayList<Boolean>();
		isEdit = false;
		ll_empty_shopping_cart.setVisibility(View.VISIBLE);
		getShoppingCart();
	}

	private class ItemAdapter extends BaseAdapter {

		Context context;
		ArrayList<HashMap<String, String>> list;

		public ItemAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
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
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_shopping_cart, parent, false);
			}

			// 公司
			TextView tv_company = (TextView) AbViewHolder.get(view,
					R.id.tv_company);
			// 商品图片
			ImageView iv_good = (ImageView) AbViewHolder
					.get(view, R.id.iv_good);
			// 商品
			TextView tv_goods = (TextView) AbViewHolder
					.get(view, R.id.tv_goods);
			LinearLayout ll_goods = (LinearLayout) AbViewHolder.get(view,
					R.id.ll_goods);
			// 价格
			final TextView tv_price = (TextView) AbViewHolder.get(view,
					R.id.tv_price);
			// 数量
			final TextView tv_num = (TextView) AbViewHolder.get(view,
					R.id.tv_num);
			// 加数量
			final ImageView iv_add = (ImageView) AbViewHolder.get(view,
					R.id.iv_add);
			// 减数量
			final ImageView iv_sub = (ImageView) AbViewHolder.get(view,
					R.id.iv_sub);
			// 选择
			final ImageView iv_select_goods = (ImageView) AbViewHolder.get(
					view, R.id.iv_select_goods);

			try {
				JSONObject productObject = new JSONObject(list.get(position)
						.get("product"));
				tv_company.setText(productObject.getString("prodName"));
				tv_goods.setText(productObject.getString("prodDesc"));
				tv_price.setText(productObject.getString("prodPrice"));
				JSONArray picArray = new JSONArray(
						productObject.getString("picarray"));
				ImageLoader.getInstance().displayImage(
						picArray.getJSONObject(0).getString("picUrl"), iv_good);

				tv_num.setText(list.get(position).get("cartitemQuantity"));

				tv_company.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(getApplicationContext(),
								CompanyDetailActivity.class));

					}
				});
				ll_goods.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(getApplicationContext(),
								GoodsDetailsActivity.class));

					}
				});
				iv_add.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						int num = Integer.parseInt(tv_num.getText().toString());
						tv_num.setText("" + (num + 1));
						iv_sub.setClickable(true);
					}
				});
				iv_sub.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						int num = Integer.parseInt(tv_num.getText().toString());
						if (num == 1) {
							iv_sub.setClickable(false);
						} else {
							tv_num.setText("" + (num - 1));
						}

					}
				});
				iv_select_goods.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (selectedList.get(position)) { // 取消选择
							selectedList.set(position, false);
							iv_select_goods
									.setImageResource(R.drawable.btn_fs_unselected);
							totalPrice = totalPrice
									- Integer.parseInt(tv_price.getText()
											.toString());
							tv_sc_price.setText("" + totalPrice);
						} else { // 选择
							selectedList.set(position, true);
							iv_select_goods
									.setImageResource(R.drawable.btn_fs_selected);
							totalPrice = totalPrice
									+ Integer.parseInt(tv_price.getText()
											.toString());
							tv_sc_price.setText("" + totalPrice);
						}
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return view;
		}

	}

	/**
	 * 获取购物车
	 * 
	 * @param symbol
	 */
	private void getShoppingCart() {
		String url = AppConfig.URL_SHOP + "cart/lookMyCart";

		Log.d("url", "url:" + url);

		JSONObject params = new JSONObject();
		try {
			params.put("custUuid", AppConfig.custUuid);
			params.put("pageIndex", "1");
			params.put("pageSize", "5");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("getShoppingCart", "getShoppingCart:"
									+ response.toString());
							// 如果没有数据
							if (response.toString().equals("")
									|| response.toString().equals("[0]")) {
								// TODO
							} else {
								try {
									JSONArray jsonArray = new JSONArray(
											response);
									ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
									Iterator<String> iterator = null;

									for (int i = 0; i < jsonArray.length(); i++) {
										HashMap<String, String> data = new HashMap<String, String>();
										iterator = jsonArray.getJSONObject(i)
												.keys();
										while (iterator.hasNext()) {
											String key = iterator.next();
											data.put(key, jsonArray
													.getJSONObject(i).get(key)
													.toString());
										}

										datas.add(data);
									}
									ll_empty_shopping_cart
											.setVisibility(View.GONE);
									list = datas;
									itemAdapter = new ItemAdapter(
											getApplicationContext(), list);
									lv_sc.setAdapter(itemAdapter);
									// 初始化列表，全不选
									for (int i = 0; i < list.size(); i++) {
										selectedList.add(false);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
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
		stringRequest.setTag("getShoppingCart");
		MyApplication.getRequestQueue().add(stringRequest);
	}

}
