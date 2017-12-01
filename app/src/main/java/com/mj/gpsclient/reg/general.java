package com.mj.gpsclient.reg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class general {

	public static List<String> isPosCancelMemp = new ArrayList<String>();
	public static List<String> isPosCancelWIFIp = new ArrayList<String>();
	public static List<String> isPosCancelJizhanp = new ArrayList<String>();

	public String logPath = "";
	public String logFilename = "";

	// ����һ��public static��list������activity
	public static List<Activity> activityList = new ArrayList();

	public void AddActivity(Activity ac) {
		this.activityList.add(ac);
	}

	// finish����list�е�activity
	public static void killall() {
		int siz = activityList.size();
		for (int i = 0; i < siz; i++) {
			if (activityList.get(i) != null) {
				activityList.get(i).finish();
			}
		}
		System.exit(0);
	}

	public static String GetTimeStringFromStamp(long stamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(stamp * 1000L);

		String format = "HH:mm yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(cal.getTime());
	}

	public static String GetTimeStringFromStampS(long stamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(stamp * 1000L);

		String format = "HH:mm:ss yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(cal.getTime());
	}

	// ���Բ��ͼƬ�ķ���
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		roundPx = 17.0f;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		// Bitmap output = bitmap;
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static boolean hasInternet(Activity activity, Context c) {
		ConnectivityManager manager = (ConnectivityManager) activity
				.getSystemService(c.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}
		return true;

	}

	public static boolean IsStringValid(String s) {
		if (s != null && !TextUtils.isEmpty(s)) {
			return true;
		} else {
			return false;
		}
	}

	public static String errstr;

	public static String GetErrorStr() {
		if (errstr == null)
			return "ʧ��";

		return errstr;
	}

	public static void SetErrorStr(String es) {
		errstr = es;

	}

}
