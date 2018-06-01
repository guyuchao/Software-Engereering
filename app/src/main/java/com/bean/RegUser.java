package com.bean;

/**
 * Created by guyuchao on 18-3-29.
 */

public class RegUser {
    public RegUser(String name, String pwd){
        regname=name;
        regpassword=pwd;

    }

    private String regname;

    public String getRegname() {
        return regname;
    }

    public void setRegname(String regname) {
        this.regname = regname;
    }

    public String getRegpassword() {
        return regpassword;
    }

    public void setRegpassword(String regpassword) {
        this.regpassword = regpassword;
    }

    private String regpassword;


}
