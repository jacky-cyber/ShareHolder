package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.personal.StockFriendsActivityCopy;
import com.example.shareholders.activity.shop.ChatActivity;
import com.example.shareholders.activity.survey.UserJoinActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class Fragment_My_Friend extends Fragment {
	
	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true).cacheOnDisc(true).build();
	
	@ViewInject(R.id.mv_friend)
	MyListView mv_friend;
	
	private BitmapUtils bitmapUtils = null;

	// 提示无评论
	@ViewInject(R.id.tv_no_content)
	private TextView tv_no_content;

	// 查看更多
	@ViewInject(R.id.tv_watch_more2)
	private TextView tv_watch_more2;

	// 标题
	@ViewInject(R.id.tv_message)
	private TextView tv_message;

	@OnClick({ R.id.tv_watch_more2 })
	private void onClick(View v) {
		switch (v.getId()) {
		// 查看更多
		case R.id.tv_watch_more2:
			if (getActivity() instanceof OtherPeolpeInformationActivity)
			{
				Intent intent=new Intent(getActivity(),
						StockFriendsActivityCopy.class);
				intent.putExtra("uuid", getActivity()
						.getIntent().getExtras().getString("uuid"));
				intent.putExtra("userName",getActivity()
						.getIntent().getExtras().getString("userName"));
				startActivity(intent);
			}
			else {
				startActivity(new Intent(getActivity(),
						StockFriendsActivityCopy.class));
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_friend, null);
		ViewUtils.inject(this, v);
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_default_friend);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_default_friend);
		init();
		return v;
	}

	private void init() {
		
		
		String url = AppConfig.VERSION_URL
				+ "user/follow/list.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		if (getActivity() instanceof OtherPeolpeInformationActivity) {
			url = url + "&type=FOLLOW&userUuid="
					+ getActivity().getIntent().getExtras().getString("uuid")
					+ "&pageSize=3&pageIndex=0";
			tv_message.setText(getActivity().getIntent().getExtras()
					.getString("userName")
					+ "关注的股友：");
		} 
		else {
			url = url + "&type=FOLLOW&userUuid=myself&pageSize=3&pageIndex=0";
			// 获取个人信息
			DbUtils dbUtils = DbUtils.create(getActivity());
			try {
				// PersonalInformation personalInformation = dbUtils.findById(
				// PersonalInformation.class,
				// RsSharedUtil.getString(getActivity(), AppConfig.UUID));
				tv_message.setText("我关注的股友：");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.d("Fragment_My_Friend__urllllll", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("Fragment_My_Friend", response);
						try {
							JSONObject jsonobject = new JSONObject(response);
							JSONArray jsonArray = jsonobject
									.getJSONArray("users");
							final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> data = null;
							Iterator<String> iterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								datas.add(data);
								mv_friend.setAdapter(new FriendAdapter(
										getActivity(), datas));
								mv_friend
										.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(
													AdapterView<?> arg0,
													View arg1, int position,
													long arg3) {
												// TODO Auto-generated method
												if (datas.get(position)
														.get("uuid")
														.equalsIgnoreCase(
																RsSharedUtil.getString(getActivity(),
																		AppConfig.UUID)))

												{
													Intent intent1 = new Intent();
													intent1.setClass(getActivity(), MyProfileActivity.class);
													startActivity(intent1);
												} 
												else {
													Intent intent = new Intent(
															getActivity(),
															OtherPeolpeInformationActivity.class);
													intent.putExtra("uuid",
															datas.get(position)
																	.get("uuid"));
													intent.putExtra(
															"userName",
															datas.get(position)
																	.get("userName"));
													intent.putExtra("isFriend",
															true);
													intent.putExtra(
															"userLogo",
															datas.get(position)
																	.get("userLogo"));
													getActivity().startActivity(
															intent);
												}
												
											}

										});

								if (datas.size() == 0) {
									tv_watch_more2.setVisibility(View.GONE);
									tv_no_content.setVisibility(View.VISIBLE);
								} else {
									tv_watch_more2.setVisibility(View.VISIBLE);
									tv_no_content.setVisibility(View.GONE);
								}

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		stringRequest.setTag("Fragment_My_Friend");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_My_Friend");
		super.onDestroy();
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
				holder.iv_guanzhu = (ImageView) contentView
						.findViewById(R.id.iv_guanzhu);
				holder.tv_cata = (TextView) contentView
						.findViewById(R.id.catalog);
				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}

			holder.tv_cata.setVisibility(View.GONE);
			holder.iv_guanzhu.setImageResource(R.drawable.ico_tiaozhuan);
			
			bitmapUtils.display(holder.ci_image,
					list.get(position).get("userLogo"));
			
			/*ImageAware imageAware = new ImageViewAware(holder.ci_image, false);
			ImageLoader.getInstance().displayImage(list.get(position).get("userLogo"),
					imageAware, defaultOptions);*/
/*			ImageLoader.getInstance().displayImage(
					list.get(position).get("userLogo"), holder.ci_image);*/
			holder.tv_name.setText((CharSequence) list.get(position).get(
					"userName"));
			String industy=list.get(position).get(
					"industryName").toString();
			String location=list.get(position).get(
					"locationName");
			if (industy.equals("")||industy.equalsIgnoreCase("null")) {
				holder.tv_industry.setText("暂无");
			}
			else {
				holder.tv_industry.setText(industy);
			}
			
			if (location.equals("")||location.equalsIgnoreCase("null")) {
				holder.tv_position.setText("暂无");
			}
			else {
				holder.tv_position.setText(location);
			}
			
			
			/*holder.tv_industry.setText((CharSequence) list.get(position).get(
					"industryName"));
			holder.tv_position.setText((CharSequence) list.get(position).get(
					"locationName"));*/
			holder.ci_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					if (list.get(position)
							.get("uuid")
							.equalsIgnoreCase(
									RsSharedUtil.getString(getActivity(),
											AppConfig.UUID)))

					{
						Intent intent1 = new Intent();
						intent1.setClass(getActivity(), MyProfileActivity.class);
						startActivity(intent1);
					} else {
						Intent intent2 = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("uuid", list.get(position).get("uuid"));
						bundle.putString("userName",
								list.get(position).get("userName"));
						bundle.putString("useLogo",
								list.get(position).get("userLogo"));
						intent2.setClass(getActivity(),
								OtherPeolpeInformationActivity.class);
						intent2.putExtras(bundle);
						startActivity(intent2);
					}
				}
			});
			return contentView;
		}

		class ViewHolder {

			CircleImageView ci_image;
			TextView tv_name;
			TextView tv_industry;
			TextView tv_position;
			ImageView iv_guanzhu;
			TextView tv_cata;
		}

	}
}
