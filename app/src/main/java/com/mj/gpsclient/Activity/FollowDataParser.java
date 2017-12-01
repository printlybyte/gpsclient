package com.mj.gpsclient.Activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mj.gpsclient.model.DeviceTrace;
import com.mj.gpsclient.model.Devices;

import android.text.TextUtils;


public class FollowDataParser {
	

	public static ArrayList<DeviceTrace> postnums(String userName,String imei,String startTime) {
		
		Long endTime_l = System.currentTimeMillis();
		String endTime = PubUtil.getDateToString(endTime_l);
		PubUtil.endTime = endTime;
		ArrayList<DeviceTrace> array=new ArrayList<DeviceTrace>();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userName", userName);
		params.put("imei", imei);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		String str = PubUtil.requestHttpPostURL("/WebService/GLService.asmx/GetTrackBack", params);
		if(str ==null){
			return array;
		}
		String json=PubUtil.getStr(str);
		try {
			
			JSONObject obj = new JSONObject(json);
			JSONArray list=obj.getJSONArray("Model");
			for(int i=0;i<list.length();i++){
				DeviceTrace bean= new DeviceTrace();
				bean.setImei(imei);
				bean.setLongitude(list.getJSONObject(i).getString("Longitude"));
				bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
				bean.setLatitude(list.getJSONObject(i).getString("Latitude"));
//				bean.setSpeed(list.getJSONObject(i).getString("Speed"));
//				bean.setOrientation(list.getJSONObject(i).getString("Direction"));
//				bean.setLocalType(list.getJSONObject(i).getString("LocType"));
//				bean.setImei(list.getJSONObject(i).getString("Imei"));
				array.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
	public static ArrayList<DeviceTrace> postnum(String num) {
		ArrayList<DeviceTrace> array = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("imeis", num);
		// params.put("UserName", name);

		String str = PubUtil.requestHttpPostURL(
				"/WebService/GLService.asmx/GetMemberPositioningByImeis",
				params);
		
		if (str==null) {
			return array;
		}
		String json = PubUtil.getStr(str);
		try {
			array = new ArrayList<DeviceTrace>();
			JSONObject obj = new JSONObject(json);
			JSONArray list = obj.getJSONArray("Model");

			for (int i = 0; i < list.length(); i++) {
				DeviceTrace bean = new DeviceTrace();
				bean.setLatitude(list.getJSONObject(i).getString("Latitude"));
				bean.setLongitude(list.getJSONObject(i).getString("Longitude"));
				bean.setImei(list.getJSONObject(i).getString("Imei"));
//				bean.setSpeed(list.getJSONObject(i).getString("Speed"));
//				bean.setOrientation(list.getJSONObject(i).getString("Direction"));
//				bean.setLocalType(list.getJSONObject(i).getString("LocType"));
				bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
				array.add(bean);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
}
