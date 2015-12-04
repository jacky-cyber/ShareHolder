package com.example.shareholders.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	private ToastUtils() {

	}

	public static void showToast(Context context, String toast) {
		if (null == mToast) {
			mToast = Toast.makeText(context, toast, Toast.LENGTH_LONG);
		} else {
			mToast.setText(toast);
		}

		mToast.show();
	}

	public static void cancel() {
		if (null != mToast) {
			mToast.cancel();
		}
	}

	public static Toast mToast = null;
}