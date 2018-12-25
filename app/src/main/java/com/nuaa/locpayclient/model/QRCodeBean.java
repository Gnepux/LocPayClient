package com.nuaa.locpayclient.model;

public class QRCodeBean {

    private String aid;

    private long time;

    private double lat;

    private double lon;

    private String sign;

    public QRCodeBean(String aid, long time, double lon, double lat, String sign) {
        this.aid = aid;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.sign = sign;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
