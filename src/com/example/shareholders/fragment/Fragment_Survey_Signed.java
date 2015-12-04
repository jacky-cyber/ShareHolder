package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path.Op;
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

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.RoundRectImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Survey_Signed extends Fragment {
	
	
	private BitmapUtils bitmapUtils;
	
	// 答复通过
	@ViewInject(R.id.iv_admitted_survey)
	private ImageView iv_admitted_survey;
	@ViewInject(R.id.lv_admitted_survey)
	private MyListView lv_admitted_survey;
	// 待答复
	@ViewInject(R.id.iv_unadmit_survey)
	private ImageView iv_unadmit_survey;
	@ViewInject(R.id.lv_unadmit_survey)
	private MyListView lv_unadmit_survey;
	// 拒绝通过
	@ViewInject(R.id.iv_banned_survey)
	private ImageView iv_banned_survey;
	@ViewInject(R.id.lv_banned_survey)
	private MyListView lv_banned_survey;
	// 状态
	private int currentPosition = -1;
	// 用户uuid
	private String userUuid = "";
	
	private boolean open = false;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_survey_signed, null);
		ViewUtils.inject(this, view);
		
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.huodongphoto);
		
		
		userUuid = getActivity().getIntent().getExtras().getString("userUuid");
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initSuccess();
		initCheck();
		initFail();
	}

	/**
	 * 未选中状态
	 */
	private void Clear() {
		iv_admitted_survey
		.setImageResource(R.drawable.btn_chakangengduo_normal);
		lv_admitted_survey.setVisibility(View.GONE);
		iv_unadmit_survey.setImageResource(R.drawable.btn_chakangengduo_normal);
		lv_unadmit_survey.setVisibility(View.GONE);
		iv_banned_survey.setImageResource(R.drawable.btn_chakangengduo_normal);
		lv_banned_survey.setVisibility(View.GONE);
	}

	@OnClick({ R.id.rl_admitted_survey, R.id.rl_unadmit_survey,
		R.id.rl_banned_survey })
	private void onClick(View v) {
		switch (v.getId()) {
		// 答复通过
		case R.id.rl_admitted_survey:
			Clear();
			if (currentPosition != 0|| !open) {
				iv_admitted_survey
				.setImageResource(R.drawable.btn_chakangengduo_selected);
				/* lv_admitted_survey.setAdapter(listViewAdapter); */
				lv_admitted_survey.setVisibility(View.VISIBLE);
			}
			open = !open;
			currentPosition = 0;
			break;
			// 待答复
		case R.id.rl_unadmit_survey:
			Clear();
			if (currentPosition != 1||!open) {
				iv_unadmit_survey
				.setImageResource(R.drawable.btn_chakangengduo_selected);
				/* lv_unadmit_survey.setAdapter(listViewAdapter); */

				lv_unadmit_survey.setVisibility(View.VISIBLE);
			}
			open = !open;
			currentPosition = 1;
			break;
			// 拒绝通过
		case R.id.rl_banned_survey:
			Clear();
			if (currentPosition != 2|| !open) {
				iv_banned_survey
				.setImageResource(R.drawable.btn_chakangengduo_selected);
				/* lv_banned_survey.setAdapter(listViewAdapter); */

				lv_banned_survey.setVisibility(View.VISIBLE);
			}
			open = !open;
			currentPosition = 2;
			break;
		default:
			break;
		}
	}

	private void initCheck() {
		String url = AppConfig.URL_USER + "survey/enroll.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&state=ENROLL&userUuid=" + userUuid
				+ "&pageIndex=0&pageSize=999";
		Log.d("fragmentsurveysigned", url);
		final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {

				// Toast.makeText(getActivity(), "initCheck() success",
				// 3000).show();
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject
							.getJSONArray("surveys");
					// JSONArray jsonArray=new JSONArray(response);
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
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// Toast.makeText(getActivity(),
					// "initCheck() error", 3000).show();
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		lv_unadmit_survey
		.setAdapter(new SurveySignAdapter(
				getActivity(), datas,0));
		stringRequest.setTag("Fragment_Survey_Signed2");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void initFail() {
		String url = AppConfig.URL_USER + "survey/enroll.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&state=FAILED&userUuid=" + userUuid
				+ "&pageIndex=0&pageSize=999";
		final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

		Log.d("fragmentsurveysigned", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// Toast.makeText(getActivity(), "initFail() success",
				// 3000).show();
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject
							.getJSONArray("surveys");
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
						lv_banned_survey
						.setAdapter(new SurveySignAdapter(
								getActivity(), datas,1));

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				// Toast.makeText(getActivity(), "initFail() error",
				// 3000).show();

			}
		});
		lv_banned_survey
		.setAdapter(new SurveySignAdapter(
				getActivity(), datas,0));
		stringRequest.setTag("Fragment_Survey_Signed2");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void initSuccess() {
		String url = AppConfig.URL_USER + "survey/enroll.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&state=SUCCESS&userUuid=" + userUuid
				+ "&pageIndex=0&pageSize=999";
		final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

		Log.d("fragmentsurveysigned", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {

				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject
							.getJSONArray("surveys");
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
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				// Toast.makeText(getActivity(), "initSuccess() error",
				// 3000).show();

			}
		});
		lv_admitted_survey
		.setAdapter(new SurveySignAdapter(
				getActivity(), datas,1));
		stringRequest.setTag("Fragment_Survey_Signed1");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_Survey_Signed1");
		MyApplication.getRequestQueue().cancelAll("Fragment_Survey_Signed2");
		MyApplication.getRequestQueue().cancelAll("Fragment_Survey_Signed3");
		super.onDestroy();
	}

	private String format(Date date) {
		String str = "";
		SimpleDateFormat ymd = null;
		ymd = new SimpleDateFormat("yyyy.MM.dd");
		str = ymd.format(date);
		return str;
	}

	/**
	 * 
	 * @author warren
	 * 
	 */
	private class SurveySignAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;
		private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.huodongphoto)
		.showImageOnFail(R.drawable.huodongphoto).cacheInMemory(true)
		.cacheOnDisc(true).build();
		//标注是否通过，能否跳到调研详情
		private int flag = 0;

		SurveySignAdapter(Context context,
				ArrayList<HashMap<String, String>> datas,int flag) {
			this.context = context;
			this.list = datas;
			Log.d("jatjatjat", datas.toString());
			mInflater = LayoutInflater.from(context);
			this.flag = flag;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list.size()==0) {
				return 1;
			}
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View contentView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (list.size()==0) {
				contentView = mInflater.inflate(R.layout.item_no_content, null);
				return contentView;
			}
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater.inflate(R.layout.item_servey_list,
						parent, false);
				
				holder.tv_title = (TextView) contentView
						.findViewById(R.id.tv_title);
				holder.tv_start_date = (TextView) contentView
						.findViewById(R.id.tv_start_date);
				holder.tv_end_date = (TextView) contentView
						.findViewById(R.id.tv_end_date);
				holder.tv_follow_member_number = (TextView) contentView
						.findViewById(R.id.tv_follow_member_number);
				holder.iv_head = (RoundRectImageView) contentView.findViewById(R.id.iv_head);
				holder.iv_state = (ImageView) contentView.findViewById(R.id.iv_state);

				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}

			Date start = new Date(Long.parseLong(list.get(position).get(
					"beginDate")));
			Date end = new Date(Long.parseLong(list.get(position)
					.get("endDate")));
			String startTime = format(start);
			String endTimeString = format(end);
			
			bitmapUtils.display(holder.iv_head, list.get(position).get("logo"));
			//ImageLoader.getInstance().displayImage(list.get(position).get("logo"), holder.iv_head, defaultOptions);
			holder.tv_title.setText((CharSequence) list.get(position).get(
					"surveyName"));
			holder.tv_start_date.setText(startTime);
			holder.tv_end_date.setText(endTimeString);
			holder.tv_follow_member_number.setText((CharSequence) list.get(
					position).get("countFollow"));
			if (flag==0) {
				
			}else {
				contentView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//跳转到调研详情
						startActivity(new Intent(getActivity(),DetailSurveyActivity.class).putExtra("uuid", list.get(position).get("uuid")));
					}
				});
			}
			
			return contentView;
		}

		class ViewHolder {

			TextView tv_title;
			TextView tv_start_date;
			TextView tv_end_date;
			TextView tv_follow_member_number;
			ImageView iv_state;
			RoundRectImageView iv_head;
		}

	}

	/**
	 * 
	 * @author warren
	 * 
	 */
	/*
	 * private class ListViewAdapter extends BaseAdapter { public
	 * ListViewAdapter() { mInflater = (LayoutInflater)
	 * getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE); }
	 * 
	 * private LayoutInflater mInflater;
	 * 
	 * @Override public int getCount() { // TODO Auto-generated method stub
	 * return 10; }
	 * 
	 * @Override public Object getItem(int arg0) { // TODO Auto-generated method
	 * stub return null; }
	 * 
	 * @Override public long getItemId(int arg0) { // TODO Auto-generated method
	 * stub return 0; }
	 * 
	 * @Override public View getView(int arg0, View contentView, ViewGroup arg2)
	 * { // TODO Auto-generated method stub if (contentView == null) {
	 * contentView = mInflater .inflate(R.layout.item_servey_list, null); }
	 * return contentView; }
	 * 
	 * }
	 */
}
