package com.example.shareholders.activity.newthird;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.e.v;
import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.MyHScrollView;
import com.example.shareholders.common.MyHScrollView.OnScrollChangedListener;
import com.example.shareholders.fragment.Fragment_NewThird_Search;
import com.example.shareholders.fragment.Fragment_NewThird_SearchHistory;
import com.example.shareholders.fragment.Fragment_NewThrid_Myself;
import com.example.shareholders.fragment.Fragment_NewThrid_Situation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
@ContentView(R.layout.activity_newthird_search)
public class NewThirdSearchActivity extends FragmentActivity{
	@ViewInject(R.id.et_sf_search_text)
	private EditText mEditText;
	private ViewPager viewpager;
	private ViewPagerAdapter fPagerAdapter;
	private ArrayList<Fragment> pagerFargmentList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initview();
	}
	private void initview(){
		viewpager=(ViewPager)findViewById(R.id.id_newthrid_viewpager);
		pagerFargmentList = new ArrayList<Fragment>();
		Fragment serachfragment=new Fragment_NewThird_Search();
		Fragment serachhistoryfragment=new Fragment_NewThird_SearchHistory();
		pagerFargmentList.add(serachhistoryfragment);
		pagerFargmentList.add(serachfragment);
		fPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), pagerFargmentList);
		viewpager.setAdapter(fPagerAdapter);
		mEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				// TODO Auto-generated method stub
				if (editable.length()==0) {
					viewpager.setCurrentItem(0);
				}else {
					viewpager.setCurrentItem(1);
					Intent intent = new Intent(); // 要发送的内容
					intent.putExtra("datas", mEditText.getText().toString());
					intent.setAction("nts_edit"); // 设置广播的action
					sendBroadcast(intent); // 发送广播
				}
				
			}
		});
	}
	@OnClick({R.id.iv_delete,R.id.rl_return})
	private void OnClick(View view){
		switch (view.getId()) {
		case R.id.iv_delete:
			mEditText.setText(null);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
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
				}
			}
}
