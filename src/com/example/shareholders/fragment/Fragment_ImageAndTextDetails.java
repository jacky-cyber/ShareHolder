package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_ImageAndTextDetails extends Fragment {

	@ViewInject(R.id.gv_goods_pic_details)
	private GridView gv_goods_pic_details;

	private String prodUuid;

	private List<String> imageViews;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_image_and_text_details,
				null);
		ViewUtils.inject(this, v);

		prodUuid = getActivity().getIntent().getExtras().getString("prodUuid");

		init();
		return v;
	}

	private void init() {
		String url = AppConfig.URL_SHOP + "product/getProdPicDetail";
		JSONObject params = new JSONObject();
		try {
			params.put("prodUuid", prodUuid);
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {
							JSONArray jsonArray = new JSONArray(response);
							imageViews = new ArrayList<String>();
							for (int i = 0; i < jsonArray.length(); i++) {
								imageViews.add(jsonArray.getJSONObject(i)
										.getString("picUrl"));
							}
							gv_goods_pic_details
									.setAdapter(new GoodsDetailsAdapter(
											getActivity(), imageViews));
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
		stringRequest.setTag("getPicDetails");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("getPicDetails");
	}

	class GoodsDetailsAdapter extends BaseAdapter {

		private List<String> lists = new ArrayList<String>();
		private Context context;

		private LayoutInflater inflater;

		public GoodsDetailsAdapter(Context context, List<String> lists) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.lists = lists;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.image_and_text_item,
						null);
			}
			ImageView iv_image_and_text_item = AbViewHolder.get(convertView,
					R.id.iv_image_and_text_item);
			ImageLoader.getInstance().displayImage(lists.get(position),
					iv_image_and_text_item);
			return convertView;
		}

	}

}
