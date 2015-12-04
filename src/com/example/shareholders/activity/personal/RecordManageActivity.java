package com.example.shareholders.activity.personal;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.db.entity.Recorder;
import com.example.shareholders.recorder.MediaManager;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.view.GeneralDialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_record_manage)
public class RecordManageActivity extends ActionBarActivity {
	// 录音listview
	@ViewInject(R.id.lv_my_record)
	private ListView lv_my_record;
	// 所有的录音
	private List<Recorder> recorders;
	// 数据库
	DbUtils dbUtils;
	Handler handler;
	private boolean startThread = false;
	// seekbar进度
	private int seekBarProgess = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dbUtils = DbUtils.create(this);
		ViewUtils.inject(this);
	}

	@Override
	protected void onResume() {
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			Toast.makeText(getApplicationContext(), "没有sd卡", Toast.LENGTH_SHORT);
		else {
			try {
				// 找到所有的录音
				recorders = dbUtils.findAll(Recorder.class);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (recorders != null) {
				MyRecordListAdapter myRecordListAdapter = new MyRecordListAdapter(
						recorders);
				lv_my_record.setAdapter(myRecordListAdapter);
				myRecordListAdapter.notifyDataSetChanged();
			}
		}
		MediaManager.resume();
		super.onResume();
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 关闭数据库并且关闭播放器
		MediaManager.release();
		if (dbUtils != null) {
			dbUtils.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		MediaManager.pause();
		super.onPause();
	};

	@OnClick({ R.id.rl_return, R.id.iv_record })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_return:
			finish();
			break;
		case R.id.iv_record:
			startActivity(new Intent(RecordManageActivity.this,
					RecordActivity.class));
			break;
		default:
			break;
		}
	}

	private void delteRecord(final int position, final List<Recorder> recorders) {
		// TODO Auto-generated method stub
		final GeneralDialog dialog = new GeneralDialog(
				RecordManageActivity.this);
		dialog.setMessage("确定删除这段录音吗?");
		dialog.setCancel(true);
		// 去掉图片提示
		dialog.noMessageIcon();
		// 确定按钮的点击事件
		dialog.setPositiveButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		// 取消按钮的点击事件
		dialog.setNegativeButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 删除录音
				Log.d("position", position + "");
				Log.d("recorders", recorders.size() + "");
				Recorder recorder = new Recorder();
				try {
					recorder = dbUtils.findById(Recorder.class,
							recorders.get(position).getFilePath());
					recorders.remove(position);
					dbUtils.delete(recorder);
					MyRecordListAdapter myRecordListAdapter = new MyRecordListAdapter(
							recorders);
					lv_my_record.setAdapter(myRecordListAdapter);
					myRecordListAdapter.notifyDataSetChanged();
					dialog.dismiss();
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// 录音adapter
	private class MyRecordListAdapter extends BaseAdapter {
		// 是否是第一次
		private boolean isFirst;
		// 是否正在播放
		private boolean isPlaying;
		// 当前位置
		private int currentPosition = -1;
		// 录音列表
		private List<Recorder> recorders;
		private LayoutInflater mInflater;
		private ProgressBar sb_bar;

		public MyRecordListAdapter(List<Recorder> recorders) {
			// TODO Auto-generated constructor stub
			this.recorders = recorders;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return recorders.size();
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
				contentView = mInflater.inflate(R.layout.item_my_record, null);
			}
			// move to trash
			ImageView iv_trash = AbViewHolder.get(contentView, R.id.iv_trash);
			iv_trash.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 删除录音
					delteRecord(position, recorders);
					notifyDataSetChanged();
				}
			});
			// 录音名字，日期，时间
			TextView tv_record_name = AbViewHolder.get(contentView,
					R.id.tv_record_name);
			TextView tv_record_date = AbViewHolder.get(contentView,
					R.id.tv_record_date);
			TextView tv_record_time = AbViewHolder.get(contentView,
					R.id.tv_record_time);
			tv_record_name.setText(recorders.get(position).getName());
			tv_record_date.setText(recorders.get(position).getDate());
			tv_record_time.setText(secondToTime(recorders.get(position)
					.getTime()));
			// 播放条
			RelativeLayout rl_seek = AbViewHolder
					.get(contentView, R.id.rl_seek);
			// 判断播放条是否显示
			if (currentPosition == position) {
				rl_seek.setVisibility(View.VISIBLE);

			} else {
				rl_seek.setVisibility(View.GONE);
			}
			sb_bar = AbViewHolder.get(contentView, R.id.sb_bar);
			RelativeLayout rl_record = AbViewHolder.get(contentView,
					R.id.rl_record);
			// 播放按钮
			final ImageView iv_state = AbViewHolder.get(contentView,
					R.id.iv_state);

			// 点击某个item就显示播放条
			rl_record.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// 关闭播放
					isFirst = true;
					isPlaying = false;
					seekBarProgess = 0;
					sb_bar.setProgress(0);
					iv_state.setImageResource(R.drawable.btn_play1);
					MediaManager.release();
					if (currentPosition == position)
						currentPosition = -1;
					else
						currentPosition = position;
					notifyDataSetChanged();
				}
			});
			// 实现消息传递
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x1234) {
						Log.d("seekBarProgess", seekBarProgess + "");
						if (startThread)
							sb_bar.setProgress(seekBarProgess);
						else {
							sb_bar.setProgress(0);
						}
						MyRecordListAdapter.this.notifyDataSetChanged();
					}
				}
			};

			iv_state.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// 睡眠1秒
					sb_bar.setMax(recorders.get(position).getTime() * 1000);
					DelayThread delaythread = new DelayThread(1000);
					// 如果未开始并且不在播放，就PlaySound
					if (isFirst && !isPlaying) {
						isPlaying = true;
						isFirst = false;
						iv_state.setImageResource(R.drawable.btn_pause);
						startThread = true;

						// 开始播放
						MediaManager.playSound(recorders.get(position)
								.getFilePath(),
								new MediaPlayer.OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										// TODO Auto-generated method stub
										// 播放完毕，MediaManager释放，线程关闭，isFirst设置为true，isPlaying设置为false
										MediaManager.release();
										startThread = false;
										isFirst = true;
										isPlaying = false;
										seekBarProgess = 0;
										sb_bar.setProgress(0);
										iv_state.setImageResource(R.drawable.btn_play1);
										MyRecordListAdapter.this
												.notifyDataSetChanged();
									}
								});

						Log.d("total_time", recorders.get(position).getTime()
								+ "");

						delaythread.start();
					}
					// 如果已经开始，并且正在播放，就暂停
					else if (!isFirst && isPlaying) {
						isFirst = false;
						isPlaying = false;
						Log.d("暂停播放", "pause");
						startThread = false;
						iv_state.setImageResource(R.drawable.btn_play1);
						MediaManager.pause();
						MyRecordListAdapter.this.notifyDataSetChanged();
					}
					// 如果已经开始，并且正在播放，就resume
					else if (!isFirst && !isPlaying) {
						isFirst = false;
						isPlaying = true;
						startThread = true;
						Log.d("重新播放", "resume");
						iv_state.setImageResource(R.drawable.btn_pause);
						MediaManager.resume();
						MyRecordListAdapter.this.notifyDataSetChanged();
						delaythread.start();
					}
				}
			});

			return contentView;
		}
	}

	/**
	 * 将秒数转为 mm:ss
	 * 
	 * @param second
	 * @return
	 */
	private String secondToTime(int second) {
		String format = "";
		int minutes = second / 60;
		int seconds = second % 60;
		if (minutes < 10)
			format = format + "0" + minutes + ":";
		else {
			format = format + minutes + ":";
		}
		if (seconds < 10)
			format = format + "0" + seconds;
		else
			format = format + seconds;
		return format;
	}

	// 因为MediaPlayer类没有播放进度的回调方法，所以需要设置一个线程实现实时刷新：
	class DelayThread extends Thread {
		int interval;
		int total_time;

		DelayThread(int i) {
			interval = i;
			total_time = 0;
		}

		@Override
		public void run() {
			while (startThread) {
				seekBarProgess += interval;
				handler.sendEmptyMessage(0x1234);
				try {
					sleep(interval);

					// 设置音乐进度读取频率
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}
