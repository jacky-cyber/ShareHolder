package com.example.shareholders.fragment;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_B_Shares extends Fragment{

	View mview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_share_list, container, false);
		ViewUtils.inject(this, mview);
		return mview;
	}
}
