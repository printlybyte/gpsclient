package com.mj.gpsclient.Activity;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.model.AbMenuItem;
import com.ab.view.sample.AbViewPager;
import com.ab.view.sliding.AbBottomTabView;
import com.ab.view.titlebar.AbTitleBar;
import com.mj.gpsclient.R;
import com.mj.gpsclient.adapter.ListPopAdapter;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.DebugLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DevicesTrackActivity extends AbActivity {

    public AlertDialog.Builder builder;
    private List<Drawable> tabDrawables = null;
    private AbBottomTabView mBottomTabView;
    private String DeviceName;
    private String DeviceStuts;
    private AbTitleBar mAbTitleBar;
    //显示地图的fragment
    private TrackRealtimeFragment trackRealtimeFragment;
    private TrackHistoryFragment trackHistoryFragment;
    private TrackFenceFragment trackFenceFragment;
    private TrackAlarmListFragment trackAlarmListFragment;
    private View popView;
    private TextView playProgress;
    private ImageView playOrPause;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
        setAbContentView(R.layout.activity_devices_track);
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("实时追踪");
        mAbTitleBar.setLogo(R.drawable.back_n);
        mAbTitleBar.setTitleBarBackground(R.drawable.tab_top_bg);
        mAbTitleBar.setTitleTextMargin(0, 0, 0, 0);
        mAbTitleBar.getTitleTextLayout().setGravity(
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        mAbTitleBar.setLogoLine(R.drawable.line);
        mAbTitleBar.getTitleTextButton().setGravity(
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        mAbTitleBar.getLogoView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Constant.REALTIME_SWITCH == 0) {
                            DevicesTrackActivity.this.finish();
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(
                                    DevicesTrackActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("退出本窗口后将关闭实时追踪，是否继续？");
                            builder.setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // 关闭实时追踪并退出
                                            trackRealtimeFragment.endTimer();
                                            DevicesTrackActivity.this.finish();
                                        }
                                    });
                            builder.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                        }

                    }
                });

        //在当前实时定位中屏蔽back键

        mAbTitleBar.getLogoView().setClickable(false);
        TimerTask task = new TimerTask() {
            public void run() {
                //execute the task
                //在这里添加变更控件是否可用的操作
                mAbTitleBar.getLogoView().setClickable(true);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 2000);
        // 从这里开始是实时定位的部分
        View rightViewtest = mInflater.inflate(R.layout.test_moni, null);
        final Switch aSwitch = (Switch) rightViewtest.findViewById(R.id.switch_test);
        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // if(!isChecked){
                aSwitch.setClickable(false);
                TimerTask task = new TimerTask() {
                    public void run() {
                        //execute the task
                        //在这里添加变更控件是否可用的操作
                        aSwitch.setClickable(true);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
                Message msg = Message.obtain();
                msg.what = trackRealtimeFragment.MAP_AUTO_TRACK_SWITCH;
                msg.arg1 = (isChecked ? 1 : 0);
                trackRealtimeFragment.mHandler.sendMessage(msg);
                // }

            }
        });
        mAbTitleBar.addRightView(rightViewtest);
        mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
        initTitleRightLayout();
        // setTitleMargin();

        mBottomTabView = (AbBottomTabView) findViewById(R.id.device_track_BottomTabView);

        // 如果里面的页面列表不能下载原因：
        // Fragment里面用的AbTaskQueue,由于有多个tab，顺序下载有延迟，还没下载好就被缓存了。改成用AbTaskPool，就ok了。
        // 或者setOffscreenPageLimit(0)
        // 缓存数量
        mBottomTabView.getViewPager().setOffscreenPageLimit(4);

        ((AbViewPager) mBottomTabView.getViewPager()).setPagingEnabled(false);
        // mBottomTabView.getViewPager().setEnabled(false);
        // 禁止滑动
        mBottomTabView.getViewPager().setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }

                });

        mBottomTabView
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int i, float v, int i2) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        setBarTitle(i);
                        // 通知轨迹界面停止
                        if (i != 1) {
                            trackHistoryFragment.mHandler
                                    .sendEmptyMessageDelayed(6, 0);
                            trackHistoryFragment.mHandler
                                    .sendEmptyMessageDelayed(9, 0);
                        }
                        if (i != 0) {
                            // 暂停实时监控的界面
                            trackRealtimeFragment.mHandler
                                    .sendEmptyMessage(TrackRealtimeFragment.MAP_PAUSE);
                        }
                        if (i == 0) {
                            // 获取一次数据
                            trackRealtimeFragment.mHandler
                                    .sendEmptyMessage(TrackRealtimeFragment.GET_DEVICE_DATA);
                        }

                        currentIndex = i;
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });

        trackRealtimeFragment = new TrackRealtimeFragment();
        trackHistoryFragment = new TrackHistoryFragment();
        trackFenceFragment = new TrackFenceFragment();
        trackAlarmListFragment = new TrackAlarmListFragment();

        List<Fragment> mFragments = new ArrayList<Fragment>();
        mFragments.add(trackRealtimeFragment);
        mFragments.add(trackHistoryFragment);
        // mFragments.add(trackFenceFragment);
        // mFragments.add(trackAlarmListFragment);

        List<String> tabTexts = new ArrayList<String>();
        tabTexts.add("实时追踪");
        tabTexts.add("历史回放");
        // tabTexts.add("电子围栏");
        // tabTexts.add("车辆报警");

        // 设置样式
        mBottomTabView.setTabTextColor(Color.rgb(255, 255, 255));
        mBottomTabView.setTabSelectColor(getResources().getColor(R.color.aaa));
        mBottomTabView.setTabTextSize(20);
        // mBottomTabView.setTabBackgroundResource(R.drawable.tab_bg2);
        mBottomTabView.setTabBackgroundResource(R.drawable.tab_home_bg);
        mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tab_home_bg);

        // 注意图片的顺序
        tabDrawables = new ArrayList<Drawable>();
        tabDrawables.add(this.getResources().getDrawable(
                R.drawable.tracking_older));
        tabDrawables.add(this.getResources().getDrawable(
                R.drawable.tracking_old_press));
        tabDrawables.add(this.getResources()
                .getDrawable(R.drawable.history_old));
        tabDrawables.add(this.getResources().getDrawable(
                R.drawable.history_old_press));
        // tabDrawables.add(this.getResources().getDrawable(R.drawable.geofence_old));
        // tabDrawables.add(this.getResources().getDrawable(R.drawable.geofence_old_press));
        // tabDrawables.add(this.getResources().getDrawable(R.drawable.more_old));
        // tabDrawables.add(this.getResources().getDrawable(R.drawable.more_old_press));
        mBottomTabView.setTabCompoundDrawablesBounds(0, 0, 50, 50);
        // 演示增加一组
        mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

        mBottomTabView.setTabPadding(10, 10, 10, 10);

    }


    @Override
    public void onBackPressed() {
        if (Constant.REALTIME_SWITCH == 0) {
            DevicesTrackActivity.this.finish();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    DevicesTrackActivity.this);
            builder.setTitle("提示");
            builder.setMessage("退出本窗口后将关闭实时追踪，是否继续？");
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            // 关闭实时追踪并退出
                            trackRealtimeFragment.endTimer();
                            DevicesTrackActivity.this.finish();
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }

    }

    private void initDatas() {
        Intent in = this.getIntent();
        DeviceName = in.getStringExtra("memberName");
        DeviceStuts = in.getStringExtra("memberStuts");
        currentIndex = 0;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getDeviceStuts() {
        return DeviceStuts;
    }

    public void setBarTitle(int i) {
        mAbTitleBar.clearRightView();
        mAbTitleBar.setTitleTextSize(20);
        Drawable d = null;// getApplication().getResources().getDrawable(R.color.photo_tab_red);
        switch (i) {
            case 0:

                mAbTitleBar.setTitleTextBackgroundDrawable(d);
                mAbTitleBar.setTitleText("实时追踪");
                mAbTitleBar
                        .setBackgroundColor(getResources().getColor(R.color.aaa));
                mAbTitleBar.setTitleTextOnClickListener(null);
                trackRealtimeFragment.mHandler.sendEmptyMessageDelayed(3, 500);
                View rightViewtest = mInflater.inflate(R.layout.test_moni, null);
                Switch aSwitch = (Switch) rightViewtest.findViewById(R.id.switch_test);
                aSwitch.setChecked(false);
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (!isChecked) {
                            trackRealtimeFragment.mHandler.sendEmptyMessage(100);
                        }

                    }
                });
                mAbTitleBar.addRightView(rightViewtest);
                mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
                // setTitleMargin();

                break;
            case 1:
                Log.e("rrr", "3");
                mAbTitleBar.setTitleText("请选择");
                mAbTitleBar.setTitleTextSize(20);
                setListTitle();

                View rightViewProgress = mInflater.inflate(R.layout.step_progress,
                        null);
                playProgress = (TextView) rightViewProgress
                        .findViewById(R.id.play_progress);
                mAbTitleBar.addRightView(rightViewProgress);
                mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);

                View rightViewApp = mInflater.inflate(R.layout.more_btn, null);
                playOrPause = (ImageView) rightViewApp
                        .findViewById(R.id.play_pause);
                playOrPause.setTag(R.drawable.pause_binoculars);
                playOrPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // trackHistoryFragment.mHandler.sendEmptyMessage(9);
                        if ((Integer) v.getTag() == R.drawable.pause_binoculars) {
                            playOrPause.setImageDrawable(abApplication
                                    .getResources().getDrawable(
                                            R.drawable.play_binoculars));
                            playOrPause.setTag(R.drawable.play_binoculars);
                        } else {
                            playOrPause.setImageDrawable(abApplication
                                    .getResources().getDrawable(
                                            R.drawable.pause_binoculars));
                            playOrPause.setTag(R.drawable.pause_binoculars);
                            trackHistoryFragment.mHandler.sendEmptyMessage(8);
                        }
                    }
                });
                mAbTitleBar.addRightView(rightViewApp);
                mAbTitleBar.setTitleBarGravity(Gravity.LEFT, Gravity.CENTER);
                // trackHistoryFragment.mHandler.sendEmptyMessageDelayed(3,500);
                break;
            case 2:
                mAbTitleBar.setTitleTextBackgroundDrawable(d);
                mAbTitleBar.setTitleText("电子围墙");
                mAbTitleBar.setTitleTextBackgroundDrawable(d);
                mAbTitleBar.setTitleTextOnClickListener(null);
                View rightViewApp3 = mInflater.inflate(R.layout.refresh_btn, null);
                ImageView img2 = (ImageView) rightViewApp3
                        .findViewById(R.id.bu_refresh);
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                mAbTitleBar.addRightView(rightViewApp3);
                mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
                rightViewApp3.setVisibility(View.INVISIBLE);
                // setTitleMargin();

                break;
            case 3:
                mAbTitleBar.setTitleText("车辆报警");
                mAbTitleBar.setTitleTextBackgroundDrawable(d);
                mAbTitleBar.setTitleTextOnClickListener(null);
                View rightViewApp2 = mInflater.inflate(R.layout.refresh_btn, null);
                ImageView img = (ImageView) rightViewApp2
                        .findViewById(R.id.bu_refresh);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                mAbTitleBar.addRightView(rightViewApp2);
                mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
                // setTitleMargin();
                break;
            default:
                break;
        }

    }

    private void setTitleMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAbTitleBar
                .getTitleTextLayout().getLayoutParams();
        int myLeft = mAbTitleBar.getTitleTextLayout().getLeft();
        int myWidth = mAbTitleBar.getTitleTextLayout().getWidth();
        int cLeft = mAbTitleBar.getWidth() / 2;
        int dd = cLeft - myLeft;
        if (dd > 0) {
            params.leftMargin += Math.abs(dd);
        } else {
            params.rightMargin += Math.abs(dd);
        }
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        DebugLog.e("cLeft=" + cLeft + " myLeft =" + myLeft + "   dd =" + dd);
        // mAbTitleBar.getTitleTextLayout().setLeft(cLeft);
        mAbTitleBar.getTitleTextLayout().setLayoutParams(params);
    }

    public void setListTitle() {
        mAbTitleBar
                .setTitleTextBackgroundResource(R.drawable.drop_down_title_btn);
        mAbTitleBar.setBackgroundColor(getResources().getColor(R.color.aaa));
        popView = mInflater.inflate(R.layout.list_pop, null);
        ListView popListView = (ListView) popView.findViewById(R.id.pop_list);
        List<AbMenuItem> list = new ArrayList<AbMenuItem>();
        list.add(new AbMenuItem("当天"));
        list.add(new AbMenuItem("过去3天"));
        list.add(new AbMenuItem("过去一周"));
        list.add(new AbMenuItem("自定义"));
        ListPopAdapter mListPopAdapter = new ListPopAdapter(
                DevicesTrackActivity.this, list, R.layout.item_list_pop);
        popListView.setAdapter(mListPopAdapter);
        mAbTitleBar.setTitleTextDropDown(popView);
        popListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int i, long l) {
                        switch (i) {
                            case 0:
                                mAbTitleBar.setTitleText("当天");
                                break;
                            case 1:
                                mAbTitleBar.setTitleText("过去3天");
                                break;
                            case 2:
                                mAbTitleBar.setTitleText("过去一周");
                                break;
                            case 3:
                                mAbTitleBar.setTitleText("自定义");
                                break;
                            default:
                                break;
                        }
                        mAbTitleBar.hideWindow();
                        // mAbTitleBar.setTitleTextBackgroundDrawable(null);
                        // mAbTitleBar.setTitleTextOnClickListener(null);
                        changeHistoryTime(i);
                    }
                });

        mAbTitleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = Message.obtain();
                msg.what = 5;
                msg.arg1 = -1;
                trackHistoryFragment.mHandler.sendEmptyMessage(5);
            }
        });
        mAbTitleBar.setTitleTextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!popView.isShown()) {
                    mAbTitleBar.showWindow(view, popView, true);
                } else {
                    mAbTitleBar.hideWindow();
                }
            }
        });

    }

    public void setStepProgress(String text) {
        if (null != playProgress) {
            playProgress.setText(text);
        }

    }

    public void setPlayOrPauseEnable(boolean isEnable) {
        if (null != playOrPause) {
            // playProgress.setText(text);
            playOrPause.setEnabled(isEnable);
        }

    }

    public void setPause(boolean isEnd) {
        if (isEnd) {
            playOrPause.setImageDrawable(abApplication.getResources()
                    .getDrawable(R.drawable.play_binoculars));
            playOrPause.setTag(R.drawable.play_binoculars);
        }
    }

    public boolean isPauseStep() {
        return (playOrPause != null && (Integer) playOrPause.getTag() != R.drawable.pause_binoculars);
    }

    public int getCurrentIndexFragmentIndex() {

        return currentIndex;
    }

    public boolean isRealtimeMode() {
        //	aSwitch != null && aSwitch.isChecked()
        return true;
    }

    private void togglePopView() {
        if (popView.isShown()) {

        } else {

        }
    }

    private void changeHistoryTime(int index) {
        Message msg = Message.obtain();
        msg.what = 4;
        msg.arg1 = index;
        trackHistoryFragment.mHandler.sendMessage(msg);

    }

    private void initData() {
        Intent in = this.getIntent();
        String model = in.getStringExtra("model");
        if (TextUtils.isEmpty(model)) {
            return;
        }

    }

    /**
     * 实时追踪中的导航模式
     */
    public void toNavigationMode(String title) {
        //aSwitch.setChecked(false);
        // 隐藏那个右侧的实时导航开关
        mAbTitleBar.getRightLayout().setVisibility(View.INVISIBLE);
        mAbTitleBar.setTitleText(title);
        mAbTitleBar.setBackgroundColor(getResources().getColor(R.color.aaa));

    }

    /**
     * 退出导航模式
     */
    public void exitNavigationMode() {
        mAbTitleBar.setTitleText("实时追踪");
        mAbTitleBar.getRightLayout().setVisibility(View.VISIBLE);
    }

    public boolean isRealTimeModle() {
        return mAbTitleBar.getTitleTextButton().getText().equals("实时追踪");

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    protected void onPause() {
        super.onPause();
        if (trackHistoryFragment.mHandler != null) {
            trackHistoryFragment.mHandler.sendEmptyMessage(9);
        }

    }

    protected void onStop() {
        super.onPause();
        if (trackHistoryFragment.mHandler != null) {
            trackHistoryFragment.mHandler.sendEmptyMessage(9);
        }
    }

    private void initTitleRightLayout() {

    }

}
