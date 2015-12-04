package com.example.shareholders.activity.personal;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.shop.ChatActivity;
import com.example.shareholders.activity.survey.ImagePagerActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_My_Comment;
import com.example.shareholders.fragment.Fragment_My_Friend;
import com.example.shareholders.fragment.Fragment_My_Stock;
import com.example.shareholders.fragment.Fragment_My_Survey;
import com.example.shareholders.jacksonModel.personal.LocalFollowStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalFollowedStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalMutualStockFriend;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mob.tools.gui.Scrollable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

@ContentView(R.layout.activity_other_peolpe_information)
public class OtherPeolpeInformationActivity extends ActionBarActivity {
	
	private BitmapUtils bitmapUtils=null;

	// 编辑
	@ViewInject(R.id.tv_edit)
	private TextView tv_edit;
	// 返回
	@ViewInject(R.id.iv_back)
	private ImageView iv_back;
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
	// 用户名
	@ViewInject(R.id.tv_name)
	private TextView tv_name;
	// 添加好友
	@ViewInject(R.id.iv_add_friend)
	private ImageView iv_add_friend;
	
	//记录第几个fragment
	private int indexPosition;

	// 行业
	@ViewInject(R.id.tv_industry)
	private TextView tv_industryName;
	private String industyString;

	// 自我介绍
	@ViewInject(R.id.tv_whatsup)
	private TextView tv_whatsup;
	private String introduce;
	// 地址
	@ViewInject(R.id.tv_location)
	private TextView tv_location;
	private String cityString;

	// PersonalInformation person=new PersonalInformation();

	// 接收的uuid
	private String uuid;
	private int coin;

	private String userName;

	private String useLogo;

	private boolean isFriend;

	private ProgressDialog progressDialog;
	@ViewInject(R.id.ci_face)
	private CircleImageView ci_headView;

	private DbUtils dbUtils;
	
