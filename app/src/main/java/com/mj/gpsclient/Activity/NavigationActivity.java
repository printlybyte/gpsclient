package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.mj.gpsclient.R;
import com.mj.gpsclient.Utils.DrivingRouteOverlay;
import com.mj.gpsclient.Utils.OverlayManager;
import com.mj.gpsclient.Utils.SharedPreferencesUtils;
import com.mj.gpsclient.Utils.SwitchIconView;
import com.mj.gpsclient.adapter.RouteLineAdapter;
import com.mj.gpsclient.bean.UserLatLng;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.model.Devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mj.gpsclient.Utils.PublicUtils.getCurrentTime;

public class NavigationActivity extends Activity {
    private TextureMapView mMapView_allTime;
    private BaiduMap mBaiduMap_allTime;
    private BitmapDescriptor bdA;
    private BitmapDescriptor bdB;
    private String startTime;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean isDestroyed = false;//Activity是否destroyed
    private BitmapDescriptor bdC;
    private RequestQueue mRequestQueue;
    private String deviceImei;
    private String deviceName;
    private Devices bean;
    private String loadName;
    private String xxx;
    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private static final int CASIONE = 1, CASELING = 0, CASITHREE = 21, CASIFIVE = 22;//实时刷新21 开始导航22
    private ImageView iv_back_follow;
    private Switch sc;
    private static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    private String mSDCardPath = null;
    String authinfo = null;


    //路径规划

    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    String startNodeStr = "龙轩宾馆";
    String endNodeStr = "白堆子地铁站";
    int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。
    DrivingRouteResult nowResultdrive = null;
    boolean hasShownDialogue = false;
    //    boolean useDefaultIcon = false;
    OverlayManager routeOverlay = null;
    RouteLine route = null;
    private SwitchIconView switchIcon2;
    private LinearLayout button2;
    private SwitchIconView switchIcon1;
    private LinearLayout button1;
    private TextView mbtn_tetx1,mbtn_text2;
    private Context mContext;
    private String TITLEONE = "livelaodingx";//实时刷新标识
    private String TITLEONETWO = "livelaodingxz";//s实时导航标识

    private GeoCoder mSearchSerch;//妮地理搜索编码实例
    private int adrsnum = 1; //获取两次位置
    private LocationClientOption option;
    private boolean realTimerefresh = false;
    private boolean realTimerefresh_once = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CASIONE:
                    Toast.makeText(NavigationActivity.this, "ASDASDAS", Toast.LENGTH_SHORT).show();
                    break;
                case CASELING:
                    ArrayList<LatLng> latlng0s = new ArrayList<LatLng>();


                    Devices mDevice = (Devices) msg.obj;
                    String Imei = mDevice.getIMEI();
                    String onTime = mDevice.getOnTime();
                    String lat = mDevice.getLat();
                    String lng = mDevice.getLng();
                    String adre = mDevice.getAderess();
                    xxx = lng;
                    if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {
                        Toast.makeText(NavigationActivity.this, "服务端没有位置信息", Toast.LENGTH_SHORT).show();
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
                    ooC = new MarkerOptions().position(latlng_1).title("dui")
                            .icon(bdA).zIndex(9).draggable(true);
                    latlng0s.add(latlng_1);

                    mBaiduMap_allTime.addOverlay(ooC);
                    // 设置中心点
                    LetPointsInScreen(latlng0s);
//                    isDraw = true;
                    Log.i("qweqwe： ", "===" + Imei + "    " + onTime + "            " + lat + "     " + lng + "   " + adre);


                    String zhelat = (String) SharedPreferencesUtils.getParam(getBaseContext(), "latxs", "");
                    String zhelot = (String) SharedPreferencesUtils.getParam(getBaseContext(), "lotxs", "");
                    double dzhelat = Double.parseDouble(zhelat);
                    double dzhelot = Double.parseDouble(zhelot);


                    LatLng p1 = new LatLng(dzhelat, dzhelot);
                    saveTargetDeveseInfo(adre, dzhelat + "", dzhelot + "", onTime, latlng_1.latitude + "", latlng_1.longitude + "");


                    double distance = GetShortDistance(dzhelat, dzhelot, latlng_1.latitude, latlng_1.longitude);

                    if (distance < 100) {
                        List<LatLng> points = new ArrayList<LatLng>();
                        points.add(p1);
                        points.add(latlng_1);
                        OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                        mBaiduMap_allTime.addOverlay(ooPolyline);
                        Toast.makeText(mContext, "距离太近无法进行路线规划", Toast.LENGTH_SHORT).show();
                    } else {


                        getSerchAddress(p1);//发起逆地理位置检索
                        getSerchAddress(latlng_1);//发起逆地理位置检索

                    }

                    Log.i("QWEQWE", "折现1" + dzhelat + "折现2" + dzhelot + "折现3" + latlng_1.latitude + "折现4" + latlng_1.longitude);

                    break;
                case CASITHREE:
//                    option.setScanSpan(1000);;
                    Log.i("qweqwe", "实时刷新开启");
//                    mHandler.sendEmptyMessageDelayed(CASITHREE, 1000);
                    break;
                case CASIFIVE://开始导航

                    break;

                case 9998:
                    String adr = msg.obj.toString();
                    if (adrsnum == 1) {
                        Log.i("qweqwe", "获取到的地址1是" + adr);
                        adrsnum = 2;
                    } else {
                        Log.i("qweqwe", "获取到的地址2是" + adr);
                        routePlanning(adr, adr);
                        adrsnum = 1;
                    }
                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mContext = this;
        initViews();
        BaiduMapClickListener();
        // 定位初始化
        initLocation();


    }

