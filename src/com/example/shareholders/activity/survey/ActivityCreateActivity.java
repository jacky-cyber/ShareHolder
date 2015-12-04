package com.example.shareholders.activity.survey;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.fragment.Fragment_ActivityCreate1;
import com.example.shareholders.fragment.Fragment_ActivityCreate2;
import com.example.shareholders.fragment.Fragment_ActivityCreate3;
import com.example.shareholders.view.DialogManager;
import com.example.shareholders.view.DialogManager2;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_activity_create)
public class ActivityCreateActivity extends FragmentActivity {

	@ViewInject(R.id.vp_activity_create)
	private MyViewPager viewPager;
	private ArrayList<Fragment> fragments;
	private Fragment_ActivityCreate1 fragment1;
	private Fragment_ActivityCreate2 fragment2;
	private Fragment_ActivityCreate3 fragment3;
	private RequestQueue volleyRequestQueue;
	private DialogManager2 dialogManager;

	@ViewInject(R.id.title_text)
	private TextView title_text;
	public static String uuid;
	public static int sign;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
		Intent intent = getIntent();
		Bundle bundle = getIntent().getExtras();
		dialogManager = DialogManager2.getInstance(this);
		uuid = bundle.getString("uuid");
		sign = intent.getIntExtra("sign", 0);
		if (sign == 0) {
			title_text.setText(R.string.create_title);
		} else {
			title_text.setText(R.string.create_edit);
		}

		initFragments();

	}

	@OnClick(R.id.rl_return)
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			changePage();
			break;

		default:
			break;
		}
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

	private void changePage() {
		switch (viewPager.getCurrentItem()) {
		// 在第0页，退出activity
		case 0:
			dialogManager.ShowBlueDialog();
			dialogManager
					.setBlueMessage("是否确定退出\n若退出,将不保存草稿");
			dialogManager
					.setBluePositiveButton(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							finish();
						}
					});
			dialogManager
					.setBlueNegativeButton(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialogManager.dismiss();
						}
					});
		
			break;
		// 在第1，2页，返回前一页
		default:
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
			break;
		}
	}

	private void initFragments() {
		fragment1 = new Fragment_ActivityCreate1();
		fragment2 = new Fragment_ActivityCreate2();
		fragment3 = new Fragment_ActivityCreate3();

		fragments = new ArrayList<Fragment>();
		fragments.add(fragment1);
		fragments.add(fragment2);
		fragments.add(fragment3);

		viewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
	}

	class MyPageAdapter extends FragmentPagerAdapter {

		public MyPageAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			changePage();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
