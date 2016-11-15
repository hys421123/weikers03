package com.team.witkers.bean;

import java.io.Serializable;

/**
 * Created by jin on 2016/9/29.
 */
public class LocationBean implements Serializable {

    private String title;
    private String content;
    private int IconId;
    private double latitude;
    private double longitude;

   public LocationBean(String title, String content, int IconId,double latitude,double longitude){
        this.title = title;
        this.content = content;
        this.IconId = IconId;
       this.latitude = latitude;
       this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIconId() {
        return IconId;
    }

    public void setIconId(int iconId) {
        IconId = iconId;
    }
}
