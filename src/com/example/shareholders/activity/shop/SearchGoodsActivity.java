package com.example.shareholders.activity.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.common.MyGridView;
import com.example.shareholders.common.MyListView;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.db.entity.SearchHistoryEntity;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.call.Position;

@ContentView(R.layout.activity_search_goods)
public class SearchGoodsActivity extends Activity {

	// 搜索框
	@ViewInject(R.id.et_search_text)
	private EditText et_search_text;

	// 搜索按钮
	@ViewInject(R.id.tv_ac_search)
	private TextView tv_ac_search;

	// 搜索及历史列表的布局
	@ViewInject(R.id.ll_search_toast)
	private LinearLayout ll_search_toast;

	// 热搜列表
	@ViewInject(R.id.gv_heat_search)
	private MyGridView gv_heat_search;

	// 搜索历史列表
	@ViewInject(R.id.lv_search_history)
	private MyListView lv_search_history;

	// 搜索框输入提示列表
	@ViewInject(R.id.lv_search)
	private ListView lv_search;

	// 搜索商品数据
	private ArrayList<HashMap<String, String>> goods_list = new ArrayList<HashMap<String, String>>();
	// 搜索商品数据
	private ArrayList<HashMap<String, String>> company_list = new ArrayList<HashMap<String, String>>();

	String[] gv_goods_prodUuid = new String[] {
			"B557FAF697ED4B0BBD9B3A4297902087",
			"CE134FF37D1448C6A92802CEFF4728A6",
			"hBBa9fg2979020jkAF67EDaBA48734B0",
			"fghjkAaa97ED4B0BD9B3A4297B902087",
			"fg2979020hjkAF697ED4B0BBaaB3A487",
			"3d3aaae22569445ea6175c6a25f9d339",
			"27d9d09f252f4becac45de5635171fea",
			"6a526c2f340d41ee9d08c13cdd644ffe" };
	String[] gv_goods_name = new String[] { "安琪酵母酸奶", "安琪酵母粉", "兔宝宝板材",
			"Gree/格力", "格力吊扇", "酱鸭300克", "鸭肫80gX4包", "鸡腿85gX3包" };
	DbUtils dbUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		dbUtils = DbUtils.create(this);

