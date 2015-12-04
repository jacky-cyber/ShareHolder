package com.example.shareholders.activity.fund;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.NoScrollViewPager;
import com.example.shareholders.fragment.Fragment_OptionalFundDisplay;
import com.example.shareholders.fragment.Fragment_OptionalFundDisplay.FundCount;
import com.example.shareholders.fragment.Fragment_OptionalFundDisplay.onUpdateListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_optional_fund)
public class OptionalFundActivity extends FragmentActivity implements
		onUpdateListener {

	@ViewInject(R.id.tv_optional_fund_manage)
	private TextView tv_optional_fund_manage;
	@ViewInject(R.id.rl_fl_title)
	private RelativeLayout rl_return;
	@ViewInject(R.id.rl_fl_comments)
	private LinearLayout rl_fl_comments;
	// 基金标题
	@ViewInject(R.id.rl_fl_foud_title)
	private MyListView rl_fl_foud_title;
	// 开放基金
	@ViewInject(R.id.tv_fl_open_foud)
	private TextView tv_fl_open_foud;
	@ViewInject(R.id.rl_fl_open_foud_buttom)
	private ImageView rl_fl_open_foud_buttom;
	// 封闭基金
	@ViewInject(R.id.tv_fl_closed_foud)
	private TextView tv_fl_closed_foud;
	@ViewInject(R.id.rl_fl_closed_foud_buttom)
	private ImageView rl_fl_closed_foud_buttom;
	// 货币基金
	@ViewInject(R.id.tv_fl_money_foud)
	private TextView tv_fl_money_foud;
	@ViewInject(R.id.rl_fl_money_foud_buttom)
	private ImageView rl_fl_money_foud_buttom;
	// no scroll viewpager
	@ViewInject(R.id.vp_optional_fund)
	private NoScrollViewPager vp_optional_fund;

	private Fragment_OptionalFundDisplay openFundFragment;
	private Fragment_OptionalFundDisplay closedFundFragment;
	private Fragment_OptionalFundDisplay moneyFundFragment;
	private ArrayList<Fragment> fragmentList;

	int fund_select_num = 0;
	final int open_foud_num = 0;
	final int closed_foud_num = 1;
	final int money_foud_num = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initFrag();
		OnTab(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		tv_optional_fund_manage.setText("管理");
		ManageFlag.state = false;
		vp_optional_fund.setScrollEnble(true);
		updateView();
	}

	@OnClick({ R.id.title_fl_research, R.id.title_fl_note,
			R.id.tv_fl_open_foud, R.id.tv_fl_closed_foud,
			R.id.tv_fl_money_foud, R.id.tv_optional_fund_manage })
	private void onClick(View v) {
		int current = 0;
		switch (v.getId()) {
		case R.id.title_fl_note:
			finish();
			break;
		case R.id.tv_fl_open_foud:
			ManageFlag.state = false;
			updateView();
			current = 0;
			OnTab(open_foud_num);
			if (vp_optional_fund.getCurrentItem() != current) {
				vp_optional_fund.setCurrentItem(current);
			}
			break;
		case R.id.tv_fl_closed_foud:
			ManageFlag.state = false;
			updateView();
			current = 1;
			OnTab(closed_foud_num);
			if (vp_optional_fund.getCurrentItem() != current) {
				vp_optional_fund.setCurrentItem(current);
			}
			break;
		case R.id.tv_fl_money_foud:
			ManageFlag.state = false;
			updateView();
			current = 2;
			OnTab(money_foud_num);
			if (vp_optional_fund.getCurrentItem() != current) {
				vp_optional_fund.setCurrentItem(current);
			}
			break;
		case R.id.title_fl_research:
		case R.id.tv_optional_fund_add_tip:
			startActivity(new Intent(this, FundSearchActivity.class));
			ManageFlag.state = false;
			break;
		case R.id.tv_optional_fund_manage:
			manageFragment(current);
			break;
		default:
			break;
		}

	}

	public void OnTab(int pos) {
		ClearTab();
		switch (pos) {
		case open_foud_num:
			fund_select_num = open_foud_num;
			tv_fl_open_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_open_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			if (FundCount.item_open_fund_num > 0) {
				tv_optional_fund_manage
						.setBackgroundResource(R.drawable.bg_optional_fund_manage_enabled);
				// showOpenFund();
			} else {
				tv_optional_fund_manage
						.setBackgroundResource(R.drawable.bg_optional_fund_manage_disabled);
			}
			break;
		case closed_foud_num:
			fund_select_num = closed_foud_num;
			tv_fl_closed_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_closed_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			if (FundCount.item_closed_fund_num > 0) {
				tv_optional_fund_manage
						.setBackgroundResource(R.drawable.bg_optional_fund_manage_enabled);
				// showClosedFund();
			} else {
				tv_optional_fund_manage
						.setBackgroundResource(R.drawable.bg_optional_fund_manage_disabled);
			}
			break;
		case money_foud_num:
			fund_select_num = money_foud_num;
			tv_fl_money_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_money_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			if (FundCount.item_money_fund_num > 0) {
				tv_optional_fund_manage
						.setBackgroundResource(R.drawable.bg_optional_fund_manage_enabled);
				// showMoneyFund();
			} else {
				tv_optional_fund_manage
						.setBackgroundResource(R.drawable.bg_optional_fund_manage_disabled);
			}
			break;
		default:
			break;
		}
	}

	private void ClearTab() {
		tv_fl_open_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_open_foud_buttom.setBackgroundResource(R.color.total_line_gray);
		tv_fl_closed_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_closed_foud_buttom.setBackgroundResource(R.color.total_line_gray);
		tv_fl_money_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_money_foud_buttom.setBackgroundResource(R.color.total_line_gray);
	}

	private void initFrag() {
		fragmentList = new ArrayList<Fragment>();
		openFundFragment = new Fragment_OptionalFundDisplay(0);
		closedFundFragment = new Fragment_OptionalFundDisplay(1);
		moneyFundFragment = new Fragment_OptionalFundDisplay(2);
		fragmentList.add(openFundFragment);
		fragmentList.add(closedFundFragment);
		fragmentList.add(moneyFundFragment);
		vp_optional_fund.setAdapter(new ViewPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		vp_optional_fund.setCurrentItem(0);
		vp_optional_fund.setOffscreenPageLimit(3);
		vp_optional_fund.setScrollEnble(true);
		vp_optional_fund.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (!ManageFlag.state) {
					int current = vp_optional_fund.getCurrentItem();
					OnTab(current);
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

	private void manageFragment(int num) {
		boolean isManaged = false;
		switch (num) {
		case 0:
			if (FundCount.item_open_fund_num > 0) {
				isManaged = true;
			}
			break;
		case 1:
			if (FundCount.item_closed_fund_num > 0) {
				isManaged = true;
			}
			break;
		case 2:
			if (FundCount.item_money_fund_num > 0) {
				isManaged = true;
			}
			break;
		default:
			break;
		}
		if (isManaged) {
			if (tv_optional_fund_manage.getText().toString().equals("管理")) {
				tv_optional_fund_manage.setText("保存");
				ManageFlag.state = true;
				vp_optional_fund.setScrollEnble(false);
			} else if (tv_optional_fund_manage.getText().toString()
					.equals("保存")) {
				tv_optional_fund_manage.setText("管理");
				ManageFlag.state = false;
				vp_optional_fund.setScrollEnble(true);
			}
			updateView();
		}

	}

	/*
	 * 通知Fragment改变状态
	 */
	public void updateView() {
		openFundFragment.updateView();
		closedFundFragment.updateView();
		moneyFundFragment.updateView();
	}

	/*
	 * 该类作为与Fragment_OptionalFundDisplay的通信桥梁 用于改变状态
	 */
	public static class ManageFlag {
		public static boolean state;
	}

	@Override
	public void onUpdate() {
		// updateView();
		tv_optional_fund_manage.setText("管理");
		ManageFlag.state = false;
		OnTab(fund_select_num);
	}

}
