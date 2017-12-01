package com.mj.gpsclient.db;

import com.mj.gpsclient.model.Devices;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqLiteHelper extends SQLiteOpenHelper {

	public static final String TB_NAME = "all_nums";
	public static final String TB_NAME_ = "follow_nums";

	public SqLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + "(" + Devices.ID
				+ " integer primary key," + Devices.IMEINUM + " varchar,"
				+ Devices.IMSINUM + " varchar," + Devices.NAME + " varchar,"
				+ Devices.ONTIME + " varchar," + Devices.LINESTATUS
				+ " varchar," + Devices.FROMDATE + " varchar," + Devices.WARNNO
				+ " varchar," + Devices.SINNO + " varchar," + Devices.USERNAME
				+ " varchar," + Devices.SELECTSTATUS + " varchar,"
				+ Devices.LAT + " varchar," + Devices.LNG + " varchar,"
				+ Devices.STARTTIME + " varchar" + ")");

		// db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME_ + "(" +
		// Devices.ID
		// + " integer primary key," + Devices.IMEINUM + " varchar,"
		// + Devices.IMSINUM + " varchar," + Devices.NAME + " varchar,"
		// + Devices.ONTIME + " varchar," + Devices.LINESTATUS + " varchar,"
		// + Devices.FROMDATE + " varchar," + Devices.WARNNO
		// + " varchar," + Devices.SINNO + " varchar,"
		// + Devices.USERNAME + " varchar," + Devices.SELECTSTATUS + " varchar"
		// + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
