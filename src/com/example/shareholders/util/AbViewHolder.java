package com.example.shareholders.util;

import android.util.SparseArray;
import android.view.View;

/**
 * ? 2012 amsoft.cn ��ƣ�AbViewHolder.java ������������ViewHolder.
 * �����򵥣������Եͣ����Ժ���
 * 
 * @author ����һ����
 * @version v1.0
 * @date��2014-06-17 ����20:32:13
 */
public class AbViewHolder {

	/**
	 * ImageView view = AbViewHolder.get(convertView, R.id.imageView);
	 * 
	 * @param view
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();

		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
