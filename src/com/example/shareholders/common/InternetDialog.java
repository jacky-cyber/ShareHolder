package com.example.shareholders.common;

import com.example.shareholders.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author 杜劲敏
 * 功能:简单操作提示框
 */
public class InternetDialog {

	private Context context;
	private int time = 2000;

	public void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return this.time;
	}

	public InternetDialog(Context context) {
		this.context = context;
	}

	private AlertDialog internertDialog = null;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;

			default:
				break;
			}
		};
	};


	
	public void showInternetDialog(String msg, boolean flag) {
		internertDialog = new AlertDialog.Builder(context).create();
		internertDialog.show();
		internertDialog.setCancelable(true);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_dianzan);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);

		progress_bar.setVisibility(View.GONE);
		// 设置提示状态：警告或者成功
		if (flag) { // true则为打勾
			iv_tips.setImageResource(R.drawable.ico_gou0);
		}
		iv_tips.setVisibility(View.VISIBLE);

		tv_message.setText(msg);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, time);
			}
		}).start();
	}
}
