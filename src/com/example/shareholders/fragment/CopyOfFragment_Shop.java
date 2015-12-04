package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@SuppressLint("ResourceAsColor")
public class CopyOfFragment_Shop extends Fragment implements OnScrollListener {

	FragmentManager fragmentManager;
	Fragment_Hot_Product fragment_product;
	Fragment_Hot_Shop fragment_shop;
	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	private ArrayList<Fragment> pagerFragmentList;
	// 热门商品
	@ViewInject(R.id.tv_hot_product)
	private TextView tv_hot_product;
	// 热门公司
	@ViewInject(R.id.tv_hot_shop)
	private TextView tv_hot_shop;

	View myView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myView = inflater.inflate(R.layout.fragment_shop_top, null);
		ViewUtils.inject(this, myView);
		init();
		return myView;
	}

	private void init() {
		viewPager = (ViewPager) myView.findViewById(R.id.vp_shop_hot);
		fragment_product = new Fragment_Hot_Product();
		fragment_shop = new Fragment_Hot_Shop();
		pagerFragmentList = new ArrayList<Fragment>();
		pagerFragmentList.add(fragment_product);
		pagerFragmentList.add(fragment_shop);
		pagerAdapter = new ViewPagerAdapter(getActivity()
				.getSupportFragmentManager(), pagerFragmentList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(0);
	}

	@OnClick({ R.id.tv_hot_product, R.id.tv_hot_shop })
	private void Onclick(View view) {
		switch (view.getId()) {

		case R.id.tv_hot_product:
			viewPager.setCurrentItem(0);
			tv_hot_product.setTextSize(15);
			tv_hot_product.setTextColor(Color.parseColor("#FFFFFF"));
			tv_hot_shop.setTextSize(13);
			tv_hot_shop.setTextColor(Color.parseColor("#d8d8d8"));
			break;

		case R.id.tv_hot_shop:
			viewPager.setCurrentItem(1);
			tv_hot_product.setTextSize(13);
			tv_hot_product.setTextColor(Color.parseColor("#d8d8d8"));
			tv_hot_shop.setTextSize(15);
			tv_hot_shop.setTextColor(Color.parseColor("#FFFFFF"));
			break;

		default:
			break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}
