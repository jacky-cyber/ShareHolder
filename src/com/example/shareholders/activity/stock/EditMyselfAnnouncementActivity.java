package com.example.shareholders.activity.stock;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.jacksonModel.stock.Announcement;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.ReadFile;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_editmyself_announcement)
public class EditMyselfAnnouncementActivity extends Activity {

	// 返回键
	@ViewInject(R.id.title_note)
	private ImageView iv_return;

	// 分享键
	@ViewInject(R.id.tv_share_news)
	private TextView tv_share_news;

	// 收藏(TextView)
	@ViewInject(R.id.tv_collect_news)
	private TextView tv_collect_news;

	// 收藏(ImageView)
	@ViewInject(R.id.iv_collect_news)
	private ImageView iv_collect_news;

	// 公告标题
	@ViewInject(R.id.tv_announcement_titile)
	private TextView tv_announcement_titile;

	// 发布日期
	@ViewInject(R.id.tv_declareDate)
	private TextView tv_declareDate;

	// 公告摘要内容
	@ViewInject(R.id.tv_content)
	private TextView tv_content;

	// 提示对话框
	private AlertDialog myDialog = null;
	private boolean isCollected = false;

	// 加载进度框
//	private ProgressDialog progressDialog;
	private LoadingDialog loadingDialog;

	// popWindow的背景
	@ViewInject(R.id.v_bg)
	private View v_bg;
	// 父容器的relativeLayout
	@ViewInject(R.id.rl_parent)
	private RelativeLayout rl_parent;

	// 公告信息的布局
	@ViewInject(R.id.rl_announcement)
	private RelativeLayout rl_announcement;
	// 下载的布局
	@ViewInject(R.id.rl_download_announcement)
	private RelativeLayout rl_download_announcement;

	@ViewInject(R.id.iv_download_announcement)
	private ImageView iv_download_announcement;

	private ShareUtils popupWindow;
	// 下载文件的路径
	private String fileurl;
	// 文件是否已经下载
	private boolean ISDOWNLOAD = false;
	private String localPath;
	DbUtils dbUtils;
	String announcementid;
	
