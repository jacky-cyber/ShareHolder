package com.example.shareholders.activity.survey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.adapter.ImageGridAdapter;
import com.example.shareholders.adapter.ImageGridAdapter.TextCallback;
import com.example.shareholders.db.entity.AlbumHelper;
import com.example.shareholders.db.entity.Bimp;
import com.example.shareholders.db.entity.ImageItem;

public class ImageGridActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	// private String activity_name;
	TextView tv_send;
	private ImageView iv_return;
	// private String surveyUuid;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this, "最多选择4张图片", 400).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_grid);
		// Intent intent = this.getIntent();
		// Bundle bundle = intent.getExtras();
		// activity_name = bundle.getString("activity");
		// Log.d("activity_name", activity_name);
		// surveyUuid=getIntent().getExtras().getString("surveyUuid");
		// Log.d("surveyUuid", surveyUuid);
		// Toast.makeText(getApplicationContext(), activity_name, 0).show();
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);
		initView();
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent i = new Intent(ImageGridActivity.this,
				// TestPicActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putString("activity", activity_name);
				// bundle.putString("surveyUuid", surveyUuid);
				// i.putExtras(bundle);
				finish();
				// startActivity(i);

			}
		});
		tv_send = (TextView) findViewById(R.id.tv_send);
		tv_send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}

				for (int i = 0; i < list.size(); i++) {
					if (Bimp.drr.size() < 9) {
						Bimp.drr.add(list.get(i));
					}
				}
				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putString("surveyUuid", surveyUuid);
				// intent.setClass(ImageGridActivity.this,
				// PublishTopicActivity.class);
				// intent.putExtras(bundle);
				finish();
				// startActivity(intent);

			}

		});
	}

	/**
	 * 鍒濆鍖杤iew瑙嗗浘
	 */
	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				tv_send.setText("完成" + "(" + count + ")");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * 閫氱煡閫傞厤鍣紝缁戝畾鐨勬暟鎹彂鐢熶簡鏀瑰彉锛屽簲褰撳埛鏂拌鍥�
				 */
				adapter.notifyDataSetChanged();
			}

		});

	}
}
