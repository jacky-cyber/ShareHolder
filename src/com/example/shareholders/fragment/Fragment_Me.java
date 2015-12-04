package com.example.shareholders.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.example.shareholders.R;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.activity.personal.CommentCollectionActivity;
import com.example.shareholders.activity.personal.ConsultsCollectActivity;
import com.example.shareholders.activity.personal.MessageCenterActivity;
import com.example.shareholders.activity.personal.MyFriendMomentActivity;
import com.example.shareholders.activity.personal.MyInformationActivity;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.MySurveyListActivity;
import com.example.shareholders.activity.personal.RecordManageActivity;
import com.example.shareholders.activity.personal.StockFriendsActivityCopy;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.receiver.UpdateInformationReceiver;
import com.example.shareholders.receiver.UpdateInformationReceiver.refreshInformation;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Fragment_Me extends Fragment implements refreshInformation {
	@ViewInject(R.id.rl_head)
	private RelativeLayout rl_head;
	@ViewInject(R.id.tv_state)
	private TextView tv_name;
	@ViewInject(R.id.ic_face)
	private CircleImageView ci_headView;
	
	private BitmapUtils bitmapUtils;
	
	// 未读信息数
	@ViewInject(R.id.iv_information)
	private TextView iv_information;
	UpdateInformationReceiver updateInformationReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_me, null);
		ViewUtils.inject(this, v);
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_other_friend);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_other_friend);
		initData();
		return v;

	}

	/**
	 * 获取未读消息数量
	 */
	private void getUnreadMsgCountTotal() {
		int count = EMChatManager.getInstance().getUnreadMsgsCount();
		Log.d("main", "count = " + count);
		if (count == 0) {
			iv_information.setVisibility(View.GONE);
		} else {
			iv_information.setVisibility(View.VISIBLE);
			iv_information.setText(count + "");
		}

	}

	@Override
	public void onStart() {
		// 接受广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("updateInformationReceiver");
		updateInformationReceiver = new UpdateInformationReceiver();
		getActivity().registerReceiver(updateInformationReceiver, intentFilter);
		updateInformationReceiver.setRefreshInformaiton(this);
		super.onStart();
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(updateInformationReceiver);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if (RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN).equals("")) {
			tv_name.setText("未登录");
			ci_headView.setImageResource(R.drawable.ico_default_headview);

		}
		else 
		{
			initData();
			getUnreadMsgCountTotal();// 获取未读消息
		}
		super.onResume();
	}

	private void initData() {
		Log.d("Fragment_ME", "initData");
		if (!RsSharedUtil.getString(getActivity(), AppConfig.UUID).equals("")) {
			// 获取个人信息
			DbUtils dbUtils = DbUtils.create(getActivity());
			try {
				PersonalInformation personalInformation = dbUtils.findById(
						PersonalInformation.class,
						RsSharedUtil.getString(getActivity(), AppConfig.UUID));
				tv_name.setText(personalInformation.getUserName());
				bitmapUtils.display(ci_headView, personalInformation.getUserLogo());
				/*ImageLoader.getInstance().displayImage(
						personalInformation.getUserLogo(), ci_headView);*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@OnClick({ R.id.rl_my_survey, R.id.rl_head, R.id.rl_friend_list,
			R.id.rl_collected_consults, R.id.rl_voice_record,
			R.id.rl_information_center, R.id.rl_collected_comments,
			R.id.rl_friend_moment })
	private void onClick(View v) {
		Intent intent = new Intent();
		// 如果未登录，直接去登录
		if (RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN) == "") {
			intent.setClass(getActivity(), LoginActivity.class);
			intent.putExtra("isFrist", false);
			startActivity(intent);
		} else {
			switch (v.getId()) {
			case R.id.rl_friend_list://股友列表
				intent.setClass(getActivity(), StockFriendsActivityCopy.class);
				startActivity(intent);
				break;
			case R.id.rl_collected_consults://资讯收藏
				intent.setClass(getActivity(), ConsultsCollectActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_friend_moment:
				Log.d("哈哈哈哈", "哈哈哈");//股友动态
				intent.setClass(getActivity(), MyFriendMomentActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_collected_comments://评论收藏
				Log.d("哈哈哈哈", "哈哈哈");
				intent.setClass(getActivity(), CommentCollectionActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_head://个人主页
				intent.setClass(getActivity(), MyProfileActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_voice_record://录音管理
				intent.setClass(getActivity(), RecordManageActivity.class);
				startActivity(intent);
				break;
			case R.id.rl_my_survey://我的调研
				intent.setClass(getActivity(), MySurveyListActivity.class)
						.putExtra("userUuid",RsSharedUtil.getString(getActivity(),"userUuid"));
				startActivity(intent);
				break;
			case R.id.rl_information_center://设置
				intent.setClass(getActivity(), MessageCenterActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		initData();
	}
}
