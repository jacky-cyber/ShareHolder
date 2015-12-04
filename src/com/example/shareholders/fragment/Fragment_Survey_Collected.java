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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.RoundRectImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Survey_Collected extends Fragment {

	private BitmapUtils bitmapUtils;
	
	@ViewInject(R.id.lv_collected_survey)
	private ListView lv_collected_survey;
	private String userUuid = "";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_survey_collected, null);
		ViewUtils.inject(this, view);
		
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.huodongphoto);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.huodongphoto);
		userUuid = getActivity().getIntent().getExtras().getString("userUuid");
		initList();
		return view;
	}

	private void initList() {
		String url = AppConfig.URL_USER + "survey/follow.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + userUuid + "&pageIndex=0&pageSize=999";
		final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

		Log.d("Fragment_Survey_Collected_ url", url);
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
			}
		});
		lv_collected_survey
		.setAdapter(new SurveyCollectAdapter(
				getActivity(), datas));
		stringRequest.setTag("Fragment_Survey_Collected");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_Survey_Collected");
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
	private class SurveyCollectAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;
		private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.huodongphoto)
		.showImageOnFail(R.drawable.huodongphoto).cacheInMemory(true)
		.cacheOnDisc(true).build();

		SurveyCollectAdapter(Context context,
				ArrayList<HashMap<String, String>> datas) {
			this.context = context;
			this.list = datas;
			Log.d("jatjatjat", datas.toString());
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

			holder.tv_title.setText((CharSequence) list.get(position).get(
					"surveyName"));
			holder.tv_start_date.setText(startTime);
			holder.tv_end_date.setText(endTimeString);
			holder.tv_follow_member_number.setText((CharSequence) list.get(
					position).get("countFollow"));
			
			bitmapUtils.display(holder.iv_head, list.get(position).get("logo"));
			//ImageLoader.getInstance().displayImage(list.get(position).get("logo"), holder.iv_head, defaultOptions);
			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//跳转到调研详情
					startActivity(new Intent(getActivity(),DetailSurveyActivity.class).putExtra("uuid", list.get(position).get("uuid")));
				}
			});
			return contentView;
		}

		class ViewHolder {

			TextView tv_title;
			TextView tv_start_date;
			TextView tv_end_date;
			TextView tv_follow_member_number;
			RoundRectImageView iv_head;
		}

	}
}
