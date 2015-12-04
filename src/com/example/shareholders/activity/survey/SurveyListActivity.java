package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MySurveyListActivity;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.ExpandableLayout;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.FilterFragment;
import com.example.shareholders.fragment.SortFragment;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

@ContentView(R.layout.activity_survey_list)
public class SurveyListActivity extends FragmentActivity {

	
	// 排序
	@ViewInject(R.id.rl_sort)
	private RelativeLayout rl_sort;

	// 排序
	@ViewInject(R.id.tv_sort)
	private TextView tv_sort;

	// 排序三角
	@ViewInject(R.id.iv_sort)
	private ImageView iv_sort;

	// 状态
	@ViewInject(R.id.rl_state)
	private RelativeLayout rl_state;

	// 状态
	@ViewInject(R.id.tv_state)
	private TextView tv_state;

	// 状态三角
	@ViewInject(R.id.iv_state)
	private ImageView iv_state;

	// 筛选
	@ViewInject(R.id.rl_filter)
	private RelativeLayout rl_filter;

	// 筛选
	@ViewInject(R.id.tv_filter)
	private TextView tv_filter;

	// 盛放Fragment的ViewPager
	@ViewInject(R.id.vp_list)
	public static MyViewPager vp_list;

	// 筛选三角
	@ViewInject(R.id.iv_filter)
	private ImageView iv_filter;

	// 滑动伸展布局
	@ViewInject(R.id.expand_layout)
	ExpandableLayout expand_layout;

	private SortFragment sortFragment;
	private FilterFragment filterFragment;
	private ArrayList<Fragment> fragments;

	private MyListView lv_popup;
	private MyListViewAdatper adapter;
	private PopupWindow popupWindow;

	private String[] sorts;
	private String[] states;

	private List<String> lists = null;
	private List<Boolean> isClicks = null;

	// 记录排序列表哪一项被点击
	private ArrayList<Boolean> isClicks_sort = null;
	// 记录状态列表哪一项被点击
	private ArrayList<Boolean> isClicks_state = null;

	public static FragmentTransaction transaction;

	/**
	 * 三个Tab的点击状态
	 */
	private boolean sortClick = false;
	private boolean stateClick = false;
	private boolean filterClick = false;

	private ViewPagerAdapter vpAdapter;

	private boolean hasHiden = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		initTab();
		initTabList();
		initFragments(); // 加载Fragment到ViewPager中

		// 先将popupwindow初始化，避免从filterFragment返回并重置popupwindow时出错
		// initPopup();

