package com.example.shareholders.activity.stock;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_ShareFriends_Search;
import com.example.shareholders.fragment.Fragment_Share_Search;
import com.example.shareholders.fragment.Fragment_Share_Search2;
import com.example.shareholders.fragment.Fragment_Share_SearchFriends_List;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_share_and_friends_search)
public class ShareAndFriendsSearchActivity extends FragmentActivity implements OnClickListener {

	public static ViewPager ViewPager;
	private ViewPagerAdapter fPagerAdapter;
	private ArrayList<Fragment> pagerFargmentList;

	@ViewInject(R.id.et_sf_search_text)
	public EditText mEditText;
	@ViewInject(R.id.iv_delete)
	private ImageView iv_delete;

	@ViewInject(R.id.rb_search_sharefriends)
	private RadioButton rb_search_sharefriends;
	
	private int index = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initView();
		// 如果是从个人中心的股友列表进入的，把ViewPager设置为第一页
		if (RsSharedUtil.getBoolean(ShareAndFriendsSearchActivity.this, AppConfig.SET_PAGE, false)) {
			index = 1;
			ViewPager.setCurrentItem(index);
			mEditText.setHint(R.string.please_input_name);
			rb_search_sharefriends.setChecked(true);
			RsSharedUtil.putBoolean(getApplicationContext(), AppConfig.SET_PAGE, false);

		}
		
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (ViewPager.getCurrentItem() == 1||ViewPager.getCurrentItem() == 3) {
					ViewPager.setCurrentItem(3);
					Intent intent = new Intent(); // 要发送的内容
					intent.putExtra("findFriends", mEditText.getText().toString());
					intent.setAction("findFriends"); // 设置广播的action
					sendBroadcast(intent); // 发送广播
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable sEditable) {

				// 后面根据内容修改
				if (sEditable.length() == 0) {

					if (ViewPager.getCurrentItem() == 0)
						ViewPager.setCurrentItem(0);
					if (ViewPager.getCurrentItem() == 1)
						ViewPager.setCurrentItem(1);
				} else {

					if (ViewPager.getCurrentItem() == 0)

					{
						ViewPager.setCurrentItem(2);
						Intent intent = new Intent(); // 要发送的内容
						intent.putExtra("datas", mEditText.getText().toString());
						intent.setAction("ss_edit"); // 设置广播的action
						sendBroadcast(intent); // 发送广播
					}

					if (ViewPager.getCurrentItem() == 2) {
						Intent intent = new Intent(); // 要发送的内容
						intent.putExtra("datas", mEditText.getText().toString());
						intent.setAction("ss_edit"); // 设置广播的action
						sendBroadcast(intent); // 发送广播
					}

					if (ViewPager.getCurrentItem() == 1) {
						ViewPager.setCurrentItem(3);
						Intent intent = new Intent(); // 要发送的内容
						intent.putExtra("findFriends", mEditText.getText().toString());
						intent.setAction("findFriends"); // 设置广播的action
						sendBroadcast(intent); // 发送广播
					}
				}
			}
		});

	}

	private void initView() {
		ViewPager = (ViewPager) findViewById(R.id.id_sfsearch_viewpager);

		pagerFargmentList = new ArrayList<Fragment>();
		Fragment sharesearch = new Fragment_Share_Search();
		Fragment sharesearch2 = new Fragment_Share_Search2();
		Fragment sharefriends = new Fragment_ShareFriends_Search();
		Fragment sharefriendslist = new Fragment_Share_SearchFriends_List();
		pagerFargmentList.add(sharesearch);
		pagerFargmentList.add(sharefriends);
		pagerFargmentList.add(sharesearch2);
		pagerFargmentList.add(sharefriendslist);

		fPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), pagerFargmentList);
		ViewPager.setAdapter(fPagerAdapter);
	}

	@OnClick({ R.id.rb_search_share, R.id.rb_search_sharefriends, R.id.iv_return, R.id.iv_delete, R.id.tv_ac_search })
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.rb_search_share:
			ViewPager.setCurrentItem(0);
			mEditText.setHint(R.string.please_input);
			break;
		case R.id.rb_search_sharefriends:
			ViewPager.setCurrentItem(1);
			mEditText.setHint(R.string.please_input_name);
			break;
		case R.id.iv_delete:
			mEditText.setText(null);
			break;
		case R.id.tv_ac_search:
			search();
			break;
		default:
			break;
		}
	}

	private void search() {
		if (ViewPager.getCurrentItem() == 0 || ViewPager.getCurrentItem() == 2)

		{
			ViewPager.setCurrentItem(2);
			Intent intent = new Intent(); // 要发送的内容
			intent.putExtra("datas", mEditText.getText().toString());
			intent.setAction("ss_edit"); // 设置广播的action
			sendBroadcast(intent); // 发送广播
		}
		if (ViewPager.getCurrentItem() == 1 || ViewPager.getCurrentItem() == 3)

		{
			ViewPager.setCurrentItem(3);
			Intent intent = new Intent(); // 要发送的内容
			intent.putExtra("findFriends", mEditText.getText().toString());
			intent.setAction("findFriends"); // 设置广播的action
			sendBroadcast(intent); // 发送广播
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	//下面三个用于隐藏软键盘
		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {

			if (ev.getAction() == MotionEvent.ACTION_DOWN) {

				// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
				View v = getCurrentFocus();

				if (isShouldHideInput(v, ev)) {
					hideSoftInput(v.getWindowToken());
				}
			}

			return super.dispatchTouchEvent(ev);
		}
		/**
		 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
		 * 
		 * @param v
		 * @param event
		 * @return
		 */
		private boolean isShouldHideInput(View v, MotionEvent event) {
			if (v != null && (v instanceof EditText)) {
				int[] l = { 0, 0 };
				v.getLocationInWindow(l);
				int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
						+ v.getWidth();
				if (event.getRawX() > left && event.getRawX() < right
						&& event.getRawY() > top && event.getRawY() < bottom) {
					// 点击EditText的事件，忽略它。
					return false;
				} else {
					return true;
				}
			}
			// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
			return false;
		}
		/**
		 * 多种隐藏软件盘方法的其中一种
		 * 
		 * @param token
		 */
		private void hideSoftInput(IBinder token) {
			if (token != null) {
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(token,
						InputMethodManager.HIDE_NOT_ALWAYS);
	
			}else {
				
			}
			
		}

}
