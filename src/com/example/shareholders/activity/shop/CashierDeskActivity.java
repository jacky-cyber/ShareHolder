package com.example.shareholders.activity.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shareholders.MainActivity;
import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_cashier_desk)
public class CashierDeskActivity extends Activity {

	@ViewInject(R.id.ll_to_pay)
	private LinearLayout layout_pay_way;
	@ViewInject(R.id.ll_pay_success)
	private LinearLayout layout_pay_success;
	@ViewInject(R.id.ll_pay_fail)
	private LinearLayout layout_pay_fail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Init();
	}

	@OnClick({ R.id.title_note, R.id.iv_zhifubao, R.id.iv_baidu,
			R.id.iv_weixin, R.id.iv_yinlian,R.id.rl_return })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.iv_zhifubao:
			paySuccess();
			break;
		case R.id.iv_baidu:
			payFail();
			break;
		case R.id.iv_weixin:
			break;
		case R.id.iv_yinlian:
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void Init() {
		layout_pay_success.setVisibility(View.GONE);
		layout_pay_fail.setVisibility(View.GONE);
		layout_pay_way.setVisibility(View.VISIBLE);
	}

	private void payFail() {
		layout_pay_way.setVisibility(View.GONE);
		layout_pay_success.setVisibility(View.GONE);
		layout_pay_fail.setVisibility(View.VISIBLE);
		TextView tv_view_order = (TextView) layout_pay_fail
				.findViewById(R.id.tv_view_order);
		tv_view_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						OrderDetailsActivity.class));

			}
		});
		TextView tv_try_again = (TextView) layout_pay_fail
				.findViewById(R.id.tv_try_again);
		tv_try_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Init();

			}
		});
	}

	private void paySuccess() {
		layout_pay_way.setVisibility(View.GONE);
		layout_pay_fail.setVisibility(View.GONE);
		layout_pay_success.setVisibility(View.VISIBLE);
		TextView tv_view_order = (TextView) layout_pay_success
				.findViewById(R.id.tv_view_order);
		tv_view_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						OrderDetailsActivity.class));

			}
		});
		TextView tv_continue = (TextView) layout_pay_success
				.findViewById(R.id.tv_continue_shopping);
		tv_continue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getApplicationContext(),
						MainActivity.class));

			}
		});
	}

}
