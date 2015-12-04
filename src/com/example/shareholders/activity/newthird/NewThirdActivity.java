package com.example.shareholders.activity.newthird;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.fragment.Fragment_NewThrid_Myself;
import com.example.shareholders.fragment.Fragment_NewThrid_Situation;
import com.example.shareholders.fragment.Fragment_Price;
import com.example.shareholders.fragment.Fragment_Share_Search;
import com.example.shareholders.fragment.Fragment_Survey;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

@ContentView(R.layout.activity_newthird)
public class NewThirdActivity extends FragmentActivity {
	// 是否已经实时刷新
	private boolean haspost = false;
	private Handler mHandler;
	Fragment_NewThrid_Situation fragment_NewThrid_Situation;
	Fragment_NewThrid_Myself fragment_NewThrid_Myself;
	@ViewInject(R.id.tv_xinzen)
	private TextView tv_xinzen;
	@ViewInject(R.id.tv_jiguo)
	private TextView tv_jiguo;
	@ViewInject(R.id.tv_huodong)
	private TextView tv_huodong;
	@ViewInject(R.id.rb_choose_situation)
	private RadioButton rb_choose_situation;
	@ViewInject(R.id.rb_choose_myself)
	private RadioButton rb_choose_myself;
	@ViewInject(R.id.iv_sousuo)
	ImageView iv_sousuo;
	@ViewInject(R.id.tv_guanli)
	private TextView tv_guanli;
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mHandler.post(mRunnable);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(mRunnable);
		super.onPause();
	}

	private void initview() {
		// 加下划线
		tv_xinzen.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_jiguo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv_huodong.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		viewpager = (ViewPager) findViewById(R.id.id_newthrid_viewpager);
		pagerFargmentList = new ArrayList<Fragment>();
		Fragment new_situation = new Fragment_NewThrid_Situation();
		Fragment new_myslef = new Fragment_NewThrid_Myself();
		pagerFargmentList.add(new_situation);
		pagerFargmentList.add(new_myslef);
		fPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), pagerFargmentList);
		viewpager.setAdapter(fPagerAdapter);
		// ViewPagerAdapter cAdapter = (ViewPagerAdapter)
		// viewpager.getAdapter();
		// fragment_NewThrid_Situation = (Fragment_NewThrid_Situation)
		// fPagerAdapter.instantiateItem(viewpager, 0);
		// fragment_NewThrid_Myself = (Fragment_NewThrid_Myself)
		// fPagerAdapter.instantiateItem(viewpager, 1);
		fragment_NewThrid_Situation = new Fragment_NewThrid_Situation();
		fragment_NewThrid_Myself = new Fragment_NewThrid_Myself();
		mHandler = new Handler();

	}

	@OnClick({ R.id.rb_choose_situation, R.id.rb_choose_myself, R.id.rl_return, R.id.iv_sousuo, R.id.tv_guanli,
			R.id.tv_xinzen, R.id.tv_jiguo, R.id.tv_huodong })
	private void onclick(View view) {

		Intent intent = new Intent();
		Uri content_url=null;
		switch (view.getId()) {
		case R.id.rb_choose_situation:

			Log.d("asdasd", "asd");
			viewpager.setCurrentItem(0);
			iv_sousuo.setVisibility(View.VISIBLE);
			tv_guanli.setVisibility(View.GONE);
			break;
		case R.id.rb_choose_myself:
			viewpager.setCurrentItem(1);
			iv_sousuo.setVisibility(View.GONE);
			tv_guanli.setVisibility(View.VISIBLE);
			break;
		case R.id.rl_return:
			finish();
			break;
		case R.id.iv_sousuo:
			startActivity(new Intent(NewThirdActivity.this, NewThirdSearchActivity.class));
			break;
		case R.id.tv_guanli:
			startActivity(new Intent(NewThirdActivity.this, NewThirdBianJiActivity.class));
			break;
		case R.id.tv_xinzen:
			intent.setAction("android.intent.action.VIEW");
			content_url = Uri.parse("http://www.ibstart.com/neeqs");
			intent.setData(content_url);
			startActivity(intent);
			break;
		case R.id.tv_jiguo:
			intent.setAction("android.intent.action.VIEW");
			content_url = Uri.parse("http://www.ibstart.com/investor");
			intent.setData(content_url);
			startActivity(intent);
			break;
		case R.id.tv_huodong:
			intent.setAction("android.intent.action.VIEW");
			content_url = Uri.parse("http://www.ibstart.com/information");
			intent.setData(content_url);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			switch (viewpager.getCurrentItem()) {
			case 0:
				fragment_NewThrid_Situation.gettoactivity();
				break;
			case 1:
				fragment_NewThrid_Myself.gettoactivity();
				break;
			default:
				break;

			}
			mHandler.postDelayed(this, 3000);
		}
	};
}
