package com.example.wedo;

/**
 * RecyclerView의 한 줄에 보여줄 데이터를 클래스로 선언. item_list.xml에서 정의한 TextView 개수에 맞추어야 한다.
 */
public class Dictionary {

    private String id;

    private String user;

    private int count = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int countNum(){
        this.count += 1;
        return  count;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public Dictionary(String id) {
        this.id = id;
    }
}

