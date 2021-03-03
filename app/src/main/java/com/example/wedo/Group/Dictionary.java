package com.example.wedo.Group;

/**
 * CustomAdapter와 MainCategoryActivity와 관련
 * RecyclerView의 한 줄에 보여줄 데이터를 클래스로 선언. item_list.xml에서 정의한 TextView 개수에 맞추어야 한다.
 */
public class Dictionary {
    private String id;

    private String user;

    private String people;

    private String imageResource;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public Dictionary(String id) {
        this.id = id;
    }
}

