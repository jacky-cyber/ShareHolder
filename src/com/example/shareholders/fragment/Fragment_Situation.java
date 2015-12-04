package com.example.shareholders.fragment;

import java.util.ArrayList;

import com.example.shareholders.MainActivity;
import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Fragment_Situation extends Fragment {

	// 股票类型
	private String stockType = "A_SHARE";
	@ViewInject(R.id.iv_price_1)
	ImageView head_iv_1;
	@ViewInject(R.id.iv_price_2)
	ImageView head_iv_2;
	@ViewInject(R.id.iv_price_3)
	ImageView head_iv_3;
	@ViewInject(R.id.situation_ViewPager)
	private ViewPager viewPager;
	private ViewPagerAdapter fPagerAdapter;
	private ArrayList<Fragment> pagerFargmentList;
	View mview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_situation, container, false);
		ViewUtils.inject(this, mview);
		initview();
		return mview;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.d("asdasduy50000000", "asdasduy50000000");
		super.onResume();
	}
	private void initview() {

		pagerFargmentList = new ArrayList<Fragment>();
		// 暂时用这几个测试，待会改
		Fragment fragment_a_shares = new Fragment_A_Shares();
		Fragment fragment_plate = new Fragment_Plate();

		pagerFargmentList.add(fragment_a_shares);
		pagerFargmentList.add(fragment_plate);

		fPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), pagerFargmentList);
		viewPager.setAdapter(fPagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
	
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				sethandler();
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

	@OnClick({ R.id.rb_situation1, R.id.rb_situation2, R.id.rb_situation3 })
	private void onclick(View view) {
		
		head_iv_1.setVisibility(View.GONE);
		head_iv_2.setVisibility(View.GONE);
		head_iv_3.setVisibility(View.GONE);
		Intent intent;
		switch (view.getId()) {
		case R.id.rb_situation1:
			viewPager.setCurrentItem(0);

			stockType = "A_SHARE";
			intent = new Intent(); // 要发送的内容
			intent.putExtra("stockType", stockType);
			intent.setAction("getstockType"); // 设置广播的action
			getActivity().sendBroadcast(intent); // 发送广播

			head_iv_1.setVisibility(View.VISIBLE);
			break;
		case R.id.rb_situation2:
			viewPager.setCurrentItem(0);

			stockType = "B_SHARE";
			intent = new Intent(); // 要发送的内容
			intent.putExtra("stockType", stockType);
			intent.setAction("getstockType"); // 设置广播的action
			getActivity().sendBroadcast(intent); // 发送广播

			head_iv_2.setVisibility(View.VISIBLE);
			break;
		case R.id.rb_situation3:
			viewPager.setCurrentItem(1);
			head_iv_3.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	
	public void sethandler(){
		ViewPagerAdapter cAdapter = (ViewPagerAdapter) viewPager.getAdapter();
		Fragment_A_Shares fragment_A_Shares = (Fragment_A_Shares) cAdapter.instantiateItem(viewPager, 0);
		fragment_A_Shares.sethandler(viewPager.getCurrentItem());
	}

	public int getviewpagerid() {
		return viewPager.getCurrentItem();
	}
}
