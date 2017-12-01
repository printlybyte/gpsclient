package com.mj.gpsclient.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by majin on 15/5/30.
 */
public class SelectStartEndTimeView extends RelativeLayout implements
        View.OnClickListener {

    private TextView startTime;

    private TextView endTime;

    private TextView ok;


    private SelectStartEndTimeCallback mCallback;



    public SelectStartEndTimeView(Context context) {
        super(context);
        init();
    }

    public SelectStartEndTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.select_start_end_time, this);
        startTime = (TextView) findViewById(R.id.start_time_text_view);
        endTime = (TextView) findViewById(R.id.end_time_text_view);
        ok = (TextView) findViewById(R.id.ok_text_view);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        ok.setOnClickListener(this);

    }

    public void setData(String one,String two, String three){
//        segmentOne.setText("全部 "+one);
//        segmentTwo.setText("在线 "+two);
//        segmentThree.setText("离线 "+three);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.start_time_text_view:
                mCallback.onStartClick(view);
                break;
            case R.id.end_time_text_view:
                mCallback.onEndClick(view);
                break;
            case R.id.ok_text_view:
                Date dateStart=null;
                Date dateEnd=null;
                if(TextUtils.isEmpty(startTime.getText().toString())||TextUtils.isEmpty(endTime.getText().toString())){
                    mCallback.onOkClick(view,null,null);
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    dateStart = dateFormat.parse(startTime.getText().toString());
                } catch (ParseException e) {
                    dateStart =null;
                    e.printStackTrace();
                }
                try {
                    dateEnd = dateFormat.parse(endTime.getText().toString());
                } catch (ParseException e) {
                    dateEnd =null;
                    e.printStackTrace();
                }
                mCallback.onOkClick(view,dateStart,dateEnd);
                break;
        }

    }

    public void hide(){
        this.setVisibility(View.GONE);
    }

    public void show(){
        this.setVisibility(View.VISIBLE);
    }

    public void setCallback(SelectStartEndTimeCallback callback) {
        this.mCallback = callback;
    }

    public interface SelectStartEndTimeCallback {
       void onStartClick(View view);
       void onEndClick(View view);
       void onOkClick(View viewDate , Date dataStart, Date dateEnd);
    }


}
