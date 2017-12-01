package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbActivityManager;
import com.ab.util.AbDateUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.wheel.AbNumericWheelAdapter;
import com.ab.view.wheel.AbStringWheelAdapter;
import com.ab.view.wheel.AbWheelView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.mj.gpsclient.R;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.model.Devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mj.gpsclient.Activity.PubUtil.dip2px;
import static com.mj.gpsclient.R.id.startEnd_sure_tv;

public class TrackHisPlayActivity extends Activity implements View.OnClickListener {

    private TextureMapView mMapView_trackPlay;
    private BaiduMap mBaiduMap_trackPlay;
    private TextView trackPlay_tv;
    private PopupWindow mPop;
    private RelativeLayout select_time_ll;
    private String valueToReturn;
    private TextView select_startTime_tv;
    private TextView select_endTime_tv;
    private RequestQueue mRequestQueue;
    private String deviceImei;
    private String deviceName;
    private Devices bean;
    private String loadName;
    ArrayList<Devices> devicesOntrack = new ArrayList<Devices>();
    private TextView trackPlay_progress_tv;//显示进度的文本
    private ImageView track_his_iv;//暂停开始按钮
    private BitmapDescriptor bdA;
    private BitmapDescriptor bdB;
    private Marker mMarkerB;
    private boolean ispause = false;//是否暂停
    private boolean isDisplaying = false;//是否正在播放
    private int PlayedData = -1;//播放到第几个数据
    private Devices beans;
    ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
    private String tag = "TrackHisPlayActivity";
    private Dialog mDialog;
    private String select_startTime;
    private String select_endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_track_his_play);
        AbActivityManager.getInstance().addActivity(this);
        findView();
        BaiduMapClickListener();
        mPop = pop();
        bean = PubUtil.bean;
        deviceImei = bean.getIMEI();
        loadName = bean.getUserName();
        deviceName = bean.getName();
        bdA = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_track_navi_end);
        bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        mDialog = new Dialog(this, R.style.DialogStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        mDialog.setContentView(view);


    }

    private void GetRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
    }


    private void findView() {
        mMapView_trackPlay = (TextureMapView) findViewById(R.id.bmapView_trackPlay);
        mBaiduMap_trackPlay = mMapView_trackPlay.getMap();
        trackPlay_tv = (TextView) findViewById(R.id.trackPlay_tv);
        trackPlay_progress_tv = (TextView) findViewById(R.id.trackPlay_progress_tv);
        track_his_iv = (ImageView) findViewById(R.id.track_his_iv);
        track_his_iv.setOnClickListener(this);
        TextView startEnd_sure_tv = (TextView) findViewById(R.id.startEnd_sure_tv);
        startEnd_sure_tv.setOnClickListener(this);
        trackPlay_tv.setOnClickListener(this);
        ImageView iv_back_track = (ImageView) findViewById(R.id.iv_back_Follow_trackPlay);
        iv_back_track.setOnClickListener(this);
        select_time_ll = (RelativeLayout) findViewById(R.id.select_Time_ll);
        select_startTime_tv = (TextView) findViewById(R.id.select_startTime_tv);
        select_startTime_tv.setOnClickListener(this);
        select_endTime_tv = (TextView) findViewById(R.id.select_endTime_tv);
        select_endTime_tv.setOnClickListener(this);
    }

    private PopupWindow pop() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.pop_track_play, null);
        TextView today_tv = (TextView) view.findViewById(R.id.today_tv);
        TextView lastThreeDay_tv = (TextView) view.findViewById(R.id.lastThreeDay_tv);
        TextView lastOneWeek_tv = (TextView) view.findViewById(R.id.lastOneWeek_tv);
        TextView custom_tv = (TextView) view.findViewById(R.id.custom_tv);
        today_tv.setOnClickListener(this);
        lastThreeDay_tv.setOnClickListener(this);
        lastOneWeek_tv.setOnClickListener(this);
        custom_tv.setOnClickListener(this);
        PopupWindow pop = new PopupWindow(view, dip2px(this, 90), dip2px(this, 160), false);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true); // 设置点击窗口外边窗口消失
        pop.setFocusable(true);// 设置此参数获得焦点，否则无法点击

        return pop;
    }

    private void BaiduMapClickListener() {
        mBaiduMap_trackPlay.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            private InfoWindow mInfoWindow;

            @Override
            public boolean onMarkerClick(final Marker mMarker) {

                View view = LayoutInflater.from(TrackHisPlayActivity.this)
                        .inflate(R.layout.info_maplayout, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView info_time_tv = (TextView) view
                        .findViewById(R.id.info_time_tv);
                TextView info_lat_tv = (TextView) view
                        .findViewById(R.id.info_lat_tv);
                TextView info_lng_tv = (TextView) view
                        .findViewById(R.id.info_lng_tv);
                LatLng ll = mMarker.getPosition();
                String info = mMarker.getTitle();
                String[] infos = info.split("/n");
                title.setText("终端名称：" + deviceName);
                info_time_tv.setText("在线时间：" + infos[1]);
                info_lng_tv.setText("经度：" + ll.longitude);
                info_lat_tv.setText("纬度：" + ll.latitude);
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(view), ll, -47, null);
                mBaiduMap_trackPlay.showInfoWindow(mInfoWindow);
                return true;
            }
        });
        mBaiduMap_trackPlay.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {

                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap_trackPlay.hideInfoWindow();

            }
        });

    }

    @Override
    protected void onPause() {
        // activity 暂停时同时暂停地图控件
        ispause = true;
        track_his_iv.setBackgroundResource(R.drawable.on_zk);
        mMapView_trackPlay.onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        // activity 恢复时同时恢复地图控件
        mMapView_trackPlay.onResume();
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        // activity 销毁时同时销毁地图控件
        mMapView_trackPlay.onDestroy();
        super.onDestroy();
    }


    public void initWheelTime(View mTimeView, TextView mText, PopupWindow pop) {
        final AbWheelView mWheelViewY = (AbWheelView) mTimeView.findViewById(R.id.wheelView0);
        final AbWheelView mWheelViewMD = (AbWheelView) mTimeView.findViewById(R.id.wheelView1);
        final AbWheelView mWheelViewMM = (AbWheelView) mTimeView.findViewById(R.id.wheelView2);
        final AbWheelView mWheelViewHH = (AbWheelView) mTimeView.findViewById(R.id.wheelView3);
        Button okBtn = (Button) mTimeView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) mTimeView.findViewById(R.id.cancelBtn);
        mWheelViewMD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        initWheelTimePicker(mText, mWheelViewY, mWheelViewMD, mWheelViewMM, mWheelViewHH, okBtn, cancelBtn, 2013, 1, 1, 10, 0, true, pop);
    }

    private PopupWindow pop_wheelview(TextView tv) {
        View mTimeView2 = null;

        mTimeView2 = LayoutInflater.from(this).inflate(R.layout.choose_three, null);
        PopupWindow pop = new PopupWindow(mTimeView2, RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(this, 300), false);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(false); // 设置点击窗口外边窗口消失
        pop.setFocusable(true);// 设置此参数获得焦点，否则无法点击
//        mTimeView2.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == event.KEYCODE_BACK) {
//                    if (event.getAction() == event.ACTION_DOWN) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
        final AbWheelView mWheelView1 = (AbWheelView) mTimeView2.findViewById(R.id.wheelView0);
        mWheelView1.setAdapter(new AbNumericWheelAdapter(2016, 2030));
        // 可循环滚动
        mWheelView1.setCyclic(true);
        // 添加文字
        mWheelView1.setLabel("      年");
        // 初始化时显示的数据
        mWheelView1.setCurrentItem(1);
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
        mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        initWheelTime(mTimeView2, tv, pop);
        return pop;
    }

    private void initWheelTimePicker(final TextView mText, final AbWheelView mWheelViewY, final AbWheelView mWheelViewMD, final AbWheelView mWheelViewHH, final AbWheelView mWheelViewMM,
                                     Button okBtn, Button cancelBtn,
                                     int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute, boolean initStart, final PopupWindow pop) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        if (initStart) {
            defaultYear = year;
            defaultMonth = month;
            defaultDay = day;
            defaultHour = hour;
            defaultMinute = minute;
        }

        String val = AbStrUtil.dateTimeFormat(defaultYear + "-" + defaultMonth + "-" + defaultDay + " " + defaultHour + ":" + defaultMinute + ":" + second);
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        //
        final List<String> textDMList = new ArrayList<String>();
        final List<String> textDMDateList = new ArrayList<String>();
        for (int i = 1; i < 13; i++) {
            if (list_big.contains(String.valueOf(i))) {
                for (int j = 1; j < 32; j++) {
                    textDMList.add(i + "月" + " " + j + "日");
                    textDMDateList.add(defaultYear + "-" + i + "-" + j);
                }
            } else {
                if (i == 2) {
                    if (AbDateUtil.isLeapYear(defaultYear)) {
                        for (int j = 1; j < 30; j++) {
                            textDMList.add(i + "月" + " " + j + "日");
                            textDMDateList.add(defaultYear + "-" + i + "-" + j);
                        }
                    } else {
                        for (int j = 1; j < 29; j++) {
                            textDMList.add(i + "月" + " " + j + "日");
                            textDMDateList.add(defaultYear + "-" + i + "-" + j);
                        }
                    }
                } else {
                    for (int j = 1; j < 31; j++) {
                        textDMList.add(i + "月" + " " + j + "日");
                        textDMDateList.add(defaultYear + "-" + i + "-" + j);
                    }
                }
            }

        }
        String currentDay = defaultMonth + "月" + " " + defaultDay + "日";
        int currentDayIndex = textDMList.indexOf(currentDay);

        // 月日
        mWheelViewMD.setAdapter(new AbStringWheelAdapter(textDMList, AbStrUtil.strLength("12月" + " " + "12日")));
        mWheelViewMD.setCyclic(true);
        mWheelViewMD.setLabel("");
        mWheelViewMD.setCurrentItem(currentDayIndex);
        mWheelViewMD.setValueTextSize(35);
        mWheelViewMD.setLabelTextSize(35);
        mWheelViewMD.setLabelTextColor(0x80000000);
        //mWheelViewMD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));

        // 时
        mWheelViewHH.setAdapter(new AbNumericWheelAdapter(0, 23));
        mWheelViewHH.setCyclic(true);
        mWheelViewHH.setLabel("点");
        mWheelViewHH.setCurrentItem(defaultHour);
        mWheelViewHH.setValueTextSize(35);
        mWheelViewHH.setLabelTextSize(35);
        mWheelViewHH.setLabelTextColor(0x80000000);
        //mWheelViewH.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));

        // 分
        mWheelViewMM.setAdapter(new AbNumericWheelAdapter(0, 59));
        mWheelViewMM.setCyclic(true);
        mWheelViewMM.setLabel("分");
        mWheelViewMM.setCurrentItem(defaultMinute);
        mWheelViewMM.setValueTextSize(35);
        mWheelViewMM.setLabelTextSize(35);
        mWheelViewMM.setLabelTextColor(0x80000000);
        //mWheelViewM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));

        okBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                int index0 = mWheelViewY.getCurrentItem();
                int index1 = mWheelViewMD.getCurrentItem();
                int index2 = mWheelViewHH.getCurrentItem();
                int index3 = mWheelViewMM.getCurrentItem();
                String year = mWheelViewY.getAdapter().getItem(index0);
                String dmStr = textDMDateList.get(index1);
                String dmStr_ = dmStr.substring(4);
                Calendar calendar = Calendar.getInstance();
                int second = calendar.get(Calendar.SECOND);
                valueToReturn = AbStrUtil.dateTimeFormat(year + dmStr_ + " " + index2 + ":" + index3 + ":" + second);
                mText.setText(valueToReturn);
                pop.dismiss();
            }

        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
            }

        });
    }

    private void RequestDatasFromService(final String startTime, final String endTime) {
        GetRequestQueue();
        String url = Constant.URL + "/WebService/GLService.asmx/GetTrackBack";
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                //解析请求的数据
                if (str == null) {
                    return;
                }

                String json = PubUtil.getStr(str);
                devicesOntrack.clear();
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray list = obj.getJSONArray("Model");
                    for (int i = 0; i < list.length(); i++) {
                        Devices bean = new Devices();
                        bean.setLat(list.getJSONObject(i).getString("Latitude"));
                        bean.setLng(list.getJSONObject(i).getString("Longitude"));
                        bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
                        devicesOntrack.add(bean);
                    }
                    mHandler.sendEmptyMessage(11);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userName", loadName);
                map.put("imei", deviceImei);
                map.put("startTime", startTime);
                map.put("endTime", endTime);
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private String formatWebserviceDate(Date date) {
        String time = new java.text.SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(date);
        StringBuilder sb = new StringBuilder(time);
        sb.insert(10, 'T');
        return sb.toString();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Calendar startCal = null;
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://请求当天的数据
                    //初始化进度文本框和暂停按钮的状态
                    initTitleStatus();
                    startCal = Calendar.getInstance();
                    startCal.set(Calendar.HOUR_OF_DAY, 0);
                    startCal.set(Calendar.MINUTE, 0);
                    startCal.set(Calendar.SECOND, 0);
                    String startTime1 = formatWebserviceDate(startCal.getTime());
                    String endTime1 = PubUtil.getDateToString(System.currentTimeMillis());
                    if (!PubUtil.isConnected(TrackHisPlayActivity.this)) {
                        Toast.makeText(TrackHisPlayActivity.this, "无网络，稍后重试", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!mDialog.isShowing()) {
                            mDialog.show();
                        }
                        RequestDatasFromService(startTime1, endTime1);
                    }

                    break;
                case 2://请求过去3天的轨迹
                    initTitleStatus();
                    startCal = Calendar.getInstance();
                    startCal.add(Calendar.DATE, -3);
                    startCal.set(Calendar.HOUR_OF_DAY, 0);
                    startCal.set(Calendar.MINUTE, 0);
                    startCal.set(Calendar.SECOND, 0);
                    String startTime2 = formatWebserviceDate(startCal.getTime());
                    String endTime2 = PubUtil.getDateToString(System.currentTimeMillis());
                    if (!PubUtil.isConnected(TrackHisPlayActivity.this)) {
                        Toast.makeText(TrackHisPlayActivity.this, "无网络，稍后重试", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!mDialog.isShowing()) {
                            mDialog.show();
                        }
                        RequestDatasFromService(startTime2, endTime2);
                    }

                    break;
                case 3://请求过去一周的轨迹
                    initTitleStatus();
                    startCal = Calendar.getInstance();
                    startCal.add(Calendar.DATE, -7);
                    startCal.set(Calendar.HOUR_OF_DAY, 0);
                    startCal.set(Calendar.MINUTE, 0);
                    startCal.set(Calendar.SECOND, 0);
                    String startTime3 = formatWebserviceDate(startCal.getTime());
                    String endTime3 = PubUtil.getDateToString(System.currentTimeMillis());
                    if (!PubUtil.isConnected(TrackHisPlayActivity.this)) {
                        Toast.makeText(TrackHisPlayActivity.this, "无网络，稍后重试", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!mDialog.isShowing()) {
                            mDialog.show();
                        }
                        RequestDatasFromService(startTime3, endTime3);
                    }

                    break;
                case 4://请求自定义的轨迹
                    initTitleStatus();
                    break;
                case 11://将请求到的数据连线
                    mDialog.dismiss();
                    if (devicesOntrack.size() > 0) {
                        track_his_iv.setBackgroundResource(R.drawable.pause_zk);
                        trackPlay_progress_tv.setText(devicesOntrack.size() + "/" + devicesOntrack.size());
                        sendEmptyMessage(12);
                    } else {
                        Toast.makeText(TrackHisPlayActivity.this, "无轨迹信息", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 12://开始画轨迹
                    BeginDrawTrack();
                    break;
                case 13://
                    mHandler.removeMessages(12);
                    break;
                case 15:////play one finish
                    mHandler.removeMessages(12);
                    sendEmptyMessageDelayed(12, 800);
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 初始化右上角两个控件的状态
     */

    private void initTitleStatus() {
        trackPlay_progress_tv.setText("0/0");
        track_his_iv.setBackgroundResource(R.drawable.on_zk);
        mMapView_trackPlay.getMap().clear();
        mMapView_trackPlay.getOverlay().clear();
        PlayedData = -1;
        ispause = false;
        latlngs.clear();
        devicesOntrack.clear();
        mHandler.sendEmptyMessage(13);

    }

    private List<String> removeDuplicate(List<String> list)

    {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    private void BeginDrawTrack() {
        if (ispause) {
            return;
        }
        PlayedData++;
        if (PlayedData < devicesOntrack.size()) {
            isDisplaying = true;
            trackPlay_progress_tv.setText(devicesOntrack.size() - 1 - PlayedData + "/" + devicesOntrack.size());

            Devices deviceTodraw = GetDataToDraw(PlayedData);
            String onTime1 = deviceTodraw.getOnTime();
            String lat_track = deviceTodraw.getLat();
            String lng_track = deviceTodraw.getLng();
            LatLng latlng_track = new LatLng(Double.parseDouble(lat_track),
                    Double.parseDouble(lng_track));
            LatLng latlng_track_ = PubUtil.convert(latlng_track);
            LatLng latlng_1 = PubUtil.DfInfomation(latlng_track_);
            if (ispause) {
                return;
            }
            if (PlayedData == 0) {
                MarkerOptions ooA = new MarkerOptions()
                        .position(latlng_1).icon(bdA)
                        .title(deviceImei + "/n" + onTime1).zIndex(9)
                        .draggable(true);
                Marker mMarkerA = (Marker) (mBaiduMap_trackPlay.addOverlay(ooA));
            } else {
                Log.i(tag, PlayedData + "");
                if (mMarkerB != null) {
                    mMarkerB.remove();
                }
                MarkerOptions ooB = new MarkerOptions()
                        .position(latlng_1).icon(bdB)
                        .title(deviceImei + "/n" + onTime1).zIndex(9)
                        .draggable(true);
                mMarkerB = (Marker) (mBaiduMap_trackPlay.addOverlay(ooB));
            }
            latlngs.add(latlng_1);

            if (latlngs.size() > 1) {
                OverlayOptions ooPolyline = new PolylineOptions()
                        .width(3).color(0xAAFF0000).points(latlngs);
                if (ispause) {
                    return;
                }
                Polyline mOverlay = (Polyline) mBaiduMap_trackPlay.addOverlay(ooPolyline);
                LetPointsInScreen(latlngs);
            } else {
                LetPointsInScreen(latlngs);

            }
            if (ispause) {
                return;
            }
            mHandler.sendEmptyMessage(15);
        } else {
            isDisplaying = false;
            track_his_iv.setBackgroundResource(R.drawable.on_zk);
            trackPlay_progress_tv.setText("0/0");
            trackPlay_tv.setText("请选择");
            Toast.makeText(this, "轨迹播放完毕", Toast.LENGTH_LONG).show();
        }


    }

    private void LetPointsInScreen(ArrayList<LatLng> arrays) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng mlatlng : arrays) {
            builder.include(mlatlng);
        }
        LatLngBounds bounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds, 500, 300);
        if (ispause) {
            return;
        }
        mBaiduMap_trackPlay.animateMapStatus(u);
    }

    private Devices GetDataToDraw(int i) {
        Devices beans = null;
        for (int y = 0; y < devicesOntrack.size(); y++) {
            if (y == i) {
                beans = devicesOntrack.get(y);
            }
        }

        return beans;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.today_tv://当天按钮的点击事件
                trackPlay_tv.setText("当天");
                mHandler.sendEmptyMessage(1);
                mPop.dismiss();
                break;
            case R.id.lastThreeDay_tv://过去3天按钮的点击事件
                trackPlay_tv.setText("过去3天");
                mHandler.sendEmptyMessage(2);
                mPop.dismiss();
                break;
            case R.id.lastOneWeek_tv://过去一周按钮的点击事件
                trackPlay_tv.setText("过去一周");
                mHandler.sendEmptyMessage(3);
                mPop.dismiss();
                break;
            case R.id.custom_tv://自定义按钮的点击事件
                trackPlay_tv.setText("自定义");
                mPop.dismiss();
                select_time_ll.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(4);
                break;
            case R.id.trackPlay_tv://选择按钮的点击事件
                mPop.showAsDropDown(view, 0, 5);
                select_time_ll.setVisibility(View.GONE);
                break;
            case R.id.select_startTime_tv: //选择开始时间
                pop_wheelview((TextView) view).showAsDropDown(view);
                break;
            case R.id.select_endTime_tv: //选择结束时间
                pop_wheelview((TextView) view).showAsDropDown(view);
                break;
            case R.id.iv_back_Follow_trackPlay: //返回按钮点击事件
                finish();
                break;
            case startEnd_sure_tv: //自定义选择时间中的确定事件
                select_startTime = select_startTime_tv.getText().toString().trim();
                select_endTime = select_endTime_tv.getText().toString().trim();
                if (select_startTime.equals("点击选择")) {
                    Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
                    return;
                } else if (select_endTime.equals("点击选择")) {
                    Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!compareTime(select_startTime, getCurrentTime())) {
                        Toast.makeText(this, "开始时间不能大于系统时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!compareTime(select_startTime, select_endTime)) {
                        Toast.makeText(this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    select_time_ll.setVisibility(View.GONE);
                    //TODo 开始请求数据
                    if (!PubUtil.isConnected(TrackHisPlayActivity.this)) {
                        Toast.makeText(TrackHisPlayActivity.this, "无网络，稍后重试", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!mDialog.isShowing()) {
                            mDialog.show();
                        }
                        RequestDatasFromService(select_startTime, select_endTime);
                    }
                }
                break;
            case R.id.track_his_iv://暂停按钮点击事件
                if (!isDisplaying) {
                    Toast.makeText(this, "请选择需要观看的轨迹", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ispause) {
                    ispause = true;
                    track_his_iv.setBackgroundResource(R.drawable.on_zk);
                } else {
                    ispause = false;
                    mHandler.sendEmptyMessage(15);
                    track_his_iv.setBackgroundResource(R.drawable.pause_zk);
                }


//                if (isDisplaying) {
//                    isDisplaying = false;
//                    ispause = true;
//                    track_his_iv.setBackgroundResource(R.drawable.on_zk);
//                }else{
//                    ispause = false;
//                    track_his_iv.setBackgroundResource(R.drawable.pause_zk);
//                }

                break;
            default:
                break;
        }
    }

    /**
     * 比较两个时间串的大小
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean compareTime(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Long a = sdf.parse(startTime).getTime();
            Long b = sdf.parse(endTime).getTime();
            if (a > b) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getCurrentTime() {
        Calendar mCalendar = Calendar.getInstance();
        Date time = mCalendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }
}
