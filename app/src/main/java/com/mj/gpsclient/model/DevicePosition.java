package com.mj.gpsclient.model;

/**
 * Created by majin on 15/5/30.
 */
public class DevicePosition {
    private String Name;
    private String OnTime;
    private String LocType ;
    private double Longitude;
    private double Latitude;
    private String BaseInfo;
    private String Speed;
    private String Direction;
    private String DeviceStatus ;
    private boolean isOnLine;
    private String Address;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean isOnLine) {
        this.isOnLine = isOnLine;
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

    public String getLocType() {
        return LocType;
    }

    public void setLocType(String locType) {
        LocType = locType;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getBaseInfo() {
        return BaseInfo;
    }

    public void setBaseInfo(String baseInfo) {
        BaseInfo = baseInfo;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public String getDeviceStatus() {
        return DeviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        DeviceStatus = deviceStatus;
    }


}
