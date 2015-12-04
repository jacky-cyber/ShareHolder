package com.example.shareholders.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.common.RoundRectImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class OtherSignedSurveyFragment extends Fragment implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	// 上下拉刷新
	@ViewInject(R.id.pull_to_refresh)
	private PullToRefreshView pull_to_refresh;

	@ViewInject(R.id.lv_survey)
	private MyListView lv_survey;

	@ViewInject(R.id.ll_wuhuodong)
	private LinearLayout ll_wuhuodong;

	private BitmapUtils bitmapUtils = null;

	private String userUuid = "";

	private int pageSize = 5;
	private int currentPage = 0;

	private static final int FIRST_PAGESIZE = 15; // 一开始加载12条数据
	private static final int ADDED_PAGESIZE = 5; // 上拉加载每次加载5条
	private static final int HEAD = 0;
	private static final int FOOT = 1;

	private ArrayList<HashMap<String, String>> all_surveys = new ArrayList<HashMap<String, String>>();
	private SurveyAdapter adapter = null;
	private AlertDialog mDialog = null;

	private AlertDialog internertDialog = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;

			// case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力
			//
			// if (internertDialog != null && internertDialog.isShowing()) {
			// internertDialog.dismiss();
			// showInternetDialog();
			// }
			//
			// break;
			//
			// case 3: // 提示网络异常的对话框消失
			// if (internertDialog != null && internertDialog.isShowing()) {
			// internertDialog.dismiss();
			// }
			// break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_other_signed_survey,
				container, false);
		ViewUtils.inject(this, view);

		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.huodongphoto);
		
		init();

		return view;
	}

	private void init() {
		userUuid = getActivity().getIntent().getExtras().getString("userUuid");
		Log.d("liang_userUuid", userUuid);

		adapter = new SurveyAdapter(getActivity(), all_surveys);
		lv_survey.setAdapter(adapter);

		pull_to_refresh.setOnHeaderRefreshListener(this);
		pull_to_refresh.setOnFooterRefreshListener(this);

		lv_survey.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int positon, long arg3) {
				Intent intent = new Intent(getActivity(),
						DetailSurveyActivity.class);

				Bundle bundle = new Bundle();
				bundle.putString("uuid", all_surveys.get(positon).get("uuid"));

				intent.putExtras(bundle);

				startActivity(intent);

			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		pageSize = FIRST_PAGESIZE; // 开始加载12条

		showLoadingDialog();
		getSurveyList(HEAD, true);

		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private void showLoadingDialog() {
		internertDialog = new AlertDialog.Builder(getActivity()).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// Message msg = new Message();
		// msg.what = 2;
		// mHandler.sendMessageDelayed(msg, 5000);
		// }
		// }).start();

	}

	/**
	 * 获取报名成功的调研活动列表 first表示是否是第一次获取，第一次获取15条 每次上拉加载5条
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @param type
	 * @param first
	 */
	public void getSurveyList(final int type, final boolean first) {
		String url = AppConfig.URL_USER + "survey/enroll.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url += "&state=SUCCESS&userUuid=" + userUuid + "&pageSize=" + pageSize
				+ "&pageIndex=" + currentPage;

		// Log.d("liang_success_survey", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// Log.d("liang_success_response", "success" +
						// response);
						if (response.equals("")) {// 如果数据为空，currentPage不变
							if (currentPage > 0) {
								currentPage--;
							}
						} else {
							try {
								JSONObject jsonObject = new JSONObject(response);

								JSONArray jsonArray = new JSONArray(jsonObject
										.getString("surveys"));
								ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
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

								if (first) {// 第一次获取12条，以后上拉加载每次获取5条
									// 第一次获取12条，计算当前的页数
									currentPage = (datas.size() - 1)
											/ ADDED_PAGESIZE;
									pageSize = ADDED_PAGESIZE;
								}

								if (type == HEAD) {// 下拉刷新
									all_surveys.clear();
									all_surveys.addAll(datas);
								} else {// 上拉加载更多
									all_surveys.addAll(datas);
								}

								// lv_survey.setAdapter(new SurveyAdapter(
								// getActivity(), all_surveys));

								adapter.notifyDataSetChanged();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Log.d("liang_Exception", e.toString());

							}

						}

						if (all_surveys.size() == 0) {
							ll_wuhuodong.setVisibility(View.VISIBLE);
						} else {
							ll_wuhuodong.setVisibility(View.GONE);
						}

						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject object = new JSONObject(error.data());

							Log.d("liang_description",
									object.getString("description"));

						} catch (JSONException e) {
							Log.d("liang_Exception", e.toString());
							e.printStackTrace();
						}

						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

						if (all_surveys.size() == 0) {
							ll_wuhuodong.setVisibility(View.VISIBLE);
						} else {
							ll_wuhuodong.setVisibility(View.GONE);
						}

					}
				});

		stringRequest.setTag("getSurveyList");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	class SurveyAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> lists;
		private LayoutInflater inflater;

		public SurveyAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;

			mDialog = new AlertDialog.Builder(getActivity()).create();
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

			// 头像
			bitmapUtils.display(viewHolder.iv_head,
					lists.get(position).get("logo"));

			// 名称
			viewHolder.tv_title.setText(lists.get(position).get("surveyName"));

			long begin = Long.parseLong(lists.get(position).get("beginDate"));
			long end = Long.parseLong(lists.get(position).get("endDate"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			String beginDate = dateFormat.format(new Date(begin));
			String endDate = dateFormat.format(new Date(end));

			// 开始时间
			viewHolder.tv_start_date.setText(beginDate);

			// 结束时间
			viewHolder.tv_end_date.setText(endDate);

			// 关注人数
			viewHolder.tv_follow_member_number.setText(lists.get(position).get(
					"countFollow"));

			// 活动状态
			final String state = lists.get(position).get("state");

			// 设置活动状态
			setState(viewHolder, state);

			viewHolder.iv_state.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (state.equals("ENROLLING")) {// 未报名
						enroll(lists.get(position).get("uuid"),
								(ImageView) arg0);

					} else if (state.equals("SUCCESS")) {// 已报名
						showDialog(getActivity().getResources().getString(
								R.string.enroll_already));
					} else if (state.equals("FAILED")) {// 已满人
						showDialog(getActivity().getResources().getString(
								R.string.enroll_full));
					} else if (state.equals("ENROLL")) {// 待审核
						showDialog(getActivity().getResources().getString(
								R.string.enroll_under_audlt));
					}

				}
			});
			return converView;
		}

		private void enroll(String uuid, final ImageView iv_state) {
			String url = AppConfig.URL_SURVEY + "enroll.json?access_token=";
			url += RsSharedUtil.getString(getActivity(), "access_token");
			url += "&surveyUuid=" + uuid;

			// Log.d("liang_url_enroll", url);
			StringRequest stringRequest = new StringRequest(Request.Method.GET,
					url, null, new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							showDialog(getActivity().getResources().getString(
									R.string.enroll_under_audlt));
							iv_state.setImageResource(R.drawable.btn_daishenhe);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							try {
								JSONObject jsonObject = new JSONObject(error
										.data());
								Log.d("error_description",
										jsonObject.getString("description"));
								Toast.makeText(getActivity(), "发起人不能对自己的活动报名",
										0).show();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								Log.d("error_Exception", e.toString());
							}

						}
					});

			stringRequest.setTag("enroll");
			MyApplication.getRequestQueue().add(stringRequest);
		}

		/**
		 * 点击活动状态后弹出的对话框
		 */
		private void showDialog(String dialog_message) {
			mDialog.show();
			mDialog.setCancelable(false);
			mDialog.getWindow().setContentView(R.layout.dialog_survey_list2);

			// 修改提示信息
			TextView message = (TextView) mDialog.getWindow().findViewById(
					R.id.tv_dialog_content);
			message.setText(dialog_message);

			// 点击确定
			mDialog.getWindow().findViewById(R.id.tv_confirm)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							mDialog.dismiss();
						}
					});
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

	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("getSurveyList");
		MyApplication.getRequestQueue().cancelAll("enroll");

		super.onDestroy();
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				currentPage++;
				pageSize = ADDED_PAGESIZE;
				getSurveyList(FOOT, false);

				pull_to_refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				pageSize = FIRST_PAGESIZE;
				currentPage = 0;
				getSurveyList(HEAD, true);
				pull_to_refresh.onHeaderRefreshComplete();
			}
		}, 2000);
	}

}
