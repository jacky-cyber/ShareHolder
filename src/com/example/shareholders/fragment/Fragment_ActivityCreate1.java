package com.example.shareholders.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.activity.survey.ActivityCreateActivity;
import com.example.shareholders.activity.survey.SelectPicActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.ActionSheetDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_ActivityCreate1 extends Fragment {
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	@ViewInject(R.id.iv_add_photo)
	private ImageView iv_add_photo;

	@ViewInject(R.id.et_set_name)
	private EditText et_set_name;

	@ViewInject(R.id.et_introduction)
	private EditText et_introduction;

	@ViewInject(R.id.tv_survey_acitivity)
	private TextView tv_survey_acitivity;
	public static boolean waitForStart = true;
	private ActionSheetDialog dialog;

	private AlertDialog myDialog = null;
	private String picPath = null;
//	private ProgressDialog progressDialog;
	
	private BitmapUtils bitmapUtils;
	@ViewInject(R.id.background)
	RelativeLayout background;
	@ViewInject(R.id.iv_amaze)
	ImageView iv_amaze;
	@ViewInject(R.id.tv_tips)
	TextView tv_tips;
	@ViewInject(R.id.ll_add_photo)
	RelativeLayout ll_add_photo;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_activitycreate1, container,
				false);

		ViewUtils.inject(this, v);
//		progressDialog = new ProgressDialog(getActivity());
		
		
		tv_survey_acitivity.setVisibility(View.GONE);
		// 如果是编辑活动
		if (ActivityCreateActivity.sign != 0) {
			// 如果是编辑活动，标题最多为12个字
			iv_amaze.setVisibility(View.VISIBLE);
			tv_tips.setText("活动名称不能更改");
			et_set_name
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							12) });
			et_set_name.setEnabled(false);
			et_set_name.setClickable(false);
			tv_survey_acitivity.setVisibility(View.GONE);
			et_set_name.setText(RsSharedUtil.getString(getActivity(),
					"surveyName"));
			et_introduction.setText(RsSharedUtil.getString(getActivity(),
					"content"));
			bitmapUtils = BitmapUtilFactory.getInstance();
			bitmapUtils.display(iv_add_photo,
					RsSharedUtil.getString(getActivity(), "logo"));
		} else {
			et_set_name
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							8) });
		}
		return v;
	}

	@OnClick({ R.id.iv_add_photo, R.id.tv_next, R.id.rl_set_name })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add_photo:
             
			Intent intent = new Intent(getActivity(), SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
		case R.id.tv_next:
			if (et_set_name.getText().toString().equals("")
					|| et_introduction.getText().toString().equals("")) {

				// 提示带*号的为必填内容
				InternetDialog internetDialog = new InternetDialog(
						getActivity());
				internetDialog.showInternetDialog(getActivity().getResources()
						.getString(R.string.have_to_fill_tip), false);

			} else {

				/*
				 * 下一步
				 */
				RsSharedUtil.putString(getActivity(), "surveyName", et_set_name
						.getText().toString().trim());
				RsSharedUtil.putString(getActivity(), "content",
						et_introduction.getText().toString());
				((MyViewPager) (getActivity()
						.findViewById(R.id.vp_activity_create)))
						.setCurrentItem(1);
				
			}
			break;

		case R.id.rl_set_name:
			if (ActivityCreateActivity.sign != 0) {
			} else {
				tv_survey_acitivity.setVisibility(View.VISIBLE);
				et_set_name.requestFocus();
				// et_set_name.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				// et_set_name.setTextIsSelectable(true);
				et_set_name.setText(" ");
				// et_set_name.setSelection(1);

				InputMethodManager imm = (InputMethodManager) et_set_name
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 上传服务器响应回调
	 */

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			if(data!=null){
				picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			}
			Drawable drawable= new BitmapDrawable(getimage(picPath));
			iv_add_photo.setBackgroundDrawable(drawable);
			iv_add_photo.setScaleType(ScaleType.CENTER);
			// 上传图片
			if (picPath != null) {
				CompressPicture(picPath);
				PostPicture(picPath);
			} else {
				InternetDialog internetDialog = new InternetDialog(
						getActivity());
				internetDialog.showInternetDialog("只能选择sd卡中的图片", false);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    private void CompressPicture(String picPath){
    	 Bitmap bitmap=getimage(picPath);
    	 File file=new File(picPath);
    	 OutputStreamWriter osw;   
    	try{ 
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//png类型
    			
    	try {   
    			FileOutputStream out=new FileOutputStream(file);
    			out.write( baos.toByteArray());
    			out.flush();
    			out.close();
    		}catch (IOException e) {   
                 // TODO Auto-generated catch block   
                 e.printStackTrace();   
             }   
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	private void PostPicture(String picPath) {
		
		String requestURL = AppConfig.URL_FILE + "upload.json?access_token=";
		requestURL = requestURL
				+ RsSharedUtil.getString(getActivity(), "access_token");
		requestURL = requestURL + "&uploadType=SURVEY";
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(picPath));
		final LoadingDialog loadingDialog = new LoadingDialog(getActivity(), "准备上传图片...", false, false, null);
		//设置10秒的上传提示框
		loadingDialog.setTime(10000);
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, requestURL, params,
				new RequestCallBack<String>() {
			
					@Override
					public void onStart() {
						Log.d("dj_start", "onStart");
						loadingDialog.setLoadingString("正在上传图片...");
						loadingDialog.showLoadingDialog();
					}

					
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (isUploading) {
							Log.d("dj_test_uploading", "isUploading");
							loadingDialog.setLoadingString("正在上传图片...");
						} else {
							Log.d("dj_test_upLoading", "notUpLoading");
							loadingDialog.setLoadingString("准备上传图片...");
						}
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						RsSharedUtil.putString(getActivity(), "logo",
								responseInfo.result);
						loadingDialog.dismissDialog();
						
						LoadingDialog loadingDialog2 = new LoadingDialog(getActivity());
						loadingDialog2.setInternetString("上传成功!");
						loadingDialog2.setFlag(true);
						loadingDialog2.showInternetDialog();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.d("dj_test_success", "fail");
//						progressDialog.dismiss();
						loadingDialog.dismissDialog();
						LoadingDialog loadingDialog2 = new LoadingDialog(getActivity());
						loadingDialog2.setInternetString("上传失败!");
						loadingDialog2.showInternetDialog();
					}
				});
	}
	private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 800f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }
}
