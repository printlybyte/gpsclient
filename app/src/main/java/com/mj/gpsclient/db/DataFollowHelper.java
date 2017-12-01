package com.mj.gpsclient.db;


import java.util.ArrayList;
import java.util.List;

import com.mj.gpsclient.model.Devices;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataFollowHelper {

	private static int DB_VERSION = 7;
	private SQLiteDatabase db;
	private SqLiteHelper dbHelper;
	private static String DB_NAME = "follow.db";
	
	public DataFollowHelper(Context context){
		
		dbHelper = new SqLiteHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	public void Close() {
		db.close();
		dbHelper.close();
	}
	//获取所有设备记录信息
	public List<Devices> GetUserList(String userName) {
		List<Devices> userList = new ArrayList<Devices>();
		Cursor cursor = db.query(SqLiteHelper.TB_NAME, null, Devices.USERNAME + "=?", new String[]{userName}, null,
				null, Devices.FROMDATE + " ASC"); // DESC
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {

			Devices user = new Devices();
			user.setIMEI(cursor.getString(1));
			user.setIMSI(cursor.getString(2));
			user.setName(cursor.getString(3));
			user.setOnTime(cursor.getString(4));
			user.setLineStatus(cursor.getString(5));
			user.setFromDate(cursor.getString(6));
			user.setWarnNo(cursor.getString(7));
			user.setSIMNo(cursor.getString(8));
			user.setUserName(cursor.getString(9));
			user.setSelectStatus(cursor.getString(10));
			user.setLat(cursor.getString(11));
			user.setLng(cursor.getString(12));
			user.setStartTime(cursor.getString(13));
			userList.add(user);
			cursor.moveToNext();
		}
		cursor.close();
		return userList;
	}
	public Devices GetUser(String num) {
		Cursor cursor = db
				.query(SqLiteHelper.TB_NAME, null, Devices.IMEINUM + "=?",
						new String[] { num }, null, null, Devices.ID + " ASC"); // DESC
		cursor.moveToFirst();
		if (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			Devices user = new Devices();
			user.setIMEI(cursor.getString(1));
			user.setIMSI(cursor.getString(2));
			user.setName(cursor.getString(3));
			user.setOnTime(cursor.getString(4));
			user.setLineStatus(cursor.getString(5));
			user.setFromDate(cursor.getString(6));
			user.setWarnNo(cursor.getString(7));
			user.setSIMNo(cursor.getString(8));
			user.setUserName(cursor.getString(9));
			user.setSelectStatus(cursor.getString(10));
			user.setLat(cursor.getString(11));
			user.setLng(cursor.getString(12));
			user.setStartTime(cursor.getString(13));
			cursor.close();
			return user;
		} else {
			return null;
		}

	}
	
	public Long SaveUserInfo(Devices user) {
		ContentValues values = new ContentValues();
		values.put(Devices.IMEINUM, user.getIMEI());
		values.put(Devices.IMSINUM, user.getIMSI());
		values.put(Devices.NAME, user.getName());
		values.put(Devices.ONTIME, user.getOnTime());
		values.put(Devices.LINESTATUS, user.getLineStatus());
		values.put(Devices.FROMDATE, user.getFromDate());
		values.put(Devices.WARNNO, user.getWarnNo());
		values.put(Devices.SINNO, user.getSIMNo());
		values.put(Devices.USERNAME, user.getUserName());
		values.put(Devices.SELECTSTATUS, user.getSelectStatus());
		values.put(Devices.LAT, user.getLat());
		values.put(Devices.LNG, user.getLng());
		values.put(Devices.STARTTIME, user.getStartTime());
		Long uid = db.insert(SqLiteHelper.TB_NAME, Devices.ID, values);
		return uid;
	}
	public boolean checkData(String num) {
		Cursor cursor = db
				.query(SqLiteHelper.TB_NAME, null, Devices.IMEINUM + "=?",
						new String[] { num }, null, null, Devices.ID + " ASC"); // DESC
		cursor.moveToFirst();
		if (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			cursor.close();
			return true;
		} else {
			return false;
		}
	}
	public int DelUserInfo(String num) {

		int id = db.delete(SqLiteHelper.TB_NAME, Devices.IMEINUM + "=?",
				new String[] { num });

		return id;

	}
	public int UpdateStatus(String select_status, String num) {
		ContentValues values = new ContentValues();
		values.put(Devices.SELECTSTATUS, select_status);
		int id = db.update(SqLiteHelper.TB_NAME, values, Devices.IMEINUM + "=? ",
				new String[] { num });
		return id;
	}
	
	public int UpdateStartTime(String StartTime, String num) {
		ContentValues values = new ContentValues();
		values.put(Devices.STARTTIME, StartTime);
		int id = db.update(SqLiteHelper.TB_NAME, values, Devices.IMEINUM + "=? ",
				new String[] { num });
		return id;
	}
	public int UpdateWarnValue(String WarnValue, String num) {
		ContentValues values = new ContentValues();
		values.put(Devices.WARNNO, WarnValue);
		int id = db.update(SqLiteHelper.TB_NAME, values, Devices.IMEINUM + "=? ",
				new String[] { num });
		return id;
	}
	public int UpdateLineStatus(String Line_status, String num) {
		ContentValues values = new ContentValues();
		values.put(Devices.LINESTATUS, Line_status);
		int id = db.update(SqLiteHelper.TB_NAME, values, Devices.IMEINUM + "=? ",
				new String[] { num });
		return id;
	}
	public int UpdatePositionInfo(String lat,String lng, String num) {
		ContentValues values = new ContentValues();
		values.put(Devices.LAT, lat);
		values.put(Devices.LNG, lng);
		int id = db.update(SqLiteHelper.TB_NAME, values, Devices.IMEINUM + "=? ",
				new String[] { num });
		return id;
	}
}
