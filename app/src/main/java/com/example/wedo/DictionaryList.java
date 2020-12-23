package com.example.wedo;

public class DictionaryList  {

    private String list;

    private String user;

//    private int count = 0;

    public String getlist() {
        return list;
    }

    public void setlist(String list) {
        this.list = list;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public DictionaryList(String list) {
        this.list = list;
    }

//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }

//    public int countNum(){
//        this.count += 1;
//        return  count;
//    }
}

