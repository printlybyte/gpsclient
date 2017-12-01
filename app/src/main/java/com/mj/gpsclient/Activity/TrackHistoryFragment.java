package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ab.fragment.AbFragment;
import com.ab.fragment.AbSampleDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.wheel.AbNumericWheelAdapter;
import com.ab.view.wheel.AbWheelUtil;
import com.ab.view.wheel.AbWheelView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.mj.gpsclient.R;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.DebugLog;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.global.XMLHelper;
import com.mj.gpsclient.model.DeviceLocation;
import com.mj.gpsclient.model.DevicePosition;
import com.mj.gpsclient.view.DevicesDetailView;
import com.mj.gpsclient.view.HostoryDetailView;
import com.mj.gpsclient.view.SelectStartEndTimeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by majin on 15/5/28.
 */
public class TrackHistoryFragment extends AbFragment implements View.OnClickListener {
    private MyApplication application;
    private Activity mActivity = null;
    private AbSoapUtil mAbSoapUtil ;
    // 地图相关
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BitmapDescriptor bdA ,bdS;
    private InfoWindow mInfoWindow;
    private String deviceName;
    private List<DeviceLocation> locationList;
    private boolean isFristLoad =true;
    private SelectStartEndTimeView selectStartEndTimeView;
    private HostoryDetailView hostoryDetailView;
    private int MAX_NUM_MARKER =2000;
    private DrawMapThread drawMapThread;
    private int playIndex;
    private AbHttpUtil mAbHttpUtil = null;
    List<LatLng> points = new ArrayList<LatLng>();
    //用于轮子控件
    private  TextView timeText_wheel_select =null;
    //记录选择的是开始还是结束
    private TextView  StartEndTime_wheel_select;

    private boolean isPause =false;

    private boolean isReloading =false;

    private Marker markerStart;
    private Marker markerNow;

