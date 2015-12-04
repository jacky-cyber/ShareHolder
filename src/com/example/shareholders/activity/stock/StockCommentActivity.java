package com.example.shareholders.activity.stock;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.survey.TranspondActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.NoScrollGridView;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_stock_comment)
public class StockCommentActivity extends Activity implements
OnFooterRefreshListener, OnHeaderRefreshListener {
	@ViewInject(R.id.iv_more)
	private ImageView iv_more;

	@ViewInject(R.id.mv_child_list)
	private ListView mv_child_list;

	// @ViewInject(R.id.rl)
	// private RelativeLayout rLayout;
	@ViewInject(R.id.pull_to_refresh)
	private PullToRefreshView pull_to_refresh;

	// 评论的布局
	@ViewInject(R.id.rl_et)
	private RelativeLayout rl_et;

	// 评论的输入框
	@ViewInject(R.id.et_comment)
	private EditText et_comment;

	// 无评论的界面
	@ViewInject(R.id.ll_wupinglun)
	private LinearLayout ll_wupinglun;

	// popupwindow背后阴影
	@ViewInject(R.id.background)
	private RelativeLayout background;
	//分享的弹框
	private ShareUtils popupWindow;
	//整个父容器的布局
	@ViewInject(R.id.rl)
	private RelativeLayout rl_parent;


	private RequestQueue volleyRequestQueue;
	private BitmapUtils bitmapUtils = null;

	// 简单操作提示框
	private InternetDialog internetDialog;

	private int pageNo = 0;
	private int pageSize = 5;

	// 存放后台传来的数据
	private ArrayList<HashMap<String, Object>> datas;

	private Bundle bundle;
	// 创建者的id
	private String creatorUuid;
	// 创建者的名字
	private String creatorName;
	// 创建者头像
	private String creatorLogoUrl;
	// 创建时间
	private String creationTime;
	// 股票代码
	private String securitySymbol;
	// 股票简称
	private String securityName;
	// 股票类型
	private String securityType;
	// 话题内容
	private String content;
	// 话题id
	private String topicUuid;
	// 是否点赞
	private String liked;

	private String refTopic;

	private String medias;

	// 点赞数
	private String praise_num;
	// 评论数
	private String comment_num;
	// 浏览数
	private String scan_num;
	// 分享数
	private String share_num;
	// 是否关注该话题
	private String followed;
	private HashMap<String, Object> first;
	ChildAdapter childAdapter;
	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	// 记录用户新增的评论数
	private int add_comment_count = 0;

	private AlertDialog reportDialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0: // 举报dialog消失
				if (reportDialog != null) {
					reportDialog.dismiss();
				}
				break;

			case 1:// 评论的对话框消失
				// 输入框出现和消失对应的动画

				Animation hiddenAnimation = AnimationUtils.loadAnimation(
						StockCommentActivity.this, R.anim.et_hidden_anim);
				hiddenAnimation.setFillAfter(true);

				// 键盘消失时评论框隐藏
				if (rl_et.getVisibility() == View.VISIBLE) {
					rl_et.setVisibility(View.GONE);
					rl_et.startAnimation(hiddenAnimation);
				}
				break;

			default:
				break;
			}
		};
	};

	private RelativeLayout activityRootView;

	private AlertDialog internertDialog = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;

			case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力

				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
					// showInternetDialog();
				}

				break;

			case 3: // 提示网络异常的对话框消失
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;
			default:
				break;
			}
		};
	};

	private boolean scan_success = false; // 增加阅读数是否成功

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		//检测网络状态
		if (!NetWorkCheck.isNetworkConnected(this)) {
			InternetDialog internetDialog = new InternetDialog(
					StockCommentActivity.this);
			internetDialog
			.showInternetDialog("网络异常",false);
		}
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils
		.configDefaultLoadFailedImage(R.drawable.ico_default_headview);

		volleyRequestQueue = Volley.newRequestQueue(this);

		pull_to_refresh.setOnFooterRefreshListener(this);
		pull_to_refresh.setOnHeaderRefreshListener(this);
		datas = new ArrayList<HashMap<String, Object>>();
		getBundle();
		sendReadnum(topicUuid);

		childAdapter = new ChildAdapter(getApplicationContext(), datas);
		mv_child_list.setAdapter(childAdapter);

		pageNo = 0;
		showLoadingDialog();
		getComments(pageNo, pageSize, HEAD);

		internetDialog = new InternetDialog(StockCommentActivity.this);
	}

	/**
	 * 获取后台数据
	 * 
	 */
	private void getBundle() {
		// TODO Auto-generated method stub
		first = new HashMap<String, Object>();
		bundle = getIntent().getExtras();
		//创建者的名字
		creatorName = bundle.getString("creatorName");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//创建时间
		creationTime = dateFormat.format(new Date(Long.parseLong(bundle
				.getString("creationTime"))));
		//创建者的头像
		creatorLogoUrl = bundle.getString("creatorLogoUrl");
		//股票代码
		securitySymbol = bundle.getString("securitySymbol");
		//股票名称
		securityName = bundle.getString("securityName");
		//股票名称
		securityType = bundle.getString("securityType");
		//评论内容
		content = bundle.getString("content");
		//点赞数
		praise_num = bundle.getString("likeNum");
		//评论数
		comment_num = bundle.getString("commentNum");
		//浏览数
		scan_num = bundle.getString("readNum");
		//分享数
		share_num = bundle.getString("transpondNum");
		//创建者Uuid
		creatorUuid = bundle.getString("creatorUuid");
		//话题Uuid
		topicUuid = bundle.getString("topicUuid");
		//是否点赞
		liked = bundle.getString("liked");
		medias = bundle.getString("medias");
		//转发话题
		refTopic = bundle.getString("refTopic");
		//是否关注
		followed = bundle.getString("followed");
	}

	/**
	 * 获取设置comments
	 * 
	 * @param pageNo
	 * @param pageSize
	 */
	private void getComments(int pageNo, int pageSize, final int type) {
		if (type == HEAD) {
			datas.clear();
			first.put("type", 0);
			datas.add(0, first);
			childAdapter.notifyDataSetChanged();
		}
		String url = AppConfig.URL_TOPIC + "commentList/" + topicUuid
				+ ".json?access_token="
				+ RsSharedUtil.getString(this, "access_token") + "&pageNo="
				+ pageNo + "&pageSize=" + pageSize;

		Log.d("coooom", url);
		StringRequest jsonArrayRequest = new StringRequest(Request.Method.GET,
				url, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				JSONArray response;
				try {
					response = new JSONArray(arg0.toString());
					for (int i = 0; i < response.length(); i++) {
						HashMap<String, Object> data = new HashMap<String, Object>();
						Iterator<String> jIterator;
						jIterator = response.getJSONObject(i).keys();
						while (jIterator.hasNext()) {
							String key = jIterator.next();
							data.put(key, response.getJSONObject(i)
									.get(key));
						}
						data.put("type", 1);
						if (!datas.contains(data))
							datas.add(data);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// childAdapter = new ChildAdapter(
				// getApplicationContext(), datas);
				// mv_child_list.setAdapter(new ChildAdapter(
				// StockCommentActivity.this, datas));

				childAdapter.notifyDataSetChanged();

				mv_child_list
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(
							AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// 如果是Child,即二级评论，弹出复制举报
						if (Integer.parseInt(datas
								.get(position).get("type")
								.toString()) == 1) {

							showAlertDialog(position);

						}

						return false;
					}
				});

				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					Log.d("error.statuCode()", error.statuCode() + "");
					JSONObject jsonObject = new JSONObject(error.data());
					ToastUtils.showToast(StockCommentActivity.this,
							jsonObject.getString("description"));

				} catch (Exception e) {
				}

				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);

			}
		});
		Log.d("dj_datas.size()", datas.size() + "");
		volleyRequestQueue.add(jsonArrayRequest);
	}

	/**
	 * 弹出复制举报的对话框
	 */
	private void showAlertDialog(final int position) {
		final AlertDialog mAlertDialog = new AlertDialog.Builder(this).create();
		mAlertDialog.show();
		mAlertDialog.setCancelable(true);
		mAlertDialog.getWindow().setContentView(R.layout.popup_copy_report);
		mAlertDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView tv_copy = (TextView) mAlertDialog.findViewById(R.id.tv_copy);
		TextView tv_report = (TextView) mAlertDialog
				.findViewById(R.id.tv_report);
		TextView tv_cancel = (TextView) mAlertDialog
				.findViewById(R.id.tv_cancel);
		// 复制功能
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String content = datas.get(position).get("content").toString();

				ClipboardManager copy = (ClipboardManager) StockCommentActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
				mAlertDialog.dismiss();
				internetDialog.showInternetDialog("已复制", true);
			}
		});

		// 举报功能
		tv_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAlertDialog.dismiss();

				// showReportDialog();
				internetDialog.showInternetDialog("已举报", true);

			}

		});

		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAlertDialog.dismiss();
			}
		});

		// 设置蒙版的黑暗度
		WindowManager.LayoutParams lp = mAlertDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.4f;

		// 令alertDialog的宽度match_parent,否则左右两边会留有空隙
		lp.width = getWindowManager().getDefaultDisplay().getWidth();
		lp.height = LayoutParams.WRAP_CONTENT;

		mAlertDialog.getWindow().setAttributes(lp);
		mAlertDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		// 设置动画效果
		mAlertDialog.getWindow().setWindowAnimations(
				R.style.mypopwindow_anim_style);
	}

	/**
	 * 弹出举报对话框
	 */
	private void showReportDialog() {
		reportDialog = new AlertDialog.Builder(StockCommentActivity.this)
		.create();
		reportDialog.show();
		reportDialog.getWindow().setContentView(R.layout.item_toast_popup);
		reportDialog.setCancelable(false);

		ImageView iv = (ImageView) reportDialog.getWindow().findViewById(
				R.id.iv_item);
		TextView tv = (TextView) reportDialog.getWindow().findViewById(
				R.id.tv_item);

		iv.setImageResource(R.drawable.ico_gantanhao);
		tv.setText(getResources().getString(R.string.already_report));

		/**
		 * 设置黑暗度
		 */
		// 设置蒙版的黑暗度
		WindowManager.LayoutParams lp = reportDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.0f;

		reportDialog.getWindow().setAttributes(lp);
		reportDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		Message msg = new Message();
		msg.what = 0;
		handler.sendMessageDelayed(msg, 2000);

	}

	/*
	 * 对话题点赞功能
	 * 
	 * @param iv_praise
	 * 
	 * @param tv_praise_num
	 */
	private void praise(final ImageView iv_praise, final TextView tv_praise_num) {
		iv_praise.setClickable(false);
		int praiseNum = Integer.parseInt(tv_praise_num.getText().toString());
		praiseNum++;
		tv_praise_num.setText("" + praiseNum);
		iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
		String url = AppConfig.URL_TOPIC + "like.json?topicUuid=" + topicUuid;
		url = url + "&access_token="
				+ RsSharedUtil.getString(this, "access_token");
		Log.d("点赞url", "url" + url);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// topics.get(position).put("liked", true);
				iv_praise.setClickable(true);
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				int praiseNum = Integer.parseInt(tv_praise_num
						.getText().toString());
				praiseNum--;
				tv_praise_num.setText("" + praiseNum);
				if (liked.equals("true")) {
					iv_praise
					.setImageResource(R.drawable.btn_dianzan_selected_cl);
				} else {
					iv_praise
					.setImageResource(R.drawable.btn_dianzan_normal_cl);
				}
				iv_praise.setClickable(true);

				try {
					Log.d("error.statuCode()", error.statuCode() + "");
					JSONObject jsonObject = new JSONObject(error.data());
					// ToastUtils.showToast(getApplicationContext(),
					// jsonObject.getString("description"));

					showInternetDialog(jsonObject
							.getString("description"));

				} catch (Exception e) {
				}
			}
		});
		volleyRequestQueue.add(stringRequest);
	}

	/*
	 * 对评论点赞功能
	 * 
	 * @param iv_praise
	 * 
	 * @param tv_praise_num
	 * 
	 * @param position
	 * 
	 * @param topics
	 */
	private void praiseItem(final ImageView iv_praise,
			final TextView tv_praise_num, final int position,
			final List<HashMap<String, Object>> topics) {

		iv_praise.setClickable(false);
		int praiseNum = Integer.parseInt(tv_praise_num.getText().toString());
		praiseNum++;
		tv_praise_num.setText("" + praiseNum);
		iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
		String uuid = topics.get(position).get("commentId").toString();
		String url = AppConfig.URL_TOPIC + "comment/like.json?topicCommentId="
				+ uuid;
		url = url + "&access_token="
				+ RsSharedUtil.getString(this, "access_token");

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				topics.get(position).put("liked", true);
				iv_praise.setClickable(true);
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				int praiseNum = Integer.parseInt(tv_praise_num
						.getText().toString());
				praiseNum--;
				tv_praise_num.setText("" + praiseNum);
				if (topics.get(position).get("liked").toString()
						.equals("true")) {
					iv_praise
					.setImageResource(R.drawable.btn_dianzan_selected_cl);
				} else {
					iv_praise
					.setImageResource(R.drawable.btn_dianzan_normal_cl);
				}
				iv_praise.setClickable(true);

				try {
					Log.d("error.statuCode()", error.statuCode() + "");
					JSONObject jsonObject = new JSONObject(error.data());
					// ToastUtils.showToast(getApplicationContext(),
					// jsonObject.getString("description"));

					showInternetDialog(jsonObject
							.getString("description"));

				} catch (Exception e) {
				}
			}
		});
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void Toast(Context context, int view, View rl, int position) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);
		TextView tv_item = (TextView) contentView.findViewById(R.id.tv_item);
		switch (position) {
		case 1:
			tv_item.setText("已复制");
			break;
		case 2:
			tv_item.setText("已收藏");
			break;
		case 3:
			tv_item.setText("已举报");
			break;
		case 4:
			tv_item.setText("取消收藏");
			break;
		}
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		// 获取xoff
		int xpos = manager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;
		// popwindow位置
		popupWindow.showAtLocation(rl, Gravity.CENTER, 0, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(final Context context, int view, int position) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		// popwindow位置
		popupWindow.showAtLocation(rl_parent, Gravity.BOTTOM, 0, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		// 转发
		TextView tv_transmit = (TextView) contentView
				.findViewById(R.id.tv_transmit);
		// 复制
		TextView tv_copy = (TextView) contentView.findViewById(R.id.tv_copy);

		// 收藏
		TextView tv_collect = (TextView) contentView
				.findViewById(R.id.tv_collect);
		// 举报
		TextView tv_report = (TextView) contentView
				.findViewById(R.id.tv_report);
		// 取消
		TextView tv_cancel = (TextView) contentView
				.findViewById(R.id.tv_cancel);
		if (creatorUuid.equals(RsSharedUtil.getString(getApplicationContext(), AppConfig.UUID))) {
			tv_collect.setVisibility(View.GONE);
			tv_report.setVisibility(View.GONE);
		}
		// 转发监听
		tv_transmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Bundle bundle = new Bundle();
				// bundle.putString("surveyUuid", creatorUuid);
				// 把话题id传过去转发的Activity，将作为下一级的转发源id
				bundle.putString("refUuid", topicUuid);
				bundle.putString("creatorName", creatorName);
				bundle.putString("medias", medias);
				// medias标志，传过去的时候便不用处理medias;
				bundle.putString("mediasFlag", "flag");
				bundle.putString("securitySymbol", securitySymbol);
				bundle.putString("content", content);

				Intent intent = new Intent(getApplicationContext(),
						TranspondActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);

				// Toast(context, R.layout.item_toast_popup, rLayout, 0);

				popupWindow.dismiss();
			}
		});
		// 复制监听
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ClipboardManager copy = (ClipboardManager) StockCommentActivity.this
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
				popupWindow.dismiss();
				internetDialog.showInternetDialog("已复制", true);
			}
		});
		if (followed.equals("true")) {
			tv_collect.setText("取消收藏");
		}
		if (followed.equals("false"))
			tv_collect.setText("收藏");
		// 收藏监听
		tv_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String url = AppConfig.URL_USER + "topic.json?access_token=";
				url = url
						+ RsSharedUtil.getString(StockCommentActivity.this,
								"access_token") + "&uuid=" + topicUuid;
				StringRequest stringRequest = new StringRequest(url, null,
						new Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("dj_collct2", response.toString());
						// TODO Auto-generated method stub
						if (response.equals("true")) {
							Toast(StockCommentActivity.this,
									R.layout.item_toast_popup,
									rl_parent, 2);
							followed = "true";
						}
						if (response.equals("false")) {
							Toast(StockCommentActivity.this,
									R.layout.item_toast_popup,
									rl_parent, 4);
							followed = "false";
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("dj_collect2", error.toString());
					}
				});
				volleyRequestQueue.add(stringRequest);
				// Toast(context, R.layout.item_toast_popup, rLayout, 2);
				popupWindow.dismiss();
			}
		});
		// 举报监听
		tv_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast(context, R.layout.item_toast_popup, rl_parent, 3);
				popupWindow.dismiss();
			}
		});
		// 取消监听
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
	}

	// 监听物理返回键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("dj_return", "return");
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("scan_success", scan_success);
			bundle.putInt("add_comment_count", add_comment_count);
			intent.putExtras(bundle);
			setResult(4, intent);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@OnClick({ R.id.rl_return, R.id.rl, R.id.tv_send })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			Log.d("dj_return", "return");
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("scan_success", scan_success);
			bundle.putInt("add_comment_count", add_comment_count);
			intent.putExtras(bundle);
			setResult(4, intent);
			finish();
			break;

		case R.id.tv_send:
			String content = et_comment.getText().toString();
			et_comment.setText("");
			publishComment(content);

			Log.d("dj_send", "sendClick");
			/**
			 * 将键盘和评论框隐藏
			 */
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			new Thread(new Runnable() {

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessageDelayed(msg, 200);
				}
			}) {
			}.start();
			break;
		case R.id.rl:

			hideKyyboard(v);

			break;
		default:
			break;
		}
	}

	/**
	 * 发表评论
	 * 
	 * @param content
	 */
	private String comment_category = ""; // 评论的类别，默认为二级评论，三级评论为three_grade
	private String comment_topicUuid = ""; // 评论接口需要topicUuid
	private String reply_to_name = ""; // 三级评论需要回复对象的名字

	private void publishComment(String my_content) {
		String url = AppConfig.URL_TOPIC + "comment/add.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");

		Log.d("dj_comment", url);
		JSONObject params = new JSONObject();
		try {
			params.put("topicUuid", comment_topicUuid);
			if (comment_category.equals("three_grade")) { // category如果是three_grade，表明是三级评论
				params.put("content", "回复" + reply_to_name + "：" + my_content); // 需要加上诸如回复SOLA：这一部分
			} else {
				params.put("content", my_content);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("dj_params", params.toString());

		showLoadingDialog();

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// 评论成功则增加评论数
				add_comment_count++;
				comment_num = Integer.parseInt(comment_num) + 1 + "";
				pageNo = 0;
				getComments(pageNo, pageSize, HEAD);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				try {
					Log.d("error.statuCode()", error.statuCode() + "");
					JSONObject jsonObject = new JSONObject(error.data());
					ToastUtils.showToast(StockCommentActivity.this,
							jsonObject.getString("description"));

				} catch (Exception e) {

					ToastUtils.showToast(
							StockCommentActivity.this,
							getResources().getString(
									R.string.unknown_error));
				}
			}
		});

		volleyRequestQueue.add(stringRequest);
	}

	// 评论Adapter
	public class ChildAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> mList;
		private LayoutInflater mInflater;
		private Context mContext;

		public ChildAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			mInflater = LayoutInflater.from(context);
			mContext = context;
			this.mList = list;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		// 父评论viewholder
		class ViewHolderParent {
			RelativeLayout rl;
			// 点赞
			ImageView iv_praise;
			// 头像
			CircleImageView ci_face;
			// 名字
			TextView tv_name;
			// 点赞数
			TextView tv_praise_num;
			//点赞
			RelativeLayout rl_praise1;
			// 评论
			RelativeLayout rl_comment1;
			// 分享
			RelativeLayout rl_share1;
			// 更多
			ImageView iv_more;
			// 时间
			TextView tv_time;
			// 内容
			TextView tv_detial;
			// // 分享数
			// TextView tv_share_num;
			// 评论数
			TextView tv_comment_num;
			// 浏览数
			TextView tv_scan_num;
			// 图片
			NoScrollGridView gridView;
			// 转发
			LinearLayout ll_transpon;
			// 创建人姓名
			TextView tv_creator_name;
			// 创建人内容
			TextView tv_creator_content;
			// 股票代码
			TextView tv_securitySymbol;
			// 股票简称
			TextView tv_securityName;
			// 图片列表
			ImageView iv_creator_face;
		}

		class ViewHolderChild {
			RelativeLayout rl;
			CircleImageView ci_face;
			TextView tv_name;
			TextView tv_time;
			TextView tv_detial;
			TextView tv_praise_num;
			ImageView iv_praise;
			ImageView iv_comment;
		}

		ViewHolderParent viewHolderParent;
		ViewHolderChild viewHolderChild;

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			Log.d("dj_mList.size()", mList.size() + "");
			// 无评论的时候显示无评论提示界面
			if (mList.size() < 2) {
				ll_wupinglun.setVisibility(View.VISIBLE);
			} else {
				ll_wupinglun.setVisibility(View.GONE);
			}
			int type = getItemViewType(position);
			// 加载不同的view
			if (convertView == null) {
				if (type == 0) {
					viewHolderParent = new ViewHolderParent();
					convertView = mInflater.inflate(
							R.layout.item_comment_stock, null);

					viewHolderParent.rl = (RelativeLayout) convertView
							.findViewById(R.id.rl);

					viewHolderParent.tv_securityName = (TextView) convertView
							.findViewById(R.id.tv_securityName);
					viewHolderParent.tv_securitySymbol = (TextView) convertView
							.findViewById(R.id.tv_symbol);
					viewHolderParent.ci_face = (CircleImageView) convertView
							.findViewById(R.id.ci_face);
					viewHolderParent.iv_praise = (ImageView) convertView
							.findViewById(R.id.iv_praise);
					viewHolderParent.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					viewHolderParent.tv_praise_num = (TextView) convertView
							.findViewById(R.id.tv_praise_num);
					viewHolderParent.rl_praise1 = (RelativeLayout) convertView.findViewById(R.id.rl_praise1);
					viewHolderParent.rl_comment1 = (RelativeLayout) convertView
							.findViewById(R.id.rl_comment1);
					viewHolderParent.rl_share1 = (RelativeLayout) convertView
							.findViewById(R.id.rl_share);
					viewHolderParent.iv_more = (ImageView) convertView
							.findViewById(R.id.iv_more);
					viewHolderParent.tv_time = (TextView) convertView
							.findViewById(R.id.tv_time);
					viewHolderParent.tv_detial = (TextView) convertView
							.findViewById(R.id.tv_comment_content);
					viewHolderParent.tv_comment_num = (TextView) convertView
							.findViewById(R.id.tv_comment_num);
					viewHolderParent.tv_scan_num = (TextView) convertView
							.findViewById(R.id.tv_scan_num);
					viewHolderParent.ll_transpon = (LinearLayout) convertView
							.findViewById(R.id.ll_transpon);
					viewHolderParent.tv_creator_name = (TextView) convertView
							.findViewById(R.id.tv_creator_name);
					viewHolderParent.tv_creator_content = (TextView) convertView
							.findViewById(R.id.tv_creator_content);
					viewHolderParent.iv_creator_face = (ImageView) convertView
							.findViewById(R.id.iv_creator_face);
					convertView.setTag(viewHolderParent);
				} else {
					viewHolderChild = new ViewHolderChild();
					convertView = mInflater.inflate(
							R.layout.comment_child_item, null);

					viewHolderChild.rl = (RelativeLayout) findViewById(R.id.rl);

					viewHolderChild.ci_face = (CircleImageView) convertView
							.findViewById(R.id.ci_face);
					viewHolderChild.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					viewHolderChild.tv_time = (TextView) convertView
							.findViewById(R.id.tv_time);
					viewHolderChild.tv_detial = (TextView) convertView
							.findViewById(R.id.tv_detial);
					viewHolderChild.tv_praise_num = (TextView) convertView
							.findViewById(R.id.tv_praise_num);
					viewHolderChild.iv_praise = (ImageView) convertView
							.findViewById(R.id.iv_praise);
					viewHolderChild.iv_comment = (ImageView) convertView
							.findViewById(R.id.iv_comment);
					convertView.setTag(viewHolderChild);
				}
			}
			if (type == 0) {
				viewHolderParent = (ViewHolderParent) convertView.getTag();

				viewHolderParent.rl.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						hideKyyboard(arg0);
					}
				});

				// 头像点击和设置
				bitmapUtils.display(viewHolderParent.ci_face, creatorLogoUrl);
				/*
				 * ImageLoader.getInstance().displayImage(creatorLogoUrl,
				 * viewHolderParent.ci_face);
				 */
				viewHolderParent.ci_face
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (creatorUuid.equals(RsSharedUtil.getString(
								StockCommentActivity.this,
								AppConfig.UUID))) {
							Log.d("dj_uuid", creatorUuid);
							Intent intent = new Intent();
							intent.setClass(StockCommentActivity.this,
									MyProfileActivity.class);
							startActivity(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid", creatorUuid);
							Log.d("dj_uuid", creatorUuid);
							bundle.putString("userName", creatorName);
							Intent intent = new Intent();
							intent.setClass(
									StockCommentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				});

				// 股票简称点击和监听-->跳到个股详情
				viewHolderParent.tv_securityName.setText(securityName);
				viewHolderParent.tv_securityName
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("symbol",securitySymbol );
						map.put("shortname", securityName);
						map.put("securityType", securityType);
						stocks.add(map);
						Intent intent = new Intent(getApplicationContext(), MyStockDetailsActivity.class);
						intent.putExtra("stocks", stocks);
						intent.putExtra("position", 0);
						startActivity(intent);
					}
				});
				// 股票代码点击和监听-->跳到个股详情
				viewHolderParent.tv_securitySymbol.setText(securitySymbol);
				viewHolderParent.tv_securitySymbol.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("symbol",securitySymbol );
						map.put("shortname", securityName);
						map.put("securityType", securityType);
						stocks.add(map);
						Intent intent = new Intent(getApplicationContext(), MyStockDetailsActivity.class);
						intent.putExtra("stocks", stocks);
						intent.putExtra("position", 0);
						startActivity(intent);
					}
				});

				// 名字点击和设置
				viewHolderParent.tv_name.setText(creatorName);
				viewHolderParent.tv_name
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (creatorUuid.equals(RsSharedUtil.getString(
								StockCommentActivity.this,
								AppConfig.UUID))) {
							Intent intent = new Intent();
							intent.setClass(StockCommentActivity.this,
									MyProfileActivity.class);
							startActivity(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid", creatorUuid);
							bundle.putString("userName", creatorName);
							Intent intent = new Intent();
							intent.setClass(
									StockCommentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}

					}
				});

				// 点赞数
				viewHolderParent.tv_praise_num.setText(praise_num);
				// 评论
				viewHolderParent.rl_comment1
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (!BtnClickUtils.isFastDoubleClick()) {
							// 评论发表需要topicUuid
							comment_topicUuid = getIntent().getExtras()
									.getString("topicUuid");
							// 评论有两类，此为二级评论
							comment_category = "";

							// 输入框出现和消失对应的动画
							Animation showAnimation = AnimationUtils
									.loadAnimation(
											StockCommentActivity.this,
											R.anim.et_show_anim);
							Animation hiddenAnimation = AnimationUtils
									.loadAnimation(
											StockCommentActivity.this,
											R.anim.et_hidden_anim);
							showAnimation.setFillAfter(true);
							hiddenAnimation.setFillAfter(true);

							if (rl_et.getVisibility() == View.GONE) {
								rl_et.setVisibility(View.VISIBLE);

								et_comment.requestFocus();

								InputMethodManager imm = (InputMethodManager) et_comment
										.getContext()
										.getSystemService(
												Context.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(0,
										InputMethodManager.SHOW_FORCED);

								rl_et.startAnimation(showAnimation);
							}
							// else {
							// rl_et.setVisibility(View.GONE);
							// rl_et.startAnimation(hiddenAnimation);
							// }
						}

					}
				});
				
				
				//分享内容 
				final String contentString=creatorName+":\n"+content;
				// 分享
				viewHolderParent.rl_share1
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showShare(contentString);
					}
				});

				// 更多
				viewHolderParent.iv_more
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						initMenu(StockCommentActivity.this,
								R.layout.item_comment_more_popup,
								position);
					}
				});

				// 点赞
				boolean like = Boolean.parseBoolean(liked);
				if (like) {
					viewHolderParent.iv_praise
					.setImageResource(R.drawable.btn_dianzan_selected_cl);
				} else {
					viewHolderParent.iv_praise
					.setImageResource(R.drawable.btn_dianzan_normal_cl);
				}
				//点赞
				viewHolderParent.rl_praise1
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (!BtnClickUtils.isFastDoubleClick()) {
							praise(viewHolderParent.iv_praise,
									viewHolderParent.tv_praise_num);
						}
					}
				});
				// 时间
				viewHolderParent.tv_time.setText(creationTime);

				// 内容
				viewHolderParent.tv_detial.setText(content);

				// // 分享数
				// viewHolderParent.tv_share_num.setText(share_num);
				// 评论
				viewHolderParent.tv_comment_num.setText(comment_num);
				// 浏览数
				viewHolderParent.tv_scan_num.setText(scan_num);

				// 转发内容
				// 为空则转发内容的布局消失
				if (refTopic.equals("null")) {
					viewHolderParent.ll_transpon.setVisibility(View.GONE);
				} else {
					final JSONObject refObject;
					try {
						refObject = new JSONObject(refTopic);
						// 设置转发源的创建者名字
						viewHolderParent.tv_creator_name.setText(refObject
								.getString("creatorName") + ":");
						// 设置转发源的content
						viewHolderParent.tv_creator_content.setText(refObject
								.getString("content"));
						// 设置转发源的头像不显示
						viewHolderParent.iv_creator_face
						.setVisibility(View.GONE);

						viewHolderParent.ll_transpon
						.setVisibility(View.VISIBLE);

						// 点击转发内容
						viewHolderParent.ll_transpon
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								try {
									Bundle bundle = new Bundle();
									bundle.putString("securityName",
											securityName);
									bundle.putString("securitySymbol",
											securitySymbol);
									bundle.putString(
											"creatorUuid",
											refObject
											.getString("creatorUuid"));
									bundle.putString(
											"creatorName",
											refObject
											.getString("creatorName"));
									bundle.putString(
											"creationTime",
											refObject
											.getString("creationTime"));
									bundle.putString(
											"creatorLogoUrl",
											refObject
											.getString("creatorLogoUrl"));
									bundle.putString(
											"content",
											refObject
											.getString("content"));
									bundle.putString(
											"likeNum",
											refObject
											.getString("likeNum"));
									bundle.putString(
											"commentNum",
											refObject
											.getString("commentNum"));
									bundle.putString(
											"readNum",
											refObject
											.getString("readNum"));
									bundle.putString(
											"transpondNum",
											refObject
											.getString("transpondNum"));
									bundle.putString(
											"topicUuid",
											refObject
											.getString("topicUuid"));
									bundle.putString("liked", refObject
											.getString("liked"));
									bundle.putString(
											"followed",
											refObject
											.getString("followed"));
									bundle.putString(
											"refTopic",
											refObject
											.getString("refTopic"));
									bundle.putString(
											"medias",
											refObject
											.getString("medias"));
									Intent intent = new Intent(
											getApplicationContext(),
											StockCommentActivity.class);
									intent.putExtras(bundle);

									startActivity(intent);
									finish();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				viewHolderChild = (ViewHolderChild) convertView.getTag();
				viewHolderChild.rl.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						hideKyyboard(arg0);
					}
				});

				// 头像

				bitmapUtils.display(viewHolderChild.ci_face, mList
						.get(position).get("userLogo").toString());
				/*
				 * ImageLoader.getInstance().displayImage(
				 * mList.get(position).get("userLogo").toString(),
				 * viewHolderChild.ci_face);
				 */
				// 名字

				viewHolderChild.ci_face
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (mList
								.get(position)
								.get("userUuid")
								.toString()
								.equals(RsSharedUtil.getString(
										StockCommentActivity.this,
										AppConfig.UUID))) {
							Log.d("dj_uuid", creatorUuid);
							Intent intent = new Intent();
							intent.setClass(StockCommentActivity.this,
									MyProfileActivity.class);
							startActivity(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid", mList
									.get(position).get("userUuid")
									.toString());
							Log.d("dj_uuid", creatorUuid);
							bundle.putString("userName",
									mList.get(position).get("userName")
									.toString());
							Intent intent = new Intent();
							intent.setClass(
									StockCommentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				});
				viewHolderChild.tv_name.setText(mList.get(position)
						.get("userName").toString());
				viewHolderChild.tv_name
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {

						if (mList
								.get(position)
								.get("userUuid")
								.toString()
								.equals(RsSharedUtil.getString(
										StockCommentActivity.this,
										AppConfig.UUID))) {
							Log.d("dj_uuid", creatorUuid);
							Intent intent = new Intent();
							intent.setClass(StockCommentActivity.this,
									MyProfileActivity.class);
							startActivity(intent);
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("uuid", mList
									.get(position).get("userUuid")
									.toString());
							Log.d("dj_uuid", creatorUuid);
							bundle.putString("userName",
									mList.get(position).get("userName")
									.toString());
							Intent intent = new Intent();
							intent.setClass(
									StockCommentActivity.this,
									OtherPeolpeInformationActivity.class);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
				});
				// 时间
				Long time_long = Long.parseLong(mList.get(position).get("date")
						.toString());
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String date = dateFormat.format(new Date(time_long));
				viewHolderChild.tv_time.setText(date);
				// 评论内容
				viewHolderChild.tv_detial.setText(mList.get(position)
						.get("content").toString());
				// 点赞数目
				viewHolderChild.tv_praise_num.setText(mList.get(position)
						.get("up").toString());
				// 点赞
				String liked = mList.get(position).get("liked").toString();
				if (liked.equals("true")) {
					viewHolderChild.iv_praise
					.setImageResource(R.drawable.btn_zan_selectedpng);
				} else {
					viewHolderChild.iv_praise
					.setImageResource(R.drawable.btn_zan_new);
				}
				viewHolderChild.iv_praise
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						if (!BtnClickUtils.isFastDoubleClick()) {
							praiseItem(viewHolderChild.iv_praise,
									viewHolderChild.tv_praise_num,
									position, mList);
						}

					}
				});
				viewHolderChild.iv_comment
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// 评论发表需要topicUuid
						if (!BtnClickUtils.isFastDoubleClick()) {
							comment_topicUuid = getIntent().getExtras()
									.getString("topicUuid");
							// 评论有两类，此为三级评论
							comment_category = "three_grade";
							// 要回复的名字
							reply_to_name = mList.get(position)
									.get("userName").toString();
							// 输入框出现和消失对应的动画
							Animation showAnimation = AnimationUtils
									.loadAnimation(
											StockCommentActivity.this,
											R.anim.et_show_anim);
							Animation hiddenAnimation = AnimationUtils
									.loadAnimation(
											StockCommentActivity.this,
											R.anim.et_hidden_anim);
							showAnimation.setFillAfter(true);
							hiddenAnimation.setFillAfter(true);

							if (rl_et.getVisibility() == View.GONE) {
								rl_et.setVisibility(View.VISIBLE);

								et_comment.requestFocus();

								InputMethodManager imm = (InputMethodManager) et_comment
										.getContext()
										.getSystemService(
												Context.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(0,
										InputMethodManager.SHOW_FORCED);

								rl_et.startAnimation(showAnimation);
							}
							// else {
							// rl_et.setVisibility(View.GONE);
							// rl_et.startAnimation(hiddenAnimation);
							// }
						}

					}
				});
			}
			return convertView;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mList.get(arg0);
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			int type = (Integer) mList.get(position).get("type");
			return type;
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				getComments(++pageNo, pageSize, FOOT);
				pull_to_refresh.onFooterRefreshComplete();
				Log.d("dj_上拉刷新", "true");
			}
		}, 2000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pageNo = 0;
		pull_to_refresh.postDelayed(new Runnable() {
			@Override
			public void run() {
				getComments(pageNo, pageSize, HEAD);
				pull_to_refresh.onHeaderRefreshComplete();
				Log.d("dj_下拉刷新", "true");
			}
		}, 2000);
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	//
	// // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
	// View v = getCurrentFocus();
	//
	// if (isShouldHideInput(v, ev)) {
	// hideSoftInput(v.getWindowToken());
	// }
	// }
	// return super.dispatchTouchEvent(ev);
	// }
	//
	// /**
	// * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	// *
	// * @param v
	// * @param event
	// * @return
	// */
	// private boolean isShouldHideInput(View v, MotionEvent event) {
	// if (v != null && (v instanceof EditText)) {
	// int[] l = { 0, 0 };
	// v.getLocationInWindow(l);
	// int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
	// + v.getWidth();
	// if (event.getX() > left && event.getX() < right
	// && event.getY() > top && event.getY() < bottom) {
	// // 点击EditText的事件，忽略它。
	// return false;
	// } else {
	// return true;
	// }
	// }
	// // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
	// return false;
	// }
	//
	// /**
	// * 多种隐藏软件盘方法的其中一种
	// *
	// * @param token
	// */
	// private void hideSoftInput(IBinder token) {
	// if (token != null) {
	// InputMethodManager im = (InputMethodManager)
	// getSystemService(Context.INPUT_METHOD_SERVICE);
	// im.hideSoftInputFromWindow(token,
	// InputMethodManager.HIDE_NOT_ALWAYS);
	//
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Message msg = new Message();
	// msg.what = 1;
	// handler.sendMessageDelayed(msg, 200);
	// }
	// }) {
	// }.start();
	//
	// }
	// }

	/**
	 * 提示網絡不給力
	 */
	private void showInternetDialog(String msg) {
		internertDialog = new AlertDialog.Builder(this).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_dianzan);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);

		progress_bar.setVisibility(View.GONE);
		iv_tips.setVisibility(View.VISIBLE);
		tv_message.setText(msg);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessageDelayed(msg, 2000);
			}
		}).start();

	}

	// 分享的方法
	private void showShare(String content) {
		background.setAlpha(0.7f);
		popupWindow = new ShareUtils(StockCommentActivity.this, rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				background.setAlpha(0.0f);

			}
		});
	}

	private void sendReadnum(String topicUUID) {
		String url = AppConfig.URL_TOPIC + "addReadNum.json?access_token="
				+ RsSharedUtil.getString(this, "access_token") + "&topicUuid="
				+ topicUUID;
		Log.d("阅读量url", url);
		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				scan_success = true;
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				scan_success = false;
			}
		}

				);
		volleyRequestQueue.add(stringRequest);
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
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

	private void hideKyyboard(View v) {
		if (rl_et.getVisibility() == View.VISIBLE) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessageDelayed(msg, 200);
				}
			}) {
			}.start();
			// rl_et.setVisibility(View.GONE);
			// rl_et.startAnimation(hiddenAnimation);

		}

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

}
