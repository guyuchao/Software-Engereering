package com.bean;

/**
 * Created by guyuchao on 18-4-30.
 */

public class Nearest {
    private String id;
    private String name;
    private String introduce;
    private String open_time;
    private String edu_activity;
    private String collection;
    private String academic;
    private String lng;
    private String lat;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getOpen_time() {
        return open_time;
    }

    public String getEdu_activity() {
        return edu_activity;
    }

    public String getCollection() {
        return collection;
    }

    public String getAcademic() {
        return academic;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public String getCity() {
        return city;
    }

    public String getDistance() {
        return distance;
    }

    private String city;
    private String distance;
}
