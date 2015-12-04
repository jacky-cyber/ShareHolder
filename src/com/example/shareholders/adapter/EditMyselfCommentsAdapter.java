package com.example.shareholders.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.survey.TranspondActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

//构造适配器
public class EditMyselfCommentsAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, Object>> list;
	private Context context;
	private LayoutInflater mInflater;
	private RelativeLayout rl_parent;
	private String followed;
	private boolean isTopVisible;

	public boolean isTopVisible() {
		return isTopVisible;
	}

	public void setTopVisible(boolean isTopVisible) {
		this.isTopVisible = isTopVisible;
	}

	public EditMyselfCommentsAdapter(Context context,
			ArrayList<HashMap<String, Object>> list, RelativeLayout rl_parent) {
		this.context = context;
		this.list = list;
		this.rl_parent = rl_parent;
		mInflater = LayoutInflater.from(context);
		isTopVisible = true;
	}

	public EditMyselfCommentsAdapter(Context context,
			ArrayList<HashMap<String, Object>> list, RelativeLayout rl_parent,
			boolean isTopVisible) {
		this.context = context;
		this.list = list;
		this.rl_parent = rl_parent;
		mInflater = LayoutInflater.from(context);
		this.isTopVisible = isTopVisible;
	}

	public EditMyselfCommentsAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_comment_stock,
					parent, false);
		}

		if (isTopVisible) {
			TextView tv_securityName = AbViewHolder.get(convertView,
					R.id.tv_securityName);
			tv_securityName.setText(list.get(position).get("securityName")
					.toString());
			// 关注股票的代码
			TextView tv_securitySymbol = AbViewHolder.get(convertView,
					R.id.tv_symbol);
			tv_securitySymbol.setText(list.get(position).get("securitySymbol")
					.toString());
		} else {
			RelativeLayout top = AbViewHolder.get(convertView, R.id.top);
			top.setVisibility(View.GONE);
		}
		// 评论者头像
		CircleImageView ci_face = AbViewHolder.get(convertView, R.id.ci_face);
		ImageLoader.getInstance().displayImage(
				list.get(position).get("creatorLogoUrl").toString(), ci_face);

		// 评论者姓名
		TextView tv_name = AbViewHolder.get(convertView, R.id.tv_name);
		tv_name.setText(list.get(position).get("creatorName").toString());

		// 话题发出的时间__
		TextView tv_time = AbViewHolder.get(convertView, R.id.tv_time);
		long Time = Long.parseLong(list.get(position).get("creationTime")
				.toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String creationTime = dateFormat.format(new Date(Time));
		tv_time.setText(creationTime);

		// 内容
		TextView tv_comment_content = AbViewHolder.get(convertView,
				R.id.tv_comment_content);
		tv_comment_content
				.setText(list.get(position).get("content").toString());

		// 转发内容的布局
		LinearLayout ll_transpon = AbViewHolder.get(convertView,
				R.id.ll_transpon);

		// 转发源头像
		ImageView iv_creator_face = AbViewHolder.get(convertView,
				R.id.iv_creator_face);

		// 转发源姓名
		TextView tv_creator_name = AbViewHolder.get(convertView,
				R.id.tv_creator_name);

		// 转发源内容
		TextView tv_creator_content = AbViewHolder.get(convertView,
				R.id.tv_creator_content);

		// 分享数
		final TextView tv_share_num = AbViewHolder.get(convertView,
				R.id.tv_share_num);
		// tv_share_num.setText(list.get(position).get("transpondNum")
		// .toString());

		// 点赞数
		final TextView tv_praise_num = AbViewHolder.get(convertView,
				R.id.tv_praise_num);
		tv_praise_num.setText(list.get(position).get("likeNum").toString());
		// 评论数
		TextView tv_comment_num = AbViewHolder.get(convertView,
				R.id.tv_comment_num);
		tv_comment_num.setText(list.get(position).get("commentNum").toString());
		// 浏览量
		TextView tv_scan_num = AbViewHolder.get(convertView, R.id.tv_scan_num);
		tv_scan_num.setText(list.get(position).get("readNum").toString());

		// 点赞
		final ImageView iv_praise = AbViewHolder.get(convertView,
				R.id.iv_praise);

		// 是否点赞
		String liked = list.get(position).get("liked").toString();

		boolean like = Boolean.parseBoolean(liked);

		if (like) {
			iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
		} else {
			iv_praise.setImageResource(R.drawable.btn_dianzan_normal_cl);
		}

		// 设置点赞监听
		RelativeLayout rl_praise = AbViewHolder.get(convertView,
				R.id.rl_praise1);
		iv_praise.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Log.d("dj_praise", tv_praise_num.getText().toString());
				praiseItem(iv_praise, tv_praise_num, position, list);
			}
		});

		// 设置分享监听
		RelativeLayout rl_share = AbViewHolder.get(convertView, R.id.rl_share);
		rl_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d("dj_share", "click");
				showShare();
			}
		});

		// 设置更多监听
		ImageView iv_more = AbViewHolder.get(convertView, R.id.iv_more);
		iv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initMenu(R.layout.item_comment_more_popup, position);
			}
		});

		// 判断是否有转发内容
		JSONObject refTopic = null;
		try {
			refTopic = new JSONObject(list.get(position).get("refTopic")
					.toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (refTopic == null) {
			ll_transpon.setVisibility(View.GONE);
		} else {
			ll_transpon.setVisibility(View.VISIBLE);
			try {
				// 转发内容的头像
				// ImageLoader.getInstance().displayImage(refTopic.get("creatorLogoUrl").toString(),
				// iv_creator_face);
				iv_creator_face.setVisibility(View.GONE);
				// 转发的名字
				tv_creator_name.setText(refTopic.get("creatorName").toString());
				// 转发的内容
				tv_creator_content.setText(refTopic.get("content").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("dj_error_refTopic", e.toString());
			}
		}

		return convertView;
	}

	/*
	 * 对话题点赞功能
	 * 
	 * @param iv_praise
	 * 
	 * @param tv_praise_num
	 * 
	 * @param position
	 * 
	 * @param topics
	 */

	private void praiseItem(final ImageView iv_praise,
			final TextView tv_praise_num, final int position,
			final List<HashMap<String, Object>> topics) {

		iv_praise.setClickable(false);
		int praiseNum = Integer.parseInt(tv_praise_num.getText().toString());
		praiseNum++;
		tv_praise_num.setText("" + praiseNum);
		iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);

		String uuid = topics.get(position).get("topicUuid").toString();
		String url = AppConfig.URL_TOPIC + "like.json?topicUuid=" + uuid;
		url = url + "&access_token="
				+ RsSharedUtil.getString(context, AppConfig.ACCESS_TOKEN);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						topics.get(position).put("liked", true);
						iv_praise.setClickable(true);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						int praiseNum = Integer.parseInt(tv_praise_num
								.getText().toString());
						praiseNum--;
						tv_praise_num.setText("" + praiseNum);
						if (topics.get(position).get("liked").toString()
								.equals("true")) {
							iv_praise
									.setImageResource(R.drawable.btn_dianzan_selected_cl);
						} else {
							iv_praise
									.setImageResource(R.drawable.btn_dianzan_normal_cl);
						}
						iv_praise.setClickable(true);

						try {
							Log.d("error.statuCode()", error.statuCode() + "");
							JSONObject jsonObject = new JSONObject(error.data());
							ToastUtils.showToast(context,
									jsonObject.getString("description"));

						} catch (Exception e) {
						}
					}
				});
		MyApplication.getRequestQueue().add(stringRequest);
	}

	// 分享的方法
	private void showShare() {
		// background.setAlpha(0.7f);
		// popupWindow = new SharePopupWindow(context, rl_parent);
		//
		// popupWindow.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss() {
		// background.setAlpha(0.0f);
		//
		// }
		// });
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param adapter
	 *            .context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(int view, final int position) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);

		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);

		// popwindow位置
		popupWindow.showAtLocation(rl_parent, Gravity.BOTTOM, 0, 0);
		// background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				// background.setAlpha(0.0f);
			}
		});
		TextView tv_transmit = (TextView) contentView
				.findViewById(R.id.tv_transmit);
		TextView tv_copy = (TextView) contentView.findViewById(R.id.tv_copy);
		TextView tv_collect = (TextView) contentView
				.findViewById(R.id.tv_collect);
		TextView tv_report = (TextView) contentView
				.findViewById(R.id.tv_report);
		TextView tv_cancel = (TextView) contentView
				.findViewById(R.id.tv_cancel);
		tv_transmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Bundle bundle = new Bundle();
				// bundle.putString("surveyUuid", creatorUuid);
				// 把话题id传过去转发的Activity，将作为下一级的转发源id
				bundle.putString("refUuid", list.get(position).get("topicUuid")
						.toString());
				bundle.putString("creatorName",
						list.get(position).get("creatorName").toString());
				bundle.putString("medias", list.get(position).get("medias")
						.toString());
				// medias标志，传过去的时候便不用处理medias;
				bundle.putString("mediasFlag", "flag");
				bundle.putString("securitySymbol",
						list.get(position).get("securitySymbol").toString());
				bundle.putString("content", list.get(position).get("content")
						.toString());

				Intent intent = new Intent(context, TranspondActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);

				// Toast(context, R.layout.item_toast_popup, rl_parent, 0);

				popupWindow.dismiss();
			}
		});
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast(context, R.layout.item_toast_popup, rl_parent, 1);
				popupWindow.dismiss();
			}
		});
		followed = list.get(position).get("followed").toString();
		if (followed.equals("true")) {
			tv_collect.setText("取消收藏");
		}
		if (followed.equals("false"))
			tv_collect.setText("收藏");
		tv_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String url = AppConfig.URL_USER + "topic.json?access_token=";
				url = url + RsSharedUtil.getString(context, "access_token")
						+ "&uuid="
						+ list.get(position).get("topicUuid").toString();
				StringRequest stringRequest = new StringRequest(url, null,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {
								Log.d("RRRRRRRRRRRRRRRRRRRRR",
										response.toString());
								// TODO Auto-generated method stub
								if (response.equals("true")) {
									Toast(context, R.layout.item_toast_popup,
											rl_parent, 2);
									followed = "true";
								}
								if (response.equals("false")) {
									Toast(context, R.layout.item_toast_popup,
											rl_parent, 4);
									followed = "false";
								}
							}

						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								Log.d("RRRRRRRRRRRRRRRRRRRRR", error.toString());
							}
						});
				MyApplication.getRequestQueue().add(stringRequest);
				// Toast(context, R.layout.item_toast_popup, rl_parent, 2);
				popupWindow.dismiss();
			}
		});
		tv_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast(context, R.layout.item_toast_popup, rl_parent, 3);
				popupWindow.dismiss();
			}
		});
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
	}

	/**
	 * 弹出菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void Toast(Context context, int view, View rl, int position) {
		final View contentView = LayoutInflater.from(context).inflate(view,
				null);
		TextView tv_item = (TextView) contentView.findViewById(R.id.tv_item);
		switch (position) {
		case 1:
			tv_item.setText("已复制");
			break;
		case 2:
			tv_item.setText("已收藏");
			break;
		case 3:
			tv_item.setText("已举报");
			break;

		}
		// 生成popupWindow
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 设置内容
		popupWindow.setContentView(contentView);
		// 设置点及外部回到外面退出popupwindow
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		// 获取xoff
		int xpos = manager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;
		// popwindow位置
		popupWindow.showAtLocation(rl, Gravity.CENTER, 0, 0);
		// background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				// background.setAlpha(0.0f);
			}
		});
	}
}
