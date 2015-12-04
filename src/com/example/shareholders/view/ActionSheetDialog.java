package com.example.shareholders.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.common.ImageTools;
import com.example.shareholders.fragment.Fragment_ActivityCreate1;
import com.gghl.view.wheelview.WheelMain;

public class ActionSheetDialog {
	private Context context;
	private Dialog dialog;
	private TextView txt_title;
	private TextView txt_cancel;
	private LinearLayout lLayout_content;
	// private ScrollView sLayout_content;
	private boolean showTitle = false;
	private List<SheetItem> sheetItemList;
	private Display display;

	private ImageView iv_line;
	private WheelMain wheelMain;
	//开始时间
	private TextView tv_start_date;
	//结束时间
	private TextView tv_end_date;
	private static int position = 0;
	/**
	 * 弹出框的类型（时间或选择图片）
	 */
	private String popUp_category = "";

	/**
	 * 拍照的三种跳转类型
	 */
	public static Intent intent;
	public static int PHOTO_TYPE;

	public static final int PICK_PHOTO = 1; // 从相册选取图片
	public static final int TAKE_PHOTO = 2; // 拍照
	public static final int CROP_PHOTO = 3; // 拍照后进行图片的裁剪

	/**
	 * 裁剪后图片的保存路径
	 */
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/survey_head.jpg";// temp
																						// file
	public static Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);// The Uri to
																// store the big

	public static String start_time="";
	public static String end_time="";
	
	/**
	 * 选取图片和拍摄的类型--改
	 */
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int CROP = 2;
	private static final int CROP_PICTURE = 3;

	public static Intent openAlbumIntent;

	/**
	 * 选择时间
	 * 
	 * @param context
	 * @param wheelMain
	 * @param tv_start_date
	 */
	public ActionSheetDialog(Context context, WheelMain wheelMain,
			TextView tv_start_date,TextView tv_end_date, String popUp_category) {
		this.context = context;
		this.wheelMain = wheelMain;
		this.tv_start_date = tv_start_date;
		this.tv_end_date=tv_end_date;
		this.popUp_category = popUp_category;

		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	/**
	 * 选择相册或者拍照
	 * 
	 * @param context
	 */
	public ActionSheetDialog(Context context, String popUp_category) {
		this.context = context;
		this.popUp_category = popUp_category;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public ActionSheetDialog builder() {
		// ��ȡDialog����
		View view = LayoutInflater.from(context).inflate(
				R.layout.toast_view_actionsheet, null);

		// ����Dialog��С���Ϊ��Ļ���
		view.setMinimumWidth(display.getWidth());

		// ��ȡ�Զ���Dialog�����еĿؼ�
		// sLayout_content = (ScrollView)
		// view.findViewById(R.id.sLayout_content);
		lLayout_content = (LinearLayout) view
				.findViewById(R.id.lLayout_content);
		iv_line = (ImageView) view.findViewById(R.id.iv_line);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);

		// ����Dialog���ֺͲ���
		dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);

		return this;
	}

	public ActionSheetDialog setTitle(String title) {
		showTitle = true;
		txt_title.setVisibility(View.VISIBLE);
		txt_title.setText(title);
		return this;
	}

	public ActionSheetDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public ActionSheetDialog setCanceledOnTouchOutside(boolean cancel) {
		dialog.setCanceledOnTouchOutside(cancel);
		return this;
	}

	/**
	 * 
	 * @param strItem
	 *            ��Ŀ���
	 * @param color
	 *            ��Ŀ������ɫ������null��Ĭ����ɫ
	 * @param listener
	 * @return
	 */
	public ActionSheetDialog addSheetItem(String strItem, SheetItemColor color,
			OnSheetItemClickListener listener) {
		if (sheetItemList == null) {
			sheetItemList = new ArrayList<SheetItem>();
		}
		sheetItemList.add(new SheetItem(strItem, color, listener));
		return this;
	}

	public void showPicturePicker(int type, boolean isCrop) {
		final boolean crop = true;

		// 类型码
		int REQUEST_CODE;

		switch (type) {
		case TAKE_PICTURE:
			Uri imageUri = null;
			String fileName = null;
			openAlbumIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (crop) {
				REQUEST_CODE = CROP;
				// 删除上一次截图的临时文件
				SharedPreferences sharedPreferences = context
						.getSharedPreferences("temp",
								Context.MODE_WORLD_WRITEABLE);
				ImageTools.deletePhotoAtPathAndName(Environment
						.getExternalStorageDirectory().getAbsolutePath(),
						sharedPreferences.getString("tempName", ""));

				// 保存本次截图临时文件名字
				fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
				Editor editor = sharedPreferences.edit();
				editor.putString("tempName", fileName);
				editor.commit();
			} else {
				REQUEST_CODE = TAKE_PICTURE;
				fileName = "image.jpg";
			}
			imageUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), fileName));
			// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
			openAlbumIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			PHOTO_TYPE = REQUEST_CODE;
			Fragment_ActivityCreate1.waitForStart = false;
			// startActivityForResult(openCameraIntent, REQUEST_CODE);
			break;

		case CHOOSE_PICTURE:
			openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			if (crop) {
				REQUEST_CODE = CROP;
			} else {
				REQUEST_CODE = CHOOSE_PICTURE;
			}
			openAlbumIntent.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			PHOTO_TYPE = REQUEST_CODE;
			Fragment_ActivityCreate1.waitForStart = false;
			// startActivityForResult(openAlbumIntent, REQUEST_CODE);
			break;

		default:
			break;
		}
	}

	public ActionSheetDialog setMyContentView(View v,final int type) {
		lLayout_content.addView(v);

		/**
		 * 选择时间
		 */

		if (popUp_category.equals("choose_time")) {
			txt_cancel.setBackgroundResource(R.drawable.photo_click_style);
			txt_cancel.setTextSize(11);
			txt_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					// ToastUtils.showToast(context, wheelMain.getTime());
					
					
					
					if (type==1) {
						
						
						start_time=wheelMain.getTime();
						end_time=tv_end_date.getText().toString();
						int start_year=Integer.parseInt(start_time.substring(0, 4));
						int start_month=Integer.parseInt(start_time.substring(5, 7));
						int start_day=Integer.parseInt(start_time.substring(8));
						
						int end_year=Integer.parseInt(end_time.substring(0, 4));
						int end_month=Integer.parseInt(end_time.substring(5, 7));
						int end_day=Integer.parseInt(end_time.substring(8));

						if (start_year>end_year) {
							end_time=wheelMain.getTime();
							tv_end_date.setText(end_time);
						}
						else if(start_year==end_year) {
							
							if (start_month>end_month) {
								end_time=wheelMain.getTime();
								tv_end_date.setText(end_time);
							}
							else if(start_month==end_month) {
								
								if (start_day>end_day) {
									end_time=wheelMain.getTime();
									tv_end_date.setText(end_time);
								}
							}
							
						}
						
						tv_start_date.setText(wheelMain.getTime());
						tv_start_date.setTextColor(context.getResources().getColor(R.color.text_666666));
					}
					
					else {

						start_time=tv_start_date.getText().toString();
						end_time=wheelMain.getTime();
						int start_year=Integer.parseInt(start_time.substring(0, 4));
						int start_month=Integer.parseInt(start_time.substring(5, 7));
						int start_day=Integer.parseInt(start_time.substring(8));
						
						int end_year=Integer.parseInt(end_time.substring(0, 4));
						int end_month=Integer.parseInt(end_time.substring(5, 7));
						int end_day=Integer.parseInt(end_time.substring(8));
						
						Log.d("start_time", start_time);
						Log.d("end_time", end_time);
						
						
						Log.d("start_time_int", start_year+"e"+start_month+"e"+start_day);
						
						if (start_year>end_year) {
							
							tv_end_date.setText(start_time);
						}
						else if(start_year==end_year) {
							
							if (start_month>end_month) {
			
								tv_end_date.setText(start_time);
							}
							else if(start_month==end_month) {
								
								if (start_day>=end_day) {
									
									tv_end_date.setText(start_time);
								}
								else {
									tv_end_date.setText(wheelMain.getTime());
								}
							}
							else {
								tv_end_date.setText(wheelMain.getTime());
							}
						}
						else {
							tv_end_date.setText(wheelMain.getTime());
						}
						tv_end_date.setTextColor(context.getResources().getColor(R.color.text_666666));
						
					}
					

					

				}
			});

			return this;

		}

		/**
		 * 选择时间-查询基金
		 */

		if (popUp_category.equals("choose_time_fund")) {
			txt_cancel.setBackgroundResource(R.drawable.photo_click_style);
			txt_cancel.setTextSize(11);
			txt_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
					
				if (type==1) {
						start_time=wheelMain.getTime();
						end_time=tv_end_date.getText().toString();
						int start_year=Integer.parseInt(start_time.substring(0, 4));
						int start_month=Integer.parseInt(start_time.substring(5, 7));
						int start_day=Integer.parseInt(start_time.substring(8));
						
						int end_year=Integer.parseInt(end_time.substring(0, 4));
						int end_month=Integer.parseInt(end_time.substring(5, 7));
						int end_day=Integer.parseInt(end_time.substring(8));
						
						Log.d("start_time", start_time);
						Log.d("end_time", end_time);
						
						
						Log.d("start_time_int", start_year+"e"+start_month+"e"+start_day);
						
						if (start_year>end_year) {
							end_time=wheelMain.getTime();
							tv_end_date.setText(end_time);
						}
						else if(start_year==end_year) {
							
							if (start_month>end_month) {
								end_time=wheelMain.getTime();
								tv_end_date.setText(end_time);
							}
							else if(start_month==end_month) {
								
								if (start_day>end_day) {
									end_time=wheelMain.getTime();
									tv_end_date.setText(end_time);
								}
							}
							
						}
						
						tv_start_date.setText(wheelMain.getTime());
						tv_start_date.setTextColor(context.getResources().getColor(R.color.black));
					}
					
					else {

						start_time=tv_start_date.getText().toString();
						end_time=wheelMain.getTime();
						int start_year=Integer.parseInt(start_time.substring(0, 4));
						int start_month=Integer.parseInt(start_time.substring(5, 7));
						int start_day=Integer.parseInt(start_time.substring(8));
						
						int end_year=Integer.parseInt(end_time.substring(0, 4));
						int end_month=Integer.parseInt(end_time.substring(5, 7));
						int end_day=Integer.parseInt(end_time.substring(8));
						
						Log.d("start_time", start_time);
						Log.d("end_time", end_time);
						
						
						Log.d("start_time_int", start_year+"e"+start_month+"e"+start_day);
						
						if (start_year>end_year) {
							
							tv_end_date.setText(start_time);
						}
						else if(start_year==end_year) {
							
							if (start_month>end_month) {
			
								tv_end_date.setText(start_time);
							}
							else if(start_month==end_month) {
								
								if (start_day>=end_day) {
									
									tv_end_date.setText(start_time);
								}
								else {
									tv_end_date.setText(wheelMain.getTime());
								}
							}
							else {
								tv_end_date.setText(wheelMain.getTime());
							}
						}
						else {
							tv_end_date.setText(wheelMain.getTime());
						}
						tv_end_date.setTextColor(context.getResources().getColor(R.color.black));
						
					}

				}
			});

			return this;

		}

		// 选择照片popup
		if (popUp_category.equals("photo")) {

			iv_line.setVisibility(View.GONE);
			txt_title.setVisibility(View.GONE);
			txt_cancel.setBackgroundResource(R.drawable.photo_click_style);
			txt_cancel.setText(context.getResources()
					.getString(R.string.cancel));
			txt_cancel.setTextSize(13);
			txt_cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			TextView tv_alumb = (TextView) v.findViewById(R.id.tv_pick_photo);
			TextView tv_film = (TextView) v.findViewById(R.id.tv_take_photo);

			tv_alumb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// pickPhoto();
					showPicturePicker(CHOOSE_PICTURE, true);
					dialog.dismiss();
				}
			});

			tv_film.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// pickPhoto();
					showPicturePicker(TAKE_PICTURE, true);
					dialog.dismiss();
				}
			});

			return this;
		}
		// 转发，复制，举报，收藏
		if (popUp_category.equals("more_view")) {
			TextView tv_transmit = (TextView) v.findViewById(R.id.tv_transmit);
			TextView tv_copy = (TextView) v.findViewById(R.id.tv_copy);
			TextView tv_collect = (TextView) v.findViewById(R.id.tv_collect);
			TextView tv_report = (TextView) v.findViewById(R.id.tv_report);
			tv_transmit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setPosition(0);
					dialog.dismiss();
				}
			});
			tv_copy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setPosition(1);
					dialog.dismiss();
				}
			});
			tv_collect.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setPosition(2);
					dialog.dismiss();
				}
			});
			tv_report.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setPosition(3);
					dialog.dismiss();
				}
			});

			return this;
		}
		return this;
	}

	public void setPosition(int p) {
		position = p;
	}

	public static int getPosition() {
		return position;
	}

	public void dismiss() {
		dialog.dismiss();
	}

	/** ������Ŀ���� */
	private void setSheetItems() {
		if (sheetItemList == null || sheetItemList.size() <= 0) {
			return;
		}

		int size = sheetItemList.size();

		// TODO �߶ȿ��ƣ�����ѽ���취
		// �����Ŀ����ʱ����Ƹ߶�
		if (size >= 7) {
			// LinearLayout.LayoutParams params = (LayoutParams) sLayout_content
			// .getLayoutParams();
			// params.height = display.getHeight() / 2;
			// sLayout_content.setLayoutParams(params);
		}

		// ѭ�������Ŀ
		for (int i = 1; i <= size; i++) {
			final int index = i;
			SheetItem sheetItem = sheetItemList.get(i - 1);
			String strItem = sheetItem.name;
			SheetItemColor color = sheetItem.color;
			final OnSheetItemClickListener listener = (OnSheetItemClickListener) sheetItem.itemClickListener;

			TextView textView = new TextView(context);
			textView.setText(strItem);
			textView.setTextSize(18);
			textView.setGravity(Gravity.CENTER);

			// ����ͼƬ
			if (size == 1) {
				if (showTitle) {
					textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
				} else {
					textView.setBackgroundResource(R.drawable.actionsheet_single_selector);
				}
			} else {
				if (showTitle) {
					if (i >= 1 && i < size) {
						textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
					} else {
						textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
					}
				} else {
					if (i == 1) {
						textView.setBackgroundResource(R.drawable.actionsheet_top_selector);
					} else if (i < size) {
						textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
					} else {
						textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
					}
				}
			}

			// ������ɫ
			if (color == null) {
				textView.setTextColor(Color.parseColor(SheetItemColor.Blue
						.getName()));
			} else {
				textView.setTextColor(Color.parseColor(color.getName()));
			}

			// �߶�
			float scale = context.getResources().getDisplayMetrics().density;
			int height = (int) (45 * scale + 0.5f);
			textView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, height));

			// ����¼�
			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(index);
					dialog.dismiss();
				}
			});

			lLayout_content.addView(textView);
		}
	}

	public void show() {
		// setSheetItems();
		dialog.show();
	}

	public interface OnSheetItemClickListener {
		void onClick(int which);
	}

	public class SheetItem {
		String name;
		OnSheetItemClickListener itemClickListener;
		SheetItemColor color;

		public SheetItem(String name, SheetItemColor color,
				OnSheetItemClickListener itemClickListener) {
			this.name = name;
			this.color = color;
			this.itemClickListener = itemClickListener;
		}
	}

	public enum SheetItemColor {
		Blue("#037BFF"), Red("#FD4A2E");

		private String name;

		private SheetItemColor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * 调用相册
	 */
	private void pickPhoto() {
		PHOTO_TYPE = PICK_PHOTO;
		Toast.makeText(context, "PHOTO_TYPE", 1).show();
		intent = new Intent(Intent.ACTION_GET_CONTENT, null);

		intent.setType("image/*");

		intent.putExtra("crop", "true");

		intent.putExtra("aspectX", 1);

		intent.putExtra("aspectY", 1); // aspectX和aspectY是裁剪的框的比例

		intent.putExtra("outputX", 300);

		intent.putExtra("outputY", 300); // //outputX和outputY是裁剪后得到的图片的长和宽的大小

		intent.putExtra("scale", true);

		intent.putExtra("return-data", false);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 设置图片的保存路径

		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		intent.putExtra("noFaceDetection", true); // no face detection

		Fragment_ActivityCreate1.waitForStart = false;
		// Toast.makeText(context, ""+Fragment_ActivityCreate1.canStart,
		// 1).show();
		// ActivityCreateActivity.class.startActivityForResult(intent,
		// PICK_PHOTO);
	}

}