		et_search_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				Log.d("jatjat", arg0 + "11111");
				// 当搜索框不为空的时候设置历史热搜隐藏
				if (!et_search_text.getText().toString().trim().equals("")) {
					ll_search_toast.setVisibility(View.GONE);
					lv_search.setVisibility(View.VISIBLE);
					// 实时监听搜索
					searchGoods(et_search_text.getText().toString().trim());
				} else {
					ll_search_toast.setVisibility(View.VISIBLE);
					lv_search.setVisibility(View.GONE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
				if (et_search_text.getText().toString().equals("")) {
					;// 什么也不做
				} else {
					searchGoods(et_search_text.getText().toString().trim());
				}

			}
		});

		/**
		 * 模拟数据
		 */
		initData();

	}

	// 搜索商品接口
	private void searchGoods(String keywords) {
		String url = AppConfig.URL_SHOP + "product/fuzzyFindProduct";
		JSONObject params = new JSONObject();
		try {
			params.put("keywords", keywords);
			Log.d("jatjat", keywords);
		} catch (Exception e) {
			// TODO: handle exception
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						goods_list.clear();
						try {
							JSONArray jsonArray = new JSONArray(response);
							HashMap<String, String> data = null;
							Iterator<String> iterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								goods_list.add(data);
							}
							lv_search.setAdapter(new TipsAdapter(
									SearchGoodsActivity.this, goods_list));

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});

		stringRequest.setTag("searchGoods");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 模糊搜索公司
	 */
	private void searchCompany() {
		String url = AppConfig.URL_SHOP + "shop/fuzzySearchCertainShop";
		JSONObject params = new JSONObject();
		try {
			params.put("keywords", et_search_text.getText().toString().trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				params, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.d("dj_company_response", response);

						try {
							JSONArray jsonArray = new JSONArray(response);
							HashMap<String, String> data = null;
							Iterator<String> iterator = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								data = new HashMap<String, String>();
								iterator = jsonArray.getJSONObject(i).keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									data.put(key, jsonArray.getJSONObject(i)
											.get(key).toString());
								}
								company_list.add(data);
							}
							lv_search.setAdapter(new ResultAdapter(
									SearchGoodsActivity.this, company_list));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							lv_search.setAdapter(new ResultAdapter(
									SearchGoodsActivity.this, company_list));
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						lv_search.setAdapter(new ResultAdapter(
								SearchGoodsActivity.this, company_list));
					}
				});

		stringRequest.setTag("searchCompany");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	private void initData() {
		// 热搜
		gv_heat_search.setAdapter(new GridViewAdapter(this,
				new ArrayList<HashMap<String, String>>()));

		gv_heat_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				startActivity(new Intent(SearchGoodsActivity.this,
						GoodsDetailsActivity.class).putExtra("prodUuid",
						gv_goods_prodUuid[arg2]));
			}
		});

		// 历史记录
		final ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> hashMap = null;

		for (int i = 0; i < 7; i++) {
			hashMap = new HashMap<String, String>();
			hashMap.put("key", "key");
			lists.add(hashMap);
		}
		List<SearchHistoryEntity> list = new ArrayList<SearchHistoryEntity>();
		try {
			list = dbUtils.findAll(Selector.from(SearchHistoryEntity.class));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lv_search_history.setAdapter(new HistoryAdapter(this, list));

	}

	@OnClick({ R.id.rl_return, R.id.tv_ac_search })
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回
		case R.id.rl_return:
			finish();
			break;
		// 搜索
		case R.id.tv_ac_search:
			SearchHistoryEntity entity = new SearchHistoryEntity();
			entity.setText(et_search_text.getText().toString().trim());
			try {
				dbUtils.saveOrUpdate(entity);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			searchGoods(et_search_text.getText().toString().trim());

			break;

		//

		default:
			break;
		}
	}

	/**
	 * 热搜的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	class GridViewAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		public GridViewAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gv_goods_name.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return gv_goods_name[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {

			if (converView == null) {
				converView = inflater.inflate(R.layout.item_heat_search_gv,
						parent, false);
			}

			TextView tv_heat_item = AbViewHolder.get(converView,
					R.id.tv_heat_item);
			tv_heat_item.setText(gv_goods_name[position]);
			return converView;
		}

	}

	/**
	 * 历史记录的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	class HistoryAdapter extends BaseAdapter {

		LayoutInflater inflater;
		List<HashMap<String, String>> lists;
		List<SearchHistoryEntity> list;

		public HistoryAdapter(Context context, List<SearchHistoryEntity> list) {
			inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (list == null) {
				return 0;
			}
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
		public View getView(final int position, View converView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;

			if (converView == null) {
				viewHolder = new ViewHolder();

				converView = inflater.inflate(
						R.layout.item_search_history_layout, parent, false);

				// 搜索历史
				viewHolder.tv_history = (TextView) converView
						.findViewById(R.id.tv_history);
				// 删除按钮
				viewHolder.iv_delete = (ImageView) converView
						.findViewById(R.id.iv_delete);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}
			viewHolder.tv_history.setText(list.get(list.size() - position - 1)
					.getText());
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					try {

						SearchHistoryEntity searchHistoryEntity = dbUtils
								.findById(SearchHistoryEntity.class,
										list.get(list.size() - position - 1)
												.getText());
						dbUtils.delete(searchHistoryEntity);
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					list.remove(list.size() - position - 1);
					notifyDataSetChanged();
				}
			});

			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					et_search_text.setText(list.get(list.size() - position - 1)
							.getText());
				}
			});
			return converView;
		}

		class ViewHolder {
			TextView tv_history; // 搜索历史
			ImageView iv_delete;// 删除按钮
		}

	}

	/**
	 * 在搜索框输入时弹出的提示的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	class TipsAdapter extends BaseAdapter {
		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists;

		public TipsAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (lists.size() == 0) {
				return 2;
			}
			return lists.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if (arg0 == 0) {
				return null;
			}
			return lists.get(arg0 - 1);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View converView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;

			if (converView == null) {
				viewHolder = new ViewHolder();

				converView = inflater.inflate(R.layout.item_search_tips_layout,
						parent, false);

				// 搜索提示
				viewHolder.tv_tips = (TextView) converView
						.findViewById(R.id.tv_tips);

				converView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			if (position == 0) {
				viewHolder.tv_tips.setText("搜索“"
						+ et_search_text.getText().toString().trim() + "”的公司");
			} else if (position == 1 && lists.size() == 0) {// 商品没数据而且在第二行提示
				viewHolder.tv_tips.setText("没有“"
						+ et_search_text.getText().toString().trim() + "”的商品");
			} else {
				viewHolder.tv_tips.setText(lists.get(position - 1).get(
						"prodName"));
			}

			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// 点击item的时候记录历史查询
					SearchHistoryEntity entity = new SearchHistoryEntity();
					entity.setText(et_search_text.getText().toString().trim());
					try {
						dbUtils.saveOrUpdate(entity);
					} catch (DbException e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
					}

					// 点击第一条item则为查询公司
					if (position == 0) {
						searchCompany();
					} else if (goods_list.size() == 0) { // 无商品则点击item无动作
						;
					} else {
						startActivity(new Intent(SearchGoodsActivity.this,
								GoodsDetailsActivity.class).putExtra(
								"prodUuid",
								goods_list.get(position - 1).get("prodUuid")));
					}
				}
			});

			return converView;
		}

		class ViewHolder {
			TextView tv_tips;
		}
	}

	/**
	 * 点击相关公司的搜索结果的adapter
	 * 
	 * @author Administrator
	 * 
	 */
	class ResultAdapter extends BaseAdapter {
		LayoutInflater inflater;
		ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();

		public ResultAdapter(Context context,
				ArrayList<HashMap<String, String>> lists) {
			Log.d("dj_list.size()", lists.toString());
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (lists.size() == 0) {
				return 1;
			}
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return lists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View converView,
				ViewGroup parent) {
			Log.d("dj_list.size()", lists.size() + "");
			// 搜索的公司数据为空
			if (lists.size() == 0) {
				if (converView == null) {
					converView = inflater.inflate(
							R.layout.item_search_tips_layout, parent, false);
				}
				Log.d("dj_company_null", "null");
				TextView tv_tips = AbViewHolder.get(converView, R.id.tv_tips);
				tv_tips.setText("没有“" + et_search_text.getText().toString()
						+ "”公司");
			} else {// 搜索的公司数据不为空
				if (converView == null) {
					converView = inflater.inflate(
							R.layout.item_search_goods_result, parent, false);
				}
				Log.d("dj_company", "yes");
				ImageView iv_head = AbViewHolder.get(converView, R.id.iv_head);
				TextView tv_name = AbViewHolder.get(converView, R.id.tv_name);
				TextView tv_goods_num = AbViewHolder.get(converView,
						R.id.tv_goods_num);
				TextView tv_follow_num = AbViewHolder.get(converView,
						R.id.tv_follow_num);

				ImageLoader.getInstance().displayImage(
						lists.get(position).get("5"), iv_head);
				tv_name.setText(lists.get(position).get("shopName"));
				Log.d("dj_company_setText", tv_name.getText().toString());
				tv_follow_num.setText(lists.get(position).get("shopFollowers"));
				tv_goods_num.setText(lists.get(position).get("shopProductNum"));
			}

			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (lists.size() == 0)
						;
					// 否则，跳转到CompanyDetailActivity
					else {
						startActivity(new Intent(SearchGoodsActivity.this,
								CompanyDetailActivity.class).putExtra("shopId",
								company_list.get(position).get("shopId")));
					}
				}
			});

			return converView;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication.getRequestQueue().cancelAll("searchGoods");
		MyApplication.getRequestQueue().cancelAll("searchCompany");
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