    private LatLng prePoint=null;


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int result = msg.what;
            switch (result) {
                case 0: //加载数据完成
                    Log.e("盖伦","1");
                    int size =msg.arg1;
                    isReloading=false;
                    ((DevicesTrackActivity)mActivity).setPlayOrPauseEnable(true);
                    mj_hideLoadView();
                    markerStart=null;
                    markerNow=null;
                    mBaiduMap.clear();
                    mBaiduMap.setMaxAndMinZoomLevel(3.0f, 16.0f);
                    if (size>1){
                        AbToastUtil.showToast(mActivity, "数据获取成功！");
                       // AbDialogUtil.removeDialog(mActivity);

                        //showLoadView();
                        resetMap();
                        clearMapClickListens();
                        if(drawMapThread!=null){
                            drawMapThread.isCancel =true;
                            drawMapThread.interrupt();
                            drawMapThread=null;
                        }
                        playIndex=0;
//                        drawMapThread =new DrawMapThread();
//                        drawMapThread.start();
                        ((DevicesTrackActivity)mActivity).setStepProgress(playIndex +" / "+locationList.size());
                        ((DevicesTrackActivity)mActivity).setPlayOrPauseEnable(true);
                        sendEmptyMessage(8);

                    }else{
                        AbToastUtil.showToast(mActivity, "没有获得轨迹数据！");
                        //AbDialogUtil.removeDialog(mActivity);
                    }

                    break;
                case 3:
                    Log.e("盖伦","2");
                    mj_showLoadView();
                    ((DevicesTrackActivity)mActivity).setPlayOrPauseEnable(false);
                   // if(isFristLoad){
                       //getDeviceTrack("2015-04-28T00:00:00","2015-05-28T23:59:00");
                        isReloading =true;
                         mHandler.removeMessages(8);
                        getDeviceTrackByTime(0);
                   // }


                    break;
                case 4://activity导航条切换
                    isReloading =true;
                    mHandler.removeMessages(8);
                    int index =msg.arg1;
                    //mj_showLoadView();
                    getDeviceTrackByTime(index);
                   // getDeviceTrack("2015-04-28T00:00:00","2015-05-28T23:59:00");
                    break;
                case 5: //头部选择的隐藏显示
                    toggleTopTiteSelect((msg.arg1>0));
                    break;
                case 6://切换到了其他的页面
                    Log.d("majin","要停止画了-----");
                    if(drawMapThread!=null){
                        drawMapThread.isCancel =true;
                        drawMapThread.interrupt();
                        drawMapThread=null;
                    }
                    break;
                case 7://全部画完
                    setMapClickListens();
                    //AbDialogUtil.removeDialog(mActivity);
                    //showContentView();
                    mj_hideLoadView();
                    break;

                case 8://paly
                    if(!((DevicesTrackActivity)mActivity).isPauseStep()&&!isReloading){
                        playOverOneStep();
                    }
                    break;

                case 9://pause
                    mHandler.removeMessages(8);
                    break;

                case 10://play one finish
                    setMapClickListens();
                    mHandler.removeMessages(8);
                    sendEmptyMessageDelayed(8,800);
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
        View v =inflater.inflate(R.layout.fragment_track_history, container, false);

        initDatas();
        initViews(v);

        //initOverlay();
//        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(new LatLng(116.34427, 39.84136), 14);
//        mBaiduMap.animateMapStatus(u);

        //加载数据必须
        this.setAbFragmentOnLoadListener(new AbFragmentOnLoadListener(){

            @Override
            public void onLoad() {
                //getDeviceTrack();

            }

        });
        selectStartEndTimeView.setCallback(new SelectStartEndTimeView.SelectStartEndTimeCallback() {
            @Override
            public void onStartClick(View view) {
                StartEndTime_wheel_select =(TextView)view;
                testWheel(view);
            }

            @Override
            public void onEndClick(View view) {
                StartEndTime_wheel_select =(TextView)view;
                testWheel(view);
            }

            @Override
            public void onOkClick(View view,Date dataStart, Date dateEnd) {
                 if(dataStart==null||null==dateEnd||dataStart.getTime()>dateEnd.getTime()){
                     AbToastUtil.showToast(mActivity, "日期格式错误");
                     return;
                 }
                getDeviceTrack2(formatWebserviceDate(dataStart),formatWebserviceDate(dateEnd));
                selectStartEndTimeView.hide();

            }
        });

        return v;
    }

    private void resetMap(){
        Log.e("盖伦","3");
        mBaiduMap.clear();
        mBaiduMap.setMaxAndMinZoomLevel(3.0f,17.0f);
        MapStatus mMapStatus = new MapStatus.Builder()
                .zoom(17)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        markerStart=null;
        markerNow=null;
        prePoint=null;
    }



    private boolean isPause(){
        return (((DevicesTrackActivity)mActivity).isPauseStep()||isReloading);
    }

    private void initDatas(){
        Log.e("盖伦","4");
        deviceName = ((DevicesTrackActivity)mActivity).getDeviceName();
        locationList =new ArrayList<DeviceLocation>();
        locationList.clear();
        drawMapThread =new DrawMapThread();
        drawMapThread.isCancel=false;
        bdA = BitmapDescriptorFactory.fromResource(R.drawable.historymark);
        bdS = BitmapDescriptorFactory.fromResource(R.drawable.icon_track_navi_end);
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);

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
    private void showMarkerInfoWindow(Marker marker){
        DevicesDetailView devicesDetailView =new DevicesDetailView(mActivity);
        DevicePosition devicePosition =new DevicePosition();
        Bundle bu =marker.getExtraInfo();
        devicePosition.setName(deviceName);
        if(null!=bu){
            String onTime =(String)bu.get("OnTime");
            devicePosition.setOnTime(onTime!=null?onTime:"");
        }

        devicesDetailView.setData(devicePosition);
        LatLng ll = marker.getPosition();
        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(devicesDetailView), ll, -47, null);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private  void convert(DeviceLocation deviceLocation){
        CoordinateConverter c =new CoordinateConverter();
        c.from(CoordinateConverter.CoordType.GPS);
        c.coord(deviceLocation.getLatLng());

        LatLng lg =c.convert();
        deviceLocation.setLatitude(lg.latitude+"");
        deviceLocation.setLongitude(lg.longitude+"");
        deviceLocation.setLatLng(new LatLng(lg.latitude, lg.longitude));

    }


