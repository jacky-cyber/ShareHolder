package com.example.shareholders.activity.survey;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ClipData.Item;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_signup)
public class SignUpActivity extends Activity {

	@ViewInject(R.id.lv_signup)
	private ListView lv_search;

	@ViewInject(R.id.et_sign_text)
	private EditText et_search;
	@ViewInject(R.id.iv_sign_return)
	ImageView iv_return;
	@ViewInject(R.id.iv_sign)
	private TextView tv_search;
	private ArrayList<String> al_text;
	private ArrayList<String> al_uuid;
	private ArrayList<HashMap<String, Object>> map;
	private ActivitySearchAdapter adapter;

	private String uuid;
	private RequestQueue volleyRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 功能：获取前一个activity传过来的uuid
		Intent intent = getIntent();
		uuid = intent.getStringExtra("uuid");
		init();
		tv_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!BtnClickUtils.isFastDoubleClick()) {
					if (et_search.getText().toString().equals("")) {

						Toast.makeText(getApplicationContext(), "请输入内容",
								Toast.LENGTH_SHORT).show();

						map.clear();
						adapter = new ActivitySearchAdapter(
								getApplicationContext(), map);
						lv_search.setAdapter(adapter);
					} else {

						/**
						 * 
						 * @Title: netConnect
						 * @Description: TODO(获取后台数据,方式为GET)
						 * @param: @param test（搜索的内容）
						 * @param: @param uuid（uuid）
						 * @param: @param pageSize（页的大小）
						 * @param: @param pageIndex（页码）
						 * @explanation:
						 * @return: void
						 * @throws
						 */
						netConnect(et_search.getText().toString(), uuid, 15, 0);
					}
				}
			}
		});
		lv_search.setOnItemClickListener(new OnItemClickListener() {
		
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				try {
					if (map.get(position)
							.get("search_uuid")
							.toString()
							.equals(RsSharedUtil.getString(
									SignUpActivity.this, AppConfig.UUID))) {

						Intent intent = new Intent();

						intent.setClass(SignUpActivity.this,
								MyProfileActivity.class);
						startActivity(intent);

					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid",
								map.get(position).get("search_uuid")
										.toString());
						bundle.putString("userName", map.get(position)
								.get("search_text").toString());
						Intent intent = new Intent();
						intent.setClass(SignUpActivity.this,
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!BtnClickUtils.isFastDoubleClick()) {
					finish();
				}
			}
		});
	}

	/**
	 * 
	 * @Title: init
	 * @Description: TODO(初始化数据)
	 * @param:
	 * @return: void
	 * @throws
	 */
	public void init() {
		volleyRequestQueue = Volley.newRequestQueue(getApplicationContext());
		al_text = new ArrayList<String>();
		al_uuid = new ArrayList<String>();

		map = new ArrayList<HashMap<String, Object>>();
		et_search.addTextChangedListener(textWatcher);
	}

	/**
	 * 
	 * @Title: putHashMap
	 * @Description: TODO(HashMap填充数据)
	 * @param: @param num （hashMap的长度）
	 * @return: void
	 * @throws
	 */
	public void putHashMap(int num) {
		map.clear();
		for (int i = 0; i < num; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("search_text", al_text.get(i));
			item.put("search_uuid", al_uuid.get(i));
			
			map.add(item);

		}

		HashMap<String, Object> item = new HashMap<String, Object>();
		if (map.size() == 0) {
			item.put("search_text", "没有找到相关的报名者");
			map.add(item);
		}
		adapter = new ActivitySearchAdapter(getApplicationContext(), map);
		lv_search.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 功能:监听edittext里字数的变化
	 */
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@SuppressLint({ "ResourceAsColor", "NewApi" })
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			String content = et_search.getText().toString();
			if (content.length() == 0) {
				tv_search.setTextColor(getResources().getColor(
						R.color.detail_line));
				tv_search.setBackground(getResources().getDrawable(
						R.drawable.btn_search_style_press));

			} else {
				tv_search.setBackground(getResources().getDrawable(
						R.drawable.btn_search_style));
				tv_search.setTextColor(getResources().getColor(R.color.white));
				String test = et_search.getText().toString();
				netConnect(test, uuid, 15, 0);
			}

		}

	};

	/**
	 * 
	 * @Title: netConnect
	 * @Description: TODO(获取后台数据,方式为GET)
	 * @param: @param test（搜索的内容）
	 * @param: @param uuid（uuid）
	 * @param: @param pageSize（页的大小）
	 * @param: @param pageIndex（页码）
	 * @return: void
	 * @throws
	 */
	public void netConnect(String test, String uuid, int pageSize, int pageIndex) {
		HttpUtils http = new HttpUtils();
		String mark = RsSharedUtil.getString(SignUpActivity.this,
				"access_token");
		String url = AppConfig.URL_SURVEY + "enroll/all/search.json?"
				+ "access_token=" + mark + "&surveyUuid=" + uuid + "&keyWord="
				+ test + "&pageSize=" + pageSize + "&pageIndex=" + pageIndex;
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(SignUpActivity.this, "网络不给力",
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						map.clear();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");
						try {
							JSONObject jsonObject = new JSONObject(arg0.result
									.toString());
							Log.d("搜索内容", arg0.result.toString());
							JSONObject pageable = jsonObject
									.getJSONObject("pageable");
							int totalPages = pageable.getInt("totalPages");
							int totalElements = pageable
									.getInt("totalElements");

							JSONArray jsonArray = jsonObject
									.getJSONArray("enrollUsers");
							al_text.clear();
							al_uuid.clear();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject item = jsonArray.getJSONObject(i);
								Log.w("milk5",item.toString());
								String surveyName = item.getString("name");
								String uuid = item.getString("uuid");
								al_text.add(surveyName);
								al_uuid.add(uuid);
							}
							putHashMap(jsonArray.length());

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 功能：ListView的适配器
	 */
	public class ActivitySearchAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public ActivitySearchAdapter(Context context,
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
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;

			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_signup, null);

				holder.tv_search_text = (TextView) view
						.findViewById(R.id.tv_item_signup);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_search_text.setText((CharSequence) list.get(position)
					.get("search_text"));

			return view;
		}

		class ViewHolder {

			TextView tv_search_text;

		}
	}

}
