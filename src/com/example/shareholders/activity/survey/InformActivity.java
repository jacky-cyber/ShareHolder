package com.example.shareholders.activity.survey;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_inform)
public class InformActivity extends Activity {
	@ViewInject(R.id.lv_announce)
	private ListView lv_announce;
	private ArrayList<String> al_announce_text;
	private ArrayList<String> al_announce_date;
	private ArrayList<HashMap<String, Object>> hashmap;
	private InformAdapter am_adapter;

//	private ProgressDialog progress;
	//请稍等的提示框
	private LoadingDialog loadingDialog;
	private RequestQueue volleyRequestQueue;
	private String uuid;
	private String messageCount;// 未读的数量
	private boolean isOrigin;// 判断是否为发起人
	@ViewInject(R.id.tv_inform_none)
	private TextView tv_none;// 该textview为通知列表没有内容时，显示。

	// 无通知时候的提示
	@ViewInject(R.id.ll_wutongzhi)
	private LinearLayout ll_wutongzhi;
	@ViewInject(R.id.iv_inform_edit)
	private ImageView iv_inform_edit;

	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		/*
		 * 功能：获取前面activity传过来的uuid
		 */
		Bundle bundle = getIntent().getExtras();
		uuid = bundle.getString("uuid");
		isOrigin = bundle.getBoolean("isOrigin");
		if (isOrigin)
			iv_inform_edit.setVisibility(View.VISIBLE);
		messageCount = bundle.getString("messageCount");
		volleyRequestQueue = Volley.newRequestQueue(this);
		context = InformActivity.this;
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setLoadingString("请稍等");
		loadingDialog.showLoadingDialog();
		// 初始化
		init();
		/*
		 * 功能：progress'判断网络状态的标志
		 */
//		progress = new ProgressDialog(InformActivity.this);
//		progress.setMessage("请稍等");
//		progress.setCanceledOnTouchOutside(false);
//		progress.setCancelable(false);
//		progress.show();
		// 获取后台数据
		NetConnect(uuid);
	}

	@Override
	protected void onResume() {
		new Thread() {
			@Override
			public void run() {
				// 这里写入子线程需要做的工作
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				NetConnect(uuid);
			}
		}.start();

		super.onResume();

	};

	// 功能：初始化
	public void init() {
		al_announce_text = new ArrayList<String>();
		al_announce_date = new ArrayList<String>();
		hashmap = new ArrayList<HashMap<String, Object>>();
		tv_none.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @Title: putHashMap
	 * @Description: TODO(HashMap填充数据)
	 * @param: @param sum，为hashMap的长度
	 * @return: void
	 * @throws
	 */
	public void putHashMap(int sum) {
		hashmap.clear();
		for (int i = 0; i < sum; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("tv_announcement_text", al_announce_text.get(i));
			item.put("tv_announcement_date", al_announce_date.get(i));
			hashmap.add(item);
		}
		am_adapter = new InformAdapter(getApplicationContext(), hashmap);
		lv_announce.setAdapter(am_adapter);
		am_adapter.notifyDataSetChanged();
	}

	@OnClick({ R.id.iv_inform_return, R.id.iv_inform_edit })
	private void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_inform_return:
			if (!BtnClickUtils.isFastDoubleClick()) {
				/**
				 * 数据传到前activity的标志
				 */
				setResult(1);
				finish();
			}
			break;
		case R.id.iv_inform_edit:
			if (!BtnClickUtils.isFastDoubleClick()) {
				Bundle bundle = new Bundle();
				Intent intent = new Intent(InformActivity.this,
						InformEditActivity.class);
				bundle.putString("uuid", uuid);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		}
	}

	/**
	 * 
	 * @Title: NetConnect
	 * @Description: TODO(获取后台数据，方式为GET)
	 * @param: @param uuid (前一个activity传过来的uuid)
	 * @return: void
	 * @throws
	 */
	public void NetConnect(String uuid) {
		String mark = RsSharedUtil.getString(getApplicationContext(),
				"access_token");
		String url = AppConfig.URL_MESSAGE + "survey.json?" + "access_token="
				+ mark + "&surveyUuid=" + uuid;

		Log.v("1234567", "access_token=" + mark + "&surveyUuid=" + uuid);
		JsonArrayRequest array = new JsonArrayRequest(Method.GET, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						// TODO Auto-generated method stub
						if (response != null) {
							ll_wutongzhi.setVisibility(View.GONE);
						}
						try {
							tv_none.setVisibility(View.GONE);
							Log.d("结构啊啊啊啊啊啊", response.toString());
							JSONArray array = new JSONArray(response.toString());
							al_announce_text.clear();
							al_announce_date.clear();
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								long createDate = object.getLong("date");
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"yyyy-MM-dd");
								String beginDate = dateFormat.format(new Date(
										createDate));// 获取时间

								String uuid = object.getString("uuid");
								boolean title = object.getBoolean("isRead");
								String content = object.getString("content");

								al_announce_text.add(content);
								al_announce_date.add(beginDate);

							}
							putHashMap(array.length());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
//							progress.dismiss();
							loadingDialog.dismissDialog();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

						try {
							switch (error.statuCode()) {
							case 204:
								tv_none.setVisibility(View.VISIBLE);
								// Toast.makeText(InformActivity.this,
								// R.string.lastest_204,
								// Toast.LENGTH_SHORT).show();
								break;

							}
						} catch (Exception e) {
							// Toast.makeText(InformActivity.this,
							// R.string.other_error, Toast.LENGTH_SHORT)
							// .show();
						} finally {
//							progress.dismiss();
							loadingDialog.dismissDialog();
						}
					}
				});

		volleyRequestQueue.add(array);
	}

	/*
	 * 功能：ListView的适配器
	 */
	@SuppressLint("ResourceAsColor")
	public class InformAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public InformAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
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
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_inform, null);

				holder.tv_announce_text = (TextView) view
						.findViewById(R.id.tv_announce_text_item);
				holder.tv_announce_date = (TextView) view
						.findViewById(R.id.tv_announce_date_item);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			if (isOrigin == false) {// 判断角色
				if (position < Integer.parseInt(messageCount)) {
					holder.tv_announce_text.setTextColor(getResources()
							.getColor(R.color.inform_first_text));
				}
			}
			holder.tv_announce_text.setText((CharSequence) list.get(position)
					.get("tv_announcement_text"));
			holder.tv_announce_date.setText((CharSequence) list.get(position)
					.get("tv_announcement_date"));

			return view;
		}

		class ViewHolder {

			TextView tv_announce_text;
			TextView tv_announce_date;

		}
	}

}
