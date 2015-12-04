package com.example.shareholders.activity.survey;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.common.RoundRectImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_select)
public class SelectActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	// 显示搜索结果内容
	@ViewInject(R.id.lv_search_result)
	private MyListView lv_search_result;
	@ViewInject(R.id.main_pull_refresh_view)
	private PullToRefreshView mPullToRefreshView;

	@ViewInject(R.id.tv_search_number)
	private TextView tv_search_number;
	// 搜索框
	@ViewInject(R.id.et_search_content)
	private EditText et_search_content;
	// 搜索的内筒
	@ViewInject(R.id.tv_search_content)
	private TextView tv_search_content;
	// 搜索的内筒
	@ViewInject(R.id.iv_search)
	private TextView iv_search;
	private ArrayList<String> al_uuid = new ArrayList<String>();

	// 搜索的内容不为空时的布局
	@ViewInject(R.id.rl_search_title_no_content)
	private RelativeLayout rl_search_title_no_content;

	// 搜索的内容为空时的布局
	@ViewInject(R.id.ll_search_title_content)
	private LinearLayout ll_search_title_content;

	private HashMap<String, Object> hashMap;

	private AlertDialog mDialog = null;
	
	private BitmapUtils bitmapUtils=null;

	/**
	 * ListView内的数据（图片，标题，时间，关注人数，地点， 状态，如确认中，已过期）
	 */

	private List<Map<String, Object>> search_hashMap_lists;
	/*****************************************************/
	private List<String> heads;
	private List<String> titles;
	private List<String> b_dates;
	private List<String> a_dates;
	private List<Integer> follows;
	private List<String> states;
	private MyListViewAdapter adapter;
	private List<Map<String, Object>> survey_lists;
	/*****************************************************/
	// 搜索框内的内容
	private String search_content;
	private RequestQueue VolleyRequestQueue;
	private String test;
	private int pageIndex = 0;// 页码，用于下拉加载时递增,表明提交的是第几页
	private int totalElements;// 后台获取，报名的总数量
	private int pageLength = 50;// 一次获取的数据量
	private int totalPages;// 总页数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.huodongphoto);

		init();// 初始化数据

		lv_search_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (!BtnClickUtils.isFastDoubleClick()) {
					Intent intent = new Intent(SelectActivity.this,
							DetailSurveyActivity.class);
					intent.putExtra("uuid", al_uuid.get(arg2));
					startActivity(intent);
				}
			}

		});
	}

	/**
	 * 功能：初始化数据
	 * 
	 */

	/*
	 * 功能：初始化数据
	 */

	private void init() {
		/**
		 * 获取前面搜索的内容
		 */
		heads = new ArrayList<String>();
		titles = new ArrayList<String>();
		b_dates = new ArrayList<String>();
		a_dates = new ArrayList<String>();
		follows = new ArrayList<Integer>();
		states = new ArrayList<String>();

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		survey_lists = new ArrayList<Map<String, Object>>();

		Intent intent = getIntent();
		test = intent.getStringExtra("test");

		tv_search_content.setText(test);
		VolleyRequestQueue = Volley.newRequestQueue(getApplicationContext());

		getListDate(test, pageLength, 0, 0, pageLength);

	}

	// 功能：HashMap填充数据
	private void setSearch_hashMap_lists(int index, int total) {
		survey_lists.clear();
		for (int i = index; i < total; i++) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("head", heads.get(i));
			hashMap.put("title", titles.get(i));
			hashMap.put("tv_start_date", b_dates.get(i));
			hashMap.put("tv_end_date", a_dates.get(i));
			hashMap.put("follow_member", follows.get(i));
			hashMap.put("state", states.get(i));
			survey_lists.add(hashMap);
		}
		adapter = new MyListViewAdapter(getApplicationContext(), survey_lists);
		lv_search_result.setAdapter(adapter);
	}

	/**
	 * <p>
	 * Title: onHeaderRefresh
	 * </p>
	 * <p>
	 * Description: 下拉刷新
	 * </p>
	 * 
	 * @param view
	 * @see com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener#onHeaderRefresh(com.example.shareholders.common.PullToRefreshView)
	 */

	/**
	 * <p>
	 * Title: onHeaderRefresh
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param view
	 * @see com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener#onHeaderRefresh(com.example.shareholders.common.PullToRefreshView)
	 */

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		// mPullToRefreshView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// survey_lists.clear();
		// pageIndex = 0;
		// getListDate(test, pageLength, 0, 0, (pageIndex + 1)
		// * pageLength);
		//
		// mPullToRefreshView.onHeaderRefreshComplete();
		//
		// }
		//
		// }, 2000);

	}

	/**
	 * <p>
	 * Title: onFooterRefresh
	 * </p>
	 * <p>
	 * Description:上拉加载（时间为2000毫秒）
	 * </p>
	 * 
	 * @param view
	 * @see com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener#onFooterRefresh(com.example.shareholders.common.PullToRefreshView)
	 */

	/**
	 * <p>
	 * Title: onFooterRefresh
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param view
	 * @see com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener#onFooterRefresh(com.example.shareholders.common.PullToRefreshView)
	 */

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		// mPullToRefreshView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // 发请求提交的页码要小于后台的页码
		// if (pageIndex < totalPages) {
		// pageIndex++;
		//
		// if (pageIndex < totalPages - 1) {// 前面pageSize倍的项数
		// /**
		// * 参数说明：
		// *
		// * @param: @param test(搜索的内容)
		// * @param: @param pageSize（页的大小）
		// * @param: @param pageIndex（页码）
		// * @param: @param index（HashMap的初始值）
		// * @param: @param total（HashMap的总长度）
		// */
		// getListDate(test, pageLength, pageIndex, pageIndex
		// * pageLength, (pageIndex + 1) * pageLength);
		//
		// } else {// 最后多余的几项
		// getListDate(test, pageLength, pageIndex, pageIndex
		// * pageLength, totalElements);
		//
		// }
		// }
		//
		// mPullToRefreshView.onFooterRefreshComplete();
		// }
		// }, 2000);
	}

	/**
	 * 、
	 * 
	 * @Title: getListDate
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param: @param test(搜索的内容)
	 * @param: @param pageSize（页的大小）
	 * @param: @param pageIndex（页码）
	 * @param: @param index（HashMap的初始值）
	 * @param: @param total（HashMap的总长度）
	 * @return: void
	 * @throws
	 */
	public void getListDate(String test, int pageSize, int pageIndex,
			final int index, final int total) {
		Log.d("liang_list", "start");
		HttpUtils http = new HttpUtils();
		/*
		 * 名称： getListDate 功能：获取后台数据，添加在ListView
		 * 参数：text为编辑框的内容，pageSize为每一页的数量，pageIndex为页的页数 编写者：zgp 时间：2015.8.9
		 */

		String mark = RsSharedUtil.getString(getApplicationContext(),
				"access_token");
		String url = AppConfig.URL_SURVEY + "query.json?" + "access_token="
				+ mark + "&keyWord=" + test + "&pageSize=" + pageSize
				+ "&pageIndex=" + pageIndex;
		Log.d("liang_search_url", url);
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.d("liang_error", "no contetn");
						// Toast.makeText(getApplicationContext(), "网络错误",
						// Toast.LENGTH_SHORT);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");

						try {
							Log.d("result", arg0.result.toString());
							JSONObject jsonObject = new JSONObject(arg0.result
									.toString());
							JSONObject pageable = jsonObject
									.getJSONObject("pageable");
							totalPages = pageable.getInt("totalPages");
							totalElements = pageable.getInt("totalElements");

							JSONArray jsonArray = jsonObject
									.getJSONArray("surveys");

							Log.d("liang_size", jsonArray.length() + "");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject item = jsonArray.getJSONObject(i);
								String uuid = item.getString("uuid");
								String surveyName = item
										.getString("surveyName");
								String logo = item.getString("logo");
								long beginDate = item.getLong("beginDate");
								long endDate = item.getLong("endDate");
								int countFollow = item.getInt("countFollow");
								String state = item.getString("state");
								heads.add(logo);
								titles.add(surveyName);
								b_dates.add(dateFormat.format(new Date(
										beginDate)));
								a_dates.add(dateFormat
										.format(new Date(endDate)));
								follows.add(countFollow);
								states.add(state);
								al_uuid.add(uuid);

							}
							setSearch_hashMap_lists(index, jsonArray.length());

							rl_search_title_no_content.setVisibility(View.GONE);
							ll_search_title_content.setVisibility(View.VISIBLE);
							tv_search_number.setText("" + totalElements);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	@OnClick({ R.id.rl_return, R.id.iv_search, R.id.et_search_content })
	private void onClick(View v) {
		switch (v.getId()) {
		// 功能：返回
		case R.id.rl_return:
			if (!BtnClickUtils.isFastDoubleClick()) {
				finish();
			}
			break;
		case R.id.iv_search:
			if (!BtnClickUtils.isFastDoubleClick()) {

				/**
				 * 说明：列表清空，重新搜索加载
				 */

				survey_lists.clear();
				adapter = new MyListViewAdapter(getApplicationContext(),
						survey_lists);
				lv_search_result.setAdapter(adapter);
				getListDate(test, pageLength, 0, 0, pageLength);

			}

			break;
		case R.id.et_search_content:
			if (!BtnClickUtils.isFastDoubleClick()) {
				finish();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 后台数据传输，get方式
	 * 
	 */

	/*******************************************************/

	class MyListViewAdapter extends BaseAdapter {

		private List<Map<String, Object>> lists;
		private LayoutInflater inflater;

		public MyListViewAdapter(Context context,
				List<Map<String, Object>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
			mDialog = new AlertDialog.Builder(SelectActivity.this).create();
		}

		@Override
		public int getCount() {

			return lists.size();
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
		public View getView(final int position, View converView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			Log.d("state", lists.get(position).get("state").toString());
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = inflater.inflate(R.layout.item_servey_list, arg2,
						false);
				viewHolder.iv_head = (RoundRectImageView) converView
						.findViewById(R.id.iv_head);
				viewHolder.tv_title = (TextView) converView
						.findViewById(R.id.tv_title);
				viewHolder.tv_start_date = (TextView) converView
						.findViewById(R.id.tv_start_date);
				viewHolder.tv_end_date = (TextView) converView
						.findViewById(R.id.tv_end_date);
				viewHolder.tv_follow_member_number = (TextView) converView
						.findViewById(R.id.tv_follow_member_number);
				viewHolder.iv_state = (ImageView) converView
						.findViewById(R.id.iv_state);
				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}
			
			bitmapUtils.display(viewHolder.iv_head,
					lists.get(position).get("head").toString());
			/*ImageLoader.getInstance().displayImage(
					lists.get(position).get("head").toString(),
					viewHolder.iv_head);*/
			viewHolder.tv_title.setText((CharSequence) lists.get(position).get(
					"title"));
			viewHolder.tv_start_date.setText((CharSequence) lists.get(position)
					.get("tv_start_date"));
			viewHolder.tv_end_date.setText((CharSequence) lists.get(position)
					.get("tv_end_date"));
			viewHolder.tv_follow_member_number.setText(""
					+ lists.get(position).get("follow_member"));
			// viewHolder.iv_state.setText((CharSequence) lists.get(position)
			// .get("state"));

			// 活动状态
			final String state = lists.get(position).get("state").toString();

			// 设置活动状态
			setState(viewHolder, state);

			viewHolder.iv_state.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (state.equals("ENROLLING")) {// 未报名
						// 报名
						// ToastUtils.showToast(getActivity(), "123");
						enroll(position);
					} else if (state.equals("SUCCESS")) {// 已报名
						showDialog(getResources().getString(
								R.string.enroll_already));
					} else if (state.equals("FAILED")) {// 已满人
						showDialog(getString(R.string.enroll_full));
					} else if (state.equals("ENROLL")) {// 待审核
						showDialog(getResources().getString(
								R.string.enroll_under_audlt));
					}

				}

				private void enroll(final int position) {
					String url = AppConfig.URL_SURVEY
							+ "enroll.json?access_token=";
					url += RsSharedUtil.getString(SelectActivity.this,
							"access_token");
					url += "&surveyUuid=" + lists.get(position).get("uuid");

					// ToastUtils.showToast(getActivity(), "123");
					StringRequest stringRequest = new StringRequest(
							Request.Method.GET, url, null,
							new Response.Listener<String>() {

								@Override
								public void onResponse(String response) {
									lists.get(position).put("state", "ENROLL");
									showDialog(getResources().getString(
											R.string.enroll_under_audlt));
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub

								}
							});
				}

				/**
				 * 点击活动状态后弹出的对话框
				 */
				private void showDialog(String dialog_message) {
					mDialog.show();
					mDialog.setCancelable(false);
					mDialog.getWindow().setContentView(
							R.layout.dialog_survey_list2);

					// 修改提示信息
					TextView message = (TextView) mDialog.getWindow()
							.findViewById(R.id.tv_dialog_content);
					message.setText(dialog_message);

					// 点击确定
					mDialog.getWindow().findViewById(R.id.tv_confirm)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									mDialog.dismiss();
								}
							});

					// 点击取消
					mDialog.getWindow().findViewById(R.id.tv_cancel)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									mDialog.dismiss();
								}
							});
				}
			});

			return converView;
		}

		private void setState(ViewHolder viewHolder, String state) {
			if (state.equals("ENROLLING")) {// 未报名
				viewHolder.iv_state.setImageResource(R.drawable.btn_baoming);
			} else if (state.equals("SUCCESS")) {// 已报名
				viewHolder.iv_state.setImageResource(R.drawable.btn_yibaoming);
			} else if (state.equals("ENROLL")) {// 待审核
				viewHolder.iv_state.setImageResource(R.drawable.btn_daishenhe);
			} else if (state.equals("FAILED")) {// 已满人
				viewHolder.iv_state.setImageResource(R.drawable.btn_daishenhe);
			} else if (state.equals("SURVEYING")) {// 进行中
				viewHolder.iv_state
						.setImageResource(R.drawable.ico_jinxingzhong);
			} else if (state.equals("SURVEYEND")) {// 已结束
				viewHolder.iv_state.setImageResource(R.drawable.ico_yijieshu);
			}
		}

		class ViewHolder {

			RoundRectImageView iv_head;
			TextView tv_title;
			TextView tv_start_date;
			TextView tv_end_date;
			TextView tv_follow_member_number;
			ImageView iv_state;
		}

		/**
		 * 
		 * @ClassName: MyClickListener
		 * @Description:TODOd(点击函数)
		 * @author: Zgp
		 * @date: 2015-8-9 上午10:44:54
		 * 
		 */
		class MyClickListener implements OnClickListener {

			private int position;
			private TextView tv;
			private AlertDialog mDialog = null;

			public MyClickListener(TextView tv, int position) {
				this.tv = tv;
				this.position = position;
				mDialog = new AlertDialog.Builder(SelectActivity.this).create();
			}

			@Override
			public void onClick(View arg0) {
				if (!BtnClickUtils.isFastDoubleClick()) {
					if (!(tv.getText().equals("已报名") || tv.getText().equals(
							"报名"))) {
						return;
					}

					mDialog.show();
					mDialog.setCancelable(false);
					mDialog.getWindow().setContentView(
							R.layout.dialog_survey_list2);

					/**
					 * 已报名
					 */
					if (tv.getText().equals("已报名")) {

						((TextView) mDialog.getWindow().findViewById(
								R.id.tv_dialog_content))
								.setText(getResources().getString(
										R.string.survey_list_cancel_sign_up));
						mDialog.getWindow().findViewById(R.id.tv_confirm)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										lists.get(position).put("state", "报名");
										tv.setText("报名");
										tv.setBackgroundResource(R.drawable.bg_state_baoming_style);
										tv.setTextColor(getResources()
												.getColor(R.color.state_baoming));
										mDialog.dismiss();
									}
								});

						mDialog.getWindow().findViewById(R.id.tv_cancel)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										mDialog.dismiss();
									}
								});

					} else {

						((TextView) mDialog.getWindow().findViewById(
								R.id.tv_dialog_content))
								.setText(getResources().getString(
										R.string.survey_list_confirm_sign_up));
						mDialog.getWindow().findViewById(R.id.tv_confirm)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										if (!BtnClickUtils.isFastDoubleClick()) {
											lists.get(position).put("state",
													"已报名");
											tv.setText("已报名");
											tv.setBackgroundResource(R.drawable.bg_state_yibaoming_style);
											tv.setTextColor(getResources()
													.getColor(R.color.white));
											mDialog.dismiss();
										}
									}
								});

						mDialog.getWindow().findViewById(R.id.tv_cancel)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										if (!BtnClickUtils.isFastDoubleClick()) {
											mDialog.dismiss();
										}
									}
								});

					}
				}
			}
		}

	}

}
