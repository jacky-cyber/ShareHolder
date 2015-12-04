package com.example.shareholders.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContentViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> list;

	public ContentViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);

		this.list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return list.size();
	}
}
