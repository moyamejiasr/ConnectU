package com.onelio.connectu.Containers;

public class BubbleData {
    private String text;
    private String author;
    private String image;
    private String date;
    private boolean isMe;

    public BubbleData() {
        text = "";
        author = "";
        image = "";
        date = "";
        isMe = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
