package com.example.shareholders.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_stock_friends_search_friends)
public class StockFriendsSearchFriendsActivity extends Activity {

	@ViewInject(R.id.title_note)
	private ImageView title_note;
	@ViewInject(R.id.rl_weixin_friends)
	private RelativeLayout rl_weixin_friends;
	@ViewInject(R.id.rl_qq_friends)
	private RelativeLayout rl_qq_friends;
	@ViewInject(R.id.rl_weibo_friends)
	private RelativeLayout rl_weibo_friends;
	@ViewInject(R.id.rl_phone_friends)
	private RelativeLayout rl_phone_friends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.title_note, R.id.rl_weixin_friends, R.id.rl_qq_friends,
			R.id.rl_weibo_friends, R.id.rl_phone_friends })
	private void Onclick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.rl_weixin_friends:
			break;
		case R.id.rl_qq_friends:
			break;
		case R.id.rl_weibo_friends:
			break;
		case R.id.rl_phone_friends:
			Intent intent = new Intent(StockFriendsSearchFriendsActivity.this,
					StockFriendsLocalContacts.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
