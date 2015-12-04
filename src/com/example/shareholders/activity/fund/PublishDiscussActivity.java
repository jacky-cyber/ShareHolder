package com.example.shareholders.activity.fund;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.TestPicActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.Bimp;
import com.example.shareholders.db.entity.FileUtils;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_publish_discuss)
public class PublishDiscussActivity extends Activity {

	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2; //
	private GridAdapter adapter;
	private ImageView iv_return;
	String securitySymbol;
	@ViewInject(R.id.et_publish)
	private EditText et_publish;
	@ViewInject(R.id.tv_send)
	private TextView tv_send;
	
	private int PUBLISH_TOPIC = 1;
	RequestQueue volleyRequestQueue = null;

	String securityTopicType = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		tv_send.setTextColor(getResources().getColor(R.color.gray));
		securitySymbol = getIntent().getExtras().getString("securitySymbol");
		try {
			securityTopicType = getIntent().getExtras().getString(
					"securityTopicType");
		} catch (Exception e) {
			// TODO: handle exception
			securityTopicType = "FUND";
		} finally {
			if (securityTopicType == null) {
				securityTopicType = "FUND";
			}
		}
		volleyRequestQueue = Volley.newRequestQueue(this);
		Init();
		setTextWhater();
	}
	private void setTextWhater() {
		et_publish.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (et_publish.getText().toString().equals("")) {
					tv_send.setTextColor(getResources().getColor(
							R.color.gray));
					tv_send.setClickable(false);
				} else {
					tv_send.setTextColor(getResources().getColor(R.color.white));
					tv_send.setClickable(true);
				}
			}
		});
	}
	@OnClick({ R.id.tv_send, R.id.iv_return })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.tv_send:
			String content = et_publish.getText().toString();
			if (content.equals("") && Bimp.drr.size() == 0) {
				ToastUtils.showToast(getApplicationContext(), "内容不能为空");
			} else {
				PublishTopic(content);
			}
			break;

		default:
			break;
		}
	}

	public void Init() {
		// noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
		// noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// adapter = new GridAdapter(this);
		// adapter.update();
		// noScrollgridview.setAdapter(adapter);
		// noScrollgridview.setOnItemClickListener(new OnItemClickListener() {
		//
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// if (arg2 == Bimp.bmp.size()) {
		// new PopupWindows(getApplicationContext(),
		// noScrollgridview);
		// } else {
		// Intent intent = new Intent(getApplicationContext(),
		// PhotoActivity.class);
		// intent.putExtra("ID", arg2);
		//
		// startActivity(intent);
		// }
		// }
		// });
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) v.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				Bimp.bmp.clear();
				finish();
			}
		});
	}

	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (Bimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_published_grida,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == Bimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public String getString(String s) {
		String path = null;
		if (s == null)
			return "";
		for (int i = s.length() - 1; i > 0; i++) {
			s.charAt(i);
		}
		return path;
	}

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(),
							TestPicActivity.class);
					Bundle bundle = new Bundle();
					// bundle.putString("activity", "published_activity");
					// bundle.putString("surveyUuid", surveyUuid);
					// intent.putExtras(bundle);
					// finish();
					startActivity(intent);

					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/myimage/");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir,
				new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
						+ ".jpg");
		path = file.getPath();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)),
				TAKE_PICTURE);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.drr.size() < 9 && resultCode == -1) {
				Bimp.drr.add(path);
			}
			break;
		}
	}

	/*
	 * 发表评论
	 * 
	 * @param num: 0为无图，1为有图
	 */
	private void PublishTopic(String content) {
		final Intent intent = new Intent(
				"com.example.shareholders.fragment.RECEIVER");
		String url = AppConfig.URL_TOPIC + "add.json?access_token=";
		url += RsSharedUtil.getString(this, "access_token");
		Log.d("PublishTopicurl", "PublishTopicurl" + url);
		JSONObject params = new JSONObject();

		try {
			params.put("content", content);
			params.put("securitySymbol", securitySymbol);

			params.put("securityTopicType", securityTopicType);
			Log.d("params", params.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("PublishTopicurl", response);
						intent.putExtra("progress", 1);
						sendBroadcast(intent);
						InternetDialog internetDialog = new InternetDialog(PublishDiscussActivity.this);
						internetDialog.showInternetDialog("发表成功", true);
						//定时任务
						TimerTask task = new TimerTask() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								finish();
							}
						};
						//定时器
						Timer timer = new Timer();
						//2秒后执行任务
						timer.schedule(task, 2000);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						LogUtils.d(error.toString());
						InternetDialog internetDialog = new InternetDialog(PublishDiscussActivity.this);
						internetDialog.showInternetDialog("发表失败", false);
					}
				});

		volleyRequestQueue.add(stringRequest);
	}
}
