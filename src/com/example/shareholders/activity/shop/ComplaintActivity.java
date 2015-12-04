package com.example.shareholders.activity.shop;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_complaint)
public class ComplaintActivity extends Activity {

	// 投诉内容
	@ViewInject(R.id.et_complaint)
	private EditText et_complaint;

	// 提交
	@ViewInject(R.id.tv_commit)
	private TextView tv_commit;
	private boolean canCommit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		setTextWatcher();

	}

	private void setTextWatcher() {
		et_complaint.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (et_complaint.getText().toString().equals("")) {
					tv_commit.setBackgroundResource(R.drawable.btn_login);
					canCommit = false;
				} else {
					tv_commit
							.setBackgroundResource(R.drawable.btn_login_enable);
					canCommit = true;
				}
			}
		});
	}

	@OnClick({ R.id.title_note, R.id.tv_commit,R.id.rl_return })
	public void onclick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.title_note:
			finish();
			break;
		// 提交
		case R.id.tv_commit:
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

}
