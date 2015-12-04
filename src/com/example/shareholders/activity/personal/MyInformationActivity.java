package com.example.shareholders.activity.personal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.SelectPicActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.service.PostPersonalInformationService;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.FileUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_my_information)
public class MyInformationActivity extends FragmentActivity {
	@ViewInject(R.id.tv_change_figure)
	private TextView tv_change_figure;
	@ViewInject(R.id.ci_friend_figure)
	private CircleImageView ci_friend_figure;
	@ViewInject(R.id.rl_userName)
	private RelativeLayout rl_userName;
	
	 private BitmapUtils bitmapUtils=null;

	// 更改头像
	public static final int TO_SELECT_PHOTO = 1;
	// 修改昵称
	public static final int TO_CHANGE_NICKNAME = 2;
	// 修改一句话介绍自己
	public static final int TO_CHANGE_INTRODCE = 3;
	@ViewInject(R.id.tv_userName_content)
	private TextView tv_userName_content;
	private ProgressDialog progressDialog;
	// 一句话介绍
	@ViewInject(R.id.rl_introduce)
	private RelativeLayout rl_introduce;
	@ViewInject(R.id.rl_background)
	private RelativeLayout rl_background;
	RequestQueue volleyRequestQueue;
	// 行业
	@ViewInject(R.id.tv_industry_content)
	private TextView tv_industry_content;
	// 位置
	private int currentPosition = -1;
	// 城市位置
	private int currentCity = -1;
	// 行业
	List<String> allIndustryList;
	// 行业代码
	private ArrayList<String> industryCodeList;
	// 城市
	private List<String> cityString = new ArrayList<String>();
	// 城市代码
	private List<String> cityCode = new ArrayList<String>();

