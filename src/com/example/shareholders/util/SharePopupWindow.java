package com.example.shareholders.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.StartActivity;
import com.example.shareholders.activity.personal.StockFriendsActivityCopy;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 分享
 * 
 * @author warren
 * 
 */
public class SharePopupWindow extends PopupWindow {
	private Context mContext;
	public static Tencent mTencent;

	private String title = "股东汇";
	// 一定要加http前缀
	private String targetUrl = "http://www.ibstart.com";
	private String Summary = "股东汇11月正式火热上线！！";

	public SharePopupWindow(final Context mContext, View parent) {
		this.mContext = mContext;
		View view = View.inflate(mContext, R.layout.item_share_popupwindow,
				null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_ins));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in_2));

		setContentView(view);

		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true); // 设置PopupWindow可获得焦点
		setOutsideTouchable(true);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);

		showAtLocation(parent, Gravity.CENTER, 0, 0);

		update();

		RelativeLayout rl_popup = (RelativeLayout) view
				.findViewById(R.id.rl_popup);
		rl_popup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		// 取消
		TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		//分享到股东汇好友
		LinearLayout ll_share_to_friendLayout= (LinearLayout) view
				.findViewById(R.id.ll_share_to_gdhfriend);
		ll_share_to_friendLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				shareToMyFriend();
				dismiss();
			}
		});
		
		// 分享到qq
		LinearLayout ll_share_to_QQ = (LinearLayout) view
				.findViewById(R.id.ll_share_to_QQ);
		ll_share_to_QQ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTencent = Tencent.createInstance(AppConfig.TENCENT_APP_ID,mContext);
				QQShare();
				dismiss();
			}
		});
		// 分享到qq空间
		LinearLayout ll_share_to_QZone = (LinearLayout) view
				.findViewById(R.id.ll_share_to_QZone);
		ll_share_to_QZone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTencent = Tencent.createInstance(AppConfig.TENCENT_APP_ID,
						mContext);
				shareToQzone();
				dismiss();
			}
		});
		// 分享到微博
		LinearLayout ll_share_to_weibo = (LinearLayout) view
				.findViewById(R.id.ll_share_to_weibo);
		ll_share_to_weibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sharetoWeiBo();
				dismiss();
			}
		});
		// 分享到微信
		LinearLayout ll_share_to_Wechat = (LinearLayout) view
				.findViewById(R.id.ll_share_to_Wechat);
		ll_share_to_Wechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wechatShare(0);
				dismiss();
			}
		});

		// 分享到微信朋友圈
		LinearLayout ll_share_to_wechat_friend = (LinearLayout) view
				.findViewById(R.id.ll_share_to_wechat_friend);
		ll_share_to_wechat_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wechatShare(1);
				dismiss();
			}
		});
	}
	
	//分享给好友
	private void shareToMyFriend()
	{
		Intent intent=new Intent(mContext,StockFriendsActivityCopy.class);
		intent.putExtra("share","share");
		intent.putExtra("shareContent", "要转发的内容。。。。");
		mContext.startActivity(intent);
		
	}

	private void QQShare() {
		Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, Summary);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
				"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
		mTencent.shareToQQ((Activity) mContext, params, new BaseUiListener());
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

	private void shareToQzone() {
		Bundle params = new Bundle();
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 必填
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, Summary);// 选填
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);// 必填
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
				new ArrayList<String>());// 必填
		mTencent.shareToQzone((Activity) mContext, params, new BaseUiListener());
	}

	private void sharetoWeiBo() {
		IWeiboShareAPI mWeiboShareAPI;
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mContext,
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
		AuthInfo authInfo = new AuthInfo(mContext, AppConfig.WEIBO_APP_KEY,
				AppConfig.REDIRECT_URL, AppConfig.SCOPE);
		mWeiboShareAPI.sendRequest((Activity) mContext, request, authInfo, "",
				new WeiboAuthListener() {

					@Override
					public void onWeiboException(WeiboException arg0) {
						Log.d("weiboshare", "weiboException");
					}

					@Override
					public void onComplete(Bundle bundle) {
						// TODO Auto-generated method stub
						Log.d("weiboshare", "weibofinish");
					}

					@Override
					public void onCancel() {
						Log.d("weiboshare", "weiboCancel");
					}
				});
	}

	private void wechatShare(int flag) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = targetUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = Summary;
		// 这里替换一张自己工程里的图片资源
		Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.demo);
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		MyApplication.getIWXAPI().sendReq(req);
	}
}
