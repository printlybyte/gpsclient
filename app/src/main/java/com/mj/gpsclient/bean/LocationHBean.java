package com.mj.gpsclient.bean;

public class LocationHBean {

	private String Longitude;
	private String Latitude;
	private String ontime;
	private String Address;

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getOnTime() {
		return ontime;
	}

	public void setOnTime(String onTime) {
		ontime = onTime;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

}
