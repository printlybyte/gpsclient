package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.fragment.AbFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbToastUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mj.gpsclient.R;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.DebugLog;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.global.XMLHelper;
import com.mj.gpsclient.model.DevicePosition;
import com.mj.gpsclient.model.Devices;
import com.mj.gpsclient.view.DevicesDetailView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majin on 15/5/26.
 */
public class DevicesMonitorFragment extends AbFragment implements View.OnClickListener,OnGetGeoCoderResultListener {

    private MyApplication application;
    private Activity mActivity = null;
    // 地图相关
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private AbSoapUtil mAbSoapUtil ;
    private List<DevicePosition> devicePositions;
    private int playIndex=0;
    private Marker mMarkerA;
    private BitmapDescriptor bdA, bdB,bdS;
    private ImageView backBtn,nextBtn;
    private InfoWindow mInfoWindow;
    private TextView textViewLoction;
    private AbHttpUtil mAbHttpUtil = null;
    //GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int result = msg.what;
            switch (result) {
                case 0: //搜索点击
                    DebugLog.e("case 0---");
//                    getMemberPositioning2();
                    break;
                case 1: //数据获取成功
                    DebugLog.e("case 0---");
                    playDevicePostion();
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = this.getActivity();
        application = (MyApplication) mActivity.getApplication();
        initDatas();
        View v =inflater.inflate(R.layout.fragment_devices_monitor, container, false);
        initViews(v);
        initDatas();
        return v;
    }
    @Override
    public void setResource() {
        //设置加载的资源
        this.setLoadDrawable(R.drawable.ic_load);
        this.setLoadMessage("正在加载,请稍候...");
        this.setTextColor(getResources().getColor(R.color.moji_black_text));

        this.setRefreshDrawable(R.drawable.ic_refresh_f);
        this.setRefreshMessage("请求出错，请重试");
    }
    private void initViews(View view){
        // 初始化地图
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMaxAndMinZoomLevel(3.0f,16.0f);
        backBtn = (ImageView) view.findViewById(R.id.back);
        nextBtn =(ImageView)view.findViewById(R.id.next);
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        textViewLoction = (TextView) view.findViewById(R.id.loaction_GeoCode);
        textViewLoction.setVisibility(View.INVISIBLE);
//        buCarLocation =(ImageView) view.findViewById(R.id.car_location_bu);
    }
    private void initDatas(){
         devicePositions =new ArrayList<DevicePosition>();
        bdA = BitmapDescriptorFactory.fromResource(R.drawable.item_27_xx);
        bdB = BitmapDescriptorFactory.fromResource(R.drawable.offline_27_0_xx);
        bdS = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_navi_end);
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
//        mSearch = GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(this);
    }
    private  LatLng  convert(LatLng latLng){
        CoordinateConverter c =new CoordinateConverter();
        c.from(CoordinateConverter.CoordType.GPS);
        c.coord(latLng);
       return  c.convert();
    }

    private  void playDevicePostion(){
        if(playIndex>=0&&playIndex<devicePositions.size()){

            mBaiduMap.clear();
            DevicePosition devicePosition =devicePositions.get(playIndex);
            LatLng llA = new LatLng( devicePosition.getLatitude(),devicePosition.getLongitude());
            getLngLatToAddress(llA);
            llA =convert(llA);
            devicePosition.setLatitude(llA.latitude);
            devicePosition.setLongitude(llA.longitude);
//            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//                    .location(llA));
//            getLngLatToAddress(llA);

            Log.d("majin",llA.toString());
            OverlayOptions ooA=null;

            if (devicePosition.isOnLine()){
                ooA = new MarkerOptions().position(llA).icon(bdA)
                        .zIndex(9).draggable(true);
            }else {
                ooA = new MarkerOptions().position(llA).icon(bdB)
                        .zIndex(9).draggable(true);
            }

            mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
            Bundle bu =new Bundle();
            bu.putInt("index",playIndex);
            mMarkerA.setExtraInfo(bu);
            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d("majin","onMarkerClick---");
                    showMarkerInfoWindow( marker);
                    return true;
                }
            });
            mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    mBaiduMap.hideInfoWindow();
                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            });
