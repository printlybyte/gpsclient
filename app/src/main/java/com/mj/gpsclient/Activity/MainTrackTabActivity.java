package com.mj.gpsclient.Activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbActivityManager;
import com.mj.gpsclient.R;

public class MainTrackTabActivity extends TabActivity {
    private TabHost host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_track_tab);
        AbActivityManager.getInstance().addActivity(this);
        initHost();
    }

    private void initHost() {
        host = getTabHost();
        TabHost.TabSpec tab1 = host.newTabSpec("tab1");
        TabHost.TabSpec tab2 = host.newTabSpec("tab2");
        TabHost.TabSpec tab3 = host.newTabSpec("tab3");

        View view1 = LayoutInflater.from(this).inflate(R.layout.tab, null);
        final ImageView iv1 = (ImageView) view1.findViewById(R.id.image_iv);
        iv1.setBackgroundResource(R.drawable.tracking_old_press);
        final TextView tv1 = (TextView) view1.findViewById(R.id.textview_iv);
        tv1.setText("实时定位");
        tv1.setTextColor(getResources().getColor(R.color.blue_text));
        Intent intent1 = new Intent(this, FollowingAllTimeActivity.class);
        tab1.setIndicator(view1).setContent(intent1);
        host.addTab(tab1);

        View view2 = LayoutInflater.from(this).inflate(R.layout.tab, null);
        final ImageView iv2 = (ImageView) view2.findViewById(R.id.image_iv);
        iv2.setBackgroundResource(R.mipmap.nagative_no);
        final TextView tv2 = (TextView) view2.findViewById(R.id.textview_iv);
        tv2.setText("导航追踪");
        tv2.setTextColor(getResources().getColor(R.color.white));
        final Intent intent2 = new Intent(this, NavigationActivity.class);
        tab2.setIndicator(view2).setContent(intent2);
        host.addTab(tab2);


        View view3 = LayoutInflater.from(this).inflate(R.layout.tab, null);
        final ImageView iv3 = (ImageView) view3.findViewById(R.id.image_iv);
        iv3.setBackgroundResource(R.drawable.history_old);
        final TextView tv3 = (TextView) view3.findViewById(R.id.textview_iv);
        tv3.setText("历史回放");
        tv3.setTextColor(getResources().getColor(R.color.white));
        final Intent intent3 = new Intent(this, TrackHisPlayActivity.class);
        tab3.setIndicator(view3).setContent(intent3);
        host.addTab(tab3);



        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab1")) {
                    iv1.setBackgroundResource(R.drawable.tracking_old_press);
                    iv2.setBackgroundResource(R.mipmap.nagative_no);
                    iv3.setBackgroundResource(R.drawable.history_old);
                    tv1.setTextColor(getResources().getColor(R.color.blue_text));
                    tv2.setTextColor(getResources().getColor(R.color.white));
                    tv3.setTextColor(getResources().getColor(R.color.white));


                } else if (tabId.equals("tab2")) {
                    if (PubUtil.switchIsOn) {
                        Toast.makeText(MainTrackTabActivity.this, "请先关闭实时跟踪轨迹按钮", Toast.LENGTH_SHORT).show();
                        host.setCurrentTab(0);
                        return;
                    } else {
                        iv1.setBackgroundResource(R.drawable.tracking_older);
                        iv2.setBackgroundResource(R.mipmap.nagative_yes);
                        iv3.setBackgroundResource(R.drawable.history_old);
                        tv1.setTextColor(getResources().getColor(R.color.white));
                        tv2.setTextColor(getResources().getColor(R.color.blue_text));
                        tv3.setTextColor(getResources().getColor(R.color.white));
                    }

                }else if (tabId.equals("tab3")) {
                    iv1.setBackgroundResource(R.drawable.tracking_older);
                    iv2.setBackgroundResource(R.mipmap.nagative_no);
                    iv3.setBackgroundResource(R.drawable.history_old_press);
                    tv1.setTextColor(getResources().getColor(R.color.white));
                    tv2.setTextColor(getResources().getColor(R.color.white));
                    tv3.setTextColor(getResources().getColor(R.color.blue_text));
                }
            }
        });

    }
}
