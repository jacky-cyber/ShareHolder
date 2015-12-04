package com.example.shareholders.activity.fund;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.MyHScrollView;
import com.example.shareholders.common.MyHScrollView.OnScrollChangedListener;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_new_fund)
public class NewFundActivity extends Activity {

	@ViewInject(R.id.fl_layout)
	private RelativeLayout fLayout;
	@ViewInject(R.id.background)
	private RelativeLayout background;
	@ViewInject(R.id.rl_fl_title)
	private RelativeLayout rl_return;
	@ViewInject(R.id.rl_fl_comments)
	private RelativeLayout rl_fl_comments;
	// 基金标题
	@ViewInject(R.id.rl_fl_foud_title)
	private MyListView rl_fl_foud_title;
	// 股票基金
	@ViewInject(R.id.tv_fl_stock_foud)
	private TextView tv_fl_stock_foud;
	@ViewInject(R.id.rl_fl_stock_foud_buttom)
	private View rl_fl_stock_foud_buttom;
	// 债券基金
	@ViewInject(R.id.tv_fl_bond_foud)
	private TextView tv_fl_bond_foud;
	@ViewInject(R.id.rl_fl_bond_foud_buttom)
	private View rl_fl_bond_foud_buttom;
	// 混合基金
	@ViewInject(R.id.tv_fl_blend_foud)
	private TextView tv_fl_blend_foud;
	@ViewInject(R.id.rl_fl_blend_foud_buttom)
	private View rl_fl_blend_foud_buttom;
	// 货币基金
	@ViewInject(R.id.tv_fl_money_foud)
	private TextView tv_fl_money_foud;
	@ViewInject(R.id.rl_fl_money_foud_buttom)
	private View rl_fl_money_foud_buttom;
	// 封闭基金
	@ViewInject(R.id.tv_fl_closed_foud)
	private TextView tv_fl_closed_foud;
	@ViewInject(R.id.rl_fl_closed_foud_buttom)
	private View rl_fl_closed_foud_buttom;
	// 其他基金
	@ViewInject(R.id.tv_fl_other_foud)
	private TextView tv_fl_other_foud;
	@ViewInject(R.id.rl_fl_other_foud_buttom)
	private View rl_fl_other_foud_buttom;
	// 基金列表
	@ViewInject(R.id.mv_foud_list)
	private ListView lv_foud_list;
	// 基金名称
	@ViewInject(R.id.item_foud_name)
	private TextView tv_name;
	// 基金代码
	@ViewInject(R.id.item_foud_num)
	private TextView tv_num;
	// 货币基金（收益时间）
	@ViewInject(R.id.item_foud_accrual_time)
	private TextView tv_accrual_time;
	// 货币基金（万份收益）
	@ViewInject(R.id.item_foud_accrual)
	private TextView tv_accrual;
	// 货币基金（7日年化收益）
	@ViewInject(R.id.item_foud_rate)
	private TextView tv_rate;
	// 非货币基金（发行时间）
	@ViewInject(R.id.item_foud_release_date)
	private TextView tv_release_date;
	// 非货币基金（最新净值）
	@ViewInject(R.id.item_foud_newest_nav)
	private TextView tv_newest_nav;
	// 非货币基金（日涨幅）
	@ViewInject(R.id.item_foud_day_rise)
	private TextView tv_day_rise;
	// 非货币基金（周涨幅）
	@ViewInject(R.id.item_foud_week_rise)
	private TextView tv_week_rise;
	// 非货币基金（月涨幅）
	@ViewInject(R.id.item_foud_month_rise)
	private TextView tv_month_rise;
	// 非货币基金（年涨幅）
	@ViewInject(R.id.item_foud_year_rise)
	private TextView tv_year_rise;

	private lvFoudListAdapter lv_foud_adapter;
	private ArrayList<HashMap<String, Object>> lv_foud_hashMaps;

	private int fund_select_num = 0;
	private final int STOCK = 0;
	private final int BOND = 1;
	private final int BLEND = 2;
	private final int MONEY = 3;
	private final int CLOSE = 4;
	private final int OTHER = 5;

	RelativeLayout mHead;

