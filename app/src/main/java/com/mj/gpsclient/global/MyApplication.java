package com.mj.gpsclient.global;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ab.global.AbActivityManager;
import com.ab.global.AbAppConfig;
import com.baidu.mapapi.SDKInitializer;

import com.mj.gpsclient.model.User;


public class MyApplication extends Application {

	// 登录用户
	public User mUser = null;
	public boolean userPasswordRemember = false;
	public boolean isFirstStart = true;
	public SharedPreferences mSharedPreferences = null;
    /**
     * 单例
     */
    private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;

        // 注册自定义的错误处理器
//        CrashHandler crash = CrashHandler.getInstance();
//		crash.init(getApplicationContext());

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());


		mSharedPreferences = getSharedPreferences(AbAppConfig.SHARED_PATH,
				Context.MODE_PRIVATE);
		initLoginParams();
	}

	/**
	 * 上次登录参数
	 */
	private void initLoginParams() {
		SharedPreferences preferences = getSharedPreferences(
				AbAppConfig.SHARED_PATH, Context.MODE_PRIVATE);
		String userName = preferences.getString(Constant.USERNAMECOOKIE, null);
		String userPwd = preferences.getString(Constant.USERPASSWORDCOOKIE,
				null);
		Boolean userPwdRemember = preferences.getBoolean(
				Constant.USERPASSWORDREMEMBERCOOKIE, false);
		if (userName != null) {
			mUser = new User();
			mUser.setUserName(userName);
			mUser.setPassword(userPwd);
			userPasswordRemember = userPwdRemember;
		}
	}

	public void updateLoginParams(User user) {
		mUser = user;
		if (userPasswordRemember) {
			Editor editor = mSharedPreferences.edit();
			editor.putString(Constant.USERNAMECOOKIE, user.getUserName());
			editor.putString(Constant.USERPASSWORDCOOKIE, user.getPassword());
			editor.putBoolean(Constant.ISFIRSTSTART, true);
			editor.commit();
		} else {
			Editor editor = mSharedPreferences.edit();
			editor.putBoolean(Constant.ISFIRSTSTART, false);
			editor.commit();
		}
		isFirstStart = false;
	}

	/**
	 * 清空上次登录参数
	 */
	public void clearLoginParams() {
		Editor editor = mSharedPreferences.edit();
        editor.putString(Constant.USERPASSWORDCOOKIE, "");
		editor.commit();
        mUser.setPassword("");
	}

    // 单例模式中获取唯一的MyApplication实例
    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * @Title: exit
     * @Description:用于异常退出
     * @return: void
     */
    public void exit() {
//        try {
//
//        } catch (Exception ex) {
//        } finally {
//
//            System.exit(0);
//        }
        AbActivityManager.getInstance().clearAllActivity();
       // android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
