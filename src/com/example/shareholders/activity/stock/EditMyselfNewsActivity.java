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
import com.example.shareholders.jacksonModel.stock.News;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.ReadFile;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_editmyself_news)
public class EditMyselfNewsActivity extends Activity {
	@ViewInject(R.id.title_note)
	private ImageView iv_return;
	// 分享
	@ViewInject(R.id.tv_share_news)
	private TextView tv_share_news;
	// 收藏（TextView）
	@ViewInject(R.id.tv_collect_news)
	private TextView tv_collect_news;
	// 收藏（ImageView）
	@ViewInject(R.id.iv_collect_news)
	private ImageView iv_collect_news;

	// 标题
	@ViewInject(R.id.tv_news_title)
	private TextView tv_news_title;
	// 时间
	@ViewInject(R.id.tv_declareDate)
	private TextView tv_declareDate;
	// 内容
	@ViewInject(R.id.tv_news_content)
	private TextView tv_news_content;
	// 新闻来源
	@ViewInject(R.id.tv_source)
	private TextView tv_source;

	private AlertDialog myDialog = null;
	private boolean isCollected = false;
//	private ProgressDialog progressDialog;
	private LoadingDialog loadingDialog;

	// popWindow的背景
	@ViewInject(R.id.v_bg)
	private View v_bg;
	// 父容器的relativeLayout
	@ViewInject(R.id.rl_parent)
	private RelativeLayout rl_parent;

	// 新闻信息的布局
	@ViewInject(R.id.rl_news)
	private RelativeLayout rl_news;

	@ViewInject(R.id.iv_download_news)
	private ImageView iv_download_news;

	private ShareUtils popupWindow;
	@ViewInject(R.id.rl_download_news)
	private RelativeLayout rl_download_news;
	// 下载文件的路径
	private String fileurl;
	// 文件是否已经下载
	private boolean ISDOWNLOAD = false;
	private String localPath;
	DbUtils dbUtils;
	String newsid;
	
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
		rl_news.setVisibility(View.INVISIBLE);
//		progressDialog = new ProgressDialog(this);
//		progressDialog.setMessage("正在加载");
//		progressDialog.show();
		loadingDialog = new LoadingDialog(EditMyselfNewsActivity.this);
		loadingDialog.showLoadingDialog();

		newsid = getIntent().getExtras().getString("newsid");

		String url = AppConfig.URL_INFO + "news/" + newsid + ".json?access_token=";
		url += RsSharedUtil.getString(EditMyselfNewsActivity.this, AppConfig.ACCESS_TOKEN);

		Log.d("dj_news_detail", url);

		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.d("dj_news_details_response", response.toString());

