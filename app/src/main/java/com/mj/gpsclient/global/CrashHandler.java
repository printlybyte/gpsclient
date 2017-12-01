package com.mj.gpsclient.global;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
	// 用于格式化日期,作为日志文件名的一部分  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 
	public static final String TAG = "CrashHandler";
	// 用来存储设备信息和异常信息  
    private Map<String, String> infos = new HashMap<String, String>(); 
	private Context mContext;
	private static CrashHandler INSTANCE = new CrashHandler();
	

	private CrashHandler() { 
		//确保只有一个异常实例
	}
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context con) {
		mContext = con;
		// 取代系统默认的错误处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, final Throwable ex) {

		// 有网络直接发送错误信息到服务器. 没有网络保存错误信息到本地数据库
		try {
			new Thread(new Runnable() {
				public void run() {

//					if (CommonTools.checkNetWorkStatus(mContext)) {
//
//						String errMsg = "";
//						StringWriter sw = new StringWriter();
//			            ex.printStackTrace(new PrintWriter(sw, true));
//						if (sw != null && !("").equals(sw.toString())) errMsg = sw.toString().replaceAll("\n", "<br/>");
//
//						ExceptionEntity exEntity = new ExceptionEntity();
//						exEntity.setImei(Utils.getMobileIMEI(mContext));
//						exEntity.setBrand(Utils.getMoblieBrand());
//						exEntity.setModel(Utils.getMoblieModel());
//						exEntity.setAppVersion(Utils.getAppVersion(mContext));
//						exEntity.setSdkVerion(Build.VERSION.RELEASE);
//						exEntity.setSdkIntVersion(Build.VERSION.SDK_INT);
//						exEntity.setNetType(CommonTools.checkNetworkType(mContext));
//						exEntity.setOperator(Utils.getMoblieOperator(mContext));
//						exEntity.setResolution(Utils.getMobileWidth(mContext) + "*" + Utils.getMobileHeight(mContext));
//						exEntity.setExType(String.valueOf(ex.getCause()));
//						exEntity.setExMsg(errMsg);
//						exEntity.setExTime(System.currentTimeMillis());
//
//						StatisticsInterfaces.exception(exEntity);
//						Intent startIntent = new Intent(mContext,MusicPlayService.class);
//						mContext.stopService(startIntent);
//
//					} else {
//					}
				}
			}).start();
		} catch (Exception e) {
//			CommonTools.error("提交错误信息发生错误#####################################################");
			e.printStackTrace();
//			CommonTools.error("########################################################################");
		} finally {
//			CommonTools.error("完整错误信息#####################################################");
			ex.printStackTrace();
//			CommonTools.error("################################################################");
			
			// 使用Toast来显示异常信息
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(mContext, "出现未知错误，请重新启动应用", Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}.start();

			collectDeviceInfo(mContext);
			saveCrashInfo2File(ex);  
			// 等待 1.5 秒后退出程序
			try {
				Thread.sleep(1500);
				MyApplication.getInstance().exit();
			} catch (Exception e) {
				
			}
		}

		
	}
	
    /** 
     * 收集设备参数信息 
     * @param ctx 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "an error occured when collect package info", e);  
        }  
  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                field.setAccessible(true);  
                infos.put(field.getName(), field.get(null).toString());  
                Log.d(TAG, field.getName() + " : " + field.get(null));  
            } catch (Exception e) {  
                Log.e(TAG, "an error occured when collect crash info", e);  
            }  
        }  
    }  
  
    /** 
     * 保存错误信息到文件中 
    * 
     * @param ex 
     * @return  返回文件名称,便于将文件传送到服务器 
     */  
    private String saveCrashInfo2File(Throwable ex) {  
        StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
  
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".txt";  
              
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                String path = "/storage/emulated/0/crash/";
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                fos.write(sb.toString().getBytes());  
                fos.close();  
            }  
  
            return fileName;  
        } catch (Exception e) {  
            Log.e(TAG, "an error occured while writing file...", e);  
        }  
  
        return null;  
    }

}
