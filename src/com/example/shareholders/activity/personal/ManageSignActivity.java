package com.example.shareholders.activity.personal;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.fragment.Fragment_Apply_Manage_Passed;
import com.example.shareholders.fragment.Fragment_Apply_Manage_Pendingaudit;
import com.example.shareholders.fragment.Fragment_Apply_Manage_Refused;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_manage_sign)
public class ManageSignActivity extends FragmentActivity {
	// 初始化“待审核”
	@ViewInject(R.id.pending_audit)
	private TextView pending_audit;
	// 初始化“已通过”
	@ViewInject(R.id.passed)
	private TextView passed;
	// 初始化“拒绝”
	@ViewInject(R.id.refused)
	private TextView refused;
	// 返回
	@ViewInject(R.id.title_note)
	private TextView title_note;

	private ViewPager ViewPager;
	private ViewPagerAdapter fPagerAdapter;
	private ArrayList<Fragment> pagerFargmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initView();
	}

	@OnClick({ R.id.title_note, R.id.pending_audit, R.id.passed, R.id.refused,R.id.rl_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.pending_audit:
			onTab(0);
			ViewPager.setCurrentItem(0);
			break;
		case R.id.passed:
			onTab(1);
			ViewPager.setCurrentItem(1);
			break;
		case R.id.refused:
			onTab(2);
			ViewPager.setCurrentItem(2);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void initView() {
		ViewPager = (ViewPager) findViewById(R.id.apply_ViewPager);

		pagerFargmentList = new ArrayList<Fragment>();
		Fragment pending_audit = new Fragment_Apply_Manage_Pendingaudit();
		Fragment passed = new Fragment_Apply_Manage_Passed();
		Fragment refused = new Fragment_Apply_Manage_Refused();
		pagerFargmentList.add(pending_audit);
		pagerFargmentList.add(passed);
		pagerFargmentList.add(refused);

		fPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
				pagerFargmentList);
		ViewPager.setAdapter(fPagerAdapter);
	}

	// 标志选中项
	private void onTab(int num) {
		clearTab();
		switch (num) {
		case 0:
			pending_audit.setBackgroundResource(R.drawable.bg_tab_selected);
			break;
		case 1:
			passed.setBackgroundResource(R.drawable.bg_tab_selected);
			break;
		case 2:
			refused.setBackgroundResource(R.drawable.bg_tab_selected);
			break;
		default:
			break;
		}
	}

	private void clearTab() {
		pending_audit.setBackgroundResource(R.drawable.bg_tab_unselected);
		passed.setBackgroundResource(R.drawable.bg_tab_unselected);
		refused.setBackgroundResource(R.drawable.bg_tab_unselected);
	}

}
