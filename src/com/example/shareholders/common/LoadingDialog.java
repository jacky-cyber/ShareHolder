package com.example.shareholders.common;

import com.example.shareholders.R;
import com.example.shareholders.util.NetWorkCheck;

import android.R.bool;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingDialog {

	private Context context;
	private int time = 5000;
	// 加载提示
	private String loadingString = "正在加载";
	// 网络提示
	private String internetString = "网络不给力";
	// 是否需要提示网络
	private boolean ifNet = true;
	// 警告或者成功
	private boolean flag = false;

	public void setTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return this.time;
	}

	public void setInternetString(String internetString) {
		this.internetString = internetString;
	}

	public String getInternetString() {
		return this.internetString;
	}

	public void setLoadingString(String loadingString) {
		this.loadingString = loadingString;
	}

	public String getLoadingString() {
		return this.loadingString;
	}

	public void setIfNet(boolean ifNet) {
		this.ifNet = ifNet;
	}

	public boolean getIfNet() {
		return this.ifNet;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean getFlag() {
		return this.flag;
	}

	// 默认为需要网络提示,分别为正在加载和网络不给力,为警告
	public LoadingDialog(Context context) {
		this.context = context;
	}

	// 是否需要网络提示？警告或者成功？正在加载和网络提示的用语？
	public LoadingDialog(Context context, String loadingString, boolean ifNet,
			boolean flag, String internetString) {
		this.context = context ;
		if (loadingString!=null) {
			this.loadingString = loadingString ;
		}
		this.ifNet = ifNet ;
		this.flag = flag ;
		if (internetString!=null){
			this.internetString = internetString ;
		}
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

			case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力

				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
					if (ifNet) { // true则需要网络提示
						try {
							showInternetDialog();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				break;

			case 3: // 提示网络异常的对话框消失
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;
			default:
				break;
			}
		};
	};

	// 加载完数据后,旋转对话框消失
	public void dismissDialog() {
		Message msg = new Message();
		msg.what = 1;
		mHandler.sendMessage(msg);
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	public void showLoadingDialog() {
		internertDialog = new AlertDialog.Builder(context).create();
		internertDialog.show();
		internertDialog.setCancelable(true);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);
		// 设置正在加载
		tv_message.setText(loadingString);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		if (!NetWorkCheck.isNetworkConnected(context)) {
			Log.d("network", "network");
			internertDialog.dismiss();
			showInternetDialog();
		}else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = 2;
					mHandler.sendMessageDelayed(msg, time);
				}
			}).start();
		}
		
	}
	

	/**
	 * 提示网络信息
	 */
	public void showInternetDialog() {
		internertDialog = new AlertDialog.Builder(context).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

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
		// 设置网络提示
		tv_message.setText(internetString);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessageDelayed(msg, 2000);
			}
		}).start();
	}

}
