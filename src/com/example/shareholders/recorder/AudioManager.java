package com.example.shareholders.recorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import android.media.MediaRecorder;

/**
 * 录音
 * 
 * @author warren
 * 
 */
public class AudioManager {
	private MediaRecorder mMediaRecorder;
	// 文件夹名称
	private String mDir;
	private String mCurrentFilePath;
	private static AudioManager mInstance;
	private boolean isPrepare;

	private AudioManager(String dir) {
		mDir = dir;
	}

	/**
	 * 回调准备完毕
	 * 
	 * @author warren
	 * 
	 */
	public interface AudioStateListener {
		// 准备完成
		void wellPrepare();
	}

	public AudioStateListener mListener;

	public void setOnAudioStateListener(AudioStateListener listener) {
		mListener = listener;
	}

	// AudioManager 单例
	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null)
					mInstance = new AudioManager(dir);
			}
		}
		return mInstance;
	}

	// 录音前准备
	public void prepareAudio() {
		// 建立文件存放录音

		try {
			isPrepare = false;
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			String fileName = generateFileName();
			// 路径为dir，名称为fileName
			File file = new File(dir, fileName);
			mCurrentFilePath = file.getAbsolutePath();
			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			// 设置mediaRecord的音频源为mic
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			// 设置音频编码为amr
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			isPrepare = true;
			if (mListener != null) {
				mListener.wellPrepare();
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 随机生成文件的名称
	 * 
	 * @return
	 */
	private String generateFileName() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(System
				.currentTimeMillis()) + UUID.randomUUID().toString() + ".amr";

	}

	// 获得音量等级
	public int getVoiceLevel(int maxLevel) {
		if (isPrepare) {
			// 获得最大振幅 1-32767
			try {
				return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			}
			// 有异常就直接返回1
			catch (Exception e) {
				// TODO Auto-generated catch bloc k

			}
		}
		return 1;
	}

	public void release() {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder = null;
	}

	/**
	 * 取消录音
	 */
	public void cancel() {
		release();
		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			file.delete();
			mCurrentFilePath = null;
		}

	}

	// 获取路径
	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}

}
