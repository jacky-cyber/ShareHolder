package com.example.shareholders.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LoginReceiver extends BroadcastReceiver {
	private AfterLogin afterLogin;

	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		// 登录后应该跳到哪个index
		int index = 1;
		try {
			index = intent.getExtras().getInt("index");

		} catch (Exception e) {
			// TODO: handle exception
		}

		Log.d("todo(int)", "todo(int)");
		afterLogin.ToDo(index);

	}

	// 登录之后，前面的actvity都应该finish，然后MainACtivity的viewpager.setCurrentItem(index);
	public interface AfterLogin {
		public void ToDo(int index);
	}

	public void setAfterLogin(AfterLogin afterLogin) {
		this.afterLogin = afterLogin;
	}
}
