package com.example.shareholders;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.util.NetUtils;
import com.example.shareholders.activity.login.LoginActivity;
import com.example.shareholders.activity.personal.SetUpActivity;
import com.example.shareholders.activity.shop.SearchGoodsActivity;
import com.example.shareholders.activity.stock.EditMyselfActivity;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.activity.stock.ShareAndFriendsSearchActivity;
import com.example.shareholders.activity.survey.ActivityCreateActivity;
import com.example.shareholders.activity.survey.ActivitySearchActivity;
import com.example.shareholders.adapter.ViewPagerAdapter;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.fragment.CopyOfFragment_Home;
import com.example.shareholders.fragment.CopyOfFragment_Shop;
import com.example.shareholders.fragment.Fragment_Me;
import com.example.shareholders.fragment.Fragment_Plate;
import com.example.shareholders.fragment.Fragment_Price;
import com.example.shareholders.fragment.Fragment_Price_Situation;
import com.example.shareholders.fragment.Fragment_Situation;
import com.example.shareholders.fragment.Fragment_Survey;
import com.example.shareholders.fragment.LeftMenuFragment;
import com.example.shareholders.receiver.LoginReceiver;
import com.example.shareholders.receiver.LoginReceiver.AfterLogin;
import com.example.shareholders.receiver.NewMessageBroadcastReceiver;
import com.example.shareholders.util.BtnClickUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.SystemStatusManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

