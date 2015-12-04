package com.example.shareholders.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.survey.SingleCommentActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_Share_Search2.ShareSearchAdapter2;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Share_SearchFriends_List extends Fragment implements
OnHeaderRefreshListener, OnFooterRefreshListener {

	@ViewInject(R.id.lv_sharefriends_list)
	ListView ShareFriList;
	private ArrayList<HashMap<String, String>> allFriends;

	private BitmapUtils bitmapUtils;

	@ViewInject(R.id.tv_tip)
	private TextView tv_tip;

	FriendAdapter adapter;

	private final int HEAD = 0;
	private final int FOOT = 1;

	// 上下拉刷新
	@ViewInject(R.id.refresh1)
	private PullToRefreshView refresh;

	// pageSize,固定为5个朋友
	private static int PAGE_SIZE = 15;

	// pageIndex,从0递增
	private int all_index = 0;

	//	private ProgressDialog progressDialog;
	private InternetDialog internetDialog;

	private String keyWordString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_sharefriends_list,
				container, false);
		ViewUtils.inject(this, v);
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils
		.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		initview();
		return v;
	}

	private void initview() {
		//		progressDialog = new ProgressDialog(getActivity());
		internetDialog = new InternetDialog(getActivity());
		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		allFriends = new ArrayList<HashMap<String, String>>();
		adapter = new FriendAdapter(getActivity(), allFriends);
		ShareFriList.setAdapter(adapter);

		ShareFriList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (allFriends.get(position)
						.get("uuid")
						.equals(RsSharedUtil.getString(getActivity(),
								AppConfig.UUID))) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), MyProfileActivity.class);
					startActivity(intent);
				} else {
					Bundle bundle = new Bundle();
					bundle.putString("uuid", allFriends.get(position).get("uuid"));
					bundle.putString("userName",
							allFriends.get(position).get("userName"));
					bundle.putString("useLogo",
							allFriends.get(position).get("userLogo"));
					bundle.putInt("position",position);

					Intent intent = new Intent();
					intent.setClass(getActivity(),
							OtherPeolpeInformationActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 200);
				}


			}


		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("findFriends");
		getActivity().registerReceiver(receiver, intentFilter);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
		MyApplication.getRequestQueue().cancelAll("findfriends");
	}

	// 用于注册广播的类
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			// 得到广播中得到的数据，并显示出来
			// 从广播得到edittext的内容
			keyWordString = intent.getExtras().getString("findFriends");
			findFriends(intent.getExtras().getString("findFriends"), all_index,
					PAGE_SIZE, HEAD);

		}
	};

	private void findFriends(String name, int pageIndex, int pageSize,
			final int type) {
		String url = AppConfig.VERSION_URL
				+ "user/query.json?access_token=";
		url += RsSharedUtil
				.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		try {

			url = url + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize
					+ "&keyWord=" + URLEncoder.encode(name,"UTF-8");;

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d("FindFriend_url", url);

		StringRequest stringRequest = new StringRequest(url, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("mylogo", response);
				try {
					Log.d("FindFriend lists", response);
					if (response.equals("")||response.equals("null")) {
						tv_tip.setVisibility(View.VISIBLE);
						ShareFriList.setVisibility(View.GONE);
					}else {
						tv_tip.setVisibility(View.GONE);
						ShareFriList.setVisibility(View.VISIBLE);
						JSONObject jsonObject = new JSONObject(response);
						JSONArray jsonArray = jsonObject
								.getJSONArray("users");
						final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> data = null;
						Iterator<String> iterator = null;
						for (int i = 0; i < jsonArray.length(); i++) {
							data = new HashMap<String, String>();
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
						Log.d("mylogo", datas.toString());
						if (type == FOOT)
							allFriends.addAll(datas);
						else {
							allFriends.clear();
							allFriends.addAll(datas);
						}

						adapter.notifyDataSetChanged();
					}


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("mylogo", error.toString());
			}
		});
		stringRequest.setTag("findfriends");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	class FriendAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		FriendAdapter(Context context, ArrayList<HashMap<String, String>> datas) {
			this.context = context;
			this.list = datas;
			mInflater = LayoutInflater.from(context);
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
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater
						.inflate(R.layout.item_friend_list, null);

				holder.ci_image = (CircleImageView) contentView
						.findViewById(R.id.ci_friend_figure);
				holder.tv_name = (TextView) contentView
						.findViewById(R.id.tv_userName);
				holder.tv_industry = (TextView) contentView
						.findViewById(R.id.tv_industry);
				holder.tv_position = (TextView) contentView
						.findViewById(R.id.tv_location);
				holder.iv_addFriend = (ImageView) contentView
						.findViewById(R.id.iv_guanzhu);
				holder.tvLetter = (TextView) contentView
						.findViewById(R.id.catalog);
				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			holder.tvLetter.setVisibility(View.GONE);

			bitmapUtils.display(holder.ci_image,
					list.get(position).get("userLogo"));

			/*ImageLoader.getInstance().displayImage(
						list.get(position).get("userLogo"), holder.ci_image);*/
			holder.tv_name.setText((CharSequence) list.get(position).get(
					"userName"));

			if (((String)list.get(position).get(
					"industryName")).equals("")||((String)list.get(position).get(
							"industryName")).equalsIgnoreCase("null")) 
			{
				holder.tv_industry.setText("暂无");
			}
			else {
				holder.tv_industry.setText((CharSequence) list.get(position).get(
						"industryName"));
			}

			if (((String)list.get(position).get(
					"locationName")).equals("")||((String)list.get(position).get(
							"locationName")).equalsIgnoreCase("null")) 
			{
				holder.tv_position.setText("暂无");
			}
			else {
				holder.tv_position.setText((CharSequence) list.get(position).get(
						"locationName"));
			}


/*
			holder.tv_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (list.get(position)
							.get("uuid")
							.equals(RsSharedUtil.getString(getActivity(),
									AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), MyProfileActivity.class);
						startActivity(intent);
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid", list.get(position).get("uuid"));
						bundle.putString("userName",
								list.get(position).get("userName"));
						bundle.putString("useLogo",
								list.get(position).get("userLogo"));
						Intent intent = new Intent();
						intent.setClass(getActivity(),
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			holder.ci_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (list.get(position)
							.get("uuid")
							.equals(RsSharedUtil.getString(getActivity(),
									AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), MyProfileActivity.class);
						startActivity(intent);
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid", list.get(position).get("uuid"));
						bundle.putString("userName",
								list.get(position).get("userName"));
						bundle.putString("useLogo",
								list.get(position).get("userLogo"));
						bundle.putInt("position",position);

												
						if (list.get(position).get("").equals("CANCEL")) {
							bundle.putBoolean("isFriend",false);
						}
						else {
							bundle.putBoolean("isFriend",true);
						}

						Intent intent = new Intent();
						intent.setClass(getActivity(),
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 200);
					}
				}
			});*/

			// 关注，被关注，相互关注
			final String isGuanZhu = (String) list.get(position).get("type");

			// 若搜索结果是自己不显示添加或删除好友图标
			if (list.get(position)
					.get("uuid")
					.equals(RsSharedUtil.getString(getActivity(),
							AppConfig.UUID))) {
				holder.iv_addFriend.setVisibility(View.GONE);
			} else {
				holder.iv_addFriend.setVisibility(View.VISIBLE);
				if (isGuanZhu.equalsIgnoreCase("FOLLOWED")
						|| isGuanZhu.equalsIgnoreCase("CANCEL")) {
					holder.iv_addFriend
					.setImageResource(R.drawable.btn_guanzhu111);
				} else if (isGuanZhu.equalsIgnoreCase("FOLLOW")) {
					holder.iv_addFriend
					.setImageResource(R.drawable.btn_quxiaoguanzhu111);
				} else if (isGuanZhu.equalsIgnoreCase("MUTUAL")) {
					holder.iv_addFriend
					.setImageResource(R.drawable.btn_xianghudequxiaoguanzhu111);
				}
			}

			holder.iv_addFriend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					final GeneralDialog dialog = new GeneralDialog(
							getActivity());
					if (isGuanZhu.equalsIgnoreCase("FOLLOWED")
							|| isGuanZhu.equalsIgnoreCase("CANCEL")) {
						dialog.setMessage("关注该股友吗?");
					} else {
						dialog.setMessage("取消关注该股友吗?");
					}

					dialog.setCancel(true);
					// 去掉图片提示
					dialog.noMessageIcon();
					// 确定按钮的点击事件
					if (!BtnClickUtils.isFastDoubleClick()) {

						dialog.setPositiveButton(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Log.d("access_token", RsSharedUtil.getString(
										getActivity(), AppConfig.ACCESS_TOKEN));
								//							progressDialog.show();
								cancelNotice(position, isGuanZhu);
								dialog.dismiss();
							}
						});
					}
					// 取消按钮的点击事件
					dialog.setNegativeButton(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

				}
			});

			return contentView;
		}

		class ViewHolder {

			CircleImageView ci_image;
			TextView tv_name;
			TextView tv_industry;
			TextView tv_position;
			ImageView iv_addFriend;

			TextView tvLetter;
		}

	}

	private void cancelNotice(final int position, final String type) {
		String url = null;
		if (type.equalsIgnoreCase("FOLLOWED")
				|| type.equalsIgnoreCase("CANCEL")) {
			//			progressDialog.setMessage("正在关注好友...");
			url = AppConfig.URL_USER
					+ "follow.json?access_token="
					+ RsSharedUtil.getString(getActivity(),
							AppConfig.ACCESS_TOKEN) + "&userUuid="
							+ allFriends.get(position).get("uuid") + "&type=FOLLOW";
			Log.d("add_friend_url", url);
		}

		else if (type.equalsIgnoreCase("FOLLOW")
				|| type.equalsIgnoreCase("MUTUAL")) {
			//			progressDialog.setMessage("正在取消关注好友...");
			url = AppConfig.URL_USER
					+ "follow.json?access_token="
					+ RsSharedUtil.getString(getActivity(),
							AppConfig.ACCESS_TOKEN) + "&userUuid="
							+ allFriends.get(position).get("uuid") + "&type=CANCEL";
		}
		//		progressDialog.show();
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {

				if (type.equalsIgnoreCase("MUTUAL")
						|| type.equalsIgnoreCase("FOLLOW")) {
					//							progressDialog.dismiss();
					//							Toast.makeText(getActivity(), "取消关注好友成功",
					//									Toast.LENGTH_SHORT).show();
					internetDialog.showInternetDialog("取消关注好友成功", true);
					allFriends.get(position).put("type", "FOLLOWED");
					adapter.notifyDataSetChanged();

				}
				// TODO Auto-generated method stub
				else {
					//							progressDialog.dismiss();
					//							Toast.makeText(getActivity(), "好友关注成功",
					//									Toast.LENGTH_SHORT).show();
					internetDialog.showInternetDialog("关注好友成功", true);
					allFriends.get(position).put("type", "FOLLOW");
					adapter.notifyDataSetChanged();
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest.setTag("AddsFriendsActvity");
		MyApplication.getRequestQueue().add(stringRequest);
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==200) {
			if(data!=null)
			{
				int position=data.getIntExtra("position", 0);
				boolean isFriend=data.getBooleanExtra("isfriend", false);
				Log.d("position_mm", position+"");
				Log.d("isFriend_mm", isFriend+"");
				if (isFriend) {
					allFriends.get(position).put("type", "FOLLOW");
				}
				else {
					allFriends.get(position).put("type", "FOLLOWED");
				}
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		MyApplication.getRequestQueue().cancelAll("AddsFriendsActvity");

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		all_index = 0;
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				findFriends(keyWordString, all_index, PAGE_SIZE, HEAD);
				refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				findFriends(keyWordString, ++all_index, PAGE_SIZE, FOOT);
				refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}


}
