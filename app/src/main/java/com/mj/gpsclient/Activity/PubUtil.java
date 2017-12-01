package com.mj.gpsclient.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mj.gpsclient.model.DeviceTrace;
import com.mj.gpsclient.model.Devices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PubUtil {

	public static int layoutNum = 0;
	public static boolean Logo2Clicked = false;// Logo2被点击
	public static boolean Logo1Clicked = false;//  Logo1被点击
	public static boolean LogoSearchClicked = false;//  search按钮被点击
	public static int follow_bt_status = 100;// 跟踪列表里面有内容，处于编辑状态
	public static String endTime = "";
	public static int refresh = 0;
	public static int sizeOnLine = 0;
	public static ArrayList<Devices> arrays = new ArrayList<Devices>();
	private static  Marker mMarkerB;
	public static  Devices bean = null;
	public static boolean switchIsOn = false;//实时追踪中的switch按钮开关状态

	
	
	public static boolean follow_iv_pressed = false;//后台跟踪按钮被点击
	public static boolean follow_iv_pressed2 = false;//page2后台跟踪按钮被点击
	public static boolean split_screen_iv = false;//分屏显示按钮被点击
	public static boolean split_screen_iv2 = false;//page2分屏显示按钮被点击
	public static int PageNum = 1;//viewPage页码
	public static HashMap<String,Devices> followHash = new HashMap<String, Devices>();
	public static HashMap<String,Devices> split_sHash = new HashMap<String, Devices>();
	public static HashMap<String,Devices> followHash2 = new HashMap<String, Devices>();
	public static HashMap<String,Devices> split_sHash2 = new HashMap<String, Devices>();



	public static LatLng getLastInfo(ArrayList<DeviceTrace> array_trace, int i) {
		DeviceTrace bean = array_trace.get(i);
		String lat = bean.getLatitude();
		String lng = bean.getLongitude();
		LatLng latlng = new LatLng(Double.parseDouble(lat),
				Double.parseDouble(lng));
		LatLng latlng1 = PubUtil.convert(latlng);
		LatLng latlng1_ = PubUtil.DfInfomation(latlng1);
		return latlng1_;

	}
	public static Bitmap toRoundBitmap(Bitmap bitmap,String string,Context context) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		// 获取这个图片的宽和高
		int width_ = output.getWidth();
		int height_ = output.getHeight();

		// 定义预转换成的图片的宽度和高度
		int newWidth = 30;
		int newHeight =30;
	   int newWidth_ = dip2px(context, newWidth);
	   int newHeight_ = dip2px(context, newWidth);
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth_) / width;
		float scaleHeight = ((float) newHeight_) / height;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(output, 0, 0, width, height,
				matrix, true);

		Bitmap result_bitmap = Bitmap.createBitmap(resizedBitmap.getWidth()*2+60,
				resizedBitmap.getHeight() + 40, Config.ARGB_8888);
		Canvas result_canvas = new Canvas(result_bitmap);

		result_canvas.drawBitmap(resizedBitmap, resizedBitmap.getWidth()/2+30, 0, null);

		Paint fontpaint = new Paint();
		fontpaint.setColor(Color.parseColor("#FF020C"));
		fontpaint.setTextSize(scalaFonts(dip2px(context, 10)));
		fontpaint.setTypeface(Typeface.create("宋体", Typeface.BOLD));
		fontpaint.setAntiAlias(true);
		
		fontpaint.setFilterBitmap(true);

		result_canvas.drawText(string, resizedBitmap.getWidth()-50, resizedBitmap.getHeight() + 10, fontpaint);
		//10表示距离bitmap的距离

		// 将上面创建的Bitmap转换成Drawable对象，使得其可以使用在ImageView, ImageButton中
		BitmapDrawable bmd = new BitmapDrawable(result_bitmap);

		return bmd.getBitmap();
	}
	private static float scalaFonts(int size) {
		// 暂未实现
		return size;
	}
	public static LatLng convert(LatLng latLng) {
		CoordinateConverter c = new CoordinateConverter();
		c.from(CoordinateConverter.CoordType.GPS);
		c.coord(latLng);
		return c.convert();
	}

	public static LatLng DfInfomation(LatLng latlng) {
		DecimalFormat df = new DecimalFormat("0.0000");
		Double lat = latlng.latitude;
		Double lng = latlng.longitude;
		String lat_ = df.format(lat);
		String lng_ = df.format(lng);
		LatLng latlng_ = new LatLng(Double.parseDouble(lat_),
				Double.parseDouble(lng_));
		return latlng_;
	}

	// 将时间戳转成字符串
	public static String getDateToString(long time) {
		Date d = new Date(time);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sf.format(d);
	}

	// 判断网络是否正常

	public static boolean isConnected(Context context) {
		boolean isOk = true;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetInfo != null && !wifiNetInfo.isConnectedOrConnecting()) {
				if (mobNetInfo != null && !mobNetInfo.isConnectedOrConnecting()) {
					NetworkInfo info = connectivityManager
							.getActiveNetworkInfo();
					if (info == null) {
						isOk = false;
					}
				}
			}
			mobNetInfo = null;
			wifiNetInfo = null;
			connectivityManager = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isOk;
	}

	public static String requestHttpPostURL(String service,
			Map<String, String> parmas) {
		String url = "http://xb.xun365.net" + service;
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		HttpPost post = new HttpPost(url);
		String result = null;
		try {
			ArrayList<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>();
			if (parmas != null) {
				Set<String> keys = parmas.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					paramList.add(new BasicNameValuePair(key, parmas.get(key)));
				}
				UrlEncodedFormEntity se = new UrlEncodedFormEntity(paramList,
						"utf-8");
				post.setEntity(se);
			}
			HttpResponse response = httpClient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			int k = response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(responseEntity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getStr(String str) {
		int ii = 0;
		int j = 0;
		ii = str.indexOf("{");
		j = str.lastIndexOf("}");
		return str.substring(ii, j + 1);
	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	//获取两点之间的距离
	public static  double getDistance(double lat_a, double lng_a, double lat_b, double lng_b){
		double pk = 180 / 3.14169;
		double a1 = lat_a / pk;
		double a2 = lng_a / pk;
		double b1 = lat_b / pk;
		double b2 = lng_b / pk;
		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);
		return 6371000 * tt;
	}

}
