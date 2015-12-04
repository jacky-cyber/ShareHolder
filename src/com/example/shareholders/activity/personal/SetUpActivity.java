package com.example.shareholders.activity.personal;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.example.shareholders.R;
import com.example.shareholders.activity.login.FindPasswordActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.LocalFollowStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalFollowedStockFriend;
import com.example.shareholders.jacksonModel.personal.LocalMutualStockFriend;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.jacksonModel.survey.Banner;
import com.example.shareholders.jacksonModel.survey.HottestComment;
import com.example.shareholders.jacksonModel.survey.LastestSurvey;
import com.example.shareholders.jacksonModel.survey.SearchHistory;
import com.example.shareholders.service.GetInformationAfterClearService;
import com.example.shareholders.service.PostPersonalInformationService;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.DialogManager;
import com.example.shareholders.view.DialogManager2;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

@ContentView(R.layout.activity_set_up)
public class SetUpActivity extends Activity {

	// ProgressDialog progressDialog;
	// "正在加载"的旋转框
	private LoadingDialog loadingDialog;
	DbUtils dbUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		ViewUtils.inject(this);
		// progressDialog = new ProgressDialog(this);
		loadingDialog = new LoadingDialog(SetUpActivity.this);
	}
	
	private void dropTable()
	{
		if(NetWorkCheck.isNetworkConnected(this))
		{
		
		try {
			dbUtils.dropTable(LocalFollowedStockFriend.class);
			dbUtils.dropTable(LocalFollowStockFriend.class);
			dbUtils.dropTable(LocalMutualStockFriend.class);
			dbUtils.dropTable(SearchHistory.class);
			dbUtils.dropTable(PersonalInformation.class);
			dbUtils.dropTable(HottestComment.class);
			dbUtils.dropTable(LastestSurvey.class);
			dbUtils.dropTable(Banner.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent();
		intent.setClass(SetUpActivity.this,
				GetInformationAfterClearService.class);
		startService(intent);
		}
	}

	@OnClick({ R.id.rl_return, R.id.rl_check_for_update, R.id.rl_apply,
			R.id.rl_change_password, R.id.rl_change_binding, R.id.rl_call_back,
			R.id.tv_logout, R.id.rl_about, R.id.rl_clear_cache })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_clear_cache:

			final DialogManager2 dialogManager = new DialogManager2(this);
			dialogManager.ShowBlueDialog();
//			dialogManager.BluenoMessageIcon();
			dialogManager.setBlueMessage("确定清除缓存吗？");
			dialogManager.setBluePositiveButton(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
			
						dbUtils = DbUtils.create(SetUpActivity.this);
						dropTable();
						
					dialogManager.dismiss();
				}
			});
			dialogManager.setBlueNegativeButton(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogManager.dismiss();
				}
			});
			break;
		case R.id.rl_return:
			finish();
			break;
		// 关于
		case R.id.rl_about:
			startActivity(new Intent(this, PersonalAboutActivity.class));
			break;
		// 申请成为上市公司
		case R.id.rl_apply:
			startActivity(new Intent(SetUpActivity.this,
					ApplyForStatementActivity.class));
			break;
		// 检查更新
		case R.id.rl_check_for_update:
			UpdateConfig.setDebug(true);
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				@Override
				public void onUpdateReturned(int updateStatus,
						UpdateResponse updateInfo) {
					// TODO Auto-generated method stub
					switch (updateStatus) {
					case UpdateStatus.Yes: // has update
						UmengUpdateAgent.showUpdateDialog(
								getApplicationContext(), updateInfo);
						break;
					case UpdateStatus.No: // has no update
						final DialogManager2 dialogManager = DialogManager2
								.getInstance(SetUpActivity.this);
						dialogManager.ShowBlueDialog();
						dialogManager.BluenoCancel();
//						dialogManager.BluenoMessageIcon();
						dialogManager.setBlueCancel(true);
						dialogManager.setBlueMessage("已经是最新版本");
						dialogManager
								.setBluePositiveButton(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										dialogManager.dismiss();
									}
								});
						break;
					case UpdateStatus.NoneWifi: // none wifi
						Toast.makeText(getApplicationContext(),
								"没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
								.show();
						break;
					case UpdateStatus.Timeout: // time out
						Toast.makeText(getApplicationContext(), "超时",
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
			});
			UmengUpdateAgent.update(this);
			break;
		// 修改密码
		case R.id.rl_change_password:
			startActivity(new Intent(SetUpActivity.this,
					FindPasswordActivity.class).putExtra("flag", 1));
			break;
		// 修改绑定
		case R.id.rl_change_binding:
			startActivity(new Intent(SetUpActivity.this,
					ModifyBindActivity.class));
			break;

		// 用户反馈
		case R.id.rl_call_back:
			startActivity(new Intent(SetUpActivity.this, FeedbackActivity.class));
			break;
		// 退出登录
		case R.id.tv_logout:

			final DialogManager2 dialogManager2 = new DialogManager2(this);
			dialogManager2.ShowBlueDialog();
//			dialogManager.BluenoMessageIcon();
			dialogManager2.setBlueMessage("确定退出登录吗？");
			dialogManager2.setBluePositiveButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("access_token", RsSharedUtil.getString(
							SetUpActivity.this, AppConfig.ACCESS_TOKEN));
					// progressDialog.show();
					// progressDialog.setMessage("请稍等...");
					loadingDialog.setLoadingString("请稍等...");
					loadingDialog.showLoadingDialog();
					logout();
					dialogManager2.dismiss();
				}
			});
			// 取消按钮的点击事件
			dialogManager2.setBlueNegativeButton(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogManager2.dismiss();
				}
			});
		default:
			break;
		}
	}

	/**
	 * 注销
	 */
	private void logout() {
		String url = AppConfig.URL_ACCOUNT + "logout.json?access_token="
				+ RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN);
		Log.d("logouturl", url);
		StringRequest stringRequest = new StringRequest(url, new JSONObject(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						// access_token置空
						// progressDialog.dismiss();
						loadingDialog.dismissDialog();

						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.ACCESS_TOKEN, "");
						Log.d("aaaaaaaaaaaa", RsSharedUtil
								.getString(getApplicationContext(),
										AppConfig.ACCESS_TOKEN));
						// 异步方法退出环信
						EMChatManager.getInstance().logout(new EMCallBack() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								// Toast.makeText(getApplicationContext(),
								// "环信退出登录成功", 0).show();
								Log.d("main", "退出成功");
							}

							@Override
							public void onProgress(int arg0, String arg1) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub
								Log.d("main", "退出失败");
							}
						});
						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.IMUSER_NAME, "");
						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

						try {
							// progressDialog.dismiss();
							loadingDialog.dismissDialog();
							JSONObject jsonObject = new JSONObject(error.data());
							// Toast.makeText(getApplicationContext(),
							// jsonObject.getString("description"),
							// Toast.LENGTH_SHORT);
							InternetDialog internetDialog = new InternetDialog(
									SetUpActivity.this);
							internetDialog.showInternetDialog(jsonObject.getString("description"), false);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
		stringRequest.setTag("logout");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("logout");
		super.onDestroy();
	}
}
