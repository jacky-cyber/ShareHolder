package com.example.shareholders.activity.survey;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.VoidRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.shop.InteractActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.EnterpriseEntity;
import com.example.shareholders.jacksonModel.personal.PersonalInformation;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.NetWorkCheck;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.example.shareholders.view.DialogManager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("ResourceAsColor")
@ContentView(R.layout.activity_detail_survey)
public class DetailSurveyActivity extends Activity {
	
	private BitmapUtils bitmapUtils=null;
	
	// 收藏
	@ViewInject(R.id.iv_collect)
	private ImageView iv_collect;
	// 是否收藏
	private boolean isFollow;
	// 是否为发起人
	private boolean isOriginator = false;
	// 调研首页传过来的uuid
	private String uuid;
	// 简介
	@ViewInject(R.id.tv_text)
	private TextView tv_text;
	// 下拉按钮
	@ViewInject(R.id.iv_drop_up_down)
	private ImageView iv_drop_up_down;
	// 调研图像
	@ViewInject(R.id.iv_title)
	private ImageView iv_title;
	
	private String userUuid="";
	// 头像
	@ViewInject(R.id.ci_face)
	private ImageView ci_face;
	// 地点
	@ViewInject(R.id.tv_location)
	private TextView tv_location;
	// 收藏按钮
	@ViewInject(R.id.rl_shoucang)
	private RelativeLayout rl_shoucang;
	// 两种状态
	@ViewInject(R.id.ll_public)
	private LinearLayout ll_public;
	@ViewInject(R.id.iv_public)
	private ImageView iv_public;
	@ViewInject(R.id.tv_public)
	private TextView tv_public;
	// 调研活动状态
	@ViewInject(R.id.tv_state)
	private TextView tv_state;
	// 调研4种状态
	private static String ENROLLING = "ENROLLING";
	private static String ENROLLEND = "ENROLLEND";
	private static String SURVEYING = "SURVEYING";
	private static String SURVEYEND = "SURVEYEND";
	// 调研说明
	@ViewInject(R.id.tv_detail1)
	private TextView tv_detail1;
	// 电话号码
	@ViewInject(R.id.tv_phone_content)
	private TextView tv_phone_content;
	// 联系人
	@ViewInject(R.id.tv_link_man_content)
	private TextView tv_link_man_content;
	// 调研名称
	@ViewInject(R.id.tv_survey_name)
	private TextView tv_survey_name;
	// 发起人名称
	@ViewInject(R.id.tv_name)
	private TextView tv_name;
	// 浏览人数
	@ViewInject(R.id.tv_scan_num)
	private TextView tv_scan_num;
	// 报名人数
	@ViewInject(R.id.tv_registration_num)
	private TextView tv_registration_num;
	// 活动时间段
	@ViewInject(R.id.tv_time)
	private TextView tv_time;
	// 状态
	private boolean status[];
	// 调研公司
	@ViewInject(R.id.mv_enterprises)
	private MyListView mv_enterprises;
	private AlertDialog internetDialog = null;
	// 现场互动
	@ViewInject(R.id.ll_easy_touch)
	private LinearLayout ll_easy_touch;
	// content是否扩展
	private boolean expended = false;
	private String messageCount;// 未读的数量
	// 报名状态
	private String enrollState = "";
	// 活动状态
	private String state = "";
	String user_logo="";
	// 四种报名状态
	private static String CANCELED = "CANCELED";
	private static String ENROLL = "ENROLL";
	private static String SUCCESS = "SUCCESS";
	private static String FAILED = "FAILED";
	private DialogManager dialogManager;
	DbUtils dbUtils;
	@ViewInject(R.id.rl)
	private View rl;

	// 聊天室id
	private String groupId;

	@ViewInject(R.id.rl_shallow)
	private View rl_shoallow;

	private ShareUtils popupWindow;
	
	private String surveyNameString="";
	
	PersonalInformation personalInformation;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				dialogManager.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewUtils.inject(this);
		
