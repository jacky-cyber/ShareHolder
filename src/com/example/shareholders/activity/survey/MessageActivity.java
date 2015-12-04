package com.example.shareholders.activity.survey;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shareholders.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_message)
public class MessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ViewUtils.inject(this);
	}

	class MyListViewAdapter extends BaseAdapter {

		private List<Map<String, Object>> lists;
		private LayoutInflater inflater;

		public MyListViewAdapter(Context context,
				List<Map<String, Object>> lists) {
			inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {

			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			if (converView == null) {
				viewHolder = new ViewHolder();

				converView = inflater.inflate(R.layout.item_invite_friends,
						arg2, false);

				viewHolder.iv_choose = (ImageView) converView
						.findViewById(R.id.iv_choose);
				viewHolder.iv_head = (ImageView) converView
						.findViewById(R.id.iv_head);
				viewHolder.tv_name = (TextView) converView
						.findViewById(R.id.tv_name);

				converView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) converView.getTag();
			}

			viewHolder.iv_head.setImageResource((Integer) lists.get(position)
					.get("head"));
			viewHolder.tv_name.setText((CharSequence) lists.get(position).get(
					"name"));

			viewHolder.iv_choose.setOnClickListener(new ChooseClickListener(
					viewHolder.iv_choose, (Boolean) lists.get(position).get(
							"isChosen"), position));
			return converView;
		}

		class ViewHolder {
			ImageView iv_choose;
			ImageView iv_head;
			TextView tv_name;
		}

		class ChooseClickListener implements OnClickListener {

			private int position;
			private ImageView iv;
			private boolean isChosen;

			public ChooseClickListener(ImageView iv, Boolean isChosen,
					int position) {
				this.iv = iv;
				this.isChosen = isChosen;
				this.position = position;
			}

			@Override
			public void onClick(View arg0) {

				if (isChosen) {
					isChosen = false;
					lists.get(position).put("isChosen", isChosen);
					iv.setImageResource(R.drawable.btn_weixuanzhong_normal);
				} else {
					isChosen = true;
					lists.get(position).put("isChosen", isChosen);
					iv.setImageResource(R.drawable.btn_xuanzhong_selected);
				}

			}
		}

	}

}
