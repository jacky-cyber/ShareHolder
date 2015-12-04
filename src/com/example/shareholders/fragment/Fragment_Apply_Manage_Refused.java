package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class Fragment_Apply_Manage_Refused extends Fragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	private BitmapUtils bitmapUtils = null;
	View mview;

/*	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.build();
	*/
	final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
	@ViewInject(R.id.lv_apply_manage)
	private MyListView lv_manage;

	// 上下拉刷新
	@ViewInject(R.id.listview_pulltorefresh)
	private PullToRefreshView mPullToRefreshView;

	private ManageSignAdapter manageAdapter;
	private ArrayList<HashMap<String, String>> list;

	private int pageSize = 10;
	private int index = 0;
	public int currentPage = 0;// 当前页数

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_apply_manage_listv,
				container, false);
		ViewUtils.inject(this, mview);
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		getzixun();
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		return mview;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void getzixun() {
		String uuid = getActivity().getIntent().getExtras().getString("uuid");
		datas.clear();
		String url = AppConfig.URL_USER;
		url += "enroll/list.json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token");
		url += "&pageSize=" + pageSize + "&pageIndex=" + index + "&surveyUuid="
				+ uuid + "&state=FAILED";

		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.e("ad", response);
						if (response.equals("") || response.equals("[0]")) {
						} else {
							try {
								JSONObject object = new JSONObject(response
										.toString());
								JSONArray all = object.getJSONArray("users");
								Log.e("all", all.toString());
								HashMap<String, String> data = null;
								Iterator<String> iterator = null;
								for (int i = 0; i < all.length(); i++) {
									data = new HashMap<String, String>();
									iterator = all.getJSONObject(i).keys();
									while (iterator.hasNext()) {
										String key = iterator.next();
										Log.e("cacacaca", key);
										data.put(key,
												all.getJSONObject(i).get(key)
														.toString());
									}
									datas.add(data);

								}
								final ManageSignAdapter adapter = new ManageSignAdapter(
										getActivity(), datas);
								lv_manage.setAdapter(adapter);
								adapter.notifyDataSetChanged();
								/*
								 * lv_manage.setOnItemClickListener(new
								 * OnItemClickListener() {
								 * 
								 * @Override public void
								 * onItemClick(AdapterView<?> arg0, View arg1,
								 * int position, long arg3) { // TODO
								 * Auto-generated method stub Intent intent=new
								 * Intent( getActivity().getApplication(),
								 * ManageSignActivity.class);
								 * 
								 * Bundle bundle =new Bundle();
								 * bundle.putString("uuid",
								 * datas.get(position).get("uuid"));
								 * intent.putExtras(bundle);
								 * 
								 * startActivity(intent); } });
								 */
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("lele_error", error.toString());
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error", jsonObject.get("description")
									.toString());
							index--;
						} catch (Exception e) {
							Log.d("lele_error", "未知错误");
						}

					}
				});
		// volleyRequestQueue.add(stringRequest);
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	public class ManageSignAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		public ManageSignAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
			this.list = list;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// return list.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View converView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = mInflater.inflate(R.layout.item_manage_sign_list,
						arg2, false);

				viewHolder.ci_image = (CircleImageView) converView
						.findViewById(R.id.person_manage_sign);
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_manage_name);
				viewHolder.tv_industry = (TextView) converView
						.findViewById(R.id.tv_manage_industry);
				viewHolder.tv_position = (TextView) converView
						.findViewById(R.id.address);
				viewHolder.tv_numb = (TextView) converView
						.findViewById(R.id.numb);
				viewHolder.tv_pass = (TextView) converView
						.findViewById(R.id.pass);
				viewHolder.tv_pass.setText("已拒绝");
				viewHolder.tv_pass.setBackground(getResources().getDrawable(
						R.drawable.shape_manage_request_pass));
				converView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}
			viewHolder.tv_name.setText(list.get(position).get("userName"));
			viewHolder.tv_industry.setText(list.get(position).get(
					"industryName"));
			viewHolder.tv_position.setText(list.get(position).get(
					"locationName"));
			viewHolder.tv_numb.setText(list.get(position).get("coin"));
			// 头像
			bitmapUtils.display(viewHolder.ci_image,
					list.get(position).get("userLogo").toString());
			
			/*ImageAware imageAware = new ImageViewAware(viewHolder.ci_image, false);
			ImageLoader.getInstance().displayImage(list.get(position).get("userLogo").toString(),
			imageAware, defaultOptions);*/
			return converView;
		}

		class ViewHolder {

			CircleImageView ci_image;
			TextView tv_name;
			TextView tv_industry;
			TextView tv_position;
			TextView tv_numb;
			TextView tv_pass;
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				index += 1;
				getzixun();
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				datas.clear();
				index = 0;
				getzixun();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
	}
}
