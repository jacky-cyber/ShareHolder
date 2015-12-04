package com.example.shareholders.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.activity.stock.MyStockDetailsActivity;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyHScrollView;
import com.example.shareholders.common.MyHScrollView.OnScrollChangedListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.Log;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

//行情Fragment
public class Fragment_OtherPeople_Price_Situation extends Fragment {

	JSONArray postarray;// 从1.5.1中获得的关注股票代码
	ArrayList<HashMap<String, String>> listhead;// 股票名称和代码
	ListView mListView1;
	MyAdapter myAdapter;
	RelativeLayout mHead;
	LinearLayout main;
	private String userUuid = "";

	View mview;

	String aget = new String();

	private ArrayList<HashMap<String, String>> share_hashMaps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mview = inflater
				.inflate(R.layout.fragment_share_list, container, false);
		ViewUtils.inject(this, mview);
		mHead = (RelativeLayout) mview
				.findViewById(R.id.fragment_share_list_head);
		mHead.setFocusable(true);
		mHead.setClickable(true);
		// mHead.setBackgroundColor(Color.parseColor("#b2d235"));
		mHead.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		mListView1 = (ListView) mview.findViewById(R.id.fsl_share_list);
		mListView1.setOnTouchListener(new ListViewAndHeadViewTouchLinstener());
		Intent intent = getActivity().getIntent();// 传过来usename和useuuid
		Bundle bundle = intent.getExtras();
		userUuid = bundle.getString("userUuid");
		initpost();
		return mview;
	}

	private void initpost() {
		// 3.1
		Log.d("uuid3.1", userUuid);
		String url = AppConfig.URL_USER + "listed/list.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		url += "&userUuid=" + userUuid + "&pageSize=10&pageIndex=0";
		Log.d("otherpeoplesituation", url);
		StringRequest stringRequest1 = new StringRequest(url, null,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (response.equals("") || response.equals("[0]")) {
							Log.d("lele_no_content", "No Content");
						} else {
							Log.d("lele_zixun", response.toString());
							try {

								listhead = new ArrayList<HashMap<String, String>>();
								JSONObject object = new JSONObject(response
										.toString());
								JSONArray all = object
										.getJSONArray("listFollowQuoteds");
								postarray = new JSONArray();

								HashMap<String, String> headdata = null;
								Log.d("all", all.toString());

								JSONObject post;
								Iterator<String> iterator = null;
								for (int i = 0; i < all.length(); i++) {
									headdata = new HashMap<String, String>();
									post = new JSONObject();

									iterator = all.getJSONObject(i).keys();
									while (iterator.hasNext()) {
										String key = iterator.next();
										Log.d("key", key);

										headdata.put(key, all.getJSONObject(i)
												.get(key).toString());
									}
									post.put("symbol", all.getJSONObject(i)
											.get("symbol").toString());
									post.put(
											"securityType",
											all.getJSONObject(i)
													.get("securityType")
													.toString());
									listhead.add(headdata);
									postarray.put(post);
								}
								Log.d("listhead", listhead.toString());
								Log.d("postarray", postarray.toString());
								initsituation();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Log.d("3.1false", e.toString());
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("lele_error", error.toString());
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error", jsonObject.get("description")
									.toString());

						} catch (Exception e) {
							Log.d("lele_error", "未知错误");
						}

					}
				});
		stringRequest1.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest1);
	}

	private void initsituation() {
		// 1.5.2
		Log.d("listhead", listhead.toString());
		Log.d("postarray", postarray.toString());

		String url = AppConfig.VERSION_URL
				+ "quotation/newestPrice.json?access_token=";
		url += RsSharedUtil.getString(getActivity(), "access_token");
		// 此处不知股票类型应怎么选
		Log.d("1.5.2url", url);
		Log.d("lololol", postarray.toString());
		StringRequest stringRequest2 = new StringRequest(postarray, url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("lelele_response", response.toString());
						if (response.equals("") || response.equals("[0]")) {
							Log.d("lele_no_content", "No Content");
						} else {
							Log.d("lele_zixun", response.toString());
							try {
								JSONArray all = new JSONArray(response);

								Log.d("all", all.toString());
								ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> data = null;
								Iterator<String> iterator = null;
								for (int i = 0; i < all.length(); i++) {

									data = new HashMap<String, String>();

									iterator = all.getJSONObject(i).keys();

									while (iterator.hasNext()) {
										String key = iterator.next();
										Log.d("key", key);
										data.put(key,
												all.getJSONObject(i).get(key)
														.toString());
									}
									datas.add(data);

								}

								myAdapter = new MyAdapter(
										getActivity(),
										listhead,
										datas,
										R.layout.item_fragment_share_list_content);
								mListView1.setAdapter(myAdapter);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Log.d("false1.5.2", e.toString());
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("lele_error", error.toString());
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("lele_error", jsonObject.get("description")
									.toString());
						} catch (Exception e) {
							Log.d("lele_error", "未知错误");
						}

					}
				});
		stringRequest2.setTag("stringRequest");
		MyApplication.getRequestQueue().add(stringRequest2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("stringRequest");
		super.onDestroy();
	}

	// 点击事件已经可以正常设置
	@OnClick({ R.id.share_new, R.id.share_updown, R.id.share_amountofincrease,
			R.id.share_tradingvolume, R.id.share_volumeoftransaction,
			R.id.share_max, R.id.share_min, R.id.share_swing,
			R.id.share_turnoverrate, R.id.share_PEratio, R.id.share_totalvalue })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_new:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_updown:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_amountofincrease:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_tradingvolume:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_volumeoftransaction:
			Log.d("asdsad", "asdasdsadasdasd");
			break;

		case R.id.share_max:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_min:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_swing:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_turnoverrate:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_PEratio:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		case R.id.share_totalvalue:
			Log.d("asdsad", "asdasdsadasdasd");
			break;
		default:
			break;
		}

	}

	class ListViewAndHeadViewTouchLinstener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			// 当在列头 和 listView控件上touch时，将这个touch的事件分发给 ScrollView
			HorizontalScrollView headSrcrollView = (HorizontalScrollView) mHead
					.findViewById(R.id.share_horizontalScrollView1);
			headSrcrollView.onTouchEvent(arg1);
			return false;
		}
	}

	public class MyAdapter extends BaseAdapter {
		// listhead短时间内固定，list不断刷新
		private ArrayList<HashMap<String, String>> listhead;
		private ArrayList<HashMap<String, String>> list;
		public List<ViewHolder> mHolderList = new ArrayList<ViewHolder>();
		int id_row_layout;
		LayoutInflater mInflater;

		public MyAdapter(Context context,
				ArrayList<HashMap<String, String>> listhead,
				ArrayList<HashMap<String, String>> list, int id_row_layout) {
			super();
			this.listhead = listhead;
			this.list = list;
			this.id_row_layout = id_row_layout;
			mInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listhead.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parentView) {
			ViewHolder holder = null;
			if (convertView == null) {
				synchronized (Fragment_OtherPeople_Price_Situation.this) {
					convertView = mInflater.inflate(id_row_layout, null);
					holder = new ViewHolder();

					MyHScrollView scrollView1 = (MyHScrollView) convertView
							.findViewById(R.id.share_horizontalScrollView1);

					holder.scrollView = scrollView1;
					// cj TextView tv = AbViewHolder.get(convertView,
					// R.id.item_share_name);

					holder.iv_ico = (ImageView) convertView
							.findViewById(R.id.share_iv_ico);
					holder.txt_name = (TextView) convertView
							.findViewById(R.id.item_share_name);
					holder.txt_num = (TextView) convertView
							.findViewById(R.id.item_share_num);
					holder.txt2 = (TextView) convertView
							.findViewById(R.id.item_share_2);
					holder.txt3 = (TextView) convertView
							.findViewById(R.id.item_share_3);
					holder.txt4 = (TextView) convertView
							.findViewById(R.id.item_share_4);
					holder.txt5 = (TextView) convertView
							.findViewById(R.id.item_share_5);
					holder.txt6 = (TextView) convertView
							.findViewById(R.id.item_share_6);
					/*
					 * holder.txt7 = (TextView) convertView
					 * .findViewById(R.id.item_share_7);
					 */
					holder.txt8 = (TextView) convertView
							.findViewById(R.id.item_share_8);
					holder.txt9 = (TextView) convertView
							.findViewById(R.id.item_share_9);
					holder.txt10 = (TextView) convertView
							.findViewById(R.id.item_share_10);
					holder.txt11 = (TextView) convertView
							.findViewById(R.id.item_share_11);
					holder.txt12 = (TextView) convertView
							.findViewById(R.id.item_share_12);
					holder.txt13 = (TextView) convertView
							.findViewById(R.id.item_share_13);

					MyHScrollView headSrcrollView = (MyHScrollView) mHead
							.findViewById(R.id.share_horizontalScrollView1);
					headSrcrollView
							.AddOnScrollChangedListener(new OnScrollChangedListenerImp(
									scrollView1));

					convertView.setTag(holder);
					mHolderList.add(holder);
				}
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			DecimalFormat df = new DecimalFormat("#0.00");// 固定格式

			double a;

			holder.txt_name.setText(listhead.get(position).get("shortname"));
			holder.txt_num.setText(listhead.get(position).get("symbol"));
			try {
				aget = list.get(position).get("nowPrice");
				if (aget.equals("null")) {
					holder.txt2.setText("---");
				} else {
					a = Double.parseDouble(aget);
					holder.txt2.setText(df.format(a));
				}

				// 涨跌
				if ((list.get(position).get("nowPrice").equals("null"))
						|| list.get(position).get("latestClosePrice")
								.equals("null")) {
					holder.txt3.setText("---");
				} else {
					a = Double.parseDouble(list.get(position).get("nowPrice"))
							- Double.parseDouble(list.get(position).get(
									"latestClosePrice"));
					holder.txt3.setText(df.format(a));
				}

				// 涨幅
				if ((list.get(position).get("nowPrice").equals("null"))
						|| list.get(position).get("latestClosePrice")
								.equals("null")) {
					holder.txt4.setText("---");
					// holder.txt4.setTextColor(getResources().getColor(R.color.text_333333));
					holder.txt4.setBackgroundColor(getResources().getColor(
							R.color.white));
				} else {
					a = (Double.parseDouble(list.get(position).get("nowPrice")) - Double
							.parseDouble(list.get(position).get(
									"latestClosePrice")))
							/ Double.parseDouble(list.get(position).get(
									"latestClosePrice"));
					if (a < 0) {
						holder.txt4.setBackgroundColor(getResources().getColor(
								R.color.share_53a00a));
					} else {
						holder.txt4.setBackgroundColor(getResources().getColor(
								R.color.share_f73131));
					}
					holder.txt4.setTextColor(getResources().getColor(
							R.color.white));
					holder.txt4.setText(df.format(a));
				}

				aget = list.get(position).get("volume");
				if (aget.equals("null")) {
					holder.txt5.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 1000000) {
						a = a / 1000000;
						holder.txt5.setText(df.format(a) + "万手");
					} else {
						a = a / 100;
						holder.txt5.setText(df.format(a) + "手");
					}
				}

				aget = list.get(position).get("amount");
				if (aget.equals("null")) {
					holder.txt6.setText("---");
				} else {
					a = Double.parseDouble(aget);
					a = a / 100000000;
					holder.txt6.setText(df.format(a) + "亿");

				}

				// holder.txt7.setText(list.get(position).get("symbol"));
				aget = list.get(position).get("highPrice");
				if (aget.equals("null")) {
					holder.txt8.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 10000) {
						a = a / 10000;
						holder.txt8.setText(df.format(a) + "万");
					} else {
						holder.txt8.setText(df.format(a));
					}
				}

				aget = list.get(position).get("lowPrice");
				if (aget.equals("null")) {
					holder.txt9.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 10000) {
						a = a / 10000;
						holder.txt9.setText(df.format(a) + "万");
					} else {
						holder.txt9.setText(df.format(a));
					}
				}

				// 振幅
				if ((list.get(position).get("nowPrice").equals("null"))
						|| list.get(position).get("latestClosePrice")
								.equals("null")) {
					holder.txt10.setText("---");
					holder.txt10.setTextColor(getResources().getColor(
							R.color.text_333333));
					holder.iv_ico.setVisibility(View.GONE);
				} else {
					a = (Double
							.parseDouble(list.get(position).get("highPrice")) - Double
							.parseDouble(list.get(position).get("lowPrice")))
							/ Double.parseDouble(list.get(position).get(
									"latestClosePrice"));
					if (a < 0) {
						holder.txt10.setTextColor(getResources().getColor(
								R.color.share_53a00a));
						holder.iv_ico.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.ico_share_die));
					} else {
						holder.txt10.setTextColor(getResources().getColor(
								R.color.share_f73131));
						holder.iv_ico.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.ico_share_zhang));
					}

					if (Math.abs(a) >= 1000) {
						a = a / 10000;
						holder.txt10.setText(df.format(a) + "万");
					} else {
						holder.txt10.setText(df.format(a));
					}
				}

				aget = list.get(position).get("turnOverRate");
				if (aget.equals("null")) {
					holder.txt11.setText("---");
				} else {
					a = Double.parseDouble(aget);
					holder.txt11.setText(df.format(a));
				}

				aget = list.get(position).get("epg");
				if (aget.equals("null")) {
					holder.txt12.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 100000000) {
						a = a / 100000000;
						holder.txt12.setText(df.format(a) + "亿");
					} else if (a >= 10000) {
						a = a / 10000;
						holder.txt12.setText(df.format(a) + "万");
					} else {
						holder.txt12.setText(df.format(a));
					}
				}

				aget = list.get(position).get("totalMarketValue");
				if (aget.equals("null")) {
					holder.txt13.setText("---");
				} else {
					a = Double.parseDouble(aget);
					if (a >= 100000000) {
						a = a / 100000000;
						holder.txt13.setText(new DecimalFormat("#0").format(a)
								+ "亿");
					} else if (a >= 10000) {
						a = a / 10000;
						holder.txt13.setText(df.format(a) + "万");
					} else {
						holder.txt13.setText(df.format(a));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("fffff", "asdsadsad");
				e.printStackTrace();
			}
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ArrayList<HashMap<String, String>> stocks = new ArrayList<HashMap<String,String>>();
					HashMap<String, String> map = new HashMap<String, String>();
					Log.d("symbol", list.toString());
					map.put("symbol", list.get(position).get("symbol").toString());
					map.put("shortname", listhead.get(position).get("shortname"));
					map.put("securityType", list.get(position).get("securityType").toString());
					stocks.add(map);
					Intent intent = new Intent(getActivity(), MyStockDetailsActivity.class);
					intent.putExtra("stocks", stocks);
					intent.putExtra("position", 0);
					startActivity(intent);
				}
			});
			return convertView;
		}

		class OnScrollChangedListenerImp implements OnScrollChangedListener {
			MyHScrollView mScrollViewArg;

			public OnScrollChangedListenerImp(MyHScrollView scrollViewar) {
				mScrollViewArg = scrollViewar;
			}

			@Override
			public void onScrollChanged(int l, int t, int oldl, int oldt) {
				mScrollViewArg.smoothScrollTo(l, t);
			}
		};

		class ViewHolder {
			ImageView iv_ico;
			TextView txt_name;
			TextView txt_num;
			TextView txt2;
			TextView txt3;
			TextView txt4;
			TextView txt5;
			TextView txt6, txt7, txt8, txt9, txt10, txt11, txt12, txt13;
			HorizontalScrollView scrollView;
		}
	}// end class my

}
