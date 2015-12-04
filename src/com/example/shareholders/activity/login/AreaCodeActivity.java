package com.example.shareholders.activity.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.adapter.SortAdapter;
import com.example.shareholders.common.CharacterParser;
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.PinyinComparator;
import com.example.shareholders.common.SideBar;
import com.example.shareholders.common.SideBar.OnTouchingLetterChangedListener;
import com.example.shareholders.common.SortModel;
import com.example.shareholders.config.AppConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_area_code)
public class AreaCodeActivity extends Activity {

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private LinearLayout ll_search;

	// 返回
	@ViewInject(R.id.tv_return)
	private TextView tv_return;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<HashMap<String, String>> AreaData;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		// 获取地区代码
		getAreaCode();
	}

	public void getAreaCode() {
		String url = AppConfig.URL_ACCOUNT + "area-code.json";
		/*
		 * String url = AppConfig.URL_USER + "about.json?access_token=" + "" +
		 * RsSharedUtil.getInt(getApplicationContext(), AppConfig.ACCESS_TOKEN);
		 */// cj
		/* RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN); */// cj
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("地区代码result", response.toString());
						JSONArray jsonArray;
						try {
							jsonArray = new JSONArray(response.toString());
							AreaData = new ArrayList<HashMap<String, String>>();
							for (int i = 0; i < jsonArray.length(); i++) {
								HashMap<String, String> area = new HashMap<String, String>();
								area.put("name", jsonArray.getJSONObject(i)
										.getString("name"));
								area.put("code", jsonArray.getJSONObject(i)
										.getString("code"));
								AreaData.add(area);
							}
							// 初始化地区代码
							initViews();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("地区代码error", error.toString());
					}
				});
		stringRequest.setTag("getAreaCode");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("getAreaCode");
		super.onDestroy();
	};

	private void initViews() {
		// 下划线
		tv_return.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		ll_search = (LinearLayout) findViewById(R.id.ll_search);

		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				String code = AreaData.get(position).get("code");
				Log.d("code", code);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("code", code);
				intent.putExtras(bundle);
				AreaCodeActivity.this.setResult(RESULT_OK, intent);
				finish();
			}
		});

		SourceDateList = filledData(AreaData);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mClearEditText.getText().toString().equals("")) {
					ll_search.setVisibility(View.VISIBLE);
				} else {
					ll_search.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	@OnClick({ R.id.tv_return })
	public void onclick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.tv_return:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<HashMap<String, String>> areaData) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < areaData.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(areaData.get(i).get("name") + "  +"
					+ areaData.get(i).get("code"));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(areaData.get(i).get(
					"name")
					+ "  +" + areaData.get(i).get("code"));
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

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
			if (event.getRawX() > left && event.getRawX() < right
					&& event.getRawY() > top && event.getRawY() < bottom) {
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
