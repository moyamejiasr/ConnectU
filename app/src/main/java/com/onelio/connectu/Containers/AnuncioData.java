package com.onelio.connectu.Containers;

import java.util.Date;

public class AnuncioData {

  private String type;
  private String title;
  private String date;
  private String text;
  private int rating;
  private String subject;
  private String teacher;
  private Date adate;
  private String id;
  private boolean isNew;

  public AnuncioData() {
    type = "";
    title = "";
    date = "";
    text = "";
    subject = "";
    teacher = "";
    rating = 0;
    id = "";
    isNew = false;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isNew() {
    return isNew;
  }

  public void setNew(boolean isNew) {
    this.isNew = isNew;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDate() {
    return date;
  }

  public Date getDate(boolean advanced) {
    return adate;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setDate(Date adate) {
    this.adate = adate;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getTeacher() {
    return teacher;
  }

  public void setTeacher(String teacher) {
    this.teacher = teacher;
  }
}