	//从搜索股友那里传递过来的
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewUtils.inject(this);
		//设置下载失败的默认图片
		bitmapUtils = new BitmapUtils(OtherPeolpeInformationActivity.this);
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_other_friend);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_other_friend);
		
		dbUtils = DbUtils.create(this);
		setResult(3);
		initView();
		fragmentManager = getSupportFragmentManager();
		initFragments(savedInstanceState);
		setTabSelection(0);
	}

	private void initView() {
		progressDialog = new ProgressDialog(OtherPeolpeInformationActivity.this);
		progressDialog.setMessage("正在加载好友消息...");

		Intent intent = getIntent();

		uuid = intent.getExtras().getString("uuid");
		try {
			userName = intent.getExtras().getString("userName");
			useLogo = intent.getExtras().getString("useLogo");
			tv_name.setText(userName);
			bitmapUtils.display(ci_headView,useLogo);
			progressDialog.show();
			getInformationFromWeb(uuid);
			Log.d("success_get_uuid", uuid);
			position=intent.getExtras().getInt("position");
			isFriend = intent.getExtras().getBoolean("isFriend");
			if (isFriend) {
				iv_add_friend.setImageResource(R.drawable.btn_quxiaoguanzhu1);
			} 
			else {
				iv_add_friend.setImageResource(R.drawable.btn_add);
			}		
			
		} catch (Exception e) {

		}

		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			Intent intent1=new Intent();
			intent1.putExtra("position", position);
			intent1.putExtra("isfriend", isFriend);
			setResult(Activity.RESULT_OK, intent1);
			finish();	
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getInformationFromWeb(String uuid) {
		Log.d("uuuu", uuid + "sssiiii");
		String url = AppConfig.URL_USER
				+ "profile.json?access_token="
				+ RsSharedUtil.getString(OtherPeolpeInformationActivity.this,
						AppConfig.ACCESS_TOKEN) + "&userUuid=" + uuid;
		Log.d("uuuu_url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							if (progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							
							Log.d("other_people_message", response+"mmm");
							JSONObject jsonobject = new JSONObject(response);


							userName = jsonobject.getString("userName");
							useLogo = jsonobject.getString("userLogo");
							industyString = jsonobject
									.getString("industryName");
							introduce = jsonobject.getString("introduction");
							cityString = jsonobject.getString("locationName");

							String isFriendString = jsonobject
									.getString("type");
							// 若是关注或相互关注的好友
							if (isFriendString.equalsIgnoreCase("FOLLOW")
									|| isFriendString
											.equalsIgnoreCase("MUTUAL")) {
								iv_add_friend
										.setImageResource(R.drawable.btn_quxiaoguanzhu1);
								isFriend = true;
							} else {
								iv_add_friend
										.setImageResource(R.drawable.btn_add);
								isFriend = false;
							}
							
							bitmapUtils.display(ci_headView,useLogo);
							


							if (userName.equals("null") || userName.equals("")) {
								tv_name.setText("暂无姓名");
							} 
							else {
								tv_name.setText(userName);
							}

							if (industyString.equals("null")
									|| industyString.equals("")) {
								tv_industryName.setText("暂无职业");
							}
							else {
								tv_industryName.setText(industyString);
							}

							if (cityString.equals("null")
									|| cityString.equals("")) {
								tv_location.setText("暂无地点");
							} else {
								tv_location.setText(cityString);
							}

							if (introduce.equals("null")
									|| introduce.equals("")) {
								tv_whatsup.setText("");
							} else {
								tv_whatsup.setText(introduce);
							}

						} catch (JSONException e) {
							try {
								progressDialog.dismiss();
								e.printStackTrace();
							} catch (Exception e2) {
								// TODO: handle exception
							}

						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("other_people_message_error", " dddd");
					}

				});
		MyApplication.getRequestQueue().add(stringRequest);

	}

	private void initFragments(Bundle savedInstanceState) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Log.d(this.toString(), "initFragments()");
		if (savedInstanceState==null) {
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
			transaction.commitAllowingStateLoss();
		}
		
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
			indexPosition =0 ;
			break;
		case 1:
			iv_comment_state.setVisibility(View.VISIBLE);
			iv_comment.setImageResource(R.drawable.btn_pinglun_selected);
			transaction.show(fragment_My_Comment);
			indexPosition =1 ;
			break;
		case 2:
			iv_survey_state.setVisibility(View.VISIBLE);
			iv_survey.setImageResource(R.drawable.btn_diaoyan_selected);
			transaction.show(fragment_My_Survey);
			indexPosition =2 ;
			break;
		case 3:
			iv_stock_state.setVisibility(View.VISIBLE);
			iv_stock.setImageResource(R.drawable.btn_gupiao_selected);
			transaction.show(fragment_My_Stock);
			indexPosition = 3 ;
			break;
		}
		transaction.commitAllowingStateLoss();
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
		iv_survey.setImageResource(R.drawable.btn_diaoyan_normal);
	}

	@OnClick({ R.id.iv_back, R.id.rl_comment, R.id.rl_friend, R.id.rl_stock,
			R.id.rl_survey, R.id.iv_add_friend, R.id.iv_chat, R.id.ci_face ,R.id.rl_return})
	private void onClick(View v) {
		if (!BtnClickUtils.isFastDoubleClick()) {
			switch (v.getId()) {
			case R.id.iv_back:
				Intent intent1=new Intent();
				intent1.putExtra("position", position);
				intent1.putExtra("isfriend", isFriend);
				setResult(Activity.RESULT_OK, intent1);
				finish();
				break;
			case R.id.ci_face:
				imageBrower(new String[] { useLogo });
				break;
			case R.id.rl_friend:
				setTabSelection(0);
				break;
			case R.id.rl_comment:
				setTabSelection(1);
				break;
			case R.id.rl_survey:
				setTabSelection(2);
				break;
			case R.id.rl_stock:
				setTabSelection(3);
				break;
			case R.id.iv_add_friend:
				addFriend();
				break;
			case R.id.iv_chat:
				if (uuid.equals(RsSharedUtil.getString(getApplicationContext(), AppConfig.UUID))) {
					
				}else {
					Intent intent = new Intent(OtherPeolpeInformationActivity.this,
							ChatActivity.class);
					intent.putExtra("uuid", uuid);
					intent.putExtra("type", 1);
					startActivity(intent);
				}
				
				break;
			case R.id.rl_return:
				finish();
				break;
			default:
				break;
			}
		}
	}

	private void addFriend() {
		// TODO Auto-generated method stub
		final GeneralDialog dialog = new GeneralDialog(
				OtherPeolpeInformationActivity.this);
		dialog.setCancel(true);
		// 去掉图片提示
		dialog.noMessageIcon();
		// 取消按钮的点击事件
		dialog.setNegativeButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if (isFriend) {
			progressDialog.setMessage("正在取消关注该好友...");
			dialog.setMessage("取消关注该股友吗?");
			// 确定按钮的点击事件
			dialog.setPositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					progressDialog.show();
					cancelNotice();

				}
			});

		} else

		{
			progressDialog.setMessage("正在关注该好友...");
			dialog.setMessage("关注该股友吗?");
			// 确定按钮的点击事件
			dialog.setPositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("access_token", RsSharedUtil.getString(
							OtherPeolpeInformationActivity.this,
							AppConfig.ACCESS_TOKEN));
					dialog.dismiss();
					progressDialog.show();
					guanzhu();
				}
			});

		}
	}

	private void cancelNotice() {
		String url = AppConfig.URL_USER
				+ "follow.json?access_token="
				+ RsSharedUtil.getString(OtherPeolpeInformationActivity.this,
						AppConfig.ACCESS_TOKEN) + "&userUuid=" + uuid
				+ "&type=CANCEL";
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Toast.makeText(OtherPeolpeInformationActivity.this,
								"取消关注成功！", Toast.LENGTH_SHORT).show();
						progressDialog.dismiss();
						iv_add_friend.setImageResource(R.drawable.btn_add);
						isFriend = false;

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
		stringRequest.setTag("OtherActvity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("OtherActvity");
	}

	private void guanzhu() {
		String url = AppConfig.URL_USER + "follow.json?access_token=";
		url += RsSharedUtil.getString(OtherPeolpeInformationActivity.this,
				AppConfig.ACCESS_TOKEN);
		url = url + "&userUuid=" + uuid + "&type=FOLLOW";

		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Toast.makeText(OtherPeolpeInformationActivity.this,
								"关注成功！", Toast.LENGTH_SHORT).show();
						progressDialog.dismiss();
						iv_add_friend
								.setImageResource(R.drawable.btn_quxiaoguanzhu1);
						isFriend = true;
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
		stringRequest.setTag("otherPeopleInformationActvity");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void imageBrower(String[] urls) {
		Intent intent = new Intent(OtherPeolpeInformationActivity.this,
				ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		OtherPeolpeInformationActivity.this.startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTabSelection(indexPosition);
	}
}
