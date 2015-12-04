package com.example.shareholders.activity.shop;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

@ContentView(R.layout.activity_punish_comment)
public class PunishCommentActivity extends Activity {

	@ViewInject(R.id.rl_pc)
	private RelativeLayout rl_pc;
	@ViewInject(R.id.iv_pc_goods)
	private ImageView iv_pc_goods;
	@ViewInject(R.id.tv_pc_goods_name)
	private TextView tv_pc_goods_name;
	@ViewInject(R.id.tv_pc_goods_price)
	private TextView tv_pc_goods_price;
	@ViewInject(R.id.et_pc)
	private EditText et_pc;
	@ViewInject(R.id.rb_pc)
	private RatingBar rb_pc;

	private String prodUuid = null;

	// 第一张商品相片
	private String iv_productUrl = null;
	// 商品名称
	private String tv_productName = null;
	// 商品价格
	private String tv_price = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		Init();
	}

	@OnClick({ R.id.title_fs_note, R.id.tv_pc_submit, R.id.rl_pc_goods,R.id.rl_return })
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		case R.id.tv_pc_submit:
			if (TextUtils.isEmpty(et_pc.getText())) {
				InternetDialog internetDialog = new InternetDialog(
						PunishCommentActivity.this);
				internetDialog.showInternetDialog("评论不能为空", false);
			} else {
				addComments();
			}
			break;
		case R.id.rl_pc_goods:
			prodUuid = getIntent().getExtras().getString("prodUuid");
			Intent intent = new Intent(PunishCommentActivity.this,
					GoodsDetailsActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("prodUuid", prodUuid);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void Init() {
		// TODO Auto-generated method stub
		iv_productUrl = getIntent().getExtras().getString("iv_productUrl");
		tv_productName = getIntent().getExtras().getString("tv_productName");
		tv_price = getIntent().getExtras().getString("tv_price");
		Log.d("dj_pu_bundle", iv_productUrl + tv_productName + tv_price);
		ImageLoader.getInstance().displayImage(iv_productUrl, iv_pc_goods);
		tv_pc_goods_name.setText(tv_productName);
		tv_pc_goods_price.setText(tv_price);
	}

	/**
	 * 弹出提示评价成功
	 * 
	 * @param context
	 * @param viewGroup
	 * @return
	 */
	public void popupSuccess(Context context) {
		final View contentView = LayoutInflater.from(context).inflate(
				R.layout.item_toast_popup, null);

		TextView tv_item = (TextView) contentView.findViewById(R.id.tv_item);
		tv_item.setText("评价成功");

		final AlertDialog dialog = new AlertDialog.Builder(
				PunishCommentActivity.this).create();
		dialog.show();
		dialog.setContentView(contentView);

	}

	/**
	 * TimerTask 定时任务
	 */

	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			setResult(2); // 设置返回码为2
			finish();
		}
	};

	/**
	 * 添加商品评论
	 */
	private void addComments() {
		String url = AppConfig.URL_SHOP + "comment/addcomment";
		Log.d("dj_addComments_url", url);
		prodUuid = getIntent().getExtras().getString("prodUuid");
		Log.d("dj_prodUuid", prodUuid);

		Log.d("dj_custUuid", RsSharedUtil.getString(
				PunishCommentActivity.this, AppConfig.UUID));
		Log.d("dj_commtContent", et_pc.getText().toString().trim());
		Log.d("dj_rb_pc.getRating()", rb_pc.getRating() + "");

		Log.d("dj_rb_pc.getRating()", rb_pc.getRating() + "");

		Log.d("dj_rb_pc.getRating()", rb_pc.getRating() + "");

		JSONObject params = new JSONObject();
		try {
			params.put("prodUuid", prodUuid);
			params.put("custUuid", RsSharedUtil.getString(
					PunishCommentActivity.this, AppConfig.UUID));
			params.put("commtType", "comment");
			params.put("commtScore", (int) rb_pc.getRating());
			params.put("commtContent", et_pc.getText().toString().trim());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("dj_punish_reponse", response);

						InternetDialog internetDialog = new InternetDialog(
								PunishCommentActivity.this);
						internetDialog.showInternetDialog("评价成功", true);
						Timer timer = new Timer(true);
						timer.schedule(timerTask, 2000);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("error", "发表评论失败");

						InternetDialog internetDialog = new InternetDialog(
								PunishCommentActivity.this);
						internetDialog.showInternetDialog("发表评论失败", false);
					}
				});

		stringRequest.setTag("addComments");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("addComments");
		super.onDestroy();
	}

	private Integer[] mThumbIds = { R.drawable.demo, R.drawable.demo,
			R.drawable.demo, R.drawable.demo };

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return mThumbIds.length;
		}

		@Override
		public Object getItem(int position) {
			return mThumbIds[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 定义一个ImageView,显示在GridView里
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setBackgroundResource(mThumbIds[position]);
			imageView.setLayoutParams(new AbsListView.LayoutParams(parent
					.getWidth() / 4, parent.getWidth() / 4));
			return imageView;
		}
	}

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
