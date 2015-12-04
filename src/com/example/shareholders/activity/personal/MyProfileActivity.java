package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.ImagePagerActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_My_Comment;
import com.example.shareholders.fragment.Fragment_My_Friend;
import com.example.shareholders.fragment.Fragment_My_Stock;
import com.example.shareholders.fragment.Fragment_My_Survey;
import com.example.shareholders.jacksonModel.personal.LocalFollowedStockFriend;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.Mapper;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

@ContentView(R.layout.activity_my_profile)
public class MyProfileActivity extends FragmentActivity {
	
	private BitmapUtils bitmapUtils = null;
	
	// 昵称
	@ViewInject(R.id.tv_name)
	private TextView name;
	
	DbUtils dbUtils;

	// 行业
	@ViewInject(R.id.tv_industry)
	private TextView industry;
	// 位置
	@ViewInject(R.id.tv_location)
	private TextView position;
	// 一句话介绍自己
	@ViewInject(R.id.tv_whatsup)
	private TextView word;
	//记录当前是哪个tab
	private int index;

	// 编辑
	@ViewInject(R.id.tv_edit)
	private TextView tv_edit;
	// 返回
	@ViewInject(R.id.iv_back)
	private RelativeLayout  iv_back;
	FragmentManager fragmentManager;
	Fragment_My_Comment fragment_My_Comment;
	Fragment_My_Survey fragment_My_Survey;
	Fragment_My_Stock fragment_My_Stock;
	Fragment_My_Friend fragment_My_Friend;
	// 我的股友
	@ViewInject(R.id.rl_friend)
	private RelativeLayout rl_friend;
	@ViewInject(R.id.iv_friend)
	private ImageView iv_friend;
	@ViewInject(R.id.iv_friend_state)
	private ImageView iv_friend_state;
	// 我的评论
	@ViewInject(R.id.rl_comment)
	private RelativeLayout rl_comment;
	@ViewInject(R.id.iv_comment)
	private ImageView iv_comment;
	@ViewInject(R.id.iv_comment_state)
	private ImageView iv_comment_state;
	// 我的调研
	@ViewInject(R.id.rl_survey)
	private RelativeLayout rl_survey;
	@ViewInject(R.id.iv_survey)
	private ImageView iv_survey;
	@ViewInject(R.id.iv_survey_state)
	private ImageView iv_survey_state;
	// 我的股票
	@ViewInject(R.id.rl_stock)
	private RelativeLayout rl_stock;
	@ViewInject(R.id.iv_stock)
	private ImageView iv_stock;
	@ViewInject(R.id.iv_stock_state)
	private ImageView iv_stock_state;
	@ViewInject(R.id.ci_face)
	private CircleImageView ci_face;
	PersonalInformation personalInformation;
	
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		bitmapUtils = new BitmapUtils(MyProfileActivity.this);
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_other_friend);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_other_friend);
		ViewUtils.inject(this);
		
		dbUtils = DbUtils.create(this);
		loadingDialog = new LoadingDialog(this);
		loadingDialog.showLoadingDialog();
		setResult(3);
		tv_edit.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		fragmentManager = getSupportFragmentManager();
		initFragments(savedInstanceState);
		setTabSelection(0);
	}

	@Override
	protected void onResume() {
		// 从数据库获取个人信息
		if (!RsSharedUtil.getString(this, AppConfig.UUID).equals("")) {
			initLocalMessage(RsSharedUtil.getString(this, AppConfig.UUID));
			
		}
		//initFromNet();
		
		setTabSelection(index);
		super.onResume();
	}
	
	
	private void initFromNet()
	{
		String url=AppConfig.URL_USER+"profile.json?access_token="+RsSharedUtil.getString(
				MyProfileActivity.this, AppConfig.ACCESS_TOKEN)+"&userUuid=myself";
		
		Log.d("person_url", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							Log.d("person_responsee", response);
								JSONObject jsonobject = new JSONObject(response);
								String  names = jsonobject.getString("userName");
								String  userLogo = jsonobject.getString("userLogo");
								String  introduction = jsonobject.getString("introduction");
								String  industryName = jsonobject.getString("industryName");
								String  locationName = jsonobject.getString("locationName");
								String coin=jsonobject.getString("coin");
								String industyCode=jsonobject.getString("industryCode");
								String locationCode=jsonobject.getString("locationCode");
								
								name.setText(names);
								word.setText(introduction);
								// 获取行业
								industry.setText(industryName);
								// 获取城市
								position.setText(locationName);
								
								bitmapUtils.display(ci_face,userLogo);
								
								
								
								PersonalInformation personalInformation1;
								personalInformation1=new PersonalInformation();
								personalInformation1.setCoin(Integer.parseInt(coin));
								personalInformation1.setIndustryCode(industyCode);
								personalInformation1.setIndustryName(industryName);							
								personalInformation1.setLocationCode(locationCode);
								personalInformation1.setLocationName(locationName);
								personalInformation1.setIntroduction(introduction);			
								personalInformation1.setUserLogo(userLogo);
								personalInformation1.setUserName(names);
								personalInformation1.setUuid(RsSharedUtil.getString(MyProfileActivity.this,
										AppConfig.UUID));
								dbUtils.saveOrUpdate(personalInformation1);

								
						} catch (Exception e) {

						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							
								JSONObject jsonObject = new JSONObject(error
										.data());
								Log.d("error_description",
										jsonObject.getString("description"));
							
						}
						catch (Exception e) {

						}
					}
				});

		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void initLocalMessage(String uuid) {
		
		try {
			personalInformation = dbUtils.findById(PersonalInformation.class,uuid);
			if(personalInformation!=null)
			{
			name.setText(personalInformation.getUserName());
			word.setText(personalInformation.getIntroduction());
			// 获取行业
			industry.setText((personalInformation.getIndustryName() == null
					|| personalInformation.getIndustryName().equals("")) ? "未填写"
					: personalInformation.getIndustryName());
			// 获取城市
			position.setText((personalInformation.getLocationName() == null || personalInformation
					.getLocationName().equals("")) ? "未填写"
					: personalInformation.getLocationName());
			bitmapUtils.display(ci_face,
					personalInformation.getUserLogo());
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initFragments(Bundle savedInstanceState) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Log.d(this.toString(), "initFragments()");
		if (savedInstanceState!=null) {
			
		}else {
			if (fragment_My_Comment == null) {
				fragment_My_Comment = new Fragment_My_Comment();
				transaction.add(R.id.rl_content, fragment_My_Comment);
			}
			if (fragment_My_Survey == null) {
				fragment_My_Survey = new Fragment_My_Survey();
				transaction.add(R.id.rl_content, fragment_My_Survey);
			}
			if (fragment_My_Stock == null) {
				fragment_My_Stock = new Fragment_My_Stock();
				transaction.add(R.id.rl_content, fragment_My_Stock);
			}
			if (fragment_My_Friend == null) {
				fragment_My_Friend = new Fragment_My_Friend();
				transaction.add(R.id.rl_content, fragment_My_Friend);
			}
		}
		
		transaction.commitAllowingStateLoss();
	}

	private void setTabSelection(int index) {
		// TODO Auto-generated method stub
		clearSelection();
		hideFragments();

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		switch (index) {
		case 0:
			iv_friend_state.setVisibility(View.VISIBLE);
			iv_friend.setImageResource(R.drawable.btn_guyou_selected);
			transaction.show(fragment_My_Friend);
			break;
		case 1:
			iv_comment_state.setVisibility(View.VISIBLE);
			iv_comment.setImageResource(R.drawable.btn_pinglun_selected);
			transaction.show(fragment_My_Comment);
			break;
		case 2:
			iv_survey_state.setVisibility(View.VISIBLE);
			iv_survey
					.setImageResource(R.drawable.btn_personal_diaoyan_selected);
			transaction.show(fragment_My_Survey);
			break;
		case 3:
			iv_stock_state.setVisibility(View.VISIBLE);
			iv_stock.setImageResource(R.drawable.btn_gupiao_selected);
			transaction.show(fragment_My_Stock);
			break;
		}
		transaction.commitAllowingStateLoss();
		loadingDialog.dismissDialog();
	}

	private void hideFragments() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (fragment_My_Comment != null) {
			transaction.hide(fragment_My_Comment);
		}
		if (fragment_My_Friend != null) {
			transaction.hide(fragment_My_Friend);
		}
		if (fragment_My_Stock != null) {
			transaction.hide(fragment_My_Stock);
		}
		if (fragment_My_Survey != null) {
			transaction.hide(fragment_My_Survey);
		}
		transaction.commitAllowingStateLoss();
	}

	private void clearSelection() {
		iv_stock_state.setVisibility(View.GONE);
		iv_comment_state.setVisibility(View.GONE);
		iv_friend_state.setVisibility(View.GONE);
		iv_survey_state.setVisibility(View.GONE);
		iv_stock.setImageResource(R.drawable.btn_gupiao_normal);
		iv_comment.setImageResource(R.drawable.btn_pinglun_normal);
		iv_friend.setImageResource(R.drawable.btn_guyou_normal);
		iv_survey.setImageResource(R.drawable.btn_personal_diaoyan_normal);
	}

	@OnClick({ R.id.ci_face, R.id.iv_back, R.id.rl_comment, R.id.rl_friend,
			R.id.rl_stock, R.id.rl_survey, R.id.tv_edit })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_edit:
			startActivity(new Intent(MyProfileActivity.this,
					MyInformationActivity.class));
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_friend:
			setTabSelection(0);
			index = 0;
			break;
		case R.id.rl_comment:
			setTabSelection(1);
			index=1;
			break;
		case R.id.rl_survey:
			setTabSelection(2);
			index=2;
			break;
		case R.id.rl_stock:
			setTabSelection(3);
			index=3;
			break;
		case R.id.ci_face:
			imageBrower(new String[] { personalInformation.getUserLogo() });
			break;
		default:
			break;
		}
	}

	private void imageBrower(String[] urls) {
		Intent intent = new Intent(MyProfileActivity.this,
				ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		MyProfileActivity.this.startActivity(intent);
	}

	public String getUserName() {
		return name.getText().toString();
	}

}
