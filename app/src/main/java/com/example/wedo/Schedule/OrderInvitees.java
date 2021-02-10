package com.example.wedo.Schedule;

public class OrderInvitees {
    private String imageResource;
    private String text;

    public OrderInvitees(String imageResource, String text1) {
        this.imageResource = imageResource;
        this.text = text1;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
