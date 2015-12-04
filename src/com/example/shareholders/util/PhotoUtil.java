package com.example.shareholders.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

public class PhotoUtil {

	public static Bitmap getBitpMap(Context context, Uri uri, int width) {
		ParcelFileDescriptor pfd;
		try {
			pfd = context.getContentResolver().openFileDescriptor(uri, "r");
		} catch (IOException ex) {
			return null;
		}
		java.io.FileDescriptor fd = pfd.getFileDescriptor();
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 先指定原始大小
		options.inSampleSize = 1;
		// 只进行大小判断
		options.inJustDecodeBounds = true;
		// 调用此方法得到options得到图片的大小
		BitmapFactory.decodeFileDescriptor(fd, null, options);
		// 我们的目标是在800pixel的画面上显示。
		// 所以需要调用computeSampleSize得到图片缩放的比例
		options.inSampleSize = computeSampleSize(options, width, width);
		// OK,我们得到了缩放的比例，现在开始正式读入BitMap数据
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		// 根据options参数，减少所需要的内存
		Bitmap sourceBitmap = BitmapFactory.decodeFileDescriptor(fd, null,
				options);
		return sourceBitmap;
	}

	// 这个函数会对图片的大小进行判断，并得到合适的缩放比例，比如2即1/2,3即1/3
	static int computeSampleSize(BitmapFactory.Options options,
			int targetWindth, int targetHieght) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidate = 0;

		int candidateW = w / targetWindth;
		int candidateH = h / targetHieght;
		candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > targetWindth) && (w / candidate) < targetWindth)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > targetHieght) && (h / candidate) < targetHieght)
				candidate -= 1;
		}

		return candidate;
	}

	public static void compressImage(String filepath, int quality, int width) {
		// 1. Calculate scale

		int scale = 1;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);
		if (options.outWidth > IMAGE_MAX_WIDTH
				|| options.outHeight > IMAGE_MAX_HEIGHT) {
			scale = calculateInSampleSize(options, width, width);
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		Matrix matrix = new Matrix();
		matrix.setRotate(readPictureDegree(filepath));

		Bitmap saveBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		writeToFile(filepath, saveBitmap, quality);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqWidth);
			} else {
				inSampleSize = Math.round((float) width / (float) reqHeight);
			}
		}
		return inSampleSize + 1;
	}

	public static void writeToFile(String file, Bitmap bitmap, int quality) {

		if (bitmap == null) {
			return;
		}
		BufferedOutputStream bos = null;
		try {

			bos = new BufferedOutputStream(new FileOutputStream(file));

			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos); // PNG
		} catch (IOException ioe) {
		} finally {
			try {
				if (bos != null) {
					bitmap.recycle();
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static final int IMAGE_MAX_WIDTH = 500;
	public static final int IMAGE_MAX_HEIGHT = 500;

	public static int readPictureDegree(String path) {

		int degree = 0;
		if (path != null) {
			try {
				ExifInterface exifInterface = new ExifInterface(path);
				int orientation = exifInterface.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return degree;
	}

	public static String getFilePath(Activity activity, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		String path = cursor.getString(cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
		return path;
	}

	public static String setMkdir(Context context) {
		String filePath;
		if (isSDCardReady()) {
			String dir = Environment.getExternalStorageDirectory().getPath()
					+ "/gosport";
			File dirFile = new File(dir);
			if (!dirFile.exists() && !dirFile.mkdir()) {
			}

			String imgDir = Environment.getExternalStorageDirectory().getPath()
					+ "/gosport/images";
			File imgFile = new File(imgDir);
			if (!imgFile.exists() && !imgFile.mkdir()) {
			}

			filePath = Environment.getExternalStorageDirectory().getPath()
					+ "/gosport/images/";
		} else {
			String dir = context.getCacheDir().getAbsolutePath() + "/images";
			File imgFile = new File(dir);
			if (!imgFile.exists() && !imgFile.mkdir()) {
			}
			filePath = context.getCacheDir().getAbsolutePath() + "/images/";
		}
		File file = new File(filePath);
		if (!file.exists()) {
			boolean b = file.mkdirs();
		}
		return filePath;
	}

	/**
	 * sdcard 是否存在
	 * 
	 * @return
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
}
