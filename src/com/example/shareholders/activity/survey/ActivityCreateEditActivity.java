package com.example.shareholders.activity.survey;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.db.entity.EnterpriseEntity;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.view.ActionSheetDialog;
import com.gghl.view.wheelview.JudgeDate;
import com.gghl.view.wheelview.ScreenInfo;
import com.gghl.view.wheelview.WheelMain;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@ContentView(R.layout.activity_activitycreate2_edit)
public class ActivityCreateEditActivity extends Activity {
	@ViewInject(R.id.iv_ac_return)
	private ImageView iv_return;
	@ViewInject(R.id.et_name)
	private EditText et_name;
	@ViewInject(R.id.et_city)
	private EditText et_city;
	@ViewInject(R.id.et_post)
	private EditText et_post;
	@ViewInject(R.id.tv_time_before)
	private TextView tv_time_before;
	@ViewInject(R.id.tv_time_after)
	private TextView tv_time_after;
	@ViewInject(R.id.et_outline)
	private EditText et_outline;
	@ViewInject(R.id.tv_delete)
	private TextView tv_delete;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	WheelMain wheelMain;
	View timepickerview;
	int flag;

	// 公司信息
	private String symbol;
	private String industryCode;
	private String locationCode;
	private String locationName;
	private String shortName;
	private String receicerpost;
	private String beginDate;
	private String endDate;
	private String content;
	private String uuid;
	private String type;
	// 创建调研
	private int CREATE_SURVEY = 0;
	// 编辑调研
	private int EDIT_SURVEY = 1;
	// 删除调研
	private int DELETE_SURVEY = 2;
	// 数据库
	private DbUtils dbUtils;
	// 调研公司当前位置，如果是新建则为-1
	private String position;
	// 创建公司
	private int ENTERPRISE = 0;
	// 创建或者修改地址
	private int LOCATION = 1;
	// 时间比较器
	private DateComparator dateComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		dateComparator = new DateComparator();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		// 位置为-1，则为创建活动，否则就是编辑活动
		position = bundle.getString("position");
		dbUtils = DbUtils.create(this);
		// 是否出现删除按钮
		// 如果是新建调研公司
		if (position.equals("-1")) {
			tv_delete.setVisibility(View.GONE);
			tv_time_before.setText(dateFormat.format(new Date()).toString());
			tv_time_after.setText(dateFormat.format(new Date()).toString());
		}
		// 如果是编辑调研公司
		else {
			tv_delete.setVisibility(View.VISIBLE);
			shortName = bundle.getString("shortName");
			locationName = bundle.getString("locationName");
			receicerpost = bundle.getString("receicerpost");
			beginDate = bundle.getString("beginDate");
			endDate = bundle.getString("endDate");
			content = bundle.getString("content");
			symbol = bundle.getString("symbol");
			locationCode = bundle.getString("locationCode");
			industryCode = bundle.getString("industryCode");
			try {
				uuid = bundle.getString("uuid");
				Log.d("uuid", uuid);
			} catch (Exception e) {

			}
			et_name.setText(shortName);
			et_city.setText(locationName);
			et_post.setText(receicerpost);
			et_outline.setText(content);
			tv_time_before.setText(beginDate);
			tv_time_after.setText(endDate);

		}
	}

	@OnClick({ R.id.iv_ac_return, R.id.et_name, R.id.et_city,
			R.id.tv_time_before, R.id.tv_time_after, R.id.commit, R.id.finish,
			R.id.tv_delete })
	private void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.iv_ac_return:
			finish();
			break;
		case R.id.et_name:
			// 创建活动的时候进去公司选择界面
			if (position.equals("-1")) {
				intent.setClass(getApplicationContext(),
						ActivityCreateCompanyBrief.class);
				intent.putExtra("type", 222);
				startActivityForResult(intent, ENTERPRISE);
			}
			// 如果是编辑活动，不能更改公司
			else {
				Toast.makeText(getApplicationContext(), "公司名称不能更改！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.et_city:
			// 点击选择城市
			intent.setClass(getApplicationContext(), SearchSurveyActivity.class);
			if (locationCode != null) {
				bundle.putString("locationName", et_city.getText().toString()
						.trim());
				bundle.putString("locationCode", locationCode);
			}
			intent.putExtras(bundle);
			startActivityForResult(intent, LOCATION);
			break;
		case R.id.tv_time_before:
			setWheelMain(tv_time_before);
			new ActionSheetDialog(this, wheelMain, tv_time_before,tv_time_after,
					"choose_time").builder().setTitle("请选择日期")
					.setCancelable(true).setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,1).show();
			
			
			/**/

			break;
		case R.id.tv_time_after:
			setWheelMain(tv_time_after);
			new ActionSheetDialog(this, wheelMain, tv_time_before,tv_time_after, "choose_time")
					.builder().setTitle("请选择日期").setCancelable(true)
					.setCanceledOnTouchOutside(true)
					.setMyContentView(timepickerview,2).show();
			String start_time2=tv_time_before.getText().toString();
			String end_time2=ActionSheetDialog.end_time;
			
			break;
		case R.id.commit:
			// 内容不能为空
			if (et_outline.getText().toString().trim().equals("")
					|| et_name.getText().toString().trim().equals("")
					|| et_city.getText().toString().trim().equals("")) {
				InternetDialog internetDialog = new InternetDialog(
						ActivityCreateEditActivity.this);
				internetDialog.showInternetDialog("内容不能为空", false);
			}
			// 起始时间不能大于结束时间
			else if (dateComparator.compare(
					transformTimeFormat(tv_time_after.getText().toString())
							.trim(), transformTimeFormat(tv_time_before
							.getText().toString().trim())) > 0) {
				InternetDialog internetDialog = new InternetDialog(
						ActivityCreateEditActivity.this);
				internetDialog.showInternetDialog("起始时间不能大于结束时间", false);
			} else {
				// 公司所在hashmap的位置
				bundle.putString("position", position);
				// 股票代码，数据库id
				bundle.putString("symbol", symbol);
				// 地点
				bundle.putString("locationName", et_city.getText().toString()
						.trim());
				// 地点代码
				bundle.putString("locationCode", locationCode);
				// 被调研人职务
				bundle.putString("receicerpost", et_post.getText().toString()
						.trim());
				// 行业代码
				bundle.putString("industryCode", industryCode.trim());
				// 开始时间
				bundle.putString("beginDate",
						transformTimeFormat(tv_time_before.getText().toString()
								.trim()));
				// 结束时间
				bundle.putString("endDate",
						transformTimeFormat(tv_time_after.getText().toString())
								.trim());
				// 具体内容
				bundle.putString("content", et_outline.getText().toString()
						.trim());
				// 企业简称
				bundle.putString("shortName", et_name.getText().toString()
						.trim());
				// 公司类型
				bundle.putString("type", type);
				// uuid
				bundle.putString("uuid", uuid);
				intent.putExtras(bundle);
				EnterpriseEntity enterprise = null;
				// 如果是创建公司
				if (position.equals("-1")) {
					enterprise = new EnterpriseEntity();
					// 修改或者创建调研公司
					if (enterprise != null) {

						// 起始时间
						enterprise
								.setBeginDate(transformTimeFormat(tv_time_before
										.getText().toString().trim()));
						// 主要内容
						enterprise.setContent(et_outline.getText().toString()
								.trim());
						// 结束时间
						enterprise.setEndDate(transformTimeFormat(
								tv_time_after.getText().toString()).trim());
						// 行业代码
						enterprise.setIndustryCode(industryCode.trim());
						// 公司类型
						enterprise.setType(type);
						// 地点代码
						enterprise.setLocationCode(locationCode);
						// 城市
						enterprise.setLocationName(et_city.getText().toString()
								.trim());
						// 被调研人职务
						enterprise
								.setReceicerpost(et_post.getText().toString());
						// 企业简称
						enterprise.setShortName(et_name.getText().toString()
								.trim());
						// 股票代码
						enterprise.setSymbol(symbol);

						try {
							dbUtils.save(enterprise);
							setResult(CREATE_SURVEY, intent);
							finish();
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							InternetDialog internetDialog = new InternetDialog(
									ActivityCreateEditActivity.this);
							internetDialog.showInternetDialog(
									"不能增加已存在的公司或已经删除的公司", false);
						}
					}

				}
				// 如果是编辑公司
				else {
					try {
						enterprise = dbUtils.findById(EnterpriseEntity.class,
								symbol);
						// 起始时间
						enterprise
								.setBeginDate(transformTimeFormat(tv_time_before
										.getText().toString().trim()));
						// 主要内容
						enterprise.setContent(et_outline.getText().toString()
								.trim());
						// 结束时间
						enterprise.setEndDate(transformTimeFormat(
								tv_time_after.getText().toString()).trim());
						// 行业代码
						enterprise.setIndustryCode(industryCode.trim());
						// 地点代码
						enterprise.setLocationCode(locationCode);
						// 公司类型
						enterprise.setType(type);
						// 城市
						enterprise.setLocationName(et_city.getText().toString()
								.trim());
						// 被调研人职务
						enterprise
								.setReceicerpost(et_post.getText().toString());
						// 企业简称
						enterprise.setShortName(et_name.getText().toString());
						// uuid
						enterprise.setUuid(uuid);
						dbUtils.update(enterprise);
						setResult(EDIT_SURVEY, intent);
						finish();
						// dbUtils.update(enterprise, updateColumnNames)
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "", 0).show();
					}

				}

			}

			break;
		// 取消
		case R.id.finish:
			finish();
			break;
		case R.id.tv_delete:
			// 如果是编辑活动
			if (!position.equals("-1")) {
				// 新建活动
				EnterpriseEntity enterpriseEntity = new EnterpriseEntity();
				try {
					enterpriseEntity = dbUtils.findById(EnterpriseEntity.class,
							symbol);
					// 如果是新建调研的编辑，就直接在数据库删除了
					if (uuid == null) {
						dbUtils.delete(enterpriseEntity);
					}
					// 如果是编辑调研的编辑，就set deleted字段为true
					else {
						enterpriseEntity.setDeleted("true");
						dbUtils.update(enterpriseEntity);
					}
					// 调研公司当前的位置
					bundle.putString("position", position);
					intent.putExtras(bundle);
					setResult(DELETE_SURVEY, intent);
					finish();
				} catch (DbException e) {
					// TODO Auto-generated catch block
					InternetDialog internetDialog = new InternetDialog(
							ActivityCreateEditActivity.this);
					internetDialog.showInternetDialog("删除失败", false);
					e.printStackTrace();
				}
			}
			// 新建活动
			else {
				finish();
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// 创建调研公司
		if (requestCode == ENTERPRISE) {
			// 根据所选调研公司返回的调研名称，调研城市，股票代码，行业代码，以及地点代码
			try {
				et_name.setText(data.getExtras().getString("shortName"));
				et_city.setText(data.getExtras().getString("locationName"));
				locationName = data.getExtras().getString("locationName");
				symbol = data.getExtras().getString("symbol");
				industryCode = data.getExtras().getString("industryCode");
				locationCode = data.getExtras().getString("locationCode");
				type = data.getExtras().getString("companyType");
				Log.d("CreatelocationCode", locationCode);
			}

			catch (Exception e) {
				Log.d("Exception", e.toString());
			}

		} else if (requestCode == LOCATION) {
			try {
				locationCode = data.getExtras().getString("locationCode");
				locationName = data.getExtras().getString("locationName");
				et_city.setText(data.getExtras().getString("locationName"));
			} catch (Exception e) {

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

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

	/**
	 * 点击空白处，键盘消失
	 */

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
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
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