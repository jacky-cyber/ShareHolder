package com.example.shareholders.activity.personal;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.stock.ShareAndFriendsSearchActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_My_FollowFriend;
import com.example.shareholders.fragment.Fragment_My_FollowedFriend;
import com.example.shareholders.fragment.Fragment_My_MutualFriend;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_stock_friends)
public class StockFriendsActivityCopy extends FragmentActivity {

	@ViewInject(R.id.title_search)
	private ImageView iv_search;

	@ViewInject(R.id.friendstock_title)
	private TextView tv_title;

	String userUuid = "";
	String userName = "";

	@ViewInject(R.id.rl_content)
	private RelativeLayout rl_content;

	// 返回
	@ViewInject(R.id.title_note)
	private ImageView iv_back;
	FragmentManager fragmentManager;
	Fragment_My_MutualFriend fragment_mutualFriend;
	Fragment_My_FollowFriend fragment_followFriend;
	Fragment_My_FollowedFriend fragment_followedFriend;

	// 相互关注的好友
	@ViewInject(R.id.tv_follow_each_other)
	private TextView tv_follow_each_other;

	// 关注的好友
	@ViewInject(R.id.tv_follow)
	private TextView tv_follow;

	// 被关注的好友
	@ViewInject(R.id.tv_not_follow)
	private TextView tv_not_follow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		// 如果从好友个人中心跳转过来
		try {
			Intent intent = getIntent();
			userUuid = intent.getExtras().getString("uuid");
			userName = intent.getExtras().getString("userName");
			Log.d("userName Copy", userName+"www");
			if (!userUuid.equals("")) {
				tv_title.setText(userName + "的股友列表");
				iv_search.setVisibility(View.GONE);
			}
		} catch (Exception e) {

		}

		try {
			Intent intent = getIntent();
			String share = intent.getExtras().getString("share");
			if (share.equals("share")) {

				tv_title.setText("分享到");
				iv_search.setVisibility(View.GONE);
			}

		} catch (Exception e) {

		}

		fragmentManager = getSupportFragmentManager();
		initFragments();
		setTabSelection(0);

	}

	/*
	 * @Override protected void onResume() { // TODO Auto-generated method stub
	 * super.onResume();
	 * 
	 * 
	 * }
	 * 
	 * @Override protected void onPause() { // TODO Auto-generated method stub
	 * super.onPause(); FragmentTransaction transaction =
	 * fragmentManager.beginTransaction();
	 * transaction.detach(fragment_mutualFriend);
	 * transaction.detach(fragment_followFriend);
	 * transaction.detach(fragment_followedFriend);
	 * transaction.remove(fragment_mutualFriend);
	 * transaction.remove(fragment_followFriend);
	 * transaction.remove(fragment_followedFriend);
	 * 
	 * }
	 */

	private void initFragments() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Log.d(this.toString(), "initFragments()");
		if (fragment_mutualFriend == null) {
			fragment_mutualFriend = new Fragment_My_MutualFriend();
			transaction.add(R.id.rl_content, fragment_mutualFriend);
		}
		if (fragment_followFriend == null) {
			fragment_followFriend = new Fragment_My_FollowFriend();
			transaction.add(R.id.rl_content, fragment_followFriend);
		}
		if (fragment_followedFriend == null) {
			fragment_followedFriend = new Fragment_My_FollowedFriend();
			transaction.add(R.id.rl_content, fragment_followedFriend);
		}
		transaction.commitAllowingStateLoss();
	}

	private void setTabSelection(int index) {
		// TODO Auto-generated method stub
		clearSelection();
		hideFragments();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		switch (index) {
		case 0:
			tv_follow_each_other
					.setBackgroundResource(R.drawable.bg_tab_selected);
			transaction.show(fragment_mutualFriend);
			break;
		case 1:
			tv_follow.setBackgroundResource(R.drawable.bg_tab_selected);
			transaction.show(fragment_followFriend);
			break;
		case 2:
			tv_not_follow.setBackgroundResource(R.drawable.bg_tab_selected);
			transaction.show(fragment_followedFriend);
			break;
		}
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (fragment_mutualFriend != null) {
			transaction.hide(fragment_mutualFriend);
		}
		if (fragment_followFriend != null) {
			transaction.hide(fragment_followFriend);
		}
		if (fragment_followedFriend != null) {
			transaction.hide(fragment_followedFriend);
		}
		transaction.commitAllowingStateLoss();

	}

	private void clearSelection() {
		tv_follow.setBackgroundResource(R.drawable.bg_tab_unselected);
		tv_not_follow.setBackgroundResource(R.drawable.bg_tab_unselected);
		tv_follow_each_other
				.setBackgroundResource(R.drawable.bg_tab_unselected);

	}

	@OnClick({ R.id.title_note, R.id.tv_follow_each_other, R.id.tv_follow,
			R.id.tv_not_follow, R.id.title_search ,R.id.rl_return})
	private void onClick(View v) {
		switch (v.getId()) {

		case R.id.title_note:
			finish();
			break;
		case R.id.tv_follow_each_other:
			setTabSelection(0);
			break;
		case R.id.tv_follow:
			setTabSelection(1);
			break;
		case R.id.tv_not_follow:
			setTabSelection(2);
			break;
		case R.id.title_search:
			Intent intent = new Intent(StockFriendsActivityCopy.this,
					ShareAndFriendsSearchActivity.class);
			RsSharedUtil.putBoolean(StockFriendsActivityCopy.this,
					AppConfig.SET_PAGE, true);
			startActivity(intent);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}
}
