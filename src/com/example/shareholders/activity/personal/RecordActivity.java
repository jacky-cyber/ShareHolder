package com.example.shareholders.activity.personal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.Recorder;
import com.example.shareholders.recorder.AudioManager;
import com.example.shareholders.recorder.AudioManager.AudioStateListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_record)
public class RecordActivity extends ActionBarActivity implements
AudioStateListener {
	// 录音按钮
	@ViewInject(R.id.iv_record)
	private ImageView iv_record;
	// 录音准备完成
	private static final int MSG_AUDIO_PREPARE = 0x110;
	// 声音变化
	private static final int MSG_VOICE_CHANGE = 0x111;
	// 录音暂停
	private static final int MSG_AUDIO_PAUSE = 0x112;
	// 录音
	AudioManager mAudioManager;
	// 计时器
	@ViewInject(R.id.tv_timer)
	private Chronometer tv_timer;
	// 是否正在录音
	private boolean isRecording = false;
	// 最大音量
	private static final int MAX_VOICE_LEVEL = 7;
	// 录音的时间长度
	private float mTime = 0;
	// 获取音量线程
	Thread voiceThread;
	// 音量图
	@ViewInject(R.id.iv_volume)
	private ImageView iv_volume;
	// 背景
	@ViewInject(R.id.background)
	private RelativeLayout background;
	/**
	 * 获取音量大小的runnable
	 */
	DbUtils dbUtils;
	private Runnable mGetVoiceLevelRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isRecording) {
				// 每隔0.1秒获取一下音量
				try {
					Thread.sleep(100);
					mTime += 0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * 弹出菜单栏
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
				(int) (width / 1.3), height / 3);
		// 设置内容
		popupWindow.setContentView(contentView);
		final EditText et_name = (EditText) contentView
				.findViewById(R.id.et_name);
		et_name.setFocusable(true);
		et_name.setFocusableInTouchMode(true);
		et_name.requestFocus();
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(false);
		//设置布局监听，后期才可以进行edittext的焦点监听
		contentView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				contentView.setFocusable(true);
				contentView.setFocusableInTouchMode(true);
				contentView.requestFocus();
				return false;
			}
		});
		//设置焦点获取监听
		et_name.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.d("foucus", "foucus");
				if (et_name.hasFocus()==false) {
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		// popwindow位置
		popupWindow.showAtLocation(background, Gravity.CENTER, 0, 0);
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		TextView tv_save = (TextView) contentView.findViewById(R.id.tv_save);
		tv_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 生成录音,时间和路径
				if (et_name.getText().toString().trim() != "") {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String date = dateFormat.format(new Date()).toString();
					Recorder record = new Recorder();
					record.setName(et_name.getText().toString().trim());
					record.setDate(date);
					record.setFilePath(mAudioManager.getCurrentFilePath());
					record.setTime((int) mTime + 1);
					// 把录音加入数据库
					try {
						dbUtils.saveOrUpdate(record);
						Toast.makeText(getApplicationContext(), "保存成功",
								Toast.LENGTH_SHORT);
						finish();
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(), "请输入名称！",
							Toast.LENGTH_SHORT);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (isRecording) {
			mAudioManager.release();
			isRecording = false;
		}
		super.onDestroy();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		dbUtils = DbUtils.create(getApplicationContext());
	}

	@OnClick({ R.id.rl_return, R.id.iv_record })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		case R.id.iv_record:
			if (isRecording) {
				// 如果已经在录音，就停止录音
				mHandler.sendEmptyMessage(MSG_AUDIO_PAUSE);

			}
			// 开始录音
			else {
				// 如果sd卡不存在就直接返回
				if (!Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED))
					Toast.makeText(getApplicationContext(), "没有sd卡",
							Toast.LENGTH_SHORT);
				else {
					mAudioManager = AudioManager
							.getInstance(AppConfig.AUDIO_PATH);
					mAudioManager.setOnAudioStateListener(this);
					// 注册audiolistener
					mAudioManager.prepareAudio();
				}
			}
		default:
			break;
		}
	}

	@Override
	public void wellPrepare() {
		// TODO Auto-generated method stub
		// 录音准备完成
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARE);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 开始录音
			case MSG_AUDIO_PREPARE:
				iv_record
				.setImageResource(R.drawable.btn_kaishiluyin_highlight);
				tv_timer.start();
				isRecording = true;
				// 开启线程更新音量
				voiceThread = new Thread(mGetVoiceLevelRunnable);
				voiceThread.start();
				break;
			case MSG_VOICE_CHANGE:
				// 获取当前音量
				switch (mAudioManager.getVoiceLevel(MAX_VOICE_LEVEL)) {
				case 1:
					iv_volume.setImageResource(R.drawable.volume1);
					break;
				case 2:
					iv_volume.setImageResource(R.drawable.volume2);
					break;
				case 3:
					iv_volume.setImageResource(R.drawable.volume3);
					break;
				case 4:
					iv_volume.setImageResource(R.drawable.volume4);
					break;
				case 5:
					iv_volume.setImageResource(R.drawable.volume5);
					break;
				case 6:
					iv_volume.setImageResource(R.drawable.volume6);
					break;
				case 7:
					iv_volume.setImageResource(R.drawable.volume7);
					break;

				default:
					break;
				}
				break;
			case MSG_AUDIO_PAUSE:
				Log.d("结束了吗", "结束2");
				iv_record.setImageResource(R.drawable.btn_kaishiluyin);
				tv_timer.stop();
				initMenu(getApplicationContext(), R.layout.popup_name_record);
				mAudioManager.release();
				isRecording = false;
				break;

			default:
				break;
			}
		};
	};

	// 下面三个用于隐藏软键盘
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (isShouldHideInput(v, ev)) {
				hideSoftInput(v.getWindowToken());
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	private boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getRawX() > left && event.getRawX() < right
					&& event.getRawY() > top && event.getRawY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	private void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
