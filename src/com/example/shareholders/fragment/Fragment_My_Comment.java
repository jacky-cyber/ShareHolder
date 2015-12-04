package com.example.shareholders.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;

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
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.personal.PersonalCommentActivity;
import com.example.shareholders.activity.stock.StockCommentActivity;
import com.example.shareholders.activity.survey.SingleCommentActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_My_Comment extends Fragment {
	@ViewInject(R.id.mv_comment)
	private MyListView mv_comment;

	// 提示无评论
	@ViewInject(R.id.tv_no_content)
	private TextView tv_no_content;

	// 查看更多
	@ViewInject(R.id.tv_more)
	private TextView tv_more;

	// 标题
	@ViewInject(R.id.tv_message)
	private TextView tv_message;

	private String userUuid = "";
	private String userName = "";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_my_comment, null);
		ViewUtils.inject(this, v);
		init();
		return v;
	}

	private String format(Date date) {
		String str = "";
		SimpleDateFormat ymd = null;
		ymd = new SimpleDateFormat("yyyy.MM.dd");
		str = ymd.format(date);
		return str;
	}

	private String initData(Date date1) {
		String time = null;
		time = format(date1);
		return time;
	}

	private void init() {
		String url = AppConfig.VERSION_URL
				+ "topic/list/topicListOfUserCreated.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		if (getActivity() instanceof OtherPeolpeInformationActivity) {
			url = url + "&userUuid="
					+ getActivity().getIntent().getExtras().getString("uuid")
					+ "&pageSize=3&pageIndex=0";
			tv_message.setText(getActivity().getIntent().getExtras()
					.getString("userName")
					+ "发起的评论：");
			userUuid = getActivity().getIntent().getExtras().getString("uuid");
			userName = getActivity().getIntent().getExtras()
					.getString("userName");
		} else {
			url = url + "&pageSize=3&pageIndex=0";
			// PersonalInformation personalInformation = dbUtils.findById(
			// PersonalInformation.class,
			// RsSharedUtil.getString(getActivity(), AppConfig.UUID));
			tv_message.setText("我发起的评论：");
		}

		Log.d("Fragment_My_Comment Success",
				url);

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("Fragment_My_Comment Success",
						response.toString());
				try {
					JSONArray jsonArray = new JSONArray(response);
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
						mv_comment.setAdapter(new CommentAdapter(
								getActivity(), datas));

						if (datas.size() == 0) {
							tv_more.setVisibility(View.GONE);
							tv_no_content.setVisibility(View.VISIBLE);
						} else {
							tv_more.setVisibility(View.VISIBLE);
							tv_no_content.setVisibility(View.GONE);
						}

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
		stringRequest.setTag("Fragment_My_Comment");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@OnClick({ R.id.tv_more })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_more:
			Intent intent = new Intent(getActivity(),
					PersonalCommentActivity.class);

			Bundle bundle = new Bundle();
			bundle.putString("userUuid", userUuid);
			bundle.putString("userName", userName);
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("Fragment_My_Comment");
		super.onDestroy();
	}

	class CommentAdapter extends BaseAdapter {

		private ViewHolder holder;
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		CommentAdapter(Context context, ArrayList<HashMap<String, String>> datas) {
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
				contentView = mInflater.inflate(R.layout.item_comment_list,
						null);

				holder.word = (TextView) contentView
						.findViewById(R.id.tv_content);
				holder.company = (TextView) contentView
						.findViewById(R.id.tv_symbol);
				holder.time = (TextView) contentView.findViewById(R.id.tv_date);

				contentView.setTag(holder);

			} else {
				holder = (ViewHolder) contentView.getTag();
			}

			Date start = new Date(Long.parseLong(list.get(position).get(
					"creationTime")));
			String time = initData(start);

			holder.word.setText((CharSequence) list.get(position)
					.get("content"));
			holder.company.setText((CharSequence) list.get(position).get(
					"securityName"));
			holder.time.setText(time);
			contentView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("commentcontent", list.toString());
					if (list.get(position).get("surveyUuid") != null) {
						Bundle bundle = new Bundle();
						bundle.putString("surveyUuid",
								list.get(position).get("surveyUuid").toString());
						bundle.putString("creatorName",
								list.get(position).get("creatorName")
								.toString());
						bundle.putString("creationTime", list.get(position)
								.get("creationTime").toString());
						bundle.putString("creatorLogoUrl", list.get(position)
								.get("creatorLogoUrl").toString());
						bundle.putString("content",
								list.get(position).get("content").toString());
						bundle.putString("likeNum",
								list.get(position).get("likeNum").toString());
						bundle.putString("commentNum",
								list.get(position).get("commentNum").toString());
						bundle.putString("readNum",
								list.get(position).get("readNum").toString());
						bundle.putString("transpondNum", list.get(position)
								.get("transpondNum").toString());
						bundle.putString("topicUuid",
								list.get(position).get("topicUuid").toString());
						bundle.putString("liked",
								list.get(position).get("liked").toString());
						bundle.putString("refTopic",
								list.get(position).get("refTopic").toString());
						bundle.putString("medias",
								list.get(position).get("medias").toString());
						bundle.putString("followed",
								list.get(position).get("followed").toString());
						bundle.putSerializable("creatorUuid", 
								list.get(position).get("creatorUuid").toString());
						bundle.putString("createByMe", "true");
						Intent intent = new Intent(getActivity(),
								SingleCommentActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}else {
						Bundle bundle = new Bundle();
						bundle.putString("creatorLogoUrl",
								list.get(position).get("creatorLogoUrl").toString());
						bundle.putString("topicUuid", list.get(position).get("topicUuid")
								.toString());
						bundle.putString("followed", list.get(position).get("followed")
								.toString());
						bundle.putString("content", list.get(position).get("content")
								.toString());
						bundle.putString("creatorName", list.get(position).get("creatorName")
								.toString());
						bundle.putString("creatorUuid", list.get(position).get("creatorUuid")
								.toString());
						bundle.putString("creationTime", list.get(position).get("creationTime")
								.toString());
						bundle.putString("securitySymbol",
								list.get(position).get("securitySymbol").toString());
						bundle.putString("securityName", list.get(position).get("securityName")
								.toString());
						bundle.putString("likeNum", list.get(position).get("likeNum")
								.toString());
						bundle.putString("readNum", list.get(position).get("readNum")
								.toString());
						bundle.putString("transpondNum", list.get(position).get("transpondNum")
								.toString());
						bundle.putString("liked", list.get(position).get("liked").toString());
						bundle.putString("refTopic", list.get(position).get("refTopic")
								.toString());
						bundle.putString("commentNum", list.get(position).get("commentNum")
								.toString());
						try {
							bundle.putString("medias", list.get(position).get("medias")
									.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("liang", e.toString());
							e.printStackTrace();
						}

						//沪深个评评论
						Intent intent = new Intent(getActivity(), StockCommentActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			return contentView;
		}

		class ViewHolder {

			TextView word;
			TextView company;
			TextView time;
		}

	}
}
