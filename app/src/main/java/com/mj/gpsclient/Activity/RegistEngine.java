package com.mj.gpsclient.Activity;

import java.util.ArrayList;

import com.mj.gpsclient.model.Devices;


public class RegistEngine extends BaseStrategyHandler {

	private static RegistEngine registEngine;

	public static RegistEngine getInstance() {
		if (registEngine == null) {
			registEngine = new RegistEngine();
		}
		return registEngine;
	}

	public void postNums(final String nums, final ICallBack<ArrayList<Devices>> iCallBack) {
		//线程池调用执行子线程（new Runnable 或者 new thread）
		executor.execute(new Runnable() {
			@Override
			public void run() {
				if (iCallBack != null) {
					iCallBack.onTaskStart();
				}
				ArrayList<Devices> array = RegistParser.postnumsdata(nums);
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
