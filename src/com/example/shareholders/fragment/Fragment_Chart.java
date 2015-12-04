package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.SetAvgLineActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_Chart extends Fragment {

	@ViewInject(R.id.cc)
	private CombinedChart combinedChart;
	@ViewInject(R.id.cc2)
	private CombinedChart combinedChart2;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.iv_zoom_in_x)
	private ImageView iv_zoom_in_x;
	@ViewInject(R.id.iv_zoom_out_x)
	private ImageView iv_zoom_out_x;

	// 复权
	@ViewInject(R.id.rbn_complex_right)
	private RadioButton rbn_complex_right;
	// 设均线
	@ViewInject(R.id.ll_avg)
	private LinearLayout ll_avg;
	// MA
	@ViewInject(R.id.tv_ma_tag1)
	private TextView tv_ma_tag1;
	@ViewInject(R.id.tv_ma_tag2)
	private TextView tv_ma_tag2;
	@ViewInject(R.id.tv_ma_tag3)
	private TextView tv_ma_tag3;

	@ViewInject(R.id.tv_ma_param1)
	private TextView tv_ma_param1;
	@ViewInject(R.id.tv_ma_param2)
	private TextView tv_ma_param2;
	@ViewInject(R.id.tv_ma_param3)
	private TextView tv_ma_param3;

	// 指数
	@ViewInject(R.id.ll_index)
	private LinearLayout ll_index;
	@ViewInject(R.id.tv_index)
	private TextView tv_index;
	// index
	@ViewInject(R.id.tv_index_tag1)
	private TextView tv_index_tag1;
	@ViewInject(R.id.tv_index_tag2)
	private TextView tv_index_tag2;
	@ViewInject(R.id.tv_index_tag3)
	private TextView tv_index_tag3;

	@ViewInject(R.id.tv_index_param1)
	private TextView tv_index_param1;
	@ViewInject(R.id.tv_index_param2)
	private TextView tv_index_param2;
	@ViewInject(R.id.tv_index_param3)
	private TextView tv_index_param3;

	final int CHART_TIME = 0;
	final int CHART_KDAY = 1;
	final int CHART_KWEEK = 2;
	final int CHART_KMONTH = 3;
	int chartType;
	String symbol;
	String complexRight = null;

	ArrayList<HashMap<String, Object>> datas;
	ArrayList<HashMap<String, Object>> indexDatas;
	ArrayList<String> responses;
	private int showCount;

	// 以收盘价为均线
	List<Float> closeValues;
	// 设均线的三个值
	int MA_param1 = 5;
	int MA_param2 = 10;
	int MA_param3 = 20;
	// 设均线的三个曲线图是否显示
	boolean isMAParam1 = true;
	boolean isMAParam2 = true;
	boolean isMAParam3 = true;

	// 四个图的加载是否完成
	boolean isFinished = false;

	int type = 5;

	final int VOLUMN = 5;
	final int MACD = 0;
	final int KDJ = 1;
	final int RSI = 2;
	final int BIAS = 3;
	final int DMA = 4;

	String[] indexs = { "macd", "kdj", "rsi", "bias", "dma" };

	OnFinishChartListener mListener;

	public Fragment_Chart(int num, String symbol) {
		chartType = num;
		this.symbol = symbol;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFinishChartListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnFinishListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chart_time, null);
		ViewUtils.inject(this, v);
		init();
		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (chartType == data.getIntExtra("type", 1)) {
				MA_param1 = data.getIntExtra("MA_param1", 5);
				MA_param2 = data.getIntExtra("MA_param2", 10);
				MA_param3 = data.getIntExtra("MA_param3", 20);
				isMAParam1 = data.getBooleanExtra("isMAParam1", true);
				isMAParam2 = data.getBooleanExtra("isMAParam2", true);
				isMAParam3 = data.getBooleanExtra("isMAParam3", true);
				tv_ma_tag1.setText("MA" + MA_param1 + ":");
				tv_ma_tag2.setText("MA" + MA_param2 + ":");
				tv_ma_tag3.setText("MA" + MA_param3 + ":");
				showCombinedChart(combinedChart, datas, showCount);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		complexRight = "FORWARD";
		showCount = 60;
		datas = new ArrayList<HashMap<String, Object>>();
		indexDatas = new ArrayList<HashMap<String, Object>>();
		responses = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			responses.add("null");
		}
		if (chartType == CHART_TIME) {
			getTimeData();
		} else {
			getKData(chartType);
		}
		for (int i = 0; i < 5; i++) {
			getIndexData(i);
		}
	}

	@OnClick({ R.id.rbn_complex_right, R.id.iv_zoom_in_x, R.id.iv_zoom_out_x,
			R.id.tv_avgline })
	private void onClick(View v) {
		switch (v.getId()) {

		case R.id.rbn_complex_right:
			initMenu(getActivity(), rbn_complex_right);
			break;
		case R.id.iv_zoom_out_x:
			try {
				showCount *= 2;
				if (showCount > datas.size()) {
					showCount = datas.size();
				} else if (showCount > 300) {
					showCount = 300;
				}
				showCombinedChart(combinedChart, datas, showCount);
				showBarChart(combinedChart2, indexDatas, showCount, type);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.iv_zoom_in_x:
			try {
				showCount /= 2;
				if (showCount < 30) {
					showCount = 30;
				}
				showCombinedChart(combinedChart, datas, showCount);
				showBarChart(combinedChart2, indexDatas, showCount, type);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.tv_avgline:
			Intent intent = new Intent(getActivity(), SetAvgLineActivity.class);
			intent.putExtra("type", chartType);
			intent.putExtra("isMAParam1", isMAParam1);
			intent.putExtra("isMAParam2", isMAParam2);
			intent.putExtra("isMAParam3", isMAParam3);
			intent.putExtra("MA_param1", MA_param1);
			intent.putExtra("MA_param2", MA_param2);
			intent.putExtra("MA_param3", MA_param3);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(Context context, View viewGroup) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.item_complex_right_popup, null);

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
		popupWindow.showAsDropDown(viewGroup, 0, 0);
		// background.setAlpha(0.5f);

		RelativeLayout rl_forward_complex = (RelativeLayout) contentView
				.findViewById(R.id.rl_forward_complex);
		RelativeLayout rl_no_complex = (RelativeLayout) contentView
				.findViewById(R.id.rl_no_complex);
		RelativeLayout rl_backward_complex = (RelativeLayout) contentView
				.findViewById(R.id.rl_backward_complex);
		// RelativeLayout rl_gd_shopping_cart = (RelativeLayout) contentView
		// .findViewById(R.id.rl_gd_shopping_cart);
		rl_forward_complex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				complexRight = "FORWARD";
				rbn_complex_right.setText("前复权");
				getKData(chartType);
				popupWindow.dismiss();
			}
		});
		rl_no_complex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				complexRight = "NONE";
				rbn_complex_right.setText("不复权");
				getKData(chartType);
				popupWindow.dismiss();
			}
		});
		rl_backward_complex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				complexRight = "BACKWARD";
				rbn_complex_right.setText("后复权");
				getKData(chartType);
				popupWindow.dismiss();
			}
		});


		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// method stub
				rbn_complex_right.setChecked(false);
			}
		});
	}

	private void showBarChart(CombinedChart chart,
			ArrayList<HashMap<String, Object>> arrayList, int count, int tp) {

		chart.clear();

		if (arrayList.size() < count) {
			count = arrayList.size();
		}

		ArrayList<String> xVals = new ArrayList<String>();

		for (int i = 0; i < count; i++) {
			xVals.add("" + i);
		}

		CombinedData data = new CombinedData(xVals);

		chart.setDrawOrder(new DrawOrder[] { DrawOrder.BAR, DrawOrder.LINE });
		chart.setNoDataText("暂无数据");

		switch (tp) {
		case VOLUMN:
			tv_index.setText("成交量");
			tv_index_tag1.setVisibility(View.GONE);
			tv_index_tag2.setVisibility(View.GONE);
			tv_index_tag3.setVisibility(View.GONE);
			if (chartType != CHART_TIME) {
				float volumn = Float.parseFloat(arrayList.get(0).get("sum")
						.toString());
				tv_index_param1
.setText(String.format("%.1f", volumn / 1000000)
						+ "万");
			} else {
				float volumn = Float.parseFloat(arrayList.get(0).get("volume")
						.toString());
				tv_index_param1
.setText(String.format("%.1f", volumn / 1000000)
						+ "万");
			}
			tv_index_param2.setVisibility(View.GONE);
			tv_index_param3.setVisibility(View.GONE);

			data.setData(generateBarData(arrayList, count));
			break;
		case MACD: {
			tv_index.setText("MACD");
			tv_index_tag1.setVisibility(View.VISIBLE);
			tv_index_tag2.setVisibility(View.VISIBLE);
			tv_index_tag3.setVisibility(View.VISIBLE);
			tv_index_param1.setVisibility(View.VISIBLE);
			tv_index_param2.setVisibility(View.VISIBLE);
			tv_index_param3.setVisibility(View.VISIBLE);

			String[] indexsStrings = { "diff", "dea" };
			data.setData(generateMACDBarData(arrayList, count));
			data.setData(generateIndexLineData(arrayList, count, indexsStrings));

			tv_index_tag1.setText("DIF:");
			tv_index_tag2.setText("DEA:");
			tv_index_tag3.setText("M:");
			tv_index_param1.setText(arrayList.get(0).get(indexsStrings[0])
					.toString());
			tv_index_param2.setText(arrayList.get(0).get(indexsStrings[1])
					.toString());
			tv_index_param3.setText(arrayList.get(0).get("macd").toString());
			break;
		}
		case KDJ: {
			tv_index.setText("KDJ");
			tv_index_tag1.setVisibility(View.VISIBLE);
			tv_index_tag2.setVisibility(View.VISIBLE);
			tv_index_tag3.setVisibility(View.VISIBLE);
			tv_index_param1.setVisibility(View.VISIBLE);
			tv_index_param2.setVisibility(View.VISIBLE);
			tv_index_param3.setVisibility(View.VISIBLE);

			String[] indexsStrings = { "k", "d", "j" };
			data.setData(generateIndexLineData(arrayList, count, indexsStrings));

			tv_index_tag1.setText("K:");
			tv_index_tag2.setText("D:");
			tv_index_tag3.setText("J:");
			tv_index_param1.setText(arrayList.get(0).get(indexsStrings[0])
					.toString());
			tv_index_param2.setText(arrayList.get(0).get(indexsStrings[1])
					.toString());
			tv_index_param3.setText(arrayList.get(0).get(indexsStrings[2])
					.toString());
			break;
		}
		case RSI: {
			tv_index.setText("RSI");
			tv_index_tag1.setVisibility(View.VISIBLE);
			tv_index_tag2.setVisibility(View.VISIBLE);
			tv_index_tag3.setVisibility(View.VISIBLE);
			tv_index_param1.setVisibility(View.VISIBLE);
			tv_index_param2.setVisibility(View.VISIBLE);
			tv_index_param3.setVisibility(View.VISIBLE);

			String[] indexsStrings = { "rsi1", "rsi2", "rsi3" };
			data.setData(generateIndexLineData(arrayList, count, indexsStrings));

			tv_index_tag1.setText("RSI1:");
			tv_index_tag2.setText("RSI2:");
			tv_index_tag3.setText("RSI3:");
			tv_index_param1.setText(arrayList.get(0).get(indexsStrings[0])
					.toString());
			tv_index_param2.setText(arrayList.get(0).get(indexsStrings[1])
					.toString());
			tv_index_param3.setText(arrayList.get(0).get(indexsStrings[2])
					.toString());
			break;
		}
		case BIAS: {
			tv_index.setText("BIAS");
			tv_index_tag1.setVisibility(View.VISIBLE);
			tv_index_tag2.setVisibility(View.VISIBLE);
			tv_index_tag3.setVisibility(View.VISIBLE);
			tv_index_param1.setVisibility(View.VISIBLE);
			tv_index_param2.setVisibility(View.VISIBLE);
			tv_index_param3.setVisibility(View.VISIBLE);

			String[] indexsStrings = { "bias1", "bias2", "bias3" };
			data.setData(generateIndexLineData(arrayList, count, indexsStrings));

			tv_index_tag1.setText("BIAS1:");
			tv_index_tag2.setText("BIAS2:");
			tv_index_tag3.setText("BIAS3:");
			tv_index_param1.setText(arrayList.get(0).get(indexsStrings[0])
					.toString());
			tv_index_param2.setText(arrayList.get(0).get(indexsStrings[1])
					.toString());
			tv_index_param3.setText(arrayList.get(0).get(indexsStrings[2])
					.toString());
			break;
		}
		case DMA: {
			tv_index.setText("DMA");
			tv_index_tag1.setVisibility(View.VISIBLE);
			tv_index_tag2.setVisibility(View.VISIBLE);
			tv_index_tag3.setVisibility(View.VISIBLE);
			tv_index_param1.setVisibility(View.VISIBLE);
			tv_index_param2.setVisibility(View.VISIBLE);
			tv_index_param3.setVisibility(View.VISIBLE);

			String[] indexsStrings = { "dif", "ama" };
			data.setData(generateIndexLineData(arrayList, count, indexsStrings));

			tv_index_tag1.setText("DIF:");
			tv_index_tag2.setText("AMA:");
			tv_index_tag3.setVisibility(View.GONE);
			tv_index_param1.setText(arrayList.get(0).get(indexsStrings[0])
					.toString());
			tv_index_param2.setText(arrayList.get(0).get(indexsStrings[1])
					.toString());
			tv_index_param3.setVisibility(View.GONE);
			break;
		}
		default:
			break;
		}

		data.setDrawValues(false);

		chart.setDescription("");

		// if more than 60 entries are displayed in the chart, no values will be
		// drawn
		// barChart.setMaxVisibleValueCount(60);

		// scaling can now only be done on x- and y-axis separately
		chart.setPinchZoom(false);
		chart.setDoubleTapToZoomEnabled(false);
		chart.setDragEnabled(false);
		chart.setHighlightEnabled(false);

		// draw shadows for each bar that show the maximum value
		// barChart.setDrawBarShadow(true);

		// barChart.setDrawXLabels(false);

		chart.setDrawGridBackground(false);
		// barChart.setDrawYLabels(false);

		XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setDrawLabels(false);
		xAxis.setAxisLineWidth(0.5f);

		YAxis leftAxis = chart.getAxisLeft();
		// leftAxis.setLabelCount(8, false);
		// leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
		// leftAxis.setDrawLabels(false);
		leftAxis.setShowOnlyMinMax(true);
		leftAxis.setDrawGridLines(false);
		leftAxis.setAxisMaxValue(data.getYMax());
		leftAxis.setTextColor(Color.WHITE);
		leftAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
		leftAxis.setAxisLineWidth(0.5f);
		// leftAxis.setAxisMinValue(set1.getYMin());

		YAxis rightAxis = chart.getAxisRight();
		rightAxis.setDrawGridLines(false);
		rightAxis.setDrawLabels(false);
		rightAxis.setAxisLineWidth(0.5f);

		chart.getLegend().setEnabled(false);

		chart.setDrawBarShadow(false);
		chart.setData(data);
		chart.invalidate();

		if (chartType != CHART_TIME) {

			chart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					type++;
					if (type > 5) {
						type = 0;
					}

					if (type < 5) {
						ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
						try {
							JSONArray array = new JSONArray(responses.get(type));
							for (int i = 0; i < array.length(); i++) {
								HashMap<String, Object> data = new HashMap<String, Object>();
								Iterator<String> jsIterator;
								try {
									jsIterator = array.getJSONObject(i).keys();
									while (jsIterator.hasNext()) {
										String key = jsIterator.next();
										data.put(key, array.getJSONObject(i)
												.get(key).toString());
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								arrayList.add(data);
							}
							indexDatas = arrayList;
							showBarChart(combinedChart2, indexDatas, showCount,
									type);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						indexDatas = datas;
						showBarChart(combinedChart2, indexDatas, showCount,
								type);
					}
				}
			});
		}
	}

	private void showCombinedChart(CombinedChart combinedChart,
			ArrayList<HashMap<String, Object>> arrayList, int count) {

		float gridWidth = 0.3f;
		float axisWidth = 1.0f;

		if (arrayList.size() < count) {
			count = arrayList.size();
		}
		switch (chartType) {
		case CHART_TIME: {

			combinedChart.setDescription("");
			combinedChart.setNoDataText("暂无数据");
			combinedChart.setDrawGridBackground(false);
			combinedChart.getLegend().setEnabled(false);
			combinedChart.setDragEnabled(false);
			// combinedChart.setPinchZoom(false);
			combinedChart.setDoubleTapToZoomEnabled(false);

			// draw bars behind lines
			combinedChart.setDrawOrder(new DrawOrder[] { DrawOrder.CANDLE,
					DrawOrder.LINE });

			String[] dates = new String[count];
			for (int i = 0; i < count; i++) {
				dates[i] = arrayList.get(count - 1 - i).get("time")
						.toString();
			}

			CombinedData data = new CombinedData(dates);

			int[] colors = { Color.WHITE, Color.YELLOW };
			// int[] colors = { Color.WHITE };
			data.setData(generateLineData(arrayList, count, colors));

			XAxis xAxis = combinedChart.getXAxis();
			xAxis.setAxisLineColor(Color.WHITE);
			xAxis.setGridColor(Color.WHITE);
			xAxis.setPosition(XAxisPosition.BOTTOM);
			xAxis.setTextColor(Color.WHITE);
			xAxis.setAxisLineWidth(0.5f);
			xAxis.setGridLineWidth(gridWidth);
			xAxis.setLabelsToSkip(count / 6);
			xAxis.setTextSize(9.0f);
			xAxis.setAvoidFirstLastClipping(true);
			xAxis.setDrawLabels(false);

			YAxis yAxisLeft = combinedChart.getAxisLeft();
			yAxisLeft.setAxisLineColor(Color.WHITE);
			yAxisLeft.setGridColor(Color.WHITE);
			yAxisLeft.setLabelCount(3, false);
			// yAxisLeft.setDrawLabels(false);
			yAxisLeft.setPosition(YAxisLabelPosition.INSIDE_CHART);
			yAxisLeft.setAxisMinValue(data.getYMin(AxisDependency.LEFT));
			yAxisLeft.setAxisMaxValue(data.getYMax(AxisDependency.LEFT));
			// yAxisLeft.setAxisMinValue(data.getYMin(AxisDependency.LEFT) -
			// 0.2f);
			// yAxisLeft.setAxisMaxValue(data.getYMax(AxisDependency.LEFT) +
			// 0.2f);
			yAxisLeft.setStartAtZero(false);
			yAxisLeft.setTextColor(Color.WHITE);
			yAxisLeft.setAxisLineWidth(0.5f);
			yAxisLeft.setGridLineWidth(gridWidth);
			yAxisLeft.setTextSize(9.0f);

			YAxis yAxisRight = combinedChart.getAxisRight();
			// yAxisRight.setEnabled(false);
			yAxisRight.setAxisLineColor(Color.WHITE);
			yAxisRight.setDrawGridLines(false);
			// yAxisRight.setLabelCount(3, false);
			// yAxisRight.setPosition(YAxisLabelPosition.INSIDE_CHART);
			// yAxisRight.setAxisMinValue(data.getYMin(AxisDependency.RIGHT));
			// yAxisRight.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT));
			// yAxisRight.setStartAtZero(false);
			// yAxisRight.setTextColor(Color.WHITE);
			yAxisRight.setAxisLineWidth(gridWidth);
			yAxisRight.setDrawLabels(false);
			// yAxisRight.setTextSize(9.0f);

			combinedChart.setData(data);
			combinedChart.invalidate();
		}
			break;
		case CHART_KDAY: {

			combinedChart.setDescription("");
			combinedChart.setNoDataText("暂无数据");
			combinedChart.setDrawGridBackground(false);
			combinedChart.getLegend().setEnabled(false);
			// combinedChart.setDragEnabled(false);
			// combinedChart.setPinchZoom(false);
			// combinedChart.setScaleYEnabled(false);
			combinedChart.setDoubleTapToZoomEnabled(false);
			// combinedChart.setScaleMinima(1, 1);
			// draw bars behind lines
			combinedChart.setDrawOrder(new DrawOrder[] { DrawOrder.CANDLE,
					DrawOrder.LINE });

			String[] dates = new String[count];
			for (int i = 0; i < count; i++) {
				dates[i] = arrayList.get(count - 1 - i)
						.get("date").toString();
			}

			CombinedData data = new CombinedData(dates);
			CandleData candleData = generateCandleData(arrayList, count);
			data.setData(candleData);

			int[] colors = { Color.WHITE, Color.GREEN, Color.BLUE };
			LineData lineData = generateMAData(arrayList, count, colors);
			data.setData(lineData);

			YAxis rightAxis = combinedChart.getAxisRight();
			rightAxis.setDrawGridLines(true);
			rightAxis.setGridColor(Color.WHITE);
			rightAxis.setGridLineWidth(gridWidth);
			rightAxis.setTextColor(Color.WHITE);
			rightAxis.setAxisLineWidth(0.5f);
			rightAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
			rightAxis.setAxisMinValue(data.getYMin(AxisDependency.RIGHT));
			rightAxis.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT));
			rightAxis.setStartAtZero(false);
			rightAxis.setShowOnlyMinMax(true);

			YAxis leftAxis = combinedChart.getAxisLeft();
			leftAxis.setDrawGridLines(false);
			leftAxis.setDrawLabels(false);
			leftAxis.setAxisLineWidth(0.5f);
			leftAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);

			XAxis xAxis = combinedChart.getXAxis();
			xAxis.setAxisLineColor(Color.WHITE);
			xAxis.setPosition(XAxisPosition.BOTTOM);
			xAxis.setTextColor(Color.WHITE);
			xAxis.setAxisLineWidth(0.5f);
			xAxis.setDrawGridLines(false);
			xAxis.setAvoidFirstLastClipping(true);
			xAxis.setLabelsToSkip(count - 2);
			xAxis.setDrawLabels(false);

			combinedChart.setData(data);
			combinedChart.moveViewToX(280);
			combinedChart.invalidate();

		}
			break;
		case CHART_KWEEK: {
			tv_title.setText("周k");

			combinedChart.setDescription("");
			combinedChart.setNoDataText("暂无数据");
			combinedChart.setDrawGridBackground(false);
			combinedChart.getLegend().setEnabled(false);
			// combinedChart.setDragEnabled(false);
			// combinedChart.setPinchZoom(false);
			// combinedChart.setScaleYEnabled(false);
			combinedChart.setDoubleTapToZoomEnabled(false);
			// combinedChart.setScaleMinima(1, 1);
			// draw bars behind lines
			combinedChart.setDrawOrder(new DrawOrder[] { DrawOrder.CANDLE,
					DrawOrder.LINE });

			String[] dates = new String[count];
			for (int i = 0; i < count; i++) {
				dates[i] = arrayList.get(arrayList.size() - count + i)
						.get("date").toString();
			}

			CombinedData data = new CombinedData(dates);
			CandleData candleData = generateCandleData(arrayList, count);
			data.setData(candleData);

			int[] colors = { Color.WHITE, Color.GREEN, Color.BLUE };
			LineData lineData = generateMAData(arrayList, count, colors);
			data.setData(lineData);

			YAxis rightAxis = combinedChart.getAxisRight();
			rightAxis.setDrawGridLines(true);
			rightAxis.setGridColor(Color.WHITE);
			rightAxis.setGridLineWidth(gridWidth);
			rightAxis.setTextColor(Color.WHITE);
			rightAxis.setAxisLineWidth(0.5f);
			rightAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
			rightAxis.setAxisMinValue(data.getYMin(AxisDependency.RIGHT));
			rightAxis.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT));
			rightAxis.setStartAtZero(false);
			rightAxis.setShowOnlyMinMax(true);

			YAxis leftAxis = combinedChart.getAxisLeft();
			leftAxis.setDrawGridLines(false);
			leftAxis.setDrawLabels(false);
			leftAxis.setAxisLineWidth(0.5f);
			leftAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);

			XAxis xAxis = combinedChart.getXAxis();
			xAxis.setAxisLineColor(Color.WHITE);
			xAxis.setPosition(XAxisPosition.BOTTOM);
			xAxis.setTextColor(Color.WHITE);
			xAxis.setAxisLineWidth(0.5f);
			xAxis.setDrawGridLines(false);
			xAxis.setAvoidFirstLastClipping(true);
			xAxis.setLabelsToSkip(count - 2);
			xAxis.setDrawLabels(false);

			combinedChart.setData(data);
			combinedChart.moveViewToX(280);
			combinedChart.invalidate();

		}
			break;
		case CHART_KMONTH: {
			tv_title.setText("月k");

			combinedChart.setDescription("");
			combinedChart.setNoDataText("暂无数据");
			combinedChart.setDrawGridBackground(false);
			combinedChart.getLegend().setEnabled(false);
			// combinedChart.setDragEnabled(false);
			// combinedChart.setPinchZoom(false);
			// combinedChart.setScaleYEnabled(false);
			combinedChart.setDoubleTapToZoomEnabled(false);
			// combinedChart.setScaleMinima(1, 1);
			// draw bars behind lines
			combinedChart.setDrawOrder(new DrawOrder[] { DrawOrder.CANDLE,
					DrawOrder.LINE });

			String[] dates = new String[count];
			for (int i = 0; i < count; i++) {
				dates[i] = arrayList.get(arrayList.size() - count + i)
						.get("date").toString();
			}

			CombinedData data = new CombinedData(dates);
			CandleData candleData = generateCandleData(arrayList, count);
			data.setData(candleData);

			int[] colors = { Color.WHITE, Color.GREEN, Color.BLUE };
			LineData lineData = generateMAData(arrayList, count, colors);
			data.setData(lineData);

			YAxis rightAxis = combinedChart.getAxisRight();
			rightAxis.setDrawGridLines(true);
			rightAxis.setGridColor(Color.WHITE);
			rightAxis.setGridLineWidth(gridWidth);
			rightAxis.setTextColor(Color.WHITE);
			rightAxis.setAxisLineWidth(0.5f);
			rightAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);
			rightAxis.setAxisMinValue(data.getYMin(AxisDependency.RIGHT));
			rightAxis.setAxisMaxValue(data.getYMax(AxisDependency.RIGHT));
			rightAxis.setStartAtZero(false);
			rightAxis.setShowOnlyMinMax(true);

			YAxis leftAxis = combinedChart.getAxisLeft();
			leftAxis.setDrawGridLines(false);
			leftAxis.setDrawLabels(false);
			leftAxis.setAxisLineWidth(0.5f);
			leftAxis.setPosition(YAxisLabelPosition.INSIDE_CHART);

			XAxis xAxis = combinedChart.getXAxis();
			xAxis.setAxisLineColor(Color.WHITE);
			xAxis.setPosition(XAxisPosition.BOTTOM);
			xAxis.setTextColor(Color.WHITE);
			xAxis.setAxisLineWidth(0.5f);
			xAxis.setDrawGridLines(false);
			xAxis.setAvoidFirstLastClipping(true);
			// xAxis.setLabelsToSkip(count / 6);
			xAxis.setDrawLabels(false);

			combinedChart.setData(data);
			combinedChart.moveViewToX(280);
			combinedChart.invalidate();

		}
			break;

		default:
			break;
		}

	}

	// 分时图
	private LineData generateLineData(
			ArrayList<HashMap<String, Object>> arrayList, int dataCount,
			int[] colors) {

		LineData d = new LineData();

		for (int i = 0; i < colors.length; i++) {

			ArrayList<Entry> entries = new ArrayList<Entry>();

			if (i == 0) {
				for (int index = 0; index < dataCount; index++) {
					float val = Float
							.parseFloat(arrayList.get(dataCount - 1 - index)
									.get("price").toString());
					entries.add(new Entry(val, index));
				}
				LineDataSet dataSet = new LineDataSet(entries, null);
				dataSet.setAxisDependency(AxisDependency.LEFT);
				dataSet.setColor(colors[i]);
				dataSet.setDrawCircles(false);
				dataSet.setLineWidth(0.5f);
				dataSet.setDrawCircles(false);
				// dataSet.setDrawCubic(true);
				// dataSet.setCubicIntensity(0.1f);
				dataSet.setDrawValues(false);
				d.addDataSet(dataSet);
			} else {
				for (int index = 0; index < dataCount; index++) {
					float amount = Float.parseFloat(arrayList
							.get(dataCount - 1 - index).get("amount")
							.toString());
					float volume = Float.parseFloat(arrayList
							.get(dataCount - 1 - index).get("volume")
							.toString());
					float val = amount / volume;
					entries.add(new Entry(val, index));
				}
				LineDataSet dataSet = new LineDataSet(entries, null);
				dataSet.setAxisDependency(AxisDependency.LEFT);
				dataSet.setColor(colors[i]);
				dataSet.setDrawCircles(false);
				dataSet.setLineWidth(0.5f);
				dataSet.setDrawCubic(true);
				dataSet.setCubicIntensity(0.1f);
				dataSet.setDrawValues(false);
				d.addDataSet(dataSet);
			}
		}

		return d;
	}

	protected CandleData generateCandleData(
			ArrayList<HashMap<String, Object>> arrayList, int count) {

		CandleData d = new CandleData();

		ArrayList<CandleEntry> entries = new ArrayList<CandleEntry>();
		int[] colors = new int[count];

		for (int index = 0; index < count; index++) {
			float shadowH = Float.parseFloat(arrayList.get(count - 1 - index)
					.get("max").toString());
			float shadowL = Float.parseFloat(arrayList.get(count - 1 - index)
					.get("min").toString());
			float open = Float.parseFloat(arrayList.get(count - 1 - index)
					.get("open").toString());
			float close = Float.parseFloat(arrayList.get(count - 1 - index)
					.get("close").toString());
			entries.add(new CandleEntry(index, shadowH, shadowL, open, close));
			if (close >= open) {
				colors[index] = Color.RED;
			} else {
				colors[index] = Color.GREEN;
			}
		}

		CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
		set.setAxisDependency(AxisDependency.RIGHT);
		set.setColors(colors);
		// set.setDecreasingColor(Color.GREEN);
		// set.setIncreasingColor(Color.RED);
		set.setShadowColorSameAsCandle(true);
		set.setDecreasingPaintStyle(Style.FILL);
		set.setIncreasingPaintStyle(Style.FILL);
		set.setDrawValues(false);
		d.addDataSet(set);

		return d;
	}

	// 设均线
	private LineData generateMAData(
			ArrayList<HashMap<String, Object>> arrayList, int dataCount,
			int[] colors) {

		LineData d = new LineData();
		int MA_num = 0;
		boolean isShowed = true;
		List<Float> closeValues = new LinkedList<Float>();
		for (int i = 0; i < colors.length; i++) {

			switch (i) {
			case 0:
				MA_num = MA_param1;
				isShowed = isMAParam1;
				break;
			case 1:
				MA_num = MA_param2;
				isShowed = isMAParam2;
				break;
			case 2:
				MA_num = MA_param3;
				isShowed = isMAParam3;
				break;
			default:
				break;
			}

			if (!isShowed) {
				continue;
			}

			closeValues = calAvgValues(arrayList, MA_num);

			// 显示最后一个
			switch (i) {
			case 0:
				tv_ma_param1.setText(String.format("%.2f",
						closeValues.get(closeValues.size() - 1)));
				break;
			case 1:
				tv_ma_param2.setText(String.format("%.2f",
						closeValues.get(closeValues.size() - 1)));
				break;
			case 2:
				tv_ma_param3.setText(String.format("%.2f",
						closeValues.get(closeValues.size() - 1)));
				break;
			default:
				break;
			}

			ArrayList<Entry> entries = new ArrayList<Entry>();

			if (dataCount < closeValues.size()) {
					for (int index = 0; index < dataCount; index++) {

					if (index < closeValues.size()) {
						entries.add(new Entry(closeValues.get(closeValues
									.size() - dataCount + index),
									index));
						}
					}
				} else {
					for (int index = 0; index < dataCount; index++) {

					// 前n个不显示
					if (index >= MA_num && index < closeValues.size() + MA_num) {
						entries.add(new Entry(closeValues.get(index - MA_num),
									index));
						}
					}
				}
				LineDataSet dataSet = new LineDataSet(entries, null);
				dataSet.setAxisDependency(AxisDependency.RIGHT);
				dataSet.setColor(colors[i]);
				dataSet.setDrawCircles(false);
				dataSet.setLineWidth(0.5f);
				dataSet.setDrawCircles(false);
				// dataSet.setDrawCubic(true);
				// dataSet.setCubicIntensity(0.1f);
				dataSet.setDrawValues(false);
				d.addDataSet(dataSet);
		}

		return d;
	}

	private BarData generateBarData(
			ArrayList<HashMap<String, Object>> arrayList, int count) {
		BarData data = new BarData();
		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		int[] colors = new int[count];
		if (chartType != CHART_TIME) {
			// 获取成交量
			for (int i = 0; i < count; i++) {
				float val = Float.parseFloat(arrayList.get(count - 1 - i)
						.get("sum")
						.toString());
				yVals1.add(new BarEntry(val, i));
				float open = Float.parseFloat(arrayList.get(count - 1 - i)
						.get("open")
						.toString());
				float close = Float.parseFloat(arrayList.get(count - 1 - i)
						.get("close")
						.toString());
				if (close >= open) {
					colors[i] = Color.RED;
				} else {
					colors[i] = Color.GREEN;
				}
			}
		} else {
			// 获取成交量
			for (int i = 0; i < count; i++) {
				float val;
				if (i == 0) {
					val = 0;
					colors[i] = Color.RED;
				} else {
					float val1 = Float.parseFloat(arrayList.get(count - 1 - i)
							.get("volume").toString());
					float val0 = Float.parseFloat(arrayList.get(count - i)
							.get("volume").toString());
					val = val1 - val0;

					float price1 = Float.parseFloat(arrayList
							.get(count - 1 - i).get("price").toString());
					float price0 = Float.parseFloat(arrayList.get(count - i)
							.get("price").toString());
					if (price1 >= price0) {
						colors[i] = Color.RED;
					} else {
						colors[i] = Color.GREEN;
					}
				}
				yVals1.add(new BarEntry(val, i));

			}
		}
		BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
		set1.setColors(colors);

		data.addDataSet(set1);

		return data;
	}

	private BarData generateMACDBarData(
			ArrayList<HashMap<String, Object>> arrayList, int count) {
		BarData data = new BarData();
		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		int[] colors = new int[count];

		for (int i = 0; i < count; i++) {
			float val = Float.parseFloat(arrayList.get(count - 1 - i)
					.get("macd").toString());
			yVals1.add(new BarEntry(val, i));
			if (val >= 0) {
				colors[i] = Color.RED;
			} else {
				colors[i] = Color.GREEN;
			}
		}

		BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
		set1.setColors(colors);
		set1.setAxisDependency(AxisDependency.RIGHT);
		data.addDataSet(set1);

		return data;
	}

	private LineData generateIndexLineData(
			ArrayList<HashMap<String, Object>> arrayList, int dataCount,
			String[] indexsStrings) {

		LineData d = new LineData();

		for (int i = 0; i < indexsStrings.length; i++) {

			ArrayList<Entry> entries = new ArrayList<Entry>();
			for (int index = 0; index < dataCount; index++) {
				float val = Float.parseFloat(arrayList
						.get(dataCount - 1 - index).get(indexsStrings[i])
						.toString());
				entries.add(new Entry(val, index));
			}
			LineDataSet dataSet = new LineDataSet(entries, null);
			dataSet.setAxisDependency(AxisDependency.LEFT);
			switch (i) {
			case 0:
				dataSet.setColor(Color.WHITE);
				break;
			case 1:
				dataSet.setColor(Color.YELLOW);
				break;
			case 2:
				dataSet.setColor(Color.BLUE);
				break;
			}
			dataSet.setDrawCircles(false);
			dataSet.setLineWidth(0.5f);
			dataSet.setDrawCircles(false);
			// dataSet.setDrawCubic(true);
			// dataSet.setCubicIntensity(0.1f);
			dataSet.setDrawValues(false);
			d.addDataSet(dataSet);
		}

		return d;
	}

	void getKData(int type) {
		String typeString = null;
		switch (type) {
		case CHART_KDAY:
			typeString = "DAY";
			tv_title.setText("日k");
			break;
		case CHART_KWEEK:
			tv_title.setText("月k");
			typeString = "WEEK";
			break;
		case CHART_KMONTH:
			tv_title.setText("年k");
			typeString = "MONTH";
			break;
		default:
			break;
		}

		String url = AppConfig.URL_QUOTATION + "candlestick.json?"
				+ RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN)
				+ "&securityType=STOCK" + "&symbol=" + symbol + "&type="
				+ typeString + "&adjust=" + complexRight;

		Log.d("getKData", "url:" + url);

		JSONObject params = new JSONObject();

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mListener.onFinishChart(chartType);
						Log.d("response", "response" + response);

						responses.set(VOLUMN, response);

						try {
							JSONArray array = new JSONArray(response);
							for (int i = 0; i < array.length(); i++) {
								String[] strings = array.get(i).toString()
										.split(" ");
								HashMap<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("date", strings[0]);
								hashMap.put("symbol", strings[1]);
								hashMap.put("open", strings[2]);
								hashMap.put("close", strings[3]);
								hashMap.put("max", strings[4]);
								hashMap.put("min", strings[5]);
								hashMap.put("sum", strings[6]);
								hashMap.put("money", strings[7]);
								hashMap.put("avg", strings[8]);
								datas.add(hashMap);
							}
							showCombinedChart(combinedChart, datas, showCount);
							indexDatas = datas;
							showBarChart(combinedChart2, indexDatas, showCount,
									VOLUMN);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mListener.onFinishChart(chartType);
						Log.d("error", error.toString());
					}
				});

		stringRequest.setTag("getKData");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 获取分时图数据
	void getTimeData() {

		tv_title.setText("分时");
		ll_index.setVisibility(View.GONE);
		// 分时图没有复权和设均线
		rbn_complex_right.setVisibility(View.GONE);
		ll_avg.setVisibility(View.GONE);

		String url = AppConfig.URL_QUOTATION
				+ "real-time.json?securityType=STOCK&symbol=" + symbol;

		Log.d("getTimeData", "url:" + url);

		JSONObject params = new JSONObject();

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						mListener.onFinishChart(chartType);
						Log.d("response", "response" + response);
						try {
							JSONObject object = new JSONObject(response);
							JSONArray array = object.getJSONArray("quotations");
							for (int i = 0; i < array.length(); i++) {
								HashMap<String, Object> data = new HashMap<String, Object>();
								Iterator<String> jsIterator;
								try {
									jsIterator = array.getJSONObject(i).keys();
									while (jsIterator.hasNext()) {
										String key = jsIterator.next();
										data.put(key, array.getJSONObject(i)
												.get(key).toString());
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								datas.add(data);
							}
							showCombinedChart(combinedChart, datas,
									datas.size());
							indexDatas = datas;
							showBarChart(combinedChart2, indexDatas,
									datas.size(),
									VOLUMN);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mListener.onFinishChart(chartType);
						Log.d("error", error.toString());
					}
				});

		stringRequest.setTag("getTimeData");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 获取指数数据
	void getIndexData(final int position) {

		if (position >= 5) {
			return;
		}

		String typeString = null;
		switch (chartType) {
		case CHART_KDAY:
			typeString = "DAY";
			break;
		case CHART_KWEEK:
			typeString = "WEEK";
			break;
		case CHART_KMONTH:
			typeString = "MONTH";
			break;
		default:
			return;
		}

		String url = AppConfig.URL_QUOTATION + "index/" + indexs[position]
				+ "/" + symbol
				+ ".json?adjustType=" + complexRight
				+ "&stockIndex=STOCK&periodType=" + typeString;

		Log.d("getMACDData", "url:" + url);

		JSONObject params = new JSONObject();

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("getMACDData", "response" + response);

						responses.set(position, response);

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// mListener.onFinishChart(chartType);
						Log.d("error", error.toString());
					}
				});

		stringRequest.setTag("getTimeData");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 计算dayNum日的均值线数据
	 * 
	 * @author 袁展智 on 2015年11月13日
	 * @param values
	 *            :closePrice
	 * @param dayNum
	 * @return
	 */
	private List<Float> calAvgValues(
			ArrayList<HashMap<String, Object>> arrayList, int dayNum) {
		// 防止index越界
		if (arrayList.size() < dayNum)
			return null;

		// 构造收盘价list
		LinkedList<Float> values = new LinkedList<Float>();
		for (int index = 0; index < arrayList.size(); index++) {
			float val = Float.parseFloat(arrayList
					.get(arrayList.size() - 1 - index)
					.get("close").toString());
			// Log.d("val", "" + val);
			values.add(val);
		}

		List<Float> result = new LinkedList<Float>();// 因为result要经常插入,所以用LinkedList比ArrayList要效率高
		// 计算前dayNum个值.
		float sum = values.get(0);
		for (int i = 1; i < dayNum; i++) {
			// result.add(0.0f);
			sum += values.get(i);
		}
		result.add(sum / dayNum);

		// 算后面的值
		for (int i = dayNum; i < values.size(); i++) {
			sum += values.get(i) - values.get(i - dayNum);
			// Log.d("val", "" + sum + " " + dayNum + " " + sum / dayNum);
			result.add(sum / dayNum);
		}

		return result;
	}

	public interface OnFinishChartListener {
		public void onFinishChart(int chartType);
	}
}
