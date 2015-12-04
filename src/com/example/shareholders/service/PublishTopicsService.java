package com.example.shareholders.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.Bimp;
import com.example.shareholders.db.entity.FileUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;

/**
 * 发布话题，包括上传图片后并发布评论
 * 
 * @author warren
 * 
 */
public class PublishTopicsService extends IntentService {
	public PublishTopicsService() {
		super("publishTopics");
		// TODO Auto-generated constructor stub
	}

	// 成功
	private static int OK = 1;
	// 内容
	private String content;
	// surveyUuid
	private String surveyUuid;
	// 图片列表
	private List<String> pictureUrl;
	RequestQueue volleyRequestQueue = null;
	private void PostPicture(String picPath, final int position, final int max) {
		String requestURL = AppConfig.URL_FILE + "upload.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		requestURL = requestURL + "&uploadType=USER_TOPIC";
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(picPath));
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, requestURL, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						pictureUrl.add(responseInfo.result);
						Log.d("哈", responseInfo.result);
						if (position == max) {
							Log.d("哈哈哈哈h", responseInfo.result);
							// 获取完图片，发布话题
							PublishTopic(1);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						stopSelf();
					}
				});
	}

	/*
	 * 发表评论
	 * 
	 * @param num: 0为无图，1为有图
	 */
	private void PublishTopic(int num) {
		String url = AppConfig.URL_TOPIC + "add.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		Log.d("PublishTopicurl", url);
		JSONObject params = new JSONObject();
		JSONArray picturesArray = new JSONArray();

		try {
			if (num != 0) {
				for (int i = 0; i < pictureUrl.size(); i++) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("url", pictureUrl.get(i));
					jsonObject.put("mediaType", "PICTURE");
					picturesArray.put(jsonObject);
				}
				params.put("medias", picturesArray);
			}
			params.put("content", content);
			params.put("surveyUuid", surveyUuid);
			Log.d("params", params.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("PublishTopicurl", response);
						// 通知Activity去刷新,谁先接收到的，就终止它
						Intent updateIntent = new Intent("updateComment");
						sendOrderedBroadcast(updateIntent, null);
						//普通广播
						Intent intent = new Intent();
						intent.setAction("updateComment");
						sendBroadcast(intent);
					
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						LogUtils.d(error.toString());

						Intent intent = new Intent();
						intent.setAction("publish_topic_fail");
						sendBroadcast(intent);
                        
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("哈哈哈哈h", "?????????");
		volleyRequestQueue = Volley.newRequestQueue(this);
		pictureUrl = new ArrayList<String>();

		content = intent.getExtras().getString("content");
		surveyUuid = intent.getExtras().getString("surveyUuid");
		String[] list = new String[Bimp.drr.size()];

		for (int i = 0; i < Bimp.drr.size(); i++) {
			String Str = Bimp.drr.get(i).substring(
					Bimp.drr.get(i).lastIndexOf("/") + 1,
					Bimp.drr.get(i).lastIndexOf("."));
			list[i] = FileUtils.SDPATH + Str + ".JPEG";
			PostPicture(list[i], i, Bimp.drr.size() - 1);
		}

		if (Bimp.drr.size() <= 0) {
			PublishTopic(0);
		}

	}
	

}