    private void playOverOneStep(){
        mj_hideLoadView();
        if(1+playIndex>=locationList.size()){
            AbToastUtil.showToast(mActivity, "轨迹已经全部播放完成！");
            ((DevicesTrackActivity)mActivity).setStepProgress(locationList.size() +" / "+locationList.size());
            return;
        }
        points.clear();
        double d=0;
        DeviceLocation locationPre=null;
        DeviceLocation locationNow=null;
        do{
            playIndex++;
            if(playIndex<locationList.size()){
                locationNow =locationList.get(playIndex);
                locationPre =locationList.get(playIndex-1<0?0:playIndex-1);
            }else{
                break;
            }
            convert(locationPre);
            convert(locationNow);
            if(prePoint==null){
                prePoint =locationNow.getLatLng();
            }
            points.add(prePoint);
            points.add(locationNow.getLatLng());
            d=DistanceUtil.getDistance(locationNow.getLatLng(), prePoint);
            DebugLog.e("d="+d);
        }while(d<10);

        if(locationNow!=null&&locationPre!=null){
            //画出当前的点和之前的一个点以前的轨迹
            OverlayOptions ooPolyline = new PolylineOptions().width(6)
                    .color(0xff379bff).points(points);
            if(isPause()){
                return;
            }
            mBaiduMap.addOverlay(ooPolyline);

            OverlayOptions ooAStart = new MarkerOptions().position(locationNow.getLatLng()).icon(bdS)
                    .draggable(true);
            if(isPause()){
                return;
            }
            if(markerStart==null){
                markerStart = (Marker) mBaiduMap.addOverlay(ooAStart);
                Bundle buStart =new Bundle();
                buStart.putString("OnTime",locationNow.getOnTime());
                buStart.putString("id",locationNow.getId()+"");
                markerStart.setExtraInfo(buStart);
            }else{

            }

            OverlayOptions ooA = new MarkerOptions().position(locationNow.getLatLng()).icon(bdA)
                    .draggable(true);
            if(isPause()){
                return;
            }

            if(markerNow==null){
                markerNow= (Marker) mBaiduMap.addOverlay(ooA);
                prePoint =locationNow.getLatLng();
            }else{
                markerNow.setPosition(locationNow.getLatLng());
                prePoint =locationNow.getLatLng();

            }
            Bundle bu =new Bundle();
            bu.putString("OnTime",locationNow.getOnTime());
            bu.putString("id",locationNow.getId()+"");
            markerNow.setExtraInfo(bu);

            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(locationNow.getLatLng())
                    .build();
            if(null!=hostoryDetailView&&!hostoryDetailView.isShown()){
                hostoryDetailView.setVisibility(View.VISIBLE);
            }
            hostoryDetailView.setData(locationNow);
            if(isPause()){
                return;
            }
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
            // AbToastUtil.showToast(mActivity, "播放了一步---"+playIndex);
            ((DevicesTrackActivity)mActivity).setStepProgress(playIndex + " / " + locationList.size());
            mHandler.sendEmptyMessage(10);

        }

    }



