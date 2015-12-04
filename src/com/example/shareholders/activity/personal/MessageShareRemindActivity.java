package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.adapter.MessgaeMySurveyAdapter;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_person_message_shareremind)
public class MessageShareRemindActivity extends Activity {
	@ViewInject(R.id.person_message_shareremind)
	private ListView MessageList;

	@ViewInject(R.id.tv_stock_set)
	private ImageView iv_setImageView;
	@ViewInject(R.id.rl_no_content)
	private RelativeLayout rl_no_content;

	// 环信conversation
	EMConversation conversation;
	// 股票信息
	List<EMMessage> messages = new ArrayList<EMMessage>();

	private MessgaeMySurveyAdapter adapter;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 获取数据
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		conversation = EMChatManager.getInstance().getConversation(
				"security_alert");
		messages = conversation.getAllMessages();
		list.clear();
		if (adapter != null) {

			if (messages.size() == 0) {
				adapter.notifyDataSetChanged();
			}
		}
		for (int i = 0; i < messages.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TextMessageBody body = (TextMessageBody) messages.get(i).getBody();
			map.put("content", body.getMessage());
			map.put("time", messages.get(i).getMsgTime());
			try {
				map.put("senderId",
						messages.get(i).getStringAttribute("senderId"));
			} catch (EaseMobException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(map);
		}
		if (list.size()==0) {
			rl_no_content.setVisibility(View.VISIBLE);
		}else {
			adapter = new MessgaeMySurveyAdapter(this, list);
			MessageList.setAdapter(adapter);
			//搜索股票，跳转到个股详情
			MessageList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					searchStocks(list.get(position).get("senderId").toString());
				}
			});
		}
		
		// 设置股价未读数清零
		conversation.resetUnreadMsgCount();
	}

	@OnClick({ R.id.message_center_return, R.id.tv_stock_set })
	private void Onclick(View view) {
		switch (view.getId()) {
		case R.id.message_center_return:
			finish();
			break;
		case R.id.tv_stock_set:
			Intent intent = new Intent(this, MessageCenterSetting.class);
			intent.putExtra("type", "stock");
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	/**
	 * 搜索股票
	 * @param symbol 股票代码
	 */
	public void searchStocks(String symbol){
		String url = AppConfig.URL_QUOTATION +"search.json?access_token=";
		url+=RsSharedUtil.getString(getApplicationContext(), AppConfig.ACCESS_TOKEN);
		url = url+"&pageIndex=0&pageSize=1&keyword="+symbol;
		StringRequest stringRequest = new StringRequest(url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					JSONArray array = new JSONArray(response);
					JSONObject object = new JSONObject(array.get(0).toString());
					ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("symbol", object.get("symbol").toString());
					map.put("shortname", object.get("shortName").toString());
					map.put("securityType", object.get("securityType").toString());
					stocks.add(map);
					Intent intent = new Intent(getApplicationContext(), MyStockDetailsActivity.class);
					intent.putExtra("stocks", stocks);
					intent.putExtra("position", 0);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		stringRequest.setTag("searchStock");
		MyApplication.getRequestQueue().add(stringRequest);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("searchStock");
		super.onDestroy();
	}

}
