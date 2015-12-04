package com.example.shareholders.activity.personal;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.LocalContactsAdapter;
import com.example.shareholders.common.CharacterParser;
import com.example.shareholders.common.LocalContactModel;
import com.example.shareholders.common.PinyinComparator;
import com.example.shareholders.common.SideBar;
import com.example.shareholders.common.SideBar.OnTouchingLetterChangedListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 获取本地联系人信息
 * 
 * @author jat
 * 
 */
@ContentView(R.layout.activity_stock_friends_local_contacts)
public class StockFriendsLocalContacts extends Activity implements
		OnQueryTextListener {

	@ViewInject(R.id.lv_local_friends)
	private ListView lv_local_friends;

	@ViewInject(R.id.sidrbar)
	private SideBar sideBar;

	@ViewInject(R.id.dialog)
	private TextView dialog;

	@ViewInject(R.id.title_note)
	private ImageView title_note;

	@ViewInject(R.id.search_view)
	private SearchView search_view;

	// 分类朋友适配器
	private LocalContactsAdapter adapter;

	ProgressDialog myDialog;

	// 本地联系人集合
	private List<LocalContactModel> list = new ArrayList<LocalContactModel>();
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<LocalContactModel> sourceDateList;
	// private List<String> allCodesList = new ArrayList<String>();
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			sourceDateList = filledData(list);

			// Collections.sort(sourceDateList,pinyinComparator);

			adapter = new LocalContactsAdapter(StockFriendsLocalContacts.this,
					sourceDateList);
			lv_local_friends.setAdapter(adapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
		GetLoaclContactsTask task = new GetLoaclContactsTask();
		task.execute();
		initView();
	}

	@OnClick({ R.id.title_note,R.id.rl_return })
	private void Onclick(View view) {
		switch (view.getId()) {
		case R.id.title_note:
			finish();
			break;

		case R.id.rl_return:
			finish();
			break;
		default:
			break;
		}
	}

	private void initView() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar.setTextView(dialog);
		lv_local_friends.setTextFilterEnabled(true);
		search_view.setOnQueryTextListener(this);
		search_view.setSubmitButtonEnabled(true);

		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					lv_local_friends.setSelection(position);
				}
			}
		});
		// ProgressDialog dialog = new
		// ProgressDialog(StockFriendsLocalContacts.this);
		// dialog.setMessage("获取联系人中...");
		// dialog.show();

		// dialog.dismiss();

	}

	class GetLoaclContactsTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			myDialog = new ProgressDialog(StockFriendsLocalContacts.this);
			myDialog.setMessage("获取联系人中...");
			myDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			ContentResolver cr = StockFriendsLocalContacts.this
					.getContentResolver();
			Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
			List<LocalContactModel> contacts = new ArrayList<LocalContactModel>();
			Cursor cursor = cr.query(uri,
					new String[] { "_id", "display_name" }, null, null, null);
			while (cursor.moveToNext()) {
				LocalContactModel info = new LocalContactModel();
				String _id = cursor.getString(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor
						.getColumnIndex("display_name"));
				info.setName(name);
				// 查询data表
				uri = Uri.parse("content://com.android.contacts/raw_contacts/"
						+ _id + "/data");
				Cursor c = cr.query(uri, new String[] { "data1", "mimetype" },
						null, null, null);
				// 处理电话号码的操作
				while (c.moveToNext()) {
					String data1 = c.getString(c.getColumnIndex("data1"));
					String mimetype = c.getString(c.getColumnIndex("mimetype"));
					if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
						info.setNumber(data1);
						contacts.add(info);
					}
				}
				c.close();
			}
			cursor.close();
			Log.d("asfffffffff", contacts.toString());
			list = contacts;
			return "1";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			myDialog.dismiss();
			myHandler.sendEmptyMessage(1);
		}

	}

	/**
	 * 填充数据
	 * 
	 * @param Data
	 * @return
	 */
	private List<LocalContactModel> filledData(List<LocalContactModel> list) {
		List<LocalContactModel> mSortList = new ArrayList<LocalContactModel>();

		for (int i = 0; i < list.size(); i++) {
			LocalContactModel localContactModel = new LocalContactModel();
			localContactModel.setName(list.get(i).getName());
			localContactModel.setNumber(list.get(i).getNumber());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(list.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				localContactModel.setSortLetters(sortString.toUpperCase());
			} else {
				localContactModel.setSortLetters("#");
			}

			mSortList.add(localContactModel);
		}
		return mSortList;

	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		if (arg0.isEmpty()) {
			lv_local_friends.clearTextFilter();
		} else {
			lv_local_friends.setFilterText(arg0.trim());
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		lv_local_friends.setFilterText(arg0.trim());
		return false;
	}

	// class Get

}
