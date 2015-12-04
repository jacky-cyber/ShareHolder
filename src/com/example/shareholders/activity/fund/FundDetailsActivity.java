package com.example.shareholders.activity.fund;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.fragment.Fragment_FundDiscuss;
import com.example.shareholders.fragment.Fragment_FundDividend;
import com.example.shareholders.fragment.Fragment_FundNetWorth;
import com.example.shareholders.fragment.Fragment_FundNotice;
import com.example.shareholders.fragment.Fragment_FundPosition;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fund_details)
public class FundDetailsActivity extends FragmentActivity implements
		OnCheckedChangeListener {

	@ViewInject(R.id.iv_fd_btn1)
	private View iv_fd_btn1;
	@ViewInject(R.id.iv_fd_btn2)
	private View iv_fd_btn2;
	@ViewInject(R.id.iv_fd_btn3)
	private View iv_fd_btn3;
	@ViewInject(R.id.iv_fd_btn4)
	private View iv_fd_btn4;
	@ViewInject(R.id.iv_fd_btn5)
	private View iv_fd_btn5;
	@ViewInject(R.id.fd_rbtn1)
	private RadioButton fd_rbtn1;
	@ViewInject(R.id.fd_rbtn2)
	private RadioButton fd_rbtn2;
	@ViewInject(R.id.fd_rbtn3)
	private RadioButton fd_rbtn3;
	@ViewInject(R.id.fd_rbtn4)
	private RadioButton fd_rbtn4;
	@ViewInject(R.id.fd_rbtn5)
	private RadioButton fd_rbtn5;
	@ViewInject(R.id.fd_rgp)
	private RadioGroup fd_rgp;
	@ViewInject(R.id.fd_ViewPager)
	private ViewPager fd_ViewPager;

	private Fragment_FundNetWorth fragment_FundNetWorth;
	private Fragment_FundNotice fragment_FundNotice;
	private Fragment_FundPosition fragment_FundPosition;
	private Fragment_FundDividend fragment_FundDividend;
	private Fragment_FundDiscuss fragment_FundDiscuss;
	private ArrayList<Fragment> fragmentlist;

	String symbolString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		init();
		initFrag();
	}

	private void init() {
		Intent intent = getIntent();
		symbolString = intent.getStringExtra("symbol");
	}

	@OnClick({ R.id.title_fs_note })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		default:
			break;
		}
	}

	private void initFrag() {
		// TODO Auto-generated method stub
		fd_rgp.setOnCheckedChangeListener(this);

		fragmentlist = new ArrayList<Fragment>();

		fragment_FundNetWorth = new Fragment_FundNetWorth();
		fragment_FundNotice = new Fragment_FundNotice();
		fragment_FundPosition = new Fragment_FundPosition();
		fragment_FundDividend = new Fragment_FundDividend();
		fragment_FundDiscuss = new Fragment_FundDiscuss();

		fragmentlist.add(fragment_FundNetWorth);
		fragmentlist.add(fragment_FundNotice);
		fragmentlist.add(fragment_FundPosition);
		fragmentlist.add(fragment_FundDividend);
		fragmentlist.add(fragment_FundDiscuss);
		fd_ViewPager.setAdapter(new ViewPagerAdapter(
				getSupportFragmentManager(), fragmentlist));
		fd_ViewPager.setCurrentItem(0);
		fd_ViewPager.setOffscreenPageLimit(5);
		fd_ViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void clearTab() {
		iv_fd_btn1.setBackgroundResource(R.color.fund_Details_unselected_color);
		iv_fd_btn2.setBackgroundResource(R.color.fund_Details_unselected_color);
		iv_fd_btn3.setBackgroundResource(R.color.fund_Details_unselected_color);
		iv_fd_btn4.setBackgroundResource(R.color.fund_Details_unselected_color);
		iv_fd_btn5.setBackgroundResource(R.color.fund_Details_unselected_color);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			int current = fd_ViewPager.getCurrentItem();
			switch (current) {
			case 0:
				fd_rgp.check(R.id.fd_rbtn1);
				break;
			case 1:
				fd_rgp.check(R.id.fd_rbtn2);
				break;
			case 2:
				fd_rgp.check(R.id.fd_rbtn3);
				break;
			case 3:
				fd_rgp.check(R.id.fd_rbtn4);
				break;
			case 4:
				fd_rgp.check(R.id.fd_rbtn5);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		int current = 0;
		switch (checkedId) {
		case R.id.fd_rbtn1:
			current = 0;
			clearTab();
			iv_fd_btn1
					.setBackgroundResource(R.color.fund_Details_selected_color);
			break;
		case R.id.fd_rbtn2:
			current = 1;
			clearTab();
			iv_fd_btn2
					.setBackgroundResource(R.color.fund_Details_selected_color);
			break;
		case R.id.fd_rbtn3:
			current = 2;
			clearTab();
			iv_fd_btn3
					.setBackgroundResource(R.color.fund_Details_selected_color);
			break;
		case R.id.fd_rbtn4:
			current = 3;
			clearTab();
			iv_fd_btn4
					.setBackgroundResource(R.color.fund_Details_selected_color);
			break;
		case R.id.fd_rbtn5:
			current = 4;
			clearTab();
			iv_fd_btn5
					.setBackgroundResource(R.color.fund_Details_selected_color);
			break;
		}
		if (fd_ViewPager.getCurrentItem() != current) {
			fd_ViewPager.setCurrentItem(current);
		}
	}
}
