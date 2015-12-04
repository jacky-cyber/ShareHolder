package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.service.AuditRegistrationService;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class Fragment_Apply_Manage_Pendingaudit extends Fragment implements
OnHeaderRefreshListener, OnFooterRefreshListener {

	/*	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnLoading(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true).cacheOnDisc(true).build();*/

	private BitmapUtils bitmapUtils = null;
	View mview;
	
	LoadingDialog loadingDialog;

	final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

	@ViewInject(R.id.lv_apply_manage)
	private MyListView lv_manage;
	// 上下拉刷新
	@ViewInject(R.id.listview_pulltorefresh)
	private PullToRefreshView mPullToRefreshView;

	// 报名审核通过的用户uuid
	ArrayList<String> userUuid = new ArrayList<String>();
	// 调研uuid
	private String surveyUuid;

	private int pageSize = 10;
	private int index = 0;

	public int currentPage = 0;// 当前页数
	
	private ManageSignAdapter manageAdapter;
	private ArrayList<HashMap<String, String>> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_apply_manage_listv,
				container, false);
		ViewUtils.inject(this, mview);

		loadingDialog = new LoadingDialog(getActivity());
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		Log.d("onresume", "onresumepending");
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
		loadingDialog.showLoadingDialog();
		// 得到AllApplyManageActivity中得到的调研uuid
		String uuid = getActivity().getIntent().getExtras().getString("uuid");
		surveyUuid = uuid;
		datas.clear();
		String url = AppConfig.URL_USER;
		url += "enroll/list.json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token");
		url += "&pageSize=" + pageSize + "&pageIndex=" + index + "&surveyUuid="
				+ uuid + "&state=ENROLL";

		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.e("adcde", response);
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
						manageAdapter = new ManageSignAdapter(
								getActivity(), datas);
						lv_manage.setAdapter(manageAdapter);
						manageAdapter.notifyDataSetChanged();
						loadingDialog.dismissDialog();
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
		loadingDialog.dismissDialog();
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 当fragment被切换或者被关闭时，使用已选择的数据，启动服务类，提交审核名单
	 */
//	@Override
//	public void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//		//批量处理报名列表
//		//		Intent intent = new Intent();
//		//		Bundle bundle = new Bundle();
//		//		bundle.putStringArrayList("userUuid", userUuid);
//		//		bundle.putString("surveyUuid", surveyUuid);
//		//		intent.putExtras(bundle);
//		//		intent.setClass(getActivity().getApplicationContext(),
//		//				AuditRegistrationService.class);
//		//		getActivity().startService(intent);
//	}

	// 提交报名审核
	private void postAuditRegistration(String surveyUuid,String userUuid,final int position) {
		loadingDialog.showLoadingDialog();
		String url = AppConfig.URL_SURVEY + "enroll/check.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		JSONObject params = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			params.put("status", "SUCCESS");
			array.put(userUuid);
			params.put("userUuids", array);
			params.put("surveyUuid", surveyUuid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
//						 Toast.makeText(getActivity(), "提交成功",
//						 0).show();
						datas.remove(position);
						manageAdapter.notifyDataSetChanged();
						//成功后发送广播通知更新，通知通过的fragment更新数据
						getActivity().sendBroadcast(new Intent("applypass"));
						
						loadingDialog.dismissDialog();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
//						 Toast.makeText(getActivity(), "提交失败",
//						 0).show();
						loadingDialog.dismissDialog();
					}
				});
		stringRequest.setTag("pass");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("ApplyForStatementActivity");
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		MyApplication.getRequestQueue().cancelAll("pass");
		super.onDestroy();
	}

	public class ManageSignAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;
		// 设置是否选中状态
		private List<Boolean> listBoolean = new ArrayList<Boolean>();

		public ManageSignAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
			this.list = list;
			this.mInflater = LayoutInflater.from(context);
			init();
		}

		/**
		 * 初始时把所有值设置为false
		 */
		private void init() {
			for (int i = 0; i < list.size(); i++) {
				listBoolean.add(false);
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// return list.get(position);
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View converView, ViewGroup arg2) {

			if (converView == null) {
				// 初始化加载页面
				converView = mInflater.inflate(R.layout.item_manage_sign_list,
						arg2, false);
			}

			TextView tv_name = AbViewHolder
					.get(converView, R.id.tv_manage_name);
			CircleImageView ci_image = AbViewHolder.get(converView,
					R.id.person_manage_sign);
			TextView tv_industry = AbViewHolder.get(converView,
					R.id.tv_manage_industry);
			TextView tv_position = AbViewHolder.get(converView, R.id.address);
			TextView tv_numb = AbViewHolder.get(converView, R.id.numb);
			final TextView tv_pass = AbViewHolder.get(converView, R.id.pass);

			tv_name.setText(list.get(position).get("userName"));
			tv_industry.setText(list.get(position).get("industryName"));
			tv_position.setText(list.get(position).get("locationName"));
			tv_numb.setText(list.get(position).get("coin"));

			// 通过选择
			tv_pass.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// 判断有没有选择
					//					if (!listBoolean.get(position)) {
					//						Log.d("chunjie", "");
					//						userUuid.add(list.get(position).get("uuid"));
					//						tv_pass.setText("已选择");
					//						listBoolean.set(position, true);
					//					} else {
					//						for (int i = 0; i < userUuid.size(); i++) {
					//							if (userUuid.get(i).equals(
					//									list.get(position).get("uuid"))) {
					//								userUuid.remove(i);
					//								tv_pass.setText("通过");
					//								listBoolean.set(position, false);
					//							}
					//						}
					//					}
					if(!BtnClickUtils.isFastDoubleClick()){
						new AlertDialog.Builder(getActivity())   
						.setTitle("报名审核")  
						.setMessage("确认是否通过")  
						.setPositiveButton("是", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								postAuditRegistration(surveyUuid, list.get(position).get("uuid"),position);
								dialog.dismiss();
							}
						})  
						.setNegativeButton("否", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						})  
						.show(); 
					}
				}
			});
			// 头像
			bitmapUtils.display(ci_image, list.get(position).get("userLogo")
					.toString());
			/*ImageAware imageAware = new ImageViewAware(ci_image, false);
			ImageLoader.getInstance().displayImage(list.get(position).get("userLogo").toString(),
			imageAware, defaultOptions);*/
			return converView;
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
