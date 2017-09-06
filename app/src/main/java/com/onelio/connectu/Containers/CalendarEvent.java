package com.onelio.connectu.Containers;

import java.util.Date;

public class CalendarEvent {
    private String type;
    private boolean allDay;
    private String title;
    private String subtitle;
    private String text;
    private String loc;
    private Date start;
    private Date end;
    private String sigua;
    private String fullData;

    public CalendarEvent() {
        type = "";
        allDay = false;
        title = "";
        subtitle = "";
        text = "";
        loc = "";
        start = new Date();
        end = new Date();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getSigua() {
        return sigua;
    }

    public void setSigua(String sigua) {
        this.sigua = sigua;
    }

    public String getFullData() {
        return fullData;
    }

    public void setFullData(String fullData) {
        this.fullData = fullData;
    }
}
