package com.example.shareholders.fragment;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.shop.MyCollectedGoodsActivity;
import com.example.shareholders.activity.shop.MyConcernCompanyActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 左侧menu的fragment 操作menu逻辑以及显示 状态等
 * 
 */
public class LeftMenuFragment extends Fragment{
	// 显示的数据
	private List<String> menuList;
	// 昵称
	@ViewInject(R.id.tv_name)
	private TextView name;
	

	DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true)
	.cacheOnDisc(true).build();

	PersonalInformation personalInformation;

	// 位置
	@ViewInject(R.id.tv_location)
	private TextView position;
	@ViewInject(R.id.ic_face)
	private CircleImageView ic_face;

	private String uuid;

	
/*	private BroadcastReceiver broadcastReceiver=new BroadcastReceiver()
	{

		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
	
			name.setText(personalInformation.getUserName());

			// 获取城市
			position.setText((personalInformation.getLocationName() == null || personalInformation
					.getLocationName().equals("")) ? "未填写"
					: personalInformation.getLocationName());

			ImageLoader.getInstance().displayImage(
					personalInformation.getUserLogo(), ic_face,defaultOptions);
		}};
*/
/*	@Override
	public void onStart() {
		// 接受广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("updateShopHeadView");
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}*/
	
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.left_menu_fragment, null);
		ViewUtils.inject(this, v);
		
		uuid = RsSharedUtil.getString(getActivity(), AppConfig.UUID);
		if (uuid == null || uuid.equals("")) {

		} else {
			Log.d("zzzzzzzz", uuid);
			initLocalMessage(uuid);
		}
		return v;
	}


	private void initLocalMessage(String uuid) {
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			personalInformation = dbUtils.findById(PersonalInformation.class,
					uuid);
			if (personalInformation != null) {

				name.setText(personalInformation.getUserName());

				// 获取城市
				position.setText((personalInformation.getLocationName() == null || personalInformation
						.getLocationName().equals("")) ? "未填写"
						: personalInformation.getLocationName());

				ImageLoader.getInstance().displayImage(
						personalInformation.getUserLogo(), ic_face,defaultOptions);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("ksb", e.toString());
		}
	}

	@OnClick({R.id.ic_face, R.id.ll_my_collect, R.id.ll_my_concern_enterprises })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.ll_my_collect:
			//登录才能进去看收藏的商品
			if (!RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN).equals("")) {
				startActivity(new Intent(getActivity(),
						MyCollectedGoodsActivity.class));
			}else {//不登录则提示需要登录
				InternetDialog internetDialog = new InternetDialog(getActivity());
				internetDialog.showInternetDialog("请先登录", false);
			}
			
			break;
		case R.id.ll_my_concern_enterprises:
			//登录才能进去看关注的公司
			if (!RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN).equals("")) {
				startActivity(new Intent(getActivity(),
						MyConcernCompanyActivity.class));
			}else {//不登录则提示需要登录
				InternetDialog internetDialog = new InternetDialog(getActivity());
				internetDialog.showInternetDialog("请先登录", false);
			}
			break;
		case R.id.ic_face:
			//登录才能跳到个人详情
			if (!RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN).equals("")) {
				startActivity(new Intent(getActivity(),
						MyProfileActivity.class));
			}else {//不登录则提示需要登录
				InternetDialog internetDialog = new InternetDialog(getActivity());
				internetDialog.showInternetDialog("请先登录", false);
			}
			break;
				
		default:
			break;
		}
	}

}
