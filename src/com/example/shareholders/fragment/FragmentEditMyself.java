package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.shareholders.R;
import com.example.shareholders.adapter.EditMyselfNewsAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FragmentEditMyself extends Fragment {
	@ViewInject(R.id.lv_edit_myself_news)
	private ListView editNewsList;

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private EditMyselfNewsAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_edit_myself_news, null);
		ViewUtils.inject(this, v);
		init();
		return v;
	}

	private void init() {
		mAdapter = new EditMyselfNewsAdapter(getActivity());
		editNewsList.setAdapter(mAdapter);
	}

	// public class EditMyselfNewsAdapter extends BaseAdapter{
	//
	// private List<Map<String,Object>> list;
	// private Context context;
	// private LayoutInflater mInflater;
	//
	// public EditMyselfNewsAdapter(Context context ,
	// List<Map<String,Object>> list){
	// this.context = context;
	// this.list = list;
	// mInflater = LayoutInflater.from(context);
	// }
	//
	// public EditMyselfNewsAdapter(Context context){
	// this.context = context;
	// mInflater = LayoutInflater.from(context);
	// }
	//
	// @Override
	// public int getCount() {
	// // TODO Auto-generated method stub
	// return 10;
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// // TODO Auto-generated method stub
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View view, ViewGroup parent) {
	// // TODO Auto-generated method stub
	// LayoutInflater mInflater = LayoutInflater.from(context);
	// view = mInflater.inflate(R.layout.item_edit_myself_news, null);
	// return view;
	// }
	//
	// }
}
