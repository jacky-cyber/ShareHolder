package com.example.shareholders.activity.personal;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.fragment.OtherCollectSurveyFragment;
import com.example.shareholders.fragment.OtherCreateSurveyFragment;
import com.example.shareholders.fragment.OtherSignedSurveyFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_other_people_survey)
public class OtherPeopleSurveyActivity extends FragmentActivity {

	// 头部栏标题
	@ViewInject(R.id.title_text)
	private TextView title_text;

	// 报名的调研活动
	@ViewInject(R.id.rl_activity_signed)
	private RelativeLayout rl_activity_signed;

	// 报名的调研活动的字体
	@ViewInject(R.id.tv_activity_signed)
	private TextView tv_activity_signed;

	// 报名的调研活动的下方的颜色条
	@ViewInject(R.id.iv_activity_signed)
	private ImageView iv_activity_signed;

	// 收藏的调研活动
	@ViewInject(R.id.rl_activity_collected)
	private RelativeLayout rl_activity_collected;

	// 收藏的调研活动的字体
	@ViewInject(R.id.tv_activity_collected)
	private TextView tv_activity_collected;

	// 收藏的调研活动的下方的颜色条
	@ViewInject(R.id.iv_activity_collected)
	private ImageView iv_activity_collected;

	// 发起的调研活动
	@ViewInject(R.id.rl_activity_organized)
	private RelativeLayout rl_activity_organized;

	// 发起的调研活动的字体
	@ViewInject(R.id.tv_activity_organized)
	private TextView tv_activity_organized;

	// 发起的调研活动的下方的颜色条
	@ViewInject(R.id.iv_activity_organized)
	private ImageView iv_activity_organized;

	// Fragment的容器
	@ViewInject(R.id.rl_content)
	private RelativeLayout rl_content;

	private String userName = "";

	private FragmentManager manager = null;
	private FragmentTransaction transaction = null;

	private OtherSignedSurveyFragment otherSignedSurveyFragment = null;
	private OtherCollectSurveyFragment otherCollectSurveyFragment = null;
	private OtherCreateSurveyFragment otherCreateSurveyFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		userName = getIntent().getExtras().getString("userName");
		title_text.setText(userName + "的调研活动");

		initFragments();

	}

	private void initFragments() {
		manager = getSupportFragmentManager();
		transaction = manager.beginTransaction();

		if (otherSignedSurveyFragment == null) {
			otherSignedSurveyFragment = new OtherSignedSurveyFragment();
			transaction.add(R.id.rl_content, otherSignedSurveyFragment);
		}

		if (otherCollectSurveyFragment == null) {
			otherCollectSurveyFragment = new OtherCollectSurveyFragment();
			transaction.add(R.id.rl_content, otherCollectSurveyFragment);
		}

		if (otherCreateSurveyFragment == null) {
			otherCreateSurveyFragment = new OtherCreateSurveyFragment();
			transaction.add(R.id.rl_content, otherCreateSurveyFragment);
		}

		transaction.commitAllowingStateLoss();

		setTabSelection(0);
	}

	private void setTabSelection(int index) {
		clearSelection();
		hideFragment();

		transaction = manager.beginTransaction();

		switch (index) {
		case 0:
			tv_activity_signed.setTextColor(getResources().getColor(
					R.color.paper_mill));
			iv_activity_signed.setVisibility(View.VISIBLE);
			transaction.show(otherSignedSurveyFragment);
			break;
		case 1:
			tv_activity_collected.setTextColor(getResources().getColor(
					R.color.paper_mill));
			iv_activity_collected.setVisibility(View.VISIBLE);
			transaction.show(otherCollectSurveyFragment);
			break;
		case 2:
			tv_activity_organized.setTextColor(getResources().getColor(
					R.color.paper_mill));
			iv_activity_organized.setVisibility(View.VISIBLE);
			transaction.show(otherCreateSurveyFragment);
			break;
		default:
			break;
		}

		transaction.commitAllowingStateLoss();

	}

	private void clearSelection() {
		tv_activity_signed.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		tv_activity_collected.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		tv_activity_organized.setTextColor(getResources().getColor(
				R.color.unselected_text_color));

		iv_activity_signed.setVisibility(View.GONE);
		iv_activity_collected.setVisibility(View.GONE);
		iv_activity_organized.setVisibility(View.GONE);

	}

	private void hideFragment() {
		transaction = manager.beginTransaction();
		if (otherSignedSurveyFragment != null) {
			transaction.hide(otherSignedSurveyFragment);
		}

		if (otherCollectSurveyFragment != null) {
			transaction.hide(otherCollectSurveyFragment);
		}

		if (otherCreateSurveyFragment != null) {
			transaction.hide(otherCreateSurveyFragment);
		}

		transaction.commitAllowingStateLoss();
	}

	@OnClick({ R.id.rl_return, R.id.rl_activity_signed,
			R.id.rl_activity_collected, R.id.rl_activity_organized })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		case R.id.rl_activity_signed:
			setTabSelection(0);
			break;
		case R.id.rl_activity_collected:
			setTabSelection(1);
			break;
		case R.id.rl_activity_organized:
			setTabSelection(2);
			break;
		default:
			break;
		}
	}

}
