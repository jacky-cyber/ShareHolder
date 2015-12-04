package com.example.shareholders;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/*** 这个是导航界面activity */
@ContentView(R.layout.activity_index)
public class IndexActivity extends Activity {

	@ViewInject(R.id.vp_index_viewpager)
	private ViewPager pager;

	private ArrayList<View> listView = new ArrayList<View>();

	private View view1, view2, view3, view4;

	private ImageView iv_dot1, iv_dot2, iv_dot3, iv_dot4;

	private MyPagerAdapter adapter;

	private LayoutInflater inflater;
	private int currentIndex = 0;

	// 点击开始按钮
	@ViewInject(R.id.btn_index)
	private TextView startBt;
	
	private boolean isFirstLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		isFirstLogin = RsSharedUtil.getBoolean(IndexActivity.this,
				AppConfig.ISDEBUTE, true);
		if (!isFirstLogin) {
			Intent intent = new Intent(IndexActivity.this, StartActivity.class);
			startActivity(intent);
			finish();

		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initPager();
		initView();
	}

	private void initPager() {
		
		iv_dot1 = (ImageView) findViewById(R.id.pagerdot1);
		iv_dot2 = (ImageView) findViewById(R.id.pagerdot2);
		iv_dot3 = (ImageView) findViewById(R.id.pagerdot3);
		iv_dot4 = (ImageView) findViewById(R.id.pagerdot4);

		inflater = LayoutInflater.from(this);
		view1 = inflater.inflate(R.layout.pager_index1, null);
		view2 = inflater.inflate(R.layout.pager_index2, null);
		view3 = inflater.inflate(R.layout.pager_index3, null);
		view4 = inflater.inflate(R.layout.pager_index4, null);

		listView.add(view1);
		listView.add(view2);
		listView.add(view3);
		listView.add(view4);

		adapter = new MyPagerAdapter();
		pager.setAdapter(adapter);
		pager.setCurrentItem(currentIndex);

	}

	private void initView() {
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {

				case 0:
					clearTag();
					iv_dot1.setImageDrawable(getResources().getDrawable(
							R.drawable.welcomepoint1));

					break;
				case 1:
					clearTag();
					iv_dot2.setImageDrawable(getResources().getDrawable(
							R.drawable.welcomepoint1));

					break;
				case 2:
					clearTag();
					iv_dot3.setImageDrawable(getResources().getDrawable(
							R.drawable.welcomepoint1));
					break;
				case 3:
					clearTag();
					iv_dot4.setImageDrawable(getResources().getDrawable(
							R.drawable.welcomepoint1));
					break;

				}
				currentIndex = arg0;
				if (arg0 == 3) {
					startBt.setVisibility(View.VISIBLE);
				} else {
					startBt.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@OnClick({ R.id.btn_index })
	private void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.btn_index:
			RsSharedUtil.putBoolean(IndexActivity.this, "isFirstLogin", false);
			intent.setClass(getApplicationContext(), StartActivity.class);
			startActivity(intent);
			finish();

			break;

		default:
			break;
		}
	}

	private void clearTag() {
		iv_dot1.setImageDrawable(getResources().getDrawable(
				R.drawable.welcomepoint2));
		iv_dot2.setImageDrawable(getResources().getDrawable(
				R.drawable.welcomepoint2));
		iv_dot3.setImageDrawable(getResources().getDrawable(
				R.drawable.welcomepoint2));
		iv_dot4.setImageDrawable(getResources().getDrawable(
				R.drawable.welcomepoint2));
	}

	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listView.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub

			((ViewPager) container).addView(listView.get(position), 0);

			return listView.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View view, int position, Object arg2) {
			((ViewPager) view).removeView(listView.get(position));
		}

	}

}
