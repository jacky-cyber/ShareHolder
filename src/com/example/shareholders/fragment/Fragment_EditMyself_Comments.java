package com.example.shareholders.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.activity.personal.MyProfileActivity;
import com.example.shareholders.activity.personal.OtherPeolpeInformationActivity;
import com.example.shareholders.activity.stock.StockCommentActivity;
import com.example.shareholders.activity.survey.TranspondActivity;
import com.example.shareholders.common.CircleImageView;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.LoadingDialog;
import com.example.shareholders.common.PullToRefreshView;
import com.example.shareholders.common.PullToRefreshView.OnFooterRefreshListener;
import com.example.shareholders.common.PullToRefreshView.OnHeaderRefreshListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ShareUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;


public class Fragment_EditMyself_Comments extends Fragment implements
		OnFooterRefreshListener, OnHeaderRefreshListener {

	/*
	 * private DisplayImageOptions defaultOptions = new
	 * DisplayImageOptions.Builder()
	 * .showImageForEmptyUri(R.drawable.ico_default_headview)
	 * .showImageOnLoading(R.drawable.ico_default_headview)
	 * .showImageOnFail(R.drawable.ico_default_headview)
	 * .cacheInMemory(true).cacheOnDisc(true).build();
	 */

	private BitmapUtils bitmapUtils = null;

	@ViewInject(R.id.iv_praise)
	private ImageView iv_praise;

	@ViewInject(R.id.lv_edit_myself_news)
	private ListView editNewsList;

	// 上下拉刷新
	@ViewInject(R.id.refresh_news)
	private PullToRefreshView pull_to_refresh;

	// popupwindow背后阴影
	@ViewInject(R.id.background)
	private RelativeLayout background;

	@ViewInject(R.id.rl)
	private RelativeLayout rl_parent;

	// 无自选股的界面提示
	@ViewInject(R.id.tv_wuzixuangu)
	private TextView tv_wuzixuangu;

	private ArrayList<HashMap<String, Object>> list;
	private HashMap<String, Object> first;
	private EditMyselfCommentsAdapter mAdapter;

	private ShareUtils popupWindow;

	private int pageIndex = 0;
	private int pageSize = 5;

	// 正在加载的旋转框
	private LoadingDialog loadingDialog;

	// 简单操作提示框
	private InternetDialog internetDialog;

	// 上拉刷新，增加数据
	private int FOOT = 1;
	// 下拉刷新，替换数据
	private int HEAD = 0;

	private String followed;
	private RequestQueue volleyRequestQueue;

	private AlertDialog reportDialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0: // 举报dialog消失
				if (reportDialog != null) {
					reportDialog.dismiss();
				}
				break;

			default:
				break;
			}
		};
	};

	private int scan_position = 0; // 记录浏览的哪一条数据

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_edit_myself_comments, null);
		ViewUtils.inject(this, v);
		bitmapUtils = new BitmapUtils(getActivity());
		bitmapUtils.configDefaultLoadingImage(R.drawable.ico_default_headview);
		bitmapUtils
				.configDefaultLoadFailedImage(R.drawable.ico_default_headview);
		loadingDialog = new LoadingDialog(getActivity());
		internetDialog = new InternetDialog(getActivity());
		init();
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 4:// 从StockComment传回数据

			try {
				Log.d("liang_scan_success_back", ""
						+ data.getExtras().getBoolean("scan_success"));
				Log.d("liang_comment_count_back",
						"" + data.getExtras().getInt("comment_count"));
				boolean scan_success = data.getExtras().getBoolean(
						"scan_success");
				int add_comment_count = data.getExtras().getInt(
						"add_comment_count");

				HashMap<String, Object> mData = list.get(scan_position);
				int comment_count = Integer.parseInt(mData.get("commentNum")
						.toString());
				mData.put("commentNum", comment_count + add_comment_count + "");

				if (scan_success) {// 增加阅读量成功
					int new_readNum = Integer.parseInt(mData.get("readNum")
							.toString()) + 1;
					mData.put("readNum", "" + new_readNum);
				}
				list.set(scan_position, mData);
				mAdapter.notifyDataSetChanged();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("liang_back_Exception_fragment", e.toString());
			}

			break;

		default:
			Log.d("dj_result", "null");
			break;
		}

		// super.onActivityResult(requestCode, resultCode, data);
	}

	// 把数据传到StockCommentActivity
	private void sendBundle(int position) {

		Bundle bundle = new Bundle();
		bundle.putString("creatorUuid", list.get(position).get("creatorUuid")
				.toString());
		bundle.putString("creatorName", list.get(position).get("creatorName")
				.toString());
		bundle.putString("creationTime", list.get(position).get("creationTime")
				.toString());
		bundle.putString("creatorLogoUrl",
				list.get(position).get("creatorLogoUrl").toString());
		bundle.putString("securitySymbol",
				list.get(position).get("securitySymbol").toString());
		bundle.putString("securityName", list.get(position).get("securityName")
				.toString());
		bundle.putString("securityType", list.get(position).get("securityType")
				.toString());
		bundle.putString("content", list.get(position).get("content")
				.toString());
		bundle.putString("likeNum", list.get(position).get("likeNum")
				.toString());
		bundle.putString("commentNum", list.get(position).get("commentNum")
				.toString());
		bundle.putString("readNum", list.get(position).get("readNum")
				.toString());
		bundle.putString("transpondNum", list.get(position).get("transpondNum")
				.toString());
		bundle.putString("topicUuid", list.get(position).get("topicUuid")
				.toString());
		bundle.putString("liked", list.get(position).get("liked").toString());
		bundle.putString("refTopic", list.get(position).get("refTopic")
				.toString());
		bundle.putString("followed", list.get(position).get("followed")
				.toString());
		try {
			bundle.putString("medias", list.get(position).get("mediaUrls")
					.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("liang", e.toString());
			e.printStackTrace();
		}

		Intent intent = new Intent(getActivity(), StockCommentActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);

	}

	// 初始化操作
	private void init() {
		loadingDialog.showLoadingDialog();
		Log.d("dj_comments", "showLoadingDialog()");
		volleyRequestQueue = Volley.newRequestQueue(getActivity());
		getComments(0, pageSize, HEAD);
		pull_to_refresh.setOnFooterRefreshListener(this);
		pull_to_refresh.setOnHeaderRefreshListener(this);
		list = new ArrayList<HashMap<String, Object>>();
		first = new HashMap<String, Object>();

		
		mAdapter = new EditMyselfCommentsAdapter(getActivity(), list);
		editNewsList.setAdapter(mAdapter);
		/*
		 * bitmapUtils.resume();
		editNewsList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				bitmapUtils.resume();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				bitmapUtils.pause();
			}
		});*/

		// 对每个item进行监听,并把数据传过去
		editNewsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 把数据传到StockCommentActivity
				scan_position = position;
				sendBundle(position);
			}
		});
		loadingDialog.dismissDialog();
	}

	// 获取评论
	private void getComments(int pageIndex, int pageSize, final int type) {

		String url = AppConfig.URL_TOPIC
				+ "list/myConcernedStock.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);
		url += "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;

		Log.d("dj_comments", url);
		StringRequest stringRequest = new StringRequest(Method.GET, url, null,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_comments_response", response.toString());

						try {
							JSONArray jsonArray = new JSONArray(response);
							final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
							HashMap<String, Object> data = null;
							Iterator<String> iterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, Object>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								datas.add(data);

							}

							// 替换或者增加数据
							if (type == FOOT)
								list.addAll(datas);
							else {
								list.clear();
								list.addAll(datas);
							}
							// 监听变化
							mAdapter.notifyDataSetChanged();
							Log.d("dj_comment.size()", list.size()+"");
							if (list.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							// TODO: handle exception
							Log.d("dj_JSONException_comments", e.toString());
							Log.d("dj_comment.size()", list.size()+"");
							if (list.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError e) {
						// TODO Auto-generated method stub

						try {
							JSONObject jsonObject = new JSONObject(e.data());
							Log.d("dj_VolleyError_comments",
									jsonObject.toString());
							Log.d("dj_comment.size()", list.size()+"");
							if (list.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							Log.d("dj_comment.size()", list.size()+"");
							if (list.size() == 0) {
								tv_wuzixuangu.setVisibility(View.VISIBLE);
							}
						}

					}
				});

		volleyRequestQueue.add(stringRequest);
	}

	// 构造适配器
	class EditMyselfCommentsAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public EditMyselfCommentsAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			this.context = context;
			this.list = list;
			mInflater = LayoutInflater.from(context);
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_comment_stock,
						parent, false);
			}

			TextView tv_securityName = AbViewHolder.get(convertView,
					R.id.tv_securityName);
			tv_securityName.setText(list.get(position).get("securityName")
					.toString());

			// 关注股票的代码
			TextView tv_securitySymbol = AbViewHolder.get(convertView,
					R.id.tv_symbol);
			tv_securitySymbol.setText(list.get(position).get("securitySymbol")
					.toString());

			// 评论者头像
			CircleImageView ci_face = AbViewHolder.get(convertView,
					R.id.ci_face);

			bitmapUtils.display(ci_face,
					list.get(position).get("creatorLogoUrl").toString());

			/*
			 * ImageAware imageAware = new ImageViewAware(ci_face, false);
			 * ImageLoader
			 * .getInstance().displayImage(list.get(position).get("creatorLogoUrl"
			 * ).toString(), imageAware, defaultOptions);
			 */
			/*
			 * ImageLoader.getInstance().displayImage(
			 * list.get(position).get("creatorLogoUrl").toString(), ci_face);
			 */

			/**
			 * 点击头像跳转到个人主页
			 */
			ci_face.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (list.get(position)
							.get("creatorUuid")
							.toString()
							.equals(RsSharedUtil.getString(getActivity(),
									AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), MyProfileActivity.class);
						startActivity(intent);

					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid",
								list.get(position).get("creatorUuid")
										.toString());
						bundle.putString("userName",
								list.get(position).get("creatorName")
										.toString());
						Intent intent = new Intent();
						intent.setClass(getActivity(),
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});

			// 评论者姓名
			TextView tv_name = AbViewHolder.get(convertView, R.id.tv_name);
			tv_name.setText(list.get(position).get("creatorName").toString());

			/**
			 * 点击姓名跳转到个人主页
			 */
			tv_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {

					if (list.get(position)
							.get("creatorUuid")
							.toString()
							.equals(RsSharedUtil.getString(getActivity(),
									AppConfig.UUID))) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), MyProfileActivity.class);
						startActivity(intent);

					} else {
						Bundle bundle = new Bundle();
						bundle.putString("uuid",
								list.get(position).get("creatorUuid")
										.toString());
						bundle.putString("userName",
								list.get(position).get("creatorName")
										.toString());
						Intent intent = new Intent();
						intent.setClass(getActivity(),
								OtherPeolpeInformationActivity.class);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});

			// 话题发出的时间__
			TextView tv_time = AbViewHolder.get(convertView, R.id.tv_time);
			long Time = Long.parseLong(list.get(position).get("creationTime")
					.toString());
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			String creationTime = dateFormat.format(new Date(Time));
			tv_time.setText(creationTime);

			// 内容
			TextView tv_comment_content = AbViewHolder.get(convertView,
					R.id.tv_comment_content);
			tv_comment_content.setText(list.get(position).get("content")
					.toString());

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
			tv_comment_num.setText(list.get(position).get("commentNum")
					.toString());
			// 浏览量
			TextView tv_scan_num = AbViewHolder.get(convertView,
					R.id.tv_scan_num);
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

					// Log.d("dj_praise", tv_praise_num.getText().toString());
					praiseItem(iv_praise, tv_praise_num, position, list);
					// iv_praise.setImageResource(R.drawable.btn_dianzan_selected_cl);
					// Toast.makeText(getActivity(), "2323",
					// Toast.LENGTH_SHORT).show();

				}
			});

			// 设置分享监听
			RelativeLayout rl_share = AbViewHolder.get(convertView,
					R.id.rl_share);
			
			final String contentString=list.get(position).get("creatorName")
			.toString()+":\n"+list.get(position).get("content")
			.toString();
			rl_share.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.d("dj_share", "click");
					showShare(contentString);
				}
			});

			// 设置更多监听
			ImageView iv_more = AbViewHolder.get(convertView, R.id.iv_more);
			iv_more.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					initMenu(getActivity(), R.layout.item_comment_more_popup,
							position);
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
					iv_creator_face.setVisibility(View.GONE);
					// 转发的名字
					tv_creator_name.setText(refTopic.get("creatorName")
							.toString());
					// 转发的内容
					tv_creator_content.setText(refTopic.get("content")
							.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("dj_error_refTopic", e.toString());
				}
			}

			return convertView;
		}
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

		// Toast.makeText(getActivity(), "2323", Toast.LENGTH_SHORT).show();
		iv_praise.setClickable(false);

		String uuid = topics.get(position).get("topicUuid").toString();
		String url = AppConfig.URL_TOPIC + "like.json?topicUuid=" + uuid;
		url = url + "&access_token="
				+ RsSharedUtil.getString(getActivity(), AppConfig.ACCESS_TOKEN);

		StringRequest stringRequest = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {

						int praiseNum = Integer.parseInt(tv_praise_num
								.getText().toString());
						praiseNum++;
						tv_praise_num.setText("" + praiseNum);
						iv_praise
								.setImageResource(R.drawable.btn_dianzan_selected_cl);

						topics.get(position).put("liked", true);
						iv_praise.setClickable(true);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// int praiseNum = Integer.parseInt(tv_praise_num
						// .getText().toString());
						// praiseNum--;
						// tv_praise_num.setText("" + praiseNum);
						// if (topics.get(position).get("liked").toString()
						// .equals("true")) {
						// iv_praise
						// .setImageResource(R.drawable.btn_dianzan_selected_cl);
						// } else {
						// iv_praise
						// .setImageResource(R.drawable.btn_dianzan_normal_cl);
						// }
						iv_praise.setClickable(true);

						try {
							// Log.d("error.statuCode()", error.statuCode() +
							// "");
							JSONObject jsonObject = new JSONObject(error.data());
							// ToastUtils.showToast(getActivity(),
							// jsonObject.getString("description"));

							showInternetDialog(jsonObject
									.getString("description"));

						} catch (Exception e) {
							Log.d("liang_dianzan_Exception", e.toString());
						}
					}
				});
		volleyRequestQueue.add(stringRequest);
	}

	// 下拉刷新替换数据
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pageIndex = 0;
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getComments(pageIndex, pageSize, HEAD);
				pull_to_refresh.onHeaderRefreshComplete();
			}
		}, 2000);

	}

	// 上拉刷新增加数据
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		pull_to_refresh.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getComments(++pageIndex, pageSize, FOOT);
				pull_to_refresh.onFooterRefreshComplete();
			}
		}, 2000);
	}

	/**
	 * 弹出复制举报的对话框
	 */
	private void showAlertDialog(final int position) {
		final AlertDialog mAlertDialog = new AlertDialog.Builder(getActivity())
				.create();
		mAlertDialog.show();
		mAlertDialog.setCancelable(true);
		mAlertDialog.getWindow().setContentView(R.layout.popup_copy_report);
		mAlertDialog.getWindow().setGravity(Gravity.BOTTOM);

		TextView tv_copy = (TextView) mAlertDialog.findViewById(R.id.tv_copy);
		TextView tv_report = (TextView) mAlertDialog
				.findViewById(R.id.tv_report);
		TextView tv_cancel = (TextView) mAlertDialog
				.findViewById(R.id.tv_cancel);

		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String content = list.get(position).get("content").toString();

				ClipboardManager copy = (ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
				mAlertDialog.dismiss();
			}
		});

		tv_report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAlertDialog.dismiss();

				showReportDialog();

			}

		});

		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAlertDialog.dismiss();
			}
		});

		// 设置蒙版的黑暗度
		WindowManager.LayoutParams lp = mAlertDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.4f;

		// 令alertDialog的宽度match_parent,否则左右两边会留有空隙
		lp.width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth();
		lp.height = LayoutParams.WRAP_CONTENT;

		mAlertDialog.getWindow().setAttributes(lp);
		mAlertDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		// 设置动画效果
		mAlertDialog.getWindow().setWindowAnimations(
				R.style.mypopwindow_anim_style);
	}

	/**
	 * 弹出举报对话框
	 */
	private void showReportDialog() {
		reportDialog = new AlertDialog.Builder(getActivity()).create();
		reportDialog.show();
		reportDialog.getWindow().setContentView(R.layout.item_toast_popup);
		reportDialog.setCancelable(false);

		ImageView iv = (ImageView) reportDialog.getWindow().findViewById(
				R.id.iv_item);
		TextView tv = (TextView) reportDialog.getWindow().findViewById(
				R.id.tv_item);

		iv.setImageResource(R.drawable.ico_gantanhao);
		tv.setText(getResources().getString(R.string.already_report));

		/**
		 * 设置黑暗度
		 */
		// 设置蒙版的黑暗度
		WindowManager.LayoutParams lp = reportDialog.getWindow()
				.getAttributes();
		lp.dimAmount = 0.0f;

		reportDialog.getWindow().setAttributes(lp);
		reportDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		Message msg = new Message();
		msg.what = 0;
		handler.sendMessageDelayed(msg, 3000);

	}

	/**
	 * 弹出提示菜单栏
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
		case 4:
			tv_item.setText("取消收藏");
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
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
	}

	/**
	 * 弹出“更多”菜单栏
	 * 
	 * @param context
	 * @param view
	 * @param viewGroup
	 * @return
	 */
	public void initMenu(final Context context, int view, final int position) {
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
		background.setAlpha(0.5f);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated
				// method stub
				background.setAlpha(0.0f);
			}
		});
		TextView tv_transmit = (TextView) contentView
				.findViewById(R.id.tv_transmit);
		TextView tv_copy = (TextView) contentView.findViewById(R.id.tv_copy);
		final TextView tv_collect = (TextView) contentView
				.findViewById(R.id.tv_collect);
		TextView tv_report = (TextView) contentView
				.findViewById(R.id.tv_report);
		TextView tv_cancel = (TextView) contentView
				.findViewById(R.id.tv_cancel);
		if (list.get(position).get("creatorUuid").equals(RsSharedUtil.getString(context, AppConfig.UUID))) {
			tv_collect.setVisibility(View.GONE);
			tv_report.setVisibility(View.GONE);
		}
		// 转发
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

				Intent intent = new Intent(getActivity(),
						TranspondActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);

				// Toast(context, R.layout.item_toast_popup, rl_parent, 0);

				popupWindow.dismiss();
			}
		});
		// 复制
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String content = list.get(position).get("content").toString();
				ClipboardManager copy = (ClipboardManager) getActivity()
						.getSystemService(Context.CLIPBOARD_SERVICE);
				copy.setText(content);
				popupWindow.dismiss();
				internetDialog.showInternetDialog("已复制", true);
			}
		});

		followed = list.get(position).get("followed").toString();
		if (followed.equals("true")) {
			tv_collect.setText("取消收藏");
		}
		if (followed.equals("false"))
			tv_collect.setText("收藏");
		// 点击收藏
		tv_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String url = AppConfig.URL_USER + "topic.json?access_token=";
				url = url
						+ RsSharedUtil.getString(getActivity(), "access_token")
						+ "&uuid="
						+ list.get(position).get("topicUuid").toString();
				StringRequest stringRequest = new StringRequest(url, null,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {
								Log.d("dj_collect", response.toString());
								// TODO Auto-generated method stub
								if (response.equals("true")) {
									HashMap<String, Object> data = list
											.get(position);
									data.put("followed", "true");
									list.set(position, data);
									Toast(getActivity(),
											R.layout.item_toast_popup,
											rl_parent, 2);
									followed = "true";
									tv_collect.setText("取消收藏");
									Log.d("dj_collect", tv_collect.getText()
											.toString());
								}
								if (response.equals("false")) {
									HashMap<String, Object> data = list
											.get(position);
									data.put("followed", "false");
									list.set(position, data);
									Toast(getActivity(),
											R.layout.item_toast_popup,
											rl_parent, 4);
									followed = "false";
									tv_collect.setText("收藏");
									Log.d("dj_collect", tv_collect.getText()
											.toString());
								}
							}

						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								Log.d("RRRRRRRRRRRRRRRRRRRRR", error.toString());
							}
						});
				volleyRequestQueue.add(stringRequest);
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

	// 分享的方法
	private void showShare(String content) {
		background.setAlpha(0.7f);
		popupWindow = new ShareUtils(getActivity(), rl_parent,content);

		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				background.setAlpha(0.0f);

			}
		});
	}

	/**
	 * 信息提示
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (internertDialog != null && internertDialog.isShowing()) {
					internertDialog.dismiss();
				}
				break;

			// case 2: // 5秒后加载对话框未消失，令对话框消失并提示网络不给力
			//
			// if (internertDialog != null && internertDialog.isShowing()) {
			// internertDialog.dismiss();
			// showInternetDialog();
			// }
			//
			// break;
			//
			// case 3: // 提示网络异常的对话框消失
			// if (internertDialog != null && internertDialog.isShowing()) {
			// internertDialog.dismiss();
			// }
			// break;
			default:
				break;
			}
		};
	};

	private AlertDialog internertDialog = null;

	private void showInternetDialog(String msg) {
		internertDialog = new AlertDialog.Builder(getActivity()).create();
		internertDialog.show();
		internertDialog.setCancelable(false);

		Window window = internertDialog.getWindow();
		window.setContentView(R.layout.dialog_dianzan);

		ProgressBar progress_bar = (ProgressBar) window
				.findViewById(R.id.progress_bar);
		ImageView iv_tips = (ImageView) window.findViewById(R.id.iv_tips);
		TextView tv_message = (TextView) window.findViewById(R.id.tv_message);

		progress_bar.setVisibility(View.GONE);
		iv_tips.setVisibility(View.VISIBLE);
		tv_message.setText(msg);

		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.0f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessageDelayed(msg, 3000);
			}
		}).start();

	}

}
