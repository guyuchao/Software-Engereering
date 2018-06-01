package com.bean;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by guyuchao on 18-3-29.
 */
public class MarkInfo implements Serializable {


    private double lng,lat;
    private String name;
    private String distance;
    private LatLng latlng;

    public String getId() {
        return id;
    }

    private String id;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int imgId;

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public MarkInfo(String id,String name,double lng, double lat, String distance, int imgId) {
        this.id=id;
        this.lng = lng;
        this.name=name;
        this.lat = lat;
        this.distance = distance;
        this.imgId=imgId;
        latlng=new LatLng(lat, lng);
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public LatLng getLatlng() {
        return latlng;
    }
    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

}