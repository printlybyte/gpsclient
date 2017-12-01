package com.mj.gpsclient.adapter;

import java.util.ArrayList;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.mj.gpsclient.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AllCitysAdapter extends BaseAdapter {

	
	Context context;
	ArrayList<MKOLSearchRecord> arrays;
	private ViewHolder viewHolder;
	public AllCitysAdapter(Context context) {
		this.context = context;
	}
	
	public void setData(ArrayList<MKOLSearchRecord> arrays) {
		this.arrays = arrays;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrays==null?0:arrays.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arrays.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		
		if(view==null){
			 viewHolder = new ViewHolder();
			view=LayoutInflater.from(context).inflate(R.layout.citys_item, null);
			viewHolder.city_name_tv = (TextView) view.findViewById(R.id.city_name_tv);
			viewHolder.isdownload_tv = (TextView) view.findViewById(R.id.isdownload_tv);
			viewHolder.city_size_tv = (TextView) view.findViewById(R.id.city_size_tv);
			viewHolder.isdownload_iv = (ImageView) view.findViewById(R.id.isdownload_iv);
			view.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) view.getTag();
		}
		MKOLSearchRecord bean = arrays.get(arg0);
		viewHolder.city_name_tv.setText(bean.cityName);
		viewHolder.city_size_tv.setText(formatDataSize(bean.size));
		
		return view;
	}
class ViewHolder{
	private TextView city_name_tv;
	private TextView isdownload_tv;
	private TextView city_size_tv;
	private ImageView isdownload_iv;
	
	
}
public String formatDataSize(int size) {
    String ret = "";
    if (size < (1024 * 1024)) {
        ret = String.format("%dK", size / 1024);
    } else {
        ret = String.format("%.1fM", size / (1024 * 1024.0));
    }
    return ret;
}

}
