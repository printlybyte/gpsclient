package com.mj.gpsclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
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
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.mj.gpsclient.Activity.BaseStrategyHandler.ICallBack;
import com.mj.gpsclient.R;
import com.mj.gpsclient.model.DeviceTrace;
import com.mj.gpsclient.model.Devices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 在一个Activity中展示多个地图
 */
public class MoreMapsActivity extends FragmentActivity {

	private static final LatLng GEO_BEIJING = new LatLng(39.945, 116.404);
	private static final LatLng GEO_SHANGHAI = new LatLng(31.227, 121.481);
	private static final LatLng GEO_GUANGZHOU = new LatLng(23.155, 113.264);
	private static final LatLng GEO_SHENGZHENG = new LatLng(22.560, 114.064);
	private ArrayList<Devices> arrays = new ArrayList<Devices>();
	private String tag = "more";
	private String loadName;
	private BaiduMap mBaiduMap1;
	private MapView mMapView1;
	private BaiduMap mBaiduMap2;
	private MapView mMapView2;
	private BaiduMap mBaiduMap3;
	private MapView mMapView3;
	private BaiduMap mBaiduMap4;
	private MapView mMapView4;
	private BitmapDescriptor bdA;
	private BitmapDescriptor bdB;
	private String imei1;
	private String imei2;
	private String imei3;
	private String imei4;
	private String time_1;
	private String time_2;
	private String time_3;
	private String time_4;
	private Timer timer1;
	private Timer timer2;
	private Timer timer3;
	private Timer timer4;
	private boolean finishRequest1 = false;
	private boolean finishRequest2 = false;
	private boolean finishRequest3 = false;
	private boolean finishRequest4 = false;
	private boolean onStop = false;
	private LatLng lastLocation1, latlng_1, latlng5;
	private LatLng lastLocation2, latlng_2, latlng6;
	private LatLng lastLocation3, latlng_3, latlng7;
	private LatLng lastLocation4, latlng_4, latlng8;
	private Marker mMarkerA1, mMarkerB1;
	private Marker mMarkerA2, mMarkerB2;
	private Marker mMarkerA3, mMarkerB3;
	private Marker mMarkerA4, mMarkerB4;

	private Marker mMarkerA5;
	private Marker mMarkerA6;
	private Marker mMarkerA7;
	private Marker mMarkerA8;

