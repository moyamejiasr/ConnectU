package com.onelio.connectu.Containers;

import java.util.Date;

public class StringReminderData {

  private String text;
  private Date date;

  public StringReminderData() {
    text = "";
    date = new Date();
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
