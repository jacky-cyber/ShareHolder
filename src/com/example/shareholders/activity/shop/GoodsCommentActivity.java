package com.example.shareholders.activity.shop;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_goods_comment)
public class GoodsCommentActivity extends Activity {
	
	private BitmapUtils bitmapUtils = null;

	@ViewInject(R.id.lv_gc_comment)
	private ListView lv_gc_comment;

	@ViewInject(R.id.ll_wupinglun)
	private LinearLayout ll_wupinglun;
	private ArrayList<HashMap<String, Object>> al_comment;
	private CommentAdapter commentAdapter;

	private String prodUuid = null;

	// 第一张商品相片
	private String iv_productUrl = null;
	// 商品名称
	private String tv_productName = null;
	// 商品价格
	private String tv_price = null;

//	private AlertDialog internertDialog = null;
	private LoadingDialog loadingDialog ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		bitmapUtils = new BitmapUtils(GoodsCommentActivity.this);
		bitmapUtils .configDefaultLoadingImage(R.drawable.morentouxiang);
		bitmapUtils .configDefaultLoadFailedImage(R.drawable.morentouxiang);
		loadingDialog = new LoadingDialog(GoodsCommentActivity.this);
		Init();
	}

	@OnClick({ R.id.iv_write_letter, R.id.title_fs_note ,R.id.rl_return})
	private void OnClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		case R.id.iv_write_letter:
			Intent intent = new Intent(this, PunishCommentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("prodUuid", prodUuid);
			bundle.putString("iv_productUrl", iv_productUrl);
			bundle.putString("tv_productName", tv_productName);
			bundle.putString("tv_price", tv_price);
			intent.putExtras(bundle);
			startActivityForResult(intent, 1); // 设置请求码为1
			break;
		case R.id.rl_return:
			finish();
			break;

		default:
			break;
		}
	}

	private void Init() {
		loadingDialog.showLoadingDialog();
//		showLoadingDialog();
		getComments();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {
			Init();
		}
	}

	/**
	 * 正在加载
	 */
