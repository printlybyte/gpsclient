package com.mj.gpsclient.bean;

public class MemerBean {

	public static final String ID = "_id";
	public static final String PICPATH = "picpath";
	public static final String PIC = "pic";
	public static final String PICTYPE = "pictype";
	public static final String NAME = "name";
	public static final String NUM = "num";
	public static final String STATUS = "status";
	public static final String SELECT_STATUS = "select_status";
	public static final String LOC_TYPE = "loc_type";
	public static final String TIME = "time";
	public static final String ADDRESS = "addressName";
	public static final String LO = "lo";
	public static final String LA = "la";
	public static final String ADDRESSDEC = "addressdec";

	private String picpath;
	private int pic;
	private String pictype;
	private String name;
	private String num;
	private String status;
	private String select_status;
	private String loc_type;
	private String time;
	private String addressName;
	private String lo;
	private String la;
	private String addressdec;

	public String getLo() {
		return lo;
	}

	public void setLo(String lo) {
		this.lo = lo;
	}

	public String getLa() {
		return la;
	}

	public void setLa(String la) {
		this.la = la;
	}

	public String getAddressdec() {
		return addressdec;
	}

	public void setAddressdec(String addressdec) {
		this.addressdec = addressdec;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getPicpath() {
		return picpath;
	}

	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	public int getPic() {
		return pic;
	}

	public void setPic(int pic) {
		this.pic = pic;
	}

	public String getPictype() {
		return pictype;
	}

	public void setPictype(String pictype) {
		this.pictype = pictype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSelect_status() {
		return select_status;
	}

	public void setSelect_status(String select_status) {
		this.select_status = select_status;
	}

	public String getLoc_type() {
		return loc_type;
	}

	public void setLoc_type(String loc_type) {
		this.loc_type = loc_type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
