package com.example.shareholders.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shareholders.R;
import com.example.shareholders.activity.personal.StockFriendsLocalContacts;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class Fragment_ShareFriends_Search extends Fragment {
	
	public static Tencent mTencent;
	
	private String title = "股东会";
	// 一定要加http前缀
	private String targetUrl = "http://duobeibao.com/index.php?r=download";
	private String Summary = "股东会11月正式火热上线！！";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_sharefriends_search,
				container, false);

		ViewUtils.inject(this, v);
		return v;
	}

	@OnClick({ R.id.rl_phone_friends,R.id.rl_qq_friends,R.id.rl_weibo_friends,R.id.rl_weixin_friends})
	private void Onclick(View view) {
		switch (view.getId()) {
		case R.id.rl_phone_friends:
			Intent intent = new Intent(getActivity(),
					StockFriendsLocalContacts.class);
			startActivity(intent);
			break;
		case R.id.rl_qq_friends:
			mTencent = Tencent.createInstance(AppConfig.TENCENT_APP_ID,getActivity());
			QQShare();
			break;
		case R.id.rl_weixin_friends:
			wechatShare(0);
			break;
		case R.id.rl_weibo_friends:
			sharetoWeiBo();
			break;
		default:
			break;
		}
	}
	
	//分享到微博
	private void sharetoWeiBo() {
		IWeiboShareAPI mWeiboShareAPI;
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(getActivity(),
				AppConfig.WEIBO_APP_KEY);
		mWeiboShareAPI.registerApp();
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		TextObject textObject = new TextObject();
		textObject.text = Summary;
		weiboMessage.textObject = textObject;
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		AuthInfo authInfo = new AuthInfo(getActivity(), AppConfig.WEIBO_APP_KEY,
				AppConfig.REDIRECT_URL, AppConfig.SCOPE);
		mWeiboShareAPI.sendRequest(getActivity(), request, authInfo, "",
				new WeiboAuthListener() {

					@Override
					public void onWeiboException(WeiboException arg0) {
					}

					@Override
					public void onComplete(Bundle bundle) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onCancel() {
					}
				});
	}

	
	//分享给微信好友
	private void wechatShare(int flag) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = targetUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = Summary;
		// 这里替换一张自己工程里的图片资源
		Bitmap thumb = BitmapFactory.decodeResource(getActivity().getResources(),
				R.drawable.invest_circle);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		MyApplication.getIWXAPI().sendReq(req);
	}
	
	private void QQShare() {
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, Summary);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
				"http://shareholder-server.oss-cn-shenzhen.aliyuncs.com/system/share/gudonghuilogo.png");
		mTencent.shareToQQ((Activity) getActivity(), params, new BaseUiListener());
	}
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.d("onCancel", "onCancel");
		}

		@Override
		public void onError(UiError arg0) {
			// TODO Auto-generated method stub
			Log.d("error", arg0.toString());
		}
	}
}