//            DevicesDetailView devicesDetailView =new DevicesDetailView(mActivity);
//            devicesDetailView.setData(devicePosition);
//            LatLng ll = mMarkerA.getPosition();
//            InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(devicesDetailView), ll, -47, null);
//            mBaiduMap.showInfoWindow(mInfoWindow);

            //定位到该点
            MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
            MapStatusUpdate u = MapStatusUpdateFactory
                    .newLatLng(llA);
            mBaiduMap.setMapStatus(msu);
            mBaiduMap.setMapStatus(u);
        }
    }

    private void showMarkerInfoWindow(Marker marker){
        DevicesDetailView devicesDetailView =new DevicesDetailView(mActivity);
        devicesDetailView.setData(devicePositions.get(playIndex));
        LatLng ll = marker.getPosition();
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(devicesDetailView), ll, -47, null);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private void getMemberPositioning2() {
        mj_showLoadView();
        String url = Constant.URL+"/WebService/GLService.asmx/GetMemberPositioning";
        AbRequestParams params = new AbRequestParams();
        params.put("UserName",application.mUser.getUserName());
        StringBuffer sb =new StringBuffer();
        List<Devices> deviceses =((MainActivity) mActivity).getDevicesAll();
        for(Devices devices:deviceses){
            sb.append(devices.getName()+",");
        }
        String names =sb.toString();
        names =sb.substring(0,names.lastIndexOf(','));
        DebugLog.e(names);
        params.put("Names", names);
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
        mAbHttpUtil.setTimeout(10000);
        mAbHttpUtil.post(url, params, new AbStringHttpResponseListener() {
            @Override
            public void onSuccess(int statusCode, String content) {
                XMLHelper.getResult("string", content, new XMLHelper.CallBack() {
                    @Override
                    public void getResult(String result) {
                        JSONObject jobj=null;
                        try {
                            jobj=new JSONObject(result);
                        } catch (JSONException e) {
                            AbToastUtil.showToast(mActivity, "系统返回异常！");
                            e.printStackTrace();
                        }
                        String r =jobj.optString("Result");
                        if(r.equals("ok")){
                            try {
                                parseMemberPositioningJson(jobj.getJSONArray("Model"), devicePositions);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            AbToastUtil.showToast(mActivity, "获取数据成功！");
                            mHandler.sendEmptyMessage(1);

                        }else{
                            AbToastUtil.showToast(mActivity, "获取数据异常！");
                        }

                        mj_hideLoadView();
                    }
                });
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {

            }
        });
    }

    private void getMemberPositioning3(){
        DebugLog.e("---queryOnlineDevices----");
        mj_showLoadView();
        String urlString="http://101.66.255.173/GLService.asmx";
        String nameSpace="http://tempuri.org/";
        String methodName = "GetMemberPositioning";
        AbSoapParams params = new AbSoapParams();
        params.put("UserName",application.mUser.getUserName());
        StringBuffer sb =new StringBuffer();
        List<Devices> deviceses =((MainActivity) mActivity).getDevicesAll();
        for(Devices devices:deviceses){
            sb.append(devices.getName()+",");
        }
        String names =sb.toString();
        names =sb.substring(0,names.lastIndexOf(','));
        DebugLog.e(names);
        params.put("Names",names);
        mAbSoapUtil= AbSoapUtil.getInstance(mActivity);
        mAbSoapUtil.setTimeout(10000);
        mAbSoapUtil.call(urlString,nameSpace,methodName,params, new AbSoapListener() {

            //获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                DebugLog.e(object.toString());
                String GetMemberPositioningResult = object.getPrimitivePropertyAsString("GetMemberPositioningResult");
                JSONObject jobj=null;
                try {
                    jobj=new JSONObject(GetMemberPositioningResult);
                } catch (JSONException e) {
                    AbToastUtil.showToast(mActivity, "系统返回异常！");
                    e.printStackTrace();
                }
                String result =jobj.optString("Result");
                if(result.equals("ok")){
                    try {
                        parseMemberPositioningJson(jobj.getJSONArray("Model"), devicePositions);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AbToastUtil.showToast(mActivity, "获取数据成功！");
                    mHandler.sendEmptyMessage(1);

                }else{
                    AbToastUtil.showToast(mActivity, "获取数据异常！");
                }

                mj_hideLoadView();
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {
                DebugLog.e("onFailure-----"+error.toString()+content);
                AbToastUtil.showToast(mActivity, "获取数据失败！");
                mj_hideLoadView();
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, SoapFault fault) {
                DebugLog.e("onFailure-----");
                AbToastUtil.showToast(mActivity, "获取数据失败！");
                mj_hideLoadView();
            }

            // 开始执行前
            @Override
            public void onStart() {

            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                Log.d("majin","onFinish");
            };

        });

    }

    /**
     * 解析在线刷新的数据
     */
    public void parseMemberPositioningJson(JSONArray jsonArray,List<DevicePosition> devicePositions){
        if (null==devicePositions){
            devicePositions =new ArrayList<DevicePosition>();
        }
        devicePositions.clear();
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jb =jsonArray.optJSONObject(i);
            DevicePosition devicePosition =new DevicePosition();
            devicePosition.setName(jb.optString("Name"));
            boolean isOnline =((MainActivity)mActivity).isOnLine(devicePosition.getName());
            devicePosition.setOnLine(isOnline);
            devicePosition.setOnTime(jb.optString("OnTime"));
            devicePosition.setLocType(jb.optString("LocType"));
            devicePosition.setLongitude(jb.optDouble("Longitude"));
            devicePosition.setLatitude(jb.optDouble("Latitude"));
            devicePosition.setBaseInfo(jb.optString("BaseInfo"));
            devicePosition.setBaseInfo(jb.optString("Speed"));
            devicePosition.setDirection(jb.optString("Direction"));
            devicePosition.setDeviceStatus(jb.optString("DeviceStatus"));
            devicePositions.add(devicePosition);
        }

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        Log.d("majin", "onPause");


        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        showContentView();
        mMapView.onResume();
        DebugLog.e("om------");
        //mHandler.sendEmptyMessage(3);

    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                playIndex++;
                if(playIndex>=devicePositions.size()){
                    playIndex=0;
                }
                playDevicePostion();
                break;
            case R.id.next:
                playIndex--;
                if(playIndex<0){
                    playIndex=devicePositions.size();
                }
                playDevicePostion();
                break;
            default:
                break;
        }
    }


    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            AbToastUtil.showToast(mActivity, "反编码地址信息错误！");
            return;
        }
        //textViewLoction.setText(result.getAddress());
    }


    private void getLngLatToAddress(LatLng latLng) {

        AbRequestParams params =new AbRequestParams();
        params.put("config","POSDES");
        params.put("x1",String.valueOf(latLng.longitude));
        params.put("y1",String.valueOf(latLng.latitude));
        params.put("a_k","38dc8a0ba054afb20cd68e635dae946519ab5bb1d05814a8be31ddb04c2662a66c9da235aac3ced7");
        params.put("config","POSDES");
        mAbHttpUtil.get("http://search1.mapabc.com/sisserver", params, new AbStringHttpResponseListener() {


            @Override
            public void onSuccess(int statusCode, String content) {
                DebugLog.e("content ="+content);
                if(!TextUtils.isEmpty(content)&&null!=textViewLoction){
                    if (!textViewLoction.isShown()){
                        textViewLoction.setVisibility(View.VISIBLE);
                    }
                    String showString = content.trim().replace(';','/');
                    textViewLoction.setText(showString);
                }

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(int statusCode, String content, Throwable error) {

            }
        });


    }

}
