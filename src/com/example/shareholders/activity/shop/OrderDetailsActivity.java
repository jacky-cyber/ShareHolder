package com.example.shareholders.activity.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_order_details)
public class OrderDetailsActivity extends Activity {

	@ViewInject(R.id.iv_backgroud)
	private ImageView iv_backgroud;
	@ViewInject(R.id.rl_fill_order)
	private RelativeLayout rl_fill_order;
	// 交易状态
	@ViewInject(R.id.tv_trade_status)
	private TextView tv_trade_status;
	// 底部按钮1
	@ViewInject(R.id.view_bottom_btn1)
	private View view_bottom_btn1;
	@ViewInject(R.id.tv_bottom_btn1)
	private TextView tv_bottom_btn1;
	// 底部按钮2
	@ViewInject(R.id.tv_bottom_btn2)
	private TextView tv_bottom_btn2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		ViewUtils.inject(this);
		initView();
	}

	@OnClick({ R.id.title_note, R.id.tv_logistics ,R.id.rl_return})
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.tv_logistics:
			startActivity(new Intent(getApplicationContext(),
					LogisticsActivity.class));
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void initView() {
		tradeSuccess();
	}

	// 交易成功
	private void tradeSuccess() {
		tv_trade_status.setText(R.string.order_details_trade_success);
		view_bottom_btn1.setVisibility(View.VISIBLE);
		tv_bottom_btn1.setVisibility(View.VISIBLE);
		tv_bottom_btn1.setText(R.string.order_details_for_return);
		tv_bottom_btn2.setText(R.string.order_details_delete_order);
		tv_bottom_btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						SalesReturnActivity.class));
			}
		});
		tv_bottom_btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO delete order
			}
		});
	}

	// 买家已收货
	private void tradeReceived() {
		tv_trade_status.setText(R.string.order_details_received);
		view_bottom_btn1.setVisibility(View.INVISIBLE);
		tv_bottom_btn1.setVisibility(View.INVISIBLE);
		tv_bottom_btn2.setText(R.string.order_details_for_return);
		tv_bottom_btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						SalesReturnActivity.class));
			}
		});
	}

	// 卖家已发货
	private void tradeDelivered() {
		tv_trade_status.setText(R.string.order_details_delivered);
		view_bottom_btn1.setVisibility(View.INVISIBLE);
		tv_bottom_btn1.setVisibility(View.INVISIBLE);
		tv_bottom_btn2.setText(R.string.order_details_for_refund);
		tv_bottom_btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						RefundActivity.class));
			}
		});
	}

	// 等待买家付款
	private void tradeToPay() {
		tv_trade_status.setText(R.string.order_details_to_pay);
		view_bottom_btn1.setVisibility(View.VISIBLE);
		tv_bottom_btn1.setVisibility(View.VISIBLE);
		tv_bottom_btn1.setText(R.string.order_details_modify_order);
		tv_bottom_btn2.setText(R.string.order_details_delete_order);
		tv_bottom_btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO modify order
			}
		});
		tv_bottom_btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO delete order
			}
		});
	}

	// 订单已取消
	private void tradeHasCancel() {
		tv_trade_status.setText(R.string.order_details_order_has_canceled);
		view_bottom_btn1.setVisibility(View.VISIBLE);
		view_bottom_btn1.setVisibility(View.INVISIBLE);
		tv_bottom_btn1.setVisibility(View.INVISIBLE);
		tv_bottom_btn2.setText(R.string.order_details_delete_order);
		tv_bottom_btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO delete order
			}
		});
	}

	/**
	 * 弹出选择分类和数量
	 * 
	 * @param context
	 * @param viewGroup
	 * @return
	 */
	public void popupChooseAddress(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_select_receiver_address, null);
		TextView tv_district = (TextView) contentView
				.findViewById(R.id.tv_district);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_ok);
		ImageView iv_gd_popup_cancel = (ImageView) contentView
				.findViewById(R.id.iv_cancel);
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		// popwindow位置
		popupWindow.showAtLocation(rl_fill_order, Gravity.CENTER, 0, 0);
		iv_backgroud.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// method stub
				iv_backgroud.setAlpha(0.0f);
			}
		});

		tv_district.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();

			}
		});

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();

			}
		});

		iv_gd_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
	}

}