    private void playOverOneStep2(){
        mj_hideLoadView();
       // playIndex+=5;
        if(++playIndex>=locationList.size()){
            AbToastUtil.showToast(mActivity, "轨迹已经全部播放完成！");
            ((DevicesTrackActivity)mActivity).setStepProgress(locationList.size() +" / "+locationList.size());
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.setMaxAndMinZoomLevel(3.0f, 16.0f);
        points.clear();
        List<DeviceLocation>  locationList_host =locationList.subList(0,playIndex<2?2:playIndex);
        int size =locationList_host.size();
        for (DeviceLocation location:locationList_host){
//            convert(location);
            points.add(location.getLatLng());
        }
        //画出以前的轨迹
        if(size>900){
            for(int i=0;i<=size/900;i++){
                List<LatLng> point_Temp = new ArrayList<LatLng>();
                if ((i+1)*900>size){
                    point_Temp =points.subList(i*900,size);
                }else{
                    point_Temp =points.subList(i*900,(i+1)*900);
                }
                Log.d("majin","画了一次了---"+i );
                if(isPause()||point_Temp.size()<2){
                    break;
                }
                OverlayOptions ooPolyline = new PolylineOptions().width(6)
                        .color(0xFF57BB47).points(point_Temp);

                mBaiduMap.addOverlay(ooPolyline);


            }
        }else{

            OverlayOptions ooPolyline = new PolylineOptions().width(6)
                    .color(0xff379bff).points(points);
            if(isPause()){
                return;
            }
            mBaiduMap.addOverlay(ooPolyline);

        }


        //画start图标
        DeviceLocation locationStart=locationList.get(0);

        OverlayOptions ooAStart = new MarkerOptions().position(locationStart.getLatLng()).icon(bdS)
                .draggable(true);
        if(isPause()){
            return;
        }
        Marker markerStart = (Marker) mBaiduMap.addOverlay(ooAStart);
        Bundle buStart =new Bundle();
        buStart.putString("OnTime",locationStart.getOnTime());
        buStart.putString("id",locationStart.getId()+"");
        markerStart.setExtraInfo(buStart);


        //
        DeviceLocation location=locationList.get(playIndex);

        OverlayOptions ooA = new MarkerOptions().position(location.getLatLng()).icon(bdA)
                .draggable(true);
        if(isPause()){
            return;
        }
        Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
        Bundle bu =new Bundle();
        bu.putString("OnTime",location.getOnTime());
        bu.putString("id",location.getId()+"");
        marker.setExtraInfo(bu);

        //定位
        LatLng llA =location.getLatLng();
        //定位到该点
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(llA);
        if(isPause()){
            return;
        }
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMapStatus(u);
       // AbToastUtil.showToast(mActivity, "播放了一步---"+playIndex);
        ((DevicesTrackActivity)mActivity).setStepProgress(playIndex +" / "+locationList.size());
        mHandler.sendEmptyMessage(10);
    }



    private void initOverlay(){

        points.clear();
        for (DeviceLocation location:locationList){
            points.add(location.getLatLng());
        }

//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Log.d("majin","onMarkerClick---");
//                if(drawMapThread!=null&&drawMapThread.isFinished){
//                    Log.d("majin","onMarkerClick---true---------");
//                    showMarkerInfoWindow( marker);
//                }else{
//                    AbToastUtil.showToast(mActivity, "地图数据加载中,请稍等...");
//                }
//
//                return true;
//            }
//        });

        int size =points.size();
        Log.d("majin","size size---"+size );
        if(size>900){
            for(int i=0;i<=size/900;i++){
                if (Thread.currentThread().isInterrupted()){
                    break;
                }
                List<LatLng> point_Temp = new ArrayList<LatLng>();
                if ((i+1)*900>size){
                    point_Temp =points.subList(i*900,size);
                }else{
                    point_Temp =points.subList(i*900,(i+1)*900);
                }
                Log.d("majin","画了一次了---"+i );
                OverlayOptions ooPolyline = new PolylineOptions().width(6)
                        .color(0xFF57BB47).points(point_Temp);
                mBaiduMap.addOverlay(ooPolyline);

            }
        }else{

            OverlayOptions ooPolyline = new PolylineOptions().width(6)
                    .color(0xFF57BB47).points(points);
            mBaiduMap.addOverlay(ooPolyline);

        }



//        LatLng llA = new LatLng(39.963175, 116.400244);
        Log.d("majin","开始化图标---"+locationList.size());
        if(size <MAX_NUM_MARKER){

            for (int i=0;i<locationList.size();i++) {
                if (Thread.currentThread().isInterrupted()){
                    break;
                }
                DeviceLocation location=locationList.get(i);
                OverlayOptions ooA = new MarkerOptions().position(location.getLatLng()).icon(bdA)
                       .draggable(true);

                Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
//                marker.setVisible(false);
                Bundle bu =new Bundle();
                bu.putString("OnTime",location.getOnTime());
                bu.putString("id",location.getId()+"");
                marker.setExtraInfo(bu);
                Log.d("majin","ThreadNmae ="+Thread.currentThread().getId()+ "化图标---" + i);
            }
        }else{
            mActivity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    AbToastUtil.showToast(mActivity, "当前轨迹较多,请耐心等候！");
                }

            });

