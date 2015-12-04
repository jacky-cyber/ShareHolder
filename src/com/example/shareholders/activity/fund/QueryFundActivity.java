package com.example.shareholders.activity.fund;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.common.MyToast;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.ActionSheetDialog;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_query_fund)
public class QueryFundActivity extends Activity {

	private Context context;

	// 开始时间
	@ViewInject(R.id.tv_start_time)
	private TextView tv_start_time;

	// 结束时间
	@ViewInject(R.id.tv_end_time)
	private TextView tv_end_time;

	// 查询
	@ViewInject(R.id.tv_query)
	public static TextView tv_query;

	// 查询
	@ViewInject(R.id.lv_query_fund)
	public static MyListView lv_query_fund;

	// 选择查询条件的LinearLayout
	@ViewInject(R.id.ll_query)
	private LinearLayout ll_query;

	// 显示查询结果的LinearLayout
	@ViewInject(R.id.ll_search_result)
	private LinearLayout ll_search_result;

	// 搜索显示的时间区间的开始时间
	@ViewInject(R.id.tv_start_result_time)
	private TextView tv_start_result_time;

	// 搜索显示的时间区间的结束时间
	@ViewInject(R.id.tv_end_result_time)
	private TextView tv_end_result_time;
	
	// 空的結果
	@ViewInject(R.id.ll_empty_result)
	private LinearLayout ll_empty_result;

	QueryFundAdapter queryFundAdapter;
	ArrayList<HashMap<String, String>> searchList = null;

	/***********************************************************/
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	WheelMain wheelMain;
	View timepickerview;

	/**********************************************************/

	private boolean keepCheck = true;
	SimpleDateFormat checkFormat = new SimpleDateFormat("yyyy.MM.dd");
	private boolean canQuery = true;

	private final static int CAN_QUERY = 0;
	private final static int CAN_NOT_QUERY = 1;

	private String[] state;


	private int pageSize = 10;
	private int pageIndex = 0;

	private RequestQueue volleyRequestQueue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		tv_query.setBackgroundResource(R.drawable.btn_next_style);
		tv_start_time.setText(checkFormat.format(new Date()).toString());
		tv_end_time.setText(checkFormat.format(new Date()).toString());
		
		
		context = QueryFundActivity.this;

		searchList = new ArrayList<HashMap<String, String>>();

		queryFundAdapter = new QueryFundAdapter(context, searchList);

