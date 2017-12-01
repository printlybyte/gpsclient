package com.mj.gpsclient.Activity;

import java.util.ArrayList;

import com.mj.gpsclient.model.DeviceTrace;


public class FollowDataEngin extends BaseStrategyHandler {
	

	private static FollowDataEngin FollowDataEngin;

	public static FollowDataEngin getInstance() {
		if (FollowDataEngin == null) {
			FollowDataEngin = new FollowDataEngin();
		}
		return FollowDataEngin;
	}

	public void postNums(final String userName,final String imei,final String startTime,
			final ICallBack<ArrayList<DeviceTrace>> iCallBack) {
		// 线程池调用执行子线程（new Runnable 或者 new thread）
		executor.execute(new Runnable() {
			@Override
			public void run() {
				if (iCallBack != null) {
					iCallBack.onTaskStart();
				}
				ArrayList<DeviceTrace> array = FollowDataParser.postnums(userName,imei,startTime);
				if (iCallBack != null) {
					if (array != null) {
						iCallBack.onTaskFinish(array);
					} else {
						iCallBack.onTaskError();
					}
				}
			}
		});

	}
	public void postNum(final String imei,
			final ICallBack<ArrayList<DeviceTrace>> iCallBack) {
		// 线程池调用执行子线程（new Runnable 或者 new thread）
		executor.execute(new Runnable() {
			@Override
			public void run() {
				if (iCallBack != null) {
					iCallBack.onTaskStart();
				}
				ArrayList<DeviceTrace> array = FollowDataParser.postnum(imei);
				if (iCallBack != null) {
					if (array != null) {
						iCallBack.onTaskFinish(array);
					} else {
						iCallBack.onTaskError();
					}
				}
			}
		});
		
		

	}

}
