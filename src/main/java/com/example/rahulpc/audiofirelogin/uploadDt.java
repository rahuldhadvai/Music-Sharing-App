package com.example.rahulpc.audiofirelogin;


public class uploadDt {

    public String username;
    public String password;

    public uploadDt(String username,String password) {
        this.username = username;
        this.password = password;
    }

    public uploadDt(){}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
