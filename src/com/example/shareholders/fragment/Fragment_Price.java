package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shareholders.MainActivity;
import com.example.shareholders.R;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class Fragment_Price extends Fragment {
	private RotateAnimation animation;
	MainActivity mainActivity;
	@ViewInject(R.id.iv_price_1)
	ImageView head_iv_1;
	@ViewInject(R.id.iv_price_2)
	ImageView head_iv_2;
	@ViewInject(R.id.iv_price_3)
	ImageView head_iv_3;
	@ViewInject(R.id.iv_price_4)
	ImageView head_iv_4;
	@ViewInject(R.id.iv_price_5)
	ImageView head_iv_5;
	private ViewPager ViewPager;
	private ViewPagerAdapter fPagerAdapter;
	private ArrayList<Fragment> pagerFargmentList;
	View mview;

	private ArrayList<HashMap<String, Object>> share_hashMaps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_price, container, false);
		ViewUtils.inject(this, mview);

		initView();

		// 不是行情fragment时停止行情刷新
		ViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if (ViewPager.getCurrentItem() != 0) {
					Log.d("if0", "not0");
					removeupdate();
				} else {
					Log.d("if0", "is0");
					postupdate();
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
		mainActivity = (MainActivity) getActivity();
		mainActivity.title_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 已登录，跳转到situation
				if (!RsSharedUtil.getString(getActivity(), "access_token").equals("")) {
					ViewPager.setCurrentItem(0);
					refreshsituation();
				}
				mainActivity.title_refresh.startAnimation(animation);
			}
		});
		return mview;

	}

	

	/*
	 * public void setUserVisibleHint(boolean isVisibleToUser) { // TODO
	 * Auto-generated method stub if (isVisibleToUser) {
	 * 
	 * } else { try { // 发广播通知行情停止刷新 Log.d("price_setUserVisibleHint",
	 * "price_setUserVisibleHint"); Intent intent = new Intent(); // 要发送的内容
	 * intent.setAction("stop_situation_update"); // 设置广播的action
	 * getActivity().sendBroadcast(intent); // 发送广播 } catch (Exception e) { //
	 * TODO: handle exception } } super.setUserVisibleHint(isVisibleToUser); }
	 */

	public void postupdate() {
		// main切换fragment时不一定进入行情界面
		if (ViewPager.getCurrentItem() == 0) {
			ViewPagerAdapter adapter = (ViewPagerAdapter) ViewPager.getAdapter();
			Fragment_Price_Situation fragment_Price_Situation = (Fragment_Price_Situation) adapter
					.instantiateItem(ViewPager, 0);
			fragment_Price_Situation.postrunnable();
		}

	}

	public void removeupdate() {
		ViewPagerAdapter adapter = (ViewPagerAdapter) ViewPager.getAdapter();
		Fragment_Price_Situation fragment_Price_Situation = (Fragment_Price_Situation) adapter
				.instantiateItem(ViewPager, 0);
		fragment_Price_Situation.remvoerunnable();
	}

	public void refreshsituation() {
		ViewPagerAdapter adapter = (ViewPagerAdapter) ViewPager.getAdapter();
		Fragment_Price_Situation fragment_Price_Situation = (Fragment_Price_Situation) adapter
				.instantiateItem(ViewPager, 0);
		fragment_Price_Situation.initpost();
	}

	public int getViewPagerid() {
		return ViewPager.getCurrentItem();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			ViewPager.setCurrentItem(5);
		}

	};
	
	private void initView() {
		// 设置旋转动画
		animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// animation.setFillAfter(true);
		animation.setDuration(500);// 设置动画持续时间
		// animation.setRepeatCount(2);//重复次数
		ViewPager = (ViewPager) mview.findViewById(R.id.price_ViewPager);

		pagerFargmentList = new ArrayList<Fragment>();
		Fragment ShareSituation = new Fragment_Price_Situation();
		Fragment ShareNews = new Fragment_EditMyself_News();
		Fragment ShareAnnouncement = new Fragment_EditMyself_Announcement();
		Fragment ShareResearchReport = new Fragment_EditMyself_ResearchReport();
		Fragment ShareComments = new Fragment_EditMyself_Comments();

		pagerFargmentList.add(ShareSituation);
		pagerFargmentList.add(ShareNews);
		pagerFargmentList.add(ShareAnnouncement);
		pagerFargmentList.add(ShareResearchReport);
		pagerFargmentList.add(ShareComments);

		fPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), pagerFargmentList);
		ViewPager.setAdapter(fPagerAdapter);
	}

	@OnClick({ R.id.rb_price1, R.id.rb_price2, R.id.rb_price3, R.id.rb_price4, R.id.rb_price5 })
	private void onClick(View v) {
		
		
		switch (v.getId()) {
		case R.id.rb_price1:
			mainActivity.setrefresh(true);
			/*mainActivity.title_refresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.d("refreshsituation2", "refreshsituation");
					refreshsituation();
					
				}
			});*/
			ViewPager.setCurrentItem(0);
			head_iv_1.setVisibility(View.VISIBLE);
			head_iv_2.setVisibility(View.GONE);
			head_iv_3.setVisibility(View.GONE);
			head_iv_4.setVisibility(View.GONE);
			head_iv_5.setVisibility(View.GONE);
			break;
		case R.id.rb_price2:
			mainActivity.setrefresh(false);
			ViewPager.setCurrentItem(1);
			head_iv_1.setVisibility(View.GONE);
			head_iv_2.setVisibility(View.VISIBLE);
			head_iv_3.setVisibility(View.GONE);
			head_iv_4.setVisibility(View.GONE);
			head_iv_5.setVisibility(View.GONE);
			break;
		case R.id.rb_price3:
			mainActivity.setrefresh(false);
			ViewPager.setCurrentItem(2);
			head_iv_1.setVisibility(View.GONE);
			head_iv_2.setVisibility(View.GONE);
			head_iv_3.setVisibility(View.VISIBLE);
			head_iv_4.setVisibility(View.GONE);
			head_iv_5.setVisibility(View.GONE);
			break;
		case R.id.rb_price4:
			mainActivity.setrefresh(false);
			ViewPager.setCurrentItem(3);
			head_iv_1.setVisibility(View.GONE);
			head_iv_2.setVisibility(View.GONE);
			head_iv_3.setVisibility(View.GONE);
			head_iv_4.setVisibility(View.VISIBLE);
			head_iv_5.setVisibility(View.GONE);
			break;
		case R.id.rb_price5:
			mainActivity.setrefresh(false);
			ViewPager.setCurrentItem(4);
			head_iv_1.setVisibility(View.GONE);
			head_iv_2.setVisibility(View.GONE);
			head_iv_3.setVisibility(View.GONE);
			head_iv_4.setVisibility(View.GONE);
			head_iv_5.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

}
