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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.RoundRectImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Survey_Organized extends Fragment {
	
	private BitmapUtils bitmapUtils;
	
	// 审核通过
	@ViewInject(R.id.iv_verified_survey)
	private ImageView iv_verified_survey;
	@ViewInject(R.id.lv_verified_survey)
	private MyListView lv_verified_survey;
	// 待审核
	@ViewInject(R.id.iv_unverify_survey)
	private ImageView iv_unverify_survey;
	@ViewInject(R.id.lv_unverify_survey)
	private MyListView lv_unverify_survey;
	// 审核失败
	@ViewInject(R.id.iv_verify_failed_survey)
	private ImageView iv_verify_failed_survey;
	@ViewInject(R.id.lv_verify_failed_survey)
	private MyListView lv_verify_failed_survey;
	ListViewAdapter listViewAdapter;
	private int currentPosition = -1;
	private String userUuid = "";
	private boolean open = false;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_survey_organized, null);
		ViewUtils.inject(this, view);
		
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.huodongphoto);
		userUuid = getActivity().getIntent().getExtras().getString("userUuid");
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		initSuccess();
		initCheck();
		initFail();
	}

	// 审核失败
	private void initFail() {
		// TODO Auto-generated method stub
		String url = AppConfig.URL_USER + "survey/sponsor.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + userUuid
				+ "&state=FAILED&pageSize=999&pageIndex=0";
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
					// Toast.makeText(getActivity(), "initFail() error",
					// 3000).show();
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		lv_verify_failed_survey
		.setAdapter(new ListViewAdapter(
				getActivity(), datas,0));
		stringRequest.setTag("Fragment_Survey_originzed3");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 待审核
	private void initCheck() {
		// TODO Auto-generated method stub
		String url = AppConfig.URL_USER + "survey/sponsor.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + userUuid
				+ "&state=CREATE&pageSize=999&pageIndex=0";
		final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
		Log.d("fragmentsurveysigned", url);

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
					// "initCheck() error",
					// 3000).show();
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		lv_unverify_survey
		.setAdapter(new ListViewAdapter(
				getActivity(), datas,0));
		stringRequest.setTag("Fragment_Survey_originzed2");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 审核通过
	private void initSuccess() {
		// TODO Auto-generated method stub
		String url = AppConfig.URL_USER + "survey/sponsor.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + userUuid
				+ "&state=NORMAL&pageSize=999&pageIndex=0";
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
					// "initCheck() error",
					// 3000).show();
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		lv_verified_survey
		.setAdapter(new ListViewAdapter(
				getActivity(), datas,1));
		stringRequest.setTag("Fragment_Survey_originzed1");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_Survey_originzed1");
		MyApplication.getRequestQueue().cancelAll("Fragment_Survey_originzed2");
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
	 * 未选中状态
	 */
	private void Clear() {
		iv_verified_survey
		.setImageResource(R.drawable.btn_chakangengduo_normal);
		lv_verified_survey.setVisibility(View.GONE);
		iv_unverify_survey
		.setImageResource(R.drawable.btn_chakangengduo_normal);
		lv_unverify_survey.setVisibility(View.GONE);
		iv_verify_failed_survey
		.setImageResource(R.drawable.btn_chakangengduo_normal);
		lv_verify_failed_survey.setVisibility(View.GONE);
	}

	@OnClick({ R.id.rl_verified_survey, R.id.rl_unverify_survey,
		R.id.rl_verify_failed_survey })
	private void onClick(View v) {
		switch (v.getId()) {
		// 审核通过
		case R.id.rl_verified_survey:
			Clear();
			if (currentPosition != 0|| !open) {
				iv_verified_survey
				.setImageResource(R.drawable.btn_chakangengduo_selected);
				// lv_verified_survey.setAdapter(listViewAdapter);
				lv_verified_survey.setVisibility(View.VISIBLE);
			}
			open = !open;
			currentPosition = 0;
			break;
			// 待审核
		case R.id.rl_unverify_survey:
			Clear();
			if (currentPosition != 1||!open) {
				iv_unverify_survey
				.setImageResource(R.drawable.btn_chakangengduo_selected);
				// lv_unverify_survey.setAdapter(listViewAdapter);

				lv_unverify_survey.setVisibility(View.VISIBLE);
			}
			open=!open;
			currentPosition = 1;
			break;
			// 审核失败
		case R.id.rl_verify_failed_survey:
			Clear();
			if (currentPosition != 2||!open) {
				iv_verify_failed_survey
				.setImageResource(R.drawable.btn_chakangengduo_selected);
				// lv_verify_failed_survey.setAdapter(listViewAdapter);

				lv_verify_failed_survey.setVisibility(View.VISIBLE);
			}
			open=!open;
			currentPosition = 2;
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @author warren
	 * 
	 */
	private class ListViewAdapter extends BaseAdapter {

		// 数据列表
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		private Context context;

		private LayoutInflater mInflater;
		private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.huodongphoto)
		.showImageOnFail(R.drawable.huodongphoto).cacheInMemory(true)
		.cacheOnDisc(true).build();
		
		private int flag;

		public ListViewAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		public ListViewAdapter(Context context,
				ArrayList<HashMap<String, String>> datas,int flag) {
			// TODO Auto-generated constructor stub
			this.list = datas;
			Log.d("jatjatjat", datas.toString());
			this.context = context;
			this.flag = flag;
			mInflater = LayoutInflater.from(context);
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
			Log.d("asdsfdfdsf", list.toString());
			if (list.size()==0) {
				contentView = mInflater.inflate(R.layout.item_no_content, null);
				return contentView;
			}
			if (contentView == null) {

				contentView = mInflater.inflate(R.layout.item_servey_list,
						parent, false);

				contentView.setBackgroundResource(R.drawable.item_no_click);

			}
			TextView tv_title = AbViewHolder.get(contentView, R.id.tv_title);
			TextView tv_start_date = AbViewHolder.get(contentView,
					R.id.tv_start_date);
			TextView tv_end_date = AbViewHolder.get(contentView,
					R.id.tv_end_date);
			TextView tv_follow_member_number = AbViewHolder.get(contentView,
					R.id.tv_follow_member_number);
			RoundRectImageView iv_head = AbViewHolder.get(contentView, R.id.iv_head);

			//ImageLoader.getInstance().displayImage(list.get(position).get("logo"), iv_head, defaultOptions);
			bitmapUtils.display(iv_head, list.get(position).get("logo"));

			ImageLoader.getInstance().displayImage(list.get(position).get("logo"), iv_head, defaultOptions);


			
			ImageView iv_state = AbViewHolder.get(contentView, R.id.iv_state);
			ImageLoader.getInstance().displayImage(list.get(position).get("logo"), iv_head, defaultOptions);

			Date start = new Date(Long.parseLong(list.get(position).get(
					"beginDate")));
			Date end = new Date(Long.parseLong(list.get(position)
					.get("endDate")));
			String startTime = format(start);
			String endTimeString = format(end);

			tv_title.setText((CharSequence) list.get(position)
					.get("surveyName"));
			tv_start_date.setText(startTime);
			tv_end_date.setText(endTimeString);
			tv_follow_member_number.setText((CharSequence) list.get(position)
					.get("countFollow"));
			if (flag==1) {
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

	}
}
