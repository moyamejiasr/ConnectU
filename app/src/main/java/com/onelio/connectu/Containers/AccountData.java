package com.onelio.connectu.Containers;

public class AccountData {

    //Session
    private String loginURL;
    private String lt;
    private String execution;
    private String email;
    private String password;

    //Profile
    private boolean isLogged = false;
    private String name;
    private String pictureURL;

    //Settings
    private int notificationTime = 1800000; //30 min

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLogged(boolean isLogged){
        this.isLogged = isLogged;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLoginURL(String loginURL){
        this.loginURL = loginURL;
    }

    public String getLoginURL(){
        return loginURL;
    }

    public void setLt(String lt){
        this.lt = lt;
    }

    public String getLt(){
        return lt;
    }

    public void setExecution(String execution){
        this.execution = execution;
    }

    public String getExecution(){
        return execution;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setPictureURL(String pic) {
        this.pictureURL = pic;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public int getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(int notificationTime) {
        this.notificationTime = notificationTime;
    }


}
