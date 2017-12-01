package com.mj.gpsclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.gpsclient.R;
import com.mj.gpsclient.Activity.DevicesTrackActivity;
import com.mj.gpsclient.Activity.FollowingActivity;
import com.mj.gpsclient.Activity.MoreMapsActivity;
import com.mj.gpsclient.Activity.PubUtil;
import com.mj.gpsclient.db.DataFollowHelper;
import com.mj.gpsclient.global.DebugLog;
import com.mj.gpsclient.model.Devices;
import com.mj.gpsclient.view.FollowingSelectView;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by majin on 15/5/27.
 */
public class DevicesListAdapter extends BaseAdapter implements Filterable {

	private List<Devices> backList;
	private List<Devices> devicesList;
	private Context mContext;
	private LayoutInflater m_layoutInflater;
	private MyFilter mFilter;
	private Handler mHandle;
	private boolean selected;// 是否需要将iv_item_device控件显示
	private FollowingSelectView followView;
	private ImageView iv;
	private DataFollowHelper helper;
	private String loadName;


	public DevicesListAdapter(List<Devices> list, Context Context,
			Handler handler, FollowingSelectView followView,
			DataFollowHelper helper,String loadName) {
		super();
		this.followView = followView;
		mContext = Context;
		this.m_layoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setDate(list);
		mHandle = handler;
		this.helper = helper;
		this.loadName = loadName;
	}

	@Override
	public int getCount() {
		return this.devicesList.size();
	}

