package com.example.wedo.SearchFilter;

public class ItemModel {
    private String imageResource;
    private String text1;

    public ItemModel(String imageResource, String text1) {
        this.imageResource = imageResource;
        this.text1 = text1;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }
}