    /**
     * @author liuguodong
     * 单次舒心点击事件
     * @Time 2017/11/30 13:31
     */
    public void planing(View v) {
        UserLatLng us = getLatlng();

        double distance = GetShortDistance(us.getLat1(), us.getLat2(), us.getLat3(), us.getLat4());

        if (distance < 100) {
            Toast.makeText(mContext, "距离太近无法进行路线规划", Toast.LENGTH_SHORT).show();
        } else {
            LatLng la = new LatLng(us.getLat1(), us.getLat2());
            LatLng lo = new LatLng(us.getLat3(), us.getLat4());
            getSerchAddress(la);//发起逆地理位置检索
            getSerchAddress(lo);//发起逆地理位置检索

        }

    }

    /**
     * @author liuguodong
     * 获取本地保存的经纬度信息
     * @Time 2017/11/30 15:43
     */
    private UserLatLng getLatlng() {
        UserLatLng us = null;
        if (us == null) {
            us = new UserLatLng();
        }
        double a = Double.parseDouble(sp.getString("latx", "0"));
        double b = Double.parseDouble(sp.getString("lotx", "0"));
        double c = Double.parseDouble(sp.getString("zhexian3", "0"));
        double d = Double.parseDouble(sp.getString("zhexian4", "0"));
        us.setLat1(a);
        us.setLat2(b);
        us.setLat3(c);
        us.setLat4(d);
        Log.i("QWEQWE", "================" + us.getLat1() + us.getLat2() + us.getLat3() + us.getLat4());
        return us;
    }

    /**
     * @author liuguodong
     * 判断是否支持路线规划或者导航
     * @Time 2017/11/30 14:27
     */
    private boolean isSupportNavigation() {
        UserLatLng us = getLatlng();
        double distance = GetShortDistance(us.getLat1(), us.getLat2(), us.getLat3(), us.getLat4());
        if (distance > 100) {
            return true;
        }
        return false;
    }


    /**
     * 初始化控件
     */

    private void initViews() {
        SharedPreferencesUtils.setParam(mContext, TITLEONE, false);
        SharedPreferencesUtils.setParam(mContext, TITLEONETWO, false);
        initRefshBtn();

        AbActivityManager.getInstance().addActivity(this);
        sp = getSharedPreferences("targetinfo", MODE_PRIVATE);
        editor = sp.edit();
        bean = PubUtil.bean;
        deviceImei = bean.getIMEI();
        deviceName = bean.getName();
        loadName = bean.getUserName();
        mMapView_allTime = (TextureMapView) findViewById(R.id.bmapView_follow_allTime);
        mBaiduMap_allTime = mMapView_allTime.getMap();
        bdA = BitmapDescriptorFactory
                .fromResource(R.mipmap.nagative_mubiao);
        bdB = BitmapDescriptorFactory.fromResource(R.mipmap.nagative_wofang);
        bdC = BitmapDescriptorFactory.fromResource(R.drawable.historymark);

        iv_back_follow = (ImageView) findViewById(R.id.iv_back_Follow_allTimen);
        //返回按钮的监听事件
        iv_back_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        sc = (Switch) findViewById(R.id.switch_bt_allTimen);
//        SwitchListener();
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
        mMapView_allTime.onDestroy();
        isDestroyed = true;
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
        mSearch.destroy();
        mSearchSerch.destroy();

        realTimerefresh_once = false;//销毁的时候恢复初始设置
        realTimerefresh = false;//销毁的时候恢复初始设置
        super.onDestroy();
    }

