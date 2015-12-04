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

/**
 * 
 * @author 杜劲敏
 * 用于无图片的选择框
 * 
 */

public class DialogManager2 {
	// 上下文
	private Context context;
	private AlertDialog alertDialog;
	// 蓝色对话
	private TextView tv_message; // 对话框的提示语
	private TextView tv_confirm; // 确定
	private TextView tv_cancel; // 取消
	

	private static DialogManager2 mInstance;

	public static DialogManager2 getInstance(Context context) {
		mInstance = new DialogManager2(context);
		return mInstance;
	}

	public DialogManager2(Context context) {
		this.context = context;
	}


	public void ShowBlueDialog() {
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_general_layout2);
		tv_message = (TextView) window.findViewById(R.id.tv_message);
		tv_confirm = (TextView) window.findViewById(R.id.tv_confirm);
		tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
		alertDialog.setCancelable(true);
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

}
