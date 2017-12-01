package com.mj.gpsclient.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mj.gpsclient.R;

/**
 * Created by majin on 15/5/30.
 */
public class segmentSelectView extends LinearLayout implements
        View.OnClickListener {

    private TextView segmentOne;

    private TextView segmentTwo;

    private TextView segmentThree;
    private TextView segmentFour;

    private TextView clickTextView;


    private SegmentSelectCallback mCallback;



    public segmentSelectView(Context context) {
        super(context);
        init();
    }

    public segmentSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.segment_select_view, this);
        segmentOne = (TextView) findViewById(R.id.segmentation_one);
        segmentTwo = (TextView) findViewById(R.id.segmentation_two);
        segmentThree = (TextView) findViewById(R.id.segmentation_three);
        segmentFour = (TextView) findViewById(R.id.segmentation_four);
        onClick(segmentOne);
        segmentOne.setOnClickListener(this);
        segmentTwo.setOnClickListener(this);
        segmentThree.setOnClickListener(this);
        segmentFour.setOnClickListener(this);

    }

    public void setData(String one,String two, String three,String four){
        segmentOne.setText("全部 "+one);
        segmentTwo.setText("在线 "+two);
        segmentThree.setText("离线 "+three);
        segmentFour.setText("跟踪 "+four);

    }

    @Override
    public void onClick(View view) {
        if(clickTextView!=null){
            clickTextView.setTextColor(Color.parseColor("#000000"));
        }
        clickTextView = (TextView) view;
        clickTextView.setTextColor(Color.parseColor("#ffffff"));
        switch (view.getId()){
            case R.id.segmentation_one:
                callDelegate(0);
                break;
            case R.id.segmentation_two:
                callDelegate(1);
                break;
            case R.id.segmentation_three:
                callDelegate(2);
                break;
            case R.id.segmentation_four:
                callDelegate(3);
                break;
        }

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
