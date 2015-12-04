package com.example.shareholders.activity.fund;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shareholders.R;
import com.example.shareholders.common.ClearEditText;
import com.example.shareholders.common.KeyboardListenRelativeLayout;
import com.example.shareholders.common.KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener;
import com.example.shareholders.common.MyToast;
import com.example.shareholders.common.SideBarFoudSearch;
import com.example.shareholders.common.SideBarFoudSearch.OnTouchingLetterChangedListener;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.util.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_fund_search)
public class FundSearchActivity extends Activity implements OnItemClickListener {

	@ViewInject(R.id.fs_layout)
	private KeyboardListenRelativeLayout fs_layout;
	// 带过滤数据作用搜索框
	@ViewInject(R.id.et_fs_filter_edit)
	private ClearEditText et_fs_filter_edit;
	@ViewInject(R.id.tv_fs_dialog1)
	private TextView tv_fs_dialog1;
	// 按字母搜索的sidebar
	@ViewInject(R.id.sb_fs_sidebar)
	private SideBarFoudSearch sideBar;
	// 基金列表
	@ViewInject(R.id.lv_foud_search_list1)
	private ListView lv_foud_search_list1;

	private ArrayList<String> lv_foud_search_name;
	private ArrayList<String> lv_foud_search_code;
	private ArrayList<String> lv_foud_search_short_name;
	private ArrayList<String> lv_foud_search_type;
	private ArrayList<String> lv_foud_search_selected;
	private lvFoudSearchListAdapter lv_foud_search_adapter;
	private ArrayList<HashMap<String, Object>> lv_foud_search_hashMaps;
	HttpUtils http;
	// 初始化
	RequestQueue volleyRequestQueue;
	// 请求的字符
	String requestString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		volleyRequestQueue = Volley.newRequestQueue(this);
		lv_foud_search_hashMaps = new ArrayList<HashMap<String, Object>>();
		initViews();
		http = new HttpUtils();
		// showSearchFund();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, FundDetailsActivity.class);
		intent.putExtra("fund_type", ""
				+ lv_foud_search_hashMaps.get(position).get("category")
						.toString());
		intent.putExtra("symbol", ""
				+ lv_foud_search_hashMaps.get(position).get("symbol")
						.toString());
		startActivity(intent);
	}

	@OnClick({ R.id.title_fs_note })
	private void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_fs_note:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 字母栏
	 */
	private void initViews() {
		fs_layout
				.setOnKeyboardStateChangedListener(new IOnKeyboardStateChangedListener() {

					@Override
					public void onKeyboardStateChanged(int state) {
						switch (state) {
						case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE:
							sideBar.setVisibility(View.VISIBLE);
							break;
						case KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW:
							sideBar.setVisibility(View.INVISIBLE);
						default:
							break;
						}

					}
				});

		lv_foud_search_list1.setOnItemClickListener(this);
		// 实例化汉字转拼音类
		// characterParser = CharacterParser.getInstance();
		//
		// pinyinComparator = new PinyinComparator();

		sideBar.setTextView(tv_fs_dialog1);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				try {
					int position = lv_foud_search_adapter
							.getPositionForSection(s.charAt(0));
					if (position != -1) {
						lv_foud_search_list1.setSelection(position);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// 根据输入框输入值的改变来过滤搜索
		et_fs_filter_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				// filterData(s.toString());
				requestString = s.toString();
				if (s.toString().trim().equals("")) {
					try {
						lv_foud_search_hashMaps.clear();
						lv_foud_search_adapter.clear();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					getSearchFund(s.toString().trim(), 0, 10);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	// private void filterData(String filterStr) {
	// ArrayList<HashMap<String, Object>> filterDateList = new
	// ArrayList<HashMap<String, Object>>();
	//
	// if (TextUtils.isEmpty(filterStr)) {
	// filterDateList = lv_foud_search_hashMaps;
	// } else {
	// filterDateList.clear();
	// for (HashMap<String, Object> sortModel : lv_foud_search_hashMaps) {
	// String name = sortModel.get("name").toString();
	// String short_name = sortModel.get("pyName").toString();
	// String code = sortModel.get("symbol").toString();
	// if (name.indexOf(filterStr.toString()) != -1
	// || short_name.indexOf(filterStr.toString()
	// .toUpperCase()) != -1
	// || code.indexOf(filterStr.toString()) != -1
	// || characterParser.getSelling(name).startsWith(
	// filterStr.toString())) {
	// filterDateList.add(sortModel);
	// }
	// }
	// }
	//
	// // 根据a-z进行排序源数据
	// Collections.sort(lv_foud_search_hashMaps,
	// new Comparator<HashMap<String, Object>>() {
	//
	// @Override
	// public int compare(HashMap<String, Object> arg0,
	// HashMap<String, Object> arg1) {
	// // TODO Auto-generated method stub
	// if (arg0.get("pyName").toString().charAt(0) > 'Z'
	// || arg0.get("pyName").toString().charAt(0) < 'A') {
	// return 1;
	// } else if (arg1.get("pyName").toString().charAt(0) > 'Z'
	// || arg1.get("pyName").toString().charAt(0) < 'A') {
	// return -1;
	// } else
	// return arg0.get("pyName").toString()
	// .compareTo(arg1.get("pyName").toString());
	// }
	// });
	// lv_foud_search_adapter.updateListView(filterDateList);
	// }

	public void showSearchFund() {
		for (int i = 0; i < lv_foud_search_name.size(); i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("name", lv_foud_search_name.get(i));
			item.put("symbol", lv_foud_search_code.get(i));
			item.put("pyName", lv_foud_search_short_name.get(i));
			item.put("category", lv_foud_search_type.get(i));
			item.put("followed", lv_foud_search_selected.get(i));

			lv_foud_search_hashMaps.add(item);
		}
		lv_foud_search_adapter = new lvFoudSearchListAdapter(getApplication(),
				lv_foud_search_hashMaps);
		lv_foud_search_list1.setAdapter(lv_foud_search_adapter);

	}

	private String[] state;

	public class lvFoudSearchListAdapter extends BaseAdapter implements
			SectionIndexer {
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public lvFoudSearchListAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			state = new String[list.size()];
			for (int position = 0; position < list.size(); position++)
				state[position] = list.get(position).get("followed").toString();
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
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.item_activity_foud_search_list, null);

			}
			TextView item_foud_search_name = AbViewHolder.get(view,
					R.id.item_foud_search_name);
			item_foud_search_name.setText((String) list.get(position).get(
					"name"));
			TextView item_foud_search_code = AbViewHolder.get(view,
					R.id.item_foud_search_code);
			item_foud_search_code.setText((String) list.get(position).get(
					"symbol"));
			TextView item_foud_search_short_name = AbViewHolder.get(view,
					R.id.item_foud_search_short_name);
			item_foud_search_short_name.setText((String) list.get(position)
					.get("pyName"));
			TextView item_foud_search_type = AbViewHolder.get(view,
					R.id.item_foud_search_type);
			item_foud_search_type.setText((String) list.get(position).get(
					"category"));

			final ImageView item_foud_search_selected = AbViewHolder.get(view,
					R.id.item_foud_search_selected);

			if (state[position].equals("true")) {
				item_foud_search_selected
						.setImageResource(R.drawable.btn_fs_selected);
			} else {
				item_foud_search_selected
						.setImageResource(R.drawable.btn_fs_unselected);
			}

			item_foud_search_selected.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Log.d("state", state[position]);
					if (state[position].trim().equals("true")) {
						item_foud_search_selected
								.setImageResource(R.drawable.btn_fs_unselected);
						state[position] = "false";
						concern("false", list.get(position).get("symbol")
								.toString(), item_foud_search_selected, state,
								position);

					} else {
						item_foud_search_selected
								.setImageResource(R.drawable.btn_fs_selected);
						state[position] = "true";
						concern("true", list.get(position).get("symbol")
								.toString(), item_foud_search_selected, state,
								position);

					}

				}
			});

			return view;
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		@Override
		public int getPositionForSection(int section) {
			// TODO Auto-generated method stub
			for (int i = 0; i < getCount(); i++) {
				String sortStr = (String) list.get(i).get("pyName");
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;

		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return ((String) (list.get(position).get("pyName"))).charAt(0);
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

		public void updateListView(ArrayList<HashMap<String, Object>> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		public void clear() {
			list.clear();
			notifyDataSetChanged();
		}

	}

	/**
	 * 获取搜索的基金
	 * 
	 * @param keyword
	 * @param index
	 * @param pageSize
	 */
	private void getSearchFund(final String keyword, int pageIndex, int pageSize) {
		String url = AppConfig.URL_FUND + "search.json?access_token="
				+ RsSharedUtil.getString(this, "access_token") + "&keyword="
				+ keyword + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;
		// String url= AppConfig.URL
		// +
		// "api/v1.0/fund/search.json?access_token=d7abc947-6df0-4073-bc74-ba4b24c478b2&keyword=易方达&pageIndex=0&pageSize=5";
		Log.d("url", "url:" + url);

		http.send(HttpRequest.HttpMethod.GET, url, null,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// Toast.makeText(getApplicationContext(),
						// "网络不给力", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							String response = arg0.result.toString();
							Log.d("获取数据", "keyword:" + keyword + response);
							// 如果没有数据
							if (response.equals("") || response.equals("[0]")) {
								try {
									lv_foud_search_hashMaps.clear();
									lv_foud_search_adapter.clear();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								try {

									JSONArray all = new JSONArray(response);
									final ArrayList<HashMap<String, Object>> datas = new ArrayList<HashMap<String, Object>>();
									for (int i = 0; i < all.length(); i++) {
										HashMap<String, Object> data = new HashMap<String, Object>();
										Iterator<String> jsIterator;
										try {
											jsIterator = all.getJSONObject(i)
													.keys();
											while (jsIterator.hasNext()) {
												String key = jsIterator.next();
												data.put(key,
														all.getJSONObject(i)
																.get(key)
																.toString());
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										datas.add(data);

									}
									lv_foud_search_hashMaps.clear();
									lv_foud_search_hashMaps = datas;

									// 根据a-z进行排序源数据
									Collections
											.sort(lv_foud_search_hashMaps,
													new Comparator<HashMap<String, Object>>() {

														@Override
														public int compare(
																HashMap<String, Object> arg0,
																HashMap<String, Object> arg1) {
															// TODO
															// Auto-generated
															// method stub
															if (arg0.get(
																	"pyName")
																	.toString()
																	.charAt(0) > 'Z'
																	|| arg0.get(
																			"pyName")
																			.toString()
																			.charAt(0) < 'A') {
																return 1;
															} else if (arg1
																	.get("pyName")
																	.toString()
																	.charAt(0) > 'Z'
																	|| arg1.get(
																			"pyName")
																			.toString()
																			.charAt(0) < 'A') {
																return -1;
															} else
																return arg0
																		.get("pyName")
																		.toString()
																		.compareTo(
																				arg1.get(
																						"pyName")
																						.toString());
														}
													});

									lv_foud_search_adapter = new lvFoudSearchListAdapter(
											getApplicationContext(),
											lv_foud_search_hashMaps);
									lv_foud_search_list1
											.setAdapter(lv_foud_search_adapter);
									lv_foud_search_adapter
											.notifyDataSetChanged();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 关注/取消关注基金
	 * 
	 * @param follow
	 * @param symbol
	 * @param iv
	 */
	private void concern(final String follow, String symbol,
			final ImageView iv, final String[] state, final int position) {
		String url = AppConfig.URL_USER
				+ "security.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(),
						"access_token")
						+ "&followType=" + follow;
		JSONArray array = new JSONArray();
		JSONObject params = new JSONObject();
		try {
			params.put("symbol", symbol);
			params.put("type", "FUND");
			array.put(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("params", params.toString());
		StringRequest stringRequest = new StringRequest(array,
				url, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						if (follow.equals("true")) {
							MyToast.makeText(getApplication(), "成功添加到自选基金",
									Toast.LENGTH_SHORT).show();
							lv_foud_search_adapter.notifyDataSetChanged();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							JSONObject jsonObject = new JSONObject(error.data());
							Log.d("error_description",
									jsonObject.getString("description"));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.d("error_Exception", e.toString());
						}
						if (state[position].equals("false")) {
							iv.setImageResource(R.drawable.btn_fs_selected);
							state[position] = "true";
							lv_foud_search_adapter.notifyDataSetChanged();
						} else {
							iv.setImageResource(R.drawable.btn_fs_unselected);
							state[position] = "false";
							lv_foud_search_adapter.notifyDataSetChanged();
						}
					}

				});
		volleyRequestQueue.add(stringRequest);
		http.sHttpCache.clear();
	}
}
