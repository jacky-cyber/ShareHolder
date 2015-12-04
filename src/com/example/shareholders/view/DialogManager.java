package com.example.shareholders.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shareholders.R;

public class DialogManager {
	// 上下文
	private Context context;
	private AlertDialog alertDialog;
	private LinearLayout ll;
	// 蓝色对话
	private ImageView iv_message_icon; // 对话框显示的提示图片
	private TextView tv_message; // 对话框的提示语
	private TextView tv_confirm; // 确定
	private TextView tv_cancel; // 取消
	// 录音
	private RecordDialog recordDialog;
	private ImageView mVoice;
	// private TextView mLable;

	private static DialogManager mInstance;

	public static DialogManager getInstance(Context context) {
		mInstance = new DialogManager(context);
		return mInstance;
	}

	public DialogManager(Context context) {
		this.context = context;
	}

	public void ShowBlackDialog(String text, int drawable) {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.item_toast_popup);
		ImageView iv_item = (ImageView) window.findViewById(R.id.iv_item);
		iv_item.setImageResource(drawable);
		TextView tv_item = (TextView) window.findViewById(R.id.tv_item);
		tv_item.setText(text);
		alertDialog.setCancelable(true);
	}

	public void ShowBlueDialog() {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_general_layout);
		ll = (LinearLayout) window.findViewById(R.id.ll);
		iv_message_icon = (ImageView) window.findViewById(R.id.iv_message_icon);
		tv_message = (TextView) window.findViewById(R.id.tv_message);
		tv_confirm = (TextView) window.findViewById(R.id.tv_confirm);
		tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
		alertDialog.setCancelable(true);
	}

	/**
	 * 设置提示图片
	 * 
	 * @param resId
	 */
	public void setBlueMessageIcon(int resId) {
		iv_message_icon.setImageResource(resId);
	}

	/**
	 * 设置对话框的提示语
	 * 
	 * @param resId
	 */
	public void setBlueMessage(int resId) {
		tv_message.setText(context.getResources().getString(resId));
	}

	public void setBlueMessage(String message) {
		tv_message.setText(message);
	}

	/**
	 * 设置确定按钮的文字
	 * 
	 * @param resId
	 */
	public void setBluePositiveText(int resId) {
		tv_confirm.setText(context.getResources().getString(resId));
	}

	public void setBluePositiveText(String message) {
		tv_confirm.setText(message);
	}

	/**
	 * 设置取消按钮的文字
	 * 
	 * @param resId
	 */
	public void setBlueNegativeText(int resId) {
		tv_cancel.setText(context.getResources().getString(resId));
	}

	public void setBlueNegativeText(String message) {
		tv_cancel.setText(message);
	}

	/**
	 * 设置确定按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setBluePositiveButton(View.OnClickListener listener) {
		tv_confirm.setOnClickListener(listener);
	}

	/**
	 * 无取消按钮，只有确定按钮
	 */
	public void BluenoCancel() {
		tv_cancel.setVisibility(View.GONE);
	}

	/**
	 * 无提示图片，只有文字提示
	 */
	public void BluenoMessageIcon() {
		iv_message_icon.setVisibility(View.GONE);
	}

	/**
	 * 设置取消按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setBlueNegativeButton(View.OnClickListener listener) {
		tv_cancel.setOnClickListener(listener);
	}

	/**
	 * 是否可以通过点击对话框之外的点来关闭对话框
	 * 
	 * @param canCancel
	 */
	public void setBlueCancel(boolean canCancel) {
		alertDialog.setCancelable(canCancel);
	}

	public void dismiss() {
		if (alertDialog != null)
			alertDialog.dismiss();
	}

	// 显示录音的对话框
	public void showRecordingDialog() {

		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// View view = LayoutInflater.from(context).inflate(
		// R.layout.dialog_recording, null);//
		//
		// mVoice = (ImageView) view.findViewById(R.id.iv_voice);
		//
		// builder.setView(view);
		// builder.create();
		// alertDialog = builder.show();

		recordDialog = new RecordDialog(context);
		recordDialog.show();

	}

	public void recording() {
		if (recordDialog != null && recordDialog.isShowing()) { // 显示状态
			// mVoice.setVisibility(View.VISIBLE);
			// mLable.setVisibility(View.VISIBLE);
			// mLable.setText("手指上滑，取消发送");
		}
	}

	// 显示想取消的对话框
	public void wantToCancel() {
		if (recordDialog != null && recordDialog.isShowing()) { // 显示状态
			// mVoice.setVisibility(View.GONE);
			// mLable.setVisibility(View.VISIBLE);
			// mLable.setText("松开手指，取消发送");
		}
	}

	// 显示时间过短的对话框
	public void tooShort() {
		if (recordDialog != null && recordDialog.isShowing()) { // 显示状态
			// mVoice.setVisibility(View.GONE);
			// mLable.setVisibility(View.VISIBLE);
			// mLable.setText("录音时间过短");
		}
	}

	// 显示取消的对话框
	public void dimissDialog() {
		if (recordDialog != null && recordDialog.isShowing()) { // 显示状态
			recordDialog.dismiss();
			recordDialog = null;
		}
	}

	// 显示更新音量级别的对话框
	public void updateVoiceLevel(int level) {
		if (recordDialog != null && recordDialog.isShowing()) { // 显示状态

			// 设置图片的id
			switch (level) {
			case 1:
				recordDialog.iv_voice.setImageResource(R.drawable.record1);
				break;
			case 2:
				recordDialog.iv_voice.setImageResource(R.drawable.record2);
				break;
			case 3:
				recordDialog.iv_voice.setImageResource(R.drawable.record3);
				break;
			case 4:
				recordDialog.iv_voice.setImageResource(R.drawable.record4);
				break;
			case 5:
				recordDialog.iv_voice.setImageResource(R.drawable.record5);
				break;
			case 6:
				recordDialog.iv_voice.setImageResource(R.drawable.record6);
				break;
			case 7:
				recordDialog.iv_voice.setImageResource(R.drawable.record7);
				break;

			default:
				recordDialog.iv_voice.setImageResource(R.drawable.record1);
				break;
			}
		}
	}

	/**
	 * 自定义dialog
	 * 
	 */
	public class RecordDialog extends Dialog {

		private ImageView iv_voice;

		public RecordDialog(Context context) {
			super(context, R.style.dialog);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_recording);
			iv_voice = (ImageView) recordDialog.findViewById(R.id.iv_voice);
		}

	}

}
