package com.example.shareholders.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareholders.R;
import com.example.shareholders.activity.survey.ActivityCreateActivity;
import com.example.shareholders.activity.survey.ActivityCreateEditActivity;
import com.example.shareholders.common.InternetDialog;
import com.example.shareholders.common.MyViewPager;
import com.example.shareholders.db.entity.EnterpriseEntity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class Fragment_ActivityCreate2 extends Fragment implements
		OnClickListener {
	private int currentPosition = -1;
	private LinearLayout ll_add;
	private ImageView iv_return;
	private ListView lv_company;
	// 所有调研公司
	private ArrayList<HashMap<String, Object>> enterprises;
	private ActivityCreateAdapter adapter;

	@ViewInject(R.id.tv_next)
	private TextView tv_next;
	/*
	 * ActivityCreateEditActivity传递过来的数据,调研公司所需要的内容
	 */
	private String beginDate;
	private String content;
	private String endDate;
	private String locationName;
	private String industryCode;
	private String locationCode;
	private String receicerpost;
	private String shortName;
	private String symbol;
	private String uuid;
	private String type;
	// 创建调研
	private int CREATE_SURVEY = 0;
	// 编辑调研
	private int EDIT_SURVEY = 1;
	// 删除调研
	private int DELETE_SURVEY = 2;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_activitycreate2, container,
				false);
		ViewUtils.inject(this, v);
		tv_next.setOnClickListener(this);

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// 初始化
		init();
		super.onActivityCreated(savedInstanceState);
	}

	// 功能：初始化数据
	public void init() {
		ll_add = (LinearLayout) getActivity().findViewById(R.id.ll_add);
		ll_add.setOnClickListener(this);
		lv_company = (ListView) getActivity().findViewById(R.id.lv_frag2);
		enterprises = new ArrayList<HashMap<String, Object>>();
		// 如果是编辑活动,获取数据的数据，获取所有调研公司
		if (ActivityCreateActivity.sign != 0) {
			DbUtils db = DbUtils.create(getActivity());
			List<EnterpriseEntity> enterpriseEntities = new ArrayList<EnterpriseEntity>();
			try {

				enterpriseEntities = db.findAll(EnterpriseEntity.class);
				if (enterpriseEntities != null) {
					for (int i = 0; i < enterpriseEntities.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						// 起始时间
						hashMap.put("beginDate", enterpriseEntities.get(i)
								.getBeginDate());
						// 具体内容
						hashMap.put("content", enterpriseEntities.get(i)
								.getContent());
						// 结束日期
						hashMap.put("endDate", enterpriseEntities.get(i)
								.getEndDate());
						// 行业代码
						hashMap.put("industryCode", enterpriseEntities.get(i)
								.getIndustryCode());
						// 企业所在城市
						hashMap.put("locationName", enterpriseEntities.get(i)
								.getLocationName());
						// 公司类型
						hashMap.put("type", enterpriseEntities.get(i).getType());
						// 企业代码
						hashMap.put("locationCode", enterpriseEntities.get(i)
								.getLocationName());
						// 被调研人职务
						hashMap.put("receicerpost", enterpriseEntities.get(i)
								.getReceicerpost());
						// 企业简称
						hashMap.put("shortName", enterpriseEntities.get(i)
								.getShortName());
						// 股票代码
						hashMap.put("symbol", enterpriseEntities.get(i)
								.getSymbol());
						// 序号
						hashMap.put("number", i + "");
						// UUID
						hashMap.put("uuid", enterpriseEntities.get(i).getUuid());
						Log.d("uuid", enterpriseEntities.get(i).getUuid());
						enterprises.add(hashMap);
					}
					adapter = new ActivityCreateAdapter(getActivity(),
							enterprises);
					lv_company.setAdapter(adapter);
					db.close();
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Title: onActivityResult
	 * </p>
	 * <p>
	 * Description: 后一个activity（ActivityCreateEditActivity）返回数据
	 * </p>
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("requestCode", requestCode + "");
		Log.d("resultCode", resultCode + "");
		if (resultCode == CREATE_SURVEY || resultCode == EDIT_SURVEY) {
			try {
				int position = Integer.parseInt(data.getExtras().getString(
						"position"));
				beginDate = data.getExtras().getString("beginDate");
				content = data.getExtras().getString("content");
				endDate = data.getExtras().getString("endDate");
				locationName = data.getExtras().getString("locationName");
				industryCode = data.getExtras().getString("industryCode");
				locationCode = data.getExtras().getString("locationCode");
				receicerpost = data.getExtras().getString("receicerpost");
				shortName = data.getExtras().getString("shortName");
				symbol = data.getExtras().getString("symbol");
				uuid = data.getExtras().getString("uuid");
				type = data.getExtras().getString("type");
				Log.d("uuid", uuid + "");
				Log.d("locationCode", locationCode + "");

				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("beginDate", beginDate);
				hashMap.put("content", content);
				hashMap.put("endDate", endDate);
				hashMap.put("locationName", locationName);
				hashMap.put("industryCode", industryCode);
				hashMap.put("locationCode", locationCode);
				hashMap.put("receicerpost", receicerpost);
				hashMap.put("shortName", shortName);
				hashMap.put("symbol", symbol);
				hashMap.put("uuid", uuid);
				hashMap.put("type", type);
				// 如果是创建活动
				if (requestCode == CREATE_SURVEY) {
					// 新建一条记录
					enterprises.add(hashMap);

				}
				// 如果是编辑活动
				else if (resultCode == EDIT_SURVEY) {
					// 修改一条记录
					enterprises.set(position, hashMap);
				}
				adapter = new ActivityCreateAdapter(getActivity(), enterprises);
				lv_company.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				LogUtils.d(e.toString());
			}
		}
		// 删除公司
		else if (resultCode == DELETE_SURVEY) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			int position = Integer.parseInt(data.getExtras().getString(
					"position"));
			Log.d("position", position + "");
			enterprises.remove(position);
			adapter = new ActivityCreateAdapter(getActivity(), enterprises);
			lv_company.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 如果是创建调研公司
		case R.id.ll_add:
			Intent intent = new Intent();
			intent.setClass(getActivity(), ActivityCreateEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("position", "-1");
			intent.putExtras(bundle);
			startActivityForResult(intent, CREATE_SURVEY);
			break;

		case R.id.tv_next:
			// 如果没有调研公司，先添加调研公司
			if (enterprises.isEmpty()) {
				InternetDialog internetDialog = new InternetDialog(
						getActivity());
				internetDialog.showInternetDialog("请先添加调研公司", false);
			} else {
				((MyViewPager) (getActivity()
						.findViewById(R.id.vp_activity_create)))
						.setCurrentItem(2);
				break;
			}
		}
	}

	/*
	 * ListView的适配器
	 */

	public class ActivityCreateAdapter extends BaseAdapter {
		private ViewHolder holder;
		private ArrayList<HashMap<String, Object>> list;
		private Context context;
		private LayoutInflater mInflater;

		public ActivityCreateAdapter(Context context,
				ArrayList<HashMap<String, Object>> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
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
				holder = new ViewHolder();
				view = LayoutInflater.from(context).inflate(
						R.layout.item_enterprise_layout, parent, false);

				holder.tv_name = (TextView) view.findViewById(R.id.tv_en_name);
				holder.tv_city = (TextView) view
						.findViewById(R.id.tv_en_location);
				holder.tv_post = (TextView) view
						.findViewById(R.id.tv_en_position);
				holder.tv_b_date = (TextView) view.findViewById(R.id.tv_b_date);
				holder.tv_a_date = (TextView) view.findViewById(R.id.tv_a_date);
				holder.tv_outline = (TextView) view
						.findViewById(R.id.tv_outline_content);
				holder.tv_confim = (TextView) view
						.findViewById(R.id.tv_en_confirm);
				holder.ll_outline = (LinearLayout) view
						.findViewById(R.id.ll_outline);
				holder.rl_out_content = (RelativeLayout) view
						.findViewById(R.id.rl_outline_content);
				holder.number = (TextView) view.findViewById(R.id.number);
				holder.iv_xiala = (ImageView) view.findViewById(R.id.iv_xiala);

				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			if (position == currentPosition) {
				holder.rl_out_content.setVisibility(View.VISIBLE);
				holder.iv_xiala.setImageResource(R.drawable.ico_shangla);
			} else {
				holder.rl_out_content.setVisibility(View.GONE);
				holder.iv_xiala.setImageResource(R.drawable.ico_xiala);
			}
			// 股票代码
			holder.number.setText((CharSequence) list.get(position).get(
					"symbol"));
			// 序号
			holder.number.setText(position + 1 + "");
			// 企业简称
			holder.tv_name.setText((CharSequence) list.get(position).get(
					"shortName"));
			// 城市名称
			holder.tv_city.setText((CharSequence) list.get(position).get(
					"locationName"));
			// 被调研人职务
			holder.tv_post.setText((CharSequence) list.get(position).get(
					"receicerpost"));
			// 起始日期
			holder.tv_b_date.setText((CharSequence) list.get(position).get(
					"beginDate"));
			// 结束日期
			holder.tv_a_date.setText((CharSequence) list.get(position).get(
					"endDate"));
			// 内容
			holder.tv_outline.setText((CharSequence) list.get(position).get(
					"content"));
			// 编辑
			holder.tv_confim.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity(),
							ActivityCreateEditActivity.class);
					Bundle bundle = new Bundle();
					Log.d("position", position + "");
					Log.d("list.size()", list.size() + "");

					// 位置
					bundle.putString("position", position + "");
					// 企业简称
					bundle.putString("shortName",
							list.get(position).get("shortName").toString());
					// 地点
					bundle.putString("locationName",
							list.get(position).get("locationName").toString());
					// 地点代码
					bundle.putString("locationCode",
							list.get(position).get("locationCode").toString());
					// 行业代码
					bundle.putString("industryCode",
							list.get(position).get("industryCode").toString());
					// 被调研人职务
					bundle.putString("receicerpost",
							list.get(position).get("receicerpost").toString());
					// 起始日期
					bundle.putString("beginDate",
							list.get(position).get("beginDate").toString());
					// 结束日期
					bundle.putString("endDate",
							list.get(position).get("endDate").toString());
					// 内容
					bundle.putString("content",
							list.get(position).get("content").toString());
					// 股票代码
					bundle.putString("symbol", list.get(position).get("symbol")
							.toString());
					// 公司类型
					bundle.putString("type", list.get(position).get("type")
							.toString());
					// uuid
					if (list.get(position).get("uuid") != null)
						bundle.putString("uuid", list.get(position).get("uuid")
								.toString());
					intent.putExtras(bundle);
					startActivityForResult(intent, EDIT_SURVEY);

				}
			});
			holder.ll_outline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (currentPosition == position) {
						currentPosition = -1;
					} else
						currentPosition = position;
					ActivityCreateAdapter.this.notifyDataSetChanged();

				}
			});
			return view;
		}

		class ViewHolder {
			TextView number;
			TextView tv_name;
			TextView tv_city;
			TextView tv_post;
			TextView tv_b_date;
			TextView tv_a_date;
			TextView tv_outline;
			TextView tv_confim;
			LinearLayout ll_outline;
			RelativeLayout rl_out_content;
			ImageView iv_xiala;
		}
	}
}
