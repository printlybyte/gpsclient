package com.mj.gpsclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.soap.AbSoapListener;
import com.ab.soap.AbSoapParams;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.mj.gpsclient.R;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.model.DeviceLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

public class DevicesTrackingActivity extends AbActivity {


    // 地图相关
   private  MapView mMapView;
    private BaiduMap mBaiduMap;
    // UI相关
    private Button resetBtn;
    private Button clearBtn;
    private AbSoapUtil mAbSoapUtil;
    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    private String DeviceName;
    private String dataJSON;
    private List<DeviceLocation> locationList;
    List<LatLng> points = new ArrayList<LatLng>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_devices_tracking);
        application =(MyApplication)abApplication;
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(locationList.get(10).getLatLng());
        u= MapStatusUpdateFactory.newLatLngZoom(locationList.get(10).getLatLng(),16);
        mBaiduMap.animateMapStatus(u);
        // UI初始化
        clearBtn = (Button) findViewById(R.id.button1);
        resetBtn = (Button) findViewById(R.id.button2);

        View.OnClickListener clearListener = new View.OnClickListener() {
            public void onClick(View v) {
                clearClick();
            }
        };
        View.OnClickListener restListener = new View.OnClickListener() {
            public void onClick(View v) {
                resetClick();
            }
        };

        clearBtn.setOnClickListener(clearListener);
        resetBtn.setOnClickListener(restListener);

        // 界面加载时添加绘制图层
        addCustomElementsDemo();
    }

    /**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElementsDemo() {
		// 添加折线
//		LatLng p1 = new LatLng(39.97923, 116.357428);
//		LatLng p2 = new LatLng(39.94923, 116.397428);
//		LatLng p3 = new LatLng(39.97923, 116.437428);
//        LatLng p4 = new LatLng(39.97923, 116.437428);
//        LatLng p5 = new LatLng(39.97923, 116.437428);
//        LatLng p6 = new LatLng(39.97923, 116.437428);
//        LatLng p7 = new LatLng(39.97923, 116.437428);
        points.clear();
        for (DeviceLocation location:locationList){
            points.add(location.getLatLng());
        }
        OverlayOptions ooPolyline = new PolylineOptions().width(6)
				.color(0xFFFF0000).points(points);
		mBaiduMap.addOverlay(ooPolyline);

    }


    private void initData(){
        Intent in =this.getIntent();
        DeviceName = in.getStringExtra("memberName");
        locationList = new ArrayList<DeviceLocation>();
        locationList.clear();
        dataJSON ="{ \"Result\": \"ok\", \"Model\": [ { \"OnTime\": \"2014-10-23 06:34:38\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:37:18\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:39:58\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:42:38\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:45:18\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:47:58\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:50:38\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:53:18\", \"Longitude\": \"116.37427\", \"Latitude\": \"39.84536\" }, { \"OnTime\": \"2014-10-23 06:55:58\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:57:47\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 06:58:38\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:01:18\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:03:58\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:06:38\", \"Longitude\": \"116.34827\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:09:18\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:11:58\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:14:38\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:17:18\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:19:58\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:22:39\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:25:19\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:27:48\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:27:49\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:27:59\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:28:17\", \"Longitude\": \"116.34427\", \"Latitude\": \"39.84136\" }, { \"OnTime\": \"2014-10-23 07:30:39\", \"Longitude\": \"116.3443\", \"Latitude\": \"39.84131\" }, { \"OnTime\": \"2014-10-23 07:32:54\", \"Longitude\": \"116.34443\", \"Latitude\": \"39.84128\" }, { \"OnTime\": \"2014-10-23 07:33:19\", \"Longitude\": \"116.34443\", \"Latitude\": \"39.88128\" } ] }";
        JSONObject job=null;
        try {
            job =new JSONObject(dataJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jarr =job.optJSONArray("Model");
        for(int i=0;i<jarr.length();i++){
            JSONObject jmode =null;

            try {
                jmode= jarr.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DeviceLocation location =new DeviceLocation(i,jmode.optString("OnTime"),jmode.optString("Latitude"),jmode.optString("Longitude"));
            locationList.add(location);
        }

    }

    private void getDeviceTrack(){

        String urlString="http://101.66.255.173/GLService.asmx";
        String nameSpace="http://tempuri.org/";
        String methodName = "GetTrackBack";
        AbSoapParams params = new AbSoapParams();
        params.put("userName",application.mUser.getUserName());
        params.put("memberName",DeviceName);

        params.put("startTime","");
        params.put("endTime","");
        mAbSoapUtil= AbSoapUtil.getInstance(this);
        mAbSoapUtil.setTimeout(10000);
        mAbSoapUtil.call(urlString,nameSpace,methodName,params, new AbSoapListener() {

            //获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, SoapObject object) {

                String LoginResult = object.getPrimitivePropertyAsString("LoginResult");
                Log.d("majin", "statusCode=" + statusCode + "SoapObject=" + LoginResult);
                AbDialogUtil.removeDialog(DevicesTrackingActivity.this);
                JSONObject jobj=null;
                try {
                    jobj=new JSONObject(LoginResult);
                } catch (JSONException e) {
                    AbToastUtil.showToast(DevicesTrackingActivity.this, "系统返回异常！");
                    e.printStackTrace();
                }
                String result =jobj.optString("Result");
                if(result.equals("ok")){

                }else {
                    AbToastUtil.showToast(DevicesTrackingActivity.this, "用户名或密码错误！");
                }

            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {

            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, SoapFault fault) {

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

    public void resetClick() {
        // 添加绘制元素
        addCustomElementsDemo();
    }

    public void clearClick() {
        // 清除所有图层
        mMapView.getMap().clear();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }




}
