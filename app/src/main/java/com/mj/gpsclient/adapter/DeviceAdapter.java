package com.mj.gpsclient.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mj.gpsclient.Activity.PubUtil;
import com.mj.gpsclient.R;
import com.mj.gpsclient.Utils.PublicUtils;
import com.mj.gpsclient.global.DebugLog;
import com.mj.gpsclient.model.Devices;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends BaseAdapter implements Filterable {

    public List<Devices> array;
    public List<Devices> devicelist;
    public Context context;
    private LayoutInflater mLayoutInfalater;
    private MyFilter mFilter;
    private boolean selected;// 是否需要将iv_item_device控件显示
    private String Tag = "DeviceAdapter";


    public DeviceAdapter(Context Context) {

        this.context = Context;
        mLayoutInfalater = LayoutInflater.from(Context);

    }

    public void setOriginalData(List<Devices> List) {
        devicelist = List;
    }

    public void setData(List<Devices> List) {
        array = List;
        notifyDataSetChanged();

    }

    public void setLayout(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int getCount() {
        DebugLog.e("sgsfgdfgdf=");
        return array == null ? 0 : array.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return array.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final Devices bean = array.get(arg0);
        if (convertView == null) {
            convertView = mLayoutInfalater.inflate(R.layout.item_list_devices,
                    null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ImageView iv_item_device = (ImageView) convertView
                .findViewById(R.id.iv_item_device);
        if (selected) {// select为true，表示进入编辑状态
            // 跟踪列表选择编辑状态
            iv_item_device.setVisibility(View.VISIBLE);
            iv_item_device.setBackgroundResource(R.drawable.check);
        } else {
            iv_item_device.setVisibility(View.GONE);
            iv_item_device.setBackgroundResource(R.drawable.check);
        }
        if (!PubUtil.followHash.isEmpty()) {
            if (PubUtil.followHash.containsKey(bean.getIMEI())) {
                iv_item_device.setBackgroundResource(R.drawable.checked);

            } else {
                iv_item_device.setBackgroundResource(R.drawable.check);
            }
        }
        if (!PubUtil.split_sHash.isEmpty()) {
            if (PubUtil.split_sHash.containsKey(bean.getIMEI())) {
                iv_item_device.setBackgroundResource(R.drawable.checked);

            } else {
                iv_item_device.setBackgroundResource(R.drawable.check);
            }
        }
        if (!PubUtil.followHash2.isEmpty()) {
            if (PubUtil.followHash2.containsKey(bean.getIMEI())) {
                iv_item_device.setBackgroundResource(R.drawable.checked);

            } else {
                iv_item_device.setBackgroundResource(R.drawable.check);
            }
        }
        if (!PubUtil.split_sHash2.isEmpty()) {
            if (PubUtil.split_sHash2.containsKey(bean.getIMEI())) {
                iv_item_device.setBackgroundResource(R.drawable.checked);

            } else {
                iv_item_device.setBackgroundResource(R.drawable.check);
            }
        }
        Devices devices = array.get(arg0);
        if (devices != null) {
            viewHolder.mTextName.setText(devices.getName());
            if (devices.getLineStatus().equals("离线")) {
                viewHolder.mOnoffline.setText("离线");
                viewHolder.mHeard.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.carofflineimage));
            } else {
                viewHolder.mOnoffline.setText("在线");
                viewHolder.mHeard.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.carstaticimage));
            }
        }
        return convertView;
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
            Log.i(Tag, filterString + "++++++++++++");

            newValues.clear();
            // 如果搜索框内容为空，就恢复原始数据
            if (TextUtils.isEmpty(filterString)) {
                newValues.addAll(devicelist);
                Log.i(Tag, devicelist.size() + "---------------");
            } else {
                // 过滤出新数据
                for (Devices devices : devicelist) {
//					DebugLog.e("devices.getName()=" + devices.getName());
                    if (-1 != devices.getName().trim().toLowerCase()
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
            array.clear();
            array.addAll((List<Devices>) results.values);
            array =  PublicUtils.SetOrderForDevices(array);
            DebugLog.e("publishResults=" + results.count);
            notifyDataSetChanged();
        }
    }

}