		// initSortFagrment(); // 加载SortFragment
	}

	/**
	 * popupWindow的选择条件全部设为默认
	 */
	public void setClicks() {

		// sortClick=true;
		// clearTab(tv_sort, iv_sort);
		// initPopupInfo("sort");
		// initPopupWindow("sort");
		// if (filterClick) {
		// vp_list.setCurrentItem(0);
		// filterClick = false;
		// }

		for (int i = 0; i < isClicks_sort.size(); i++) {
			isClicks_sort.set(i, false);
		}

		for (int i = 0; i < isClicks_state.size(); i++) {
			isClicks_state.set(i, false);
		}

		isClicks_sort.set(0, true);
		isClicks_state.set(0, true);
		Log.d("liang_isclicks", isClicks.toString());
		adapter.notifyDataSetChanged();
	}

	/**
	 * 定义排序和筛选两个列表的信息
	 */
	private void initTabList() {
		sorts = new String[] {
				getResources().getString(R.string.time_in_ascending_order),
				getResources().getString(R.string.time_in_descending_order),
				getResources().getString(R.string.name_in_ascending_order),
				getResources().getString(R.string.name_in_descending_order),
				getResources().getString(R.string.heat_in_ascending_order) };

		states = new String[] { getResources().getString(R.string.all),
				getResources().getString(R.string.signing_up),
				getResources().getString(R.string.under_way),
				getResources().getString(R.string.past_survey) };

		isClicks_sort = new ArrayList<Boolean>();
		isClicks_state = new ArrayList<Boolean>();

		for (int i = 0; i < sorts.length; i++) {
			if (i == 0) {
				isClicks_sort.add(true);
			} else {
				isClicks_sort.add(false);
			}
		}

		for (int i = 0; i < states.length; i++) {
			if (i == 0) {
				isClicks_state.add(true);
			} else {
				isClicks_state.add(false);
			}
		}

	}

	/**
	 * 将Fragment加载到ViewPager中
	 */
	private void initFragments() {
		sortFragment = new SortFragment();
		filterFragment = new FilterFragment();

		fragments = new ArrayList<Fragment>();

		fragments.add(sortFragment);
		fragments.add(filterFragment);
		vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        
		vp_list.setAdapter(vpAdapter);
	}

	/**
	 * 弹出popupWindow时，动态改变其数据
	 * 
	 * @param category
	 */
	private void initPopupInfo(String category) {

		lists = new ArrayList<String>();
		if (category.equals("sort")) {
			for (int i = 0; i < sorts.length; i++) {
				lists.add(sorts[i]);
				// isClicks=new ArrayList<Boolean>(isClicks_sort);
			}
		} else if (category.equals("state")) {
			for (int i = 0; i < states.length; i++) {
				lists.add(states[i]);
				// isClicks=new ArrayList<Boolean>(isClicks_state);
			}
		}

	}

	/**
	 * 给三个Tab加下划线
	 */
	private void initTab() {
		tv_sort.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_state.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_filter.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

	}

	/**
	 * 三个Tab的 状态都恢复到默认状态
	 */
	public void clearAllTab() {
		filterClick = false;

		tv_sort.setTextColor(getResources().getColor(
				R.color.tab_tv_color_normal));
		tv_state.setTextColor(getResources().getColor(
				R.color.tab_tv_color_normal));
		tv_filter.setTextColor(getResources().getColor(
				R.color.tab_tv_color_normal));

		iv_sort.setImageResource(R.drawable.ico_zhuangtai2);
		iv_state.setImageResource(R.drawable.ico_zhuangtai2);
		iv_filter.setImageResource(R.drawable.ico_zhuangtai2);
	}

	/**
	 * 判断状态和排序是否被点击
	 * 
	 * @return
	 */
	public boolean getClick() {
		return (sortClick || stateClick);
	}

	/**
	 * 设置三个Tab的点击切换事件
	 * 
	 * @param tv
	 * @param iv
	 */
	private void clearTab(TextView tv, ImageView iv) {
		tv_sort.setTextColor(getResources().getColor(
				R.color.tab_tv_color_normal));
		tv_state.setTextColor(getResources().getColor(
				R.color.tab_tv_color_normal));
		tv_filter.setTextColor(getResources().getColor(
				R.color.tab_tv_color_normal));

		iv_sort.setImageResource(R.drawable.ico_zhuangtai2);
		iv_state.setImageResource(R.drawable.ico_zhuangtai2);
		iv_filter.setImageResource(R.drawable.ico_zhuangtai2);

		tv.setTextColor(getResources().getColor(R.color.tab_tv_color_selected));
		iv.setImageResource(R.drawable.ico_zhuagntai);
	}

	private void initPopupWindow(String category) {
		// ImageLoader.getInstance().displayImage(uri, imageAware)
		View converView = LayoutInflater.from(this).inflate(
				R.layout.popup_sort_layout, null);
		lv_popup = (MyListView) converView.findViewById(R.id.lv_popup);

		adapter = new MyListViewAdatper(this, category);

		lv_popup.setAdapter(adapter);

		popupWindow = new PopupWindow(converView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(rl_state, 0, 0);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				clearAllTab();
			}
		});

	}

	public void initPopup() {
		clearTab(tv_sort, iv_sort);
		initPopupInfo("sort");
		// initPopupWindow("sort");

		View converView = LayoutInflater.from(this).inflate(
				R.layout.popup_sort_layout, null);
		lv_popup = (MyListView) converView.findViewById(R.id.lv_popup);

		adapter = new MyListViewAdatper(this, "sort");

		lv_popup.setAdapter(adapter);

		popupWindow = new PopupWindow(converView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		if (filterClick) {
			vp_list.setCurrentItem(0);
			filterClick = false;
		}

	}

	@OnClick({ R.id.rl_sort, R.id.rl_state, R.id.rl_filter, R.id.rl_return,
			R.id.rl_search, R.id.rl_search_bg })
	public void onClick(View v) {
		switch (v.getId()) {

		// 返回
		case R.id.rl_return:
			finish();
			break;

		case R.id.rl_sort:
			sortClick = true;

			Log.d("liang_123", (sortClick || stateClick) + "");

			clearTab(tv_sort, iv_sort);
			initPopupInfo("sort");
			initPopupWindow("sort");
			if (filterClick) {
				vp_list.setCurrentItem(0);
				filterClick = false;
			}
			break;
		case R.id.rl_state:
			stateClick = true;
			Log.d("liang_123", (sortClick || stateClick) + "");
			clearTab(tv_state, iv_state);
			initPopupInfo("state");
			initPopupWindow("state");
			if (filterClick) {
				vp_list.setCurrentItem(0);
				filterClick = false;
			}
			break;
		case R.id.rl_filter:
			// filterFragment.initAllInfo();

			if (filterClick) {
				vp_list.setCurrentItem(0);

				clearAllTab();
				filterClick = false;
			} else {
				vp_list.setCurrentItem(1);
				filterClick = true;
				clearTab(tv_filter, iv_filter);
			}
			break;
		case R.id.rl_search:
			String userUuid = RsSharedUtil.getString(this,
					AppConfig.UUID);

			startActivity(new Intent(SurveyListActivity.this, MySurveyListActivity.class)
					.putExtra("userUuid", userUuid));
			break;
		// 点击搜索框
		case R.id.rl_search_bg:
			if (!hasHiden) {
				expand_layout.DownAndUp();
				hasHiden = true;
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivity(new Intent(SurveyListActivity.this,
							ActivitySearchActivity.class));

				}
			}) {
			}.start();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onRestart() {
		if (hasHiden) {
			expand_layout.DownAndUp();
			hasHiden = false;
		}

		super.onRestart();
	}

	class MyListViewAdatper extends BaseAdapter {

		private LayoutInflater inflater;
		private String category; // 标识是哪个popupWindow

		public MyListViewAdatper(Context context, String category) {
			inflater = LayoutInflater.from(context);
			this.category = category;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
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
		public View getView(final int position, View converView, ViewGroup arg2) {
           
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				converView = inflater.inflate(R.layout.item_popup_survey_list,
						arg2, false);
				viewHolder.rl = (RelativeLayout) converView
						.findViewById(R.id.rl_ascending);
				viewHolder.tv = (TextView) converView
						.findViewById(R.id.tv_ascending);
				viewHolder.iv = (ImageView) converView
						.findViewById(R.id.iv_ascending);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.tv.setText(lists.get(position));

			if (category.equals("sort")) {
				isClicks = isClicks_sort;
			} else if (category.equals("state")) {
				isClicks = isClicks_state;
			}

			if (isClicks.get(position)) {
				viewHolder.tv.setTextColor(getResources().getColor(
						R.color.tab_tv_color_selected));
				viewHolder.iv.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tv.setTextColor(getResources().getColor(
						R.color.tab_tv_color_normal));
				viewHolder.iv.setVisibility(View.GONE);
			}

			viewHolder.rl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					for (int i = 0; i < isClicks.size(); i++) {
						isClicks.set(i, false);
					}

					isClicks.set(position, true);
					recordFilterState();
					notifyDataSetChanged();
					
					popupWindow.dismiss();
					// 如果列表显示的是筛选数据，那么保留筛选条件，重新获取后台数据
					if (RsSharedUtil.getBoolean(getApplication(), "filter",
							false)) {

						int pageSize = sortFragment.getPageSize();
						sortFragment.clearDatas();
						sortFragment.setFilterInfo(0, pageSize);
//						new Thread(new Runnable(
//								) {
//							
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//
//
//								        try {
//											Thread.sleep(300);
//										} catch (InterruptedException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}   
//
//							}
//
//						}).run();
						sortFragment.Reflash();
					} else {// 否则，对所有后台数据进行筛选
						int pageSize = sortFragment.getPageSize();
						sortFragment.clearDatas();
						sortFragment.setLatestInfo(pageSize, 0);
//						new Thread(new Runnable(
//								) {
//							
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//
//
//								        try {
//											Thread.sleep(300);
//										} catch (InterruptedException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}   
//
//							}
//
//						}).run();
						sortFragment.Reflash();
					}

				}

				/**
				 * 记录点击的哪一项，用sharepreference记录筛选条件
				 */
				private void recordFilterState() {
					// 点击的是排序
					if (category.equals("sort")) {
						switch (position) {
						case 0:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_sortType", "timeDesc");
							break;
						case 1:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_sortType", "timeSort");
							break;
						case 2:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_sortType", "nameSort");
							break;
						case 3:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_sortType", "nameDesc");
							break;
						case 4:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_sortType", "heatsort");
							break;

						default:
							break;
						}
					} else if (category.equals("state")) {// 点击的是状态
						switch (position) {
						case 0:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_surveyState", "null");
							break;
						case 1:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_surveyState", "ENROLLING");
							Log.w("thelasttwo",RsSharedUtil.getString(getApplicationContext(), "filter_surveyState")+"不是全部");
							break;
						case 2:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_surveyState", "SURVEYING");
							Log.w("thelasttwo",RsSharedUtil.getString(getApplicationContext(), "filter_surveyState")+"不是全部");
							break;
						case 3:
							RsSharedUtil.putString(getApplicationContext(),
									"filter_surveyState", "SURVEYEND");
							Log.w("thelasttwo",RsSharedUtil.getString(getApplicationContext(), "filter_surveyState")+"不是全部");
							break;
						default:
							break;
						}
					}
				}

			});
			return converView;

		}

		class ViewHolder {
			RelativeLayout rl;
			TextView tv;
			ImageView iv;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}
