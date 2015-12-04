package com.example.shareholders.activity.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_return_detail)
public class ReturnDetailActivity extends Activity {

	// 还款编号
	@ViewInject(R.id.tv_refund_number)
	private TextView tv_refund_number;

	// 公司名称
	@ViewInject(R.id.tv_company_name)
	private TextView tv_company_name;

	// 退款原因
	@ViewInject(R.id.tv_refund_reason)
	private TextView tv_refund_reason;

	// 退款金额
	@ViewInject(R.id.tv_refund_amount)
	private TextView tv_refund_amount;

	// 申请时间
	@ViewInject(R.id.tv_refund_date)
	private TextView tv_refund_date;

	// 动态添加退款商品的状态
	@ViewInject(R.id.rl_add)
	private RelativeLayout rl_add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		addRefundState();
	}

	/**
	 * 显示退款的状态：失败/等待
	 */
	private void addRefundState() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.return_wait_layout, null);

		/**
		 * 设置点击事件
		 */

		// 重新申请
		TextView tv_apply_again = (TextView) view
				.findViewById(R.id.tv_apply_again);
		tv_apply_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(ReturnDetailActivity.this,
						SalesReturnActivity.class));
			}
		});

		rl_add.addView(view);
	}

	@OnClick({ R.id.iv_return ,R.id.rl_return})
	public void onClick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.iv_return:
			finish();
			break;

		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

}
