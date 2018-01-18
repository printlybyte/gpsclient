package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbActivityManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.mj.gpsclient.R;
import com.mj.gpsclient.Utils.SwitchButton;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.model.Devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FollowingAllTimeActivity extends Activity {
    public static List<Activity> activityList = new LinkedList<Activity>();
    public static final String ROUTE_PLAN_NODE = "routePlanNode";

    private TextureMapView mMapView_allTime;
    private BaiduMap mBaiduMap_allTime;
    private ImageView iv_back_follow;
    private SwitchButton sc;
    private Dialog dialog;
    private BitmapDescriptor bdA;
    private BitmapDescriptor bdB;
    private BitmapDescriptor bdC;
    private BDLocation mBDLocation = null;

    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    private String mSDCardPath = null;
    String authinfo = null;

    private SharedPreferences sp;
    private String deviceImei;
    private String deviceName;
    private Devices bean;
    private String loadName;
    private RequestQueue mRequestQueue;
    private Timer timer;
    private String startTime;
    private final static String TAG = "FollowingA";

    private boolean isDraw = true;//是否请求到数据并画点
    private boolean isDestroyed = false;//Activity是否destroyed
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_following_all_time);


        FollowingAllTimeActivity.activityList.add(this);


//        if (initDirs()) {
//            initNavi();
//        }

        AbActivityManager.getInstance().addActivity(this);
        mMapView_allTime = (TextureMapView) findViewById(R.id.bmapView_follow_allTime);
        mBaiduMap_allTime = mMapView_allTime.getMap();
        iv_back_follow = (ImageView) findViewById(R.id.iv_back_Follow_allTime);
        sc = (SwitchButton) findViewById(R.id.switch_bt_allTime);
        sp = getSharedPreferences("Switch", MODE_PRIVATE);
        bean = PubUtil.bean;
        deviceImei = bean.getIMEI();
        deviceName = bean.getName();
        loadName = bean.getUserName();
        BaiduMapClickListener();
        SwitchListener();
        GetSwitchStatus();
        bdA = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_track_navi_end);
        bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        bdC = BitmapDescriptorFactory.fromResource(R.drawable.historymark);
        // 定位初始化
        initLocation();
        iv_back_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }


    // 定位初始化
    private void initLocation() {

        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);//0是定位一次  1000是一秒一次
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void GetRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
    }

    private void BaiduMapClickListener() {
        mBaiduMap_allTime.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            private InfoWindow mInfoWindow;

            @Override
            public boolean onMarkerClick(final Marker mMarker) {

                View view = LayoutInflater.from(FollowingAllTimeActivity.this)
                        .inflate(R.layout.info_maplayout, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView info_time_tv = (TextView) view
                        .findViewById(R.id.info_time_tv);
                TextView info_lat_tv = (TextView) view
                        .findViewById(R.id.info_lat_tv);
                TextView info_lng_tv = (TextView) view
                        .findViewById(R.id.info_lng_tv);
                TextView info_position_tv = (TextView) view
                        .findViewById(R.id.info_position_tv);
                LatLng ll = mMarker.getPosition();
                String info = mMarker.getTitle();
                String[] infos = info.split("/n");
                if (infos.length == 3) {
                    title.setText("您的位置");
                    title.setTextSize(18);
                    title.setGravity(Gravity.CENTER);
                    info_time_tv.setText("定位时间：" + infos[1]);
                    info_lng_tv.setText("经度：" + ll.longitude);
                    info_lat_tv.setText("纬度：" + ll.latitude);
                    info_position_tv.setText("地址：" + infos[2]);
                } else {
                    title.setText("终端名称：" + deviceName);
                    info_time_tv.setText("在线时间：" + infos[1]);
                    info_lng_tv.setText("经度：" + ll.longitude);
                    info_lat_tv.setText("纬度：" + ll.latitude);
                }

                mInfoWindow = new InfoWindow(BitmapDescriptorFactory
                        .fromView(view), ll, -47, null);
                mBaiduMap_allTime.showInfoWindow(mInfoWindow);
                return true;
            }
        });
        mBaiduMap_allTime.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {

                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap_allTime.hideInfoWindow();

            }
        });

    }

    private void GetSwitchStatus() {
        String status = sp.getString(deviceImei, "");
        if (status.equals("0")) {//说明switch按钮状态是关闭状态
            sc.setChecked(false);
            PubUtil.switchIsOn = false;
            //Todo 请求最近一次的位置信息
            mHandler.sendEmptyMessage(11);
        } else if (status.equals("1")) {//说明switch按钮状态是开启状态
            sc.setChecked(true);
            PubUtil.switchIsOn = true;
            //TOdo 开启实时跟踪轨迹

        } else {
            sc.setChecked(false);
            //Todo 请求最近一次的位置信息
            mHandler.sendEmptyMessage(11);
        }
    }

    private void GetTrackBack() {
        GetRequestQueue();
        String url = Constant.URL + "/WebService/GLService.asmx/GetTrackBack";
        final String endTime = PubUtil.getDateToString(System.currentTimeMillis());
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                if (str == null) {
                    return;
                }
                ArrayList<Devices> arrays = new ArrayList<Devices>();
                String json = PubUtil.getStr(str);
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray list = obj.getJSONArray("Model");
                    for (int i = 0; i < list.length(); i++) {
                        Devices bean = new Devices();
                        bean.setLat(list.getJSONObject(i).getString("Latitude"));
                        bean.setLng(list.getJSONObject(i).getString("Longitude"));
                        bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
                        arrays.add(bean);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = arrays;
                    mHandler.sendMessage(msg);

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

    /**
     * 请求最近一次的位置信息
     */
    private void RequestLastestPosition() {
        GetRequestQueue();
        String url = Constant.URL + "/WebService/GLService.asmx/GetMemberPositioningByImeis";
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String str) {
                if (str == null) {
                    return;
                }
                String json = PubUtil.getStr(str);
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray list = obj.getJSONArray("Model");
                    for (int i = 0; i < list.length(); i++) {
                        Devices bean = new Devices();
                        bean.setIMEI(list.getJSONObject(i).getString("Imei"));
                        bean.setLat(list.getJSONObject(i).getString("Latitude"));
                        bean.setLng(list.getJSONObject(i).getString("Longitude"));
                        bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
                        bean.setSpeed(list.getJSONObject(i).getString("Speed"));
                        bean.setOrientation(list.getJSONObject(i).getString("Direction"));
                        bean.setLocalType(list.getJSONObject(i).getString("LocType"));
                        //TODO 将请求的点在地图上展示
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("imeis", deviceImei);
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    private void SwitchListener() {
        sc.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                ShowDialog();
                if (!isChecked) {//关闭
                    editor.putString(deviceImei, "0");
                    editor.commit();
                    PubUtil.switchIsOn = false;
                    //TOdo  关闭timer
                    mHandler.sendEmptyMessage(21);
                    //TODO  请求最近一次的位置信息
                    mHandler.sendEmptyMessage(11);
                    //开启定位
                    initLocation();
                } else {//开启
                    editor.putString(deviceImei, "1");
                    editor.commit();
                    PubUtil.switchIsOn = true;
                    startTime = PubUtil.getDateToString(System.currentTimeMillis());
                    mBaiduMap_allTime.clear();
                    //TODO　开始跟踪轨迹
                    mHandler.sendEmptyMessage(13);
                }
            }
        });


    }

    private void ShowDialog() {
        dialog = new Dialog(this, R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();

    }

    @Override
    protected void onPause() {
        // activity 暂停时同时暂停地图控件
        mMapView_allTime.onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        // activity 恢复时同时恢复地图控件
        mMapView_allTime.onResume();
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        mHandler.sendEmptyMessage(21);
        ShutDownDialog();
        isDestroyed = true;
        // activity 销毁时同时销毁地图控件
        mMapView_allTime.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        super.onDestroy();
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
    }

    private void ShutDownDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
//        if (lineStatus.equals("离线")) {
//            Toast.makeText(FollowingActivity.this, "离线状态无法获取最新位置信息",
//                    Toast.LENGTH_LONG).show();
//        }
    }

    private LatLng getLastInfo(ArrayList<Devices> array_trace, int i) {
        Devices bean = array_trace.get(i);
        String lat = bean.getLat();
        String lng = bean.getLng();
        LatLng latlng = new LatLng(Double.parseDouble(lat),
                Double.parseDouble(lng));
        LatLng latlng1 = PubUtil.convert(latlng);
        LatLng latlng1_ = PubUtil.DfInfomation(latlng1);
        return latlng1_;

    }

    private void LetPointsInScreen(ArrayList<LatLng> arrays) {
        if (arrays != null && arrays.size() == 1) {
            mBaiduMap_allTime.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(arrays.get(0), 18));
            return;
        }
        if (arrays != null && arrays.size() == 2) {
            double distance = PubUtil.getDistance(arrays.get(0).latitude, arrays.get(0).longitude, arrays.get(1).latitude, arrays.get(1).longitude);
            if (distance < 20) {
                mBaiduMap_allTime.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(arrays.get(0), 18));
                return;
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng mlatlng : arrays) {
            builder.include(mlatlng);
        }
        LatLngBounds bounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds, 500, 300);

        mBaiduMap_allTime.animateMapStatus(u);
    }


    Handler mHandler = new Handler() {

        private Marker mMarkerA;
        private Marker mMarkerB;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://最近一次位置信息
                    ArrayList<LatLng> latlng0s = new ArrayList<LatLng>();
                    ShutDownDialog();
                    mBaiduMap_allTime.clear();
                    Devices mDevice = (Devices) msg.obj;
                    String Imei = mDevice.getIMEI();
                    String onTime = mDevice.getOnTime();
                    String lat = mDevice.getLat();
                    String lng = mDevice.getLng();
                    if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {
                        Toast.makeText(FollowingAllTimeActivity.this, "服务端没有位置信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LatLng latlng = new LatLng(Double.parseDouble(lat),
                            Double.parseDouble(lng));
                    LatLng latlng1 = PubUtil.convert(latlng);
                    LatLng latlng_1 = PubUtil.DfInfomation(latlng1);
                    MarkerOptions ooC = null;
                    if (isDestroyed) {
                        return;
                    }
                    if (sp.getString(deviceImei, "").equals("1")) {
                        ooC = new MarkerOptions().position(latlng_1).title(Imei + "/n" + onTime)
                                .icon(bdA).zIndex(9).draggable(true);
                        latlng0s.add(latlng_1);
                    } else {
                        ooC = new MarkerOptions().position(latlng_1).title(Imei + "/n" + onTime)
                                .icon(bdC).zIndex(9).draggable(true);
                        latlng0s.add(latlng_1);
                        if (mBDLocation != null) {
                            String locTime = mBDLocation.getTime();
                            String address = mBDLocation.getAddrStr();
                            Double locLat = mBDLocation.getLatitude();
                            Double locLng = mBDLocation.getLongitude();
                            LatLng latlng_ = new LatLng(locLat, locLng);
                            LatLng latlng_loc_ = PubUtil.DfInfomation(latlng_);
                            MarkerOptions ooD = new MarkerOptions()
                                    .position(latlng_loc_).icon(bdB)
                                    .title(deviceName + "/n" + locTime + "/n" + address).zIndex(9)
                                    .draggable(true);
                            mBaiduMap_allTime.addOverlay(ooD);
                            latlng0s.add(latlng_loc_);
                        }

                    }
                    mBaiduMap_allTime.addOverlay(ooC);
                    // 设置中心点
                    LetPointsInScreen(latlng0s);
                    isDraw = true;
                    break;
                case 1://轨迹
                    ShutDownDialog();
                    ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
                    ArrayList<Devices> array_trace = (ArrayList<Devices>) msg.obj;
                    if (array_trace != null && array_trace.size() > 0) {

                        for (int i = 0; i < array_trace.size(); i++) {
                            Devices bean = array_trace.get(i);
                            String onTime1 = bean.getOnTime();
                            String lat_track = bean.getLat();
                            String lng_track = bean.getLng();
                            LatLng latlng_track = new LatLng(Double.parseDouble(lat_track),
                                    Double.parseDouble(lng_track));
                            LatLng latlng_track_ = PubUtil.convert(latlng_track);
                            latlng_1 = PubUtil.DfInfomation(latlng_track_);
                            if (isDestroyed) {
                                return;
                            }
                            if (i == 0) {
                                if (mMarkerA != null) {
                                    mMarkerA.remove();
                                }
                                MarkerOptions ooA = new MarkerOptions()
                                        .position(latlng_1).icon(bdA)
                                        .title(deviceImei + "/n" + onTime1).zIndex(9)
                                        .draggable(true);
                                mMarkerA = (Marker) (mBaiduMap_allTime.addOverlay(ooA));
                            }
                            if (i > 0 && i == array_trace.size() - 1) {
                                if (mMarkerB != null) {
                                    mMarkerB.remove();
                                }
                                LatLng lastLocation = getLastInfo(array_trace, i);
                                MarkerOptions ooB = new MarkerOptions()
                                        .position(lastLocation).icon(bdB)
                                        .title(deviceImei + "/n" + onTime1).zIndex(9)
                                        .draggable(true);
                                mMarkerB = (Marker) (mBaiduMap_allTime.addOverlay(ooB));
                            }
                            latlngs.add(latlng_1);
                            // // 反Geo搜索
                            // mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            // .location(latlng));
                        }
                        if (latlngs.size() > 1) {
                            OverlayOptions ooPolyline = new PolylineOptions()
                                    .width(3).color(0xAAFF0000).points(latlngs);
                            Polyline mPolyline = (Polyline) mBaiduMap_allTime.addOverlay(ooPolyline);

                            LetPointsInScreen(latlngs);
                        } else {
                            LetPointsInScreen(latlngs);

                        }
                    } else {
                        //TODO 请求最近一次的位置信息
                        RequestLastestPosition();
                    }
                    isDraw = true;
                    break;
                case 11:// 请求最近一次的位置信息
                    RequestLastestPosition();
                    break;
                case 12://开启跟踪模式
                    GetTrackBack();
                    break;
                case 13://开启timer
                    if (timer == null) {
                        TimerTask task = new TimerTask() {
                            public void run() {
                                if (isDraw && sp.getString(deviceImei, "").equals("1")) {
                                    isDraw = false;
                                    mHandler.sendEmptyMessage(12);
                                }

                            }
                        };

                        timer = new Timer(true);
                        timer.schedule(task, 1000, 5000); // 延时1000ms后执行，1000ms执行一次
                    }
                    break;
                case 21:
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接的位置
            if (location == null || mBaiduMap_allTime == null) {
                return;
            }
            mBDLocation = location;
//            getLocInfo(location);
        }

        public void onConnectHotSpotMessage(String s, int i) {
            Log.i("qweqwe",""+s);
        }

    }

    private void getLocInfo(BDLocation location) {
        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        Log.i("qweqwe", "获取纬度信息:" + latitude + "    获取经度信息:" + longitude + "   :错误信息：" + errorCode);
        drawMark(latitude,longitude);
    }

    private void drawMark(double lat,double lot){

        //定义Maker坐标点
//        LatLng point = new LatLng(39.963175, 116.400244);
        LatLng point = new LatLng(lat, lot);
       //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap_allTime.addOverlay(option);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    private void initNavi() {
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                Log.i("QWEQWE",""+authinfo);
                FollowingAllTimeActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(FollowingAllTimeActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void initSuccess() {
                Toast.makeText(FollowingAllTimeActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
//                hasInitSuccess = true;
                initSetting();
            }
            @Override
            public void initStart() {
                Toast.makeText(FollowingAllTimeActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void initFailed() {
                Toast.makeText(FollowingAllTimeActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, null, null);
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10341938");
        BNaviSettingManager.setNaviSdkParam(bundle);
        initListener();
    }
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initListener() {

        if (BaiduNaviManager.isNaviInited()) {
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.WGS84);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09_MC);
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
        }

    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
//        mCoordinateType = coType;
//        if (!hasInitSuccess) {
//            Toast.makeText(BNDemoMainActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
//        }


        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(116.333591, 39.933401, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            Toast.makeText(FollowingAllTimeActivity.this, "算路陈宫", Toast.LENGTH_SHORT).show();
            Log.i("QWEQWE","算路陈宫");
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

//            for (Activity ac : activityList) {
//
//                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
//
//                    return;
//                }
//            }
            Intent intent = new Intent(FollowingAllTimeActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(FollowingAllTimeActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }
}