		//设置调研人的默认头像
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils .configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		//检测网络连接状态
		if (!NetWorkCheck.isNetworkConnected(this)) {
			InternetDialog internetDialog = new InternetDialog(
					DetailSurveyActivity.this);
			internetDialog
			.showInternetDialog("网络异常",false);
		}
		Bundle bundle = getIntent().getExtras();
		uuid = bundle.getString("uuid");
		dialogManager = new DialogManager(DetailSurveyActivity.this);
		dbUtils = DbUtils.create(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// 获取调研详情数据
		showLoadingDialog();
		new AsyncDetail().execute(uuid);

		super.onResume();
	}

	/**
	 * 进入画面后立即显示加载旋转
	 */
	private void showLoadingDialog() {
		// 如果网络不可用，加载的提示不显示
		if (!NetWorkCheck.isNetworkConnected(this)) {
			return;
		}
		internetDialog = new AlertDialog.Builder(this).create();
		internetDialog.show();
		internetDialog.setCancelable(true);

		Window window = internetDialog.getWindow();
		window.setContentView(R.layout.dialog_no_internet);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	}

	/**
	 * 关注和取消关注
	 * 
	 * @param uuid
	 */
	private void Follow(String uuid) {
		String url = AppConfig.URL_USER + "survey.json?access_token=";
		url = url + RsSharedUtil.getString(this, "access_token");
		Log.d("关注/取消关注", url);
		JSONObject params = new JSONObject();
		try {
			params.put("follow", !isFollow);
			params.put("surveyUuid", uuid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VoidRequest jsonObjectRequest = new VoidRequest(Request.Method.POST,
				url, params, new Response.Listener<Void>() {

			@Override
			public void onResponse(Void response) {
				// TODO Auto-generated method stub
				if (!isFollow) {
					dialogManager.ShowBlackDialog("已收藏",
							R.drawable.ico_gou1);

				} else {
					dialogManager.ShowBlackDialog("取消收藏成功",
							R.drawable.ico_gou1);
				}
				isFollow = !isFollow;
				Message message = new Message();
				message.what = 1;
				handler.sendMessageDelayed(message, 1000);

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("关注/取消关注", error.toString());

				// TODO Auto-generated method stub
				try {
					Log.d("error.statuCode()", error.statuCode() + "");
					try {
						JSONObject jsonObject = new JSONObject(error
								.data());
						Log.d("error_description",
								jsonObject.getString("description"));
						;

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d("error_Exception", e.toString());
					}
					if (!isFollow) {
						iv_collect
						.setImageResource(R.drawable.btn_shocuang_normal);

					} else {
						iv_collect
						.setImageResource(R.drawable.btn_shocuang_selected);
					}

				} catch (Exception e) {

				}
			}
		}

				);
		// 4.请求对象放入请求队列
		MyApplication.getRequestQueue().add(jsonObjectRequest);
	}

	/**
	 * 报名参加调研
	 * 
	 * @param uuid
	 */
	@SuppressLint("ResourceAsColor")
	private void Enroll(String uuid) {
		String url = AppConfig.URL_SURVEY + "enroll.json?access_token=";
		url = url + RsSharedUtil.getString(this, "access_token")
				+ "&surveyUuid=" + uuid;
		VoidRequest jsonObjectRequest = new VoidRequest(Request.Method.GET,
				url, null, new Response.Listener<Void>() {
			@Override
			public void onResponse(Void response) {
				// TODO Auto-generated method stub

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
		}

				);
		// 4.请求对象放入请求队列
		jsonObjectRequest.setTag("Enroll");
		MyApplication.getRequestQueue().add(jsonObjectRequest);

	}

	@OnClick({ R.id.rl_shallow, R.id.rl_return,
		R.id.ll_mail, R.id.ll_signup_list, R.id.ll_easy_touch,
		R.id.rl_shoucang, R.id.ll_public, R.id.ll_comment, R.id.rl_share,
		R.id.ci_face, R.id.tv_name ,R.id.tv_text,R.id.tv_phone_content})
	private void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("uuid", uuid);
		intent.putExtras(bundle);
		switch (v.getId()) {
		case R.id.ci_face:
			personCenter();
			break;
		case R.id.tv_name:
			personCenter();
			break;

			// 分享

		case R.id.rl_share:
			rl_shoallow.setVisibility(View.VISIBLE);
			popupWindow = new ShareUtils(DetailSurveyActivity.this, rl,surveyNameString);

			popupWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					rl_shoallow.setVisibility(View.GONE);

				}
			});
			break;
			//点击电话号码拨号
		case R.id.tv_phone_content:
			if (enrollState.equals(SUCCESS)||isOriginator) {
				Intent CallIntent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+tv_phone_content.getText().toString()));
				//intent.addCategory("android.intent.category.DEFAULT");内部会自动添加类别，
//				CallIntent.setData(Uri.parse("tel:"+tv_phone_content));
				//激活Intent
				startActivity(CallIntent);
			}
			break;
		case R.id.ll_comment:
			intent.setClass(this, SurveyCommentActivity.class);
			startActivity(intent);
			break;
			// 报名或者编辑
		case R.id.ll_public:
			if (!isOriginator) {
				// 活动状态
				ll_public.setBackgroundResource(R.color.ll_background_thin);
				if (state.equals(ENROLLING)) {

					Log.d("enrollstate", enrollState);
					// SignUp();

					if (enrollState.equals(CANCELED)) {// 未报名
						InternetDialog internetDialog = new InternetDialog(
								DetailSurveyActivity.this);
						internetDialog
						.showInternetDialog(
								DetailSurveyActivity.this
								.getResources()
								.getString(
										R.string.enroll_under_audlt),
										true);

					} else if (enrollState.equals(SUCCESS)) {// 已报名成功
						InternetDialog internetDialog = new InternetDialog(
								DetailSurveyActivity.this);
						internetDialog.showInternetDialog(
								DetailSurveyActivity.this.getResources()
								.getString(R.string.enroll_already),
								true);
					} else if (enrollState.equals(FAILED)) {// 已满人
						InternetDialog internetDialog = new InternetDialog(
								DetailSurveyActivity.this);
						internetDialog
						.showInternetDialog(
								DetailSurveyActivity.this
								.getResources().getString(
										R.string.enroll_full),
										false);
					} else if (enrollState.equals(ENROLL)) {// 待审核
						InternetDialog internetDialog = new InternetDialog(
								DetailSurveyActivity.this);
						internetDialog
						.showInternetDialog(
								DetailSurveyActivity.this
								.getResources()
								.getString(
										R.string.enroll_under_audlt),
										true);
					}

					if (enrollState.equals("CANCELED"))
						Enroll(uuid);
				} else {
					// dialogManager.ShowBlackDialog("报名时间已结束",
					// R.drawable.ico_gantanhao0);
					InternetDialog internetDialog = new InternetDialog(
							DetailSurveyActivity.this);
					internetDialog.showInternetDialog("报名时间已结束", false);
				}
			} else {
				if (state.equals(SURVEYEND)) {
					// dialogManager.ShowBlackDialog("活动已结束",
					// R.drawable.ico_gantanhao0);
					InternetDialog internetDialog = new InternetDialog(
							DetailSurveyActivity.this);
					internetDialog.showInternetDialog("报名时间已结束", false);
				} else {
					intent.setClass(this, ActivityCreateActivity.class);
					intent.putExtra("sign", 1);
					startActivity(intent);
				}
			}
			break;
			// 收藏
		case R.id.rl_shoucang:
			if (!isFollow)
				iv_collect.setImageResource(R.drawable.btn_shocuang_selected);
			else
				iv_collect.setImageResource(R.drawable.btn_shocuang_normal);

			Follow(uuid);
			break;
		case R.id.rl_return:
			finish();
			break;
			// content下拉扩展
		case R.id.tv_text:
			if (expended == false) {
				// DropEditText.expandTextView(tv_text);
				tv_text.setMaxLines(10);
				iv_drop_up_down.setImageResource(R.drawable.btn_xiala_selected);
				Log.d("milk7","downt");
				expended = true;
			} else {
				// DropEditText.expandTextView(tv_text);
				tv_text.setMaxLines(4);
				iv_drop_up_down.setImageResource(R.drawable.btn_xiala_normal);
				Log.d("milk8","upt");
				expended = false;
			}
			break;
			// 通知
		case R.id.ll_mail:
			if (!isOriginator && !enrollState.equals(SUCCESS)) {
				// dialogManager.ShowBlackDialog("您尚未报名",
				// R.drawable.ico_gantanhao2);
				InternetDialog internetDialog = new InternetDialog(
						DetailSurveyActivity.this);
				internetDialog.showInternetDialog("您尚未报名", false);
			} else {
				bundle.putString("messageCount", messageCount);
				bundle.putBoolean("isOrigin", isOriginator);
				intent.setClass(this, InformActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			}
			break;
			// 报名列表
		case R.id.ll_signup_list:
			if (!isOriginator && !enrollState.equals(SUCCESS)) {
				// dialogManager.ShowBlackDialog("您尚未报名",
				// R.drawable.ico_gantanhao2);
				InternetDialog internetDialog = new InternetDialog(
						DetailSurveyActivity.this);
				internetDialog.showInternetDialog("您尚未报名", false);
			} else {
				Bundle bundlee=new Bundle();
				bundlee.putBoolean("isOriginator", isOriginator);
				intent.putExtra("isOriginator", bundlee);
				intent.setClass(this, UserJoinActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_easy_touch:
			Intent intent2 = new Intent(DetailSurveyActivity.this,
					InteractActivity.class);
			intent2.putExtra("groupId", groupId);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	// 跳转去个人中心
	private void personCenter() {
		if(!NetWorkCheck.isNetworkConnected(this))
		{
			Toast.makeText(this, "请先开启网络~~", 2000).show();
		}
		else {
			//如果是自己，则跳去个人中心
			if (uuid.equals(RsSharedUtil.getString(DetailSurveyActivity.this,
					AppConfig.UUID))) {
				Intent intent = new Intent();
				intent.setClass(DetailSurveyActivity.this, MyProfileActivity.class);
				startActivity(intent);
			} 
			else 
			{
				Bundle bundle = new Bundle();
				bundle.putString("uuid", userUuid);
				bundle.putString("userName",tv_name.getText().toString());
				bundle.putString("useLogo",user_logo);
				Intent intent = new Intent();
				intent.setClass(DetailSurveyActivity.this,
						OtherPeolpeInformationActivity.class);
				Log.d("uuid_survey", uuid + " none");
				Log.d("user_survey", tv_name.getText().toString() + "no");
				Log.d("logo_survey", user_logo + "no");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
		
	}

	private void SignUp() {
		String tips = null;
		Log.d("哈哈哈哈哈", enrollState);
		// 未报名
		if (enrollState.equals(CANCELED)) {
			tips = "您的报名已转交给发起人，请耐心等待答复";
		}
		// 待审核
		if (enrollState.equals(ENROLL)) {
			tips = "您的报名已转交给发起人，请耐心等待答复";
		}
		// 报名成功
		if (enrollState.equals(SUCCESS)) {
			tips = "您已报名，欢迎您来参加活动";
		}
		// 满员
		if (enrollState.equals(FAILED)) {
			tips = "活动已满员，欢迎您通过现场互动、发起人评论实时了解更多活动信息";
		}

		dialogManager.ShowBlueDialog();
		dialogManager.setBlueMessage(tips);
		dialogManager.BluenoCancel();
		dialogManager.BluenoMessageIcon();
		dialogManager.setBluePositiveButton(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogManager.dismiss();
			}
		});
	}

	/**
	 * 把数据显示在UI上
	 * 
	 * @param response
	 * @throws JSONException
	 */
	@SuppressLint("ResourceAsColor")
	private void WriteOnUI(JSONObject response) throws JSONException {
		// 调研图像

		//		为了能够使封面图片保持固定，采用bitmap显示：
		byte[] bis = getIntent().getByteArrayExtra("bitmap");

		if (bis!=null)  {
			Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
			iv_title.setImageBitmap(bitmap);
		}
		else 	ImageLoader.getInstance().displayImage(
				response.getString("logo").toString(), iv_title);
		iv_title.setScaleType(ScaleType.CENTER_CROP);
		
		userUuid=response.getString("originatorUuid").toString();
		
		// 头像
		user_logo=response.getString("originatorLogo").toString();
		bitmapUtils.display(ci_face, response.getString("originatorLogo").toString());
		/*ImageLoader.getInstance().displayImage(
				response.getString("originatorLogo").toString(), ci_face,defaultOptions);*/
		Log.d("originatorLogo", response.getString("originatorLogo").toString());
		// 活动地点
		tv_location.setText(response.getString("surveyLocation").toString());
		// 是否为发起人
		isOriginator = Boolean.parseBoolean(response.getString("isOriginator")
				.toString());
		Log.d("isOriginator", isOriginator + "");
		// 聊天室id
		groupId = response.getString("groupId");
		// 状态
		state = response.getString("state").toString();
		enrollState = response.getString("enrollState").toString();
		Log.d("enrollState", enrollState);
		// 根据是否是发起人，调研状态，共8种情况,显示不同的ui
		if (isOriginator) {
			rl_shoucang.setVisibility(View.GONE);
			// 写到数据库里面
			WriteToDB(response);
			if (state.equals(SURVEYEND)) {
				ll_public.setBackgroundResource(R.color.white);
				iv_public.setImageResource(R.drawable.btn_bianji);
				tv_public.setTextColor(R.color.uneditable_color);
				tv_public.setText(R.string.sc_edit);
			} else {
				ll_public.setBackgroundResource(R.color.white);
				iv_public.setImageResource(R.drawable.btn_bianji);
				tv_public.setTextColor(R.color.editable_color);
				tv_public.setText(R.string.sc_edit);
			}

		} else {
			rl_shoucang.setVisibility(View.VISIBLE);
			// 如果报名结束
			if (!state.equals(ENROLLING)) {
				ll_public.setBackgroundResource(R.color.ll_background_unsign);
				iv_public.setImageResource(R.drawable.ico_baomingjiahao);
				tv_public.setText(R.string.sign_up);
			} else {
				if (enrollState.equals(CANCELED)) {
					ll_public.setBackgroundResource(R.color.ll_background_sign);
					iv_public.setImageResource(R.drawable.ico_baomingjiahao);
					tv_public.setText(R.string.sign_up);
				} else if (enrollState.equals(ENROLL)) {
					ll_public
					.setBackgroundResource(R.color.ll_background_unsign);
					iv_public.setImageResource(R.drawable.ico_baomingjiahao);
					tv_public.setText(R.string.sign_up);
				} else if (enrollState.equals(SUCCESS)) {
					ll_public
					.setBackgroundResource(R.color.ll_background_unsign);
					iv_public.setImageResource(R.drawable.btn_yijing);
					tv_public.setText(R.string.signed_up);
				} else if (state.equals(FAILED)) {
					ll_public.setBackgroundResource(R.color.ll_background_sign);
					iv_public.setImageResource(R.drawable.ico_baomingjiahao);
					tv_public.setText(R.string.sign_up);
				}
			}
		}
		Log.d("yeah_yeah_state", state);

		// 活动状态
		if (state.equals(ENROLLING)) {
			tv_state.setText(R.string.enrolling);
		} else if (state.equals(ENROLLEND)) {
			tv_state.setText(R.string.enrollend);
		} else if (state.equals(SURVEYING)) {
			ll_easy_touch.setVisibility(View.VISIBLE);
			// 现场互动
			tv_state.setText(R.string.surveying);

		} else if (state.equals(SURVEYEND)) {
			// 如果是发起人，编辑的颜色发生变化
			tv_state.setText(R.string.surveyend);
		}
		// 电话号码
		if (enrollState.equals(SUCCESS)||isOriginator) {
			tv_phone_content.setText(response.getString("contactPhone").toString());
		}else{
			String temp=response.getString("contactPhone").toString();
			temp=temp.substring(0,2)+"*******"+temp.substring(9,11);
			tv_phone_content.setText(temp);
			Log.d("phonenumber",temp);

		}
		// 是否收藏
		isFollow = Boolean.parseBoolean(response.getString("isFollow")
				.toString());
		if (isFollow)
			iv_collect.setImageResource(R.drawable.btn_shocuang_selected);
		else
			iv_collect.setImageResource(R.drawable.btn_shocuang_normal);
		// 调研说明
		tv_text.setText(response.getString("content").toString());
		// 备注
		if (response.getString("noted") == null
				|| response.getString("noted").trim().equals("")) {
			tv_detail1.setText("暂无任何其他说明");
			tv_detail1.setTextColor(this.getResources().getColor(
					R.color.detail_line));
		} else {
			tv_detail1.setText(response.getString("noted").toString());
		}
		// 联系人
		tv_link_man_content.setText(response.getString("contact").toString());
		// 调研名称
		tv_survey_name.setText(response.getString("surveyName").toString());

		surveyNameString=response.getString("surveyName").toString();

		// 发起人
		tv_name.setText(response.getString("originator").toString());
		// 关注人数
		tv_scan_num.setText(response.getString("readNum").toString());

		// 报名人数
		tv_registration_num.setText(response.getString("enrollCount")
				.toString());
		// 通知信息数
		messageCount = response.getString("messageCount").toString();
		long begin = Long.parseLong(response.getString("beginDate").toString());
		long end = Long.parseLong(response.getString("endDate").toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// 开始日期
		String beginDate = dateFormat.format(new Date(begin));
		beginDate = beginDate.substring(5);
		// 结束日期
		String endDate = dateFormat.format(new Date(end));
		endDate = endDate.substring(5);
		// 活动时间
		tv_time.setText(beginDate + "--" + endDate);

		// 调研公司
		JSONArray securityResponses = new JSONArray(response.getString(
				"securityResponses").toString());
		ArrayList<HashMap<String, String>> enterprises = new ArrayList<HashMap<String, String>>();
		status = new boolean[securityResponses.length()];
		for (int i = 0; i < securityResponses.length(); i++) {
			status[i] = false;
			HashMap<String, String> data = new HashMap<String, String>();
			Iterator<String> jsIterator;
			jsIterator = securityResponses.getJSONObject(i).keys();
			while (jsIterator.hasNext()) {
				String key = jsIterator.next();
				data.put(key, securityResponses.getJSONObject(i).get(key)
						.toString());
			}
			enterprises.add(data);
		}
		EnterPriseAdapter enterPriseAdapter = new EnterPriseAdapter(this,
				enterprises);
		mv_enterprises.setAdapter(enterPriseAdapter);
	}

	/**
	 * 把数据写到数据库和sharepreferences
	 * 
	 * @param response
	 * @throws JSONException
	 */
	private void WriteToDB(JSONObject response) throws JSONException {
		JSONObject jsonObject;
		jsonObject = new JSONObject(response.toString());
		// logo
		RsSharedUtil.putString(this, "logo", jsonObject.getString("logo"));
		// 联系电话
		RsSharedUtil.putString(this, "contactPhone",
				jsonObject.getString("contactPhone"));
		// 备注
		RsSharedUtil.putString(this, "noted", jsonObject.getString("noted"));
		// 状态
		RsSharedUtil.putString(this, "state", jsonObject.getString("state"));
		// 调研名称
		RsSharedUtil.putString(this, "surveyName",
				jsonObject.getString("surveyName"));
		// 日期
		long begin = Long.parseLong(response.getString("beginDate").toString());
		long end = Long.parseLong(response.getString("endDate").toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// 开始日期
		String beginDate = dateFormat.format(new Date(begin));
		beginDate = beginDate.substring(5);
		// 结束日期
		String endDate = dateFormat.format(new Date(end));
		endDate = endDate.substring(5);
		RsSharedUtil.putString(this, "endDate", endDate);
		// 发起人图标
		RsSharedUtil.putString(this, "originatorLogo",
				jsonObject.getString("originatorLogo"));
		// 内容
		RsSharedUtil
		.putString(this, "content", jsonObject.getString("content"));
		// 联系人
		RsSharedUtil
		.putString(this, "contact", jsonObject.getString("contact"));
		// 发起人
		RsSharedUtil.putString(this, "originator",
				jsonObject.getString("originator"));
		// 将调研公司放到数据库里面
		JSONArray jsonArray = new JSONArray(
				jsonObject.getString("securityResponses"));
		if (jsonArray.length() > 0) {

			// 先删除数据库的所有公司
			try {
				dbUtils.deleteAll(EnterpriseEntity.class);
			} catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.d("!!!!!!!!!!!", jsonArray.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsObject2 = jsonArray.getJSONObject(i);
				EnterpriseEntity enterprise = new EnterpriseEntity();
				// 内容
				enterprise
				.setContent(jsObject2.getString("content").toString());
				long begin1 = Long.parseLong(jsObject2.getString("beginDate")
						.toString());
				long end1 = Long.parseLong(jsObject2.getString("endDate")
						.toString());
				SimpleDateFormat dateFormat1 = new SimpleDateFormat(
						"yyyy-MM-dd");
				// 开始日期
				String beginDate1 = dateFormat.format(new Date(begin));
				// 结束日期
				String endDate1 = dateFormat.format(new Date(end));
				enterprise.setBeginDate(beginDate1);
				enterprise.setEndDate(endDate1);
				// 地点
				enterprise.setLocationName(jsObject2.getString("locationName")
						.toString());
				// 公司简称
				enterprise.setShortName(jsObject2.getString("shortName")
						.toString());
				// 股票代码
				enterprise.setSymbol(jsObject2.getString("symbol").toString());
				// 职位
				enterprise.setReceicerpost(jsObject2.getString("receiverpost")
						.toString());
				// 地点代码
				enterprise.setLocationCode(jsObject2.getString("locationCode")
						.toString());
				// 行业代码
				enterprise.setIndustryCode(jsObject2.getString("industryCode")
						.toString());
				// uuid
				enterprise.setUuid(jsObject2.getString("uuid").toString());
				// 数据类型
				enterprise.setType(jsObject2.getString("type").toString());
				// 写入数据库
				try {
					dbUtils.save(enterprise);
				} catch (DbException e) {
					Log.d("数据库", "错误无误");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			dbUtils.close();
		}
	}

	// 调研公司
	private class EnterPriseAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		private ArrayList<HashMap<String, String>> enterprises;
		private int currentPosition = -1;

		public EnterPriseAdapter(Context context,
				ArrayList<HashMap<String, String>> enterprises) {
			this.context = context;
			this.enterprises = enterprises;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return enterprises.size();
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
		public View getView(final int position, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (contentView == null) {
				contentView = inflater.inflate(R.layout.item_survey_enterprise,
						null);
			}
			//可触摸栏

			RelativeLayout fingertouch=(RelativeLayout) AbViewHolder.get(contentView,
					R.id.fingertouch);

			// 企业名称
			TextView tv_enterprise_name = AbViewHolder.get(contentView,
					R.id.tv_enterprise_name);
			tv_enterprise_name.setText(enterprises.get(position)
					.get("shortName").toString());
			// 简介
			TextView tv_detail = AbViewHolder.get(contentView, R.id.tv_detail);

			tv_detail.setText(enterprises.get(position).get("content")
					.toString());
			if (position == currentPosition) {
				tv_detail.setVisibility(View.VISIBLE);
			} else {
				tv_detail.setVisibility(View.GONE);
			}
			// 地点
			TextView tv_place = AbViewHolder.get(contentView, R.id.tv_place);
			tv_place.setText(enterprises.get(position).get("locationName")
					.toString());
			// 被调研人职务
			TextView tv_stake_holder_name = AbViewHolder.get(contentView,
					R.id.tv_stake_holder_name);
			tv_stake_holder_name.setText(enterprises.get(position)
					.get("receiverpost").toString());
			// 起始日期
			TextView tv_begin_date = AbViewHolder.get(contentView,
					R.id.tv_begin_date);

			// 结束日期
			TextView tv_end_date = AbViewHolder.get(contentView,
					R.id.tv_end_date);
			long enterprisesbegin = Long.parseLong(enterprises.get(position)
					.get("beginDate").toString());
			long enterprisesend = Long.parseLong(enterprises.get(position)
					.get("endDate").toString());
			SimpleDateFormat enterprisesdateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			// 开始日期
			String enterprisesbeginDate = dateFormat.format(new Date(
					enterprisesbegin));
			enterprisesbeginDate = enterprisesbeginDate.substring(5);
			// 结束日期
			String enterprisesendDate = dateFormat.format(new Date(
					enterprisesend));
			enterprisesendDate = enterprisesendDate.substring(5);
			tv_begin_date.setText(enterprisesbeginDate);
			tv_end_date.setText(enterprisesendDate);
			// 提纲
			LinearLayout rl_outline = AbViewHolder.get(contentView,
					R.id.rl_outline);
			// 图片向上向下
			final ImageView iv_state = AbViewHolder.get(contentView,
					R.id.iv_state);
			Log.d("milk4","every");
			// 点击事件
			fingertouch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (position == currentPosition) {
						currentPosition = -1;
						iv_state.setImageResource(R.drawable.btn_chakangengduo);
						Log.d("milk5","up");

					} else {
						currentPosition = position;
						iv_state.setImageResource(R.drawable.btn_chakangengduo_selected);
						Log.d("milk6","down");
					}
					EnterPriseAdapter.this.notifyDataSetChanged();
				}
			});
			return contentView;
		}

	}

	/**
	 * 获取调研详情
	 * 
	 * @param uuid
	 */
	private void Detail(String uuid) {
		// TODO Auto-generated method stub
		String url = AppConfig.URL_SURVEY + "detail.json?access_token=";
		url = url + RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN)
				+ "&uuid=" + uuid;
		Log.d("获取调研详情", url);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET, url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub

						Log.d("调研详情", response.toString());
						try {
							internetDialog.dismiss();
							// 把数据写在UI控件
							WriteOnUI(response);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// Toast.makeText(this, "网络错误", 0).show();
							Log.d("error", e.toString());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("调研详情", error.toString());
						// TODO Auto-generated method stub

						try {
							internetDialog.dismiss();
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							// ToastUtils.showToast(DetailSurveyActivity.this,
							// jsonObject.getString("description"));

						} catch (Exception e) {
						}
					}
				}

				);
		// 4.请求对象放入请求队列
		jsonObjectRequest.setTag("Detail");
		MyApplication.getRequestQueue().add(jsonObjectRequest);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("Detail");
		try {
			dbUtils.deleteAll(EnterpriseEntity.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

	/**
	 * 异步加载调研详情全部数据
	 * 
	 * @author warren
	 * 
	 */
	private class AsyncDetail extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {

		};

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			// 获取数据
			Detail(arg0[0]);
			return null;
		}

		@Override
		protected void onProgressUpdate(Void[] values) {

		};

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}
}
