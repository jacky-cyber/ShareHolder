package com.example.shareholders.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.activity.fund.FundHomeActivity;
import com.example.shareholders.activity.login.LogAndRegiActivity;
import com.example.shareholders.activity.newthird.NewThirdActivity;
import com.example.shareholders.activity.personal.StockFriendsActivityCopy;
import com.example.shareholders.view.CircleMenuLayout;
import com.example.shareholders.view.CircleMenuLayout.OnMenuItemClickListener;

public class CopyOfFragment_Home extends Fragment {

	private CircleMenuLayout mCircleMenuLayout;
	private TextView tv_home_shareholder;

	private String[] mItemTexts = new String[] { "调研", "股友", "沪深", "基金", "商城",
			"我的", "新三板" };
	private int[] mItemImgs = new int[] { R.drawable.menu_diaoyan,
			R.drawable.menu_guyou, R.drawable.menu_hushen,
			R.drawable.menu_jijin, R.drawable.menu_shangcheng,
			R.drawable.menu_wode, R.drawable.menu_xinsanban };

	private final int SURVEY = 0;
	private final int STOCKFRIENDS = 1;
	private final int PRICE = 2;
	private final int FUND = 3;
	private final int SHOP = 4;
	private final int ME = 5;
	private final int NEWTHREE = 6;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.copyof_fragment_home, null);
		mCircleMenuLayout = (CircleMenuLayout) v
				.findViewById(R.id.id_menulayout);
		// tv_home_shareholder = (TextView) v
		// .findViewById(R.id.tv_home_shareholder);
		// tv_home_shareholder.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// startActivity(new Intent(getActivity(),
		// MyStockDetailsActivity.class));
		// }
		// });
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		super.onActivityCreated(savedInstanceState);
	}

	public void init() {

		mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);

		mCircleMenuLayout
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void itemClick(View view, int pos) {
						// Toast.makeText(getActivity(), mItemTexts[pos],
						// Toast.LENGTH_SHORT).show();
						Intent intent = null;
						Bundle bundle = null;
						switch (pos) {
						case SURVEY:
							// 跳到调研页,即index = 1;
							intent = new Intent("LoginReceiver");
							bundle = new Bundle();
							bundle.putInt("index", 1);
							intent.putExtras(bundle);
							getActivity().sendBroadcast(intent);
							break;
						case STOCKFRIENDS:
							startActivity(new Intent(getActivity(),
									StockFriendsActivityCopy.class));
							break;
						case PRICE:
							// 跳到沪深,即index = 2;
							intent = new Intent("LoginReceiver");
							bundle = new Bundle();
							bundle.putInt("index", 2);
							intent.putExtras(bundle);
							getActivity().sendBroadcast(intent);
							break;
						case FUND:
							startActivity(new Intent(getActivity(),
									FundHomeActivity.class));
							break;
						case SHOP:
							// 跳到商店,即index = 3;
							intent = new Intent("LoginReceiver");
							bundle = new Bundle();
							bundle.putInt("index", 3);
							intent.putExtras(bundle);
							getActivity().sendBroadcast(intent);
							break;
						case ME:
							// 跳到我的,即index = 4;
							intent = new Intent("LoginReceiver");
							bundle = new Bundle();
							bundle.putInt("index", 4);
							intent.putExtras(bundle);
							getActivity().sendBroadcast(intent);
							break;
						case NEWTHREE:
							// 跳到新三板;
							startActivity(new Intent(getActivity(),NewThirdActivity.class));
							break;

						default:
							break;
						}

					}

					@Override
					public void itemCenterClick(View view) {
						startActivity(new Intent(getActivity(),
								LogAndRegiActivity.class));
					}
				});
	}

}
