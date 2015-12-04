package com.example.shareholders.activity.shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.activity.survey.SelectPicActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_refund)
public class RefundActivity extends Activity {

	// 退款原因
	@ViewInject(R.id.et_reason)
	private EditText et_reason;

	// 退款金额
	@ViewInject(R.id.et_amount)
	private EditText et_amount;

	// 上传凭证
	@ViewInject(R.id.iv_add_photo)
	private ImageView iv_add_photo;
	public static final int TO_SELECT_PHOTO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.iv_return, R.id.iv_add_photo, R.id.tv_commit ,R.id.rl_return})
	public void onClick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.iv_return:
			finish();
			break;
		// 付款凭证
		case R.id.iv_add_photo:
			// showPopwindow();
			Intent intent = new Intent();
			intent.setClass(this, SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
		// 提交
		case R.id.tv_commit:

			if (et_reason.getText().toString().equals("")
					|| et_amount.getText().toString().equals("")) {
				showAlertDialog(false);
			} else {
				showAlertDialog(true);
			}

			break;
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	private void showAlertDialog(final boolean success) {
		final AlertDialog myDialog = new AlertDialog.Builder(
				RefundActivity.this).create();
		myDialog.show();
		myDialog.setCancelable(false);
		myDialog.getWindow().setContentView(
				R.layout.dialog_create_activity_layout);

		ImageView iv = (ImageView) myDialog.getWindow().findViewById(
				R.id.iv_gou);
		Button btn_complete = (Button) myDialog.getWindow().findViewById(
				R.id.btn_inform_confirm);
		TextView tv_message = (TextView) myDialog.getWindow().findViewById(
				R.id.tv_inform_dialog_content);

		if (success) {
			tv_message.setText(getResources()
					.getString(R.string.commit_success));
		} else {
			iv.setImageResource(R.drawable.ico_gantanhao);
			tv_message.setText(getResources().getString(
					R.string.have_to_fill_tip));
		}

		btn_complete.setText(getResources().getString(R.string.confirm));
		btn_complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (success) {
					finish();
				} else {
					myDialog.dismiss();
				}
			}
		});
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
				Bitmap bm = BitmapFactory.decodeFile(picPath);
				iv_add_photo.setImageBitmap(bm);
				// PostPicture(picPath);
			} else {
				Toast.makeText(getApplicationContext(), "只能选择sd卡中的图片",
						Toast.LENGTH_LONG).show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
