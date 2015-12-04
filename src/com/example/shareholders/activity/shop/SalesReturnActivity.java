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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.activity.survey.SelectPicActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_sales_return)
public class SalesReturnActivity extends Activity {

	// 退货原因
	@ViewInject(R.id.et_reason)
	private EditText et_reason;

	// 上传凭证
	@ViewInject(R.id.iv_add_photo)
	private ImageView iv_add_photo;
	public static final int TO_SELECT_PHOTO = 1;

	// 货物状态
	@ViewInject(R.id.tv_received_sales)
	private TextView tv_received_sales;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.iv_return, R.id.iv_add_photo, R.id.tv_commit,
			R.id.tv_received_sales ,R.id.rl_return})
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

			if (et_reason.getText().toString().equals("")) {
				showAlertDialog(false);
			} else {
				showAlertDialog(true);
			}

			break;
		// 是否拿到货
		case R.id.tv_received_sales:
			showReceivedDialog();
			break;
			
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	private void showReceivedDialog() {
		final AlertDialog myDialog = new AlertDialog.Builder(
				SalesReturnActivity.this).create();
		myDialog.show();
		myDialog.setCancelable(false);
		myDialog.getWindow().setContentView(R.layout.popup_select_sent);

		TextView tv_message = (TextView) myDialog.getWindow().findViewById(
				R.id.tv_message);
		RelativeLayout rl_sent_normal = (RelativeLayout) myDialog.getWindow()
				.findViewById(R.id.rl_sent_normal);
		RelativeLayout rl_sent_EMS = (RelativeLayout) myDialog.getWindow()
				.findViewById(R.id.rl_sent_EMS);
		TextView tv_sent_normal = (TextView) myDialog.getWindow().findViewById(
				R.id.tv_sent_normal);
		TextView tv_sent_EMS = (TextView) myDialog.getWindow().findViewById(
				R.id.tv_sent_EMS);
		TextView tv_ok = (TextView) myDialog.getWindow().findViewById(
				R.id.tv_ok);

		final ImageView iv_gou_sent_normal = (ImageView) myDialog.getWindow()
				.findViewById(R.id.iv_gou_sent_normal);
		final ImageView iv_gou_sent_EMS = (ImageView) myDialog.getWindow()
				.findViewById(R.id.iv_gou_sent_EMS);

		// 请选择货物的状态的提示
		tv_message.setText(getResources()
				.getString(R.string.select_sales_state));
		// 已拿到货
		tv_sent_normal.setText(getResources().getString(
				R.string.has_gotton_goods));
		// 未拿到货
		tv_sent_EMS.setText(getResources().getString(R.string.not_got_goods));

		// 选择已拿到货
		rl_sent_normal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				iv_gou_sent_normal.setVisibility(View.VISIBLE);
				iv_gou_sent_EMS.setVisibility(View.GONE);
			}
		});

		// 选择未拿到货
		rl_sent_EMS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				iv_gou_sent_normal.setVisibility(View.GONE);
				iv_gou_sent_EMS.setVisibility(View.VISIBLE);
			}
		});

		// 确定
		tv_ok.setText(getResources().getString(R.string.confirm));
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (iv_gou_sent_normal.getVisibility() == View.VISIBLE) {
					tv_received_sales.setText(getResources().getString(
							R.string.has_gotton_goods));
				} else {
					tv_received_sales.setText(getResources().getString(
							R.string.not_got_goods));
				}
				myDialog.dismiss();
			}
		});
	}

	private void showAlertDialog(final boolean success) {
		final AlertDialog myDialog = new AlertDialog.Builder(
				SalesReturnActivity.this).create();
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
