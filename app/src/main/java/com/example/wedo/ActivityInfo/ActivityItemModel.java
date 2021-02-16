package com.example.wedo.ActivityInfo;

public class ActivityItemModel {
    private String dateTime;
    private String user;
    private String status;
    private String text;

    public ActivityItemModel(String dateTime, String user, String status, String text) {
        this.dateTime = dateTime;
        this.user = user;
        this.status = status;
        this.text = text;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
