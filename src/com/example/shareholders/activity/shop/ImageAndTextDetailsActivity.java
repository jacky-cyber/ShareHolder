package com.example.shareholders.activity.shop;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.NoScrollViewPager;
import com.example.shareholders.fragment.Fragment_GoodsParam;
import com.example.shareholders.fragment.Fragment_ImageAndTextDetails;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_image_and_text_details)
public class ImageAndTextDetailsActivity extends FragmentActivity {

	@ViewInject(R.id.tv_gd_details_it)
	private TextView tv_gd_details_it;
	@ViewInject(R.id.iv_gd_details_it_bottom)
	private ImageView iv_gd_details_it_bottom;
	@ViewInject(R.id.iv_gd_details_param_bottom)
	private ImageView iv_gd_details_param_bottom;
	@ViewInject(R.id.tv_gd_details_param)
	private TextView tv_gd_details_param;
	@ViewInject(R.id.vp_gd_details)
	private NoScrollViewPager vp_gd_details;

	Fragment_ImageAndTextDetails fragment_ImageAndTextDetails;
	Fragment_GoodsParam fragment_GoodsParam;
	private ArrayList<Fragment> fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initFrag();
	}

	@OnClick({ R.id.tv_gd_details_it, R.id.tv_gd_details_param,
		R.id.title_fs_note,R.id.rl_return })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.tv_gd_details_it:
			onTab(0);
			break;
		case R.id.tv_gd_details_param:
			onTab(1);
			break;
		case R.id.title_fs_note:
			finish();
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void initFrag() {
		fragmentList = new ArrayList<Fragment>();
		fragment_ImageAndTextDetails = new Fragment_ImageAndTextDetails();
		fragment_GoodsParam = new Fragment_GoodsParam();
		fragmentList.add(fragment_ImageAndTextDetails);
		fragmentList.add(fragment_GoodsParam);
		vp_gd_details.setAdapter(new ViewPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		vp_gd_details.setCurrentItem(0);
		vp_gd_details.setOffscreenPageLimit(2);
		vp_gd_details.setScrollEnble(true);
		vp_gd_details.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				onTab(arg0);

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

	private void clearTab() {
		tv_gd_details_it.setTextColor(Color.parseColor("#939393"));
		iv_gd_details_it_bottom.setBackgroundColor(Color.parseColor("#939393"));
		tv_gd_details_param.setTextColor(Color.parseColor("#939393"));
		iv_gd_details_param_bottom.setBackgroundColor(Color
				.parseColor("#939393"));
	}

	private void onTab(int num) {
		clearTab();
		switch (num) {
		case 0:
			tv_gd_details_it.setTextColor(Color.parseColor("#dd8822"));
			iv_gd_details_it_bottom.setBackgroundColor(Color
					.parseColor("#dd8822"));
			vp_gd_details.setCurrentItem(0);
			break;
		case 1:
			tv_gd_details_param.setTextColor(Color.parseColor("#dd8822"));
			iv_gd_details_param_bottom.setBackgroundColor(Color
					.parseColor("#dd8822"));
			vp_gd_details.setCurrentItem(1);
			break;
		default:
			break;
		}

	}
}