	// 初始化
	RequestQueue volleyRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(this);
		initList();
		OnTab(STOCK);

	}

	@OnClick({ R.id.title_fl_research, R.id.title_fl_note,
			R.id.tv_fl_stock_foud, R.id.tv_fl_bond_foud, R.id.tv_fl_blend_foud,
			R.id.tv_fl_money_foud, R.id.tv_fl_closed_foud,
			R.id.tv_fl_other_foud })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_fl_note:
			finish();
			break;
		case R.id.tv_fl_stock_foud:
			ClearTab();
			OnTab(0);
			break;
		case R.id.tv_fl_bond_foud:
			ClearTab();
			OnTab(1);
			break;
		case R.id.tv_fl_blend_foud:
			ClearTab();
			OnTab(2);
			break;
		case R.id.tv_fl_money_foud:
			ClearTab();
			OnTab(3);
			break;
		case R.id.tv_fl_closed_foud:
			ClearTab();
			OnTab(4);
			break;
		case R.id.tv_fl_other_foud:
			ClearTab();
			OnTab(5);
			break;
		case R.id.title_fl_research:
			startActivity(new Intent(this, FundSearchActivity.class));
			break;
		default:
			break;
		}
	}

	private void OnTab(int num) {
		fund_select_num = num;
		switch (fund_select_num) {
		case STOCK:
			tv_fl_stock_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_stock_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			getFundList(0, "INCEPTION_DATE", "true", 0, 10);
			break;
		case BOND:
			tv_fl_bond_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_bond_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			getFundList(1, "INCEPTION_DATE", "true", 0, 10);
			break;
		case BLEND:
			tv_fl_blend_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_blend_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			getFundList(2, "INCEPTION_DATE", "true", 0, 10);
			break;
		case MONEY:
			tv_fl_money_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_money_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			getFundList(3, "ACHIEVERETURN", "true", 0, 10);
			break;
		case CLOSE:
			tv_fl_closed_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_closed_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			getFundList(4, "INCEPTION_DATE", "true", 0, 10);
			break;
		case OTHER:
			tv_fl_other_foud.setTextColor(this.getResources().getColor(
					R.color.foud_text_select_color));
			rl_fl_other_foud_buttom
					.setBackgroundResource(R.color.foud_text_select_color);
			getFundList(5, "INCEPTION_DATE", "true", 0, 10);
			break;
		default:
			break;
		}

	}

	private void ClearTab() {
		tv_fl_stock_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_stock_foud_buttom
				.setBackgroundResource(R.color.foud_text_base_color);
		tv_fl_bond_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_bond_foud_buttom
				.setBackgroundResource(R.color.foud_text_base_color);
		tv_fl_blend_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_blend_foud_buttom
				.setBackgroundResource(R.color.foud_text_base_color);
		tv_fl_money_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_money_foud_buttom
				.setBackgroundResource(R.color.foud_text_base_color);
		tv_fl_closed_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_closed_foud_buttom
				.setBackgroundResource(R.color.foud_text_base_color);
		tv_fl_other_foud.setTextColor(this.getResources().getColor(
				R.color.foud_text_base_color));
		rl_fl_other_foud_buttom
				.setBackgroundResource(R.color.foud_text_base_color);
	}

	public void initList() {
		tv_name.setTextColor(Color.parseColor("#939393"));
		tv_num.setVisibility(View.GONE);
		tv_accrual.setTextColor(Color.parseColor("#939393"));
		tv_accrual_time.setTextColor(Color.parseColor("#939393"));
		tv_rate.setTextColor(Color.parseColor("#939393"));
		tv_release_date.setTextColor(Color.parseColor("#939393"));
		tv_newest_nav.setTextColor(Color.parseColor("#939393"));
		tv_day_rise.setTextColor(Color.parseColor("#939393"));
		tv_week_rise.setTextColor(Color.parseColor("#939393"));
		tv_month_rise.setTextColor(Color.parseColor("#939393"));
		tv_year_rise.setTextColor(Color.parseColor("#939393"));

		mHead = (RelativeLayout) findViewById(R.id.head);
		mHead.setFocusable(true);
		mHead.setClickable(true);
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		lv_foud_hashMaps = new ArrayList<HashMap<String, Object>>();
		lv_foud_list.setFocusable(false);
		lv_foud_list
				.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		lv_foud_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(getApplicationContext(),
						FundDetailsActivity.class);
				switch (fund_select_num) {
				case STOCK:
				case BOND:
				case BLEND:
				case CLOSE:
				case OTHER:
					intent.putExtra("fund_type", "非货币型基金");
					break;
				case MONEY:
					intent.putExtra("fund_type", "货币型基金");
					break;
				default:
					break;
				}
				Log.d("symbol", lv_foud_hashMaps.get(position).get("symbol")
						.toString());
				intent.putExtra("symbol",
						lv_foud_hashMaps.get(position).get("symbol").toString());
				startActivity(intent);

			}
		});
	}

	public class lvFoudListAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;
		public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();
		int id_row_layout;

		public lvFoudListAdapter(Context context,
				ArrayList<HashMap<String, Object>> list, int id_row_layout) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			this.id_row_layout = id_row_layout;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				synchronized (NewFundActivity.this) {
					holder = new ViewHolder();
					view = LayoutInflater.from(context).inflate(id_row_layout,
							null);
					MyHScrollView scrollView1 = (MyHScrollView) view
							.findViewById(R.id.horizontalScrollView1);
					holder.scrollView = scrollView1;

					holder.item_foud_name = (TextView) view
							.findViewById(R.id.item_foud_name);
					holder.item_foud_num = (TextView) view
							.findViewById(R.id.item_foud_num);
					// 货币基金
					holder.item_foud_accrual_time = (TextView) view
							.findViewById(R.id.item_foud_accrual_time);
					holder.item_foud_accrual = (TextView) view
							.findViewById(R.id.item_foud_accrual);
					holder.item_foud_rate = (TextView) view
							.findViewById(R.id.item_foud_rate);
					// 非货币基金
					holder.item_foud_release_date = (TextView) view
							.findViewById(R.id.item_foud_release_date);
					holder.item_foud_newest_nav = (TextView) view
							.findViewById(R.id.item_foud_newest_nav);
					holder.item_foud_day_rise = (TextView) view
							.findViewById(R.id.item_foud_day_rise);
					holder.item_foud_week_rise = (TextView) view
							.findViewById(R.id.item_foud_week_rise);
					holder.item_foud_month_rise = (TextView) view
							.findViewById(R.id.item_foud_month_rise);
					holder.item_foud_year_rise = (TextView) view
							.findViewById(R.id.item_foud_year_rise);

					holder.item_rl_fl = (RelativeLayout) view
							.findViewById(R.id.item_rl_fl);

					MyHScrollView headSrcrollView = (MyHScrollView) mHead
							.findViewById(R.id.horizontalScrollView1);
					headSrcrollView
							.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
									scrollView1));

					view.setTag(holder);
					mHolderList.add(holder);
				}
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.item_foud_name.setText((String) list.get(position).get(
					"name"));
			holder.item_foud_num.setText((String) list.get(position).get(
					"symbol"));
			// holder.item_foud_accrual.setText((String)list.get(position).get(
			// "achieveReturn"));
			// holder.item_foud_rate.setText((String)list.get(position).get(
			// "annualizedYield"));

			if (fund_select_num != MONEY) {// 非货币基金
				tv_accrual_time.setVisibility(View.GONE);
				tv_accrual.setVisibility(View.GONE);
				tv_rate.setVisibility(View.GONE);
				tv_release_date.setVisibility(View.VISIBLE);
				tv_newest_nav.setVisibility(View.VISIBLE);
				tv_day_rise.setVisibility(View.VISIBLE);
				tv_week_rise.setVisibility(View.VISIBLE);
				tv_month_rise.setVisibility(View.VISIBLE);
				tv_year_rise.setVisibility(View.VISIBLE);

				holder.item_foud_accrual_time.setVisibility(View.GONE);
				holder.item_foud_accrual.setVisibility(View.GONE);
				holder.item_foud_rate.setVisibility(View.GONE);
				holder.item_foud_release_date.setVisibility(View.VISIBLE);
				holder.item_foud_newest_nav.setVisibility(View.VISIBLE);
				holder.item_foud_day_rise.setVisibility(View.VISIBLE);
				holder.item_foud_week_rise.setVisibility(View.VISIBLE);
				holder.item_foud_month_rise.setVisibility(View.VISIBLE);
				holder.item_foud_year_rise.setVisibility(View.VISIBLE);

				try {
					long Time = Long.parseLong(list.get(position)
							.get("tradingDate").toString());
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
					String creationTime = dateFormat.format(new Date(Time));
					holder.item_foud_release_date.setText(creationTime);
				} catch (NumberFormatException e1) {
					holder.item_foud_release_date.setText("--");
				}
				try {
					String nav = (String) list.get(position).get("nav");
					holder.item_foud_newest_nav.setText(String.format("%.4f",
							Double.parseDouble(nav)));
				} catch (NumberFormatException e1) {
					holder.item_foud_newest_nav.setText("--");
				}
				try {
					Double day_rise = Double.parseDouble((String) list.get(
							position).get("lastDayGrowthRate"));
					if (day_rise > 0) {
						holder.item_foud_day_rise.setText("+"
								+ String.format("%.2f", day_rise * 10) + "%");
					} else {
						holder.item_foud_day_rise.setText(String.format("%.2f",
								day_rise * 10) + "%");
					}
				} catch (NumberFormatException e1) {
					holder.item_foud_day_rise.setText("--");
				}
				try {
					Double week_rise = Double.parseDouble((String) list.get(
							position).get("lastWeekGrowthRate"));
					if (week_rise > 0) {
						holder.item_foud_week_rise.setText("+"
								+ String.format("%.2f", week_rise * 10) + "%");
					} else {
						holder.item_foud_week_rise.setText(String.format(
								"%.2f", week_rise * 10) + "%");
					}
				} catch (NumberFormatException e1) {
					holder.item_foud_week_rise.setText("--");
				}
				try {
					Double month_rise = Double.parseDouble((String) list.get(
							position).get("lastMonthGrowthRate"));
					if (month_rise > 0) {
						holder.item_foud_month_rise.setText("+"
								+ String.format("%.2f", month_rise * 10) + "%");
					} else {
						holder.item_foud_month_rise.setText(String.format(
								"%.2f", month_rise * 10) + "%");
					}
				} catch (NumberFormatException e1) {
					holder.item_foud_month_rise.setText("--");
				}
				try {
					Double year_rise = Double.parseDouble((String) list.get(
							position).get("lastYearGrowthRate"));
					if (year_rise > 0) {
						holder.item_foud_year_rise.setText("+"
								+ String.format("%.2f", year_rise * 10) + "%");
					} else {
						holder.item_foud_year_rise.setText(String.format("%.2f",
								year_rise * 10) + "%");
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					holder.item_foud_year_rise.setText("--");
				}
			} else {// 货币基金
				tv_accrual_time.setVisibility(View.VISIBLE);
				tv_accrual.setVisibility(View.VISIBLE);
				tv_rate.setVisibility(View.VISIBLE);
				tv_release_date.setVisibility(View.GONE);
				tv_newest_nav.setVisibility(View.GONE);
				tv_day_rise.setVisibility(View.GONE);
				tv_week_rise.setVisibility(View.GONE);
				tv_month_rise.setVisibility(View.GONE);
				tv_year_rise.setVisibility(View.GONE);

				holder.item_foud_accrual_time.setVisibility(View.VISIBLE);
				holder.item_foud_accrual.setVisibility(View.VISIBLE);
				holder.item_foud_rate.setVisibility(View.VISIBLE);
				holder.item_foud_release_date.setVisibility(View.GONE);
				holder.item_foud_newest_nav.setVisibility(View.GONE);
				holder.item_foud_day_rise.setVisibility(View.GONE);
				holder.item_foud_week_rise.setVisibility(View.GONE);
				holder.item_foud_month_rise.setVisibility(View.GONE);
				holder.item_foud_year_rise.setVisibility(View.GONE);

				try {
					long Time = Long.parseLong(list.get(position)
							.get("tradingDate").toString());
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
					String creationTime = dateFormat.format(new Date(Time));
					holder.item_foud_accrual_time.setText(creationTime);
				} catch (NumberFormatException e1) {
					holder.item_foud_accrual_time.setText("--");
				}
				try {
					String nav = (String) list.get(position).get(
							"achieveReturn");
					holder.item_foud_accrual.setText(String.format("%.4f",
							Double.parseDouble(nav)));
				} catch (NumberFormatException e1) {
					holder.item_foud_accrual.setText("--");
				}
				try {
					Double day_rise = Double.parseDouble((String) list
							.get(position).get("annualizedYield"));
					if (day_rise > 0) {
						holder.item_foud_rate.setText("+"
								+ String.format("%.2f", day_rise * 10) + "%");
					} else {
						holder.item_foud_rate.setText(String.format("%.2f",
								day_rise * 10) + "%");
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					holder.item_foud_rate.setText("--");
				}
			}

			// holder.item_rl_fl.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// Intent intent = new Intent(getApplicationContext(),
			// FundDetailsActivity.class);
			// switch (fund_select_num) {
			// case STOCK:
			// case BOND:
			// case BLEND:
			// case CLOSE:
			// case OTHER:
			// intent.putExtra("fund_type", "非货币型基金");
			// break;
			// case MONEY:
			// intent.putExtra("fund_type", "货币型基金");
			// break;
			// default:
			// break;
			// }
			// Log.d("symbol", list.get(position).get("symbol").toString());
			// intent.putExtra("symbol", list.get(position).get("symbol")
			// .toString());
			// startActivity(intent);
			// }
			// });

			return view;
		}

		class OnScrollChangedListenerImp implements OnScrollChangedListener {
			MyHScrollView mScrollViewArg;

			public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
				mScrollViewArg = scrollViewar;
			}

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				mScrollViewArg.smoothScrollTo(l, t);
			}
		};

		class ViewHolder {

			TextView item_foud_name;
			TextView item_foud_num;
			TextView item_foud_accrual_time;
			TextView item_foud_accrual;
			TextView item_foud_rate;
			TextView item_foud_release_date;
			TextView item_foud_newest_nav;
			TextView item_foud_day_rise;
			TextView item_foud_week_rise;
			TextView item_foud_month_rise;
			TextView item_foud_year_rise;
			RelativeLayout item_rl_fl;

			HorizontalScrollView scrollView;
		}
	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// 当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
					.findViewById(R.id.horizontalScrollView1);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}

	/**
	 * 获取关注的基金列表
	 * 
	 * @param fundType
	 *            :STOCK,BOND,BLEND,CLOSE,OTHER
	 * @param sortField
	 *            :
	 *            "NEWEST_NAV,DAY_GROWTH,WEEK_GROWTH,MONTH_GROWTH,YEAR_GROWTH,INC
	 *            E P T I O N _ D A T E
	 * @param pageIndex
	 * @param pageSize
	 */
	private void getFundList(final int fundType, String sortField,
			String isDesc, int pageIndex, int pageSize) {
		String url = null;
		switch (fundType) {
		case STOCK:
			url = AppConfig.URL_FUND
					+ "list/fundExceptCurrency.json?access_token="
					+ RsSharedUtil.getString(getApplicationContext(),
							"access_token") + "&fundType=STOCK" + "&sortField="
					+ sortField + "&isDesc=" + isDesc + "&page=" + pageIndex
					+ "&size=" + pageSize;
			break;
		case BOND:
			url = AppConfig.URL_FUND
					+ "list/fundExceptCurrency.json?access_token="
					+ RsSharedUtil.getString(getApplicationContext(),
							"access_token") + "&fundType=BOND" + "&sortField="
					+ sortField + "&isDesc=" + isDesc + "&page=" + pageIndex
					+ "&size=" + pageSize;
			break;
		case BLEND:
			url = AppConfig.URL_FUND
					+ "list/fundExceptCurrency.json?access_token="
					+ RsSharedUtil.getString(getApplicationContext(),
							"access_token") + "&fundType=BLEND" + "&sortField="
					+ sortField + "&isDesc=" + isDesc + "&page=" + pageIndex
					+ "&size=" + pageSize;
			break;
		case CLOSE:
			url = AppConfig.URL_FUND
					+ "list/fundExceptCurrency.json?access_token="
					+ RsSharedUtil.getString(getApplicationContext(),
							"access_token") + "&fundType=CLOSE" + "&sortField="
					+ sortField + "&isDesc=" + isDesc + "&page=" + pageIndex
					+ "&size=" + pageSize;
			break;
		case OTHER:
			url = AppConfig.URL_FUND
					+ "list/fundExceptCurrency.json?access_token="
					+ RsSharedUtil.getString(getApplicationContext(),
							"access_token") + "&fundType=OTHER" + "&sortField="
					+ sortField + "&isDesc=" + isDesc + "&page=" + pageIndex
					+ "&size=" + pageSize;
			break;
		case MONEY:
			url = AppConfig.URL_FUND
					+ "list/currency.json?access_token="
					+ RsSharedUtil.getString(getApplicationContext(),
							"access_token") + "&sortField=" + sortField
					+ "&isDesc=" + isDesc + "&page=" + pageIndex + "&size="
					+ pageSize;
			break;
		default:
			break;
		}
		Log.d("url", "url:" + url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							Log.d("获取基金列表", "FUNDLIST" + fundType + ":"
									+ response.toString());
							// 如果没有数据
							if (response.toString().equals("")
									|| response.toString().equals("[0]")) {

							} else {
								try {
									JSONArray all = new JSONArray(response
											.toString());
									final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
									for (int i = 0; i < all.length(); i++) {
										HashMap<String, Object> data = new HashMap<String, Object>();
										Iterator<String> jsIterator;
										try {
											jsIterator = all.getJSONObject(i)
													.keys();
											while (jsIterator.hasNext()) {
												String key = jsIterator.next();
												data.put(key,
														all.getJSONObject(i)
																.get(key)
																.toString());
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										datas.add(data);
									}
									lv_foud_hashMaps = datas;
									lv_foud_adapter = new lvFoudListAdapter(
											getApplication(), lv_foud_hashMaps,
											R.layout.item_activity_foud_list);
									lv_foud_list.setAdapter(lv_foud_adapter);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
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

}
