package com.example.shareholders.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateInformationReceiver extends BroadcastReceiver {
	refreshInformation refreshInformation;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		refreshInformation.refresh();
	}

	public void setRefreshInformaiton(refreshInformation refreshInformation) {
		this.refreshInformation = refreshInformation;
	}

	public interface refreshInformation {
		public void refresh();
	}
}
