package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.EasyTouchAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_easy_touch)
public class EasyTouchActivity extends Activity {

	@ViewInject(R.id.tv_et_text_breviary)
	private TextView tv_et_text_breviary;
	@ViewInject(R.id.tv_et_text_expand)
	private TextView tv_et_text_expand;
	private int text_line;
	private boolean text_line_flag;
	@ViewInject(R.id.lv_et)
	private ListView lv_et_time;
	private ArrayList<Integer> al_etl_right;
	private ArrayList<String> al_etl_name;
	private ArrayList<String> al_etl_title;
	private ArrayList<String> al_etl_text;
	private ArrayList<String> al_etl_time;
	private ArrayList<HashMap<String, Object>> et_map;
	private EasyTouchAdapter et_adapter;
	int num = 4;
	String text_line_text = "用这个方法就能很简单用这个方" + "法就能很简单用这个方法就能很简单用这个方法就能很简单用这"
			+ "个方法就能很简单用这个方法就能很简单用这个方法就能很用" + "这个方法就能很简单"
			+ "用这个方法就能很简单用这个方法就能很简单简单";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initView();
		initList();
		for (int i = 0; i < num; i++) {
			al_etl_right.add(R.drawable.img_load);
			al_etl_name.add("He Jv");
			al_etl_text.add("现场活动好热烈啊，希望大家能够玩的开心，也希望大家真正获取到自己想要的信息");
			al_etl_time.add("07:12");
			al_etl_title.add("一回头就找到出路");

		}
		et_PutHashMap(num);
	}

	public void initView() {
		/*
		 * 监听textview的行数，实现收起全文的功能
		 */
		tv_et_text_breviary.setText(text_line_text);
		tv_et_text_breviary.post(new Runnable() {
			@Override
			public void run() {
				text_line = tv_et_text_breviary.getLineCount();
				if (tv_et_text_breviary.getLineCount() <= 1) {
					tv_et_text_breviary.setLines(1);
					tv_et_text_expand.setVisibility(View.GONE);
				} else if (tv_et_text_breviary.getLineCount() == 2) {
					tv_et_text_breviary.setLines(2);
					tv_et_text_expand.setVisibility(View.GONE);

				} else {
					tv_et_text_breviary.setLines(2);
					tv_et_text_expand.setText("全文");
				}
			}
		});

	}

	public void initList() {
		al_etl_right = new ArrayList<Integer>();
		al_etl_name = new ArrayList<String>();
		al_etl_text = new ArrayList<String>();
		al_etl_time = new ArrayList<String>();
		al_etl_title = new ArrayList<String>();
		et_map = new ArrayList<HashMap<String, Object>>();

	}

	public void et_PutHashMap(int num) {
		for (int i = 0; i < num; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("iv_etl_right", al_etl_right.get(i));
			item.put("tv_etl_name", al_etl_name.get(i));
			item.put("tv_etl_text", al_etl_text.get(i));
			item.put("tv_etl_title", al_etl_title.get(i));
			item.put("tv_etl_time", al_etl_time.get(i));
			et_map.add(item);
			et_adapter = new EasyTouchAdapter(getApplicationContext(), et_map);
			lv_et_time.setAdapter(et_adapter);

		}

	}

	@OnClick({ R.id.tv_et_text_expand, R.id.iv_easy_return })
	private void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_et_text_expand:
			tv_et_text_breviary.post(new Runnable() {
				@Override
				public void run() {
					if (!text_line_flag) {
						// Toast.makeText(getApplicationContext(), ""+
						// text_line, Toast.LENGTH_SHORT).show();
						tv_et_text_breviary.setLines(text_line);

						tv_et_text_expand.setText("收起");

					} else {
						tv_et_text_breviary.setLines(2);

						tv_et_text_expand.setText("全文");

					}
					text_line_flag = !text_line_flag;
				}
			});

			break;
		case R.id.iv_easy_return:
			finish();
			break;
		}
	}

}
