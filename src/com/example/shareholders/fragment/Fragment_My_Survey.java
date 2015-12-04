package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MySurveyListActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.personal.OtherPeopleSurveyActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_My_Survey extends Fragment {
	@ViewInject(R.id.mv_survey)
	MyListView mv_survey;

	// 提示无评论
	@ViewInject(R.id.tv_no_content)
	private TextView tv_no_content;

	// 查看更多
	@ViewInject(R.id.tv_watch_more)
	private TextView tv_watch_more;

	// 标题
	@ViewInject(R.id.tv_message)
	private TextView tv_message;

	// 个人uuid
	private String userUuid = "";

	private String userName = "";

	@OnClick({ R.id.tv_watch_more })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_watch_more:
			if (userName.equals("")) { // 用户自身的个人主页
				startActivity(new Intent(getActivity(),
						MySurveyListActivity.class).putExtra("userUuid",
						userUuid));
			} else {

				Intent intent = new Intent(getActivity(),
						OtherPeopleSurveyActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userUuid", userUuid);
				bundle.putString("userName", userName);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_survey, null);
		ViewUtils.inject(this, v);
		if (getActivity() instanceof OtherPeolpeInformationActivity) {
			userUuid = getActivity().getIntent().getExtras().getString("uuid");
		} else {
			userUuid = RsSharedUtil.getString(getActivity(), AppConfig.UUID);
		}
		init();
		return v;
	}

	private void init() {
		String url = AppConfig.VERSION_URL
				+ "user/survey/enroll.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + userUuid + "&pageIndex=0&pageSize=3";
		if (getActivity() instanceof OtherPeolpeInformationActivity) {
			tv_message.setText(getActivity().getIntent().getExtras()
					.getString("userName")
					+ "的调研活动：");
			userName = getActivity().getIntent().getExtras()
					.getString("userName");
		} else {
			tv_message.setText("我的调研活动：");
		}

		Log.d("Fragment_My_Survey_url", url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("Fragment_My_Survey  Respone",
								response.toString());
						try {
							JSONObject jsonObject = new JSONObject(response);
							JSONArray jsonArray = jsonObject
									.getJSONArray("surveys");
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

							}
							mv_survey.setAdapter(new SurveyListAdapter(
									getActivity(), datas));

							if (datas.size() == 0) {
								tv_watch_more.setVisibility(View.GONE);
								tv_no_content.setVisibility(View.VISIBLE);
							} else {
								tv_watch_more.setVisibility(View.VISIBLE);
								tv_no_content.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
				});
		stringRequest.setTag("Fragment_My_Survey");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_My_Survey");
		super.onDestroy();
	}

	private String format(Date date) {
		String str = "";
		SimpleDateFormat ymd = null;
		ymd = new SimpleDateFormat("yyyy.MM.dd");
		str = ymd.format(date);
		return str;
	}

	private String initData(Date date1, Date date2) {
		String time = null;
		time = format(date1) + "-" + format(date2);
		return time;
	}

	class SurveyListAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		SurveyListAdapter(Context context,
				ArrayList<HashMap<String, String>> datas) {
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
						.inflate(R.layout.item_survey_list, null);

				holder.company = (TextView) contentView
						.findViewById(R.id.tv_enterprise_name);
				holder.time = (TextView) contentView
						.findViewById(R.id.tv_schedule);
				holder.personCount = (TextView) contentView
						.findViewById(R.id.tv_follow_number);
				holder.state = (TextView) contentView
						.findViewById(R.id.tv_state);

				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			Date start = new Date(Long.parseLong(list.get(position).get(
					"beginDate")));
			Date end = new Date(Long.parseLong(list.get(position)
					.get("endDate")));
			String timeString = initData(start, end);

			holder.company.setText((CharSequence) list.get(position).get(
					"surveyName"));
			holder.time.setText(timeString);
			holder.personCount.setText((CharSequence) list.get(position).get(
					"countFollow"));
			holder.state
					.setText((CharSequence) list.get(position).get("state"));
			contentView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					startActivity(new Intent(getActivity(),DetailSurveyActivity.class).putExtra("uuid", list.get(position).get("uuid").toString()));
				}
			});
			return contentView;
		}

		class ViewHolder {

			TextView company;
			TextView time;
			TextView personCount;
			TextView state;
		}

	}
}