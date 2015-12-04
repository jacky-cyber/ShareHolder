package com.example.shareholders.common;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.easemob.chat.EMChat;
import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MyApplication extends Application {

	// 全局的volleyRequest
	static RequestQueue requestQueue;
	// 微信
	static IWXAPI miwxapi;

	public MyApplication() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		BitmapUtilFactory.init(this); // init
		EMChat.getInstance().init(this);// 初始化环信
		/**
		 * debugMode == true 时为打开，sdk 会在log里输入调试信息
		 * 
		 * @param debugMode
		 *            在做代码混淆的时候需要设置成false
		 */
		EMChat.getInstance().setDebugMode(true);// 在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.img_load)
				.showImageOnFail(R.drawable.img_load)
				.cacheInMemory(true)
				.cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCacheExtraOptions(480, 800)
				.imageDownloader(new BaseImageDownloader(this,5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间   
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)// 缓存一百张图片
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		// 初始化全局的volleyrequest
		requestQueue = Volley.newRequestQueue(getApplicationContext());
		// 将你的应用id注册到微信
		regToWX();
	}

	public static RequestQueue getRequestQueue() {
		return requestQueue;
	}

	public static IWXAPI getIWXAPI() {
		return miwxapi;
	}

	// 将你的应用id注册到微信
	private void regToWX() {
		miwxapi = WXAPIFactory.createWXAPI(this, AppConfig.WEIXIN_APP_ID, true);
		miwxapi.registerApp(AppConfig.WEIXIN_APP_ID);
	}
}
