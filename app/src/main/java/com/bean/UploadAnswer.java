package com.bean;

/**
 * Created by guyuchao on 18-5-14.
 */

public class UploadAnswer {
    private String user_id;
    private String museum_id;
    private String addr;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMuseum_id() {
        return museum_id;
    }

    public void setMuseum_id(String museum_id) {
        this.museum_id = museum_id;
    }

    public UploadAnswer(String user_id, String museum_id, String addr, String description) {
        this.user_id = user_id;
        this.museum_id = museum_id;
        this.addr = addr;
        this.description = description;
    }

    public String getAddr() {

        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;
}
