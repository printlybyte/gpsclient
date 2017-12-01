package com.mj.gpsclient.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbToastUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.mj.gpsclient.R;
import com.mj.gpsclient.Activity.BaseStrategyHandler.ICallBack;
import com.mj.gpsclient.adapter.DevicesListAdapter;
import com.mj.gpsclient.db.DataFollowHelper;
import com.mj.gpsclient.global.Constant;
import com.mj.gpsclient.global.DebugLog;
import com.mj.gpsclient.global.MyApplication;
import com.mj.gpsclient.global.XMLHelper;
import com.mj.gpsclient.model.Devices;
import com.mj.gpsclient.view.FollowingSelectView;
import com.mj.gpsclient.view.FollowingSelectView.FollowingCallBack;
import com.mj.gpsclient.view.SearchBarView;
import com.mj.gpsclient.view.segmentSelectView;

/**
 * Created by majin on 15/5/26.
 */
public class DevicesListFragment extends AbFragment {
	private MyApplication application;
	private Activity mActivity = null;
	private ListView listView;
	private String loadName;
	String tag = "DevicesListFragment";
	private segmentSelectView segmentSelectView;
	private SearchBarView searchBarView;
	private DevicesListAdapter devicesAdapter;
	private List<Devices> list;
	private TextView tv_select_all;
	private Dialog dialog;
	private Timer timer;
	private FollowingSelectView followView;
	private List<Devices> allDeviceList;
	private List<Devices> onLineDeviceList;
	private List<Devices> offLineDeviceList;
	private List<Devices> followDeviceList;
	private AbHttpUtil http;
	private int SELECTSTATUS = 100, SELECTSTATUS_ = 100;// SELECTSTATUS 未全选为跟踪设备
	private ImageView iv;
	private boolean success = false;

