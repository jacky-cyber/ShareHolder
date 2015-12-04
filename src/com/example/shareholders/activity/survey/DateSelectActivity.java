package com.example.shareholders.activity.survey;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.util.SystemStatusManager;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_date_select)
public class DateSelectActivity extends Activity {

	@ViewInject(R.id.rl_title)
	private RelativeLayout rl_title;
	private LayoutParams params;
	@ViewInject(R.id.date_begin)
	TextView tv_begin;
	@ViewInject(R.id.date_finish)
	TextView tv_finish;
	@ViewInject(R.id.iv_return)
	TextView iv_return;
	WheelMain wheelMain;

	DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setTranslucentStatus();
		initView();
	}

	public void initView() {
		Calendar calendar = Calendar.getInstance();
	}

	@OnClick({ R.id.date_begin, R.id.date_finish, R.id.iv_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.date_begin:
			getDateSelect(tv_begin);
			break;

		case R.id.date_finish:
			getDateSelect(tv_finish);
			break;
		case R.id.iv_return:
			finish();
			break;
		}
	}

	/*
	 * 时间选择器对话框
	 */
	private void getDateSelect(final TextView tv) {
		LayoutInflater inflater = LayoutInflater.from(DateSelectActivity.this);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(DateSelectActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = tv.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		wheelMain.initDateTimePicker(year, month, day);
		new AlertDialog.Builder(DateSelectActivity.this).setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tv.setText(wheelMain.getTime());
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

	}

	private void setTranslucentStatus() {
		params = (LayoutParams) rl_title.getLayoutParams();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
			params.topMargin = 60;
		} else {
			params.topMargin = 0;
		}
		rl_title.setLayoutParams(params);

		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);
	}
}
