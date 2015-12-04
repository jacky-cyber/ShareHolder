package com.example.shareholders.activity.stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.shareholders.R;
import com.example.shareholders.common.MyApplication;
import com.example.shareholders.config.AppConfig;
import com.example.shareholders.util.AbViewHolder;
import com.example.shareholders.util.RsSharedUtil;
import com.example.shareholders.view.DragView;
import com.example.shareholders.view.DragView.Drag;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@ContentView(R.layout.activity_edit_choose_myself)
public class EditMyselfActivity extends Activity implements Drag {

	@ViewInject(R.id.lv_edit_myself)
	private DragView lv_edit_myself;

	// 是否全选
	private boolean isAllSelected = false;

	// 关注了的数据
	private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	// 准备要取消关注的数据
	private List<HashMap<String, String>> deleteData = new ArrayList<HashMap<String, String>>();
	private DragListAdapter adapter;

	// 全选按钮
	@ViewInject(R.id.iv_edit_allselected)
	private ImageView iv_edit_allselected;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		initView();
	}

	/**
	 * 完成编辑
	 */
	private void finishEdit() {
		String url = AppConfig.URL_QUOTATION + "myStock/editSort.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(), AppConfig.ACCESS_TOKEN);
		Log.d("url", url);
		JSONArray jsonArray = new JSONArray();
		Log.d("url", data.size() + "");
		try {
			for (int i = 0; i < data.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("symbol", data.get(i).get("symbol"));
				jsonObject.put("securityType", data.get(i).get("securityType"));
				jsonArray.put(jsonObject);
			}
			Log.d("url", jsonArray.toString() + "");
			Log.d("url", jsonArray.length() + "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("jsonArray2", "jsonArray");
		}

		StringRequest stringRequest = new StringRequest(jsonArray, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.d("finish_response", "finish_response");
				Log.d("finish_response", response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d("error", "error");
				Log.d("finish_response", error.toString());
			}
		});
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 获取自选股的数据
	 */
	private void initView() {
		String url = AppConfig.URL_QUOTATION + "myStocks.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(), AppConfig.ACCESS_TOKEN);
		Log.d("url", url);
		StringRequest stringRequest = new StringRequest(url, null, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				try {
					JSONArray jsonArray = new JSONArray(response);
					for (int i = 0; i < jsonArray.length(); i++) {
						HashMap<String, String> hashMap = new HashMap<String, String>();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Iterator<String> iterator = jsonObject.keys();
						while (iterator.hasNext()) {
							String key = iterator.next();
							hashMap.put(key, jsonObject.getString(key).toString());

						}
						hashMap.put("isChoosed", "false");
						data.add(hashMap);
					}
					adapter = new DragListAdapter(getApplicationContext(), data);
					lv_edit_myself.setDrag(EditMyselfActivity.this);
					lv_edit_myself.setAdapter(adapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		stringRequest.setTag("initView");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	/**
	 * 批量取消订阅自选股
	 * 
	 * @param deleteData
	 */
	private void deleteStock(List<HashMap<String, String>> deleteData) {
		String url = AppConfig.URL_USER + "security.json?access_token="
				+ RsSharedUtil.getString(getApplicationContext(), AppConfig.ACCESS_TOKEN) + "&followType=false";
		Log.d("deleteStock", url);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < deleteData.size(); i++) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("symbol", deleteData.get(i).get("symbol"));
				jsonObject.put("type", deleteData.get(i).get("securityType"));
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (jsonArray.length()==0) {
			finish();
		}
		Log.d("deleteStock", jsonArray.toString());
		StringRequest stringRequest = new StringRequest(jsonArray, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.d("response", "success");
				// 提交成功后发送广播通知行情界面更新
	//			Intent intent = new Intent(); // 要发送的内容
	//			intent.setAction("situation_update"); // 设置广播的action
	//			sendBroadcast(intent); // 发送广播
				finish();
				
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d("response", error.toString());
			}
		});
		stringRequest.setTag("deleteStock");
		MyApplication.getRequestQueue().add(stringRequest);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		MyApplication.getRequestQueue().cancelAll("initView");
		super.onDestroy();
	}

	@OnClick({ R.id.iv_title_back, R.id.tv_edit_sure, R.id.rl_allchoose, R.id.tv_edit_delete })
	public void onClick(View view) {
		switch (view.getId()) {
		// 返回
		case R.id.iv_title_back:
			finish();
			break;
		// 确定
		case R.id.tv_edit_sure:
			finishEdit();
			deleteStock(deleteData);
			break;
		// 全选
		case R.id.rl_allchoose:
			selectAllItem();
			break;
		// 删除
		case R.id.tv_edit_delete:
			deleteItem();
			
			break;
		default:
			break;
		}
	}

	/**
	 * 删除
	 */
	private void deleteItem() {
		// TODO Auto-generated method stub
		for (int i = data.size() - 1; i >= 0; i--) {
			if (data.get(i).get("isChoosed").equals("true")) {
				// 把数据添加到要取消关注的list中
				deleteData.add(data.get(i));
				// 删除
				data.remove(i);
			}
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 点击全选按钮
	 */
	private void selectAllItem() {
		// TODO Auto-generated method stub
		Log.d("selectAllItem", "selectAllItem");
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (isAllSelected) {
			iv_edit_allselected.setImageResource(R.drawable.btn_allselected);

			for (int i = 0; i < data.size(); i++) {
				Log.d("issssdatasize", data.size()+"");
				hashMap = data.get(i);
				hashMap.put("isChoosed", "false");
				data.set(i, hashMap);
			}
		} else {
			iv_edit_allselected.setImageResource(R.drawable.btn_selected);
			for (int i = 0; i < data.size(); i++) {
				Log.d("notsssdatasize", data.size()+"");
				hashMap = data.get(i);
				hashMap.put("isChoosed", "true");
				data.set(i, hashMap);
			}
		}
		isAllSelected = !isAllSelected;
		adapter.notifyDataSetChanged();
	}

	public class DragListAdapter extends ArrayAdapter<HashMap<String, String>> {
		public DragListAdapter(Context context, List<HashMap<String, String>> objects) {
			super(context, 0, objects);
		}

		public List<HashMap<String, String>> getList() {
			return data;
		}

		@Override
		public boolean isEnabled(int position) {
			return super.isEnabled(position);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.item_edit_myself, null);
			}
			// 置顶
			ImageView iv_edit_setTop = (ImageView) AbViewHolder.get(view, R.id.iv_edit_setTop);
			iv_edit_setTop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					data.add(0, data.get(position));
					data.remove(position + 1);
					notifyDataSetChanged();
				}
			});
			// 股票代码
			TextView tv_codeTextView = (TextView) AbViewHolder.get(view, R.id.tv_lv_num);
			// 公司名称
			TextView tv_name = (TextView) AbViewHolder.get(view, R.id.tv_edit_name);
			// 提醒设置
			ImageView iv_edit_remind = (ImageView) AbViewHolder.get(view, R.id.iv_edit_remind);
			// 选中框
			final ImageView iv_choose = (ImageView) AbViewHolder.get(view, R.id.iv_edit_selected);
			// 若当前的Item选中
			if (data.get(position).get("isChoosed").equals("true")) {
				iv_choose.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selected));

			}
			// 若当前的Item还没选中
			else {
				iv_choose.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_unselected));
			}

			// 查看是否选中，若选中，则切换为不选中， 否则，相反
			final int itemPosition = position;
			iv_choose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (data.get(itemPosition).get("isChoosed").equals("false")) {
						HashMap<String, String> hashMap = new HashMap<String, String>();
						hashMap = data.get(itemPosition);
						hashMap.put("isChoosed", "true");
						data.set(itemPosition, hashMap);
						iv_choose.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selected));
					} else {
						HashMap<String, String> hashMap = new HashMap<String, String>();
						hashMap = data.get(itemPosition);
						hashMap.put("isChoosed", "false");
						data.set(itemPosition, hashMap);
						iv_choose.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_unselected));
					}
				}
			});
			tv_codeTextView.setText(data.get(position).get("symbol"));
			tv_name.setText(data.get(position).get("shortname"));
			iv_edit_remind.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Bundle bundle = new Bundle();
					bundle.putString("symbol", data.get(position).get("symbol").replaceAll("[^(0-9)]", ""));
					bundle.putString("securityType", data.get(position).get("securityType"));
					bundle.putString("shortname", data.get(position).get("shortname"));
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(getApplicationContext(), RemindEditActivity.class);
					startActivity(intent);
				}
			});
			return view;
		}
	}

	/**
	 * 拖动后的操作 fromPosition toPosition
	 */
	@Override
	public void doForDrag(int fromPosition, int toPosition) {
		// TODO Auto-generated method stub
		data = indexExChange(data, fromPosition, toPosition);
		adapter.notifyDataSetChanged();
	}

	/**
	 * HashMap内部交换
	 * 
	 * @param list
	 * @param fromPosition
	 * @param toPosition
	 * @return
	 */
	public List<HashMap<String, String>> indexExChange(List<HashMap<String, String>> list, int fromPosition,
			int toPosition) {
		HashMap<String, String> t = list.get(fromPosition);
		list.set(fromPosition, list.get(toPosition));
		list.set(toPosition, t);
		return list;
	}
}