	private DataFollowHelper helper;
	private int segmentIndex;
	boolean select_ = false;
	private AbTitleBar mAbTitleBar;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int result = msg.what;
			switch (result) {
			case 0: // 搜索点击
				showSearchView();
				break;
			case 1:

				// 刷新按钮点击事件
				CheckNetWork();
				sendEmptyMessage(101);
				break;
			case 2: // 跟踪列表点击

				PubUtil.Logo1Clicked = true;
				CheckNetWork();

				if (PubUtil.follow_bt_status == 103) {

					List<Devices> arrays_follow = GetFollowDevices();
					if (arrays_follow.size() == 0) {
						mAbTitleBar.setLogo(R.drawable.follow_iv);
						Toast.makeText(mActivity, "请添加需要跟踪的设备", 0).show();
						return;
					} else {
						tv_select_all.setText("全部停止跟踪");
						Set_Order(arrays_follow);
						devicesAdapter.setLayout(true);
						devicesAdapter.setDate(arrays_follow);
						devicesAdapter.notifyDataSetChanged();
						showFollowView();
					}
				} else if (PubUtil.follow_bt_status == 102) {
					mAbTitleBar.setLogo(R.drawable.follow_iv);
					Toast.makeText(mActivity, "离线设备无法实时跟踪，请选择在线设备", 0).show();
					return;
				} else if (PubUtil.follow_bt_status == 101) {
					initLeftButton();
				} else {
					initLeftButton();
				}
				break;
			case 3:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				ArrayList<Devices> arrays = (ArrayList<Devices>) msg.obj;
				if (arrays != null && arrays.size() > 0) {
					for (int i = 0; i < arrays.size(); i++) {
						Devices bean = arrays.get(i);
						String num = bean.getIMEI();
						String lat = bean.getLat();
						String lng = bean.getLng();
						helper.UpdatePositionInfo(lat, lng, num);
					}
				}
				break;
			case 4:// 分屏显示按钮被点击
				PubUtil.Logo2Clicked = true;

				if (PubUtil.follow_bt_status == 103) {

					List<Devices> arrays_follow = GetFollowDevices();
					if (arrays_follow.size() == 0) {
						mAbTitleBar.setLogo2(R.drawable.home_old);
						Toast.makeText(mActivity, "无跟踪的设备", 0).show();
						return;
					} else {

						// setSelectIV_(arrays_follow);
						Set_Order(arrays_follow);
						devicesAdapter.setLayout(true);
						devicesAdapter.setDate(arrays_follow);
						devicesAdapter.notifyDataSetChanged();
						showFollowView();
					}
				} else if (PubUtil.follow_bt_status == 102) {
					mAbTitleBar.setLogo2(R.drawable.home_old);
					Toast.makeText(mActivity, "离线设备无法实时跟踪，请选择在线设备", 0).show();
					return;
				} else if (PubUtil.follow_bt_status == 101) {
					initLeftButton();
				} else if (PubUtil.follow_bt_status == 100){
					initLeftButton();
				}
				tv_select_all.setText("地图跟踪显示");
				break;
			case 6:
				if (dialog == null) {
					dialog = new Dialog(mActivity, R.style.DialogStyle);
					dialog.setContentView(R.layout.dialog);
					dialog.show();
				}
				break;
			case 7:
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				break;
			case 8: // 选择状态中的返回事件
				hideFollowingView();
				initFollowViewStatus();
				sendEmptyMessage(111);
				break;
			case 9:// 停止timer
				if (timer != null) {
					timer.cancel();
					timer = null;
				}
				break;

			case 11:// 隐藏searchView
				hideSearchView();
				break;
			case 100:
				// AbToastUtil.showToast(mActivity, "数据获取成功！");
				mj_hideLoadView();
				break;
			case 101:
				// 请求在线设备
				queryOnlineDevices2();
				break;
			case 102:
				// 请求离线设备
				queryOfflineDevices2();
				break;
			case 103:
				if(!PubUtil.LogoSearchClicked){
					refreshListView(segmentIndex, true);
				}
				break;
			case 104:// 退出编辑模式
				initFollowViewStatus();
				hideFollowingView();
				break;
			case 105:
				mAbTitleBar.setLogo(null);
				mAbTitleBar.setLogo2(null);
				break;
			case 111:// 开始timer
				
				if (timer == null) {
					TimerTask task = new TimerTask() {
						public void run() {
							if (!PubUtil.isConnected(mActivity)) {
								success = false;
								return;
							}
							if (!success
									&& (!PubUtil.Logo1Clicked || !PubUtil.Logo2Clicked)) {
								success = true;
								//Log.i(tag, "实时刷新+++");
								mHandler.sendEmptyMessage(101);
							}
						}
					};

					timer = new Timer(true);
					timer.schedule(task, 1000, 5000); // 延时1000ms后执行，1000ms执行一次
				}
				break;
			default:
				break;
			}
		}

		private void initLeftButton() {
			if(loadName!=null&&!loadName.equals("")){
				List<Devices> arrays_liebiao = helper.GetUserList(loadName);
			
			
			List<Devices> arraysStatusOn = new ArrayList<Devices>();
			List<Devices> arraysStatusOff = new ArrayList<Devices>();
			for (int i = 0; i < arrays_liebiao.size(); i++) {
				Devices bean = arrays_liebiao.get(i);
				if (bean.getLineStatus().equals("离线")) {
					arraysStatusOff.add(bean);
				} else {
					arraysStatusOn.add(bean);
				}
			}
			if (arraysStatusOn.size() == 0) {
				mAbTitleBar.setLogo(R.drawable.follow_iv);
				mAbTitleBar.setLogo2(R.drawable.home_old);
				Toast.makeText(mActivity, "无在线设备，请刷新后重试", 0).show();
				return;
			} else {
				if (PubUtil.follow_bt_status == 101) {
					devicesAdapter.setDate(arraysStatusOn);
				} else if (PubUtil.follow_bt_status == 100 || PubUtil.follow_bt_status == 99) {
					Set_Order(arrays_liebiao);
					devicesAdapter.setDate(arrays_liebiao);
				}
				devicesAdapter.setLayout(true);
				devicesAdapter.notifyDataSetChanged();
				showFollowView();
				tv_select_all.setText("后台在线跟踪");
			}

		}
			}

	};
	

	@Override
	public View onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mActivity = this.getActivity();
		mAbTitleBar = ((AbActivity) mActivity).getTitleBar();
		application = (MyApplication) mActivity.getApplication();
		helper = new DataFollowHelper(mActivity);
		SharedPreferences sp = mActivity.getSharedPreferences("userName", mActivity.MODE_PRIVATE);
		loadName = sp.getString("username", "");
		View v = inflater.inflate(R.layout.fragment_devices_list, container,
				false);
		initViews(v);
		initDatas();
		iv = (ImageView) followView.findViewById(R.id.iv_follow_view);
		tv_select_all = (TextView) followView.findViewById(R.id.tv_select_all);
		initSearchView();
		initFollowView();
		requestPosition();
		devicesAdapter = new DevicesListAdapter(list, mActivity, mHandler,
				followView, helper,loadName);
		listView.setAdapter(devicesAdapter);
		listView.setTextFilterEnabled(true);
		devicesAdapter.notifyDataSetChanged();
		setSegmentSelectView();
		segmentSelectView
				.setCallback(new segmentSelectView.SegmentSelectCallback() {

					@Override
					public void onSelect(int index) {
						if (index == 0) {
							PubUtil.follow_bt_status = 100;
							CheckNetWork();
						} else if (index == 1) {
							CheckNetWork();
							PubUtil.follow_bt_status = 101;
						} else if (index == 2) {
							CheckNetWork();
							PubUtil.follow_bt_status = 102;
						} else {
							PubUtil.follow_bt_status = 103;
						}
						refreshListView(index, true);
						segmentIndex = index;
					}

				});
		searchBarView.setCallback(new SearchBarView.SearchBarCallback() {

			@Override
			public void onchange(String text) {
				Filter filter = ((Filterable) devicesAdapter).getFilter();
				DebugLog.e("onchange---" + text);
				if (TextUtils.isEmpty(text)) {
					filter.filter(" ");
				} else {
					filter.filter(text);
				}
			}

			@Override
			public void onCancel() {
				mHandler.sendEmptyMessage(111);
				hideSearchView();
				Filter filter = ((Filterable) devicesAdapter).getFilter();
				filter.filter(" ");
			}
		});
		followView.SetCallBack(new FollowingCallBack() {

			@Override
			// 确定按钮点击事件
			public void sureSelect() {
				ArrayList<Devices> FinalArrayList = new ArrayList<Devices>();
				List<Devices> beans = helper.GetUserList(loadName);
				for (Devices device_ : beans) {
					if (device_.getWarnNo().equals("8")) {
						FinalArrayList.add(device_);
					}
				}
				if (PubUtil.Logo2Clicked) {

					if (FinalArrayList.size() > 4) {
						Toast.makeText(mActivity, "只能选择4个设备进行多屏显示", 0).show();
						return;
					} else if (FinalArrayList.size() == 0) {
						Toast.makeText(mActivity, "请选择设备进行多屏显示", 0).show();
						return;
					} else {
						mHandler.sendEmptyMessage(104);
						PubUtil.arrays = FinalArrayList;
						Intent intent = new Intent(mActivity,
								MoreMapsActivity.class);
						startActivity(intent);
						return;
					}
				} else if (PubUtil.Logo1Clicked) {

					if (FinalArrayList.size() == 0) {
						Toast.makeText(mActivity, "请选择需要操作的设备", 0).show();
						return;
					} else {
						if (PubUtil.follow_bt_status != 103) {
							for (Devices device : FinalArrayList) {
								if (!device.getSelectStatus().equals("选中")) {
									// 记下开始时间
									long time = System.currentTimeMillis();
									String time_ = PubUtil
											.getDateToString(time);
									helper.UpdateStatus("选中", device.getIMEI());
									helper.UpdateStartTime(time_,
											device.getIMEI());
								}
							}

						} else {
							for (Devices device : FinalArrayList) {
								helper.UpdateStatus("未选中", device.getIMEI());
								helper.UpdateStartTime("", device.getIMEI());
							}
						}
					}
					mHandler.sendEmptyMessage(8);
				}

			}

			@Override
			// 全选按钮点击事件
			public void selectAll() {
				if (PubUtil.Logo1Clicked) {
					initSelectStatus();
				} else if (PubUtil.Logo2Clicked) {
					initSelectStatus();
				}
			}

			private void initSelectStatus() {
				if (!select_) {
					select_ = true;
					iv.setBackgroundResource(R.drawable.checked);

					if (PubUtil.follow_bt_status == 100) {
						UpDataWarnValue(helper.GetUserList(loadName));
						devicesAdapter.setDate(Set_Order(helper.GetUserList(loadName)));
					} else if (PubUtil.follow_bt_status == 103) {
						UpDataWarnValue(GetFollowDevices());
						devicesAdapter.setDate(Set_Order(GetFollowDevices()));
					} else if (PubUtil.follow_bt_status == 101) {
						UpDataWarnValue(GetonLineDeviceList());
						devicesAdapter.setDate(GetonLineDeviceList());
					}
				} else {
					select_ = false;
					iv.setBackgroundResource(R.drawable.check);
					if (PubUtil.follow_bt_status == 100) {
						UpDataWarnValue2(helper.GetUserList(loadName));
						devicesAdapter.setDate(Set_Order(helper.GetUserList(loadName)));
					} else if (PubUtil.follow_bt_status == 103) {
						UpDataWarnValue2(GetFollowDevices());
						devicesAdapter.setDate(Set_Order(GetFollowDevices()));
					} else if (PubUtil.follow_bt_status == 101) {
						UpDataWarnValue2(GetonLineDeviceList());
						devicesAdapter.setDate(GetonLineDeviceList());
					}
				}

			}

			private void UpDataWarnValue(List<Devices> array) {
				for (Devices bean : array) {
					if (PubUtil.follow_bt_status == 103) {
						helper.UpdateWarnValue("8", bean.getIMEI());
					} else {
						if (bean.getLineStatus().equals("在线")) {
							helper.UpdateWarnValue("8", bean.getIMEI());
						}
					}

				}

			}

			private void UpDataWarnValue2(List<Devices> array) {
				for (Devices bean : array) {
					helper.UpdateWarnValue("1", bean.getIMEI());
				}

			}

			private List<Devices> GetonLineDeviceList() {
				List<Devices> array = new ArrayList<Devices>();
				List<Devices> beans = helper.GetUserList(loadName);
				for (Devices bean : beans) {
					if (bean.getLineStatus().equals("在线")) {
						array.add(bean);
					}
				}
				return array;
			}

		});
		refreshListView(segmentIndex, true);

		return v;
	}

	private List<Devices> GetFollowDevices() {
		List<Devices> arrays = helper.GetUserList(loadName);
		List<Devices> arraysStatusSelected = new ArrayList<Devices>();
		for (int i = 0; i < arrays.size(); i++) {
			Devices bean = arrays.get(i);
			if (bean.getSelectStatus().equals("选中")) {
				arraysStatusSelected.add(bean);
			}
		}
		return arraysStatusSelected;
	}

	private void CheckNetWork() {
		
		if (!PubUtil.isConnected(mActivity)) {
			Toast.makeText(mActivity, "网络异常，稍后再试", 0).show();
			return;
		}

	}

	/**
	 * 在线设备靠前，离线设备在后
	 * 
	 * @param arrays
	 */
	private List<Devices> Set_Order(List<Devices> arrays) {
		List<Devices> Aarray = new ArrayList<Devices>();
		List<Devices> Barray = new ArrayList<Devices>();
		for (int i = 0; i < arrays.size(); i++) {
			Devices bean = arrays.get(i);
			if (bean.getLineStatus().equals("在线")) {
				Aarray.add(bean);
			} else {
				Barray.add(bean);
			}
		}
		Aarray.addAll(Barray);
		arrays.clear();
		arrays.addAll(Aarray);
		return arrays;

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		queryOnlineDevices2();
	}

	@Override
	public void onStart() {
		super.onStart();
		SharedPreferences sp = mActivity.getSharedPreferences("userName", mActivity.MODE_PRIVATE);
		loadName = sp.getString("username", "");
		queryOnlineDevices2();
	}

	private void requestPosition() {
		StringBuffer nums = new StringBuffer();
		List<Devices> array = helper.GetUserList(loadName);
		if (array.size() != 0) {
			for (int i = 0; i < array.size(); i++) {
				Devices bean = array.get(i);
				String num = bean.getIMEI();
				if (num.length() == 10 || num.length() == 15) {
					if (!num.equals("0000000000")
							&& !num.equals("000000000000000")) {
						nums.append(num + ",");
					}
				}
			}
			if (nums.length() > 0) {
				postDatePoint(nums.toString().substring(0,
						nums.toString().length() - 1));
			}
		}

	}

	private void postDatePoint(String nums) {
		CheckNetWork();
		RegistEngine.getInstance().postNums(nums,
				new ICallBack<ArrayList<Devices>>() {
					@Override
					public void onTaskStart() {
						mHandler.sendEmptyMessage(6);
					}

					@Override
					// 服务器返回结果，执行此方法
					public void onTaskFinish(ArrayList<Devices> params) {
						// array=params;
						Message msg = new Message();
						msg.what = 3;
						msg.obj = params;
						mHandler.sendMessage(msg);
					}

					@Override
					// 返回失败，执行此方法
					public void onTaskError() {
						mHandler.sendEmptyMessage(7);
					}
				});
	}

	private void initDatas() {
		list = new ArrayList<Devices>();
		list.clear();
		list.addAll(((MainActivity) mActivity).getDevicesAll());// new
																// ArrayList<Devices>();

		allDeviceList = new ArrayList<Devices>();
		allDeviceList.clear();
		allDeviceList.addAll(((MainActivity) mActivity).getDevicesAll());

		onLineDeviceList = new ArrayList<Devices>();
		onLineDeviceList.clear();
		onLineDeviceList
				.addAll(((MainActivity) mActivity).getDevicesesOnline());

		offLineDeviceList = new ArrayList<Devices>();
		offLineDeviceList.clear();
		offLineDeviceList.addAll(((MainActivity) mActivity)
				.getDevicesesOffline());
		followDeviceList = GetFollowDevices();
		segmentIndex = 0;
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

	private void refreshListView(int index, boolean isRefresh) {

		if (isRefresh) {
			followDeviceList = GetFollowDevices();
			allDeviceList.clear();
			allDeviceList.addAll(onLineDeviceList);
			allDeviceList.addAll(offLineDeviceList);
			((MainActivity) mActivity).refreshDeviceList(allDeviceList,
					onLineDeviceList, offLineDeviceList, followDeviceList);
			PubUtil.sizeOnLine = onLineDeviceList.size();
			setSegmentSelectView();
			// 将所有的设备添加到本地数据库
			for (Devices bean : allDeviceList) {
				bean.setSelectStatus("1");
				bean.setStartTime("1");
				bean.setWarnNo("1");
				bean.setLat("0.0");
				bean.setLng("0.0");
				bean.setOnTime("ontime");
				String line_status = bean.getLineStatus();
				if (bean.getIMEI() == null) {
					return;
				}
				if (!helper.checkData(bean.getIMEI())) {
					helper.SaveUserInfo(bean);
				}
				helper.UpdateLineStatus(line_status, bean.getIMEI());

			}
		}
		switch (index) {
		case 0:
			list.clear();
			list.addAll(allDeviceList);
			devicesAdapter.setDate(list);
			devicesAdapter.notifyDataSetChanged();
			setSegmentSelectView();
			break;
		case 1:
			list.clear();
			list.addAll(onLineDeviceList);
			devicesAdapter.setDate(list);
			devicesAdapter.notifyDataSetChanged();
			setSegmentSelectView();
			break;
		case 2:
			list.clear();
			list.addAll(offLineDeviceList);
			devicesAdapter.setDate(list);
			devicesAdapter.notifyDataSetChanged();
			setSegmentSelectView();
			break;
		case 3:
			list.clear();
			list.addAll(followDeviceList);
			Set_Order(list);
			devicesAdapter.setDate(list);
			devicesAdapter.notifyDataSetChanged();
			setSegmentSelectView();
			break;
		}
	}

	private void setSegmentSelectView() {
		int segmentOne = 0;
		int segmentTwo = 0;
		int segmentThree = 0;
		int segmentFour = 0;
		if (((MainActivity) mActivity).getDevicesAll() != null
				&& ((MainActivity) mActivity).getDevicesAll().size() > 0) {
			segmentOne = ((MainActivity) mActivity).getDevicesAll().size();
		}
		if (((MainActivity) mActivity).getDevicesesOnline() != null
				&& ((MainActivity) mActivity).getDevicesesOnline().size() > 0) {
			segmentTwo = ((MainActivity) mActivity).getDevicesesOnline().size();
		}
		if (((MainActivity) mActivity).getDevicesesOffline() != null
				&& ((MainActivity) mActivity).getDevicesesOffline().size() > 0) {
			segmentThree = ((MainActivity) mActivity).getDevicesesOffline()
					.size();
		}
		if (followDeviceList != null && followDeviceList.size() > 0) {
			segmentFour = followDeviceList.size();
		}

		segmentSelectView.setData("" + segmentOne, "" + segmentTwo, ""
				+ segmentThree, "" + segmentFour);
	}

	private void initViews(View view) {
		listView = (ListView) view.findViewById(R.id.devices_listView);
		segmentSelectView = (segmentSelectView) view
				.findViewById(R.id.segmentation_view);
		searchBarView = (SearchBarView) view.findViewById(R.id.search_view);
		followView = (FollowingSelectView) view.findViewById(R.id.follow_view);
	}

	/**
	 * 初始化FollowViewde 状态
	 */
	private void initFollowViewStatus() {
		List<Devices> beans = helper.GetUserList(loadName);
		devicesAdapter.setLayout(false);
		devicesAdapter.notifyDataSetChanged();
		select_ = false;
		PubUtil.Logo2Clicked = false;
		PubUtil.Logo1Clicked = false;
		PubUtil.LogoSearchClicked = false;
		PubUtil.layoutNum = 0;
		mAbTitleBar.setLogo2(R.drawable.home_old);
		mAbTitleBar.setLogo(R.drawable.follow_iv);
		// 将数据库中所有的设备的warn的数值初始化为“1”
		for (Devices bean : beans) {
			helper.UpdateWarnValue("1", bean.getIMEI());
		}

	}

	private void hideSearchView() {
		PubUtil.layoutNum = 0;
		toggleMode(segmentSelectView, searchBarView, 1);
		refreshListView(segmentIndex, false);
	}

	private void hideFollowingView() {
		toggleMode(segmentSelectView, followView, 1);
		refreshListView(segmentIndex, true);
	}

	private void showSearchView() {
		// initFollowViewStatus();
		PubUtil.LogoSearchClicked = true;
		refreshListView(0, false);
		toggleMode(searchBarView, segmentSelectView, -1);

	}

	private void showFollowView() {
		iv.setBackgroundResource(R.drawable.check);
		toggleMode(followView, segmentSelectView, -1);
	}

	private void toggleMode(View showView, View hideView, int direction) {
		int hight = segmentSelectView.getHeight() + 10;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				hideView.getLayoutParams());
		params.setMargins(0, direction * hight, 0, 0);
		hideView.setLayoutParams(params);

		AnimationSet animationSet = new AnimationSet(true);
		animationSet.setDuration(500);
		animationSet.setInterpolator(new DecelerateInterpolator());
		Animation animation1 = new TranslateAnimation(0, 0, -direction * hight,
				0);
		animationSet.addAnimation(animation1);
		AlphaAnimation animAlpha = new AlphaAnimation(1.0f, 0.7f);
		animationSet.addAnimation(animAlpha);
		hideView.startAnimation(animationSet);

		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				showView.getLayoutParams());
		params2.setMargins(0, 0, 0, 0);
		showView.setLayoutParams(params2);
		Animation animation2 = new TranslateAnimation(0, 0, -direction * hight,
				0);
		animation2.setDuration(500);
		animation2.setInterpolator(new DecelerateInterpolator());
		showView.startAnimation(animation2);
	}

	private void initSearchView() {
		int location[] = new int[3];
		searchBarView.getLocationInWindow(location);
		int hight = segmentSelectView.getHeight();
		int ydix = -location[1] - hight;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				searchBarView.getLayoutParams());
		params.setMargins(0, 400, 0, 0);
		searchBarView.setLayoutParams(params);
		searchBarView.setVisibility(View.VISIBLE);

	}

	private void initFollowView() {
		int location[] = new int[3];
		followView.getLocationInWindow(location);
		int hight = segmentSelectView.getHeight();
		int ydix = -location[2] - hight;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				followView.getLayoutParams());
		params.setMargins(0, 400, 0, 0);
		followView.setLayoutParams(params);
		followView.setVisibility(View.VISIBLE);

	}

	@Override
	public void onResume() {
		super.onResume();
		showContentView();
		mHandler.sendEmptyMessage(111);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(9);
		mHandler.sendEmptyMessage(104);
		super.onStop();
	}

	private void queryOnlineDevices2() {
		String url = Constant.URL + "/WebService/GLService.asmx/MemberOnLine";
		http = AbHttpUtil.getInstance(getActivity());
		http.setTimeout(1000);
		AbRequestParams params = new AbRequestParams();
		params.put("name", application.mUser.getUserName());
		// mj_showLoadView();
		http.post(url, params, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				XMLHelper.getResult("string", content,
						new XMLHelper.CallBack() {
							@Override
							public void getResult(String result) {
								JSONObject jobj = null;
								try {
									jobj = new JSONObject(result);
								} catch (JSONException e) {
									AbToastUtil.showToast(mActivity, "系统返回异常！");
									e.printStackTrace();
								}
								String r = jobj.optString("Result");
								if (r.equals("ok")) {
									try {
										ArrayList<Devices> data = new ArrayList<Devices>();
										parseJsonOnline(
												jobj.getJSONArray("Model"),
												data);
										DebugLog.d("data.size =" + data.size());
										onLineDeviceList.clear();
										onLineDeviceList.addAll(data);
										mHandler.sendEmptyMessage(102);
									} catch (JSONException e) {
										e.printStackTrace();
									}

								} else {
									AbToastUtil.showToast(mActivity, "获取数据异常！");
								}

								// mj_hideLoadView();
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


	/**
	 * 解析在线刷新的数据
	 */
	public void parseJsonOnline(JSONArray jsonArray, List<Devices> devicesList) {
		if (null == devicesList) {
			devicesList = new ArrayList<Devices>();
		}
		devicesList.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jb = jsonArray.optJSONObject(i);
			Devices device = new Devices();
			device.setIMEI(jb.optString("IMEI"));
			device.setIMSI(jb.optString("IMSI"));
			device.setName(jb.optString("Name"));
			device.setOnTime(jb.optString("OnTime"));
			device.setLineStatus(jb.optString("LineStatus"));
			device.setFromDate(jb.optString("FromDate"));
			device.setWarnNo(jb.optString("WarnNo"));
			device.setSIMNo(jb.optString("SIMNo"));
			device.setUserName(jb.optString("UserName"));
			devicesList.add(device);
		}

	}

	private void queryOfflineDevices2() {
		// mj_showLoadView();
		String url = Constant.URL + "/WebService/GLService.asmx/MemberOffLine";
		http = AbHttpUtil.getInstance(getActivity());
		http.setTimeout(10000);
		AbRequestParams params = new AbRequestParams();
		params.put("name", application.mUser.getUserName());
		http.post(url, params, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				XMLHelper.getResult("string", content,
						new XMLHelper.CallBack() {
							@Override
							public void getResult(String result) {
								JSONObject jobj = null;
								try {
									jobj = new JSONObject(result);
								} catch (JSONException e) {
									AbToastUtil.showToast(mActivity, "系统返回异常！");
									e.printStackTrace();
								}
								String r = jobj.optString("Result");
								if (r.equals("ok")) {

									if (success) {
										success = false;
//										AbToastUtil.showToast(mActivity,
//												"Timer+++++kaiq");
									} else {
										if (PubUtil.refresh == 1) {
											PubUtil.refresh = 0;
											AbToastUtil.showToast(mActivity,
													"获取列表成功！");
										}
									}
									try {
										ArrayList<Devices> data = new ArrayList<Devices>();
										parseJsonOnline(
												jobj.getJSONArray("Model"),
												data);
										DebugLog.d("data.size =" + data.size());
										offLineDeviceList.clear();
										offLineDeviceList.addAll(data);
										mHandler.sendEmptyMessage(103);
									} catch (JSONException e) {
										e.printStackTrace();
									}

								} else {
									AbToastUtil.showToast(mActivity, "获取数据异常！");
								}

								// mj_hideLoadView();
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
	public void onDestroy() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
	}
}
