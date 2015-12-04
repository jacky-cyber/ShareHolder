package com.example.shareholders.activity.survey;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.ManageSignActivity;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BitmapUtilFactory;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sina.weibo.sdk.constant.WBConstants.Msg;

@ContentView(R.layout.activity_userjoin)
public class UserJoinActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

/*	private DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	.showImageForEmptyUri(R.drawable.ico_default_headview)
	.showImageOnFail(R.drawable.ico_default_headview)
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.build();*/
	
	// 报名管理
	@ViewInject(R.id.ll_manage_sign_up_list)
	private LinearLayout ll_manage_sign_up_list;

	// 当无人报名时，显示暂无人报名
	@ViewInject(R.id.ll_wubaoming)
	private LinearLayout ll_wubaoming;

	@ViewInject(R.id.lv_userjoin)
	private ListView lv_userjoin;
	@ViewInject(R.id.userjoin_pull_refresh_view)
	private PullToRefreshView mPullToRefreshView;
	@ViewInject(R.id.iv_user_return)
	private RelativeLayout iv_return;
	@ViewInject(R.id.tv_person_num)
	private TextView tv_total_num;
	@ViewInject(R.id.il_user)
	private LinearLayout ll_research;
	@ViewInject(R.id.tv_none)
	private TextView tv_none;
	private ArrayList<String> al_uj_uuid;
	private ArrayList<String> al_uj_img;
	private ArrayList<String> al_uj_name;
	private ArrayList<String> al_uj_date;
	private ArrayList<Integer> al_uj_num;
	private ArrayList<HashMap<String, Object>> hashmap;
	private UserJoinAdapter adapter;

	private String uuid;
	private RequestQueue VolleyRequestQueue;

	private BitmapUtils bitmapUtils = null;

	private int pageIndex = 0;// 页码，用于下拉加载时递增,表明提交的是第几页
	private int totalElements;// 后台获取，报名的总数量
	private int pageLength = 8;// 一次获取的数据量
	private int totalPages;// 总页数
	HttpUtils http;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);		
		showLoadingDialog();
		tv_none.setVisibility(View.GONE);
		ll_wubaoming.setVisibility(View.GONE);
		init();
		getListDate(uuid, 0, pageLength, 0, (pageIndex + 1) * pageLength);// 获取后台数据
	}

	// 功能：初始化
	public void init() {
		
		bitmapUtils =new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		http = new HttpUtils();
		
		al_uj_uuid = new ArrayList<String>();
		al_uj_img = new ArrayList<String>();
		al_uj_name = new ArrayList<String>();
		al_uj_date = new ArrayList<String>();
		al_uj_num = new ArrayList<Integer>();
		hashmap = new ArrayList<HashMap<String, Object>>();

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		Intent intent = getIntent();
		uuid = intent.getStringExtra("uuid");

		VolleyRequestQueue = Volley.newRequestQueue(UserJoinActivity.this);

		/*
		 * 返回
		 */
		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!BtnClickUtils.isFastDoubleClick()) {
					finish();
				}
			}
		});

		/**
		 * 搜索查找
		 */
		ll_research.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!BtnClickUtils.isFastDoubleClick()) {
					Intent intent = new Intent(UserJoinActivity.this,
							SignUpActivity.class);
					intent.putExtra("uuid", uuid);
					startActivity(intent);
				}
			}
		});
	}

	@OnClick({ R.id.ll_manage_sign_up_list })
	public void onClick(View v) {
		switch (v.getId()) {
		// 报名管理
		case R.id.ll_manage_sign_up_list:
			 
		if((Boolean)getIntent().getExtras().getBoolean("isOriginator")){
			Intent intent = new Intent(this, ManageSignActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("uuid", uuid);
			intent.putExtras(bundle);
			startActivity(intent);
		}else{
			Toast.makeText(getApplicationContext(), "您不是发起人", 500).show();
		}
			break;

		default:
			break;
		}
	}

	/*
	 * 功能：HashMap填充数据
	 */
	public void putHashMap(int index, int num) {
		for (int i = index; i < num; i++) {

			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("iv_uj_uuid", al_uj_uuid.get(i));
			item.put("iv_uj_img", al_uj_img.get(i));
			item.put("tv_uj_name", al_uj_name.get(i));
			item.put("tv_uj_date", al_uj_date.get(i));
			item.put("tv_uj_num", al_uj_num.get(i));
			hashmap.add(item);

		}
		
		if(hashmap.size()==0){
		
				ll_wubaoming.setVisibility(View.VISIBLE);
			
		}
		adapter = new UserJoinAdapter(getApplicationContext(), hashmap);
		lv_userjoin.setAdapter(adapter);
	}

	/**
	 * <p>
	 * Title: onHeaderRefresh
	 * </p>
	 * <p>
	 * Description: 下拉刷新，重新全部获取一遍
	 * </p>
	 * 
	 * @param view
	 * 
	 */
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				hashmap.clear();
				pageIndex = 0;
				getListDate(uuid, 0, pageLength, 0, (pageIndex + 1)
						* pageLength);
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 2000);
	}

	/**
	 * <p>
	 * Title: onFooterRefresh
	 * </p>
	 * <p>
	 * Description: 上拉加载，获取更多的信息
	 * </p>
	 * 
	 * @param view
	 * 
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (pageIndex < totalPages) {

					pageIndex++;
					if (pageIndex < totalPages - 1) {

						getListDate(uuid, pageIndex, pageLength, pageIndex
								* pageLength, (pageIndex + 1) * pageLength);

					} else {
						getListDate(uuid, pageIndex, pageLength, pageIndex
								* pageLength, totalElements);

					}
				}
				mPullToRefreshView.onFooterRefreshComplete();

			}
		}, 2000);
	}

	/**
	 * 
	 * @Title: getListDate
	 * @Description: TODO(获取后台数据，添加到报名列表中)
	 * @param: @param uuid
	 * @param: @param pageIndex (页码_)
	 * @param: @param pageSize (总页数)
	 * @param: @param index（HashMap的初始值）
	 * @param: @param total （HashMap的长度）
	 * @return: void
	 * @author ZGP
	 * @date: 2015.8.12
	 * @throws
	 */
	public void getListDate(String uuid, final int pageIndex,
			final int pageSize, final int index, final int total) {

		String mark = RsSharedUtil.getString(UserJoinActivity.this,
				"access_token");

		String url = AppConfig.URL_SURVEY + "enroll/all.json?"
				+ "access_token=" + mark + "&surveyUuid=" + uuid
				+ "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
		Log.d("报名列表", url);
		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						http.sHttpCache.clear();
						Message msg=new Message();
						msg.what=1;
						mHandler.sendMessage(msg);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 当报名列表不为空时，隐藏无人报名的提示
					
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd");

						try {
							JSONObject jsonObject = new JSONObject(arg0.result
									.toString());
							JSONObject pageable = jsonObject
									.getJSONObject("pageable");
							totalPages = pageable.getInt("totalPages");
							totalElements = pageable.getInt("totalElements");

							JSONArray jsonArray = jsonObject
									.getJSONArray("enrollUsers");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject item = jsonArray.getJSONObject(i);
								String uuid = item.getString("uuid");
								String name = item.getString("name");
								String logo = item.getString("userLogo");
								long date = item.getLong("date");

								al_uj_uuid.add(uuid);
								al_uj_img.add(logo);
								al_uj_name.add(name);
								al_uj_date.add(dateFormat
										.format(new Date(date)));
								if (hashmap.size() > 0)
									al_uj_num.add(i + hashmap.size());
								else {
									al_uj_num.add(i);
								}
							}
							tv_total_num.setText("" + totalElements);
							if (pageLength <= totalElements) {
								putHashMap(index, total);
							} else {
								putHashMap(index, totalElements);
							}
							http.sHttpCache.clear();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Message msg=new Message();
						msg.what=1;
						mHandler.sendMessage(msg);
						
					}
				});
	}

	/*
	 * 功能：ListView的适配器
	 */

	public class UserJoinAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public UserJoinAdapter(Context context,
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;

			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_userjoin, null);

				holder.iv_uj_img = (ImageView) view.findViewById(R.id.iv_uj);
				holder.tv_uj_name = (TextView) view
						.findViewById(R.id.tv_uj_name);
				holder.tv_uj_date = (TextView) view
						.findViewById(R.id.tv_uj_date);
				holder.tv_uj_num = (TextView) view
						.findViewById(R.id.tv_uj_score);
				holder.ll_back = (LinearLayout) view.findViewById(R.id.ll_back);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			// 功能：每隔一个item，背景颜色不同
			if (position % 2 == 0) {
				holder.ll_back.setBackgroundColor(getResources().getColor(
						R.color.white));
			} else {
				holder.ll_back.setBackgroundColor(getResources().getColor(
						R.color.total_gray));
			}
			
			/*ImageAware imageAware = new ImageViewAware(holder.iv_uj_img, false);
			ImageLoader.getInstance().displayImage(list.get(position).get("iv_uj_img").toString(),
			imageAware, defaultOptions);*/
			
			bitmapUtils.display(holder.iv_uj_img,
					list.get(position).get("iv_uj_img").toString());

			holder.iv_uj_img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					try {
						if (list.get(position)
								.get("iv_uj_uuid")
								.toString()
								.equals(RsSharedUtil.getString(
										UserJoinActivity.this, AppConfig.UUID))) {

							Intent intent = new Intent();

							intent.setClass(UserJoinActivity.this,
									MyProfileActivity.class);
							startActivity(intent);

						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid",
									list.get(position).get("iv_uj_uuid")
											.toString());
							bundle.putString("userName", list.get(position)
									.get("tv_uj_name").toString());
							Intent intent = new Intent();
							intent.setClass(UserJoinActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("liang_iamge_Exeption", e.toString());
					}
				}
			});

			holder.tv_uj_name.setText((CharSequence) list.get(position).get(
					"tv_uj_name"));

			holder.tv_uj_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					try {
						if (list.get(position)
								.get("iv_uj_uuid")
								.toString()
								.equals(RsSharedUtil.getString(
										UserJoinActivity.this, AppConfig.UUID))) {

							Intent intent = new Intent();

							intent.setClass(UserJoinActivity.this,
									MyProfileActivity.class);
							startActivity(intent);

						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid",
									list.get(position).get("iv_uj_uuid")
											.toString());
							bundle.putString("userName", list.get(position)
									.get("tv_uj_name").toString());
							Intent intent = new Intent();
							intent.setClass(UserJoinActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d("liang_iamge_Exeption", e.toString());
					}
				}
			});

			holder.tv_uj_date.setText((CharSequence) list.get(position).get(
					"tv_uj_date"));

			holder.tv_uj_num.setText("" + list.get(position).get("tv_uj_num"));
			

			return view;
		}

		class ViewHolder {

			ImageView iv_uj_img;
			TextView tv_uj_name;
			TextView tv_uj_num;
			TextView tv_uj_date;
			ImageView iv_xinxi;
			LinearLayout ll_back;
		}
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private AlertDialog internertDialog = null;

	private void showLoadingDialog() {
		internertDialog = new AlertDialog.Builder(this).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	}

	/**
	 * 信息提示
	 */

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;

			// case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力
			//
			// if (internertDialog != null && internertDialog.isShowing()) {
			// internertDialog.dismiss();
			// showInternetDialog();
			// }
			//
			// break;
			//
			// case 3: // 提示网络异常的对话框消失
			// if (internertDialog != null && internertDialog.isShowing()) {
			// internertDialog.dismiss();
			// }
			// break;
			default:
				break;
			}
		};
	};

}