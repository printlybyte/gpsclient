package com.mj.gpsclient.model;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by majin on 15/5/27.
 */
public class DeviceLocation {
    private int id;
    private String OnTime;
    private String Longitude;
    private String Latitude;
    private LatLng latLng;

    public DeviceLocation(int id,String OnTime,String Latitude,String Longitude){
        this.id =id;
        this.OnTime =OnTime;
        this.Longitude =Longitude;
        this.Latitude =Latitude;
        this.latLng =new LatLng(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOnTime() {
        return OnTime;
    }

    public void setOnTime(String onTime) {
        OnTime = onTime;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
