package com.example.shareholders.activity.personal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_all_apply_manage)
public class AllApplyManageActivity extends Activity {
	@ViewInject(R.id.all_apply_list)
	ListView lv_all_apply;
	private AllApplyAdapter allapplyAdapter;
	private ArrayList<HashMap<String, String>> list;
	private BitmapUtils bitmapUtils = null;

	private ProgressDialog progressDialog;
	// 未关注界面
	@ViewInject(R.id.ll_no_apply)
	private LinearLayout ll_no_apply;

	// 已关注数据界面
	@ViewInject(R.id.ll_all_apply)
	private LinearLayout ll_apply;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		bitmapUtils = BitmapUtilFactory.getInstance();
		progressDialog = new ProgressDialog(this);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getdate();
		super.onResume();
	}

	@OnClick({ R.id.title_note,R.id.rl_return })
	private void OnClick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	private void getdate() {

		/*
		 * * String url = AppConfig.URL_INFO +&pageSize=2&pageIndex=1
		 * "follow/list.json?access_token="+AppConfig.ACCESS_TOKEN+
		 * "&pageIndex=0&pageSize=5&type=ALL";
		 */
		progressDialog.setMessage("正在加载...");
		progressDialog.show();
		String url = AppConfig.URL_USER + "enroll/survey.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token")
				+ "&pageSize=5&pageIndex=0";
		Log.d("qweq", url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						progressDialog.dismiss();
						Log.e("lelele_response", response.toString());
						if (response.equals("") || response.equals("[0]")) {
							Log.d("lele_no_content", "No Content");
							Log.d("dddddddddub", "ll_no_apply");
							ll_no_apply.setVisibility(View.VISIBLE);
							ll_apply.setVisibility(View.GONE);
						} else {
							Log.d("lele_zixun", response.toString());
							try {
								JSONObject object = new JSONObject(response
										.toString());
								JSONArray all = object.getJSONArray("surveys");
								Log.e("all", all.toString());
								final ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = null;
								Iterator<String> iterator = null;
								for (int i = 0; i < all.length(); i++) {
									data = new HashMap<String, String>();
									iterator = all.getJSONObject(i).keys();
									while (iterator.hasNext()) {
										String key = iterator.next();
										Log.e("cacacaca", key);
										data.put(key,
												all.getJSONObject(i).get(key)
														.toString());
									}
									datas.add(data);

								}
								if (datas == null) {
									Log.d("dddddddddub", "ll_no_apply");
									ll_no_apply.setVisibility(View.VISIBLE);
									ll_apply.setVisibility(View.GONE);
								} else {

									Log.d("dgggggggggg", "ll_apply");

									ll_no_apply.setVisibility(View.GONE);
									ll_apply.setVisibility(View.VISIBLE);
									lv_all_apply
											.setAdapter(new AllApplyAdapter(
													AllApplyManageActivity.this,
													datas));
									lv_all_apply
											.setOnItemClickListener(new OnItemClickListener() {

												@Override
												public void onItemClick(
														AdapterView<?> arg0,
														View arg1,
														int position, long arg3) {
													// TODO Auto-generated
													// method stub
													Intent intent = new Intent(
															getApplication(),
															ManageSignActivity.class);

													Bundle bundle = new Bundle();
													bundle.putString(
															"uuid",
															datas.get(position)
																	.get("uuid"));
													intent.putExtras(bundle);

													startActivity(intent);
												}
											});

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("lele_error", error.toString());
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error", jsonObject.get("description")
									.toString());

						} catch (Exception e) {
							Log.d("lele_error", "未知错误");
						}

					}
				});
		// volleyRequestQueue.add(stringRequest);
		stringRequest.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	private void initview() {
		allapplyAdapter = new AllApplyAdapter(this, list);
		lv_all_apply.setAdapter(allapplyAdapter);
	}

	private class AllApplyAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, String>> list;
		private Context context;
		private LayoutInflater mInflater;

		public AllApplyAdapter(Context context,
				ArrayList<HashMap<String, String>> list) {
			this.context = context;
			this.list = list;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// return list.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();
				converView = mInflater.inflate(
						R.layout.item_all_apply_manage_list, arg2, false);

				viewHolder.iv_all_apply = (ImageView) converView
						.findViewById(R.id.iv_all_apply);
				viewHolder.tv_all_apply_name = (TextView) converView
						.findViewById(R.id.tv_all_apply_name);
				viewHolder.tv_all_apply_time = (TextView) converView
						.findViewById(R.id.tv_all_apply_time);
				viewHolder.tv_countEnroll = (TextView) converView
						.findViewById(R.id.tv_countEnroll);
				viewHolder.tv_countSuccess = (TextView) converView
						.findViewById(R.id.tv_countSuccess);
				converView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			// 头像
			bitmapUtils.display(viewHolder.iv_all_apply, list.get(position)
					.get("logo").toString());

			viewHolder.tv_all_apply_name.setText(list.get(position).get(
					"surveyName"));
			viewHolder.tv_countEnroll.setText(list.get(position).get(
					"countEnroll"));
			viewHolder.tv_countSuccess.setText(list.get(position).get(
					"countSuccess"));

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy.MM.dd");
			long begin_date_long = Long.parseLong(list.get(position).get(
					"beginDate"));
			String date1 = simpleDateFormat.format(new Date(begin_date_long));

			long end_date_long = Long.parseLong(list.get(position).get(
					"endDate"));
			String date2 = simpleDateFormat.format(new Date(end_date_long));

			String locationName = new String();
			locationName = list.get(position).get("locationName");
			viewHolder.tv_all_apply_time.setText(date1 + "--" + date2 + "  "
					+ locationName);

			return converView;
		}

		class ViewHolder {

			ImageView iv_all_apply;
			TextView tv_all_apply_name;
			TextView tv_all_apply_time;
			TextView tv_countEnroll;
			TextView tv_countSuccess;
		}
	}

}
