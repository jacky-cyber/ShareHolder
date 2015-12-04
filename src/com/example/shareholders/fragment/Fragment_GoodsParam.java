package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.shop.GoodsDetailsActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_GoodsParam extends Fragment {

	@ViewInject(R.id.lv_gd_details_param)
	ListView lv_gd_details_param;

	ArrayList<String> al_param;

	private ParamAdapter paramAdapter;

	private RequestQueue requestQueue;

	private String prodUuid;

	private String prodCode;

	private long time;

	private String prodDesc;

	private int prodWeight;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_goods_param, null);
		ViewUtils.inject(this, v);
		requestQueue = Volley.newRequestQueue(getActivity());
		prodUuid = getActivity().getIntent().getExtras().getString("prodUuid");
		GetSkuByProd();
		// initView();
		return v;
	}

	// private void initView() {
	// paramAdapter = new ParamAdapter(getActivity());
	// lv_gd_details_param.setAdapter(paramAdapter);
	//
	// }

	// 获取商品参数
	private void GetSkuByProd() {
		String url = AppConfig.URL_SHOP + "product/findProductDetail";
		Log.d("dj_param_url", url);
		Log.d("dj_prodUuid", prodUuid);
		Log.d("dj_custId",  RsSharedUtil.getString(
				getActivity(), AppConfig.UUID));
		JSONObject params = new JSONObject();
		try {
			params.put("prodUuid", prodUuid);
//			params.put("custId", AppConfig.custId);
			params.put("custUuid", RsSharedUtil.getString(
					getActivity(), AppConfig.UUID));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_param_reponse", response);
//						al_param = new ArrayList<String>();
//						Log.d("jat", response);

						// Toast.makeText(getActivity(), response, 0).show();

						al_param = new ArrayList<String>();
						try {
							JSONObject jsonObject = new JSONObject(response);
							prodCode = jsonObject.getString("prodCode");
							al_param.add(prodCode);
							prodDesc = jsonObject.getString("prodDesc");
							if (!prodDesc.equals("")) {
								al_param.add(prodDesc);
							}
							prodWeight = jsonObject.getInt("prodWeight");
							al_param.add(prodWeight + "");
							time = jsonObject.getJSONObject("prodDate")
									.getLong("time");
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss");
							al_param.add(sdf.format(time));
							Log.d("dj_al_param", al_param.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						paramAdapter = new ParamAdapter(getActivity(), al_param);
						lv_gd_details_param.setAdapter(paramAdapter);

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Toast.makeText(getActivity(), "失败", 0).show();
					}
				});
		requestQueue.add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		requestQueue.cancelAll(requestQueue);
	}

	private class ParamAdapter extends BaseAdapter {

		Context context;
		private List<String> list = new ArrayList<String>();

		public ParamAdapter(Context context, ArrayList<String> list) {
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
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_gd_details_param, parent, false);
			}

			TextView tv_param_name = (TextView) AbViewHolder.get(view,
					R.id.tv_param_name);
			TextView tv_param = (TextView) AbViewHolder
					.get(view, R.id.tv_param);
			if (list.size() == 4) {
				if (position == 0) {
					tv_param_name.setText("条形码");
					tv_param.setText(list.get(position));
				} else if (position == 1) {
					tv_param_name.setText("产品描述");
					tv_param.setText(list.get(position));
				} else if (position == 2) {
					tv_param_name.setText("产品重量");
					tv_param.setText(list.get(position));
				} else if (position == 3) {
					tv_param_name.setText("添加时间");
					tv_param.setText(list.get(position));
				}
			} else if (list.size() == 3) {
				if (position == 0) {
					tv_param_name.setText("条形码");
					tv_param.setText(list.get(position));
				} else if (position == 1) {
					tv_param_name.setText("产品重量");
					tv_param.setText(list.get(position));
				} else if (position == 2) {
					tv_param_name.setText("添加时间");
					tv_param.setText(list.get(position));
				}
			}

			return view;
		}

	}

}
