package com.example.shareholders.activity.stock;

import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.http.protocol.ResponseConnControl;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.h.m;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.Fragment_Price_Situation.MyAdapter;
import com.example.shareholders.jacksonModel.stock.SecurityAlert;
import com.example.shareholders.jacksonModel.stock.quotationDetail;
import com.example.shareholders.util.Mapper;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_remind_edit)
public class RemindEditActivity extends Activity {
	@ViewInject(R.id.iv_title_back)
	private ImageView iv_back;
	// 确定
	@ViewInject(R.id.tv_edit_sure)
	private TextView tv_sure;
	// 涨位
	@ViewInject(R.id.iv_remind_btn_close1)
	private ImageView iv_btn_checked1;
	// 跌位
	@ViewInject(R.id.iv_remind_btn_close2)
	private ImageView iv_btn_checked2;
	// 涨幅
	@ViewInject(R.id.iv_remind_btn_close3)
	private ImageView iv_btn_checked3;
	// 成交额
	@ViewInject(R.id.iv_remind_btn_close4)
	private ImageView iv_btn_checked4;
	// 换手率
	@ViewInject(R.id.iv_remind_btn_close5)
	private ImageView iv_btn_checked5;
	// 公告新闻处理
	@ViewInject(R.id.iv_remind_btn_close6)
	private ImageView iv_btn_checked6;
	// 股票代码
	String symbol;
	// 类型
	String securityType;
	// 股票名称
	String shortname;
	// 最新价
	@ViewInject(R.id.tv_newestPrice_num)
	private TextView tv_newestPrice_num;
	// 涨跌
	@ViewInject(R.id.tv_zhangdie_num)
	private TextView tv_zhangdie_num;
	// 涨幅
	@ViewInject(R.id.tv_zhangfu_num)
	private TextView tv_zhangfu_num;
	// 换手率
	@ViewInject(R.id.tv_huanshoulv_num)
	private TextView tv_huanshoulv_num;
	// 成交金额
	@ViewInject(R.id.tv_turnoverAmount_num)
	private TextView tv_turnoverAmount_num;
	// 公司名称
	@ViewInject(R.id.tv_edit_name)
	private TextView tv_edit_name;
	// 股价涨到
	@ViewInject(R.id.et_high_price)
	private EditText et_high_price;
	// 股价跌到
	@ViewInject(R.id.et_low_price)
	private EditText et_low_price;
	// 日涨幅超过
	@ViewInject(R.id.et_changeRatio)
	private EditText et_changeRatio;
	// 成交金额
	@ViewInject(R.id.et_amount)
	private EditText et_amount;
	// 换手率
	@ViewInject(R.id.et_turnoverate)
	private EditText et_turnoverate;
	// 默认没有设置提醒的值
	private final static int DEFAULT = -10;
	private ImageView[] iv_btn_checked = { iv_btn_checked1, iv_btn_checked2,
			iv_btn_checked3, iv_btn_checked4, iv_btn_checked5, iv_btn_checked6 };
	// 从来未设置过
	private boolean[] flag = { false, false, false, false, false, false };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		symbol = this.getIntent().getExtras().getString("symbol");
		securityType = this.getIntent().getExtras().getString("securityType");
		shortname = this.getIntent().getExtras().getString("shortname");
		getSecurityAlert(symbol, securityType);
		getData(symbol, securityType);
	}

	/**
	 * 添加更新提醒信息
	 * 
	 * @param symbol
	 * @param securityType
	 * @param highPrice
	 * @param lowPrice
	 * @param changeRatio
	 * @param amount
	 * @param turnoveRate
	 * @param newsAnnRep
	 */
	private void addSecurityAlert(String symbol, String securityType,
			double highPrice, double lowPrice, double changeRatio, long amount,
			double turnoveRate, boolean newsAnnRep) {
		String url = AppConfig.URL_INFO
				+ "addSecurityAlert/"
				+ symbol
				+ ".json?"
				+ "access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&highPrice=" + highPrice
				+ "&lowPrice=" + lowPrice + "&changeRatio=" + changeRatio
				+ "&amount=" + amount + "&turnoverRate=" + turnoveRate
				+ "&newsAnnRep=" + newsAnnRep + "&securityType=" + securityType;
		Log.d("addSecurityAlert", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "修改成功",
								Toast.LENGTH_SHORT);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("error", error.toString());
					}
				});
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 获得提醒信息
	 * 
	 * @param symbol
	 * @param securityType
	 */
	private void getSecurityAlert(String symbol, String securityType) {
		String url = AppConfig.URL_INFO
				+ "getSecurityAlert/"
				+ symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&securityType="
				+ securityType;
		Log.d("getSecurityAlert", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Mapper mapper = new Mapper();
						try {
							SecurityAlert securityAlert = mapper.readValue(
									response, SecurityAlert.class);
							DecimalFormat decimalFormat = new DecimalFormat(
									"0.00");
							// 股价涨到
							if (securityAlert.getHighPrice() != DEFAULT) {
								flag[0] = true;
								setBtnChecked(iv_btn_checked1, flag[0]);
								et_high_price.setText(securityAlert
										.getHighPrice() + "");
							}
							// 股价跌倒
							if (securityAlert.getLowPrice() != DEFAULT) {
								flag[1] = true;
								setBtnChecked(iv_btn_checked2, flag[1]);
								et_low_price.setText(securityAlert
										.getLowPrice() + "");
							}
							// 日涨幅超过
							if (securityAlert.getChangeRatio() != DEFAULT) {
								flag[2] = true;
								setBtnChecked(iv_btn_checked3, flag[2]);

								et_changeRatio.setText(decimalFormat
										.format(securityAlert.getChangeRatio() * 100));
							}
							// 成交金额
							if (securityAlert.getAmount() != DEFAULT) {
								flag[3] = true;
								setBtnChecked(iv_btn_checked4, flag[3]);
								et_amount.setText(securityAlert.getAmount()
										+ "");
							}
							// 换手率
							if (securityAlert.getTurnoverRate() != DEFAULT) {
								flag[4] = true;
								setBtnChecked(iv_btn_checked5, flag[4]);
								et_turnoverate.setText(decimalFormat
										.format(securityAlert.getTurnoverRate() * 100));
							}
							// 公告新闻研报处理
							if (securityAlert.isNewsAnnRep()) {
								flag[5] = true;
								setBtnChecked(iv_btn_checked6, flag[5]);
							}
						} catch (JsonParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("getSecurityAlert", error.toString());
					}
				});
		stringRequest.setTag("getSecurityAlert");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 获取股票或者指数报价信息
	 * 
	 * @param symbol
	 */
	private void getData(String symbol, String securityType) {
		tv_edit_name.setText(shortname);
		if (securityType.equals("LISTED"))
			securityType = "STOCK";
		String url = AppConfig.URL_QUOTATION
				+ "quotationDetaill/"
				+ symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN) + "&securityType="
				+ securityType;
		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Mapper mapper = new Mapper();
						DecimalFormat decimalFormat = new DecimalFormat("0.00");
						try {
							quotationDetail quotationDetail = mapper.readValue(
									response, quotationDetail.class);
							// 最新价
							tv_newestPrice_num.setText(decimalFormat
									.format(quotationDetail.getPrice()));
							// 涨跌
							tv_zhangdie_num.setText(decimalFormat
									.format(quotationDetail.getChange()));
							// 涨幅
							tv_zhangfu_num.setText(decimalFormat
									.format(quotationDetail.getChangeRatio())
									+ "%");
							// 换手率
							tv_huanshoulv_num
									.setText(decimalFormat
											.format(quotationDetail
													.getTurnoverRate() * 100)
											+ "%");
							// 成交金额
							double amount = quotationDetail.getAmount();
							if (amount > 10000) {
								amount = amount / 10000;
								tv_turnoverAmount_num.setText(decimalFormat
										.format(amount) + "万元");
							} else
								tv_turnoverAmount_num.setText(decimalFormat
										.format(amount) + "元");
						} catch (JsonParseException e) {
							// v
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
		stringRequest.setTag("getData");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("getData");
		MyApplication.getRequestQueue().cancelAll("getSecurityAlert");
		super.onDestroy();
	}

	private void setBtnChecked(ImageView iv, boolean flag) {
		if (flag) {
			iv.setImageResource(R.drawable.btn_open);
		} else {
			iv.setImageResource(R.drawable.btn_close);
		}
	}

	private void setBtn(ImageView[] iv, boolean[] flag) {
		for (int i = 0; i < iv.length; i++) {
			setBtnChecked(iv[i], flag[i]);
		}
	}

	@OnClick({ R.id.iv_title_back, R.id.tv_edit_sure,
			R.id.iv_remind_btn_close1, R.id.iv_remind_btn_close2,
			R.id.iv_remind_btn_close3, R.id.iv_remind_btn_close4,
			R.id.iv_remind_btn_close5, R.id.iv_remind_btn_close6 })
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_title_back:
			finish();
			break;
		case R.id.tv_edit_sure:
			// 最高价
			double highPrice = DEFAULT;
			// 最低价
			double lowPrice = DEFAULT;
			// 日涨幅
			double changeRatio = DEFAULT;
			// 成交金额
			long amount = DEFAULT;
			// 换手率
			double turnoverate = DEFAULT;
			try {
				if (flag[0])
					highPrice = Double.parseDouble(et_high_price.getText()
							.toString());
				if (flag[1])
					lowPrice = Double.parseDouble(et_low_price.getText()
							.toString());
				if (flag[2]) {
					changeRatio = Double.parseDouble(et_changeRatio.getText()
							.toString());
					changeRatio = changeRatio / 100;
				}
				if (flag[3])
					amount = Long.parseLong(et_amount.getText().toString());
				if (flag[4]) {
					turnoverate = Double.parseDouble(et_turnoverate.getText()
							.toString());
					turnoverate = turnoverate / 100;
				}
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(getApplicationContext(), "输入错误", 0).show();
				break;
			}
			addSecurityAlert(symbol, securityType, highPrice, lowPrice,
					changeRatio, amount, turnoverate, flag[5]);
			//cj
			finish();
			break;
		case R.id.iv_remind_btn_close1:
			flag[0] = !flag[0];
			// setBtn(iv_btn_checked, flag);
			setBtnChecked(iv_btn_checked1, flag[0]);
			break;
		case R.id.iv_remind_btn_close2:
			flag[1] = !flag[1];
			setBtnChecked(iv_btn_checked2, flag[1]);
			break;
		case R.id.iv_remind_btn_close3:
			flag[2] = !flag[2];
			setBtnChecked(iv_btn_checked3, flag[2]);
			break;
		case R.id.iv_remind_btn_close4:
			flag[3] = !flag[3];
			setBtnChecked(iv_btn_checked4, flag[3]);
			break;
		case R.id.iv_remind_btn_close5:
			flag[4] = !flag[4];
			setBtnChecked(iv_btn_checked5, flag[4]);
			break;
		case R.id.iv_remind_btn_close6:
			flag[5] = !flag[5];
			setBtnChecked(iv_btn_checked6, flag[5]);
			break;

		default:
			break;
		}

	}
	//下面三个用于隐藏软键盘
			@Override
			public boolean dispatchTouchEvent(MotionEvent ev) {

				if (ev.getAction() == MotionEvent.ACTION_DOWN) {

					// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
					View v = getCurrentFocus();

					if (isShouldHideInput(v, ev)) {
						hideSoftInput(v.getWindowToken());
					}
				}

				return super.dispatchTouchEvent(ev);
			}
			/**
			 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
			 * 
			 * @param v
			 * @param event
			 * @return
			 */
			private boolean isShouldHideInput(View v, MotionEvent event) {
				if (v != null && (v instanceof EditText)) {
					int[] l = { 0, 0 };
					v.getLocationInWindow(l);
					int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
							+ v.getWidth();
					if (event.getRawX() > left && event.getRawX() < right
							&& event.getRawY() > top && event.getRawY() < bottom) {
						// 点击EditText的事件，忽略它。
						return false;
					} else {
						return true;
					}
				}
				// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
				return false;
			}
			/**
			 * 多种隐藏软件盘方法的其中一种
			 * 
			 * @param token
			 */
			private void hideSoftInput(IBinder token) {
				if (token != null) {
					InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					im.hideSoftInputFromWindow(token,
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
}