    /**
     * 设置地图初始化
     */
    // 定位初始化
    private void initLocation() {

        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setIsNeedAddress(true);// 设置定位结果包含地址信息
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//0是定位一次  1000是一秒一次
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocClient.setLocOption(option);
        mLocClient.start();

        // 妮地理编码初始化
        mSearchSerch = GeoCoder.newInstance();
        mSearchSerch.setOnGetGeoCodeResultListener(listenerGeoCode);

        //路线规划实例
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listner);


        //导航初始化
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10341938");
        BNaviSettingManager.setNaviSdkParam(bundle);

    }

    /**
     * @author liuguodong
     * @method 发起妮地理搜索
     * @Pparameter
     * @Pparameter
     * @other
     * @Time 2017/11/30 11:04
     */
    private void getSerchAddress(LatLng var1) {
        mSearchSerch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(var1));

    }


    /**
     * @author liuguodong
     * @method 逆地理编码监听
     * @Pparameter
     * @Pparameter
     * @other
     * @Time 2017/11/30 11:02
     */
    OnGetGeoCoderResultListener listenerGeoCode = new OnGetGeoCoderResultListener() {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                Log.i("qweqwe", "没有检索到结果");
                return;
            }
            Log.i("qweqwe", "获取到的地址是：" + result.getAddress());

            //获取地理编码结果
        }

        @Override

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Log.i("qweqwe", "没有检索到反向结果");
                return;
                //没有找到检索结果
            }
            Log.i("qweqwe", "获取到的反向地址是：" + result.getAddress());
            Message message = new Message();
            message.obj = result.getAddress();
            message.what = 9998;
            mHandler.sendMessage(message);//发送逆地理编码实现

            //获取反向地理编码结果
        }
    };

    /**
     * @author liuguodong
     * @method 初始化实时按钮
     * @Pparameter
     * @Pparameter
     * @other
     * @Time 2017/11/29 15:45
     */
    private void initRefshBtn() {
        switchIcon1 = (SwitchIconView) findViewById(R.id.switchIconView3);
        switchIcon1.switchState(false);
        button1 = (LinearLayout) findViewById(R.id.button3_native);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSupportNavigation()) {
                    Toast.makeText(mContext, "距离太近，无法导航", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isLoadingRefi = (boolean) SharedPreferencesUtils.getParam(mContext, TITLEONETWO, false);
                if (!isLoadingRefi) {
                    switchIcon1.switchState(true);
                    SharedPreferencesUtils.setParam(mContext, TITLEONETWO, true);
                    Toast.makeText(mContext, "开启导航", Toast.LENGTH_SHORT).show();
                } else {
                    switchIcon1.switchState(false);
                    SharedPreferencesUtils.setParam(mContext, TITLEONETWO, false);
                    Toast.makeText(mContext, "关闭导航", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switchIcon2 = (SwitchIconView) findViewById(R.id.switchIconView2);
        switchIcon2.switchState(false);
        button2 = (LinearLayout) findViewById(R.id.button2_native);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoadingRefi = (boolean) SharedPreferencesUtils.getParam(mContext, TITLEONE, false);
                if (!isLoadingRefi) {
                    switchIcon2.switchState(true);
//                    mHandler.sendEmptyMessage(CASITHREE);
                    SharedPreferencesUtils.setParam(mContext, TITLEONE, true);
                    realTimerefresh = true;
                    Toast.makeText(mContext, "开启实时刷新", Toast.LENGTH_SHORT).show();
                } else {
                    switchIcon2.switchState(false);
//                    mHandler.removeMessages(CASITHREE);
                    SharedPreferencesUtils.setParam(mContext, TITLEONE, false);
                    realTimerefresh = false;
                    Toast.makeText(mContext, "关闭实时刷新", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * @author liuguodong
     * @method routePlanning
     * @Pparameter
     * @Pparameter
     * @other 路线规划
     * @Time 2017/11/24 11:35
     */
    private void routePlanning(String startNodeStr, String endNodeStr) {
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", startNodeStr);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", endNodeStr);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode).to(enNode));
        nowSearchType = 1;

    }

    /**
     * @author liuguodong
     * @method listner
     * @Pparameter
     * @Pparameter
     * @other 线路规划监听
     * @Time 2017/11/24 11:35
     */
    OnGetRoutePlanResultListener listner = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(NavigationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                    nodeIndex = -1;//节点浏览
                if (drivingRouteResult.getRouteLines().size() > 1) {
                    nowResultdrive = drivingRouteResult;

                    route = nowResultdrive.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap_allTime);
                    mBaiduMap_allTime.setOnMarkerClickListener(overlay);
                    routeOverlay = overlay;
                    overlay.setData(nowResultdrive.getRouteLines().get(0));
                    overlay.addToMap();

                    overlay.zoomToSpan();


                } else if (drivingRouteResult.getRouteLines().size() == 1) {
                    route = drivingRouteResult.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap_allTime);
                    routeOverlay = overlay;
                    mBaiduMap_allTime.setOnMarkerClickListener(overlay);
                    overlay.setData(drivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();

                } else {
                    Log.d("route result", "结果数<0");
                    return;
                }

            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
            return null;
        }
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }


    /**
     * swich 监听
     */

    private void SwitchListener() {
        sc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {//关闭

                } else {//开启
                    Toast.makeText(NavigationActivity.this, "ADA", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /**
     * 保存页面进来的信息
     */
    private void saveTargetDeveseInfo(String adress, String lat, String lot, String time, String zx3, String zx4) {
        if (adress.length() < 1) {
            adress = "没有获取位置信息";
        }
        if (lat.length() < 1) {
            lat = "暂无信息";
        }
        if (lot.length() < 1) {
            lot = "暂无信息";
        }
        if (time.length() < 1) {
            time = "暂无信息";
            ;
        }

        if (editor == null) {
            if (sp == null) {
                sp = getSharedPreferences("targetinfo", MODE_PRIVATE);
            }
            editor = sp.edit();
        }
        editor.putString("adressx", adress);
        editor.putString("latx", lat);
        editor.putString("lotx", lot);
        editor.putString("timex", time);

        editor.putString("zhexian3", zx3);
        editor.putString("zhexian4", zx4);
        editor.commit();
    }

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

            if (!realTimerefresh_once) {  //进来的时候只执行一次
                getLocInfo(location);
                realTimerefresh_once = true;
            }

            if (realTimerefresh) {  //进来的时候只执行一次
                getLocInfo(location);

            }

        }


        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            Log.i("qweqwe", "" + s);
        }

    }

    private void getLocInfo(BDLocation location) {

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
        int errorCode = location.getLocType();
        setAddress(location.getAddrStr());
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        Log.i("qweqwe", "获取纬度信息:" + latitude + "    获取经度信息:" + longitude + "   :错误信息：" + errorCode + " 地址信息：" + location.getAddrStr());

        drawMark(latitude, longitude);
    }

    private void drawMark(double lat, double lot) {
        mBaiduMap_allTime.clear();
        //定义Maker坐标点
        //        LatLng point = new LatLng(39.963175, 116.400244);
        LatLng point = new LatLng(lat, lot);
        //构建Marker图标
//        BitmapDescriptor bitmap = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_marka);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .title("me")
                .position(point)
                .icon(bdB);
        //在地图上添加Marker，并显示
        mBaiduMap_allTime.addOverlay(option);
        SharedPreferencesUtils.setParam(getBaseContext(), "latxs", "" + lat);
        SharedPreferencesUtils.setParam(getBaseContext(), "lotxs", "" + lot);
        RequestLastestPosition();
    }

    /**
     * 地图监听器
     */

    private void BaiduMapClickListener() {
        mBaiduMap_allTime.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            private InfoWindow mInfoWindow;

            @Override
            public boolean onMarkerClick(final Marker mMarker) {

                View view = LayoutInflater.from(NavigationActivity.this)
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
                if ("me".equals(info)) {
                    title.setText("您的位置");
                    title.setTextSize(18);
                    title.setGravity(Gravity.CENTER);
                    info_time_tv.setText("定位时间：" + getCurrentTime());
                    info_lng_tv.setText("经度：" + ll.longitude + ll.longitudeE6);
                    info_lat_tv.setText("纬度：" + ll.latitude);
                    info_position_tv.setText("地址：" + getAddress());
                } else if ("dui".equals(info)) {

                    title.setText("目标位置");
                    title.setTextSize(18);
                    title.setGravity(Gravity.CENTER);
                    if (sp == null) {
                        sp = getSharedPreferences("targetinfo", MODE_PRIVATE);
                    }
                    String timex = sp.getString("timex", "");
                    String latx = sp.getString("zhexian3", "");
                    String lotx = sp.getString("zhexian4", "");
                    String adressx = sp.getString("adressx", "");
                    Log.i("qweqwe", "xasdasdasdas" + latx + "     " + lotx);
                    info_time_tv.setText("定位时间：" + timex);
                    info_lng_tv.setText("经度：" + latx);
                    info_lat_tv.setText("纬度：" + lotx);
                    info_position_tv.setText("地址：" + adressx);
                } else {
                    title.setText("没有获取到位置信息");
                }


                Log.i("qweqwe", "" + mMarker.getTitle());


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

    /**
     * 获取本地的定位地址信息
     */
    private String getAddress() {
        String result = (String) SharedPreferencesUtils.getParam(getBaseContext(), "adress_Ss", "查询中");
        return result;
    }

    /**
     * 保存本地的定位地址信息
     */
    private void setAddress(String address) {
        SharedPreferencesUtils.setParam(getBaseContext(), "adress_Ss", address);
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
                        bean.setAderess(list.getJSONObject(i).getString("Address"));
                        Log.i("qweqwea", "" + bean.getAderess());
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

    /**
     * 请求设备位置信息
     */
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
                map.put("startTime", startTime());
                map.put("endTime", endTime);
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


    private String startTime() {
        return startTime = PubUtil.getDateToString(System.currentTimeMillis());
    }

    private void GetRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
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
                Log.i("QWEQWE", "" + authinfo);
                NavigationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(NavigationActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void initSuccess() {
//                Toast.makeText(NavigationActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
//                hasInitSuccess = true;


                initListener();

            }

            @Override
            public void initStart() {
//                Toast.makeText(NavigationActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void initFailed() {
                Toast.makeText(NavigationActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        }, null, null, null);
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

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * @author liuguodong
     * 四种坐标换算方法
     * @Time 2017/11/30 14:20
     */
    private void initListener() {
        if (BaiduNaviManager.isNaviInited()) {
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.GCJ02);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.WGS84);
//            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09_MC);
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);
        }
    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {

        double a = Double.parseDouble(sp.getString("latx", ""));
        double b = Double.parseDouble(sp.getString("lotx", ""));
        double c = Double.parseDouble(sp.getString("zhexian3", ""));
        double d = Double.parseDouble(sp.getString("zhexian4", ""));
        Log.i("qweqwe", "折现N" + a + "折现N" + b + "折现N" + c + "折现N" + d);


        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(a, b, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
//                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
//                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
                sNode = new BNRoutePlanNode(a, b, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(c, d, "北京天安门", null, coType);

                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
                break;
            }
            case BD09LL: {
//                sNode = new BNRoutePlanNode(a, b, "增光路21号院", null, coType);
//                eNode = new BNRoutePlanNode(c, d, "增光路21 ", null, coType);
//                eNode = new BNRoutePlanNode(39.8997006138,116.3291047966, "北京西站 ", null, coType);

                sNode = new BNRoutePlanNode(b, a, null, null, coType);
                //39.9296920000,116.3315380000
                eNode = new BNRoutePlanNode(d, c, null, null, coType);
                double distance = GetShortDistance(a, b, c, d);
                Log.i("QWEQWE", "=========================距离目标： " + distance);
                if (distance < 100) {
                    switchIcon1.switchState(false);
                    SharedPreferencesUtils.setParam(mContext, TITLEONETWO, false);
                    Toast.makeText(this, "距离太近，无法导航", Toast.LENGTH_SHORT).show();
                    return;
                }
//                searchRoute(sNode,eNode);
//                sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
//                eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
                break;
            }
            default:

        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new NavigationActivity.DemoRoutePlanListener(sNode));
            BNRouteGuideManager.getInstance().setVoiceModeInNavi(BNRouteGuideManager.VoiceMode.Quite);
        }
    }

    /**
     * 近距离转换
     */
    public static double GetShortDistance(double lon1, double lat1, double lon2, double lat2) {
        double DEF_PI = 3.14159265359; // PI
        double DEF_2PI = 6.28318530712; // 2*PI
        double DEF_PI180 = 0.01745329252; // PI/180.0
        double DEF_R = 6370693.5; // radius of earth
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI) {
            dew = DEF_2PI - dew;
        } else if (dew < -DEF_PI) {
            dew = DEF_2PI + dew;
        }
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {

            Toast.makeText(NavigationActivity.this, "算路陈宫", Toast.LENGTH_SHORT).show();
            Log.i("QWEQWE", "算路陈宫");
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
            Intent intent = new Intent(NavigationActivity.this, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(NavigationActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }
}
