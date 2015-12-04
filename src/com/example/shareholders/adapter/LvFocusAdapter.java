package com.example.shareholders.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;
import com.example.shareholders.activity.survey.SingleCommentActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.survey.HottestComment;
import com.example.shareholders.util.Mapper;
import com.example.shareholders.util.RsSharedUtil;

public class LvFocusAdapter extends BaseAdapter {
	private ViewHolder holder;
	private List<HottestComment> hottestComments;
	private Context context;
	private LayoutInflater mInflater;

	public LvFocusAdapter(Context context, List<HottestComment> hottestComments) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.hottestComments = hottestComments;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hottestComments.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hottestComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.item_fragment_price_surveyfocus, null);
			holder.focus_title = (TextView) view.findViewById(R.id.focus_title);
			holder.focus_name = (TextView) view.findViewById(R.id.focus_name);
			holder.focus_heart = (TextView) view.findViewById(R.id.focus_heart);
			holder.focus_chat = (TextView) view.findViewById(R.id.focus_chat);
			holder.ll_focus = (LinearLayout) view.findViewById(R.id.ll_focus);

			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.focus_title.setText(hottestComments.get(position).getContent());
		holder.focus_name
				.setText(hottestComments.get(position).getSurveyName());
		holder.focus_heart.setText(""
				+ hottestComments.get(position).getLikeNum());
		holder.focus_chat.setText(""
				+ hottestComments.get(position).getCommentNum());

		holder.focus_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!RsSharedUtil.getLoginState(context)){
					Intent intent = new Intent(context,
							LoginActivity.class);
					context.startActivity(intent);
				}else{
				String uuid = hottestComments.get(position).getSurveyUuid()
						.toString();
				Intent intent = new Intent(context, DetailSurveyActivity.class);
				Bundle bundle = new Bundle();

				bundle.putString("uuid", uuid);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}}
		});

		holder.ll_focus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!RsSharedUtil.getLoginState(context)){
					Intent intent = new Intent(context,
							LoginActivity.class);
					context.startActivity(intent);
				}else{
				Bundle bundle = new Bundle();
				bundle.putString("surveyUuid", hottestComments.get(position)
						.getSurveyUuid());
				bundle.putString("creatorUuid", hottestComments.get(position)
						.getCreatorUuid());
				bundle.putString("creatorName", hottestComments.get(position)
						.getCreatorName().toString());
				bundle.putString("creationTime", hottestComments.get(position)
						.getCreationTime().toString());
				bundle.putString("followed", hottestComments.get(position)
						.isFollowed() + "");
				bundle.putString("creatorLogoUrl", hottestComments
						.get(position).getCreatorLogoUrl());
				bundle.putString("content", hottestComments.get(position)
						.getContent().toString());
				bundle.putString("likeNum", hottestComments.get(position)
						.getLikeNum() + "");
				bundle.putString("commentNum", hottestComments.get(position)
						.getCommentNum() + "");
				bundle.putString("readNum", hottestComments.get(position)
						.getReadNum() + "");
				bundle.putString("transpondNum", hottestComments.get(position)
						.getTranspondNum() + "");
				bundle.putString("topicUuid", hottestComments.get(position)
						.getTopicUuid().toString());
				bundle.putString("liked", hottestComments.get(position)
						.isLiked() + "");
				bundle.putString("refTopic", hottestComments.get(position)
						.getRefTopic() + "");
				Mapper mapper = new Mapper();

				try {
					mapper.writeValueAsString(hottestComments.get(position)
							.getMedias());
					bundle.putString(
							"medias",
							mapper.writeValueAsString(hottestComments.get(
									position).getMedias()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(context, SingleCommentActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);
				new AsyncSendReadNum().execute(hottestComments.get(position)
						.getTopicUuid().toString());
				}
			}
		});

		return view;
	}

	class ViewHolder {

		TextView focus_title;
		TextView focus_name;
		TextView focus_heart;
		TextView focus_chat;
		LinearLayout ll_focus;
	}

	/**
	 * 发起阅读动作
	 * 
	 * @param index
	 * @param pageSize
	 */
	private void sendReadnum(String topicUUID) {
		String url = AppConfig.URL_TOPIC + "addReadNum.json?access_token="
				+ RsSharedUtil.getString(context, "access_token")
				+ "&topicUuid=" + topicUUID;
		// String
		// url=AppConfig.URL+"api/v1.0/topic/list/survey/creator.json?access_token=d7abc947-6df0-4073-bc74-ba4b24c478b2&surveyUuid=123456&pageIndex=0&pageSize=5";
		Log.d("阅读量url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}

		);
		RequestQueue volleyRequestQueue;
		volleyRequestQueue = Volley.newRequestQueue(context);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 异步发送数据
	 * 
	 * @author Administrator
	 * 
	 */
	private class AsyncSendReadNum extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {

		};

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// 获取数据
			sendReadnum(arg0[0]);
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
}