	@ViewInject(R.id.tv_city_content)
	private TextView city;
	DbUtils dbUtils;
	PersonalInformation personalInformation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);

		bitmapUtils = new BitmapUtils(MyInformationActivity.this);
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		
		tv_change_figure.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		dbUtils = DbUtils.create(this);

		// progressDialog = new ProgressDialog(this);
		volleyRequestQueue = Volley.newRequestQueue(this);
	}

	@Override
	protected void onResume() {
		// 获取本地信息
		try {
			personalInformation = dbUtils.findById(PersonalInformation.class,
					RsSharedUtil.getString(getApplicationContext(),
							AppConfig.UUID));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getInfo();
		super.onResume();
	}

	private void getInfo() {
		// TODO Auto-generated method stub

		// 获取昵称
		tv_userName_content.setText(personalInformation.getUserName());
		// 获取头像
		bitmapUtils.display(ci_friend_figure, personalInformation.getUserLogo());
		/*ImageLoader.getInstance().displayImage(
				personalInformation.getUserLogo(), ci_friend_figure);*/
		// 获取行业
		tv_industry_content
				.setText((personalInformation.getIndustryName() == null || personalInformation
						.getIndustryName().equals("")) ? "未填写"
						: personalInformation.getIndustryName());
		// 获取城市
		city.setText((personalInformation.getLocationName() == null || personalInformation
				.getLocationName().equals("")) ? "未填写" : personalInformation
				.getLocationName());
	}

	@OnClick({ R.id.rl_return, R.id.rl_head, R.id.rl_userName,
			R.id.rl_introduce, R.id.rl_industry, R.id.rl_city })
	private void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		// 返回
		case R.id.rl_return:
			finish();
			break;
		// 修改头像
		case R.id.rl_head:
			intent.setClass(this, SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
		// 修改nickname
		case R.id.rl_userName:
			intent.setClass(this, ChangeNickNameActivity.class);
			startActivityForResult(intent, TO_CHANGE_NICKNAME);
			break;
		// 一句话介绍自己
		case R.id.rl_introduce:
			intent.setClass(this, IntroduceMyselfActivity.class);
			startActivityForResult(intent, TO_CHANGE_INTRODCE);
			break;
		// 行业
		case R.id.rl_industry:
			initMenu(getApplicationContext(), R.layout.popup_choose_industry);
			break;
		//城市
		case R.id.rl_city:
			initCityData(getApplicationContext(), R.layout.popup_choose_city);
			break;
		default:
			break;
		}
	}

	// 城市列表popupWindow
	private void initCityData(final Context context, int view) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				(int) (width / 1.5), (int) (height / 1.5));
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		// popwindow位置
		popupWindow.showAtLocation(rl_background, Gravity.CENTER, 0, 0);
		rl_background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				rl_background.setAlpha(0.0f);
			}
		});

		ListView lv_city = (ListView) contentView.findViewById(R.id.list_city);
		getCity(lv_city);
		TextView tv_con = (TextView) contentView
				.findViewById(R.id.tv_city_confirm);
		// 获取城市
		tv_con.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (currentCity != -1 && cityString != null) {
					city.setText(cityString.get(currentCity));
					personalInformation.setLocationName(cityString
							.get(currentCity));
					try {
						dbUtils.saveOrUpdate(personalInformation);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 写入本地
					postCity(cityCode.get(currentCity));
				}
				popupWindow.dismiss();
			}
		});

	}

	// 提交城市
	private void postCity(String cityCode) {
		// progressDialog.setMessage("正在修改所在城市...");
		// progressDialog.show();
//		final LoadingDialog loadingDialog = new LoadingDialog(
//				MyInformationActivity.this);
//		loadingDialog.setLoadingString("正在修改所在城市...");
//		loadingDialog.showLoadingDialog();
		String requestURL = AppConfig.VERSION_URL
				+ "user/profile/change.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("locationCode", cityCode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST,
				requestURL, jsonObject, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// Toast.makeText(MyInformationActivity.this, "修改成功",
						// Toast.LENGTH_SHORT).show();
//						loadingDialog.dismissDialog();
//
//						LoadingDialog loadingDialog2 = new LoadingDialog(
//								MyInformationActivity.this);
//						loadingDialog2.setInternetString("修改成功!");
//						loadingDialog2.setFlag(true);
//						loadingDialog2.showInternetDialog();

						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.LOCATION, cityString.get(currentCity));
						// progressDialog.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}

				});
		volleyRequestQueue.add(stringRequest);
	}

	// 获得城市
	private void getCity(final ListView listview) {
		String url = AppConfig.URL_SURVEY + "city/all.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
						Iterator<String> jIterator;
						HashMap<String, String> data = null;
						for (int i = 0; i < response.length(); i++) {
							try {
								jIterator = response.getJSONObject(i).keys();
								data = new HashMap<String, String>();
								while (jIterator.hasNext()) {
									String key = jIterator.next();
									data.put(key, response.getJSONObject(i)
											.get(key).toString());
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							datas.add(data);
						}

						// Log.d("lele_datas", datas.toString());
						// 把数据分别放到行业名称和行业代码的集合中
						for (int i = 0; i < datas.size(); i++) {
							cityString.add(""
									+ datas.get(i).get("locationName"));
							cityCode.add("" + datas.get(i).get("locationCode"));
						}

						CityAdapter industryAdapter = new CityAdapter(
								cityString);
						listview.setAdapter(industryAdapter);
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

		volleyRequestQueue.add(jsonArrayRequest);
	}

	class CityAdapter extends BaseAdapter {
		private List<String> citys;
		LayoutInflater mInflater;

		CityAdapter(List<String> cities) {
			this.citys = cities;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return citys.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return citys.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_listview_city,
						null);
			}
			final TextView tv_city_name = AbViewHolder.get(convertView,
					R.id.tv_city_name);
			tv_city_name.setText(citys.get(position));
			if (currentCity == position) {
				tv_city_name
						.setBackgroundResource(R.drawable.btn_city_selected_style);
				tv_city_name.setTextColor(MyInformationActivity.this
						.getResources().getColor(R.color.white));
			} else {

				tv_city_name.setBackgroundResource(R.drawable.btn_city_style);
				tv_city_name.setTextColor(MyInformationActivity.this
						.getResources().getColor(R.color.gridview_item_color));
			}
			tv_city_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					currentCity = position;
					CityAdapter.this.notifyDataSetChanged();
				}
			});
			return convertView;
		}

	}

	/**
	 * 修改头像啦
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			String picPath = data
					.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);

			// 获取图片
			if (picPath != null) {
				Log.d("path", picPath);
				
				
				
				//这里设置头像
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeFile(picPath, options);
				ci_friend_figure.setImageBitmap(bm);
				
				
				Log.d("file_size", FileUtils.getFileOrFilesSize(picPath, 3)+"");
				//压缩图片
				BitmapFactory.Options options1 = new BitmapFactory.Options();
				if(FileUtils.getFileOrFilesSize(picPath, 3)>1)
				{
					options1.inSampleSize = 8;
				}
				else {
					options1.inSampleSize = 4;
				}
				
				Bitmap bm1 = BitmapFactory.decodeFile(picPath, options1);
				saveToLocal(bm1);
				PostPicture(pathString);
				
			} else {
				Toast.makeText(getApplicationContext(), "只能选择sd卡中的图片",
						Toast.LENGTH_LONG).show();
			}
		}
		if (resultCode == Activity.RESULT_OK
				&& requestCode == TO_CHANGE_NICKNAME) {
			// 获取昵称
			try {
				tv_userName_content.setText(data.getExtras().getString(
						"nickname"));
				postNickName(data.getExtras().getString("nickname"));
			} catch (Exception e) {

			}
		}

		if (resultCode == Activity.RESULT_OK
				&& requestCode == TO_CHANGE_INTRODCE) {
			// 上传一句话介绍自己
			try {
				postIntroduce(data.getExtras().getString("introduction"));
			} catch (Exception e) {

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	private String pathString="";
	
	//保存作压缩的图像
	public int saveToLocal(Bitmap bitmap){
		File appDir = new File(Environment.getExternalStorageDirectory(), "gdh");
		if (!appDir.exists()) {
			appDir.mkdir();
		}

	
		//把app下drawable图片转换成流
		String fileName = System.currentTimeMillis() + ".jpg";
		pathString=appDir.toString()+"/"+fileName;
		Log.d("pathString",pathString);
		File file = new File(appDir, fileName);
		try {
			InputStream is;
				//InputStream is = context.getResources().openRawResource(list[position]);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				is = new ByteArrayInputStream(baos.toByteArray());

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count=is.read(buffer))>0) {
				fos.write(buffer,0,count);
			}
			fos.flush();
			fos.close();
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("jatjat", e.toString()+"0000000000000");
		}
		return 0;
	}

	
	

	private void postNickName(final String name) {
		// progressDialog.setMessage("正在修改昵称...");
		// progressDialog.show();
		final LoadingDialog loadingDialog = new LoadingDialog(
				MyInformationActivity.this);
		loadingDialog.setLoadingString("正在修改昵称...");
		loadingDialog.showLoadingDialog();
		String requestURL = AppConfig.VERSION_URL
				+ "user/profile/change.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userName", name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST,
				requestURL, jsonObject, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.NICKNAME, name);
						// Toast.makeText(MyInformationActivity.this, "修改成功",
						// Toast.LENGTH_SHORT).show();
						// progressDialog.dismiss();

						loadingDialog.dismissDialog();

						LoadingDialog loadingDialog2 = new LoadingDialog(
								MyInformationActivity.this);
						loadingDialog2.setInternetString("修改成功!");
						loadingDialog2.setFlag(true);
						loadingDialog2.showInternetDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}

				});
		volleyRequestQueue.add(stringRequest);
	}

	private void postIntroduce(final String introduce) {
		// progressDialog.setMessage("正在修改个人简介...");
		// progressDialog.show();
		final LoadingDialog loadingDialog = new LoadingDialog(
				MyInformationActivity.this);
		loadingDialog.setLoadingString("正在修改个人简介...");
		loadingDialog.showLoadingDialog();
		String requestURL = AppConfig.VERSION_URL
				+ "user/profile/change.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("introduction", introduce);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST,
				requestURL, jsonObject, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.INTRODUCE_CONTENT, introduce);
						// Toast.makeText(MyInformationActivity.this, "修改成功",
						// Toast.LENGTH_SHORT).show();
						// progressDialog.dismiss();

						loadingDialog.dismissDialog();

						LoadingDialog loadingDialog2 = new LoadingDialog(
								MyInformationActivity.this);
						loadingDialog2.setInternetString("修改成功!");
						loadingDialog2.setFlag(true);
						loadingDialog2.showInternetDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}

				});
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 弹出所有行业
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(final Context context, int view) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				(int) (width / 1.3), (int) (height / 2));
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		// popwindow位置
		popupWindow.showAtLocation(rl_background, Gravity.CENTER, 0, 0);
		rl_background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				rl_background.setAlpha(0.0f);
			}
		});
		GridView gv_industry = (GridView) contentView
				.findViewById(R.id.gv_industry);
		GetIndustry(gv_industry);
		TextView tv_confirm = (TextView) contentView
				.findViewById(R.id.tv_confirm);
		// 获取行业
		tv_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (currentPosition != -1 && allIndustryList != null) {
					tv_industry_content.setText(allIndustryList
							.get(currentPosition));
					personalInformation.setIndustryName(allIndustryList
							.get(currentPosition));
					try {
						dbUtils.saveOrUpdate(personalInformation);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					postIndustry(industryCodeList.get(currentPosition));
				}
				popupWindow.dismiss();
			}
		});
	}

	// 修改所在行业
	private void postIndustry(String industry) {
		// progressDialog.show();
//		final LoadingDialog loadingDialog = new LoadingDialog(
//				MyInformationActivity.this);
//		loadingDialog.setLoadingString("正在修改所在行业...");
//		loadingDialog.showLoadingDialog();
		String requestURL = AppConfig.VERSION_URL
				+ "user/profile/change.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token");
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("industryCode", industry);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST,
				requestURL, jsonObject, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {

						// 写入本地
						RsSharedUtil.putString(getApplicationContext(),
								AppConfig.INDUSTRY,
								allIndustryList.get(currentPosition));
						// Toast.makeText(MyInformationActivity.this, "修改成功",
						// Toast.LENGTH_SHORT).show();
						// progressDialog.dismiss();

//						loadingDialog.dismissDialog();

//						LoadingDialog loadingDialog2 = new LoadingDialog(
//								MyInformationActivity.this);
//						loadingDialog2.setInternetString("修改成功!");
//						loadingDialog2.setFlag(true);
//						loadingDialog2.showInternetDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}

				});
		volleyRequestQueue.add(stringRequest);

	}

	// 上传图片
	private void PostPicture(String picPath) {
		String requestURL = AppConfig.VERSION_URL
				+ "file/upload.json?access_token=";
		requestURL = requestURL + RsSharedUtil.getString(this, "access_token")
				+ "&uploadType=USER";
		Log.d("requestURL", requestURL);
		RequestParams params = new RequestParams();
		params.addBodyParameter("file", new File(picPath));
		final LoadingDialog loadingDialog = new LoadingDialog(
				MyInformationActivity.this, "准备上传图片...", false, false, null);
		// 设置10秒的上传提示框
		loadingDialog.setTime(10000);
		Log.d("dj_getTime", loadingDialog.getTime() + "");
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, requestURL, params,
				new RequestCallBack<String>() {

					@Override
					public void onStart() {
						Log.d("dj_start", "onStart");
						loadingDialog.setLoadingString("正在上传图片...");
						loadingDialog.showLoadingDialog();
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						if (isUploading) {
							Log.d("dj_test_uploading", "isUploading");
							loadingDialog.setLoadingString("正在上传图片...");
						} else {
							Log.d("dj_test_upLoading", "notUpLoading");
							loadingDialog.setLoadingString("准备上传图片...");
						}
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						RsSharedUtil.putString(MyInformationActivity.this,
								AppConfig.FIGURE_URL, responseInfo.result);
						personalInformation.setUserLogo(responseInfo.result);

						ImageLoader.getInstance().displayImage(
								responseInfo.result, ci_friend_figure);
						
						
						try {
							dbUtils.saveOrUpdate(personalInformation);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("logo", responseInfo.result);
						intent.putExtras(bundle);
						intent.setClass(MyInformationActivity.this,
								PostPersonalInformationService.class);
						
						startService(intent);
						
						//删除临时文件
						File file=new File(pathString);
						file.delete();
						
						// progressDialog.dismiss();
						loadingDialog.dismissDialog();

						LoadingDialog loadingDialog2 = new LoadingDialog(
								MyInformationActivity.this);
						loadingDialog2.setInternetString("上传成功!");
						loadingDialog2.setFlag(true);
						loadingDialog2.showInternetDialog();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.d("dj_test_success", "fail");
						// progressDialog.dismiss();
						//删除临时文件
						File file=new File(pathString);
						file.delete();
						
						loadingDialog.dismissDialog();

						LoadingDialog loadingDialog2 = new LoadingDialog(
								MyInformationActivity.this);
						loadingDialog2.setInternetString("上传失败!");
						loadingDialog2.showInternetDialog();
					}
				});
	}

	private void GetIndustry(final GridView gv) {

		String url = AppConfig.URL_SURVEY + "industry/all.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
				Request.Method.POST, url, null,
				new Response.Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Log.d("industry   code", response.toString());
						industryCodeList = new ArrayList<String>();
						allIndustryList = new ArrayList<String>();
						for (int i = 0; i < response.length(); i++) {
							try {
								allIndustryList.add(response.getJSONObject(i)
										.get("industryName").toString());
								industryCodeList.add(response.getJSONObject(i)
										.get("industryCode").toString());

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						IndustryAdapter industryAdapter = new IndustryAdapter(
								allIndustryList);
						gv.setAdapter(industryAdapter);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							Log.d("lele_industry", "" + error.statuCode());
						} catch (Exception e) {
							Toast.makeText(MyInformationActivity.this,
									e.toString(), 1).show();
						}
					}
				});

		volleyRequestQueue.add(jsonArrayRequest);
	}

	class IndustryAdapter extends BaseAdapter {
		private List<String> industries;
		LayoutInflater mInflater;

		IndustryAdapter(List<String> industries) {
			this.industries = industries;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return industries.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.item_gridview_industry, null);
			}
			final TextView tv_industry_name = AbViewHolder.get(convertView,
					R.id.tv_industry_name);
			tv_industry_name.setText(industries.get(position));
			if (currentPosition == position) {
				tv_industry_name
						.setBackgroundResource(R.drawable.btn_industry_selected_style);
				tv_industry_name.setTextColor(MyInformationActivity.this
						.getResources().getColor(R.color.white));
			} else {
				tv_industry_name
						.setBackgroundResource(R.drawable.btn_industry_style);
				tv_industry_name.setTextColor(MyInformationActivity.this
						.getResources().getColor(R.color.gridview_item_color));
			}
			tv_industry_name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					currentPosition = position;
					IndustryAdapter.this.notifyDataSetChanged();
				}
			});
			return convertView;
		}

	}
}
