package com.example.shareholders.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateCommentReceiver extends BroadcastReceiver {
	private UpLoadDownListener upLoadDownListener;

	public void setUpLoadDownListener(UpLoadDownListener upLoadDownListener) {
		this.upLoadDownListener = upLoadDownListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		/**
		 * 首先界面(commentFragment,ReviewDetailsActivity)
		 * 完成Update这个接口，然后谁先刷新了，谁就终止这个broadcast
		 */
		upLoadDownListener.ToUpdate();
		abortBroadcast();
	}

	// 通知与comment有关的activity去更新
	public interface UpLoadDownListener {
		void ToUpdate();
	}
}
