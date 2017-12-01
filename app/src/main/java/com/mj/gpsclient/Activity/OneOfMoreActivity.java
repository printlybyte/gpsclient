package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.ab.global.AbActivityManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.mj.gpsclient.Activity.BaseStrategyHandler.ICallBack;
import com.mj.gpsclient.R;
import com.mj.gpsclient.model.DeviceTrace;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class OneOfMoreActivity extends Activity {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ImageView iv_back_One;
	private String loadName;
	private String imei;
	private String startTime;
	private BitmapDescriptor bdA;
	private BitmapDescriptor bdB;
	private String tag = "OneOfMoreActivity";
	private boolean finishRequest = false;
	private Timer timer;

	private LatLng latlng_1,latlng_2;
	private Marker mMarkerA1,mMarkerA2;
	private Marker mMarkerB1;
	private LatLng lastLocation1;
	private LatLng latlng;
	private boolean BitmapRecycled = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_activity_one);
		AbActivityManager.getInstance().addActivity(this);
		BitmapRecycled = false;
		bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_track_navi_end);
		bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		iv_back_One = (ImageView) findViewById(R.id.iv_back_One);
		mMapView = (MapView) findViewById(R.id.bmapView_One);
		mBaiduMap = mMapView.getMap();
		setOnClick();
		initData();
		initMap();
	}

	private void initMap() {
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(PubUtil
				.convert(latlng), 18));
		// 隐藏logo
		View child = mMapView.getChildAt(1);
		if (child != null
				&& (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}
		// 地图上比例尺
		mMapView.showScaleControl(false);
	}

	private void initData() {
		SharedPreferences sp = getSharedPreferences("userName", MODE_PRIVATE);
		loadName =sp.getString("username", "");
		Intent intent = getIntent();
		imei = intent.getStringExtra("IMEI");
		startTime = intent.getStringExtra("TIME");
		String lat = intent.getStringExtra("LAT");
		String lng = intent.getStringExtra("LNG");
		if(imei==null||startTime==null||lat==null||lng==null){
			return;
		}
		latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
		postDate(loadName, imei, startTime);
		// 开启Timer
		handler.sendEmptyMessage(3);
	}

	private void postDate(String userName, String imei, String startTime) {

		if (!PubUtil.isConnected(getApplicationContext())) {
			Toast.makeText(this, "网络异常，稍后再试", 0).show();
			return;
		}

		FollowDataEngin.getInstance().postNums(userName, imei, startTime,
				new ICallBack<ArrayList<DeviceTrace>>() {

					@Override
					public void onTaskStart() {
						// handler.sendEmptyMessage(6);
					}

					@Override
					public void onTaskFinish(ArrayList<DeviceTrace> params) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	protected void postDataOne2(String num) {
		if (!PubUtil.isConnected(getApplicationContext())) {
			Toast.makeText(this, "网络异常，稍后再试", 0).show();
			return;
		}
		FollowDataEngin.getInstance().postNum(num,
				new ICallBack<ArrayList<DeviceTrace>>() {

					@Override
					public void onTaskStart() {
					}

					@Override
					public void onTaskFinish(ArrayList<DeviceTrace> params) {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = 2;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	private void setOnClick() {
		
		iv_back_One.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				finish();

			}
		});
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker mMarker) {
				View view = LayoutInflater.from(OneOfMoreActivity.this)
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
				String[] infos = info.split("\n");
				String name = infos[0];
				String time = infos[1];
				title.setText("终端名称：" +name );
				info_time_tv.setText("在线时间：" + time);
				info_lng_tv.setText("经度：" + ll.longitude);
				info_lat_tv.setText("纬度：" + ll.latitude);
				InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(view), ll, -47, null);
				mBaiduMap.showInfoWindow(mInfoWindow);
				
				
				
				return true;
			}
		});

	}

	private Handler handler = new Handler() {


		private LatLng latlng2;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 第一个设备返回的数据
				ArrayList<LatLng> latlngs1 = new ArrayList<LatLng>();
				ArrayList<DeviceTrace> array_trace1 = (ArrayList<DeviceTrace>) msg.obj;
				if (array_trace1.size() != 0) {
					if(BitmapRecycled){
						return;
					}
					for (int i = 0; i < array_trace1.size(); i++) {
						DeviceTrace bean = array_trace1.get(i);
						String Imei = bean.getImei();
						String onTime = bean.getOnTime();
						String lat = bean.getLatitude();
						String lng = bean.getLongitude();
						LatLng latlng = new LatLng(Double.parseDouble(lat),
								Double.parseDouble(lng));
						LatLng latlng1 = PubUtil.convert(latlng);
						latlng_1 = PubUtil.DfInfomation(latlng1);
						if (i == 0) {
							if (mMarkerA1 != null) {
								mMarkerA1.remove();
								
							}
							MarkerOptions ooA = new MarkerOptions().position(latlng_1).title(Imei+"\n"+onTime)
									.icon(bdA).zIndex(9).draggable(true);
							
								mMarkerA1 = (Marker) (mBaiduMap.addOverlay(ooA));
							
						}
						if (i > 0 && i == array_trace1.size() - 1) {
							if (mMarkerB1 != null) {
								mMarkerB1.remove();
							}
							lastLocation1 = PubUtil.getLastInfo(array_trace1, i);
							MarkerOptions ooB = new MarkerOptions().position(lastLocation1).title(Imei+"\n"+onTime)
									.icon(bdB).zIndex(9).draggable(true);
							
								mMarkerB1 = (Marker) (mBaiduMap.addOverlay(ooB));
							
						}
						latlngs1.add(latlng_1);
						// // 反Geo搜索
						// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						// .location(latlng));
					}
					if (latlngs1.size() > 1) {
						OverlayOptions ooPolyline = new PolylineOptions().width(3)
								.color(0xAAFF0000).points(latlngs1);
						Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

						//Toast.makeText(OneOfMoreActivity.this, "最少2条位置信息", 0).show();
						mBaiduMap.setMapStatus(MapStatusUpdateFactory
								.newLatLng(lastLocation1));

					} else {
						// 设置中心点
						mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng_1));

					}
					finishRequest = false;
					Log.e(tag, "timer111111");
				} else {
					postDataOne2(imei);
				}
				break;
			case 2:
				if(BitmapRecycled){
					return;
				}
				ArrayList<DeviceTrace> array2 = (ArrayList<DeviceTrace>) msg.obj;
				if (array2.size() != 0) {

					for (int i = 0; i < array2.size(); i++) {
						DeviceTrace bean2 = array2.get(i);
						if (bean2.getLatitude().equals("")
								|| bean2.getLongitude().equals("")) {
							Toast.makeText(OneOfMoreActivity.this, "服务器无上传位置数据", 0).show();
							return;
						}
						String Imei = bean2.getImei();
						String onTime = bean2.getOnTime();
						double a = Double.parseDouble(bean2.getLatitude());
						double b = Double.parseDouble(bean2.getLongitude());
						LatLng latlng = new LatLng(a, b);
						LatLng latlng2_ = PubUtil.convert(latlng);
						latlng2 = PubUtil.DfInfomation(latlng2_);
						mBaiduMap.clear();
						
						if(mMarkerA2!=null){
							mMarkerA2.remove();
						}
						MarkerOptions ooA2 = new MarkerOptions().position(latlng2).title(Imei+"\n"+onTime)
								.icon(bdA).zIndex(9).draggable(true);
						mMarkerA2 = (Marker) (mBaiduMap.addOverlay(ooA2));

					}
					//Toast.makeText(OneOfMoreActivity.this, "最近一次的位置信息", 0).show();
					// 设置中心点
					mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng2));

				} else {
					Toast.makeText(OneOfMoreActivity.this, "没有得到位置信息，请确认IMEI号或网络是否正常", 2000).show();
					return;
				}
				finishRequest = false;
				Log.e(tag, "timer2222222");
				break;
			case 3:// 开启timer

				if (timer == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!finishRequest) {
								finishRequest = true;
								handler.sendEmptyMessage(4);
							}
						}
					};
					timer = new Timer(true);
					timer.schedule(task, 2000, 5000); // 延时1000ms后执行，5000ms执行一次
				}
				break;
			case 4:
				postDate(loadName, imei, startTime);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};
	

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		bdA.recycle();
		bdB.recycle();
		BitmapRecycled = true;
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}

}
