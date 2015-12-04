package com.example.shareholders.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.RoundRectImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SurveyAdapter extends BaseAdapter {

/*	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.huodongphoto)
			.showImageOnFail(R.drawable.huodongphoto).cacheInMemory(true)
			.cacheOnDisc(true).build();*/
	
	private BitmapUtils bitmapUtils=null;

	AlertDialog mDialog;

	private ArrayList<HashMap<String, String>> lists;
	private LayoutInflater inflater;
	private Context context;

	public SurveyAdapter(Context context,
			ArrayList<HashMap<String, String>> lists) {
		this.context = context;
		
		bitmapUtils=new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.huodongphoto);
		
		inflater = LayoutInflater.from(context);
		this.lists = lists;

		mDialog = new AlertDialog.Builder(context).create();
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

		bitmapUtils.display(viewHolder.iv_head,
				lists.get(position).get("logo"));
		/*ImageLoader.getInstance().displayImage(lists.get(position).get("logo"),
				viewHolder.iv_head, defaultOptions);*/
		// 头像
		/*
		 * BitmapUtilFactory.getInstance().display(viewHolder.iv_head,
		 * lists.get(position).get("logo"));
		 */

		// 名称
		viewHolder.tv_title.setText(lists.get(position).get("surveyName"));

		long begin = Long.parseLong(lists.get(position).get("beginDate"));
		long end = Long.parseLong(lists.get(position).get("endDate"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String beginDate = transformTimeToDot(dateFormat
				.format(new Date(begin)));
		String endDate = transformTimeToDot(dateFormat.format(new Date(end)));

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
					enroll(lists.get(position).get("uuid"), (ImageView) arg0);

				} else if (state.equals("SUCCESS")) {// 已报名
					InternetDialog internetDialog = new InternetDialog(context);
					internetDialog.showInternetDialog(context.getResources()
							.getString(R.string.enroll_already), true);
				} else if (state.equals("FAILED")) {// 已满人
					InternetDialog internetDialog = new InternetDialog(context);
					internetDialog.showInternetDialog(context.getResources()
							.getString(R.string.enroll_full), false);
				} else if (state.equals("ENROLL")) {// 待审核
					InternetDialog internetDialog = new InternetDialog(context);
					internetDialog.showInternetDialog(context.getResources()
							.getString(R.string.enroll_under_audlt), true);
				}

			}
		});
		return converView;
	}

	private void enroll(String uuid, final ImageView iv_state) {
		String url = AppConfig.URL_SURVEY + "enroll.json?access_token=";
		url += RsSharedUtil.getString(context, "access_token");
		url += "&surveyUuid=" + uuid;

		Log.d("liang_url_enroll", url);
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						InternetDialog internetDialog = new InternetDialog(
								context);
						internetDialog.showInternetDialog(
								context.getResources().getString(
										R.string.enroll_under_audlt), true);
						iv_state.setImageResource(R.drawable.btn_daishenhe);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							// Toast.makeText(context, "发起人不能对自己的活动报名",
							// 0).show();
							InternetDialog internetDialog = new InternetDialog(
									context);
							internetDialog.showInternetDialog(
									jsonObject.getString("description"), false);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}

					}
				});
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
			viewHolder.iv_state.setImageResource(R.drawable.ico_jinxingzhong);
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
	 * 讲yyyy-mm-dd转换为yyyy.mm.dd
	 * 
	 * @param time
	 * @return
	 */
	private String transformTimeToDot(String time) {
		int begin = 0;
		int end = 0;

		String returnTime = "";
		for (int i = 0; i < time.length(); i++) {
			if (time.charAt(i) == '-') {
				end = i;
				returnTime += time.substring(begin, end) + ".";
				begin = i + 1;
			}

			if (i == time.length() - 1) {
				end = time.length();
				returnTime += time.substring(begin, end);
			}
		}

		return returnTime.trim();
	}

}