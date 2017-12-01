package com.mj.gpsclient.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.ab.fragment.AbFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.soap.AbSoapUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
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
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.mj.gpsclient.R;
import com.mj.gpsclient.db.DataFollowHelper;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.DebugLog;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.global.XMLHelper;
import com.mj.gpsclient.model.DeviceLocation;
import com.mj.gpsclient.model.DevicePosition;
import com.mj.gpsclient.model.Devices;
import com.mj.gpsclient.view.DevicesDetailView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majin on 15/5/28.
 */
public  class TrackRealtimeFragment extends AbFragment implements
		View.OnClickListener, OnGetGeoCoderResultListener,
		OnGetRoutePlanResultListener {
	BitmapDescriptor my_Location;
	String GeoCoder = "";
	private MyApplication application;
	private Activity mActivity = null;
	// 地图相关
	private MapView mMapView;
	public BaiduMap mBaiduMap;
	private ImageView buCarLocation;
	private ImageView buMyLocation;
	private ImageView buNaviLocation;
	private Marker mMarkerA, mlMarker;
	private InfoWindow mInfoWindow;
	private AbSoapUtil mAbSoapUtil;
	private BitmapDescriptor bdA, bdS;
	private DevicePosition devicePosition;
	private boolean isFristLoad = true;
	private List<DeviceLocation> locationList;
	private Marker markerStart;
	private Marker markerNow;
	private TextView textViewLoction;
	// GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private LatLng prePoint = null;
	List<LatLng> points = new ArrayList<LatLng>();
	private double test_plus;
	boolean isFirstLoc = true;// 是否首次定位
	BitmapDescriptor mCurrentMarker;
	boolean useDefaultIcon = false;
	private MyLocationConfiguration.LocationMode mCurrentMode;
	// 定位相关
	LocationClient mLocClient;
	// 搜索相关
	RoutePlanSearch mSearchRoute = null; // 搜索模块，也可去掉地图模块独立使用

	private LatLng carLatLng;
	private LatLng myLatLng;
	//OverlayManager routeOverlay = null;
	private BDLocation myBDLocation;

	public MyLocationListenner myListener = new MyLocationListenner();
	public static final int HANDLE_MSG_WHAT = -1;
	public static final int DRAW_MAP = HANDLE_MSG_WHAT + 1;
	public static final int GET_DEVICE_DATA = HANDLE_MSG_WHAT + 2;
	public static final int LOOP_MSG_ONE = HANDLE_MSG_WHAT + 3;
	public static final int LOOP_MSG_TWO = HANDLE_MSG_WHAT + 4;
	public static final int MAP_PAUSE = HANDLE_MSG_WHAT + 5;
	public static final int MAP_AUTO_TRACK_SWITCH = HANDLE_MSG_WHAT + 6;
	public static final int ROUT_DRAW_END = HANDLE_MSG_WHAT + 7;
	public static final int START_GET_MY_LOCATION = HANDLE_MSG_WHAT + 8;
	public static final int GET_ROUNT = HANDLE_MSG_WHAT + 9;
	public static final int GET_MYLOCATION = HANDLE_MSG_WHAT + 10;

	// 是否是导航模式
	public boolean isNaviModel = false;
	private AbHttpUtil mAbHttpUtil = null;

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int result = msg.what;
			int arg1 = msg.arg1;
			switch (result) {
			case DRAW_MAP:
				if (null != devicePosition) {
					System.out.println("图层加载");
					initOverlay();
				} else {
					AbToastUtil.showToast(mActivity, "实时追踪首次定位失败！");
				}
				break;
			case GET_DEVICE_DATA:
				getDeviceDetail2();
				Log.e("www", "1");
				break;

			case LOOP_MSG_ONE:
				if (isAutoModle()) {
					mHandler.sendEmptyMessageDelayed(LOOP_MSG_TWO, 1000);
					sendEmptyMessage(GET_DEVICE_DATA);// 自动获取数据
					Log.e("www", "2");
				}
				break;

			case LOOP_MSG_TWO:
				if (isAutoModle()) {
					mHandler.sendEmptyMessageDelayed(LOOP_MSG_ONE, 9000);
					Log.e("www", "3");
				}
				break;

			case MAP_AUTO_TRACK_SWITCH:
				resetMap();
				Log.e("www", "4");
				if (arg1 == 1) {
					bdS = BitmapDescriptorFactory
							.fromResource(R.drawable.icon_track_navi_end);
					startTimer();
					AbToastUtil.showToast(getActivity(), "实时路线跟踪轨迹开启！");
				} else if (arg1 == 0) {
					endTimer();
					AbToastUtil.showToast(getActivity(), "实时路线跟踪轨迹关闭！");
					bdS = BitmapDescriptorFactory.fromResource(R.drawable.xx);
					sendEmptyMessage(GET_DEVICE_DATA);
				}
				break;

			case MAP_PAUSE:
				resetMap();
				endTimer();
				mBaiduMap.setMaxAndMinZoomLevel(3.0f, 16.0f);
				Log.e("www", "5");
				break;
			case ROUT_DRAW_END:
//				mj_hideLoadView();
				showMyLocation(myBDLocation);
				Log.e("www", "6");
				break;

			case START_GET_MY_LOCATION:
				getMyLocation(false);
				Log.e("www", "7");
				break;
			case GET_ROUNT:
//				mj_hideLoadView();
				getRouteOverlay();
				Log.e("www", "8");
				break;
			case GET_MYLOCATION:
				 getMyLocation(true);
//				mj_hideLoadView();
				showMyLocation(myBDLocation);
				Log.e("www", "9");
				break;
			default:
				break;
			}
		}

	};

	@Override
	public View onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		application = (MyApplication) mActivity.getApplication();
		View v = inflater.inflate(R.layout.fragment_track_realtime, container,
				false);
		initDatas();
		initViews(v);
		buCarLocation.setOnClickListener(this);
		buMyLocation.setOnClickListener(this);
		//buNaviLocation.setOnClickListener(this);
		// 加载数据必须
		this.setAbFragmentOnLoadListener(new AbFragmentOnLoadListener() {

			@Override
			public void onLoad() {

			}

		});
		mHandler.sendEmptyMessageDelayed(START_GET_MY_LOCATION, 400);
		return v;
	}
