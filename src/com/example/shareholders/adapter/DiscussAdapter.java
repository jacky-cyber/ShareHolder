package com.example.shareholders.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DiscussAdapter extends BaseAdapter {

	public ArrayList<HashMap<String, Object>> list;
	public Context context;

	public DiscussAdapter(Context context,
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_fund_dicuss, parent, false);

		}

		// 头像
		CircleImageView ci_face = (CircleImageView) AbViewHolder.get(
				convertView, R.id.iv_head);
		ImageLoader.getInstance().displayImage(
				list.get(position).get("creatorLogoUrl").toString(), ci_face);
		// 名字
		TextView tv_name = (TextView) AbViewHolder.get(convertView,
				R.id.tv_name);
		tv_name.setText(list.get(position).get("creatorName").toString());
		// 时间
		TextView tv_time = (TextView) AbViewHolder.get(convertView,
				R.id.tv_time);
		long Time = Long.parseLong(list.get(position).get("creationTime")
				.toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String creationTime = dateFormat.format(new Date(Time));
		tv_time.setText(creationTime);
		// 点赞数目
		final TextView tv_dianzan_number = (TextView) AbViewHolder.get(
				convertView, R.id.tv_dianzan_number);
		tv_dianzan_number.setText(list.get(position).get("likeNum").toString());
		// 点赞按钮
		final ImageView iv_dianzan = (ImageView) AbViewHolder.get(convertView,
				R.id.iv_dianzan);
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
				+ RsSharedUtil.getString(context, "access_token");
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
		MyApplication.getRequestQueue().add(stringRequest);
	}

}
