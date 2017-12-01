package com.mj.gpsclient.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;
import com.mj.gpsclient.model.DeviceLocation;

/**
 * Created by majin on 15/5/30.
 */
public class HostoryDetailView extends LinearLayout{

    private TextView time;
    private TextView location;
    public HostoryDetailView(Context context) {
        super(context);
        init();
    }

    public HostoryDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.loaction_detail_view, this);
        time = (TextView) findViewById(R.id.location_time);
        location = (TextView) findViewById(R.id.location_msg);


    }

    public void setData(DeviceLocation deviceLocation){
        //   android:text="2015-06-14 15:41:09"
        time.setText(" "+deviceLocation.getOnTime());
//        android:text="维度:39.845604 经度:116.359906 速度:19.00KM/H"
        location.setText(" 维度:"+deviceLocation.getLatitude() +"  经度:"+deviceLocation.getLongitude()+ " 速度:19.00KM/H");

    }

}
