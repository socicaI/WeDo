package com.example.wedo.SearchFilter;

public class ItemModel {
    private String imageResource;
    private String text;

    public ItemModel(String imageResource, String text1) {
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