				try {
					JSONObject jsonObject = new JSONObject(response);
					HashMap<String, Object> data = new HashMap<String, Object>();
					Iterator<String> iterator = jsonObject.keys();

					while (iterator.hasNext()) {
						String key = iterator.next();
						data.put(key, jsonObject.get(key).toString());
					}
					setView(data);
//					progressDialog.dismiss();
					loadingDialog.dismissDialog();
					rl_news.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			private void setView(HashMap<String, Object> data) {
				if (data.get("followed").toString().equals("false")) {
					Log.d("ccctf", "sdas");
					isCollected=false;
				}else {
					Log.d("ccctf", "asd");
					isCollected=true;
				}
				initCollected();
				title=data.get("title").toString();
				tv_news_title.setText(data.get("title").toString());
				try {
					News news = dbUtils.findById(News.class, data.get("title").toString().trim());
					Log.d("路径啊", data.get("title").toString());
					// 已经存在
					if (news != null) {
						ISDOWNLOAD = true;
						iv_download_news.setImageResource(R.drawable.survey_downloaded);
						localPath = AppConfig.NEWS_PATH + "/" + data.get("title").toString();
						Log.d("路径啊", "yes");
					} else {
						ISDOWNLOAD = false;
						Log.d("路径啊", "fuck");
					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				long date_long = Long.parseLong(data.get("declaredate").toString());
				String declareDate = simpleDateFormat.format(new Date(date_long));
				tv_declareDate.setText(declareDate);
				// 显示新闻内容还是下载
				if (data.get("newscontent").toString().equals("null")) {
					tv_news_content.setVisibility(View.GONE);
					rl_download_news.setVisibility(View.VISIBLE);
					fileurl = data.get("accessoryroute").toString();
				} else {
					tv_news_content.setVisibility(View.VISIBLE);
					rl_download_news.setVisibility(View.GONE);
					tv_news_content.setText(data.get("newscontent").toString());
				}

				tv_source.setText(data.get("newssource").toString());
				tv_news_content.setText(data.get("newscontent").toString());
				tv_source.setText(data.get("newssource").toString());
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject = new JSONObject(error.data());
					Log.d("dj_news_detail_errorResponse", jsonObject.getString("description"));
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("dj_news_detail_errorResponse", e.toString());
				}
			}
		});
		stringRequest.setTag("NewsDetail");
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
		Log.d("downLoadFileurl", url);
		File dir = new File(AppConfig.NEWS_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, tv_news_title.getText().toString());
		localPath = file.getAbsolutePath();
		http.download(url, file.getAbsolutePath(), true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {

					@Override
					public void onStart() {
						Log.d("down0", "dow1");
						Toast.makeText(EditMyselfNewsActivity.this, "正在下载", 1000)
						.show();
						iv_download_news.setImageResource(R.drawable.survey_download);
					}

					@Override
					public void onLoading(long total, long current, boolean isUploading) {
						Log.d("down1", "dow1");
						iv_download_news.setImageResource(R.drawable.survey_download);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						Log.d("down2", "dow1");
						iv_download_news.setImageResource(R.drawable.survey_downloaded);
						ISDOWNLOAD = true;

						try {
							Log.d("路径啊111", localPath);
							Log.d("路径啊111", tv_news_title.getText().toString().trim());
							News news = new News();
							news.setFileName(tv_news_title.getText().toString().trim());
							dbUtils.saveOrUpdate(news);
							Intent intent = ReadFile.openFile(localPath);
							startActivity(intent);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d("error", e.toString());
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						Log.d("newserror", error.toString());
						Log.d("newsmsg", msg.toString());
						InternetDialog internetDialog = new InternetDialog(EditMyselfNewsActivity.this);
						internetDialog.showInternetDialog("暂不提供下载", false);
//						Toast.makeText(EditMyselfNewsActivity.this, "暂不提供下载", 1000)
//						.show();
						iv_download_news.setImageResource(R.drawable.survey_download);
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("NewsDetail");
		MyApplication.getRequestQueue().cancelAll("Changenews");
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
		Log.d("newssss", "newssss");
		myDialog = new AlertDialog.Builder(EditMyselfNewsActivity.this).create();
		myDialog.show();
		handler.sendEmptyMessageDelayed(0x123, 1000);
		myDialog.setCancelable(false);
		myDialog.getWindow().setContentView(R.layout.item_toast_popup);
		TextView tv_item = (TextView) myDialog.getWindow().findViewById(R.id.tv_item);
		String url=AppConfig.URL_INFO+"follow.json?access_token=";
		url+=RsSharedUtil.getString(EditMyselfNewsActivity.this, AppConfig.ACCESS_TOKEN);
		url+="&id="+newsid;
		if (isCollected) {
			url+="&infoType=NEWS&type=CANCEL";
			tv_item.setText("已取消收藏");
			tv_collect_news.setText("收藏");
			iv_collect_news.setImageResource(R.drawable.shoucang_normal);
		}else {
			url+="&infoType=NEWS&type=FOLLOW";
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
		stringRequest.setTag("Changenews");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@OnClick({ R.id.title_note, R.id.tv_share_news, R.id.ll_collect, R.id.rl_download_news })

	private void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;

		case R.id.tv_share_news:
			showShare(title);
			break;
		case R.id.ll_collect:
			collectNews();
			break;
		case R.id.rl_download_news:
			if (!ISDOWNLOAD) {
				downLoadFile(fileurl);
			} else {
				Intent intent = ReadFile.openFile(localPath);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}

	private void showShare(String content) {
		v_bg.setAlpha(0.5f);
		popupWindow = new ShareUtils(EditMyselfNewsActivity.this, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				v_bg.setAlpha(0.0f);

			}
		});
	}

}
