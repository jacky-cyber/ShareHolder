package com.example.shareholders.activity.stock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_set_avg_line)
public class SetAvgLineActivity extends Activity {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;

	final int CHART_KDAY = 1;
	final int CHART_KWEEK = 2;
	final int CHART_KMONTH = 3;

	private int type;

	private EditText et_MA_param1;
	private EditText et_MA_param2;
	private EditText et_MA_param3;

	private ImageView iv_selected1;
	private ImageView iv_selected2;
	private ImageView iv_selected3;

	private boolean isMAParam1 = true;
	private boolean isMAParam2 = true;
	private boolean isMAParam3 = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		type = getIntent().getIntExtra("type", 1);
		switch (type) {
		case CHART_KDAY:
			tv_title.setText("日均线");
			break;
		case CHART_KWEEK:
			tv_title.setText("周均线");
			break;
		case CHART_KMONTH:
			tv_title.setText("月均线");
			break;
		default:
			break;
		}

		et_MA_param1 = (EditText) findViewById(R.id.layout_ma_param1)
				.findViewById(R.id.et_ma);
		et_MA_param2 = (EditText) findViewById(R.id.layout_ma_param2)
				.findViewById(R.id.et_ma);
		et_MA_param3 = (EditText) findViewById(R.id.layout_ma_param3)
				.findViewById(R.id.et_ma);

		iv_selected1 = (ImageView) findViewById(R.id.layout_ma_param1)
				.findViewById(R.id.iv_selected);
		iv_selected2 = (ImageView) findViewById(R.id.layout_ma_param2)
				.findViewById(R.id.iv_selected);
		iv_selected3 = (ImageView) findViewById(R.id.layout_ma_param3)
				.findViewById(R.id.iv_selected);

		et_MA_param1.setText("" + getIntent().getIntExtra("MA_param1", 5));
		et_MA_param2.setText("" + getIntent().getIntExtra("MA_param2", 10));
		et_MA_param3.setText("" + getIntent().getIntExtra("MA_param3", 20));

		if (getIntent().getBooleanExtra("isMAParam1", true)) {
			isMAParam1 = true;
			iv_selected1.setVisibility(View.VISIBLE);
		} else {
			isMAParam1 = false;
			iv_selected1.setVisibility(View.INVISIBLE);
		}

		if (getIntent().getBooleanExtra("isMAParam2", true)) {
			isMAParam2 = true;
			iv_selected2.setVisibility(View.VISIBLE);
		} else {
			isMAParam2 = false;
			iv_selected2.setVisibility(View.INVISIBLE);
		}

		if (getIntent().getBooleanExtra("isMAParam3", true)) {
			isMAParam3 = true;
			iv_selected3.setVisibility(View.VISIBLE);
		} else {
			isMAParam3 = false;
			iv_selected3.setVisibility(View.INVISIBLE);
		}

		// iv_selected1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// isMAParam1 = !isMAParam1;
		// if (isMAParam1) {
		// iv_selected1.setVisibility(View.VISIBLE);
		// } else {
		// iv_selected1.setVisibility(View.INVISIBLE);
		// }
		// }
		// });

		iv_selected2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isMAParam2 = !isMAParam2;
				if (isMAParam2) {
					iv_selected2.setVisibility(View.VISIBLE);
				} else {
					iv_selected2.setVisibility(View.INVISIBLE);
				}
			}
		});

		iv_selected3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isMAParam3 = !isMAParam3;
				if (isMAParam3) {
					iv_selected3.setVisibility(View.VISIBLE);
				} else {
					iv_selected3.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	@OnClick({ R.id.title_note, R.id.tv_edit_sure, R.id.layout_ma_param1,
			R.id.layout_ma_param2, R.id.layout_ma_param3 })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.tv_edit_sure:
			Intent intent = new Intent();
			int MA_param1 = Integer.parseInt(et_MA_param1.getText().toString());
			int MA_param2 = Integer.parseInt(et_MA_param2.getText().toString());
			int MA_param3 = Integer.parseInt(et_MA_param3.getText().toString());
			if (MA_param1 <= 250 && MA_param2 <= 250 && MA_param3 <= 250
					&& MA_param1 >= 0 && MA_param2 >= 0 && MA_param3 >= 0) {
				intent.putExtra("type", type);
				intent.putExtra("isMAParam1", isMAParam1);
				intent.putExtra("isMAParam2", isMAParam2);
				intent.putExtra("isMAParam3", isMAParam3);
				intent.putExtra("MA_param1", MA_param1);
				intent.putExtra("MA_param2", MA_param2);
				intent.putExtra("MA_param3", MA_param3);
				setResult(0, intent);
				finish();
			}
 else {
				InternetDialog internetDialog = new InternetDialog(
						SetAvgLineActivity.this);
				internetDialog.showInternetDialog("请输入0-250范围内的数字", false);
			}
			break;
		case R.id.layout_ma_param1:
			isMAParam1 = !isMAParam1;
			if (isMAParam1) {
				iv_selected1.setVisibility(View.VISIBLE);
			} else {
				iv_selected1.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.layout_ma_param2:
			isMAParam2 = !isMAParam2;
			if (isMAParam2) {
				iv_selected2.setVisibility(View.VISIBLE);
			} else {
				iv_selected2.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.layout_ma_param3:
			isMAParam3 = !isMAParam3;
			if (isMAParam3) {
				iv_selected3.setVisibility(View.VISIBLE);
			} else {
				iv_selected3.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}
}
