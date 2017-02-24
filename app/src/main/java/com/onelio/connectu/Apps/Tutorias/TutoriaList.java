package com.onelio.connectu.Apps.Tutorias;

/**
 * Created by Onelio on 16/12/2016.
 */

public class TutoriaList {
    private String name;
    private String startdate;
    private String state;
    private String user;
    private String src;
    private String id;
    private String sign;

    public String getStartdate() {
        return startdate;
    }
    public void setStartdate(String text) {
        this.startdate = text;
    }

    public String getState() {
        return state;
    }
    public void setState(String text) {
        this.state = text;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String text) {
        this.user = text;
    }

    public String getName() {
        return name;
    }
    public void setName(String text) {
        this.name = text;
    }

    public String getSrc() {
        return src;
    }
    public void setSrc(String text) {
        this.src = text;
    }

    public String getId() {
        return id;
    }
    public void setId(String text) {
        this.id = text;
    }

    public String getSign() {
        return sign;
    }
    public void setSign(String text) {
        this.sign = text;
    }

}
