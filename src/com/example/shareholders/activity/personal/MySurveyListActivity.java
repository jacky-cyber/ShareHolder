package com.example.shareholders.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.fragment.Fragment_Survey_Collected;
import com.example.shareholders.fragment.Fragment_Survey_Organized;
import com.example.shareholders.fragment.Fragment_Survey_Signed;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_my_survey_list)
public class MySurveyListActivity extends ActionBarActivity {
	@ViewInject(R.id.background)
	private RelativeLayout background;

	// 报名的调研
	@ViewInject(R.id.rl_activity_signed)
	private RelativeLayout rl_activity_signed;
	@ViewInject(R.id.tv_activity_signed)
	private TextView tv_activity_signed;
	@ViewInject(R.id.iv_activity_signed)
	private ImageView iv_activity_signed;
	// 收藏的调研
	@ViewInject(R.id.rl_activity_collected)
	private RelativeLayout rl_activity_collected;
	@ViewInject(R.id.tv_activity_collected)
	private TextView tv_activity_collected;
	@ViewInject(R.id.iv_activity_collected)
	private ImageView iv_activity_collected;
	// 发起的调研
	@ViewInject(R.id.rl_activity_organized)
	private RelativeLayout rl_activity_organized;
	@ViewInject(R.id.tv_activity_organized)
	private TextView tv_activity_organized;
	@ViewInject(R.id.iv_activity_organized)
	private ImageView iv_activity_organized;
	// 更多
	@ViewInject(R.id.iv_more)
	private ImageView iv_more;
	// 报名的，收藏的，发起的调研
	Fragment_Survey_Signed fragment_Survey_Signed;
	Fragment_Survey_Collected fragment_Survey_Collected;
	Fragment_Survey_Organized fragment_Survey_Organized;
	FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		fragmentManager = getSupportFragmentManager();
		initFragments();
		setTabSelection(0);
	}

	private void initFragments() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Log.d(this.toString(), "initFragments()");
		if (fragment_Survey_Signed == null) {
			fragment_Survey_Signed = new Fragment_Survey_Signed();
			transaction.add(R.id.rl_content, fragment_Survey_Signed);
		}
		if (fragment_Survey_Collected == null) {
			fragment_Survey_Collected = new Fragment_Survey_Collected();
			transaction.add(R.id.rl_content, fragment_Survey_Collected);
		}
		if (fragment_Survey_Organized == null) {
			fragment_Survey_Organized = new Fragment_Survey_Organized();
			transaction.add(R.id.rl_content, fragment_Survey_Organized);
		}
		transaction.commitAllowingStateLoss();
	}

	private void setTabSelection(int index) {
		// TODO Auto-generated method stub
		ClearTab();
		hideFragments();

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		switch (index) {
		case 0:
			tv_activity_signed.setTextColor(getResources().getColor(
					R.color.selected_text_color));
			iv_activity_signed.setVisibility(View.VISIBLE);
			transaction.show(fragment_Survey_Signed);
			break;
		case 1:
			tv_activity_collected.setTextColor(getResources().getColor(
					R.color.selected_text_color));
			iv_activity_collected.setVisibility(View.VISIBLE);
			transaction.show(fragment_Survey_Collected);
			break;
		case 2:
			tv_activity_organized.setTextColor(getResources().getColor(
					R.color.selected_text_color));
			iv_activity_organized.setVisibility(View.VISIBLE);
			transaction.show(fragment_Survey_Organized);
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (fragment_Survey_Signed != null) {
			transaction.hide(fragment_Survey_Signed);
		}
		if (fragment_Survey_Collected != null) {
			transaction.hide(fragment_Survey_Collected);
		}
		if (fragment_Survey_Organized != null) {
			transaction.hide(fragment_Survey_Organized);
		}
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 未选择状态
	 */
	private void ClearTab() {
		tv_activity_signed.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		iv_activity_signed.setVisibility(View.GONE);
		tv_activity_collected.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		iv_activity_collected.setVisibility(View.GONE);
		tv_activity_organized.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		iv_activity_organized.setVisibility(View.GONE);
	}

	@OnClick({ R.id.rl_return, R.id.rl_more, R.id.rl_activity_signed,
		R.id.rl_activity_collected, R.id.rl_activity_organized })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
			// 更多
		case R.id.rl_more:
			initMenu(getApplicationContext(), iv_more,
					R.layout.popup_survey_more);
			// 报名的调研
		case R.id.rl_activity_signed:
			setTabSelection(0);
			break;
			// 收藏的调研
		case R.id.rl_activity_collected:
			setTabSelection(1);
			break;
			// 发起的调研
		case R.id.rl_activity_organized:
			setTabSelection(2);
			break;
		default:
			break;
		}
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param viewGroup
	 * @param view
	 * @return
	 */
	public void initMenu(Context context, View viewGroup, int view) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);
		TextView tv_ask_for_survey = (TextView) contentView
				.findViewById(R.id.tv_ask_for_survey);
		TextView tv_manage_sign = (TextView) contentView
				.findViewById(R.id.tv_manage_sign);
		//录音管理
		TextView tv_manage_record = (TextView) contentView.findViewById(R.id.tv_manage_record);
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		// 获取xoff
		int xpos = manager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;
		// popwindow位置
		popupWindow.showAsDropDown(viewGroup, xpos, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		//调研需求
		tv_ask_for_survey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MySurveyListActivity.this,
						AskForSurveyActivity.class));
				popupWindow.dismiss();
			}
		});
		//报名管理
		tv_manage_sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MySurveyListActivity.this,
						AllApplyManageActivity.class));
				popupWindow.dismiss();
			}
		});
		//录音管理
		tv_manage_record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),RecordManageActivity.class));
				popupWindow.dismiss();
			}
		});
	}
}
