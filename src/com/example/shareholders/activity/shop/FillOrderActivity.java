package com.example.shareholders.activity.shop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fill_order)
public class FillOrderActivity extends Activity {

	@ViewInject(R.id.rl_fill_order)
	private RelativeLayout rl_fill_order;

	// 地址栏
	@ViewInject(R.id.ll_address)
	private LinearLayout ll_address;

	// 空地址栏
	@ViewInject(R.id.ll_address_empty)
	private LinearLayout ll_address_empty;

	// 收货人地址
	@ViewInject(R.id.tv_address)
	private TextView tv_address;

	// 收货人姓名
	@ViewInject(R.id.tv_name)
	private TextView tv_name;

	// 收货人手机
	@ViewInject(R.id.tv_telephone)
	private TextView tv_telephone;

	// 收货人详细地址
	@ViewInject(R.id.tv_address_details)
	private TextView tv_address_details;

	private String addressId;
	private String addressUuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		ViewUtils.inject(this);
		initView();
	}

	@OnClick({ R.id.title_fs_note, R.id.ll_address, R.id.ll_pay, R.id.ll_sent,
			R.id.tv_bottom_buy_now, R.id.tv_company, R.id.rl_pc_goods,R.id.rl_return })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		case R.id.ll_address:
			popupChooseAddress(getApplicationContext());
			break;
		case R.id.ll_pay:
			popupChoosePay(getApplicationContext());
			break;
		case R.id.ll_sent:
			popupChooseSent(getApplicationContext());
			break;
		case R.id.tv_bottom_buy_now:
			startActivity(new Intent(getApplicationContext(),
					CashierDeskActivity.class));
			break;
		case R.id.tv_company:
			startActivity(new Intent(getApplicationContext(),
					CompanyDetailActivity.class));
			break;
		case R.id.rl_pc_goods:
			startActivity(new Intent(getApplicationContext(),
					GoodsDetailsActivity.class));
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void initView() {
		addressId = "null";
		addressUuid = "null";
		getAddress();
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
		final TextView tv_district = (TextView) contentView
				.findViewById(R.id.tv_district);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_ok);
		ImageView iv_gd_popup_cancel = (ImageView) contentView
				.findViewById(R.id.iv_cancel);
		final EditText et_name = (EditText) contentView
				.findViewById(R.id.et_receiver);
		et_name.setText(tv_name.getText().toString());
		final EditText et_telephone = (EditText) contentView
				.findViewById(R.id.et_telephone);
		et_telephone.setText(tv_telephone.getText().toString());
		final EditText et_address_details = (EditText) contentView
				.findViewById(R.id.et_address);
		et_address_details.setText(tv_address_details.getText().toString());

		final AlertDialog dialog = new AlertDialog.Builder(
				FillOrderActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

		tv_district.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				popupChooseDistrict(getApplicationContext());

			}
		});

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tv_name.setText(et_name.getText().toString().trim());
				tv_telephone.setText(et_telephone.getText().toString().trim());
				tv_address_details.setText(et_address_details.getText()
						.toString().trim());
				tv_address.setText(tv_district.getText().toString().trim());
				updateAddress();
				dialog.dismiss();

			}
		});

		iv_gd_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 弹出选择送货地址
	 * 
	 * @param context
	 * @return
	 */
	public void popupChooseDistrict(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_select_address, null);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_ok);

		final AlertDialog dialog = new AlertDialog.Builder(
				FillOrderActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});
	}

	/**
	 * 弹出选择配送方式
	 * 
	 * @param context
	 * @return
	 */
	public void popupChooseSent(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_select_sent, null);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_ok);

		final AlertDialog dialog = new AlertDialog.Builder(
				FillOrderActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});
	}

	/**
	 * 弹出选择支付方式
	 * 
	 * @param context
	 * @return
	 */
	public void popupChoosePay(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_select_pay, null);
		TextView tv_gd_popup_ok = (TextView) contentView
				.findViewById(R.id.tv_ok);

		final AlertDialog dialog = new AlertDialog.Builder(
				FillOrderActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

		tv_gd_popup_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});
	}

	/**
	 * 获取用户地址
	 */
	private void getAddress() {
		String url = AppConfig.URL_SHOP + "address/getAddressByPage";

		JSONObject params = new JSONObject();
		try {
			params.put("custId", AppConfig.custId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// Log.d("liang_response_detail", response);
						try {
							JSONObject jsonObject = new JSONArray(response)
									.getJSONObject(0);

							ll_address_empty.setVisibility(View.GONE);
							ll_address.setVisibility(View.VISIBLE);
							tv_name.setText(jsonObject.getString("addressName"));
							tv_telephone.setText(jsonObject
									.getString("addressTele"));
							tv_address.setText(jsonObject
									.getString("addressProvince")
									+ jsonObject.getString("addressCity"));
							tv_address_details.setText(jsonObject
									.getString("addressDetail"));

							addressId = jsonObject.getString("addressId");
							addressUuid = jsonObject.getString("addressUuid");

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),
									"获取地址失败" + e.toString(), 1).show();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								"获取地址失败" + error.toString(), 1).show();
					}
				});

		stringRequest.setTag("getProductDetail");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	/**
	 * 增加或修改用户地址
	 */
	private void updateAddress() {

		String url = null;
		JSONObject params = new JSONObject();

		if (addressId.equals("null")) {
			url = AppConfig.URL_SHOP + "address/addAddress";
			try {
				params.put("custId", AppConfig.custId);
				params.put("addressName", tv_name.getText().toString().trim());
				params.put("addressTele", tv_telephone.getText().toString()
						.trim());
				params.put("addressCity", tv_address.getText().toString()
						.trim());
				params.put("addressProvince", tv_address.getText().toString()
						.trim());
				params.put("addressDetail", tv_address_details.getText()
						.toString().trim());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			url = AppConfig.URL_SHOP + "address/updateAddress";
			try {
				params.put("custId", AppConfig.custId);
				params.put("addressUuid", addressUuid);
				params.put("addressId", addressId);
				params.put("addressName", tv_name.getText().toString().trim());
				params.put("addressTele", tv_telephone.getText().toString()
						.trim());
				params.put("addressCity", tv_address.getText().toString()
						.trim());
				params.put("addressProvince", tv_address.getText().toString()
						.trim());
				params.put("addressDetail", tv_address_details.getText()
						.toString().trim());
				params.put("addressDefaulted", "true");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								"更新地址失败" + error.toString(), 1).show();
					}
				});

		stringRequest.setTag("updateAddress");
		MyApplication.getRequestQueue().add(stringRequest);

	}

}
