package com.example.shareholders.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.fragment.Fragment_OtherPeople_Price_Situation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_other_people_stock)
public class OtherPeopleStockActivity extends FragmentActivity {

	private String userName = "";
	@ViewInject(R.id.fl_container)
	private FrameLayout fl_container;
	@ViewInject(R.id.title_text)
	private TextView title_text;
	//返回
	@ViewInject(R.id.rl_return)
	private RelativeLayout rl_return;
	private Fragment_OtherPeople_Price_Situation fragment_OtherPeople_Price_Situation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Intent intent = getIntent();// 传过来usename和useuuid
		Bundle bundle = intent.getExtras();
		userName = bundle.getString("userName");
		title_text.setText(userName + "的自选股");
		//返回
		rl_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		addFragment();
	}

	private void addFragment() {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		fragment_OtherPeople_Price_Situation = new Fragment_OtherPeople_Price_Situation();

		transaction
				.add(R.id.fl_container, fragment_OtherPeople_Price_Situation);
		transaction.commit();
		brocastUpdate();
	}

	private void brocastUpdate() {
		Intent intent = new Intent();
		intent.setAction("situation_update");
		this.sendBroadcast(intent);
	}

}
