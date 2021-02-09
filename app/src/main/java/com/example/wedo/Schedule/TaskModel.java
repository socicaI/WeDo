package com.example.wedo.Schedule;

import java.util.ArrayList;

public class TaskModel {
    String title;
    ArrayList<String> subTitle;
    ArrayList<String> booleanValue;
    int all;
    String[] array;
    String[] checkArray;

    public TaskModel() {
        this.title = "업무 설정 안됨";
        subTitle = new ArrayList<String>();
        booleanValue = new ArrayList<String>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addSubTitle(String sTitle) {
        subTitle.add(sTitle);
    }

    public void addBooleanValue(String check) {
        booleanValue.add(check);
    }

    public String[] getSubTitleArray() {
        array = new String[subTitle.size()];
        System.out.println("체크박스여부1: " + array.length);
        all  =  array.length;

        for (int i = 0; i < subTitle.size(); i++) {
            array[i] = subTitle.get(i);
        }
        return array;
    }

    public String[] getBooleanValueArray() {
         checkArray = new String[booleanValue.size()];
        System.out.println("체크박스여부2: " + booleanValue.size());

        for (int i = 0; i < booleanValue.size(); i++) {
            checkArray[i] = booleanValue.get(i);
        }
        return checkArray;
    }
}
