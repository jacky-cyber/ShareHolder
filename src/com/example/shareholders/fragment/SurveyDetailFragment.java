package com.example.shareholders.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.VoidRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.ActivityCreateActivity;
import com.example.shareholders.activity.survey.InformActivity;
import com.example.shareholders.activity.survey.InformEditActivity;
import com.example.shareholders.activity.survey.UserJoinActivity;
import com.example.shareholders.common.DropEditText;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.EnterpriseEntity;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@SuppressLint("ResourceAsColor")
public class SurveyDetailFragment extends Fragment {
	private AlertDialog mDialog = null;
	private AlertDialog internetDialog = null;
	@ViewInject(R.id.rl_survey_name)
	private RelativeLayout rl_survey_name;
	// 报名
	@ViewInject(R.id.rl_sign)
	private RelativeLayout rl_sign;
	@ViewInject(R.id.iv_add)
	private ImageView iv_add;
	@ViewInject(R.id.tv_sign)
	private TextView tv_sign;
	@ViewInject(R.id.iv_collect)
	private ImageView iv_collect;
	// 收藏
	@ViewInject(R.id.tv_collect)
	private TextView tv_collect;
	// 现场互动
	@ViewInject(R.id.rl_easy_touch)
	private RelativeLayout rl_easy_touch;
	// 初始化
	RequestQueue volleyRequestQueue;
	private String uuid;
	@ViewInject(R.id.fl_mail)
	private FrameLayout fl_mail;
	// 调研名称
	@ViewInject(R.id.tv_survey_name)
	private TextView tv_survey_name;
	// 发起人
	@ViewInject(R.id.tv_name)
	private TextView tv_name;
	// 浏览量
	@ViewInject(R.id.tv_follow_num)
	private TextView tv_follow_num;
	// 关注量
	@ViewInject(R.id.tv_collection_num)
	private TextView tv_collection_num;
	// 报名人数
	@ViewInject(R.id.tv_registration_num)
	private TextView tv_registration_num;
	// 活动时间
	@ViewInject(R.id.tv_schedule_content)
	private TextView tv_schedule_content;
	// 联系人
	@ViewInject(R.id.tv_link_man_content)
	private TextView tv_link_man_content;
	// 联系人
	@ViewInject(R.id.tv_phone_content)
	private TextView tv_phone_content;
	// 备注
	@ViewInject(R.id.tv_detail1)
	private TextView tv_detail1;
	// 调研说明
	@ViewInject(R.id.tv_text)
	private TextView tv_text;
	// 调研状态
	@ViewInject(R.id.iv_state)
	private ImageView iv_state;
	// 通知信息量
	@ViewInject(R.id.tv_unread)
	private TextView tv_unread;
	// 是否收藏
	private boolean isFollow;
	// 状态
	private boolean status[];
	// 是否为发起人
	private boolean isOriginator = false;
	// content是否扩展
	private boolean expended = false;
	private int i;
	@ViewInject(R.id.mv_enterprises)
	private MyListView mv_enterprises;
	@ViewInject(R.id.tv_place_content)
	private TextView tv_place_content;
	private String messageCount;// 未读的数量
	private boolean isOrigin = false;// 判断是否为发起人
	private AlertDialog myDialog = null;// 弹窗
	@ViewInject(R.id.iv_drop_up_down)
	private ImageView iv_drop_up_down;
	// 头像
	@ViewInject(R.id.iv_face)
	private ImageView iv_face;
	// 调研图片
	@ViewInject(R.id.rl_banner)
	private View rl_banner;
	private BitmapUtils bitmapUtils = null;
	private ProgressDialog progress;
	// 报名状态
	private String state;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View mView = inflater.inflate(R.layout.fragment_survey_detail,
				container, false);
		ViewUtils.inject(this, mView);
		// progress = new ProgressDialog(getActivity());
		// progress.setMessage("正在拼命为你加载中...");
		// progress.setCanceledOnTouchOutside(false);
		// progress.setCancelable(false);
		// progress.show();
		showLoadingDialog();

		return mView;

	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private void showLoadingDialog() {
		// 如果网络不可用，加载的提示不显示
		if (!NetWorkCheck.isNetworkConnected(getActivity())) {
			return;
		}
		internetDialog = new AlertDialog.Builder(getActivity()).create();
		internetDialog.show();
		internetDialog.setCancelable(false);

		Window window = internetDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	}

