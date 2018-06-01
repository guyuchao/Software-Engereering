package com.bean;

/**
 * Created by guyuchao on 18-3-20.
 */

public class LoginUser {
    public LoginUser(String name, String pwd){
        loginname=name;
        password=pwd;

    }

    private String loginname;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }
}
