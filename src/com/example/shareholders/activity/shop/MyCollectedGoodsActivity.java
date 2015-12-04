package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_my_collected_goods)
public class MyCollectedGoodsActivity extends Activity implements
OnItemClickListener {

	@ViewInject(R.id.lv_mcg)
	private ListView lv_mcg;

	private ArrayList<HashMap<String, String>> al_goods;

	private GoodsAdapter goodsAdapter;
	
	//正在加载的提示框
	private LoadingDialog loadingDialog;
	//状态提示框
	private InternetDialog internetDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();
		
		internetDialog = new InternetDialog(this);
		
		initView();
	}

	@Override
	protected void onStop() {
		MyApplication.getRequestQueue().cancelAll("getCollectedGoods");
		MyApplication.getRequestQueue().cancelAll("cancelCollectGood");
		super.onStop();
	}

	@OnClick({ R.id.title_fs_note })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		default:
			break;
		}
	}

	private void initView() {

		lv_mcg.setOnItemClickListener(this);
		getCollectedGoods(0, 10);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(this, GoodsDetailsActivity.class);
		JSONObject jsonObject;
		try {
			//将键值对数据取出转化为json，取值，键值对取值为空异常
			jsonObject = new JSONObject(al_goods.get(position).get("product").toString());

			intent.putExtra("prodUuid", jsonObject.get("prodUuid").toString());
			intent.putExtra("prodId", jsonObject.get("prodId").toString());
			
			startActivity(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Intent intent;

	private class GoodsAdapter extends BaseAdapter {

		Context context;
		ArrayList<HashMap<String, String>> list;

		public GoodsAdapter(Context context,
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
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			if (position<0) {
				return 0;
			}
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_my_collected_goods, parent, false);
			}

			// 商品图片
			ImageView iv_goods = (ImageView) AbViewHolder.get(view,
					R.id.iv_goods);

			// 商品名
			TextView tv_goods_name = (TextView) AbViewHolder.get(view,
					R.id.tv_goods_name);


			// 商品价格
			TextView tv_goods_price = (TextView) AbViewHolder.get(view,
					R.id.tv_goods_price);


			// 月销量
			TextView tv_month_sale = (TextView) AbViewHolder.get(view,
					R.id.tv_month_sale);


			// 好评率
			TextView tv_good_comment = (TextView) AbViewHolder.get(view,
					R.id.tv_good_comment);


			try {
				// 对product进行解析
				JSONObject jsonObject1 = new JSONObject(list.get(position)
						.get("product").toString());
				// 商品图片
				String picUrl = jsonObject1.getString("picUrl");
				ImageLoader.getInstance().displayImage(picUrl, iv_goods);
				// 商品名
				String prodName = jsonObject1.getString("prodName");
				tv_goods_name.setText(prodName);
				// 商品价格
				String prodPrice = jsonObject1.getString("prodPrice");
				tv_goods_price.setText(prodPrice);
				// 月销量
				String prodSell = jsonObject1.getString("prodSell");
				tv_month_sale.setText(prodSell);

				// 好评率--3星及以上等级的评论占整个评论的比率
				float goodTotalscore = Float.parseFloat(jsonObject1
						.getString("prodTotalscore")) * 100;
				float goodRaw = (float) (Math
						.round(goodTotalscore * 10)) / 10;
				tv_good_comment.setText(goodRaw+ "%");
				//图片的url
				final String prodId = jsonObject1.getString("prodId");

				ImageView iv_delete = (ImageView) AbViewHolder.get(view,
						R.id.iv_delete);

				iv_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Log.d("dj_list_prodId", prodId);

						cancelCollectGood(prodId);
						list.remove(position);
						notifyDataSetChanged();
						Log.d("dj_position", position+"");

					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			return view;
		}

	}

	/**
	 * 获取收藏的商品列表
	 * 
	 * @param symbol
	 */
	private void getCollectedGoods(int pageIndex, int pageSize) {
		String url = AppConfig.URL_SHOP + "collect/findFollProdByPage";

		Log.d("url", "url:" + url);

		JSONObject params = new JSONObject();
		try {
			params.put("custUuid", RsSharedUtil.getString(
					MyCollectedGoodsActivity.this, AppConfig.UUID));
			Log.d("url", RsSharedUtil.getString(
					MyCollectedGoodsActivity.this, AppConfig.UUID));
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
				try {
					Log.d("dj_getCollectedGoods", "getCollectedGoods:"
							+ response.toString());
					// 如果没有数据
					if (response.toString().equals("")
							|| response.toString().equals("[0]")) {
						Log.d("dj_getCollectedGoods", "nothing");
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
							al_goods = datas;
							goodsAdapter = new GoodsAdapter(
									getApplicationContext(), al_goods);
							lv_mcg.setAdapter(goodsAdapter);
							loadingDialog.dismissDialog();
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
		stringRequest.setTag("getCollectedGoods");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 取消收藏
	 * 
	 * @param symbol
	 */
	private void cancelCollectGood(String prodId) {
		String url = AppConfig.URL_SHOP + "collect/unfollowProduct";

		Log.d("dj_url", "url:" + url);
		Log.d("dj_prodId", "prodId:" + prodId);
		JSONObject params = new JSONObject();
		try {
			params.put("prodId", prodId);
			params.put("custUuid", RsSharedUtil.getString(
					MyCollectedGoodsActivity.this, AppConfig.UUID));
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
//					popupTips(MyCollectedGoodsActivity.this, "取消收藏成功");
					internetDialog.showInternetDialog("取消收藏成功", true);
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
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("dj_cancel2",
							jsonObject.getString("description"));
					;
					internetDialog.showInternetDialog("取消收藏失败", false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("error_Exception", e.toString());

				}
			}

		});
		stringRequest.setTag("cancelCollectGood");
		MyApplication.getRequestQueue().add(stringRequest);
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
				MyCollectedGoodsActivity.this).create();
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
