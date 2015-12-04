package com.example.shareholders.activity.stock;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_my_stock_details_param)
public class MyStockDetailsParamActivity extends Activity {

	Context context = MyStockDetailsParamActivity.this;

	@ViewInject(R.id.rl_parent)
	private RelativeLayout rl_parent;

	@ViewInject(R.id.tv_stock_name)
	private TextView tv_stock_name;
	@ViewInject(R.id.tv_stock_code)
	private TextView tv_stock_code;
	@ViewInject(R.id.lv)
	private ListView lv;
	@ViewInject(R.id.ll_short_introduce)
	private LinearLayout ll_short_introduce;
	@ViewInject(R.id.tv_short_introduce)
	private TextView tv_short_introduce;

	// popWindow的背景
	@ViewInject(R.id.v_bg)
	private View v_bg;
	
	//概况的选择按钮
	@ViewInject(R.id.rb_short_intro)
	private RadioButton rb_short_intro;
	
	private ShareUtils popupWindow;

	// 公司代码

	String symbol="";
	
	String stockName="";
	
	
	private String share_content="";
	// 是否来自公司简介
	Boolean ifFromCompanyDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		stockName=getIntent().getStringExtra("name");
		tv_stock_name.setText(getIntent().getStringExtra("name"));
		symbol = getIntent().getStringExtra("symbol");
		ifFromCompanyDetails = Boolean.parseBoolean(getIntent().getStringExtra(
				"ifFromCompanyDetails"));
		tv_stock_code.setText("" + symbol);
		//如果是从公司详情那边跳过来，则直接去概况
		Log.d("dj_ifFromCompanyDetails", ifFromCompanyDetails+"!");
		if (ifFromCompanyDetails) {
			onTab(1);
		}
		getParams();
		getDescription();
	}

	@OnClick({ R.id.title_note, R.id.rb_price, R.id.rb_short_intro,
			R.id.iv_share })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.rb_price:
			onTab(0);
			break;
		case R.id.rb_short_intro:
			onTab(1);
			break;
		case R.id.iv_share:
			showShare(share_content);
			break;
		default:
			break;
		}
	}

	private void onTab(int i) {
		switch (i) {
		case 0:
			lv.setVisibility(View.VISIBLE);
			ll_short_introduce.setVisibility(View.GONE);
			break;
		case 1:
			lv.setVisibility(View.GONE);
			ll_short_introduce.setVisibility(View.VISIBLE);
			rb_short_intro.setChecked(true);
			break;
		default:
			break;
		}
	}

	class ParamAdapter extends BaseAdapter {

		Context context;
		JSONObject params;

		public ParamAdapter(Context context, JSONObject object) {
			this.context = context;
			this.params = object;
		}

		@Override
		public int getCount() {
			// return params.size();
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return params;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_activity_stock_details_param, parent,
						false);
			}

			TextView tv_param_name_1 = AbViewHolder.get(view,
					R.id.tv_param_name_1);
			TextView tv_param_1 = AbViewHolder.get(view, R.id.tv_param_1);
			TextView tv_param_name_2 = AbViewHolder.get(view,
					R.id.tv_param_name_2);
			TextView tv_param_2 = AbViewHolder.get(view, R.id.tv_param_2);

			try {
				switch (position) {
				case 0:
					tv_param_name_1.setText("现价");
					tv_param_1.setText(String.format("%.2f",
							Double.parseDouble(params.getString("price"))));
					tv_param_name_2.setText("均价");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("avgPrice"))));
					break;
				case 1:
					tv_param_name_1.setText("涨幅");
					tv_param_1
							.setText(String.format("%.2f", Double
									.parseDouble(params
											.getString("changeRatio"))));
					tv_param_name_2.setText("涨跌");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("change"))));
					break;
				case 2:
					tv_param_name_1.setText("今开");
					tv_param_1.setText(String.format("%.2f",
							Double.parseDouble(params.getString("openPrice"))));
					tv_param_name_2.setText("最高");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("highPrice"))));
					break;
				case 3:
					tv_param_name_1.setText("昨收");

					tv_param_1.setText(String.format("%.2f", Double
							.parseDouble(params.getString("preClosePrice"))));

					tv_param_1.setText(String.format("%.2f",
 Double
							.parseDouble(params.getString("preClosePrice"))));

					tv_param_1.setText(String.format("%.2f",
 
							Double.parseDouble(params.getString("preClosePrice"))));

					tv_param_name_2.setText("最低");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("lowPrice"))));
					break;
				case 4:
					tv_param_name_1.setText("成交量");
					tv_param_1
							.setText(String.format("%.2f",
									Double.parseDouble(params
											.getString("volume")) / 10000)
									+ "万");
					tv_param_name_2.setText("成交额");
					tv_param_2
							.setText(String.format("%.2f",
									Double.parseDouble(params
											.getString("amount")) / 10000)
									+ "万");
					break;
				case 5:
					tv_param_name_1.setText("换手率");
					tv_param_1.setText(String.format("%.2f", Double
							.parseDouble(params.getString("turnoverRate"))));
					tv_param_name_2.setText("市盈率");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("pe"))));
					break;
				case 6:
					tv_param_name_1.setText("量比");
					tv_param_1.setText("--");
					tv_param_name_2.setText("市盈率");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("pb"))));
					break;
				case 7:
					tv_param_name_1.setText("每股净资产");
					tv_param_1.setText(String.format("%.2f",
							Double.parseDouble(params.getString("navps"))));
					tv_param_name_2.setText("每股收益");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params.getString("eps"))));
					break;
				case 8:
					tv_param_name_1.setText("总市值");
					tv_param_1
							.setText(String.format("%.2f", Double
									.parseDouble(params
											.getString("marketValue")) / 10000)
									+ "万");
					tv_param_name_2.setText("流通市值");
					tv_param_2
							.setText(String.format(
									"%.2f",
									Double.parseDouble(params
											.getString("circulatedMarketValue")) / 10000)
									+ "万");
					break;
				case 9:
					tv_param_name_1.setText("总资本");
					tv_param_1
							.setText(String.format("%.2f",
									Double.parseDouble(params
											.getString("totalShare")) / 10000)
									+ "万");
					tv_param_name_2.setText("流通股本");
					tv_param_2.setText(String.format("%.2f",
							Double.parseDouble(params
									.getString("circulatedShare")) / 10000)
							+ "万");
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return view;
		}

	}

	//获取报价
	private void getParams() {
		String url = AppConfig.URL_QUOTATION + "quotationDetaill/" + symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN)
				+ "&securityType=STOCK";

		Log.d("dj_researchReport", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_researchReport_response", response.toString());

						try {
							JSONObject object = new JSONObject(response);
							
							
							String nowString=" 现价:"+String.format("%.2f",
									Double.parseDouble(object.getString("price")));
							String change=" 涨跌:"+String.format("%.2f",
									Double.parseDouble(object.getString("change")));
							
							String radioString=" 涨幅:"+String.format("%.2f", Double
									.parseDouble(object.getString("changeRatio")));
							
							
							share_content=stockName+" "+symbol+"\n"+nowString+change+radioString;
							ParamAdapter paramAdapter = new ParamAdapter(
									context, object);
							lv.setAdapter(paramAdapter);
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_researchReport",
									e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_researchReport",
									jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("getParams");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	//获取概况
	private void getDescription() {
		String url = AppConfig.URL_QUOTATION + "description/" + symbol
				+ ".json?access_token="
				+ RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN);

		Log.d("getDescription", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("getDescription", response.toString());

						try {
							JSONObject object = new JSONObject(response);
							tv_short_introduce.setText(object
									.getString("description"));
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("getDescription", e.toString());

							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("getDescription", jsonObject.toString());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

		stringRequest.setTag("getDescription");
		MyApplication.getRequestQueue().add(stringRequest);

	}

	private void showShare(String content) {

		v_bg.setAlpha(0.5f);
		popupWindow = new ShareUtils(context, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {

				v_bg.setAlpha(0.0f);
			}
		});
	}
}