private String getDeviceName(Context context,String imei){
	DataFollowHelper helper = new DataFollowHelper(context);
	Devices mDevice = helper.GetUser(imei.trim());
	String name = mDevice.getName();
	return name;
}
	@Override
	public void setResource() {
		// 设置加载的资源
		this.setLoadDrawable(R.drawable.ic_load);
		this.setLoadMessage("正在加载,请稍候...");
		this.setTextColor(getResources().getColor(R.color.moji_black_text));
		this.setRefreshDrawable(R.drawable.ic_refresh_f);
		this.setRefreshMessage("请求出错，请重试");
	}

	private void initDatas() {
		// bdA = BitmapDescriptorFactory.fromResource(R.drawable.item_27_xx);
		// bdS =
		// BitmapDescriptorFactory.fromResource(R.drawable.icon_track_navi_end);
		if (determinLine())
			bdA = BitmapDescriptorFactory.fromResource(R.drawable.historymark);
		else
			bdA = BitmapDescriptorFactory.fromResource(R.drawable.historymark2);
		bdS = BitmapDescriptorFactory.fromResource(R.drawable.xx);
		// 初始化搜索模块，注册事件监听
		// mSearch = GeoCoder.newInstance();
		// mSearch.setOnGetGeoCodeResultListener(this);
		isNaviModel = false;
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
	}

	private LatLng convert(LatLng latLng) {
		CoordinateConverter c = new CoordinateConverter();
		c.from(CoordinateConverter.CoordType.GPS);
		c.coord(latLng);
		return c.convert();
	}

	/*
	 * 开启定时任务
	 */
	private void startTimer() {
		Constant.REALTIME_SWITCH = 1;
		mHandler.removeMessages(LOOP_MSG_ONE);
		mHandler.removeMessages(LOOP_MSG_TWO);
		mHandler.sendEmptyMessage(LOOP_MSG_ONE);

	}

	public void endTimer() {
		Constant.REALTIME_SWITCH = 0;
		mHandler.removeMessages(GET_DEVICE_DATA);
		mHandler.removeMessages(LOOP_MSG_ONE);
		mHandler.removeMessages(LOOP_MSG_TWO);
	}

	private boolean isAutoModle() {
		return ((DevicesTrackActivity) mActivity).isRealtimeMode();
	}

	private boolean isRealTimeModle() {
		return ((DevicesTrackActivity) mActivity).isRealTimeModle();
	}

	private void initOverlay() {
		mBaiduMap.hideInfoWindow();
		LatLng llA = null;
		llA = new LatLng(devicePosition.getLatitude(),
				devicePosition.getLongitude());
		getLngLatToAddress(llA);
		llA = convert(llA);
		carLatLng = llA;
		devicePosition.setLatitude(llA.latitude);
		devicePosition.setLongitude(llA.longitude);
		Log.d("majin", llA.toString());
		OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
				.zIndex(9).draggable(true);
		// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
		// .location(llA));
		// getLngLatToAddress(carLatLng);

		if (mMarkerA == null) {
			mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
		} else {
			mMarkerA.setPosition(llA);
		}
		mBaiduMap
				.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						Log.d("majin", "onMarkerClick---");
						showMarkerInfoWindow(marker);
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

		if (isPause()) {
			return;
		}
		if (markerStart == null) {
			OverlayOptions ooAStart = new MarkerOptions().position(llA)
					.icon(bdS).draggable(true);
			markerStart = (Marker) mBaiduMap.addOverlay(ooAStart);
		}

		if (prePoint != null) {
			// 画出当前的点和之前的一个点以前的轨迹

			double d = DistanceUtil.getDistance(llA, prePoint);
			DebugLog.e("实时 d=" + d);
			if (d > 5) {
				points.clear();
				points.add(prePoint);
				points.add(llA);
				OverlayOptions ooPolyline = new PolylineOptions().width(6)
						.color(0xff379bff).points(points);
				if (isPause()) {
					return;
				}
				mBaiduMap.addOverlay(ooPolyline);
			}

		}

		// 定位到该点
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(llA);
		mBaiduMap.setMapStatus(u);
		// 保存当前点的坐标，以便下一次划线
		prePoint = llA;
		
//		mj_hideLoadView();
	}

	private void showMarkerInfoWindow(Marker marker) {
		DevicesDetailView devicesDetailView = new DevicesDetailView(mActivity);
		devicesDetailView.setData(devicePosition);
		LatLng ll = marker.getPosition();
		mInfoWindow = new InfoWindow(
				BitmapDescriptorFactory.fromView(devicesDetailView), ll, -47,
				null);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	private void convert(DeviceLocation deviceLocation) {
		CoordinateConverter c = new CoordinateConverter();
		c.from(CoordinateConverter.CoordType.GPS);
		c.coord(deviceLocation.getLatLng());

		LatLng lg = c.convert();
		deviceLocation.setLatitude(lg.latitude + "");
		deviceLocation.setLongitude(lg.longitude + "");
		deviceLocation.setLatLng(new LatLng(lg.latitude, lg.longitude));

	}

	private boolean determinLine() {
		String stuts = ((DevicesTrackActivity) mActivity).getDeviceStuts();
		if (stuts.equals("在线"))
			return true;
		else
			return false;
	}

	private boolean isPause() {
		return !((DevicesTrackActivity) mActivity).isRealTimeModle();
	}

	private void getDeviceDetail2() {
//		mj_showLoadView();
		String url = Constant.URL
				+ "/WebService/GLService.asmx/GetMemberPositioningByImeis";
		AbRequestParams params = new AbRequestParams();
		String hah  = application.mUser.getUserName();
		String hah1 = ((DevicesTrackActivity) mActivity).getDeviceName();
		params.put("imeis", hah1);
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
		mAbHttpUtil.setTimeout(3000);
		mAbHttpUtil.post(url, params, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				XMLHelper.getResult("string", content,
						new XMLHelper.CallBack() {
							@Override
							public void getResult(String result) {
								AbDialogUtil.removeDialog(mActivity);
								JSONObject jobj = null;
								try {
									jobj = new JSONObject(result);
								} catch (JSONException e) {
									AbToastUtil.showToast(mActivity, "系统返回异常！");
									e.printStackTrace();
								}
								String r = jobj.optString("Result");

								if (r.equals("ok")) {
									JSONArray jarray = jobj
											.optJSONArray("Model");
									JSONObject jmode = null;
									try {
										if (jarray != null
												|| jarray.length() > 0) {
											jmode = jarray.getJSONObject(0);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (jmode != null) {
										devicePosition = new DevicePosition();
										devicePosition.setOnTime(jmode
												.optString("OnTime"));
										devicePosition.setLocType(jmode
												.optString("LocType"));
										devicePosition.setLongitude(jmode
												.optDouble("Longitude"));
										devicePosition.setLatitude(jmode
												.optDouble("Latitude"));
										devicePosition.setBaseInfo(jmode
												.optString("BaseInfo"));
										devicePosition.setSpeed(jmode
												.optString("Speed"));
										devicePosition.setDirection(jmode
												.optString("Direction"));
										devicePosition.setDeviceStatus(jmode
												.optString("DeviceStatus"));
										devicePosition.setName(getDeviceName(getActivity(),jmode
												.optString("Imei")));
									}
									isFristLoad = false;
									mHandler.sendEmptyMessage(0);
								} else {
									AbToastUtil.showToast(mActivity, "系统异常！");
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
			public void onFailure(int statusCode, String content,
					Throwable error) {

			}
		});
	}


	@Override
	public void onPause() {
		mMapView.onPause();
		Log.d("majin", "onPause");
		mHandler.sendEmptyMessage(9);
		if (mMarkerA != null) {
			mMarkerA.remove();
			mMarkerA = null;
		}

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		// mHandler.postDelayed(runnable, 4000);
		showContentView();
		mMapView.onResume();
		if (((DevicesTrackActivity) mActivity).getCurrentIndexFragmentIndex() == 0) {
			// 请求一次数据
			mHandler.removeMessages(GET_DEVICE_DATA);
			mHandler.sendEmptyMessage(GET_DEVICE_DATA);
		}

		// mHandler.sendEmptyMessage(3);

	}

	@Override
	public void onDestroy() {
		// 退出时销毁定位
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 退出时销毁定位
		if (mLocClient != null) {
			mLocClient.stop();
		}

		bdA.recycle();
		super.onDestroy();
	}

	private void initViews(View view) {
		// 初始化地图
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		buCarLocation = (ImageView) view.findViewById(R.id.car_location_bu);
		//buNaviLocation = (ImageView) view.findViewById(R.id.nav_bu);
		buMyLocation = (ImageView) view.findViewById(R.id.mobile_bu);
		textViewLoction = (TextView) view.findViewById(R.id.loaction_GeoCode);
		resetMap();
	}

	private void resetMap() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.clear();
		mBaiduMap.setMaxAndMinZoomLevel(3.0f, 16.0f);
		MapStatus mMapStatus = new MapStatus.Builder().zoom(16).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mMarkerA = null;
		markerStart = null;
		prePoint = null;
		mlMarker = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.car_location_bu:
			if (Constant.REALTIME_SWITCH == 1) {
				Toast.makeText(mActivity, "已开启实时追踪,请勿重复点击",
					     Toast.LENGTH_SHORT).show();
			}else {
				resetMap();
				getCarLocation();
				isNaviModel = false;
				bdS = BitmapDescriptorFactory.fromResource(R.drawable.xx);
				((DevicesTrackActivity) getActivity()).exitNavigationMode();
			}
			
			break;
		case R.id.mobile_bu:
			if (Constant.REALTIME_SWITCH == 1) {

				final AlertDialog.Builder builder = new AlertDialog.Builder(
						mActivity);
				builder.setTitle("提示");
				builder.setMessage("定位将关闭实时追踪，是否继续？");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								// getLngLatToAddress(myLatLng);
//								mj_showLoadView();
								AbToastUtil.showToast(mActivity, "正在定位，请稍后......");
								endTimer();
								((DevicesTrackActivity) getActivity()).toNavigationMode("定位");
								resetMap();
								Constant.MY_Location = 1;
								mHandler.removeMessages(GET_MYLOCATION);
								mHandler.sendEmptyMessageDelayed(GET_MYLOCATION, 1000);
								isNaviModel = false;
								bdS = BitmapDescriptorFactory.fromResource(R.drawable.xx);
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			
			}else {
				// getLngLatToAddress(myLatLng);
//				mj_showLoadView();
				AbToastUtil.showToast(mActivity, "正在定位，请稍后......");
				endTimer();
				((DevicesTrackActivity) getActivity()).toNavigationMode("定位");
				resetMap();
				Constant.MY_Location = 1;
				mHandler.removeMessages(GET_MYLOCATION);
				mHandler.sendEmptyMessageDelayed(GET_MYLOCATION, 1000);
				isNaviModel = false;
				bdS = BitmapDescriptorFactory.fromResource(R.drawable.xx);
			}
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
		// textViewLoction.setText(result.getAddress());
	}

	public void getCarLocation() {
		mHandler.removeMessages(GET_DEVICE_DATA);
		mHandler.sendEmptyMessage(GET_DEVICE_DATA);
	}

	public void getMyLocation(boolean isShowInMap) {

		// 传入null则，恢复默认图标
		// 修改为自定义marker
		// mCurrentMarker = BitmapDescriptorFactory
		// .fromResource(R.drawable.icon_geo);
		isFirstLoc = isShowInMap;
		mCurrentMarker = null;
		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(mActivity);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
//		mj_showLoadView();
		mLocClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// DebugLog.e("onReceiveLocation=-----");
			myBDLocation = location;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			myLatLng = ll;
			if (isNaviModel) {
				// showMyLocation(myBDLocation);
			}
		}


		public void onConnectHotSpotMessage(String s, int i) {

		}

		public void onReceivePoi(BDLocation poiLocation) {
			// mj_hideLoadView();
			DebugLog.e("onReceivePoi=-----");
		}
	}

	private void showMyLocation(BDLocation location) {
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null) {
			Toast.makeText(mActivity, "定位异常", Toast.LENGTH_SHORT).show();
			return;
		}
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		// ll =convert(ll);
		myLatLng = ll;
			BitmapDescriptor my_Location = BitmapDescriptorFactory
                    .fromResource(R.drawable.my_location);
			OverlayOptions ooo = new MarkerOptions().position(myLatLng).icon(my_Location)
					.zIndex(9).draggable(true);
			if (mlMarker == null) {
				mlMarker = (Marker) (mBaiduMap.addOverlay(ooo));
			} else {
				mlMarker.setPosition(myLatLng);
			}
		// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		MapStatus mMapStatus = new MapStatus.Builder().target(ll).zoom(17)
				.build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		mBaiduMap.animateMapStatus(mMapStatusUpdate);
		getLngLatToAddress(myLatLng);
		// mSearch.reverseGeoCode(new ReverseGeoCodeOption()
		// .location(ll));
	}


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
	public void onGetDrivingRouteResult(DrivingRouteResult result) {

		final DrivingRouteResult result1 = result;
		DebugLog.e(result.toString());
		// DebugLog.e(result.getSuggestAddrInfo().toString());
		// DebugLog.e(result.getTaxiInfo().getDistance()+"   "+result.getTaxiInfo().getDuration());
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//			mj_hideLoadView();
			Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();

		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
//			mj_hideLoadView();
			return;
		}
	}

	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

	}

	public void getRouteOverlay() {
		mBaiduMap = mMapView.getMap();
		mSearchRoute = RoutePlanSearch.newInstance();
		mSearchRoute.setOnGetRoutePlanResultListener(this);
		if (null == myLatLng || null == carLatLng) {
			Toast.makeText(mActivity, "定位失败", Toast.LENGTH_SHORT).show();
			return;
		}else {
			PlanNode stNode = PlanNode.withLocation(myLatLng);
			PlanNode enNode = PlanNode.withLocation(carLatLng);
			mSearchRoute.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.to(enNode));
		}
	}

	private void getLngLatToAddress(LatLng latLng) {

		mAbHttpUtil.get("http://restapi.amap.com/v3/geocode/regeo?output=json&location="+latLng.longitude+","+latLng.latitude+"&key=786efe99399865add6633ac50f05c183", null,
				new AbStringHttpResponseListener() {

					@Override
					public void onSuccess(int statusCode, String content) {
						if (!TextUtils.isEmpty(content)&& null != textViewLoction) {

							try {
								JSONObject object = new JSONObject(content);
								String result = object.getString("regeocode");
								JSONObject object_ = new JSONObject(result);
								GeoCoder= object_.getString("formatted_address");
								textViewLoction.setText(GeoCoder);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							textViewLoction.setText("获取地理位置信息失败，请重试!");
						}

					}

					@Override
					public void onStart() {

					}

					@Override
					public void onFinish() {

					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {

					}
				});

	}

	@Override
	public void onGetBikingRouteResult(BikingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}


}

