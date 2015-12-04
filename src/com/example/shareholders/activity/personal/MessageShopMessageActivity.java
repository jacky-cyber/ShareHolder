package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.MessgaeMySurveyAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_person_message_shopmessage)
public class MessageShopMessageActivity extends Activity {
	@ViewInject(R.id.person_message_shopmessage)
	private ListView MessageList;

	private MessgaeMySurveyAdapter adapter;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initView();
	}

	private void initView() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已完成，请签收");
		map.put("time", "12:20");
		list.add(map);
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已发货，请注意查收");
		map.put("time", "12:20");
		list.add(map);
		map = new HashMap<String, Object>();
		map.put("content", "【物流助手】您有一笔订单已完成，请签收");
		map.put("time", "12:20");
		list.add(map);

		adapter = new MessgaeMySurveyAdapter(this, list);
		MessageList.setAdapter(adapter);
	}

}