            int x= size/MAX_NUM_MARKER;
            int count =0;
            for (int i=0;i<locationList.size();i++) {
                if (Thread.currentThread().isInterrupted()){
                    break;
                }
                DeviceLocation location=locationList.get(i);
                OverlayOptions ooA = new MarkerOptions().position(location.getLatLng()).icon(bdA)
                        .draggable(true);
                Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
                Bundle bu =new Bundle();
                bu.putString("OnTime",location.getOnTime());
                bu.putString("id",location.getId()+"");
                marker.setExtraInfo(bu);
                //i+=(x-1);
                Log.d("majin","ThreadNmae ="+Thread.currentThread().getId()+ "化图标---" + i);
            }
        }


        LatLng llA =locationList.get(size/2).getLatLng();
        //定位到该点
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(llA);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMapStatus(u);



    }

    private void clearMapClickListens(){
//        mMapView.setEnabled(false);
//        mMapView.setOnTouchListener(null);
//        mBaiduMap.setOnMapTouchListener(null);
//        mBaiduMap.setOnMapDoubleClickListener(null);
        mBaiduMap.setOnMapClickListener(null);
        mBaiduMap.setOnMarkerClickListener(null);
    }

    private void setMapClickListens(){
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
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("majin", "onMarkerClick---");
//                if(drawMapThread!=null&&drawMapThread.isFinished){
//                    Log.d("majin","onMarkerClick---true---------");
                showMarkerInfoWindow(marker);
//               }else{
//                    AbToastUtil.showToast(mActivity, "地图数据加载中,请稍等...");
//                }
                return true;
            }
        });
    }

    private void getDeviceTrack2(String startTime,String endTime){
        mj_showLoadView();
        ((DevicesTrackActivity)mActivity).setPlayOrPauseEnable(false);
        isFristLoad=false;
        String url = Constant.URL+"/WebService/GLService.asmx/GetTrackBack";
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
        mAbHttpUtil.setTimeout(10000);
        AbRequestParams params = new AbRequestParams();
        params.put("userName",application.mUser.getUserName());
        params.put("imei",deviceName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        mAbHttpUtil.post(url, params, new AbStringHttpResponseListener() {
            @Override
            public void onSuccess(int statusCode, String content) {
                XMLHelper.getResult("string", content, new XMLHelper.CallBack() {
                    @Override
                    public void getResult(String result) {
                        JSONObject jobj=null;
                        try {
                            jobj=new JSONObject(result);
                            Log.i("德玛西亚","22222222222");
                            AsynparseJson(jobj);
                        } catch (JSONException e) {
                            mj_hideLoadView();
                            AbToastUtil.showToast(mActivity, "系统返回异常！");
                            e.printStackTrace();
                        }
                        String r =jobj.optString("Result");
                        if(result.equals("ok")) {
                            Log.i("德玛西亚","22222222222");
                            AsynparseJson(jobj);
                        }else {
                            AbDialogUtil.removeDialog(mActivity);
                        }
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

    private void getDeviceTrack3(String startTime,String endTime){
        mj_showLoadView();
        ((DevicesTrackActivity)mActivity).setPlayOrPauseEnable(false);
        isFristLoad=false;
        String urlString="http://101.66.255.173/GLService.asmx";
        String nameSpace="http://tempuri.org/";
        String methodName = "GetTrackBack";
        AbSoapParams params = new AbSoapParams();
        params.put("userName",application.mUser.getUserName());
        params.put("memberName",deviceName);

        //http://www.iteye.com/problems/39880
//        params.put("startTime","2015-05-28T00:00:00.123+08:00");
//        params.put("endTime","2015-05-28T23:59:00.123+08:00");
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        Log.d("majin", "params" + params.toString());
        mAbSoapUtil= AbSoapUtil.getInstance(mActivity);
        mAbSoapUtil.setTimeout(10000);
//        AbProgressDialogFragment dialogFragment= AbDialogUtil.showProgressDialog(mActivity, 0,
//                "获取中...");
//        dialogFragment.setCancelable(false);

        mAbSoapUtil.call(urlString,nameSpace,methodName,params, new AbSoapListener() {

            //获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, SoapObject object) {
                Log.d("majin", "statusCode=" + statusCode + "SoapObject=" + object.toString());
                String GetTrackBackResult = object.getPrimitivePropertyAsString("GetTrackBackResult");
                JSONObject jobj=null;
                try {
                    jobj=new JSONObject(GetTrackBackResult);
                } catch (JSONException e) {
                    mj_hideLoadView();
                    AbToastUtil.showToast(mActivity, "系统返回异常！");
                    e.printStackTrace();
                }
                String result =jobj.optString("Result");
                if(result.equals("ok")){
                    AsynparseJson(jobj);
//                    JSONArray jarr =  jobj.optJSONArray("Model");
//                    locationList =new ArrayList<DeviceLocation>();
//                    locationList.clear();
//
//                    for(int i=0;i<jarr.length();i++){
//                        JSONObject jmodeObj=null;
//                        try {
//                            jmodeObj= jarr.getJSONObject(i);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        DeviceLocation location =new DeviceLocation(jmodeObj.optString("OnTime"),jmodeObj.optString("Latitude"),jmodeObj.optString("Longitude"));
//                        locationList.add(location);
//                    }
//                    if(locationList.size()>1){
//                        if(locationList.size()>980){
//                            locationList= locationList.subList(0,900);
//                        }
//                        AbToastUtil.showToast(mActivity, "获取轨迹数据成功！");
//                        mHandler.sendEmptyMessageDelayed(0,200);
//                    }else{
//                        AbToastUtil.showToast(mActivity, "没有获得轨迹数据！");
//                    }


                }else {
                    AbDialogUtil.removeDialog(mActivity);
                }

            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {

                Log.d("majin","onFailure  "+statusCode+"   "+content);
//                AbDialogUtil.removeDialog(mActivity);
                mj_hideLoadView();
                AbToastUtil.showToast(mActivity, "系统返回异常！");
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, SoapFault fault) {
                Log.d("majin","onFailure  "+statusCode );
                //AbDialogUtil.removeDialog(mActivity);
                mj_hideLoadView();
                AbToastUtil.showToast(mActivity, "系统返回异常！");
            }

            // 开始执行前
            @Override
            public void onStart() {
                Log.d("majin","onStart");
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                Log.d("majin","onFinish");
            };

        });

    }

    private void getDeviceTrackByTime(int index){
        //2015-04-28T00:00:00
//        String startTime =formatWebserviceDate(new Date());
//        String endTime=formatWebserviceDate(new Date());
          String startTime ="";
          String endTime="";
         Calendar startCal =null;
        switch (index){
            case 0: //今天
                startCal=Calendar.getInstance();
                startCal.set(Calendar.HOUR_OF_DAY,0);
                startCal.set(Calendar.MINUTE,0);
                startCal.set(Calendar.SECOND,0);
                startTime =formatWebserviceDate(startCal.getTime());
                endTime =formatWebserviceDate(new Date());

                break;
            case 1://过去三天
                startCal=Calendar.getInstance();
                startCal.add(Calendar.DATE, -3);
                startCal.set(Calendar.HOUR_OF_DAY,0);
                startCal.set(Calendar.MINUTE,0);
                startCal.set(Calendar.SECOND,0);
                startTime =formatWebserviceDate(startCal.getTime());
                endTime =formatWebserviceDate(new Date());
                break;
            case 2://过去一周
                startCal=Calendar.getInstance();
                startCal.add(Calendar.DATE, -7);
                startCal.set(Calendar.HOUR_OF_DAY,0);
                startCal.set(Calendar.MINUTE,0);
                startCal.set(Calendar.SECOND,0);
                startTime =formatWebserviceDate(startCal.getTime());
                endTime =formatWebserviceDate(new Date());
                break;
            case 3://自定义
//                startCal=Calendar.getInstance();
//                startCal.add(Calendar.DATE, -7);
//                startCal.set(Calendar.HOUR_OF_DAY,0);
//                startCal.set(Calendar.MINUTE,0);
//                startCal.set(Calendar.SECOND,0);
//                startTime =formatWebserviceDate(startCal.getTime());
//                endTime =formatWebserviceDate(new Date());
               // this.topTiteSelect.setVisibility(View.VISIBLE);
                Message msg =Message.obtain();
                msg.what =5;
                msg.arg1 =1;
                mHandler.sendMessage(msg);
                return;
        }
        if(index!=3){
            Message msg =Message.obtain();
            msg.what =5;
            msg.arg1 =-1;
            mHandler.sendMessage(msg);
        }
        getDeviceTrack2(startTime,endTime);

    }

    private void toggleTopTiteSelect(boolean isShow){
        if(!isShow){
            this.selectStartEndTimeView.hide();
        }else{
            this.selectStartEndTimeView.show();
        }
    }


    private String formatWebserviceDate(Date date){
        String time =new java.text.SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(date);
        StringBuilder sb=new StringBuilder(time);
        sb.insert(10,'T');
        Log.d("majin","StringBuilder="+sb.toString());
        return sb.toString();
    }

    private void AsynparseJson(final JSONObject jobj){
        if(null == jobj)
        return;
        new Thread( ){
            @Override
            public void run() {
                JSONArray jarr =  jobj.optJSONArray("Model");
                locationList =new ArrayList<DeviceLocation>();
                locationList.clear();

                for(int i=0;i<jarr.length();i++){
                    JSONObject jmodeObj=null;
                    try {
                        jmodeObj= jarr.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DeviceLocation location =new DeviceLocation(i,jmodeObj.optString("OnTime"),jmodeObj.optString("Latitude"),jmodeObj.optString("Longitude"));
                    locationList.add(location);
                }

//                    if(locationList.size()>980){
//                        locationList= locationList.subList(0,900);
//                    }
                    Message msg =Message.obtain();
                    msg.what =0;
                    msg.arg1 =locationList.size();
                    mHandler.sendMessageDelayed(msg, 10);

            }
        }.start();


    }

    @Override
    public void onPause() {
        if(null!= mHandler){
            mHandler.sendEmptyMessage(9);
        }
        if(drawMapThread!=null){
            drawMapThread.isCancel =true;
            drawMapThread.interrupt();
            drawMapThread=null;
        }
        if(null!=markerNow&&1+playIndex<locationList.size()){
            markerNow.remove();
            markerNow=null;
        }

        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        showContentView();
        Log.d("majin", "-------onResume");
        mMapView.onResume();
        if(((DevicesTrackActivity)mActivity).getCurrentIndexFragmentIndex()==1){
            mHandler.removeMessages(8);
            mHandler.sendEmptyMessage(8);
        }


    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        // 关闭定位图层
        if(drawMapThread!=null){
            drawMapThread.isCancel =true;
            drawMapThread.interrupt();
            drawMapThread=null;
        }
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private void initViews(View view){
        timeText_wheel_select =new TextView(application);
        // 初始化地图
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        this.selectStartEndTimeView = (SelectStartEndTimeView) view.findViewById(R.id.selectStartEndTimeView);
        this.selectStartEndTimeView.setVisibility(View.GONE);

        hostoryDetailView = (HostoryDetailView) view.findViewById(R.id.location_detail_View);
        hostoryDetailView.setVisibility(View.INVISIBLE);
        mBaiduMap = mMapView.getMap();


//        View rightViewApp = ((DevicesTrackActivity)mActivity).mInflater.inflate(R.layout.more_btn, null);
//        AbTitleBar mAbTitleBar =((DevicesTrackActivity)mActivity).getTitleBar();
//        mAbTitleBar.addRightView(rightViewApp);
    }

    @Override
    public void onClick(View view) {

    }
    private void testWheel(View view){

        View mTimeView2=null;

        mTimeView2 = ((DevicesTrackActivity)this.getActivity()).mInflater.inflate(R.layout.choose_three, null);
        initWheelTime(mTimeView2,timeText_wheel_select);

        final AbWheelView mWheelView1 = (AbWheelView)mTimeView2.findViewById(R.id.wheelView0);
        mWheelView1.setAdapter(new AbNumericWheelAdapter(2000, 2020));
        // 可循环滚动
        mWheelView1.setCyclic(true);
        // 添加文字
        mWheelView1.setLabel("      年");
        // 初始化时显示的数据
        mWheelView1.setCurrentItem(16);
        mWheelView1.setValueTextSize(35);
        mWheelView1.setLabelTextSize(35);
        mWheelView1.setLabelTextColor(0x80000000);
        mWheelView1.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));

        AbSampleDialogFragment abs = AbDialogUtil.showDialog(mTimeView2, Gravity.BOTTOM);
        abs.setOnDismissListener(new DialogInterface.OnDismissListener(){

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                int index = mWheelView1.getCurrentItem();
                String val = mWheelView1.getAdapter().getItem(index);
                Log.d("majin","val =" +val+"---"+timeText_wheel_select.getText());

                String date_hour =timeText_wheel_select.getText().toString().substring(4);
                date_hour =date_hour.substring(0,date_hour.length()-3);
                StartEndTime_wheel_select.setText(val+date_hour);
            }
        });

    }

    public void initWheelTime(View mTimeView,TextView mText){
        final AbWheelView mWheelViewMD = (AbWheelView)mTimeView.findViewById(R.id.wheelView1);
        final AbWheelView mWheelViewMM = (AbWheelView)mTimeView.findViewById(R.id.wheelView2);
        final AbWheelView mWheelViewHH = (AbWheelView)mTimeView.findViewById(R.id.wheelView3);
        Button okBtn = (Button)mTimeView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button)mTimeView.findViewById(R.id.cancelBtn);
        mWheelViewMD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        AbWheelUtil.initWheelTimePicker( mText, mWheelViewMD, mWheelViewMM, mWheelViewHH, okBtn, cancelBtn, 2013, 1, 1, 10, 0, true);
    }


    class DrawMapThread extends Thread{
        boolean isCancel =false;
        boolean isFinished=false;

        @Override
        public void run() {
            super.run();
            isFinished=false;
            try{
                Thread.sleep(10);
                if (!isCancel){
                  initOverlay();
                 //playOverStep();
                }
            }catch(InterruptedException e){
                e.printStackTrace();
                Log.d("majin","停止异常------------------------------");

                return;
            }
            isFinished=true;
            mHandler.sendEmptyMessage(7);
        }
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
//                textViewLoction.setText(content);
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
