package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_survey)
public class SurveyActivity extends Activity {
	@ViewInject(R.id.lv_survey_person)
	private ListView lv_person_list;
	private ArrayList<String> al_survey_content;
	private ArrayList<String> al_survey_name;
	private ArrayList<HashMap<String, Object>> hashmap;
	private SurveyActivityAdapter sa_adpter;
	private int itemnum = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
		/*
		 * 静态内容到时可根据实际需求动态添加
		 */
		for (int i = 0; i < itemnum; i++) {
			al_survey_content.add("可以看到何广浩和黎法鹏在更衣间吗");
			al_survey_name.add("韩梅梅");

		}
		putHashMap(itemnum);
	}

	public void init() {
		al_survey_content = new ArrayList<String>();
		al_survey_name = new ArrayList<String>();
		hashmap = new ArrayList<HashMap<String, Object>>();

	}

	public void putHashMap(int num) {
		for (int i = 0; i < itemnum; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("tv_survey_content", al_survey_content.get(i));
			item.put("tv_survey_name", al_survey_name.get(i));
			hashmap.add(item);
			sa_adpter = new SurveyActivityAdapter(getApplicationContext(),
					hashmap);
			lv_person_list.setAdapter(sa_adpter);
		}
	}

	/*
	 * listView的适配器adapter
	 */
	public class SurveyActivityAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public SurveyActivityAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_survey_activity, null);

				holder.tv_survey_content = (TextView) view
						.findViewById(R.id.tv_survey_item_content);
				holder.tv_survey_name = (TextView) view
						.findViewById(R.id.tv_survey_item_name);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_survey_content.setText((CharSequence) list.get(position)
					.get("tv_survey_content"));
			holder.tv_survey_name.setText((CharSequence) list.get(position)
					.get("tv_survey_name"));

			return view;
		}

		class ViewHolder {

			TextView tv_survey_content;
			TextView tv_survey_name;

		}
	}
}
