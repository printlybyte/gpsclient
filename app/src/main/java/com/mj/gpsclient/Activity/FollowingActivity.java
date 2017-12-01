package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbActivityManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
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
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.mj.gpsclient.Activity.BaseStrategyHandler.ICallBack;
import com.mj.gpsclient.R;
import com.mj.gpsclient.db.DataFollowHelper;
import com.mj.gpsclient.model.DeviceTrace;
import com.mj.gpsclient.model.Devices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FollowingActivity extends Activity {

	private MapView mMapView;
	private String deviceImei;
	private BaiduMap mBaiduMap;
	private String loadName;
	private DataFollowHelper helper;
	private String startTime;
	private Dialog dialog;
	Polyline mPolyline;
	private Timer timer;
	private boolean switchStatus = true;// switch按钮开启
	private Timer timer2;
	private BitmapDescriptor bdA;
	private BitmapDescriptor bdB;
	private ImageView iv_back_Follow;
	private Switch bt_switch;
	private Switch sc;
	private String lat;
	private String lng;

	private boolean result_ = false;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private LatLng latlng2;
	private LatLng lastLocation;
	private Marker mMarkerB;
	private Marker mMarkerA;
	private String lineStatus;
	private BitmapDescriptor bdC;
	private LatLng latlng_1;

	private String imei1;
	private String onTime1;
	private String imei2;
	private String onTime2;
	private Marker mMarkerA2;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			// 返回轨迹数据
			case 1:
				
				if(isDestroy){
					return;
				}
				ShutDownDialog();
				if (!switchStatus) {
					return;
				}
				ArrayList<LatLng> latlngs = new ArrayList<LatLng>();
				ArrayList<DeviceTrace> array_trace = (ArrayList<DeviceTrace>) msg.obj;
				if (array_trace.size() != 0) {

					for (int i = 0; i < array_trace.size(); i++) {
						DeviceTrace bean = array_trace.get(i);
						imei1 = bean.getImei();
						onTime1 = bean.getOnTime();
						String lat = bean.getLatitude();
						String lng = bean.getLongitude();
						LatLng latlng = new LatLng(Double.parseDouble(lat),
								Double.parseDouble(lng));
						LatLng latlng1 = PubUtil.convert(latlng);
						latlng_1 = PubUtil.DfInfomation(latlng1);

						if (i == 0) {
							if (mMarkerA != null) {
								mMarkerA.remove();
							}
							MarkerOptions ooA = new MarkerOptions()
									.position(latlng_1).icon(bdA)
									.title(imei1 + "/n" + onTime1).zIndex(9)
									.draggable(true);
							mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
						}
						if (i > 0 && i == array_trace.size() - 1) {
							if (mMarkerB != null) {
								mMarkerB.remove();
							}
							lastLocation = getLastInfo(array_trace, i);
							MarkerOptions ooB = new MarkerOptions()
									.position(lastLocation).icon(bdB)
									.title(imei1 + "/n" + onTime1).zIndex(9)
									.draggable(true);
							mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
						}
						latlngs.add(latlng_1);
						// // 反Geo搜索
						// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						// .location(latlng));
					}
					if (latlngs.size() > 1) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(3).color(0xAAFF0000).points(latlngs);
						mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

						LetPointsInScreen(latlngs);
					} else {
					LetPointsInScreen(latlngs);

					}
					result_ = false;
				} else {
					postDataOne(deviceImei);
				}

				break;
			case 2:// 最近一次定位信息
				ArrayList<LatLng> latlngs2 = new ArrayList<LatLng>();
				if(isDestroy){
					return;
				}
				ShutDownDialog();
				ArrayList<DeviceTrace> array2 = (ArrayList<DeviceTrace>) msg.obj;
				if (array2.size() != 0) {
					for (int i = 0; i < array2.size(); i++) {
						DeviceTrace bean2 = array2.get(i);
						imei2 = bean2.getImei();
						onTime2 = bean2.getOnTime();
						if (bean2.getLatitude().equals("")
								|| bean2.getLongitude().equals("")) {
							Toast.makeText(FollowingActivity.this,
									"服务器无上传位置数据", 0).show();
							return;
						}
						double a = Double.parseDouble(bean2.getLatitude());
						double b = Double.parseDouble(bean2.getLongitude());
						LatLng latlng = new LatLng(a, b);
						LatLng latlng2_ = PubUtil.convert(latlng);
						latlng2 = PubUtil.DfInfomation(latlng2_);
						if (switchStatus) {
							//mBaiduMap.clear();
							MarkerOptions ooA2 = new MarkerOptions()
									.position(latlng2).icon(bdA).zIndex(9)
									.title(imei2 + "/n" + onTime2)
									.draggable(true);
							mMarkerA2 = (Marker) (mBaiduMap.addOverlay(ooA2));
						} else {
							//mBaiduMap.clear();
							MarkerOptions ooA2 = new MarkerOptions()
									.position(latlng2).icon(bdC).zIndex(9)
									.title(imei2 + "/n" + onTime2)
									.draggable(true);
							mMarkerA2 = (Marker) (mBaiduMap.addOverlay(ooA2));
						}
					}
					
					latlngs2.add(latlng2);
					LetPointsInScreen(latlngs2);

					
				} else {
					Toast.makeText(FollowingActivity.this,
							"没有得到位置信息，请确认IMEI号或网络是否正常", 2000).show();
					return;
				}
				result_ = false;
				break;
			case 4:	//请求轨迹信息
				postDate(loadName, deviceImei, startTime);
				break;
			case 7:// 请求失败时关闭dialog
				ShutDownDialog();
				break;
			case 9:// 请求最近一次的位置信息
				postDataOne(deviceImei);
				break;
			case 11:// 开启timer
				if (timer == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!result_ && switchStatus) {
								result_ = true;
								handler.sendEmptyMessage(4);
							}
						}
					};

					timer = new Timer(true);
					timer.schedule(task, 1000, 5000); // 延时1000ms后执行，1000ms执行一次
				}
				break;
			case 12:
				if (timer2 == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!result_ && !switchStatus) {
								result_ = true;
								handler.sendEmptyMessage(9);
							}
						}
					};

					timer2 = new Timer(true);
					timer2.schedule(task, 1000, 5000); // 延时1000ms后执行，1000ms执行一次
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

		private LatLng getLastInfo(ArrayList<DeviceTrace> array_trace, int i) {
			DeviceTrace bean = array_trace.get(i);
			String lat = bean.getLatitude();
			String lng = bean.getLongitude();
			LatLng latlng = new LatLng(Double.parseDouble(lat),
					Double.parseDouble(lng));
			LatLng latlng1 = PubUtil.convert(latlng);
			LatLng latlng1_ = PubUtil.DfInfomation(latlng1);
			return latlng1_;

		}

	};
	private SharedPreferences sp;
	private boolean isDestroy = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.followlayout);
		AbActivityManager.getInstance().addActivity(this);
		mMapView = (MapView) findViewById(R.id.bmapView_follow);
		mBaiduMap = mMapView.getMap();
		iv_back_Follow = (ImageView) findViewById(R.id.iv_back_Follow);
		sc = (Switch) findViewById(R.id.switch_bt);
		SwitchListener();
		BaiduMapClickListener();
		helper = new DataFollowHelper(this);
		ShowDialog();
		GetIntentMSG();
		GetStartTime();
		GetSwitchStatus();
		
		bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_track_navi_end);
		bdB = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		bdC = BitmapDescriptorFactory.fromResource(R.drawable.historymark);
		iv_back_Follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				if (timer2 != null) {
					timer2.cancel();
					timer2 = null;
				}
				finish();
			}
		});
	}

	private void GetSwitchStatus() {
		sp = getSharedPreferences("switchStatus", MODE_PRIVATE);
		String status = sp.getString(deviceImei, "");
		if(status.equals("1")){//说明switch按钮状态是开启状态
			sc.setChecked(true);
			switchStatus = true;
			//开启timer 请求轨迹信息
			handler.sendEmptyMessage(11);
			
		}else if(status.equals("2")){//说明switch按钮状态是关闭状态
			sc.setChecked(false);
			switchStatus = false;
			handler.sendEmptyMessage(12);
		}else{
			sc.setChecked(true);
			switchStatus = true;
			//开启timer 请求轨迹信息
			handler.sendEmptyMessage(11);
		}
	}

	private void BaiduMapClickListener() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			private InfoWindow mInfoWindow;

			@Override
			public boolean onMarkerClick(final Marker mMarker) {

				View view = LayoutInflater.from(FollowingActivity.this)
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
				title.setText("终端名称：" + infos[0]);
				info_time_tv.setText("在线时间：" + infos[1]);
				info_lng_tv.setText("经度：" + ll.longitude);
				info_lat_tv.setText("纬度：" + ll.latitude);
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(view), ll, -47, null);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {

				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mBaiduMap.hideInfoWindow();

			}
		});
		
	}

	private void SwitchListener() {
		sc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked) {// 关闭跟踪轨迹
					Editor et = sp.edit();
					et.putString(deviceImei, "2");
					et.commit();
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					ShutDownDialog();
					ShowDialog();
					mBaiduMap.clear();
					// 实时请求最近一次的定位点
					// 开启timer2
					switchStatus = false;
					handler.sendEmptyMessage(12);
				} else {// 开启跟踪轨迹
					Editor et = sp.edit();
					et.putString(deviceImei, "1");
					et.commit();
					// 关闭timer2
					if (timer2 != null) {
						timer2.cancel();
						timer2 = null;
					}
					ShutDownDialog();
					ShowDialog();
					mBaiduMap.clear();
					switchStatus = true;
					// 开启timer
					handler.sendEmptyMessage(11);

				}
			}
		});
		
	}

	private void ShowDialog() {
		dialog = new Dialog(this, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog);
		dialog.show();

	}

	

	/**
	 * 当时间段内不存在位置数据时 请求最近位置数据，
	 * 
	 * @param num
	 *            设备IMEI
	 */
	protected void postDataOne(String num) {
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

	/**
	 * 点击跟踪列表对象，请求时间段内数据列表
	 * 
	 * @param userName
	 * @param imei
	 * @param startTime
	 *            开始时间
	 */
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

	private void GetStartTime() {
		// TODO Auto-generated method stub
		Devices bean = helper.GetUser(deviceImei);
		startTime = bean.getStartTime();
		lineStatus = bean.getLineStatus();

	}

	private void GetIntentMSG() {
		Intent intent = getIntent();
		deviceImei = intent.getStringExtra("memberImei");
		SharedPreferences sp = getSharedPreferences("userName", MODE_PRIVATE);
		loadName =sp.getString("username", "");
	}

	private void ShutDownDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if (lineStatus.equals("离线")) {
			Toast.makeText(FollowingActivity.this, "离线状态无法获取最新位置信息",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
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
		isDestroy  = true;
		
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (timer2 != null) {
			timer2.cancel();
			timer2 = null;
		}
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
		super.onDestroy();
		bdA.recycle();
		bdB.recycle();
		bdC.recycle();
	}

	private void LetPointsInScreen(ArrayList<LatLng> arrays) {
		if (arrays!=null&&arrays.size()==1) {
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(arrays.get(0),18));
			return;
		}
		if (arrays!=null&&arrays.size()==2) {
			double  distance = PubUtil.getDistance(arrays.get(0).latitude,arrays.get(0).longitude,arrays.get(1).latitude,arrays.get(1).longitude);
			if (distance<20) {
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(arrays.get(0),18));
				return;
			}
		}

		Builder builder = new LatLngBounds.Builder();
		for(LatLng mlatlng:arrays){
			builder.include(mlatlng);
		}
		LatLngBounds bounds = builder.build();
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds,500,300);

		mBaiduMap.animateMapStatus(u);
	}

}
