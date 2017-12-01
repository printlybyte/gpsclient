package com.mj.gpsclient.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.util.AbToastUtil;
import com.ab.view.sample.AbViewPager;
import com.ab.view.sliding.AbBottomTabView;
import com.ab.view.titlebar.AbTitleBar;
import com.mj.gpsclient.R;
import com.mj.gpsclient.model.Devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbActivity {
	private AbBottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;
	private List<Devices> devices;
	private List<Devices> devicesesOnline;
	private List<Devices> devicesesOffline;
	private List<Devices> devicesesFollow;
	private Boolean isExit = false;
	private Boolean hha = false;
	private boolean clicked = false;
	private DevicesListFragment devicesListFragment;
	private DevicesMonitorFragment devicesMonitorFragment;
	private boolean search_btn = false;
	private ImageView img_search;

	// private String s;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_main);
		// System.out.println(s.equals("any string"));
		
		initData();
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("设备列表");
		// mAbTitleBar.setLogo(R.drawable.ic_launcher);
		mAbTitleBar.setTitleBarBackground(R.drawable.tab_top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		mAbTitleBar.setTitleTextMargin(0, 0, 0, 0);
		mAbTitleBar.setLogoLine(R.drawable.line);
		initTitleRightLayout();
		setBarTitle(0);
		mAbTitleBar.setTitleTextSize(20);
		mBottomTabView = (AbBottomTabView) findViewById(R.id.mBottomTabView);
		// 如果里面的页面列表不能下载原因：
		// Fragment里面用的AbTaskQueue,由于有多个tab，顺序下载有延迟，还没下载好就被缓存了。改成用AbTaskPool，就ok了。
		// 或者setOffscreenPageLimit(0)

		// 缓存数量
		mBottomTabView.getViewPager().setOffscreenPageLimit(5);
		((AbViewPager) mBottomTabView.getViewPager()).setPagingEnabled(false);

		mBottomTabView
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageScrolled(int i, float v, int i2) {

					}

					@Override
					public void onPageSelected(int i) {
						clicked = true;
						setBarTitle(i);
					}

					@Override
					public void onPageScrollStateChanged(int i) {

					}
				});
		devicesListFragment = new DevicesListFragment();
		devicesMonitorFragment = new DevicesMonitorFragment();
		Fragment page3 = new DevicesAlarmFragment();
		Fragment page4 = new MoreFragment();
		// Fragment page5 = new WbDeviceList();

		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(devicesListFragment);
		// mFragments.add(page5);
		// mFragments.add(devicesMonitorFragment);
		// mFragments.add(page3);
		mFragments.add(page4);

		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("信标列表");
		tabTexts.add("更多");

		// 设置样式
		mBottomTabView.setTabTextColor(Color.rgb(255, 255, 255));
		// mBottomTabView.setTabSelectColor(Color.rgb(234, 110, 9));
		mBottomTabView.setTabSelectColor(getResources().getColor(R.color.aaa));
		mBottomTabView.setTabTextSize(20);
		mBottomTabView.setTabBackgroundResource(R.drawable.tab_home_bg);
		mBottomTabView.setTabLayoutBackgroundResource(R.drawable.tab_home_bg);

		// 注意图片的顺序
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(R.drawable.tab_leftbt));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.tab_leftbt_press));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.carsmonitor_old));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.carsmonitor_old_press));
		// tabDrawables.add(this.getResources().getDrawable(R.drawable.warn_old));
		// tabDrawables.add(this.getResources().getDrawable(R.drawable.warn_old_press));
		tabDrawables.add(this.getResources().getDrawable(R.drawable.more_old));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.more_old_press));
		mBottomTabView.setTabCompoundDrawablesBounds(0, 0, 50, 50);
		// 演示增加一组
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);

		mBottomTabView.setTabPadding(10, 10, 10, 10);

	}

	public void setBarTitle(int i) {
		final AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setBackgroundColor(getResources().getColor(R.color.aaa));
		switch (i) {
		case 0:
			if(clicked){
				clicked = false;
				devicesListFragment.mHandler.sendEmptyMessage(111);
			}
			resetTileBar();
			mAbTitleBar.setTitleText("设备列表");
			View rightViewApp = mInflater.inflate(R.layout.refresh_btn, null);
			ImageView img = (ImageView) rightViewApp
					.findViewById(R.id.bu_refresh);
			img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (PubUtil.layoutNum != 1) {
						devicesListFragment.mHandler.sendEmptyMessage(1);
						PubUtil.refresh = 1;
					} else {

						if (search_btn) {
							search_btn = false;
							// 隐藏搜索View
							devicesListFragment.mHandler.sendEmptyMessage(11);
						}
						devicesListFragment.mHandler.sendEmptyMessage(8);
					}
				}
			});
			View rightViewApp_search = mInflater.inflate(R.layout.search_btn,
					null);
			img_search = (ImageView) rightViewApp_search
					.findViewById(R.id.search_bu);
			img_search.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (PubUtil.layoutNum != 1) {
						PubUtil.layoutNum = 1;
						search_btn = true;
						devicesListFragment.mHandler.sendEmptyMessage(0);
						devicesListFragment.mHandler.sendEmptyMessage(9);
					} else {
						devicesListFragment.mHandler.sendEmptyMessage(8);
						devicesListFragment.mHandler.sendEmptyMessage(111);
					}
				}
			});
			mAbTitleBar.addRightView(rightViewApp);
			mAbTitleBar.addRightView(rightViewApp_search);
			// 加载左边布局，实现点击事件
			mAbTitleBar.setLogo(R.drawable.follow_iv);
			mAbTitleBar.setLogoLine(null);
			mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (PubUtil.layoutNum != 1) {
						mAbTitleBar.setLogo(R.drawable.follow_iv_press);
						devicesListFragment.mHandler.sendEmptyMessage(2);
						devicesListFragment.mHandler.sendEmptyMessage(9);
					} else {
						devicesListFragment.mHandler.sendEmptyMessage(8);
					}
				}
			});
			if (!hha) {
				hha = true;
			} else {
				devicesListFragment.mHandler.sendEmptyMessage(111);
			}
			// 加载左边布局，实现点击事件
			mAbTitleBar.setLogo2(R.drawable.home_old);
			mAbTitleBar.setLogo2OnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (PubUtil.layoutNum != 1) {
						mAbTitleBar.setLogo2(R.drawable.home_old_press);
						devicesListFragment.mHandler.sendEmptyMessage(9);
						devicesListFragment.mHandler.sendEmptyMessage(4);
					} else {
						devicesListFragment.mHandler.sendEmptyMessage(8);
					}
				}
			});
			mAbTitleBar.setPadding(40, 0, 0, 0);
			break;
		case 1:
			resetTileBar();
			mAbTitleBar.setTitleText("更多");
			devicesListFragment.mHandler.sendEmptyMessage(9);
			devicesListFragment.mHandler.sendEmptyMessage(104);
			devicesListFragment.mHandler.sendEmptyMessage(105);
			devicesMonitorFragment.mHandler.sendEmptyMessage(0);
			break;
		default:
			break;
		}

	}

	private void resetTileBar() {
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.clearRightView();
		mAbTitleBar.setLogo(null);
		mAbTitleBar.setLogo2(null);
		mAbTitleBar.setLogoOnClickListener(null);
		mAbTitleBar.setPadding(0, 0, 0, 0);

	}

	private void initData() {
		Intent in = this.getIntent();
		String model = in.getStringExtra("model");
		if (TextUtils.isEmpty(model)) {
			return;
		}
		devices = new ArrayList<Devices>();
		devicesesOnline = new ArrayList<Devices>();
		devicesesOffline = new ArrayList<Devices>();
		devicesesFollow = new ArrayList<Devices>();
		devices.clear();
		devicesesOnline.clear();
		devicesesOffline.clear();

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(model);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jb = jsonArray.optJSONObject(i);
			Devices device = new Devices();
			device.setName(jb.optString("name"));
			device.setLineStatus(jb.optString("linestatus"));
			if (device.getLineStatus().equals("离线")) {
				devicesesOffline.add(device);
			} else {
				devicesesOnline.add(device);
			}
			devices.add(device);
		}

	}

	public List<Devices> getDevicesAll() {
		return devices;
	}

	public List<Devices> getDevicesesOnline() {
		return devicesesOnline;
	}

	public List<Devices> getDevicesesOffline() {
		return devicesesOffline;
	}

	public void refreshDeviceList(List<Devices> all, List<Devices> online,
			List<Devices> offline, List<Devices> followDeviceList) {
		devices.clear();
		devices.addAll(all);
		devicesesOnline.clear();
		devicesesOnline.addAll(online);
		devicesesOffline.clear();
		devicesesOffline.addAll(offline);
		devicesesFollow.clear();
		devicesesFollow.addAll(followDeviceList);
	}
	/**
	 * 描述：返回.
	 */
	@Override
	public void onBackPressed() {
		if (PubUtil.Logo1Clicked||PubUtil.Logo2Clicked ) {
			devicesListFragment.mHandler.sendEmptyMessage(8);
		} else {
			if (isExit == false) {
				isExit = true;
				AbToastUtil.showToast(MainActivity.this, "再按一次退出程序");
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						isExit = false;
					}

				}, 2000);
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override
	protected void onStart() {
		
		super.onStart();

	}

	private void initTitleRightLayout() {

	}
@Override
protected void onDestroy() {
	PubUtil.follow_bt_status = 100;
	super.onDestroy();
}
	public boolean isOnLine(String name) {
		boolean isOnline = false;
		for (Devices d : devicesesOnline) {
			if (d.getName().equals(name)) {
				isOnline = true;
				break;
			}
		}
		return isOnline;
	}

}
