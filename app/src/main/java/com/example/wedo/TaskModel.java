package com.example.wedo;

import java.util.ArrayList;

public class TaskModel {
    String title;
    ArrayList<String> subTitle;
    ArrayList<String> booleanValue;


    public TaskModel() {
        this.title = "업무 설정 안됨";
        subTitle = new ArrayList<String>();
        booleanValue = new ArrayList<String>();
    }

    public TaskModel(String title) {
        this.title = title;
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

    public String removeSubTitle(int index) {
        if (index >= 0 && index < subTitle.size()) {
            return subTitle.remove(index);
        } else {
            return null;
        }
    }

    public String getSubTitle(int index) {
        if (index >= 0 && index < subTitle.size()) {
            return subTitle.get(index);
        } else {
            return null;
        }
    }

    public int subTitleSize() {
        return subTitle.size();
    }

    public int booleanValueSize() {
        return booleanValue.size();
    }

    public String[] getSubTitleArray() {
        String[] array = new String[subTitle.size()];
        System.out.println("체크박스여부1: " + array.length);

        for (int i = 0; i < subTitle.size(); i++) {
            array[i] = subTitle.get(i);
        }
        return array;
    }

    public String[] getBooleanValueArray() {
        String[] checkArray = new String[booleanValue.size()];
        System.out.println("체크박스여부2: " + booleanValue.size());

        for (int i = 0; i < booleanValue.size(); i++) {
            checkArray[i] = booleanValue.get(i);
        }
        return checkArray;
    }

    public void print() {
        System.out.println("TASK 제목 : " + title);
        for (int i = 0; i < subTitle.size(); i++) {
            System.out.println(Integer.toString(i + 1) + " 번 째 SUB TASK 제목 : " + subTitle.get(i));
        }
        for (int i = 0; i < booleanValue.size(); i++) {
            System.out.println(Integer.toString(i + 1) + " 번 째 SUB TASK 제목 : " + booleanValue.get(i));
        }
    }
}