	String title="";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		dbUtils = DbUtils.create(this);
		init();
		
	}

	private void init() {
		v_bg.setAlpha(0.0f);
		rl_announcement.setVisibility(View.INVISIBLE);
//		progressDialog = new ProgressDialog(this);
//		progressDialog.setMessage("正在加载");
//		progressDialog.show();
		loadingDialog = new LoadingDialog(EditMyselfAnnouncementActivity.this);
		loadingDialog.showLoadingDialog();

		announcementid = getIntent().getExtras().getString(
				"announcementid");

		String url = AppConfig.URL_INFO + "ann/" + announcementid
				+ ".json?access_token=";
		url += RsSharedUtil.getString(EditMyselfAnnouncementActivity.this,
				AppConfig.ACCESS_TOKEN);

		Log.d("dj_announcement_detail", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_announcement_details_response",
								response.toString());

						try {
							JSONObject jsonObject = new JSONObject(response);
							fileurl = jsonObject.getString("announcementroute");
							HashMap<String, Object> data = new HashMap<String, Object>();
							Iterator<String> iterator = jsonObject.keys();

							while (iterator.hasNext()) {
								String key = iterator.next();
								data.put(key, jsonObject.get(key).toString());
							}

							setView(data);
//							progressDialog.dismiss();
							loadingDialog.dismissDialog();
							rl_announcement.setVisibility(View.VISIBLE);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}

					// 设置接收过来的内容 : 当公告标题、格式、路径存在时，摘要标题、内容、大小为空，反之亦然
					private void setView(HashMap<String, Object> data) {
						
						if (data.get("followed").toString().equals("false")) {
							Log.d("ccctf", "sdas");
							isCollected=false;
						}else {
							Log.d("ccctf", "asd");
							isCollected=true;
						}
						initCollected();
						
						if ((data.get("title").toString() != null)
								&& (data.get("announcementroute").toString() != null)) {
							title=data.get("title").toString();
							// 设置公告标题
							tv_announcement_titile.setText(data.get("title")
									.toString());
							// 看看数据库是否存在该文件
							try {
								Announcement announcement = dbUtils.findById(
										Announcement.class,
										data.get("title").toString().trim());
								Log.d("路径啊", data.get("title").toString());
								// 已经存在
								if (announcement != null) {
									ISDOWNLOAD = true;
									iv_download_announcement
											.setImageResource(R.drawable.survey_downloaded);
									localPath = AppConfig.ANNOUNCEMENT_PATH+"/"
											+ data.get("title").toString();
									Log.d("路径啊", "yes");
								} else {
									ISDOWNLOAD = false;
									Log.d("路径啊", "fuck");
								}
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// 内容为空，设置内容布局不可视

							tv_content.setVisibility(View.INVISIBLE);
						} else {
							// 设置摘要标题
							tv_announcement_titile.setText(data.get(
									"summarytitle").toString());
							title=data.get(
									"summarytitle").toString();
							// 设置公告摘要内容
							tv_content.setText(data.get("summarycontent")
									.toString());
							// 设置下载文件的布局消失
							rl_download_announcement.setVisibility(View.GONE);
						}

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm");
						long date_long = Long.parseLong(data.get("declaredate")
								.toString());
						String declareDate = simpleDateFormat.format(new Date(
								date_long));
						// 设置发布日期
						tv_declareDate.setText(declareDate);

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("dj_announcement_detail_errorResponse",
									jsonObject.getString("description"));
						} catch (Exception e) {
							// TODO: handle exception
							Log.d("dj_announcement_detail_errorResponse",
									e.toString());
						}
					}
				});
		stringRequest.setTag("AnnouncementDetails");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method 
		MyApplication.getRequestQueue().cancelAll("AnnouncementDetails");
		MyApplication.getRequestQueue().cancelAll("stubChangecollect");
		super.onDestroy();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (myDialog.isShowing()) {
					myDialog.dismiss();
				}
			}
		}
	};

	private void collectNews() {

		myDialog = new AlertDialog.Builder(EditMyselfAnnouncementActivity.this)
				.create();
		myDialog.show();
		handler.sendEmptyMessageDelayed(0x123, 1000);
		myDialog.setCancelable(false);
		myDialog.getWindow().setContentView(R.layout.item_toast_popup);
		TextView tv_item = (TextView) myDialog.getWindow().findViewById(
				R.id.tv_item);
		
		String url=AppConfig.URL_INFO+"follow.json?access_token=";
		url+=RsSharedUtil.getString(EditMyselfAnnouncementActivity.this, AppConfig.ACCESS_TOKEN);
		url+="&id="+announcementid;
		if (isCollected) {
			url+="&infoType=ANN&type=CANCEL";
			tv_item.setText("已取消收藏");
			tv_collect_news.setText("收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_normal);
		}else {
			url+="&infoType=ANN&type=FOLLOW";
			tv_item.setText("已收藏");
			tv_collect_news.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			tv_collect_news.setText("已收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_selected);
		}
		Log.d("4.1url", url);
		StringRequest stringRequest=new StringRequest(Method.GET, url, null, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				isCollected = !isCollected;
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		stringRequest.setTag("Changecollect");
		MyApplication.getRequestQueue().add(stringRequest);
		
	}
	private void initCollected(){
		if (isCollected) {
			tv_collect_news.setText("已收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_selected);
		} else {
			tv_collect_news.setText("收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_normal);
		}
	}

	private void downLoadFile(String url) {
		HttpUtils http = new HttpUtils();
		Log.d("url", url);
		File dir = new File(AppConfig.ANNOUNCEMENT_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, tv_announcement_titile.getText().toString());
		localPath = file.getAbsolutePath();
		HttpHandler httpHandler = http.download(url, file.getAbsolutePath(),
				true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {

					@Override
					public void onStart() {
						Toast.makeText(EditMyselfAnnouncementActivity.this, "正在下载", 1000)
						.show();
						iv_download_announcement
								.setImageResource(R.drawable.survey_download);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						iv_download_announcement
								.setImageResource(R.drawable.survey_download);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						iv_download_announcement
								.setImageResource(R.drawable.survey_downloaded);
						ISDOWNLOAD = true;
						
						try {
							Log.d("路径啊111", localPath);
							Announcement announcement = new Announcement();
							announcement.setFileName(tv_announcement_titile.getText().toString().trim());
							dbUtils.saveOrUpdate(announcement);
							Intent intent = ReadFile
									.openFile(localPath);
							startActivity(intent);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("error", e.toString());
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.d("error", error.toString());
						Log.d("msg", msg.toString());
						InternetDialog internetDialog = new InternetDialog(EditMyselfAnnouncementActivity.this);
						internetDialog.showInternetDialog("暂不提供下载", false);
//						Toast.makeText(EditMyselfAnnouncementActivity.this, "暂不提供下载", 1000)
//						.show();
						iv_download_announcement
								.setImageResource(R.drawable.survey_download);
					}
				});
	}

	@OnClick({ R.id.title_note, R.id.rl_download_announcement,
			R.id.tv_share_news, R.id.rl_collect })

	private void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;

		case R.id.rl_download_announcement:


			if (!ISDOWNLOAD) {
				downLoadFile(fileurl);
			} else {
				Intent intent = ReadFile.openFile(localPath);
				startActivity(intent);
			}
			
			break;

		case R.id.tv_share_news:
			showShare(title);
			break;
		case R.id.rl_collect:
			collectNews();
			break;
		default:
			break;
		}
	}

	private void showShare(String content) {
		v_bg.setAlpha(0.5f);
		popupWindow = new ShareUtils(EditMyselfAnnouncementActivity.this,
				rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				v_bg.setAlpha(0.0f);

			}
		});
	}
}
