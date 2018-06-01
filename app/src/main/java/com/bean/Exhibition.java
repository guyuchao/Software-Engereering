package com.bean;

/**
 * Created by guyuchao on 18-5-9.
 */

public class Exhibition {
    private String id;
    private String title;
    private String museum;
    private String museum_id;
    private String time;
    private String address;
    private String introduce;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMuseum_id() {
        return museum_id;
    }

    public void setMuseum_id(String museum_id) {
        this.museum_id = museum_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMuseum() {
        return museum;
    }

    public void setMuseum(String museum) {
        this.museum = museum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
