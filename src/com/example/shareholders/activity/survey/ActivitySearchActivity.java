package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.jacksonModel.survey.SearchHistory;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_activity_search)
public class ActivitySearchActivity extends Activity implements
		OnItemClickListener {
	
	@ViewInject(R.id.lv_search_history)
	private ListView lv_search;
	
	@ViewInject(R.id.et_search_text)
	private EditText et_search;
	
	@ViewInject(R.id.tv_ac_search)
	private TextView tv_search;
	
	
	@ViewInject(R.id.tv_clear)
	private TextView tv_delete;
	
	private List<SearchHistory> al_text;

	private ActivitySearchAdapter adapter;
	
	
	private DbUtils dbUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		tv_delete.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		

	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		init();
	}

	// 功能：初始化
	public void init() {
		dbUtils=DbUtils.create(this);
		
		try {
			al_text = dbUtils.findAll(SearchHistory.class);
			
			Log.d("search_db", "success");
		} catch (DbException e1) {
			
			e1.printStackTrace();
		}
		if (al_text==null) {
			al_text=new ArrayList<SearchHistory>();
			tv_delete.setVisibility(View.GONE);
			Log.d("mm_search_db", "mm_success");
		}
		else if(al_text.size()>0){
			tv_delete.setVisibility(View.VISIBLE);
		}
		
		adapter=new ActivitySearchAdapter(this, al_text);

		lv_search.setAdapter(adapter);
		
		lv_search.setOnItemClickListener(this);

		// 搜索按钮的监听事件
		tv_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (et_search.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "内容不能为空",
							Toast.LENGTH_SHORT).show();
				} else {
					SearchHistory  history=new SearchHistory();
					history.setHistory(et_search.getText().toString());
					try {
						dbUtils.saveOrUpdate(history);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Intent intent = new Intent(ActivitySearchActivity.this,
							SelectActivity.class);
					intent.putExtra("test", et_search.getText().toString());
					startActivity(intent);
				}
			}
		});
	}

	@OnClick({ R.id.rl_return,R.id.tv_clear })
	private void onClick(View v) {
		switch (v.getId()) {

		case R.id.rl_return:
			finish();
			break;
			
		case R.id.tv_clear:
			try {
				dbUtils.dropTable(SearchHistory.class);
				al_text.clear();
				adapter.notifyDataSetChanged();
				tv_delete.setVisibility(View.GONE);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		et_search.setText("" + al_text.get(position).getHistory());

	}



	public class ActivitySearchAdapter extends BaseAdapter {
		private ViewHolder holder;
		private List<SearchHistory> list;
		private Context context;


		public ActivitySearchAdapter(Context context,
				List<SearchHistory> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;

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
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_activity_search, null);

				holder.tv_search_text = (TextView) view
						.findViewById(R.id.tv_search);
				holder.iv_search_delete=(ImageView)view
						.findViewById(R.id.iv_search);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.iv_search_delete.setVisibility(View.GONE);
			holder.tv_search_text.setText(list.get(position).getHistory());
			return view;
		}

		class ViewHolder {

			TextView tv_search_text;
			ImageView iv_search_delete;

		}
	}

	/**
	 * 点击空白处，输入法消失
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
