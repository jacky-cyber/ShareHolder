package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.shop.ChatActivity;
import com.example.shareholders.common.CharacterParser;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MutualFriendPinyinComparator;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.common.SideBar;
import com.example.shareholders.common.SideBar.OnTouchingLetterChangedListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.LocalFollowStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalFollowedStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalMutualStockFriend;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.Mapper;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_My_MutualFriend extends Fragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private String uuid = "";

	// 判断是否是分享，getIntent获取过来的
	private String user_share = "";

	private String share_content = "";

	/*
	 * private DisplayImageOptions defaultOptions = new
	 * DisplayImageOptions.Builder()
	 * .showImageForEmptyUri(R.drawable.ico_default_headview)
	 * .showImageOnFail(R.drawable.ico_default_headview)
	 * .cacheInMemory(true).cacheOnDisc(true).build();
	 */

	private BitmapUtils bitmapUtils = null;

	@ViewInject(R.id.lv_friends)
	private ListView lv_mutulList;

	@ViewInject(R.id.ll_no_friend)
	private LinearLayout ll_no_friendLayout;

	private FriendsAdapter adapter;

	// 字母提醒
	@ViewInject(R.id.tv_fs_dialog1)
	private TextView tv_dialog;
	// 侧滑搜索
	@ViewInject(R.id.sb_sidebar)
	private SideBar sb_fs_sidebar;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<LocalMutualStockFriend> SourceDateList = new ArrayList<LocalMutualStockFriend>();

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private MutualFriendPinyinComparator pinyinComparator;

	// 上下拉刷新
	@ViewInject(R.id.refresh1)
	private PullToRefreshView refresh;

	// pageSize,固定为200个朋友
	private static int PAGE_SIZE = 200;

	// pageIndex,从0递增
	private int all_index = 0;

	/** 全部好友 */
	private ArrayList<HashMap<String, String>> allFriends = new ArrayList<HashMap<String, String>>();

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	private ProgressDialog progressDialog;
	DbUtils dbUtils;
	ImageLoader iLoader;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			{
				try {

					friends = dbUtils.findAll(LocalMutualStockFriend.class);
					if (friends != null) {
						initFromDb(friends);
					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_stock_friends, null);
		ViewUtils.inject(this, v);

		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils
				.configDefaultLoadFailedImage(R.drawable.ico_default_headview);

		// 如果是好友的股友列表
		try {
			uuid = getActivity().getIntent().getExtras().getString("uuid");
		} catch (Exception e) {

		}

		if (uuid == null) {
			uuid = "";
		}

		try {
			user_share = getActivity().getIntent().getExtras()
					.getString("share");
			share_content = getActivity().getIntent().getExtras()
					.getString("shareContent");
		} catch (Exception e) {

		}
		if (user_share == null) {
			user_share = "";
		}
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("正在取消关注该好友...");

		initView();

		IntentFilter intentFilter = new IntentFilter("followFriendsent");
		getActivity().registerReceiver(broadcastReceiver, intentFilter);

		IntentFilter intentFilter2 = new IntentFilter("followedFriendsent");
		getActivity().registerReceiver(broadcastReceiver, intentFilter2);
		initData(0, 200, HEAD);

		return v;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("MutualActvity");
		getActivity().unregisterReceiver(broadcastReceiver);
	}

	private void initView() {

		bitmapUtils.resume();

		lv_mutulList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				// bitmapUtils.resume();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				// bitmapUtils.pause();
			}
		});

		dbUtils = DbUtils.create(getActivity());

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new MutualFriendPinyinComparator();

		sb_fs_sidebar.setTextView(tv_dialog);
		if (adapter != null) {
			// 设置右侧触摸监听
			sb_fs_sidebar
					.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

						@Override
						public void onTouchingLetterChanged(String s) {
							// 该字母首次出现的位置
							int position = adapter.getPositionForSection(s
									.charAt(0));
							if (position != -1) {
								lv_mutulList.setSelection(position);
							}

						}
					});
		}

		refresh.setOnHeaderRefreshListener(this);
		refresh.setOnFooterRefreshListener(this);
		lv_mutulList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (user_share.equals("share")) {
					Intent intent = new Intent(getActivity(),
							ChatActivity.class);
					intent.putExtra("uuid", SourceDateList.get(arg2).getUuid());
					intent.putExtra("share_content", share_content);
					intent.putExtra("type", 1);
					startActivity(intent);
					getActivity().finish();
				} else {

					if (SourceDateList
							.get(arg2)
							.getUuid()
							.equals(RsSharedUtil.getString(getActivity(),
									AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), MyProfileActivity.class);
						startActivity(intent);
					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid", SourceDateList.get(arg2)
								.getUuid());
						bundle.putString("userName",SourceDateList.get(arg2)
								.getUserName());
						bundle.putString("useLogo", SourceDateList.get(arg2)
								.getUserLogo());
						Intent intent = new Intent();
						intent.setClass(getActivity(),
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}

			}
		});
	}

	// 成功从网络获取
	private void init() {

		SourceDateList = filledData(allFriends);
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new FriendsAdapter(getActivity(), SourceDateList);
		lv_mutulList.setAdapter(adapter);
	}

	// 失败时从数据库获取
	private void initFromDb(List<LocalMutualStockFriend> localFriends) {
		SourceDateList.clear();
		SourceDateList = fillLocalData(localFriends);
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new FriendsAdapter(getActivity(), SourceDateList);
		lv_mutulList.setAdapter(adapter);
	}

	private List<LocalMutualStockFriend> fillLocalData(
			List<LocalMutualStockFriend> areaData) {
		List<LocalMutualStockFriend> mSortList = new ArrayList<LocalMutualStockFriend>();

		for (int i = 0; i < areaData.size(); i++) {
			LocalMutualStockFriend sortModel = new LocalMutualStockFriend();
			sortModel.setUserName(areaData.get(i).getUserName());
			// sortModel.setCoin(areaData.get(i).getCoin());
			sortModel.setIndustryName(areaData.get(i).getIndustryName());
			sortModel.setLocationName(areaData.get(i).getLocationName());
			sortModel.setType(areaData.get(i).getType());
			sortModel.setUserLogo(areaData.get(i).getUserLogo());
			sortModel.setUuid(areaData.get(i).getUuid());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(areaData.get(i)
					.getUserName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<LocalMutualStockFriend> filledData(
			List<HashMap<String, String>> areaData) {
		List<LocalMutualStockFriend> mSortList = new ArrayList<LocalMutualStockFriend>();

		for (int i = 0; i < areaData.size(); i++) {
			LocalMutualStockFriend sortModel = new LocalMutualStockFriend();
			sortModel.setUserName(areaData.get(i).get("userName"));
			// sortModel.setCoin(areaData.get(i).get("coin"));
			sortModel.setIndustryName(areaData.get(i).get("industryName"));
			sortModel.setLocationName(areaData.get(i).get("locationName"));
			sortModel.setType(areaData.get(i).get("type"));
			sortModel.setUserLogo(areaData.get(i).get("userLogo"));
			sortModel.setUuid(areaData.get(i).get("uuid"));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(areaData.get(i).get(
					"userName"));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	private List<LocalMutualStockFriend> friends = new ArrayList<LocalMutualStockFriend>();

	private void initData(int pageIndex, int pageSize, final int type) {

		String url = "";
		if (uuid.equals("")) {
			url = AppConfig.VERSION_URL + "user/follow/list.json?access_token=";
			url += RsSharedUtil
					.getString(getActivity(), AppConfig.ACCESS_TOKEN);
			url = url + "&type=MUTUAL&userUuid=myself&pageIndex=" + pageIndex
					+ "&pageSize=" + pageSize;
		} else {
			url = AppConfig.VERSION_URL + "user/follow/list.json?access_token=";
			url += RsSharedUtil
					.getString(getActivity(), AppConfig.ACCESS_TOKEN);
			url = url + "&type=MUTUAL&userUuid=" + uuid + "&pageIndex="
					+ pageIndex + "&pageSize=" + pageSize;
		}

		Log.d("MutualFriend_url", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("MutualFriend lists", response);
							if (response.equals("")) {
								ll_no_friendLayout.setVisibility(View.VISIBLE);
								refresh.setVisibility(View.GONE);
							} else {
								ll_no_friendLayout.setVisibility(View.GONE);
								refresh.setVisibility(View.VISIBLE);
								JSONObject jsonobject = new JSONObject(response);
								JSONArray jsonArray = jsonobject
										.getJSONArray("users");

								if (uuid.equals("")) {
									friends.clear();
									Mapper mapper = new Mapper();
									dbUtils.deleteAll(LocalMutualStockFriend.class);

									for (int i = 0; i < jsonArray.length(); i++) {
										LocalMutualStockFriend hottestComment = mapper
												.readValue(
														jsonArray.get(i)
																.toString(),
														LocalMutualStockFriend.class);
										dbUtils.saveOrUpdate(hottestComment);
										friends.add(hottestComment);
									}
								}

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

								if (type == FOOT)
									allFriends.addAll(datas);
								else {
									allFriends.clear();
									allFriends.addAll(datas);
								}

								init();
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
							if (uuid.equals("")) {
								friends.clear();
								friends = dbUtils
										.findAll(LocalMutualStockFriend.class);
								if (friends != null) {
									initFromDb(friends);

								}
								JSONObject jsonObject = new JSONObject(error
										.data());
								Log.d("error_description",
										jsonObject.getString("description"));
							}
						} catch (Exception e) {

						}

					}
				});

		MyApplication.getRequestQueue().add(stringRequest);
	}

	public class FriendsAdapter extends BaseAdapter implements SectionIndexer {

		private ViewHolder holder;

		private LayoutInflater mInflater;
		private List<LocalMutualStockFriend> listCode;

		FriendsAdapter(Context context, List<LocalMutualStockFriend> listCode) {

			this.listCode = listCode;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return listCode.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listCode.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
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
				holder.tvLetter = (TextView) contentView
						.findViewById(R.id.catalog);
				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			if (!uuid.equals("")) {

				holder.iv_guanzhu.setVisibility(View.GONE);
			}

			bitmapUtils.display(holder.ci_image, listCode.get(position)
					.getUserLogo());
			/*
			 * ImageLoader.getInstance().displayImage(
			 * listCode.get(position).getUserLogo(), holder.ci_image,
			 * defaultOptions);
			 */

			holder.tv_name.setText((CharSequence) listCode.get(position)
					.getUserName());
			String industy = (String) listCode.get(position).getIndustryName();
			String location = (String) listCode.get(position).getLocationName();
			/*
			 * holder.tv_industry.setText((CharSequence) listCode.get(position)
			 * .getIndustryName()); holder.tv_position.setText((CharSequence)
			 * listCode.get(position) .getLocationName());
			 */
			
			if (industy == null) {
				holder.tv_industry.setText("暂无");

			} else if (industy.equals("") || industy.equalsIgnoreCase("null")) {
				holder.tv_industry.setText("暂无");
			} else {
				holder.tv_industry.setText((CharSequence) listCode
						.get(position).getIndustryName());
			}

			if (location == null) {
				holder.tv_position.setText("暂无");


			} else if (location.equals("")
					|| location.equalsIgnoreCase("null")) {
				holder.tv_position.setText("暂无");
			} else {
				holder.tv_position.setText((CharSequence) listCode
						.get(position).getLocationName());
			}
			

/*			if (industy.equals("") || industy.equalsIgnoreCase("null")) {
				holder.tv_industry.setText("暂无");
			} else {
				holder.tv_industry.setText((CharSequence) listCode
						.get(position).getIndustryName());
			}

			if (location.equals("") || location.equalsIgnoreCase("null")) {
				holder.tv_position.setText("暂无");
			} else {
				holder.tv_position.setText((CharSequence) listCode
						.get(position).getLocationName());
			}*/
/*
			holder.ci_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Bundle bundle = new Bundle();
					bundle.putString("uuid", listCode.get(position).getUuid());
					bundle.putString("userName", listCode.get(position)
							.getUserName());
					bundle.putString("useLogo", listCode.get(position)
							.getUserLogo());
					bundle.putBoolean("isFriend", true);
					Intent intent = new Intent();
					intent.setClass(getActivity(),
							OtherPeolpeInformationActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});*/

			// 取消关注好友
			holder.iv_guanzhu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					final GeneralDialog dialog = new GeneralDialog(
							getActivity());
					dialog.setMessage("取消关注该股友吗?");
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

								progressDialog.show();
								cancelNotice(position);
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

			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter
						.setText(listCode.get(position).getSortLetters());
			} else {
				holder.tvLetter.setVisibility(View.GONE);
			}
			return contentView;
		}

		class ViewHolder {

			CircleImageView ci_image;
			TextView tv_name;
			TextView tv_industry;
			TextView tv_position;
			ImageView iv_guanzhu;

			TextView tvLetter;
		}

		// 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = (listCode.get(i)).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return listCode.get(position).getSortLetters().charAt(0);
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

		public void updateListView(ArrayList<HashMap<String, String>> list) {
			this.listCode = listCode;
			notifyDataSetChanged();
		}

		public void clear() {
			listCode.clear();
			notifyDataSetChanged();
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		all_index = 0;
		refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				initData(all_index, PAGE_SIZE, HEAD);
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
				initData(++all_index, PAGE_SIZE, FOOT);
				refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}

	private void cancelNotice(final int position) {
		String url = AppConfig.URL_USER + "follow.json?access_token="
				+ RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN)
				+ "&userUuid=" + SourceDateList.get(position).getUuid()
				+ "&type=CANCEL";
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						progressDialog.dismiss();
						Toast.makeText(getActivity(), "取消关注成功！",
								Toast.LENGTH_SHORT).show();
						try {

							dbUtils.deleteById(LocalMutualStockFriend.class,
									SourceDateList.get(position).getUuid());
							dbUtils.deleteById(LocalFollowStockFriend.class,
									SourceDateList.get(position).getUuid());

							dbUtils.deleteById(LocalFollowedStockFriend.class,
									SourceDateList.get(position).getUuid());
							// 将好友保存在被关注好友的数据库中
							LocalFollowedStockFriend mutualFriend = new LocalFollowedStockFriend();
							mutualFriend.setCoin(SourceDateList.get(position)
									.getCoin());
							mutualFriend.setIndustryCode(SourceDateList.get(
									position).getIndustryCode());
							mutualFriend.setIndustryName(SourceDateList.get(
									position).getIndustryName());
							mutualFriend.setLocationCode(SourceDateList.get(
									position).getLocationCode());
							mutualFriend.setIntroduction(SourceDateList.get(
									position).getLocationName());
							mutualFriend.setType("FOLLOWED");
							mutualFriend.setUserLogo(SourceDateList.get(
									position).getUuid());
							mutualFriend.setUserName(SourceDateList.get(
									position).getUserName());
							mutualFriend.setUuid(SourceDateList.get(position)
									.getUuid());
							mutualFriend.setSortLetters(SourceDateList.get(
									position).getSortLetters());
							dbUtils.save(mutualFriend);

						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Intent intent = new Intent();
						intent.setAction("cancle_follow_friend");
						getActivity().sendBroadcast(intent);

						SourceDateList.remove(position);
						adapter.notifyDataSetChanged();

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
		stringRequest.setTag("MutualActvity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

}
