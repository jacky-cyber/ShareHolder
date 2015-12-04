package com.example.shareholders.fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.ActivityCreateActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.EnterpriseEntity;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.DateComparator;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.example.shareholders.view.DialogManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Fragment_ActivityCreate3 extends Fragment {

	@ViewInject(R.id.tv_compeleted)
	private TextView tv_compeleted;
	private AlertDialog myDialog = null;
	RequestQueue volleyRequestQueue;
	@ViewInject(R.id.et_contact)
	private EditText et_contact;
	@ViewInject(R.id.et_tel)
	private EditText et_tel;
	@ViewInject(R.id.et_other_statement)
	private EditText et_other_statement;
	private DbUtils dbUtils;
	private DialogManager dialogManager;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_activitycreate3, container,
				false);
	
		ViewUtils.inject(this, v);
		et_tel.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		// 1.创建请求队列
		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		dbUtils = DbUtils.create(getActivity());
		// dialogManager = new DialogManager(getActivity());
		dialogManager = DialogManager.getInstance(getActivity());
		return v;
	}

	@OnClick(R.id.tv_compeleted)
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_compeleted:
			if (!BtnClickUtils.isFastDoubleClick()) {
				// 判断联系人和电话号码是否为空
				if (et_contact.getText().toString().trim().equals("")
						|| et_tel.getText().toString().trim().equals("")) {

					InternetDialog internetDialog = new InternetDialog(
							getActivity());
					internetDialog.showInternetDialog(getActivity()
							.getResources()
							.getString(R.string.have_to_fill_tip), false);

				}
				// 判断电话号码是否正确
				else if (!DateComparator.isPhoneNumberValid(et_tel.getText()
						.toString().trim())) {
					// Toast.makeText(getActivity(), "请输入正确的手机号", 0).show();
					InternetDialog internetDialog = new InternetDialog(
							getActivity());
					internetDialog.showInternetDialog("请输入正确的手机号", false);
				} else {
					// 点击创建或者编辑调研活动
					CreateSurvey(ActivityCreateActivity.uuid != null ? ActivityCreateActivity.uuid
							: null);
				}
				break;
			}
		}
	}

	/*
	 * 功能：3秒后对话框退出,标志位为0x123
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (myDialog.isShowing()) {
					myDialog.dismiss();
					getActivity().finish();
				}
			}
		};
	};

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 如果是编辑活动 m
		if (ActivityCreateActivity.sign != 0) {
			// 联系人
			et_contact
					.setText(RsSharedUtil.getString(getActivity(), "contact"));
			// 联系电话
			et_tel.setText(RsSharedUtil
					.getString(getActivity(), "contactPhone"));
			// 其他说明
			et_other_statement.setText(RsSharedUtil.getString(getActivity(),
					"noted"));
		}
		super.onActivityCreated(savedInstanceState);

	}

	/**
	 * 设定定时器
	 */
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getActivity().finish();
		}
	};

	/**
	 * 创建或者编辑调研活动
	 */
	public void CreateSurvey(final String uuid) {

		String beginDate = null;
		String endDate = null;
		String mark = RsSharedUtil.getString(getActivity(), "access_token");

		String url = AppConfig.URL_SURVEY + "add.json?" + "access_token="
				+ mark;
		Log.d("url", url);
		// 创建调研
		JSONObject survey = new JSONObject();
		// 调研名称以及UUID
		// 创建调研加调研活动后缀，否则不加
		try {
			if (uuid != null) {

				Log.d("uuid", uuid);
				survey.put("uuid", uuid);
				survey.put("surveyName",
						RsSharedUtil.getString(getActivity(), "surveyName"));
			} else
				survey.put("surveyName",
						RsSharedUtil.getString(getActivity(), "surveyName")
								+ "调研活动");
			// 图片logo接口还没有
			// 调研简介
			survey.put("content",
					RsSharedUtil.getString(getActivity(), "content"));
			// 图片logo接口还没有
			survey.put("logo", RsSharedUtil.getString(getActivity(), "logo"));
			// 联系人
			survey.put("contact", et_contact.getText().toString());
			// 联系电话
			survey.put("contactPhone", et_tel.getText().toString());
			// 其他说明
			survey.put("noted", et_other_statement.getText().toString());
			// 找到所有调研公司
			List<EnterpriseEntity> enterpriseEntities = null;
			try {
				enterpriseEntities = dbUtils.findAll(EnterpriseEntity.class);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray enterprises = new JSONArray();
			DateComparator dateComparator = new DateComparator();
			// 调研公司
			for (int i = 0; i < enterpriseEntities.size(); i++) {
				// 选择公司的最早时间和最晚时间
				if (i == 0) {
					beginDate = enterpriseEntities.get(i).getBeginDate();
					endDate = enterpriseEntities.get(i).getEndDate();
				}
				if (dateComparator.compare(beginDate, enterpriseEntities.get(i)
						.getBeginDate()) < 0) {
					beginDate = enterpriseEntities.get(i).getBeginDate();
				}
				if (dateComparator.compare(endDate, enterpriseEntities.get(i)
						.getEndDate()) > 0) {
					endDate = enterpriseEntities.get(i).getEndDate();
				}
				JSONObject enterprise = new JSONObject();
				// 如果uuid不为空
				if (enterpriseEntities.get(i).getUuid() != null) {
					enterprise.put("uuid", enterpriseEntities.get(i).getUuid());
				}
				// 股票代码
				enterprise.put("symbol", enterpriseEntities.get(i).getSymbol());
				// 企业简称
				enterprise.put("shortName", enterpriseEntities.get(i)
						.getShortName());
				// 提纲
				enterprise.put("content", enterpriseEntities.get(i)
						.getContent());
				// 地点代码
				enterprise.put("locationCode", enterpriseEntities.get(i)
						.getLocationCode());
				// 行业代码
				enterprise.put("industryCode", enterpriseEntities.get(i)
						.getIndustryCode());
				// 被调研人职务
				enterprise.put("receiverpost", enterpriseEntities.get(i)
						.getReceicerpost());
				// 起始时间
				enterprise.put("beginDate", enterpriseEntities.get(i)
						.getBeginDate());
				// 结束时间
				enterprise.put("endDate", enterpriseEntities.get(i)
						.getEndDate());
				// 是否删除
				if (enterpriseEntities.get(i).getDeleted() != null) {
					enterprise.put("deleted", enterpriseEntities.get(i)
							.getDeleted());
				}
				// 公司类型
				enterprise.put("type", enterpriseEntities.get(i).getType());
				enterprises.put(enterprise);

			}
			survey.put("surveySecurity", enterprises);
			// 起始时间
			survey.put("beginDate", beginDate);
			// 结束时间
			survey.put("endDate", endDate);
			Log.d("调研活动详细信息", survey.toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.d("uuuuuu", survey.toString());
		StringRequest request = new StringRequest(Method.POST, url, survey,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("创建调研活动", response.toString() + "123");
						if (uuid == null) {// 创建活动
							// InternetDialog internetDialog = new
							// InternetDialog(
							// getActivity());
							// internetDialog.showInternetDialog(
							// getActivity().getResources().getString(
							// R.string.create_activity_tip),
							// false);
							// // 两秒之后返回首页
							// Timer timer = new Timer(true);
							// timer.schedule(task, 2000);

							dialogManager.ShowBlueDialog();
							dialogManager.setBlueMessage(getActivity()
									.getResources().getString(
											R.string.create_activity_tip));
							//确定监听
							dialogManager
									.setBluePositiveButton(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											//返回上一级
											getActivity().finish();
										}
									});
							//取消监听
							dialogManager
									.setBlueNegativeButton(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											//对话框消失
											dialogManager.dismiss();
										}
									});
							// getActivity().finish();

						} else {// 编辑活动
						// InternetDialog internetDialog = new InternetDialog(
						// getActivity());
						// internetDialog.showInternetDialog(
						// "编辑成功\n系统将自动给每个报名者发送通知", true);
						// // 两秒之后返回
						// Timer timer = new Timer(true);
						// timer.schedule(task, 2000);

							dialogManager.ShowBlueDialog();
							dialogManager
									.setBlueMessage("编辑成功\n系统将自动给每个报名者发送通知");
							dialogManager
									.setBluePositiveButton(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											getActivity().finish();
										}
									});
							dialogManager
									.setBlueNegativeButton(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											dialogManager.dismiss();
										}
									});
							// getActivity().finish();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));
							InternetDialog internetDialog = new InternetDialog(
									getActivity());
							internetDialog.showInternetDialog(
									jsonObject.getString("description"), false);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
					}
				});
		volleyRequestQueue.add(request);
	}

}