@ContentView(R.layout.activity_main)
public class MainActivity extends SlidingFragmentActivity implements
		OnCheckedChangeListener, AfterLogin {
	@ViewInject(R.id.rbtn1)
	private RadioButton rbtn1;
	@ViewInject(R.id.rbtn2)
	private RadioButton rbtn2;
	@ViewInject(R.id.rbtn3)
	private RadioButton rbtn3;
	@ViewInject(R.id.rbtn4)
	private RadioButton rbtn4;
	@ViewInject(R.id.rbtn5)
	private RadioButton rbtn5;
	@ViewInject(R.id.rb_zixuan)
	private RadioButton rb_zixuan;
	@ViewInject(R.id.rb_bankuai)
	private RadioButton rb_bankuai;
	@ViewInject(R.id.main_ViewPager)
	private MyViewPager viewpager;
	@ViewInject(R.id.rgp)
	private RadioGroup rgp_main;
	@ViewInject(R.id.rl_title)
	private RelativeLayout rl_title;
	private LayoutParams params;
	@ViewInject(R.id.tv_main_titile)
	private TextView title;
	@ViewInject(R.id.title_note)
	private ImageView iv_note;
	@ViewInject(R.id.tv_guanli)
	private TextView tv_guanli;
	@ViewInject(R.id.title_research)
	private ImageView title_research;
	@ViewInject(R.id.title_refresh)
	public ImageView title_refresh;
	@ViewInject(R.id.rg_stock)
	private RadioGroup rg_stock;
	private ArrayList<Fragment> fragmentlist;
	private CopyOfFragment_Home fragmenthome;
	private Fragment_Survey fragmentsurvey;
	private Fragment_Price fragmentprice;
	private CopyOfFragment_Shop fragmentgoods;
	private Fragment_Me fragmentme;
	private Fragment_Plate fragmentplate;
	private Fragment_Situation fragmentsituation;
	private CanvasTransformer mTransformer;
	int current = 0;
	boolean isOriginator;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	LeftMenuFragment leftMenu;
	SlidingMenu sm;
	LoginReceiver loginReceiver;

	private AlertDialog mDialog = null;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x123) {
				if (mDialog != null && mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}
		};
	};

	
	
	protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			current = 2;
			title.setText("沪深");
			iv_note.setImageResource(R.drawable.btn_zuocelan);
			iv_note.setVisibility(View.VISIBLE);
			title_research.setImageResource(R.drawable.btn_sousuo);
			title_research.setVisibility(View.VISIBLE);

			if (viewpager.getCurrentItem() != current) {
				viewpager.setCurrentItem(current);
				rgp_main.check(R.id.rbtn3);

			}

		}
	};

	@Override
	protected void onResume() {
		if (RsSharedUtil.getString(getApplicationContext(),
				AppConfig.ACCESS_TOKEN).equals("")) {
			isOriginator = false;
			iv_note.setVisibility(View.GONE);
			// title_research.setVisibility(View.VISIBLE);
		} else {
			getCharacter();
		}
		// 商城
		if (current == 3) {
			iv_note.setImageResource(R.drawable.btn_zuocelan);
			iv_note.setVisibility(View.VISIBLE);
		}

		// 个人中心
		if (current == 4) {
			Log.d("jatjat", current + "gone");
			iv_note.setVisibility(View.GONE);
		}
		// 调研
		if (current == 1) {
			if (RsSharedUtil.getString(this, "survey_right").equals("false")) {
				iv_note.setVisibility(View.GONE);
			}

			if (isOriginator == false) {
				iv_note.setVisibility(View.GONE);
			}

		}
		// 在当前的activity中注册广播 ，收到广播后将ViewPager换到沪深
		IntentFilter filter = new IntentFilter();
		filter.addAction("currentItem2");
		this.registerReceiver(this.broadcastReceiver, filter);

		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		// 如果网络不可用
		// if (!NetWorkCheck.isNetworkConnected(this)) {
		// showNoInternetDialog();
		// }
		// 功能：初始化数据

		initFrag(savedInstanceState);

		initAnimation();
		// 设置左侧的menu
		setBehindContentView(R.layout.left_menu);
		sm = getSlidingMenu();
		// 左侧fragment
		leftMenu = new LeftMenuFragment();
		// 得到fragment管理器
		manager = getSupportFragmentManager();
		// 开启事务
		transaction = manager.beginTransaction();
		// 将menu替换成LeftMenuFragment
		transaction.replace(R.id.fl_left_menu, leftMenu);
		// 提交事务
		transaction.commit();
		// setTranslucentStatus();状态一体化
		// 得到Slidingment组件(侧滑 )
		// 设置阴影宽度
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// 获取屏幕的宽度
		WindowManager wm = (WindowManager) MainActivity.this
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		// 设置阴影的效果
		sm.setShadowDrawable(R.drawable.shadow);
		// 设置menu打开的偏移 越大显示的越少
		sm.setBehindOffset(width / 2);
		// setRes(R.dimen.slidingmenu_offset);
		// 设置menu淡入淡出程度
		sm.setFadeDegree(0.35f);
		// 设置menu能够打开的模式
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// 如果是登录时从startactivity过来的
		try {
			Intent intent = getIntent();
			if (intent.getBooleanExtra("isfromstart", false)) {
				current = 0;
				// 设置位置
				setPosition();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		// 跳到别的fragment时停止行情更新
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

				ViewPagerAdapter cAdapter = (ViewPagerAdapter) viewpager
						.getAdapter();
				Fragment_Price fragment_Price = (Fragment_Price) cAdapter
						.instantiateItem(viewpager, 2);
				Fragment_Survey fragment_Survey = (Fragment_Survey) cAdapter
						.instantiateItem(viewpager, 1);
				Fragment_Situation fragment_Situation = (Fragment_Situation) cAdapter
						.instantiateItem(viewpager, 5);
				if (viewpager.getCurrentItem() == 1) {
					fragment_Survey.lv_current.setAdapter(null);
					fragment_Survey.lv_focus.setAdapter(null);

					fragment_Survey.getBanner();
					fragment_Survey.latestSurvey(0, 3);
					fragment_Survey.getHottestTopics(0, 3);

				}

				if (viewpager.getCurrentItem() != 2) {
					fragment_Price.removeupdate();
				} else {
					fragment_Price.postupdate();

					Log.d("ididididiidid", fragment_Price.getViewPagerid() + "");
					if (fragment_Price.getViewPagerid() == 0) {

						title_refresh.setVisibility(View.VISIBLE);
					} else {
						title_refresh.setVisibility(View.GONE);
					}
				}
				if (viewpager.getCurrentItem() == 5) {
					fragment_Situation.sethandler();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	// 在行情fragment是将刷新按钮设置为可见
	public void setrefresh(boolean ifvis) {
		if (ifvis) {
			title_refresh.setVisibility(View.VISIBLE);
		} else {
			title_refresh.setVisibility(View.GONE);
		}

	}

	// /**
	// * 提示网络不可用
	// */
	// private void showNoInternetDialog() {
	// Log.d("liang_internent", "网络错误");
	// mDialog = new AlertDialog.Builder(this).create();
	// mDialog.show();
	// mDialog.setCancelable(false);
	// Window window = mDialog.getWindow();
	// window.setContentView(R.layout.dialog_no_internet);
	// TextView tv_message = (TextView) window.findViewById(R.id.tv_message);
	// ProgressBar progressBar = (ProgressBar)
	// window.findViewById(R.id.progress_bar);
	// ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
	//
	// WindowManager.LayoutParams lp = window.getAttributes();
	// lp.dimAmount = 0.0f;
	// window.setAttributes(lp);
	// window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	//
	// iv_tips.setVisibility(View.VISIBLE);
	// progressBar.setVisibility(View.GONE);
	// tv_message.setText("网络异常");
	//
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Message msg = new Message();
	// msg.what = 0x123;
	// handler.sendMessageDelayed(msg, 4000);
	// }
	// }).start();
	// }

	@Override
	public void onDestroy() {
		// 注销广播
		unregisterReceiver(loginReceiver);
		this.unregisterReceiver(this.broadcastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// 接受广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("LoginReceiver");
		loginReceiver = new LoginReceiver();
		registerReceiver(loginReceiver, intentFilter);
		loginReceiver.setAfterLogin(this);

		// 注册环信接收新信息的广播
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter2 = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter2.setPriority(3);
		registerReceiver(msgReceiver, intentFilter2);

		// 监听联系人变化
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		// 注册监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());

		super.onStart();
	};

	@OnClick({ R.id.title_research, R.id.title_note, R.id.title_refresh,
			R.id.rb_zixuan, R.id.rb_bankuai, R.id.tv_guanli })
	public void onClick(View v) {
		if (!BtnClickUtils.isFastDoubleClick()) {
			Intent intent = new Intent();
			switch (v.getId()) {

			case R.id.title_research:
				// 如果按钮属于调研
				if (current == 1) {
					intent.setClass(this, ActivitySearchActivity.class);
					startActivity(intent);
				}
				// 如果按钮属于个人中心
				if (current == 4) {
					// 如果未登录，直接去登录
					if (RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN) == "") {
						intent.setClass(this, LoginActivity.class);
						startActivity(intent);
					} else
						startActivity(new Intent(MainActivity.this,
								SetUpActivity.class));
				}

				// 如果按钮属于商城
				if (current == 3) {
					startActivity(new Intent(MainActivity.this,
							SearchGoodsActivity.class));
				}

				// 如果按钮属于沪深
				if (current == 2) {
					startActivity(new Intent(MainActivity.this,
							ShareAndFriendsSearchActivity.class));
				}
				break;
			case R.id.title_note:
				if (current == 0) {
					intent.setClass(this, MyStockDetailsActivity.class);
					startActivity(intent);
				}
				// 如果按钮属于调研
				if (current == 1) {
					intent.setClass(this, ActivityCreateActivity.class);
					intent.putExtra("sign", 0);
					startActivity(intent);
				}
				// 如果按钮属于商城
				if (current == 3) {
					getSlidingMenu().toggle();
				}
				// 如果按钮属于沪深
				/*
				 * if (current == 2) { startActivity(new
				 * Intent(MainActivity.this, EditMyselfActivity.class)); }
				 */

				break;
			case R.id.tv_guanli:
				if (current == 2) {
					if (RsSharedUtil.getString(this, AppConfig.ACCESS_TOKEN)
							.equals("")) {
						intent.setClass(this, LoginActivity.class);
						intent.putExtra("isFirst", false);
						startActivity(intent);
					} else {
						startActivity(new Intent(MainActivity.this,
								EditMyselfActivity.class));
					}
				}
			default:
				break;
			}
		}
		switch (v.getId()) {
		case R.id.rb_zixuan:
			Log.d("rb_zixuan", "rb_zixuan");
			tv_guanli.setVisibility(View.VISIBLE);
			viewpager.setCurrentItem(2);
			break;
		case R.id.rb_bankuai:
			Log.d("rb_bankuai", "rb_bankuai");
			title_refresh.setVisibility(View.VISIBLE);
			tv_guanli.setVisibility(View.GONE);
			viewpager.setCurrentItem(5);
			break;
		default:
			break;
		}
	}

	public void initFrag(Bundle savedInstanceState) {
		rgp_main.setOnCheckedChangeListener(this);
		fragmentlist = new ArrayList<Fragment>();
		// Viewpage加载的页面
		fragmenthome = new CopyOfFragment_Home();
		fragmentsurvey = new Fragment_Survey();
		fragmentprice = new Fragment_Price();
		fragmentgoods = new CopyOfFragment_Shop();
		fragmentme = new Fragment_Me();
		fragmentplate = new Fragment_Plate();
		fragmentsituation = new Fragment_Situation();

		fragmentlist.add(fragmenthome);
		fragmentlist.add(fragmentsurvey);
		fragmentlist.add(fragmentprice);
		fragmentlist.add(fragmentgoods);
		fragmentlist.add(fragmentme);
		// fragmentlist.add(fragmentplate);
		fragmentlist.add(fragmentsituation);

		viewpager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),
				fragmentlist));
		viewpager.setCurrentItem(0);
		iv_note.setVisibility(View.GONE);
		title.setText("首页");
		viewpager.setOffscreenPageLimit(6);
	}

	// 实现ConnectionListener接口
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						// Toast.makeText(getApplicationContext(), "账号被移除",
						// 0).show();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆
						// Toast.makeText(getApplicationContext(), "账号在其他设备上登录",
						// 0).show();
					} else {
						if (NetUtils.hasNetwork(MainActivity.this)) {
							// 连接不到聊天服务器
							// Toast.makeText(getApplicationContext(),
							// "连接不到聊天服务器", 0).show();
						} else {
							// 当前网络不可用，请检查网络设置
							// Toast.makeText(getApplicationContext(),
							// "当前网络不可用", 0).show();
						}
					}
				}
			});
		}
	}

	/**
	 * 监听联系人的变化
	 * 
	 * @author jat
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒

		}

		@Override
		public void onContactAgreed(String username) {
			// 同意好友请求
		}

		@Override
		public void onContactRefused(String username) {
			// 拒绝好友请求

		}
	}

	/*
	 * 功能：判断用户的身份 说明：usernum为0时，用户为普通用户，为1时为发起人
	 */
	private void getCharacter() {

		// 获取access_token
		String url = AppConfig.URL_USER
				+ "type.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						AppConfig.ACCESS_TOKEN);
		Log.w("thelastthree", url);
		// Log.d("liang_character_url", url);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						Log.d("response", response.toString());
						try {
							String user = response.get("type").toString();

							if (user.equals("true")) {
								/*
								 * iv_note.setVisibility(View.VISIBLE);
								 * title_research.setVisibility(View.VISIBLE);
								 */
								isOriginator = true;
							} else {
								/*
								 * iv_note.setVisibility(View.GONE);
								 * title_research.setVisibility(View.VISIBLE);
								 */
								isOriginator = false;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
							;

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
					}
				});
		MyApplication.getRequestQueue().add(jsonObjectRequest);
	}

	/**
	 * 
	 * <p>
	 * Title: onCheckedChanged
	 * </p>
	 * <p>
	 * Description: RadioButton点击事件
	 * </p>
	 * 
	 * @param group
	 * @param checkedId
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup,
	 *      int)
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		rb_zixuan.setChecked(true);
		iv_note.setVisibility(View.GONE);
		tv_guanli.setVisibility(View.GONE);
		title_research.setVisibility(View.GONE);
		title_refresh.setVisibility(View.GONE);
		rg_stock.setVisibility(View.GONE);
		title.setVisibility(View.VISIBLE);
		manager = getSupportFragmentManager();
		transaction = manager.beginTransaction();
		switch (checkedId) {
		case R.id.rbtn1:
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			current = 0;
			title.setText("股东会");
			break;
		case R.id.rbtn2:
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			current = 1;
			title.setText("调研");
			title_research.setImageResource(R.drawable.btn_sousuo);
			if (!isOriginator) {
				iv_note.setVisibility(View.GONE);
				title_research.setVisibility(View.VISIBLE);
			} else {
				iv_note.setImageResource(R.drawable.btn_fabu);
				iv_note.setVisibility(View.VISIBLE);
				title_research.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rbtn3:
			title.setVisibility(View.GONE);
			rg_stock.setVisibility(View.VISIBLE);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			current = 2;
			// title.setText("自选中心");
			// iv_note.setImageResource(R.drawable.btn_share_bianji);
			// iv_note.setVisibility(View.VISIBLE);
			tv_guanli.setVisibility(View.VISIBLE);
			title_research.setImageResource(R.drawable.btn_sousuo);
			title_research.setVisibility(View.VISIBLE);
			break;
		case R.id.rbtn4:
			current = 3;
			title.setText("商城");
			// 设置menu能够打开的模式
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			iv_note.setImageResource(R.drawable.btn_zuocelan);
			iv_note.setVisibility(View.VISIBLE);
			title_research.setImageResource(R.drawable.btn_sousuo);
			title_research.setVisibility(View.VISIBLE);
			break;
		case R.id.rbtn5:
			iv_note.setVisibility(View.GONE);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			title_research.setImageResource(R.drawable.btn_shezhi);
			title_research.setVisibility(View.VISIBLE);
			current = 4;
			title.setText("个人中心");
			break;
		}
		if (viewpager.getCurrentItem() != current) {
			viewpager.setCurrentItem(current);
		}
		if (current == 4) {
			iv_note.setVisibility(View.GONE);
		}
	}

	private void initAnimation() {
		mTransformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				// 1
				canvas.scale(percentOpen, 1, 0, 0);

			}

		};
	}

	/**
	 * 
	 * @ClassName: MyListner
	 * @Description:TODO(ViewPage页面改变事件)
	 * @author: Zgp
	 * @date: 2015-8-9 上午11:07:22
	 * 
	 */
	public class MyListner implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			int current = viewpager.getCurrentItem();
			setPosition();
		}

	}

	/**
	 * 
	 * @Title: setTranslucentStatus @Description:
	 *         TODO(状态一体化，如果需要，直接在Oncreate（）调用) @param: @return: void @throws
	 */
	private void setTranslucentStatus() {
		params = (LayoutParams) rl_title.getLayoutParams();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
			params.topMargin = 60;// 距离状态栏上边的距离
		} else {
			params.topMargin = 0;
		}
		rl_title.setLayoutParams(params);

		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);
	}

	@Override
	public void ToDo(int index) {
		// TODO Auto-generated method stub
		if (index >= 0) {
			Log.d("setcccccccccc", "sew");
			current = index;
			// 设置位置
			setPosition();
		}

	}

	private void setPosition() {
		switch (current) {
		case 0:
			rgp_main.check(R.id.rbtn1);
			break;
		case 1:
			rgp_main.check(R.id.rbtn2);
			break;
		case 2:
			rgp_main.check(R.id.rbtn3);
			break;
		case 3:
			rgp_main.check(R.id.rbtn4);
			break;
		case 4:
			rgp_main.check(R.id.rbtn5);
			break;
		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出股东汇~~~",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public int getmainviewpagerid() {
		return viewpager.getCurrentItem();
	}

	public int getsituationviewpagerid() {
		ViewPagerAdapter cAdapter = (ViewPagerAdapter) viewpager.getAdapter();
		Fragment_Situation fragment_Situation = (Fragment_Situation) cAdapter
				.instantiateItem(viewpager, 5);
		return fragment_Situation.getviewpagerid();
	}

}
