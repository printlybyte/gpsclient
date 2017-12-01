package com.mj.gpsclient.Activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mj.gpsclient.model.Devices;




public class RegistParser  {
	
	public static ArrayList<Devices> postnumsdata(String nums) {
		ArrayList<Devices> array=null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("imeis", nums);

		String str = PubUtil.requestHttpPostURL("/WebService/GLService.asmx/GetMemberPositioningByImeis", params);
		if (str==null) {
			return array;
		}
		String json=PubUtil.getStr(str);
		try {
			array=new ArrayList<Devices>();
			JSONObject obj = new JSONObject(json);
			JSONArray list=obj.getJSONArray("Model");
			for(int i=0;i<list.length();i++){
				Devices bean=new Devices();
				bean.setIMEI(list.getJSONObject(i).getString("Imei"));
				bean.setLat(list.getJSONObject(i).getString("Latitude"));
				bean.setLng(list.getJSONObject(i).getString("Longitude"));
				bean.setOnTime(list.getJSONObject(i).getString("OnTime"));
				bean.setSpeed(list.getJSONObject(i).getString("Speed"));
				bean.setOrientation(list.getJSONObject(i).getString("Direction"));
				bean.setLocalType(list.getJSONObject(i).getString("LocType"));
//				bean.setAddress(list.getJSONObject(i).getString("Address"));
//				bean.setImei(list.getJSONObject(i).getString("Imei"));
//				bean.setAddress(list.getJSONObject(i).getString("Address"));
//				bean.setSpeed(list.getJSONObject(i).getString("Speed"));
//				bean.setDirection(list.getJSONObject(i).getString("Direction"));
				array.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}
}
