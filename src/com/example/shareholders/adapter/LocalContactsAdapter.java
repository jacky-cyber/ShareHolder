package com.example.shareholders.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.shareholders.R;
import com.example.shareholders.common.LocalContactModel;
import com.example.shareholders.util.AbViewHolder;

public class LocalContactsAdapter extends BaseAdapter implements
		SectionIndexer, Filterable {

	private Context context;
	private boolean isSearch = false;
	private boolean isEdit = false;
	private String curPath = null;
	private List<LocalContactModel> allModelList = new ArrayList<LocalContactModel>();
	private List<LocalContactModel> list = null;
	private List<LocalContactModel> resultList = new ArrayList<LocalContactModel>();

	public LocalContactsAdapter(Context context, List<LocalContactModel> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}

	private void notifyDataSetChanged(boolean isEdit) {
		this.isEdit = isEdit;
		notifyDataSetChanged();
	}

	public void setCurPath(String curPath) {
		this.curPath = curPath;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (isSearch) {
			return resultList.size();
		}
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if (isSearch) {
			return resultList.get(arg0);
		}
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View contentView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LocalContactModel localContactModel = null;
		if (contentView == null) {
			contentView = LayoutInflater.from(context).inflate(
					R.layout.local_contact_item, null);
		}
		TextView tvTitle = AbViewHolder.get(contentView, R.id.title);
		TextView tvLetter = AbViewHolder.get(contentView, R.id.catalog);
		TextView tvInvite = AbViewHolder.get(contentView, R.id.tv_invite);

		if (isSearch) {
			localContactModel = resultList.get(position);
		} else {
			localContactModel = list.get(position);
		}

		tvTitle.setText(localContactModel.getName().toString());

		tvInvite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String numberString = null;
				if (isSearch) {
					numberString = resultList.get(position).getNumber();
				} else {
					numberString = list.get(position).getNumber();
				}

				Uri smsUri = Uri.parse("smsto:" + numberString);
				Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
				intent.putExtra("sms_body", context.getResources().getString(R.string.sms_share));
				context.startActivity(intent);
			}
		});

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			tvLetter.setVisibility(View.VISIBLE);
			tvLetter.setText(localContactModel.getSortLetters());
		} else {
			tvLetter.setVisibility(View.GONE);
		}
		return contentView;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return list.get(position).getSortLetters().charAt(0);
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	private final int MSG_SEARCH_START = 0;
	private final int MSG_SEARCH_FINISH = 1;
	private final int MSG_SEARCH_RESET = 2;
	private final int MSG_UPDATE_UI = 3;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_SEARCH_START) {
			} else if (msg.what == MSG_SEARCH_FINISH) {
				notifyDataSetChanged();
			} else if (msg.what == MSG_SEARCH_RESET) {
				notifyDataSetChanged();
			} else if (msg.what == MSG_UPDATE_UI) {
				notifyDataSetChanged();
			}
		};
	};

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		Filter filter = new Filter() {

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// TODO Auto-generated method stub
				if (isSearch) {
					List<LocalContactModel> tempList = (List<LocalContactModel>) results.values;
					if (isSearch) {
						resultList.clear();
						resultList.addAll(tempList);
					}
					notifyDataSetChanged();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(MSG_SEARCH_START);
				FilterResults results = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {
					ArrayList<LocalContactModel> resultList = new ArrayList<LocalContactModel>();
					int size = list.size();
					for (int i = 0; i < size; i++) {
						LocalContactModel localContactModel = list.get(i);
						String desStr = constraint.toString().toLowerCase();
						String orgStr = localContactModel.getName()
								.toLowerCase();
						if (orgStr.contains(desStr)) {
							resultList.add(localContactModel);
						}
					}
					results.values = resultList;
					results.count = resultList.size();
					isSearch = true;
					handler.sendEmptyMessage(MSG_SEARCH_FINISH);
				} else {
					isSearch = false;
					handler.sendEmptyMessage(MSG_SEARCH_RESET);
					synchronized (list) {
						results.values = list;
						results.count = list.size();
					}
				}
				return results;
			}
		};
		return filter;
	}

}
