package com.example.shareholders.activity.newthird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activitytouhangquanweb)
public class TouHangQuanWebActivity extends Activity {
	@ViewInject(R.id.wv_touhangquan)
	private WebView wv_touhangquan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initview();
	}

	private void initview() {
		Intent intent = getIntent();
		int i = intent.getIntExtra("tzweb", 0);
		String url="http://www.ibstart.com/neeqs";
		switch (i) {
		case 0:
			url="http://www.ibstart.com/neeqs";
			break;
		case 1:
			url="http://www.ibstart.com/investor";
			break;
		case 2:
			url="http://www.ibstart.com/information";
			break;
		default:
			break;
		}
		wv_touhangquan.loadUrl(url);
		wv_touhangquan.getSettings().setJavaScriptEnabled(true);
		wv_touhangquan.getSettings().setAppCacheEnabled(true);
		wv_touhangquan.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		/*wv_touhangquan.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return false;
			}
		});*/
	}

}