	@Override
	public Object getItem(int i) {
		return devicesList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	public void setLayout(boolean selected) {
		this.selected = selected;
	}

	public void setDate(List<Devices> data) {
		this.backList = data;
		if (devicesList == null) {
			this.devicesList = new ArrayList<Devices>();
		}
		this.devicesList.clear();
		this.devicesList.addAll(data);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		iv = (ImageView) followView.findViewById(R.id.iv_follow_view);
		final Devices bean = devicesList.get(i);
		if (convertView == null) {
			convertView = m_layoutInflater.inflate(R.layout.item_list_devices,
					null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final ImageView iv_item_device = (ImageView) convertView
				.findViewById(R.id.iv_item_device);
		RelativeLayout rl = (RelativeLayout) convertView
				.findViewById(R.id.rl_forClick);
		rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PubUtil.layoutNum != 1) {
					if (PubUtil.follow_bt_status == 103) {
						Intent intent = new Intent(mContext,
								FollowingActivity.class);
						intent.putExtra("memberName", bean.getName());
						intent.putExtra("memberImei", bean.getIMEI());
						intent.putExtra("lat", bean.getLat());
						intent.putExtra("lng", bean.getLng());
						mContext.startActivity(intent);
					} else {
						Intent intent = new Intent(mContext,
								DevicesTrackActivity.class);
						intent.putExtra("memberName", bean.getName());
						intent.putExtra("memberStuts", bean.getLineStatus());
						mContext.startActivity(intent);
					}
				} else {
					if (PubUtil.Logo1Clicked) {
						//
						if (PubUtil.follow_bt_status == 103) {
							initClick(bean, iv_item_device);
						} else {
							if (bean.getLineStatus().equals("离线")) {
								Toast.makeText(mContext, "离线设备不可实时跟踪", 0)
										.show();
							} else {
								initClick(bean, iv_item_device);
							}
						}
					} else if (PubUtil.Logo2Clicked) {// Logo2Clicked被点击
						if (PubUtil.follow_bt_status == 103) {
							initClick(bean, iv_item_device);
						} else {
							if (bean.getLineStatus().equals("离线")) {
								Toast.makeText(mContext, "离线设备不可实时跟踪", 0)
										.show();
							} else {
								initClick(bean, iv_item_device);
							}
						}
					}else if(PubUtil.LogoSearchClicked){
						
						Intent intent = new Intent(mContext,
								DevicesTrackActivity.class);
						intent.putExtra("memberName", bean.getName());
						intent.putExtra("memberStuts", bean.getLineStatus());
						mContext.startActivity(intent);
					}
				}

			}

		});
		if (selected) {// select为true，表示进入编辑状态
			PubUtil.layoutNum = 1;
			// 跟踪列表选择编辑状态
			iv_item_device.setVisibility(View.VISIBLE);
			iv_item_device.setBackgroundResource(R.drawable.check);
			if (PubUtil.Logo1Clicked) {
				if (bean.getWarnNo().equals("8")) {
					iv_item_device.setBackgroundResource(R.drawable.checked);
				} else {
					iv_item_device.setBackgroundResource(R.drawable.check);
				}
			} else if (PubUtil.Logo2Clicked) {
				if (bean.getWarnNo().equals("8")) {
					iv_item_device.setBackgroundResource(R.drawable.checked);
				} else {
					iv_item_device.setBackgroundResource(R.drawable.check);
				}
			}
		} else {
			iv_item_device.setVisibility(View.GONE);
			iv_item_device.setBackgroundResource(R.drawable.check);
		}
		Devices devices = devicesList.get(i);
		if (devices != null) {
			viewHolder.mTextName.setText(devices.getName());
			if (devices.getLineStatus().equals("离线")) {
				viewHolder.mOnoffline.setText("离线");
				viewHolder.mHeard.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.carofflineimage));
			} else {
				viewHolder.mOnoffline.setText("在线");
				viewHolder.mHeard.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.carstaticimage));
			}
		}

		return convertView;
	}


	private void initClick(Devices bean, ImageView iv_item_device) {

		// 这个地方需从数据库获取，warn的值，再比较，
		Devices bean_ = helper.GetUser(bean.getIMEI());
		if (!bean_.getWarnNo().equals("1")) {
			iv_item_device.setBackgroundResource(R.drawable.check);
			helper.UpdateWarnValue("1", bean.getIMEI());
			checkWarnValue();
		} else {
			iv_item_device.setBackgroundResource(R.drawable.checked);
			// warn值更新为8
			helper.UpdateWarnValue("8", bean.getIMEI());
			checkWarnValue();
		}
		if (PubUtil.follow_bt_status == 101) {
			setDate(getOnlineDevices());
		} else if (PubUtil.follow_bt_status == 103) {
			setDate(Set_Order(GetFollowDevices()));
		} else if (PubUtil.follow_bt_status == 100) {
			setDate(Set_Order(helper.GetUserList(loadName)));
		}
	}

	private List<Devices> getOnlineDevices() {
		List<Devices> array = new ArrayList<Devices>();
		List<Devices> beans = helper.GetUserList(loadName);
		for (Devices bean : beans) {
			if (bean.getLineStatus().equals("在线")) {
				array.add(bean);
			}
		}
		return array;

	}

	private void checkWarnValue() {
		ArrayList<Devices> FinalArrayList = new ArrayList<Devices>();
		List<Devices> beans = helper.GetUserList(loadName);
		for (Devices array : beans) {
			if (array.getWarnNo().equals("8")) {
				FinalArrayList.add(array);
			}
		}
		if (PubUtil.follow_bt_status == 100) {// 如果全部列表被编辑，判断是否在线设备全部选中时比较的对象就是在线设备
			if (PubUtil.sizeOnLine == FinalArrayList.size()) {
				iv.setBackgroundResource(R.drawable.checked);
			} else {
				iv.setBackgroundResource(R.drawable.check);
			}
		} else {
			if (devicesList.size() == FinalArrayList.size()) {
				iv.setBackgroundResource(R.drawable.checked);
			} else {
				iv.setBackgroundResource(R.drawable.check);
			}
		}

	}

	private List<Devices> GetFollowDevices() {
		List<Devices> arrays = helper.GetUserList(loadName);
		List<Devices> arraysStatusSelected = new ArrayList<Devices>();
		for (int i = 0; i < arrays.size(); i++) {
			Devices bean = arrays.get(i);
			if (bean.getSelectStatus().equals("选中")) {
				arraysStatusSelected.add(bean);
			}
		}
		return arraysStatusSelected;
	}
	private List<Devices> Set_Order(List<Devices> arrays) {
		List<Devices> Aarray = new ArrayList<Devices>();
		List<Devices> Barray = new ArrayList<Devices>();
		for (int i = 0; i < arrays.size(); i++) {
			Devices bean = arrays.get(i);
			if (bean.getLineStatus().equals("在线")) {
				Aarray.add(bean);
			} else {
				Barray.add(bean);
			}
		}
		Aarray.addAll(Barray);
		arrays.clear();
		arrays.addAll(Aarray);
		return arrays;

	}

	static class ViewHolder {
		TextView mTextName;
		ImageView mHeard;
		TextView mOnoffline;

		ViewHolder(View view) {
			mTextName = (TextView) view.findViewById(R.id.device_name);
			mHeard = (ImageView) view.findViewById(R.id.heard_icon);
			mOnoffline = (TextView) view.findViewById(R.id.devices_onoffline);
		}
	}

	@Override
	public Filter getFilter() {
		if (null == mFilter) {
			mFilter = new MyFilter();
		}
		return mFilter;
	}

	// 自定义Filter类
	class MyFilter extends Filter {

		@Override
		// 该方法在子线程中执行
		// 自定义过滤规则
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			DebugLog.e("performFiltering=" + constraint);
			List<Devices> newValues = new ArrayList<Devices>();
			String filterString = constraint.toString().trim().toLowerCase();

			// 如果搜索框内容为空，就恢复原始数据
			if (TextUtils.isEmpty(filterString)) {
				newValues.clear();
				newValues.addAll(backList);
			} else {
				// 过滤出新数据
				for (Devices devices : backList) {
					DebugLog.e("devices.getName()=" + devices.getName());
					if (-1 != devices.getName().toLowerCase()
							.indexOf(filterString)) {
						newValues.add(devices);
					}
				}
			}

			results.values = newValues;
			results.count = newValues.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// devicesList = (List<Devices>) results.values;
			devicesList.clear();
			devicesList.addAll((List<Devices>) results.values);
			DebugLog.e("publishResults=" + results.count);
			if (results.count > 0) {
				// mHandle.sendEmptyMessage(10);
				DevicesListAdapter.this.notifyDataSetChanged(); // 通知数据发生了改变
			} else {
				// mHandle.sendEmptyMessage(11);
				DevicesListAdapter.this.notifyDataSetChanged(); // 通知数据发生了改变
				// DevicesListAdapter.this.notifyDataSetInvalidated(); // 通知数据失效
			}
		}
	}

}
