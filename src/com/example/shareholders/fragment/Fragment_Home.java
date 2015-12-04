package com.example.shareholders.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundHomeActivity;
import com.example.shareholders.activity.login.LogAndRegiActivity;
import com.example.shareholders.activity.survey.DetailSurveyActivity;

public class Fragment_Home extends Fragment {

	private TextView tv_home;
	private TextView tv_login;
	private TextView tv_detail;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, null);
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		super.onActivityCreated(savedInstanceState);
	}

	public void init() {
		tv_home = (TextView) getActivity().findViewById(R.id.tv_home);
		tv_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), FundHomeActivity.class));
			}
		});

		tv_login = (TextView) getActivity().findViewById(R.id.tv_login);
		tv_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),
						LogAndRegiActivity.class));
			}
		});
		tv_detail = (TextView) getActivity().findViewById(R.id.tv_detail);
		tv_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),
						DetailSurveyActivity.class));
			}
		});

	}

}
