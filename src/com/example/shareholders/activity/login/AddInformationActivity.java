package com.example.shareholders.activity.login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.SelectPicActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.service.GetInformationAfterLoginService;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.util.FileUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_add_information)
public class AddInformationActivity extends Activity {
	boolean isFrist;
	// 头像
	@ViewInject(R.id.ci_face)
	private CircleImageView ci_face;
	// 昵称
	@ViewInject(R.id.et_nickname)
	private EditText et_nickname;
	// 更改头像
	public static final int TO_SELECT_PHOTO = 1;
	//	private ProgressDialog progressDialog;
	//正在加载提示框
	private LoadingDialog loadingDialog;
	// 对话框
	private AlertDialog mDialog = null;
	// 密码框
	@ViewInject(R.id.et_password)
	private EditText et_password;
	// 确认密码框
	@ViewInject(R.id.et_password_again)
	private EditText et_password_again;

	// 昵称是否存在
	private boolean nickNameExist = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		// 显示默认图像和名字
		ImageLoader.getInstance().displayImage(
				RsSharedUtil.getString(getApplicationContext(),
						AppConfig.FIGURE_URL), ci_face);
		et_nickname.setText(RsSharedUtil.getString(getApplicationContext(),
				AppConfig.NICKNAME));
		// 当昵称edittext失去焦点的时候
		et_nickname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (et_nickname.hasFocus() == false) {
					// 检查昵称是否已经被使用
					checkNickName(et_nickname.getText().toString().trim());
				}
			}
		});
		// 密码输入是否符合格式
		et_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (et_password.hasFocus() == false) {
					// 密码输入是否符合格式
					if (!DateComparator.passwordFormatRegister(et_password.getText()
							.toString().trim())) {
						showdialog("密码必须包含数字和英文，并且是6-16位");
					}
				}
			}
		});
		et_password_again.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (et_password_again.hasFocus() == false) {
					// 密码输入是否符合格式
					if (!et_password_again.equals(et_password.getText()
							.toString().trim())) {
						showdialog("两次密码输入必须一样！");
					}
				}
			}
		});
		mDialog = new AlertDialog.Builder(this).create();
		//		progressDialog = new ProgressDialog(this);
		loadingDialog = new LoadingDialog(AddInformationActivity.this, null, false, false, null);
		isFrist = false;
		try {
			Intent intent2 = getIntent();
			Bundle bundle2 = intent2.getExtras();
			isFrist = bundle2.getBoolean("isFrist");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("checkNickName");
		MyApplication.getRequestQueue().cancelAll("finishRegister");
		MyApplication.getRequestQueue().cancelAll("thirdregister");

		super.onDestroy();
	};

	/**
	 * 检查昵称是否已经存在
	 * 
	 * @param nickName
	 */
	private void checkNickName(String nickName) {
		String url = AppConfig.URL_ACCOUNT + "username/check.json?userName="
				+ nickName;
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				nickNameExist = false;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				showdialog("该昵称已经被使用");
			}
		});
		stringRequest.setTag("checkNickName");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 完成注册
	 * 
	 * @param userName
	 * @param password
	 */
	public void finishRegister(String userName, final String password) {
		String url = AppConfig.URL_ACCOUNT + "signup.json";
		Log.d("注册url", "finishRegister");
		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("password", password);
			jsonObject.put("userName", userName);
			jsonObject.put("accountName", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.PHONE_NUMBER));
			jsonObject.put("userLogo", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.FIGURE_URL));
			jsonObject.put("verificationCode", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.VERIFY_CODE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(url, jsonObject,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Toast.makeText(AddInformationActivity.this, "注册成功", 1500).show();
				Login(RsSharedUtil.getString(getApplicationContext(),
						AppConfig.PHONE_NUMBER), password);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				//						progressDialog.dismiss();
				loadingDialog.dismissDialog();
				try {

					JSONObject jsonObject = new JSONObject(error.data());
					// 验证码已经过期的话返回上一页
					if (error.statuCode() == 408) {
						Toast.makeText(getApplicationContext(),
								jsonObject.getString("description"),
								Toast.LENGTH_SHORT);
						finish();
						// 否则弹窗弹出错误
					} else
						showdialog(jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		stringRequest.setTag("finishRegister");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@OnClick({ R.id.tv_return, R.id.ci_face, R.id.tv_register })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_return:
			finish();
			break;
			// 换头像
		case R.id.ci_face:
			Intent intent = new Intent();
			intent.setClass(this, SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
			// 点击注册
		case R.id.tv_register:
			// 一些检查
			if (!BtnClickUtils.isFastDoubleClick()) {
				if (nickNameExist) {
					showdialog("改昵称已经被使用");
				} else if (!DateComparator.passwordFormat(et_password.getText()
						.toString().trim())) {
					showdialog("密码必须包含数字和英文，并且是8-16位");
				} else if (!(et_password_again.getText().toString().trim())
						.equals(et_password.getText().toString().trim())) {
					showdialog("两次密码输入必须一样！");
				} else {
					//					progressDialog.show();
					//					progressDialog.setMessage("请稍等...");
					loadingDialog.setLoadingString("请稍等...");
					loadingDialog.showLoadingDialog();
					// 判断是正常注册还是第三方注册
					if (!RsSharedUtil.getBoolean(getApplicationContext(),
							AppConfig.IS_FORM_THIRD, false))
						finishRegister(et_nickname.getText().toString().trim(),
								et_password.getText().toString().trim());
					else {
						registerFromThird(et_nickname.getText().toString()
								.trim(), et_password.getText().toString()
								.trim());
					}
				}
			}
		default:
			break;
		}
	}

	/**
	 * 修改头像啦
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			String picPath = data
					.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			// 获取图片
			if (picPath != null) {
				//这里设置头像
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(picPath, options);
				ci_face.setImageBitmap(bm);
				
				
				Log.d("file_size", FileUtils.getFileOrFilesSize(picPath, 3)+"");
				//压缩图片
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				if(FileUtils.getFileOrFilesSize(picPath, 3)>1)
				{
					options1.inSampleSize = 8;
				}
				else {
					options1.inSampleSize = 4;
				}
				
				Bitmap bm1 = BitmapFactory.decodeFile(picPath, options1);
				saveToLocal(bm1);
				PostPicture(picPath);
			} else {
				//				Toast.makeText(getApplicationContext(), "只能选择sd卡中的图片",
				//						Toast.LENGTH_LONG).show();
				InternetDialog internetDialog = new InternetDialog(AddInformationActivity.this);
				internetDialog.showInternetDialog("只能选择sd卡中的图片", false);
			}
		}
	}
	
	private String pathString="";
	//保存作压缩的图像
	public int saveToLocal(Bitmap bitmap){
		File appDir = new File(Environment.getExternalStorageDirectory(), "gdh");
		if (!appDir.exists()) {
			appDir.mkdir();
		}


		//把app下drawable图片转换成流
		String fileName = System.currentTimeMillis() + ".jpg";
		pathString=appDir.toString()+"/"+fileName;
		Log.d("pathString",pathString);
		File file = new File(appDir, fileName);
		try {
			InputStream is;
			//InputStream is = context.getResources().openRawResource(list[position]);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			is = new ByteArrayInputStream(baos.toByteArray());

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count=is.read(buffer))>0) {
				fos.write(buffer,0,count);
			}
			fos.flush();
			fos.close();
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("jatjat", e.toString()+"0000000000000");
		}
		return 0;
	}
	//上传头像
	private void PostPicture(String picPath) {
		final LoadingDialog loadingDialog = new LoadingDialog(AddInformationActivity.this, "准备上传图片...", false, false, null);
		String requestURL = AppConfig.URL_FILE + "upload.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		requestURL = requestURL + "&uploadType=USER";
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(picPath));
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, requestURL, params,
				new RequestCallBack<String>() {

			@Override
			public void onStart() {
				//						progressDialog.setMessage("准备上传文件...");
				//						progressDialog.show();
				loadingDialog.setLoadingString("正在上传图片...");
				loadingDialog.showLoadingDialog();
			}

			@Override
			public void onLoading(long total, long current,
					boolean isUploading) {
				if (isUploading) {
					//							progressDialog.setMessage("正在上传文件...");
					loadingDialog.setLoadingString("正在上传图片...");
				} else {
					//							progressDialog.setMessage("准备上传文件...");
					loadingDialog.setLoadingString("准备上传图片...");
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				RsSharedUtil.putString(AddInformationActivity.this,
						AppConfig.FIGURE_URL, responseInfo.result);
				Toast.makeText(AddInformationActivity.this, "上传成功",
						Toast.LENGTH_SHORT).show();
				//						progressDialog.dismiss();
				LoadingDialog loadingDialog2 = new LoadingDialog(AddInformationActivity.this);
				loadingDialog2.setInternetString("上传成功!");
				loadingDialog2.setFlag(true);
				loadingDialog2.showInternetDialog();
				if (!pathString.equals("")) {
					//删除压缩临时文件，清除内存
					File file=new File(pathString);
					file.delete();
				}
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.d("error", error.toString());
				Log.d("error", msg.toString());
				//						progressDialog.dismiss();
				loadingDialog.dismissDialog();
				LoadingDialog loadingDialog2 = new LoadingDialog(AddInformationActivity.this);
				loadingDialog2.setInternetString("上传失败!");
				loadingDialog2.showInternetDialog();
				if (!pathString.equals("")) {
					//删除压缩临时文件，清除内存
					File file=new File(pathString);
					file.delete();
				}
			}
		});
	}

	/**
	 * show出各种提示对话框
	 */
	private void showdialog(String tips) {
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.getWindow().setContentView(R.layout.dialog_survey_list2);
		((TextView) mDialog.getWindow().findViewById(R.id.tv_dialog_content))
		.setText(tips);
		mDialog.getWindow().findViewById(R.id.tv_confirm)
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});
	}

	/**
	 * 登录
	 */
	private void Login(String userName, String password) {
		HttpUtils http = new HttpUtils();
		String url = "http://120.24.254.176:8080/shareholder-server/oauth/token?client_id=app-client&grant_type=password&scope=read&username="
				+ userName + "&password=" + password;
		Log.d("登录url", url);
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.d("dj_test", "fail");
				Intent intent = new Intent("LoginReceiver");
				sendBroadcast(intent);
				//						progressDialog.dismiss();
				loadingDialog.dismissDialog();
				startActivity(new Intent(AddInformationActivity.this,
						LoginActivity.class));
				finish();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {

				try {
					Log.d("dj_test", "success");
					JSONObject jsonObject = new JSONObject(arg0.result);
					RsSharedUtil.putString(AddInformationActivity.this,
							AppConfig.ACCESS_TOKEN,
							jsonObject.getString("access_token"));
					Intent getInformationIntent = new Intent(
							AddInformationActivity.this,
							GetInformationAfterLoginService.class);
					startService(getInformationIntent);
					// 跳到调研页,即index = 1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d("dj_test", "exception");
					e.printStackTrace();
				}
				/*Intent intent = new Intent("LoginReceiver");
						Bundle bundle = new Bundle();
						bundle.putInt("index", 1);
						intent.putExtras(bundle);
						sendBroadcast(intent);*/

				if (isFrist) {
					// 跳到调研页,即index = 1;
					Intent intent = new Intent("LoginReceiver");
					Bundle bundle = new Bundle();
					bundle.putInt("index", 1);
					intent.putExtras(bundle);
					sendBroadcast(intent);
					Log.d("dj_test", "isFirst");
				}else {

					Intent intent = new Intent("LoginReceiver");
					Bundle bundle = new Bundle();
					bundle.putInt("index", -1);
					intent.putExtras(bundle);
					sendBroadcast(intent);
					Log.d("dj_test", "notFirst");
				}

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
				//						progressDialog.dismiss();
				loadingDialog.dismissDialog();
				finish();
			}
		});
	}

	/**
	 * 通过第三方注册
	 * 
	 * @param userName
	 * @param password
	 */
	public void registerFromThird(String userName, String password) {
		String url = AppConfig.URL_THIRD + "signup.json";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("openid", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.OPENID));
			jsonObject.put("accessToken", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.THIRD_ACCESS_TOKEN));
			// jsonObject.put("openid", RsSharedUtil.getString(
			// getApplicationContext(), AppConfig.OPENID));
			jsonObject.put("userName", userName);
			jsonObject.put("userLogo", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.FIGURE_URL));
			jsonObject.put("password", password);
			jsonObject.put("type", RsSharedUtil.getString(
					getApplicationContext(), AppConfig.THIRD_TYPE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("jsonObject", jsonObject.toString());
		StringRequest stringRequest = new StringRequest(url, jsonObject,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					RsSharedUtil.putString(AddInformationActivity.this,
							AppConfig.ACCESS_TOKEN,
							jsonObject.getString("access_token"));
					Intent getInformationIntent = new Intent(
							AddInformationActivity.this,
							GetInformationAfterLoginService.class);
					startService(getInformationIntent);
					// 跳到调研页,即index = 1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Auto-generated method stub
				Intent intent = new Intent("LoginReceiver");
				Bundle bundle = new Bundle();
				bundle.putInt("index", 1);
				intent.putExtras(bundle);
				sendBroadcast(intent);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0,
						InputMethodManager.HIDE_NOT_ALWAYS);
				//						progressDialog.dismiss();
				loadingDialog.dismissDialog();
				finish();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				//						progressDialog.dismiss();
				loadingDialog.dismissDialog();
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					showdialog(jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});
		stringRequest.setTag("thirdregister");
		MyApplication.getRequestQueue().add(stringRequest);
	}
}
