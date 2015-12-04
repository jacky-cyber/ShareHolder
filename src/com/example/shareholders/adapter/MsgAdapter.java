package com.example.shareholders.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.db.entity.ChatMsgEntity;
import com.example.shareholders.db.entity.ChatMsgEntity.MsgType;
import com.example.shareholders.util.AbViewHolder;
import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MsgAdapter extends BaseAdapter {
	
	private BitmapUtils bitmapUtils;

	private MediaPlayer player;
	private ArrayList<ChatMsgEntity> chatMsgEntities;
	private Context context;

	private boolean isPlaying = false;

	public MsgAdapter(Context context, ArrayList<ChatMsgEntity> chatMsgEntities) {
		this.context = context;
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		this.chatMsgEntities = chatMsgEntities;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chatMsgEntities.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return chatMsgEntities.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (chatMsgEntities.get(position).getIsMine()) {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_chat_right, parent, false);
		} else {
			view = LayoutInflater.from(context).inflate(
					R.layout.item_chat_left, parent, false);
		}
		LinearLayout ll_time = (LinearLayout) AbViewHolder.get(view,
				R.id.ll_time);
		// 名字
		TextView tv_name = (TextView) AbViewHolder.get(view, R.id.tv_name);
		tv_name.setText(chatMsgEntities.get(position).getName());
		// 时间
		TextView tv_time = (TextView) AbViewHolder.get(view, R.id.tv_time);
		if (position == 0) {
			tv_time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.format(chatMsgEntities.get(position).getTime()));
		} else if ((chatMsgEntities.get(position).getTime()
				- chatMsgEntities.get(position - 1).getTime() > 180000)) {
			tv_time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.format(chatMsgEntities.get(position).getTime()));
		} else {
			ll_time.setVisibility(View.GONE);
		}
		// 是否为发起人
		TextView tv_creator = (TextView) AbViewHolder
				.get(view, R.id.tv_creator);
		// tv_creator.setVisibility(View.VISIBLE);

		// 文字内容
		TextView tv_content = (TextView) AbViewHolder
				.get(view, R.id.tv_content);
		// 录音
		ImageView iv_record_chatbox = (ImageView) AbViewHolder.get(view,
				R.id.iv_record_chatbox);
		// 图片
		ImageView iv_img = (ImageView) AbViewHolder.get(view, R.id.iv_img);

		// 头像
		CircleImageView iv_face = AbViewHolder.get(view, R.id.iv_face);
		bitmapUtils.display(iv_face,
				chatMsgEntities.get(position).getUserLogo());
		/*ImageLoader.getInstance().displayImage(
				chatMsgEntities.get(position).getUserLogo(), iv_face);*/

		int type = chatMsgEntities.get(position).getType();
		if (type == MsgType.RECORD) {
			tv_content.setVisibility(View.GONE);
			iv_record_chatbox.setVisibility(View.VISIBLE);
			iv_record_chatbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					isPlaying = !isPlaying;
					if (isPlaying) {
						player = new MediaPlayer();
						try {
							player.setDataSource(chatMsgEntities.get(position)
									.getResUrl());
							player.prepare();
							player.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer arg0) {
									// TODO Auto-generated method stub
								}
							});
							player.start();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						player.stop();
					}
				}

			});
		} else if (type == MsgType.IMAGE) {
			tv_content.setVisibility(View.GONE);
			iv_img.setVisibility(View.VISIBLE);

			if (chatMsgEntities.get(position).getIsMine()
					&& chatMsgEntities.get(position).getLocalUrl() != null) {
				String uri = chatMsgEntities.get(position).getLocalUrl();
				iv_img.setImageBitmap(decodeSampledBitmapFromFile(uri, 120, 160));
			} else {
				try {
					String uri = chatMsgEntities.get(position).getResUrl();
					ImageLoader.getInstance().displayImage(uri, iv_img);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					iv_img.setImageResource(R.drawable.empty_photo);
				}
			}

		} else {

			tv_content.setText(chatMsgEntities.get(position).getContent());
		}

		if (chatMsgEntities.get(position).getIsMine()
				&& !chatMsgEntities.get(position).getIsSendSuccess()) {
			Toast.makeText(context, "" + position, Toast.LENGTH_LONG).show();
			final LinearLayout ll_resend = (LinearLayout) AbViewHolder.get(
					view, R.id.ll_resend);
			ll_resend.setVisibility(View.VISIBLE);
			ImageView iv_resend = (ImageView) AbViewHolder.get(view,
					R.id.iv_resend);
			iv_resend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {
						ll_resend.setVisibility(View.GONE);
						// resend(position, chatMsgEntities.get(position)
						// .getMessage());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ll_resend.setVisibility(View.VISIBLE);
						Toast.makeText(context, "网络异常，请稍候重试", Toast.LENGTH_LONG)
								.show();
					}
				}
			});
		}
		

		return view;
	}

	// 计算压缩比
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 压缩图片
	public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth,
			int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeFile(path, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	public void sendFail(int position) {
		chatMsgEntities.get(position).setIsSendSuccess(false);
		notifyDataSetChanged();
	}
}
