package com.example.shareholders.fragment;

import com.baidu.location.e.r;
import com.example.shareholders.MainActivity;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.ShareAndFriendsSearchActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Fragment_Price_NotLogin extends Fragment {
	@ViewInject(R.id.iv_nothing)
	ImageView iv_nothing;
	View mview;

	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater.inflate(R.layout.fragment_price_notlogin, container,
				false);
		ViewUtils.inject(this, mview);
		return mview;
	}*/

	@OnClick(R.id.iv_nothing)
	void oncilck(View v) {
		switch (v.getId()) {
		case R.id.iv_nothing:
			startActivity(new Intent(getActivity(),
					ShareAndFriendsSearchActivity.class));
			break;

		default:
			break;
		}
	}
}
