package com.example.shareholders.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.fund.PublishDiscussActivity;
import com.example.shareholders.activity.survey.ReviewDetailsActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_FundDiscuss extends Fragment {

	// 评论列表
	@ViewInject(R.id.lv_dicuss)
	private MyListView lv_dicuss;
	// 按时间排序
	@ViewInject(R.id.tv_sort_in_time)
	private TextView tv_sort_in_time;
	// 按时间排序
	@ViewInject(R.id.tv_sort_in_heat)
	private TextView tv_sort_in_heat;
	
	private BitmapUtils bitmapUtils = null;

	// 初始化
	RequestQueue volleyRequestQueue;

	// list
	ArrayList<HashMap<String, Object>> list_discuss_time;
	ArrayList<HashMap<String, Object>> list_discuss_heat;
	// 评论列表的适配器
	private DicussAdapter dicussAdapter;

	// 基金代码
	private String symbol;

	// 当前选中的tab
	private int currentTab;
	// 广播接收器
	private MyReceiver myReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_fund_discuss, null);
		ViewUtils.inject(this, v);
		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		initView();
		return v;
	}

	private void initView() {
		// 注册广播
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.example.shareholders.fragment.RECEIVER");
		getActivity().registerReceiver(myReceiver, intentFilter);
		currentTab = 0;
		symbol = getActivity().getIntent().getStringExtra("symbol");
		list_discuss_time = new ArrayList<HashMap<String, Object>>();
		list_discuss_heat = new ArrayList<HashMap<String, Object>>();
		getDicuss(0, 0, 10);
	}

	private void ClearTab() {
		tv_sort_in_time.setTextColor(this.getResources()
				.getColor(R.color.black));
		tv_sort_in_heat.setTextColor(this.getResources()
				.getColor(R.color.black));
	}

	private void OnTab(int num) {

		switch (num) {
		case 0:
			if (currentTab != 0) {
				ClearTab();
				currentTab = 0;
				tv_sort_in_time.setTextColor(this.getResources().getColor(
						R.color.selected_cities_color));
				setHashMaps(currentTab);
			}
			break;
		case 1:
			if (currentTab != 1) {
				ClearTab();
				currentTab = 1;
				tv_sort_in_heat.setTextColor(this.getResources().getColor(
						R.color.selected_cities_color));
				setHashMaps(currentTab);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	@OnClick({ R.id.tv_sort_in_time, R.id.tv_sort_in_heat, R.id.iv_write_letter })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_sort_in_time:
			OnTab(0);
			break;
		case R.id.tv_sort_in_heat:
			OnTab(1);
			break;
		case R.id.iv_write_letter:
			// TODO
			Intent intent = new Intent(getActivity(),
					PublishDiscussActivity.class);
			intent.putExtra("securitySymbol", symbol);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}

	/**
	 * 1:本地先发一个评论，同时用service与后台对接
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("requestCode", requestCode + "");
		Log.d("resultCode", resultCode + "");
		// 如果是发布话题
		switch (resultCode) {
		case 1:// 刷新新话题
			OnTab(0);
			getDicuss(0, 0, 10);
			break;
		}
	}

	/**
	 * 获取评论
	 * 
	 * @param num
	 *            :0为按时间排序，1为按热度排序
	 * @param pageIndex
	 * @param pageSize
	 */
	private void getDicuss(final int num, int pageIndex, int pageSize) {
		String sortType = null;
		switch (num) {
		case 0:
			sortType = "NEWEST";
			break;
		case 1:
			sortType = "HOTTEST";
			break;

		default:
			break;
		}
		String url = AppConfig.URL_TOPIC + "list/security.json?access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token")
				+ "&sortType=" + sortType + "&symbol=" + symbol + "&pageIndex="
				+ pageIndex + "&pageSize=" + pageSize;
		Log.d("评论url", "url" + num + ":" + url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("评论" + num, response.toString());
						// 如果没有数据
						if (response.equals("") || response.equals("[0]")) {
							// Toast.makeText(getActivity(), "没有任何话题",
							//
							// Toast.LENGTH_SHORT).show();

						} else {
							try {
								JSONArray all = new JSONArray(response
										.toString());
								final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
								for (int i = 0; i < all.length(); i++) {
									HashMap<String, Object> data = new HashMap<String, Object>();
									Iterator<String> jsIterator;
									try {
										jsIterator = all.getJSONObject(i)
												.keys();
										while (jsIterator.hasNext()) {
											String key = jsIterator.next();
											data.put(key, all.getJSONObject(i)
													.get(key).toString());
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									datas.add(data);

								}
								switch (num) {
								case 0:
									list_discuss_time = datas;
									break;
								case 1:
									list_discuss_heat = datas;
								default:
									break;
								}

								setHashMaps(currentTab);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
				}

		);
		volleyRequestQueue.add(stringRequest);
	}

	class DicussAdapter extends BaseAdapter {

		public ArrayList<HashMap<String, Object>> list;
		public Context context;

		public DicussAdapter(Context context,
				ArrayList<HashMap<String, Object>> list_discuss) {
			this.list = list_discuss;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_fund_dicuss, parent, false);

			}

			// 头像
			CircleImageView ci_face = (CircleImageView) AbViewHolder.get(
					convertView, R.id.iv_head);
			bitmapUtils.display(ci_face,
					list.get(position).get("creatorLogoUrl").toString()
					);
			// 名字
			TextView tv_name = (TextView) AbViewHolder.get(convertView,
					R.id.tv_name);
			tv_name.setText(list.get(position).get("creatorName").toString());
			// 时间
			TextView tv_time = (TextView) AbViewHolder.get(convertView,
					R.id.tv_time);
			long Time = Long.parseLong(list.get(position).get("creationTime")
					.toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			String creationTime = dateFormat.format(new Date(Time));
			tv_time.setText(creationTime);
			// 点赞数目
			final TextView tv_dianzan_number = (TextView) AbViewHolder.get(
					convertView, R.id.tv_dianzan_number);
			tv_dianzan_number.setText(list.get(position).get("likeNum")
					.toString());
			// 点赞按钮
			final ImageView iv_dianzan = (ImageView) AbViewHolder.get(
					convertView, R.id.iv_dianzan);
			if (list.get(position).get("liked").toString().equals("false")) {
				iv_dianzan.setImageResource(R.drawable.btn_dianzanqian_sc);
			} else {
				iv_dianzan.setImageResource(R.drawable.btn_dianzanhou_sc);
			}
			iv_dianzan.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					praise(iv_dianzan, tv_dianzan_number, position, list);

				}
			});

			// 评论内容
			TextView tv_content = (TextView) AbViewHolder.get(convertView,
					R.id.tv_content);
			tv_content.setText(list.get(position).get("content").toString());
			return convertView;
		}

	}

	/**
	 * 设置hashmaps
	 * 
	 * @param position
	 *            0 : 全部 1 : 热评 2 : 股友 3 : 发起人
	 */
	public void setHashMaps(int position) {
		Log.d("position", "position:" + position);
		switch (position) {
		case 0:
			if (list_discuss_time.size() > 0) {
				try {
					dicussAdapter = new DicussAdapter(getActivity(),
							list_discuss_time);
					lv_dicuss.setAdapter(dicussAdapter);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case 1:
			if (list_discuss_heat.size() > 0) {
				try {
					dicussAdapter = new DicussAdapter(getActivity(),
							list_discuss_heat);
					lv_dicuss.setAdapter(dicussAdapter);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}

	/*
	 * 点赞功能
	 * 
	 * @param iv_praise
	 * 
	 * @param tv_praise_num
	 * 
	 * @param position
	 * 
	 * @param topics
	 */
	private void praise(final ImageView iv_praise,
			final TextView tv_praise_num, final int position,
			final List<HashMap<String, Object>> topics) {

		iv_praise.setClickable(false);
		int praiseNum = Integer.parseInt(tv_praise_num.getText().toString());
		praiseNum++;
		tv_praise_num.setText("" + praiseNum);
		iv_praise.setImageResource(R.drawable.btn_dianzanhou_sc);
		String uuid = topics.get(position).get("topicUuid").toString();
		String url = AppConfig.URL_TOPIC + "like.json?topicUuid=" + uuid;
		url = url + "&access_token="
				+ RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("点赞url", "url" + url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						topics.get(position).put("liked", true);
						iv_praise.setClickable(true);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						int praiseNum = Integer.parseInt(tv_praise_num
								.getText().toString());
						praiseNum--;
						tv_praise_num.setText("" + praiseNum);
						if (topics.get(position).get("liked").toString()
								.equals("true")) {
							iv_praise
									.setImageResource(R.drawable.btn_dianzanhou_sc);
						} else {
							iv_praise
									.setImageResource(R.drawable.btn_dianzanqian_sc);
						}
						iv_praise.setClickable(true);

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
		volleyRequestQueue.add(stringRequest);
	}

	/*
	 * 广播接收器 接受到发表成功的广播即刷新
	 */
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			int progress = intent.getIntExtra("progress", 0);
			if (progress == 1) {
				Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT)
						.show();
				getDicuss(0, 0, 10);
				dicussAdapter = new DicussAdapter(getActivity(),
						list_discuss_time);
				lv_dicuss.setAdapter(dicussAdapter);
			}
		}

	}

	@Override
	public void onDestroy() {
		// 注销广播
		getActivity().unregisterReceiver(myReceiver);
		super.onDestroy();
	}
}