		volleyRequestQueue = Volley.newRequestQueue(this);

	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}
	
	


	/**
	 * 检查是否可以查询
	 */
	/*private void checkQueryState() {
		String startDate = tv_start_time.getText().toString();
		String endDate = tv_end_time.getText().toString();
		String initDate = tv_start_time.getText().toString();

		while (keepCheck) {
			startDate = tv_start_time.getText().toString();
			endDate = tv_end_time.getText().toString();

			try {
				Date date0 = checkFormat.parse(initDate);
				Date date1 = checkFormat.parse(startDate);
				Date date2 = checkFormat.parse(endDate);

				// 当开始时间和结束时间都大于1990.1.1且结束时间大于等于开始时间是，方可查询
				if (date1.getTime() > date0.getTime()
						&& (date2.getTime() > date1.getTime() || date1
								.getTime() == date0.getTime())) {
					if (!canQuery) {
						canQuery = true;
						Message msg = new Message();
						msg.what = CAN_QUERY;
						handler.sendMessage(msg);
					}
				} else {
					if (canQuery) {
						canQuery = false;
						Message msg = new Message();
						msg.what = CAN_NOT_QUERY;
						handler.sendMessage(msg);
					}
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
*/
	@OnClick({ R.id.rl_return, R.id.ll_start_time, R.id.ll_end_time,
			R.id.tv_query, R.id.tv_choose_again })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		case R.id.ll_start_time:
			setWheelMain(tv_start_time);
			new ActionSheetDialog(this, wheelMain, tv_start_time,tv_end_time,
					"choose_time_fund").builder().setTitle("请选择日期")
					.setCancelable(true).setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,1).show();

			break;

		case R.id.ll_end_time:
			setWheelMain(tv_end_time);
			new ActionSheetDialog(this, wheelMain, tv_start_time,tv_end_time,
					"choose_time_fund").builder().setTitle("请选择日期")
					.setCancelable(true).setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,2).show();

			break;

		case R.id.tv_query:
			if (canQuery) {
				ll_query.setVisibility(View.GONE);
				ll_search_result.setVisibility(View.VISIBLE);
				// 获取后台数据
				getSearchInfo();

			}
			break;
		case R.id.tv_choose_again:
			ll_query.setVisibility(View.VISIBLE);
			ll_search_result.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	/**
	 * 点击查询后获取后台的数据
	 */
	private void getSearchInfo() {
		String startDate = tv_start_time.getText().toString();
		String endDate = tv_end_time.getText().toString();

		tv_start_result_time.setText(startDate);
		tv_end_result_time.setText(endDate);

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");

		try {
			startDate = format2.format(format1.parse(startDate));
			endDate = format2.format(format1.parse(endDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = AppConfig.URL_FUND
				+ "list/bestOnTradingDate.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token") + "&startDate=";
		url += startDate + "&endDate=";
		url += endDate + "&size=" + pageSize + "&page=" + pageIndex;

		Log.d("liang_url_query", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("liang_response_query", response);
						try {
							JSONArray jsonArray = new JSONArray(response
									.toString());
							searchList = new ArrayList<HashMap<String, String>>();
							HashMap<String, String> data = null;
							Iterator<String> jIterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								jIterator = jsonArray.getJSONObject(i).keys();
								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}

								searchList.add(data);
							}
							ll_empty_result.setVisibility(View.GONE);
							queryFundAdapter = new QueryFundAdapter(context,
									searchList);
							lv_query_fund.setAdapter(queryFundAdapter);
							lv_query_fund
									.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long arg3) {
											Intent intent = new Intent(context,
													FundDetailsActivity.class);
											intent.putExtra("fund_type", ""
													+ searchList.get(position)
															.get("category")
															.toString());
											intent.putExtra("symbol", ""
													+ searchList.get(position)
															.get("symbol")
															.toString());
											startActivity(intent);
										}
									});

						} catch (Exception e) {
							// TODO Auto-generated catch block

							Log.d("error_Exception", e.toString());
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						ll_empty_result.setVisibility(View.VISIBLE);
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 设置时间滚轮的信息
	 */
	private void setWheelMain(TextView tv) {
		LayoutInflater inflater = LayoutInflater.from(this);
		timepickerview = inflater.inflate(R.layout.timepicker2, null);
		ScreenInfo screenInfo = new ScreenInfo(this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = transformTimeFormat(tv.getText().toString());
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	/**
	 * 讲yyyy.mm.dd转换为yyyy-mm-dd
	 * 
	 * @param time
	 * @return
	 */
	private String transformTimeFormat(String time) {
		int begin = 0;
		int end = 0;

		String returnTime = "";
		for (int i = 0; i < time.length(); i++) {
			if (time.charAt(i) == '.') {
				end = i;
				returnTime += time.substring(begin, end) + "-";
				begin = i + 1;
			}

			if (i == time.length() - 1) {
				end = time.length();
				returnTime += time.substring(begin, end);
			}
		}

		return returnTime.trim();
	}

	class QueryFundAdapter extends BaseAdapter {

		LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> list;

		public QueryFundAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			inflater = LayoutInflater.from(context);
			this.list = list;
			state = new String[list.size()];
			for (int position = 0; position < list.size(); position++)
				state[position] = list.get(position).get("followed")
						.toString();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View converView, ViewGroup arg2) {

			if (converView == null) {
				converView = inflater.inflate(R.layout.item_query_fund, arg2,
						false);
			}

			TextView tv_fund_name = (TextView) AbViewHolder.get(converView,
					R.id.tv_fund_name);
			TextView tv_code = (TextView) AbViewHolder.get(converView,
					R.id.tv_code);
			TextView tv_category = (TextView) AbViewHolder.get(converView,
					R.id.tv_category);
			TextView tv_fund_rate = (TextView) AbViewHolder.get(converView,
					R.id.tv_fund_rate);
			TextView tv_first = (TextView) AbViewHolder.get(converView,
					R.id.tv_first);
			TextView tv_second = (TextView) AbViewHolder.get(converView,
					R.id.tv_second);
			final ImageView iv_choose = (ImageView) AbViewHolder.get(
					converView, R.id.iv_choose);

			// 基金名称
			tv_fund_name.setText(list.get(position)
					.get("name"));

			// 基金代码
			tv_code.setText(list.get(position).get("symbol"));

			// 基金类别
			tv_category.setText(list.get(position).get(
					"category"));

			// 基金收益率
			double growthRate_double = Double.parseDouble(list.get(
					position).get("growthRate"));

			// DecimalFormat decimalFormat = new DecimalFormat("#.00");
			// String growthRate = decimalFormat.format(growthRate_double);

			tv_fund_rate
					.setText(String.format("%.2f",
					growthRate_double) + "%");

			// 开始的交易日
			String startDate = list.get(position).get("startTradingDate");
			long start = Long.parseLong(startDate);
			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
			startDate = format.format(new Date(start));

			tv_first.setText(startDate);

			// 结束的交易日
			String endDate = list.get(position).get("endTradingDate");
			long end = Long.parseLong(endDate);
			endDate = format.format(new Date(end));

			tv_second.setText(endDate);

			// 是否已关注
			if (state[position].equals("true")) {
				iv_choose
						.setImageResource(R.drawable.btn_fs_selected);
			} else {
				iv_choose
						.setImageResource(R.drawable.btn_fs_unselected);
			}

			iv_choose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Log.d("state", state[position]);
					if (state[position].trim().equals("true")) {
						iv_choose
								.setImageResource(R.drawable.btn_fs_unselected);
						state[position] = "false";
						concern("false", list.get(position).get("symbol")
								.toString(), iv_choose, state, position);

					} else {
						iv_choose.setImageResource(R.drawable.btn_fs_selected);
						state[position] = "true";
						concern("true", list.get(position).get("symbol")
								.toString(), iv_choose, state, position);

					}

				}
			});

			return converView;
		}

	}

	/**
	 * 关注/取消关注基金
	 * 
	 * @param follow
	 * @param symbol
	 * @param iv
	 */
	private void concern(final String follow, String symbol,
			final ImageView iv, final String[] state, final int position) {
		String url = AppConfig.URL_USER
				+ "security.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						"access_token") + "&followType=" + follow;
		JSONArray array = new JSONArray();
		JSONObject params = new JSONObject();
		try {
			params.put("symbol", symbol);
			params.put("type", "FUND");
			array.put(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("params", params.toString());
		StringRequest stringRequest = new StringRequest(array, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (follow.equals("true")) {
							MyToast.makeText(getApplication(), "成功添加到自选基金",
									Toast.LENGTH_SHORT).show();
							queryFundAdapter.notifyDataSetChanged();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
						if (state[position].equals("false")) {
							iv.setImageResource(R.drawable.btn_fs_selected);
							state[position] = "true";
							queryFundAdapter.notifyDataSetChanged();
						} else {
							iv.setImageResource(R.drawable.btn_fs_unselected);
							state[position] = "false";
							queryFundAdapter.notifyDataSetChanged();
						}
					}

				});
		volleyRequestQueue.add(stringRequest);
	}
}
