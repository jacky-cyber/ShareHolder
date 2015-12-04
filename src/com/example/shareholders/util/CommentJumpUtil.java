package com.example.shareholders.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.activity.stock.StockCommentActivity;
import com.example.shareholders.activity.survey.SingleCommentActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

//消息中心我的评论的跳转工具类
public class CommentJumpUtil {

	private Context context;
	//话题评论uuid
	private String uuid;
	
	public CommentJumpUtil(Context context,String uuid) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.uuid = uuid;
		getDetail();
	}
	
	//根据传过来的话题id，获取网络数据
	private void getDetail(){
		String url = AppConfig.URL_TOPIC+"detail.json?access_token=";
		url+= RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN);
		url = url+ "&topicUuid=" + uuid;
		Log.d("jatjat", url);
		StringRequest stringRequest = new StringRequest(url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				//处理数据，判断属于调研评论还是属于股票评论
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getString("topicType").equals("SURVEY")) {
						Bundle bundle = new Bundle();
						bundle.putString("surveyUuid",
								jsonObject.get("surveyUuid").toString());
						bundle.putString("creatorName",
								jsonObject.get("creatorName")
								.toString());
						bundle.putString("creationTime", jsonObject
								.get("creationTime").toString());
						bundle.putString("creatorLogoUrl", jsonObject
								.get("creatorLogoUrl").toString());
						bundle.putString("content",
								jsonObject.get("content").toString());
						bundle.putString("likeNum",
								jsonObject.get("likeNum").toString());
						bundle.putString("commentNum",
								jsonObject.get("commentNum").toString());
						bundle.putString("readNum",
								jsonObject.get("readNum").toString());
						bundle.putString("transpondNum", jsonObject
								.get("transpondNum").toString());
						bundle.putString("topicUuid",
								jsonObject.get("topicUuid").toString());
						bundle.putString("liked",
								jsonObject.get("liked").toString());
						bundle.putString("refTopic",
								jsonObject.get("refTopic").toString());
						bundle.putString("medias",
								jsonObject.get("medias").toString());
						bundle.putString("followed", jsonObject.get("followed")
								.toString());
						bundle.putSerializable("creatorUuid", 
								jsonObject.get("creatorUuid").toString());
						//启动调研评论个评评论
						Intent intent = new Intent(context,
								SingleCommentActivity.class);
						intent.putExtras(bundle);
						context.startActivity(intent);
					}else {
						Bundle bundle = new Bundle();
						bundle.putString("creatorLogoUrl",
								jsonObject.get("creatorLogoUrl").toString());
						bundle.putString("topicUuid", jsonObject.get("topicUuid")
								.toString());
						bundle.putString("followed", jsonObject.get("followed")
								.toString());
						bundle.putString("content", jsonObject.get("content")
								.toString());
						bundle.putString("creatorName", jsonObject.get("creatorName")
								.toString());
						bundle.putString("creatorUuid", jsonObject.get("creatorUuid")
								.toString());
						bundle.putString("creationTime", jsonObject.get("creationTime")
								.toString());
						bundle.putString("securitySymbol",
								jsonObject.get("securitySymbol").toString());
						bundle.putString("securityName", jsonObject.get("securityName")
								.toString());
						bundle.putString("likeNum", jsonObject.get("likeNum")
								.toString());
						bundle.putString("readNum", jsonObject.get("readNum")
								.toString());
						bundle.putString("transpondNum", jsonObject.get("transpondNum")
								.toString());
						bundle.putString("liked", jsonObject.get("liked").toString());
						bundle.putString("refTopic", jsonObject.get("refTopic")
								.toString());
						bundle.putString("commentNum", jsonObject.get("commentNum")
								.toString());
						try {
							bundle.putString("medias", jsonObject.get("medias")
									.toString());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("liang", e.toString());
							e.printStackTrace();
						}

						//启动沪深评论个评
						Intent intent = new Intent(context, StockCommentActivity.class);
						intent.putExtras(bundle);
						context.startActivity(intent);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		stringRequest.setTag("details");
		MyApplication.getRequestQueue().add(stringRequest);
	}
	
}