	private String lat1, lat2, lat3, lat4;
	private String lng1, lng2, lng3, lng4;
	private boolean BitmapRecycled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_maps);
		AbActivityManager.getInstance().addActivity(this);
		BitmapRecycled = false;
		bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_track_navi_end);
		bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		initMap();
		setOnClick();
	}

	private void setOnClick() {

		mBaiduMap1.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei1 == null || time_1 == null || lat1 == null
						|| lng1 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return;
				}
				intent.putExtra("IMEI", imei1);
				intent.putExtra("TIME", time_1);
				intent.putExtra("LAT", lat1);
				intent.putExtra("LNG", lng1);

				startActivity(intent);

			}
		});
		mBaiduMap1.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei1 == null || time_1 == null || lat1 == null
						|| lng1 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return false;
				}
				intent.putExtra("IMEI", imei1);
				intent.putExtra("TIME", time_1);
				intent.putExtra("LAT", lat1);
				intent.putExtra("LNG", lng1);

				startActivity(intent);
				return true;
			}
		});
		mBaiduMap2.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei2 == null || time_2 == null || lat2 == null
						|| lng2 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return;
				}
				intent.putExtra("IMEI", imei2);
				intent.putExtra("TIME", time_2);
				intent.putExtra("LAT", lat2);
				intent.putExtra("LNG", lng2);
				startActivity(intent);

			}
		});
		mBaiduMap2.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei2 == null || time_2 == null || lat2 == null
						|| lng2 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return false;
				}
				intent.putExtra("IMEI", imei2);
				intent.putExtra("TIME", time_2);
				intent.putExtra("LAT", lat2);
				intent.putExtra("LNG", lng2);
				startActivity(intent);
				return true;
			}
		});
		mBaiduMap3.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei3 == null || time_3 == null || lat3 == null
						|| lng3 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return;
				}
				intent.putExtra("IMEI", imei3);
				intent.putExtra("TIME", time_3);
				intent.putExtra("LAT", lat3);
				intent.putExtra("LNG", lng3);
				startActivity(intent);

			}
		});
		mBaiduMap3.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei3 == null || time_3 == null || lat3 == null
						|| lng3 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return false;
				}
				intent.putExtra("IMEI", imei3);
				intent.putExtra("TIME", time_3);
				intent.putExtra("LAT", lat3);
				intent.putExtra("LNG", lng3);
				startActivity(intent);
				return true;
			}
		});
		mBaiduMap4.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei4 == null || time_4 == null || lat4 == null
						|| lng4 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return;
				}
				intent.putExtra("IMEI", imei4);
				intent.putExtra("TIME", time_4);
				intent.putExtra("LAT", lat4);
				intent.putExtra("LNG", lng4);
				startActivity(intent);

			}
		});
		mBaiduMap4.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				Intent intent = new Intent(MoreMapsActivity.this,
						OneOfMoreActivity.class);
				if (imei4 == null || time_4 == null || lat4 == null
						|| lng4 == null) {
					Toast.makeText(MoreMapsActivity.this, "请选择需要跟踪的设备", 0)
							.show();
					return false;
				}
				intent.putExtra("IMEI", imei4);
				intent.putExtra("TIME", time_4);
				intent.putExtra("LAT", lat4);
				intent.putExtra("LNG", lng4);
				startActivity(intent);
				return true;
			}
		});

	}

	/**
	 * 初始化Map
	 */
	private void initMap() {
		TextView tv1 = (TextView) findViewById(R.id.tv_map1);
		TextView tv2 = (TextView) findViewById(R.id.tv_map2);
		TextView tv3 = (TextView) findViewById(R.id.tv_map3);
		TextView tv4 = (TextView) findViewById(R.id.tv_map4);
		arrays = PubUtil.arrays;
		SharedPreferences sp = getSharedPreferences("userName", MODE_PRIVATE);
		loadName =sp.getString("username", "");
		if (arrays != null && arrays.size() > 0) {
			for (int i = 0; i < arrays.size(); i++) {
				Devices bean = arrays.get(i);

				if (i == 0) {
					lat1 = bean.getLat();
					lng1 = bean.getLng();
					if (PubUtil.PageNum != 2) {
						// 记下开始时间
						long time = System.currentTimeMillis();
						time_1 = PubUtil.getDateToString(time);
					} else {
						time_1 = bean.getStartTime();

					}
					imei1 = bean.getIMEI();
					String name1 = bean.getName();
					tv1.setVisibility(View.VISIBLE);
					tv1.setText("终端：" + name1);
					postDate1(loadName, imei1, time_1);
					handler.sendEmptyMessage(13);
				} else if (i == 1) {
					lat2 = bean.getLat();
					lng2 = bean.getLng();
					if (PubUtil.PageNum != 2) {
						// 记下开始时间
						long time = System.currentTimeMillis();
						time_2 = PubUtil.getDateToString(time);
					} else {
						time_2 = bean.getStartTime();

					}
					imei2 = bean.getIMEI();
					String name2 = bean.getName();
					tv2.setVisibility(View.VISIBLE);
					tv2.setText("终端：" + name2);
					postDate2(loadName, imei2, time_2);
					handler.sendEmptyMessage(14);
				} else if (i == 2) {
					lat3 = bean.getLat();
					lng3 = bean.getLng();
					if (PubUtil.PageNum != 2) {
						// 记下开始时间
						long time = System.currentTimeMillis();
						time_3 = PubUtil.getDateToString(time);
					} else {
						time_3 = bean.getStartTime();

					}
					String name3 = bean.getName();
					imei3 = bean.getIMEI();
					tv3.setVisibility(View.VISIBLE);
					tv3.setText("终端：" + name3);
					postDate3(loadName, imei3, time_3);
					handler.sendEmptyMessage(15);
				} else if (i == 3) {
					lat4 = bean.getLat();
					lng4 = bean.getLng();
					if (PubUtil.PageNum != 2) {
						// 记下开始时间
						long time = System.currentTimeMillis();
						time_4 = PubUtil.getDateToString(time);
					} else {
						time_4 = bean.getStartTime();

					}
					String name4 = bean.getName();
					imei4 = bean.getIMEI();
					tv4.setVisibility(View.VISIBLE);
					tv4.setText("终端：" + name4);
					postDate4(loadName, imei4, time_4);
					handler.sendEmptyMessage(16);
				}
			}

		}

		MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(GEO_BEIJING);
		SupportMapFragment map1 = (SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map1));
		mBaiduMap1 = map1.getBaiduMap();
		mMapView1 = map1.getMapView();
		mBaiduMap1.setMapStatus(u1);
		mBaiduMap1.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
		initMapControls(mMapView1);

		MapStatusUpdate u2 = MapStatusUpdateFactory.newLatLng(GEO_SHANGHAI);
		SupportMapFragment map2 = (SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map2));
		mBaiduMap2 = map2.getBaiduMap();
		mMapView2 = map2.getMapView();
		map2.getBaiduMap().setMapStatus(u2);
		mBaiduMap2.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
		initMapControls(mMapView2);

		MapStatusUpdate u3 = MapStatusUpdateFactory.newLatLng(GEO_GUANGZHOU);
		SupportMapFragment map3 = (SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map3));
		mBaiduMap3 = map3.getBaiduMap();
		mMapView3 = map3.getMapView();
		map3.getBaiduMap().setMapStatus(u3);
		mBaiduMap3.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
		initMapControls(mMapView3);

		MapStatusUpdate u4 = MapStatusUpdateFactory.newLatLng(GEO_SHENGZHENG);
		SupportMapFragment map4 = (SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map4));
		mBaiduMap4 = map4.getBaiduMap();
		mMapView4 = map4.getMapView();
		map4.getBaiduMap().setMapStatus(u4);
		mBaiduMap4.setMapStatus(MapStatusUpdateFactory.zoomTo(18));
		initMapControls(mMapView4);
	}

	private void postDate1(String userName, String imei, String startTime) {

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

	private void postDate2(String userName, String imei, String startTime) {

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
						msg.what = 2;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	private void postDate3(String userName, String imei, String startTime) {

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
						msg.what = 3;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	private void postDate4(String userName, String imei, String startTime) {

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
						msg.what = 4;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	/**
	 * 当时间段内不存在位置数据时 请求最近位置数据，
	 * 
	 * @param num
	 *            设备IMEI
	 */
	protected void postDataOne1(String num) {
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
						msg.what = 5;
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
						msg.what = 6;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	protected void postDataOne3(String num) {
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
						msg.what = 7;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	protected void postDataOne4(String num) {
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
						msg.what = 8;
						msg.obj = params;
						handler.sendMessage(msg);
					}

					@Override
					public void onTaskError() {
					}
				});
	}

	private void initMapControls(MapView map) {
		// 隐藏缩放控件
		map.showZoomControls(false);
		// 隐藏logo
		View child = map.getChildAt(1);
		if (child != null
				&& (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}
		// 地图上比例尺
		map.showScaleControl(false);

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 第一个设备返回的数据
				ArrayList<LatLng> latlngs1 = new ArrayList<LatLng>();
				ArrayList<DeviceTrace> array_trace1 = (ArrayList<DeviceTrace>) msg.obj;
				if (array_trace1.size() != 0) {
					if (BitmapRecycled) {
						return;
					}

					for (int i = 0; i < array_trace1.size(); i++) {
						DeviceTrace bean = array_trace1.get(i);
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
							MarkerOptions ooA = new MarkerOptions()
									.position(latlng_1).icon(bdA).zIndex(9)
									.draggable(true);

							mMarkerA1 = (Marker) (mBaiduMap1.addOverlay(ooA));

						}
						if (i > 0 && i == array_trace1.size() - 1) {
							if (mMarkerB1 != null) {
								mMarkerB1.remove();
							}
							lastLocation1 = PubUtil
									.getLastInfo(array_trace1, i);
							MarkerOptions ooB = new MarkerOptions()
									.position(lastLocation1).icon(bdB)
									.zIndex(9).draggable(true);

							mMarkerB1 = (Marker) (mBaiduMap1.addOverlay(ooB));

						}
						latlngs1.add(latlng_1);
						// // 反Geo搜索
						// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						// .location(latlng));
					}
					if (latlngs1.size() > 1) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(3).color(0xAAFF0000).points(latlngs1);
						Polyline mPolyline = (Polyline) mBaiduMap1
								.addOverlay(ooPolyline);

						// Toast.makeText(MoreMapsActivity.this, "最少2条位置信息",
						// 0).show();
						mBaiduMap1.setMapStatus(MapStatusUpdateFactory
								.newLatLng(lastLocation1));

					} else {
						// 设置中心点
						mBaiduMap1.setMapStatus(MapStatusUpdateFactory
								.newLatLng(latlng_1));

					}
					finishRequest1 = false;
					Log.e(tag, "timer1");
				} else {
					postDataOne1(imei1);
				}
				break;
			case 2:// 第2个设备返回的数据
				ArrayList<LatLng> latlngs2 = new ArrayList<LatLng>();
				ArrayList<DeviceTrace> array_trace2 = (ArrayList<DeviceTrace>) msg.obj;
				if (array_trace2.size() != 0) {
					if (BitmapRecycled) {
						return;
					}

					for (int i = 0; i < array_trace2.size(); i++) {
						DeviceTrace bean = array_trace2.get(i);
						String lat = bean.getLatitude();
						String lng = bean.getLongitude();
						LatLng latlng = new LatLng(Double.parseDouble(lat),
								Double.parseDouble(lng));
						LatLng latlng1 = PubUtil.convert(latlng);
						latlng_2 = PubUtil.DfInfomation(latlng1);
						if (i == 0) {
							if (mMarkerA2 != null) {
								mMarkerA2.remove();

							}
							MarkerOptions ooA = new MarkerOptions()
									.position(latlng_2).icon(bdA).zIndex(9)
									.draggable(true);

							mMarkerA2 = (Marker) (mBaiduMap2.addOverlay(ooA));

						}
						if (i > 0 && i == array_trace2.size() - 1) {
							if (mMarkerB2 != null) {
								mMarkerB2.remove();
							}
							lastLocation2 = PubUtil
									.getLastInfo(array_trace2, i);
							MarkerOptions ooB = new MarkerOptions()
									.position(lastLocation2).icon(bdB)
									.zIndex(9).draggable(true);

							mMarkerB2 = (Marker) (mBaiduMap2.addOverlay(ooB));

						}
						latlngs2.add(latlng_2);
						// // 反Geo搜索
						// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						// .location(latlng));
					}
					if (latlngs2.size() > 1) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(3).color(0xAAFF0000).points(latlngs2);
						Polyline mPolyline = (Polyline) mBaiduMap2
								.addOverlay(ooPolyline);

						// Toast.makeText(MoreMapsActivity.this, "最少2条位置信息",
						// 0).show();
						mBaiduMap2.setMapStatus(MapStatusUpdateFactory
								.newLatLng(lastLocation2));

					} else {
						// 设置中心点
						mBaiduMap2.setMapStatus(MapStatusUpdateFactory
								.newLatLng(latlng_2));

					}
					finishRequest2 = false;
					Log.e(tag, "timer2");
				} else {
					postDataOne2(imei2);
				}
				break;
			case 3:// 第3个设备返回的数据
				ArrayList<LatLng> latlngs3 = new ArrayList<LatLng>();
				ArrayList<DeviceTrace> array_trace3 = (ArrayList<DeviceTrace>) msg.obj;
				if (array_trace3.size() != 0) {
					if (BitmapRecycled) {
						return;
					}

					for (int i = 0; i < array_trace3.size(); i++) {
						DeviceTrace bean = array_trace3.get(i);
						String lat = bean.getLatitude();
						String lng = bean.getLongitude();
						LatLng latlng = new LatLng(Double.parseDouble(lat),
								Double.parseDouble(lng));
						LatLng latlng1 = PubUtil.convert(latlng);
						latlng_3 = PubUtil.DfInfomation(latlng1);
						if (i == 0) {
							if (mMarkerA3 != null) {
								mMarkerA3.remove();

							}
							MarkerOptions ooA = new MarkerOptions()
									.position(latlng_3).icon(bdA).zIndex(9)
									.draggable(true);

							mMarkerA3 = (Marker) (mBaiduMap3.addOverlay(ooA));

						}
						if (i > 0 && i == array_trace3.size() - 1) {
							if (mMarkerB3 != null) {
								mMarkerB3.remove();
							}
							lastLocation3 = PubUtil
									.getLastInfo(array_trace3, i);
							MarkerOptions ooB = new MarkerOptions()
									.position(lastLocation3).icon(bdB)
									.zIndex(9).draggable(true);

							mMarkerB3 = (Marker) (mBaiduMap3.addOverlay(ooB));

						}
						latlngs3.add(latlng_3);
						// // 反Geo搜索
						// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						// .location(latlng));
					}
					if (latlngs3.size() > 1) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(3).color(0xAAFF0000).points(latlngs3);
						Polyline mPolyline = (Polyline) mBaiduMap3
								.addOverlay(ooPolyline);

						// Toast.makeText(MoreMapsActivity.this, "最少2条位置信息",
						// 0).show();
						mBaiduMap3.setMapStatus(MapStatusUpdateFactory
								.newLatLng(lastLocation3));

					} else {
						// 设置中心点
						mBaiduMap3.setMapStatus(MapStatusUpdateFactory
								.newLatLng(latlng_3));

					}
					finishRequest3 = false;
					Log.e(tag, "timer3");
				} else {
					postDataOne3(imei3);
				}
				break;
			case 4:// 第4个设备返回的数据
				ArrayList<LatLng> latlngs4 = new ArrayList<LatLng>();
				ArrayList<DeviceTrace> array_trace4 = (ArrayList<DeviceTrace>) msg.obj;
				if (array_trace4.size() != 0) {
					if (BitmapRecycled) {
						return;
					}

					for (int i = 0; i < array_trace4.size(); i++) {
						DeviceTrace bean = array_trace4.get(i);
						String lat = bean.getLatitude();
						String lng = bean.getLongitude();
						LatLng latlng = new LatLng(Double.parseDouble(lat),
								Double.parseDouble(lng));
						LatLng latlng1 = PubUtil.convert(latlng);
						latlng_4 = PubUtil.DfInfomation(latlng1);
						if (i == 0) {
							if (mMarkerA4 != null) {
								mMarkerA4.remove();

							}
							MarkerOptions ooA = new MarkerOptions()
									.position(latlng_4).icon(bdA).zIndex(9)
									.draggable(true);

							mMarkerA4 = (Marker) (mBaiduMap4.addOverlay(ooA));

						}
						if (i > 0 && i == array_trace4.size() - 1) {
							if (mMarkerB4 != null) {
								mMarkerB4.remove();
							}
							lastLocation4 = PubUtil
									.getLastInfo(array_trace4, i);
							MarkerOptions ooB = new MarkerOptions()
									.position(lastLocation4).icon(bdB)
									.zIndex(9).draggable(true);

							mMarkerB4 = (Marker) (mBaiduMap4.addOverlay(ooB));

						}
						latlngs4.add(latlng_4);
						// // 反Geo搜索
						// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						// .location(latlng));
					}
					if (latlngs4.size() > 1) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(3).color(0xAAFF0000).points(latlngs4);
						Polyline mPolyline = (Polyline) mBaiduMap4
								.addOverlay(ooPolyline);

						// Toast.makeText(MoreMapsActivity.this, "最少2条位置信息",
						// 0).show();
						mBaiduMap4.setMapStatus(MapStatusUpdateFactory
								.newLatLng(lastLocation4));

					} else {
						// 设置中心点
						mBaiduMap4.setMapStatus(MapStatusUpdateFactory
								.newLatLng(latlng_4));

					}
					finishRequest4 = false;
					Log.e(tag, "timer4");
				} else {
					postDataOne4(imei4);
				}
				break;
			case 5:// 最近一次定位信息
				if (BitmapRecycled) {
					return;
				}
				ArrayList<DeviceTrace> array5 = (ArrayList<DeviceTrace>) msg.obj;
				if (array5.size() != 0) {

					for (int i = 0; i < array5.size(); i++) {
						DeviceTrace bean2 = array5.get(i);
						if (bean2.getLatitude().equals("")
								|| bean2.getLongitude().equals("")) {
							Toast.makeText(MoreMapsActivity.this, "服务器无上传位置数据",
									0).show();
							return;
						}
						double a = Double.parseDouble(bean2.getLatitude());
						double b = Double.parseDouble(bean2.getLongitude());
						LatLng latlng = new LatLng(a, b);
						LatLng latlng2_ = PubUtil.convert(latlng);
						latlng5 = PubUtil.DfInfomation(latlng2_);
						mBaiduMap1.clear();

						if (mMarkerA5 != null) {
							mMarkerA5.remove();
						}
						MarkerOptions ooA2 = new MarkerOptions()
								.position(latlng5).icon(bdA).zIndex(9)
								.draggable(true);
						mMarkerA5 = (Marker) (mBaiduMap1.addOverlay(ooA2));

					}
					// Toast.makeText(MoreMapsActivity.this, "最近一次的位置信息",
					// 0).show();
					// 设置中心点
					mBaiduMap1.setMapStatus(MapStatusUpdateFactory
							.newLatLng(latlng5));

				} else {
					Toast.makeText(MoreMapsActivity.this,
							"没有得到位置信息，请确认IMEI号或网络是否正常", 2000).show();
					return;
				}

				finishRequest1 = false;
				Log.e(tag, "timer11");
				break;
			case 6:
				if (BitmapRecycled) {
					return;
				}
				ArrayList<DeviceTrace> array6 = (ArrayList<DeviceTrace>) msg.obj;
				if (array6.size() != 0) {

					for (int i = 0; i < array6.size(); i++) {
						DeviceTrace bean2 = array6.get(i);
						if (bean2.getLatitude().equals("")
								|| bean2.getLongitude().equals("")) {
							Toast.makeText(MoreMapsActivity.this, "服务器无上传位置数据",
									0).show();
							return;
						}
						double a = Double.parseDouble(bean2.getLatitude());
						double b = Double.parseDouble(bean2.getLongitude());
						LatLng latlng = new LatLng(a, b);
						LatLng latlng2_ = PubUtil.convert(latlng);
						latlng6 = PubUtil.DfInfomation(latlng2_);
						mBaiduMap2.clear();

						if (mMarkerA6 != null) {
							mMarkerA6.remove();
						}
						MarkerOptions ooA2 = new MarkerOptions()
								.position(latlng6).icon(bdA).zIndex(9)
								.draggable(true);
						mMarkerA6 = (Marker) (mBaiduMap2.addOverlay(ooA2));

					}
					// Toast.makeText(MoreMapsActivity.this, "最近一次的位置信息",
					// 0).show();
					// 设置中心点
					mBaiduMap2.setMapStatus(MapStatusUpdateFactory
							.newLatLng(latlng6));

				} else {
					Toast.makeText(MoreMapsActivity.this,
							"没有得到位置信息，请确认IMEI号或网络是否正常", 2000).show();
					return;
				}

				finishRequest2 = false;
				Log.e(tag, "timer22");
				break;
			case 7:
				if (BitmapRecycled) {
					return;
				}
				ArrayList<DeviceTrace> array7 = (ArrayList<DeviceTrace>) msg.obj;
				if (array7.size() != 0) {

					for (int i = 0; i < array7.size(); i++) {
						DeviceTrace bean2 = array7.get(i);
						if (bean2.getLatitude().equals("")
								|| bean2.getLongitude().equals("")) {
							Toast.makeText(MoreMapsActivity.this, "服务器无上传位置数据",
									0).show();
							return;
						}
						double a = Double.parseDouble(bean2.getLatitude());
						double b = Double.parseDouble(bean2.getLongitude());
						LatLng latlng = new LatLng(a, b);
						LatLng latlng2_ = PubUtil.convert(latlng);
						latlng7 = PubUtil.DfInfomation(latlng2_);
						mBaiduMap3.clear();

						if (mMarkerA7 != null) {
							mMarkerA7.remove();
						}
						MarkerOptions ooA2 = new MarkerOptions()
								.position(latlng7).icon(bdA).zIndex(9)
								.draggable(true);
						mMarkerA7 = (Marker) (mBaiduMap3.addOverlay(ooA2));

					}
					// Toast.makeText(MoreMapsActivity.this, "最近一次的位置信息",
					// 0).show();
					// 设置中心点
					mBaiduMap3.setMapStatus(MapStatusUpdateFactory
							.newLatLng(latlng7));

				} else {
					Toast.makeText(MoreMapsActivity.this,
							"没有得到位置信息，请确认IMEI号或网络是否正常", 2000).show();
					return;
				}

				finishRequest3 = false;
				Log.e(tag, "timer33");
				break;
			case 8:
				if (BitmapRecycled) {
					return;
				}
				ArrayList<DeviceTrace> array8 = (ArrayList<DeviceTrace>) msg.obj;
				if (array8.size() != 0) {

					for (int i = 0; i < array8.size(); i++) {
						DeviceTrace bean2 = array8.get(i);
						if (bean2.getLatitude().equals("")
								|| bean2.getLongitude().equals("")) {
							Toast.makeText(MoreMapsActivity.this, "服务器无上传位置数据",
									0).show();
							return;
						}
						double a = Double.parseDouble(bean2.getLatitude());
						double b = Double.parseDouble(bean2.getLongitude());
						LatLng latlng = new LatLng(a, b);
						LatLng latlng2_ = PubUtil.convert(latlng);
						latlng8 = PubUtil.DfInfomation(latlng2_);
						mBaiduMap4.clear();

						if (mMarkerA8 != null) {
							mMarkerA8.remove();
						}
						MarkerOptions ooA2 = new MarkerOptions()
								.position(latlng8).icon(bdA).zIndex(9)
								.draggable(true);
						mMarkerA8 = (Marker) (mBaiduMap4.addOverlay(ooA2));

					}
					// Toast.makeText(MoreMapsActivity.this, "最近一次的位置信息",
					// 0).show();
					// 设置中心点
					mBaiduMap4.setMapStatus(MapStatusUpdateFactory
							.newLatLng(latlng8));

				} else {
					Toast.makeText(MoreMapsActivity.this,
							"没有得到位置信息，请确认IMEI号或网络是否正常", 2000).show();
					return;
				}

				finishRequest4 = false;
				Log.e(tag, "timer44");
				break;
			case 9:
				postDate1(loadName, imei1, time_1);
				break;
			case 10:
				postDate2(loadName, imei2, time_2);
				break;
			case 11:
				postDate3(loadName, imei3, time_3);
				break;
			case 12:
				postDate4(loadName, imei4, time_4);
				break;
			case 13:// 开启timer

				if (timer1 == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!finishRequest1) {
								finishRequest1 = true;
								handler.sendEmptyMessage(9);
							}
						}
					};
					timer1 = new Timer(true);
					timer1.schedule(task, 2000, 5000); // 延时1000ms后执行，5000ms执行一次
				}
				break;
			case 14:
				if (timer2 == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!finishRequest2) {
								finishRequest2 = true;
								handler.sendEmptyMessage(10);
							}
						}
					};
					timer2 = new Timer(true);
					timer2.schedule(task, 2000, 5000); // 延时1000ms后执行，1000ms执行一次
				}
				break;
			case 15:
				if (timer3 == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!finishRequest3) {
								finishRequest3 = true;
								handler.sendEmptyMessage(11);
							}
						}
					};
					timer3 = new Timer(true);
					timer3.schedule(task, 2000, 5000); // 延时1000ms后执行，1000ms执行一次
				}
				break;
			case 16:
				if (timer4 == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!finishRequest4) {
								finishRequest4 = true;
								handler.sendEmptyMessage(12);
							}
						}
					};
					timer4 = new Timer(true);
					timer4.schedule(task, 2000, 5000); // 延时1000ms后执行，1000ms执行一次
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onStop() {
		// 将timer关掉
		onStop = true;
		if (timer1 != null) {
			timer1.cancel();
			timer1 = null;
		}
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
		if (timer3 != null) {
			timer3.cancel();
			timer3 = null;
		}
		if (timer4 != null) {
			timer4.cancel();
			timer4 = null;
		}
		super.onStop();
	}

	@Override
	protected void onStart() {
		// 将timer开启
		if (onStop) {
			onStop = false;
			if (arrays.size() == 1) {
				handler.sendEmptyMessage(13);
			} else if (arrays.size() == 2) {
				handler.sendEmptyMessage(13);
				handler.sendEmptyMessage(14);
			} else if (arrays.size() == 3) {
				handler.sendEmptyMessage(13);
				handler.sendEmptyMessage(14);
				handler.sendEmptyMessage(15);
			} else {
				handler.sendEmptyMessage(13);
				handler.sendEmptyMessage(14);
				handler.sendEmptyMessage(15);
				handler.sendEmptyMessage(16);
			}

		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {

		if (timer1 != null) {
			timer1.cancel();
			timer1 = null;
		}
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
		if (timer3 != null) {
			timer3.cancel();
			timer3 = null;
		}
		if (timer4 != null) {
			timer4.cancel();
			timer4 = null;
		}
//	        mMapView1.onDestroy();  
//	        mMapView2.onDestroy();  
//	        mMapView3.onDestroy();  
//	        mMapView4.onDestroy();  
		// 回收 bitmap 资源
		bdA.recycle();
		bdB.recycle();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		BitmapRecycled = true;
		super.onBackPressed();
	}

}
