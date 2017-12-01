package com.mj.gpsclient.global;



public class Constant {
	
	public static final boolean DEBUG = true;
	public static final String sharePath = "andbase_share";
    public static final String USERSID = "user";
    //页面默认显示南京，登陆后显示注册用户的城市

    //cookies
    public static final String USERNAMECOOKIE = "cookieName";
    public static final String USERPASSWORDCOOKIE = "cookiePassword";
    public static final String USERPASSWORDREMEMBERCOOKIE = "cookieRemember";
    public static final String ISFIRSTSTART = "isfirstStart";
    
    // 连接超时
 	public static final int timeOut = 12000;
 	// 建立连接
 	public static final int connectOut = 12000;
 	// 获取数据
 	public static final int getOut = 60000;
 	
 	//1表示已下载完成
 	public static final int downloadComplete = 1;
 	//0表示未开始下载
 	public static final int undownLoad = 0;
 	//2表示已开始下载
 	public static final int downInProgress = 2;
 	//3表示下载暂停
 	public static final int downLoadPause = 3;
 	
 	public static final String BASEURL = "http://www.amsoft.cn/";
 	
 	//应用的key
 	//1512528
 	public final static String APPID = "1512528";
 		
 	//jfa97P4HIhjxrAgfUdq1NoKC
 	public final static String APIKEY = "jfa97P4HIhjxrAgfUdq1NoKC";

    public static  int FINAL_MSG =0;

    public static final int History_MSG_LOADEND =FINAL_MSG+1;

    public static final int History_MSG_LOAD_TODAY =FINAL_MSG+2;

    public static final int History_MSG_LOAD_CUSTOM =FINAL_MSG+3;

    public static final int History_MSG_TOGGLE_TIME__VIEW  =FINAL_MSG+4;

    public static final int History_MSG_PLAY  =FINAL_MSG+5;

    public static final int History_MSG_PAUSE  =FINAL_MSG+6;

    public static final int History_MSG_STOP  =FINAL_MSG+7;

    public static String URL = "http://xb.xun365.net";
    
    public static int REALTIME_SWITCH = 0;
    
    public static int MY_Location = 0;
}
