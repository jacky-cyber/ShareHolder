package com.example.shareholders.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shareholders.R;

public class GeneralDialog {
	private Context context;
	private AlertDialog alertDialog;

	private LinearLayout ll;

	private ImageView iv_message_icon; // 对话框显示的提示图片
	private TextView tv_message; // 对话框的提示语
	private TextView tv_confirm; // 确定
	private TextView tv_cancel; // 取消

	public GeneralDialog(Context context) {
		this.context = context;
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();

		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_general_layout);

		ll = (LinearLayout) window.findViewById(R.id.ll);
		iv_message_icon = (ImageView) window.findViewById(R.id.iv_message_icon);
		tv_message = (TextView) window.findViewById(R.id.tv_message);
		tv_confirm = (TextView) window.findViewById(R.id.tv_confirm);
		tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

	}

	/**
	 * 设置提示图片
	 * 
	 * @param resId
	 */
	public void setMessageIcon(int resId) {
		iv_message_icon.setImageResource(resId);
	}

	/**
	 * 设置对话框的提示语
	 * 
	 * @param resId
	 */
	public void setMessage(int resId) {
		tv_message.setText(context.getResources().getString(resId));
	}

	public void setMessage(String message) {
		tv_message.setText(message);
	}

	/**
	 * 设置确定按钮的文字
	 * 
	 * @param resId
	 */
	public void setPositiveText(int resId) {
		tv_confirm.setText(context.getResources().getString(resId));
	}

	public void setPositiveText(String message) {
		tv_confirm.setText(message);
	}

	/**
	 * 设置取消按钮的文字
	 * 
	 * @param resId
	 */
	public void setNegativeText(int resId) {
		tv_cancel.setText(context.getResources().getString(resId));
	}

	public void setNegativeText(String message) {
		tv_cancel.setText(message);
	}

	/**
	 * 设置确定按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setPositiveButton(View.OnClickListener listener) {
		tv_confirm.setOnClickListener(listener);
	}

	/**
	 * 无取消按钮，只有确定按钮
	 */
	public void noCancel() {
		tv_cancel.setVisibility(View.GONE);
	}

	/**
	 * 无提示图片，只有文字提示
	 */
	public void noMessageIcon() {
		iv_message_icon.setVisibility(View.GONE);
	}

	/**
	 * 设置取消按钮的点击事件
	 * 
	 * @param listener
	 */
	public void setNegativeButton(View.OnClickListener listener) {
		tv_cancel.setOnClickListener(listener);
	}

	/**
	 * 是否可以通过点击对话框之外的点来关闭对话框
	 * 
	 * @param canCancel
	 */
	public void setCancel(boolean canCancel) {
		alertDialog.setCancelable(canCancel);
	}

	public void dismiss() {
		alertDialog.dismiss();
	}

}