	/**
	 * 异步加载数据
	 * 
	 * @author Administrator
	 * 
	 */
	private class AsyncDetail extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {

		};

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// 获取数据
			Detail(arg0[0]);
			return null;
		}

		@Override
		protected void onProgressUpdate(Void[] values) {

		};

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}

	@Override
	public void onResume() {
		bitmapUtils = BitmapUtilFactory.getInstance();
		Detail(uuid);
		super.onResume();
	};

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle = getActivity().getIntent().getExtras();
		uuid = bundle.getString("uuid");
		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		bitmapUtils = BitmapUtilFactory.getInstance();
		if (tv_text.getLineCount() <= 3)
			iv_drop_up_down.setVisibility(View.GONE);
		// 获取调研详情数据
		new AsyncDetail().execute(uuid);
		super.onActivityCreated(savedInstanceState);
	}

	// 调研公司
	private class EnterPriseAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> enterprises;
		private int currentPosition = -1;

		public EnterPriseAdapter(Context context,
				ArrayList<HashMap<String, String>> enterprises) {
			this.context = context;
			this.enterprises = enterprises;
			inflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return enterprises.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				contentView = inflater.inflate(R.layout.enterprise_item, null);
			}
			RelativeLayout rl_detail = AbViewHolder.get(contentView,
					R.id.rl_detail);
			// 企业名称
			TextView tv_enterprise_name = AbViewHolder.get(contentView,
					R.id.tv_enterprise_name);
			tv_enterprise_name.setText(enterprises.get(position)
					.get("shortName").toString());
			// 简介
			TextView tv_detail = AbViewHolder.get(contentView, R.id.tv_detail);

			tv_detail.setText(enterprises.get(position).get("content")
					.toString());
			if (position == currentPosition) {
				rl_detail.setVisibility(View.VISIBLE);
			} else {
				rl_detail.setVisibility(View.GONE);
			}
			// 地点
			TextView tv_place = AbViewHolder.get(contentView, R.id.tv_place);
			tv_place.setText(enterprises.get(position).get("locationName")
					.toString());
			// 被调研人职务
			TextView tv_stake_holder_name = AbViewHolder.get(contentView,
					R.id.tv_stake_holder_name);
			tv_stake_holder_name.setText(enterprises.get(position)
					.get("receiverpost").toString());
			// 起始日期
			TextView tv_begin_date = AbViewHolder.get(contentView,
					R.id.tv_begin_date);

			// 结束日期
			TextView tv_end_date = AbViewHolder.get(contentView,
					R.id.tv_end_date);
			long enterprisesbegin = Long.parseLong(enterprises.get(position)
					.get("beginDate").toString());
			long enterprisesend = Long.parseLong(enterprises.get(position)
					.get("endDate").toString());
			SimpleDateFormat enterprisesdateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			// 开始日期
			String enterprisesbeginDate = dateFormat.format(new Date(
					enterprisesbegin));
			enterprisesbeginDate = enterprisesbeginDate.substring(5);
			// 结束日期
			String enterprisesendDate = dateFormat.format(new Date(
					enterprisesend));
			enterprisesendDate = enterprisesendDate.substring(5);
			tv_begin_date.setText(enterprisesbeginDate);
			tv_end_date.setText(enterprisesendDate);
			// 提纲
			RelativeLayout rl_outline = AbViewHolder.get(contentView,
					R.id.rl_outline);
			// 图片向上向下
			final ImageView iv_state = AbViewHolder.get(contentView,
					R.id.iv_state);
			// 点击事件
			rl_outline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (position == currentPosition) {
						currentPosition = -1;
						iv_state.setImageResource(R.drawable.ico_xiala);
					} else {
						currentPosition = position;
						iv_state.setImageResource(R.drawable.ico_shangla);
					}
					EnterPriseAdapter.this.notifyDataSetChanged();
				}
			});
			return contentView;
		}

	}

	/*
	 * 功能：3秒后对话框退出,标志位为0x123
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (myDialog.isShowing()) {
					myDialog.dismiss();
				}
			}
			if (msg.what == 0x124) {
				if (internetDialog.isShowing() && internetDialog != null) {
					internetDialog.dismiss();
				}
			}
		};
	};

	/**
	 * 关注和取消关注
	 * 
	 * @param uuid
	 */
	private void Follow(String uuid) {
		String url = AppConfig.URL_USER + "survey.json?access_token=";
		url = url + RsSharedUtil.getString(getActivity(), "access_token");
		Log.d("关注/取消关注", url);
		JSONObject params = new JSONObject();
		try {
			params.put("follow", !isFollow);
			params.put("surveyUuid", uuid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VoidRequest jsonObjectRequest = new VoidRequest(Request.Method.POST,
				url, params, new Response.Listener<Void>() {

					@Override
					public void onResponse(Void response) {
						// TODO Auto-generated method stub
						myDialog = new AlertDialog.Builder(getActivity())
								.create();
						myDialog.show();
						handler.sendEmptyMessageDelayed(0x123, 3000);
						myDialog.setCancelable(false);
						myDialog.getWindow().setContentView(
								R.layout.item_toast_popup);
						TextView tv_item = (TextView) myDialog.getWindow()
								.findViewById(R.id.tv_item);
						if (!isFollow) {
							tv_item.setText("已收藏");
							tv_collect.setText(getResources().getString(
									R.string.followed));
						} else {
							tv_item.setText("取消收藏成功");
							tv_collect.setText(getResources().getString(
									R.string.unfollow));
						}
						isFollow = !isFollow;

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("关注/取消关注", error.toString());

						// TODO Auto-generated method stub
						try {
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							if (!isFollow) {
								iv_collect
										.setImageResource(R.drawable.ico_shoucang);
								tv_collect.setText(getResources().getString(
										R.string.unfollow));
							} else {
								iv_collect
										.setImageResource(R.drawable.btn_yijing);
								tv_collect.setText(getResources().getString(
										R.string.followed));
							}

						} catch (Exception e) {

						}
					}
				}

		);
		// 4.请求对象放入请求队列
		volleyRequestQueue.add(jsonObjectRequest);

	}

	/**
	 * 报名参加调研
	 * 
	 * @param uuid
	 */
	@SuppressLint("ResourceAsColor")
	private void Enroll(String uuid) {
		String url = AppConfig.URL_SURVEY + "enroll.json?access_token=";
		url = url + RsSharedUtil.getString(getActivity(), "access_token")
				+ "&surveyUuid=" + uuid;
		VoidRequest jsonObjectRequest = new VoidRequest(Request.Method.GET,
				url, null, new Response.Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						// TODO Auto-generated method stub
						iv_add.setImageResource(R.drawable.btn_yijing);
						tv_sign.setTextColor(R.color.remain_to_review);
						tv_sign.setText("待审核");
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
		// 4.请求对象放入请求队列
		volleyRequestQueue.add(jsonObjectRequest);

	}

	/**
	 * 取消报名
	 */
	private void cancelEnroll(String uuid) {
		String url = AppConfig.URL_SURVEY + "enroll/cancel.json?access_token=";
		url = url + RsSharedUtil.getString(getActivity(), "access_token")
				+ "&surveyUuid=" + uuid;
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.d("取消报名", response.toString());

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
		// 4.请求对象放入请求队列
		volleyRequestQueue.add(jsonObjectRequest);
	}

	/**
	 * 把数据写到数据库和sharepreferences
	 * 
	 * @param response
	 * @throws JSONException
	 */
	private void WriteToDB(JSONObject response) throws JSONException {
		JSONObject jsonObject;
		jsonObject = new JSONObject(response.toString());
		// logo
		RsSharedUtil.putString(getActivity(), "logo",
				jsonObject.getString("logo"));
		// 联系电话
		RsSharedUtil.putString(getActivity(), "contactPhone",
				jsonObject.getString("contactPhone"));
		// 备注
		RsSharedUtil.putString(getActivity(), "noted",
				jsonObject.getString("noted"));
		// 状态
		RsSharedUtil.putString(getActivity(), "state",
				jsonObject.getString("state"));
		// 调研名称
		RsSharedUtil.putString(getActivity(), "surveyName",
				jsonObject.getString("surveyName"));
		// 日期
		long begin = Long.parseLong(response.getString("beginDate").toString());
		long end = Long.parseLong(response.getString("endDate").toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// 开始日期
		String beginDate = dateFormat.format(new Date(begin));
		beginDate = beginDate.substring(5);
		// 结束日期
		String endDate = dateFormat.format(new Date(end));
		endDate = endDate.substring(5);
		RsSharedUtil.putString(getActivity(), "endDate", endDate);
		// 发起人图标
		RsSharedUtil.putString(getActivity(), "originatorLogo",
				jsonObject.getString("originatorLogo"));
		// 内容
		RsSharedUtil.putString(getActivity(), "content",
				jsonObject.getString("content"));
		// 联系人
		RsSharedUtil.putString(getActivity(), "contact",
				jsonObject.getString("contact"));
		// 发起人
		RsSharedUtil.putString(getActivity(), "originator",
				jsonObject.getString("originator"));
		// 将调研公司放到数据库里面
		JSONArray jsonArray = new JSONArray(
				jsonObject.getString("securityResponses"));
		if (jsonArray.length() > 0) {
			DbUtils db = DbUtils.create(getActivity());
			// 先删除数据库的所有公司
			try {
				db.deleteAll(EnterpriseEntity.class);
			} catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("!!!!!!!!!!!", jsonArray.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsObject2 = jsonArray.getJSONObject(i);
				EnterpriseEntity enterprise = new EnterpriseEntity();
				// 内容
				enterprise
						.setContent(jsObject2.getString("content").toString());
				long begin1 = Long.parseLong(jsObject2.getString("beginDate")
						.toString());
				long end1 = Long.parseLong(jsObject2.getString("endDate")
						.toString());
				SimpleDateFormat dateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd");
				// 开始日期
				String beginDate1 = dateFormat.format(new Date(begin));
				// 结束日期
				String endDate1 = dateFormat.format(new Date(end));
				enterprise.setBeginDate(beginDate1);
				enterprise.setEndDate(endDate1);
				// 地点
				enterprise.setLocationName(jsObject2.getString("locationName")
						.toString());
				// 公司简称
				enterprise.setShortName(jsObject2.getString("shortName")
						.toString());
				// 股票代码
				enterprise.setSymbol(jsObject2.getString("symbol").toString());
				// 职位
				enterprise.setReceicerpost(jsObject2.getString("receiverpost")
						.toString());
				// 地点代码
				enterprise.setLocationCode(jsObject2.getString("locationCode")
						.toString());
				// 行业代码
				enterprise.setIndustryCode(jsObject2.getString("industryCode")
						.toString());
				// uuid
				enterprise.setUuid(jsObject2.getString("uuid").toString());
				// 数据类型
				enterprise.setType(jsObject2.getString("type").toString());
				// 写入数据库
				try {
					db.save(enterprise);
				} catch (DbException e) {
					Log.d("数据库", "错误无误");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			db.close();
		}
	}

	/**
	 * 把数据显示在UI上
	 * 
	 * @param response
	 * @throws JSONException
	 */
	private void WriteOnUI(JSONObject response) throws JSONException {
		// 调研图像
		bitmapUtils
				.display(rl_banner, response.getString("picture").toString());
		Log.d("picture", response.getString("picture").toString());
		// 头像
		bitmapUtils.display(iv_face, response.getString("originatorLogo")
				.toString());
		Log.d("originatorLogo", response.getString("originatorLogo").toString());
		// 活动地点
		tv_place_content.setText(response.getString("surveyLocation")
				.toString());
		// 是否为发起人
		isOriginator = Boolean.parseBoolean(response.getString("isOriginator")
				.toString());
		if (isOriginator) {
			tv_unread.setVisibility(View.GONE);
			iv_add.setImageResource(R.drawable.ico_fatongzhi);
			tv_sign.setText(getActivity().getResources().getString(
					R.string.edit_Activity));
			iv_collect.setImageResource(R.drawable.ico_bianjihuodong);
			tv_collect.setText(getActivity().getResources().getString(
					R.string.notice));
			// 是发起人
			fl_mail.setVisibility(View.VISIBLE);
			isOrigin = true;
		} else {
			// 不是发起人
			isOrigin = false;
			// 是否已经收藏
			boolean hasCollected = Boolean.parseBoolean(response.getString(
					"isFollow").toString());
			isFollow = hasCollected;
			if (hasCollected) {
				iv_collect.setImageResource(R.drawable.btn_yijing);
				tv_collect.setText("已收藏");
			} else {
				iv_collect.setImageResource(R.drawable.ico_shoucang);
				tv_collect.setText("收藏");
			}
			// 是否报名
			state = response.getString("enrollState").toString();
			Log.d("sign_state", state);
			// 没报名成功没邮件显示
			setSigned(state);
			if (state.equals("SUCCESS")) {
				fl_mail.setVisibility(View.VISIBLE);
			} else {
				fl_mail.setVisibility(View.GONE);
			}
			// 通知信息数
			messageCount = response.getString("messageCount").toString();

			tv_unread.setText(response.getString("messageCount").toString());

		}
		rl_survey_name.setVisibility(View.VISIBLE);

		// 状态
		String state = response.getString("state").toString();
		if (state.equals("ENROLLING")) {
			iv_state.setImageResource(R.drawable.btn_1);
		} else if (state.equals("ENROLLEND")) {
			iv_state.setImageResource(R.drawable.btn_2);
		} else if (state.equals("SURVEYING")) {
			rl_easy_touch.setBackgroundColor(getActivity().getResources()
					.getColor(R.color.activity_starting));
			iv_state.setImageResource(R.drawable.btn_3);
		} else if (state.equals("SURVEYEND")) {
			iv_state.setImageResource(R.drawable.btn_4);
		}
		// 调研说明
		tv_text.setText(response.getString("content").toString());
		// 备注
		if (response.getString("noted") == null
				|| response.getString("noted").trim().equals("")) {
			tv_detail1.setText("暂无任何其他说明");
			tv_detail1.setTextColor(this.getResources().getColor(
					R.color.detail_line));
		} else {
			tv_detail1.setText(response.getString("noted").toString());
		}
		// 电话号码
		tv_phone_content.setText(response.getString("contactPhone").toString());
		// 联系人
		tv_link_man_content.setText(response.getString("contact").toString());
		// 调研名称
		tv_survey_name.setText(response.getString("surveyName").toString());
		// 发起人
		tv_name.setText(response.getString("originator").toString());
		// 关注人数
		tv_follow_num.setText(response.getString("readNum").toString());
		// 收藏人数
		tv_collection_num.setText(response.getString("followCount").toString());
		// 报名人数
		tv_registration_num.setText(response.getString("enrollCount")
				.toString());
		long begin = Long.parseLong(response.getString("beginDate").toString());
		long end = Long.parseLong(response.getString("endDate").toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// 开始日期
		String beginDate = dateFormat.format(new Date(begin));
		beginDate = beginDate.substring(5);
		// 结束日期
		String endDate = dateFormat.format(new Date(end));
		endDate = endDate.substring(5);
		// 活动时间
		tv_schedule_content.setText(beginDate + "----" + endDate);
		// 调研公司
		JSONArray securityResponses = new JSONArray(response.getString(
				"securityResponses").toString());
		ArrayList<HashMap<String, String>> enterprises = new ArrayList<HashMap<String, String>>();
		status = new boolean[securityResponses.length()];
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		View view[] = new View[securityResponses.length()];
		final ImageView imageView[] = new ImageView[securityResponses.length()];
		final RelativeLayout relativeLayout[] = new RelativeLayout[securityResponses
				.length()];
		RelativeLayout rl_survey_enterprise[] = new RelativeLayout[securityResponses
				.length()];
		for (int i = 0; i < securityResponses.length(); i++) {
			status[i] = false;
			HashMap<String, String> data = new HashMap<String, String>();
			Iterator<String> jsIterator;
			jsIterator = securityResponses.getJSONObject(i).keys();
			while (jsIterator.hasNext()) {
				String key = jsIterator.next();
				data.put(key, securityResponses.getJSONObject(i).get(key)
						.toString());
			}
			enterprises.add(data);
		}
		EnterPriseAdapter enterPriseAdapter = new EnterPriseAdapter(
				getActivity(), enterprises);
		mv_enterprises.setAdapter(enterPriseAdapter);
	}

	/**
	 * 获取调研详情
	 * 
	 * @param uuid
	 */
	private void Detail(String uuid) {
		// TODO Auto-generated method stub
		String url = AppConfig.URL_SURVEY + "detail.json?access_token=";
		url = url + RsSharedUtil.getString(getActivity(), "access_token")
				+ "&uuid=" + uuid;
		Log.d("调研详情url", url);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.d("调研详情", response.toString());
						try {
							// 把数据写在UI控件
							WriteOnUI(response);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// Toast.makeText(getActivity(), "网络错误", 0).show();
							Log.d("error", e.toString());
						}
						// 若果是发起人
						if (isOriginator) {
							JSONObject jsonObject;
							try {
								// 把数据写到数据库和sharePreference
								WriteToDB(response);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						handler.sendEmptyMessageDelayed(0x124, 2000);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("调研详情", error.toString());
						handler.sendEmptyMessageDelayed(0x124, 2000);
						// TODO Auto-generated method stub
						try {
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {
						}
					}
				}

		);
		// 4.请求对象放入请求队列
		volleyRequestQueue.add(jsonObjectRequest);

	}

	private void setSigned(String state) {
		if (state.equals("CANCELED")) {
			iv_add.setImageResource(R.drawable.ico_baomingjiahao);
			tv_sign.setText(getResources().getString(R.string.sign_up));

		}
		if (state.equals("ENROLL")) {
			iv_add.setImageResource(R.drawable.btn_yijing);
			tv_sign.setTextColor(R.color.remain_to_review);
			tv_sign.setText("待审核");

		}
		if (state.equals("SUCCESS")) {
			iv_add.setImageResource(R.drawable.btn_yijing);
			tv_sign.setTextColor(R.color.white);
			tv_sign.setText("已报名");
		}
		if (state.equals("FAILED")) {
			iv_add.setImageResource(R.drawable.ico_baomingjiahao);
			tv_sign.setTextColor(R.color.sign_fail);
			tv_sign.setText(getResources().getString(R.string.sign_up));
		}
	}

	@OnClick({ R.id.fl_mail, R.id.tv_registration_list, R.id.rl_sign,
			R.id.rl_collect, R.id.rl_easy_touch, R.id.iv_drop_up_down })
	private void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("uuid", uuid);
		intent.putExtras(bundle);
		switch (v.getId()) {
		// content下拉扩展
		case R.id.iv_drop_up_down:
			if (expended == false) {
				DropEditText.expandTextView(tv_text);
				iv_drop_up_down.setImageResource(R.drawable.btn_xiala_selected);
				expended = true;
			} else {
				DropEditText.expandTextView(tv_text);
				iv_drop_up_down.setImageResource(R.drawable.btn_xiala_normal);
				expended = false;
			}
			break;
		// 查看邮件
		case R.id.fl_mail:
			bundle.putString("messageCount", messageCount);
			bundle.putBoolean("isOrigin", isOrigin);
			intent.setClass(getActivity(), InformActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1);
			break;
		// 报名列表
		case R.id.tv_registration_list:
			intent.setClass(getActivity(), UserJoinActivity.class);
			startActivity(intent);
			break;
		// 报名
		case R.id.rl_sign:
			// 如果不是发起人就报名，是发起人就编辑活动
			if (!isOriginator) {
				SignUp();
				if (state.equals("CANCELED"))
					Enroll(uuid);
			} else {
				intent.setClass(getActivity(), ActivityCreateActivity.class);
				intent.putExtra("sign", 1);
				startActivity(intent);
			}
			break;
		// 收藏
		case R.id.rl_collect:
			if (!BtnClickUtils.isFastDoubleClick()) {
				// 如果不是发起人就收藏，是发起人就发通知
				if (!isOriginator) {
					if (!isFollow) {
						iv_collect.setImageResource(R.drawable.btn_yijing);
						tv_collect.setText(getResources().getString(
								R.string.followed));
					} else {
						iv_collect.setImageResource(R.drawable.ico_shoucang);
						tv_collect.setText(getResources().getString(
								R.string.followed));
					}
					Follow(uuid);
				} else {
					intent.setClass(getActivity(), InformEditActivity.class);
					startActivity(intent);
				}
			}
			break;
		case R.id.rl_easy_touch:
			EMChatManager.getInstance().login("123456", "123456",
					new EMCallBack() {// 回调
						@Override
						public void onSuccess() {
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									Log.d("main", "登陆聊天服务器成功！");
								}
							});
						}

						@Override
						public void onProgress(int progress, String status) {

						}

						@Override
						public void onError(int code, String message) {
							Log.d("main", message);
						}
					});
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 1) {
			tv_unread.setVisibility(View.GONE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void SignUp() {
		String tips = null;
		// 未报名
		if (state.equals("CANCELED")) {
			tips = "您的报名已转交给发起人，请耐心等待答复";
		}
		// 待审核
		if (state.equals("ENROLL")) {
			tips = "您的报名已转交给发起人，请耐心等待答复";
		}
		// 报名成功
		if (state.equals("SUCCESS")) {
			tips = "您已报名，欢迎您来参加活动";
		}
		// 满员
		if (state.equals("FAILED")) {
			tips = "活动已满员，欢迎您通过现场互动、发起人评论实时了解更多活动信息";
		}
		mDialog = new AlertDialog.Builder(getActivity()).create();
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_survey_list2);
		((TextView) mDialog.getWindow().findViewById(R.id.tv_dialog_content))
				.setText(tips);
		mDialog.getWindow().findViewById(R.id.tv_confirm)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
				});
	}
}