//	private void showLoadingDialog() {
//		internertDialog = new AlertDialog.Builder(this).create();
//		internertDialog.show();
//		internertDialog.setCancelable(false);
//
//		Window window = internertDialog.getWindow();
//		window.setContentView(R.layout.dialog_no_internet);
//
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.dimAmount = 0.0f;
//		window.setAttributes(lp);
//		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//	}

	/**
	 * 获取商品评论
	 */
	private void getComments() {
		ll_wupinglun.setVisibility(View.GONE);
		String url = AppConfig.URL_SHOP + "comment/retrieveComment";
		Log.d("dj_getComments_url", url);
		prodUuid = getIntent().getExtras().getString("prodUuid");
		iv_productUrl = getIntent().getExtras().getString("iv_productUrl");
		tv_productName = getIntent().getExtras().getString("tv_productName");
		tv_price = getIntent().getExtras().getString("tv_price");
		Log.d("dj_gc_bundle", iv_productUrl + tv_productName + tv_price);
		JSONObject params = new JSONObject();
		try {
			params.put("token", RsSharedUtil.getString(
					GoodsCommentActivity.this, AppConfig.ACCESS_TOKEN));
			params.put("prodUuid", prodUuid);
			params.put("pageIndex", 1);
			params.put("pageSize", 20);
			Log.d("dj_token", RsSharedUtil.getString(
					GoodsCommentActivity.this, AppConfig.ACCESS_TOKEN));
			Log.d("dj_proUuid", prodUuid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				url, params, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d("liang_response", response);

				try {
					JSONArray jsonArray = new JSONArray(response);
					ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> data = null;
					Iterator<String> iterator = null;

					for (int i = 0; i < jsonArray.length(); i++) {
						iterator = jsonArray.getJSONObject(i).keys();
						data = new HashMap<String, Object>();

						while (iterator.hasNext()) {
							String key = iterator.next();
							data.put(key, jsonArray.getJSONObject(i)
									.get(key));
						}

						datas.add(data);

					}

					lv_gc_comment.setAdapter(new CommentAdapter(
							GoodsCommentActivity.this, datas));
//					internertDialog.dismiss();
					loadingDialog.dismissDialog();
					Log.d("dj_datas", datas + "");
					if (datas.size() == 0) {
						Log.d("dj_ll_wupinglun", "visble");
						ll_wupinglun.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("liang_error1", "解析评论信息出错：" + e.toString());
					InternetDialog internetDialog = new InternetDialog(
							GoodsCommentActivity.this);
					internetDialog.showInternetDialog( "解析评论信息出错",
							false);
//					internertDialog.dismiss();
					loadingDialog.dismissDialog();
					ll_wupinglun.setVisibility(View.VISIBLE);
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("liang_error2", "获取商品评论失败");
				InternetDialog internerDialog = new InternetDialog(
						GoodsCommentActivity.this);
				internerDialog.showInternetDialog( "获取商品评论失败", false);
//				internertDialog.dismiss();
				loadingDialog.dismissDialog();
				ll_wupinglun.setVisibility(View.VISIBLE);
			}
		});

		stringRequest.setTag("getComment");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		MyApplication.getRequestQueue().cancelAll("getComment");

		super.onDestroy();
	}

	private class CommentAdapter extends BaseAdapter {

		Context context;
		ArrayList<HashMap<String, Object>> list;

		public CommentAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_goods_comment, parent, false);
				//评论者头像
				viewHolder.iv_gc_face = (CircleImageView) view
						.findViewById(R.id.iv_gc_face);
				//评论者名字
				viewHolder.tv_gc_name = (TextView) view
						.findViewById(R.id.tv_gc_name);
				//好评等级
				viewHolder.rb_comment = (RatingBar) view
						.findViewById(R.id.rb_comment);
				//评论内容
				viewHolder.tv_gc_content = (TextView) view
						.findViewById(R.id.tv_gc_content);
				//评论时间
				viewHolder.tv_gc_time = (TextView) view
						.findViewById(R.id.tv_gc_time);
				//商家回复布局
				viewHolder.ll_reply = (LinearLayout) view
						.findViewById(R.id.ll_reply);
				//商家回复内容
				viewHolder.tv_gc_reply = (TextView) view
						.findViewById(R.id.tv_gc_reply);

				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			// viewHolder.iv_gc_face

			try {
				JSONObject jsonObject2 = new JSONObject(list.get(position).get("customer").toString());
				String tv_custName = jsonObject2.getString("userName");
				String iv_logo = jsonObject2.getString("userLogo");
				// 评论者的名字
				viewHolder.tv_gc_name.setText(tv_custName);
				bitmapUtils.display(viewHolder.iv_gc_face , iv_logo);
				Log.d("customer", tv_custName);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// 评分
			String starNum = list.get(position).get("commtScore").toString();
			viewHolder.rb_comment.setNumStars(Integer.parseInt(starNum));

			// 评论内容
			viewHolder.tv_gc_content.setText(list.get(position)
					.get("commtContent").toString());

			// 评论时间
			JSONObject dateObject = (JSONObject) list.get(position).get(
					"commtDate");
			try {
				String date_string = dateObject.getString("time");
				long date_long = Long.parseLong(date_string);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy.MM.dd HH:mm:ss");
				viewHolder.tv_gc_time.setText(simpleDateFormat.format(new Date(
						date_long)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("dj_error", "data_error:" + e.toString());
			}
			
			// 商家回复
			String commtReply = list.get(position).get("commtReply").toString();
			if (commtReply.equals("")) {
				viewHolder.ll_reply.setVisibility(View.GONE);
			} else {
				viewHolder.tv_gc_reply.setText(commtReply);
			}

			// viewHolder.tv_gc_time.setText("");

			return view;
		}

		class ViewHolder {
			CircleImageView iv_gc_face;
			TextView tv_gc_name;
			RatingBar rb_comment;
			TextView tv_gc_content;
			TextView tv_gc_time;
			LinearLayout ll_reply;
			TextView tv_gc_reply; // 商家回复
		}

	}
}
