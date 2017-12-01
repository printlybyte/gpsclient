package com.mj.gpsclient.model;

/**
 * Created by majin on 15/5/27.
 */
public class Devices {
	
	
	
	public static final String ID = "_id";
	public static final String IMEINUM = "imei";
	public static final String IMSINUM = "imsi";
	public static final String NAME = "name";
	public static final String ONTIME = "ontime";
	public static final String LINESTATUS = "linestatus";
	public static final String FROMDATE = "FromDate";
	public static final String WARNNO = "WarnNo";
	public static final String SINNO = "SIMNo";
	public static final String USERNAME = "UserName";
	public static final String SELECTSTATUS = "selectstatus";
	public static final String LAT = "Lat";
	public static final String LNG = "Lng";
	public static final String STARTTIME = "startTime";
	public static final String ADDRESS = "address";

//    IMEI ：终端IMEI码
//    IMSI ：终端IMSI码
//    Name ：终端名称
//    OnTime ：在线时间
//    LineStatus ：在线状态
//    FromDate ：到期时间
//    WarnNo ：手机号码
//    SIMNo ：报警电话
//    UserName ：所属用户组
    private String IMEI ;
    private String IMSI;
    private String Name;
    private String OnTime;
    private String LineStatus;
    private String FromDate;
    private String WarnNo;
    private String SIMNo;
    private String UserName;
    private String selectStatus;
    private String Lat;
    private String Lng;
    
    private String Speed;
    private String Orientation;
    private String LocalType;

    public String getAderess() {
        return Aderess;
    }

    public void setAderess(String aderess) {
        Aderess = aderess;
    }

    private String  Aderess;
    
    public String getLat() {
		return Lat;
	}


	public String getSpeed() {
		return Speed;
	}


	public void setSpeed(String speed) {
		Speed = speed;
	}


	public String getOrientation() {
		return Orientation;
	}


	public void setOrientation(String orientation) {
		Orientation = orientation;
	}


	public String getLocalType() {
		return LocalType;
	}


	public void setLocalType(String localType) {
		LocalType = localType;
	}


	public void setLat(String lat) {
		Lat = lat;
	}


	public String getLng() {
		return Lng;
	}


	public void setLng(String lng) {
		Lng = lng;
	}

	private String startTime;
    



    public String getSelectStatus() {
		return selectStatus;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public void setSelectStatus(String selectStatus) {
		this.selectStatus = selectStatus;
	}

	public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getOnTime() {
        return OnTime;
    }

    public void setOnTime(String onTime) {
        OnTime = onTime;
    }

    public String getLineStatus() {
        return LineStatus;
    }

    public void setLineStatus(String lineStatus) {
        LineStatus = lineStatus;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getWarnNo() {
        return WarnNo;
    }

    public void setWarnNo(String warnNo) {
        WarnNo = warnNo;
    }

    public String getSIMNo() {
        return SIMNo;
    }

    public void setSIMNo(String SIMNo) {
        this.SIMNo = SIMNo;
    }





}
