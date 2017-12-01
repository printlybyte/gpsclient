package com.mj.gpsclient.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;
import com.mj.gpsclient.model.DevicePosition;

/**
 * Created by majin on 15/5/30.
 */
public class DevicesDetailView extends LinearLayout implements
        View.OnClickListener {

    private TextView TextName;
    private TextView TextOnTime;
    private TextView TextLocType;
    private TextView TextLocSpeed;
    private TextView TextLocDirection;
    private TextView TextDeviceStatus;
    private TextView TextBaseInfo;


    private SegmentSelectCallback mCallback;

    public DevicesDetailView(Context context) {
        super(context);
        init();
    }

    public DevicesDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.devices_detail_view, this);
        TextName = (TextView) findViewById(R.id.device_detail_Name);
        TextOnTime = (TextView) findViewById(R.id.device_detail_OnTime);
        TextLocType = (TextView) findViewById(R.id.device_detail_LocType);
        TextLocSpeed = (TextView) findViewById(R.id.device_detail_Speed);
        TextLocDirection = (TextView) findViewById(R.id.device_detail_Direction);
        TextDeviceStatus = (TextView) findViewById(R.id.device_detail_DeviceStatus);
        TextBaseInfo =(TextView) findViewById(R.id.device_detail_BaseInfo);
    }

    public void setData(DevicePosition devicePosition){
        TextName.setText(devicePosition.getName()!=null?"终端名称: "+devicePosition.getName():"终端名称 ");
        TextOnTime.setText(devicePosition.getOnTime()!=null?"在线时间: "+devicePosition.getOnTime():"在线时间 ");
        TextLocType.setText(devicePosition.getLocType()!=null?"定位类型 :"+devicePosition.getLocType():"定位类型 GPS");
        TextLocSpeed.setText(devicePosition.getSpeed()!=null?"速度: "+devicePosition.getSpeed():"速度 0.00");
        TextLocDirection.setText(devicePosition.getDirection()!=null?"方向: "+devicePosition.getDirection():"方向 正北");
        TextDeviceStatus.setText(devicePosition.getDeviceStatus()!=null?"设备状态: "+devicePosition.getDeviceStatus():"设备状态 无充电状态");
        TextBaseInfo.setText(devicePosition.getBaseInfo()!=null?"基站信息: "+devicePosition.getBaseInfo():"基站信息 460,0,35041,4585");
    }

    @Override
    public void onClick(View view) {

    }

    private void callDelegate(int index){
        if(mCallback!=null)
            mCallback.onSelect(index);
    }

    public void setCallback(SegmentSelectCallback callback) {
        this.mCallback = callback;
    }

    public interface SegmentSelectCallback {
        void onSelect(int index);
    }
}
