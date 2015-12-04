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

@ContentView(R.layout.activity_my_concern_company)
public class MyConcernCompanyActivity extends Activity implements
		OnItemClickListener {

	@ViewInject(R.id.lv_mcp)
	private ListView lv_mcp;

	private ArrayList<HashMap<String, String>> al_company;

	private CompanyAdapter companyAdapter;

	// 正在加载的提示框
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
		
		initView();
	}

	@Override
	protected void onStop() {
		MyApplication.getRequestQueue().cancelAll("getConcernCompanies");
		MyApplication.getRequestQueue().cancelAll("cancelFollowCompany");
		super.onStop();
	}

	@OnClick({ R.id.title_fs_note ,R.id.rl_return})
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void initView() {
		al_company = new ArrayList<HashMap<String, String>>();
		companyAdapter = new CompanyAdapter(this, al_company);
		lv_mcp.setAdapter(companyAdapter);
		lv_mcp.setOnItemClickListener(this);
		getConcernCompanies();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, CompanyDetailActivity.class);
		intent.putExtra("shopId", al_company.get(arg2).get("shopId"));
		// intent.putExtra("picUrl", al_company.get(arg2).get("picUrl"));
		startActivity(intent);
	}

	private class CompanyAdapter extends BaseAdapter {

		Context context;
		ArrayList<HashMap<String, String>> list;

		public CompanyAdapter(Context context,
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
						R.layout.item_my_concerned_company, parent, false);
			}

			// 公司图片
			ImageView iv_head = (ImageView) AbViewHolder
					.get(view, R.id.iv_head);
			// ImageLoader.getInstance().displayImage(
			// list.get(position).get("picUrl"), iv_head);

			// 公司名
			TextView tv_company = (TextView) AbViewHolder.get(view,
					R.id.tv_company);
			// tv_company.setText(list.get(position).get("shopName"));

			// 公司商品数
			TextView tv_goods_num = (TextView) AbViewHolder.get(view,
					R.id.tv_goods_num);
			// tv_goods_num.setText(list.get(position).get("shopProductNum"));

			// 公司关注人数
			TextView tv_follow_num = (TextView) AbViewHolder.get(view,
					R.id.tv_follow_num);
			// tv_follow_num.setText(list.get(position).get("shopFollowers"));

			// // 是否有促销
			TextView tv_promotion = (TextView) AbViewHolder.get(view,
					R.id.tv_promotion);
			tv_promotion.setVisibility(View.GONE);
			// if (Boolean.parseBoolean(list.get(position).get("lastProm"))) {
			// tv_promotion.setVisibility(View.VISIBLE);
			// } else {
			// tv_promotion.setVisibility(View.GONE);
			// }

			// 是否有新品
			TextView tv_has_new_goods = (TextView) AbViewHolder.get(view,
					R.id.tv_has_new_goods);
			tv_has_new_goods.setVisibility(View.GONE);
			// if (Boolean.parseBoolean(list.get(position).get("lastProd"))) {
			// tv_has_new_goods.setVisibility(View.VISIBLE);
			// } else {
			// tv_has_new_goods.setVisibility(View.GONE);
			// }

			try {
				// 对product进行解析
				JSONObject jsonObject1 = new JSONObject(list.get(position)
						.get("shop").toString());
				// 公司图片
				String picUrl = jsonObject1.getString("picUrl");
				ImageLoader.getInstance().displayImage(picUrl, iv_head);
				// 公司名
				String shopName = jsonObject1.getString("shopName");
				tv_company.setText(shopName);
				// 公司商品数
				String shopProductNum = jsonObject1.getString("shopProductNum");
				tv_goods_num.setText(shopProductNum);
				// 公司关注人数
				String shopFollowers = jsonObject1.getString("shopFollowers");
				tv_follow_num.setText(shopFollowers);
				// 公司图片
				final String shopId = jsonObject1.getString("shopId");
				// 删除监听
				ImageView iv_delete = (ImageView) AbViewHolder.get(view,
						R.id.iv_delete);
				iv_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						cancelFollowCompany(shopId);
						list.remove(position);
						notifyDataSetChanged();
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
	 * 获取关注的公司列表
	 * 
	 * @param symbol
	 */
	private void getConcernCompanies() {
		String url = AppConfig.URL_SHOP + "collect/findFollShopByPage";

		Log.d("url", "url:" + url);

		JSONObject params = new JSONObject();
		try {
			params.put("custUuid", RsSharedUtil.getString(
					MyConcernCompanyActivity.this, AppConfig.UUID));
			params.put("pageIndex", "1");
			params.put("pageSize", "10");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("getConcernCompanies", "getConcernCompanies:"
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
									al_company = datas;
									companyAdapter = new CompanyAdapter(
											getApplicationContext(), al_company);
									lv_mcp.setAdapter(companyAdapter);
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
		stringRequest.setTag("getConcernCompanies");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 取消关注
	 * 
	 * @param symbol
	 */
	private void cancelFollowCompany(String shopId) {
		String url = AppConfig.URL_SHOP + "shop/cancelFollowCertainShop";

		Log.d("url", "url:" + url);

		JSONObject params = new JSONObject();
		try {
			params.put("shopId", shopId);
			params.put("custUuid", RsSharedUtil.getString(
					MyConcernCompanyActivity.this, AppConfig.UUID));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("cancelFollowCompany", "cancelFollowCompany:"
									+ response.toString());
//							popupTips(MyConcernCompanyActivity.this, "取消关注成功");
							internetDialog.showInternetDialog("取消关注成功", true);
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
							internetDialog.showInternetDialog("取消关注失败", false);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
					}

				});
		stringRequest.setTag("cancelFollowCompany");
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
				MyConcernCompanyActivity.this).create();
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
