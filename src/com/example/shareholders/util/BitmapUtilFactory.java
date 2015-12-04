package com.example.shareholders.util;

/**
 * ͼƬ���ش���
 * author warren
 */

import android.content.Context;

import com.example.shareholders.R;
import com.lidroid.xutils.BitmapUtils;

public class BitmapUtilFactory {
	public static BitmapUtils butils;

	// init the factory
	public static void init(Context context) {
		butils = new BitmapUtils(context);
		butils.configDefaultLoadingImage(R.drawable.img_load);
		butils.configDefaultLoadFailedImage(R.drawable.img_load);
	}

	public static BitmapUtils getInstance() {
		return butils;
	}
}
