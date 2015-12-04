package com.example.shareholders.activity.personal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * @category 这个类是PersonCommentSetActivity里面的消息中心 的 设置按钮 Activity
 * */
@ContentView(R.layout.activity_person_comment_set)
public class PersonCommentSetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.comment_set_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_set_return:
			finish();
			break;
		default:
			break;
		}

	}

}
