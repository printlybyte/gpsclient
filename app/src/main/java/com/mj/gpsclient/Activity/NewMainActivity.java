package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.ab.global.AbActivityManager;
import com.ab.util.AbToastUtil;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.mj.gpsclient.R;
import com.mj.gpsclient.Utils.PublicUtils;
import com.mj.gpsclient.adapter.DeviceAdapter;
import com.mj.gpsclient.db.DataFollowHelper;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.model.Devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;

public class NewMainActivity extends Activity implements
        android.view.View.OnClickListener {

    private ImageView fresh_item1_iv;
    private ImageView add_item1_pop_iv;
    private RelativeLayout search_item1_rl;
    private RelativeLayout follow_item1_dev_rl;
    private RelativeLayout split_item1_screen_rl;
    private RelativeLayout search_item2_rl;
    private RelativeLayout follow_item2_dev_rl;
    private RelativeLayout split_item2_screen_rl;
    private LinearLayout deviceList_ll;
    private LinearLayout followList_ll;
    private LinearLayout map_show_ll;
    private LinearLayout more_ll;
    private ImageView deviceList_iv;
    private TextView deviceList_tv;
    private ImageView followList_iv;
    private TextView followList_tv;
    private ImageView map_show_iv;
    private TextView map_show_tv;
    private ImageView more_iv;
    private TextView more_tv;
    private MyApplication application;
    private List<Devices> allDeviceList = new ArrayList<Devices>();
    private ListView devices_item1_listView;
    private ListView devices_item2_listView;
    private DeviceAdapter adapter;
    private PopupWindow pop;
    private boolean isExit = false;
    private EditText search_item1_et;
    private View view1;
    private View view2;
    private View view3;
    private View view4;
    private List<View> views;
    String tag = "NewMainActivity";



    private DataFollowHelper helper;
    private RequestQueue mRequestQueue;
    private ArrayList<Devices> deviceFollowList;
    private Timer timer;

    private Timer timer_online;
    private RelativeLayout reset_pwd;
    private RelativeLayout logout;
    private TextView follow_pop_tv;
    private boolean istimer_online = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_main);
        AbActivityManager.getInstance().addActivity(this);
        helper = new DataFollowHelper(this);
        application = (MyApplication) this.getApplication();
        loadView();
        findView();
        initMap();
        setAdapter();
        setClickListener();
        adapter = new DeviceAdapter(this);
        devices_item1_listView.setAdapter(adapter);
        devices_item2_listView.setAdapter(adapter);
        getRequestQueue();

        loadDataForPage1();
        ListViewOnClick();
        ListViewOnClick2();



    }





    private void ListViewOnClick2() {
        devices_item2_listView
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int arg2, long arg3) {
                        Devices bean = deviceFollowList.get(arg2);
                        if (PubUtil.follow_iv_pressed2) {// page2删除按钮被点击
                            if (PubUtil.followHash2.containsKey(bean.getIMEI())) {
                                PubUtil.followHash2.remove(bean.getIMEI());
                                adapter.notifyDataSetChanged();
                            } else {
                                PubUtil.followHash2.put(bean.getIMEI(), bean);
                                adapter.notifyDataSetChanged();
                            }

                            if (TheNumIsTheSame2(PubUtil.followHash2) == 1) {
                                follow_item2_all_iv
                                        .setBackgroundResource(R.drawable.checked);
                            } else if (TheNumIsTheSame2(PubUtil.followHash2) == 2) {
                                follow_item2_all_iv
                                        .setBackgroundResource(R.drawable.check);
                            }

                        } else if (PubUtil.split_screen_iv2) {// page2分屏按钮被点击
                            if (PubUtil.split_sHash2.containsKey(bean.getIMEI())) {
                                PubUtil.split_sHash2.remove(bean.getIMEI());
                                adapter.notifyDataSetChanged();
                            } else {
                                PubUtil.split_sHash2.put(bean.getIMEI(), bean);
                                adapter.notifyDataSetChanged();
                            }
                            if (TheNumIsTheSame2(PubUtil.split_sHash2) == 1) {
                                split_item2_all_iv
                                        .setBackgroundResource(R.drawable.checked);
                            } else if (TheNumIsTheSame(PubUtil.split_sHash2) == 2) {
                                split_item2_all_iv
                                        .setBackgroundResource(R.drawable.check);
                            }
                        } else {
                            Intent intent = new Intent(NewMainActivity.this,
                                    FollowingActivity.class);
                            intent.putExtra("memberName", bean.getName());
                            intent.putExtra("memberImei", bean.getIMEI());
                            intent.putExtra("lat", bean.getLat());
                            intent.putExtra("lng", bean.getLng());
                            NewMainActivity.this.startActivity(intent);
                        }

                    }
                });

    }

    private void ListViewOnClick() {
        devices_item1_listView
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                                            View view, int arg2, long arg3) {
                        Devices bean = allDeviceList.get(arg2);
                        if (PubUtil.split_screen_iv) {// 分屏显示按钮被点击
                            if (bean.getLineStatus().equals("离线")) {
                                Toast.makeText(NewMainActivity.this,
                                        "离线设备不可分屏展示", Toast.LENGTH_LONG).show();
                            } else {
                                if (PubUtil.split_sHash.containsKey(bean
                                        .getIMEI())) {
                                    PubUtil.split_sHash.remove(bean.getIMEI());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    PubUtil.split_sHash.put(bean.getIMEI(),
                                            bean);
                                    adapter.notifyDataSetChanged();
                                }
                                if (TheNumIsTheSame(PubUtil.split_sHash) == 1) {
                                    split_item1_all_iv
                                            .setBackgroundResource(R.drawable.checked);
                                } else if (TheNumIsTheSame(PubUtil.split_sHash) == 2) {
                                    split_item1_all_iv
                                            .setBackgroundResource(R.drawable.check);
                                }
                            }
                        } else if (PubUtil.follow_iv_pressed) {// 后台跟踪按钮被点击
                            if (bean.getLineStatus().equals("离线")) {
                                Toast.makeText(NewMainActivity.this,
                                        "离线设备不可添加后台跟踪", Toast.LENGTH_LONG).show();
                            } else {
                                if (PubUtil.followHash.containsKey(bean
                                        .getIMEI())) {
                                    PubUtil.followHash.remove(bean.getIMEI());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    PubUtil.followHash.put(bean.getIMEI(), bean);
                                    adapter.notifyDataSetChanged();
                                }

                                if (TheNumIsTheSame(PubUtil.followHash) == 1) {
                                    follow_item1_all_iv
                                            .setBackgroundResource(R.drawable.checked);
                                } else if (TheNumIsTheSame(PubUtil.followHash) == 2) {
                                    follow_item1_all_iv
                                            .setBackgroundResource(R.drawable.check);
                                }

                            }

                        } else {
                            PubUtil.bean = bean;
                            Intent intent = new Intent(NewMainActivity.this,
                                    MainTrackTabActivity.class);
                            NewMainActivity.this.startActivity(intent);
                        }
                    }
                });

    }

    private int TheNumIsTheSame2(HashMap<String, Devices> hashmap) {
        List<Devices> array = new ArrayList<Devices>();
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        for (Devices bean : arrays) {
            if (bean.getSelectStatus().equals("选中")) {
                array.add(bean);
            }
        }

        int allNum = array.size();
        int hashNum = hashmap.size();
        if (allNum != 0) {
            if (hashNum == allNum) {
                return 1;//
            } else {
                return 2;
            }
        } else {
            return 3;
        }

    }

    private List<Devices> getOnLineDevices() {
        List<Devices> deviceOnline = new ArrayList<Devices>();
        for (Devices bean : allDeviceList) {
            if (bean.getLineStatus().equals("在线")) {
                deviceOnline.add(bean);
            }
        }
        return deviceOnline;
    }

    private int TheNumIsTheSame(HashMap<String, Devices> hashmap) {
        List<Devices> deviceOnline = new ArrayList<Devices>();
        for (Devices bean : allDeviceList) {
            if (bean.getLineStatus().equals("在线")) {
                deviceOnline.add(bean);
            }
        }
        int allNum = deviceOnline.size();
        int hashNum = hashmap.size();
        if (allNum != 0) {
            if (hashNum == allNum) {
                return 1;//
            } else {
                return 2;
            }
        } else {
            return 3;
        }

    }

    private void findView() {
        download_rl = (RelativeLayout) view4.findViewById(R.id.download_rl);
        reset_pwd = (RelativeLayout) view4.findViewById(R.id.reset_pwd_ll);
        logout = (RelativeLayout) view4.findViewById(R.id.logout_ll);
        // page3
        mMapView = (TextureMapView) view3.findViewById(R.id.map_display);
        mBaiduMap = mMapView.getMap();

        follow_item2_ll = (LinearLayout) view2
                .findViewById(R.id.follow_item2_ll);
        add_item2_pop_iv = (ImageView) view2
                .findViewById(R.id.add_item2_pop_iv);
        fresh_item2_iv = (ImageView) view2.findViewById(R.id.fresh_item2_iv);
        split_item2_ll = (LinearLayout) view2.findViewById(R.id.split_item2_ll);
        split_item1_ll = (LinearLayout) view1.findViewById(R.id.split_item1_ll);
        follow_item1_ll = (LinearLayout) view1
                .findViewById(R.id.follow_item1_ll);
        follow_item1_sure_tv = (TextView) view1
                .findViewById(R.id.follow_item1_sure_tv);
        follow_item2_sure_tv = (TextView) view2
                .findViewById(R.id.follow_item2_sure_tv);
        split_item2_sure_tv = (TextView) view2
                .findViewById(R.id.split_item2_sure_tv);
        split_item1_sure_tv = (TextView) view1
                .findViewById(R.id.split_item1_sure_tv);
        split_item2_all_iv = (ImageView) view2
                .findViewById(R.id.split_item2_all_iv);
        split_item1_all_iv = (ImageView) view1
                .findViewById(R.id.split_item1_all_iv);
        follow_item1_all_iv = (ImageView) view1
                .findViewById(R.id.follow_item1_all_iv);
        follow_item2_all_iv = (ImageView) view2
                .findViewById(R.id.follow_item2_all_iv);
        deviceList_ll = (LinearLayout) findViewById(R.id.deviceList_ll);
        followList_ll = (LinearLayout) findViewById(R.id.followList_ll);
        map_show_ll = (LinearLayout) findViewById(R.id.map_show_ll);
        more_ll = (LinearLayout) findViewById(R.id.more_ll);
        deviceList_iv = (ImageView) findViewById(R.id.deviceList_iv);
        deviceList_tv = (TextView) findViewById(R.id.deviceList_tv);
        followList_iv = (ImageView) findViewById(R.id.followList_iv);
        followList_tv = (TextView) findViewById(R.id.followList_tv);
        map_show_iv = (ImageView) findViewById(R.id.map_show_iv);
        map_show_tv = (TextView) findViewById(R.id.map_show_tv);
        more_iv = (ImageView) findViewById(R.id.more_iv);
        more_tv = (TextView) findViewById(R.id.more_tv);
        viewPage = (MyViewPage) findViewById(R.id.tabpager);
        viewPage.setNoScroll(true);
        viewPage.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    default:break;
                    case 0:
                        CheckLayoutStatus();
                        mHandler.sendEmptyMessage(6);
                        mMapView.onPause();
                        mMapView.setVisibility(View.INVISIBLE);
                        PubUtil.PageNum = 1;
                        loadDataForPage1();

                        deviceList_iv
                                .setBackgroundResource(R.drawable.tab_leftbt_press);
                        deviceList_tv.setTextColor(getResources().getColor(
                                R.color.aaa));
                        followList_iv.setBackgroundResource(R.drawable.follow_iv);
                        followList_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        map_show_iv.setBackgroundResource(R.drawable.home_old);
                        map_show_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        more_iv.setBackgroundResource(R.drawable.carsmonitor_old);
                        more_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        break;
                    case 1:
                        CheckLayoutStatus();
                        mHandler.sendEmptyMessage(6);
                        mMapView.setVisibility(View.INVISIBLE);
                        mMapView.onPause();
                        PubUtil.PageNum = 2;
                        deviceFollowList = getDataForAdapter();
                        adapter.setData(deviceFollowList);

                        followList_iv
                                .setBackgroundResource(R.drawable.follow_iv_pressed);
                        followList_tv.setTextColor(getResources().getColor(
                                R.color.aaa));
                        deviceList_iv.setBackgroundResource(R.drawable.tab_leftbt);
                        deviceList_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        map_show_iv.setBackgroundResource(R.drawable.home_old);
                        map_show_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        more_iv.setBackgroundResource(R.drawable.carsmonitor_old);
                        more_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));

                        break;
                    case 2:
                        if (!PubUtil.isConnected(NewMainActivity.this)) {
                            Toast.makeText(NewMainActivity.this, "网络异常，稍后再试", Toast.LENGTH_LONG).show();
                        }
                        mHandler.sendEmptyMessage(10);
                        mMapView.setVisibility(View.VISIBLE);
                        mMapView.onResume();
                        mBaiduMap.clear();
                        PubUtil.PageNum = 3;
                        mHandler.sendEmptyMessage(5);
                        CheckLayoutStatus();
                        map_show_iv
                                .setBackgroundResource(R.drawable.map_show_pressed);
                        map_show_tv.setTextColor(getResources().getColor(
                                R.color.aaa));
                        deviceList_iv.setBackgroundResource(R.drawable.tab_leftbt);
                        deviceList_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        followList_iv.setBackgroundResource(R.drawable.follow_iv);
                        followList_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        more_iv.setBackgroundResource(R.drawable.carsmonitor_old);
                        more_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));

                        break;
                    case 3:
                        mHandler.sendEmptyMessage(6);
                        mHandler.sendEmptyMessage(10);
                        mMapView.setVisibility(View.INVISIBLE);
                        mMapView.onPause();
                        PubUtil.PageNum = 4;
                        CheckLayoutStatus();
                        more_iv.setBackgroundResource(R.drawable.carsmonitor_old_press);
                        more_tv.setTextColor(getResources().getColor(R.color.aaa));
                        deviceList_iv.setBackgroundResource(R.drawable.tab_leftbt);
                        deviceList_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        followList_iv.setBackgroundResource(R.drawable.follow_iv);
                        followList_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        map_show_iv.setBackgroundResource(R.drawable.home_old);
                        map_show_tv.setTextColor(getResources().getColor(
                                R.color.background_list_front));
                        break;
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        fresh_item1_iv = (ImageView) view1.findViewById(R.id.fresh_item1_iv);
        add_item1_pop_iv = (ImageView) view1
                .findViewById(R.id.add_item1_pop_iv);
        search_item1_rl = (RelativeLayout) view1
                .findViewById(R.id.search_item1_rl);
        follow_item1_dev_rl = (RelativeLayout) view1
                .findViewById(R.id.follow_item1_dev_rl);
        split_item1_screen_rl = (RelativeLayout) view1
                .findViewById(R.id.split_item1_screen_rl);

        search_item2_rl = (RelativeLayout) view2
                .findViewById(R.id.search_item2_rl);
        follow_item2_dev_rl = (RelativeLayout) view2
                .findViewById(R.id.follow_item2_dev_rl);
        split_item2_screen_rl = (RelativeLayout) view2
                .findViewById(R.id.split_item2_screen_rl);

        devices_item1_listView = (ListView) view1
                .findViewById(R.id.devices_item1_listView);
        devices_item2_listView = (ListView) view2
                .findViewById(R.id.devices_item2_listView);
        search_item2_et = (EditText) view2.findViewById(R.id.search_item2_et);
        search_item2_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                String text = String.valueOf(search_item2_et.getText());
                Filter filter = ((Filterable) adapter).getFilter();
                adapter.setOriginalData(deviceFollowList);
                if (TextUtils.isEmpty(text)) {
                    filter.filter("");
                } else {
                    filter.filter(text);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
        search_item1_et = (EditText) view1.findViewById(R.id.search_item1_et);
        search_item1_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                String text = search_item1_et.getText().toString().trim();
                Filter filter = ((Filterable) adapter).getFilter();
                adapter.setOriginalData(helper.GetUserList(application.mUser
                        .getUserName()));
                if (TextUtils.isEmpty(text)) {
                    filter.filter("");
                } else {
                    filter.filter(text);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void GetOnlineNumsToRequest() {
        StringBuffer sb = new StringBuffer();
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        for (Devices bean : arrays) {
            if (bean.getLineStatus().equals("在线")) {
                sb.append(bean.getIMEI() + ",");
            }
        }
        if (sb.toString().length() == 0) {
            Toast.makeText(this, "无在线设备 刷新后重试", Toast.LENGTH_LONG).show();
        } else {
            String nums = sb.toString()
                    .substring(0, sb.toString().length() - 1);
            RequestLacations(nums);
        }

    }

    private ArrayList<Devices> getDataForAdapter() {
        ArrayList<Devices> arraysF = new ArrayList<Devices>();
        ArrayList<Devices> arraysOn = new ArrayList<Devices>();
        ArrayList<Devices> arraysOff = new ArrayList<Devices>();
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        for (Devices bean : arrays) {
            if (bean.getSelectStatus().equals("选中")) {
                arraysF.add(bean);
            }
        }
        for (Devices bean : arraysF) {
            if (bean.getLineStatus().equals("在线")) {
                arraysOn.add(bean);
            } else {
                arraysOff.add(bean);
            }
        }
        arraysOn.addAll(arraysOff);
        return arraysOn;
    }

    private void setClickListener() {
        download_rl.setOnClickListener(this);
        logout.setOnClickListener(this);
        reset_pwd.setOnClickListener(this);
        deviceList_ll.setOnClickListener(new MyOnClickListener(0));
        followList_ll.setOnClickListener(new MyOnClickListener(1));
        map_show_ll.setOnClickListener(new MyOnClickListener(2));
        more_ll.setOnClickListener(new MyOnClickListener(3));
        fresh_item1_iv.setOnClickListener(this);
        add_item1_pop_iv.setOnClickListener(this);
        follow_item2_sure_tv.setOnClickListener(this);
        follow_item1_sure_tv.setOnClickListener(this);
        split_item1_sure_tv.setOnClickListener(this);
        split_item2_sure_tv.setOnClickListener(this);

        follow_item1_ll.setOnClickListener(this);
        split_item1_ll.setOnClickListener(this);
        split_item2_ll.setOnClickListener(this);
        fresh_item2_iv.setOnClickListener(this);
        add_item2_pop_iv.setOnClickListener(this);
        follow_item2_ll.setOnClickListener(this);
        mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker mMarker) {
                View view = LayoutInflater.from(NewMainActivity.this).inflate(
                        R.layout.info_maplayout, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView info_time_tv = (TextView) view
                        .findViewById(R.id.info_time_tv);
                TextView info_lat_tv = (TextView) view
                        .findViewById(R.id.info_lat_tv);
                TextView info_lng_tv = (TextView) view
                        .findViewById(R.id.info_lng_tv);
                LatLng ll = mMarker.getPosition();
                String info = mMarker.getTitle();
                String[] infos = info.split("\n");
                String name = infos[0];
                String time = infos[1];
                title.setText("终端名称：" + name);
                info_time_tv.setText("在线时间：" + time);
                info_lng_tv.setText("经度：" + ll.longitude);
                info_lat_tv.setText("纬度：" + ll.latitude);
                InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(view), ll, -100, null);
                mBaiduMap.showInfoWindow(mInfoWindow);

                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fresh_item1_iv:// item1 刷新按钮点击事件
                if (!PubUtil.isConnected(this)) {
                    Toast.makeText(this, "网络异常，稍后再试", Toast.LENGTH_LONG).show();
                    return;
                }
                PostToService();
                break;
            case R.id.fresh_item2_iv:// item2 刷新按钮点击事件
                if (!PubUtil.isConnected(this)) {
                    Toast.makeText(this, "网络异常，稍后再试", Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayList<Devices> DataItem2 = getDataForAdapter();
                if (DataItem2.isEmpty()) {
                    Toast.makeText(NewMainActivity.this, "无添加的跟踪设备，添加后重试", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                PostToService();

                break;
            case R.id.add_item1_pop_iv:// item1 加号按钮点击事件
                pop().showAsDropDown(view, -view.getWidth() - 50, 50);

                break;
            case R.id.add_item2_pop_iv:// item2 加号按钮点击事件
                pop().showAsDropDown(view, -view.getWidth() - 50, 50);
                follow_pop_tv.setText("取消跟踪");
                break;
            case R.id.more_ll:// 更多按钮点击事件
                Intent intent = new Intent(this, MoreSet.class);
                startActivity(intent);
                break;
            case R.id.search_pop_ll:// 搜索按钮
                if (PubUtil.PageNum == 2) {
                    ArrayList<Devices> deviceFollowList2 = getDevicesFollowListData();
                    if (deviceFollowList2.isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "无跟踪设备，请添加后重试", Toast.LENGTH_LONG).show();
                        return;
                    }

                    search_item2_et.setText("");
                    search_item2_rl.setVisibility(View.VISIBLE);
                    fresh_item2_iv.setVisibility(View.INVISIBLE);
                    add_item2_pop_iv.setVisibility(View.INVISIBLE);
                } else if (PubUtil.PageNum == 1) {
                    search_item1_et.setText("");
                    search_item1_rl.setVisibility(View.VISIBLE);
                    fresh_item1_iv.setVisibility(View.INVISIBLE);
                    add_item1_pop_iv.setVisibility(View.INVISIBLE);
                }
                pop.dismiss();
                break;
            case R.id.follow_pop_ll:// 后台跟踪
                if (PubUtil.PageNum == 2) {// page2删除信标
                    ArrayList<Devices> deviceFollowList2 = getDevicesFollowListData();
                    if (deviceFollowList2.isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "无跟踪设备，请添加后重试", Toast.LENGTH_LONG).show();
                        return;
                    }
                    fresh_item2_iv.setVisibility(View.INVISIBLE);
                    PubUtil.follow_iv_pressed2 = true;
                    follow_item2_dev_rl.setVisibility(View.VISIBLE);
                    adapter.setLayout(true);
                    add_item2_pop_iv.setVisibility(View.INVISIBLE);
                } else if (PubUtil.PageNum == 1) {// page1后台跟踪
                    if (getOnLineDevices().isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "无在线设备，请刷新后重试", Toast.LENGTH_LONG).show();
                        return;
                    }
                    fresh_item1_iv.setVisibility(View.INVISIBLE);
                    PubUtil.follow_iv_pressed = true;
                    follow_item1_dev_rl.setVisibility(View.VISIBLE);
                    adapter.setLayout(true);
                    add_item1_pop_iv.setVisibility(View.INVISIBLE);
                }

                pop.dismiss();
                break;
            case R.id.split_pop_ll:// 分屏展示
                if (PubUtil.PageNum == 2) {
                    ArrayList<Devices> deviceFollowList2 = getDevicesFollowListData();
                    if (deviceFollowList2.isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "无跟踪设备，请添加后重试", Toast.LENGTH_LONG).show();
                        return;
                    }
                    fresh_item2_iv.setVisibility(View.INVISIBLE);
                    PubUtil.split_screen_iv2 = true;
                    split_item2_screen_rl.setVisibility(View.VISIBLE);
                    adapter.setLayout(true);
                    add_item2_pop_iv.setVisibility(View.INVISIBLE);
                } else if (PubUtil.PageNum == 1) {
                    if (getOnLineDevices().isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "无在线设备，请刷新后重试", Toast.LENGTH_LONG).show();
                        return;
                    }
                    fresh_item1_iv.setVisibility(View.INVISIBLE);
                    PubUtil.split_screen_iv = true;
                    split_item1_screen_rl.setVisibility(View.VISIBLE);
                    adapter.setLayout(true);
                    add_item1_pop_iv.setVisibility(View.INVISIBLE);
                }

                pop.dismiss();
                break;
            case R.id.follow_item1_ll:// 后台在线跟踪全选框
                if (TheNumIsTheSame(PubUtil.followHash) == 1) {
                    follow_item1_all_iv.setBackgroundResource(R.drawable.check);
                    PubUtil.followHash.clear();
                    adapter.notifyDataSetChanged();
                } else if (TheNumIsTheSame(PubUtil.followHash) == 2) {
                    follow_item1_all_iv.setBackgroundResource(R.drawable.checked);
                    AddOnlineDevToHashMap(PubUtil.followHash);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(NewMainActivity.this, "无在线设备，请刷新重试", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.follow_item2_ll:// page2 删除全部 全选框
                if (TheNumIsTheSame2(PubUtil.followHash2) == 1) {
                    follow_item2_all_iv.setBackgroundResource(R.drawable.check);
                    PubUtil.followHash2.clear();
                    adapter.notifyDataSetChanged();
                } else if (TheNumIsTheSame2(PubUtil.followHash2) == 2) {
                    follow_item2_all_iv.setBackgroundResource(R.drawable.checked);
                    AddFollowDevToHashMap(PubUtil.followHash2);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.split_item1_ll:// 后台地图分屏全选框

                if (TheNumIsTheSame(PubUtil.split_sHash) == 1) {
                    PubUtil.split_sHash.clear();
                    split_item1_all_iv.setBackgroundResource(R.drawable.check);
                    adapter.notifyDataSetChanged();

                } else if (TheNumIsTheSame(PubUtil.split_sHash) == 2) {
                    split_item1_all_iv.setBackgroundResource(R.drawable.checked);
                    AddOnlineDevToHashMap(PubUtil.split_sHash);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(NewMainActivity.this, "无在线设备，请刷新重试", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.split_item2_ll:// page2地图分屏全选框

                if (TheNumIsTheSame2(PubUtil.split_sHash2) == 1) {
                    split_item2_all_iv.setBackgroundResource(R.drawable.check);
                    PubUtil.split_sHash2.clear();
                    adapter.notifyDataSetChanged();
                } else if (TheNumIsTheSame2(PubUtil.split_sHash2) == 2) {
                    split_item2_all_iv.setBackgroundResource(R.drawable.checked);
                    AddFollowDevToHashMap(PubUtil.split_sHash2);
                    adapter.notifyDataSetChanged();
                }

                break;

            case R.id.split_item1_sure_tv:// 分屏显示的确定按钮
                ArrayList<Devices> splitArray = new ArrayList<Devices>();
                if (PubUtil.split_sHash.isEmpty()) {
                    Toast.makeText(NewMainActivity.this, "请选择需要分屏展示的设备", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (PubUtil.split_sHash.size() > 4) {
                        Toast.makeText(NewMainActivity.this, "只能选择4个终端分屏显示", Toast.LENGTH_LONG)
                                .show();
                        return;
                    } else {
                        Iterator it = PubUtil.split_sHash.keySet().iterator();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            Devices value = PubUtil.split_sHash.get(key);
                            splitArray.add(value);
                        }
                        PubUtil.arrays = splitArray;
                        Intent intent_split = new Intent(NewMainActivity.this,
                                MoreMapsActivity.class);
                        startActivity(intent_split);

                        initSplitScreenStatus();
                    }
                }

                break;
            case R.id.split_item2_sure_tv:// page2中分屏显示的确定按钮
                ArrayList<Devices> deviceFollowList2 = getDevicesFollowListData();
                if (deviceFollowList2.isEmpty()) {
                    Toast.makeText(NewMainActivity.this, "无跟踪设备，请添加后重试", Toast.LENGTH_LONG).show();
                    initSplitScreenStatus2();
                } else {
                    ArrayList<Devices> splitArray2 = new ArrayList<Devices>();
                    if (PubUtil.split_sHash2.isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "请选择需要分屏展示的设备", Toast.LENGTH_LONG)
                                .show();
                        return;
                    } else {
                        if (PubUtil.split_sHash2.size() > 4) {
                            Toast.makeText(NewMainActivity.this, "只能选择4个终端分屏显示", Toast.LENGTH_LONG)
                                    .show();
                            return;
                        } else {
                            Iterator it = PubUtil.split_sHash2.keySet().iterator();
                            while (it.hasNext()) {
                                String key = (String) it.next();
                                Devices value = PubUtil.split_sHash2.get(key);
                                splitArray2.add(value);
                            }
                            PubUtil.arrays = splitArray2;
                            Intent intent_split = new Intent(NewMainActivity.this,
                                    MoreMapsActivity.class);
                            startActivity(intent_split);

                            initSplitScreenStatus2();
                        }
                    }
                }

                break;
            case R.id.follow_item1_sure_tv:// 后台跟踪的确定按钮
                if (PubUtil.followHash.isEmpty()) {
                    Toast.makeText(NewMainActivity.this, "请选择需要添加到后台的设备", Toast.LENGTH_LONG).show();
                    return;
                } else {// 将hashMap里面的数据添加到跟踪列表里，（将数据的选择状态改为“选中”）
                    ArrayList<String> followArray = GetDevicesToFollow();
                    Iterator it = PubUtil.followHash.keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        Devices value = PubUtil.followHash.get(key);
                        if (!followArray.contains(key)) {
                            // 记下开始时间
                            long time = System.currentTimeMillis();
                            String time_ = PubUtil.getDateToString(time);
                            helper.UpdateStatus("选中", value.getIMEI());
                            helper.UpdateStartTime(time_, value.getIMEI());
                        }
                    }
                }
                initFollowStatus();
                break;
            case R.id.follow_item2_sure_tv:// page2删除的确定按钮
                ArrayList<Devices> deviceFollowList = getDevicesFollowListData();
                if (deviceFollowList.isEmpty()) {
                    Toast.makeText(NewMainActivity.this, "无跟踪设备，请添加后重试", Toast.LENGTH_LONG).show();

                } else {
                    if (PubUtil.followHash2.isEmpty()) {
                        Toast.makeText(NewMainActivity.this, "请选择需要删除的设备", Toast.LENGTH_LONG)
                                .show();
                        return;
                    } else {// 将hashMap里面的数据添加到跟踪列表里，（将数据的选择状态改为“选中”）
                        Iterator it = PubUtil.followHash2.keySet().iterator();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            helper.UpdateStatus("未选中", key);
                            helper.UpdateStartTime("", key);

                        }
                        deviceFollowList = getDataForAdapter();
                        adapter.setData(deviceFollowList);

                    }
                }
                initFollowStatus2();
                break;
            case R.id.reset_pwd_ll:// 重改密码
                intent = new Intent(NewMainActivity.this, ResetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_ll:// 注销登录
                application.clearLoginParams();
                intent = new Intent(NewMainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.download_rl:
                intent = new Intent(NewMainActivity.this, DownOffPageActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private ArrayList<Devices> getDevicesFollowListData() {
        ArrayList<Devices> arraysF = new ArrayList<Devices>();
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        for (Devices bean : arrays) {
            if (bean.getSelectStatus().equals("选中")) {
                arraysF.add(bean);
            }
        }
        return arraysF;
    }

    private boolean CheckLayoutStatus() {
        if (search_item1_rl.isShown()) {
            fresh_item1_iv.setVisibility(View.VISIBLE);

            add_item1_pop_iv.setVisibility(View.VISIBLE);
            search_item1_rl.setVisibility(View.GONE);
            return true;
        } else if (follow_item1_dev_rl.isShown()) {
            initFollowStatus();
            return true;
        } else if (split_item1_screen_rl.isShown()) {
            initSplitScreenStatus();
            return true;
        } else if (search_item2_rl.isShown()) {
            fresh_item2_iv.setVisibility(View.VISIBLE);

            add_item2_pop_iv.setVisibility(View.VISIBLE);
            search_item2_rl.setVisibility(View.GONE);
            return true;
        } else if (follow_item2_dev_rl.isShown()) {
            initFollowStatus2();
            return true;
        } else if (split_item2_screen_rl.isShown()) {
            initSplitScreenStatus2();
            return true;
        } else {
            return false;
        }

    }

    private ArrayList<String> GetDevicesToFollow() {
        ArrayList<String> follows = new ArrayList<String>();
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        for (Devices bean : arrays) {
            if (bean.getSelectStatus().equals("选中")) {
                follows.add(bean.getIMEI());
            }
        }
        return follows;

    }

    /**
     * 初始化后台跟踪按钮
     */
    private void initFollowStatus() {
        if (!PubUtil.followHash.isEmpty()) {
            PubUtil.followHash.clear();
        }
        fresh_item1_iv.setVisibility(View.VISIBLE);
        add_item1_pop_iv.setVisibility(View.VISIBLE);
        follow_item1_dev_rl.setVisibility(View.GONE);
        adapter.setLayout(false);
        PubUtil.follow_iv_pressed = false;
        follow_item1_all_iv.setBackgroundResource(R.drawable.check);

    }

    /**
     * 初始化后台跟踪按钮
     */
    private void initFollowStatus2() {
        if (!PubUtil.followHash2.isEmpty()) {
            PubUtil.followHash2.clear();
        }
        fresh_item2_iv.setVisibility(View.VISIBLE);
        add_item2_pop_iv.setVisibility(View.VISIBLE);
        follow_item2_dev_rl.setVisibility(View.GONE);
        adapter.setLayout(false);
        PubUtil.follow_iv_pressed2 = false;
        follow_item2_all_iv.setBackgroundResource(R.drawable.check);

    }

    private void AddOnlineDevToHashMap(HashMap<String, Devices> hashmap) {

        if (!hashmap.isEmpty()) {
            hashmap.clear();
        }
        List<Devices> onlineDevs = new ArrayList<Devices>();
        for (Devices bean : allDeviceList) {
            if (bean.getLineStatus().equals("在线")) {
                onlineDevs.add(bean);
            }
        }
        for (Devices bean : onlineDevs) {
            hashmap.put(bean.getIMEI(), bean);
        }

    }

    private void AddFollowDevToHashMap(HashMap<String, Devices> hashmap) {

        if (!hashmap.isEmpty()) {
            hashmap.clear();
        }
        List<Devices> followDev = new ArrayList<Devices>();
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        for (Devices bean : arrays) {
            if (bean.getSelectStatus().equals("选中")) {
                followDev.add(bean);
            }
        }
        for (Devices bean : followDev) {
            hashmap.put(bean.getIMEI(), bean);
        }

    }

    private ImageView follow_item1_all_iv;
    private ImageView follow_item2_all_iv;
    private ImageView split_item1_all_iv;
    private ImageView split_item2_all_iv;

    private TextView follow_item1_sure_tv;
    private TextView follow_item2_sure_tv;

    private TextView split_item1_sure_tv;
    private TextView split_item2_sure_tv;

    private LinearLayout follow_item1_ll;
    private LinearLayout follow_item2_ll;
    private LinearLayout split_item1_ll;
    private LinearLayout split_item2_ll;

    private ImageView fresh_item2_iv;
    private ImageView add_item2_pop_iv;
    private EditText search_item2_et;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private MyViewPage viewPage;

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPage.setCurrentItem(index);
        }
    }

    ;

    private PopupWindow pop() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.chat_item_, null);
        LinearLayout search_pop_ll = (LinearLayout) view
                .findViewById(R.id.search_pop_ll);
        LinearLayout follow_pop_ll = (LinearLayout) view
                .findViewById(R.id.follow_pop_ll);
        LinearLayout split_pop_ll = (LinearLayout) view
                .findViewById(R.id.split_pop_ll);
        follow_pop_tv = (TextView) view.findViewById(R.id.follow_pop_tv);
        search_pop_ll.setOnClickListener(this);
        follow_pop_ll.setOnClickListener(this);
        split_pop_ll.setOnClickListener(this);
        pop = new PopupWindow(view, dip2px(this, 130), dip2px(this, 160), false);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true); // 设置点击窗口外边窗口消失
        pop.setFocusable(true);// 设置此参数获得焦点，否则无法点击

        return pop;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void loadDataForPage1() {
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        if (!PublicUtils.SetOrderForDevices(arrays).isEmpty()) {
            adapter.setData(PublicUtils.SetOrderForDevices(arrays));
            allDeviceList = PublicUtils.SetOrderForDevices(arrays);
        }
        // 开启timer_online
        mHandler.sendEmptyMessage(8);
    }

    private void RequestLacations(final String nums) {
        String url = Constant.URL
                + "/WebService/GLService.asmx/GetMemberPositioningByImeis";
        StringRequest stringRequest = new StringRequest(Method.POST, url,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            String json = getStr(response);
                            ArrayList<Devices> data = parseJsonPage3(json);
                            Message msg = new Message();
                            msg.obj = data;
                            msg.what = 3;
                            mHandler.sendMessage(msg);
                        }

                    }

                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("imeis", nums);
                return map;
            }

        };

        mRequestQueue.add(stringRequest);
    }

    private ArrayList<Devices> parseJsonPage3(String json) {
        ArrayList<Devices> array = new ArrayList<Devices>();
        try {

            JSONObject obj = new JSONObject(json);
            JSONArray list = obj.getJSONArray("Model");
            for (int i = 0; i < list.length(); i++) {
                Devices bean = new Devices();
                bean.setLat(list.getJSONObject(i).getString("Latitude"));
                bean.setLng(list.getJSONObject(i).getString("Longitude"));
                bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
                bean.setIMEI(list.getJSONObject(i).getString("Imei").trim());
                bean.setSpeed(list.getJSONObject(i).getString("Speed"));
                array.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;

    }

    private void PostToService() {
        String url = Constant.URL
                + "/WebService/GLService.asmx/GetMemberAllByUserName";
        StringRequest strRequest = new StringRequest(Method.POST, url,
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            String json = getStr(response);
                            parseJsonData(json);
                            mHandler.sendEmptyMessage(1);
                        }

                    }

                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        }

        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userName", application.mUser.getUserName());
                return map;
            }

        };
        mRequestQueue.add(strRequest);
    }

    private void parseJsonData(String json) {// 解析json数据
        try {
            if (!allDeviceList.isEmpty()) {
                allDeviceList.clear();
            }
            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray("Model");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj_ = (JSONObject) jsonArray.opt(i);
                Devices device = new Devices();

                device.setIMEI(obj_.optString("IMEI"));
                device.setIMSI(obj_.optString("IMSI"));
                device.setName(obj_.optString("Name"));
                device.setOnTime(obj_.optString("OnTime"));
                device.setLineStatus(obj_.optString("LineStatus"));
                device.setFromDate(obj_.optString("FromDate"));
                device.setWarnNo(obj_.optString("WarnNo"));
                device.setSIMNo(obj_.optString("SIMNo"));
                device.setUserName(obj_.optString("UserName"));
                device.setLat(obj_.optString("Latitude"));
                device.setLng(obj_.optString("Longitude"));
                allDeviceList.add(device);

            }
            syncLocalData();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 同步本地数据库存储数据
     */
    private void syncLocalData() {
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        List<String> imeis_local = new ArrayList<String>();
        for (Devices array : arrays) {
            imeis_local.add(array.getIMEI());
        }
        List<String> imeis = new ArrayList<String>();
        for (Devices array : allDeviceList) {
            imeis.add(array.getIMEI());
        }
        Iterator<String> iterator = imeis_local.iterator();
        while (iterator.hasNext()) {
            String imei = iterator.next();
            if (!imeis.contains(imei)) {
                helper.DelUserInfo(imei);
            }
        }
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    public static String getStr(String str) {
        int ii = 0;
        int j = 0;
        ii = str.indexOf("{");
        j = str.lastIndexOf("}");
        return str.substring(ii, j + 1);
    }

    private void loadView() {
        // 将要分页显示的View装入数组中
        LayoutInflater inflater = getLayoutInflater().from(this);
        view1 = inflater.inflate(R.layout.viewpage_item1, null);
        view2 = inflater.inflate(R.layout.viewpage_item2, null);
        view3 = inflater.inflate(R.layout.viewpage_item3, null);
        view4 = inflater.inflate(R.layout.viewpage_item4, null);
        views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

    }

    private void initSplitScreenStatus() {
        if (!PubUtil.split_sHash.isEmpty()) {
            PubUtil.split_sHash.clear();
        }
        fresh_item1_iv.setVisibility(View.VISIBLE);
        add_item1_pop_iv.setVisibility(View.VISIBLE);
        split_item1_screen_rl.setVisibility(View.GONE);
        adapter.setLayout(false);
        PubUtil.split_screen_iv = false;
        split_item1_all_iv.setBackgroundResource(R.drawable.check);

    }

    private void initSplitScreenStatus2() {
        if (!PubUtil.split_sHash2.isEmpty()) {
            PubUtil.split_sHash2.clear();
        }
        fresh_item2_iv.setVisibility(View.VISIBLE);
        add_item2_pop_iv.setVisibility(View.VISIBLE);
        split_item2_screen_rl.setVisibility(View.GONE);
        adapter.setLayout(false);
        PubUtil.split_screen_iv2 = false;
        split_item2_all_iv.setBackgroundResource(R.drawable.check);

    }

    private void setAdapter() {

        PagerAdapter pageAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return views.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(views.get(position));
                return views.get(position);
            }
        };

        viewPage.setAdapter(pageAdapter);

    }

    /**
     * 描述：返回.
     */
    @Override
    public void onBackPressed() {
        if (CheckLayoutStatus()) {
            return;
        }
        if (isExit == false) {
            isExit = true;
            AbToastUtil.showToast(NewMainActivity.this, "再按一次退出程序");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    isExit = false;
                }

            }, 2000);
        } else {
            AbActivityManager.getInstance().clearAllActivity();
            super.onBackPressed();
        }

    }

    private void initMap() {
        // mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(PubUtil
        // .convert(latlng), 18));
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null
                && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 地图上比例尺
        mMapView.showScaleControl(false);
    }

    @Override
    protected void onPause() {

        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mHandler.sendEmptyMessage(6);
        mHandler.sendEmptyMessage(10);
        // 将数据库中的所有信标的在线状态设为离线
        List<Devices> arrays = helper.GetUserList(application.mUser
                .getUserName());
        if (!arrays.isEmpty()) {
            for (Devices bean : arrays) {
                helper.UpdateLineStatus("离线", bean.getIMEI());
            }
            super.onDestroy();
        } else {
            super.onDestroy();
        }
        mMapView.onDestroy();
        super.onDestroy();
    }



    Handler mHandler = new Handler() {

        private String name;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {
                case 1:
                    if (PubUtil.PageNum == 1) {
                        if (!search_item1_rl.isShown()) {
                            allDeviceList = PublicUtils.SetOrderForDevices(allDeviceList);
                            adapter.setData(allDeviceList);
                        }else{
                            return;
                        }
                    } else if (PubUtil.PageNum == 2) {
                        if (!search_item2_rl.isShown()) {
                            deviceFollowList = getDataForAdapter();
                            adapter.setData(deviceFollowList);
                        }else{
                            return;
                        }
                    }
                    if (!istimer_online) {
                        Toast.makeText(NewMainActivity.this, "数据刷新成功", 0).show();
                    } else {
                        istimer_online = false;
                    }

                    // 将所有的设备添加到本地数据库
                    for (Devices bean : allDeviceList) {
                        bean.setSelectStatus("1");
                        bean.setStartTime("1");
                        String line_status = bean.getLineStatus();
                        if (bean.getIMEI() == null) {
                            return;
                        }
                        if (!helper.checkData(bean.getIMEI())) {
                            helper.SaveUserInfo(bean);
                        }
                        helper.UpdateLineStatus(line_status, bean.getIMEI());

                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    break;
                case 3:
                    mBaiduMap.clear();
                    ArrayList<Devices> arrays = (ArrayList<Devices>) msg.obj;
                    ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
                    for (int i = 0; i < arrays.size(); i++) {
                        Devices bean = arrays.get(i);
                        if (bean.getLat().equals("") || bean.getLng().equals("")) {
                            Toast.makeText(NewMainActivity.this, "服务器无上传位置数据", 0)
                                    .show();
                            return;
                        }
                        String imei = bean.getIMEI();
                        if (!TextUtils.isEmpty(imei)) {
                            Devices devices = helper.GetUser(imei.trim());
                            name = devices.getName();
                        }
                        String onTime = bean.getOnTime();
                        double a = Double.parseDouble(bean.getLat());
                        double b = Double.parseDouble(bean.getLng());
                        LatLng latln = new LatLng(a, b);
                        LatLng latlng_ = PubUtil.convert(latln);
                        LatLng latlng = PubUtil.DfInfomation(latlng_);
                        latlngs.add(latlng);
                        Bitmap bitmap = null;
                        bitmap = PubUtil.toRoundBitmap(BitmapFactory
                                        .decodeResource(
                                                NewMainActivity.this.getResources(),
                                                R.drawable.new_ni), name,
                                NewMainActivity.this);
                        MarkerOptions marker = new MarkerOptions().position(latlng)
                                .title(name + "\n" + onTime)
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .zIndex(9).draggable(true);
                        mBaiduMap.addOverlay(marker);

                    }
                    LetPointsInScreen(latlngs);

                    break;

                case 5:// 开启Timer
                    if (timer == null) {
                        TimerTask task = new TimerTask() {

                            @Override
                            public void run() {
                                mHandler.sendEmptyMessage(7);

                            }
                        };
                        timer = new Timer(true);
                        timer.schedule(task, 1000, 5000);
                    }
                    break;
                case 6:// 关闭timer
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    break;

                case 7:// 跟服务器请求数据
                    GetOnlineNumsToRequest();
                    break;

                case 8:
                    if (timer_online == null) {
                        TimerTask task = new TimerTask() {


                            @Override
                            public void run() {
                                mHandler.sendEmptyMessage(9);
                                istimer_online = true;
                            }
                        };
                        timer_online = new Timer(true);
                        timer_online.schedule(task, 1000, 10000);
                    }
                    break;

                case 9:
                    PostToService();
                    break;
                case 10:
                    if (timer_online != null) {
                        timer_online.cancel();
                        timer_online = null;
                    }
                    break;
                default:break;
            }
        }

        private void LetPointsInScreen(ArrayList<LatLng> arrays) {

            if (arrays != null && arrays.size() == 1) {
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(arrays.get(0), 18));
                return;
            }
            if (arrays != null && arrays.size() == 2) {
                double distance = PubUtil.getDistance(arrays.get(0).latitude, arrays.get(0).longitude, arrays.get(1).latitude, arrays.get(1).longitude);
                if (distance < 20) {
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(arrays.get(0), 18));
                    return;
                }
            }
            Builder builder = new LatLngBounds.Builder();
            for (LatLng mlatlng : arrays) {
                builder.include(mlatlng);
            }
            LatLngBounds bounds = builder.build();
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds,
                    500, 300);
            mBaiduMap.animateMapStatus(u);
        }

    };
    private RelativeLayout download_rl;

}
